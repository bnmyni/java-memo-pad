package com.tuoming.mes.strategy.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.TdOffKpiDao;
import com.tuoming.mes.strategy.model.TdOffKpiModel;

@Repository("tdOffKpiDao")
public class TdOffKpiDaoImpl extends
        AbstractBaseDao<TdOffKpiModel, Integer> implements TdOffKpiDao {

    @Override
    public TdOffKpiModel querySleepAreaKpiSet(String groupName) {
        String hql = HQL_LIST_ALL + " where groupname = '"
                + groupName + "'";
        Query query = this.getSession().createQuery(hql);
        return (TdOffKpiModel) query.list().get(0);
    }
}
