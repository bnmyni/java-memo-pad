package com.tuoming.mes.strategy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import com.pyrlong.dsl.tools.DSLUtil;

@Entity
@Table(name="mes_fcast_setting")
public class HisDataFCastSetting implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column(name="db_name",length=20, nullable=false)
	private String dbName;
	@Column(name="enabled",length=1, nullable=false)
	private Boolean enabled;
	@Column(name="groupname",length=120, nullable=false)
	private String group;
	@Column(name="source_table",length=120, nullable=false)
	private String sourceTable;
	@Column(name="begin_hour")
	private int beginHour;
	@Column(name="end_Hour")
	private int endHour;
	@Column(name="minute_interval")
	private int minuteInterval;
	@Type(type="text")  
	@Column(name="querySql", nullable=false)  
	private String querySql;
	@Column(name="row_bs")
	private String rowBs;
	@Column(name="column_filter",length=1000)
	private String columnFilter;
	@Column(name="nocal_col")
	private String noCalCol;
	@Column(name="data_bs")
	private String dateBs;
	@Transient
	private Map<String, String> noCalColMap;//非计算列
	@Transient
	private List<String> columnList;//打印字段
	public String getDateBs() {
		return dateBs;
	}
	public void setDateBs(String dateBs) {
		this.dateBs = dateBs;
	}
	public List<String> getColumnList() {
		if(StringUtils.isEmpty(this.getColumnFilter())){
			return null;
		}
		if(columnList==null||columnList.isEmpty()) {
			List<String> columnList  = (List<String>) JSONArray.toCollection(JSONArray.fromObject(this.columnFilter));
			this.columnList = columnList;
		}
		return columnList;
	}
	public Map<String, String> getNoCalColMap() {
		if(StringUtils.isEmpty(this.getNoCalCol())){
			return null;
		}
		if(this.noCalColMap==null||noCalColMap.isEmpty()) {
			Map<String, String> noCalColMap  = (Map<String, String>) DSLUtil.getDefaultInstance().compute(noCalCol);
			this.noCalColMap = noCalColMap;
		}
		return noCalColMap;
	}
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
	public String getSourceTable() {
		return sourceTable;
	}
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}
	public int getMinuteInterval() {
		return minuteInterval;
	}
	public void setMinuteInterval(int minuteInterval) {
		this.minuteInterval = minuteInterval;
	}
	public int getBeginHour() {
		return beginHour;
	}
	public void setBeginHour(int beginHour) {
		this.beginHour = beginHour;
	}
	public int getEndHour() {
		return endHour;
	}
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public String getRowBs() {
		return rowBs;
	}
	public void setRowBs(String rowBs) {
		this.rowBs = rowBs;
	}
	public String getColumnFilter() {
		return columnFilter;
	}
	public void setColumnFilter(String columnFilter) {
		this.columnFilter = columnFilter;
	}
	public String getNoCalCol() {
		return noCalCol;
	}
	public void setNoCalCol(String noCalCol) {
		this.noCalCol = noCalCol;
	}

}
