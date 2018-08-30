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

// Created On: 13-8-5 下午3:44
package com.tuoming.mes.collect.dpp.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pyrlong.json.JSONObject;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class PageInfo {
    private int pageIndex;
    private int pageSize;
    private int recordCount;
    private int pageCount;
    private boolean canAdd = false;
    private boolean canDel = false;
    private boolean canEdit = false;
    private boolean canSearch = false;
    private boolean canImport = false;

    private String caption;
    private String reportName;
    private String filterString;
    private DataTable table;
    private TableInfo tableInfo;
    List<Map> colMaps;

    List<Map> buttons;


    public List<Map> getButtons() {
        return buttons;
    }

    public void setButtons(List<Map> buttons) {
        this.buttons = buttons;
    }

    public boolean isCanImport() {
        return canImport;
    }

    public void setCanImport(boolean canImport) {
        this.canImport = canImport;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public boolean isCanSearch() {
        return canSearch;
    }

    public void setCanSearch(boolean canSearch) {
        this.canSearch = canSearch;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanDel() {
        return canDel;
    }

    public void setCanDel(boolean canDel) {
        this.canDel = canDel;
    }

    public boolean isCanAdd() {
        return canAdd;
    }

    public void setCanAdd(boolean canAdd) {
        this.canAdd = canAdd;
    }

    public List<Map> getColMaps() {
        return colMaps;
    }

    public void setColMaps(List<Map> colMaps) {
        this.colMaps = colMaps;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public DataTable getTable() {
        return table;
    }

    public void setTable(DataTable table) {
        this.table = table;
    }

    /**
     * 生成对应的JSON字符串
     */
    public String toJson() {
        String json = "";
        Map jsonMap = Maps.newHashMap();
        jsonMap.put("page", pageIndex);
        jsonMap.put("total", pageCount);
        jsonMap.put("records", pageSize);
        jsonMap.put("cols", colMaps);
        jsonMap.put("caption", caption);
        jsonMap.put("canAdd", canAdd);
        jsonMap.put("canDel", canDel);
        jsonMap.put("canImport", canImport);
        jsonMap.put("canEdit", canEdit);
        jsonMap.put("canSearch", canSearch);
        jsonMap.put("reportName", reportName);
        jsonMap.put("buttons", buttons);
        //
        List rowList = Lists.newArrayList();
        for (DataRow row : getTable().getRows()) {
            Map cellMap = new HashMap();
            cellMap.put("cell", row.getItemMap());
            rowList.add(cellMap);
        }
        jsonMap.put("rows", rowList);
        JSONObject object = new JSONObject(jsonMap);
        return object.toString();
    }
}
