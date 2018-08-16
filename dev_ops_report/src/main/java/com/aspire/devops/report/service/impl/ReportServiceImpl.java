package com.aspire.devops.report.service.impl;

import com.aspire.devops.report.dao.ReportDao;
import com.aspire.devops.report.model.BuildBugsQueryResut;
import com.aspire.devops.report.model.BuildManHaurTop5Info;
import com.aspire.devops.report.model.ProgressInfo;
import com.aspire.devops.report.model.Top5ResultInfo;
import com.aspire.devops.report.service.CommonService;
import com.aspire.devops.report.service.ReportService;
import com.aspire.devops.report.vo.BuildBugsQueryParamsVo;
import com.aspire.devops.report.vo.ProgressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 版本进度统计Service实现
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service.impl
 * fileName: ReportServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/23 09:32
 */
@Service
@Transactional
public class ReportServiceImpl extends CommonService implements ReportService {

    @Autowired
    private ReportDao reportDao;

    /***
     * 版本进度统计
     * @param pid 大项目
     * @param years 年份
     * @return 版本进度信息
     */
    public ProgressInfo progress(Integer pid, Integer years) {
        ProgressVo vo = new ProgressVo();
        initQueryParams(pid, null, years, null, vo);
        return reportDao.progress(vo);
    }

    public List<Top5ResultInfo> top5(Integer pid, Integer years, Integer limits) {
        if (StringUtils.isEmpty(pid)) {
            return top5InCompany(years, limits);
        } else {
            return top5InPid(pid, years, limits);
        }
    }

    public List<Top5ResultInfo> top5InCompany(Integer years, Integer limits) {
        BuildBugsQueryParamsVo vo = new BuildBugsQueryParamsVo();
        initQueryParams(null, null, years, null, vo);
        vo.setLimits(limits == null ? 5 : (limits == -1 ? 200 : limits));
        List<BuildManHaurTop5Info> manHaurList = reportDao.top5byManhaur(vo);
        List<Top5ResultInfo> list = new ArrayList<Top5ResultInfo>();
        StringBuilder ids = new StringBuilder();

        Top5ResultInfo tmp;
        for (BuildManHaurTop5Info info : manHaurList) {
            tmp = new Top5ResultInfo();
            tmp.setManHaur(info.getManHaur());
            tmp.setPid(info.getId());
            tmp.setPname(info.getName());
            tmp.setBugs(0);
            tmp.setCodeQuality(0f);
            tmp.setTestBugs(0);
            tmp.setUt(0f);
            ids.append(info.getId()).append(",");
            list.add(tmp);
        }
        ids.append("-1");
        vo.setIds(ids.toString());
        List<BuildBugsQueryResut> bugsResultList = reportDao.getBuildBugs(vo);
        if (CollectionUtils.isEmpty(bugsResultList)) {
            return list;
        }
        for (Top5ResultInfo info : list) {
            for (BuildBugsQueryResut rst : bugsResultList) {
                if (info.getPid().equals(rst.getId())) {
                    info.setUt(rst.getUt());
                    info.setCodeQuality(rst.getIssues());
                    info.setBugs(rst.getBugs());
                    info.setTestBugs(rst.getTestBugs());
                }
            }
        }
        return list;
    }


    public List<Top5ResultInfo> top5InPid(Integer pid, Integer years, Integer limits) {
        BuildBugsQueryParamsVo vo = new BuildBugsQueryParamsVo();
        initQueryParams(pid, null, years, null, vo);
        vo.setLimits(limits == null ? 5 : (limits == -1 ? 200 : limits));
        return reportDao.top5ByPid(vo);
    }


}