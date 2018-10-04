package com.tuoming.mes.strategy.dao;

import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.KpiCalModel;

/**
 * 关键性指标计算数据访问接口
 *
 * @author Administrator
 */
public interface KpiCalDao extends BaseDao<KpiCalModel, Integer> {

    /**
     * 通过组名查询kpi配置列表
     *
     * @param groupName
     * @return
     */
    List<KpiCalModel> querySetList(String groupName);

    /**
     * 根据配置sql查询计算kpi需要的元数据
     *
     * @param querySql
     * @return
     */
    List<Map<String, Object>> quertDataBySql(String querySql, String starttime);

    /**
     * 清除历史数据
     *
     * @param tableName
     */
    void delHisData(String tableName);

    /**
     * 将数据保存于历史表
     *
     * @param key
     */
    void insertHisData(String key);

    /**
     * 插入中间表数据
     */
    void insertGsmKpi(final List<Map<String, Object>> data);

    void insertL2lKpi(final List<Map<String, Object>> data);

    void insertT2GKpi(final List<Map<String, Object>> data);

    void insertL2TKpi(final List<Map<String, Object>> data);

    /****************Neusoft********************/
    void insertT2LKpi(final List<Map<String, Object>> data);

    void insertT2TKpi(final List<Map<String, Object>> data);
}
