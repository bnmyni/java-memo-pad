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
public class BuildUtDetailInfo extends BuildCommonInfo{

    private Integer status;
    private Integer ut;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUt() {
        return ut;
    }

    public void setUt(Integer ut) {
        this.ut = ut;
    }
}