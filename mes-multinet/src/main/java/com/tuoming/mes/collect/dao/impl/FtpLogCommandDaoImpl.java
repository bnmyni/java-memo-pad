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

// Created On: 13-7-24 上午11:43
package com.tuoming.mes.collect.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dao.FtpLogCommandDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.collect.models.FtpLogCommand;

/**
 * FTP采集数据任务
 * mes_ftp_command
 */
@Component("FtpLogCommandDao")
public class FtpLogCommandDaoImpl extends AbstractBaseDao<FtpLogCommand, String> implements FtpLogCommandDao {

    private static Logger logger = LogFacade.getLog4j(FtpLogCommandDaoImpl.class);

    @Override
    public List<FtpLogCommand> getByGroup(String groupName) {
        return list(HQL_LIST_ALL + " where groupName=?  and enabled=?", new Object[]{groupName, true});
    }

    @Override
    public List<FtpLogCommand> getAllEnabled() {
        return list(HQL_LIST_ALL + " where  enabled=?", new Object[]{true});
    }
}
