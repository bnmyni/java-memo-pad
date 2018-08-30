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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.datatype.DataRow;

/**
 * @author James Cheung
 */
public class DataRowToCsvHandle extends AbstractDataRowHandler {
    private static Logger logger = LogFacade.getLog4j(DataRowToCsvHandle.class);
    BufferedWriter out;
    static String split = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_SPLIT_CHAR, ",");
    final static String codeName = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_ENCODING, "utf-8");
    boolean includeHeader = ConfigurationManager.getDefaultConfig().getBoolean(DPPConstants.FILE_PRINT_HEADER);
    int record_count = 0;
    int fileIdx = 0;
    static String nullValue = "NULL";
    String targetFileName;
    private int maxRowPerFile = 0;

    public void setMaxRowPerFile(int nm) {
        maxRowPerFile = nm;
    }

    public DataRowToCsvHandle(String targetFileName) throws UnsupportedEncodingException, FileNotFoundException {
        this.targetFileName = targetFileName;
        FileOper.checkAndCreateForder(targetFileName);
        nullValue = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_NULL_VALUE, nullValue);
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName, false), codeName));
    }

    public DataRowToCsvHandle(String targetFileName, boolean append, boolean includeHeader) throws UnsupportedEncodingException, FileNotFoundException {
        this.includeHeader = includeHeader;
        if (this.includeHeader) {
            if (!FileOper.isFileExist(targetFileName)) {
                this.includeHeader = true;
            } else if (FileOper.getLength(targetFileName) == 0) {
                this.includeHeader = true;
            } else {
                this.includeHeader = false;
            }
        }
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileName, append),Charset.defaultCharset()));
    }

    public void close() {
        try {
            if (out != null) {
                if (maxRowPerFile > 0)
                    record_count = maxRowPerFile * fileIdx + record_count;
                logger.info("Save " + record_count + " records to  " + targetFileName);
                out.close();
                out = null;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void writeHeader(DataRow row) throws IOException {
        int count = row.getColumns().size();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (row.getColumns().get(i).isDisplayed()) {
                line.append(row.getColumns().get(i).getCaptionName());
                line.append(split);
            }
        }
        // 写入文件
        out.write(line.toString().substring(0, line.length() - 1));
        out.write(Envirment.LINE_SEPARATOR);
    }

    StringBuilder line = new StringBuilder();

    public synchronized void process(String key, DataRow row) {
        try {
            if (row == null) return;
            if (includeHeader) {
                writeHeader(row);
                includeHeader = false;
            }
            int count = row.getColumns().size();
            line.setLength(0);
            for (int i = 0; i < count; i++) {
                Object value = row.getValue(i);
                if (value == null || StringUtil.isEmpty(value + ""))
                    value = nullValue;
                if (value.toString().indexOf(split) >= 0)
                    value = ("~" + value + "~").replace("\r", " ").replace("\n", " ");
                line.append(value);
                line.append(split);
            }
            // 写入文件
            String lv = line.toString();
            if (lv.length() > 0) {
                record_count++;
                out.write(lv.substring(0, lv.length() - 1));
                out.write(Envirment.LINE_SEPARATOR);
            }
            if (record_count > maxRowPerFile && maxRowPerFile > 0) {
                out.close();
                String newFile = targetFileName.replace(".csv", "_" + fileIdx + ".csv");
                logger.info("Create file " + newFile);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile, false), codeName));
                fileIdx++;
                includeHeader = true;
                record_count = 0;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pyrlong.dpp.DataRowHandler#loadTag(java.lang.Object)
     */
    @Override
    public void loadTag(Object o) {
        if (o != null)
            split = o.toString();
    }
}
