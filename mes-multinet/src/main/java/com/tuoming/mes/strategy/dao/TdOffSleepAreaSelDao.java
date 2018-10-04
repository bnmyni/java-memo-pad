package com.tuoming.mes.strategy.dao;

import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.TdOffSleepSelectModel;

public interface TdOffSleepAreaSelDao extends BaseDao<TdOffSleepSelectModel, Integer> {

    public void removeAllData(String resTable);

    /**
     * 查询3G退网配置表
     *
     * @param groupName 分组名称
     * @param cal_type  计算方式，0：覆盖度 1：覆盖度+性能指标
     * @return
     */
    public List<TdOffSleepSelectModel> querySleepAreaSelSet(String groupName, int cal_type);

    public List<Map<String, Object>> queryMetaData(String querySql);

    /**
     * 查询3G退网TD降耗门限值
     *
     * @return
     */
    List<Map<String, Object>> queryTdDic();

    /**
     * 查询3G退网GSM降耗门限值
     *
     * @return
     */
    List<Map<String, Object>> queryGsmDic();

    /**
     * 查询3G退网GSM降耗门限值
     *
     * @return
     */
    List<Map<String, Object>> queryLteDic();

    /**
     * 按db_name,export_cols分组查询设置表
     *
     * @param busytype
     * @return
     */
    public List<TdOffSleepSelectModel> queryTdOffSetGroup(String busytype);

    /**
     * 查询每个线程处理的数据的条数
     *
     * @return
     */
    public String queryDataNum();

    /**
     * 根据场景类型查询覆盖度表中数据条数
     *
     * @param busytype
     * @return
     */
    public int queryDataCount(boolean isT2G);

    /**
     * 更新计算表结束时间
     */
    public void updateCalculate();

    /**
     * 更新计算表计算状态为计算finish=1
     */
    public void updateCalStatus();

    /**
     * 从覆盖度表中去除3G退网部分的数据
     *
     * @param isT2G
     */
    public void removeOverByTdOff(boolean isT2G);

    /**
     * 计算是否完成
     *
     * @return
     */
    public boolean calFinish();

    /**
     * 查询执行按钮的状态
     *
     * @return 0：不休眠也不唤醒  1：执行  2：取消执行  9: 监控流程
     */
    public int updateAndQueryExecuteStatus();

    /**
     * 从动态休眠表中删除3G退网小区
     *
     * @param isT2G
     */
    public void removeSleepByTdOff(boolean isT2G);

    /**
     * 如果小区为静态、永久休眠小区，则从动态休眠成功表中删除这部分数据，
     * 同时将数据添加到静态、永久休眠成功表中
     */
    public void addTdOffFromDynamicSleep(boolean isT2G);

    /**
     * 将3G退网数据还原到覆盖度表
     */
    public void addOverByTdOff(boolean isT2G);

    public void updateSleepArea(String sql);

    /**
     * 更新执行表中结束时间
     */
    public void updateExeTime();


    public List<Map<String, Object>> queryMetaDataTest(String sql, String sTime, String eTime, String cellKey1, String cellKey2);
}
