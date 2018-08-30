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

import java.util.List;

import org.springframework.stereotype.Component;

import com.tuoming.mes.collect.dao.ServerCommandDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.ServerCommand;

/**
 * @see com.pyrlong.dpp.dao.impl.AbstractBaseDao
 */
@Component("ServerCommandDao")
public class ServerCommandDaoImpl extends AbstractBaseDao<ServerCommand, Integer> implements ServerCommandDao {

    @Override
    public List<ServerCommand> getNamedCommands(String server, String action) {
        return list(HQL_LIST_ALL + " where action=? and  neServer=? order by orderId asc", new Object[]{action, server});
    }
}
