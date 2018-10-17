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

import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.pyrlong.Envirment;
import com.pyrlong.collection.PMap;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.exception.PyrlongException;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.net.InterruptFoundException;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dao.CommandMapDao;
import com.tuoming.mes.collect.dao.LogCommandDao;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dao.ServerDao;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.TextLogParserDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.dpp.dao.impl.PairedObject;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.file.TextFileProcessor;
import com.tuoming.mes.collect.dpp.file.handles.AbstractTextLineHandle;
import com.tuoming.mes.collect.dpp.handles.DataRowToCsvHandle;
import com.tuoming.mes.collect.dpp.models.TextLogParser;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.models.LogCommand;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.collect.models.Server;
import com.tuoming.mes.execute.dao.impl.AbstractCommandSendThread;
import com.tuoming.mes.services.serve.LogCommandService;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.services.serve.ServerService;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.util.HttpUtil;

/**
 * 实现基于采集指令获取信息的服务，主要实现功能包括：<br/> 1. 根据配置执行指定的指令获取日志文件到本地，并调用配置的解析器进行解析入库操作 <br/> 2. 针对同一指令在不同服务器的采集支持并发执行，统一解析入库<br/>
 * 3.采集过程中的各种操作记录日志，包括采集内容、成功与否等<br/> 4. 注意通过本服务调度的采集命令必须是在aos_command_map中注册了的命令，如果没有注册则不会执行 <br/> 5. 本类默认缓存路径为 *
 * 程序部署目录的data/年月日/服务器名/文件名<br/> 6. 本实现涉及配置参数需要配置到dpp.properties文件或aos_appsettings表 <br/> <p> 注意： <br/>
 * 针对同一批（组）采集指令，每次根据配置启动相应的并发线程进行采集，待本组所有采集任务结束后再调用解析入库方法执行解析和入库操作，也就是说如果你希望 一些参数能先完成采集然后基于这个结果执行后续采集，那么
 * 需要将它们划分为不同的分组，然后分别调用。 </p>
 *
 * @see java.util.concurrent.ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit,
 * java.util.concurrent.BlockingQueue)
 */
@Scope("prototype")
@Component("LogCommandService")
public class LogCommandServiceImpl extends AbstractBaseService<LogCommand, String> implements LogCommandService {
    private final static Logger logger = LogFacade.getLog4j(LogCommandServiceImpl.class);
    private static String LOGPARSER = MESConstants.LOG_PARSER;
    private static String LOGFILE = MESConstants.LOGFILE;
    List<PairedObject> serverRedo = new ArrayList<PairedObject>();
    private TextLogParserDao textLogParserDao;
    private LogCommandDao logCommandDao;
    private ServerDao serverDao;
    private OperationLogDao operationLogDao;
    private CommandMapDao commandMapDao;
    /**
     * 记录日志文件及对应解析器标识 ,不同线程的命令日志文件通过这个对象保存
     */
    private Map<String, Map<String, String>> logParsers = new HashMap<String, Map<String, String>>();

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
    @Qualifier("TextLogParserDao")
    public void setServerDao(TextLogParserDao textLogParserDao) {
        this.textLogParserDao = textLogParserDao;
    }

    @Autowired
    @Qualifier("ServerDao")
    public void setNeServerDao(ServerDao serverDao) {
        this.serverDao = serverDao;
    }

    @Autowired
    @Qualifier("LogCommandDao")
    public void setBaseDao(BaseDao<LogCommand, String> baseDao) {
        this.baseDao = baseDao;
        this.logCommandDao = (LogCommandDao) baseDao;
    }

    @Override
    public void queryAll(String groupName, long batch) {
        logger.info("start cm telnet collect ,group name is " +  groupName);
        List<LogCommand> commands = logCommandDao.getCommands(groupName);
        query(commands, batch);
    }

    @Override
    public void queryAllByLogParser(String logParser, long batch) {
        List<LogCommand> commands = logCommandDao.getCommandList(logParser);
        query(commands, batch);
    }

