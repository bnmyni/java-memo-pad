package com.aspire.devops.report.controller;

import com.aspire.devops.report.dto.Result;
import com.aspire.devops.report.model.ProjectInfo;
import com.aspire.devops.report.service.BaseInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 项目基础信息，元数据控制层
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.controller
 * fileName: BaseInfoController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/19 10:15
 */
@Controller
@RequestMapping("/base")
public class BaseInfoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseInfoService service;

    @RequestMapping(value = "/project", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<ProjectInfo>> list() {

        logger.info("/base/project request without request params");
        List<ProjectInfo> list = service.list();
        logger.info("base/project response detail:{}", list);
        return new Result<List<ProjectInfo>>(200, list, "success");
    }
}