package com.aspire.devops.report.service.impl;


import com.aspire.devops.report.model.ProjectInfo;
import com.aspire.devops.report.model.UserInfo;
import com.aspire.devops.report.service.BaseInfoService;
import com.aspire.devops.report.service.UserService;
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
@Transactional
public class UserServiceImplTest {

	@Autowired
	private UserService userService;

	@Test
	public void list() throws Exception {
		UserInfo info = userService.login("sunke", "aaa111");
		Assert.assertNotNull(info);
	}

}
