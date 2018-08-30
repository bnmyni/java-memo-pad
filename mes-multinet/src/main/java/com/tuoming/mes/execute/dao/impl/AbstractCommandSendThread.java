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

//Created On: 13-9-10 下午4:43
package com.tuoming.mes.execute.dao.impl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dao.CommandMapDao;
import com.tuoming.mes.collect.models.CommandMap;
import com.tuoming.mes.services.impl.LogCommandServiceImpl;
import com.tuoming.mes.services.serve.ServerService;

/**
 * 用于参数采集及指令下发的基础实现类，该类封装了根据配置内容动态执行指令、执行指令附加操作、跌代执行指令等操作
 *
 * @version 1.0
 * @see com.tuoming.mes.execute.dao.impl.AdjustCommandServiceImpl
 * @see com.tuoming.mes.services.impl.LogCommandServiceImpl
 * @since 1.0
 */

public abstract class AbstractCommandSendThread {

    /**
     * 日志记录器
     */
    private final static Logger logger = LogFacade.getLog4j(AbstractCommandSendThread.class);
    private CommandMapDao commandMapDao;
    /**
     * 当前上下文内的指令发送开关
     */
    protected boolean sendFlag = true;


    public void setCommandMapDao(CommandMapDao commandMapDao) {
        this.commandMapDao = commandMapDao;
    }

    /**
     * 执行配置的附加动作
     *
     * @param cmd  要执行的动作内容
     * @param envs 运行的上下文环境
     */
    protected abstract void doSomeAction(String cmd, Map<String, String> envs);

    /**
     * 记录已发送指令，避免重复发送
     */
    Map<String, String> cmdSentRecord = new HashMap<String, String>();

    /**
     * 将给定指令序列顺序发送到serverService对象关联的服务器对象
     *
     * @param cmds          要发送到指令队列
     * @param envs          当前运行环境上下文字典
     * @param serverService 服务器对象实例
     * @param logFile       输出日志文件路径
     * @return 标识指令是否成功发送，指令序列中只要有一个指令发送失败就返回false,否则返回true
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    protected boolean sendCommands(String[] cmds, Map envs, ServerService serverService, String logFile) throws FileNotFoundException, InterruptedException {
        boolean cmdSuccess = true;
        for (String cmd : cmds) {
            cmd = DSLUtil.getDefaultInstance().buildString(cmd, envs);
            envs.put("command", cmd);
            if (StringUtil.isEmpty(cmd.trim())) {
                logger.warn("Command is empty, skip  .... ");
                continue;
            }
            //如果遇到表达式标识 则 执行配置的操作，这主要用于处理指令执行前后需要等待一段时间等操作
            if (cmd.startsWith("$") && cmd.endsWith("$")) {
                logger.info("Run Expressions :" + cmd);
                doSomeAction(cmd.substring(1, cmd.length() - 1), envs);
                continue;
            }
            //获取当前指令对应当公共配置信息
            CommandMap commandMap = commandMapDao.getMatchedCommandMap(cmd, serverService.getServer().getManufacturers().getId());
            if (commandMap == null) {
                logger.warn("Command definitions not found " + cmd);
                return false;
            }
            //如果命令已经发送并且没有标识为可以重复发
            if (cmdSentRecord.containsKey(cmd) && !commandMap.getResent()) {
                logger.info(cmd + " has been sent,skip ...");
                continue;
            } else {
                cmdSentRecord.put(cmd, null);
            }

            doSomeAction(commandMap.getPreAction(), envs);
            //设置结束符
            if (StringUtil.isNotEmpty(commandMap.getDoneMark()))
                serverService.setPrompt(commandMap.getDoneMark());
            if (StringUtil.isNotEmpty(commandMap.getSucessfullMark()))
                serverService.addKeywordToCount(commandMap.getSucessfullMark());
            if (StringUtil.isNotEmpty(commandMap.getFailMark()))
                serverService.addKeywordToCount(commandMap.getFailMark());
            serverService.setTimeout(commandMap.getTimeOut());
            Map<String, String> interactiveMap = new HashMap<String, String>();
            if (StringUtil.isNotEmpty(commandMap.getInteractiveCmd())) {
                interactiveMap = (Map<String, String>) DSLUtil.getDefaultInstance().compute(commandMap.getInteractiveCmd(), envs);
                for (Map.Entry<String, String> entry : interactiveMap.entrySet()) {
                    serverService.addKeywordToCount(entry.getKey());
                }
            }
            //下发指令
            sendCommand(serverService, cmd, interactiveMap, envs);
            //判断执行结果 ,如果发现执行失败标识 则 直接跳过不继续 执行
            if (StringUtil.isNotEmpty(commandMap.getFailMark()) && serverService.getKeywordCount(commandMap.getFailMark()) > 0&&sendFlag) {
            	/*if(commandMap.getFailMark().equals("INTERUPTED|FUNCTION BUSY|INHIBITED")){
                  Thread.currentThread().sleep(10000);
                }*/
            	cmdSuccess = false;
                doSomeAction(commandMap.getFailAction(), envs);
            }
            //如果没有发现执行成功标识 也认为是失败了
            if (StringUtil.isNotEmpty(commandMap.getSucessfullMark()) && serverService.getKeywordCount(commandMap.getSucessfullMark()) == 0&&sendFlag) {
                cmdSuccess = false;
                doSomeAction(commandMap.getFailAction(), envs);
            }
            if (cmdSuccess) {
                doSomeAction(commandMap.getSucessAction(), envs);
            } else {
                //logger.warn("Last response********************************");
                //logger.warn(serverService.getResponse());
                logger.warn(cmd + "  command execute fails, check the log file for details  :" + logFile);
                break;
            }
            doSomeAction(commandMap.getAfterAction(), envs);
        }
        return cmdSuccess;
    }

    /**
     * 发送单条指令到服务器对象
     *
     * @param serverService  服务器对象实例
     * @param cmd            指令内容
     * @param interactiveMap 指令对应的交互策略配置字典
     * @param envs           指令对应的当前上下文环境配置
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    private void sendCommand(ServerService serverService, String cmd, Map<String, String> interactiveMap, Map envs) throws FileNotFoundException, InterruptedException {
        //只有打开全局下发开关时才实际下发
        if (sendFlag) {
            logger.info("Send to  : " + serverService.getServer() + " :  " + cmd);
            serverService.sendCommand(cmd);
        } else {
            logger.info("[Test send] [" + cmd + "] to " + serverService.getServer());
            Thread.sleep(10);//模拟指令执行时间间隔
            return;
        }
        for (Map.Entry<String, String> entry : interactiveMap.entrySet()) {
            if (serverService.getKeywordCount(entry.getKey()) > 0) {
                String newCmd = entry.getValue();
                String[] cmds = newCmd.split("#");
                for (String c : cmds) {
                    c = DSLUtil.getDefaultInstance().buildString(c, envs);
                    if (c.startsWith(">>>")) {
                        doSomeAction(c, envs);
                    } else if (c.startsWith(">")) {
                        doSomeAction(c.substring(1, c.length() - 1), envs);
                    } else {
                        logger.debug("Send interactive command : " + c);
                        if (c.equals(cmd) && StringUtil.isNotEmpty(cmd)) {
                            serverService.sendCommand(c);
                        } else {
                            sendCommand(serverService, c, interactiveMap, envs);
                        }
                    }
                }
            }
        }
    }
}
