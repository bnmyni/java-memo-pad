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
@Table(name="mes_alarminfo_setting")
public class AlarmInfoModel extends AbstractModel{

	private static final long serialVersionUID = 4575171349973729917L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column(name="db_name",length=20, nullable=false)
	private String dbName;
	@Column(name="enabled",length=1, nullable=false)
	private Boolean enabled;
	@Column(name="groupname",length=120, nullable=false)
	private String group;
	@Column(name="res_table",length=120, nullable=false)
	private String resTable;
	@Lob
	@Type(type="text")  
	@Column(name="exe_sql", nullable=false)  
	private String exeSql;
	
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

	public String getExeSql() {
		return exeSql;
	}
	public void setExeSql(String exeSql) {
		this.exeSql = exeSql;
	}
	public String getResTable() {
		return resTable;
	}
	public void setResTable(String resTable) {
		this.resTable = resTable;
	}
	
}
