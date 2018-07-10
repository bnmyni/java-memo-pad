package com.scoco.demo.dao;

import com.scoco.demo.model.Appointment;
import org.apache.ibatis.annotations.Param;

/**
 * 图书预约处理
 * Copyright ©scoco
 * package: com.scoco.demo.dao
 * fileName: AppointmentDao.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:17
 */
public interface AppointmentDao {

	/**
	 * 插入预约图书记录
	 * 
	 * @param bookId 图书id
	 * @param studentId 学生id
	 * @return 插入的行数
	 */
	int insertAppointment(@Param("bookId") long bookId, @Param("studentId") long studentId);

	/**
	 * 通过主键查询预约图书记录，并且携带图书实体
	 * 
	 * @param bookId 图书id
	 * @param studentId 学生id
	 * @return 返回预约信息
	 */
	Appointment queryByKeyWithBook(@Param("bookId") long bookId, @Param("studentId") long studentId);

}
