package com.tuoming.mes.strategy.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.execute.dao.AdjustCommandDao;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.model.SleepExeSetting;
import com.tuoming.mes.strategy.util.DateUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 休眠小区执行流程数据访问接口实现类
 * 
 * @author Administrator
 *
 */
@Repository("sleepExeDao")
public class SleepExeDaoImpl extends AbstractBaseDao<SleepExeSetting, Integer>
		implements SleepExeDao {
	@Autowired
	@Qualifier("AdjustCommandDao")
	private AdjustCommandDao commandDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<SleepExeSetting> querySleepExeSetList(String groupName) {
		String hql = "";
		if (StringUtils.isEmpty(groupName)) {
			hql += HQL_LIST_ALL + " where 1=1 and enabled=1 and group in('l2l','g2g','td')";
		} else {
			hql += HQL_LIST_ALL + " where enabled=1 and group = '" + groupName
					+ "'";
		}
		return this.getSession().createQuery(hql).list();
	}

	@Override
	public List<Map<String, Object>> querySleepAreaBySql(String querySql, boolean isAziumuth) {
		String sql = querySql.replaceAll("\\$TABLESUFFIX\\$", isAziumuth?Constant.AZIMUTH:Constant.MR)
				.replace("$ORDER$", isAziumuth?"azimuth desc":"overdegree asc");
		return this.getSession().createSQLQuery(sql)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Override
	public Map<String, Integer> queryCellAmount(String zs) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) num,");
		if (Constant.GSM.equalsIgnoreCase(zs)) {
			sql.append("src_bscid unitbs from mes_g2g_currentsleep  group by  src_bscid");
		} else if (Constant.TD.equalsIgnoreCase(zs)) {
			sql.append("src_rnc unitbs from (select src_rnc from mes_t2g_currentsleep union all select src_rnc from mes_t2l_currentsleep union all select src_rnc from mes_t2t_many_currentsleep) a group by src_rnc");
		} else if (Constant.LTE.equalsIgnoreCase(zs)) {
			sql.append("omm unitbs from (select omm from mes_l2l_currentsleep union all select omm from mes_l2l_many_currentsleep) a group by omm");
		}
		List<Map<String, Object>> list = this.getSession()
				.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		Map<String, Integer> res = new HashMap<String, Integer>();
		for (Map<String, Object> map : list) {
			res.put(String.valueOf(map.get("unitbs")),
					(int) FormatUtil.tranferCalValue(map.get("num")));
		}
		return res;
	}

	@Override
	public List<Map<String, Object>> querySleepDic() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM mes_sleep_dormancy_threshold");
		return this.getSession().createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public void insertHisCommand(String appMultinet, String groupName) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into mes_adjust_command_his select * from mes_adjust_command where group_name=? and app_name=?");
		this.getSession().createSQLQuery(sql.toString())
				.setString(0, groupName).setString(1, appMultinet)
				.executeUpdate();
	}

	@Override
	public void delCommand(String appMultinet, String groupName) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from mes_adjust_command where group_name=? and app_name=?");
		this.getSession().createSQLQuery(sql.toString())
				.setString(0, groupName).setString(1, appMultinet)
				.executeUpdate();
	}

	@Override
	public void updateBlack(Map<String, Object> data, String reason) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder delSql = new StringBuilder();
		StringBuilder insSql = new StringBuilder();
		/************Neusoft***************/
		//if(Constant.T2G.equals(bus_type)){
		if(Constant.T2G.equals(bus_type) || Constant.T2L.equals(bus_type)) {
			delSql.append("delete from mes_td_black where lac=? and lcid = ?");
			insSql.append("insert into mes_td_black (rnc,lcid,lac,reason) values(?,?,?,?)");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_lac"))
			.setInteger(1, (Integer)data.get("src_lcid")).executeUpdate();
			this.getSession().createSQLQuery(insSql.toString()).setString(0, (String)data.get("src_rnc"))
			.setInteger(1, (Integer)data.get("src_lcid"))
			.setInteger(2, (Integer)data.get("src_lac"))
			.setString(3, reason).executeUpdate();
		}else if(Constant.G2G.equals(bus_type)) {
			delSql.append("delete from mes_gsm_black where lac=? and ci = ?");
			insSql.append("insert into mes_gsm_black (bsc,cellindex,lac,ci,reason) values(?,?,?,?,?)");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_lac"))
			.setInteger(1, (Integer)data.get("src_ci")).executeUpdate();
			this.getSession().createSQLQuery(insSql.toString())
			.setString(0, String.valueOf(data.get("src_bscid")))
			.setString(1, String.valueOf(data.get("src_cellid")))
			.setInteger(2, (Integer)data.get("src_lac"))
			.setInteger(3, (Integer)data.get("src_ci"))
			.setString(4, reason).executeUpdate();
		}//else if(Constant.L2L.equals(bus_type)||Constant.L2T.equals(bus_type)) {
		else if(Constant.L2L.equals(bus_type)){
			delSql.append("delete from mes_lte_black where enodebid=? and localcellid = ?");
			insSql.append("insert into mes_lte_black (enodebid,localcellid,reason) values(?,?,?)");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_enodebid"))
			.setInteger(1, (Integer)data.get("src_localcellid")).executeUpdate();
			this.getSession().createSQLQuery(insSql.toString())
			.setInteger(0, (Integer)data.get("src_enodebid"))
			.setInteger(1, (Integer)data.get("src_localcellid")).setString(2, reason)
			.executeUpdate();
		}
	}
	
	@Override
	public void delBlack(Map<String, Object> data) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder delSql = new StringBuilder();
		/****************Neusoft********************/
		//if(Constant.T2G.equals(bus_type)){
		if(Constant.T2G.equals(bus_type)||Constant.T2L.equals(bus_type)) {
			delSql.append("delete from mes_td_black where lac=? and lcid = ?");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_lac"))
			.setInteger(1, (Integer)data.get("src_lcid")).executeUpdate();
		}else if(Constant.G2G.equals(bus_type)) {
			delSql.append("delete from mes_gsm_black where lac=? and ci = ?");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_lac"))
			.setInteger(1, (Integer)data.get("src_ci")).executeUpdate();
		}//else if(Constant.L2L.equals(bus_type)||Constant.L2T.equals(bus_type)) {
		else if(Constant.L2L.equals(bus_type) || Constant.L2L_MANY.equals(bus_type)){
			delSql.append("delete from mes_lte_black where enodebid=? and localcellid = ?");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_enodebid"))
			.setInteger(1, (Integer)data.get("src_localcellid")).executeUpdate();
		}
	}

	@Override
	public void addAlarm(Map<String, Object> data, String reason) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder insSql = new StringBuilder();
		/***************Neusoft**********************/
		//if(Constant.T2G.equals(bus_type)) {
		if(Constant.T2G.equals(bus_type)||Constant.T2L.equals(bus_type)) {
			insSql.append("insert into mes_td_selfalarm (rnc,lcid,lac,reason,starttime) values(?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString()).setString(0, String.valueOf(data.get("src_rnc")))
			.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_lcid"))))
			.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_lac"))))
			.setString(3, reason).setString(4, String.valueOf(data.get("starttime"))).executeUpdate();
		}else if(Constant.G2G.equals(bus_type)) {
			insSql.append("insert into mes_gsm_selfalarm (bscid,cellid,lac,ci,reason,starttime) values(?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString()).setString(0, String.valueOf(data.get("src_bscid")))
			.setString(1, String.valueOf(data.get("src_cellid"))).setInteger(2, (Integer)data.get("src_lac"))
			.setInteger(3, Integer.parseInt(String.valueOf(data.get("src_ci")))).setString(4, reason).setString(5, String.valueOf(data.get("starttime"))).executeUpdate();
		}//else if(Constant.L2L.equals(bus_type)||Constant.L2T.equals(bus_type)) {
		else if(Constant.L2L.equals(bus_type)){
			insSql.append("insert into mes_lte_selfalarm (enodebid,localcellid,reason,starttime) values(?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			.setInteger(0, Integer.parseInt(String.valueOf(data.get("src_enodebid"))))
			.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_localcellid")))).setString(2, reason)
			.setString(3, String.valueOf(data.get("starttime")))
			.executeUpdate();
		}
	}

	public void addSleepArea(Map<String, Object> data, AdjustCommand command) {
		commandDao.save(command);
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder insSql = new StringBuilder();
		if(Constant.T2G.equalsIgnoreCase(bus_type)) {
			insSql.append("insert into mes_t2g_currentsleep(starttime,src_rnc,src_lcid,src_lac,src_vender")
			.append(",dest_lac,dest_ci,dest_bscid,dest_cellid,dest_vender) values(?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			.setString(0, String.valueOf(data.get("starttime")))
			.setString(1, String.valueOf(data.get("src_rnc")))
			.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_lcid"))))
			.setInteger(3, Integer.parseInt(String.valueOf(data.get("src_lac"))))
			.setString(4, String.valueOf(data.get("src_vender")))
			.setInteger(5, Integer.parseInt(String.valueOf(data.get("dest_lac"))))
			.setInteger(6, Integer.parseInt(String.valueOf(data.get("dest_ci"))))
			.setString(7, String.valueOf(data.get("dest_bscid")))
			.setString(8, String.valueOf(data.get("dest_cellid")))
			.setString(9, String.valueOf(data.get("dest_vender"))).executeUpdate();
		}else if(Constant.G2G.equalsIgnoreCase(bus_type)) {
			insSql.append("INSERT INTO mes_g2g_currentsleep (starttime,src_bscid,src_cellid,src_lac,src_ci,dest_lac,dest_ci,dest_bscid,dest_cellid,src_vender,dest_vender) values(?,?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			        .setString(0, String.valueOf(data.get("starttime")))
					.setString(1, String.valueOf(data.get("src_bscid")))
					.setString(2, String.valueOf(data.get("src_cellid")))
					.setInteger(3, Integer.parseInt(String.valueOf(data.get("src_lac"))))
					.setInteger(4, Integer.parseInt(String.valueOf(data.get("src_ci"))))
					.setInteger(5, Integer.parseInt(String.valueOf(data.get("dest_lac"))))
					.setInteger(6, Integer.parseInt(String.valueOf(data.get("dest_ci"))))
					.setString(7, String.valueOf(data.get("dest_bscid")))
					.setString(8, String.valueOf(data.get("dest_cellid")))
					.setString(9, String.valueOf(data.get("src_vender")))
					.setString(10, String.valueOf(data.get("dest_vender")))
					.executeUpdate();
		}else if(Constant.L2L.equalsIgnoreCase(bus_type)) {
			insSql.append("INSERT INTO mes_l2l_currentsleep (starttime,src_enodebid,src_localcellid,dest_enodebid,dest_localcellid,src_vender,dest_vender,subnetwork,userlabel,omm,ip,cagroupid,preferredpcellpriority) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			        .setString(0, String.valueOf(data.get("starttime")))
					.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_enodebid"))))
					.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_localcellid"))))
					.setInteger(3, Integer.parseInt(String.valueOf(data.get("dest_enodebid"))))
					.setInteger(4, Integer.parseInt(String.valueOf(data.get("dest_localcellid"))))
					.setString(5, String.valueOf(data.get("src_vender")))
					.setString(6, String.valueOf(data.get("dest_vender")))
					.setString(7, String.valueOf(data.get("subnetwork")))
			        .setString(8, String.valueOf(data.get("userlabel")))
			        .setString(9, String.valueOf(data.get("omm")))
			        .setString(10, data.get("ip")==null?"":String.valueOf(data.get("ip")))
			        .setString(11, data.get("cagroupid")==null?null:String.valueOf(data.get("cagroupid")))
			        .setString(12, data.get("preferredpcellpriority")==null?null:String.valueOf(data.get("preferredpcellpriority")))
					.executeUpdate();
		}
		/**************************Neusoft*****************************/
		else if(Constant.T2L.equalsIgnoreCase(bus_type)){
			insSql.append("INSERT INTO mes_t2l_currentsleep (starttime,src_lac,src_ci,src_rnc,dest_enodebid,dest_localcellid,src_vender,")
					.append( "dest_vender,subnetwork,userlabel,omm,ip,cagroupid,preferredpcellpriority,src_lcid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			 .setString(0, String.valueOf(data.get("starttime")))
					.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_lac"))))
					.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_ci"))))
					.setString(3, String.valueOf(data.get("src_rnc")))
					.setInteger(4, Integer.parseInt(String.valueOf(data.get("dest_enodebid"))))
					.setInteger(5, Integer.parseInt(String.valueOf(data.get("dest_localcellid"))))
					.setString(6, String.valueOf(data.get("src_vender")))
					.setString(7, String.valueOf(data.get("dest_vender")))
					.setString(8, String.valueOf(data.get("subnetwork")))
			        .setString(9, String.valueOf(data.get("userlabel")))
			        .setString(10, String.valueOf(data.get("omm")))
			        .setString(11, data.get("ip")==null?"":String.valueOf(data.get("ip")))
			        .setString(12, data.get("cagroupid")==null?null:String.valueOf(data.get("cagroupid")))
			        .setString(13, data.get("preferredpcellpriority")==null?null:String.valueOf(data.get("preferredpcellpriority")))
			        .setString(14, String.valueOf(data.get("src_lcid")))
					.executeUpdate();
		}
		
		
		
		/*else if(Constant.L2T.equalsIgnoreCase(bus_type)) {
			insSql.append("INSERT INTO mes_l2t_currentsleep (starttime,src_enodebid,src_localcellid,dest_rnc,dest_lcid,dest_lac,src_vender,dest_vender,subnetwork,userlabel,omm,ip,cagroupid,preferredpcellpriority) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			 .setString(0, String.valueOf(data.get("starttime")))
					.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_enodebid"))))
					.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_localcellid"))))
					.setString(3, String.valueOf(data.get("dest_rnc")))
					.setInteger(4, Integer.parseInt(String.valueOf(data.get("dest_lcid"))))
					.setInteger(5, Integer.parseInt(String.valueOf(data.get("dest_lac"))))
					.setString(6, String.valueOf(data.get("src_vender")))
					.setString(7, String.valueOf(data.get("dest_vender")))
					.setString(8, String.valueOf(data.get("subnetwork")))
			        .setString(9, String.valueOf(data.get("userlabel")))
			        .setString(10, String.valueOf(data.get("omm")))
			        .setString(11, data.get("ip")==null?"":String.valueOf(data.get("ip")))
			        .setString(12, data.get("cagroupid")==null?null:String.valueOf(data.get("cagroupid")))
			        .setString(13, data.get("preferredpcellpriority")==null?null:String.valueOf(data.get("preferredpcellpriority")))
					.executeUpdate();
		}*/
	}

	@Override
	public void delNofifyFromSleep(Map<String, Object> data) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder delSql = new StringBuilder();
		if(Constant.T2G.equalsIgnoreCase(bus_type)) {
			delSql.append("delete from mes_t2g_currentsleep where src_lac=? and src_lcid=?");
			this.getSession().createSQLQuery(delSql.toString())
			.setInteger(0, (Integer)data.get("src_lac")).setInteger(1, (Integer)data.get("src_lcid"))
			.executeUpdate();
		}else if(Constant.G2G.equalsIgnoreCase(bus_type)) {
			delSql.append("delete from mes_g2g_currentsleep where src_lac=? and src_ci=?");
			this.getSession().createSQLQuery(delSql.toString())
					.setInteger(0, (Integer)data.get("src_lac"))
					.setInteger(1, (Integer)data.get("src_ci"))
					.executeUpdate();
		}else if(Constant.L2L.equalsIgnoreCase(bus_type)) {
			delSql.append("delete from mes_l2l_currentsleep where src_enodebid =? and src_localcellid=?");
			this.getSession().createSQLQuery(delSql.toString())
					.setInteger(0, (Integer)data.get("src_enodebid"))
					.setInteger(1, (Integer)data.get("src_localcellid"))
					.executeUpdate();
		}
		/**************Neusoft*****************/
		else if(Constant.T2L.equalsIgnoreCase(bus_type)) {
			delSql.append("delete from mes_t2l_currentsleep where src_lac =? and src_lcid=?");
			this.getSession().createSQLQuery(delSql.toString())
					.setInteger(0, (Integer)data.get("src_lac"))
					.setInteger(1, (Integer)data.get("src_lcid"))
					.executeUpdate();
		}
		/*else if(Constant.L2T.equalsIgnoreCase(bus_type)) {
			delSql.append("delete from mes_l2t_currentsleep where src_enodebid =? and src_localcellid=?");
			this.getSession().createSQLQuery(delSql.toString())
					.setInteger(0, (Integer)data.get("src_enodebid"))
					.setInteger(1, (Integer)data.get("src_localcellid"))
					.executeUpdate();
		}*/
	}
	
	@Override
	public void addSleepOrNotifyLog(Map<String, Object> data, String operation) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder sql = new StringBuilder();
		if(Constant.T2G.equalsIgnoreCase(bus_type)) {
			sql.append("insert into mes_td_commandlog (rnc,cellid,lac,commandtype,command,opertime,starttime,dest_bsc,dest_cell) values(?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(sql.toString()).setString(0, String.valueOf(data.get("src_rnc")))
			.setInteger(1, (Integer)data.get("src_lcid")).setInteger(2, (Integer)data.get("src_lac"))
			.setString(3, operation).setString(4, String.valueOf(data.get("command"))).setTimestamp(5, new Date())
			.setTimestamp(6, DateUtil.tranStrToDate(String.valueOf(data.get("starttime"))))
			.setString(7, String.valueOf(data.get("dest_bscid"))).setString(8, String.valueOf(data.get("dest_cellid"))).executeUpdate();
		}else if(Constant.G2G.equalsIgnoreCase(bus_type)) {
			sql.append("insert into mes_gsm_commandlog (bscid,cellid,lac,ci,commandtype,command,opertime,starttime,dest_bsc,dest_cell) values(?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(sql.toString()).setString(0, String.valueOf(data.get("src_bscid")))
			.setString(1, String.valueOf(data.get("src_cellid"))).setInteger(2, (Integer)data.get("src_lac"))
			.setInteger(3, (Integer)data.get("src_ci")).setString(4, operation)
		    .setString(5, String.valueOf(data.get("command"))).setTimestamp(6, new Date())
			.setTimestamp(7, DateUtil.tranStrToDate(String.valueOf(data.get("starttime"))))
			.setString(8, String.valueOf(data.get("dest_bscid"))).setString(9, String.valueOf(data.get("dest_cellid"))).executeUpdate();
		}else if(Constant.L2L.equalsIgnoreCase(bus_type)||Constant.L2L_MANY.equalsIgnoreCase(bus_type)) {
			sql.append("insert into mes_lte_commandlog (enodebid,cellid,commandtype,command,opertime,starttime,bustype,dest_enorrn,dest_cellid) values(?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(sql.toString()).setInteger(0, (Integer)data.get("src_enodebid"))
			.setInteger(1, (Integer)data.get("src_localcellid")).setString(2, operation).setString(3, String.valueOf(data.get("command")))
		    .setTimestamp(4, new Date()).setTimestamp(5, DateUtil.tranStrToDate(String.valueOf(data.get("starttime"))))
		    .setString(6, String.valueOf(data.get("bus_type"))).setString(7, String.valueOf(data.get("dest_enodebid")))
		    .setString(8, String.valueOf(data.get("dest_localcellid"))).executeUpdate();
		}
		/******************Neusoft********************/
		else if(Constant.T2L.equalsIgnoreCase(bus_type)) {
			sql.append("insert into mes_td_commandlog (rnc,cellid,lac,commandtype,command,opertime,starttime,dest_bsc,dest_cell) values(?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(sql.toString()).setString(0, String.valueOf(data.get("src_rnc")))
			.setInteger(1, (Integer)data.get("src_lcid")).setInteger(2, (Integer)data.get("src_lac"))
			.setString(3, operation).setString(4, String.valueOf(data.get("command"))).setTimestamp(5, new Date())
			.setTimestamp(6, DateUtil.tranStrToDate(String.valueOf(data.get("starttime"))))
			.setString(7, String.valueOf(data.get("dest_enodebid"))).setString(8, String.valueOf(data.get("dest_localcellid"))).executeUpdate();
		}
		
		/*else if(Constant.L2T.equalsIgnoreCase(bus_type)) {
			sql.append("insert into mes_lte_commandlog (enodebid,cellid,commandtype,command,opertime,starttime,bustype,dest_enorrn,dest_cellid) values(?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(sql.toString()).setInteger(0, (Integer)data.get("src_enodebid"))
			.setInteger(1, (Integer)data.get("src_localcellid")).setString(2, operation).setString(3, String.valueOf(data.get("command")))
		    .setTimestamp(4, new Date()).setTimestamp(5, DateUtil.tranStrToDate(String.valueOf(data.get("starttime"))))
		    .setString(6, String.valueOf(data.get("bus_type"))).setString(7, String.valueOf(data.get("dest_rnc")))
		    .setString(8, String.valueOf(data.get("dest_lcid"))).executeUpdate();
		}*/
	}

	@Override
	public void addtdOffSleepArea(Map<String, Object> data,
			AdjustCommand command, String sleep_type) {
		commandDao.save(command);
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder insSql = new StringBuilder();
		if(Constant.T2G.equalsIgnoreCase(bus_type)) {
			if(sleep_type.equalsIgnoreCase(Constant.PERMANENCE_AREA)){//永久
				insSql.append("insert into mes_t2g_permanence_currentsleep(src_rnc,src_lcid,src_longitude,src_latitude,src_ci,src_lac,src_vender,src_azimuth")
				.append(",dest_bsc,dest_longitude,dest_latitude,dest_lac,dest_ci,dest_azimuth,dest_vender,rst_azimuth,rst_instance)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				this.getSession().createSQLQuery(insSql.toString())
				.setString(0, String.valueOf(data.get("src_rnc")))
				.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_cellid"))))
				.setDouble(2, Double.parseDouble(String.valueOf(data.get("src_longitude"))))
				.setDouble(3, Double.parseDouble(String.valueOf(data.get("src_latitude"))))
				.setInteger(4, Integer.parseInt(String.valueOf(data.get("src_ci"))))
				.setInteger(5, Integer.parseInt(String.valueOf(data.get("src_lac"))))
				.setString(6, String.valueOf(data.get("src_vender")))
				.setInteger(7, Integer.parseInt(String.valueOf(data.get("src_azimuth"))))
				.setString(8, String.valueOf(data.get("dest_bsc")))
				.setDouble(9, Double.parseDouble(String.valueOf(data.get("dest_longitude"))))
				.setDouble(10, Double.parseDouble(String.valueOf(data.get("dest_latitude"))))
				.setInteger(11, Integer.parseInt(String.valueOf(data.get("dest_lac"))))
				.setInteger(12, Integer.parseInt(String.valueOf(data.get("dest_ci"))))
				.setInteger(13, Integer.parseInt(String.valueOf(data.get("dest_azimuth"))))
				.setString(14, String.valueOf(data.get("dest_vender")))
				.setInteger(15, Integer.parseInt(String.valueOf(data.get("rst_azimuth"))))
				.setDouble(16, Double.parseDouble(String.valueOf(data.get("rst_instance")))).executeUpdate();
			}else{//静态
				insSql.append("insert into mes_t2g_static_currentsleep(src_rnc,src_lcid,src_longitude,src_latitude,src_ci,src_lac,src_vender,src_azimuth")
				.append(",dest_bsc,dest_longitude,dest_latitude,dest_lac,dest_ci,dest_azimuth,dest_vender,rst_azimuth,rst_instance,stime,etime)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				this.getSession().createSQLQuery(insSql.toString())
				.setString(0, String.valueOf(data.get("src_rnc")))
				.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_cellid"))))
				.setDouble(2, Double.parseDouble(String.valueOf(data.get("src_longitude"))))
				.setDouble(3, Double.parseDouble(String.valueOf(data.get("src_latitude"))))
				.setInteger(4, Integer.parseInt(String.valueOf(data.get("src_ci"))))
				.setInteger(5, Integer.parseInt(String.valueOf(data.get("src_lac"))))
				.setString(6, String.valueOf(data.get("src_vender")))
				.setInteger(7, Integer.parseInt(String.valueOf(data.get("src_azimuth"))))
				.setString(8, String.valueOf(data.get("dest_bsc")))
				.setDouble(9, Double.parseDouble(String.valueOf(data.get("dest_longitude"))))
				.setDouble(10, Double.parseDouble(String.valueOf(data.get("dest_latitude"))))
				.setInteger(11, Integer.parseInt(String.valueOf(data.get("dest_lac"))))
				.setInteger(12, Integer.parseInt(String.valueOf(data.get("dest_ci"))))
				.setInteger(13, Integer.parseInt(String.valueOf(data.get("dest_azimuth"))))
				.setString(14, String.valueOf(data.get("dest_vender")))
				.setInteger(15, Integer.parseInt(String.valueOf(data.get("rst_azimuth"))))
				.setDouble(16, Double.parseDouble(String.valueOf(data.get("rst_instance"))))
				.setString(17, String.valueOf(data.get("stime")))
				.setString(18, String.valueOf(data.get("etime"))).executeUpdate();
			}
			
		}else if(Constant.T2L.equalsIgnoreCase(bus_type)){
			if(sleep_type.equalsIgnoreCase(Constant.PERMANENCE_AREA)){
				insSql.append("INSERT INTO mes_t2l_permanence_currentsleep (src_rnc,src_lcid,src_azimuth,src_longitude,src_latitude,src_vender,src_ci,src_lac,")
				.append("dest_enodebid,dest_localcellid,dest_azimuth,dest_longitude,dest_latitude,dest_vender,rst_azimuth,rst_instance)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				this.getSession().createSQLQuery(insSql.toString())
				.setString(0, String.valueOf(data.get("src_rnc")))
				.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_cellid"))))
				.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_azimuth"))))
				.setDouble(3, Double.parseDouble(String.valueOf(data.get("src_longitude"))))
				.setDouble(4, Double.parseDouble(String.valueOf(data.get("src_latitude"))))
				.setString(5, String.valueOf(data.get("src_vender")))
				.setInteger(6, Integer.parseInt(String.valueOf(data.get("src_ci"))))
				.setInteger(7, Integer.parseInt(String.valueOf(data.get("src_lac"))))
				.setInteger(8, Integer.parseInt(String.valueOf(data.get("dest_enodebid"))))
				.setInteger(9, Integer.parseInt(String.valueOf(data.get("dest_localcellid"))))
				.setInteger(10, Integer.parseInt(String.valueOf(data.get("dest_azimuth"))))
				.setDouble(11, Double.parseDouble(String.valueOf(data.get("dest_longitude"))))
				.setDouble(12, Double.parseDouble(String.valueOf(data.get("dest_latitude"))))
				.setString(13, String.valueOf(data.get("dest_vender")))
				.setInteger(14, Integer.parseInt(String.valueOf(data.get("rst_azimuth"))))
				.setDouble(15, Double.parseDouble(String.valueOf(data.get("rst_instance")))).executeUpdate();
			}else{
				insSql.append("INSERT INTO mes_t2l_static_currentsleep (src_rnc,src_lcid,src_azimuth,src_longitude,src_latitude,src_vender,src_ci,src_lac,")
				.append("dest_enodebid,dest_localcellid,dest_azimuth,dest_longitude,dest_latitude,dest_vender,rst_azimuth,rst_instance,stime,etime)")
				.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				this.getSession().createSQLQuery(insSql.toString())
				.setString(0, String.valueOf(data.get("src_rnc")))
				.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_cellid"))))
				.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_azimuth"))))
				.setDouble(3, Double.parseDouble(String.valueOf(data.get("src_longitude"))))
				.setDouble(4, Double.parseDouble(String.valueOf(data.get("src_latitude"))))
				.setString(5, String.valueOf(data.get("src_vender")))
				.setInteger(6, Integer.parseInt(String.valueOf(data.get("src_ci"))))
				.setInteger(7, Integer.parseInt(String.valueOf(data.get("src_lac"))))
				.setInteger(8, Integer.parseInt(String.valueOf(data.get("dest_enodebid"))))
				.setInteger(9, Integer.parseInt(String.valueOf(data.get("dest_localcellid"))))
				.setInteger(10, Integer.parseInt(String.valueOf(data.get("dest_azimuth"))))
				.setDouble(11, Double.parseDouble(String.valueOf(data.get("dest_longitude"))))
				.setDouble(12, Double.parseDouble(String.valueOf(data.get("dest_latitude"))))
				.setString(13, String.valueOf(data.get("dest_vender")))
				.setInteger(14, Integer.parseInt(String.valueOf(data.get("rst_azimuth"))))
				.setDouble(15, Double.parseDouble(String.valueOf(data.get("rst_instance"))))
		        .setString(16, String.valueOf(data.get("stime")))
		        .setString(17, String.valueOf(data.get("etime"))).executeUpdate();
			}

		}		
	}
	
	@Override
	public void delNofifyFromTdOffSleep(Map<String, Object> data) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder delSql1 = new StringBuilder();
		StringBuilder delSql2 = new StringBuilder();
		if(Constant.T2G.equalsIgnoreCase(bus_type)) {
			delSql1.append("delete from mes_t2g_permanence_currentsleep where src_lac=? and src_lcid=?");
			this.getSession().createSQLQuery(delSql1.toString())
			.setInteger(0, (Integer)data.get("src_lac")).setInteger(1, (Integer)data.get("src_lcid"))
			.executeUpdate();
			
			delSql2.append("delete from mes_t2g_static_currentsleep where src_lac=? and src_lcid=?");
			this.getSession().createSQLQuery(delSql2.toString())
			.setInteger(0, (Integer)data.get("src_lac")).setInteger(1, (Integer)data.get("src_lcid"))
			.executeUpdate();
		}else if(Constant.T2L.equalsIgnoreCase(bus_type)) {
			delSql1.append("delete from mes_t2l_permanence_currentsleep where src_lac =? and src_lcid=?");
			this.getSession().createSQLQuery(delSql1.toString())
					.setInteger(0, (Integer)data.get("src_lac"))
					.setInteger(1, (Integer)data.get("src_lcid"))
					.executeUpdate();
			
			delSql2.append("delete from mes_t2l_static_currentsleep where src_lac =? and src_lcid=?");
			this.getSession().createSQLQuery(delSql2.toString())
					.setInteger(0, (Integer)data.get("src_lac"))
					.setInteger(1, (Integer)data.get("src_lcid"))
					.executeUpdate();
		}		
	}
	
	/**
	 * 将多补一休眠小区添加当前多补一休眠小区表中
	 * @param data
	 * @param command 
	 */
	public void addManySleepArea(Map<String, Object> data, AdjustCommand command) {
		commandDao.save(command);
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder insSql = new StringBuilder();
		if(Constant.L2L_MANY.equalsIgnoreCase(bus_type)) {
			insSql.append("INSERT INTO mes_l2l_many_currentsleep ")
			      .append("(starttime,src_enodebid,src_localcellid,dest_enodebid,dest_localcellid,src_vender,dest_vender,")
			      .append("subnetwork,userlabel,omm,ip,cagroupid,preferredpcellpriority) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			        .setString(0, String.valueOf(data.get("starttime")))
					.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_enodebid"))))
					.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_localcellid"))))
					.setInteger(3, Integer.parseInt(String.valueOf(data.get("dest_enodebid"))))
					.setInteger(4, Integer.parseInt(String.valueOf(data.get("dest_localcellid"))))
					.setString(5, String.valueOf(data.get("src_vender")))
					.setString(6, String.valueOf(data.get("dest_vender")))
					.setString(7, String.valueOf(data.get("subnetwork")))
			        .setString(8, String.valueOf(data.get("userlabel")))
			        .setString(9, String.valueOf(data.get("omm")))
			        .setString(10, data.get("ip")==null?"":String.valueOf(data.get("ip")))
			        .setString(11, data.get("cagroupid")==null?null:String.valueOf(data.get("cagroupid")))
			        .setString(12, data.get("preferredpcellpriority")==null?null:String.valueOf(data.get("preferredpcellpriority")))
					.executeUpdate();
		}
		else if(Constant.T2T_MANY.equalsIgnoreCase(bus_type)){
			insSql.append("INSERT INTO mes_t2t_many_currentsleep (starttime,src_rnc,src_lcid,src_lac,src_vender,dest_rnc,dest_lcid,dest_lac,")
			      .append( "dest_vender) values(?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			.setString(0, String.valueOf(data.get("starttime")))
			.setString(1, String.valueOf(data.get("src_rnc")))
			.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_lcid"))))
			.setInteger(3, Integer.parseInt(String.valueOf(data.get("src_lac"))))
			.setString(4, String.valueOf(data.get("src_vender")))
			.setString(5, String.valueOf(data.get("dest_rnc")))
			.setInteger(6, Integer.parseInt(String.valueOf(data.get("dest_lcid"))))
			.setInteger(7, Integer.parseInt(String.valueOf(data.get("dest_lac"))))
			.setString(8, String.valueOf(data.get("dest_vender")))
			.executeUpdate();
		}
	}
	
	/**
	 * 将多补一休眠小区添加当前多补一休眠小区表中
	 * @param data
	 * @param command 
	 */
	public void addManySleepArea(Map<String, Object> data) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder insSql = new StringBuilder();
		if(Constant.L2L.equalsIgnoreCase(bus_type)) {
			insSql.append("INSERT INTO mes_l2l_many_currentsleep ")
			      .append("(starttime,src_enodebid,src_localcellid,dest_enodebid,dest_localcellid,src_vender,dest_vender,")
			      .append("subnetwork,userlabel,omm,ip,cagroupid,preferredpcellpriority) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			this.getSession().createSQLQuery(insSql.toString())
			        .setString(0, String.valueOf(data.get("starttime")))
					.setInteger(1, Integer.parseInt(String.valueOf(data.get("src_enodebid"))))
					.setInteger(2, Integer.parseInt(String.valueOf(data.get("src_localcellid"))))
					.setInteger(3, Integer.parseInt(String.valueOf(data.get("dest_enodebid"))))
					.setInteger(4, Integer.parseInt(String.valueOf(data.get("dest_localcellid"))))
					.setString(5, String.valueOf(data.get("src_vender")))
					.setString(6, String.valueOf(data.get("dest_vender")))
					.setString(7, String.valueOf(data.get("subnetwork")))
			        .setString(8, String.valueOf(data.get("userlabel")))
			        .setString(9, String.valueOf(data.get("omm")))
			        .setString(10, data.get("ip")==null?"":String.valueOf(data.get("ip")))
			        .setString(11, data.get("cagroupid")==null?null:String.valueOf(data.get("cagroupid")))
			        .setString(12, data.get("preferredpcellpriority")==null?null:String.valueOf(data.get("preferredpcellpriority")))
					.executeUpdate();
		}
	}
	
	@Override
	public void delNofifyFromManySleep(Map<String, Object> data) {
		String bus_type = String.valueOf(data.get("bus_type"));
		StringBuilder delSql = new StringBuilder();
		if(Constant.L2L.equalsIgnoreCase(bus_type)||Constant.L2L_MANY.equalsIgnoreCase(bus_type)) {
			delSql.append("delete from mes_l2l_many_currentsleep where src_enodebid =? and src_localcellid=?");
			this.getSession().createSQLQuery(delSql.toString())
					.setInteger(0, (Integer)data.get("src_enodebid"))
					.setInteger(1, (Integer)data.get("src_localcellid"))
					.executeUpdate();
		}
	}
}
