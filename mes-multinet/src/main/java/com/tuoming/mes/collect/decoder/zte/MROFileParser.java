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

package com.tuoming.mes.collect.decoder.zte;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.util.StringUtil;

/**
 * 解析MRO文件的实现类，该类基本废弃，请使用MROPartFileParser替代
 *
 * @since 1.0.1
 */
@Scope("prototype")
@Component("MROFileParser")
public class MROFileParser extends AbstractContentHandler {
    private static Logger logger = Logger.getLogger(MROFileParser.class);

    public MROFileParser() {//构造函数，初始化该类要解析的标签属性
        resultFilePrefix = "MRO";
        columnMap.put("rnc-userLabel", "");
        columnMap.put("rnc-id", "");
        columnMap.put("fileHeader-startTime", "");
        columnMap.put("object-id", "");
        columnMap.put("object-IMSI", "");
        columnMap.put("measurement-mrName", "");
        columnTag.put("fileHeader", "");
        columnTag.put("rnc", "");
        columnTag.put("object", "");
        columnTag.put("class", "");
        columnTag.put("measurement", "");
    }

    /**
     * 文件名生成规则
     */
    protected String getNewFileName() {
        StringBuilder targetCsvFile = new StringBuilder();
        targetCsvFile.append(resultFilePrefix);
        if (StringUtil.isNotEmpty(columnMap.get("rnc-userLabel"))) {
            targetCsvFile.append("_");
            targetCsvFile.append(columnMap.get("rnc-userLabel"));
        }
        if (StringUtil.isNotEmpty(columnMap.get("rnc-id"))) {
            targetCsvFile.append("_");
            targetCsvFile.append(columnMap.get("rnc-id"));
        }
        if (StringUtil.isNotEmpty(columnMap.get("measurement-mrName"))) {
            targetCsvFile.append("_");
            targetCsvFile.append(columnMap.get("measurement-mrName"));
        }
        targetCsvFile.append("_");
        targetCsvFile.append(columnMap.get("fileHeader-startTime").replace("-", "").replace(":", "").replace(" ", ""));
        targetCsvFile.append("_").append(System.currentTimeMillis());
        targetCsvFile.append(".csv");
        return targetCsvFile.toString();
    }
}
