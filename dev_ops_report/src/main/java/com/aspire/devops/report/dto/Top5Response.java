package com.aspire.devops.report.dto;

/**
 * 管理看板top5响应结果
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dto
 * fileName: Top5Response.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/18 14:09
 */
public class Top5Response {
    //大项目id
    private Long pid;
    //大项目名称
    private String pname;
    //子项目id
    private String sub_pid;
    // 子项目名称
    private String sub_pname;
    // 代码质量分析均值
    private Long code_quality;
    // 现网bug
    private Long bugs;
    // 系统测试bug数
    private Long test_bugs;
    // 单元测试覆盖率
    private Long ut;
    // 投入工时总数
    private Long man_haur;

    public String getSub_pid() {
        return sub_pid;
    }

    public void setSub_pid(String sub_pid) {
        this.sub_pid = sub_pid;
    }

    public String getSub_pname() {
        return sub_pname;
    }

    public void setSub_pname(String sub_pname) {
        this.sub_pname = sub_pname;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public Long getCode_quality() {
        return code_quality;
    }

    public void setCode_quality(Long code_quality) {
        this.code_quality = code_quality;
    }

    public Long getBugs() {
        return bugs;
    }

    public void setBugs(Long bugs) {
        this.bugs = bugs;
    }

    public Long getTest_bugs() {
        return test_bugs;
    }

    public void setTest_bugs(Long test_bugs) {
        this.test_bugs = test_bugs;
    }

    public Long getUt() {
        return ut;
    }

    public void setUt(Long ut) {
        this.ut = ut;
    }

    public Long getMan_haur() {
        return man_haur;
    }

    public void setMan_haur(Long man_haur) {
        this.man_haur = man_haur;
    }
}