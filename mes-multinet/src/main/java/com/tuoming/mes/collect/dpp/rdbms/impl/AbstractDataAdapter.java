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

package com.tuoming.mes.collect.dpp.rdbms.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pyrlong.Envirment;
import com.pyrlong.collection.FixSizeMap;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.CharacterSetToolkit;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.dao.TableInfoDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.datatype.DataColumn;
import com.tuoming.mes.collect.dpp.datatype.DataKey;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataRowHandler;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.datatype.DataTypes;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;
import com.tuoming.mes.collect.dpp.models.PageInfo;
import com.tuoming.mes.collect.dpp.models.TableInfo;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapter;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.dpp.rdbms.DbStat;
import com.tuoming.mes.collect.dpp.rdbms.DbType;
import com.tuoming.mes.collect.dpp.rdbms.ExecuteResult;

/**
 * 数据库适配器抽象类
 */
public abstract class AbstractDataAdapter implements DataAdapter {

    private static FixSizeMap<String, Object> synchronizedHandle = new FixSizeMap<String, Object>();
    protected static final Logger logger = LogFacade.getLog4j(AbstractDataAdapter.class);
    private static Boolean ExcuteSqlFileBreakOnError = true;
    protected Connection connection = null;
    int connCheckTime = ConfigurationManager.getDefaultConfig().getInteger(DPPConstants.DB_CONNECTION_CHECK, 120000);
    private DbType dbType = null;
    private DbStat dbStat = DbStat.Closed;
    private DataAdapterPool dataAdapterPool = null;
    protected static final String split = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_SPLIT_CHAR, ",");
    protected static final String enclosed = ConfigurationManager.getDefaultConfig().getString(DPPConstants.LOAD_DATA_ENCLOSED, "~");
    private long lastVisited = DateUtil.getTimeinteger();
    protected static String csvFileEncoding = "utf8";
    private final static long queryLongTime = 10000;
    private final static long queryVeryLongTime = 60000;

    public static int getDataTypeFromJdbcType(int JDBC_TPYE) {
        return JDBC_TPYE;
    }

    public AbstractDataAdapter() {
        csvFileEncoding = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_ENCODING, "utf8");
        synchronizedHandle.setMaxSize(100);
    }

    protected Object getSynchronizedHandle(String tableName) {
        String ky = connectionStringSetting.getName() + tableName;
        if (!synchronizedHandle.containsKey(ky)) {
            synchronizedHandle.put(ky, new Object());
        }
        return synchronizedHandle.get(ky);
    }

    @Override
    public void disableKeyCheck() {

    }

    @Override
    public void enableKeyCheck() {

    }

    @Override
    public void lockToUpdate(String tableName) {

    }

    @Override
    public void unlock(String table) {

    }

    /**
     * 针对指定连接执行指定SQL语句
     *
     * @param sql
     * @author James Cheung Date:Jul 15, 2012
     */
    public void executeNonQuery(String sql) {
        Statement st = null;
        logger.debug(sql);
        Long start = DateUtil.getTimeinteger();
        try {
            // 初始化数据库连接
            Connection connection = getConnection();
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.setQueryTimeout(ConfigurationManager.getDefaultConfig().getInteger(DPPConstants.DB_QUERY_TIME_OUT, 60000));
            st.executeUpdate(sql);
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
            logger.error(sql);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            returnConnection();
            checkTime(start, sql);
        }
    }

    private void checkTime(Long start, String sql) {
        Long end = DateUtil.getTimeinteger() - start;
        if (end > queryVeryLongTime) {
            logger.warn("*****SQL Execute over 1min******" + sql);
        } else if (end > queryLongTime) {
            logger.warn("====SQL Execute over 10s======" + sql);
        }
    }

    /**
     * 顺序执行指定的SQL脚本文件，要求文件内每条SQL以分号结束，以"--$"标识该行需要执行表达式计算，以"//"、“#”、"--“标识注释行
     *
     * @param scriptFile 要执行的脚本绝对或相对路径
     * @param context    当前运行环境上下文对象
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @author James Cheung Date:Sep 1, 2012
     */
    public void excuteSqlFile(String scriptFile, Map context) throws Exception {
        scriptFile = Envirment.findFile(scriptFile);
        logger.debug("execute " + scriptFile);
        if (context == null) context = Envirment.getEnvs();
        ExcuteSqlFileBreakOnError = ConfigurationManager.getDefaultConfig().getBoolean(DPPConstants.DB_EXECUTE_FILE_BREAK_ON_ERROR, true);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile), CharacterSetToolkit.UTF_8));
        String line = null;
        StringBuilder cmd = new StringBuilder();
        // 初始化数据库连接
        Connection connection = getConnection();
        Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        st.setQueryTimeout(ConfigurationManager.getDefaultConfig().getInteger(DPPConstants.DB_QUERY_TIME_OUT, 30000));
        String finalCmd = "";
        try {
            // connection.setAutoCommit(false);
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--$")) {
                    DSLUtil.getDefaultInstance().compute(line.substring(3), context);
                    line = "";
                    continue;
                }
                if (line.startsWith("//") || line.startsWith("#") || line.startsWith("--") || StringUtil.isEmpty(line))
                    continue; // 跳过注释行
                cmd.append(line.trim());
                cmd.append("\n");
                if (line.trim().endsWith(";")) // 遇到分号结尾的行认为一个命令读取完成
                {
                    finalCmd = DSLUtil.getDefaultInstance().relpaceVariable(cmd.toString(), context);
                    finalCmd = finalCmd.trim().substring(0, finalCmd.length() - 2);
                    logger.debug(finalCmd);
                    try {
                        logger.debug(st.executeUpdate(finalCmd) + " rows updated");
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                        //增加一个开关控制是否在执行sql出现错误时候退出后面的执行
                        if (ExcuteSqlFileBreakOnError)
                            break;
                    }
                    cmd = new StringBuilder();
                }
            }
            br.close();
            returnConnection();
            // connection.commit();
            // connection.setAutoCommit(true);
        } catch (Exception e) {
            logger.error(finalCmd);
            logger.fatal(e.getMessage(), e);
            // connection.rollback();
        } finally {
            // connection.close();
            //returnConnection();
        }
    }

    public ExecuteResult executeQuery(String sql) throws Exception {
        PreparedStatement stat = null;
        try {
            ResultSet resultSet = null;
            logger.debug(sql);
            Connection connection = getConnection();
            stat = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stat.setQueryTimeout(ConfigurationManager.getDefaultConfig().getInteger(DPPConstants.DB_QUERY_TIME_OUT, 3600));
            resultSet = stat.executeQuery();
            return new ExecuteResult(resultSet, stat);
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
            logger.error(sql);
        }
        return null;
    }

    /**
     * 针对指定的连接指向查询并返回ResultSet对象
     *
     * @param sql
     * @return
     * @throws Exception
     * @author James Cheung Date:Jul 14, 2012
     */
    public void executeQuery(String sql, DataRowHandler handler) throws Exception {
        executeQuery("", sql, handler);
        handler.close();
    }

    public void executeQuery(String queryName, String sql, DataRowHandler handler) throws Exception {
        try {
            if (handler == null) {
                logger.warn("The DataRowHandler given can not be null...");
                return;
            }
            Long start = DateUtil.getTimeinteger();
            ExecuteResult er = executeQuery(sql);
            if (er == null)
                return;
            ResultSet resultSet = er.getResultSet();
            //
            ResultSetMetaData md = resultSet.getMetaData();
            try {
                DataTable table = buildTable(queryName, md);
                int columnCount = table.getColumns().size();
                int count = 0;
                //resultSet.setFetchDirection(ResultSet.TYPE_FORWARD_ONLY);
                resultSet.setFetchSize(ConfigurationManager.getDefaultConfig().getInteger("dpp.row_fetch_size", 500));
                while (resultSet.next()) {
                    DataRow row = table.newRow();
                    count++;
                    buildDataRow(row, columnCount, resultSet, handler.isUseRealType());
                    if (count % 10000 == 0)
                        logger.debug(count + " record readied!");
                    handler.process(null, row);
                }
                logger.debug(count + " record readied!");
            } catch (Exception ex) {
                logger.fatal(ex.getMessage(), ex);
            } finally {
                try {
                    er.close();
                } catch (Exception e) {
                    //logger.error(e.getMessage(), e);
                }
                returnConnection();
                checkTime(start, sql);
            }
        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    private void buildDataRow(DataRow row, int columnCount, ResultSet resultSet, boolean useRealType) throws Exception {
        for (int i = 1; i <= columnCount; i++) {
            DataColumn column = row.getTable().getColumns().get(i - 1);
            try {
                if (useRealType) {
                    row.setValue(column.getColumnName(), DataTypes.convertTo(resultSet.getObject(i), column.getDataType()));
                } else {
                    row.setValue(column.getColumnName(), resultSet.getString(i));
                }
            } catch (Exception ex) {
                logger.warn(ex.getMessage());
                row.setValue(column.getColumnName(), null);
            }
        }
    }

    private DataTable buildTable(String queryName, ResultSetMetaData md) {
        // 构造数据表结构
        DataTable table = new DataTable();
        try {
            TableInfo info = null;
            /**
             * 如果指定了查询名，则会根据配置的表结构生成DataTable对象，包括列名和显示名，
             * 主要用于数据导出操作.
             */
            if (StringUtil.isNotEmpty(queryName) && !queryName.startsWith("#")) {
                TableInfoDao tableInfoDao = AppContext.getBean(TableInfoDao.class);
                info = tableInfoDao.get(queryName);
            } else {
                table.setTableName(StringUtil.substring(queryName, 1));
            }
            int columnCount = md.getColumnCount();
            DataKey pkey = new DataKey("temp-p-key");
            for (int i = 1; i <= columnCount; i++) {
                DataColumn col = table.addColumn(md.getColumnLabel(i).toLowerCase(), md.getColumnType(i));
                String caption = md.getColumnLabel(i);
                col.setSize(md.getScale(i));
                if (info != null) {
                    caption = info.getColumnCaption(caption);
                }
                col.setCaptionName(caption);
                if (col.getColumnName().startsWith("key_")) {
                    pkey.getKeyColumns().add(col);
                }
            }
            table.setPrimaryKey(pkey);
        } catch (Exception ex) {
            logger.error(ex);
        }
        return table;
    }

    public DataTable queryTable(String sql) throws Exception {
        return queryTable(sql, -1, 9999999);
    }

    public DataTable queryTable(String queryName, String sql) throws Exception {
        return queryTable(queryName, sql, -1, 9999999);
    }

    public PageInfo queryPage(String sql, PageInfo pageInfo) throws Exception {
        return queryPage("", sql, pageInfo);
    }

    public PageInfo queryPage(String queryName, String sql, PageInfo pageInfo) throws Exception {
        String countSql = "select count(1) cnt from (" + sql + ")  t";
        pageInfo.setRecordCount(Integer.parseInt(queryOne(countSql) + ""));
        pageInfo.setPageCount((int) Math.ceil(pageInfo.getRecordCount() / (1.0 * pageInfo.getPageSize())));
        int start = (pageInfo.getPageIndex() - 1) * pageInfo.getPageSize();
        int end = pageInfo.getPageIndex() * pageInfo.getPageSize();
        pageInfo.setTable(queryTable(queryName, sql, start, end));
        return pageInfo;
    }

    public Object queryOne(String sql) throws Exception {
        DataTable table = queryTable(sql);
        if (table != null && table.getRows().size() > 0) {
            return table.getValue(0, 0);
        }
        return "0";
    }

    public DataTable queryTable(String sql, int start, int max) throws Exception {
        return queryTable("", sql, start, max);
    }

    public DataTable queryTable(String queryName, String sql, int start, int max) throws Exception {
        Long startTime = DateUtil.getTimeinteger();
        ExecuteResult er = executeQuery(sql);
        ResultSet resultSet = er.getResultSet();
        ResultSetMetaData md = resultSet.getMetaData();
        DataTable table = buildTable(queryName, md);
        try {
            int columnCount = table.getColumns().size();
            int count = 0;

            while (resultSet.next()) {
                //如果当前读取的数据行索引小于设置的起始值则跳过处理
                count++;
                if (count < start)
                    continue;
                DataRow row = table.newRow();
                buildDataRow(row, columnCount, resultSet, true);
                table.addRow(row);
                if (count % 10000 == 0)
                    logger.debug(count + " record readied!!");
                if (count >= max) {
                    //如果已经读取到设置可以读取的最大行数，则退出读取
                    break;
                }
            }
            logger.debug(count + " record readied!!");
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
        } finally {
            er.close();
            checkTime(startTime, sql);
        }
        returnConnection();
        return table;
    }

    private ConnectionStringSetting connectionStringSetting;

    public void setConnectionStringSetting(ConnectionStringSetting connectionStringSetting) {
        this.connectionStringSetting = connectionStringSetting;
    }

    public ConnectionStringSetting getConnectionStringSetting() {
        return connectionStringSetting;
    }


    public Connection getConnection() {
        try {
            boolean needReconnect = false;
            if (connection.isClosed()) {
                needReconnect = true;
            }
            if (DateUtil.getTimeinteger() - lastVisited > connCheckTime) {
                if (!testAlive()) {
                    needReconnect = true;
                }
            }
            if (needReconnect) {
                connection = DriverManager.getConnection(this.connectionStringSetting.getUrl(), this.connectionStringSetting.getUsername(), this.connectionStringSetting.getPassword());
            }
        } catch (SQLException e) {
            logger.fatal(e.getMessage(), e);
        }
        lastVisited = DateUtil.getTimeinteger();
        return connection;
    }

    public void setConnection(Connection conn) {
        this.connection = conn;
    }

    protected abstract String getTestAliveSql();

    public boolean testAlive() {
        if (StringUtil.isNotEmpty(getTestAliveSql())) {
            // 判断连接是否可用
            Statement stmt;
            try {
                stmt = connection.createStatement();
                stmt.execute(getTestAliveSql());
                stmt.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return true;
    }

    public DbStat getDbStat() {
        return this.dbStat;
    }

    public void setDbStat(DbStat dbStat) {
        this.dbStat = dbStat;
    }

    public DbType getDbType() {
        return this.dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public DataAdapterPool getDataAdapterPool() {
        return dataAdapterPool;
    }

    public void setDataAdapterPool(DataAdapterPool dataAdapterPool) {
        this.dataAdapterPool = dataAdapterPool;
    }

    public void initialize() {

    }

    public void close() {
        this.dataAdapterPool.closeConnection(this.connection);
    }

    public void returnConnection() {
        this.dataAdapterPool.returnConnection(this);
    }

    protected abstract String getDateTimeExpression(Object date, String format);

    protected abstract String getNullValue();

    /**
     * TODO: 还没有实现 生成基于传入DataTable结构的建表/或表结构更新语句
     *
     * @param table 要生成的目标表对象
     * @return
     */
    public String genCreateOrUpdateTableSql(DataTable table) {
        return "";
    }

    public String genUpdate(String targetTable, DataRow row) {
        StringBuffer sb = new StringBuffer();
        sb.append("update " + targetTable + " set ");
        String sql = "";
        for (DataColumn dc : row.getTable().getColumns()) {
            Object value = row.getValue(dc.getColumnName());
            if (value == null) {
                logger.warn("found null value where update record, " + dc.getCaptionName() + "=" + row.getValue(dc.getColumnName()));
                continue;
            }
            value = formateValue(row.getValue(dc.getColumnName()), dc.getDataType());
            sb.append(dc.getColumnName());
            sb.append("=");
            sb.append(value);
            sb.append(",");
        }
        sql = sb.toString();
        sql = sql.substring(0, sql.length() - 1);
        DataKey primeKey = row.getTable().getPrimaryKey();
        sb = new StringBuffer();
        if (primeKey != null) {
            for (DataColumn dc : primeKey.getKeyColumns()) {
                sb.append(dc.getColumnName());
                sb.append("=");
                sb.append(formateValue(row.getValue(dc.getColumnName()), dc.getDataType()));
                sb.append(" and ");
            }
        }
        sql = sql + " where " + sb.toString();
        sql = sql.substring(0, sql.length() - 4);
        logger.info("Gen update sql : " + sql);
        return sql;
    }

    public String formateValue(Object value, int dataType) {
        String result = "";
        if (value == null || StringUtil.isEmpty(value.toString()))
            result += getNullValue();
        else if (dataType == Types.DATE
                || dataType == Types.TIMESTAMP) {
            int idx = value.toString().indexOf(".");
            if (idx > 0)
                value = value.toString().substring(0, idx);
            result += getDateTimeExpression(value, "yyyy-MM-dd HH:mm:ss");
        } else if (dataType == Types.BIGINT
                || dataType == Types.BIT
                || dataType == Types.DECIMAL
                || dataType == Types.DOUBLE
                || dataType == Types.FLOAT
                || dataType == Types.INTEGER
                || dataType == Types.NUMERIC
                || dataType == Types.TINYINT) {
            result += value;
        } else {
            result += ("'" + value + "'");
        }
        return result;
    }

    public String genSqlInsert(String targetTable, DataRow row) {
        String result = "insert into " + targetTable + " (";
        for (DataColumn dc : row.getTable().getColumns()) {
            result += dc.getColumnName();
            result += ",";
        }
        if (result.endsWith(","))
            result = result.substring(0, result.length() - 1);
        result += ")  values (";
        logger.debug(row.getTable().getColumns().size() + " Cols found ");
        for (DataColumn dc : row.getTable().getColumns()) {
            Object value = row.getValue(dc.getColumnName());
            result += formateValue(value, dc.getDataType());
            result += ",";
        }
        if (result.endsWith(","))
            result = result.substring(0, result.length() - 1);
        result += ")";
        return result;
    }


    @Override
    public void dropTable(String tableName) {
        executeNonQuery("drop table " + tableName);
    }

    @Override
    public void emptyTable(String tableName) {
        executeNonQuery("truncate table " + tableName);
    }
}
