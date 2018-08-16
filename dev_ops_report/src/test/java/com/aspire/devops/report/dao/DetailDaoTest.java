package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.BuildCostDetailInfo;
import com.aspire.devops.report.model.BuildProgressDetailInfo;
import com.aspire.devops.report.model.ProgressInfo;
import com.aspire.devops.report.vo.ProgressVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * spring-test && junit: load spring ioc when junit start
 * Copyright ©scoco
 * package: com.scoco.demo.dao
 * fileName: BaseInfoDaoTest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 14:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class DetailDaoTest {

	@Autowired
	private DetailDao detailDao;

	@Test
	public void testProgress() throws Exception {
		ProgressVo vo  = new ProgressVo();
		List<BuildProgressDetailInfo> list = detailDao.getProgressDetail(vo);
		Assert.assertNotNull(list);

	}

	@Test
	public void testCost() throws Exception {
		ProgressVo vo  = new ProgressVo();
		List<BuildCostDetailInfo> list = detailDao.getCostDetail(vo);
		Assert.assertNotNull(list);
	}

}
