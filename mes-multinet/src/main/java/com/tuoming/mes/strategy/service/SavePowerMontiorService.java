package com.tuoming.mes.strategy.service;

import java.util.Map;

/**
 * 节能监控业务接口
 *
 * @author Administrator
 */
public interface SavePowerMontiorService {

    /**
     * 各网络小区监控主流程
     */
    public void mainMontior(Map<String, String> context);

    /**
     * 执行唤醒
     */
    public void executeNotify();

    /**
     * 唤醒所有小区
     */
    public void notifyAllArea();

}
