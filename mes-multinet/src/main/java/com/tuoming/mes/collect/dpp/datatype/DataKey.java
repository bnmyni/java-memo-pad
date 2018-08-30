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

package com.tuoming.mes.collect.dpp.datatype;

import java.util.ArrayList;
import java.util.List;

import com.pyrlong.exception.PyrlongException;

public final class DataKey {
    /**
     * 主键或外键名称
     */
    private String keyName;
    List<DataColumn> keyColumns = new ArrayList<DataColumn>();

    public DataKey() {
        keyColumns = new ArrayList<DataColumn>();
    }

    public DataKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public List<DataColumn> getKeyColumns() {
        return keyColumns;
    }

    public void setKeyColumns(List<DataColumn> keyColumns) {
        this.keyColumns = keyColumns;
    }

    public String getKeyString(DataRow row) {
        if (row == null) {
            throw new PyrlongException("Datakey-getKeyString datarow can't be null");
        }
        String key = "";
        for (DataColumn col : keyColumns) {
            key += row.getValue(col.getColumnIndex()) + ",";
        }
        if (key.endsWith(",")) key = key.substring(0, key.length() - 1);
        return key;
    }
}
