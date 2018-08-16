package com.aspire.devops.report.model;

/**
 * top5排序结果
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: Top5ResultInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/30 21:00
 */
public class Top5ResultInfo {

    private Integer pid;

    private String pname;

    private Float codeQuality;

    private Integer bugs;

    private Integer testBugs;

    private Float ut;

    private Integer manHaur;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public Float getCodeQuality() {
        return codeQuality;
    }

    public void setCodeQuality(Float codeQuality) {
        this.codeQuality = codeQuality;
    }

    public Integer getBugs() {
        return bugs;
    }

    public void setBugs(Integer bugs) {
        this.bugs = bugs;
    }

    public Integer getTestBugs() {
        return testBugs;
    }

    public void setTestBugs(Integer testBugs) {
        this.testBugs = testBugs;
    }

    public Float getUt() {
        return ut;
    }

    public void setUt(Float ut) {
        this.ut = ut;
    }

    public Integer getManHaur() {
        return manHaur;
    }

    public void setManHaur(Integer manHaur) {
        this.manHaur = manHaur;
    }
}