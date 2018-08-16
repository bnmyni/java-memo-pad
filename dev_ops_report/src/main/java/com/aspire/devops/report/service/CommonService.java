package com.aspire.devops.report.service;

import com.aspire.devops.report.vo.ProgressVo;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * service通用方法
 * Copyright © 2008   卓望公司
 * package: com.aspire.devops.report.service
 * fileName: CommonService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/07/31 10:53
 */
public abstract class CommonService {

    protected void initQueryParams(Integer pid, Integer sub_pid, Integer years, Integer status, ProgressVo vo) {
        vo.setStatus(status == null ? null : (status >= 1 && status <= 4 ? status : null));
        vo.setPid(pid);
        vo.setSub_pid(pid == null ? null : sub_pid);
        String startDate;
        String endDate;
        Calendar calendar = Calendar.getInstance();
        if (years == null || years == calendar.get(Calendar.YEAR)) {
            startDate = MessageFormat.format("{0}-01-01", calendar.get(Calendar.YEAR) + "");
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        } else {
            startDate = MessageFormat.format("{0}-01-01", years);
            endDate = MessageFormat.format("{0}-12-31", years);
        }
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
    }
}