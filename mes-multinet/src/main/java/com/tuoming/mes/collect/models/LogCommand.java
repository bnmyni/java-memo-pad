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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 指令采集配置对象，对应数据表：   aos_log_command 该表配置了需要通过指令采集的数据列表 、所关联解析器等信息
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_log_command")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LogCommand extends AbstractModel {
    /**
     * 指令名称，英文，用于程序调用 指令，同一个厂家同一个设备类型下 该值唯一
     */
    @Id
    @Column(name = "name", length = 120, nullable = false)
    private String commandName;
    /**
     * 分组名
     */
    @Column(name = "group_name", length = 120, nullable = false)
    private String groupName;
    /**
     * 适用对象类型,这里的对象类型是指命令下发对象的类型 如:我们采集小区参数 的执行对象 是bsc,那么这里就写bsc类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false)
    private ObjectType objectType;
    /**
     * 指令内容  支持基于表达式模板配置
     */
    @Column(name = "cmd", length = 500, nullable = false)
    private String command;
    /**
     * 关联解析器对象,这里可以配置多个解析对象Bean名称 ,#分割
     */
    @Column(name = "log_parser", length = 120, nullable = false)
    private String logParser;
    /**
     * 执行采集前执行的操作，该操作位于 CommandMap配置的前置操作之后
     */
    @Column(name = "pre_action", length = 500, nullable = true)
    private String preAction;

    public String getIterator() {
        return iterator;
    }

    public void setIterator(String iterator) {
        this.iterator = iterator;
    }

    /**
     * 迭代器，用于配置需要多次执行的指令，配置时可以通过配置一个返回DataTable的表达式或者返回Map对象的表达式 #修改类型，改为长字符串
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "iterator", nullable = true)
    private String iterator;

    public String getAfterAction() {
        return afterAction;
    }

    public void setAfterAction(String afterAction) {
        this.afterAction = afterAction;
    }

    public String getPreAction() {
        return preAction;
    }

    public void setPreAction(String preAction) {
        this.preAction = preAction;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * 执行采集后执行的操作，该操作位于 CommandMap配置的前置操作之前
     */
    @Column(name = "after_action", length = 500, nullable = true)
    private String afterAction;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Column(name = "order_id", nullable = true)
    private int orderId;
    /**
     * 指令是否生效
     */
    @Column(name = "enabled", nullable = true)
    private boolean enabled;
    /**
     * 附加说明信息
     */
    @Column(name = "remark", length = 120, nullable = true)
    private String remark;
    /**
     * 适用厂家版本信息
     */
    @ManyToOne
    @JoinColumn(name = "manufacturers", nullable = false, insertable = true, updatable = true)
    private Manufacturers manufacturers;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Manufacturers getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(Manufacturers manufacturers) {
        this.manufacturers = manufacturers;
    }

    public String getName() {
        return commandName;
    }

    public void setName(String name) {
        this.commandName = name;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLogParser() {
        return logParser;
    }

    public void setLogParser(String logParser) {
        this.logParser = logParser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
