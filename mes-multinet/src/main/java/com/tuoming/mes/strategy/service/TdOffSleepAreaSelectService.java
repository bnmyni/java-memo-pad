package com.tuoming.mes.strategy.service;

import java.util.Map;

public interface TdOffSleepAreaSelectService {
	/**
	 * 3G退网根据覆盖度或覆盖度+性能指标对小区进行筛选
	 * @param context
	 */
	public void tdNetworkOff(Map<String, String> context);
	/**
	 * 判断3G退网功能计算是否完成
	 * @return
	 */
	public boolean calFinish();
	
	/**
	 * 3G退网去除休眠小区及补偿小区中黑白名单、告警
	 */
	public void tdNetworkOffFilter();
	
	/**
	 * 查询执行按钮状态
	 * @return
	 */
	public int queryExecuteStatus();
	/**
	 * 指令生成
	 * 执行：(1)删除覆盖度表中3G退网小区;
	 * (2)删除动态休眠表中3G退网小区;
	 * (3)若小区已在动态休眠成功表中存在，则将此小区从成功表中移到3G退网休眠成功表中
	 * (4)生成休眠指令
	 * (5)休眠指令下发
	 */
	public void tdOffExecuteSleep();
	
	/**
	 * 取消执行操作
	 * (1)3G退网小区数据还原到覆盖度表;
	 * (2)生成唤醒指令
	 */
	public void tdOffExecuteNotify();
	/**
	 * 监控，监控静态休眠小区是否到唤醒时间
	 */
	public void tdOffMonitor();

}
