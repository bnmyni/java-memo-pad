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

package com.tuoming.mes.collect.dpp.handles;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import com.pyrlong.Envirment;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapter;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.dpp.rdbms.DbType;

public class DataRowToSqlHandle extends AbstractDataRowHandler {
    private static Logger logger = LogFacade.getLog4j(DataRowToSqlHandle.class);
    BufferedWriter out;
    private DataAdapter dataAdapter;
    private String targetTable;

    public DataRowToSqlHandle(DbType dbType, String targetTable, String fileName) throws UnsupportedEncodingException, FileNotFoundException {
        dataAdapter = DataAdapterPool.getDataAdapter(dbType);
        FileOper.checkAndCreateForder(fileName);
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), "utf-8"));
        this.targetTable = targetTable;
    }

    @Override
    public synchronized void process(String key, DataRow row) {
        // 写入文件
        try {
            String line = dataAdapter.genSqlInsert(targetTable, row);
            out.write(line);
            out.write(";" + Envirment.LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (out != null)
                out.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void loadTag(Object o) {

    }
}
