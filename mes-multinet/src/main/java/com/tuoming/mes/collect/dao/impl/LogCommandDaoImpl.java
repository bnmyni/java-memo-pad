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

import com.tuoming.mes.collect.dao.LogCommandDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.LogCommand;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;

/**
 * @see com.pyrlong.dpp.dao.impl.AbstractBaseDao
 */
@Component("LogCommandDao")
public class LogCommandDaoImpl extends AbstractBaseDao<LogCommand, String> implements LogCommandDao {
    @Override
    public List<LogCommand> getCommands(String groupName) {
        String hql = HQL_LIST_ALL + "  where groupName = ? and enabled =? order by orderId";
        return list(hql, new Object[]{groupName, true});
    }

    @Override
    public List<LogCommand> getCommands(ObjectType objectType, Manufacturers manufacturers) {
        String hql = HQL_LIST_ALL + " where objectType = ? and manufacturers=? and enabled=？ order by orderId";
        return list(hql, new Object[]{objectType, manufacturers, true});
    }

	@Override
	public List<LogCommand> getCommandList(String logParser) {
		String hql = HQL_LIST_ALL + " where logParser = ? and enabled=? order by orderId";
        return list(hql, new Object[]{logParser, true});
	}
}
