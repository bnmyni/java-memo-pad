package com.tuoming.mes.strategy.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.EricConfDao;
import com.tuoming.mes.strategy.model.BscNameConf;

@SuppressWarnings("unchecked")
@Repository("ericConfDao")
public class EricConfDaoImpl extends AbstractBaseDao<BscNameConf, Integer>
		implements EricConfDao {

	@Override
	public List<BscNameConf> queryEricBsc() {
		Query query = this.getSession().createQuery(HQL_LIST_ALL);
		return query.list();
	}

}
