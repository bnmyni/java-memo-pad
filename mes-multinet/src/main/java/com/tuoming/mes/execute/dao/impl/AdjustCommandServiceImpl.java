/*******************************************************************************
 * Copyright (c) 2013.  Pyrlong All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tuoming.mes.execute.dao.impl;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.pyrlong.Envirment;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dao.CommandMapDao;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dao.ServerDao;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.dpp.dao.impl.PairedObject;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.execute.dao.AdjustCommandDao;
import com.tuoming.mes.execute.dao.AdjustCommandService;
import com.tuoming.mes.services.impl.ServerConnectPool;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.services.serve.ServerService;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.dao.WarningCollectionDao;

/**
 * 调整指令下发服务接口实现
 *
 * @see com.pyrlong.dpp.service.impl.AbstractBaseService
 */
@Scope("prototype")
@Component("AdjustCommandService")
public class AdjustCommandServiceImpl extends AbstractBaseService<AdjustCommand, Long> implements AdjustCommandService {

    private final static Logger logger = LogFacade.getLog4j(AdjustCommandServiceImpl.class);
    private AdjustCommandDao bizCommandDao;
    private CommandMapDao commandMapDao;
    private ServerDao serverDao;
    private OperationLogDao operationLogDao;
    private List<PairedObject> adjustRedo = null;
    @Autowired
    @Qualifier("sleepExeDao")
    private SleepExeDao sleepExeDao;