    @Override
    public void queryAll(ObjectType objectType, Manufacturers manufacturers, long batch) {
        List<LogCommand> commands = logCommandDao.getCommands(objectType, manufacturers);
        query(commands, batch);
    }

    @Override
    public void query(String queryCommand, long batch) {
        LogCommand command = logCommandDao.get(queryCommand);
        if (command == null) {
            logger.warn("The command name you specify does not exist :" + queryCommand);
            return;
        }
        List<LogCommand> commands = new ArrayList<LogCommand>();
        commands.add(command);
        query(commands, batch);
    }

    @Override
    public void query(Server[] servers, String queryCommand, long batch) {
        if (servers.length == 0 || StringUtil.isEmpty(queryCommand)) {
            logger.warn("Servers/query command is null or empty ");
            return;
        }
        ThreadPoolExecutor threadPool = getNewThreadPoolExecutor();
        query(servers, logCommandDao.get(queryCommand), threadPool, batch);
        waitDone(threadPool);
        parseFile();
    }


    @Override
    public void query(String server, String queryCommand, long batch) {
        List<LogCommand> commands = new ArrayList<LogCommand>();
        commands.add(logCommandDao.get(queryCommand));
        ThreadPoolExecutor threadPool = getNewThreadPoolExecutor();
        query(serverDao.get(server), commands, threadPool, batch);
        waitDone(threadPool);
        parseFile();
    }

    @Override
    public void query(Server server, long batch) {
        List<LogCommand> commands = logCommandDao.getCommands(server.getObjectType(), server.getManufacturers());
        ThreadPoolExecutor threadPool = getNewThreadPoolExecutor();
        query(server, commands, threadPool, batch);
        waitDone(threadPool);
        parseFile();
    }

    @Override
    public void query(Server[] servers, long batch) {
        for (Server server : servers) {
            query(server, batch);
        }
    }

    private synchronized void addFileToParser(String filename, Map<String, String> env) {
        logParsers.put(filename, env);
    }

    private synchronized void removeFile(String filename) {
        if (logParsers.containsKey(filename))
            logParsers.remove(filename);
    }

    public void parse(String logFile, String logParser) {
        List<String> files = new ArrayList<String>();
        files.add(logFile);
        parse(files, logParser);
    }

