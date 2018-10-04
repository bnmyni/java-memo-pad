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
public class GsmNeighbourData {
    private int bcch;
    private int ncc;
    private int bcc;
    private int rssi;
    private int relativetimestamp;
    public GsmNeighbourData(int relativetimestamp, int bcch, int ncc, int bcc, int rssi) {
        this.relativetimestamp = relativetimestamp;
        this.bcch = bcch;
        this.ncc = ncc;
        this.bcc = bcc;
        this.rssi = rssi;
    }

    public int getRelativetimestamp() {
        return relativetimestamp;
    }

    public void setRelativetimestamp(int relativetimestamp) {
        this.relativetimestamp = relativetimestamp;
    }

    public int getBcch() {
        return bcch;
    }

    public void setBcch(int bcch) {
        this.bcch = bcch;
    }

    public int getNcc() {
        return ncc;
    }

    public void setNcc(int ncc) {
        this.ncc = ncc;
    }

    public int getBcc() {
        return bcc;
    }

    public void setBcc(int bcc) {
        this.bcc = bcc;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

}