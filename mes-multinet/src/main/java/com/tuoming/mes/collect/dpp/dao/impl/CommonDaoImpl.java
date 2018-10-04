/*******************************************************************************
 * Copyright (c) 2013.  Pyrlong All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tuoming.mes.collect.dpp.dao.impl;


import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.dao.CommonDao;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;

@Component("CommonDao")
public class CommonDaoImpl implements CommonDao {

    private static Logger logger = LogFacade.getLog4j(CommonDao.class);

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public <T> T save(T model) {
        getSession().save(model);
        return model;
    }

    public <T> void saveOrUpdate(T model) {
        getSession().saveOrUpdate(model);

    }

    public <T> void update(T model) {
        getSession().update(model);
    }

    public <T> void merge(T model) {
        getSession().merge(model);
    }

    public <T, PK extends Serializable> void delete(Class<T> entityClass, PK id) {
        getSession().delete(get(entityClass, id));
    }

    public <T> void deleteObject(T model) {
        getSession().delete(model);
    }

    public <T, PK extends Serializable> T get(Class<T> entityClass, PK id) {
        return (T) getSession().get(entityClass, id);

    }

    public <T> int countAll(Class<T> entityClass) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> listAll(Class<T> entityClass, String orderby, String filter) {
        String hql = "from " + entityClass.getSimpleName() + orderby + filter;
        Query query = getSession().createQuery(hql);
        List<T> results = query.list();
        return results;
    }

    @Override
    public DataTable queryTable(String dbName, String sql) throws Exception {
        return DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter().queryTable(sql);
    }


}
