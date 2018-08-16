package com.aspire.devops.report.service.impl;

import com.aspire.devops.report.dao.BaseInfoDao;
import com.aspire.devops.report.model.ProjectInfo;
import com.aspire.devops.report.service.BaseInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目基础信息查询
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service.impl
 * fileName: BaseInfoServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/19 10:40
 */
@Service
@Transactional
public class BaseInfoServiceImpl implements BaseInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseInfoDao baseInfoDao;

    public List<ProjectInfo> list() {
        logger.debug("list project info .....");
        return baseInfoDao.list();
    }
}