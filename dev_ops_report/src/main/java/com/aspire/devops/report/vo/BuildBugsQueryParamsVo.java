package com.aspire.devops.report.vo;

/**
 * 版本缺陷数查询vo
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.vo
 * fileName: BuildBugsQueryParamsVo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/30 20:52
 */
public class BuildBugsQueryParamsVo extends ProgressVo {

    private String ids;

    private Integer limits;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getLimits() {
        return limits;
    }

    public void setLimits(Integer limits) {
        this.limits = limits;
    }
}