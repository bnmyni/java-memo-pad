package com.scoco.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scoco.demo.model.Book;
/**
 * 图书信息DAO
 * Copyright ©scoco
 * package: com.scoco.demo.dao
 * fileName: BookDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:17
 */
public interface BookDao {

	/**
	 * 通过ID查询单本图书
	 * 
	 * @param id 图书id
	 * @return 图书信息
	 */
	Book queryById(long id);

	/**
	 * 查询所有图书
	 * 
	 * @param offset 查询起始位置
	 * @param limit 查询条数
	 * @return 图书列表
	 */
	List<Book> queryAll(@Param("offset") int offset, @Param("limit") int limit);

	/**
	 * 减少馆藏数量
	 * 
	 * @param bookId 图书id
	 * @return 如果影响行数等于>1，表示更新的记录行数
	 */
	int reduceNumber(long bookId);

}
