package com.tuoming.mes.strategy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

@Entity
@Table(name="mes_td_off_setting")
public class TdOffSleepSelectModel extends AbstractModel{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Lob
	@Type(type="text")
	@Column(name="querySql", nullable=false)  
	private String querySql;
	@Column(name="db_name",length=20, nullable=false)
	private String dbName;
	@Column(name="enabled",length=1, nullable=false)
	private Boolean enabled;
	@Column(name="groupname",length=120, nullable=false)
	private String groupName;
	@Column(name="res_table",length=120, nullable=false)
	private String resTable;
	@Column(name="cal_type",length=1, nullable=false)
	private Boolean calType;
	@Column(name="del_flag",length=120, nullable=false)
	private boolean delFlag;
	@Column(name="export_cols",length=1000, nullable=false)
	private String exportCols;
	public Integer getId() {
		return id;
	}
	public boolean isDelFlag() {
		return delFlag;
	}
	public void setDelFlag(boolean delFlag) {
		this.delFlag = delFlag;
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
	public Boolean getCalType() {
		return calType;
	}
	public void setCalType(Boolean calType) {
		this.calType = calType;
	}
	public String getResTable() {
		return resTable;
	}
	public void setResTable(String resTable) {
		this.resTable = resTable;
	}
	public String getExportCols() {
		return exportCols;
	}
	public void setExportCols(String exportCols) {
		this.exportCols = exportCols;
	}
}
