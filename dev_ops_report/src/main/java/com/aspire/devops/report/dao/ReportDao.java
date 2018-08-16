package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.BuildBugsQueryResut;
import com.aspire.devops.report.model.BuildManHaurTop5Info;
import com.aspire.devops.report.model.ProgressInfo;
import com.aspire.devops.report.model.Top5ResultInfo;
import com.aspire.devops.report.vo.BuildBugsQueryParamsVo;
import com.aspire.devops.report.vo.ProgressVo;

import java.util.List;

/**
 * 看板报表统计DAO
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.dao
 * fileName: ReportDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/23 09:33
 */
public interface ReportDao {
    /**
     * 版本进度统计
     * @param vo ProgressVo
     * @return 版本进度汇总信息
     */
    ProgressInfo progress(ProgressVo vo);

    /**
     * 根据大项目分组按工时总量排序查询
     *  @param vo ProgressVo
     * @return 查询结果
     */
    List<BuildManHaurTop5Info> top5byManhaur(BuildBugsQueryParamsVo vo);

    /**
     * 版本缺陷统计
     * @param vo BuildBugsQueryParamsVo
     * @return 版本缺陷统计结果
     */
    List<BuildBugsQueryResut> getBuildBugs(BuildBugsQueryParamsVo vo);

    /**
     *
     * @param vo
     * @return
     */
    List<Top5ResultInfo> top5ByPid(BuildBugsQueryParamsVo vo);
}