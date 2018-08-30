/*******************************************************************************
 * Copyright (c) 2015.  Pyrlong All rights reserved.
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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.tuoming.mes.collect.dpp.handles.DataRowPrintHandle;

/**
 * Created by James on 4/3/15.
 */
public class XlsxDataRowPrintHandler extends DataRowPrintHandle implements XlsxDataRowHandler {
    @Override
    public void startProcessSheet(String sheetId, String sheetName) throws UnsupportedEncodingException, FileNotFoundException {
        System.out.println(sheetName);
    }

    @Override
    public Map<String, String> getOutputFiles() {
        return null;
    }



    @Override
    public void setUseRealType(boolean itype) {

    }

    @Override
    public boolean isUseRealType() {
        return false;
    }

}
