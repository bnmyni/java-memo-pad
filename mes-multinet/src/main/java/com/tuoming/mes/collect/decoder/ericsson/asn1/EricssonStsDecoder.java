/*******************************************************************************
 * Copyright (c) 2013.  Pyrlong All rights reserved.
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

package com.tuoming.mes.collect.decoder.ericsson.asn1;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasData;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasDataCollection;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasFileHeader;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasInfo;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasValue;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.NEId;


/**
 * 爱立信话统数据ASN.1解码器(BER编码)
 *
 * @author Bao boyuan
 */
public class EricssonStsDecoder extends AbstractBerDecoder {

    private static Log logger = LogFactory.getLog(EricssonStsDecoder.class);
    private MeasDataCollection measDataCollection = null;
    private File file;

    /**
     * 解码器构造函数
     *
     * @param file
     *         要被解码的数据文件
     * @param collection
     *         存储解码后的数据容器
     */
    public EricssonStsDecoder(File file, MeasDataCollection collection) {
        super(file);
        this.file = file;
        this.measDataCollection = collection;
    }

    /**
     * 对数据进行解码
     * @throws IOException 
     * @throws Exception 
     */
    public void decode() throws IOException{
        logger.debug("BerEricssonStsDecoder start decoding file : " + file);
        try {
            double beginTime = System.currentTimeMillis();
            measDataCollection.setMeasFileHeader(readMeasFileHeader());
            measDataCollection.setMeasData(readMeasData());
            measDataCollection.setMeasFileFooter(readMeasFileFooter());

            double endTime = System.currentTimeMillis();
            logger.debug("Decoding has been done in " + (endTime - beginTime) / 1000 + "seconds.");

        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }

    // 读取MeasFileHeader部分
    private MeasFileHeader readMeasFileHeader() throws Exception {

        logger.debug("====Start Parse MeasFileHeader====");

        MeasFileHeader measFileHeader = new MeasFileHeader();

        // 跳过30 80 A0 80
        skipBytes(4);

        // 读取fileFormatVersion
        skipNextFixedTLV();

        // 读取senderName
        String senderName = getNextFixedValueToString();
        measFileHeader.setSenderName(senderName);

        // 截取BSC Name
        int endIdx = senderName.indexOf("/");
        //String bscName = senderName.substring(0, endIdx-3);//zhql
        String bscName;
        //xxj
        if (endIdx != -1) {
            bscName = senderName.substring(0, endIdx);
        } else {
            bscName = senderName.trim();
        }

        //与资源，参数同步，BSCNAME不加版本号
        if (bscName.contains("R12")) {
            bscName = bscName.substring(0, bscName.indexOf("R12"));
        }

        measFileHeader.setBscName(bscName);

        // 读取senderType,跳过2个byte
        // not defined for sts
        skipBytes(2);

        // 读取vendorName
        measFileHeader.setVendorName(getNextFixedValueToString());

        // 读取collectionBeginTime
        measFileHeader.setCollectionBeginTime(getNextFixedValueToTimeStamp());

        // 结束00 00
        skipBytes(2);

        logger.debug("====解析MeasFileHeader完成====");

        return measFileHeader;
    }

    // 读取readMeasData部分
    private List<MeasData> readMeasData() throws Exception {
        logger.debug("====开始解析MeasData====");

        // 循环读取MeasData
        List<MeasData> measDataList = new ArrayList<MeasData>(1);
        skipBytes(2);
        while (!isEndOfContent(2)) {

            skipBytes(2);
            while (!isEndOfContent(2)) {
                MeasData measData = new MeasData();

                // 跳过nEUserName和nEDistinguishedName
                NEId neId = new NEId();
                measData.setNEId(neId);
                skipBytes(2);
                skipNextFixedTLV();
                skipNextFixedTLV();
                skipBytes(2);

                // 循环读取MeasInfo
                List<MeasInfo> measInfoList = new ArrayList<MeasInfo>();
                skipBytes(2);
                while (!isEndOfContent(2)) {

                    skipBytes(2);
                    while (!isEndOfContent(2)) {

                        MeasInfo measInfo = new MeasInfo();

                        // 设置NE
                        measInfo.setNe(measDataCollection.getMeasFileHeader().getBscName());

                        // 取得measStartTime
                        measInfo.setMeasStartTime(getNextFixedValueToTimeStamp());

                        // 取得granularityPeriod
                        measInfo.setGranularityPeriod(getNextFixedValueToInt());

                        // 循环读取measTypes(Counter names)
                        List<String> measTypesList = new ArrayList<String>();
                        measInfo.setMeasTypes(measTypesList);

                        skipBytes(2);
                        while (!isEndOfContent(2)) {

                            measTypesList.add(getNextFixedValueToString());
                        }

                        // 循环读取measValues
                        List<MeasValue> measValuesList = new ArrayList<MeasValue>();
                        measInfo.setMeasValues(measValuesList);
                        skipBytes(2);
                        while (!isEndOfContent(2)) {

                            skipBytes(2);
                            while (!isEndOfContent(2)) {

                                MeasValue measValue = new MeasValue();

                                // 读取measObjInstId<Object Type>.<Instance Name>
                                String measObjInstId = getNextFixedValueToString();
                                measValue.setMeasObjInstId(measObjInstId);

                                // 截取Object Type
                                String[] params = measObjInstId.split("\\.");
                                measInfo.setObjectType(params[0]);

                                // 截取Mo Name
                                String moName = params[1];
                                if ("-".equals(moName)) {
                                    moName = measDataCollection.getMeasFileHeader().getBscName();
                                }
                                measValue.setMo(moName);

                                // 循环读取measResults(Counter values)
                                List<Long> resultsList = new ArrayList<Long>();
                                measValue.setMeasResults(resultsList);
                                skipBytes(2);
                                while (!isEndOfContent(2)) {

                                    // 读取measResult
                                    resultsList.add(getNextFixedValueToLong());
                                }

                                // 跳过suspectFlag
                                skipNextFixedTLV();

                                measValuesList.add(measValue);
                            }
                        }
                        measInfoList.add(measInfo);
                    }
                }
                measData.setMeasInfo(measInfoList);
                measDataList.add(measData);
            }
        }

        logger.debug("====解析MeasData完成====");
        return measDataList;
    }

    // 读取readMeasFileFooter部分
    private Timestamp readMeasFileFooter() throws IOException {
        logger.debug("====开始解析MeasFileHeader====");
        // 取得MeasFileFooter
        Timestamp measFileFooter = getNextFixedValueToTimeStamp();
        logger.debug("====解析MeasData完成====");
        return measFileFooter;
    }

}
