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

package com.tuoming.mes.collect.dpp.rdbms.impl;

import com.pyrlong.Envirment;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;

public class MysqlDataAdapterImpl extends AbstractDataAdapter {
    @Override
    protected String getTestAliveSql() {
        return "select 1";
    }

    @Override
    protected String getDateTimeExpression(Object date, String format) {
        return GetDateTimeExpression(date);
    }

    @Override
    protected String getNullValue() {
        return "null";
    }

    private String GetDateTimeExpression(Object date) {
        return "STR_TO_DATE('" + date + "', '%Y-%m-%d %H:%i:%s')";
    }


    /*
     * (non-Javadoc)通过命令导入sql文件
     * @see com.tuoming.mes.collect.dpp.rdbms.DataAdapter#loadfile(java.lang.String, java.lang.String)
     */
    @Override
    public void loadfile(String filename, String targetTable) {
        try {
            logger.info("Load data  from " + filename + " to table " + getConnectionStringSetting().getName() + "-" + targetTable);
            String sqlCmd = "load data local infile '" + filename + "' " +
                    "into table " + targetTable + " character set  " + csvFileEncoding +
                    " fields terminated by '" + split + "'  optionally enclosed by '" + enclosed + "'  lines terminated by '" + Envirment.LINE_SEPARATOR.replace("\n", "\\n").replace("\r", "\\r") + "'";
            logger.info(sqlCmd);
            synchronized (getSynchronizedHandle(targetTable)) {
                executeNonQuery(sqlCmd);
            }
            logger.info("Load data from " + filename + " to table " + getConnectionStringSetting().getName() + "-" + targetTable + " done!");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void lockToUpdate(String tableName) {
        try {
            executeNonQuery(String.format("LOCK TABLES `%s` WRITE", tableName));
            executeNonQuery(String.format("/*!40000 ALTER TABLE `%s` DISABLE KEYS */", tableName));
            logger.info(String.format("Lock table %s", tableName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void unlock(String tableName) {
        try {
            executeNonQuery(String.format("/*!40000 ALTER TABLE `%s` ENABLE KEYS */", tableName));
            executeNonQuery(String.format("UNLOCK TABLES"));
            logger.info(String.format("Unlock table %s", tableName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void disableKeyCheck() {
        executeNonQuery("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */");
        executeNonQuery("/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */");
        executeNonQuery("/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */");
        executeNonQuery("/*!40101 SET NAMES utf8 */");
        executeNonQuery("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */");
        executeNonQuery("/*!40103 SET TIME_ZONE='+00:00' */");
        executeNonQuery("/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */");
        executeNonQuery("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */");
        executeNonQuery("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */");
        executeNonQuery("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */");
    }

    @Override
    public void enableKeyCheck() {
        executeNonQuery("/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */");
        executeNonQuery("/*!40101 SET SQL_MODE=@OLD_SQL_MODE */");
        executeNonQuery("/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */");
        executeNonQuery("/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */");
        executeNonQuery("/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */");
        executeNonQuery("/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */");
        executeNonQuery("/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */");
        executeNonQuery("/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */");
    }


    @Override
    public void renameTable(String oldName, String newName) {
        executeNonQuery(String.format("rename table %s to %s", oldName, newName));
    }


    private void disableForeignKey() {
        executeNonQuery("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0");
    }

    private void enableForeignKey() {
        executeNonQuery("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
    }


    public void dropAll() {
        try {

            DataTable table = queryTable("show tables");
            for (DataRow row : table.getRows()) {
                logger.info("drop table " + row.getValue(0));
                executeNonQuery("drop table " + row.getValue(0));
            }
            logger.info("all tables dropped");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void clearAll() {
        try {
            DataTable table = queryTable("show tables");
            for (DataRow row : table.getRows()) {
                logger.info("truncate table " + row.getValue(0));
                executeNonQuery("truncate table " + row.getValue(0));
            }
            logger.info("all tables truncated");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
