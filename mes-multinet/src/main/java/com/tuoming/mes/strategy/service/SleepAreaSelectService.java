package com.tuoming.mes.strategy.service;

import java.util.Map;

/**
 * 选择要休眠的小区和对应的补偿小区
 *
 * @author Administrator
 */
public interface SleepAreaSelectService {

    /**
     * 预测下一个时刻的数据
     *
     * @param context
     */
    public void foreCastNextData(Map<String, String> context);

    /**
     * 休眠小区筛选
     *
     * @param context
     */
    void sleepSelect(Map<String, String> context);

    /**
     * 休眠小区冲突
     *
     * @param context
     */
    public void conflictDeal(Map<String, String> context);

    /**
     * 休眠流程执行
     *
     * @param context
     */
    public void executeSleep(Map<String, String> context);

    /**
     * 下发休眠命令
     */
    public void dispatchSleepCommand();

    /**
     * 查询不同场景
     *
     * @return
     */
    public Map<String, String> queryScene();

    /**
     * 多补一休眠指令生成
     */
    public void executeManySleep();

}
