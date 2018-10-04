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
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.collect.models.Server;

/**
 * 服务器对象Dao接口
 *
 * @author James
 * @version 1.0
 */
public interface ServerDao extends BaseDao<Server, String> {
    /**
     * 获取特定类型的服务器对象列表
     *
     * @param objectType 服务器类型对象
     * @return 符合给定对象类型的服务器对象列表
     */
    List<Server> getNeServers(ObjectType objectType, Manufacturers manufacturers);

    /**
     * 查询可用的采集命令服务器
     *
     * @return
     */
    List<Server> getEnabledServers();
}
