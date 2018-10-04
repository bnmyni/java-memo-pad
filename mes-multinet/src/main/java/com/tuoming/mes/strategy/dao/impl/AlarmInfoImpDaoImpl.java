package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.AlarmInfoImpDao;
import com.tuoming.mes.strategy.model.AlarmInfoModel;

@Repository("alarmInfoImpDao")
public class AlarmInfoImpDaoImpl extends AbstractBaseDao<AlarmInfoModel, Integer> implements AlarmInfoImpDao {

    public List<AlarmInfoModel> queryAlarmSet(String groupName) {
        String hql = "";
        if (StringUtils.isEmpty(groupName)) {
            hql += HQL_LIST_ALL + " where 1=1 and enabled=1";
            Query query = this.getSession().createQuery(hql);
            return query.list();
        } else {
            hql += HQL_LIST_ALL + " where enabled=1 and groupName =?";
            Query query = this.getSession().createQuery(hql).setString(0, groupName);
            return query.list();
        }
    }

    @Override
    public void removeData(String resTable) {
        String sql = "truncate table " + resTable;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void updateAlarmInfo(String exeSql) {
        this.getSession().createSQLQuery(exeSql).executeUpdate();
    }

}
