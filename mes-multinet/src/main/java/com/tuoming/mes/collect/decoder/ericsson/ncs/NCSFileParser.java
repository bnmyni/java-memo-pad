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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.pyrlong.util.ConvertBinaryUtil;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;

/**
 * Created by James on 14/11/12.
 */
@Scope("prototype")
@Component("NCSFileParser")
public class NCSFileParser extends AbstractFileProcessor {
    private static Logger logger = Logger.getLogger(NCSFileParser.class);
    Map<Integer, Integer> countMap = Maps.newHashMap();
    private String batchId;

    private void parserFiles() throws IOException {
        NCSRecordFactory ncsRecordFactory = new NCSRecordFactory(printHeander);
        int fileIdx = 0;
        for (Map.Entry<String, Map<String, String>> fileSet : sourceFileList.entrySet()) {
        	String bsc = fileSet.getValue().get("ftpServer");
            String fileName = fileSet.getKey();
            fileIdx++;
            batchId = DateUtil.getTimeinteger() + "" + fileIdx;// fileSet.getValue().get(AOSConstants.BATCH_KEY);
            if (isFileDone(fileName) || fileName.endsWith(".done"))
                continue;
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(fileName)));
            if (dis != null) {
                while (dis.available() > 0) {
                    byte[] buffer = new byte[1];
                    //读取record type
                    dis.read(buffer, 0, 1);
                    int recordType = ConvertBinaryUtil.bytesToInt(buffer);
                    buffer = new byte[2];
                    dis.read(buffer, 0, 2);
                    byte last = buffer[0];
                    buffer[0] = buffer[1];
                    buffer[1] = last;
                    int recordLength = ConvertBinaryUtil.bytesToInt(buffer);
                    if (recordType == 0 && recordLength == 0)
                        continue;
//                    System.out.println(recordType + " length=" + recordLength);
                    RecordType rt = ncsRecordFactory.getRecordType(recordType);
                    if (countMap.containsKey(recordType)) {
                        countMap.put(recordType, countMap.get(recordType) + 1);
                    } else {
                        countMap.put(recordType, 1);
                    }
                    if (rt == null) {
                        dis.skipBytes(recordLength - 3);
                    } else {
                        rt.saveRecordToFile(targetPath, dis, batchId, bsc, recordLength - 3,csvEncoding);
                    }
                }
            }
            markFileDone(fileName);
        }
        ncsRecordFactory.clear();
        System.out.println(countMap);
        //设置文件输出结果
        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            if (ncsRecordFactory.getRecordType(entry.getKey()) != null)
                resultFiles.add(entry.getKey() + "_" + ncsRecordFactory.getRecordType(entry.getKey()).getRecordName() + ".csv");
        }
    }

    @Override
    public void run() {
        try {
            parserFiles();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
