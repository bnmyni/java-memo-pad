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
@Table(name="mes_td_off_kpi_setting")
public class TdOffKpiModel  extends AbstractModel{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column(name="groupname",length=120, nullable=false)
	private String group;
	@Lob
	@Type(type="text")  
	@Column(name="querySql", nullable=false)  
	private String querySql;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	
}
