package com.aspire.devops.report.dto;

/**
 * 看板进度明细响应对象
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dto
 * fileName: ProgressDetailResponse.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/18 14:15
 */
public class ProgressDetailResponse {
    private Long pid;

    private Long sub_pid;

    private String pname;

    private String sub_pname;

    private String num;

    private String start_date;

    private String end_date;
    //   状态 1.已完成，2，正在进行中，3.延期完成，4延期未完成
    private Long status;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getSub_pid() {
        return sub_pid;
    }

    public void setSub_pid(Long sub_pid) {
        this.sub_pid = sub_pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getSub_pname() {
        return sub_pname;
    }

    public void setSub_pname(String sub_pname) {
        this.sub_pname = sub_pname;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}