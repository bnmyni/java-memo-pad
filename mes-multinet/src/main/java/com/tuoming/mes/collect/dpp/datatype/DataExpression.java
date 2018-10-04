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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.pyrlong.dsl.tools.Context;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.dsl.tools.ExecuteContext;


/**
 * 此类描述的是： 专用于数据处理的表达式扩展类
 *
 * @author: James Cheung
 * @version: 2.0
 */
public final class DataExpression extends DSLUtil {
    public static final ExecuteContext executeContext;
    private static final Number ROUND_NUM = 4;

    static {
        executeContext = DSLUtil.getNewInstance();
        executeContext.registFunctions(DataExpression.class);
        executeContext.registFunctions(DataTable.class);
    }

    public DataExpression() {

    }

    // 以下为基于DataTable实现的自定义函数

    /**
     * 功能描述： 获取符合过滤条件的指定指标的最新值，过滤条件支持为空
     *
     * @param
     * @return: Object
     * @author: James Cheung
     * @version: 2.0
     */
    public static Object newest(Context context, String field, String filter) {
        Context ct = context;
        List<String> fields = getVariable(filter.toString());
        field = field.toString().replace("[", "").replace("]", "");
        fields.add(field.toString());
        DataTable table = getDataTable((DataSet) ct.get("dataSet"), fields);
        if (table != null) {
            Object value = DataTable.newest(table, field, filter);
            return value;
        } else {
            return null;
        }
    }

    public static Object max(Context context, String field, String filter) {
        Context ct = context;
        List<String> fields = getVariable(filter.toString());
        field = field.toString().replace("[", "").replace("]", "");
        fields.add(field.toString());
        DataTable table = getDataTable((DataSet) ct.get("dataSet"), fields);
        Object value = DataTable.max(table, field, filter);
        value = roundDecimal(value, ROUND_NUM);
        return value;
    }

    public static Object min(Context context, String field, String filter) {
        Context ct = (Context) context;
        List<String> fields = getVariable(filter.toString());
        field = field.toString().replace("[", "").replace("]", "");
        fields.add(field.toString());
        DataTable table = getDataTable((DataSet) ct.get("dataSet"), fields);
        Object value = DataTable.min(table, field, filter);
        value = roundDecimal(value, ROUND_NUM);
        return value;
    }

    public static Object count(Context context, String filter) {
        Context ct = (Context) context;
        List<String> fields = getVariable(filter.toString());
        DataTable table = getDataTable(
                (DataSet) ((Context) context).get("dataSet"), fields);
        if (table == null) {
            return 0;
        } else {
            Object obj = DataTable.count(table, filter);
            return obj;
        }
    }

    public static Object sum(Context context, String field, String filter) {
        Context ct = (Context) context;
        List<String> fields = getVariable(filter.toString());
        field = field.toString().replace("[", "").replace("]", "");
        fields.add(field.toString());
        DataTable table = getDataTable((DataSet) ct.get("dataSet"), fields);
        Object value = DataTable.sum(table, field, filter);
        value = roundDecimal(value, ROUND_NUM);
        return value;
    }

    public static Object avg(Context context, String field, String filter) {
        Context ct = (Context) context;
        List<String> fields = getVariable(filter.toString());
        field = field.toString().replace("[", "").replace("]", "");
        fields.add(field.toString());
        DataTable table = getDataTable((DataSet) ct.get("dataSet"), fields);
        Object value = DataTable.avg(table, field, filter);
        value = roundDecimal(value, ROUND_NUM);
        return value;
    }

    public static DataTable getDataTable(DataSet dataSet, String[] fields) {
        if (dataSet != null) {
            // 寻呼每个DataTable获取需要的Table对象
            for (DataTable table : dataSet) {
                if (table != null) {
                    boolean r = fields.length > 0;
                    for (String s : fields) {
                        r = r && table.getColumns().contains(s.toLowerCase());
                    }
                    if (r) return table;
                }
            }
        }
        if (dataSet.getDataTables().size() > 0) return dataSet.getDataTable(0);
        return null;
    }

    /**
     * 功能描述： 实现对DataTable对象的复杂查询，包括分组、汇总、过滤等
     *
     * @param tableName    要生成的数据表名称,如 "FilterResultTable"
     * @param selectField  要选择的字段，需要显示到最终结果表中的数据，其形态如: max(col_1) col_1_max,min(col_2)
     *                     col_2_min,avg(col_3) col_3_avg,count(col_4) col_4_count
     * @param filterString col_1>10 && col_2<100 && (col_3+col_1)>0
     * @param groupField   lac,ci
     * @throws Exception
     * @return: DataTable
     * @author: James Cheung
     * @version: 2.0
     */
    public static DataTable selectGroupBy(Context context, String tableName,
                                          String selectField, String filterString, String groupField)
            throws Exception {
        String fieldString = groupField + "," + selectField;
        List<String> fields = getVariable(fieldString + "," + filterString);
        // 获取需要操作的数据表
        DataTable table = getDataTable(
                (DataSet) ((Context) context).get("dataSet"), fields);
        // selectField = selectField.replace("[", "").replace("]", "");
        groupField = groupField.replace("[", "").replace("]", "");
        filterString = filterString.replace("[", "").replace("]", "");
        // 构造结果表结构
        DataTable result = buildTableStruct(fieldString);
        // 首先过滤数据，并按照分组字段排序
        DataTable tmpTable = table.select(filterString, groupField);
        // 处理分组，循环处理数据行
        Map<String, Object> groupValue = new HashMap<String, Object>();
        DataTable tempTable = tmpTable.cloneTable();// 克隆一个数据表
        String[] groups = groupField.split(",");
        for (DataRow sourceRow : tmpTable.getRows()) {
            String groupKey = "";
            for (String s : groups) {
                groupKey += ("_" + sourceRow.getValue(s.trim()));
            }
            if (!groupValue.containsKey(groupKey)) {
                groupValue.put(groupKey, null);// 将新记录加入MAP
                addSelectGroupByRow(tempTable, result);
            }
            // 将当前行加入临时表
            tempTable.addRow(sourceRow);
        }
        addSelectGroupByRow(tempTable, result);
        return result;
    }

