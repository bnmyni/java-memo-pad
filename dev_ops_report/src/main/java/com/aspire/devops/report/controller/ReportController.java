package com.aspire.devops.report.controller;

import com.aspire.devops.report.model.ProgressInfo;
import com.aspire.devops.report.dto.ReportRequest;
import com.aspire.devops.report.dto.Top5Response;
import com.aspire.devops.report.model.Top5ResultInfo;
import com.aspire.devops.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.aspire.devops.report.dto.Result;

import java.util.List;

/**
 * 管理看板报表
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.controller
 * fileName: ReportController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/18 11:32
 */
@Controller
@RequestMapping("/report")
public class ReportController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/progress", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<ProgressInfo> progress(@RequestBody ReportRequest request) {
        logger.info("start progress...");
        ProgressInfo response = reportService.progress(request.getPid(), request.getYears());
        logger.info("end progress...");
        return new Result<ProgressInfo>(response == null ? 204 : 200, response, "success");
    }

    @RequestMapping(value = "/top5", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<Top5ResultInfo>> top(@RequestBody ReportRequest request) {
        logger.info("start Top5...");
        List<Top5ResultInfo> list = reportService.top5(request.getPid(), request.getYears(), request.getLimits());
        logger.info("end Top5...");
        return new Result<List<Top5ResultInfo>>(200, list, "success");
    }

}