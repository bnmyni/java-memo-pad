package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.ProjectInfo;

import java.util.List;

/**
 * 基础信息查询
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dao
 * fileName: BaseInfoDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/19 10:41
 */
public interface BaseInfoDao {

    List<ProjectInfo> list();
}