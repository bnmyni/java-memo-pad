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

package com.tuoming.mes.collect.dpp.datatype;

import com.pyrlong.util.StringUtil;



public final class DataColumn {
    private boolean readOnly; // 只读

    private DataTable table; // dataTable的引用

    private String columnName; // 列名

    private String captionName; // 显示名称

    private Object tag;// //通过tag对象保存公式配置

    private int columnIndex;// 列索引

    private int dataType;// 列数据类型

    private int size;

    private boolean isDisplayed = true; // 是否显示

    public DataColumn() {
        this("default1");
    }

    public DataColumn(int dataType) {
        this("default1", dataType);
    }

    public DataColumn(String columnName) {
        this(columnName, 0);
    }

    public DataColumn(String columnName, int dataType) {
        this.setDataType(dataType);
        this.columnName = columnName.toLowerCase();
    }

    public String getColumnName() {
        return this.columnName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName.toLowerCase();
        if (StringUtil.isEmpty(captionName)) captionName = columnName;
    }

    public String getCaptionName() {
        return captionName;
    }

    public void setCaptionName(String captionName) {
        this.captionName = captionName;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public DataTable getTable() {
        return this.table;
    }

    public void setTable(DataTable table) {
        this.table = table;
    }

    /**
     * @param dataType
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * @param columnIndex
     */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * @return the columnIndex
     */
    public int getColumnIndex() {
        return columnIndex;
    }


    /**
     * 功能描述： 将输入数据转为当前列的数据类型返回
     *
     * @param
     * @return: Object
     * @author: James Cheung
     * @version: 2.0
     */
    public Object convertTo(Object value) {
        return value;
    }

    @Override
    public String toString() {
        return this.columnName;
    }

    /**
     * @param tag
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * @return the tag
     */
    public Object getTag() {
        return tag;
    }

    /**
     * @return the isDisplayed
     */
    public boolean isDisplayed() {
        return isDisplayed;
    }

    /**
     * @param isDisplayed
     */
    public void setDisplayed(boolean isDisplayed) {
        this.isDisplayed = isDisplayed;
    }

}
