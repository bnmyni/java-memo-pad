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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import com.google.common.collect.Maps;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.configuration.ConnectionStringSettingsCollection;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;
import com.tuoming.mes.collect.dpp.rdbms.impl.MysqlDataAdapterImpl;
import com.tuoming.mes.collect.dpp.rdbms.impl.OraDataAdapterImpl;
import com.tuoming.mes.collect.dpp.rdbms.impl.SqlServerDataAdapter;

public class DataAdapterPool implements Serializable {
    /**
     * 10: 连接池的初始大小
     */
    public static final int ADAPTER_POOL_INIT_CONNS = 5; // 连接池的初始大小
    /**
     * 5: 连接池自动增加的大小
     */
    public static final int ADAPTER_POOL_INCRE_CONNS = 3; // 连接池自动增加的大小
    /**
     * 50: 连接池最大值
     */
    public static final int ADAPTER_POOL_MAX_CONNS = 20; // 连接池最大值
    private static final long serialVersionUID = 9178829890648304032L;
    private static Map<String, DataAdapterPool> dataAdapterPools = Maps.newConcurrentMap();
    ConnectionStringSetting connectionStringSetting;
    private String testTable = ""; // 测试连接是否可用的测试表名，默认没有测试表
    private int initialConnections; // 连接池的初始大小
    private int incrementalConnections;// 连接池自动增加的大小
    private int maxConnections; // 连接池最大的大小
    private Vector<DataAdapter> dataAdapters = null;
    private static Object synObject = new Object();

    /**
     * 构造函数
     */

    public DataAdapterPool(ConnectionStringSetting conn) {
        this.connectionStringSetting = conn;
        this.initialConnections = ADAPTER_POOL_INIT_CONNS;
        this.incrementalConnections = ADAPTER_POOL_INCRE_CONNS;
        this.maxConnections = ADAPTER_POOL_MAX_CONNS;
        DriverManager.setLoginTimeout(1000);// 设置超时时间1000s
        try {
            createPool();
        } catch (Exception e) {
            LogFacade.error(e.getMessage());
        }
    }

    public static DataAdapterPool getDataAdapterPool(String dbName) throws Exception {
        synchronized (synObject) {
            ConnectionStringSetting conn = ConnectionStringSettingsCollection.getConnectionSetting(dbName);
            if (conn == null) {
                LogFacade.warn(dbName + " not set ........");
                return null;
            }
            if (!dataAdapterPools.containsKey(conn.getName())) {
                dataAdapterPools.put(conn.getName(), new DataAdapterPool(conn));
            }
            DataAdapterPool pool = dataAdapterPools.get(conn.getName());
            if (pool.getDataAdapter().getConnection() == null) {
                dataAdapterPools.remove(conn.getName());
                pool = new DataAdapterPool(conn);
                dataAdapterPools.put(conn.getName(), pool);
            }
            return pool;
        }
    }

    public static DataAdapter getDataAdapter(DbType dbType) {
        DataAdapter dataAapter = null;
        if (dbType == DbType.Oracle) {
            dataAapter = new OraDataAdapterImpl();
            dataAapter.setDbType(DbType.Oracle);
        } else if (dbType == DbType.Mysql) {
            dataAapter = new MysqlDataAdapterImpl();
            dataAapter.setDbType(DbType.Mysql);
        } else if (dbType == DbType.SqlServer || dbType == DbType.Sybase) {
            dataAapter = new SqlServerDataAdapter();
            dataAapter.setDbType(DbType.SqlServer);
        } 
        return dataAapter;
    }

    /**
     * 返回连接池的初始大小
     *
     * @return 初始连接池中可获得的连接数量
     */
    public int getInitialConnections() {
        return this.initialConnections;
    }

    /**
     * 设置连接池的初始大小
     *
     * @param initialConnections 用于设置初始连接池中连接的数量
     */
    public void setInitialConnections(int initialConnections) {
        this.initialConnections = initialConnections;
    }

    /**
     * 返回连接池自动增加的大小
     *
     * @return 连接池自动增加的大小
     */
    public int getIncrementalConnections() {
        return this.incrementalConnections;
    }

    /**
     * 设置连接池自动增加的大小
     *
     * @param incrementalConnections 连接池自动增加的大小
     */
    public void setIncrementalConnections(int incrementalConnections) {
        this.incrementalConnections = incrementalConnections;
    }

    /**
     * 返回连接池中最大的可用连接数量
     *
     * @return 连接池中最大的可用连接数量
     */
    public int getMaxConnections() {
        return this.maxConnections;
    }

    /**
     * 设置连接池中最大可用的连接数量
     *
     * @param maxConnections 设置连接池中最大可用的连接数量值
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 获取测试数据库表的名字
     *
     * @return 测试数据库表的名字
     */
    public String getTestTable() {
        return this.testTable;
    }

    /**
     * 设置测试表的名字
     *
     * @param testTable String 测试表的名字
     */

    public void setTestTable(String testTable) {
        this.testTable = testTable;
    }

