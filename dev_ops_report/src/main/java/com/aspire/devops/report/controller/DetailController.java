package com.aspire.devops.report.controller;

import com.aspire.devops.report.dto.ProgressDetailResponse;
import com.aspire.devops.report.dto.ReportRequest;
import com.aspire.devops.report.dto.Result;
import com.aspire.devops.report.model.*;
import com.aspire.devops.report.service.ReportDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 明细查询controller
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.controller
 * fileName: DetailController.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/18 14:13
 */
@Controller
@RequestMapping("/detail")
public class DetailController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReportDetailService reportDetailService;

    @RequestMapping(value = "/progress", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<BuildProgressDetailInfo>> progress(@RequestBody ReportRequest request) {
        ProgressDetailResponse response = new ProgressDetailResponse();
        logger.info("detail progress request:{}", request);
        List<BuildProgressDetailInfo> list = reportDetailService.getProgressDetail(request.getPid(), request.getSub_pid(), request.getStatus(), request.getYears());
        logger.info("detail progress response data:{}", list);
        return new Result<List<BuildProgressDetailInfo>>(200, list, "success");
    }

    @RequestMapping(value = "/cost", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<BuildCostDetailInfo>> getCostDetail(@RequestBody ReportRequest request) {
        logger.info("detail cost request:{}", request);
        List<BuildCostDetailInfo> list = reportDetailService.getCostDetail(request.getPid(), request.getSub_pid(), request.getStatus(), request.getYears());
        logger.info("detail cost data:{}", list);
        return new Result<List<BuildCostDetailInfo>>(200, list, "success");
    }

    @RequestMapping(value = "/ut", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<BuildUtDetailInfo>> getUtDetail(@RequestBody ReportRequest request) {
        logger.info("detail ut request:{}", request);
        List<BuildUtDetailInfo> list = reportDetailService.getUtDetail(request.getPid(), request.getSub_pid(), request.getStatus(), request.getYears());
        logger.info("detail ut data:{}", list);
        return new Result<List<BuildUtDetailInfo>>(200, list, "success");
    }

    @RequestMapping(value = "/bugs", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<BuildBugsDetailInfo>> getBugsDetail(@RequestBody ReportRequest request) {
        logger.info("detail ut request:{}", request);
        List<BuildBugsDetailInfo> list = reportDetailService.getBugsDetail(request.getPid(), request.getSub_pid(), request.getStatus(), request.getYears());
        logger.info("detail ut data:{}", list);
        return new Result<List<BuildBugsDetailInfo>>(200, list, "success");
    }

    @RequestMapping(value = "/code_analysis", method = RequestMethod.POST,
            produces = {"application/json; charset=utf-8"})
    @ResponseBody
    private Result<List<BuildCodeAnalysisInfo>> getCodeAnalysisDetail(@RequestBody ReportRequest request) {
        logger.info("detail ut request:{}", request);
        List<BuildCodeAnalysisInfo> list = reportDetailService.getCodeAnalysis(request.getPid(), request.getSub_pid(), request.getStatus(), request.getYears());
        logger.info("detail ut data:{}", list);
        return new Result<List<BuildCodeAnalysisInfo>>(200, list, "success");
    }

}