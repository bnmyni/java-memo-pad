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
public class NeData {

    private int sRNCID;
    private int sCellId;
    private int mrsCount;
    private int uarfcn;
    private int slot;
    private int ncisRNCID;
    private int ncisCellId;
    private List<Integer> valueList = new ArrayList<Integer>();


    public NeData(int sRNCID, int sCellId, int mrsCount, int uarfcn, int slot, int ncisRNCID, int ncisCellId) {
        this.sRNCID = sRNCID;
        this.sCellId = sCellId;
        this.mrsCount = mrsCount;
        this.uarfcn = uarfcn;
        this.slot = slot;
        this.ncisRNCID = ncisRNCID;
        this.ncisCellId = ncisCellId;
    }

    public List<Integer> getValueList() {
        return valueList;
    }

    public void setValueList(List<Integer> valueList) {
        this.valueList = valueList;
    }

    public int getsRNCID() {
        return sRNCID;
    }

    public void setsRNCID(int sRNCID) {
        this.sRNCID = sRNCID;
    }

    public void addValue(int value) {
        valueList.add(value);
    }

    public int getsCellId() {
        return sCellId;
    }

    public void setsCellId(int sCellId) {
        this.sCellId = sCellId;
    }


    public int getMrsCount() {
        return mrsCount;
    }

    public void setMrsCount(int mrsCount) {
        this.mrsCount = mrsCount;
    }

    public int getUarfcn() {
        return uarfcn;
    }

    public void setUarfcn(int uarfcn) {
        this.uarfcn = uarfcn;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getNcisRNCID() {
        return ncisRNCID;
    }

    public void setNcisRNCID(int ncisRNCID) {
        this.ncisRNCID = ncisRNCID;
    }

    public int getNcisCellId() {
        return ncisCellId;
    }

    public void setNcisCellId(int ncisCellId) {
        this.ncisCellId = ncisCellId;
    }

}