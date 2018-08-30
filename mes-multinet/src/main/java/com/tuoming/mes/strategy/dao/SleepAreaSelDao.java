package com.tuoming.mes.strategy.dao;

import java.util.List;
import java.util.Map;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.SleepSelectModel;

/**
 * 休眠小区筛选数据访问接口
 * @author Administrator
 *
 */
public interface SleepAreaSelDao extends BaseDao<SleepSelectModel, Integer>{

	/**
	 * 查询休眠小区筛选配置
	 * @param group
	 * @return
	 */
	List<SleepSelectModel> querySleepAreaSelSet(String group);

	/**
	 * 查询门限字典
	 * @return
	 */
	List<Map<String, Object>> queryGsmDicList();

	/**
	 * 查询td制式的数据字典列表
	 * @return
	 */
	List<Map<String, Object>> queryTdDicList();

	/**
	 * 查询lte制式门限字典
	 * @return
	 */
	Map<String, Double> queryLteDic();


	/**
	 * 查询休眠-补偿小区优先级
	 * @return
	 */
	List<String> queryPriorities();

	/**
	 * 删除lte小区不唯一冲突
	 * @param l2lAzimuth 
	 */
	void deleteConflictsForLte(boolean l2lAzimuth);

	/**
	 * 查询gsm小区同时补偿td和gsm的小区
	 * @param gsmPriority 
	 * @param tdPriority 
	 * @return
	 */
	Map<String, List<Map<String, Object>>> queryMakeUpGsmAndTd(boolean g2gAzimuth, boolean t2gAzimuth, int tdPriority, int gsmPriority);

	/**
	 * 根据结果删除指定G2g休眠小区-节能小区表
	 * @param list
	 * @param isAzimuth
	 */
	void deleteG2gByList(Map<String, Object> data , boolean isAzimuth);
	
	/**
	 * 根据结果删除指定T2g休眠小区-节能小区表
	 * @param list
	 * @param isAzimuth
	 */
	void deleteT2gByList(Map<String, Object> data, boolean isAzimuth);

	/**
	 * 根据G2G休眠小区删除T2G补偿小区
	 * 
	 * @param collectTime
	 * @param isAzimuth
	 */
	void delMakeUpConflictInT2GByG2G(boolean g2gAzimuth, boolean t2gAzimuth);

	/**
	 * 根据T2G休眠小区删除L2T补偿小区
	 * 
	 * @param collectTime
	 * @param t2Azimuth
	 */
	//void delMakeUpConflictInL2TByT2G(boolean t2Azimuth);

	/**
	 * 根据L2T的补偿小区删除T2G的休眠小区
	 * 
	 * @param collectTime
	 * @param isAzimuth
	 */
	//void delSleepConflictInT2GByL2T(boolean isAzimuth);

	/**
	 * 根据T2G的补偿小区删除G2G的休眠小区
	 * 
	 * @param collectTime
	 * @param isAzimuth
	 */
	void delSleepConflictInG2GByT2G(boolean g2gAzimuth, boolean t2gAzimuth);

	/**
	 * 根据L2L补偿小区删除L2T的休眠小区
	 * 
	 * @param collectTime
	 * @param isAzimuth
	 */
	//void delSleepConflictInL2TByL2L(boolean l2lAzimuth);

	/**
	 * 查询休眠小区筛选元数据
	 * @param sleepDate
	 * @param querySql
	 * @return
	 */
	List<Map<String, Object>> queryMetaData(String querySql);

	/**
	 * 清除上一时刻休眠补偿关系对应表
	 * @param resTable
	 */
	void removeAllData(String resTable);

	/**
	 * 查询l2l,t2g,g2g,l2t是基于方位角还是mr
	 */
	List<Map<String, Integer>> queryScene();

	/**
	 * 删除g2g中存在于休眠小区的补偿小区
	 * @param g2gAzimuth
	 */
	void deleteMakeUpBySleepForG2g(boolean g2gAzimuth);

	/**
	 * 删除l2l中存在于休眠小区的补偿小区
	 * @param l2lAzimuth
	 */
	void deleteMakeUpBySleepForL2l(boolean l2lAzimuth);

	/**
	 * 查询休眠唤醒字典
	 * @return
	 */
	Map<String, Double> querySleepNotifyDic();
	
	/**
	 * 根据话务量反查爱尔兰B表信道数
	 * @return
	 */
	int queryERLangB(double dest_hwl);
	/******************Neusoft**********************/
	/**
	 * L2L的休眠小区中去除已经在T2L情况作为补偿小区的LTE小区
	 * @param l2lAzimuth
	 * @param t2lAzimuth
	 */
	void delSleepConflictInL2LByT2L(boolean l2lAzimuth, boolean t2lAzimuth);
	/**
	 * T2L的补偿小区中去除已经在L2L情况作为休眠小区的LTE小区
	 * @param t2lAzimuth
	 * @param l2lAzimuth
	 */
	void delMakeUpConflictInT2LByL2L(boolean t2lAzimuth, boolean l2lAzimuth);
	
	/**
	 * 筛选出LTE小区同时补偿LTE,TD及多补一中LTE情况的小区
	 * @param t2lAzimuth
	 * @param l2lAzimuth
	 * @param tdPriority
	 * @param ltePriority
	 */
	Map<String, List<Map<String, Object>>> queryMakeUpLteAndTd(boolean t2lAzimuth, boolean l2lAzimuth, int tdPriority, int ltePriority);
	
	void deleteT2lByList(Map<String, Object> data, boolean isAzimuth);
	
	void deleteL2lByList(Map<String, Object> data, boolean isAzimuth);
	
	void deleteL2lManyByList(Map<String, Object> data);
	
	/**
	 * 删除T2T中存在于休眠小区的补偿小区
	 * @param t2tAzimuth
	 */
	public void deleteMakeUpBySleepForT2T();
	
	/**
	 * T2T多补一的补偿小区中去除已经在T2G、T2L情况作为休眠小区的TD小区
	 * @param t2lAzimuth
	 * @param l2lAzimuth
	 */
	void delMakeUpConflictInT2TByT2GT2L(boolean t2gAzimuth, boolean t2lAzimuth);
}
