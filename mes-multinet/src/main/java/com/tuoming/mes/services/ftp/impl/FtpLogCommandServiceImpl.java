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
package com.tuoming.mes.services.ftp.impl;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.tuoming.mes.collect.dao.BusinessLogDao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.pyrlong.Envirment;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.FtpLogCommandDao;
import com.tuoming.mes.collect.dao.FtpServerDao;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.models.FtpLogCommand;
import com.tuoming.mes.collect.models.FtpServer;
import com.tuoming.mes.services.ftp.FileParserWorker;
import com.tuoming.mes.services.ftp.io.ConnectionInfo;
import com.tuoming.mes.services.ftp.io.FtpConnection;
import com.tuoming.mes.services.ftp.io.FtpConnectionFactory;
import com.tuoming.mes.services.serve.FtpLogCommandService;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.strategy.consts.Constant;


/**
 * Ftp采集执行任务，读取系统配置的FTP采集任务，然后根据不同的服务器多线程实现文件下载及解析入库操作 具体要求：<br/> 1. 支持根据分组、任务名触发采集任务 <br/>2. 自动判断本地是否存在重名文件，如果存在则不重复下载
 * <br/>3. 解析文件列表必须是本次下载的文件列表，避免重复解析
 *
 * @version 1.0.0
 * @since 1.0.0
 */
/*@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("FtpLogCommandService")*/
@Scope("prototype")
@Component("FtpLogCommandServiceImpl")
public class FtpLogCommandServiceImpl extends AbstractBaseService<FtpLogCommand, String> implements FtpLogCommandService {

    private final static Logger logger = LogFacade.getLog4j(FtpLogCommandServiceImpl.class);
    private static int FTP_POOL_NUM_PER_SERVER = 5;
    private static int FTP_POOL_WAIT_SECOND = 3600;
    private final static boolean deleteLocalFileBeforeDownload =
            ConfigurationManager.getDefaultConfig().getBoolean(MESConstants.FTP_DELETE_LOCAL_FILE, false);
    FtpLogCommandDao ftpLogCommandDao;
    private Map<String, List<String>> loadFileMapList = Maps.newHashMap();
    private OperationLogDao operationLogDao;
    private FtpServerDao ftpServerDao;
    private static FtpConnectionFactory ftpFactory;
    private BusinessLogDao businessLogDao;
    
    public int module_type = 0;
    @Autowired
    @Qualifier("businessLogDao")
    public void setBusinessLogDao(BusinessLogDao businessLogDao){
    	this.businessLogDao = businessLogDao;
    }

