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

package com.tuoming.mes.collect.decoder.zte.model.mro;

/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
public class IntraFreqData {

    public IntraFreqData(int relativetimestamp, int uarfcn, int cellParamID, int pccpchRscp) {

        this.relativetimestamp = relativetimestamp;
        this.uarfcn = uarfcn;
        this.cellParamID = cellParamID;
        this.pccpchRscp = pccpchRscp;
    }

    public int getRelativetimestamp() {
        return relativetimestamp;
    }

    public void setRelativetimestamp(int relativetimestamp) {
        this.relativetimestamp = relativetimestamp;
    }

    public int getUarfcn() {
        return uarfcn;
    }

    public void setUarfcn(int uarfcn) {
        this.uarfcn = uarfcn;
    }

    private int relativetimestamp;
    private int uarfcn;
    private int cellParamID;
    private int pccpchRscp;

    public int getCellParamID() {
        return cellParamID;
    }

    public void setCellParamID(int cellParamID) {
        this.cellParamID = cellParamID;
    }

    public int getPccpchRscp() {
        return pccpchRscp;
    }

    public void setPccpchRscp(int pccpchRscp) {
        this.pccpchRscp = pccpchRscp;
    }

}