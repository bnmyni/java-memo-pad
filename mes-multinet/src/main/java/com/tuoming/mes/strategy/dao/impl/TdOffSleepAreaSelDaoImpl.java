package com.tuoming.mes.strategy.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.TdOffSleepAreaSelDao;
import com.tuoming.mes.strategy.model.TdOffSleepSelectModel;
import com.tuoming.mes.strategy.util.DateUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

@Repository("tdOffSleepAreaSelDao")
public class TdOffSleepAreaSelDaoImpl extends
		AbstractBaseDao<TdOffSleepSelectModel, Integer> implements
		TdOffSleepAreaSelDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TdOffSleepSelectModel> querySleepAreaSelSet(String groupName,
			int cal_type) {
		// TODO Auto-generated method stub
		String hql = "";
		if (StringUtils.isEmpty(groupName)) {
			hql += HQL_LIST_ALL + " where enabled=1 and cal_type=" + cal_type;
		} else {
			hql += HQL_LIST_ALL + " where enabled=1 and cal_type=" + cal_type
					+ " and groupname = '" + groupName + "'";
		}
		Query query = this.getSession().createQuery(hql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeAllData(String resTable) {
		String sql = "truncate table " + resTable;
		this.getSession().createSQLQuery(sql).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryMetaData(String querySql) {
		return this.getSession().createSQLQuery(querySql)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		// SQLQuery query = this.getSession().createSQLQuery(querySql);
		// query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		// return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TdOffSleepSelectModel> queryTdOffSetGroup(String busytype) {
		String hql = "SELECT db_name,export_cols from mes_td_off_setting where groupname= '"
				+ busytype + "' group by db_name,export_cols";
		Query query = this.getSession().createQuery(hql);
		return query.list();
	}

	@Override
	public String queryDataNum() {
		String sql = "select value from mes_appsetting where name='td_off_data_num'";
		return (String) this.getSession().createSQLQuery(sql).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int queryDataCount(boolean isT2G) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(1) num from ").append(
				isT2G ? "rst_td_gsm_azimuth" : "rst_td_lte_azimuth");
		List<Map<String, Object>> list = this.getSession()
				.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (list == null)
			return 0;
		return (int) FormatUtil.tranferCalValue(list.get(0).get("num"));
	}

	@Override
	public void updateCalculate() {
		String nowTime = DateUtil.format(new Date());
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE mes_td_calculate_table set endtime='")
				.append(nowTime)
				.append("' WHERE id=(select a.id from (select MAX(id) id from mes_td_calculate_table) a)");
		this.getSession().createSQLQuery(sql.toString()).executeUpdate();
	}

	public void updateCalStatus() {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE mes_td_calculate_table set finish=1 ")
				.append(" WHERE id=(select a.id from (select MAX(id) id from mes_td_calculate_table) a)");
		this.getSession().createSQLQuery(sql.toString()).executeUpdate();
	}

	@Override
	public boolean calFinish() {
		String sql = "select finish,endtime from mes_td_calculate_table where id=(select MAX(id) from mes_td_calculate_table)";
		List<Map<String, Object>> list = this.getSession()
				.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (null != list && list.size() > 0) {
			if (null != list.get(0).get("endtime")
					&& !"".equals(list.get(0).get("endtime"))
					&& "1".equals(list.get(0).get("finish").toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeOverByTdOff(boolean isT2G) {
		String over_table = isT2G ? "rst_td_gsm_azimuth" : "rst_td_lte_azimuth";
		String sleep_table = isT2G ? Constant.T2G : Constant.T2L;
		String dest_key = isT2G ? "dest_lac,dest_ci"
				: "dest_enodebid,dest_localcellid";
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE a.* from ")
				.append(over_table)
				.append(" a,(select src_lac, src_ci,")
				.append(dest_key)
				.append(" from ")
				.append("mes_")
				.append(sleep_table)
				.append("_static_sleep_azimuth ")
				.append(" UNION ALL select src_lac, src_ci,")
				.append(dest_key)
				.append(" from mes_")
				.append(sleep_table)
				.append("_permanence_sleep_azimuth ) b")
				.append(" where a.src_lac = b.src_lac and a.src_ci = b.src_ci and ")
				.append(isT2G ? "a.dest_lac=b.dest_lac and a.dest_ci = b.dest_ci"
						: "a.dest_enodebid = b.dest_enodebid and a.dest_localcellid = b.dest_localcellid");
		this.getSession().createSQLQuery(sql.toString()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int updateAndQueryExecuteStatus() {
		int result = 0;// 不休眠也不唤醒
		String sql = "select * from mes_td_execute_table where id=(select MAX(id) from mes_td_execute_table) and endtime is null ";
		String maxid = null;
		List<Map<String, Object>> list = this.getSession()
				.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (null != list && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			maxid = map.get("id").toString();
			if ("0".equals(map.get("isExecute").toString())) {
				result = 2;// 执行唤醒
			} else if ("1".equals(map.get("isExecute").toString())) {
				result = 1;// 执行休眠
			}
		}

		return result;
	}

	@Override
	public void removeSleepByTdOff(boolean isT2G) {
		StringBuilder sql = new StringBuilder();
		String sleep_tab = isT2G ? "mes_t2g_sleep_azimuth"
				: "mes_t2l_sleep_azimuth";
		String td_table = isT2G ? Constant.T2G : Constant.T2L;
		String dest_key = isT2G ? "dest_lac,dest_ci"
				: "dest_enodebid,dest_localcellid";
		sql.append("delete a.* from ")
				.append(sleep_tab)
				.append(" a,(select src_lac,src_ci,")
				.append(dest_key)
				.append(" from ")
				.append("mes_")
				.append(td_table)
				.append("_static_sleep_azimuth ")
				.append(" union all select src_lac,src_ci,")
				.append(dest_key)
				.append(" from mes_")
				.append(td_table)
				.append("_permanence_sleep_azimuth ) b")
				.append(" where a.src_lac = b.src_lac and a.src_lcid = b.src_ci and ")
				.append(isT2G ? "a.dest_lac=b.dest_lac and a.dest_ci = b.dest_ci"
						: "a.dest_enodebid = b.dest_enodebid and a.dest_localcellid = b.dest_localcellid");
		this.getSession().createSQLQuery(sql.toString()).executeUpdate();
	}

	/**
	 * 将在动态休眠成功的数据添加到永久、静态休眠成功表中 删除动态休眠成功表中上述数据
	 */
	@Override
	public void addTdOffFromDynamicSleep(boolean isT2G) {
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder sql3 = new StringBuilder();
		StringBuilder sql4 = new StringBuilder();
		if (isT2G) {
			sql1.append("insert into mes_t2g_static_currentsleep")
					.append("(select b.* from mes_t2g_currentsleep a,")
					.append("mes_t2g_static_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_lac=b.dest_lac and a.dest_ci=b.dest_ci)");
			sql2.append("insert into mes_t2g_permanence_currentsleep")
					.append("(select b.* from mes_t2g_currentsleep a,")
					.append("mes_t2g_permanence_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_lac=b.dest_lac and a.dest_ci=b.dest_ci)");
			sql3.append("delete a.* from mes_t2g_currentsleep a,")
					.append("(select a.src_lac,a.src_lcid,a.dest_lac,a.dest_ci from mes_t2g_currentsleep a,mes_t2g_static_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_lac=b.dest_lac and a.dest_ci=b.dest_ci) c")
					.append(" where a.src_lac=c.src_lac and a.src_lcid=c.src_lcid")
					.append(" and a.dest_lac=c.dest_lac and a.dest_ci=c.dest_ci");
			sql4.append("delete a.* from mes_t2g_currentsleep a,")
					.append("(select a.src_lac,a.src_lcid,a.dest_lac,a.dest_ci from mes_t2g_currentsleep a,mes_t2g_permanence_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_lac=b.dest_lac and a.dest_ci=b.dest_ci) c")
					.append(" where a.src_lac=c.src_lac and a.src_lcid=c.src_lcid")
					.append(" and a.dest_lac=c.dest_lac and a.dest_ci=c.dest_ci");
			int res1 = this.getSession().createSQLQuery(sql1.toString())
					.executeUpdate();
			int res2 = this.getSession().createSQLQuery(sql2.toString())
					.executeUpdate();
			if (res1 > 0) {
				this.getSession().createSQLQuery(sql3.toString())
						.executeUpdate();
			}
			if (res2 > 0) {
				this.getSession().createSQLQuery(sql4.toString())
						.executeUpdate();
			}
		} else {
			sql1.append("insert into mes_t2l_static_currentsleep")
					.append("(select b.* from mes_t2l_currentsleep a,")
					.append("mes_t2l_static_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_enodebid=b.dest_enodebid and a.dest_localcellid=b.dest_localcellid)");
			sql2.append("insert into mes_t2l_permanence_currentsleep")
					.append("(select b.* from mes_t2l_currentsleep a,")
					.append("mes_t2l_permanence_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_enodebid=b.dest_enodebid and a.dest_localcellid=b.dest_localcellid)");
			sql3.append("delete a.* from mes_t2l_currentsleep a,")
					.append("(select a.src_lac,a.src_lcid,a.dest_enodebid,a.dest_localcellid from mes_t2l_currentsleep a,mes_t2l_static_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_enodebid=b.dest_enodebid and a.dest_localcellid=b.dest_localcellid) c")
					.append(" where a.src_lac=c.src_lac and a.src_lcid=c.src_lcid")
					.append(" and a.dest_enodebid=c.dest_enodebid and a.dest_localcellid=c.dest_localcellid");
			sql4.append("delete a.* from mes_t2l_currentsleep a,")
					.append("(select a.src_lac,a.src_lcid,a.dest_enodebid,a.dest_localcellid from mes_t2l_currentsleep a,mes_t2l_permanence_currentsleep b ")
					.append(" where a.src_lac=b.src_lac and a.src_lcid=b.src_ci")
					.append(" and a.dest_enodebid=b.dest_enodebid and a.dest_localcellid=b.dest_localcellid) c")
					.append(" where a.src_lac=c.src_lac and a.src_lcid=c.src_lcid")
					.append(" and a.dest_enodebid=c.dest_enodebid and a.dest_localcellid=c.dest_localcellid");
			int res1 = this.getSession().createSQLQuery(sql1.toString())
					.executeUpdate();
			int res2 = this.getSession().createSQLQuery(sql2.toString())
					.executeUpdate();
			if (res1 > 0) {
				this.getSession().createSQLQuery(sql3.toString())
						.executeUpdate();
			}
			if (res2 > 0) {
				this.getSession().createSQLQuery(sql4.toString())
						.executeUpdate();
			}
		}
	}

	@Override
	public void addOverByTdOff(boolean isT2G) {
		StringBuilder sql = new StringBuilder();
		if (isT2G) {
			sql.append("insert into ")
					.append("rst_td_gsm_azimuth")
					.append(" select * from (select src_rnc,src_cellid,src_longitude,src_latitude,src_ci,src_lac,src_azimuth,src_vender,")
					.append("dest_bsc,dest_longitude,dest_latitude,dest_lac,dest_ci,dest_azimuth,dest_vender,")
					.append(" rst_azimuth,rst_instance ")
					.append(" from mes_t2g_permanence_sleep_azimuth")
					.append(" UNION ALL")
					.append(" select src_rnc,src_cellid,src_longitude,src_latitude,src_ci,src_lac,src_azimuth,src_vender,")
					.append("dest_bsc,dest_longitude,dest_latitude,dest_lac,dest_ci,dest_azimuth,dest_vender,")
					.append(" rst_azimuth,rst_instance ")
					.append(" from mes_t2g_static_sleep_azimuth) lsb");
		} else {
			sql.append("insert into ")
					.append("rst_td_lte_azimuth")
					.append(" select * from (select src_rnc,src_cellid,src_azimuth,src_longitude,src_latitude,src_vender,src_ci,src_lac,")
					.append("dest_enodebid,dest_localcellid,dest_azimuth,dest_longitude,dest_latitude,dest_vender,")
					.append(" rst_azimuth,rst_instance ")
					.append(" from mes_t2l_permanence_sleep_azimuth")
					.append(" UNION ALL")
					.append(" select src_rnc,src_cellid,src_azimuth,src_longitude,src_latitude,src_vender,src_ci,src_lac,")
					.append("dest_enodebid,dest_localcellid,dest_azimuth,dest_longitude,dest_latitude,dest_vender,")
					.append(" rst_azimuth,rst_instance ")
					.append(" from mes_t2l_static_sleep_azimuth) lsb");
		}
		this.getSession().createSQLQuery(sql.toString()).executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryTdDic() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from mes_td_off_td_switch_threshold");
		return this.getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryGsmDic() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from mes_td_off_gsm_switch_threshold");
		return this.getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryLteDic() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from mes_td_off_lte_switch_threshold");
		return this.getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public void updateSleepArea(String sql) {
		this.getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void updateExeTime() {
		String nowTime = DateUtil.format(new Date());
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE mes_td_execute_table set endtime='")
				.append(nowTime)
				.append("' WHERE id=(select a.id from (select MAX(id) id from mes_td_execute_table) a)");
		this.getSession().createSQLQuery(sql.toString()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryMetaDataTest(String querySql,
			String sTime, String eTime, String cellKey1, String cellKey2) {
		Session session = this.getSession();
		List<Map<String, Object>> list = session.createSQLQuery(querySql)
				.setCacheable(false).setString(2, sTime).setString(3, eTime)
				.setInteger(0, Integer.valueOf(cellKey2))
				.setString(1, cellKey1)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		session.flush();
		session.clear();
		return list;

		// SQLQuery query = this.getSession().createSQLQuery(querySql);
		// query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		// return query.list();
	}
}
