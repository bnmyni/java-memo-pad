package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.RequirementInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 需求查询DAO
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dao
 * fileName: RequirementDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/20 17:05
 */
public interface RequirementDao {

    /**
     * 需求查询
     * @param content 需求关键字
     * @return 匹配的需求列表
     */
    List<RequirementInfo> list(@Param("content") String content);
}