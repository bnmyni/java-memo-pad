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

package com.tuoming.mes.collect.dpp.file.openxml;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import com.google.common.collect.Maps;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.handles.DataRowToCsvHandle;

/**
 * Created by james on 14-6-12.
 */
public class XlsxRowToCsvHandle implements XlsxDataRowHandler {

    private static Logger logger = LogFacade.getLog4j(XlsxRowToCsvHandle.class);
    DataRowToCsvHandle dataRowToCsvHandle = null;
    Map<String, String> outputFiles = Maps.newHashMap();
    private boolean useRealType = true;

    public boolean isUseRealType() {
        return useRealType;
    }

    public void setUseRealType(boolean itype) {
        useRealType = itype;
    }

    @Override
    public void startProcessSheet(String sheetId, String sheetName) throws UnsupportedEncodingException, FileNotFoundException {
        close();
        String fileName = AppContext.getCacheFileName(sheetName + "_" + DateUtil.getTimeinteger() + ".csv");
        dataRowToCsvHandle = new DataRowToCsvHandle(fileName);
        outputFiles.put(sheetName, fileName);
        logger.info("Create " + fileName);
    }

    @Override
    public Map<String, String> getOutputFiles() {
        return outputFiles;
    }

    @Override
    public void process(String key, DataRow row) {
        if (dataRowToCsvHandle != null)
            dataRowToCsvHandle.process(key, row);
    }

    @Override
    public void close() {
        if (dataRowToCsvHandle != null)
            dataRowToCsvHandle.close();
    }
}
