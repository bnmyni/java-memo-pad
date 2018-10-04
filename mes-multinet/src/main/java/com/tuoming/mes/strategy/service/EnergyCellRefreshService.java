package com.tuoming.mes.strategy.service;

import java.util.Map;

/**
 * 刷新小区列表业务接口
 *
 * @author Administrator
 */
public interface EnergyCellRefreshService {

    /**
     * 节能小区刷新
     *
     * @param groupName 配置表中的组名
     * @param mrAzi     mr或者azimuth标识
     */
    public void refreshEnergyCell(Map<String, String> context);

    /**
     * 更新告警信息
     */
    public void updateAlarmInfo();

    /**
     * 完善MR映射关系
     */
    public void improveMrData(String groupName);
}
