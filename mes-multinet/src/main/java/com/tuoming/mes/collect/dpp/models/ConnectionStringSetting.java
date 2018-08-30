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

package com.tuoming.mes.collect.dpp.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.pyrlong.Envirment;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.rdbms.DbType;

/**
 * Created with IntelliJ IDEA. User: james Date: 6/11/13 Time: 9:02 AM 数据库连接对象配置,对应配置文件里的 connectionStrings
 * 当前处理方法是xml文件内每类配置会对应一个数据表，然后后台系统自动实现数据库与配置文件的同步操作 这样可以有效减少前后台系统的耦合性
 */
@Entity
@Table(name = "mes_connections")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ConnectionStringSetting extends AbstractModel {

    @Pattern(regexp = "[A-Za-z0-9]{5,20}", message = "{object.name.illegal}")
    @Id
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "url", length = 256, nullable = false)
    private String url;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "driver_class", length = 120, nullable = false)
    private String driverClass;

    @Column(name = "enabled", length = 120, nullable = false)
    private boolean enabled;

    @Column(name = "remark", length = 120, nullable = false)
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public Object getValue() {
        return url;
    }

    public String toString() {
        return url;
    }

    public DbType getDbType() throws Exception {
        DbType dbType = null;
        String dbUrl = url;
        if (dbUrl.indexOf("jdbc:oracle") >= 0) {
            dbType = DbType.Oracle;
        } else if (dbUrl.indexOf("jdbc:sybase") >= 0 || dbUrl.indexOf("jtds:sybase") >= 0) {
            dbType = DbType.Sybase;
        } else if (dbUrl.indexOf("jdbc:sqlserver") >= 0 || dbUrl.indexOf("jtds:sqlserver") >= 0) {
            dbType = DbType.SqlServer;
        } else if (dbUrl.indexOf("jdbc:microsoft:sqlserver") >= 0) {
            dbType = DbType.SqlServer;
        } else if (dbUrl.indexOf("jdbc:mysql") >= 0) {
            dbType = DbType.Mysql;
        } else if (dbUrl.indexOf("jdbc:informix-sqli") >= 0) {
            dbType = DbType.Informix;
        } else if (dbUrl.indexOf("jdbc:h2") >= 0) {
            dbType = DbType.H2;
        } else {
            throw new Exception("未知的数据库类型：dbUrl=" + dbUrl);
        }
        return dbType;
    }

    public static String getHibernateDialect(String dbType) {
        dbType = dbType.toLowerCase();
        if (dbType.indexOf("mysql") >= 0)
            return "org.hibernate.dialect.MySQLDialect";
        if (dbType.indexOf("oracle") >= 0)
            return "org.hibernate.dialect.Oracle9iDialect";
        if (dbType.indexOf("sqlserver") >= 0)
            return "org.hibernate.dialect.SQLServer2008Dialect";
        if (dbType.indexOf("sybase") >= 0)
            return "org.hibernate.dialect.SQLServer2008Dialect";
        if (dbType.indexOf("h2") >= 0) {
            return "org.hibernate.dialect.H2Dialect";
        }
        return "";
    }

    public static String getUrlTemplate(String dbType) {
        dbType = dbType.toLowerCase();
        if (dbType.indexOf("mysql") >= 0)
            return "jdbc:mysql://[IP]:[PORT]/[SCHEME]?characterEncoding=utf-8&autoReconnect=true&maxReconnects=3&zeroDateTimeBehavior=convertToNull";
        if (dbType.indexOf("oracle") >= 0)
            return "jdbc:oracle:thin:@[IP]:[PORT]:[SCHEME]";
        if (dbType.indexOf("sqlserver") >= 0)
            return "jdbc:jtds:sqlserver://[IP]:[PORT]/[SCHEME]";
        if (dbType.indexOf("sybase") >= 0)
            return "jdbc:jtds:sybase://[IP]:[PORT]/[SCHEME]";
        if (dbType.indexOf("h2") >= 0) {
            String path = Envirment.getHome() + "data/h2/[SCHEME]";
            return "jdbc:h2:" + path + ";MODE=MYSQL";
        }
        return "";
    }

    public static String getDriverClass(String dbType) {
        dbType = dbType.toLowerCase();
        if (dbType.indexOf("mysql") >= 0)
            return "com.mysql.jdbc.Driver";
        if (dbType.indexOf("oracle") >= 0)
            return "oracle.jdbc.driver.OracleDriver";
        if (dbType.indexOf("sqlserver") >= 0)
            return "net.sourceforge.jtds.jdbc.Driver";
        if (dbType.indexOf("sybase") >= 0)
            return "net.sourceforge.jtds.jdbc.Driver";
        if (dbType.indexOf("h2") >= 0)
            return "org.h2.Driver";
        return "";
    }

    public String getDefaultScheme(String dbType) {
        dbType = dbType.toLowerCase();
        if (dbType.indexOf("mysql") >= 0)
            return getScheme();
        if (dbType.indexOf("oracle") >= 0)
            return getUsername();
        if (dbType.indexOf("sqlserver") >= 0)
            return "dbo";
        if (dbType.indexOf("sybase") >= 0)
            return "dbo";
        if (dbType.indexOf("h2") >= 0)
            return "dbo";
        return "";
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ConnectionStringSetting) {
            return name.equals(((ConnectionStringSetting) o).getName());
        }
        return false;
    }
    
    
    @Override
    public int hashCode(){
    	  return  this.getName().hashCode();
    	 }
    	 
    

    public String getIP() {
        return StringUtil.getMatchString(getUrl(), "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
    }

    public String getPort() {
        return StringUtil.getMatchString(getUrl(), ":([0-9]+)", 1);
    }

    public String getScheme() {
        return StringUtil.getMatchString(getUrl(), "[:|/]([\\w]+)($|\\?)", 1);
    }
}
