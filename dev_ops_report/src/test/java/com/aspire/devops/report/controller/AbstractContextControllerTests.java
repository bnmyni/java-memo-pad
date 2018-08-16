package com.aspire.devops.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

/**
 * Abstract controller test
 * Copyright ©scoco
 * package: com.scoco.demo.controller
 * fileName: AbstractContextControllerTests.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 14:40
 */
@WebAppConfiguration
@ContextConfiguration({ "classpath:spring/spring-web.xml", "classpath:spring/spring-service.xml",
		"classpath:spring/spring-dao.xml" })
public class AbstractContextControllerTests {

	@Autowired
	protected WebApplicationContext wac;
}