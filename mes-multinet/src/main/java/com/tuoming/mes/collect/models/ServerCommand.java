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

package com.tuoming.mes.collect.models;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;


/**
 * 服务固化操作配置，主要配置一些针对服务器固化的操作内容，如登录、退出等
 * 对应数据表   aos_server_command
 */
@Entity
@Table(name = "mes_server_command")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ServerCommand extends AbstractModel {

    /**
     * 配置项唯一标识，通过系统保存时该标识会自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 发送到指令内容
     */
    @NotEmpty(message = "{object.notempty.illegal}")
    @Column(name = "command", length = 128, nullable = false)
    private String command;
    /**
     * 同一组操作内指令的执行顺序
     */
    @Column(name = "order_id", length = 128, nullable = false)
    private Integer orderId;

    public String getNeServer() {
        return neServer;
    }

    public void setNeServer(String neServer) {
        this.neServer = neServer;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 指令关联的服务器
     */
    @Column(name = "server_group", length = 128, nullable = false)
    private String neServer;

    /**
     * 标识指令对应的操作类型  ，其中 login\logout是每个服务器必须具有的配置
     */
    @Column(name = "action", length = 16, nullable = false)
    private String action;

    /**
     * 指令提示符，当出现这个提示符是发送本条指令
     */
    @Column(name = "prompt", length = 128, nullable = true)
    private String prompt;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
