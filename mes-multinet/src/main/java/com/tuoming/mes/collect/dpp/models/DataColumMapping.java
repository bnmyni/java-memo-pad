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

public class DataColumMapping {
    private String columnId;
    // 目标列名
    private String columnName;
    // 目标列数据类型
    private String columnDataType;
    // 采集数据计算表达式
    private String gatherFormula;
    // 标识本列是否可见，通过设置本列可以控制生成的目标表是否包括本列数据，针对用于临时数据标识的列一般通过设置本属性达到在目标表隐藏指定列的目的！
    private Boolean hide=false;
    // 该字段默认值
    private String defaultValue;
    // 发生错误时该字段需要填入的值
    private String errorValue;
    // 字段值校验表达式，用于校验采集值的准确性
    private String validCheck;
    // 当前配置是否生效
    private Boolean enabled;
    // 是否打开数据校验
    private Boolean validOpened;
    private Boolean nullAbled;
    private Boolean isKey;

    public Boolean getIsKey() {
        return isKey;
    }

    public void setIsKey(Boolean isKey) {
        this.isKey = isKey;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public Boolean getNullAbled() {
        return nullAbled;
    }

    public void setNullAbled(Boolean nullAbled) {
        this.nullAbled = nullAbled;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean sourceTable) {
        this.hide = sourceTable;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    public String getGatherFormula() {
        return gatherFormula;
    }

    public void setGatherFormula(String gatherFormula) {
        this.gatherFormula = gatherFormula;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getErrorValue() {
        return errorValue;
    }

    public void setErrorValue(String errorValue) {
        this.errorValue = errorValue;
    }

    public String getValidCheck() {
        return validCheck;
    }

    public void setValidCheck(String validCheck) {
        this.validCheck = validCheck;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getValidOpened() {
        return validOpened;
    }

    public void setValidOpened(Boolean validOpened) {
        this.validOpened = validOpened;
    }

}
