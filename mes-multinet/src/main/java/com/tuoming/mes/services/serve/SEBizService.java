package com.tuoming.mes.services.serve;

import java.util.Map;

/**
 * 策略模块服务接口定义
 *
 * @author Administrator
 */
public interface SEBizService {
    /**
     * 执行一次标准的主策略判断流程，这里是指触发一个15分钟周期内所有涉及操作，
     * 即同时触发休眠和唤醒判断流程。
     *
     * @param context
     */
    void alysAll(Map context);

    /**
     * 历史业务预测
     *
     * @param context
     */
    void hisDataFcast(Map context);

    /**
     * 刷新重叠覆盖度数据，同时根据计算结果刷新节能目标小区集合
     *
     * @param groupName
     */
    void refreshCoverRate(String groupName);

    /**
     * 刷新模型预测数据
     *
     * @param context
     */
    void refreshPredict(Map context);

    /**
     * 只执行小区休眠判断流程
     *
     * @param context
     */
    void executeSleepProcess(Map context);

    /**
     * 只执行小区唤醒流程
     *
     * @param context
     */
    void executeWakeupProcess(Map context);

    /**
     * 根据历史预测数据和上一时刻数据预测当前时刻数据
     *
     * @param context
     */
    void fcastNextData(Map context);

    /**
     * kpi计算
     *
     * @param groupName
     */
    void calKpi(String groupName);

    void calMinuteKpi(Map<String, String> context);

    /**
     * 采集数据清理
     *
     * @param groupName
     */
    void dataHandle(String groupName);

    /**
     * 唤醒所有休眠小区
     */
    void notifyAllSleep();

    /**
     * 构建节能系统需要的配置参数
     *
     * @return
     */
    Map<String, String> buildContext();

    /**
     * 性能统计外部访问接口
     *
     * @param context
     */
    void performanceCal(Map context);

    /**
     * 检查命令服务器是否可用
     */
    void checkCmdServerState();

    /**
     * 更新告警信息
     */
    void updateAlarmInfo();

    /**
     * 清除数天之前生成的服务器文件
     *
     * @param days
     */
    void cleanServerFileByDay(int days);

    /**
     * 重新采集失败的命令文件
     */
    void reCollectFailCommand(int times);

    /**
     * 是否采集mr数据
     *
     * @param type
     * @param days
     * @return
     */
    boolean sfCollectMr(String type, int days);

    /**
     * 删除mr数据
     */
    void cleanMrFile();

    /**
     * 是否就算重叠覆盖度
     *
     * @param type
     * @return
     */
    boolean sfCalOverDegree(String type);

    /**
     * 是否就算重叠覆盖度
     *
     * @param type
     * @return
     */
    void calOverDegreeDone(String type);


    void improveMrData(String groupName);

    void l2lhwMRParser(String dir, String regex);

    void l2lhwMRParser(String dir, String regex, String tableSuffix);

    void query(String paramString, long paramLong);

    /****************Neusoft*************************/
    /**
     * 3G智能退网功能计算模块
     */
    void tdNetworkOffCal(Map<String, String> context);

    /**
     * 3G智能退网执行模块
     */
    void tdNetworkOffExe();

    /**
     * 数据预测
     *
     * @param context
     */
    void bigDataForecast(Map<String, String> context);

    /**
     * R语言大数据建模
     */
    void rBigDataModel(Map<String, String> context);

    /**
     * R语言大数据预测
     */
    void rBigDataForecast(Map<String, String> context);

    void calKpiByTime(Map<String, String> context);

}
