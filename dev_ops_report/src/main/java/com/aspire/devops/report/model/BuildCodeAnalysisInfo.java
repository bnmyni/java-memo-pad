package com.aspire.devops.report.model;

/**
 * 版本代码质量分析结果信息
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildCodeAnalysisInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 14:38
 */
public class BuildCodeAnalysisInfo extends BuildCommonInfo {

    private Integer status;

    private Integer issue;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }

}