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

package com.tuoming.mes.collect.decoder.ericsson;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;
import com.pyrlong.Constants;
import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.decoder.ericsson.asn1.EricssonStsDecoder;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasData;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasDataCollection;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasInfo;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasValue;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;


/**
 * 爱立信文件解析器,二进制文件解析类(ASN)
 *
 * @author zhql
 */
@Scope("prototype")
@Component("EriAsnFileHandle")
public class EriAsnFileHandle extends AbstractFileProcessor {
    private static Logger logger = Logger.getLogger(EriAsnFileHandle.class);

    private MeasDataCollection mdc = null;
    private EricssonStsDecoder esd = null;
    private BufferedWriter bw = null;
    private List<String> objectTypes = new ArrayList<String>();
    private Map<String, String> objectTypeParsed = Maps.newHashMap();

    public EriAsnFileHandle() {
        super();
    }

    public List<String> convertToCSVFiles(List<String> fileList) throws IOException, ParseException {
        setFiles(fileList);
        return convertToCSVFiles();
    }

    public List<String> convertToCSVFiles() throws IOException, ParseException {
        if (sourceFileList == null)
            return resultFiles;
        //数据保存路径
        String dataPath = targetPath;
        //分隔符
        String separator = Envirment.CSV_SEPARATOR;
        //Key 为各分组的组名BSC，Value为各组下的ObjectType集合
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (Map.Entry<String, Map<String, String>> fileSet : sourceFileList.entrySet()) {
            try {
                String fileName = fileSet.getKey();
                //初始化当前配置的需解析数据列表
                String exp = fileSet.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER);
                if (StringUtil.isEmpty(exp))
                    exp = "[]";
                objectTypes = (List<String>) DSLUtil.getDefaultInstance().compute(exp);
                if (isFileDone(fileName) || fileName.endsWith(".done"))
                    continue;
                mdc = new MeasDataCollection();

                File file = new File(fileName);
                //C20130728.2300-20130729.0000_JNBSC15_1002
                String timestamp = file.getName();
                timestamp = timestamp.substring(1, 5) + "-" + timestamp.substring(5, 7) + "-" + timestamp.substring(7, 9) + " " + timestamp.substring(10, 12) + ":"
                        + timestamp.substring(12, 14) + ":00";
                timestamp = DateUtil.format(DateUtil.addHours(DateUtil.getDate(timestamp), ConfigurationManager.getDefaultConfig().getInteger(Constants.TIMEZONE, 8)), "yyyy-MM-dd HH:mm:ss");
                //
                esd = new EricssonStsDecoder(file, mdc);
                esd.decode();//解码操作
                //由解析器提取的bsc名称
                String bscName = mdc.getMeasFileHeader().getBscName();
                logger.debug("=====bsc:" + bscName + "========");
                if (map.get(bscName) == null) {//如果该分组不存在则新增
                    map.put(bscName, new ArrayList<String>());
                }
                List<String> list = map.get(bscName);
                List<MeasData> measList = mdc.getMeasData();
                for (MeasData md : measList) {
                    List<MeasInfo> measInfoList = md.getMeasInfo();
                    for (MeasInfo measInfo : measInfoList) {
                        String csvFileName = "";
                        File csv = null;
                        String objectName = measInfo.getObjectType();//objectType－CSV文件名
                        //如果该小区在同分组内已存在则废弃和字典里没有的也要废弃
                        String tempKey = bscName + objectName + timestamp;
                        if (null != objectName && objectTypes.contains(objectName) && !objectTypeParsed.containsKey(tempKey)) {
                            csvFileName = new String(measInfo.getObjectType() + ".csv");
                            objectTypeParsed.put(tempKey, null);
                            //创建CSV数据文件
                            csv = new File(dataPath + csvFileName);
                            list.add(objectName);
                            if (!resultFiles.contains(csvFileName)) {
                                //统计导出CSV文件名列表
                                resultFiles.add(csvFileName);
                            }
                        } else {
                            continue;
                        }
                        boolean isExists = csv.exists();
                        logger.debug("--导出CSV文件路径: " + csv.getAbsolutePath());
                        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(csv, true), csvEncoding);//创建指定编码的csv文件输出流
                        bw = new BufferedWriter(out);//将字节流封装为字符流
                        StringBuffer typeSb = null;
                        if (!isExists && printHeander) {//假如需要打印列头，则打印列头
                            List<String> measTypes = measInfo.getMeasTypes();
                            typeSb = new StringBuffer("BSC").append(separator).append("PERIOD").append(separator).append("TIME_STAMP").append(separator).append("NE_NAME");
                            for (String type : measTypes) {
                                typeSb.append(separator).append(type);
                            }
                            bw.write(typeSb.append(separator).append(MESConstants.BATCH_KEY).toString());
                            bw.newLine();
                        }
                        //行值
                        List<MeasValue> measValues = measInfo.getMeasValues();
                        logger.debug("--导出记录总数据:" + measValues.size());
                        StringBuffer valueSb = null;
                        for (MeasValue value : measValues) {//for循环打印数据值
                            String mo = value.getMo();
                            if (mo.indexOf("-") > 0) {
                                mo = mo.replace("-", separator);//将-替换为逗号
                            }
                            List<Long> resultValue = value.getMeasResults();//获取每一行的数据结果
                            valueSb = new StringBuffer();//定义数据行变量
                            valueSb.append(bscName).append(separator).append(measInfo.getGranularityPeriod()).append(separator)
                                    .append(timestamp)
                                    .append(separator)
                                    .append(mo);//拼接数据结果
                            for (Long value1 : resultValue) {
                                valueSb.append(separator).append(value1.longValue());
                            }
                            valueSb.append(separator).append(fileSet.getValue().get(MESConstants.BATCH_KEY));
                            bw.append(valueSb.toString());//写入数据
                            bw.newLine();//换行
                        }
                        bw.flush();
                        bw.close();
                    }
                }
                markFileDone(fileName);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        return resultFiles;
    }


    @Override
    public void run() {
        try {
            convertToCSVFiles();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
