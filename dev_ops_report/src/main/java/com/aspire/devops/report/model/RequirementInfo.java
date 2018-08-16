package com.aspire.devops.report.model;

/**
 * 需求信息
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: RequirementInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/20 16:37
 */
public class RequirementInfo {

    private String demand;

    private Long quantity;

    private Integer status;

    private String num;

    private String endDate;

    private String startDate;

    private String releasedDate;

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }
}