package com.tuoming.mes.strategy.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.TdLstTcellHWDao;
import com.tuoming.mes.strategy.model.TdLstTcellHW;

@SuppressWarnings("unchecked")
@Repository("tdLstTcellHWDao")
public class TdLstTcellHWDaoImpl extends AbstractBaseDao<TdLstTcellHW, Integer>
        implements TdLstTcellHWDao {

    @Override
    public List<TdLstTcellHW> queryList() {
        Query query = this.getSession().createQuery(HQL_LIST_ALL);
        return query.list();
    }


}
