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
import java.util.ArrayList;
import java.util.List;

import com.tuoming.mes.collect.decoder.zte.model.mrs.MeasResultMRS;

/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
public class ResultDataCollection {

    private FileHeader header = null;

    private MeasResultList moduleCount = null;

    private MeasResultMRO mro = null;

    private List<IntraFreqData> intraFreqList = new ArrayList<IntraFreqData>();

    private List<InterFreqData> interFreqList = new ArrayList<InterFreqData>();

    private List<GsmNeighbourData> gsmNeighbourList = new ArrayList<GsmNeighbourData>();

    private List<MeasResultMRS> mrsList = new ArrayList<MeasResultMRS>();

    public List<MeasResultMRS> getMrsResultList() {
        return mrsList;
    }

    public void setMrsList(List<MeasResultMRS> mrsList) {
        this.mrsList = mrsList;
    }

    public FileHeader getFileHeader() {
        return header;
    }

    public void setFileHeader(FileHeader header) {
        this.header = header;
    }

    public MeasResultList getMeasResultMROList() {
        return moduleCount;
    }

    public void setMeasResultMROList(MeasResultList mroList) {
        this.moduleCount = mroList;

    }

    public MeasResultMRO getMeasResultMRO() {
        return mro;
    }

    public void setMeasResultMRO(MeasResultMRO mro) {
        this.mro = mro;
    }

    public void setOtherData( Object obj ) {
    }


    public List<IntraFreqData> getIntraFreqList() {
        return intraFreqList;
    }
    public void setIntraFreqList(List<IntraFreqData> intraFreqList) {
        this.intraFreqList.addAll(intraFreqList);
    }

    public List<InterFreqData> getInterFreqList() {
        return interFreqList;
    }

    public void setInterFreqList(List<InterFreqData> interFreqList) {
        this.interFreqList.addAll(interFreqList);
    }


    public List<GsmNeighbourData> getGsmNeighbourList() {
        return gsmNeighbourList;
    }

    public void setGsmNeighbourList(List<GsmNeighbourData> gsmNeighbourList) {
        this.gsmNeighbourList.addAll(gsmNeighbourList);
    }

}