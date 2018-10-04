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
package com.tuoming.mes.collect.dpp.rdbms;

import java.util.Map;
import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.tuoming.mes.collect.dpp.configuration.ConnectionStringSettingsCollection;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataRowHandler;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.handles.DataRowToCsvHandle;
import com.tuoming.mes.collect.dpp.handles.DataRowToSqlHandle;
import com.tuoming.mes.collect.dpp.handles.DataRowToXlsxHandle;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;


/**
 * @author James Cheung
 */
public class DbOperation {

    public static void executeNonQuery(String dbName, String sql) throws Exception {
        DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter().executeNonQuery(sql);
    }

    public static void executeSqlFile(String dbName, String scriptFile) throws Exception {
        executeSqlFile(dbName, scriptFile, Envirment.getEnvs());
    }

    public static void executeSqlFile(String dbName, String scriptFile, Map context) throws Exception {
        DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter().excuteSqlFile(scriptFile, context);
    }

    public static void executeQuery(String dbName, String sql, DataRowHandler handler) throws Exception {
        DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter().executeQuery(sql, handler);
    }

    public static DataTable queryTable(String dbName, String sql) throws Exception {
        return DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter().queryTable(sql);
    }

    public static void query2Csv(String dbName, String sql, String file) throws Exception {
        DataRowToCsvHandle data2csvHandle = new DataRowToCsvHandle(file);
        DbOperation.executeQuery(dbName, sql, data2csvHandle);
    }

    public static void query2Sql(DbType dbType, String dbName, String tableName, String sql, String file) throws Exception {
        DataRowToSqlHandle handle = new DataRowToSqlHandle(dbType, tableName, file);
        DbOperation.executeQuery(dbName, sql, handle);
    }

    public static void query2xlsx(String dbName, String sql, String file, String sheetName) throws Exception {
        DataRowToXlsxHandle handle = new DataRowToXlsxHandle(file);
        handle.setSheetName(sheetName);
        DbOperation.executeQuery(dbName, sql, handle);
    }

    public static ConnectionStringSetting getConnection(String name) {
        return ConnectionStringSettingsCollection.getConnectionSetting(name);
    }

    public static Map queryDic(String dbName, String sql) throws Exception {
        Map result = Maps.newHashMap();
        DataTable table = queryTable(dbName, sql);
        if (table != null) {
            for (DataRow row : table.getRows()) {
                Object name = row.getValue(0);
                Object val = row.getValue(1);
                if (name != null && val != null) {
                    result.put(name, val);
                }
            }
        }
        return result;
    }

    public static Object queryOne(String dbName, String sql) throws Exception {
        DataTable table = queryTable(dbName, sql);
        if (table != null) {
            if (table.getRows().size() > 0) {
                return table.getValue(0, 0);
            }
        }
        return null;
    }
}
