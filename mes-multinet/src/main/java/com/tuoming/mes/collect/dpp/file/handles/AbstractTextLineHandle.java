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

package com.tuoming.mes.collect.dpp.file.handles;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.Convert;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataRowHandler;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.file.TextLine;
import com.tuoming.mes.collect.dpp.file.TextLineHandle;
import com.tuoming.mes.collect.dpp.models.TextItemRule;
import com.tuoming.mes.collect.dpp.models.TextLogParser;

/**
 * 文本文件解析抽象工具类，提供文本文件读取、解析、存储的主要处理流程实现
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.6
 */
public abstract class AbstractTextLineHandle implements TextLineHandle {

    static String MAX_INT_STRING = Convert.toString(Integer.MAX_VALUE);
    static String MAX_DOUBLE_STRING = Convert.toString(Double.MAX_VALUE);
    private static Logger logger = LogFacade.getLog4j(AbstractTextLineHandle.class);
    protected Map<String, String> _itemAdded = new LinkedHashMap<String, String>();
    protected TextLine CurrentLine;
    protected DataRow CurrentRow;
    protected DataTable CurrentTable;
    protected DataRowHandler dataRowHandler;
    protected TextLogParser logParser;
    protected Map<String, String> envs;
    protected String firstColumnName = "";
    private List<String> headerRowRegex;
    private List<String> ignoreRegex;

    public AbstractTextLineHandle() {

    }

    public AbstractTextLineHandle(TextLogParser parser) {
        setLogParser(parser);

    }

    private String getFirstColumnName() {
        for (Map.Entry<String, String> entry : _itemAdded.entrySet()) {
            firstColumnName = entry.getKey();
            return entry.getKey();
        }
        return "";
    }

    protected void newItemAddedMap() {
        String firstColumn = getFirstColumnName();
        _itemAdded = new LinkedHashMap<String, String>();
        if (StringUtil.isNotEmpty(firstColumn))
            _itemAdded.put(firstColumn, null);
    }

    public String getTrigger() {
        return logParser.getTrigger();
    }

    public List<String> getHeaderRowRegex() {
        return headerRowRegex;
    }

    public List<String> getIgnoreRegex() {
        return ignoreRegex;
    }

    public TextLogParser getLogParser() {
        return logParser;
    }

    public void setLogParser(TextLogParser logParser) {
        ignoreRegex = new ArrayList<String>();
        headerRowRegex = new ArrayList<String>();
        if (StringUtil.isNotEmpty(logParser.getIgnoreRegex())) {
            String[] vals = logParser.getIgnoreRegex().split(DPPConstants.FILE_REGEX_SPLIT);
            for (String s : vals) {
                ignoreRegex.add(s);
            }
        }
        if (StringUtil.isNotEmpty(logParser.getHeaderRegex())) {
            String[] vals = logParser.getHeaderRegex().split(DPPConstants.FILE_REGEX_SPLIT);
            for (String s : vals) {
                headerRowRegex.add(s);
            }
        }
        this.logParser = logParser;
    }

    protected void newRow() throws Exception {
        // 生成新的数据行
        CurrentRow = CurrentTable.newRow();
        if (CurrentRow != null) {
            for (TextItemRule rule : logParser.getItemRuleList()) {
                CurrentRow.setValue(rule.getName(), rule.getDefaultValue() == null ? null : DSLUtil.getDefaultInstance().buildString(rule.getDefaultValue() + "", envs));
            }
        }
    }

    public void setDataRowHandle(DataRowHandler hdl) {
        dataRowHandler = hdl;
    }

    public void processLine(TextLine line, Map<String, String> envs) {
        this.envs = envs;
        if (StringUtil.isNotEmpty(logParser.getLineFormater()) && StringUtil.isNotEmpty(logParser.getLineFormater().trim()))
            line.setLine((String) DSLUtil.getDefaultInstance().compute(logParser.getLineFormater(), envs));
    }

    public void buildTable() throws Exception {
        CurrentTable = new DataTable();
        for (TextItemRule rule : logParser.getItemRuleList()) {
            logger.debug("Build table add column:" + rule.getName());
            CurrentTable.addColumn(rule.getName(), rule.getDataType());
        }
    }

    public void close() {
        this.dataRowHandler.close();
        CurrentTable.clearup();
    }

    public boolean isValid(Object val) {
        if (val == null)
            return false;
        if (val instanceof Date)
            return true;
        if (val.toString().equals("<NULL>"))
            return false;
        if (val.toString().equals(MAX_INT_STRING) || val.toString().equals(MAX_DOUBLE_STRING))
            return false;
        if (StringUtil.isNotEmpty(val.toString().trim()))
            return true;
        return false;
    }

}
