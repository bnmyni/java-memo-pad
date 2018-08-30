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
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.Envirment;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dao.DbQueryCommandDao;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.handles.DataRowToCsvHandle;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.models.DbQueryCommand;
import com.tuoming.mes.services.serve.DbQueryService;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * 数据查询接口通用处理进程,包括 <br/> 1. 多线程处理数据查询、处理、入库 <br/> 2. 自动适配数据库类型，执行建表、入库等操作 <br/>
 *  3. 自动处理数据库负担，查询及入库线程等均可以通过配置文件控制<br/> 4.
 * 自动处理历史数据维护、采集失败数据回滚等操作
 *
 * @see com.pyrlong.dpp.service.impl.AbstractBaseService
 */
@Scope("prototype")
@Component("DbQueryService")
public class DbQueryServiceImpl extends AbstractBaseService<DbQueryCommand, String> implements DbQueryService {

    DbQueryCommandDao dbQueryCommandDao;
    private final Logger logger = LogFacade.getLog4j(DbQueryServiceImpl.class);
    private   OperationLogDao operationLogDao;

    @Autowired
    @Qualifier("OperationLogDao")
    public void setServerDao(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    /**
     * 执行配置表内所有采集任务
     */
    public void loadAll() {
        List<DbQueryCommand> workers = dbQueryCommandDao.listAll();
        loadWorkThread(workers);
    }

    @Override
    public void load(String commandName) {
        DbQueryCommand command = dbQueryCommandDao.get(commandName);
        List<DbQueryCommand> commands = new ArrayList<DbQueryCommand>();
        commands.add(command);
        loadWorkThread(commands);
    }

    /**
     * 执行指定分组的数据库采集任务
     *
     * @param groupName 要执行的采集任务分组名，对应aos_db_command配置
     */
    @Override
    public void loadAll(String groupName) {
        List<DbQueryCommand> workers = dbQueryCommandDao.listGroup(groupName);
        loadWorkThread(workers);
    }

    /**
     * 执行指定分组的非采集 SQL
     *
     * @param groupName 要执行的分组名称
     */
    @Override
    public void executeAll(String groupName) {
        List<DbQueryCommand> workers = dbQueryCommandDao.listGroup(groupName);
        execute(workers);
    }

    @Override
    public void execute(String commandName) {
        DbQueryCommand command = dbQueryCommandDao.get(commandName);
        List<DbQueryCommand> commands = new ArrayList<DbQueryCommand>();
        commands.add(command);
        execute(commands);
    }

    private void execute(List<DbQueryCommand> commands) {
        for (DbQueryCommand queryCommand : commands) {
            executeNonQuery(queryCommand);
        }
    }

    private void executeNonQuery(DbQueryCommand queryCommand) {
        try {
            String sql = DSLUtil.getDefaultInstance().buildString(queryCommand.getQuerySql(), getEnvCopy());
            if (StringUtil.isNotBlank(queryCommand.getTargetDb())) {
                DataAdapterPool.getDataAdapterPool(queryCommand.getTargetDb()).getDataAdapter().executeNonQuery(sql);
            } else {
                if (StringUtil.isNotBlank(queryCommand.getTargetTableName())) {
                    Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(queryCommand.getTargetTableName());
                    if (result != null) {
                        for (Map.Entry<String, String> entry : result.entrySet()) {
                            DataAdapterPool.getDataAdapterPool(entry.getKey()).getDataAdapter().executeNonQuery(sql);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
        }
    }

    /**
     * 分线程处理传入的任务列表
     *
     * @param workers
     */
    private void loadWorkThread(List<DbQueryCommand> workers) {
        logger.info("Data acquisition thread begins execution....");
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(ConfigurationManager.getDefaultConfig().getInteger(
                MESConstants.QUERY_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.QUERY_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.QUERY_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()
        );
        threadPool.setThreadFactory(new CustomThreadFactory(MESConstants.QUERY_THREAD_NAME));

        logger.info("Create query threads...");
        for (DbQueryCommand command : workers) {
            try {
                logger.info("Add " + command.getQueryName());
                threadPool.execute(new DbQueryThread(command));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        threadPool.shutdown();
        logger.info("Wait for done...");
        //等待所有线程执行完成
        while (threadPool.getPoolSize() > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            }
        }
        logger.info("Data acquisition thread has finished");
    }

    @Autowired
    @Qualifier("DbQueryCommandDao")
    public void setBaseDao(BaseDao<DbQueryCommand, String> baseDao) {
        this.baseDao = baseDao;
        this.dbQueryCommandDao = (DbQueryCommandDao) baseDao;
    }

    /**
     * 执行数据库采集的线程实现
     */
    class DbQueryThread implements Runnable {
        DbQueryCommand worker;
        Map<String, String> currentEnv;
        private final Logger logger = LogFacade.getLog4j(DbQueryThread.class);

        public DbQueryThread(DbQueryCommand worker) {
            this.worker = worker;
            currentEnv = getEnvCopy();
            updateEnv(worker, currentEnv);
        }

        @Override
        public void run() {
            try {
                //处理前置任务
                doAction(worker.getPreAction(), currentEnv);
                //临时文件名
                String tempFile = AppContext.getCacheFileName("Database" + Envirment.PATH_SEPARATOR + worker.getSourceDbName() + Envirment.PATH_SEPARATOR + worker.getTargetTableName() + Envirment.PATH_SEPARATOR + worker.getTargetTableName() + DateUtil.currentDateString("yyyyMMddHHmmss") + "_" + Thread.currentThread().getId() + ".csv");
                //将查询结果保存到csv文件
                DataRowToCsvHandle handle = new DataRowToCsvHandle(tempFile);
                //执行数据查询
                String querySql = worker.getQuerySql();

                if (querySql.trim().startsWith("select") || querySql.trim().startsWith("SELECT")) {
                    if (StringUtil.isNotEmpty(worker.getIterator())) {
                        String queryCmd = "";
                        try {
                            queryCmd = DSLUtil.getDefaultInstance().relpaceVariable(worker.getIterator(), currentEnv);
                            DataTable table = (DataTable) DSLUtil.getDefaultInstance().compute(queryCmd, currentEnv);
                            for (DataRow row : table.getRows()) {
                                Map envs = mergerMap(row.getItemMap(), currentEnv);
                                querySql = DSLUtil.getDefaultInstance().buildString(worker.getQuerySql(), envs);
                                DataAdapterPool.getDataAdapterPool(worker.getSourceDbName()).getDataAdapter().executeQuery(querySql, handle);
                            }
                        } catch (Exception eex) {
                            logger.error(worker.getQueryName() + " :  iterator configuration error, check the table  aos_db_command\n" + queryCmd);
                            logger.error(eex.getMessage(), eex);
                        }
                    } else {
                        try {
                            querySql = DSLUtil.getDefaultInstance().buildString(worker.getQuerySql(), getEnvCopy());
                            DataAdapterPool.getDataAdapterPool(worker.getSourceDbName()).getDataAdapter().executeQuery(querySql, handle);
                        } catch (Exception ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                    handle.close();
                    //如果指定了目标数据库 和数据表 则调用入库方法 调用适配器的入库方法，将数据入库
                    //如果指定了目标数据库则执行普通入库操作，否则执行多表入库
                    if (StringUtil.isNotBlank(worker.getTargetDb())) {
                        DataAdapterPool.getDataAdapterPool(worker.getTargetDb()).getDataAdapter().loadfile(tempFile, worker.getTargetTableName());
                    } else if (StringUtil.isNotBlank(worker.getTargetTableName())) {
                        Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(worker.getTargetTableName());
                        if (result != null) {
                            for (Map.Entry<String, String> entry : result.entrySet()) {
                                DataAdapterPool.getDataAdapterPool(entry.getKey()).getDataAdapter().loadfile(tempFile, entry.getValue());
                            }
                        }
                    }
                } else {
                    executeNonQuery(worker);
                }
                //处理后置任务
                doAction(worker.getAfterAction(), currentEnv);
            } catch (Exception ex) {
                logger.fatal(ex.getMessage(), ex);
            } finally {
            }
            logger.info(worker.getQueryName() + " Done");
        }
    }
}

