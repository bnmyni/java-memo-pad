package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.FcastNextDataDao;
import com.tuoming.mes.strategy.model.FcastNextIntervalSetting;

@Repository("fcastNextDataDao")
public class FcastNextDataDaoImpl extends
        AbstractBaseDao<FcastNextIntervalSetting, Integer> implements FcastNextDataDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<FcastNextIntervalSetting> queryForecastNextSet(String groupName) {
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

    public void removeResTable(String tableName) {
        // TODO Auto-generated method stub
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("truncate TABLE   ").append(tableName);
            this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("删除表时，没找到目标表。。。");
        }
    }

    // 创建预测结果表
    public void createResTable(String resTable, String querySql,
                               Date collectTime) {
        String sql = "insert into " + resTable + "  " + querySql;
        this.getSession().createSQLQuery(sql).setTimestamp(0, collectTime).executeUpdate();
    }
}