    @Autowired
    @Qualifier("warningCollectionDao")
    private WarningCollectionDao warningCollectionDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    @Autowired
    @Qualifier("OperationLogDao")
    public void setServerDao(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    @Autowired
    @Qualifier("CommandMapDao")
    public void setCommandMapDao(CommandMapDao commandMapDao) {
        this.commandMapDao = commandMapDao;
    }

    @Autowired
    @Qualifier("ServerDao")
    public void setServerDao(ServerDao serverDao) {
        this.serverDao = serverDao;
    }

    @Autowired
    @Qualifier("AdjustCommandDao")
    public void setBaseDao(BaseDao<AdjustCommand, Long> bizCommandDao) {
        this.baseDao = bizCommandDao;
        this.bizCommandDao = (AdjustCommandDao) bizCommandDao;
    }

    public void apply(String appName, String groupName) {
        //获取需要执行的指令列表,取给定分组并且未下发的指令
        List<AdjustCommand> commands = bizCommandDao.getCommands(appName, groupName);
        apply(commands, appName, groupName);
    }

    /**
     * 唤醒命令执行
     *
     * @param appName
     * @param groupName
     */
    public void sleepOrNotify(String appName, String groupName) {
        List<AdjustCommand> commands = bizCommandDao.getCommands(appName, groupName);//根据appName和groupName要唤醒的小区
        apply(commands, appName, groupName);//下发休眠/唤醒命令
        apply(commands, appName, groupName, true);//下发查询小区状态指令,获取小区状态

    }

    /**
     * 验证时间差是否满足一分钟
     *
     * @param date
     * @param sentTime
     * @return
     */
    private boolean valTimeInv(Date date, Date sentTime) {
        return (date.getTime() - sentTime.getTime()) / 1000 / 60 > 1;
    }

    /**
     * 重载发送命令方法
     *
     * @param commands
     * @param appName
     * @param groupName
     */
    public void apply(List<AdjustCommand> commands, String appName, String groupName) {
        apply(commands, appName, groupName, false);
    }

    /**
     * 执行特定应用生成的分组命令 同时处理如下功能： 1. 根据命令执行对象的不同并发执行任务，并记录执行日志到不同的日志文件 2. 执行配置命令执行前后的动作表达式 3.
     * 根据配置对指令执行是否成功进行判断，并根据执行结果执行对应的操作
     */
    private void apply(List<AdjustCommand> commands, String appName, String groupName, boolean needQueryFlag) {
        adjustRedo = new ArrayList<PairedObject>();
        //对获取的指令根据执行对象不同进行分组
        Map<ObjectType, Map<String, List<AdjustCommand>>> commandCollection = new LinkedHashMap<ObjectType, Map<String, List<AdjustCommand>>>();
        for (AdjustCommand cmd : commands) {
            //按网元分组存入第一层MAP
            if (!commandCollection.containsKey(cmd.getObjectType())) {
                commandCollection.put(cmd.getObjectType(), new LinkedHashMap<String, List<AdjustCommand>>());
            }
            //按服务器分组，存入第二层
            Map<String, List<AdjustCommand>> subCommands = commandCollection.get(cmd.getObjectType());
            if (!subCommands.containsKey(cmd.getTargetObject())) {
                subCommands.put(cmd.getTargetObject(), new LinkedList<AdjustCommand>());
            }
            subCommands.get(cmd.getTargetObject()).add(cmd);
        }
        //对各组命令启动独立线程进行执行
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(ConfigurationManager.getDefaultConfig().getInteger(
                MESConstants.ADJUST_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.ADJUST_THREAD_MAX_POOL_SIZE,
                        MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.ADJUST_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        threadPool.setThreadFactory(new CustomThreadFactory(MESConstants.ADJUST_THREAD_NAME));

        //启动处理线程，同一个网元下同一个服务器启动一个线程下发指令
        for (ObjectType objType : commandCollection.keySet()) {
            Map<String, List<AdjustCommand>> bizCommands = commandCollection.get(objType);
            for (String targetObject : bizCommands.keySet()) {
                try {
                    threadPool.execute(new BizCommandRunThread(adjustRedo, targetObject, bizCommands.get(targetObject), groupName, appName, needQueryFlag));
                } catch (Exception e) {
                    businessLogDao.insertLog(14, "下发指令执行线程异常", 1);
                    logger.error(e.getMessage(), e);
                }
            }
        }
        threadPool.shutdown(); //关闭后不能加入新线程，队列中的线程则依次执行完
        while (threadPool.getPoolSize() != 0) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        //处理第一次发送服务器没有连接成功的情况
        logger.info("<<<<<<<adjust command redo " + adjustRedo.size());
        threadPool = new ThreadPoolExecutor(ConfigurationManager.getDefaultConfig().getInteger(
                MESConstants.ADJUST_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.ADJUST_THREAD_MAX_POOL_SIZE, 10),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.ADJUST_THREAD_KEEP_ALIVE_TIME_IN_SECOND, 10),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        threadPool.setThreadFactory(new CustomThreadFactory(MESConstants.ADJUST_THREAD_NAME));
        for (PairedObject pairedObject : adjustRedo) {
            try {
                threadPool.execute(new BizCommandRunThread(adjustRedo, pairedObject.getObject1().toString(), (List<AdjustCommand>) pairedObject.getObject2(), groupName, appName, needQueryFlag));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        threadPool.shutdown(); //关闭后不能加入新线程，队列中的线程则依次执行完
        while (threadPool.getPoolSize() != 0) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        adjustRedo.clear();
        logger.info("All commands have been sent");
        businessLogDao.insertLog(14, "所有指令已发送完成", 0);

    }

    /**
     * 调整指令下发线程对象，用于系统多线程下发调整指令
     */
    class BizCommandRunThread extends AbstractCommandSendThread implements Runnable {
        private final Logger logger = LogFacade.getLog4j(BizCommandRunThread.class);
        boolean needQueryFlag;
        String serverName;
        List<AdjustCommand> commands;
        ServerService serverService;
        String logFileName = "";
        Map<String, String> curentEnv;
        List<PairedObject> adjustRedo;

        /**
         * @param serverName 服务器名称
         * @param commands   要发送的调整指令列表
         * @param groupName  调整指令所属分组，主要用于记录日志
         * @param appName    调整指令所属应用，主要用于记录日志
         * @throws IOException
         */
        public BizCommandRunThread(List<PairedObject> adjustRedo, String serverName, List<AdjustCommand> commands, String groupName, String appName) throws IOException {
            this.commands = commands;
            this.adjustRedo = adjustRedo;
            sendFlag = ConfigurationManager.getDefaultConfig().getBoolean(MESConstants.ADJUST_SEND_FLAG, false);
            this.serverName = serverName;
            setCommandMapDao(commandMapDao);
            logFileName = AppContext.getCacheFileName("adjust" + Envirment.PATH_SEPARATOR + serverName + Envirment.PATH_SEPARATOR + groupName + Envirment.PATH_SEPARATOR + serverName + "_" + DateUtil.currentDateString("yyyyMMddHHmmss") + ".log");
            curentEnv = getEnvCopy();
        }

        /**
         * @param serverName 服务器名称
         * @param commands   要发送的调整指令列表
         * @param groupName  调整指令所属分组，主要用于记录日志
         * @param appName    调整指令所属应用，主要用于记录日志
         * @throws IOException
         */
        public BizCommandRunThread(List<PairedObject> adjustRedo, String serverName, List<AdjustCommand> commands, String groupName, String appName, boolean needQueryFlag) throws IOException {
            this.commands = commands;
            this.adjustRedo = adjustRedo;
            sendFlag = ConfigurationManager.getDefaultConfig().getBoolean(MESConstants.ADJUST_SEND_FLAG, false);
            this.serverName = serverName;
            this.needQueryFlag = needQueryFlag;
            setCommandMapDao(commandMapDao);
            logFileName = AppContext.getCacheFileName("adjust" + Envirment.PATH_SEPARATOR + DateUtil.getNow("yyyy_MM_dd") + Envirment.PATH_SEPARATOR + serverName + Envirment.PATH_SEPARATOR + groupName + Envirment.PATH_SEPARATOR + serverName + "_" + DateUtil.currentDateString("yyyyMMddHHmmss") + ".log", true);
            curentEnv = getEnvCopy();
        }

        @Override
        public void run() {
            try {
                logger.info("Start sending commands to the server  " + serverName);
                if (commands.size() == 0) {
                    logger.warn(" No command need to send to " + serverService.getServer());
                    return;
                }
                serverService = ServerConnectPool.getServerServiceFromPool(serverName, logFileName);
                //如果打开了全局下发开关
                if (sendFlag) {
                    if (!serverService.isEnabled()) {
                        ServerConnectPool.releaseServer(serverService);
                        logger.fatal(serverService.getServer() + " init failed,add to adjust redo list...");
                        if (adjustRedo != null)
                            synchronized (adjustRedo) {
                                adjustRedo.add(new PairedObject(serverName, commands));
                            }
                        //登录失败
                        return;
                    }
                }

                for (AdjustCommand cmd : commands) {
                    //首先根据当前处理对象更新环境变量
                    updateEnv(cmd, curentEnv);
                    //每次发送新指令之前清空关键字列表
                    serverService.clearKeywrod();
                    if (StringUtil.isEmpty(cmd.getCommand()))
                        continue;
                    boolean cmdSuccess = true;
                    JSONObject jsonObject = JSONObject.fromObject(JSONSerializer.toJSON(cmd.getExtend4()).toString());
                    Map<String, Object> data = JSONObject.fromObject(jsonObject);
                    if (needQueryFlag) {
                        boolean queryFail = false;
                        String[] cmds = cmd.getExtend1().split(cmd.getSplitChar());
                        try {
                            cmdSuccess = sendCommands(cmds, curentEnv, serverService, logFileName);

                        } catch (Exception e) {
                            try {
                                logger.info("send command interrupt reconnect " + cmd.getExtend1());
                                Thread.currentThread().sleep(5000);
//									serverService.reconnect();
                                serverService = ServerConnectPool.reconnect(serverName, logFileName, serverService);
                                cmdSuccess = sendCommands(cmds, curentEnv, serverService, logFileName);
                            } catch (Exception e2) {
                                businessLogDao.insertLog(14, "下发指令连接中断", 1);
                                queryFail = true;
                            }
                        }
                        try {
                            if (queryFail) {
                                if (cmd.getGroupName().startsWith(Constant.SLEEP)) {
                                    if (cmd.isSucessfull()) {//休眠成功
                                        sleepExeDao.addSleepOrNotifyLog(data, cmd.getGroupName());
                                    } else {//休眠失败
                                        sleepExeDao.updateBlack(data, Constant.REASON_SLEEP_FALI);
                                        warningCollectionDao.insertAlarm(data, Constant.REASON_SLEEP_FALI);//采集告警信息
                                    }
                                } else {//唤醒
                                    if (!cmd.isSucessfull()) {//唤醒失败
                                        sleepExeDao.updateBlack(data, Constant.REASON_NOTIFY_FALI);
                                        sleepExeDao.addAlarm(data, Constant.REASON_NOTIFY_FALI);
                                        warningCollectionDao.insertAlarm(data, Constant.REASON_NOTIFY_FALI);//采集告警信息
                                    }
                                }
                            } else {
                                if ((cmd.getGroupName().startsWith(Constant.SLEEP) || cmd.getGroupName().startsWith(Constant.SLEEP_MANY)) && !cmdSuccess) {//假如执行休眠命令且休眠成功，则记录当前休眠小区和对应的补偿小区
                                    cmdSuccess = true;
                                    sleepExeDao.addSleepOrNotifyLog(data, cmd.getGroupName());
                                } else if ((cmd.getGroupName().startsWith(Constant.SLEEP) || cmd.getGroupName().startsWith(Constant.SLEEP_MANY))
                                        && cmdSuccess && !cmd.isSucessfull()) {//当休眠失败时，将休眠小区添加的黑名单
                                    cmdSuccess = false;
//                						sleepExeDao.delNofifyFromSleep(data);
                                    sleepExeDao.updateBlack(data, Constant.REASON_SLEEP_FALI);
                                    warningCollectionDao.insertAlarm(data, Constant.REASON_SLEEP_FALI);//采集告警信息
                                } else if (cmd.getGroupName().equalsIgnoreCase(Constant.NOTIFY) && cmdSuccess) {//假如当前执行命令是唤醒命令，且唤醒成功，则记录日志，并将唤醒小区从当前休眠的小区中删除
                                    cmdSuccess = true;
                                    sleepExeDao.delBlack(data);
                                    sleepExeDao.delNofifyFromSleep(data);
                                    sleepExeDao.addSleepOrNotifyLog(data, cmd.getGroupName());
                                } else if (cmd.getGroupName().startsWith(Constant.NOTIFY) && !cmdSuccess) {//假如当前是最后一次查询,且未成功唤醒小区，则将数据假如黑名单和告警名单
                                    cmdSuccess = false;
                                    sleepExeDao.updateBlack(data, Constant.REASON_NOTIFY_FALI);
                                    sleepExeDao.addAlarm(data, Constant.REASON_NOTIFY_FALI);
                                    warningCollectionDao.insertAlarm(data, Constant.REASON_NOTIFY_FALI);//采集告警信息
                                } else if (cmd.getGroupName().equalsIgnoreCase(Constant.TD_NETWORK_OFF_NOTIFY) && cmdSuccess) {
                                    //3G退网，唤醒命令且执行成功,更新黑名单，删除休眠表中唤醒成功的小区
                                    cmdSuccess = true;
                                    sleepExeDao.delBlack(data);
                                    sleepExeDao.delNofifyFromTdOffSleep(data);
                                    sleepExeDao.addSleepOrNotifyLog(data, cmd.getGroupName());
                                } else if (cmd.getGroupName().equalsIgnoreCase(Constant.NOTIFY_MANY) && cmdSuccess) {
                                    // 多补一的场合唤醒命令且执行成功,更新黑名单，删除多补一休眠小区表中唤醒成功的小区
                                    cmdSuccess = true;
                                    sleepExeDao.delBlack(data);
                                    sleepExeDao.delNofifyFromManySleep(data);
                                    sleepExeDao.addSleepOrNotifyLog(data, cmd.getGroupName());
                                }

                                cmd.setSucessfull(cmdSuccess);
                            }
                        } catch (Exception e) {
                            logger.info(cmd.getCommand() + " execute fail!");
                            businessLogDao.insertLog(14, "下发指令执行失败,command:[" + cmd.getCommand() + "]", 1);
                            e.printStackTrace();
                        }
                    } else {
                        //设置指令执行时间
                        cmd.setSentTime(DateUtil.currentDate());
                        //对指令进行分割执行
                        logger.info(cmd.getCommand() + " sent to " + serverName);
                        String[] cmds = cmd.getCommand().split(cmd.getSplitChar());
                        try {
                            cmdSuccess = sendCommands(cmds, curentEnv, serverService, logFileName);
                        } catch (Exception e) {
                            try {
                                logger.info("send command interrupt reconnect " + cmd.getCommand());
                                Thread.currentThread().sleep(5000);
                                serverService = ServerConnectPool.reconnect(serverName, logFileName, serverService);
//									serverService.reconnect();
                                cmdSuccess = sendCommands(cmds, curentEnv, serverService, logFileName);
                            } catch (Exception e2) {
                                businessLogDao.insertLog(14, "下发指令连接中断", 1);
                                cmdSuccess = false;
                            }
                        }
                        cmd.setApplied(1);
                        cmd.setSucessfull(cmdSuccess);
                        cmd.setCmdLog(logFileName);
                    }

                    bizCommandDao.saveOrUpdate(cmd);
                    if ("爱立信".equals(data.get("src_vender")) && "g2g".equals(data.get("bus_type"))) {
                        if (needQueryFlag) {
                            Thread.currentThread().sleep(1000);
                        } else {
                            Thread.currentThread().sleep(15000);
                        }
                    }

                		/*try {
	                		if(Constant.SLEEP.equalsIgnoreCase(cmd.getGroupName())&&cmdSuccess) {//假如执行休眠命令且休眠成功，则记录当前休眠小区和对应的补偿小区
	                			sleepExeDao.addSleepOrNotifyLog(data, Constant.SLEEP);
	                		}else if(Constant.SLEEP.equalsIgnoreCase(cmd.getGroupName())&&!cmdSuccess){
//	                			sleepExeDao.delNofifyFromSleep(data);
	                			sleepExeDao.updateBlack(data, Constant.REASON_SLEEP_FALI);
	                		}else if(Constant.NOTIFY.equalsIgnoreCase(cmd.getGroupName())&&cmdSuccess) {//假如当前执行命令是唤醒命令，且唤醒成功，则记录日志，并将唤醒小区从当前休眠的小区中删除
	                			sleepExeDao.delNofifyFromSleep(data);
	                			sleepExeDao.addSleepOrNotifyLog(data, Constant.NOTIFY);
	                		}else if(lastQuery&&!cmdSuccess) {//假如当前是最后一次查询,且未成功唤醒小区，则将数据假如黑名单和告警名单
	                			sleepExeDao.updateBlack(data, Constant.REASON_NOTIFY_FALI);
	                			sleepExeDao.addAlarm(data, Constant.REASON_NOTIFY_FALI);
	                		}
	                		//保存执行后指令状态
	                		bizCommandDao.saveOrUpdate(cmd);
	                		if("爱立信".equals(data.get("src_vender"))&&"g2g".equals(data.get("bus_type"))) {
	                			Thread.currentThread().sleep(30000);
	                		}
						} catch (Exception e) {
							logger.error(e);
							logger.info(cmd.getCommand()+" execute fail!");
						}*/
                }
                logger.info("Send command to the " + serverService.getServer() + " completed!");
            } catch (Exception e) {
                businessLogDao.insertLog(14, "下发指令出现异常", 1);
                logger.fatal(e.getMessage(), e);
            } finally {
                try {
                    //serverService.logout();
                    ServerConnectPool.releaseServer(serverService);
                } catch (Exception e) {
                    businessLogDao.insertLog(14, "下发指令退出异常", 1);
                    //退出登录
                    e.printStackTrace();
                }

            }
        }

        public void saveMessage() {
//            List<MesSubscribeMessageAlarmEntity> phoneList = serverDao.listAll();
        }

        @Override
        protected void doSomeAction(String action, Map<String, String> envs) {
            doAction(action, envs);
        }
    }
}