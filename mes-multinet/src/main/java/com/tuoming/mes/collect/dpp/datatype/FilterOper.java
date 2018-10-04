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

// Created On: 13-8-8 下午9:14
package com.tuoming.mes.collect.dpp.datatype;

import org.apache.log4j.Logger;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import com.pyrlong.json.JSONArray;
import com.pyrlong.json.JSONException;
import com.pyrlong.json.JSONObject;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.Convert;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.models.TableColumnInfo;
import com.tuoming.mes.collect.dpp.models.TableInfo;

/**
 * 定义用于数据过滤的操作符及相关方法
 * {"groupOp":"AND","rules":[{"field":"email","op":"cn","data":"1"},{"field":"orderno","op":"ge","data":"2"}]}
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class FilterOper {

    private static Logger logger = LogFacade.getLog4j(FilterOper.class);

    private static Map<String, String> Q2Oper;

    public FilterOper() {
        Q2Oper = new HashMap();
        //['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
        Q2Oper.put("eq", " = ");
        Q2Oper.put("ne", " <> ");
        Q2Oper.put("lt", " < ");
        Q2Oper.put("le", " <= ");
        Q2Oper.put("gt", " > ");
        Q2Oper.put("ge", " >= ");
        Q2Oper.put("bw", " LIKE ");  //以..开始
        Q2Oper.put("bn", " NOT LIKE ");         //不以什么开始
        Q2Oper.put("in", " IN ");
        Q2Oper.put("ni", " NOT IN ");
        Q2Oper.put("ew", " LIKE ");
        Q2Oper.put("en", " NOT LIKE ");
        Q2Oper.put("cn", " LIKE ");
        Q2Oper.put("nc", " NOT LIKE ");
        Q2Oper.put("nu", " is null ");
        Q2Oper.put("nn", " is  not null");

    }

    public String constructWhere(String filter, TableInfo tableInfo) throws JSONException {
        String query = "";
        logger.debug(filter);
        StringBuffer queryBuffer = new StringBuffer();
        if (!filter.isEmpty()) {
            JSONObject jsono = new JSONObject(filter);
            if (jsono != null) {
                String group = jsono.get("groupOp").toString();
                JSONArray rules = (JSONArray) jsono.get("rules");
                int count = rules.length();
                for (int i = 0; i < count; i++) {
                    JSONObject jsonObject = rules.getJSONObject(i);
                    String field = jsonObject.getString("field");
                    String op = jsonObject.getString("op");
                    String val = jsonObject.getString("data");
                    if (StringUtil.isEmpty(val))
                        continue;
                    TableColumnInfo columnInfo = tableInfo.getColumnInfo(field);
                    if (columnInfo == null) {
                        for (TableColumnInfo col : tableInfo.getColumnInfoSet()) {
                            if (col.isPrimary()) {
                                field = col.getFieldName();
                                columnInfo = col;
                            }
                        }
                    }
                    op = columnInfo.getFilterOP();
                    if (StringUtil.isEmpty(op)) continue;
                    queryBuffer.append(field);
                    queryBuffer.append(" ");
                    queryBuffer.append(Q2Oper.get(op));
                    queryBuffer.append(" ");
                    queryBuffer.append(formatValue(op, val, columnInfo));
                    queryBuffer.append(" ");
                    queryBuffer.append(group);
                    queryBuffer.append(" ");
                }
                query = queryBuffer.toString().trim();
                if (query.endsWith(group))
                    query = query.substring(0, query.length() - group.length());
            }
        }
        return query;
    }

    private String formatValue(String oper, String value, TableColumnInfo dc) {
        if (DataTypes.isBoolean(dc.getDataType()))
            value = Convert.toBoolean(value) ? "1" : "0";
        if (dc != null) {
            if (StringUtil.isEmpty(value)) {
                return "";
            } else {
                logger.debug(oper + ">>" + value);
                if (oper.equals("cn") || oper.equals("nc"))
                    value = "%" + value + "%";
                else if (oper.equals("bw") || oper.equals("bn")) {
                    value = value + "%";
                } else if (oper.equals("ew") || oper.equals("en"))
                    value = "%" + value;
            }
        }
        value = getValue(value, dc);
        if (oper.equals("in") || oper.equals("ni")) {
            value = "(" + value + ")";
        }
        logger.debug(oper + ",FormatValue=" + value);
        return value;
    }

    private String getValue(String value, TableColumnInfo dc) {
        if (dc.getDataType() == Types.NCHAR || dc.getDataType() == Types.NCLOB
                || dc.getDataType() == Types.CHAR || dc.getDataType() == Types.NVARCHAR || dc.getDataType() == Types.VARCHAR)
            return "'" + value.replace(",", "','") + "'";
        else if (dc.getDataType() == Types.DATE || dc.getDataType() == Types.TIMESTAMP) {
            int idx = value.toString().indexOf(".");
            if (idx > 0)
                value = value.toString().substring(0, idx);
            return "'" + value + "'";
        } else if (dc.getDataType() == Types.BIGINT
                || dc.getDataType() == Types.BIT
                || dc.getDataType() == Types.DECIMAL
                || dc.getDataType() == Types.DOUBLE
                || dc.getDataType() == Types.FLOAT
                || dc.getDataType() == Types.INTEGER
                || dc.getDataType() == Types.NUMERIC
                || dc.getDataType() == Types.TINYINT) {
            return value;
        }
        return value;
    }
}
