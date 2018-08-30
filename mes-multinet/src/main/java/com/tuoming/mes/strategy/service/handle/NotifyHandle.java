package com.tuoming.mes.strategy.service.handle;

import java.util.Date;

import com.tuoming.mes.strategy.model.SavePowerMontiorModel;

/**
 * 唤醒处理器
 * @author Administrator
 *
 */
public interface NotifyHandle {
	
	/**
	 * 进行唤醒操作
	 * @param set
	 * @param isAzimuth
	 */
	void handle(String group, Date collectDate);

}
