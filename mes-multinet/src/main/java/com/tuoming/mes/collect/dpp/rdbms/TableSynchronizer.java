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

// Created On: 13-9-13 上午10:47
package com.tuoming.mes.collect.dpp.rdbms;

import java.util.Collection;

/**
 * 定义数据表同步器接口
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public interface TableSynchronizer {

    /**
     * 备份数据表内容到文件    ，注意本方法只支持表数据量小于60000的备份，超过部分将被舍弃
     *
     * @param dbName 数据源名称
     * @param tables 要备份的数据表
     */
    public String backupData(String dbName, Collection<String> tables);

    /**
     * 恢复文件内数据到数据表，恢复之前会自动备份表内数据，如果恢复失败则会将备份数据还原到数据表内
     *
     * @param dbName 数据源名称
     * @param tables 要回复的数据表名列表
     * @param files  备份文件列表
     */
    public void restoreData(String dbName, Collection<String> tables, Collection<String> files);

    public void restoreData(String dbName, Collection<String> tables, Collection<String> files, boolean clearOldData);

    public void restoreData(String dbName, Collection<String> files, boolean clearOldData);

    public void restoreData(String dbName, Collection<String> files);

}
