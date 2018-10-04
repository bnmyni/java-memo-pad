package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.NotifyDao;
import com.tuoming.mes.strategy.model.NotifyModel;

@Repository("notifyDao")
public class NotifyDaoImpl extends AbstractBaseDao<NotifyModel, Integer> implements NotifyDao {

    @Override
    public List<NotifyModel> querySetList(String groupName) {
        StringBuilder sql = new StringBuilder();
        sql.append(HQL_LIST_ALL).append(" WHERE enabled=1");
        if (!StringUtils.isEmpty(groupName)) {
            sql.append(" and group = '").append(groupName).append("'");
        }
        return this.getSession().createQuery(sql.toString()).list();
    }

    public List<Map<String, Object>> queryDataList(String querySql, Date collDate) {
        int count = StringUtil.matchCount(querySql, "\\?");
        SQLQuery query = this.getSession().createSQLQuery(querySql);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (int i = 0; i < count; i++) {
            query.setTimestamp(i, collDate);
        }
        return query.list();
    }

}
