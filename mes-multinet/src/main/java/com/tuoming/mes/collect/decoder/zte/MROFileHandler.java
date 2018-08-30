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

package com.tuoming.mes.collect.decoder.zte;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.collect.decoder.zte.decoder.MRODecoder;
import com.tuoming.mes.collect.decoder.zte.model.mro.FileHeader;
import com.tuoming.mes.collect.decoder.zte.model.mro.GsmNeighbourData;
import com.tuoming.mes.collect.decoder.zte.model.mro.InterFreqData;
import com.tuoming.mes.collect.decoder.zte.model.mro.IntraFreqData;
import com.tuoming.mes.collect.decoder.zte.model.mro.MeasResultMRO;
import com.tuoming.mes.collect.decoder.zte.model.mro.ResultDataCollection;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * Created by shenhaitao on 2014/7/22 0022.
 */
@Scope("prototype")
@Component("MROFileHandler")
public class MROFileHandler extends AbstractFileProcessor {

    private static Logger logger = Logger.getLogger(MROFileHandler.class);
    private  String  sepapartor ="";

    public void convertToCSVFiles() {
        sepapartor = Envirment.CSV_SEPARATOR;
        for (Map.Entry<String, Map<String, String>> fileSet : sourceFileList.entrySet()) {
            try {
                String fileName = fileSet.getKey();
                if (isFileDone(fileName))
                    continue;
                String targetFile = fileName.substring(0, fileName.length() - 3);
                CompressionUtils.decompress(fileName, targetFile);

                File file = new File(targetFile);
                String srcFileName = file.getName();
                srcFileName = srcFileName.substring(0, srcFileName.lastIndexOf("."));
                ResultDataCollection mdc = new ResultDataCollection();
                MRODecoder decoder = new MRODecoder(file, mdc);
                decoder.decode();

                List<String> objectTypes = new ArrayList<String>();
                String exp = fileSet.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER);
                objectTypes = (List<String>) DSLUtil.getDefaultInstance().compute(exp);

                 for (String type :objectTypes) {
                     if (type.equalsIgnoreCase("intra"))
                     {
                         saveIntraToCsv(mdc, srcFileName,type);
                     }
                    else if(type.equalsIgnoreCase("inter"))
                     {
                         saveInterToCsv(mdc, srcFileName,type);
                     }
                     else if(type.equalsIgnoreCase("gsmneighbour"))
                     {
                         saveGsmNbToCsv(mdc, srcFileName,type);
                     }
                 }
                 //将文件后缀更改为.done，即标识文件为已解析文件
                markFileDone(fileName);
                markFileDone(targetFile);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private StringBuffer getCaptionBuilder() {
        StringBuffer builder = new StringBuffer();
        builder.append("fileFormatVersion").append(sepapartor).append("StartTime").append(sepapartor).append("EndTime").append(sepapartor).append("RNCID_Z").append(sepapartor).append("IMSIlen").append(sepapartor).append("IMSI").append(sepapartor).append("SRNCidentity").append(sepapartor).append("SRNTI")
                .append(sepapartor).append("RNCID").append(sepapartor).append("CELLID").append(sepapartor).append("CellParameterID").append(sepapartor).append("CellUarfcn").append(sepapartor).append("ScanTime");
        return builder;
    }

    private StringBuffer getValueBuilder(FileHeader header, MeasResultMRO mro) {
        StringBuffer builder = new StringBuffer();
        builder.append(header.getFileFormatVersion()).append(sepapartor).append(header.getStartTime()).append(sepapartor).append(header.getEndTime()).append(sepapartor).append(header.getRncID()).append(sepapartor).append(mro.getImsilen()).append(sepapartor).append(mro.getImsi()).append(sepapartor)
                .append(mro.getSrncIdentity()).append(sepapartor).append(mro.getsRnti()).append(sepapartor).append(mro.getRncID()).append(sepapartor).append(mro.getCellID()).append(sepapartor).append(mro.getCellParamID()).append(sepapartor).append(mro.getCellUarfcn()).append(sepapartor)
                .append(mro.getTimestamp());
        return builder;
    }

    private void saveIntraToCsv(ResultDataCollection mdc, String fileName,String type) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        
        String csvFileName = String.format("%s_%s.csv", fileName,type);
        if (!resultFiles.contains(csvFileName)) {
            resultFiles.add(csvFileName);
        }
        String fullCsvFileName =  String.format("%s%s", targetPath,csvFileName);
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(fullCsvFileName)), csvEncoding);
        BufferedWriter bw = new BufferedWriter(out);
        FileHeader header = mdc.getFileHeader();
        MeasResultMRO mro = mdc.getMeasResultMRO();
        List<IntraFreqData> intrafreqList = mdc.getIntraFreqList();
        boolean hastCaption=false;
        if (printHeander) {
            StringBuffer captionBuilder = getCaptionBuilder();
            captionBuilder.append(sepapartor).append("timestamp").append(sepapartor).append("Uarfcn").append(sepapartor).append("InterCellParameterID").append(sepapartor).append("PccpchRscp");
            bw.write(captionBuilder.toString());
            hastCaption=true;
        }
        for (IntraFreqData interFreqData : intrafreqList) {
            if(hastCaption)
            {
                bw.newLine();
            }
            hastCaption=true;
            StringBuffer valueBuilder = getValueBuilder(header, mro);
            valueBuilder.append(sepapartor).append(interFreqData.getRelativetimestamp()).append(sepapartor).append(interFreqData.getUarfcn()).append(sepapartor).append(interFreqData.getCellParamID()).append(sepapartor).append(interFreqData.getPccpchRscp());
            bw.write(valueBuilder.toString());
        }
        bw.close();
    }

    private void saveGsmNbToCsv(ResultDataCollection mdc, String fileName,String type) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String csvFileName = String.format("%s_%s.csv", fileName,type);
        if (!resultFiles.contains(csvFileName)) {
            resultFiles.add(csvFileName);
        }
        String fullCsvFileName =  String.format("%s%s", targetPath,csvFileName);
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(fullCsvFileName)), csvEncoding);

        BufferedWriter bw = new BufferedWriter(out);
        FileHeader header = mdc.getFileHeader();
        MeasResultMRO mro = mdc.getMeasResultMRO();
        List<GsmNeighbourData> gsmNeighbourList = mdc.getGsmNeighbourList();
        boolean hastCaption=false;
        if (printHeander) {
            StringBuffer captionBuilder = getCaptionBuilder();
            captionBuilder.append(sepapartor).append("timestamp").append(sepapartor).append("BCCH").append(sepapartor).append("NCC").append(sepapartor).append("BCC").append(sepapartor).append("RSSI");
            bw.write(captionBuilder.toString());
            hastCaption=true;
        }
        for (GsmNeighbourData gsmNeighbourData : gsmNeighbourList) {
            if(hastCaption)
            {
                bw.newLine();
            }
            hastCaption=true;
            StringBuffer valueBuilder = getValueBuilder(header, mro);
            valueBuilder.append(sepapartor).append(gsmNeighbourData.getRelativetimestamp()).append(sepapartor).append(gsmNeighbourData.getBcch()).append(sepapartor).append(gsmNeighbourData.getNcc()).append(sepapartor).append(gsmNeighbourData.getBcc()).append(sepapartor)
                    .append(gsmNeighbourData.getRssi());
            bw.write(valueBuilder.toString());
        }
        bw.close();
    }

    private void saveInterToCsv(ResultDataCollection mdc, String fileName,String type) throws UnsupportedEncodingException, FileNotFoundException, IOException {
      String csvFileName = String.format("%s_%s.csv", fileName,type);
        if (!resultFiles.contains(csvFileName)) {
            resultFiles.add(csvFileName);
        }
        String fullCsvFileName =  String.format("%s%s", targetPath,csvFileName);
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(fullCsvFileName)), csvEncoding);
        BufferedWriter bw = new BufferedWriter(out);
        FileHeader header = mdc.getFileHeader();
        MeasResultMRO mro = mdc.getMeasResultMRO();
        List<InterFreqData> interfreqList = mdc.getInterFreqList();
        boolean hastCaption=false;
        if (printHeander) {
            StringBuffer captionBuilder = getCaptionBuilder();
            captionBuilder.append(sepapartor).append("timestamp").append(sepapartor).append("Uarfcn").append(sepapartor).append("InterCellParameterID").append(sepapartor).append("PccpchRscp");
            bw.write(captionBuilder.toString());
            hastCaption=true;
        }
        for (InterFreqData interFreqData : interfreqList) {
            if(hastCaption)
            {
                bw.newLine();
            }
            hastCaption=true;
            StringBuffer valueBuilder = getValueBuilder(header, mro);
            valueBuilder.append(sepapartor).append(interFreqData.getRelativetimestamp()).append(sepapartor).append(interFreqData.getUarfcn()).append(sepapartor).append(interFreqData.getCellParamID()).append(sepapartor).append(interFreqData.getPccpchRscp());
            bw.write(valueBuilder.toString());
        }
        bw.close();
    }

    @Override
    public void run() {
            convertToCSVFiles();
    }
}