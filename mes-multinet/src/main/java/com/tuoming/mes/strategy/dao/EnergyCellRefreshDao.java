package com.tuoming.mes.strategy.dao;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.EnergyCellRefreshSetting;

public interface EnergyCellRefreshDao extends
        BaseDao<EnergyCellRefreshSetting, Integer> {

    /**
     * 查询刷新小区配置表中的配置
     *
     * @param groupName 组名
     * @return List<EnergyCellRefreshSetting>
     */
    public List<EnergyCellRefreshSetting> queryRefCellSetting(String busyType, String groupName);

    /**
     * 创建临时表
     */
    public String createTempTable(String sql);

    /**
     * 删除表
     *
     * @param tableName 要删除的表名
     */
    public void removeTable(String tableName);

    /**
     * Azimuth
     * 根据方位角 创建方位角刷新结果表
     *
     * @param tableName 要创建的结果表名
     * @param tempName  临时表名
     */
    public void createRstAzimuthTable(String tableName, String tempName);

    /**
     * MR
     * 根据mr重叠覆盖度 创建真实的GtoG结果表
     */
    public void createRstGtoGMrTable(String tableName);

    /**
     * MR
     * 根据mr重叠覆盖度 创建真实的TtoG结果表
     */
    public void createRstTtoGMrTable(String tableName);

    /**
     * MR
     * 根据mr重叠覆盖度 创建真实的LtoL结果表
     */
    public void createRstLtoLMrTable(String tableName);

    /**
     * 按MR
     * 按lte对lte 将临时表的数据添加进入结果表
     *
     * @param wlbm 临时数据表名
     */
    public void insertToRealTableForL2L(String real, String wlbm);

    /**
     * 按MR
     * 按GSM对GSM 将临时表的数据添加进入结果表
     *
     * @param wlbm 临时数据表名
     */
    public void insertToRealTableForG2G(String real, String wlbm);

    /**
     * 按MR
     * 按TD对GSM 将临时表的数据添加进入结果表
     *
     * @param wlbm      临时数据表名
     * @param factoryid 厂家标识
     */
    public void insertToRealTableForT2G(String real, String wlbm);

    /**
     * 删除源小区在gsm制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteSrcGsmBwa(String tempName);

    /**
     * 删除邻区在gsm制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteLinGsmBwa(String tempName);

    /**
     * 删除源小区在td制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteSrcTdBwa(String tempName);

    /**
     * 删除源小区在td制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteSrcTdOffBwa(String tempName);

    /**
     * 删除邻区在td制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteLinTdBwa(String tempName);

    /**
     * 删除源小区在Ltegsm制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteSrcLteBwa(String tempName);

    /**
     * 删除邻区在Lte制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteLinLteBwa(String tempName);

    /**
     * 根据临时表创建结果表
     *
     * @param rsTable
     * @param lsb
     */
    public void createResTable(String rsTable, String lsb);

    /**
     * 把临时表的数据导入结果表
     */
    public void addData(String rsTable, String lsb);

    /**
     * 从节能列表中删除G2G场景当前休眠的小区，以及作为补偿的小区
     *
     * @param wlbm
     */
    public void deleteG2gCellInBySleep(String wlbm);

    /**
     * 从节能列表中删除L2L场景当前休眠的小区，以及作为补偿的小区
     *
     * @param wlbm
     */
    public void deleteL2lCellInBySleep(String wlbm);

    /**
     * 从节能列表中删除T2G场景当前休眠的小区，以及作为补偿的小区
     *
     * @param wlbm
     */
    public void deleteT2gCellInBySleep(String wlbm);

    /**
     * 从节能列表中删除L2T场景当前休眠的小区，以及作为补偿的小区
     *
     * @param wlbm
     */
    public void deleteL2tCellInBySleep(String wlbm);

    /**
     * Neusoft
     * 从节能小区表中删除T2L场景当前休眠小区，以作为补偿小区
     */
    public void deleteT2lCellInBySleep(String wlbm);

    /**
     * 删除邻区在Lte制式下黑白名单的对应主小区，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteManyNcLteBwa(String tempName);

    /**
     * 从节能列表中删除L2L场景当前休眠的小区，以及作为多补一补偿的小区
     *
     * @param wlbm
     */
    public void deleteL2lManyCellInBySleep(String wlbm);

    /**
     * 删除源小区为空的无效数据
     *
     * @param tempName
     */
    public void deleteTDisNullBwa(String tempName);

    /**
     * 删除源小区在td多补一制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    public void deleteSrcTDManyBwa(String tempName);

    /**
     * 从节能列表中删除T2T多补一场景当前休眠的小区，以及作为补偿的小区
     *
     * @param wlbm
     */
    public void deletet2tManyCellInBySleep(String wlbm);

}
