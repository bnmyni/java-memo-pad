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

import java.util.HashMap;
import java.util.Map;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.ProcessHelper;
import com.pyrlong.util.StringUtil;

/**
 * @author James Cheung
 */
public class SqlServerDataAdapter extends AbstractDataAdapter {

    protected String getTestAliveSql() {
        return "select 1 ";//
    }

    @Override
    protected String getDateTimeExpression(Object date, String format) {
        return GetDateTimeExpression(date);
    }

    private String GetDateTimeExpression(Object date) {
        return "'" + date + "'";
    }

    @Override
    protected String getNullValue() {
        return "null";
    }

    /**
     * 使用前需要运行环境支持BCP命令，并在系统配置表或文件里配置BCP命令，格式为： bcp_cmd=    bcp "ZTEDB.dbo.[TABLENAME]" in "[DATAFILE]" -c -t"," -b
     * 10000 -S"localhost" -U"sa" -P"tmcsoft
     *
     * @param filename
     * @param targetTable
     */
    @Override
    public void loadfile(String filename, String targetTable) {
        try {
            logger.info("load data to " + getConnectionStringSetting().getName() + "-" + targetTable + " via bcp");
            String bcp = "bcp \"[DBNAME].dbo.[TABLENAME]\" in \"[DATAFILE]\" -c -t\",\" -b 10000 -S\"[SIP][SPORT]\" -U\"[UID]\" -P\"[PWD]\"";
            Map<String, String> env = new HashMap<String, String>();
            env.put("DATAFILE", filename);
            env.put("TABLENAME", targetTable);
            String ip = StringUtil.getMatchString(getConnectionStringSetting().getUrl(), "(//)([A-Z|a-z|0-9|.]+)", 2);
            String port = StringUtil.getMatchString(getConnectionStringSetting().getUrl(), ":[0-9]+");
            String db = StringUtil.getMatchString(getConnectionStringSetting().getUrl(), "(/)([^.]+$)", 2);
            env.put("DBNAME", db);
            env.put("SIP", ip);
            env.put("SPORT", port.replace(":", ","));
            env.put("UID", getConnectionStringSetting().getUsername());
            env.put("PWD", getConnectionStringSetting().getPassword());
            synchronized (getSynchronizedHandle(targetTable)) {
                bcp = DSLUtil.getDefaultInstance().buildString(bcp, env);
                logger.info(bcp);
                if (StringUtil.isNotEmpty(bcp))
                    ProcessHelper.startProcess("bcp_" + Thread.currentThread().getId() + "_" + DateUtil.getTimeinteger(), bcp, true);
            }
            logger.info("Load data from " + filename + " to table " + getConnectionStringSetting().getName() + "-" + targetTable + " done");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void renameTable(String oldName, String newName) {

    }

}
