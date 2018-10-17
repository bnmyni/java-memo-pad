package com.tuoming.mes.collect.dao.impl;

import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.models.BusinessLog;
import com.tuoming.mes.services.ftp.AbstractBaseDao;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * 业务日志
 * mes_business_log_info
 */
@Repository("businessLogDao")
public class BusinessLogDaoImpl extends AbstractBaseDao<BusinessLog, Integer> implements BusinessLogDao {
    public void insertLog(Integer module_type, String desc, Integer result) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into mes_business_log_info(timestamp,module_type,content,result)")
                .append(" values (?,?,?,?)");
        this.getSession().createSQLQuery(sql.toString())
                .setString(0, DateUtil.getCurrentDate())
                .setInteger(1, module_type)
                .setString(2, desc)
                .setInteger(3, result)
                .executeUpdate();
    }
}
