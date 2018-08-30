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

// Created On: 13-7-24 上午10:55
package com.tuoming.mes.services.serve;

import com.tuoming.mes.collect.dpp.dao.BaseService;
import com.tuoming.mes.collect.models.FtpLogCommand;

/**
 * 基于FTP的数据采集实现类，实现根据配置从指定FTP服务器下载文件到本地，
 * 并调用解析器完成解析入库操作
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */
public interface FtpLogCommandService extends BaseService<FtpLogCommand, String> {
    /**
     * 执行特定名称配置的采集任务
     *
     * @param name
     *         要采集的任务名称，对应FtpLogCommand中的配置
     */
    public void query(String name, long batch);

    /**
     * 采集指定分组的任务
     *
     * @param groupName
     *         要采集的分组
     */
    public void queryAll(String groupName, long batch);

    /**
     * 采集当前配置的所有采集任务
     */
    public void queryAll(long batch);

    /**
     * 执行特定名称配置的采集任务
     *
     * @param name
     *         要采集的任务名称，对应FtpLogCommand中的配置
     */
    public void query(String name);

    /**
     * 采集指定分组的任务
     *
     * @param groupName
     *         要采集的分组
     */
    public void queryAll(String groupName);

    /**
     * 采集当前配置的所有采集任务
     */
    public void queryAll();
    
}
