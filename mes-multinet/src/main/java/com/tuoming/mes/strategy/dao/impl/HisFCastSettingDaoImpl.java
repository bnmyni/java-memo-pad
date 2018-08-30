package com.tuoming.mes.strategy.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.HisFCastSettingDao;
import com.tuoming.mes.strategy.model.HisDataFCastSetting;

@Repository("hisFCastSettingDao")
public class HisFCastSettingDaoImpl extends AbstractBaseDao<HisDataFCastSetting, Integer> implements HisFCastSettingDao{
	private final static Logger logger = LogFacade.getLog4j(HisFCastSettingDaoImpl.class);
	@Override
	public List<HisDataFCastSetting> queryFCastConByGroup(String groupName) {
		String hql = "";
		if (StringUtils.isEmpty(groupName)) {
			hql += HQL_LIST_ALL + " where 1=1 and enabled=1";
		} else {
			hql += HQL_LIST_ALL + " where enabled=1 and group = '" + groupName+"'";
		}
		Query query = this.getSession().createQuery(hql);
		return query.list();
	}


	@Override
	public void createRstTable(String sourceTable, String rstTable) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + rstTable + " AS select * from "
				+ sourceTable + " where 1=2";
		SQLQuery q = this.getSession().createSQLQuery(sql);
		q.executeUpdate();
	}

	@Override
	public void removeRstTable(String tablename) {
		// TODO Auto-generated method stub
		String sql = "truncate table " + tablename ;
		SQLQuery q = this.getSession().createSQLQuery(sql);
		q.executeUpdate();
	}


	@Override
	public List<Map<String, Object>> queryMetaData(String querySql) {
//		logger.info(querySql);
		SQLQuery query = this.getSession().createSQLQuery(querySql);
		query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		return query.list();
//		return new ArrayList();
	}
	


	@Override
	public Map<String, String> getMultinetPeriod() {
		String sql= "select start_period,begin_period from mes_zs_open_period";
		List<Map<String, String>> list = this.getSession().createSQLQuery(sql).setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
		.list();
		if(!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}


}
