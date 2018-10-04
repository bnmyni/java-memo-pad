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

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.pyrlong.logging.LogFacade;

/**
 * 数据表定义记录对象
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "mes_tab_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TableInfo extends AbstractModel {

    private static Logger logger = LogFacade.getLog4j(TableInfo.class);
    /**
     * 数据表对应的字段配置
     */
    @OrderBy("orderId asc")
    @OneToMany(mappedBy = "tableInfo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<TableColumnInfo> columnInfoSet;
    @Transient
    Map<String, TableColumnInfo> columnInfoHashMap = null;
    /**
     * 存储数据库中数据表名
     */
    @Id
    @Column(name = "tab_name", length = 64, nullable = false)
    private String name;
    /**
     * 数据表显示名称
     */
    @Column(name = "tab_caption", length = 72, nullable = false)
    private String caption;
    /**
     * 数据表类型 view/table/....
     */
    @Column(name = "tab_type", length = 64, nullable = false)
    private String tabType;
    /**
     * 数据表对应的实体对象，当处理该表的编辑操作时 基于该对象通过Hibernate实现
     */
    @Column(name = "model_class", length = 64, nullable = true)
    private String modelClass;
    /**
     * 主键表达式 ，用于基于现有列计算主键值的表达式
     */
    @Column(name = "primary_key_exp", length = 64, nullable = true)
    private String primaryKey;
    @Column(name = "form_width", nullable = true)
    private Integer width = 500;
    @Column(name = "form_height", length = 64, nullable = true)
    private Integer height = 500;

    public Integer getWidth() {
        setWidth(width);
        return width;
    }

    public void setWidth(Integer width) {
        if (width == null || width == 0)
            width = 400;
        this.width = width;
    }

    public Integer getHeight() {
        setHeight(height);
        return height;
    }

    public void setHeight(Integer height) {
        if (height == null || height == 0)
            height = 400;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Set<TableColumnInfo> getColumnInfoSet() {
        return columnInfoSet;
    }

    public void setColumnInfoSet(Set<TableColumnInfo> columnInfoSet) {
        this.columnInfoSet = columnInfoSet;
    }

    public String getTabType() {
        return tabType;
    }

    public void setTabType(String tabType) {
        this.tabType = tabType;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getColumnCaption(String name) {
        TableColumnInfo info = getColumnInfo(name);
        if (info == null)
            return name;
        return info.getFieldCaption();
    }


    public TableColumnInfo getColumnInfo(String name) {
        if (columnInfoHashMap == null) {
            columnInfoHashMap = new HashMap<String, TableColumnInfo>();
            for (TableColumnInfo columnInfo : this.columnInfoSet) {
                columnInfoHashMap.put(columnInfo.getFieldName(), columnInfo);
            }
        }
        if (columnInfoHashMap.containsKey(name))
            return columnInfoHashMap.get(name);
        logger.warn("Not found TableColumnInfo for :" + this.getName() + "---" + name);
        return null;
    }
}
