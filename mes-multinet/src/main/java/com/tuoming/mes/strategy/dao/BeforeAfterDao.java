package com.tuoming.mes.strategy.dao;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.BeforeAndAfterSetting;

public interface BeforeAfterDao extends BaseDao<BeforeAndAfterSetting, Integer> {
    /**
     * 根据组名和执行类型查询配置
     *
     * @param groupName
     * @param exeType
     * @return
     */
    public List<BeforeAndAfterSetting> querySetting(String groupName);

    /**
     * 清楚cm和pm数据表中数据
     *
     * @param table
     * @param exetype
     */
    public void updateSql(String sql);

    /**
     * 将载频级的小区表汇总成小区级别的表
     *
     * @param sql
     * @param table
     */
    public void createTableForCarrier(BeforeAndAfterSetting set);

    /**
     * 删除结果表
     *
     * @param table
     */
    public void deleteResultTable(String table);

    /**
     * 清除每个PM表中六个月前的数据
     *
     * @param tableName
     * @param timeColumn
     * @param time
     */
    public void deleteTableForPm(String exeSql, String tableName, String timeColumn, String time);

    /**
     * 清除每个CM表中的数据
     *
     * @param tableName
     * @param timeColumn
     */
    public void deleteTableForCm(String exeSql, String tableName);

    /**
     * 将cm的bak表数据复制到源表
     *
     * @param table
     */
    public void updateData(String table);

    /**
     * 查询表中数据量
     *
     * @param table
     * @return
     */
    public int queryDataCount(String table);

    /**
     * 汇总carrier到数据为小区级别
     *
     * @param set
     */
    public void insertDataForCarrier(BeforeAndAfterSetting set);

    /**
     * 将pm数据导入历史表中，并情况当前数据表
     *
     * @param string
     */
    public void removePmData(String table);

    /**
     * 清空表数据
     *
     * @param bakTabelName
     */
    public void removeData(String bakTabelName);

}
