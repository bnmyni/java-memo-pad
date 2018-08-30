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

package com.tuoming.mes.collect.decoder.zte.decoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tuoming.mes.collect.decoder.zte.model.mro.FileHeader;
import com.tuoming.mes.collect.decoder.zte.model.mro.GsmNeighbourData;
import com.tuoming.mes.collect.decoder.zte.model.mro.InterFreqData;
import com.tuoming.mes.collect.decoder.zte.model.mro.IntraFreqData;
import com.tuoming.mes.collect.decoder.zte.model.mro.MeasResultList;
import com.tuoming.mes.collect.decoder.zte.model.mro.MeasResultMRO;
import com.tuoming.mes.collect.decoder.zte.model.mro.ResultDataCollection;
/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
public class MRODecoder extends Decoder {

    private ResultDataCollection mroDataCollection = null;

    public MRODecoder(File file, ResultDataCollection collection) throws FileNotFoundException {
        super(file);
        this.mroDataCollection = collection;
    }

    public void decode() throws IOException {

       try {
           mroDataCollection.setFileHeader(readFileHeader());
           mroDataCollection.setMeasResultMROList(readMeasResultMROList());
           mroDataCollection.setMeasResultMRO(readMeasResultMRO());
           mroDataCollection.setOtherData(readOtherData());
       }
       finally {
           close();
       }
    }

    private FileHeader readFileHeader() throws IOException {
        FileHeader header = new FileHeader();
        String fileFormatVersion = readBitAsStr(24 * 8);
        header.setFileFormatVersion(fileFormatVersion.trim());
        int rncid = readBitAsNum(16);
        header.setRncID(rncid);
        int year = readBitAsNum(10) + 2000;
        int month = readBitAsNum(4) + 1;
        int day = readBitAsNum(5) + 1;
        int hour = readBitAsNum(5);
        int minute = readBitAsNum(6);
        int second = readBitAsNum(6);
        String startTime = String.format("%s-%s-%s %s:%s:%s", year, month, day, hour, minute, second);
        header.setStartTime(startTime);
        int year1 = readBitAsNum(10) + 2000;
        int month1 = readBitAsNum(4) + 1;
        int day1 = readBitAsNum(5) + 1;
        int hour1 = readBitAsNum(5);
        int minute1 = readBitAsNum(6);
        int second1 = readBitAsNum(6);
        String endTime = String.format("%s-%s-%s %s:%s:%s", year1, month1, day1, hour1, minute1, second1);
        header.setEndTime(endTime);
        return header;
    }

    private MeasResultList readMeasResultMROList() throws IOException {
        int mroModuleCount = readBitAsNum(8) + 1;
        MeasResultList mroList = new MeasResultList();
        mroList.setModuleCount(mroModuleCount);
        return mroList;
    }

    private MeasResultMRO readMeasResultMRO() throws IOException {
        MeasResultMRO mro = new MeasResultMRO();
        readBitAsNum(1);
        int imsilen = readBitAsNum(3) + 3;
        mro.setImsilen(imsilen);
        String imsi = readBitAsHexStr(imsilen * 8);
        mro.setImsi(imsi);
        int srncIdentity = readBitAsNum(12);
        mro.setSrncIdentity(srncIdentity);
        int sRnti = readBitAsNum(20);
        mro.setsRnti(sRnti);
        int rncID = readBitAsNum(16);
        mro.setRncID(rncID);
        int cellID = readBitAsNum(16);
        mro.setCellID(cellID);
        int cellParamID = readBitAsNum(7);
        mro.setCellParamID(cellParamID);
        int cellUarfcn = readBitAsNum(14);
        mro.setCellUarfcn(cellUarfcn);
        int ueUarfcn = readBitAsNum(14);
        mro.setUeUarfcn(ueUarfcn);
        int year = readBitAsNum(10) + 2000;
        int month = readBitAsNum(4) + 1;
        int day = readBitAsNum(5) + 1;
        int hour = readBitAsNum(5);
        int minute = readBitAsNum(6);
        int second = readBitAsNum(6);
        String timestamp = String.format("%s-%s-%s %s:%s:%s", year, month, day, hour, minute, second);
        mro.setTimestamp(timestamp);
        int mroCount = readBitAsNum(6) + 1;
        mro.setMroCount(mroCount);
        return mro;
    }

    private Object readOtherData() throws IOException {
        int i = 0;
        String mroFlag = "";
        while (i < mroDataCollection.getMeasResultMROList().getModuleCount()) {
            mroFlag = readBitAsBitStr(3);
            if (mroFlag.equals("100")) {
                mroDataCollection.setIntraFreqList(readIntraFreq());
            } else if (mroFlag.equals("010")) {

                mroDataCollection.setInterFreqList(readInterFreq());

            } else if (mroFlag.equals("001")) {

                mroDataCollection.setGsmNeighbourList(readGsmNeighbour());
            }
            i++;
        }
        return null;
    }

    private List<IntraFreqData> readIntraFreq() throws IOException {
        List<IntraFreqData> list = new ArrayList<IntraFreqData>();
        int relativetimestamp = readBitAsNum(22);
        int uarfcn = readBitAsNum(14);
        int resultslistCount = readBitAsNum(5);
        int i = 0;
        while (i < resultslistCount) {
            int cellParamID = readBitAsNum(7);
            int pccpchRscp = readBitAsNum(7);
            list.add(new IntraFreqData(relativetimestamp, uarfcn, cellParamID, pccpchRscp));
            i++;
        }
        return list;
    }

    private List<InterFreqData> readInterFreq() throws IOException {
        List<InterFreqData> list = new ArrayList<InterFreqData>();
        int relativetimestamp = readBitAsNum(22);
        int resultslistCount = readBitAsNum(5);
        int i = 0;
        while (i < resultslistCount) {
            int uarfcn = readBitAsNum(14);
            int cellParamID = readBitAsNum(7);
            int pccpchRscp = readBitAsNum(7);
            list.add(new InterFreqData(relativetimestamp, uarfcn, cellParamID, pccpchRscp));
            i++;
        }
        return list;
    }

    private List<GsmNeighbourData> readGsmNeighbour() throws IOException {
        List<GsmNeighbourData> list = new ArrayList<GsmNeighbourData>();
        int relativetimestamp = readBitAsNum(22);
        int resultslistCount = readBitAsNum(5);
        int i = 0;
        while (i < resultslistCount) {
            int bcch = readBitAsNum(10);
            int ncc = readBitAsNum(3);
            int bcc = readBitAsNum(3);
            int rssi = readBitAsNum(6);
            list.add(new GsmNeighbourData(relativetimestamp, bcch, ncc, bcc, rssi));
            i++;
        }
        return list;
    }
}
