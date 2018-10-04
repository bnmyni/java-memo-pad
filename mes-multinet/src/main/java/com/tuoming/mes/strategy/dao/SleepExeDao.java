package com.tuoming.mes.strategy.dao;

import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.strategy.model.SleepExeSetting;

/**
 * 休眠小区执行流程数据接口
 *
 * @author Administrator
 */
public interface SleepExeDao extends BaseDao<SleepExeSetting, Integer> {

    /**
     * 查询休眠小区执行流程配置
     *
     * @param groupName
     * @return
     */
    public List<SleepExeSetting> querySleepExeSetList(String groupName);

    /**
     * @param querySql
     * @param isAzimuth
     * @return
     */
    public List<Map<String, Object>> querySleepAreaBySql(String querySql,
                                                         boolean isAzimuth);


    /**
     * 查询同一网元下小区休眠小区个数
     *
     * @param zs
     * @return
     */
    public Map<String, Integer> queryCellAmount(String zs);

    /**
     * 查询每种制式下，每个单元小区休眠个数
     *
     * @return
     */
    public List<Map<String, Object>> querySleepDic();

    /**
     * 将已发送命令一直到历史表中
     *
     * @param appMultinet
     * @param groupName
     */
    public void insertHisCommand(String appMultinet, String groupName);

    /**
     * 删除已下发命令数据
     *
     * @param appMultinet
     * @param groupName
     */
    public void delCommand(String appMultinet, String groupName);


    /**
     * 更新黑名单
     *
     * @param data
     * @param reason
     */
    public void updateBlack(Map<String, Object> data, String reason);

    /**
     * 删除二次操作成功黑名单
     *
     * @param data
     * @param reason
     */
    public void delBlack(Map<String, Object> data);

    /**
     * 将为唤醒的小区假如告警名单
     *
     * @param data
     * @param reason
     */
    public void addAlarm(Map<String, Object> data, String reason);

    /**
     * 将休眠小区添加当前休眠小区表中
     *
     * @param data
     * @param command
     */
    public void addSleepArea(Map<String, Object> data, AdjustCommand command);

    /**
     * 将唤醒小区从当前休眠小区中移除
     *
     * @param data
     */
    public void delNofifyFromSleep(Map<String, Object> data);

    /**
     * 添加休眠或唤醒操作日志
     *
     * @param data
     * @param notify
     */
    public void addSleepOrNotifyLog(Map<String, Object> data, String operation);

    /**
     * 将多补一休眠小区添加当前多补一休眠小区表中
     *
     * @param data
     * @param command
     */
    public void addManySleepArea(Map<String, Object> data, AdjustCommand command);

    /**
     * 将多补一休眠小区添加当前多补一休眠小区表中
     *
     * @param data
     * @param command
     */
    public void addManySleepArea(Map<String, Object> data);

    /**
     * 将多补一休眠表中删除已经唤醒成功的小区
     *
     * @param data
     */
    public void delNofifyFromManySleep(Map<String, Object> data);

    /*************3G退网****************/
    /**
     * 将休眠小区添加当前休眠小区表中
     *
     * @param data
     * @param command
     * @param sleep_type 休眠方式：PERMANENCE_AREA：永久，STATIC_AREA:静态
     */
    public void addtdOffSleepArea(Map<String, Object> data, AdjustCommand command, String sleep_type);

    /**
     * 从3G休眠表中删除已经唤醒成功的小区
     *
     * @param data
     */
    public void delNofifyFromTdOffSleep(Map<String, Object> data);
}
