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

package com.tuoming.mes.collect.dao;

import java.util.List;

import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.models.LogCommand;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;

/**
 * 采集命令对象Dao接口
 *
 * @author James
 * @version 1.0
 */
public interface LogCommandDao extends BaseDao<LogCommand, String> {
    /**
     * 获取特定分组采集指令
     *
     * @param groupName
     *         指令分组名称
     */
    List<LogCommand> getCommands(String groupName);

    /**
     * 执行特定厂家特定对象相关采集指令
     *
     * @param objectType
     *         对象类型
     * @param manufacturers
     *         厂家
     */
    List<LogCommand> getCommands(ObjectType objectType, Manufacturers manufacturers);
    
    /**
     * 根据解析器执行相关采集指令
     *
     * @param objectType
     *         对象类型
     * @param manufacturers
     *         厂家
     */
    List<LogCommand> getCommandList(String logParser);


}
