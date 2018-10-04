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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.Envirment;

public final class DataRow {
    // 定义该行记录在table所处的行数
    private int rowIndex = -1;
    private DataColumnCollection columns;

    // table的一个引用
    private DataTable table;

    // 用于存储数据的Map对象，这里保存的对象不包括顺序信息，数据获取的索引通过行信息标识
    private Map<String, Object> itemMap = new LinkedHashMap<String, Object>();

    public DataRow() {

    }

    public DataRow(DataTable table) {
        this.table = table;
        this.columns = table.getColumns();
    }

    public void clearup() {
        itemMap.clear();
        table = null;
    }

    /**
     * 功能描述： 获取当前行的行索引
     *
     * @param
     * @return: int
     * @author: James Cheung
     * @version: 2.0
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * 功能描述： 获取当前行所属数据表对象
     *
     * @param
     * @return: DataTable
     * @author: James Cheung
     * @version: 2.0
     */
    public DataTable getTable() {
        return this.table;
    }

    /**
     * @return the columns
     */
    public DataColumnCollection getColumns() {
        return columns;
    }

    /**
     * @param columns
     */
    public void setColumns(DataColumnCollection columns) {
        this.columns = columns;
    }

    public void setValue(Object[] values) {
        int i = 0;
        for (DataColumn column : table.getColumns()) {
            setValue(column, values[i]);
            i++;
        }
    }

    public void setValue(int index, Object value) {
        setValue(this.columns.get(index), value);
    }

    public void setValue(String columnName, Object value) {
        setValue(this.columns.get(columnName), value);
    }

    public void setValue(DataColumn column, Object value) {
        if (column != null) {
            getItemMap().put(column.getColumnName(), column.convertTo(value));
        }
    }

    public Object getValue(int index) {
        String colName = this.columns.get(index).getColumnName();
        return this.getItemMap().get(colName);
    }

    public Object getValue(String columnName) {
        return this.getItemMap().get(columnName.toLowerCase());
    }

    /**
     * @return the itemMap
     */
    public Map<String, Object> getItemMap() {
        return itemMap;
    }

    public void copyFrom(DataRow row) {
        this.itemMap.clear();// 首先请客当前记录
        for (Object c : this.columns) {
            this.itemMap.put(c.toString(), row.getValue(c.toString()));
        }
    }

    public String valueToString() {
        String result = "";
        for (DataColumn column : this.getColumns()) {
            if (!column.isDisplayed())
                continue;
            String val = "" + this.itemMap.get(column.getColumnName());
            if (val.indexOf(",") >= 0)
                val = "\"" + val.replace("\"", " ") + "\"";
            result += val;
            result += Envirment.CSV_SEPARATOR;
        }
        if (result.endsWith(Envirment.CSV_SEPARATOR))
            return result.substring(0, result.length() - Envirment.CSV_SEPARATOR.length());
        return "";
    }

    public void parseString(String inputString) {
        String[] values = inputString.split("$$");
        itemMap.clear();
        if (values.length == this.getColumns().size()) {
            int i = 0;
            for (DataColumn dc : columns) {
                itemMap.put(dc.getColumnName(), values[i]);
                i++;
            }
        }
    }

    public Object[] toArray() {
        List<Object> objectList = new ArrayList<Object>();
        for (Map.Entry entry : itemMap.entrySet())
            objectList.add(entry.getValue());
        return objectList.toArray();
    }


    public String toString() {
        if (this.getTable().getPrimaryKey() != null && this.getTable().getPrimaryKey().getKeyColumns().size() > 0) {
            return getTable().getPrimaryKey().getKeyString(this);
        }
        return this.getItemMap().toString();
    }
}
