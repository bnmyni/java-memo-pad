package com.scoco.demo.enums;

/**
 * 使用枚举表述常量数据字典
 * Copyright ©scoco
 * package: com.scoco.demo.enums
 * fileName: AppointStateEnum.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/7/10 15:19
 */
public enum AppointStateEnum {

	SUCCESS(1, "预约成功"), NO_NUMBER(0, "库存不足"), REPEAT_APPOINT(-1, "重复预约"), INNER_ERROR(-2, "系统异常");

	private int state;

	private String stateInfo;

	private AppointStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public int getState() {
		return state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public static AppointStateEnum stateOf(int index) {
		for (AppointStateEnum state : values()) {
			if (state.getState() == index) {
				return state;
			}
		}
		return null;
	}

}
