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

package com.tuoming.mes.services.serve;

import com.tuoming.mes.collect.dpp.dao.BaseService;
import com.tuoming.mes.collect.models.DbQueryCommand;

/**
 * 数据库查询采集任务服务接口定义
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DbQueryService extends BaseService<DbQueryCommand, String> {

    /**
     * 运行当前数据库配置的所有采集任务
     */
    public void loadAll();

    /**
     * 运行数据库配置的指定名称的采集任务
     *
     * @param commandName
     */
    public void load(String commandName);

    /**
     * 运行数据库配置的指定分组的采集任务
     *
     * @param groupName
     */
    public void loadAll(String groupName);

    /**
     * 执行配置的指定分组的非查询操作，如历史数据维护 数据更新等
     *
     * @param groupName
     */
    public void executeAll(String groupName);

    /**
     * 执行特定数据库配置的非采集任务
     *
     * @param commandName
     *         数据库采集任务名称，对应aos_db_command表配置
     */
    public void execute(String commandName);
}
