package com.aspire.devops.report.service.impl;

import com.aspire.devops.report.dao.RequirementDao;
import com.aspire.devops.report.model.RequirementInfo;
import com.aspire.devops.report.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 需求查询service实现
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service.impl
 * fileName: RequirementServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/20 17:03
 */
@Service
@Transactional
public class RequirementServiceImpl implements RequirementService {

    @Autowired
    private RequirementDao requirementDao;
    /**
     * 通过关键字进行需求查询
     *
     * @param content 需求关键字
     * @return 和关键字匹配的需求版本信息
     */
    public List<RequirementInfo> list(String content) {
        return requirementDao.list(content);
    }
}