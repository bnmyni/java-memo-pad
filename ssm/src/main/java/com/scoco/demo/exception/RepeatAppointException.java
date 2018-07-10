package com.scoco.demo.exception;

/**
 * 重复预约异常
 * Copyright ©scoco
 * package: com.scoco.demo.exception
 * fileName: RepeatAppointException.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:20
 */
public class RepeatAppointException extends RuntimeException {

	public RepeatAppointException(String message) {
		super(message);
	}

	public RepeatAppointException(String message, Throwable cause) {
		super(message, cause);
	}

}
