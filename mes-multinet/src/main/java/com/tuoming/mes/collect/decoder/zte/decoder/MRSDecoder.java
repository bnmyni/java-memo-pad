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
import com.tuoming.mes.collect.decoder.zte.model.mro.MeasResultList;
import com.tuoming.mes.collect.decoder.zte.model.mro.ResultDataCollection;
import com.tuoming.mes.collect.decoder.zte.model.mrs.MeasResultMRS;
import com.tuoming.mes.collect.decoder.zte.model.mrs.NeData;

/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
public class MRSDecoder extends Decoder {

    private enum PmType {
        PccpchRscp, UlRscp, UtranTxPower, UtranCodePower, UtranUppts, UeTsIscp, UtranTsIscp, UeTxPower, ReceivedTotalWideBandPower, AoaAngle, TimingAdvance, RxTimeDev, UtranSir, UeSir, UtranSirt, AmrUlBlerLog, Cs64UlBlerLog, PsUlBlerLog, AmrDlBlerLog, Cs64DlBlerLog, PsDlBlerLog, T2SfnSfnTime, TadvPccpchRscp, TadvAoa
    };

    private enum NeType {
        Cell, Carrier, TimeSlot, TDNeighbourCell
    };

    private ResultDataCollection mroDataCollection = null;

    public MRSDecoder(File file, ResultDataCollection collection) throws FileNotFoundException {
        super(file);
        this.mroDataCollection = collection;
    }

    public void decode() throws IOException {
        try {
            mroDataCollection.setFileHeader(readFileHeader());
            mroDataCollection.setMeasResultMROList(readMeasResultList());
            mroDataCollection.setMrsList(setOtherData());
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

    private MeasResultList readMeasResultList() throws IOException {
        int moduleCount = readBitAsNum(5) + 1;
        MeasResultList resultList = new MeasResultList();
        resultList.setModuleCount(moduleCount);
        return resultList;
    }

    private List<MeasResultMRS> setOtherData() throws IOException {
        List<MeasResultMRS> list = new ArrayList<MeasResultMRS>();
        int i = 0;
        while (i < mroDataCollection.getMeasResultMROList().getModuleCount()) {
            MeasResultMRS mrs = new MeasResultMRS();
            int mrsItemType = readBitAsNum(5);
            int period = readBitAsNum(7);
            int objectType = readBitAsNum(2);
            PmType pmType = PmType.values()[mrsItemType];
            NeType neType = NeType.values()[objectType];

            mrs.setmRSItemType(pmType.toString());
            mrs.setObjectType(neType.toString());
            mrs.setPeriod(period);

            switch (neType) {
                case Cell:
                    mrs.addNeDataList(getCellData());
                    break;
                case Carrier:
                    mrs.addNeDataList(getCarrierData());
                    break;
                case TimeSlot:
                    mrs.addNeDataList(getTimeSlotData());
                    break;
                case TDNeighbourCell:
                    mrs.addNeDataList(getTDNeighbourCell());
                    break;
            }
            i++;
            list.add(mrs);
        }
        return list;
    }

    private List<NeData> getCellData() throws IOException {
        List<NeData> list = new ArrayList<NeData>();
        int mrsNum = readBitAsNum(14) + 1;
        int i = 0;
        while (i < mrsNum) {
            int sRNCID = readBitAsNum(12);
            int sCellId = readBitAsNum(16);
            int mrsCount = readBitAsNum(9) + 1;
            NeData cell = new NeData(sRNCID, sCellId, mrsCount, 0, 0, 0, 0);
            int j = 0;
            while (j < mrsCount) {
                readBitAsNum(16);
                int value = readBitAsNum(16);
                cell.addValue(value);
                j++;
            }
            i++;
            list.add(cell);
        }
        return list;
    }

    private List<NeData> getTDNeighbourCell() throws IOException {
        List<NeData> list = new ArrayList<NeData>();
        int MRSnum = readBitAsNum(8) + 1;
        int i = 0;
        while (i < MRSnum) {
            int sRNCID = readBitAsNum(12);
            int sCellId = readBitAsNum(16);
            int ncisRNCID = readBitAsNum(12);
            int ncisCellId = readBitAsNum(16);
            int mrsCount = readBitAsNum(9) + 1;
            NeData tDNeighbourCell = new NeData(sRNCID, sCellId, mrsCount, 0, 0, ncisRNCID, ncisCellId);
            int j = 0;
            while (j < mrsCount) {
                readBitAsNum(16);
                int value = readBitAsNum(16);
                tDNeighbourCell.addValue(value);
                j++;
            }
            i++;
            list.add(tDNeighbourCell);
        }
        return list;
    }

    private List<NeData> getTimeSlotData() throws IOException {
        List<NeData> list = new ArrayList<NeData>();
        readBitAsNum(1);
        int MRSnum = readBitAsNum(15);
        int i = 0;
        while (i < MRSnum) {
            int sRNCID = readBitAsNum(12);
            int sCellId = readBitAsNum(16);
            int uarfcn = readBitAsNum(14);
            int slot = readBitAsNum(4);
            int mrsCount = readBitAsNum(9) + 1;
            NeData timeSlot = new NeData(sRNCID, sCellId, mrsCount, uarfcn, slot, 0, 0);
            int j = 0;
            while (j < mrsCount) {
                readBitAsNum(16);
                int value = readBitAsNum(16);
                timeSlot.addValue(value);
                j++;
            }
            i++;
            list.add(timeSlot);
        }
        return list;
    }

    private List<NeData> getCarrierData() throws IOException {
        List<NeData> list = new ArrayList<NeData>();
        int MRSnum = readBitAsNum(18) + 1;
        int i = 0;
        while (i < MRSnum) {
            int sRNCID = readBitAsNum(12);
            int sCellId = readBitAsNum(16);
            int uarfcn = readBitAsNum(14);
            int mrsCount = readBitAsNum(9) + 1;
            NeData carrier = new NeData(sRNCID, sCellId, mrsCount, uarfcn, 0, 0, 0);
            int j = 0;
            while (j < mrsCount) {
                readBitAsNum(16);
                int value = readBitAsNum(16);
                carrier.addValue(value);
                j++;
            }
            i++;
            list.add(carrier);
        }
        return list;
    }
}