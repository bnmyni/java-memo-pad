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

package com.tuoming.mes.execute.dao;

import com.tuoming.mes.collect.dpp.dao.BaseService;
import com.tuoming.mes.collect.models.AdjustCommand;

/**
 * 业务指令适配器，用于处理指令下发等相关操作, 支持针对不同对象(BSC) 同步下发调整指令 可以通过Spring注入调用
 * 也可以使用AOSManager获取服务的一个实例对象，然后进行相关操作
 * <p>
 * <li>Spring调用</li>
 * <p>AppContext.getBean(AdjustCommandService.class)</p>
 * <p>
 * <li>AOSManager调用</li>
 * <p>new AOSManager().getService("AdjustCommandService")</p>
 *
 * @author James
 * @version 1.0.1
 * @since 1.0.0
 */
public interface AdjustCommandService extends BaseService<AdjustCommand, Long> {
    /**
     * 执行特定应用生成的分组命令,同时处理如下功能：
     * 1. 根据命令执行对象的不同并发执行任务，并记录执行日志到不同的日志文件
     * 2. 执行配置命令执行前后的动作表达式
     * 3. 根据配置对指令执行是否成功进行判断，并根据执行结果执行对应的操作
     *
     * @param appName   指令所属的应用名 如：HW_COX
     * @param groupName 要执行指令 的分组名称 如：ADD
     * @see BaseService
     */
    public void apply(String appName, String groupName);

    /**
     * 节能休眠或唤醒命令下发
     *
     * @param appName
     * @param groupName
     */
    public void sleepOrNotify(String appName, String groupName);

}
