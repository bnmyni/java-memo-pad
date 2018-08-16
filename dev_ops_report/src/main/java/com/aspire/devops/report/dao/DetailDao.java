package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.*;
import com.aspire.devops.report.vo.ProgressVo;

import java.util.List;

/**
 * 明细查询DAO
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dao
 * fileName: DetailDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 10:36
 */
public interface DetailDao {
    /**
     * 版本进度明细查询
     * @param vo 查询参数
     * @return 版本进度明细
     */
   List<BuildProgressDetailInfo> getProgressDetail(ProgressVo vo);

    /**
     * 版本成本明细查询
     * @param vo 查询参数
     * @return 版本成本明细
     */
   List<BuildCostDetailInfo> getCostDetail(ProgressVo vo);
    /**
     * 版本ut明细查询
     * @param vo 查询参数
     * @return 版本ut明细
     */
    List<BuildUtDetailInfo> getUtDetail(ProgressVo vo);

    /**
     * 版本ut明细查询
     * @param vo 查询参数
     * @return 版本ut明细
     */
    List<BuildBugsDetailInfo> getBugsDetail(ProgressVo vo);

    /**
     * 版本ut明细查询
     * @param vo 查询参数
     * @return 版本ut明细
     */
    List<BuildCodeAnalysisInfo> getCodeAnalysis(ProgressVo vo);
}