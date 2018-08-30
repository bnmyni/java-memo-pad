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

// Created On: 13-8-5 下午1:17
package com.tuoming.mes.collect.dpp.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.pyrlong.util.StringUtil;

/**
 * 数据列信息记录表
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "mes_tab_columns")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TableColumnInfo extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    /**
     * 所属数据表定义对象
     */
    @Column(name = "tab_info", length = 64, nullable = false)
    private String tableInfo;

    /**
     * 标识该字段是否允许编辑
     */
    @Column(name = "editable", nullable = true)
    private boolean editable;

    /**
     * 数据类型，使用JDBC定义
     */
    @Column(name = "data_type", nullable = false)
    private Integer dataType;

    /**
     * 字段名
     */
    @Column(name = "field_name", length = 64, nullable = false)
    private String fieldName;

    /**
     * 字段显示名
     */
    @Column(name = "field_caption", length = 64, nullable = false)
    private String fieldCaption;

    /**
     * 字段显示长度，定义字段所需显示长度
     */
    @Column(name = "field_length", length = 64, nullable = false)
    private Integer fieldLength;

    /**
     * 这个字段是否是主键
     */
    @Column(name = "is_primary", nullable = true)
    private boolean isPrimary;

    @Column(name = "order_id", nullable = true)
    private int orderId;

    @Column(name = "visiblity", nullable = true)
    private boolean visiblity;

    @Column(name = "formatter", length = 500, nullable = true)
    private String formatter = "";

    /**
     * 字段值是否可以为空
     */
    @Column(name = "nullabled", nullable = true)
    private boolean nullabled = false;


    @Column(name = "filter_op", length = 16, nullable = true)
    private String filterOP;

    public String getFilterOP() {
        return filterOP;
    }

    public void setFilterOP(String filterOP) {
        if (StringUtil.isNotEmpty(filterOP))
            this.filterOP = filterOP;
    }

    public boolean isNullabled() {
        return nullabled;
    }

    public void setNullabled(boolean nullabled) {
        this.nullabled = nullabled;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getEditoptions() {
        return editoptions;
    }

    public void setEditoptions(String editoptions) {
        this.editoptions = editoptions;
    }

    @Column(name = "edit_options", length = 500, nullable = true)
    private String editoptions;

    public String getFormatter() {
        if (StringUtil.isEmpty(formatter))
            return "";
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public boolean isVisiblity() {
        return visiblity;
    }

    public void setVisiblity(boolean visiblity) {
        this.visiblity = visiblity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(String tableInfo) {
        this.tableInfo = tableInfo;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldCaption() {
        return fieldCaption;
    }

    public void setFieldCaption(String fieldCaption) {
        this.fieldCaption = fieldCaption;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }


}
