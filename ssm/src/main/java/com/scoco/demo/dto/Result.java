package com.scoco.demo.dto;

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

	private boolean success;// 是否成功标志

	private T data;// 成功时返回的数据

	private String error;// 错误信息

	public Result() {
	}

	// 成功时的构造器
	public Result(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	// 错误时的构造器
	public Result(boolean success, String error) {
		this.success = success;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "JsonResult [success=" + success + ", data=" + data + ", error=" + error + "]";
	}

}
