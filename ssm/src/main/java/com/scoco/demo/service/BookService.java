package com.scoco.demo.service;

import java.util.List;

import com.scoco.demo.dto.AppointExecution;
import com.scoco.demo.model.Book;

/**
 * 业务接口
 * Copyright ©scoco
 * package: com.scoco.demo.service
 * fileName: BookService.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:22
 */
public interface BookService {

	/**
	 * 查询一本图书
	 * 
	 * @param bookId 图书id
	 * @return 图书信息
	 */
	Book getById(long bookId);

	/**
	 * 查询所有图书
	 * 
	 * @return 图书列表信息
	 */
	List<Book> getList();

	/**
	 * 预约图书
	 * 
	 * @param bookId 图书id
	 * @param studentId 学生id
	 * @return 预约信息
	 */
	AppointExecution appoint(long bookId, long studentId);

}
