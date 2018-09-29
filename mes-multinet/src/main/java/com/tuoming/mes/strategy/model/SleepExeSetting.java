package com.tuoming.mes.strategy.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 休眠小区执行流程
 *
 * @author Administrator
 */
@Entity
@Table(name = "mes_sleepexe_setting")
public class SleepExeSetting extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Lob
    @Type(type = "text")
    @Column(name = "querySql", nullable = false)
    private String querySql;
    @Column(name = "db_name", length = 20, nullable = false)
    private String dbName;
    @Column(name = "enabled", length = 1, nullable = false)
    private Boolean enabled;
    @Column(name = "groupname", length = 120, nullable = false)
    private String group;
    @Column(name = "command_map", length = 500, nullable = false)
    private String commandMap;
    @Column(name = "service_handle", length = 120, nullable = false)
    private String serviceHandle;
    @Column(name = "zs", length = 120, nullable = false)
    private String zs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceHandle() {
        return serviceHandle;
    }

    public void setServiceHandle(String serviceHandle) {
        this.serviceHandle = serviceHandle;
    }

    public String getZs() {
        return zs;
    }

    public void setZs(String zs) {
        this.zs = zs;
    }

    public String getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(String commandMap) {
        this.commandMap = commandMap;
    }
}
