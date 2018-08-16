package com.aspire.devops.report.service;

import com.aspire.devops.report.model.ProjectInfo;

import java.util.List;

/**
 * 项目基础信息查询
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service
 * fileName: BaseInfoService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/19 10:39
 */
public interface BaseInfoService {

    List<ProjectInfo> list();
}