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

import java.util.List;
import java.util.Map;
import javax.script.ScriptException;
import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.scripts.AbstractEngine;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.BaseService;

public abstract class AbstractBaseService<M extends java.io.Serializable, PK extends java.io.Serializable> implements BaseService<M, PK> {

    private static Logger logger = LogFacade.getLog4j(AbstractBaseService.class);
    protected BaseDao<M, PK> baseDao;
    Map<String, String> envs = Envirment.getEnvs();

    public static Map<String, String> mergerMap(Map map1, Map<String, String> map2) {
        return Envirment.mergerMap(map1, map2);
    }

    public static void updateEnv(Object o, Map<String, String> newMap) {
        Envirment.updateEnv(o, newMap);
    }

    public Map<String, String> getEnvCopy() {
        return Maps.newHashMap(envs);
    }

    public void updateEnv(String customEnv, Map<String, String> newMap) {
        try {
            if (StringUtil.isEmpty(customEnv))
                return;
            Map<String, String> result = (Map<String, String>) DSLUtil.getDefaultInstance().compute(customEnv);
            newMap = Envirment.mergerMap(result, newMap);
            logger.info("updateEnv to count:" + newMap.size());
        } catch (Exception ex) {
            logger.warn("CustomEnv must config like : \"['Bob' : 'BobValue', 'Michael' : 'MichaelValue']\"");
        }
    }

    public void doAction(String action, Map<String, String> newMap) {
        if (StringUtil.isEmpty(action))
            return;
        String exp = DSLUtil.getDefaultInstance().buildString(action, newMap);
        logger.debug("Do action :" + exp);
        if (exp.startsWith(">>>")) {
            try {
                AbstractEngine pythonEngine = AbstractEngine.getEngine("python");
                pythonEngine.eval(exp.substring(3));
            } catch (ScriptException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            DSLUtil.getDefaultInstance().compute(exp, newMap);
        }
    }

    public void setEnv(String name, String value, Map<String, String> newMap) {
        if (newMap.containsKey(name))
            newMap.remove(name);
        newMap.put(name, value);
    }

    public void setEnv(Map<String, String> envs) {
        this.envs = envs;
    }

    @Override
    public M save(M model) {
        baseDao.save(model);
        return model;
    }

    @Override
    public void merge(M model) {
        baseDao.merge(model);
    }

    @Override
    public void saveOrUpdate(M model) {
        baseDao.saveOrUpdate(model);
    }

    @Override
    public void update(M model) {
        baseDao.update(model);
    }

    @Override
    public void delete(PK id) {
        baseDao.delete(id);
    }

    @Override
    public void deleteObject(M model) {
        baseDao.deleteObject(model);
    }

    @Override
    public M get(PK id) {
        return baseDao.get(id);
    }

    @Override
    public int countAll() {
        return baseDao.countAll();
    }

    @Override
    public List<M> listAll() {
        return baseDao.listAll();
    }
}