    @Override
    public void parse(List<String> logFile, String logParser) {
        TextLogParser textLogParser = textLogParserDao.get(logParser);
        if (textLogParser != null) {
            AbstractTextLineHandle handle = AppContext.getBean(textLogParser.getParseHandle());
            if (handle != null) {
                try {
                    String csvFile = AppContext.getCacheFileName(textLogParser.getName() + Envirment.PATH_SEPARATOR + textLogParser.getName() + "_" + DateUtil.getNow("yyyyMMddHHmm") + "_" + Thread.currentThread().getId() + ".csv");
                    Map<String, Map<String, String>> fileMap = new HashMap<String, Map<String, String>>();
                    for (String f : logFile)
                        fileMap.put(f, getEnvCopy());
                    TextFileProcessor textFileProcessor = new TextFileProcessor(fileMap);
                    handle.setLogParser(textLogParser);
                    handle.buildTable();
                    DataRowToCsvHandle toCsvHandle = new DataRowToCsvHandle(csvFile);
                    handle.setDataRowHandle(toCsvHandle);
                    textFileProcessor.addHandle(handle);
                    textFileProcessor.run();
                    //调用适配器的入库方法，将数据入库
                    //如果指定了目标数据库则执行入库操作
                    if (StringUtil.isNotBlank(textLogParser.getTargetDb())) {
                        DataAdapterPool.getDataAdapterPool(textLogParser.getTargetDb()).getDataAdapter().loadfile(csvFile, textLogParser.getTargetTable());
                    } else if (StringUtil.isNotBlank(textLogParser.getTargetTable())) {
                        Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(textLogParser.getTargetTable());
                        if (result != null) {
                            for (Map.Entry<String, String> entry : result.entrySet()) {
                                DataAdapterPool.getDataAdapterPool(entry.getKey()).getDataAdapter().loadfile(csvFile, entry.getValue());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    private void query(Server[] servers, LogCommand command, ThreadPoolExecutor threadPool, long batch) {
        for (Server server : servers) {
            try {
                threadPool.execute(new QueryProcessor(serverRedo, server, command, batch));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void query(Server server, Collection<LogCommand> commandList, ThreadPoolExecutor threadPool, long batch) {
        //启动处理线程
        for (LogCommand command : commandList) {
            try {
                threadPool.execute(new QueryProcessor(server, command, batch));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private ThreadPoolExecutor getNewThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        threadPoolExecutor.setThreadFactory(new CustomThreadFactory(MESConstants.LOG_THREAD_NAME));
        return threadPoolExecutor;
    }

    /**
     * 采集命令执行的入口方法，本方法接收一系列需要采集的指令，
     * 然后根据指令关联的对象类型执行采集指令并关联解析器执行解析操作
     * 本方法实现步骤如下：
     * 对传入需执行指令按照对象类型进行分类 对象类型-关联指令 获取每个对象类型对应的对象列表 然后分别执行 采集指令
     * @param commandList 需要处理的指令集合
     */
    private void query(List<LogCommand> commandList, long batch) {
        if (commandList == null || commandList.size() == 0) {
            return;
        }
        ThreadPoolExecutor threadPool = getNewThreadPoolExecutor();
        serverRedo = new ArrayList<>();
        //循环获取每个类型的对象列表并执行指令
        for (LogCommand command : commandList) {
            List<Server> servers = serverDao.getNeServers(command.getObjectType(), command.getManufacturers());
            System.out.println("Query thread started," + servers.size() + " servers ready to go");
            //mes_servers表结果集，mes_log_command表结果集，线程池，Constant。CURRENT_BATCH = 1
            Server[] servers1 = new Server[servers.size()];
            servers.toArray(servers1);
            query(servers1, command, threadPool, batch);
        }
        waitDone(threadPool);
        threadPool = getNewThreadPoolExecutor();
        try {
            //等待半分钟之后重试失败任务
            if (serverRedo.size() > 0)
                Thread.currentThread().sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("<<<<<<<Server redo : " + serverRedo.size());
        for (PairedObject pairedObject : serverRedo) {
            try {
                threadPool.execute(new QueryProcessor((Server) pairedObject.getObject1(), (LogCommand) pairedObject.getObject2(), batch));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        serverRedo = null;
        waitDone(threadPool);
        parseFile();


    }

    private void waitDone(ThreadPoolExecutor threadPool) {
        threadPool.shutdown();
        //等待所有线程执行完成
        while (threadPool.getPoolSize() > 0) {
            try {
                Thread.currentThread().sleep(10);
                //logger.debug("wait thread done ....");
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            }
        }
        threadPool.shutdownNow();
    }

    private void parseFile() {
        logger.info(">>>>>Start parsing file:" + logParsers.size());
        //重新打开一个处理线程池
        ThreadPoolExecutor threadPool = getNewThreadPoolExecutor();
        Map<String, Map<String, Map<String, String>>> loggerFiles = new HashMap<>();
        /*
            对生成文件执行解析 文件名/解析器对象
            这里处理的主要目的是 将使用相同解析器的文件同时进行解析并保存到同一个目标文件，然后入库 目的是减少对目标表的占用时间
            然而由于每个文件的运行环境参数是不同的，也就是说 解析文件时需要同时带着 日志文件对应的环境变量
         */
        for (Map.Entry<String, Map<String, String>> file : logParsers.entrySet()) {
            if (file.getValue() != null) {
                //如果当前配置了解析器
                if (file.getValue().containsKey(LOGPARSER) && file.getValue().get(LOGPARSER) != null) {
                    //处理同一个文件具有多个解析器的情况
                    String[] parsers = file.getValue().get(LOGPARSER).toString().split("#");
                    for (String parser : parsers) {
                        if (StringUtil.isEmpty(parser)) {
                            logger.info("获取解析器失败");
                            continue;
                        }
                        if (!loggerFiles.containsKey(parser)) {
                            loggerFiles.put(parser, new HashMap<String, Map<String, String>>());
                        }
                        loggerFiles.get(parser).put(file.getKey(), PMap.getMapCopy(file.getValue()));
                        logger.debug("Add " + file.getKey());
                    }
                }
            }
        }
        //对分类的文件调用解析器分别解析
        for (String parser : loggerFiles.keySet()) {
            try {
                threadPool.execute(new ParserProcessor(parser, loggerFiles.get(parser)));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        waitDone(threadPool);
        logParsers = new HashMap<>();
        logger.info("All file parsed");
    }

    /**
     * 处理指令下发，日志生成等相关操作
     */
    class ParserProcessor implements Runnable {
        private final Logger logger = LogFacade.getLog4j(ParserProcessor.class);
        String parser;
        Map<String, Map<String, String>> files;

        public ParserProcessor(String parser, Map<String, Map<String, String>> files) {
            this.parser = parser;
            this.files = files;
        }

        private synchronized TextLogParser getTextLogParser() {
            return textLogParserDao.get(parser);
        }


        @Override
        public void run() {
            TextLogParser textLogParser = getTextLogParser();
            if (textLogParser != null) {
                logger.info("解析log文件：" + textLogParser);
                AbstractTextLineHandle handle = AppContext.getBean(textLogParser.getParseHandle());
                if (handle != null) {
                    try {
                        String csvFile = AppContext.getCacheFileName("Log" + Envirment.PATH_SEPARATOR + "out" + Envirment.PATH_SEPARATOR + textLogParser.getName() + Envirment.PATH_SEPARATOR + textLogParser.getName() + "_" + DateUtil.getNow("yyyyMMddHHmm") + "_" + Thread.currentThread().getId() + ".csv");
                        TextFileProcessor textFileProcessor = new TextFileProcessor(files);
                        handle.setLogParser(textLogParser);
                        handle.buildTable();
                        DataRowToCsvHandle toCsvHandle = new DataRowToCsvHandle(csvFile);
                        handle.setDataRowHandle(toCsvHandle);
                        textFileProcessor.addHandle(handle);
                        textFileProcessor.run();
                        //TODO 批文件解析完成
                        submitCollectState(Constant.Log_COLLECT_ANALYZE_END);
                        //调用适配器的入库方法，将数据入库
                        //如果指定了目标数据库则执行入库操作
                        if (StringUtil.isNotBlank(textLogParser.getTargetDb())) {
                            DataAdapterPool.getDataAdapterPool(textLogParser.getTargetDb()).getDataAdapter().loadfile(csvFile, textLogParser.getTargetTable());
                        } else if (StringUtil.isNotBlank(textLogParser.getTargetTable())) {
                            Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(textLogParser.getTargetTable());
                            if (result != null) {
                                for (Map.Entry<String, String> entry : result.entrySet()) {
                                    DataAdapterPool.getDataAdapterPool(entry.getKey()).getDataAdapter().loadfile(csvFile, entry.getValue());
                                }
                            }
                        }

                        //TODO 入库完成ywy
                        submitCollectState(Constant.Log_COLLECT_END);


                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

        private void submitCollectState(int state) {
            boolean enableMontior = ConfigurationManager.getDefaultConfig().getBoolean(Constant.ENABLE_MONTIOR, true);
            if (!enableMontior) {
                return;
            }
            List<Map<String, String>> paramList = new ArrayList<Map<String, String>>();
            for (Entry<String, Map<String, String>> entry : files.entrySet()) {
                Map<String, String> envMap = entry.getValue();
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("commandname", envMap.get("commandName"));
                paramMap.put("servername", envMap.get("serverName"));
                paramMap.put("username", envMap.get("userName"));
                paramMap.put("ip", envMap.get("ip"));
                paramMap.put("port", envMap.get("port"));
                paramMap.put("datatype", Constant.CM);
                paramMap.put("grading", Constant.DAYUNIT);
                paramMap.put("status", state + "");
                paramMap.put("type", Constant.MONTIOR_TYPE_COLLECT);
                String[] zs_factory = envMap.get("manufacturers").split("_");
                paramMap.put("zs", zs_factory[0]);
                paramMap.put("factory", zs_factory[1]);
                paramMap.put("dlfs", Constant.TELNET);
                paramList.add(paramMap);
            }
            HttpUtil.post(ConfigurationManager.getDefaultConfig().getString(Constant.MONTIOR_COLLECT_STATE_URL, null), "collectstate=" + JSONSerializer.toJSON(paramList).toString());
        }
    }

    /**
     * 任务处理线程
     */
    class QueryProcessor extends AbstractCommandSendThread implements Runnable {
        private final Logger logger = LogFacade.getLog4j(QueryProcessor.class);
        Server neServer;
        LogCommand command;
        long batch;
        Map<String, String> currentEnv;
        boolean iterator = true;
        private List<PairedObject> serverRedo;

        public QueryProcessor(Server server, LogCommand command, long batch, Map<String, String> mapSet) {
            iterator = false;
            this.neServer = server;
            this.command = command;
            this.batch = batch;
            setCommandMapDao(commandMapDao);
            currentEnv = mergerMap(mapSet, getEnvCopy());
            updateEnv(server, currentEnv);
            updateEnv(command, currentEnv);
            updateEnv(server.getCustomEnv(), currentEnv);
        }

        //mes_servers表结果（一条），mes_log_command表结果集，Constant。CURRENT_BATCH = 1
        public QueryProcessor(List<PairedObject> serverRedo, Server server, LogCommand command, long batch) {
            this(server, command, batch);
            this.serverRedo = serverRedo;
        }

        public QueryProcessor(Server server, LogCommand command, long batch) {
            this.neServer = server;
            this.command = command;
            this.batch = batch;
            currentEnv = getEnvCopy();
            updateEnv(server, currentEnv);
            setCommandMapDao(commandMapDao);
            updateEnv(command, currentEnv);
            updateEnv(server.getCustomEnv(), currentEnv);
        }

        public void run() {
            String logFile = AppContext.getCacheFileName("Log" + Envirment.PATH_SEPARATOR + neServer.getServerName() + "/" + neServer.getServerName() + "_" + command.getName() + "_" + DateUtil.getNow("yyyyMMddHHmm") + ".log");
            System.out.println("start create log file :" + logFile);
            try {
                submitCollecteState(Constant.Log_COLLECT_BEGIN_LINK);
                ServerService serverService = ServerConnectPool.getServerServiceFromPool(neServer.getServerName(), logFile);
                String[] cmds = StringUtil.split(command.getCommand(), "#");
                doAction(command.getPreAction(), currentEnv);
                if (!serverService.isEnabled()) {
                    setCommandRedo(logFile);
                    ServerConnectPool.releaseServer(serverService);
                    submitCollecteState(Constant.Log_COLLECT_LINK_FAIL);
                    return;
                }
                //TODO cmd登陆成功ywy
                submitCollecteState(Constant.Log_COLLECT_LINK_SUCCESS);
                //设置输出文件
                //serverService.setOutputFile(logFile);
                //serverService.login();
                // 这里处理采集指令下发操作
                //如果配置了迭代方式
                boolean finishMark = true;
                if (StringUtil.isNotEmpty(command.getIterator()) && iterator) {
                    String queryCmd = "";
                    try {
                        queryCmd = DSLUtil.getDefaultInstance().relpaceVariable(command.getIterator(), currentEnv);
                        DataTable table = (DataTable) DSLUtil.getDefaultInstance().compute(queryCmd, currentEnv);
                        int interruptFoundCount = 0;
                        if (table.getRows().size() == 0)
                            logger.warn(String.format("%s table is empty!!", command.getName()));
                        for (DataRow row : table.getRows()) {
                            Map envs = mergerMap(row.getItemMap(), currentEnv);
                            try {
                                finishMark = sendCommands(cmds, envs, serverService, logFile) && finishMark;
                            } catch (InterruptFoundException e) {
                                interruptFoundCount++;
                                if (interruptFoundCount > 5) {
                                    logger.error("Interrupted more than five times ,break all");
                                } else {
                                    logger.warn("Interrupted found need reconnect");
                                    ServerConnectPool.reconnect(neServer.getServerName(), logFile, serverService);
//									serverService.reconnect();
                                    finishMark = sendCommands(cmds, envs, serverService, logFile) && finishMark;
                                }
                            }
//                            if (!sendCommands(cmds, envs, serverService, logFile)) {
//                                finishMark = false;
//                            }
//                            finishMark = finishMark && true;
                        }
                    } catch (Exception eex) {
                        logger.error(command.getName() + " : iterator configuration error, check the table  aos_log_command\n" + queryCmd);
                        logger.error(eex.getMessage(), eex);
                    }
                } else {
                    try {
                        finishMark = sendCommands(cmds, currentEnv, serverService, logFile) && finishMark;
                    } catch (InterruptFoundException e) {
                        finishMark = false;
                    }
//                    //单指令
//                    if (!sendCommands(cmds, currentEnv, serverService, logFile)) {
//                        finishMark = false;
//                    }
//                    finishMark = finishMark && true;
                    //如果指令执行失败则标识为需要重复执行一次,目前只在单指令这里做判断，并且只重试一次
                    if (!finishMark) {
                        logger.warn("Command execute fail,add to redo list");
                        setCommandRedo(logFile);
                    }
                }
                //serverService.logout();
                ServerConnectPool.releaseServer(serverService);
                //设置文件对应的解析器
                setEnv(LOGFILE, logFile, currentEnv);
                setEnv("BATCH", batch + "", currentEnv);
                addFileToParser(logFile, currentEnv);
                doAction(command.getAfterAction(), currentEnv);

                //TODO 下载完成ywy
                submitCollecteState(Constant.Log_COLLECT_DONWLOAD_END);

            } catch (PyrlongException e) {
                logger.fatal(e.getMessage(), e);
                setCommandRedo(logFile);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        private void submitCollecteState(int state) {
            boolean enableMontior = ConfigurationManager.getDefaultConfig().getBoolean(Constant.ENABLE_MONTIOR, true);
            if (!enableMontior) {
                return;
            }
            Map<String, String> paramMap = new HashMap<String, String>();
            List<Map<String, String>> paramList = new ArrayList<Map<String, String>>();
            paramMap.put("commandname", command.getName());
            paramMap.put("servername", neServer.getServerName());
            paramMap.put("username", neServer.getUsername());
            paramMap.put("ip", neServer.getIp());
            paramMap.put("port", neServer + "");
            paramMap.put("datatype", Constant.CM);
            paramMap.put("grading", Constant.DAYUNIT);
            paramMap.put("type", Constant.MONTIOR_TYPE_COLLECT);
            paramMap.put("status", state + "");
            String[] zs_factory = command.getManufacturers().toString().split("_");
            paramMap.put("zs", zs_factory[0]);
            paramMap.put("factory", zs_factory[1]);
            paramMap.put("dlfs", Constant.TELNET);
            paramList.add(paramMap);
            HttpUtil.post(ConfigurationManager.getDefaultConfig().getString(Constant.MONTIOR_COLLECT_STATE_URL, null), "collectstate=" + JSONSerializer.toJSON(paramList).toString());

        }

        /**
         * 标识指定指令为需要重新执行的指令
         *
         * @param logfile 要删除的文件
         */
        private void setCommandRedo(String logfile) {
            if (serverRedo != null)
                synchronized (serverRedo) {
                    serverRedo.add(new PairedObject(this.neServer, this.command));
                    removeFile(logfile);
                }
        }

        @Override
        protected void doSomeAction(String cmd, Map<String, String> envs) {
            doAction(cmd, envs);
        }
    }
}
