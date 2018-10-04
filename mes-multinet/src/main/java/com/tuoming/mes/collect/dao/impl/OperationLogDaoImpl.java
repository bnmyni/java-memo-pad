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

// Created On: 13-7-26 下午10:06
package com.tuoming.mes.collect.dao.impl;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.OperationLog;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 */
@Component("OperationLogDao")
public class OperationLogDaoImpl extends AbstractBaseDao<OperationLog, String> implements OperationLogDao {

    @Override
    public List<OperationLog> getLastOper(String objectName, String operType, Long startId) {
        String hql = HQL_LIST_ALL;
        String where = "";
        List<Object> paras = new ArrayList<Object>();
        if (StringUtil.isNotEmpty(objectName)) {
            where += " objectName = ? ";
            paras.add(objectName);
        }
        if (StringUtil.isNotEmpty(operType)) {
            if (StringUtil.isNotEmpty(where))
                where += " and ";
            where += " operType = ? ";
            paras.add(operType);
        }
        if (StringUtil.isNotEmpty(where)) {
            hql = hql + " where " + where;
        }
        if (startId == 0) {
            OperationLog operationLog = unique(hql + " order by Id", new Object[0]);
            startId = operationLog.getId();
        }
        if (StringUtil.isEmpty(where))
            hql += " where ";
        hql += " Id>? ";
        paras.add(startId);
        return list(hql, paras.toArray());
    }
}
