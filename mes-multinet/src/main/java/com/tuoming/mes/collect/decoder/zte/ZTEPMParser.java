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
package com.tuoming.mes.collect.decoder.zte;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.util.StringUtil;

/**
 * Created by james on 14-6-25.
 * 中兴参数解析器，建议使用ZTEPMPartParser
 */
@Scope("prototype")
@Component("ZTEPMParser")
public class ZTEPMParser extends AbstractContentHandler {

    public ZTEPMParser() {//构造函数，初始化该类要解析的标签属性
        resultFilePrefix = "PM";
        columnMap.put("managedElement-userLabel", "");
        columnMap.put("measValue-measObjLdn", "");
        columnMap.put("measCollec-beginTime", "");
        columnMap.put("fileSender-elementType", "");

        columnTag.put("measCollec", "");
        columnTag.put("managedElement", "");
        columnTag.put("measValue", "");
        columnTag.put("fileSender", "");

        colDef = "measTypes";
        valueDef = "measResults";
        timeTag = "beginTime";
    }

    /**
     * 文件名生成规则
     */
    protected String getNewFileName() {
        StringBuilder targetCsvFile = new StringBuilder();
        targetCsvFile.append(resultFilePrefix);
        if (StringUtil.isNotEmpty(columnMap.get("managedElement-userLabel"))) {
            targetCsvFile.append("_");
            targetCsvFile.append(columnMap.get("managedElement-userLabel"));
        }
        if (StringUtil.isNotEmpty(columnMap.get("fileSender-elementType"))) {
            targetCsvFile.append("_");
            targetCsvFile.append(columnMap.get("fileSender-elementType"));
        }
        targetCsvFile.append("_");
        targetCsvFile.append(columnMap.get("measCollec-beginTime").replace("-", "").replace(":", "").replace(" ", ""));
        targetCsvFile.append(".csv");
        return targetCsvFile.toString();
    }
}
