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

package com.tuoming.mes.services.serve;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.scripts.AbstractEngine;
import com.tuoming.mes.collect.dao.CommandTemplateDao;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dpp.configuration.ConnectionStringSettingsCollection;
import com.tuoming.mes.collect.dpp.dao.BaseService;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.dpp.rdbms.DbOperation;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.collect.models.CommandTemplate;
import com.tuoming.mes.collect.models.OperationLog;
import com.tuoming.mes.execute.dao.AdjustCommandService;

/**
 * <p>AOS系统管理器工具类 ,本类主要目的是简化基于Python或者Groovy进行开发时需要了解的细节内容 将一些常用的操作通过本类封装之后提供给调用者。<br/> 本类的使用需要事前初始化一个实例对象
 * 下面为在Python下调用的实例:<br/> <p/> #首先在文件头引用本类<br/> from com.pyrlong.aos import AOSManger<br/> #初始化一个对象的实例<br/> aos =
 * AOSManager()<br/> #调用相关方法<br/> </p>
 *
 * @version 1.0.2
 * @since 1.0.1
 */
public class MESManager {

    private final static Logger logger = LogFacade.getLog4j(MESManager.class);
    /**
     * 记录系统操作日志的Dao对象实例，主要应用于系统提供的公共记录操作日志的接口方法
     */
    private static OperationLogDao operationLogDao;
    /**
     * 当前对象保存的上下文环境变量，该对象默认是从系统的 Envirment.getEnvs()获取初始值，并且由调用者通过相关方法更改配置内容。
     */
    Map<String, String> envs = null;
    Map<String, Object> cacheMap = new HashMap<String, Object>();
    CommandTemplateDao commandTemplateDao;
    /**
     * 用于封装的系统线程池对象，改对象每次用户调用时都会重新初始化，该线程池产生的线程名为com.pyrlong.aos.manager_pool前缀的，线程池初始化参数为：<br/> corePoolSize： 配置名为
     * pyrlong.aos.manager.thread_corePoolSize，默认为 5 <br/> maximumPoolSize： 配置名为 pyrlong.aos.manager.thread_maximumPoolSize，默认为
     * 10
     */
    ThreadPoolExecutor poolExecutor;
    private long batchId = 0;

    /**
     * 无参构造函数,使用当前环境变量初始化运行上下文,一般情况下都可以通过这个方法初始化本对象的实例
     */
    public MESManager() {
        envs = Envirment.getEnvs();
        batchId = DateUtil.getTimeinteger() / 1000;
        operationLogDao = AppContext.getBean(OperationLogDao.class);
        commandTemplateDao = AppContext.getBean(CommandTemplateDao.class);
    }

    /**
     * 在一些特点情况下，用户可以使用自己的Map对象初始化一个本对象的实例
     *
     * @param envs 指定的上下文集合初始化对象
     */
    public MESManager(Map<String, String> envs) {
        this.envs = envs;
    }

    /**
     * 读取用户输入方法，由于基于当前jython调用方法，实现读取用户输入比较麻烦，所以通过这个方法代替
     *
     * @param msg 提示信息，在接收用户输入前，先输出这个提示信息给用户
     * @return 返回用户输入的信息
     * @throws IOException
     * @since 1.0.1
     */
    public static String readInput(String msg) throws IOException {
        System.out.print(msg + " : ");
        //在Java当中，用户输入要通过InputStream(输入流)来获取。
        //System.in就是系统的输入流。缺省情况下这个输入流连接到控制台(命令行)。
        InputStreamReader is_reader = new InputStreamReader(System.in, "UTF-8");
        String str = new BufferedReader(is_reader).readLine();
        return str;
    }

    /**
     * 更新当前环境BatchID值
     */
    public void setNewBatchId() {
        batchId = DateUtil.getTimeinteger() / 1000;
    }

    public Map createLinkedMap() {
        return Maps.newLinkedHashMap();
    }

    public List<CommandTemplate> getCommandTemplate(String groupName) {
        return commandTemplateDao.getCommandTemplate(groupName);
    }

    public Object compute(String val, Map context) {
        return DSLUtil.getDefaultInstance().compute(val, context);
    }

    public String buildString(String val, Map context) {
        return DSLUtil.getDefaultInstance().buildString(val, context);
    }

    public Long getBatchId() {
        return batchId;
    }

    /**
     * 更新系统配置，为了保证执行时修改的系统配置已经生效，调用本方法时，系统首先根据aos_appsetting表刷新系统内参数值，然后和当前对象的环境 变量进行合并，合并原则是： 首先
     * 读取aos_appsetting，然后将读取的配置覆盖到当前实例的配置内
     */
    public void updateEnv() {
        //刷新系统配置
        Envirment.refreshConfig();
        Envirment.mergerMap(Envirment.getEnvs(), envs);
    }

