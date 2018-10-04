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
import com.tuoming.mes.collect.dao.DbQueryCommandDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.DbQueryCommand;

/**
 * @see com.pyrlong.dpp.dao.impl.AbstractBaseDao
 */
@Component("DbQueryCommandDao")
public class DbQueryCommandDaoImpl extends AbstractBaseDao<DbQueryCommand, String> implements DbQueryCommandDao {
    @Override
    public List<DbQueryCommand> listGroup(String groupName) {
        return list(HQL_LIST_ALL + "  where groupName=? and enabled=?", new Object[]{groupName, true});
    }

    @Override
    public List<DbQueryCommand> listAll() {
        return list(HQL_LIST_ALL + "  where  enabled=?", new Object[]{true});
    }
}
