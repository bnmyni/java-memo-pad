package com.aspire.devops.report.model;

/**
 * 按大项目工时消耗总量top5数据结果（过程对象）
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.model
 * fileName: BuildManHaurTop5Info.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/30 20:31
 */
public class BuildManHaurTop5Info {

    private Integer manHaur;

    private Integer id;

    private String name;

    public Integer getManHaur() {
        return manHaur;
    }

    public void setManHaur(Integer manHaur) {
        this.manHaur = manHaur;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}