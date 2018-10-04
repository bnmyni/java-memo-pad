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

package com.tuoming.mes.collect.decoder.ericsson.asn1.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectType {

    private String name;
    private List<String> colNames = new ArrayList<String>();
    private Map<String, List<Object>> colValues;

    public String getName() {
        return name;
    }

    public void setName(String objectTypeName) {
        this.name = objectTypeName;
    }

    public List<String> getColNames() {
        return colNames;
    }

    public Map<String, List<Object>> getColValues() {
        return colValues;
    }

    public void setColValues(Map<String, List<Object>> colValues) {
        this.colValues = colValues;
    }

    public void addColName(String name) {
        this.colNames.add(name);
    }

    public void addColValues(String key, List<Object> value) {
        colValues.put(key, value);
    }
}
