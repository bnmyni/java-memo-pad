package com.tuoming.mes.strategy.service;

import java.util.Map;

/**
 * 关键性指标计算接口
 *
 * @author Administrator
 */
public interface KpiCalService {

    public void calKpi(String groupName, String startTime);

    public void performanceCal(Map<String, String> context);
}
