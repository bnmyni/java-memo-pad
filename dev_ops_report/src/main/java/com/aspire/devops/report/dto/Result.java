package com.aspire.devops.report.dto;

/**
 * 封装json对象，所有返回结果都使用它
 * Copyright ©scoco
 * package: com.scoco.demo.dto
 * fileName: Result.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:19
 */
public class Result<T> {

	private Integer code;

	private T data;

	private String message;

	public Result() {
	}

	public Result(Integer code, T data, String message) {
		this.code = code;
		this.data = data;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
