package com.scoco.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 使用MockMvc测试controller,配置事务回滚，循环使用测试用例
 * Copyright ©scoco
 * package: com.scoco.demo.controller
 * fileName: BookControllerTest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 14:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class BookControllerTest extends AbstractContextControllerTests {

	private MockMvc mockMvc;
	private String detailUrl = "/book/{bookId}/detail";
	private long bookId = 1000;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).alwaysDo(print()).build();
	}

	@Test
	public void list() throws Exception {
		String listUrl = "/book/list";
		this.mockMvc.perform(get(listUrl)).andExpect(view().name("list"));
	}

	@Test
	public void existDetail() throws Exception {
		this.mockMvc.perform(get(detailUrl, bookId)).andExpect(view().name("detail"))
				.andExpect(model().attributeExists("book"));
	}

	@Test
	public void notExistDetail() throws Exception {
		this.mockMvc.perform(get(detailUrl, 1100)).andExpect(forwardedUrl("/book/list"));
	}

	@Test
	public void appointTest() throws Exception {
		String appointUrl = "/book/{bookId}/appoint";
		this.mockMvc.perform(post(appointUrl, bookId).param("studentId", "1").accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType("application/json;charset=utf-8"));
	}
}
