package com.tuoming.mes.strategy.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.BigDataForecastDao;
import com.tuoming.mes.strategy.model.BigDataForecastSetting;

@SuppressWarnings("unchecked")
@Repository("bigDataForecastDao")
public class BigDataForecastDaoImpl extends
AbstractBaseDao<BigDataForecastSetting, Integer> implements BigDataForecastDao{

	@Override
	public List<BigDataForecastSetting> queryForecastSet(String groupName) {
		String hql = "";
		if (StringUtils.isEmpty(groupName)) {
			hql += HQL_LIST_ALL + " where 1=1 and enabled=1";
		} else {
			hql += HQL_LIST_ALL + " where enabled=1 and groupName = '"
					+ groupName + "'";
		}
		Query query = this.getSession().createQuery(hql);
		return query.list();
	}
	
	@Override
	public void removeResTable(String tableName) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("truncate TABLE   ").append(tableName);
			this.getSession().createSQLQuery(sql.toString()).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("清空表时出现异常");
		}
	}

	@Override
	public List<Map<String, Object>> queryMetaData(String querySql) {
		return this.getSession().createSQLQuery(querySql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Override
	public Date queryUniqueData(String table,String column){
		String sql = "select MAX("+column+") from "+table;
		return (Date)this.getSession().createSQLQuery(sql).uniqueResult();
	}
	@Override
	public String queryDays(){
		String sql = "select value from mes_appsetting where name='bigdata_days'";
		return (String)this.getSession().createSQLQuery(sql).uniqueResult();
	}
}
