package com.aspire.devops.report.dao;

import com.aspire.devops.report.model.ProjectInfo;
import com.aspire.devops.report.model.UserInfo;
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
public class UserDaoTest {

	@Autowired
	private UserDao userDao;

	@Test
	public void loginTest() throws Exception {
		UserInfo info = userDao.login("sunke","aaa111");
		Assert.assertNotNull(info);
	}

}
