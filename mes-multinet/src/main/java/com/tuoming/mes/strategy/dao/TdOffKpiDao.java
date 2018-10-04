package com.tuoming.mes.strategy.dao;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.TdOffKpiModel;

public interface TdOffKpiDao extends BaseDao<TdOffKpiModel, Integer> {
    public TdOffKpiModel querySleepAreaKpiSet(String groupName);
}
