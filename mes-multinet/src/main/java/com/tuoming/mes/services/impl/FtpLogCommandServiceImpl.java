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
package com.tuoming.mes.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.net.ftp.FTPClientExt;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dao.FtpLogCommandDao;
import com.tuoming.mes.collect.dao.FtpServerDao;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.file.FileProcessor;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.models.FtpLogCommand;
import com.tuoming.mes.collect.models.FtpServer;
import com.tuoming.mes.services.serve.FtpLogCommandService;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.util.HttpUtil;

/**
 * ****************************************************************
 * Ftp采集执行任务，读取系统配置的FTP采集任务，然后根据不同的服务器多线程实现文件下载及解析入库操作 具体要求：
 * <br/> 1. 支持根据分组、任务名触发采集任务 <br/>2. 自动判断本地是否存在重名文件，如果存在则不重复下载
 * <br/>3. 解析文件列表必须是本次下载的文件列表，避免重复解析
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Scope("prototype")
@Component("FtpLogCommandService")
public class FtpLogCommandServiceImpl extends AbstractBaseService<FtpLogCommand, String> implements FtpLogCommandService {

    private final static Logger logger = LogFacade.getLog4j(FtpLogCommandServiceImpl.class);
    private final static boolean deleteLocalFileBeforeDownload =
            ConfigurationManager.getDefaultConfig().getBoolean(MESConstants.FTP_DELETE_LOCAL_FILE, false);
    FtpLogCommandDao ftpLogCommandDao;
    List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<ThreadPoolExecutor>();
    private Map<String, List<String>> loadFileMapList = Maps.newHashMap();
    private OperationLogDao operationLogDao;

    private FtpServerDao ftpServerDao;
    
    private BusinessLogDao businessLogDao;

    @Autowired
    @Qualifier("OperationLogDao")
    public void setServerDao(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    @Autowired
    @Qualifier("FtpServerDao")
    public void setFtpServerDao(FtpServerDao ftpServerDao) {
        this.ftpServerDao = ftpServerDao;
    }
    public int module_type = 0;
    @Autowired
    @Qualifier("businessLogDao")
    public void setBusinessLogDao(BusinessLogDao businessLogDao){
    	this.businessLogDao = businessLogDao;
    }
    public FtpLogCommandServiceImpl() {

    }


    @Autowired
    @Qualifier("FtpLogCommandDao")
    public void setBaseDao(BaseDao<FtpLogCommand, String> baseDao) {
        this.baseDao = baseDao;
        this.ftpLogCommandDao = (FtpLogCommandDao) baseDao;
    }

    @Override
    public void query(String name, long batch) {
        FtpLogCommand ftpLogCommand = ftpLogCommandDao.get(name);
        if (ftpLogCommand != null) {
            List<FtpLogCommand> logCommands = new ArrayList<FtpLogCommand>();
            logCommands.add(ftpLogCommand);
            try {
                downloadAndParser(logCommands, batch);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void queryAll(String groupName, long batch) {
        List<FtpLogCommand> logCommandList = ftpLogCommandDao.getByGroup(groupName);
        if(groupName.equalsIgnoreCase(Constant.PM)){
        	module_type = 1;
        }else if(groupName.startsWith(Constant.MRO)){
        	module_type = 5;
        }else if(groupName.trim().equalsIgnoreCase(Constant.ALARM)){
        	module_type = 16;
        }else{//CM采集
        	module_type = 3;
        }
        businessLogDao.insertLog(module_type, "采集及解析开始", 0);
        try {
            downloadAndParser(logCommandList, batch);                        
        } catch (InterruptedException e) {
            logger.fatal(e.getMessage(), e);
            businessLogDao.insertLog(module_type, "采集及解析出现异常", 1);
        }
        businessLogDao.insertLog(module_type, "采集及解析结束", 0);
    }

    @Override
    public void queryAll(long batch) {
        List<FtpLogCommand> logCommandList = ftpLogCommandDao.getAllEnabled();
        try {
            downloadAndParser(logCommandList, batch);
        } catch (InterruptedException e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    /**
     * 对配置的任务执行下载、解析、入库 操作 处理步骤： 1. 首先对要执行的任务根据不同ftp服务器进行分类 2. 针对不同的server分别启动 处理线程进行处理
     *
     * @param logCommandList 要执行的采集指令列表
     */
    private synchronized void downloadAndParser(List<FtpLogCommand> logCommandList, long batchId) throws InterruptedException {
        Map<FtpServer, List<FtpLogCommand>> commandMap = new HashMap<FtpServer, List<FtpLogCommand>>();
        for (FtpLogCommand cmd : logCommandList) {
            if (cmd != null && cmd.getFtpServer() != null) {
                if (!commandMap.containsKey(cmd.getFtpServer())) {
                    commandMap.put(cmd.getFtpServer(), new ArrayList<FtpLogCommand>());
                }
                List<FtpLogCommand> lists = commandMap.get(cmd.getFtpServer());
                lists.add(cmd);
            }
        }
        //针对每个FTP服务器分别启动处理进程
        for (Map.Entry<FtpServer, List<FtpLogCommand>> entry : commandMap.entrySet()) {
            downloadAndParser(entry.getKey(), entry.getValue(), batchId);
        }
        for (ThreadPoolExecutor executor : threadPoolExecutors) {
            while (executor.isShutdown() && executor.getPoolSize() > 0) {
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException e) {
                	businessLogDao.insertLog(module_type, "采集及解析线程池异常", 1);
                    e.printStackTrace();
                }
            }
        }
        threadPoolExecutors.clear();
        //都采集完了再合并入库，避免出现入库过程占用资源过多的情况
        loadfile();
    }

    private void loadfile() {
    	//这里loadFileMapList包含Map的key：mes_ftp_command表中target_db + "-" + target_table_map
    	//value：XXXXXX/out/XXXXXX.csv
        for (Map.Entry<String, List<String>> entry : loadFileMapList.entrySet()) {
        	//此次在FtpLog目录下生成output/mes_ftp_command表中target_db + "-" + target_table_map + "_" + 当前时间毫秒值+ .data
            String targetFile = AppContext.getCacheFileName("FtpLog" + Envirment.PATH_SEPARATOR + "output" + Envirment.PATH_SEPARATOR + entry.getKey() + "_" + DateUtil.getTimeinteger() + ".data");
            for (String file : entry.getValue()) {
                try {
                	//将解析后的csv文件copy份更名存至output
                    FileOper.copyFile(file, targetFile, true);
                } catch (Exception e) {
                	businessLogDao.insertLog(module_type, "采集及解析复制文件异常", 1);
                    logger.error(e.getMessage(), e);
                }
            }
            String[] target = entry.getKey().split("-");
            try {
                logger.info("load files to " + entry.getKey());
                DataAdapterPool.getDataAdapterPool(target[0]).getDataAdapter().loadfile(targetFile, target[1]);
            } catch (Exception e) {
            	businessLogDao.insertLog(module_type, "采集及解析入库异常", 1);
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 针对访问同一个服务器的任务使用一个线程池，方便对同一个Server的并发连接控制
     *
     * @param server
     * @param commands
     */
    private void downloadAndParser(FtpServer server, List<FtpLogCommand> commands, long batchId) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.FTP_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.FTP_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.FTP_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        threadPool.setThreadFactory(new CustomThreadFactory(MESConstants.FTP_THREAD_NAME));
        for (FtpLogCommand logCommand : commands) {
            if (logCommand.getFtpServer().isEnabled()) {
                FtpParser ftpParser = new FtpParser(logCommand, batchId);
                threadPool.execute(ftpParser);
            }
        }
        threadPool.shutdown();
        threadPoolExecutors.add(threadPool);
    }

    @Override
    public void query(String name) {
        query(name, 0);
    }

    @Override
    public void queryAll(String groupName) {
        queryAll(groupName, 0);
    }

    @Override
    public void queryAll() {
        queryAll(0);
    }

    /**
     * 处理FTP文件下载及解析操作的线程对象
     */
    class FtpParser implements Runnable {

        FtpLogCommand logCommand;
        Map<String, String> currentEnv;
        private final Logger logger = LogFacade.getLog4j(FtpParser.class);

        /**
         * 构造函数
         *
         * @param logCommand
         */
        public FtpParser(FtpLogCommand logCommand, long batchId) {
            this.logCommand = logCommand;
            currentEnv = getEnvCopy();
            updateEnv(logCommand, currentEnv);
            currentEnv.put(MESConstants.BATCH_KEY, batchId + "");
        }

        @Override
        public void run() {
        	submitCollectState(Constant.FTP_COLLECT_BEGIN_LOGIN);
            FTPClientExt ftpClientExt = new FTPClientExt();
            ftpClientExt.setUrl(logCommand.getFtpServer().getUrl());
            ftpClientExt.setName(logCommand.getFtpServer().getName());
            ftpClientExt.setPort(logCommand.getFtpServer().getPort());
            ftpClientExt.setUid(logCommand.getFtpServer().getUid());
            ftpClientExt.setPassword(logCommand.getFtpServer().getPassword());
            ftpClientExt.setControlEncoding(logCommand.getFtpServer().getControlEncoding());
            ftpClientExt.setEnterLocalPassiveMode(logCommand.getFtpServer().isEnterLocalPassiveMode());
            ftpClientExt.setConnectTimeout(logCommand.getFtpServer().getConnectTimeout());
            //ftp连接失败更新表mes_ftp_servers中相应ftp服务器状态,将状态置为100
            if (!ftpClientExt.connect()) {
                logCommand.getFtpServer().setStatus(100);
                ftpServerDao.update(logCommand.getFtpServer());
                submitCollectState(Constant.FTP_COLLECT_LOGIN_FAIL);
                return;
            } else if (logCommand.getFtpServer().getStatus() > 0) {
            	//ftp连接成功，如果数据库中ftp状态为不成功，更改状态为成功
                logCommand.getFtpServer().setStatus(0);
                ftpServerDao.update(logCommand.getFtpServer());
            }
            //TODO  FTP采集数据状态维护
            submitCollectState(Constant.FTP_COLLECT_LOGIN_SUCCESS);
            try {
            	/* 对ftp指令进行过滤
            	 * 表mes_ftp_command中当group_name="MRO_TD_HW"时，key:ftp_command_result_filter  value:"gsmneighbour","inter","intra"
            	 */
                currentEnv.put(MESConstants.FTP_COMMAND_RESULT_FILTER, logCommand.getResultFilter());
                //下载并解析文件
                downloadAndParseFiles(ftpClientExt, currentEnv);
            } catch (Exception ex) {
            	businessLogDao.insertLog(module_type, "采集及解析下载文件异常", 1);
                logger.fatal(ex.getMessage(), ex);
            }
        }

        private void submitCollectState(int state) {
        	boolean enableMontior = ConfigurationManager.getDefaultConfig().getBoolean(Constant.ENABLE_MONTIOR, true);
        	if(!enableMontior) {
        		return;
        	}
        	List<Map<String, String>> paramList = new ArrayList<Map<String,String>>();
            Map<String, String> paramMap = new HashMap<String, String>();
            String[] infoArr = logCommand.getCommandName().split("_");
        	paramMap.put("commandname", logCommand.getCommandName());
        	paramMap.put("servername", logCommand.getFtpServer().getName());
            paramMap.put("username", logCommand.getFtpServer().getUid());
            paramMap.put("ip", logCommand.getFtpServer().getUrl());
            paramMap.put("port", logCommand.getFtpServer().getPort()+"");
            paramMap.put("datatype", logCommand.getGroupName().startsWith(Constant.MRO)?Constant.MRO:logCommand.getGroupName());
            paramMap.put("zs", infoArr[0]);
            paramMap.put("factory", infoArr[1]);
            paramMap.put("status", String.valueOf(state));
            paramMap.put("grading", Constant.DAYUNIT);
            paramMap.put("type", Constant.MONTIOR_TYPE_COLLECT);
            paramMap.put("dlfs", Constant.FTP);
            if(Constant.PM.equals(logCommand.getGroupName())) {
            	paramMap.put("grading", Constant.MINUTEUNIT);
            }else if(Constant.CM.equals(logCommand.getGroupName())) {
            	paramMap.put("grading", Constant.DAYUNIT);
            }else {
            	paramMap.put("grading", Constant.MROUNIT);
            }
            paramList.add(paramMap);
            //将服务器信息及采集状态信息以json形式进行展示
            HttpUtil.post(ConfigurationManager.getDefaultConfig().getString(Constant.MONTIOR_COLLECT_STATE_URL, null), "collectstate="+JSONSerializer.toJSON(paramList));
		}

		private void downloadAndParseFiles(FTPClientExt ftpClientExt, Map<String, String> newEnvs) {
            List<String> files = Lists.newArrayList();
            /**************************ftp download start****************************************/
          //本地缓存目录
        	String localPath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getLocalPath(), currentEnv));
        	if (!logCommand.getLocalPath().startsWith("/") && logCommand.getLocalPath().indexOf(":") < 0) {
        		if(localPath.indexOf(Constant.MRO)>=0) {
        			localPath = AppContext.getCacheFileName("mr" + Envirment.PATH_SEPARATOR + localPath, true);
        		}else {
        			localPath = AppContext.getCacheFileName("FtpLog" + Envirment.PATH_SEPARATOR + localPath, false);
        		}
        	}
            if (StringUtil.isNotEmpty(logCommand.getIterator())) {
            	//通过替换命令中变量字符获得执行指令
                String queryCmd = DSLUtil.getDefaultInstance().relpaceVariable(logCommand.getIterator(), currentEnv);
                DataTable table = (DataTable) DSLUtil.getDefaultInstance().compute(queryCmd, currentEnv);
                if (table==null||table.getRows().size() == 0) {
                	logger.info(String.format("%s table is empty!!", logCommand.getCommandName()));
                }else {
                	for (DataRow row : table.getRows()) {
                		Map envs = mergerMap(row.getItemMap(), currentEnv);
                		//获取下载文件过滤器
                		String fileFilter = DSLUtil.getDefaultInstance().buildString(logCommand.getFilter(), newEnvs);
                		//获取下载文件远程路径
                		String remotePath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getRemotePath(), envs));
                		String childLocaPath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(localPath, envs));
                		//下载文件
                		if (!ftpClientExt.downloadDir(remotePath, childLocaPath, fileFilter, logCommand.getGetSubDir(), logCommand.getDeleteFileAfterGet())) {
                			logger.info("下载文件失败:" + ftpClientExt.getName());
                		}
                		//完成文件下载
                		if(fileFilter.contains("getFileByCreateDate")) {
                			fileFilter = fileFilter.split("getFileByCreateDate")[0];
                		}
                		files.addAll(FileOper.getSubFiles(childLocaPath, fileFilter, true));
                    }
                	ftpClientExt.disconnect();
                }
                if(localPath.indexOf("/dynaic_dir/$")>0) {
                	localPath = localPath.substring(0,localPath.indexOf("/dynaic_dir/")+12);
            	}
            } else {
            	String remotePath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getRemotePath(), currentEnv));
                String fileFilter = DSLUtil.getDefaultInstance().buildString(logCommand.getFilter(), newEnvs);
                logger.info("Ftp filter = " + fileFilter);
                try {
                    //下载文件
                    if (!ftpClientExt.downloadDir(remotePath, localPath, fileFilter, logCommand.getGetSubDir(), logCommand.getDeleteFileAfterGet())) {
                        logger.info("下载文件失败:" + ftpClientExt.getName());
                    }
                } catch (Exception ex) {
                	businessLogDao.insertLog(module_type, "采集及解析下载文件异常", 1);
                    logger.warn(ex.getMessage(), ex);
                }finally {
                	ftpClientExt.disconnect();
                }
                //完成文件下载
                localPath = FileOper.formatePath(localPath);
                FileOper.checkAndCreateForder(localPath);
                if(fileFilter.contains("getFileByCreateDate")) {
        			fileFilter = fileFilter.split("getFileByCreateDate")[0];
        		}
                //基于localPath路径查看以下所有路径中是否包含fileFilter，包含的路径全部返回。
                files = FileOper.getSubFiles(localPath, fileFilter, true);
            }
            
            /**************************ftp download end****************************************/
            
            /**************************ftp parse start****************************************/
            List<String> finalFiles = Lists.newArrayList();
            for (String f : files) {
                //过滤掉非法文件
                if (FileOper.isFileExist(f)) {
                    if (!f.endsWith(".tmp") && !f.endsWith(".done") && !FileOper.isFileExist(f + ".done"))
                        finalFiles.add(f);
                } else {
                    logger.warn(f + " not found");
                }
            }
            
            List<Map<String, String>> paramList = new ArrayList<Map<String,String>>();
            submitCollectState(Constant.FTP_COLLECT_DOWNLOAD_END);
            
            //logCommand.getLogParser()从数据库获得解析文件处理器名称
            if (StringUtil.isEmpty(logCommand.getLogParser())){
            	businessLogDao.insertLog(module_type, "采集及解析解析文件失败，解析器：["+logCommand.getLogParser()+"]", 1);
            	logger.info("解析文件失败-parser:" + logCommand.getLogParser());
                return;
            }
            //加载mes_ftp_command表中log_parser配置的解析器
            FileProcessor fileProcessor = AppContext.getBean(logCommand.getLogParser());
            localPath = localPath + "out" + Envirment.PATH_SEPARATOR;
            FileOper.checkAndCreateForder(localPath);
            //为了避免数据冲突，输出目录需要为空
            if (deleteLocalFileBeforeDownload)
                FileOper.delAllFile(localPath);
            fileProcessor.setTargetPath(localPath);
            //将存放文件的list形式的finalFiles转换成map存储
            fileProcessor.setFiles(finalFiles, newEnvs);
            //不同厂商调用不同的处理器解析文件
            fileProcessor.run();
            
            submitCollectState(Constant.FTP_COLLECT_PARSE_END);
            /**************************ftp parse end****************************************/
            
            /**************************ftp load database start****************************************/
            //文件入库
            //判断mes_ftp_command表中target_db和target_table_map不为空时处理
            if (StringUtil.isNotEmpty(logCommand.getTargetDb()) && StringUtil.isNotEmpty(logCommand.getTargetTableMap())) {
                Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(logCommand.getTargetTableMap());
                try {
                    //DataAdapter adapter = DataAdapterPool.getDataAdapterPool(logCommand.getTargetDb()).getDataAdapter();
                    for (Map.Entry<String, String> targetTable : result.entrySet()) {
                        for (String resultFile : fileProcessor.getFiles()) {
                            if (StringUtil.isMatch(resultFile, targetTable.getKey())) {
                                resultFile = localPath + resultFile;
                                String loadFilekey = logCommand.getTargetDb() + "-" + targetTable.getValue();
                                //adapter.loadfile(resultFile, targetTable.getValue());
                                synchronized (loadFileMapList) {
                                    if (!loadFileMapList.containsKey(loadFilekey))
                                        loadFileMapList.put(loadFilekey, new ArrayList<String>());
                                    loadFileMapList.get(loadFilekey).add(resultFile);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                	businessLogDao.insertLog(module_type, "采集及解析入库异常", 1);
                    logger.fatal(e.getMessage(), e);
                }
            }
            submitCollectState(Constant.FTP_COLLECT_END);
            /**************************ftp load database end****************************************/
        }
    }

}