    private static void addSelectGroupByRow(DataTable tempTable, DataTable result) throws Exception {
        if (tempTable.getRows().size() > 0) {
            // 将当前计算后加入结果表
            DataRow newRow = result.newRow();
            // 循环处理每个数据列
            for (DataColumn dc : result.getColumns()) {
                newRow.setValue(dc.getColumnName(),
                        tempTable.compute(dc.getTag()));
            }
            result.addRow(newRow);
            // 清空临时表
            tempTable.getRows().clear();
        }
    }

    /**
     * @param context     上下文对象
     * @param displayName 显示的名称
     */
    public static Object referenceFlow(Object context, String displayName) {
        Context nodeContext = (Context) context;
        Object referenceFlowPath = nodeContext.get("FILEPATH");
        nodeContext.set("##" + displayName, referenceFlowPath);
        return true;
    }

    /**
     * 参考案例
     *
     * @param context
     * @param displayName 显示的名称
     */
    public static Object referenceCase(Object context, String displayName) {
        Context nodeContext = (Context) context;
        Object referenceFlowPath = nodeContext.get("FILEPATH");
        nodeContext.set("%%" + displayName, referenceFlowPath);
        return true;
    }

    public static boolean valueEquals(Object o1, Object o2) {
        if (o1 == null || o2 == null) return false;
        return o1.equals(o2);
    }

    public static DataTable select(Object table, String filterString) {
        DataTable dt = (DataTable) table;
        dt.getRows().clear();
        return dt.select(filterString.replace("[", "").replace("]", "]"));
    }

    private static DataTable buildTableStruct(String colString) {
        // 如果输入字段为空则返回
        if (!(colString.replace(",", "").replace(" ", "").length() > 0)) return null;
        colString = colString.replace("[", "").replace("]", "");
        String[] fieldParts;
        String[] fields = colString.split(",");
        DataTable dt = new DataTable();
        for (String f : fields) {
            fieldParts = f.trim().split(" ");
            DataColumn dc = new DataColumn();
            switch (fieldParts.length) {
                case 1:
                    dc.setColumnName(fieldParts[0]); // 设置列名
                    dc.setCaptionName(fieldParts[0]); // 设置标题
                    dc.setTag(fieldParts[0]); // 通过tag对象保存公式配置
                    dc.setDataType(com.pyrlong.dsl.DataTypes.STRING);//
                    dt.getColumns().add(dc);
                    break;
                case 2:
                    dc.setColumnName(fieldParts[1]); // 设置列名
                    dc.setCaptionName(fieldParts[1]); // 设置标题
                    dc.setTag(fieldParts[0]); // 通过tag对象保存公式配置
                    dc.setDataType(com.pyrlong.dsl.DataTypes.STRING);//
                    dt.getColumns().add(dc);
                    break;
                default:
                    break;
            }
        }
        return dt;

    }

    public static DataTable getDataTable(DataSet dataSet,
                                         Collection<String> fields) {
        String[] str = new String[fields.size()];
        return getDataTable(dataSet, fields.toArray(str));
    }

    /**
     * 功能描述： 获取输入字符串内用[]标识的变量
     *
     * @param
     * @return: List<String>
     * @author: James Cheung
     * @version: 2.0
     */
    public static List<String> getVariable(String expString) {
        List<String> ids = new ArrayList<String>();
        // 首先通过正则表达式获取排序字段包含的指标ID
        if (expString != null) {
            String regEx = "\\[[0-9|a-z|A-Z|_]+]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(expString);
            while (m.find()) {
                String id = m.group(0).replace("[", "").replace("]", "");
                if (!ids.contains(id)) ids.add(id);
            }
        }
        return ids;
    }

    /*
     * 按指定的位数对数值进行四舍五入,对小数有用
     * @param number it's type must be Number
     * @param count it's type must be Number
     */
    public static Object roundDecimal(Object number, Object count) {

        try {
            count = null == count ? ROUND_NUM : count;
            if (null == number || !(number instanceof Number)
                    || !(count instanceof Number) || number instanceof Integer) {
                return number;
            }
            double value = ((Number) number).doubleValue();
            int numInt = ((Number) count).intValue();

            return new java.math.BigDecimal(value).setScale(numInt,
                    java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            return number;
        }
    }
}
