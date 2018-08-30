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

/**
 * 节能小区监控实体模型
 * @author Administrator
 *
 */
@Entity
@Table(name="mes_montior_setting")
public class SavePowerMontiorModel extends AbstractModel{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Lob
	@Type(type="text")  
	@Column(name="exesql", nullable=false)  
	private String exeSql;
	@Column(name="db_name",length=20, nullable=false)
	private String dbName;
	@Column(name="enabled",length=1, nullable=false)
	private Boolean enabled;
	@Column(name="groupname",length=120, nullable=false)
	private String group;
	@Column(name="rs_table",length=120, nullable=false)
	private String rsTable;
	@Column(name="notify_handle",length=20, nullable=false)
	private String notifyHandle;
	@Column(name="del_flag",length=20, nullable=false)
	private boolean delFlag;
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getNotifyHandle() {
		return notifyHandle;
	}
	public void setNotifyHandle(String notifyHandle) {
		this.notifyHandle = notifyHandle;
	}
	public String getExeSql() {
		return exeSql;
	}
	public void setExeSql(String exeSql) {
		this.exeSql = exeSql;
	}
	public boolean isDelFlag() {
		return delFlag;
	}
	public void setDelFlag(boolean delFlag) {
		this.delFlag = delFlag;
	}
	public String getRsTable() {
		return rsTable;
	}
	public void setRsTable(String rsTable) {
		this.rsTable = rsTable;
	}
}
