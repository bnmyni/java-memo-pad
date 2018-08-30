package com.tuoming.mes.strategy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 关键性指标计算模型
 * @author Administrator
 *
 */
@Entity
@Table(name="mes_kpical_set")
public class KpiCalModel extends AbstractModel{
	private static final long serialVersionUID = 6145265364473285941L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column(name="db_name",length=20, nullable=false)
	private String dbName;
	@Column(name="enabled",length=1, nullable=false)
	private Boolean enabled;
	@Column(name="group_name",length=120, nullable=false)
	private String groupName;
	@Column(name="delete_flag",length=20, nullable=false)
	private boolean deleteFlag;
	@Lob
	@Type(type="text")  
	@Column(name="querySql", nullable=false)  
	private String querySql;
	@Column(name="cal_handle", nullable=false)
	private String calHandle;
	@Column(name="res_table", nullable=false)
	private String resTable;
	@Column(name="collist", nullable=false)
	private String colList;
	@Transient
	private String starttime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public boolean isDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public String getCalHandle() {
		return calHandle;
	}
	public void setCalHandle(String calHandle) {
		this.calHandle = calHandle;
	}
	public String getResTable() {
		return resTable;
	}
	public void setResTable(String resTable) {
		this.resTable = resTable;
	}
	public String getColList() {
		return colList;
	}
	public void setColList(String colList) {
		this.colList = colList;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

}
