package com.aspire.devops.report.model;

/**
 * 进度看板响应对象
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: ProgressInfo.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/18 11:39
 */
public class ProgressInfo {

    //开发中的版本数
    private Long developing;

    //延期开发中版本数
    private Long delayDeveloping;

    //已完成版本数
    private Long released;

    //延期完成版本数
    private Long delayReleased;

    public Long getDeveloping() {
        return developing;
    }

    public void setDeveloping(Long developing) {
        this.developing = developing;
    }

    public Long getDelayDeveloping() {
        return delayDeveloping;
    }

    public void setDelayDeveloping(Long delayDeveloping) {
        this.delayDeveloping = delayDeveloping;
    }

    public Long getReleased() {
        return released;
    }

    public void setReleased(Long released) {
        this.released = released;
    }

    public Long getDelayReleased() {
        return delayReleased;
    }

    public void setDelayReleased(Long delayReleased) {
        this.delayReleased = delayReleased;
    }
}