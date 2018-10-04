package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.services.impl.SEBizServiceImpl;
import com.tuoming.mes.strategy.dao.BeforeAfterDao;
import com.tuoming.mes.strategy.model.BeforeAndAfterSetting;
import com.tuoming.mes.strategy.util.FormatUtil;

@Component("beforeAfterDao")
public class BeforeAfterDaoImpl extends
        AbstractBaseDao<BeforeAndAfterSetting, Integer> implements
        BeforeAfterDao {
    private static final Logger logger = LogFacade.getLog4j(SEBizServiceImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<BeforeAndAfterSetting> querySetting(String groupName) {
        String hql = "";
        if (StringUtils.isEmpty(groupName)) {
            hql += HQL_LIST_ALL + " where 1=1 and enabled=1";
        } else {
            hql += HQL_LIST_ALL + " where enabled=1 and groupname = '"
                    + groupName + "'";
        }
        Query query = this.getSession().createQuery(hql);
        return query.list();
    }

    // String table, String exetype
    @Override
    public void updateSql(String sql) {
        // TODO Auto-generated method stub
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void createTableForCarrier(BeforeAndAfterSetting set) {
        // TODO Auto-generated method stub
        String sql = "create table " + set.getTablename() + " as "
                + set.getExecutesql();
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void deleteResultTable(String table) {
        // TODO Auto-generated method stub
        String sql = "DROP TABLE IF EXISTS " + table;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void deleteTableForPm(String exeSql, String tableName,
                                 String timeColumn, String time) {
        // TODO Auto-generated method stub
        String sql = exeSql.replace("$TABLENAME$", tableName).replace(
                "$TIMECOLUMN$", timeColumn).replace("$TIME$", time);
        logger.info("删除历史表sql:" + sql);
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void deleteTableForCm(String exeSql, String tableName) {
        // TODO Auto-generated method stub
        String sql = exeSql.replace("$TABLENAME$", tableName);
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void updateData(String table) {
        String sql = "truncate table  " + table;
        this.getSession().createSQLQuery(sql).executeUpdate();
        String sql2 = "insert into  " + table + "  select * from " + table + "_bak";
        this.getSession().createSQLQuery(sql2).executeUpdate();
    }

    @Override
    public int queryDataCount(String table) {
        String sql = "select count(1) num from " + table;
        List<Map<String, Object>> list = this.getSession()
                .createSQLQuery(sql)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
        if (list.isEmpty()) return 0;
        return (int) FormatUtil.tranferCalValue(list.get(0).get("num"));
    }

    @Override
    public void insertDataForCarrier(BeforeAndAfterSetting set) {
        String sql = "insert into  " + set.getTablename() + " "
                + set.getExecutesql();
        this.getSession().createSQLQuery(sql).executeUpdate();

    }

    @Override
    public void removePmData(String table) {
        String sql = "insert into " + table + "_his select * from " + table;
        this.getSession().createSQLQuery(sql).executeUpdate();
        String sql2 = "truncate table " + table;
        this.getSession().createSQLQuery(sql2).executeUpdate();
    }

    @Override
    public void removeData(String bakTabelName) {
        String sql2 = "truncate table " + bakTabelName;
        this.getSession().createSQLQuery(sql2).executeUpdate();

    }

}
