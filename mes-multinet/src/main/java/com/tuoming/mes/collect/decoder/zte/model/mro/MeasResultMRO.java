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
public class MeasResultMRO {

    private int imsilen;
    private String imsi;
    private int srncIdentity;
    private int sRnti;
    private int rncID;
    private int cellID;
    private int cellParamID;
    private int cellUarfcn;
    private int ueUarfcn;
    private String timestamp;
    private int mroCount;

    public int getImsilen() {
        return imsilen;
    }

    public void setImsilen(int imsilen) {
        this.imsilen = imsilen;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public int getSrncIdentity() {
        return srncIdentity;
    }

    public void setSrncIdentity(int srncIdentity) {
        this.srncIdentity = srncIdentity;
    }

    public int getsRnti() {
        return sRnti;
    }

    public void setsRnti(int sRnti) {
        this.sRnti = sRnti;
    }

    public int getRncID() {
        return rncID;
    }

    public void setRncID(int rncID) {
        this.rncID = rncID;
    }

    public int getCellID() {
        return cellID;
    }

    public void setCellID(int cellID) {
        this.cellID = cellID;
    }

    public int getCellParamID() {
        return cellParamID;
    }

    public void setCellParamID(int cellParamID) {
        this.cellParamID = cellParamID;
    }

    public int getCellUarfcn() {
        return cellUarfcn;
    }

    public void setCellUarfcn(int cellUarfcn) {
        this.cellUarfcn = cellUarfcn;
    }

    public int getUeUarfcn() {
        return ueUarfcn;
    }

    public void setUeUarfcn(int ueUarfcn) {
        this.ueUarfcn = ueUarfcn;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getMroCount() {
        return mroCount;
    }

    public void setMroCount(int mroCount) {
        this.mroCount = mroCount;
    }

}