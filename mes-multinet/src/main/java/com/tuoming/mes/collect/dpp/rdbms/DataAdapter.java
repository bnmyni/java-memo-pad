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

package com.tuoming.mes.collect.dpp.rdbms;

import java.sql.Connection;
import java.util.Map;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataRowHandler;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;
import com.tuoming.mes.collect.dpp.models.PageInfo;

/**
 * 数据适配器接口，通过该接口来实现对不同类型数据（数据库、文件等）的访问
 *
 * @author James Cheung
 * @version 1.0
 * @created 02-九月-2010 18:14:11
 */
public interface DataAdapter {
    /**
     * 执行当前适配器的初始化操作，一般实现类在构造期间会自动调用该方法进行初始化操作，同时也支持外部通过本方法对当前适配器重新进行初始化，
     * 一般当数据适配器关键属性被改 变时需要调用此方法来重新初始化，初始化后之前的属性将被重置。
     */
    public void initialize();

    /**
     * 加载文件到目标表
     *
     * @param filename
     * @param targetTable
     */
    public void loadfile(String filename, String targetTable);

    /**
     * 关闭当前适配器到数据库的连接
     */
    public void close();

    /**
     * 功能描述： 返回数据库连接
     *
     * @param
     * @return: void
     * @author: James Cheung
     * @version: 2.0
     */
    public void returnConnection();

    /**
     * 执行SQL语句
     *
     * @param sql 要执行的查询语句
     * @return: void
     * @author: James Cheung
     * @version: 2.0
     */
    public void executeNonQuery(String sql);

    /**
     * 顺序执行指定的SQL脚本文件，要求文件内每条SQL以分号结束，以"--$"标识该行需要执行表达式计算，以"//"、“#”、"--“标识注释行
     *
     * @param scriptFile 要执行的脚本绝对或相对路径
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @author James Cheung Date:Sep 1, 2012
     */
    public void excuteSqlFile(String scriptFile, Map context) throws Exception;

    public void executeQuery(String sql, DataRowHandler handler) throws Exception;

    public void executeQuery(String queryName, String sql, DataRowHandler handler) throws Exception;

    public DataTable queryTable(String queryName, String sql) throws Exception;

    public DataTable queryTable(String sql) throws Exception;

    public PageInfo queryPage(String sql, PageInfo pageInfo) throws Exception;

    public DataTable queryTable(String sql, int pageSize, int pageIndex) throws Exception;

    public PageInfo queryPage(String queryName, String sql, PageInfo pageInfo) throws Exception;

    public DataTable queryTable(String queryName, String sql, int pageSize, int pageIndex) throws Exception;

    public void lockToUpdate(String tableName);

    public void unlock(String table);

    public void disableKeyCheck();

    public void enableKeyCheck();

    /**
     * 功能描述： 获取当前数据适配器的状态信息，当适配器处于空
     * 闲状态时才能提供给外部使用,否则返回一个新的适配器实例
     *
     * @return: DbStat
     * @author: James Cheung
     * @version: 2.0
     */
    public DbStat getDbStat();

    /**
     * 功能描述： 设置当前数据适配器的状态信息，当适配器处于空闲状态时才能提供给外部使用,
     * 否则返回一个新的适配器实例
     *
     * @param dbStat 数据库状态描述对象
     * @return: void
     * @author: James Cheung
     * @version: 2.0
     */
    public void setDbStat(DbStat dbStat);

    /**
     * 功能描述： 获取当前数据库类型，当需要针对不同数据库做特殊处理时通过本方法获取相关信息。
     *
     * @param
     * @return: DbType
     * @author: James Cheung
     * @version: 2.0
     */
    public DbType getDbType();

    /**
     * 功能描述： 设置当前适配器对应的数据库类型
     *
     * @param dbType 数据库类型
     * @return: void
     * @author: James Cheung
     * @version: 2.0
     */
    public void setDbType(DbType dbType);

    /**
     * 功能描述： 获取用于当前适配器的数据库连接
     *
     * @return: Connection 数据库连接对象
     * @author: James Cheung
     * @version: 2.0
     */
    public Connection getConnection();

    /**
     * 功能描述： 设置当前数据适配器的连接
     *
     * @param
     * @return: void
     * @author: James Cheung
     * @version: 2.0
     */
    public void setConnection(Connection conn);

    public ConnectionStringSetting getConnectionStringSetting();

    public void setConnectionStringSetting(ConnectionStringSetting connectionStringSetting);

    /**
     * 功能描述： 获取当前适配器所在的连接池
     *
     * @param
     * @return: DataAdapterPool
     * @author: James Cheung
     * @version: 2.0
     */
    public DataAdapterPool getDataAdapterPool();

    /**
     * 设置当前适配器所在的连接池
     *
     * @return
     */
    public void setDataAdapterPool(DataAdapterPool dataAdapterPool);

    /**
     * 获取指定行数据对应的insert语句
     *
     * @param targetTable 目标表名
     * @param row         数据行
     * @return
     */
    public String genSqlInsert(String targetTable, DataRow row);

    public String genUpdate(String targetTable, DataRow row);

    /**
     * 测试连接是否可用
     *
     * @return
     */
    public boolean testAlive();

    public String formateValue(Object value, int dataType);

    public void renameTable(String oldName, String newName);

    public void dropTable(String tableName);

    public void emptyTable(String tableName);

}
