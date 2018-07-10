package com.scoco.demo.service.impl;

import java.util.List;

import com.scoco.demo.dao.AppointmentDao;
import com.scoco.demo.dao.BookDao;
import com.scoco.demo.dto.AppointExecution;
import com.scoco.demo.model.Appointment;
import com.scoco.demo.model.Book;
import com.scoco.demo.enums.AppointStateEnum;
import com.scoco.demo.exception.AppointException;
import com.scoco.demo.exception.NoNumberException;
import com.scoco.demo.exception.RepeatAppointException;
import com.scoco.demo.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 图书业务实现
 * Copyright ©scoco
 * package: com.scoco.demo.service.impl
 * fileName: BookServiceImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:23
 */
@Service
public class BookServiceImpl implements BookService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BookDao bookDao;

	@Autowired
	private AppointmentDao appointmentDao;


	public Book getById(long bookId) {
		return bookDao.queryById(bookId);
	}

	public List<Book> getList() {
		return bookDao.queryAll(0, 1000);
	}

	@Transactional
	public AppointExecution appoint(long bookId, long studentId) {
		try {
			int update = bookDao.reduceNumber(bookId);
			if (update <= 0) {
				throw new NoNumberException("no number");
			} else {
				int insert = appointmentDao.insertAppointment(bookId, studentId);
				if (insert <= 0) {
					throw new RepeatAppointException("repeat appoint");
				} else {
					Appointment appointment = appointmentDao.queryByKeyWithBook(bookId, studentId);
					return new AppointExecution(bookId, AppointStateEnum.SUCCESS, appointment);
				}
			}
		} catch (NoNumberException e1) {
			throw e1;
		} catch (RepeatAppointException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppointException("appoint inner error:" + e.getMessage());
		}
	}

}
