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

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import com.pyrlong.collection.CollectionsBase;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.models.DataColumMapping;

/**
 * @author James Cheung
 */
public final class DataCalculate {

    private static Logger logger = LogFacade.getLog4j(DataCalculate.class);
    private static Map<String, CollectionsBase<DataColumMapping>> maps = new HashMap<String, CollectionsBase<DataColumMapping>>();

    public static CollectionsBase<DataColumMapping> getDataColumnMap(String dataColumnMapName) {
        if (maps.containsKey(dataColumnMapName)) {
            return maps.get(dataColumnMapName);
        } else {
            CollectionsBase<Object> map = ConfigurationManager.getDefaultConfig().getAdvanceObjectCollection().get(dataColumnMapName);
            CollectionsBase<DataColumMapping> result = new CollectionsBase<DataColumMapping>();
            for (Object o : map) {
                result.add((DataColumMapping) o);
            }
            maps.put(dataColumnMapName, result);
            return result;

        }
    }

    /**
     * 根据配置的计算公式填充计算列公式
     *
     * @param table 要计算的用于存储源数据的DataTable对象
     * @param map   字段映射及计算方法配置
     * @author James Cheung
     * Date:Oct 16, 2012
     */
    public static void tableCalculate(DataTable table, CollectionsBase<DataColumMapping> map) {
        if (table != null) table.calculate(map);
        logger.warn("tableCalculate" + "table is null!");
    }
}
