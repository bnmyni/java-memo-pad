package com.tuoming.mes.strategy.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.MrDetailDao;
import com.tuoming.mes.strategy.model.MrDetailModel;
import com.tuoming.mes.strategy.service.handle.himpl.AzimuthCalculationHandle;
import com.tuoming.mes.strategy.util.FormatUtil;

@Repository("MrDetailDao")
public class MrDetailDaoImpl extends AbstractBaseDao<MrDetailModel, Integer> implements MrDetailDao {


	@Override
	public List<MrDetailModel> querySetList(String groupName) {
		StringBuilder sql = new StringBuilder();
		sql.append(HQL_LIST_ALL).append(" WHERE enabled=1 ");
		if(!StringUtils.isEmpty(groupName)) {
			sql.append(" and groupName like ?");
			
		}
		Query query = this.getSession().createQuery(sql.toString());
		if(!StringUtils.isEmpty(groupName)) {
			query.setString(0, groupName+"%");
		}
		return query.list();
	}

	@Override
	public void removeAllData(String tableName) {
		String sql = "truncate table "+tableName;
		this.getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void insertData(final String resTable, String querySql) {
		List<Map<String,Object>> dataList = this.getSession().createSQLQuery(querySql)
				.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		final List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> data:dataList) {
			double slong = FormatUtil.tranferCalValue(data.get("srclong"));
			double slat = FormatUtil.tranferCalValue(data.get("srclat"));
			double dlong = FormatUtil.tranferCalValue(data.get("destlong"));
			double dlat = FormatUtil.tranferCalValue(data.get("destlat"));
			double instance = AzimuthCalculationHandle.getInstance(slong, slat, dlong, dlat);
			if(instance<500) {
				resList.add(data);
			}
		}
		if(resList.isEmpty()) {
			return;
		}
		
		if(resTable.toLowerCase().indexOf("g2g")>0) {
			this.getSession().doWork(new Work() {
				
				public void execute(Connection connection) throws SQLException {
					StringBuilder sql = new StringBuilder();
					sql.append("insert into ").append(resTable)
					.append("(src_bsc,src_cellid,src_lac,src_ci,src_vender,dest_bsc,dest_cellid,")
					.append("dest_lac,dest_ci,dest_vender,sstate,nstate,overdegree)")
					.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
					PreparedStatement pst = connection.prepareStatement(sql.toString());
					for(Map<String,Object> map:resList) {
						pst.setObject(1, map.get("src_bsc"));
						pst.setObject(2, map.get("src_cellid"));
						pst.setObject(3, map.get("src_lac"));
						pst.setObject(4, map.get("src_ci"));
						pst.setObject(5, map.get("src_vender"));
						pst.setObject(6, map.get("dest_bsc"));
						pst.setObject(7, map.get("dest_cellid"));
						pst.setObject(8, map.get("dest_lac"));
						pst.setObject(9, map.get("dest_ci"));
						pst.setObject(10, map.get("dest_vender"));
						pst.setObject(11, map.get("sstate"));
						pst.setObject(12, map.get("nstate"));
						pst.setObject(13, map.get("overdegree"));
						pst.addBatch();
					}
					pst.executeBatch();
					pst.clearBatch();
					pst.close();
				}
			});
		}else if(resTable.toLowerCase().indexOf("l2l")>0) {
			this.getSession().doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					StringBuilder sql = new StringBuilder();
					sql.append("insert into ").append(resTable)
					.append("(src_localcellid,src_enodebid,subnetwork,userlable,omm,enodebname,")
					.append("src_vender,dest_localcellid,dest_enodebid,dest_vender,sstate,nstate,overdegree)")
					.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
					PreparedStatement pst = connection.prepareStatement(sql.toString());
					for(Map<String,Object> map:resList) {
						pst.setObject(1, map.get("src_localcellid"));
						pst.setObject(2, map.get("src_enodebid"));
						pst.setObject(3, map.get("subnetwork"));
						pst.setObject(4, map.get("userlabel"));
						pst.setObject(5, map.get("omm"));
						pst.setObject(6, map.get("enodebname"));
						pst.setObject(7, map.get("src_vender"));
						pst.setObject(8, map.get("dest_localcellid"));
						pst.setObject(9, map.get("dest_enodebid"));
						pst.setObject(10, map.get("dest_vender"));
						pst.setObject(11, map.get("sstate"));
						pst.setObject(12, map.get("nstate"));
						pst.setObject(13, map.get("overdegree"));
						pst.addBatch();
					}
					pst.executeBatch();
					pst.clearBatch();
					pst.close();
				}
			});
		}
	}

}
