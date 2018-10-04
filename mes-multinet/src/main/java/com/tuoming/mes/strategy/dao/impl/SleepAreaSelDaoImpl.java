package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.SleepAreaSelDao;
import com.tuoming.mes.strategy.model.SleepSelectModel;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 休眠小区筛选数据访问接口实现
 *
 * @author Administrator
 */

@Repository("sleepAreaSelDao")
public class SleepAreaSelDaoImpl extends
        AbstractBaseDao<SleepSelectModel, Integer> implements
        SleepAreaSelDao {


    @SuppressWarnings("unchecked")
    @Override
    public List<SleepSelectModel> querySleepAreaSelSet(String groupName) {
        // TODO Auto-generated method stub
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


    // 查询GSM门限
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> queryGsmDicList() {
        // TODO Auto-generated method stub
        String sql = "select * from mes_gsm_switch_threshold";
        return this.getSession().createSQLQuery(sql)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    public List<Map<String, Object>> queryTdDicList() {
        String sql = "select * from mes_td_switch_threshold";
        return this.getSession().createSQLQuery(sql)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 查询lte门限字典
     */
    public Map<String, Double> queryLteDic() {
        String sql = "select * from mes_lte_switch_threshold";
        Map<String, Double> dataMap = (Map<String, Double>) this.getSession()
                .createSQLQuery(sql)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list()
                .get(0);
        return dataMap;
    }

    @Override
    public List<String> queryPriorities() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ZS FROM mes_zs_priority ORDER BY priority");
        return this.getSession().createSQLQuery(sql.toString()).list();
    }

    @Override
    public void deleteConflictsForLte(boolean l2lAzimuth) {
        StringBuilder sql = new StringBuilder();
        String wlbm = l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr";
        sql.append(
                "DELETE FROM mes_l2t_sleep_azimuth where (src_enodebid,src_localcellid)")
                .append(" in (select src_enodebid,src_localcellid from ").append(wlbm).append(" )");
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();

    }

    @Override
    public Map<String, List<Map<String, Object>>> queryMakeUpGsmAndTd(boolean g2gAzimuth, boolean t2gAzimuth, int tdPriority, int gsmPriority) {
        StringBuilder g2gSql = new StringBuilder();
        StringBuilder t2gSql = new StringBuilder();
        g2gSql.append(
                "SELECT dest_lac,dest_ci,src_lac,src_ci,destkpi.mxhwl dest_mxhwl,destkpi.tchxdcspz dest_tchxdcspz,")
                .append(" srckpi.mxhwl src_mxhwl,srckpi.pdchczl src_pdchczl,destkpi.pdchczl dest_pdchczl,'g2g' bus_type FROM ")
                .append(g2gAzimuth ? "mes_g2g_sleep_azimuth"
                        : "mes_g2g_sleep_mr")
                .append(" g2g JOIN mes_gsm_kpi destkpi")
                .append(" on destkpi.lac=g2g.dest_lac and destkpi.ci=g2g.dest_ci  join mes_gsm_kpi srckpi")
                .append(" on srckpi.lac=g2g.dest_lac and srckpi.ci=g2g.dest_ci ")
                .append(" WHERE (dest_lac, dest_ci) IN ( SELECT DISTINCT g2g.dest_lac, g2g.dest_ci FROM ")
                .append(g2gAzimuth ? "mes_g2g_sleep_azimuth"
                        : "mes_g2g_sleep_mr")
                .append(" g2g")
                .append(" JOIN ")
                .append(t2gAzimuth ? "mes_t2g_sleep_azimuth"
                        : "mes_t2g_sleep_mr")
                .append(" t2g ON g2g.dest_lac = t2g.dest_lac AND g2g.dest_ci = t2g.dest_ci ) order by srckpi.mxhwl ");
        t2gSql.append(
                " SELECT dest_lac, dest_ci,src_lac,src_lcid, destkpi.mxhwl dest_mxhwl, srckpi.yyyw src_yyyw,destkpi.tchxdcspz dest_tchxdcspz,")
                .append(" destkpi.pdchczl dest_pdchczl,destkpi.pdchzygs dest_pdchzygs,srckpi.sjll src_sjll,'t2g' bus_type  FROM ")
                .append(t2gAzimuth ? "mes_t2g_sleep_azimuth"
                        : "mes_t2g_sleep_mr")
                .append(" t2g JOIN mes_gsm_kpi destkpi on destkpi.lac=t2g.dest_lac")
                .append(" and destkpi.ci=t2g.dest_ci  join mes_td_kpi srckpi on srckpi.lac=t2g.src_lac and srckpi.lcid=t2g.src_lcid")
                .append("  WHERE (dest_lac, dest_ci) IN ( SELECT DISTINCT g2g.dest_lac, g2g.dest_ci")
                .append(" FROM ")
                .append(g2gAzimuth ? "mes_g2g_sleep_azimuth"
                        : "mes_g2g_sleep_mr")
                .append(" g2g JOIN ")
                .append(t2gAzimuth ? "mes_t2g_sleep_azimuth"
                        : "mes_t2g_sleep_mr")
                .append(" t2g ON g2g.dest_lac = t2g.dest_lac AND g2g.dest_ci = t2g.dest_ci ) order by srckpi.yyyw");

        List<Map<String, Object>> g2gDataList = this.getSession().createSQLQuery(g2gSql.toString())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        List<Map<String, Object>> t2gDataList = this.getSession().createSQLQuery(t2gSql.toString())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        Map<String, List<Map<String, Object>>> resMap = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        if (tdPriority < gsmPriority) {
            resList.addAll(t2gDataList);
            resList.addAll(g2gDataList);
        } else {
            resList.addAll(g2gDataList);
            resList.addAll(t2gDataList);
        }
        for (Map<String, Object> data : resList) {
            String key = String.valueOf(data.get("dest_lac") + "_" + data.get("dest_ci"));
            if (resMap.get(key) == null) {
                resMap.put(key, new ArrayList<Map<String, Object>>());
            }
            resMap.get(key).add(data);
        }
        return resMap;
    }

    @Override
    public void deleteG2gByList(Map<String, Object> data,
                                boolean isAzimuth) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ")
                .append(isAzimuth ? "mes_g2g_sleep_azimuth"
                        : "mes_g2g_sleep_mr")
                .append(" where src_lac=? and src_ci=? and dest_lac=? and dest_ci=? ");
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setParameter(0, data.get("src_lac"));
        query.setParameter(1, data.get("src_ci"));
        query.setParameter(2, data.get("dest_lac"));
        query.setParameter(3, data.get("dest_ci"));
        query.executeUpdate();
    }

    @Override
    public void deleteT2gByList(Map<String, Object> data, boolean isAzimuth) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ")
                .append(isAzimuth ? "mes_t2g_sleep_azimuth"
                        : "mes_t2g_sleep_mr")
                .append(" where src_lac=? and src_lcid=? and dest_lac=? and dest_ci=? ");
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setParameter(0, data.get("src_lac"));
        query.setParameter(1, data.get("src_lcid"));
        query.setParameter(2, data.get("dest_lac"));
        query.setParameter(3, data.get("dest_ci"));
        query.executeUpdate();
    }

    @Override
    public void delMakeUpConflictInT2GByG2G(boolean g2gAzimuth, boolean t2gAzimuth) {
        StringBuilder s1 = new StringBuilder();
        String g2gWlb = g2gAzimuth ? "mes_g2g_sleep_azimuth" : "mes_g2g_sleep_mr";
        String t2gWlb = t2gAzimuth ? "mes_t2g_sleep_azimuth" : "mes_t2g_sleep_mr";
        s1.append("DELETE FROM ").append(t2gWlb);
        s1.append(" WHERE (dest_lac,dest_ci) IN (");
        s1.append("SELECT src_lac,src_ci");
        s1.append(" FROM ").append(g2gWlb);
        s1.append(" )");
        this.getSession().createSQLQuery(s1.toString())
                .executeUpdate();
    }

//	@Override
//	public void delMakeUpConflictInL2TByT2G(boolean t2gAzimuth) {
//		String t2gWlbm =t2gAzimuth? "mes_t2g_sleep_azimuth":"mes_t2g_sleep_mr";
//		StringBuilder s1 = new StringBuilder();
//		s1.append("DELETE FROM mes_l2t_sleep_azimuth ");
//		s1.append(" where (dest_lac,dest_lcid) IN (");
//		s1.append("SELECT src_lac,src_lcid FROM ").append(t2gWlbm).append(")");
//		this.getSession().createSQLQuery(s1.toString())
//		.executeUpdate();
//	}
//
//	@Override
//	public void delSleepConflictInT2GByL2T(boolean t2gAzimuth) {
//		// TODO Auto-generated method stub
//		StringBuilder s1 = new StringBuilder();
//		String t2gWlb = t2gAzimuth?"mes_t2g_sleep_azimuth":"mes_t2g_sleep_mr";
//		s1.append("DELETE FROM ").append(t2gWlb);
//		s1.append(" where (src_lac,src_lcid) IN (");
//		s1.append("SELECT dest_lac,dest_lcid FROM mes_l2t_sleep_azimuth )");
//		this.getSession().createSQLQuery(s1.toString())
//				.executeUpdate();
//	}
//
//	@Override
//	public void delSleepConflictInL2TByL2L(boolean l2lAzimuth) {
//		// TODO Auto-generated method stub
//		StringBuilder s1 = new StringBuilder();
//		String l2lWlbm = l2lAzimuth?"mes_l2l_sleep_azimuth":"mes_l2l_sleep_mr";
//		s1.append("DELETE FROM mes_l2t_sleep_azimuth WHERE ");
//		s1.append("  (src_enodebid,src_localcellid) IN (");
//		s1.append("SELECT dest_enodebid,dest_localcellid FROM  ").append(l2lWlbm).append(")");
//		this.getSession().createSQLQuery(s1.toString())
//				.executeUpdate();
//	}

    @Override
    public void delSleepConflictInG2GByT2G(boolean g2gAzimuth, boolean t2gAzimuth) {
        // TODO Auto-generated method stub
        StringBuilder s1 = new StringBuilder();
        String g2gWlb = g2gAzimuth ? "mes_g2g_sleep_azimuth" : "mes_g2g_sleep_mr";
        String t2gWlb = g2gAzimuth ? "mes_t2g_sleep_azimuth" : "mes_t2g_sleep_mr";
        s1.append("DELETE FROM ").append(g2gWlb).append(" where (src_lac,src_ci) IN (");
        s1.append("SELECT dest_lac,dest_ci FROM ").append(t2gWlb).append(")");
        this.getSession().createSQLQuery(s1.toString())
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> queryMetaData(String querySql) {
        return this.getSession().createSQLQuery(querySql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    @Override
    public void removeAllData(String resTable) {
        String sql = "truncate table " + resTable;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }


    @Override
    public List<Map<String, Integer>> queryScene() {
        String sql = "select busytype,base_overlay_degree from mes_zs_coverage";
        return this.getSession().createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }


    @Override
    public void deleteMakeUpBySleepForG2g(boolean g2gAzimuth) {
        String lsb = "g2g_" + System.currentTimeMillis();
        try {
            String wlbm = g2gAzimuth ? "mes_g2g_sleep_azimuth" : "mes_g2g_sleep_mr";
            StringBuilder cSql = new StringBuilder();
            cSql.append("create table ").append(lsb).append(" as select dest_lac,dest_ci from ").append(wlbm);
            this.getSession().createSQLQuery(cSql.toString()).executeUpdate();
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM ").append(wlbm).append(" where (src_lac,src_ci) in (select dest_lac,dest_ci from ")
                    .append(lsb).append(")");
            this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        } finally {
            this.delLsb(lsb);
        }
    }


    private void delLsb(String lsb) {
        StringBuilder sql = new StringBuilder();
        sql.append("drop table if exists ").append(lsb);
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
    }


    @Override
    public void deleteMakeUpBySleepForL2l(boolean l2lAzimuth) {
        //一补一补偿小区临时表
        String lsb = "l2l_" + System.currentTimeMillis();
        //多补一补偿小区临时表
        String manyLsb = "l2lMany_" + System.currentTimeMillis();
        //创建多补一临时表
        StringBuilder manyCopySql = new StringBuilder();
        manyCopySql.append("create table ").append(manyLsb).append(" as select dest_enodebid,dest_localcellid from mes_l2l_sleep_many");
        //多补一去除语句
        StringBuilder manyDelSql = new StringBuilder();
        manyDelSql.append("DELETE FROM mes_l2l_sleep_many where (src_enodebid,src_localcellid) in (select dest_enodebid,dest_localcellid from ").append(manyLsb).append(")");
        //多补一休眠去除一补一补偿的小区
        StringBuilder manyDelSql2 = new StringBuilder();
        manyDelSql2.append("DELETE FROM mes_l2l_sleep_many where (src_enodebid,src_localcellid) in (select dest_enodebid,dest_localcellid from ").append(lsb).append(")");
        try {
            //一对一优先
            String wlbm = l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr";
            StringBuilder cSql = new StringBuilder();
            cSql.append("create table ").append(lsb).append(" as select dest_enodebid,dest_localcellid from ").append(wlbm);
            this.getSession().createSQLQuery(cSql.toString()).executeUpdate();
            StringBuilder sql = new StringBuilder();
            //一对一的休眠小区去除作为补偿小区的数据
            sql.append("DELETE FROM ").append(wlbm).append(" where (src_enodebid,src_localcellid) in (select dest_enodebid,dest_localcellid from ")
                    .append(lsb).append(")");
            this.getSession().createSQLQuery(sql.toString()).executeUpdate();
            //多对一处理
            this.getSession().createSQLQuery(manyCopySql.toString()).executeUpdate();
            //多对一休眠小区去除作为补偿小区的数据
            this.getSession().createSQLQuery(manyDelSql.toString()).executeUpdate();
            //多对一休眠小区去除作为一对一的补偿小区数据
            this.getSession().createSQLQuery(manyDelSql2.toString()).executeUpdate();

        } finally {
            this.delLsb(lsb);
            this.delLsb(manyLsb);
        }

    }


    @Override
    public Map<String, Double> querySleepNotifyDic() {
        String sql = "select threshold_name,threshold_value from mes_sleep_threshold_dic";
        @SuppressWarnings("unchecked")
        List<Map<String, String>> dataList = this.getSession().createSQLQuery(sql)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        Map<String, Double> res = new HashMap<String, Double>();
        for (Map<String, String> data : dataList) {
            res.put(data.get("threshold_name"), FormatUtil.tranferStrToNum(data.get("threshold_value")));
        }
        return res;
    }

    @Override
    public int queryERLangB(double dest_hwl) {
        String sql = "select tchn from erlangb where traffic>" + dest_hwl + " order by tchn asc limit 1";
        List<Map<String, Object>> list = this.getSession()
                .createSQLQuery(sql)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (list.isEmpty()) return 0;
        return (int) FormatUtil.tranferCalValue(list.get(0).get("tchn"));
    }


    /*******************Neusoft**********************/
    /**
     * 筛选出LTE小区同时补偿LTE,TD及多补一中LTE情况的小区
     */
    @Override
    public Map<String, List<Map<String, Object>>> queryMakeUpLteAndTd(boolean t2lAzimuth, boolean l2lAzimuth, int tdPriority, int ltePriority) {
        StringBuilder l2lSql = new StringBuilder();
        StringBuilder t2lSql = new StringBuilder();
        StringBuilder l2lManySql = new StringBuilder();
        //筛选出l2l情况中LTE小区同时补偿LTE,TD情况的LTE小区，及多补一中的LTE补偿小区
        l2lSql.append("select l2l.src_enodebid,l2l.src_localcellid,l2l.dest_enodebid,l2l.dest_localcellid,")
                .append("srckpi.sxsjll src_sxsjll,srckpi.xxsjll src_xxsjll,srckpi.zdyhs src_zdyhs,")
                .append("destkpi.sxsjll dest_sxsjll,destkpi.xxsjll dest_xxsjll,destkpi.zdyhs dest_zdyhs, 'l2l' bus_type ")
                .append("from ")
                .append(l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr")
                .append(" l2l join mes_lte_kpi srckpi on srckpi.enodebid=l2l.src_enodebid  and srckpi.cellid=l2l.src_localcellid")
                .append(" join mes_lte_kpi destkpi on destkpi.enodebid=l2l.dest_enodebid  and destkpi.cellid=l2l.dest_localcellid")
                .append(" where (l2l.dest_enodebid,l2l.dest_localcellid) in ")
                .append("( select distinct l2l.dest_enodebid,l2l.dest_localcellid")
                .append(" from ")
                .append(l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr")
                .append(" l2l join ")
                .append(t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr")
                .append(" t2l on l2l.dest_enodebid=t2l.dest_enodebid and l2l.dest_localcellid=t2l.dest_localcellid")
                .append(" union all ")
                .append(" select distinct l2l.dest_enodebid,l2l.dest_localcellid")
                .append(" from ").append(l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr").append(" l2l ")
                .append(" join mes_l2l_sleep_many l2ml on l2l.dest_enodebid=l2ml.dest_enodebid and l2l.dest_localcellid=l2ml.dest_localcellid)");
        //筛选出t2l情况中LTE小区同时补偿LTE,TD情况的LTE小区，及多补一中的LTE补偿小区
        t2lSql.append("select t2l.src_rnc,t2l.src_lcid,t2l.src_lac,t2l.dest_enodebid,t2l.dest_localcellid,'t2l' bus_type, ")
                .append("srckpi.zdyhs src_zdyhs, srckpi.hzbpzs src_hzbpzs, srckpi.mzylyl src_mzylyl, srckpi.sjll src_sjll, srckpi.yyyw src_yyyw, srckpi.dyhsl src_dyhsl,")
                .append("destkpi.sxsjll dest_sxsjll,destkpi.xxsjll dest_xxsjll,destkpi.zdyhs dest_zdyhs ")
                .append(" from ")
                .append(t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr")
                .append(" t2l join mes_td_kpi srckpi on srckpi.rnc=t2l.src_rnc  and srckpi.lcid=t2l.src_lcid ")
                .append(" join mes_lte_kpi destkpi on destkpi.enodebid=t2l.dest_enodebid  and destkpi.cellid=t2l.dest_localcellid")
                .append(" where (t2l.dest_enodebid,t2l.dest_localcellid) in ")
                .append("( select distinct t2l.dest_enodebid,t2l.dest_localcellid")
                .append(" from ")
                .append(l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr")
                .append(" l2l join ")
                .append(t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr")
                .append(" t2l on l2l.dest_enodebid=t2l.dest_enodebid and l2l.dest_localcellid=t2l.dest_localcellid")
                .append(" union all ")
                .append(" select distinct t2l.dest_enodebid,t2l.dest_localcellid")
                .append(" from ").append(t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr").append(" t2l ")
                .append(" join mes_l2l_sleep_many l2ml on t2l.dest_enodebid=l2ml.dest_enodebid and t2l.dest_localcellid=l2ml.dest_localcellid)");
        //筛选出l2l多补一的数据
        l2lManySql.append("select l2ml.src_enodebid,l2ml.src_localcellid,l2ml.dest_enodebid,l2ml.dest_localcellid,")
                .append("srckpi.sxsjll src_sxsjll,srckpi.xxsjll src_xxsjll,srckpi.zdyhs src_zdyhs,")
                .append("destkpi.sxsjll dest_sxsjll,destkpi.xxsjll dest_xxsjll,destkpi.zdyhs dest_zdyhs, 'l2lMany' bus_type ")
                .append("from ")
                .append("mes_l2l_sleep_many")
                .append(" l2ml join mes_lte_kpi srckpi on srckpi.enodebid=l2ml.src_enodebid  and srckpi.cellid=l2ml.src_localcellid")
                .append(" join mes_lte_kpi destkpi on destkpi.enodebid=l2ml.dest_enodebid  and destkpi.cellid=l2ml.dest_localcellid")
                .append(" where (l2ml.dest_enodebid,l2ml.dest_localcellid) in ")
                .append("( select distinct t2l.dest_enodebid,t2l.dest_localcellid")
                .append(" from ").append(t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr").append(" t2l ")
                .append(" join mes_l2l_sleep_many l2ml on t2l.dest_enodebid=l2ml.dest_enodebid and t2l.dest_localcellid=l2ml.dest_localcellid")
                .append(" union all ")
                .append(" select distinct l2l.dest_enodebid,l2l.dest_localcellid")
                .append(" from ").append(l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr").append(" l2l ")
                .append(" join mes_l2l_sleep_many l2ml on l2l.dest_enodebid=l2ml.dest_enodebid and l2l.dest_localcellid=l2ml.dest_localcellid)");

        List<Map<String, Object>> l2lDataList = this.getSession().createSQLQuery(l2lSql.toString())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        List<Map<String, Object>> t2lDataList = this.getSession().createSQLQuery(t2lSql.toString())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        List<Map<String, Object>> l2lManyDataList = this.getSession().createSQLQuery(l2lManySql.toString())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        Map<String, List<Map<String, Object>>> resMap = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        if (tdPriority < ltePriority) {
            resList.addAll(t2lDataList);
            resList.addAll(l2lDataList);
        } else {
            resList.addAll(l2lDataList);
            resList.addAll(t2lDataList);
        }
        //先处理一补一数据后，再处理多补一数据
        resList.addAll(l2lManyDataList);

        //将数据集合按相同邻区分组
        for (Map<String, Object> data : resList) {
            //Lte邻区key
            String key = String.valueOf(data.get("dest_enodebid") + "_" + data.get("dest_localcellid"));
            if (!resMap.containsKey(key)) {
                resMap.put(key, new ArrayList<Map<String, Object>>());
            }
            resMap.get(key).add(data);
        }
        return resMap;
    }

    @Override
    public void delSleepConflictInL2LByT2L(boolean l2lAzimuth,
                                           boolean t2lAzimuth) {
        //一补一中，TD优先的场合，删除l2l休眠小区有在t2l中作为补偿小区的数据
        StringBuilder s1 = new StringBuilder();
        String l2lWlb = l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr";
        String t2lWlb = t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr";
        s1.append("DELETE FROM ").append(l2lWlb).append(" where (src_enodebid,src_localcellid) IN (");
        s1.append("SELECT dest_enodebid,dest_localcellid FROM ").append(t2lWlb).append(")");
        this.getSession().createSQLQuery(s1.toString()).executeUpdate();
        //多补一中
        StringBuilder s2 = new StringBuilder();
        //TD优先的场合，删除多补一l2l休眠小区有在t2l中作为补偿小区的数据
        s2.append("DELETE FROM mes_l2l_sleep_many where (src_enodebid,src_localcellid) IN (");
        s2.append("SELECT dest_enodebid,dest_localcellid FROM ").append(t2lWlb).append(")");
        this.getSession().createSQLQuery(s2.toString()).executeUpdate();
    }


    @Override
    public void delMakeUpConflictInT2LByL2L(boolean t2lAzimuth,
                                            boolean l2lAzimuth) {
        //一补一中，LTE优先的场合，删除t2l休眠小区有在l2l中作为补偿小区的数据
        StringBuilder s1 = new StringBuilder();
        String l2lWlb = l2lAzimuth ? "mes_l2l_sleep_azimuth" : "mes_l2l_sleep_mr";
        String t2lWlb = t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr";
        s1.append("DELETE FROM ").append(t2lWlb).append(" where (dest_enodebid,dest_localcellid) IN (");
        s1.append("SELECT src_enodebid,src_localcellid FROM ").append(l2lWlb).append(")");
        this.getSession().createSQLQuery(s1.toString()).executeUpdate();
        //多补一中
        StringBuilder s2 = new StringBuilder();
        //一补一优先，删除多补一l2l休眠小区有在t2l中作为补偿小区的数据
        s2.append("DELETE FROM mes_l2l_sleep_many where (src_enodebid,src_localcellid) IN (");
        s2.append("SELECT dest_enodebid,dest_localcellid FROM ").append(t2lWlb).append(")");
        this.getSession().createSQLQuery(s2.toString()).executeUpdate();

    }


    @Override
    public void deleteT2lByList(Map<String, Object> data, boolean isAzimuth) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ")
                .append(isAzimuth ? "mes_t2l_sleep_azimuth"
                        : "mes_t2l_sleep_mr")
                .append(" where src_lac=? and src_lcid=? and dest_enodebid=? and dest_localcellid=? ");
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setParameter(0, data.get("src_lac"));
        query.setParameter(1, data.get("src_lcid"));
        query.setParameter(2, data.get("dest_enodebid"));
        query.setParameter(3, data.get("dest_localcellid"));
        query.executeUpdate();
    }


    @Override
    public void deleteL2lByList(Map<String, Object> data, boolean isAzimuth) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ")
                .append(isAzimuth ? "mes_l2l_sleep_azimuth"
                        : "mes_l2l_sleep_mr")
                .append(" where src_enodebid=? and src_localcellid=? and dest_enodebid=? and dest_localcellid=? ");
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setParameter(0, data.get("src_enodebid"));
        query.setParameter(1, data.get("src_localcellid"));
        query.setParameter(2, data.get("dest_enodebid"));
        query.setParameter(3, data.get("dest_localcellid"));
        query.executeUpdate();
    }

    @Override
    public void deleteL2lManyByList(Map<String, Object> data) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM mes_l2l_sleep_many where src_enodebid=? and src_localcellid=?");
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setParameter(0, data.get("src_enodebid"));
        query.setParameter(1, data.get("src_localcellid"));
        query.executeUpdate();
    }

    @Override
    public void deleteMakeUpBySleepForT2T() {
        String lsb = "t2t_" + System.currentTimeMillis();
        try {
            String wlbm = "mes_t2t_sleep_many";
            StringBuilder cSql = new StringBuilder();
            cSql.append("create table ").append(lsb).append(" as select dest_lac,dest_lcid from ").append(wlbm);
            this.getSession().createSQLQuery(cSql.toString()).executeUpdate();
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM ").append(wlbm).append(" where (src_lac,src_lcid) in (select dest_lac,dest_lcid from ")
                    .append(lsb).append(")");
            this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        } finally {
            this.delLsb(lsb);
        }
    }

    /**
     * T2T多补一的补偿小区中去除已经在T2G、T2L情况作为休眠小区的TD小区
     *
     * @param t2lAzimuth
     * @param l2lAzimuth
     */
    @Override
    public void delMakeUpConflictInT2TByT2GT2L(boolean t2gAzimuth, boolean t2lAzimuth) {
        String t2gWlb = t2gAzimuth ? "mes_t2g_sleep_azimuth" : "mes_t2g_sleep_mr";
        String t2lWlb = t2lAzimuth ? "mes_t2l_sleep_azimuth" : "mes_t2l_sleep_mr";
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM mes_t2t_sleep_many")
                .append(" where (src_rnc,src_lcid) in (select a.src_rnc,a.src_lcid from (")
                .append("select src_rnc,src_lcid from mes_t2t_sleep_many")
                .append(" WHERE (dest_rnc,dest_lcid) in (SELECT src_rnc,src_lcid from ").append(t2gWlb).append(")")
                .append(" GROUP BY src_rnc,src_lcid) a)");

        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM mes_t2t_sleep_many")
                .append(" where (src_rnc,src_lcid) in (select a.src_rnc,a.src_lcid from (")
                .append("select src_rnc,src_lcid from mes_t2t_sleep_many")
                .append(" WHERE (dest_rnc,dest_lcid) in (SELECT src_rnc,src_lcid from ").append(t2lWlb).append(")")
                .append(" GROUP BY src_rnc,src_lcid) a)");
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();

    }
}
