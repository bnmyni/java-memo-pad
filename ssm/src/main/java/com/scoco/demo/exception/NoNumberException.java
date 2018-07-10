package com.scoco.demo.exception;

/**
 * 库存不足异常
 * Copyright ©scoco
 * package: com.scoco.demo.exception
 * fileName: NoNumberException.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:20
 */
public class NoNumberException extends RuntimeException {

	public NoNumberException(String message) {
		super(message);
	}

	public NoNumberException(String message, Throwable cause) {
		super(message, cause);
	}

}