    @Autowired
    @Qualifier("OperationLogDao")
    public void setServerDao(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    @Autowired
    @Qualifier("FtpServerDao")
    public void setFtpServerDao(FtpServerDao ftpServerDao) {
        this.ftpServerDao = ftpServerDao;
        initFtpFactory();
    }

    public FtpLogCommandServiceImpl() {
    	FTP_POOL_NUM_PER_SERVER = ConfigurationManager.getDefaultConfig().getInteger("aos.ftp_pool_num", Integer.valueOf(5)).intValue();
        FTP_POOL_WAIT_SECOND = ConfigurationManager.getDefaultConfig().getInteger("aos.ftp_pool_wait_second", Integer.valueOf(3600)).intValue();
    }

    private static GenericKeyedObjectPool pool = null;

    private synchronized  void initFtpFactory() {
        if (ftpFactory == null) {
            logger.info("Init ftp connect factory");
            ftpFactory = new FtpConnectionFactory();
            List<FtpServer> servers = ftpServerDao.listAll();
            for (FtpServer ftpServer : servers) {
                if (ftpServer.isEnabled()) {
                    ConnectionInfo connectionInfo = new ConnectionInfo(ftpServer.getName(), ftpServer.getConnectionType(),
                            ftpServer.getUrl(), ftpServer.getPort(), ftpServer.getUid(), ftpServer.getPassword());
                    connectionInfo.setDataTimeout(ftpServer.getDataTimeout());
                    connectionInfo.setControlEncoding(ftpServer.getControlEncoding());
                    connectionInfo.setPassiveMode(ftpServer.isEnterLocalPassiveMode());
                    connectionInfo.setConnectTimeout(ftpServer.getConnectTimeout());
                    ftpFactory.addConnectionInfo(connectionInfo);
                }
            }

            if (pool == null)
                pool = new GenericKeyedObjectPool(ftpFactory, FTP_POOL_NUM_PER_SERVER,
                        GenericKeyedObjectPool.WHEN_EXHAUSTED_BLOCK, FTP_POOL_WAIT_SECOND * 1000, true, false);
        }
    }

    public FtpConnection getFtpConnection(String name) {
        try {
        	logger.info("136 row pool---------"+pool);
            return (FtpConnection) pool.borrowObject(name);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
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
        try {
            downloadAndParser(logCommandList, batch);
        } catch (InterruptedException e) {
        	businessLogDao.insertLog(module_type, "采集及解析出现异常", 1);
            logger.fatal(e.getMessage(), e);
        }
        businessLogDao.insertLog(module_type, "采集及解析完成", 0);
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
        logger.info("There are " + logCommandList.size() + " ftp command founded!");

        Map<FtpServer, List<FtpLogCommand>> commandMap = new HashMap<FtpServer, List<FtpLogCommand>>();
        List<FtpLogCommand> localCommands = new ArrayList<FtpLogCommand>();
        for (FtpLogCommand cmd : logCommandList) {
            if (cmd != null && cmd.getFtpServer() != null) {
                if (!commandMap.containsKey(cmd.getFtpServer())) {
                    commandMap.put(cmd.getFtpServer(), new ArrayList<FtpLogCommand>());
                }
                List<FtpLogCommand> lists = commandMap.get(cmd.getFtpServer());
                lists.add(cmd);
            } else {
                localCommands.add(cmd);
            }
        }
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.FTP_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.FTP_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.FTP_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        threadPool.setThreadFactory(new CustomThreadFactory(MESConstants.FTP_THREAD_NAME));
        //针对每个FTP服务器分别启动处理进程
        for (Map.Entry<FtpServer, List<FtpLogCommand>> entry : commandMap.entrySet()) {
            for (FtpLogCommand logCommand : entry.getValue()) {
                if (logCommand.getFtpServer().isEnabled()) {
                    FtpParser ftpParser = new FtpParser(logCommand, batchId);
                    threadPool.execute(ftpParser);
                }
            }
        }
        //启动本地文件处理线程
        for (FtpLogCommand logCommand : localCommands) {
            FtpParser ftpParser = new FtpParser(logCommand, batchId);
            threadPool.execute(ftpParser);
        }
        threadPool.shutdown();
        threadPool.awaitTermination(72000000, TimeUnit.MILLISECONDS);
        //都采集完了再合并入库，避免出现入库过程占用资源过多的情况
        loadfile();
    }

    private synchronized void loadfile() {
        Long starttime = DateUtil.getTimeinteger();
        synchronized (loadFileMapList) {
            for (Map.Entry<String, List<String>> entry : loadFileMapList.entrySet()) {
                logger.info(starttime + "  :: Start merge file for " + entry.getKey());
                String targetFile = AppContext.getCacheFileName("FtpLog" + Envirment.PATH_SEPARATOR + "output" + Envirment.PATH_SEPARATOR + entry.getKey() + "_" + DateUtil.getTimeinteger() + ".");
                String[] target = entry.getKey().split("-");
                long targetFileSize = 0;
                int fileCount = 0;
                for (String file : entry.getValue()) {
                    try {
                        targetFileSize += FileOper.copyFile(file, targetFile + fileCount, true);
                        //控制单个文件10兆左右
                        if (targetFileSize > 10240000) {
                            try {
                                logger.info("load files to " + entry.getKey());
                                DataAdapterPool.getDataAdapterPool(target[0]).getDataAdapter().loadfile(targetFile + fileCount, target[1]);
                                targetFileSize = 0;
                                fileCount++;
                            } catch (Exception e) {
                            	businessLogDao.insertLog(module_type, "采集及解析入库异常", 1);
                                logger.error(e.getMessage(), e);
                            }
                        }
                    } catch (Exception e) {
                    	businessLogDao.insertLog(module_type, "采集及解析复制文件异常", 1);
                        logger.error(e.getMessage(), e);
                    }
                }
                if (targetFileSize > 0) {
                    try {
                        DataAdapterPool.getDataAdapterPool(target[0]).getDataAdapter().loadfile(targetFile + fileCount, target[1]);
                    } catch (Exception e) {
                    	businessLogDao.insertLog(module_type, "采集及解析入库异常", 1);
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            loadFileMapList.clear();
            logger.info("All file loaded!");
        }
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
        BlockingDeque<String> remoteFileList = Queues.newLinkedBlockingDeque();

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
            final FtpServer ftpServer = logCommand.getFtpServer();
            if (ftpServer != null) {
                try {
                    currentEnv.put(MESConstants.FTP_COMMAND_RESULT_FILTER, logCommand.getResultFilter());
                    //本地缓存目录
                    String localPath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getLocalPath(), currentEnv));
                    final String remotePath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getRemotePath().split("#")[0], currentEnv));
                    if (!logCommand.getLocalPath().startsWith("/") && logCommand.getLocalPath().indexOf(":") < 0)
                    if (StringUtil.isNotEmpty(logCommand.getIterator())) {
                        String queryCmd = DSLUtil.getDefaultInstance().relpaceVariable(logCommand.getIterator(), currentEnv);
                        DataTable table = (DataTable) DSLUtil.getDefaultInstance().compute(queryCmd, currentEnv);
                        if (table.getRows().size() == 0)
                        logger.error(String.format("%s table is empty!!", logCommand.getCommandName()));
                        List<String> fileNeedToGet = new ArrayList<String>();
                        if(remotePath.endsWith("/")){
                        	 for (DataRow row : table.getRows()) {
								 Map envs = mergerMap(row.getItemMap(), currentEnv);//mergerMap(map1,map2)将map2合并到map1
                        		 //替换正则后的本地路径
                        		 String localFilePath = AppContext.getCacheFileName("FtpLog" + Envirment.PATH_SEPARATOR 
                        						 + FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getLocalPath(), envs)));
                        		 //替换正则后的远程路径
                        		 final String remoPath = FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getRemotePath(), envs));
                        		 //获取文件名称
                        		 final String fileFilter = DSLUtil.getDefaultInstance().buildString(logCommand.getFilter(), envs);
                                 try {
                                     //尝试连接Ftp服务器,记录连接状态并获取需要下载的文件列表
                                     FtpConnection ftpClientExt = getFtpConnection(ftpServer.getName());
                                     if (ftpClientExt != null && !ftpClientExt.isOpened()) {
                                         ftpServer.setStatus(100);
                                         ftpServerDao.update(ftpServer);
                                         return;
                                     } else if (ftpServer.getStatus() > 0) {
                                         ftpServer.setStatus(0);
                                         ftpServerDao.update(ftpServer);
                                     }
                                     ftpClientExt.getFileNames(remoPath, fileFilter, logCommand.getGetSubDir(), remoteFileList);
                                     remoteFileList.put("END");
                                     pool.returnObject(ftpServer.getName(), ftpClientExt);
                                 } catch (Exception ex) {
                                	 businessLogDao.insertLog(module_type, "FTP采集数据异常", 1);
                                     logger.warn(ex.getMessage(), ex);
                                 }
                                 final String lastlocalPath = FileOper.formatePath(localFilePath);
                                 FileOper.checkAndCreateForder(lastlocalPath);
                                 downloadFiles(ftpServer, lastlocalPath, currentEnv);
                             }
                        	 return;
                        }else{
	                        for (DataRow row : table.getRows()) {
	                            Map envs = mergerMap(row.getItemMap(), currentEnv);
	                            String remoteFile = DSLUtil.getDefaultInstance().buildString(logCommand.getFilter(), envs);
	                            String remoteFilePath =  FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(remotePath, envs));
	                            //避免下载重复数据
	                            if (!fileNeedToGet.contains(remoteFile)) {
	                                fileNeedToGet.add(remoteFile);
	                                remoteFileList.add(remoteFilePath);
	                            }
	                        }
                        }
                    } else {
                        final String fileFilter = DSLUtil.getDefaultInstance().buildString(logCommand.getFilter(), currentEnv);
                        logger.info("File filter = " + fileFilter);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //尝试连接Ftp服务器,记录连接状态并获取需要下载的文件列表
                                    FtpConnection ftpClientExt = getFtpConnection(ftpServer.getName());
                                    if (ftpClientExt != null && !ftpClientExt.isOpened()) {
                                        ftpServer.setStatus(100);
                                        ftpServerDao.update(ftpServer);
                                        return;
                                    } else if (ftpServer.getStatus() > 0) {
                                        ftpServer.setStatus(0);
                                        ftpServerDao.update(ftpServer);
                                    }
                                    logger.info("logCommand===>"+logCommand);
                                    logger.info("RemotePath===>"+FileOper.formatePath(DSLUtil.getDefaultInstance().buildString(logCommand.getRemotePath(),currentEnv)));
                                    logger.info("logCommand.getGetSubDir()===>"+logCommand.getGetSubDir());
                                    logger.info("remoteFileList===>"+remoteFileList);
                                    ftpClientExt.getFileNames(DSLUtil.getDefaultInstance().buildString(logCommand.getRemotePath(),currentEnv), fileFilter, logCommand.getGetSubDir(), remoteFileList);
                                    remoteFileList.put("END");
                                    pool.returnObject(ftpServer.getName(), ftpClientExt);
                                } catch (Exception ex) {
                                	businessLogDao.insertLog(module_type, "FTP采集数据异常", 1);
                                    logger.warn(ex.getMessage(), ex);
                                }
                            }
                        });
                        thread.start();
                    }
                    //文件下载
                    final String lastlocalPath = FileOper.formatePath(AppContext.getCacheFileName("FtpLog" + Envirment.PATH_SEPARATOR + localPath));
                    FileOper.checkAndCreateForder(lastlocalPath);
                    downloadFiles(ftpServer, lastlocalPath, currentEnv);
                } catch (Exception ex) {
                    logger.fatal(ex.getMessage(), ex);
                }
            } else {
                //如果没有指定远端路径则认为需要处理本地文件
                List<String> fileDownloaded = new ArrayList<String>();
                currentEnv.put(MESConstants.FTP_COMMAND_RESULT_FILTER, logCommand.getResultFilter());
                String localPath = DSLUtil.getDefaultInstance().buildString(logCommand.getLocalPath(), currentEnv);
                String fileFilter = DSLUtil.getDefaultInstance().buildString(logCommand.getFilter(), currentEnv);
                fileDownloaded.addAll(FileOper.getSubFiles(localPath, fileFilter, logCommand.getGetSubDir()));
                logger.info("Found " + fileDownloaded.size() + " in path:" + localPath);
                String lastlocalPath = localPath + "out" + Envirment.PATH_SEPARATOR;
                FileOper.checkAndCreateForder(localPath);
                //为了避免数据冲突，输出目录需要为空
                if (deleteLocalFileBeforeDownload)
                    FileOper.delAllFile(localPath);
                FileParserWorker parserWorker = new FileParserWorker(logCommand.getLogParser()+"Ftp", currentEnv, lastlocalPath);
                parserWorker.start();
                parserWorker.addFiles(fileDownloaded);
                parserWorker.addFile("END");
                try {
                    parserWorker.waitAllFileProcessed();
                } catch (InterruptedException e) {
                	businessLogDao.insertLog(module_type, "处理本地文件异常", 1);
                    logger.error(e.getMessage(), e);
                }

            }
        }

        private void downloadFiles(final FtpServer ftpServer, final String lastlocalPath, final Map<String, String> newEnvs) {
            ExecutorService executor = Executors.newFixedThreadPool(FTP_POOL_NUM_PER_SERVER, new CustomThreadFactory(MESConstants.FTP_THREAD_NAME));

            long start = System.currentTimeMillis();
            for (int i = 0; i < FTP_POOL_NUM_PER_SERVER; i++) {
                logger.info("Start download thread " + i);
                Runnable runner = new Runnable() {
                    @Override
                    public void run() {
                        String localPath = lastlocalPath + "out" + Envirment.PATH_SEPARATOR;
                        FileOper.checkAndCreateForder(localPath);
                        //为了避免数据冲突，输出目录需要为空
                        if (deleteLocalFileBeforeDownload)
                            FileOper.delAllFile(localPath);
                        FileParserWorker parserWorker = null;
                        if (StringUtil.isNotEmpty(logCommand.getLogParser())) {
                            parserWorker = new FileParserWorker(logCommand.getLogParser(), newEnvs, localPath);
                            parserWorker.start();
                        }
                        logger.info("#################11111111111111111#################"+ftpServer.getName());
                        FtpConnection connection = null;
                        try {
                            connection = (FtpConnection) pool.borrowObject(ftpServer.getName());
                            String fileName = remoteFileList.take();
                            while (!"END".equals(fileName)) {
                                File remote = new File(fileName);
                                String localFileName = lastlocalPath + remote.getName();
                                if (FileOper.isFileExist(localFileName) && parserWorker != null) {
                                    parserWorker.addFile(localFileName);
                                } else if (connection.getFile(fileName, localFileName)) {
                                	logger.info("##################################"+StringUtils.substringBeforeLast(fileName, "/")+"/*");
                                	if(logCommand.getDeleteFileAfterGet()){
                                		connection.deleteFile(StringUtils.substringBeforeLast(fileName, "/")+"/*");
                                		connection.deleteDirectory(StringUtils.substringBeforeLast(fileName, "/"));
                                		logger.info("FtpServer deleteDirectory " + StringUtils.substringBeforeLast(fileName, "/"));
                                	}
                                    if (parserWorker != null)
                                        parserWorker.addFile(localFileName);
                                } else {
                                    //如果没有下载成功则重连再试
                                    logger.warn("Retry get file " + fileName);
                                    connection.reconnect();
                                    if (connection.getFile(fileName, localFileName)) {
                                    	if(logCommand.getDeleteFileAfterGet()){
                                    		connection.deleteFile(StringUtils.substringBeforeLast(fileName, "/")+"/*");
                                    		connection.deleteDirectory(StringUtils.substringBeforeLast(fileName, "/"));
                                    		logger.info("FtpServer deleteDirectory " + StringUtils.substringBeforeLast(fileName, "/"));
                                    	}
                                        if (parserWorker != null) parserWorker.addFile(localFileName);
                                    }
                                }
                                fileName = remoteFileList.take();
                            }
                            remoteFileList.put("END");
                            if (parserWorker != null) {
                                parserWorker.addFile("END");
                                parserWorker.waitAllFileProcessed();
                                Collection<String> resultFiles = parserWorker.getResultFiles();
                                List<String> fileParsed = Lists.newArrayList();
                                for (String fil : resultFiles) {
                                    fileParsed.add(lastlocalPath +Envirment.PATH_SEPARATOR +"out"+ Envirment.PATH_SEPARATOR +fil);
                                }
                                saveFiles(fileParsed);//文件入库
                            }
                        } catch (Exception e) {
                        	businessLogDao.insertLog(module_type, "FTP下载数据文件异常", 1);
                            logger.error(e.getMessage(), e);
                        } finally {
                            try {
                                if (connection != null)
                                    pool.returnObject(ftpServer.getName(), connection);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                };
                executor.execute(runner);
            }

            try {
                Thread.sleep(100);
                executor.shutdown();
                executor.awaitTermination(72000000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {
            }
            System.out.println("All Done in " + (System.currentTimeMillis() - start));
        }

        private void saveFiles(Collection<String> finalFiles) {
            //文件入库
            if (StringUtil.isNotEmpty(logCommand.getTargetDb()) && StringUtil.isNotEmpty(logCommand.getTargetTableMap())) {
                Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(logCommand.getTargetTableMap());
                try {
                    //DataAdapter adapter = DataAdapterPool.getDataAdapterPool(logCommand.getTargetDb()).getDataAdapter();
                    for (Map.Entry<String, String> targetTable : result.entrySet()) {
                        for (String resultFile : finalFiles) {
                            if (StringUtil.isMatch(resultFile, targetTable.getKey())) {
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
                    logger.fatal(e.getMessage(), e);
                }
            }
        }
    }
}

