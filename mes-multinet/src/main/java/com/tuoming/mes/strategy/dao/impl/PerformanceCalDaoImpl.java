package com.tuoming.mes.strategy.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.PerformanceCalDao;
import com.tuoming.mes.strategy.model.PerformanceCalSetting;

@Component("performanceCalDao")
public class PerformanceCalDaoImpl extends
		AbstractBaseDao<PerformanceCalSetting, Integer> implements
		PerformanceCalDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<PerformanceCalSetting> queryPerfmanceSetting(String groupName) {
		// TODO Auto-generated method stub
		String hql = "";
		if (StringUtils.isEmpty(groupName)) {
			hql += HQL_LIST_ALL + " where 1=1 and enabled=1";
		} else {
			hql += HQL_LIST_ALL + " where enabled=1 and groupName = '"
					+ groupName + "'";
		}
		return this.getSession().createQuery(hql).list();
	}

	@Override
	public void delOldResPerfTable(String table) {
		// TODO Auto-generated method stub
		String sql = "truncate TABLE " + table;
		this.getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void createResPerfTable(String querySql, String table, String time) {
		try {
			String newSql = querySql.replace("$TIMESFORMAT$", time);
			String sql = "insert into  " + table + "   " + newSql;
			this.getSession().createSQLQuery(sql).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
