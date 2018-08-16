package com.aspire.devops.report.service.impl;

import com.aspire.devops.report.dao.DetailDao;
import com.aspire.devops.report.model.*;
import com.aspire.devops.report.service.CommonService;
import com.aspire.devops.report.service.ReportDetailService;
import com.aspire.devops.report.vo.ProgressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 报表明细查询Service
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service.impl
 * fileName: ReportDetailServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 10:47
 */
@Service
@Transactional
public class ReportDetailServiceImpl extends CommonService implements ReportDetailService {

    @Autowired
    private DetailDao detailDao;

    public List<BuildProgressDetailInfo> getProgressDetail(Integer pid, Integer subPid, Integer status, Integer years) {
        ProgressVo vo = new ProgressVo();
        initQueryParams(pid, subPid, years, status, vo);
        return detailDao.getProgressDetail(vo);
    }

    public List<BuildCostDetailInfo> getCostDetail(Integer pid, Integer subPid, Integer status, Integer years) {
        ProgressVo vo = new ProgressVo();
        initQueryParams(pid, subPid, years, status, vo);
        return detailDao.getCostDetail(vo);
    }

    public List<BuildUtDetailInfo> getUtDetail(Integer pid, Integer subPid, Integer status, Integer years) {
        ProgressVo vo = new ProgressVo();
        initQueryParams(pid, subPid, years, status, vo);
        return detailDao.getUtDetail(vo);
    }

    public List<BuildBugsDetailInfo> getBugsDetail(Integer pid, Integer subPid, Integer status, Integer years) {
        ProgressVo vo = new ProgressVo();
        initQueryParams(pid, subPid, years, status, vo);
        return detailDao.getBugsDetail(vo);
    }

    public List<BuildCodeAnalysisInfo> getCodeAnalysis(Integer pid, Integer subPid, Integer status, Integer years) {
        ProgressVo vo = new ProgressVo();
        initQueryParams(pid, subPid, years, status, vo);
        return detailDao.getCodeAnalysis(vo);
    }
}