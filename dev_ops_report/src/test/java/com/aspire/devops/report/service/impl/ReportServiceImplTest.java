package com.aspire.devops.report.service.impl;


import com.aspire.devops.report.model.ProgressInfo;
import com.aspire.devops.report.model.ProjectInfo;
import com.aspire.devops.report.model.Top5ResultInfo;
import com.aspire.devops.report.service.BaseInfoService;
import com.aspire.devops.report.service.ReportService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * spring-test && junit: load spring ioc when junit start
 * Copyright © scoco
 * package: com.scoco.demo.service.impl
 * fileName: BaseInfoServiceImplTest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 14:28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ReportServiceImplTest {

	@Autowired
	private ReportService reportService;

	@Test
	public void testProgress() throws Exception {
		ProgressInfo info = reportService.progress(null,null);
		Assert.assertNotNull(info);


		info = reportService.progress(null,2018);
		Assert.assertNotNull(info);

		info = reportService.progress(null,2018);
		Assert.assertNotNull(info);

	}

	@Test
	public void testTop5() throws Exception {
		List<Top5ResultInfo> top5 = reportService.top5(null, null, null);
		Assert.assertNotNull(top5);
	}

}
