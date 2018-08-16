package com.aspire.devops.report.service;

import com.aspire.devops.report.model.RequirementInfo;

import java.util.List;

/**
 * 需求查询service
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service
 * fileName: RequirementService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/20 17:00
 */
public interface RequirementService {
    /**
     * 通过关键字进行需求查询
     * @param content 需求关键字
     * @return 和关键字匹配的需求版本信息
     */
    List<RequirementInfo> list(String content);
}