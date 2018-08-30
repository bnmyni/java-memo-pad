package com.tuoming.mes.strategy.dao;

import java.util.List;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.AlarmInfoModel;

/**
 * 告警信息录入数据库访问接口
 * @author Administrator
 *
 */
public interface AlarmInfoImpDao extends BaseDao<AlarmInfoModel, Integer> {
	
	/**
	 * 查询告警配置
	 * @param groupName
	 * @return
	 */
	List<AlarmInfoModel> queryAlarmSet(String groupName);

    /**
     * 清除表数据
     * @param resTable
     */
	void removeData(String resTable);

	/**
	 * 更新告警信息表
	 * @param exeSql
	 */
	void updateAlarmInfo(String exeSql);
	

}
