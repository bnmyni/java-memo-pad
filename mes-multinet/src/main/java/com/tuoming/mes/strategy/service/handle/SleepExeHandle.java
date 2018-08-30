package com.tuoming.mes.strategy.service.handle;

import java.util.List;
import java.util.Map;

import com.tuoming.mes.strategy.model.SleepExeSetting;

public interface SleepExeHandle {
	
	/**
	 * 小区休眠流程处理器
	 * @param  
	 */
	public void handle(List<Map<String, Object>> dataList, int top, SleepExeSetting set, Map<String, Integer> rncCount);

}
