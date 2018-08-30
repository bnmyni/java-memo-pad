package com.tuoming.mes.strategy.service.handle;

import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tuoming.mes.strategy.model.HisDataFCastSetting;

/**
 * 历史数据预测业务处理器
 * @author Administrator
 *
 */
public interface HisDataFCastHandle {
	
	/**
	 * 处理存在3个月以上的历史数据的预测
	 * @param dataList
	 * @param ps
	 * @param date 
	 */
	public void handleThrMon(HisDataFCastSetting setting, 
			PrintStream ps, Date date);
	
	/**
	 * 处理存在2个月以上的历史数据的预测
	 * @param dataList
	 * @param ps
	 * @param date 
	 */
	public void handleTwoMon(HisDataFCastSetting setting, 
			PrintStream ps, Date date);
	
	/**
	 * 处理存在1个月以上的历史数据的预测
	 * @param dataList
	 * @param ps
	 */
	public void handleOneMon(HisDataFCastSetting setting, 
			PrintStream ps, Date date);

	/**
	 * 处理不满足一个月的历史数据预测
	 * @param setting
	 * @param ps
	 */
	public void handleNoMon(HisDataFCastSetting setting, PrintStream ps, Date date);
}
