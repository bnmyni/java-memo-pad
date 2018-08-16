package com.aspire.devops.report.controller;

import com.aspire.devops.report.dto.*;
import com.aspire.devops.report.model.RequirementInfo;
import com.aspire.devops.report.service.RequirementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 需求查询控制
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.controller
 * fileName: RequirementContoller.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/20 15:45
 */
@Controller
@RequestMapping("/requirement")
public class RequirementController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RequirementService requirementService;

    @RequestMapping(value = "/query", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<RequirementInfo>> query(@RequestBody RequirementRequest request) {
        logger.info("/requirement/query progress request:{}", request);
        if (StringUtils.isEmpty(request.getDemand())) {
            return  new Result<List<RequirementInfo>>(200, null, "success");
        }
        List<RequirementInfo> list = requirementService.list(request.getDemand());
        logger.info("/requirement/query progress response data:{}", list);
        return new Result<List<RequirementInfo>>(200, list, "success");
    }
}