package com.tuoming.mes.strategy.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.strategy.model.BigDataForecastSetting;

public interface BigDataForecastDao {
    /**
     * 查询下一时刻数据预测配置表
     *
     * @param groupName
     * @return
     */
    List<BigDataForecastSetting> queryForecastSet(String groupName);

    /**
     * 清空原表中数据
     *
     * @param tableName
     */
    void removeResTable(String tableName);

    List<Map<String, Object>> queryMetaData(String querySql);

    /**
     * 查询单条数据
     *
     * @param table
     * @return
     */
    Date queryUniqueData(String table, String column);

    /**
     * 查询预测数据天数
     *
     * @return
     */
    String queryDays();
}
