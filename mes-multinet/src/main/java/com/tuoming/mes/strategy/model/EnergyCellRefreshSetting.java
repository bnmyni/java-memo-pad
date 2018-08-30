package com.tuoming.mes.strategy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 小区列表刷新配置表
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "mes_celllist_ref")
public class EnergyCellRefreshSetting extends AbstractModel {
	private static final long serialVersionUID = 2938467650431226750L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "enabled", length = 1, nullable = false)
	private int enabled;// 判断是否执行该行配置
	@Column(name = "groupname", length = 120, nullable = false)
	private String groupname;// 组名
	@Type(type = "text")
	@Column(name = "querySql", nullable = false)
	private String querySql;// 要查询的sql
	@Column(name = "res_table", length = 120, nullable = false)
	private String resTable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
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
