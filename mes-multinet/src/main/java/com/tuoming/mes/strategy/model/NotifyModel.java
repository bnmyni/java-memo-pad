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
@Table(name="mes_notify_setting")
public class NotifyModel extends AbstractModel{

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
	@Column(name="commandmap",length=1000, nullable=false)
	private String commandMap;
	@Column(name="query_commandmap",length=1000, nullable=false)
	private String queryCommandMap;
	@Lob
	@Type(type="text")  
	@Column(name="querySql", nullable=false)  
	private String querySql;
	@Column(name="weak", nullable=false) 
	private boolean weak;
	
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
	public String getCommandMap() {
		return commandMap;
	}
	public void setCommandMap(String commandMap) {
		this.commandMap = commandMap;
	}
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public boolean isWeak() {
		return weak;
	}
	public void setWeak(boolean weak) {
		this.weak = weak;
	}
	public String getQueryCommandMap() {
		return queryCommandMap;
	}
	public void setQueryCommandMap(String queryCommandMap) {
		this.queryCommandMap = queryCommandMap;
	}
	
}
