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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 指令字典对象 ,对应aos_command_map数据表中存储的内容 该对象存储某个指令对应的一些公共配置，
 * 如 结束标识、成功标识、失败标识、成功及失败之后需要执行的动作 等
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_command_map")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommandMap extends AbstractModel {
    /**
     * 记录唯一标识，通过系统增加的记录该值会自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 名称，仅用于标识本条配置的命令
     */
    @Column(name = "name", length = 500, nullable = true)
    private String name;

    /**
     * 过滤器，用于与实际指令进行匹配，此处支持正则匹配
     */
    @Column(name = "command_filter", length = 500, nullable = true)
    private String commandFilter;

    public String getDoneMark() {
        return doneMark;
    }

    public void setDoneMark(String doneMark) {
        this.doneMark = doneMark;
    }

    /**
     * 命令结束标识
     */
    @Column(name = "done_mark", length = 100, nullable = true)
    private String doneMark;
    /**
     * 指令试用厂家
     */
    @ManyToOne
    @JoinColumn(name = "manufacturers", nullable = false, insertable = true, updatable = true)
    private Manufacturers manufacturers;

    /**
     * 指令对象类型
     */
    @Column(name = "object_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ObjectType objectType;

    /**
     * 判断指令是否执行成功的标识
     */
    @Column(name = "sucess_mark", length = 500, nullable = true)
    private String sucessfullMark;

    /**
     * 判断指令是否执行失败的标识
     */
    @Column(name = "fail_mark", length = 500, nullable = true)
    private String failMark;

    /**
     * 指令发出前可以执行的统一操作
     */
    @Column(name = "pre_action", length = 500, nullable = true)
    private String preAction;
    /**
     * 指令发出后可以执行的操作
     */
    @Column(name = "after_action", length = 500, nullable = true)
    private String afterAction;

    /**
     * 指令执行失败需要执行的操作
     */
    @Column(name = "fail_action", length = 500, nullable = true)
    private String failAction;
    /**
     * 执行成功后需要执行的操作
     */
    @Column(name = "sucess_action", length = 500, nullable = true)
    private String sucessAction;

    /**
     * 指令的全局开关，如果这里设置成false,则整个系统内符合本指令匹配条件的指令都不执行
     */
    @Column(name = "enabled", nullable = true)
    private Boolean enabled = true;

    public Boolean getResent() {
        if (resent == null) return true;
        return resent;
    }

    public void setResent(Boolean resent) {
        if (resent != null)
            this.resent = resent;
    }

    @Column(name = "resent", nullable = true)
    private Boolean resent = false;

    public String getInteractiveCmd() {
        return interactiveCmd;
    }

    public void setInteractiveCmd(String interactiveCmd) {
        this.interactiveCmd = interactiveCmd;
    }

    /**
     * 互动指令，处理指令发送过程需要交互操作的，如按任意键继续、确认等
     */
    @Column(name = "interactive_cmd", length = 500, nullable = true)
    private String interactiveCmd;

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    @Column(name = "time_out", nullable = true)
    private int timeOut = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommandFilter() {
        return commandFilter;
    }

    public void setCommandFilter(String commandFilter) {
        this.commandFilter = commandFilter;
    }

    public Manufacturers getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(Manufacturers manufacturers) {
        this.manufacturers = manufacturers;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getSucessfullMark() {
        return sucessfullMark;
    }

    public void setSucessfullMark(String sucessfullMark) {
        this.sucessfullMark = sucessfullMark;
    }

    public String getFailMark() {
        return failMark;
    }

    public void setFailMark(String failMark) {
        this.failMark = failMark;
    }

    public String getPreAction() {
        return preAction;
    }

    public void setPreAction(String preAction) {
        this.preAction = preAction;
    }

    public String getAfterAction() {
        return afterAction;
    }

    public void setAfterAction(String afterAction) {
        this.afterAction = afterAction;
    }

    public String getFailAction() {
        return failAction;
    }

    public void setFailAction(String failAction) {
        this.failAction = failAction;
    }

    public String getSucessAction() {
        return sucessAction;
    }

    public void setSucessAction(String sucessAction) {
        this.sucessAction = sucessAction;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
