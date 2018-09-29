package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.OverlayDegreeDao;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;

/**
 * 计算重叠覆盖度dao层实现
 *
 * @author Administrator
 */
@Repository("overlayDegreeDao")
public class OverlayDegreeDaoImpl extends AbstractBaseDao<OverlayDegreeSetting, Integer> implements OverlayDegreeDao {

    public List<OverlayDegreeSetting> queryCalConByGroup(String groupName) {
        String hql = StringUtils.isEmpty(groupName) ? HQL_LIST_ALL + " where enabled=1 order by id" : HQL_LIST_ALL
                + " where group =? and enabled=1 order by id";
        Query query = this.getSession().createQuery(hql);
        if (StringUtils.isNotEmpty(groupName)) {
            setParameters(query, new Object[]{groupName});
        }
        return query.list();
    }

    /**
     * 查询要计算的一补一重叠覆盖度的配置
     */
    public List<OverlayDegreeSetting> queryCalConBySingle() {
        String hql = HQL_LIST_ALL + " where group <> ? and enabled=1 order by id";
        Query query = this.getSession().createQuery(hql);
        setParameters(query, new Object[]{"MANY"});
        return query.list();
    }

    /**
     * 查询要计算的多补一重叠覆盖度的配置
     */
    public List<OverlayDegreeSetting> queryCalConByMany() {
        String hql = HQL_LIST_ALL + " where group =? and enabled=1 order by id";
        Query query = this.getSession().createQuery(hql);
        setParameters(query, new Object[]{"MANY"});
        return query.list();
    }

    public List<Map<String, Object>> queryMetaData(String lsbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM  ").append(lsbm).append(" order by null ");
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    public void createLsb(String lsbm, String querySql) {
        String sql = "CREATE TABLE " + lsbm + " AS " + querySql;
        SQLQuery query = this.getSession().createSQLQuery(sql);
        query.executeUpdate();
    }

    @Override
    public int getTotalCount(String lsbm) {
        String sql = "SELECT COUNT(1)  FROM " + lsbm;
        SQLQuery query = this.getSession().createSQLQuery(sql);
        Object obj = query.uniqueResult();
        if (obj == null) {
            return 0;
        }
        return ((BigInteger) query.uniqueResult()).intValue();
    }

    @Override
    public List<Map<String, Object>> queryMetaData(String lsbm, int startIndex,
                                                   int num) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM  ").append(lsbm).append(" order  by null limit ").append(startIndex)
                .append(",").append(num);
        SQLQuery query = this.getSession().createSQLQuery(sql.toString());
        query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    public void removeTable(String lsbm) {
        String sql = "DROP TABLE IF EXISTS " + lsbm;
        SQLQuery query = this.getSession().createSQLQuery(sql);
        query.executeUpdate();
    }

    /**
     * 创建结果表
     */
    public void createRstTable(String createSql) {
        if (StringUtils.isEmpty(createSql)) {
            return;
        }
        String[] sqlArr = createSql.split("#");
        for (String sql : sqlArr) {
            if (StringUtils.isEmpty(sql)) {
                continue;
            }
            this.getSession().createSQLQuery(sql).executeUpdate();
        }

    }

    /**
     * 根据场景配置表更新重叠覆盖度计算开关
     */
    public void updateDegreeSetting() {
        String sql = "update mes_over_setting set enabled=0";
        String sql2 = "update mes_over_setting set enabled=1 where bus_type in (select busytype from mes_zs_coverage where enabled=1) or bus_type = 't2t' or groupname = 'MANY' ";
        this.getSession().createSQLQuery(sql).executeUpdate();
        this.getSession().createSQLQuery(sql2).executeUpdate();
    }

    /**
     * 清除表
     */
    public void removeAllTable(String tab) {
        String sql = "truncate table " + tab;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    /**
     * 华为Lte所有邻区可被补偿的MRO采集点汇总去重后的采集点数量
     */
    public int queryTrueLteCellCoint(String querySql) {
        String sql = "SELECT COUNT(a.1)  FROM (SELECT 1  FROM rst_pm_l2l_hw_nc_info where " + querySql + ") group by mmeUeS1apId,timeStamp,objectId) a";
        SQLQuery query = this.getSession().createSQLQuery(sql);
        Object obj = query.uniqueResult();
        if (obj == null) {
            return 0;
        }
        return ((BigInteger) query.uniqueResult()).intValue();

    }

    /**
     * 华为TD所有邻区可被补偿的MRO采集点汇总去重后的采集点数量
     */
    public int queryTrueTdCellCoint(String querySql) {
        String sql = "SELECT COUNT(a.1)  FROM (SELECT 1  FROM rst_pm_t2t_hw_nc_info where " + querySql + ") group by cellid,timeStamp,imsi) a";
        SQLQuery query = this.getSession().createSQLQuery(sql);
        Object obj = query.uniqueResult();
        if (obj == null) {
            return 0;
        }
        return ((BigInteger) query.uniqueResult()).intValue();

    }
}
