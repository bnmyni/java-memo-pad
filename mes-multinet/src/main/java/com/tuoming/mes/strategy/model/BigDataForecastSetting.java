package com.tuoming.mes.strategy.model;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 数据预测
 *
 * @author Administrator
 */
@Entity
@Table(name = "mes_bigdata_forecast_setting")
public class BigDataForecastSetting implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
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
    @Column(name = "res_table", length = 120, nullable = false)
    private String resTable;
    @Column(name = "column_filter", length = 1000)
    private String columnFilter;
    @Transient
    private List<String> columnList;//打印字段

    public List<String> getColumnList() {
        if (StringUtils.isEmpty(this.getColumnFilter())) {
            return null;
        }
        if (columnList == null || columnList.isEmpty()) {
            List<String> columnList = (List<String>) JSONArray.toCollection(JSONArray.fromObject(this.columnFilter));
            this.columnList = columnList;
        }
        return columnList;
    }

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

    public String getResTable() {
        return resTable;
    }

    public void setResTable(String resTable) {
        this.resTable = resTable;
    }

    public String getColumnFilter() {
        return columnFilter;
    }

    public void setColumnFilter(String columnFilter) {
        this.columnFilter = columnFilter;
    }

}
