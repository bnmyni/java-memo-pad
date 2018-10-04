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

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseService;
import com.tuoming.mes.collect.models.LogCommand;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.collect.models.Server;

/**
 * 查询指令解析服务， 对特定分组指令、特点名称、特定网元类型采集指令进行执行
 */
public interface LogCommandService extends BaseService<LogCommand, String> {

    /**
     * 执行特定分组采集指令
     *
     * @param groupName 要执行采集的分组名称，对应aos_log_command表的group_name字段
     * @param batch     采集批次标识
     */
    void queryAll(String groupName, long batch);

    /**
     * 执行特定厂家特定对象相关采集指令，如2/3g分析时，我们可以通过这个方法分别实现对2/3g参数的采集
     *
     * @param objectType    要采集的对象类型标识，大部分情况下我们都是使用BSC（注意，这里不区分2/3g我们统一都叫BSC）
     * @param manufacturers 要执行采集的厂家标识，对应aos_manufacturers表配置
     * @param batch         采集批次标识
     */
    void queryAll(ObjectType objectType, Manufacturers manufacturers, long batch);

    void queryAllByLogParser(String logParser, long batch);

    /**
     * 执行特定名称对应指令的采集，根据指令配置的对象类型，厂家等信息 更新所有涉及网元的本类数据
     *
     * @param queryCommand 要执行日志采集的查询指令名，对应aos_log_command表中采集指令名称的配置
     * @param batch        采集批次标识
     */
    void query(String queryCommand, long batch);

    /**
     * 多线程更新指定服务器列表的指定类型的参数，即对指定服务器列表执行指定名称的采集
     *
     * @param server       要执行采集的服务器列表
     * @param queryCommand 要执行的采集指令名称，对应aos_log_command中的配置
     * @param batch        采集操作批次号
     */
    void query(Server[] server, String queryCommand, long batch);

    /**
     * 更新某个服务器某个参数
     *
     * @param server       要执行采集的服务器名称,对应aos_servers表内配置
     * @param queryCommand 要执行的采集指令名称，对应aos_log_command指令配置
     * @param batch        采集操作批次号
     */
    void query(String server, String queryCommand, long batch);

    /**
     * 针对特定服务器执行相关采集指令，如更新某个特定BSC的参数，执行这个方法会直接将这个服务器对应的所有可用采集配置执行一遍
     *
     * @param server 要执行采集的服务器对象
     * @param batch  采集操作批次号
     */
    void query(Server server, long batch);

    /**
     * 使用多线程更新给定服务器列表对应的参数数据，执行这个方法会直接将服务器对应的所有可用采集配置执行一遍
     *
     * @param server 要执行采集的服务器对象列表
     * @param batch  采集操作批次号
     */
    void query(Server[] server, long batch);

    /**
     * 使用指定解析器解析存在的文件,这个方法一般可以用于测试采集配置是否正确
     *
     * @param logFile   要解析的日志文件路径，这里可以使用相对路径或绝对路径
     * @param logParser 对应的解析器名称，即aos_log_parser中的配置名称
     */
    void parse(String logFile, String logParser);

    /**
     * 使用指定解析器解析存在的文件,这个方法一般可以用于测试采集配置是否正确
     *
     * @param logFiles  要解析的日志文件路径列表，这里可以使用相对路径或绝对路径
     * @param logParser 对应的解析器名称，即aos_log_parser中的配置名称
     */
    public void parse(List<String> logFiles, String logParser);
}
