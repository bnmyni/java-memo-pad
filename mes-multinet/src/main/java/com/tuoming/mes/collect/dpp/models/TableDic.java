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

// Created On: 13-8-27 下午5:46
package com.tuoming.mes.collect.dpp.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "mes_tab_dic")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TableDic extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dic_id", nullable = false)
    private int id;
    @Column(name = "group_name", length = 72, nullable = true)
    private String groupName;
    @Column(name = "dic_code", length = 64, nullable = false)
    private String dicCode;
    @Column(name = "dic_value", length = 64, nullable = false)
    private String dicValue;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDicCode() {
        return dicCode;
    }

    public void setDicCode(String dicCode) {
        this.dicCode = dicCode;
    }

    public String getDicValue() {
        return dicValue;
    }

    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }
}
