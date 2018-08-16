package com.aspire.devops.report.service;

import com.aspire.devops.report.model.ProgressInfo;
import com.aspire.devops.report.model.Top5ResultInfo;

import java.util.List;

/**
 * 看报报表统计Service
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service
 * fileName: ReportService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/23 09:30
 */
public interface ReportService {

    /***
     * 版本进度统计
     * @param pid 大项目
     * @param years 年份
     * @return 版本进度信息
     */
    ProgressInfo progress(Integer pid, Integer years);

    List<Top5ResultInfo> top5(Integer pid, Integer years, Integer limits);
}