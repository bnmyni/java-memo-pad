package com.tuoming.mes.strategy.model;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.tuoming.mes.collect.dpp.models.AbstractModel;


/**
 * 重叠覆盖度配置表
 *
 * @author Administrator
 */
@Entity
@Table(name = "mes_over_setting")
public class OverlayDegreeSetting extends AbstractModel {

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
    @Column(name = "result_table", length = 120, nullable = false)
    private String resultTable;
    @Column(name = "service_handle", length = 120, nullable = false)
    private String serviceHandle;
    @Column(name = "top_amount")
    private int topAmount;
    @Column(name = "source_bs")
    private String sourceBs;
    @Column(name = "column_filter", length = 1000)
    private String columnFilter;
    @Lob
    @Type(type = "text")
    @Column(name = "create_sql", nullable = false)
    private String createSql;
    @Column(name = "bus_type", length = 10, nullable = false)
    private String busType;
    @Transient
    private List<String> columnList;

    public String getServiceHandle() {
        return serviceHandle;
    }

    public void setServiceHandle(String serviceHandle) {
        this.serviceHandle = serviceHandle;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getResultTable() {
        return resultTable;
    }

    public void setResultTable(String resultTable) {
        this.resultTable = resultTable;
    }

    public int getTopAmount() {
        return topAmount;
    }

    public void setTopAmount(int topAmount) {
        this.topAmount = topAmount;
    }

    public String getSourceBs() {
        return sourceBs;
    }

    public void setSourceBs(String sourceBs) {
        this.sourceBs = sourceBs;
    }

    public String getColumnFilter() {
        return columnFilter;
    }

    public void setColumnFilter(String columnFilter) {
        this.columnFilter = columnFilter;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public List<String> getColumnList() {
        if (StringUtils.isEmpty(this.getColumnFilter())) {
            return null;
        }
        if (columnList == null || columnList.isEmpty()) {
            List<String> columnList = (List<String>) JSONArray.toCollection(JSONArray.fromObject(this.columnFilter));
            this.setColumnList(columnList);
        }
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public String getCreateSql() {
        return createSql;
    }

    public void setCreateSql(String createSql) {
        this.createSql = createSql;
    }

}
