package com.tuoming.mes.collect.dao;

import com.tuoming.mes.collect.models.BusinessLog;
import com.tuoming.mes.collect.dpp.dao.BaseDao;

public interface BusinessLogDao extends BaseDao<BusinessLog, Integer>{
	//public void insertLog(Map<String,Object> data);
	public void insertLog(Integer module_type,String desc,Integer result);
}
