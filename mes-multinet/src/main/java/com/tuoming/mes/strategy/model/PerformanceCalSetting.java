package com.tuoming.mes.strategy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

@Entity
@Table(name = "mes_performancecal_setting")
public class PerformanceCalSetting extends AbstractModel {
	private static final long serialVersionUID = -1355005830763109901L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "groupname", nullable = false, length = 20)
	private String groupName;
	@Column(name = "enabled", nullable = false, length = 20)
	private int enabled;
	@Type(type = "text")
	@Column(name = "querysql", nullable = false)
	private String querySql;
	@Column(name = "restable", nullable = false, length = 50)
	private String resTable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	public String getResTable() {
		return resTable;
	}

	public void setResTable(String resTable) {
		this.resTable = resTable;
	}

}
