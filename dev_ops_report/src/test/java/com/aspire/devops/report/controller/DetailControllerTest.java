package com.aspire.devops.report.controller;

import com.aspire.devops.report.dto.ReportRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * 使用MockMvc测试controller,配置事务回滚，循环使用测试用例
 * Copyright ©scoco
 * package: com.scoco.demo.controller
 * fileName: BaseInfoControllerTest.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 14:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class DetailControllerTest extends AbstractContextControllerTests {

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).alwaysDo(print()).build();
	}

	@Test
	public void progressTest() throws Exception {

		ReportRequest request = new ReportRequest();
//		request.setYears(2018);
		request.setStatus(1);
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(request);

		this.mockMvc.perform(post("/detail/progress")
				.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(content().contentType("application/json;charset=utf-8")).andReturn();
	}


	@Test
	public void getCostDetailTest() throws Exception {

		ReportRequest request = new ReportRequest();
		request.setYears(2018);
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(request);

		this.mockMvc.perform(post("/detail/cost")
				.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(content().contentType("application/json;charset=utf-8")).andReturn();
	}


	@Test
	public void getUtDetailTest() throws Exception {

		ReportRequest request = new ReportRequest();
		request.setYears(2018);
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(request);

		this.mockMvc.perform(post("/detail/ut")
				.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(content().contentType("application/json;charset=utf-8")).andReturn();
	}

	@Test
	public void getBugsDetailTest() throws Exception {

		ReportRequest request = new ReportRequest();
		request.setYears(2018);
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(request);

		this.mockMvc.perform(post("/detail/bugs")
				.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(content().contentType("application/json;charset=utf-8")).andReturn();
	}

}
