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

// Created On: 13-7-22 下午3:59
package com.tuoming.mes.collect.dpp.handles;

import org.apache.log4j.Logger;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.6
 */

public class DataRowToTableHandle extends AbstractDataRowHandler {

    private static Logger logger = LogFacade.getLog4j(DataRowToTableHandle.class);
    DataTable resultTable = null;

    public DataTable getTable() {
        return resultTable;
    }

    public DataRowToTableHandle() {

    }

    @Override
    public void process(String key, DataRow row) {
        if (resultTable == null)
            resultTable = row.getTable();
        try {
            resultTable.addRow(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void loadTag(Object o) {

    }
}
