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

package com.tuoming.mes.collect.decoder.zte.model.mrs;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
public class MeasResultMRS {
    private int period;
    private String mRSItemType;
    private String objectType;
    private List<NeData> neDataList = new ArrayList<NeData>();

    public void addNeDataList(List<NeData> list) {
        neDataList.addAll(list);
    }

    public int getPeriod() {
        return period;
    }
    public void setPeriod(int period) {
        this.period = period;
    }
    public String getmRSItemType() {
        return mRSItemType;
    }
    public void setmRSItemType(String mRSItemType) {
        this.mRSItemType = mRSItemType;
    }
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    public List<NeData> getNeDataList() {
        return neDataList;
    }
    public void setNeDataList(List<NeData> neDataList) {
        this.neDataList = neDataList;
    }

}