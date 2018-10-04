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

//Created On: 13-9-13 上午10:52
package com.tuoming.mes.collect.dpp.rdbms.impl;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.handles.DataRowToCsvHandle;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapter;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.dpp.rdbms.TableSynchronizer;

/**
 * 基于CSV文件的表同步器实现
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class CsvTableSynchronizer implements TableSynchronizer {

    private static Logger logger = LogFacade.getLog4j(CsvTableSynchronizer.class);

    @Override
    public String backupData(String dbName, Collection<String> tables) {
        String backDir = AppContext.CACHE_ROOT + "backup/" + DateUtil.currentDateString("yyyy_MM_dd/");
        try {
            logger.info("Start data backup");
            DataAdapter dataAdapter = DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter();
            for (String t : tables) {
                String sql = "select * from " + t;
                String file = backDir + t + ".csv";
                logger.info("Backup " + t + " to " + file);
                DataRowToCsvHandle csvHandle = new DataRowToCsvHandle(file);
                dataAdapter.executeQuery(sql, csvHandle);
                csvHandle.close();
            }
            logger.info("Data backup is complete");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void restoreData(String dbName, Collection<String> tables, Collection<String> files, boolean clearOldData) {
        backupData(dbName, tables);
        logger.info("Start data restore");
        try {
            DataAdapter dataAdapter = DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter();
            for (String t : tables) {
                for (String f : files) {
                    File file = new File(f);
                    if (file.exists() && !file.isDirectory()) {
                        if (file.getName().toLowerCase().equals(t.toLowerCase() + ".csv")) {
                            if (clearOldData)
                                dataAdapter.executeNonQuery("truncate table " + t);
                            dataAdapter.loadfile(f, t);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.info("Data restore is complete");
    }

    @Override
    public void restoreData(String dbName, Collection<String> tables, Collection<String> files) {
        restoreData(dbName, tables, files, true);
    }

    @Override
    public void restoreData(String dbName, Collection<String> files) {
        restoreData(dbName, files, true);
    }

    @Override
    public void restoreData(String dbName, Collection<String> files, boolean clearOldData) {
        List<String> tables = new ArrayList<String>();
        for (String file : files) {
            File f = new File(file);
            if (f.exists()) {
                tables.add(f.getName().replace(".csv", ""));
            }
        }
        restoreData(dbName, tables, files, clearOldData);
    }
}