    /**
     * 打印当前实例内的环境变量列表，本方法一般用于验证或调试程序时使用
     */
    public void printEnv() {
        for (Map.Entry<String, String> entry : envs.entrySet()) {
            logger.info(entry.getKey() + " = " + entry.getValue());
        }
        ConnectionStringSettingsCollection connectionStringSettings = (ConnectionStringSettingsCollection)
                ConfigurationManager.getDefaultConfig().getSection("connectionStrings");
        for (ConnectionStringSetting css : connectionStringSettings) {
            logger.info(css.getName() + "," + css.getUrl());
        }
    }

    /**
     * 记录操作日志的公共方法，相关数据记录到aos_oper_log表
     *
     * @param objectName  操作影响的对象标识或者说明，该值长度不能超过60个字符长度
     * @param opType      操作类型，调用者根据自己的需求可以对操作进行分类，方便日志后续的分类查询及显示
     * @param operContent 操作内容说明，填写本次操作的详细描述，不能超过1000个字符
     * @param result      操作结果，字符串类型，这里没有约定必须填写什么，由用户根据实际需要进行枚举
     * @since 1.0.1
     */
    public void LogOper(String objectName, String opType, String operContent, String result) {
        OperationLog operationLog = new OperationLog(objectName, opType, operContent);
        operationLog.setOperResult(result);
        logger.info(operationLog.toString());
        operationLogDao.save(operationLog);
    }

