/**
 * Copyright (c) 2013.  Pyrlong All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package com.tuoming.mes.collect.dpp.dao.impl;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.Id;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.Assert;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.ConditionQuery;
import com.tuoming.mes.collect.dpp.dao.OrderBy;

/**
 * @author James Cheung
 */
public abstract class AbstractBaseDao<M extends java.io.Serializable, PK extends java.io.Serializable> implements BaseDao<M, PK> {

    protected static final Logger LOGGER = LogFacade.getLog4j(AbstractBaseDao.class);
    protected final String HQL_LIST_ALL;
    private final Class<M> entityClass;
    private final String HQL_COUNT_ALL;
    private final String HQL_OPTIMIZE_PRE_LIST_ALL;
    private final String HQL_OPTIMIZE_NEXT_LIST_ALL;
    private String pkName = null;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;


    @SuppressWarnings("unchecked")
    public AbstractBaseDao() {
        //getGenericSuperclass() 通过反射获取当前类表示的实体（类，接口，基本类型或void）的直接父类的Type
        //getActualTypeArguments()返回参数数组
        this.entityClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Field[] fields = this.entityClass.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                this.pkName = f.getName();
            }
        }
        if (StringUtil.isEmpty(pkName)) {
            fields = this.entityClass.getSuperclass().getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(Id.class)) {
                    this.pkName = f.getName();
                }
            }
        }
        Assert.notNull(pkName);
        HQL_LIST_ALL = "from " + this.entityClass.getSimpleName();
        HQL_OPTIMIZE_PRE_LIST_ALL = "from " + this.entityClass.getSimpleName() + " where " + pkName + " > ? order by " + pkName + " asc";
        HQL_OPTIMIZE_NEXT_LIST_ALL = "from " + this.entityClass.getSimpleName() + " where " + pkName + " < ? order by " + pkName + " desc";
        HQL_COUNT_ALL = " select count(*) from " + this.entityClass.getSimpleName();
    }

    public Session getSession() {
        // 事务必须是开启的，否则获取不到
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PK save(M model) {
        return (PK) getSession().save(model);
    }

    @Override
    public void saveOrUpdate(M model) {
        getSession().saveOrUpdate(model);
    }

    @Override
    public void saveAll(List<M> modelList) {
        Session session;
        if (modelList != null && modelList.size() > 0) {
            session = sessionFactory.openSession(); // 获取Session
            Transaction tx = session.getTransaction(); // 开启事物
            try {
                tx.begin();
                for (M m : modelList) {
                    session.save(m);
                }
                session.flush();
                session.clear();
                tx.commit(); // 提交事物
            } catch (Exception e) {
                e.printStackTrace(); // 打印错误信息
                tx.rollback(); // 出错将回滚事物
            } finally {
                session.close();
            }
        }
    }

    @Override
    public void update(M model) {
        getSession().update(model);
    }

    @Override
    public void merge(M model) {
        getSession().merge(model);
    }

    @Override
    public void delete(PK id) {
        getSession().delete(this.get(id));
    }

    @Override
    public void deleteObject(M model) {
        getSession().delete(model);
    }

    @Override
    public boolean exists(PK id) {
        return get(id) != null;
    }

    @Override
    public M get(PK id) {
        return (M) getSession().get(this.entityClass, id);
    }

    @Override
    public int countAll() {
        Long total = aggregate(HQL_COUNT_ALL);
        return total.intValue();
    }

    @Override
    public List<M> listAll() {
        return list(HQL_LIST_ALL);
    }

    @Override
    public List<M> listAll(int pn, int pageSize) {
        return listPage(HQL_LIST_ALL, pn, pageSize);
    }

    @Override
    public List<M> pre(PK pk, int pn, int pageSize) {
        if (pk == null) {
            return list(HQL_LIST_ALL, pn, pageSize);
        }
        // 倒序，重排
        List<M> result = listPage(HQL_OPTIMIZE_PRE_LIST_ALL, 1, pageSize, pk);
        Collections.reverse(result);
        return result;
    }

    @Override
    public List<M> next(PK pk, int pn, int pageSize) {
        if (pk == null) {
            return list(HQL_LIST_ALL, pn, pageSize);
        }
        return listPage(HQL_OPTIMIZE_NEXT_LIST_ALL, 1, pageSize, pk);
    }

    @Override
    public void flush() {
        getSession().flush();
    }

    @Override
    public void clear() {
        getSession().clear();
    }

    protected long getIdResult(String hql, Object... paramlist) {
        long result = -1;
        List<?> list = list(hql, paramlist);
        if (list != null && list.size() > 0) {
            return ((Number) list.get(0)).longValue();
        }
        return result;
    }

    protected List<M> listSelf(final String hql, final int pn, final int pageSize, final Object... paramlist) {
        return this.<M>listPage(hql, pn, pageSize, paramlist);
    }

    /**
     * for in
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> listWithIn(final String hql, final int start, final int length, final Map<String, Collection<?>> map, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);
        for (Entry<String, Collection<?>> e : map.entrySet()) {
            query.setParameterList(e.getKey(), e.getValue());
        }
        if (start > -1 && length > -1) {
            query.setMaxResults(length);
            if (start != 0) {
                query.setFirstResult(start);
            }
        }
        List<T> results = query.list();
        return results;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> listPage(final String hql, final int pn, final int pageSize, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);
        List<T> results = query.list();
        return results;
    }

    /**
     * 根据查询条件返回唯一一条记录
     */
    @SuppressWarnings("unchecked")
    protected <T> T unique(final String hql, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);
        return (T) query.setMaxResults(1).uniqueResult();
    }

    /**
     * for in
     */
    @SuppressWarnings("unchecked")
    protected <T> T aggregate(final String hql, final Map<String, Collection<?>> map, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        if (paramlist != null) {
            setParameters(query, paramlist);
            for (Entry<String, Collection<?>> e : map.entrySet()) {
                query.setParameterList(e.getKey(), e.getValue());
            }
        }

        return (T) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    protected <T> T aggregate(final String hql, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);

        return (T) query.uniqueResult();

    }

    /**
     * 执行批处理语句.如 之间insert, update, delete 等.
     */
    protected int execteBulk(final String hql, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);
        Object result = query.executeUpdate();
        return result == null ? 0 : ((Integer) result).intValue();
    }

    protected int execteNativeBulk(final String natvieSQL, final Object... paramlist) {
        Query query = getSession().createSQLQuery(natvieSQL);
        setParameters(query, paramlist);
        Object result = query.executeUpdate();
        return result == null ? 0 : ((Integer) result).intValue();
    }

    protected <T> List<T> list(final String sql, final Object... paramlist) {
        return listPage(sql, -1, -1, paramlist);
    }

    @SuppressWarnings("unchecked")
    protected <T> T aggregateByNative(final String natvieSQL, final List<Entry<String, Type>> scalarList, final Object... paramlist) {
        SQLQuery query = getSession().createSQLQuery(natvieSQL);
        if (scalarList != null) {
            for (Entry<String, Type> entity : scalarList) {
                query.addScalar(entity.getKey(), entity.getValue());
            }
        }
        setParameters(query, paramlist);
        Object result = query.uniqueResult();
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(ConditionQuery query, OrderBy orderBy, final int pn, final int pageSize) {
        Criteria criteria = getSession().createCriteria(this.entityClass);
        query.build(criteria);
        orderBy.build(criteria);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(Criteria criteria) {
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public <T> T unique(Criteria criteria) {
        return (T) criteria.uniqueResult();
    }

    public <T> List<T> list(DetachedCriteria criteria) {
        return list(criteria.getExecutableCriteria(getSession()));
    }

    @SuppressWarnings("unchecked")
    public <T> T unique(DetachedCriteria criteria) {
        return (T) unique(criteria.getExecutableCriteria(getSession()));
    }

    protected void setParameters(Query query, Object[] paramlist) {
        if (paramlist != null) {
            for (int i = 0; i < paramlist.length; i++) {
                if (paramlist[i] instanceof Date) {
                    // TODO 难道这是bug 使用setParameter不行？？
                    query.setTimestamp(i, (Date) paramlist[i]);
                } else {
                    query.setParameter(i, paramlist[i]);
                }
            }
        }
    }

    /**
     * 根据查询条件返回唯一一条记录
     */
    @SuppressWarnings("unchecked")
    protected <T> T getObject(final String hql, final Object... paramlist) {
        Query query = getSession().createQuery(hql);
        setParameters(query, paramlist);
        List<T> results = query.list();

        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }


}
