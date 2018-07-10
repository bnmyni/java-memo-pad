package com.scoco.demo.exception;

/**
 * 预约业务异常
 * Copyright ©scoco
 * package: com.scoco.demo.exception
 * fileName: AppointException.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:20
 */
public class AppointException extends RuntimeException {

	public AppointException(String message) {
		super(message);
	}

	public AppointException(String message, Throwable cause) {
		super(message, cause);
	}

}
