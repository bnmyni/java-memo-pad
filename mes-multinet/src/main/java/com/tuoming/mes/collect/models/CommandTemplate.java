/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
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

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 指令模板对象，用于存储系统内需要生成的指令格式，通过通配符配置
 * Created by james on 14-3-10.
 */
@Entity
@Table(name = "mes_command_template")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommandTemplate extends AbstractModel {

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
     * 是否生效
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "group_name", length = 164, nullable = false)
    private String groupName;

    /**
     * 同一组操作内指令的执行顺序
     */
    @Column(name = "order_id", length = 128, nullable = false)
    private Integer orderId;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
