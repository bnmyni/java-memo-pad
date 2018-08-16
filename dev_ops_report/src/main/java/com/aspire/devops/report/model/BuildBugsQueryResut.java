package com.aspire.devops.report.model;

/**
 * 版本bugs数量查询结果
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildBugsQueryResut.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/30 20:50
 */
public class BuildBugsQueryResut {

    private Integer id;

    private Integer bugs;

    private Integer testBugs;

    private Float ut;

    private Float issues;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Float getIssues() {
        return issues;
    }

    public void setIssues(Float issues) {
        this.issues = issues;
    }
}