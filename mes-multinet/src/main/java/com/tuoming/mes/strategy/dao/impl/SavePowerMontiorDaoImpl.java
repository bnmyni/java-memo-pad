package com.tuoming.mes.strategy.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.SavePowerMontiorDao;
import com.tuoming.mes.strategy.model.SavePowerMontiorModel;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * 节能监控数据访问接口实现类
 * @author Administrator
 *
 */
@Repository("savePowerMontiorDao")
public class SavePowerMontiorDaoImpl extends AbstractBaseDao<SavePowerMontiorModel, Integer> implements SavePowerMontiorDao{


	@Override
	public List<SavePowerMontiorModel> querySetList(String groupName) {
		StringBuilder sql = new StringBuilder();
		sql.append(HQL_LIST_ALL).append(" WHERE enabled=1");
		if(!StringUtils.isEmpty(groupName)) {
			sql.append(" and group = '").append(groupName).append("'");
		}
		return this.getSession().createQuery(sql.toString()).list();
	}

	@Override
	public void updateBlack(Date collDate, String zs) {
		StringBuilder delSql = new StringBuilder();
		StringBuilder inSql = new StringBuilder();
		Date date15 = DateUtil.getRelateInterval(collDate, -15, Calendar.MINUTE);
		Date date30 = DateUtil.getRelateInterval(collDate, -30, Calendar.MINUTE);
		if(Constant.GSM.equalsIgnoreCase(zs)) {
			delSql.append("DELETE bla FROM mes_gsm_black AS bla WHERE EXISTS ( SELECT 1 FROM mes_gsm_nonsleep b WHERE b.starttime IN ( ?, ?, ? ) AND b.kpiweak = 1 AND b.lac = bla.lac AND b.ci = bla.ci GROUP BY b.lac, b.ci HAVING count(1) = 3 )");
			inSql.append("insert into mes_gsm_black(lac,ci,bsc,cellindex,reason) select lac,ci,bscid,cellid,? from mes_gsm_nonsleep where starttime in(?,?,?) and kpiweak=1 group by lac,ci having count(1)=3");
		}else if (Constant.LTE.equalsIgnoreCase(zs)) {
			delSql.append("delete bla from mes_lte_black as bla where exists (select enodebid,localcellid from mes_lte_nonsleep b where b.starttime in (?,?,?) and b.enodebid=bla.enodebid and b.localcellid=bla.localcellid and b.kpiweak=1 group by b.enodebid,b.localcellid having count(1)=3)");
			inSql.append("insert into mes_lte_black(enodebid,localcellid,reason) select enodebid,localcellid,? from mes_lte_nonsleep where starttime in(?,?,?) and kpiweak=1 group by enodebid,localcellid having count(1)=3");
		}else if (Constant.TD.equalsIgnoreCase(zs)) {
			delSql.append("delete bla from mes_td_black as bla where exists (select 1 from mes_td_nonsleep b where b.starttime in (?,?,?) and b.kpiweak=1 and bla.lac=b.lac and b.lcid=bla.lcid group by b.lac,b.lcid having count(1)=3)");
			inSql.append("insert into mes_td_black(rnc,lcid,lac,reason) select rnc,lcid,lac,? from mes_td_nonsleep where starttime in(?,?,?) and kpiweak=1 group by lac,lcid having count(1)=3");
		}
		this.getSession().createSQLQuery(delSql.toString()).setTimestamp(0, date15).setTimestamp(1, date30).setTimestamp(2, collDate).executeUpdate();
		this.getSession().createSQLQuery(inSql.toString()).setString(0, Constant.REASON_WEAK_ZB)
		.setTimestamp(1, date15).setTimestamp(2, date30).setTimestamp(3, collDate).executeUpdate();
		
	}

	public void addMotiorCellState(String exeSql, Date collDate) {
		this.getSession().createSQLQuery(exeSql).executeUpdate();
	}

	public void delHisData(String tableName) {
		String sql = "truncate table "+tableName;
		this.getSession().createSQLQuery(sql).executeUpdate();
	}

	
}
