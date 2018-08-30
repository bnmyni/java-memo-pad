/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
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

package com.tuoming.mes.collect.dao;

import java.util.List;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.models.CommandTemplate;

/**
 * Created by james on 14-3-10.
 */
public interface CommandTemplateDao extends BaseDao<CommandTemplate, Long> {
    public List<CommandTemplate> getCommandTemplate(String groupName);
}
