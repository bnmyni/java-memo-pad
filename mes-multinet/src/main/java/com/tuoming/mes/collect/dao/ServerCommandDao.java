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
import com.tuoming.mes.collect.models.ServerCommand;

/**
 * 服务器对象固化指令Dao接口定义
 *
 * @author James
 * @version 1.0
 */
public interface ServerCommandDao extends BaseDao<ServerCommand, Integer> {
    /**
     * 获取指定服务器对应操作类型的指令 列表
     *
     * @param action 指令类型 如login/logout
     * @return 按照配置的执行顺序的指令对象
     */
    List<ServerCommand> getNamedCommands(String server, String action);
}
