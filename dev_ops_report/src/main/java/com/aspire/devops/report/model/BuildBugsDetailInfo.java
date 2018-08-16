package com.aspire.devops.report.model;

/**
 * 版本单元测试覆盖率明细结果
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildUtDetailInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 14:10
 */
public class BuildBugsDetailInfo extends BuildCommonInfo{

    private Integer status;
    private Integer bugs;
    private Integer testBugs;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}