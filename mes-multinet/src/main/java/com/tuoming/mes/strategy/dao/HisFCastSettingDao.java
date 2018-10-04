package com.tuoming.mes.strategy.dao;

import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.HisDataFCastSetting;

public interface HisFCastSettingDao extends BaseDao<HisDataFCastSetting, Integer> {

    /**
     * 根据分组名称查询预测条件
     *
     * @param groupName
     * @return
     */
    public List<HisDataFCastSetting> queryFCastConByGroup(String groupName);

    /**
     * 创建预测结果表
     *
     * @param string
     * @param string
     */
    public void createRstTable(String sourceTable, String rstTable);

    /**
     * 删除预测结果表
     *
     * @param string
     * @param string
     */
    public void removeRstTable(String tableName);

    /**
     * 查询满足条件的历史数据
     *
     * @param querySql
     * @return
     */
    public List<Map<String, Object>> queryMetaData(String querySql);


    public Map<String, String> getMultinetPeriod();

}
