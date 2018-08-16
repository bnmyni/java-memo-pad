package com.aspire.devops.report.dto;

/**
 * 报表看板需求查询请求对象
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dto
 * fileName: RequirementRequest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/20 15:46
 */
public class RequirementRequest {

    private String date;

    private String demand;

    public RequirementRequest() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }
}