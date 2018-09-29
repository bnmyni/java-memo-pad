package com.tuoming.mes.strategy.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

@Entity
@Table(name = "mes_beforeafter_setting")
public class BeforeAndAfterSetting extends AbstractModel {
    private static final long serialVersionUID = -1869500962691937353L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "enabled", nullable = false)
    private int enabled;// 执行开关
    @Column(name = "groupname", nullable = false)
    private String groupname;// 组名
    @Type(type = "text")
    @Column(name = "tablename", nullable = false)
    private String tablename;// 要执行的表名
    @Column(name = "celldata", nullable = false)
    private String celldata;// 表的类型(PM)(CM)
    @Type(type = "text")
    @Column(name = "executesql", nullable = false)
    private String executesql;// 要执行的sql语句

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getCelldata() {
        return celldata;
    }

    public void setCelldata(String celldata) {
        this.celldata = celldata;
    }

    public String getExecutesql() {
        return executesql;
    }

    public void setExecutesql(String executesql) {
        this.executesql = executesql;
    }

}
