package com.tuoming.mes.strategy.model;


public class TdMroManyCellModel  {
	//本小区rnc id
	private String scRncId;
	//本小区cell id
	private String scCellId;
	//采样点标识
	private String imsi;
	//采样时间
	private String timestamp;
	//邻区Uarfcn
	private String ncUarfcn;
	//邻区sc
	private String ncSc;
	
	public String getScRncId() {
		return scRncId;
	}
	public void setScRncId(String scRncId) {
		this.scRncId = scRncId;
	}
	public String getScCellId() {
		return scCellId;
	}
	public void setScCellId(String scCellId) {
		this.scCellId = scCellId;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getNcUarfcn() {
		return ncUarfcn;
	}
	public void setNcUarfcn(String ncUarfcn) {
		this.ncUarfcn = ncUarfcn;
	}
	public String getNcSc() {
		return ncSc;
	}
	public void setNcSc(String ncSc) {
		this.ncSc = ncSc;
	}

}
