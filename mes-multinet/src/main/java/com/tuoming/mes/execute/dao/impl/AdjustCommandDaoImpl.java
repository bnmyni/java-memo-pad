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

package com.tuoming.mes.execute.dao.impl;

import org.springframework.stereotype.Component;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.execute.dao.AdjustCommandDao;

/**
 * @see com.pyrlong.dpp.dao.impl.AbstractBaseDao
 */
@Component("AdjustCommandDao")
public class AdjustCommandDaoImpl extends AbstractBaseDao<AdjustCommand, Long> implements AdjustCommandDao {

    @Override
    public List<AdjustCommand> getCommands(String appName, String groupName) {
        String hql = HQL_LIST_ALL + " where applied=0 and appName=? and groupName=? order by  neObject,orderId";
        return list(hql, new Object[]{appName, groupName});
    }
}
