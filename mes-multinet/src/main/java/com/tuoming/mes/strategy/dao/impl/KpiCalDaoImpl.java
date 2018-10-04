package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.KpiCalDao;
import com.tuoming.mes.strategy.model.KpiCalModel;

/**
 * kpi计算数据访问接口实现类
 *
 * @author Administrator
 */
@Repository("kpiCalDao")
public class KpiCalDaoImpl extends AbstractBaseDao<KpiCalModel, Integer> implements KpiCalDao {

    public List<KpiCalModel> querySetList(String groupName) {
        StringBuilder sql = new StringBuilder();
        sql.append(HQL_LIST_ALL).append(" WHERE enabled=1 ");
        if (!StringUtils.isEmpty(groupName)) {
            sql.append(" and groupName like ?");

        }
        Query query = this.getSession().createQuery(sql.toString());
        if (!StringUtils.isEmpty(groupName)) {
            query.setString(0, groupName + "%");
        }
        return query.list();
    }

    public List<Map<String, Object>> quertDataBySql(String querySql, String starttime) {
        Query query = this.getSession().createSQLQuery(querySql);
        if (StringUtil.isNotEmpty(starttime)) {
            int paramCount = StringUtil.matchCount(querySql, "\\?");
            for (int i = 0; i < paramCount; i++) {
                query.setString(i, starttime);
            }
        }
        return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    public void delHisData(String tableName) {
        String sql = "truncate table " + tableName;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void insertHisData(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(wlbm).append("_his")
                .append(" select * from ").append(wlbm);
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();

    }

    public void insertGsmKpi(final List<Map<String, Object>> data) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // TODO Auto-generated method stub
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append("mes_g2g_mobile_table")
                        .append("(starttime,src_bscid,src_cellid,src_lac,src_ci,src_vender,")
                        .append("dest_bscid,dest_cellid,dest_lac,dest_ci,dest_vender,src_wxzylyl,src_mxhwl,src_hwl,src_tbffyd,src_wxzylylTHR,src_mxhwlTHR,src_hwlTHR,src_tbffydTHR,dest_mxhwl,dest_wxzylyl,dest_mxhwlTHR,dest_wxzylylTHR)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : data) {
                    pst.setObject(1, map.get("starttime"));
                    pst.setObject(2, map.get("src_bscid"));
                    pst.setObject(3, map.get("src_cellid"));
                    pst.setObject(4, map.get("src_lac"));
                    pst.setObject(5, map.get("src_ci"));
                    pst.setObject(6, map.get("src_vender") == null ? "" : map.get("src_vender"));
                    pst.setObject(7, map.get("dest_bscid"));
                    pst.setObject(8, map.get("dest_cellid"));
                    pst.setObject(9, map.get("dest_lac"));
                    pst.setObject(10, map.get("dest_ci"));
                    pst.setObject(11, map.get("dest_vender") == null ? "" : map.get("dest_vender"));
                    pst.setObject(12, map.get("src_wxzylyl") == null ? "" : map.get("src_wxzylyl"));
                    pst.setObject(13, map.get("src_mxhwl") == null ? "" : map.get("src_mxhwl"));
                    pst.setObject(14, map.get("src_hwl") == null ? "" : map.get("src_hwl"));
                    pst.setObject(15, map.get("src_tbffud") == null ? "" : map.get("src_tbffud"));
                    pst.setObject(16, map.get("src_wxzylylTHR") == null ? "" : map.get("src_wxzylylTHR"));
                    pst.setObject(17, map.get("src_mxhwlTHR") == null ? "" : map.get("src_mxhwlTHR"));
                    pst.setObject(18, map.get("src_hwlTHR") == null ? "" : map.get("src_hwlTHR"));
                    pst.setObject(19, map.get("src_tbffydTHR") == null ? "" : map.get("src_tbffydTHR"));
                    pst.setObject(20, map.get("dest_mxhwl_lj") == null ? "" : map.get("dest_mxhwl_lj"));
                    pst.setObject(21, map.get("dest_wxzylyl_lj") == null ? "" : map.get("dest_wxzylyl_lj"));
                    pst.setObject(22, map.get("dest_mxhwlTHR") == null ? "" : map.get("dest_mxhwlTHR"));
                    pst.setObject(23, map.get("dest_wxzylylTHR") == null ? "" : map.get("dest_wxzylylTHR"));
                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

    public void insertL2lKpi(final List<Map<String, Object>> data) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // TODO Auto-generated method stub
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append("mes_l2l_mobile_table")
                        .append("(starttime,src_enodebid,src_localcellid,src_vender,subnetwork,src_cnname,")
                        .append("omm,ip,dest_enodebid,dest_localcellid,dest_cnname,dest_vender,cagroupid,preferredpcellpriority,")
                        .append("src_sxsjll,src_xxsjll,src_prblyl,src_zdyhs,src_sxsjllTHR,src_xxsjllTHR,src_prblylTHR,src_zdyhsTHR,")
                        .append("dest_sxsjll,dest_xxsjll,dest_zdyhs,dest_sxsjllTHR,dest_xxsjllTHR,dest_zdyhsTHR)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : data) {
                    pst.setObject(1, map.get("starttime"));
                    pst.setObject(2, map.get("src_enodebid"));
                    pst.setObject(3, map.get("src_localcellid"));
                    pst.setObject(4, map.get("src_vender") == null ? "" : map.get("src_vender"));
                    pst.setObject(5, map.get("subnetwork") == null ? "" : map.get("subnetwork"));
                    pst.setObject(6, map.get("userlabel") == null ? "" : map.get("userlabel"));
                    pst.setObject(7, map.get("omm") == null ? "" : map.get("omm"));
                    pst.setObject(8, map.get("ip") == null ? "" : map.get("ip"));
                    pst.setObject(9, map.get("dest_enodebid"));
                    pst.setObject(10, map.get("dest_localcellid"));
                    pst.setObject(11, map.get("dest_cnname") == null ? "" : map.get("dest_cnname"));
                    pst.setObject(12, map.get("dest_vender") == null ? "" : map.get("dest_vender"));
                    pst.setObject(13, map.get("cagroupid") == null ? 0 : map.get("cagroupid"));
                    pst.setObject(14, map.get("preferredpcellpriority") == null ? "" : map.get("preferredpcellpriority"));
                    pst.setObject(15, map.get("src_sxsjll") == null ? "" : map.get("src_sxsjll"));
                    pst.setObject(16, map.get("src_xxsjll") == null ? "" : map.get("src_xxsjll"));
                    pst.setObject(17, map.get("src_prblyl") == null ? "" : map.get("src_prblyl"));
                    pst.setObject(18, map.get("src_zdyhs") == null ? "" : map.get("src_zdyhs"));
                    pst.setObject(19, map.get("src_sxsjllTHR") == null ? "" : map.get("src_sxsjllTHR"));
                    pst.setObject(20, map.get("src_xxsjllTHR") == null ? "" : map.get("src_xxsjllTHR"));
                    pst.setObject(21, map.get("src_prblylTHR") == null ? "" : map.get("src_prblylTHR"));
                    pst.setObject(22, map.get("src_zdyhsTHR") == null ? "" : map.get("src_zdyhsTHR"));
                    pst.setObject(23, map.get("dest_sxsjll_lj") == null ? "" : map.get("dest_sxsjll_lj"));
                    pst.setObject(24, map.get("dest_xxsjll_lj") == null ? "" : map.get("dest_xxsjll_lj"));
                    pst.setObject(25, map.get("dest_zdyhs_lj") == null ? "" : map.get("dest_zdyhs_lj"));
                    pst.setObject(26, map.get("dest_sxsjllTHR") == null ? "" : map.get("dest_sxsjllTHR"));
                    pst.setObject(27, map.get("dest_xxsjllTHR") == null ? "" : map.get("dest_xxsjllTHR"));
                    pst.setObject(28, map.get("dest_zdyhsTHR") == null ? "" : map.get("dest_zdyhsTHR"));

                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

    public void insertT2GKpi(final List<Map<String, Object>> data) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // TODO Auto-generated method stub
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append("mes_t2g_mobile_table")
                        .append("(starttime,src_rnc,src_lcid,src_lac,src_vender,dest_lac,")
                        .append("dest_ci,dest_cellid,dest_vender,src_yyyw,src_sjll,src_mzylyl,src_zdyhs,src_dyhsl,")
                        .append("src_yyywTHR,src_sjllTHR,src_mzylylTHR,src_zdyhsTHR,dest_mxhwl,dest_dpdchczl,dest_mxhwlTHR,")
                        .append("dest_dpdchczlTHR)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : data) {
                    pst.setObject(1, map.get("starttime"));
                    pst.setObject(2, map.get("src_rnc"));
                    pst.setObject(3, map.get("src_lcid"));
                    pst.setObject(4, map.get("src_lac"));
                    pst.setObject(5, map.get("src_vender") == null ? "" : map.get("src_vender"));
                    pst.setObject(6, map.get("dest_lac"));
                    pst.setObject(7, map.get("dest_ci"));
                    pst.setObject(8, map.get("dest_cellid"));
                    pst.setObject(9, map.get("dest_vender") == null ? "" : map.get("dest_vender"));
                    pst.setObject(10, map.get("src_yyyw") == null ? "" : map.get("src_yyyw"));
                    pst.setObject(11, map.get("src_sjll") == null ? "" : map.get("src_sjll"));
                    pst.setObject(12, map.get("src_mzylyl") == null ? "" : map.get("src_mzylyl"));
                    pst.setObject(13, map.get("src_zdyhs") == null ? "" : map.get("src_zdyhs"));
                    pst.setObject(14, map.get("src_dyhsl") == null ? "" : map.get("src_dyhsl"));
                    pst.setObject(15, map.get("src_yyywTHR") == null ? "" : map.get("src_yyywTHR"));
                    pst.setObject(16, map.get("src_sjllTHR") == null ? "" : map.get("src_sjllTHR"));
                    pst.setObject(17, map.get("src_mzylylTHR") == null ? "" : map.get("src_mzylylTHR"));
                    pst.setObject(18, map.get("src_zdyhsTHR") == null ? "" : map.get("src_zdyhsTHR"));
                    pst.setObject(19, map.get("dest_mxhwl_lj") == null ? "" : map.get("dest_mxhwl_lj"));
                    pst.setObject(20, map.get("dest_dpdchczl_lj") == null ? "" : map.get("dest_dpdchczl_lj"));
                    pst.setObject(21, map.get("dest_mxhwlTHR") == null ? "" : map.get("dest_mxhwlTHR"));
                    pst.setObject(22, map.get("dest_dpdchczlTHR") == null ? "" : map.get("dest_dpdchczlTHR"));

                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

    public void insertL2TKpi(final List<Map<String, Object>> data) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // TODO Auto-generated method stub
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append("mes_L2T_mobile_table")
                        .append("(starttime,src_enodebid,src_localcellid,src_vender,subnetwork,userlabel,")
                        .append("omm,ip,dest_rnc,dest_lcid,dest_lac,dest_vender,cagroupid,preferredpcellpriority,")
                        .append("src_sxsjll,src_xxsjll,src_zdyhs,src_dyhsl,src_sxsjllTHR,src_xxsjllTHR,src_zdyhsTHR,")
                        .append("dest_yyyw,dest_sjll,dest_mzylyl,dest_zdyhs,dest_yyywTHR,dest_sjllTHR,dest_mzylylTHR,dest_zdyhsTHR)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : data) {
                    pst.setObject(1, map.get("starttime"));
                    pst.setObject(2, map.get("src_enodebid"));
                    pst.setObject(3, map.get("src_localcellid"));
                    pst.setObject(4, map.get("src_vender") == null ? "" : map.get("src_vender"));
                    pst.setObject(5, map.get("subnetwork") == null ? "" : map.get("subnetwork"));
                    pst.setObject(6, map.get("userlabel") == null ? "" : map.get("userlabel"));
                    pst.setObject(7, map.get("omm") == null ? "" : map.get("omm"));
                    pst.setObject(8, map.get("ip") == null ? "" : map.get("ip"));
                    pst.setObject(9, map.get("dest_rnc"));
                    pst.setObject(10, map.get("dest_lcid"));
                    pst.setObject(11, map.get("dest_lac"));
                    pst.setObject(12, map.get("dest_vender") == null ? "" : map.get("dest_vender"));
                    pst.setObject(13, map.get("cagroupid") == null ? 0 : map.get("cagroupid"));
                    pst.setObject(14, map.get("preferredpcellpriority") == null ? "" : map.get("preferredpcellpriority"));
                    pst.setObject(15, map.get("src_sxsjll") == null ? "" : map.get("src_sxsjll"));
                    pst.setObject(16, map.get("src_xxsjll") == null ? "" : map.get("src_xxsjll"));
                    pst.setObject(17, map.get("src_zdyhs") == null ? "" : map.get("src_zdyhs"));
                    pst.setObject(18, map.get("src_dyhsl") == null ? "" : map.get("src_dyhsl"));
                    pst.setObject(19, map.get("src_sxsjllTHR") == null ? "" : map.get("src_sxsjllTHR"));
                    pst.setObject(20, map.get("src_xxsjllTHR") == null ? "" : map.get("src_xxsjllTHR"));
                    pst.setObject(21, map.get("src_zdyhsTHR") == null ? "" : map.get("src_zdyhsTHR"));
                    pst.setObject(22, map.get("dest_yyyw") == null ? "" : map.get("dest_yyyw"));
                    pst.setObject(23, map.get("dest_sjll_lj") == null ? "" : map.get("dest_sjll_lj"));
                    pst.setObject(24, map.get("dest_mzylyl") == null ? "" : map.get("dest_mzylyl"));
                    pst.setObject(25, map.get("dest_zdyhs_lj") == null ? "" : map.get("dest_zdyhs_lj"));
                    pst.setObject(26, map.get("dest_yyywTHR") == null ? "" : map.get("dest_yyywTHR"));
                    pst.setObject(27, map.get("dest_sjllTHR") == null ? "" : map.get("dest_sjllTHR"));
                    pst.setObject(28, map.get("dest_mzylylTHR") == null ? "" : map.get("dest_mzylylTHR"));
                    pst.setObject(29, map.get("dest_zdyhsTHR") == null ? "" : map.get("dest_zdyhsTHR"));
                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

    public void insertT2LKpi(final List<Map<String, Object>> data) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // TODO Auto-generated method stub
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append("mes_t2l_mobile_table")
                        .append("(starttime,src_rnc,src_lcid,src_lac,src_ci,src_vender,subnetwork,userlabel,")
                        .append("omm,ip,dest_enodebid,dest_localcellid,dest_vender,cagroupid,preferredpcellpriority,")
                        .append("src_yyyw,src_sjll,src_zdyhs,src_dyhsl,src_mzylyl,src_yyywTHR,src_sjllTHR,src_zdyhsTHR,src_dyhslTHR,src_mzylylTHR,")
                        .append("dest_zdyhs,dest_sxsjll,dest_xxsjll,dest_sxsjllTHR,dest_xxsjllTHR,dest_zdyhsTHR)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : data) {
                    pst.setObject(1, map.get("starttime"));
                    pst.setObject(2, map.get("src_rnc"));
                    pst.setObject(3, map.get("src_lcid"));
                    pst.setObject(4, map.get("src_lac"));
                    pst.setObject(5, map.get("src_ci"));
                    pst.setObject(6, map.get("src_vender") == null ? "" : map.get("src_vender"));
                    pst.setObject(7, map.get("subnetwork") == null ? "" : map.get("subnetwork"));
                    pst.setObject(8, map.get("userlabel") == null ? "" : map.get("userlabel"));
                    pst.setObject(9, map.get("omm") == null ? "" : map.get("omm"));
                    pst.setObject(10, map.get("ip") == null ? "" : map.get("ip"));
                    pst.setObject(11, map.get("dest_enodebid"));
                    pst.setObject(12, map.get("dest_localcellid"));
                    pst.setObject(13, map.get("dest_vender") == null ? "" : map.get("dest_vender"));
                    pst.setObject(14, map.get("cagroupid") == null ? 0 : map.get("cagroupid"));
                    pst.setObject(15, map.get("preferredpcellpriority") == null ? "" : map.get("preferredpcellpriority"));
                    pst.setObject(16, map.get("src_yyyw") == null ? "" : map.get("src_yyyw"));
                    pst.setObject(17, map.get("src_sjll") == null ? "" : map.get("src_sjll"));
                    pst.setObject(18, map.get("src_zdyhs") == null ? "" : map.get("src_zdyhs"));
                    pst.setObject(19, map.get("src_dyhsl") == null ? "" : map.get("src_dyhsl"));
                    pst.setObject(20, map.get("src_mzylyl") == null ? "" : map.get("src_mzylyl"));
                    pst.setObject(21, map.get("src_yyywTHR") == null ? "" : map.get("src_yyywTHR"));
                    pst.setObject(22, map.get("src_sjllTHR") == null ? "" : map.get("src_sjllTHR"));
                    pst.setObject(23, map.get("src_zdyhsTHR") == null ? "" : map.get("src_zdyhsTHR"));
                    pst.setObject(24, map.get("src_dyhslTHR") == null ? "" : map.get("src_dyhslTHR"));
                    pst.setObject(25, map.get("src_mzylylTHR") == null ? "" : map.get("src_mzylylTHR"));
                    pst.setObject(26, map.get("dest_zdyhs") == null ? "" : map.get("dest_zdyhs"));
                    pst.setObject(27, map.get("dest_sxsjll_lj") == null ? "" : map.get("dest_sxsjll_lj"));
                    pst.setObject(28, map.get("dest_xxsjll_lj") == null ? "" : map.get("dest_xxsjll_lj"));
                    pst.setObject(29, map.get("dest_sxsjllTHR") == null ? "" : map.get("dest_sxsjllTHR"));
                    pst.setObject(30, map.get("dest_xxsjllTHR") == null ? "" : map.get("dest_xxsjllTHR"));
                    pst.setObject(31, map.get("dest_zdyhsTHR") == null ? "" : map.get("dest_zdyhsTHR"));
                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

    public void insertT2TKpi(final List<Map<String, Object>> data) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append("mes_t2t_mobile_table")
                        .append("(starttime,src_rnc,src_lcid,src_lac,src_vender,")
                        .append("dest_rnc,dest_lcid,dest_lac,dest_vender,")
                        .append("src_yyyw,src_sjll,src_mzylyl,src_zdyhs,src_dyhsl,src_yyywTHR,src_sjllTHR,src_mzylylTHR,src_zdyhsTHR,")
                        .append("dest_yyyw,dest_sjll,dest_mzylyl,dest_zdyhs,dest_yyywTHR,dest_sjllTHR,dest_mzylylTHR,dest_zdyhsTHR)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : data) {
                    pst.setObject(1, map.get("starttime"));
                    pst.setObject(2, map.get("src_rnc"));
                    pst.setObject(3, map.get("src_lcid"));
                    pst.setObject(4, map.get("src_lac"));
                    pst.setObject(5, map.get("src_vender") == null ? "" : map.get("src_vender"));
                    pst.setObject(6, map.get("dest_rnc"));
                    pst.setObject(7, map.get("dest_lcid"));
                    pst.setObject(8, map.get("dest_lac"));
                    pst.setObject(9, map.get("dest_vender") == null ? "" : map.get("dest_vender"));
                    pst.setObject(10, map.get("src_yyyw") == null ? "" : map.get("src_yyyw"));
                    pst.setObject(11, map.get("src_sjll") == null ? "" : map.get("src_sjll"));
                    pst.setObject(12, map.get("src_mzylyl") == null ? "" : map.get("src_mzylyl"));
                    pst.setObject(13, map.get("src_zdyhs") == null ? "" : map.get("src_zdyhs"));
                    pst.setObject(14, map.get("src_dyhsl") == null ? "" : map.get("src_dyhsl"));
                    pst.setObject(15, map.get("src_yyywTHR") == null ? "" : map.get("src_yyywTHR"));
                    pst.setObject(16, map.get("src_sjllTHR") == null ? "" : map.get("src_sjllTHR"));
                    pst.setObject(17, map.get("src_mzylylTHR") == null ? "" : map.get("src_mzylylTHR"));
                    pst.setObject(18, map.get("src_zdyhsTHR") == null ? "" : map.get("src_zdyhsTHR"));
                    pst.setObject(19, map.get("dest_yyyw") == null ? "" : map.get("dest_yyyw"));
                    pst.setObject(20, map.get("dest_sjll_lj") == null ? "" : map.get("dest_sjll_lj"));
                    pst.setObject(21, map.get("dest_mzylyl") == null ? "" : map.get("dest_mzylyl"));
                    pst.setObject(22, map.get("dest_zdyhs_lj") == null ? "" : map.get("dest_zdyhs_lj"));
                    pst.setObject(23, map.get("dest_yyywTHR") == null ? "" : map.get("dest_yyywTHR"));
                    pst.setObject(24, map.get("dest_sjllTHR") == null ? "" : map.get("dest_sjllTHR"));
                    pst.setObject(25, map.get("dest_mzylylTHR") == null ? "" : map.get("dest_mzylylTHR"));
                    pst.setObject(26, map.get("dest_zdyhsTHR") == null ? "" : map.get("dest_zdyhsTHR"));

                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

}