    /**
     * 创建一个数据库连接池，连接池中的可用连接的数量采用类成员
     * <p/>
     * initialConnections 中设置的值
     */
    public synchronized void createPool() throws Exception {
        // 确保连接池没有创建
        // 如果连接池己经创建了，保存连接的向量 connections 不会为空
        if (dataAdapters != null) {
            return; // 如果己经创建，则返回
        }
        // 实例化 JDBC Driver 中指定的驱动类实例
        Driver driver = (Driver) (Class.forName(connectionStringSetting.getDriverClass()).newInstance());
        DriverManager.registerDriver(driver); // 注册 JDBC 驱动程序
        // 创建保存连接的向量 , 初始时有 0 个元素
        dataAdapters = new Vector<DataAdapter>();
        // 根据 initialConnections 中设置的值，创建连接。
        createDataAdapters(this.initialConnections);
        LogFacade.debug(" 数据库连接池创建成功！ ");
    }

    /**
     * 创建由 numConnections 指定数目的数据库连接 , 并把这些连接
     * <p/>
     * 放入 connections 向量中
     *
     * @param numConnections 要创建的数据库连接的数目
     */
    private void createDataAdapters(int numConnections) throws Exception {
        // 循环创建指定数目的数据库连接
        for (int x = 0; x < numConnections; x++) {
            // 是否连接池中的数据库连接的数量己经达到最大？最大值由类成员 maxConnections
            // 指出，如果 maxConnections 为 0 或负数，表示连接数量没有限制。
            // 如果连接数己经达到最大，即退出。
            if (this.maxConnections > 0 && this.dataAdapters.size() >= this.maxConnections) {
                LogFacade.error("连接数己经达到最大......");
                break;
            }
            // add a new PooledConnection object to connections vector
            // 增加一个连接到连接池中（向量 connections 中）
            DataAdapter dataAapter = null;
            DbType dbType = connectionStringSetting.getDbType();
            try {
                dataAapter = getDataAdapter(dbType);
                dataAapter.setDataAdapterPool(this);
                dataAapter.setConnection(this.newConnection());
                dataAapter.setConnectionStringSetting(connectionStringSetting);
                dataAapter.setDbStat(DbStat.Enable);
                dataAdapters.addElement(dataAapter);
            } catch (SQLException e) {
                LogFacade.error(" 创建数据库连接失败！ " + e.getMessage());
                throw (e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建一个新的数据库连接并返回它
     *
     * @return 返回一个新创建的数据库连接
     */
    private Connection newConnection() throws SQLException {
        // 创建一个数据库连接
        Connection conn = DriverManager.getConnection(this.connectionStringSetting.getUrl(), this.connectionStringSetting.getUsername(), this.connectionStringSetting.getPassword());
        // 如果这是第一次创建数据库连接，即检查数据库，获得此数据库允许支持的
        // 最大客户连接数目
        // connections.size()==0 表示目前没有连接己被创建
        if (dataAdapters.size() == 0) {
            DatabaseMetaData metaData = conn.getMetaData();
            int driverMaxConnections = metaData.getMaxConnections();
            // 数据库返回的 driverMaxConnections 若为 0 ，表示此数据库没有最大
            // 连接限制，或数据库的最大连接限制不知道
            // driverMaxConnections 为返回的一个整数，表示此数据库允许客户连接的数目
            // 如果连接池中设置的最大连接数量大于数据库允许的连接数目 , 则置连接池的最大
            // 连接数目为数据库允许的最大数目
            if (driverMaxConnections > 0 && this.maxConnections > driverMaxConnections) {
                this.maxConnections = driverMaxConnections;
            }
        }
        return conn; // 返回创建的新的数据库连接
    }

    /**
     * 通过调用 getFreeConnection() 函数返回一个可用的数据库连接 ,
     * <p/>
     * 如果当前没有可用的数据库连接，并且更多的数据库连接不能创
     * <p/>
     * 建（如连接池大小的限制），此函数等待一会再尝试获取。
     *
     * @return 返回一个可用的数据库连接对象
     */
    public synchronized DataAdapter getDataAdapter() throws Exception {
        // 确保连接池己被创建
        if (dataAdapters == null) {
            return null; // 连接池还没创建，则返回 null
        }
        DataAdapter conn = getFreeDataAdapter(); // 获得一个可用的数据库连接
        // 如果目前没有可以使用的连接，即所有的连接都在使用中
        while (conn == null || conn.getConnection() == null) {
            // 等一会再试
            wait(250);
            conn = getFreeDataAdapter(); // 重新再试，直到获得可用的连接，如果
            // getFreeConnection() 返回的为 null
            // 则表明创建一批连接后也不可获得可用连接
        }
        return conn;// 返回获得的可用的连接
    }

    /**
     * 本函数从连接池向量 connections 中返回一个可用的的数据库连接，如果
     * <p/>
     * 当前没有可用的数据库连接，本函数则根据 incrementalConnections 设置
     * <p/>
     * 的值创建几个数据库连接，并放入连接池中。
     * <p/>
     * 如果创建后，所有的连接仍都在使用中，则返回 null
     *
     * @return 返回一个可用的数据库连接
     */
    private DataAdapter getFreeDataAdapter() throws Exception {
        // 从连接池中获得一个可用的数据库连接
        DataAdapter conn = findFreeDataAdapter();
        if (conn == null) {
            // 如果目前连接池中没有可用的连接
            // 创建一些连接
            createDataAdapters(incrementalConnections);
            // 重新从池中查找是否有可用连接
            conn = findFreeDataAdapter();
            if (conn == null) {
                // 如果创建连接后仍获得不到可用的连接，则返回 null
                return null;
            }
        }
        return conn;
    }

    /**
     * 查找连接池中所有的连接，查找一个可用的数据库连接，
     * <p/>
     * 如果没有可用的连接，返回 null
     *
     * @return 返回一个可用的数据库连接
     */
    private DataAdapter findFreeDataAdapter() throws SQLException {
        Connection conn = null;
        DataAdapter pConn = null;
        // 获得连接池向量中所有的对象
        Enumeration<DataAdapter> enumerate = dataAdapters.elements();
        // 遍历所有的对象，看是否有可用的连接
        while (enumerate.hasMoreElements()) {
            pConn = (DataAdapter) enumerate.nextElement();
            if (pConn.getDbStat() != DbStat.Busy) {
                // 如果此对象不忙，则获得它的数据库连接并把它设为忙
                conn = pConn.getConnection();
                pConn.setDbStat(DbStat.Busy);
                // 测试此连接是否可用
                if (!pConn.testAlive()) {
                    // 如果此连接不可再用了，则创建一个新的连接，
                    // 并替换此不可用的连接对象，如果创建失败，返回 null
                    try {
                        conn = newConnection();
                        pConn.setConnection(conn);
                    } catch (SQLException e) {
                        LogFacade.error(" 创建数据库连接失败！ " + e.getMessage());
                        return null;
                    }
                }
                break; // 己经找到一个可用的连接，退出
            }
        }
        return pConn;// 返回找到到的可用连接
    }

    /**
     * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。
     * <p/>
     * 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
     *
     * @param
     */
    public void returnConnection(DataAdapter conn) {
        // 确保连接池存在，如果连接没有创建（不存在），直接返回
        if (dataAdapters == null) {
            LogFacade.error(" 连接池不存在，无法返回此连接到连接池中 !");
            return;
        }
        DataAdapter pConn = null;
        Enumeration<DataAdapter> enumerate = dataAdapters.elements();
        // 遍历连接池中的所有连接，找到这个要返回的连接对象
        boolean isExistConn = false;
        while (enumerate.hasMoreElements()) {
            pConn = (DataAdapter) enumerate.nextElement();
            // 先找到连接池中的要返回的连接对象
            if (conn.getConnection() == pConn.getConnection()) {
                // 找到了 , 设置此连接为空闲状态
                pConn.setDbStat(DbStat.Enable);
                isExistConn = true;
                break;
            }
        }
        if (!isExistConn) {
            LogFacade.error(" 返回的连接在连接池中不存在");
        }
    }

    /**
     * 刷新连接池中所有的连接对象
     */
    public synchronized void refreshConnections() throws SQLException {
        // 确保连接池己创新存在
        if (dataAdapters == null) {
            LogFacade.error(" 连接池不存在，无法刷新 !");
            return;
        }
        DataAdapter pConn = null;
        Enumeration<DataAdapter> enumerate = dataAdapters.elements();
        while (enumerate.hasMoreElements()) {
            // 获得一个连接对象
            pConn = (DataAdapter) enumerate.nextElement();
            // 如果对象忙则等 5 秒 ,5 秒后直接刷新
            if (pConn.getDbStat() == DbStat.Busy) {
                wait(5000); // 等 5 秒
            }
            // 关闭此连接，用一个新的连接代替它。
            closeConnection(pConn.getConnection());
            pConn.setConnection(newConnection());
            pConn.setDbStat(DbStat.Enable);
        }
    }

    /**
     * 关闭连接池中所有的连接，并清空连接池。
     */
    public synchronized void closeConnectionPool() throws SQLException {
        // 确保连接池存在，如果不存在，返回
        if (dataAdapters == null) {
            LogFacade.error(" 连接池不存在，无法关闭 !");
            return;
        }
        DataAdapter pConn = null;
        Enumeration<DataAdapter> enumerate = dataAdapters.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (DataAdapter) enumerate.nextElement();
            // 如果忙，等 5 秒
            if (pConn.getDbStat() == DbStat.Busy) {
                wait(5000); // 等 5 秒
            }
            // 5 秒后直接关闭它
            closeConnection(pConn.getConnection());
            // 从连接池向量中删除它
            dataAdapters.removeElement(pConn);
        }
        // 置连接池为空
        dataAdapters = null;
    }

    /**
     * 关闭一个数据库连接
     *
     * @param
     */
    public void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            LogFacade.error(" 关闭数据库连接出错： " + e.getMessage());
        }
    }

    /**
     * 使程序等待给定的毫秒数
     *
     * @param
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
        }
    }
}
