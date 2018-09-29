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
 * 下一时刻数据预测配置
 *
 * @author Administrator
 */
@Entity
@Table(name = "mes_fcastnext_setting")
public class FcastNextIntervalSetting extends AbstractModel {
    private static final long serialVersionUID = 6605823391422234268L;
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
    private String groupName;
    @Column(name = "source_table", length = 120, nullable = false)
    private String sourceTable;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

}
