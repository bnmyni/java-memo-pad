/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.json.JSONArray;
import com.pyrlong.json.JSONException;
import com.pyrlong.json.JSONObject;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.file.TextLine;
import com.tuoming.mes.collect.dpp.models.TextItemRule;

/**
 * Created by james on 14-3-10.
 */
@Scope("prototype")
@Component("JSONFileHandle")
public class JSONFileHandle extends AbstractTextLineHandle {
    private static Logger logger = LogFacade.getLog4j(JSONFileHandle.class);

    /**
     * 重写增加对JSON字符串的处理
     *
     * @param line
     * @param envs
     */
    @Override
    public synchronized void processLine(TextLine line, Map<String, String> envs) {
        super.processLine(line, envs);
        //如果是数据行
        if (line.isMatch(getLogParser().getValueLineRegex())) {
            try {
                JSONArray jsonArray = new JSONArray(line.toString());
                int size = jsonArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject valueObject = jsonArray.getJSONObject(i);
                    try {
                        newRow();
                        for (TextItemRule rule : logParser.getItemRuleList()) {
                            if (StringUtil.isNotEmpty(rule.getValueExpression()))
                                CurrentRow.setValue(rule.getName(), DSLUtil.getDefaultInstance().buildString(rule.getValueExpression(), valueObject.getMap()));
                        }
                        if (dataRowHandler != null && CurrentRow != null) {
                            dataRowHandler.process("", CurrentRow);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        //否则查看是否需要正则解析 ，此处临时做如下约定：即正则解析结果均是需要缓存的数据
        for (TextItemRule rule : logParser.getItemRuleList()) {
            if (StringUtil.isNotEmpty(rule.getRegexFilter())) {
                if (line.isMatch(rule.getRegexFilter())) {
                    Object result = DSLUtil.getDefaultInstance().compute(rule.getValueExpression(), envs);
                    logger.debug(rule.getName() + "->" + result);
                    rule.setDefaultValue(result + "");
                }
            }
        }
    }

    @Override
    public void done() {

    }

}
