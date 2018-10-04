package com.tuoming.mes.strategy.dao;

import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;

/**
 * 重叠覆盖度数据库操作类
 *
 * @author Administrator
 */
public interface OverlayDegreeDao extends BaseDao<OverlayDegreeSetting, Integer> {
    /**
     * 根据分组查询计算重叠覆盖度的配置条件
     *
     * @param groupName
     * @return
     */
    public List<OverlayDegreeSetting> queryCalConByGroup(String groupName);

    /**
     * 查询要计算的元数据
     *
     * @return
     */
    public List<Map<String, Object>> queryMetaData(String wlbm);

    /**
     * 将要分析的数据放入临时表中
     *
     * @param lsbm
     * @param querySql
     */
    public void createLsb(String lsbm, String querySql);

    /**
     * 获取要查询的数据量
     *
     * @param lsbm
     * @return
     */
    public int getTotalCount(String lsbm);

    /**
     * 分页查询要计算的元数据
     *
     * @param lsbm
     * @param startIndex
     * @param endIndex
     * @return
     */
    public List<Map<String, Object>> queryMetaData(String lsbm, int startIndex,
                                                   int num);

    /**
     * 删除指定表名的物理表
     *
     * @param lsbm
     */
    public void removeTable(String lsbm);

    /**
     * 创建结果表
     *
     * @param createSql
     */
    public void createRstTable(String createSql);

    /**
     * 根据场景配置表更新重叠覆盖度计算开关
     */
    public void updateDegreeSetting();

    /**
     * 清空表中结果
     */
    public void removeAllTable(String tab);

    /**
     * 华为Lte所有邻区可被补偿的MRO采集点汇总去重后的采集点数量
     */
    public int queryTrueLteCellCoint(String querySql);

    /**
     * 查询一对一计算重叠覆盖度的配置条件
     *
     * @return
     */
    public List<OverlayDegreeSetting> queryCalConBySingle();

    /**
     * 查询多对一计算重叠覆盖度的配置条件
     *
     * @param groupName
     * @return
     */
    public List<OverlayDegreeSetting> queryCalConByMany();

    /**
     * 华为Td所有邻区可被补偿的MRO采集点汇总去重后的采集点数量
     */
    public int queryTrueTdCellCoint(String querySql);
}
