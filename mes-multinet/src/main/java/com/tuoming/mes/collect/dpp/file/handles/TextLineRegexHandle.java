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

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.file.TextLine;
import com.tuoming.mes.collect.dpp.models.TextItemRule;

/**
 * @author james
 */
@Scope("prototype")
@Component("TextLineRegexHandle")
public class TextLineRegexHandle extends AbstractTextLineHandle {

    private static Logger logger = LogFacade.getLog4j(TextLineRegexHandle.class);
    private boolean dataFound = false;

    public void close() {
        super.close();
    }

    @Override
    public void done() {
        _itemAdded.clear();
        if (dataFound) {
            raiseRow();
            this.dataFound = false;
        }
        CurrentRow = null;
    }

    private Object getValue(TextItemRule textItemRule) {
        try {
            if (StringUtil.isNotEmpty(textItemRule.getValueExpression())) {
                //Object result = dsl.compute(textItemRule.getValueExpression(), paras);
                Object result = DSLUtil.getDefaultInstance().buildString(textItemRule.getValueExpression(), envs);
                logger.debug(CurrentLine + "" + textItemRule.getValueExpression() + " ==>" + result);
                return result;
            }
        } catch (Exception ex) {
            logger.error(textItemRule.getValueExpression() + "\n" + ex.getMessage(), ex);
        }
        return null;
    }

    private void raiseRow() {
        // 对已经生成的数据行进行处理
        if (dataRowHandler != null && CurrentRow != null) {
            dataRowHandler.process("", CurrentRow);
        }
        // 生成新的数据行
        CurrentRow = null;
        for (TextItemRule textItemRule : logParser.getItemRuleList()) {
            if (_itemAdded.containsKey(textItemRule.getName()) && !textItemRule.getCachedItem()) {
                _itemAdded.remove(textItemRule.getName());
            }
        }
    }

    private void setValue(TextItemRule textItemRule) throws Exception {
        // 如果当前数据行还没有初始化，或者当前要保存的列已经加过了，则生成一个新的数据行保存对象
        if (CurrentRow == null) {
            newRow();
            dataFound = false;
        }
        Object value = getValue(textItemRule);
        dataFound = !textItemRule.getCachedItem() || dataFound;
        if (isValid(value)) {
            CurrentRow.setValue(textItemRule.getName(), value);
            if (textItemRule.getCachedItem())
                textItemRule.setDefaultValue(value + "");
        }
        _itemAdded.put(textItemRule.getName(), null);
    }

    public synchronized void processLine(TextLine line, Map<String, String> envs) {
        this.envs = envs;
        if (CurrentLine == null)
            CurrentLine = new TextLine("");
        synchronized (CurrentLine) {
            CurrentLine = line;
            boolean founded = false;
            for (TextItemRule textItemRule : logParser.getItemRuleList()) {
                if (line.isMatch(textItemRule.getRegexFilter())) {
                    if (_itemAdded.containsKey(textItemRule.getName())) {
                        founded = true;
                        _itemAdded.remove(textItemRule.getName());
                    }
                }
            }
            if (founded && dataFound)
                raiseRow();
            for (TextItemRule textItemRule : logParser.getItemRuleList()) {
                if (line.isMatch(textItemRule.getRegexFilter())) {
                    try {
                        setValue(textItemRule);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
