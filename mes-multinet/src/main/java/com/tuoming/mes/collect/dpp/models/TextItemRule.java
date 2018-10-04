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

package com.tuoming.mes.collect.dpp.models;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mes_log_parser_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TextItemRule extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "field_name", length = 64, nullable = false)
    private String name;
    @Column(name = "regex_filter", length = 120)
    private String regexFilter;
    @Column(name = "data_type")
    private int dataType;
    @Column(name = "default_value", length = 500)
    private String defaultValue;
    @Column(name = "value_expression", length = 500)
    private String valueExpression;
    @Column(name = "cached")
    private Boolean cachedItem = false;
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "parser_name", length = 255, nullable = false)
    private String parserName;

    public TextItemRule() {

    }

    public TextItemRule(String name, String regexFilter, int dataType,
                        String defaultValue, String valueExpression) {
        super();
        this.name = name;
        this.regexFilter = regexFilter;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.valueExpression = valueExpression;
    }

    public String getParserName() {
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Boolean getCachedItem() {
        return cachedItem;
    }

    public void setCachedItem(Boolean cachedItem) {
        this.cachedItem = cachedItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public void setRegexFilter(String regexFilter) {
        this.regexFilter = regexFilter;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

}