    /**
     * 通过SQL注册环境参数到当前配置内，这个方法可以用于在运行过程中根据配置批量更新系统配置值，比如针对不同对象或不同时间使用不同配置门限参数的情况
     *
     * @param sql 用于查询参数配置的SQL语句，注意需要要求查询的结果第一列为key，第二列为value
     */
    public void setParameters(String sql) throws Exception {
        DataTable dataTable = DbOperation.queryTable(DPPConstants.DB_DEFAULT_NAME, sql);
        if (dataTable != null) {
            if (dataTable.getColumns().size() >= 2) {
                for (DataRow row : dataTable.getRows()) {
                    String key = row.getValue(0) + "";
                    String value = row.getValue(1) + "";
                    if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
                        setEnv(key, value);
                        logger.debug("set " + key + "=" + value);
                    }
                }
            }
        }
    }

    /**
     * 获取指定文件路径对应的本地路径, 即将相对路径转换为绝对路径
     *
     * @param file 要转换的文件路径,相对于系统根路径的目录，,如 <p>data/test.csv</p>
     * @since 1.0.0
     */
    public String getPath(String file) {
        return Envirment.getHome() + file;
    }

    /**
     * 获取一个指定名称的服务对象
     *
     * @param serviceName 服务名，该名称必须是Spring注解为服务对象的服务
     * @return 指定服务名对应的对象实例
     * @since 1.0.0
     */
    public BaseService getService(String serviceName) {
        BaseService service = AppContext.getBean(serviceName);
        service.setEnv(envs);
        return service;
    }

    public Object getBean(String name) {
        return AppContext.getBean(name);
    }

    /**
     * 保存调整指令对象，本方法主要为了方便调用者保存生成的系统调整指令到指令调整表
     *
     * @param command 由外部传入的调整指令对象
     * @return 该方法调用后会返回保存后的对象，一般用于后续需要获取配置ID的情况，大多数情况可以不管这个返回值
     * @since 1.0.0
     */
    public AdjustCommand saveAdjustCommand(AdjustCommand command) {
        try {
            if (command == null) {
                logger.warn("AdjustCommand is null ,can't be saved.....");
                return null;
            }
            getService("AdjustCommandService").save(command);
            return command;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * 指令下发方法，将指定应用指定分组的指令下发，需要注意的是，这里的下发分为模拟下发和真实下发，切换方法通过在aos_appsetting配置pyrlong.aos.send_flag，该值默认为false,即模拟下发，
     * 如果需要实际下发需要将该值配置改为true，指令下发后，在aos_adjust_command表会更新指令下发状态和关了日志文件等信息。
     *
     * @param appName   要下发指令所属程序名，该参数由用户在生成指令时指定，主要用于区别不同应用的指令内容，方便管理及呈现
     * @param groupName 要下发的指令所属分组名，该参数由用户在生成指令时指定，该参数与appName 共同确定一组需要下发的指令
     */
    public void apply(String appName, String groupName) {
        AdjustCommandService commandService = AppContext.getBean(AdjustCommandService.class);
        commandService.apply(appName, groupName);
    }

    /**
     * 将指定文件导入到指定数据库的指定表内
     */
    public void loadfile(String dbName, String fileName, String tabName) throws Exception {
        DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter().loadfile(fileName, tabName);
    }

    /**
     * 从当前系统运行上下文环境获取指定名称的参数配置
     *
     * @param key          要获取的参数名
     * @param defaultValue 参数没有获取到时使用的默认值
     * @return 获取到的参数值
     */
    public Object getParameters(String key, Object defaultValue) {
        if (envs.containsKey(key))
            return envs.get(key);
        return ConfigurationManager.getDefaultConfig().getString(key, defaultValue + "");
    }

    /**
     * 从当前系统运行上下文环境获取指定名称的字符串参数配置
     *
     * @param key          要获取的参数名
     * @param defaultValue 默认值
     * @return 参数值，如果没有找到则返回默认值
     */
    public String getParameters(String key, String defaultValue) {
        if (envs.containsKey(key))
            return envs.get(key);
        return ConfigurationManager.getDefaultConfig().getString(key, defaultValue);
    }

    /**
     * 从当前系统运行上下文环境获取指定名称的Double参数配置
     *
     * @param key          要获取的参数名
     * @param defaultValue 默认值
     * @return 参数值，如果没有找到则返回默认值
     */
    public Double getParameters(String key, Double defaultValue) {
        if (envs.containsKey(key))
            return Double.parseDouble(envs.get(key));
        return ConfigurationManager.getDefaultConfig().getDouble(key, defaultValue);
    }

    /**
     * 从当前系统运行上下文环境获取指定名称的Long参数配置
     *
     * @param key          要获取的参数名
     * @param defaultValue 默认值
     * @return 参数值，如果没有找到则返回默认值
     */
    public Long getParameters(String key, Long defaultValue) {
        if (envs.containsKey(key))
            return Long.parseLong(envs.get(key));
        return ConfigurationManager.getDefaultConfig().getLong(key, defaultValue);
    }

    /**
     * 从当前系统运行上下文环境获取指定名称的Integer参数配置
     *
     * @param key          要获取的参数名
     * @param defaultValue 默认值
     * @return 参数值，如果没有找到则返回默认值
     */
    public Integer getParameters(String key, Integer defaultValue) {
        if (envs.containsKey(key))
            return Integer.parseInt(envs.get(key));
        return ConfigurationManager.getDefaultConfig().getInteger(key, defaultValue);
    }

    /**
     * 更新当前环境指定参数的值
     *
     * @param key   参数名
     * @param value 新的参数值
     */
    public void setEnv(String key, String value) {
        envs.put(key, value);
    }

    /**
     * 判断当前环境内是否存在指定名称的配置
     *
     * @param key 要判断是否存在的 key值
     * @return 如果存在返回true, 否则返回false
     */
    public boolean has(String key) {
        return envs.containsKey(key);
    }

    /**
     * 获取指定名称的配置值
     *
     * @param key 要获取的参数名称
     * @return 给定名称对应的参数值, 如果不存在则返回空字符串
     */
    public String getEnv(String key) {
        return getParameters(key, "");
    }

    /**
     * 封装的用于python多线程处理的方法，使用方法如下：<br/> <code> aos=AOSManager() <br/>aos.executeWork("thrd","dowork('A')")<br/>
     * aos.executeWork("thrd","dowork('B')")<br/> aos.executeWork("thrd","dowork('C')")<br/> aos.waitWorkDone()<br/>
     * </code>
     *
     * @param sourceFile
     * @param method
     */
    public void executeWork(final String sourceFile, final String method, final Object... paras) {
        if (poolExecutor == null)
            poolExecutor = getNewThreadPoolExecutor();
        poolExecutor.execute(new Thread() {
            public void run() {
                try {
                    logger.info("Run " + sourceFile + "-" + method);
                    AbstractEngine engine = AbstractEngine.getEngine("python");
                    engine.eval("from " + sourceFile + " import *");
                    String finalCall = method + "(";
                    String parPrefix = "aos_par_";
                    for (int i = 0; i < paras.length; i++) {
                        String parName = parPrefix + i;
                        engine.setVariables(parName, paras[i]);
                        finalCall = finalCall + parName + ",";
                    }
                    if (finalCall.endsWith(","))
                        finalCall = finalCall.substring(0, finalCall.length() - 1);
                    finalCall += ")";
                    engine.eval(finalCall);
                    logger.info(sourceFile + "-" + method + " done!");
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    /**
     * 与executeWork配合使用，用于启动所有处理线程并等待线程执行结束
     */
    public void waitWorkDone() {
        poolExecutor.shutdown();
        //等待所有线程执行完成
        logger.info("Wait workers done...");
        while (poolExecutor.getPoolSize() > 0) {
            try {
                Thread.currentThread().sleep(10);
                logger.debug("wait thread done ....");
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            }
        }
        poolExecutor.shutdownNow();
        poolExecutor = null;
        logger.info("All workers done!");
    }

    public void clearCache() {
        cacheMap.clear();
    }

    public Object getCache(String name) {
        return cacheMap.get(name);
    }

    public boolean hasCache(String name) {
        return cacheMap.containsKey(name);
    }


    public void setCache(String name, Object val) {
        cacheMap.put(name, val);
    }

    /**
     * 获取一个新的线程池对象
     *
     * @return
     */
    private ThreadPoolExecutor getNewThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.MANAGER_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.MANAGER_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.MANAGER_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        threadPoolExecutor.setThreadFactory(new CustomThreadFactory(MESConstants.MANAGER_THREAD_NAME));
        return threadPoolExecutor;
    }
}
