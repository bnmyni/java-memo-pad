package com.tuoming.mes.strategy.dao;

import java.util.Date;
import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.FcastNextIntervalSetting;

public interface FcastNextDataDao extends BaseDao<FcastNextIntervalSetting, Integer> {
    /**
     * 查询预测下一时刻数据的配置表
     *
     * @param groupName
     * @return
     */
    List<FcastNextIntervalSetting> queryForecastNextSet(String groupName);


    /**
     * 创建下一时刻结果表
     *
     * @param resultTable
     * @param createSql
     */
    void createResTable(String resultTable, String querySql, Date collectTime);


    /**
     * 删除上次的结果表
     *
     * @param tableName 表名
     */
    void removeResTable(String tableName);
}
