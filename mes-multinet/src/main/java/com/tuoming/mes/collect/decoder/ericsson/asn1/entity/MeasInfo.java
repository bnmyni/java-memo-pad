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

import java.sql.Timestamp;
import java.util.List;

public class MeasInfo {

    private Timestamp measStartTime = null;
    private Integer granularityPeriod = null;
    private List<String> measTypes = null;
    private List<MeasValue> measValues = null;
    private String objectType = null;
    private String ne = null;

    public String getNe() {
        return ne;
    }

    public void setNe(String ne) {
        this.ne = ne;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Timestamp getMeasStartTime() {
        return this.measStartTime;
    }

    public void setMeasStartTime(Timestamp value) {
        this.measStartTime = value;
    }

    public Integer getGranularityPeriod() {
        return this.granularityPeriod;
    }

    public void setGranularityPeriod(Integer value) {
        this.granularityPeriod = value;
    }

    public List<String> getMeasTypes() {
        return this.measTypes;
    }

    public void setMeasTypes(List<String> value) {
        this.measTypes = value;
    }

    public List<MeasValue> getMeasValues() {
        return this.measValues;
    }

    public void setMeasValues(List<MeasValue> value) {
        this.measValues = value;
    }

}
