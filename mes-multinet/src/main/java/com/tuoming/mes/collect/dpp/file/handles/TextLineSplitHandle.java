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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.file.TextLine;
import com.tuoming.mes.collect.dpp.models.TextItemRule;

@Scope("prototype")
@Component("TextLineSplitHandle")
public class TextLineSplitHandle extends AbstractTextLineHandle {

    private static Logger logger = LogFacade.getLog4j(TextLineSplitHandle.class);
    private Map<String, Integer> valMap;

    /**
     * 对读取的每行数据进行处理，支持分割和正则匹配方式
     */
    @Override
    public synchronized void processLine(TextLine line, Map<String, String> envs) {
        this.envs = envs;
        if (line.isMatch(getLogParser().getHeaderRegex())) {
            // 如果是定义行
            logger.debug("Found header for " + getLogParser().getHeaderRegex());
            valMap = new LinkedHashMap<String, Integer>();
            String[] cols = StringUtil.split(line.toString(), getLogParser().getSplitRegex());
            int i = 0;
            for (String c : cols) {
                valMap.put(c.trim(), i++);
            }
        } else if (line.isMatch(getLogParser().getValueLineRegex())) {
            // 如果是数据行
            if (valMap == null) return;
            String[] vals = StringUtil.split(line.toString(), getLogParser().getSplitRegex());
            // 生成新的数据行
            try {
                newRow();
                Map<String, Object> varMap = new HashMap<String, Object>();
                for (Entry<String, Integer> c : valMap.entrySet()) {
                    if (vals.length > c.getValue())
                        varMap.put(c.getKey(), vals[c.getValue()]);
                    else {
                        logger.debug("Index out of bound, " + c.getKey() + "\n" + line);
                    }
                }
                if (CurrentRow != null) {
                    // 设置默认值
                    for (TextItemRule rule : logParser.getItemRuleList()) {
                        if (StringUtil.isNotBlank(rule.getValueExpression())) {
                            Object value = DSLUtil.getDefaultInstance().buildString(rule.getValueExpression(), varMap);
                            if (isValid(value) && !value.equals(rule.getValueExpression()))
                                CurrentRow.setValue(rule.getName(), value.toString().trim());
                        }
                    }

                    if (dataRowHandler != null)
                        dataRowHandler.process("", CurrentRow);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            //否则查看是否需要正则解析 ，此处临时做如下约定：即正则解析结果均是需要缓存的数据
            for (TextItemRule rule : logParser.getItemRuleList()) {
                if (StringUtil.isNotEmpty(rule.getRegexFilter())) {
                    if (line.isMatch(rule.getRegexFilter())) {
                        Object result = DSLUtil.getDefaultInstance().compute(rule.getValueExpression(), envs);
                        rule.setDefaultValue(result + "");
                    }
                }
            }
        }
    }

    public void close() {
        super.close();
        valMap = null;
    }

    @Override
    public void done() {
        valMap = new HashMap<String, Integer>();
    }

}
