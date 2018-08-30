package com.tuoming.mes.strategy.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.SavePowerMontiorModel;

/**
 * 查询节能小区监控数据访问接口
 * @author Administrator
 *
 */
public interface SavePowerMontiorDao extends BaseDao<SavePowerMontiorModel, Integer>{


	/**
	 * 查询监控主流程配置表
	 * @param groupName
	 * @return
	 */
	public List<SavePowerMontiorModel> querySetList(String groupName);

	/**
	 * 更新黑名单
	 * @param collDate
	 * @param blackSql
	 */
	public void updateBlack(Date collDate, String zs);

	/**
	 * 添加监控小区状态：即当前采集的小区是否差小区和补偿小区
	 * @param exeSql
	 * @param collDate
	 */
	public void addMotiorCellState(String exeSql, Date collDate);

	/**
	 * 删除历史数据
	 * @param rsTable
	 */
	public void delHisData(String tableName);



}
