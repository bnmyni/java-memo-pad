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

package com.tuoming.mes.collect.dao.impl;

import org.springframework.stereotype.Component;

import java.util.List;
import com.tuoming.mes.collect.dao.ServerDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.collect.models.Server;

/**
 * 服务器对象
 * mes_servers
 */
@Component("ServerDao")
public class ServerDaoImpl extends AbstractBaseDao<Server, String> implements ServerDao {
    @Override
    public List<Server> getNeServers(ObjectType objectType, Manufacturers manufacturers) {
        String hql = HQL_LIST_ALL + " where objectType=? and enabled=?  and manufacturers=?";
        return list(hql, new Object[]{objectType, true, manufacturers});
    }

    @Override
    public List<Server> getEnabledServers() {
        String hql = HQL_LIST_ALL + " where enabled=?";
        return list(hql, new Object[]{true});
    }
}
