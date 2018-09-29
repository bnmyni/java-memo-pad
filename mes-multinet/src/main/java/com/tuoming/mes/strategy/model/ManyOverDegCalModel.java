package com.tuoming.mes.strategy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ManyOverDegCalModel {
    //查询语句
    private StringBuffer querySql;
    //待写入文件的小区集合
    private List<Map<String, Object>> cellDataList;
    //计算器
    private int count;

    public ManyOverDegCalModel() {
        querySql = new StringBuffer();
        cellDataList = new ArrayList<Map<String, Object>>();
        count = 0;
    }

    public String getQuerySql() {
        return querySql.toString();
    }

    public void addQuerySql(Object qSql) {
        this.querySql.append(qSql);
    }

    public List<Map<String, Object>> getCellDataList() {
        return cellDataList;
    }

    public void addCellDataList(Map<String, Object> cellData) {
        this.cellDataList.add(cellData);
    }

    public int getCount() {
        return count;
    }

    public void addCount() {
        this.count++;
    }


}
