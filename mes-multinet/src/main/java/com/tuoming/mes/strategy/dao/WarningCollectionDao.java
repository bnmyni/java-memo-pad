package com.tuoming.mes.strategy.dao;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.SleepExeSetting;

import java.util.Map;


/**
 * Created by geyu on 2016/9/7.
 */
public interface WarningCollectionDao extends BaseDao<SleepExeSetting, Integer> {

    /**
     * 添加告警信息
     * @param data
     * @param reason
     */
    public void insertAlarm(Map<String, Object> data, String reason);

    /**
     * 添加指标恶化信息
     * @param data
     * @param type
     */
    public void insertDeteriorate(Map<String, Object> data, String type);

}
