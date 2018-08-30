package com.tuoming.mes.strategy.service;

import java.util.Map;

/**
 * 历史数据预测
 * @author Administrator
 *
 */
public interface HisDataFCastService {
	
	/**
	 * 预测下一时段数据
	 */
	public void fCastNextData(String groupName);

	/**
	 * 查询节能时段
	 * @return
	 */
	public Map<String, String> getMultinetPeriod();

}
