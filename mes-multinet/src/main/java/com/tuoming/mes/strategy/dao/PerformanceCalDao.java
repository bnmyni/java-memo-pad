package com.tuoming.mes.strategy.dao;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.PerformanceCalSetting;

public interface PerformanceCalDao extends
        BaseDao<PerformanceCalSetting, Integer> {
    /**
     * 查询性能统计配置表
     *
     * @param groupName
     * @return List
     */
    List<PerformanceCalSetting> queryPerfmanceSetting(String groupName);

    /**
     * 删除上次统计的结果(表)
     *
     * @param table
     */
    void delOldResPerfTable(String table);

    /**
     * 创建性能统计结果表
     *
     * @param querySql 配置中的查询语句
     * @param table    结果表
     * @param time     时间
     * @param format   sql查询的时间格式
     */
    void createResPerfTable(String querySql, String table, String time);

}
