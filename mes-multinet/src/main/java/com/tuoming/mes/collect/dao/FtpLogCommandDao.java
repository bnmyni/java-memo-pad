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

// Created On: 13-7-24 上午11:40
package com.tuoming.mes.collect.dao;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.models.FtpLogCommand;

/**
 * FTP采集任务Dao定义
 *
 * @author James Cheung
 * @version 1.0
 */

public interface FtpLogCommandDao extends BaseDao<FtpLogCommand, String> {

    /**
     * 获取指定分组配置的任务列表
     *
     * @param groupName 分组名称
     * @return 符合条件的对象列表
     */
    List<FtpLogCommand> getByGroup(String groupName);

    /**
     * 获取所有设置为可用的配置列表
     *
     * @return
     */
    List<FtpLogCommand> getAllEnabled();
}
