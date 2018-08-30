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

// Created On: 13-7-31 下午4:16
package com.tuoming.mes.services.impl;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.models.FtpServer;
import com.tuoming.mes.services.serve.FtpServerService;

/**
 * @version 1.0
 * @see com.tuoming.mes.services.serve.aos.services.FtpServerService
 * @since 1.0
 */
@Scope("prototype")
@Component("FtpServerService")
public class FtpServerServiceImpl extends AbstractBaseService<FtpServer, String> implements FtpServerService {
    private final static Logger logger = LogFacade.getLog4j(FtpServerServiceImpl.class);

    public void setBaseDao(BaseDao<FtpServer, String> baseDao) {
        this.baseDao = baseDao;
    }
}
