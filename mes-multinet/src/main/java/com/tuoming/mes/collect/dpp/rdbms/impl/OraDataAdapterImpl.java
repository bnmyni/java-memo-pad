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

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.ProcessHelper;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileAppend;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.rdbms.ExecuteResult;

/**
 * 此类描述的是： Oracle数据库操作类
 *
 * @author: guangfu.wang
 * @version: 2.0
 */
public class OraDataAdapterImpl extends AbstractDataAdapter {
    private static Logger Log = LogFacade.getLog4j(OraDataAdapterImpl.class);

    protected String getTestAliveSql() {
        return "select sysdate from dual";//
    }

    @Override
    protected String getDateTimeExpression(Object date, String format) {
        return GetDateTimeExpression(date);
    }

    private String GetDateTimeExpression(Object date) {
        return "to_date('" + date + "', 'yyyy-mm-dd hh24:mi:ss')";
    }

    @Override
    protected String getNullValue() {
        return "null";
    }

    private String genOraTableSelectField(String targetTable) throws Exception {
        String tableSchemeSql = "select  column_name,  data_type from user_tab_columns where lower(table_name)=lower('" + targetTable + "')  order by column_id ";
        ExecuteResult er = executeQuery(tableSchemeSql);
        ResultSet result = er.getResultSet();
        String selectField = "";
        if (result != null) {
            while (result.next()) {
                String dataType = result.getString("data_type");
                // 如果是时间类型
                if (dataType.equals("DATE")) {
                    selectField = selectField + "to_char(" + result.getString("column_name") + ",'YYYY-MM-DD HH24:MI:SS') " + result.getString("column_name") + ",";
                } else if (dataType.equals("TIMESTAMP(6)")) {
                    selectField = selectField + "to_char(" + result.getString("column_name") + ",'YYYY-MM-DD HH24:MI:SS.FF6') " + result.getString("column_name") + ",";
                } else {
                    selectField = selectField + result.getString("column_name") + ",";
                }
            }
        }
        er.close();
        returnConnection();
        return selectField.length() > 0 ? selectField.substring(0, selectField.length() - 1) : "*";
    }

    /**
     * 对指定目标表生成sqlldr控制文件，如果文件已经存在则
     *
     * @param targetTable 目标表
     * @return 控制文件名称
     * @throws Exception
     * @author James Cheung Date:Sep 2, 2012
     */
    public String genOraControlFile(String infile, String targetTable) throws Exception {
        String ctlFile = " LOAD DATA characterset  " + csvFileEncoding;
        File file = new File(infile);
        ctlFile = ctlFile + "\n INFILE '" + file.getAbsolutePath() + "' \n into table " + targetTable + "\n APPEND \n FIELDS TERMINATED BY ','\nOPTIONALLY ENCLOSED BY '~'\nTRAILING NULLCOLS\n(";
        // 读取目标表结构
        String tableSchemeSql = "select  column_name,  data_type from user_tab_columns where lower(table_name)=lower('" + targetTable + "')  order by column_id ";
        ExecuteResult er = executeQuery(tableSchemeSql);
        ResultSet result = er.getResultSet();
        if (result != null) {
            while (result.next()) {
                String dataType = result.getString("data_type");
                // 如果是时间类型
                if (dataType.equals("DATE")) {
                    ctlFile = ctlFile + result.getString("column_name") + " DATE  \"YYYY-MM-DD HH24:MI:SS\",";
                } else if (dataType.equals("TIMESTAMP(6)")) {
                    ctlFile = ctlFile + result.getString("column_name") + " TIMESTAMP(6) \"YYYY-MM-DD HH24:MI:SS.FF6\",";
                } else {
                    ctlFile = ctlFile + result.getString("column_name") + ",";
                }
            }
        }
        er.close();
        ctlFile = ctlFile.substring(0, ctlFile.length() - 1) + ")";
        String ctlFileName = infile + ".ctl";
        FileOper.checkAndCreateForder(ctlFileName);
        if (FileOper.isFileExist(ctlFileName))
            FileOper.delFile(ctlFileName);
        FileAppend.appendMethodBytes(ctlFileName, ctlFile);
        file = new File(ctlFileName);
        return file.getAbsolutePath();
    }


    @Override
    public void loadfile(String filename, String targetTable) {
        try {
            String ctlFile = genOraControlFile(filename, targetTable);
            logger.info("load data to " + getConnectionStringSetting().getName() + "-" + targetTable + " via sqlldr");
            String sqlldr;
            Map<String, String> env = new HashMap<String, String>();
            env.put("CTLFILE", ctlFile);
            env.put("DATAFILE", filename);
            env.put("TABLENAME", targetTable);
            String ip = StringUtil.getMatchString(getConnectionStringSetting().getUrl(), "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
            String port = StringUtil.getMatchString(getConnectionStringSetting().getUrl(), ":[0-9]+");
            String sid = StringUtil.getMatchString(getConnectionStringSetting().getUrl(), ":[\\w]+$").replace(":", "/");
            sqlldr = "sqlldr " + getConnectionStringSetting().getUsername() + "/" + getConnectionStringSetting().getPassword() + "@//" + ip + port + sid + " rows=10000   readsize=20971520 bindsize=20971520  direct=true   control=" + ctlFile + "  log=" + ctlFile + ".log";
            logger.info(sqlldr);
            synchronized (getSynchronizedHandle(targetTable)) {
                if (StringUtil.isNotEmpty(sqlldr))
                    ProcessHelper.startProcess("sqlldr_" + Thread.currentThread().getId() + "_" + DateUtil.getTimeinteger(), sqlldr, true);
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
