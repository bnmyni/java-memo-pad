package com.aspire.devops.report.service;

import com.aspire.devops.report.model.*;
import com.aspire.devops.report.vo.ProgressVo;

import java.util.List;

/**
 * 报表明细查询
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service
 * fileName: ReportDetailService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 10:45
 */
public interface ReportDetailService {

    List<BuildProgressDetailInfo> getProgressDetail(Integer pid, Integer subPid, Integer status, Integer years);

    List<BuildCostDetailInfo> getCostDetail(Integer pid, Integer subPid, Integer status, Integer years);

    List<BuildUtDetailInfo> getUtDetail(Integer pid, Integer subPid, Integer status, Integer years);

    List<BuildBugsDetailInfo> getBugsDetail(Integer pid, Integer subPid, Integer status, Integer years);

    List<BuildCodeAnalysisInfo> getCodeAnalysis(Integer pid, Integer subPid, Integer status, Integer years);
}