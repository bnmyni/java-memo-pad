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

package com.tuoming.mes.collect.decoder.ericsson.ncs;

import java.util.Map;
import com.google.common.collect.Maps;

/**
 * Created by James on 14/11/12.
 */
public class NCSRecordFactory {
    Map<Integer, RecordType> recordTypeMap = Maps.newHashMap();

    public NCSRecordFactory(boolean printHeader) {
        RecordType recordType;
        /**
         * RIRI定义
         */
        /**
         * 40	Administrative record
         */
        recordType = new RecordType("rir_admin", 40, printHeader);
        //recordType.addRecordValue("length", new Numeral(1, 2, "Record length always 52"));
        recordType.addRecordValue("file_rev", new Numeral(3, 1, "File format rev,60"));
        recordType.addRecordValue("year", new DigitString(4, 1, "Year"));
        recordType.addRecordValue("month", new DigitString(5, 1, "Month"));
        recordType.addRecordValue("day", new DigitString(6, 1, "Day"));
        recordType.addRecordValue("Hour", new DigitString(7, 1, "hour"));
        recordType.addRecordValue("Minute", new DigitString(8, 1, "minute"));
        recordType.addRecordValue("Second", new DigitString(9, 1, "second"));
        recordType.addRecordValue("Record information", new Numeral(10, 2, "Record information"));
        recordType.addRecordValue("RID", new Identifier(12, 7, "RID"));
        recordType.addRecordValue("TTIME", new DigitString(19, 2, "TTIME"));
        recordType.addRecordValue("Percentile value", new DigitString(21, 2, "Percentile value"));
        recordTypeMap.put(40, recordType);

        /**
         * 41	Record RADIO INTERFERENCE RECORDING CELL DATA
         */
        recordType = new RecordType("Record RADIO INTERFERENCE RECORDING CELL DATA", 41, printHeader);
        recordType.addRecordValue("CELL", new Identifier(3, 8, "CELL"));
        recordType.addRecordValue("frequencies_count", new Numeral(11, 1, "frequencies"));
        BytesValueTemplate template = new BytesValueTemplate(12, 150, "");
        template.addByteValue(new Numeral("ARFCN", 12, 2, "ARFCN1"));
        template.addByteValue(new Numeral("AVMEDIAN", 14, 1, "AVMEDIAN"));
        template.addByteValue(new Numeral("AVPERCENTILE", 15, 1, "AVPERCENTILE"));
        template.addByteValue(new Numeral("NOOFMEAS", 16, 4, "NOOFMEAS"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(41, recordType);

        /**
         * 定义NCS包括的记录类型
         * 50	Administrative record
         */
        recordType = new RecordType("ncs-admin", 50, printHeader);
        //recordType.addRecordValue("length", new Numeral(1, 2, "Record length always 52"));
        recordType.addRecordValue("file_rev", new Numeral(3, 1, "File format rev,60"));
        recordType.addRecordValue("year", new DigitString(4, 1, "Year"));
        recordType.addRecordValue("month", new DigitString(5, 1, "Month"));
        recordType.addRecordValue("day", new DigitString(6, 1, "Day"));
        recordType.addRecordValue("Hour", new DigitString(7, 1, "hour"));
        recordType.addRecordValue("Minute", new DigitString(8, 1, "minute"));
        recordType.addRecordValue("Second", new DigitString(9, 1, "second"));
        recordType.addRecordValue("Record information", new Numeral(10, 4, "Record information"));
        recordType.addRecordValue("RID", new Identifier(14, 7, "RID"));
        recordType.addRecordValue("start_year", new DigitString(21, 1, "Start Year"));
        recordType.addRecordValue("start_month", new DigitString(22, 1, "Start Month"));
        recordType.addRecordValue("start_day", new DigitString(23, 1, "Start Day"));
        recordType.addRecordValue("start_hour", new DigitString(24, 1, "Start hour"));
        recordType.addRecordValue("start_minute", new DigitString(25, 1, "Start minute"));
        recordType.addRecordValue("start_second", new DigitString(26, 1, "Start second"));
        recordType.addRecordValue("abss", new Numeral(27, 1, "ABSS"));
        recordType.addRecordValue("relss +/-", new Numeral(28, 1, "RELSS +/-,0=Positive,1=Negative"));
        recordType.addRecordValue("relss", new Numeral(29, 1, "RELSS"));
        recordType.addRecordValue("relss2+/-", new Numeral(30, 1, "RELSS2 +/-"));
        recordType.addRecordValue("relss2", new Numeral(31, 1, "RELSS2"));
        recordType.addRecordValue("relss3+/-", new Numeral(32, 1, "RELSS3 +/-"));
        recordType.addRecordValue("relss3", new Numeral(33, 1, "RELSS3"));
        recordType.addRecordValue("relss4+/-", new Numeral(34, 1, "RELSS4 +/-"));
        recordType.addRecordValue("relss4", new Numeral(35, 1, "RELSS4"));
        recordType.addRecordValue("relss5+/-", new Numeral(36, 1, "RELSS5 +/-"));
        recordType.addRecordValue("relss5", new Numeral(37, 1, "RELSS5"));
        recordType.addRecordValue("ncell_type", new Numeral(38, 1, "NCELLTYPE"));
        recordType.addRecordValue("num_freq", new Numeral(39, 1, "NUMFREQ"));
        recordType.addRecordValue("seg_time", new Numeral(40, 2, "SEGTIME"));
        recordType.addRecordValue("termination_reson", new Numeral(42, 1, "Termination reason"));
        recordType.addRecordValue("rec_time", new Numeral(43, 2, "RECTIME"));
        recordType.addRecordValue("ecnoabss", new Numeral(45, 1, "ECNOABSS"));
        recordType.addRecordValue("NUCELLTYPE", new Numeral(46, 1, "NUCELLTYPE"));
        recordType.addRecordValue("TFDDMRR", new Numeral(47, 1, "TFDDMRR"));
        recordType.addRecordValue("NUMUMFI", new Numeral(48, 1, "NUMUMFI"));
        recordType.addRecordValue("TNCCPERM", new Numeral(49, 1, "TNCCPERM"));
        recordType.addRecordValue("TNCCPERM bitmap", new Numeral(50, 1, "TNCCPERM bitmap"));
        recordType.addRecordValue("TMBCR", new Numeral(51, 1, "TMBCR"));
        recordTypeMap.put(50, recordType);
        /**
         * 51	Active BA-list Recording Cell Data
         */
        recordType = new RecordType("Active BA-list Recording Cell Data", 51, printHeader);
        recordType.addRecordValue("cell_name", new Identifier(3, 8, "CELL NAME"));
        recordType.addRecordValue("chgr", new Numeral(11, 1, "CHGR"));
        recordType.addRecordValue("rep", new Numeral(12, 4, "REP"));
        recordType.addRecordValue("REPHR", new Numeral(16, 4, "REPHR"));
        recordType.addRecordValue("REPUNDEFGSM", new Numeral(20, 4, "REPUNDEFGSM"));
        recordType.addRecordValue("AVSS", new Numeral(24, 1, "AVSS"));
        recordTypeMap.put(51, recordType);
        /**
         * 52	Active BA-list Recording Neighbouring Cell Data
         */
        recordType = new RecordType("Active BA-list Recording Neighbouring Cell Data", 52, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("CHGR", new Numeral(11, 1, "CHGR"));
        recordType.addRecordValue("BSIC", new Octal(12, 1, "BSIC"));
        recordType.addRecordValue("ARFCN", new Numeral(13, 2, "ARFCN"));
        recordType.addRecordValue("is_ncell", new Numeral(15, 1, "neighbouring cell"));
        recordType.addRecordValue("RECTIMEARFCN", new Numeral(16, 2, "RECTIMEARFCN"));
        recordType.addRecordValue("REPARFCN", new Numeral(18, 4, "REPARFCN"));
        recordType.addRecordValue("TIMES", new Numeral(22, 4, "TIMES"));
        recordType.addRecordValue("NAVSS", new Numeral(26, 1, "NAVSS"));
        recordType.addRecordValue("TIMES1", new Numeral(27, 4, "TIMES1"));
        recordType.addRecordValue("NAVSS1", new Numeral(31, 1, "NAVSS1"));
        recordType.addRecordValue("TIMES2", new Numeral(32, 4, "TIMES2"));
        recordType.addRecordValue("NAVSS2", new Numeral(36, 1, "NAVSS2"));
        recordType.addRecordValue("TIMES3", new Numeral(37, 4, "TIMES3"));
        recordType.addRecordValue("NAVSS3", new Numeral(41, 1, "NAVSS3"));
        recordType.addRecordValue("TIMES4", new Numeral(42, 4, "TIMES4"));
        recordType.addRecordValue("NAVSS4", new Numeral(46, 1, "NAVSS4"));
        recordType.addRecordValue("TIMES5", new Numeral(47, 4, "TIMES5"));
        recordType.addRecordValue("NAVSS5", new Numeral(51, 1, "NAVSS5"));
        recordType.addRecordValue("TIMES6", new Numeral(52, 4, "TIMES6"));
        recordType.addRecordValue("NAVSS6", new Numeral(56, 1, "NAVSS6"));
        recordType.addRecordValue("TIMESRELSS", new Numeral(57, 4, "TIMESRELSS"));
        recordType.addRecordValue("TIMESRELSS2", new Numeral(61, 4, "TIMESRELSS2"));
        recordType.addRecordValue("TIMESRELSS3", new Numeral(65, 4, "TIMESRELSS3"));
        recordType.addRecordValue("TIMESRELSS4", new Numeral(69, 4, "TIMESRELSS4"));
        recordType.addRecordValue("TIMESRELSS5", new Numeral(73, 4, "TIMESRELSS5"));
        recordType.addRecordValue("TIMESABSS", new Numeral(77, 4, "TIMESABSS"));
        recordType.addRecordValue("TIMESALONE", new Numeral(81, 4, "TIMESALONE"));
        recordTypeMap.put(52, recordType);

        /**
         * 53	Active BA-list Recording Frequencies Not Reported Data
         */
        recordType = new RecordType("Active BA-list Recording Frequencies Not Reported Data", 53, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("ARFCN", new Numeral(11, 2, "ARFCN"));
        recordType.addRecordValue("RECTIMEARFCN", new Numeral(13, 2, "RECTIMEARFCN"));
        recordType.addRecordValue("REPARFCN", new Numeral(15, 4, "REPARFCN"));
        recordTypeMap.put(53, recordType);

        /**
         * 54	Active BA-list Recording Neighbouring UMTS Cell Data
         */
        recordType = new RecordType("Active BA-list Recording Neighbouring UMTS Cell Data", 54, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("MFDDARFCN", new Numeral(11, 2, "MFDDARFCN"));
        recordType.addRecordValue("MSCRCODE", new Numeral(13, 2, "MSCRCODE"));
        recordType.addRecordValue("DIVERSITY", new Numeral(15, 1, "DIVERSITY"));

        recordType.addRecordValue("is_neighbouring", new Numeral(16, 1, "is_neighbouring"));
        recordType.addRecordValue("RECTIMEUMFI", new Numeral(17, 2, "RECTIMEUMFI"));
        recordType.addRecordValue("REPUMFI", new Numeral(19, 4, "REPUMFI"));
        recordType.addRecordValue("UTIMES", new Numeral(23, 4, "UTIMES"));
        recordType.addRecordValue("AVECNO", new Numeral(27, 1, "AVECNO"));
        recordType.addRecordValue("UTIMES1", new Numeral(28, 4, "UTIMES1"));
        recordType.addRecordValue("AVECNO1", new Numeral(32, 1, "AVECNO1"));
        recordType.addRecordValue("UTIMES2", new Numeral(33, 4, "UTIMES2"));

        recordType.addRecordValue("AVECNO2", new Numeral(37, 1, "AVECNO2"));
        recordType.addRecordValue("UTIMES3", new Numeral(38, 4, "UTIMES3"));
        recordType.addRecordValue("AVECNO3", new Numeral(42, 1, "AVECNO3"));
        recordType.addRecordValue("UTIMESECNOABSS", new Numeral(43, 4, "UTIMESECNOABSS"));
        recordType.addRecordValue("UTIMESALONE", new Numeral(47, 4, "UTIMESALONE"));
        recordTypeMap.put(54, recordType);

        /**
         * 55	Active BA-list Recording UMFIs Not Reported Data
         */
        recordType = new RecordType("Active BA-list Recording UMFIs Not Reported Data", 55, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("MFDDARFCN", new Numeral(11, 2, "MFDDARFCN"));
        recordType.addRecordValue("MSCRCODE", new Numeral(13, 2, "MSCRCODE"));
        recordType.addRecordValue("DIVERSITY", new Numeral(15, 1, "DIVERSITY"));
        recordType.addRecordValue("RECTIMEUMFI", new Numeral(16, 2, "RECTIMEUMFI"));
        recordType.addRecordValue("REPUMFI", new Numeral(18, 4, "REPUMFI"));
        recordTypeMap.put(55, recordType);

        /**
         * 56	Active BA-list Recording UMTS Cell Data
         */
        recordType = new RecordType("Active BA-list Recording UMTS Cell Data", 56, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("REPUNDEFUMTS", new Numeral(11, 4, "REPUNDEFUMTS"));
        recordType.addRecordValue("REPUMTS", new Numeral(15, 4, "REPUMTS"));
        recordTypeMap.put(56, recordType);

        /*
        30	Administrative record
        */

        recordType = new RecordType("mrr_admin", 30, printHeader);
        //recordType.addRecordValue("length", new Numeral(1, 2, "Record length always 52"));
        recordType.addRecordValue("file_rev", new Numeral(3, 1, "File format rev,60"));
        recordType.addRecordValue("year", new DigitString(4, 1, "Year"));
        recordType.addRecordValue("month", new DigitString(5, 1, "Month"));
        recordType.addRecordValue("day", new DigitString(6, 1, "Day"));
        recordType.addRecordValue("Hour", new DigitString(7, 1, "hour"));
        recordType.addRecordValue("Minute", new DigitString(8, 1, "minute"));
        recordType.addRecordValue("Second", new DigitString(9, 1, "second"));
        recordType.addRecordValue("Record information", new Numeral(10, 2, "Record information"));
        recordType.addRecordValue("RID", new Identifier(12, 7, "RID"));
        recordType.addRecordValue("TTIME", new DigitString(19, 2, "TTIME"));
        recordType.addRecordValue("MEASLIM", new DigitString(21, 1, "MEASLIM"));
        recordType.addRecordValue("MEASLIM+/-", new DigitString(22, 1, "MEASLIM+/-"));

        recordType.addRecordValue("MEASINT", new DigitString(23, 1, "MEASINT"));

        recordType.addRecordValue("MEASTYPE", new DigitString(24, 1, "MEASTYPE"));
        recordType.addRecordValue("MEASLINK", new DigitString(25, 1, "MEASLINK"));
        recordType.addRecordValue("MEASLIM2", new DigitString(26, 1, "MEASLIM2"));
        recordType.addRecordValue("MEASLIM3", new DigitString(27, 1, "MEASLIM3"));
        recordType.addRecordValue("MEASLIM4", new DigitString(28, 1, "MEASLIM4"));
        recordType.addRecordValue("CONTYPE", new DigitString(29, 1, "CONTYPE"));
        recordType.addRecordValue("DTMFILTER", new DigitString(30, 1, "DTMFILTER"));
        recordTypeMap.put(30, recordType);

        /**
         * 31	Uplink and Downlink Signal Strength Cell Data record
         */
        recordType = new RecordType("Uplink and Downlink Signal Strength Cell Data record", 31, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 64, "");
        template.addByteValue(new Numeral("RXLEVUL", 13, 4, "RXLEVUL"));
        recordType.addRecordValue("", template);
        template = new BytesValueTemplate(269, 64, "");
        template.addByteValue(new Numeral("RXLEVDL", 269, 4, "RXLEVDL"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(31, recordType);

        /**
         *      32	Uplink and Downlink Signal Quality Cell Data record
         */
        recordType = new RecordType("Uplink and Downlink Signal Quality Cell Data record", 32, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 8, "");
        template.addByteValue(new Numeral("RXQUALUL", 13, 4, "RXQUALUL"));
        recordType.addRecordValue("", template);
        template = new BytesValueTemplate(45, 8, "");
        template.addByteValue(new Numeral("RXQUALDL", 269, 4, "RXQUALDL"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(32, recordType);

        /**
         *   33	BTS and MS Transmit Power Level Cell Data record
         */
        recordType = new RecordType("BTS and MS Transmit Power Level Cell Data record", 33, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 32, "");
        template.addByteValue(new Numeral("MSPOWER", 13, 4, "MSPOWER"));
        recordType.addRecordValue("", template);
        template = new BytesValueTemplate(141, 16, "");
        template.addByteValue(new Numeral("BSPOWER", 141, 4, "BSPOWER"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(33, recordType);

        /*
         34	Actual Timing Advance Cell Data record
         */
        recordType = new RecordType("Actual Timing Advance Cell Data record", 34, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 76, "");
        template.addByteValue(new Numeral("TAVAL", 13, 4, "TAVAL"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(34, recordType);

        /**
         * 35	Uplink and Downlink Path Loss Cell Data record
         */
        recordType = new RecordType("Record UPLINK AND DOWNLINK PATH LOSS CELL DATA", 35, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 60, "");
        template.addByteValue(new Numeral("PLOSSUL", 13, 4, "PLOSSUL"));
        recordType.addRecordValue("", template);
        template = new BytesValueTemplate(253, 65, "");
        template.addByteValue(new Numeral("PLOSSDL", 253, 4, "PLOSSDL"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(35, recordType);

        /**
         * 36	Path Loss Difference Cell Data record
         */
        recordType = new RecordType("Path Loss Difference Cell Data record", 36, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 51, "");
        template.addByteValue(new Numeral("PLDIFF", 13, 4, "PLDIFF"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(36, recordType);

        /**
         * 37  Record NUMBER OF MEASUREMENT RESULTS CELL DATA
         */
        recordType = new RecordType("Record NUMBER OF MEASUREMENT RESULTS CELL DATA", 37, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));

        recordType.addRecordValue("REP", new Numeral(13, 4, "REP"));
        recordType.addRecordValue("REPFERUL", new Numeral(17, 4, "REPFERUL"));
        recordType.addRecordValue("REPFERDL", new Numeral(21, 4, "REPFERDL"));
        recordType.addRecordValue("REPFERBL", new Numeral(25, 4, "REPFERBL"));
        recordType.addRecordValue("REPFERTHL", new Numeral(29, 4, "REPFERTHL"));

        recordTypeMap.put(37, recordType);

        /**
         * 38 Record UPLINK AND DOWNLINK FRAME ERASURE RATE CELL DATA
         */
        recordType = new RecordType("Record UPLINK AND DOWNLINK FRAME ERASURE RATE CELL DATA", 38, printHeader);
        recordType.addRecordValue("CELLNAME", new Identifier(3, 8, "CELLNAME"));
        recordType.addRecordValue("Subcell", new Numeral(11, 1, "Subcell"));
        recordType.addRecordValue("Channel group number", new Numeral(12, 1, "Channel group number"));
        template = new BytesValueTemplate(13, 97, "");
        template.addByteValue(new Numeral("FERUL", 13, 4, "FERUL"));
        recordType.addRecordValue("", template);
        template = new BytesValueTemplate(401, 97, "");
        template.addByteValue(new Numeral("FERDL", 401, 4, "FERDL"));
        recordType.addRecordValue("", template);
        recordTypeMap.put(38, recordType);
    }


    public void clear() {
        for (Map.Entry<Integer, RecordType> entry : recordTypeMap.entrySet())
            entry.getValue().clear();
    }

    /**
     * 根据id获取对应的RecordType定义
     *
     * @return
     */
    public RecordType getRecordType(Integer id) {
        if (recordTypeMap.containsKey(id))
            return recordTypeMap.get(id);
        return null;
    }
}
