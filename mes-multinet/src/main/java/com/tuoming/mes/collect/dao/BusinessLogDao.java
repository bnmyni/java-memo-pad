package com.tuoming.mes.collect.dao;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.models.BusinessLog;

public interface BusinessLogDao extends BaseDao<BusinessLog, Integer> {

    void insertLog(Integer module_type, String desc, Integer result);
}
