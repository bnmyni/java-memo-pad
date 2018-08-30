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

// Created On: 13-8-2 上午9:37
package com.tuoming.mes.collect.dpp.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.dao.AppsettingDao;
import com.tuoming.mes.collect.dpp.models.Appsetting;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */
@Component("AppsettingDao")
public class AppsettingDaoImpl extends AbstractBaseDao<Appsetting, String> implements AppsettingDao {

    private static Logger logger = LogFacade.getLog4j(AppsettingDaoImpl.class);

}
