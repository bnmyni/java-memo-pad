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

package com.tuoming.mes.collect.decoder.hw;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.CharacterSetToolkit;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * Mrf文件解析工具类
 */
@Scope("prototype")
@Component("MrfFileHandle")
public class MrfFileHandle extends AbstractFileProcessor {
    private static Logger logger = LogFacade.getLog4j(MrfFileHandle.class);
    List<CounterGroup> counterGroups = new ArrayList<CounterGroup>();
    Map<String, Map<String, String>> groupCountersMaps = new HashMap<String, Map<String, String>>();
    private String batchId = "-1";
    String nullValue;

    /**
     * 构造函数，传入一个要解析的数据文件初始化本解析器
     */
    public MrfFileHandle() {
        nullValue = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_NULL_VALUE, nullValue);
    }

    private void updateFilter(String filterString) {
    	counterGroups.clear();
    	groupCountersMaps.clear();
        //初始化要解析的列表
        if (StringUtil.isNotBlank(filterString)) {//假如过滤字符串不为空
        	
            Map<String, List<String>> counterMaps = (Map<String, List<String>>) DSLUtil.getDefaultInstance().compute(filterString);//将字符串解析为Map集合
            for (Map.Entry<String, List<String>> entry : counterMaps.entrySet()) {
                CounterGroup counterGroup = new CounterGroup(entry.getKey());//创建计数器分组
                counterGroups.add(counterGroup);//在counterGroups集合中增加创建的计数器分组
                Map<String, String> counters = new LinkedHashMap<String, String>(3 + entry.getValue().size());//新建map
                counters.put("bsc", "");//在map中初始化赋值
                counters.put("timestamp", "");
                counters.put("99999999", "");
                for (String counter : entry.getValue()) {
                    counters.put(counter, "0");
                }
                groupCountersMaps.put(entry.getKey(), counters);//将创建的map放入全局groupCountersMaps中
            }
        }
    }

    private void parseFiles() {
        for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet()) {//循环解析文件
        	try {
        		String targetFile = fileName.getKey().substring(0, fileName.getKey().length() - 3);//获取文件名
        		if (super.isFileDone(fileName.getKey()))//假如文件已经被解析，则进入下一循环
        			continue;
        		if (!fileName.getKey().endsWith("mrf"))// 假如文件不以mrf结尾, 则解压缩文件
        			CompressionUtils.decompress(fileName.getKey(), targetFile);
        		else
        			targetFile = fileName.getKey();
        		updateFilter(fileName.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER));
        		parse(targetFile, fileName.getValue());//解析文件
        		markFileDone(fileName.getKey());//标识文件是否处理过
        		markFileDone(targetFile);//删除解压后的文件
        	} catch (Exception e) {
        		logger.error(e.getMessage(), e);
        	}
        }
    }

    /**
     * 解析文件内容到指定的结果文件
     *
     * @param fileName
     */
    public void parse(String fileName, Map<String, String> envs) {
        try {
            File file = new File(fileName);//创建文件类型实例
            String bsc = envs.get("ftpServer");//从环境变量中获取ftp服务器
            batchId = envs.get(MESConstants.BATCH_KEY);//从环境变量中获取批id
            String timestamp = StringUtil.substring(file.getName(), 1, 5) + "-" + StringUtil.substring(file.getName(), 5, 7) + "-" + StringUtil.substring(file.getName(), 7, 9) + " " + StringUtil.substring(file.getName(), 10, 12) + ":" + StringUtil.substring(file.getName(), 12, 14) + ":00";
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(fileName)));//创建文件输入流
            int workCount = 0;
            byte[] buffer = new byte[4];
            if (dis != null) {
                while (dis.available() > 0) {//当输入流可用时
                    dis.read(buffer, 0, 4);//读入四个字节到buffer数组中
                    String code = CharacterSetToolkit.toHexString(buffer);//将buffer数组的byte字节转换为16进制字符串
                    for (CounterGroup group : counterGroups) {
                        String[] groupIds = group.getGroupId().split(",");//将分组ID用, 分隔
                        if (groupIds[0].equals(code)) {//找到要解析的指标组
                            Map<String, String> counters = groupCountersMaps.get(group.getGroupId());
                            String resultFile = bsc + "_" + group.getGroupId() + "_" + timestamp.replace("-", "_").replace(":", "_").replace(" ", "") + ".csv";
                            logger.info("Parse " + code);
                            group.getCounters().clear();
                            //找到要解析的分组数据段
                            Counter counter = new Counter();
                            counter.setSize(dis.readInt());
                            if (groupIds.length > 1 && !groupIds[1].equals(counter.getSize().toString()))
                                continue;
                            counter.setCounterId("99999999");
                            counter.setTypeId(0);
                            group.getCounters().add(counter);
                            group.setCount(dis.readInt());
                            workCount++;
                            logger.info("There are " + group.getCount() + " counters  in group " + group.getGroupId());
                            //循环读取指标配置
                            for (int i = 0; i < group.getCount(); i++) {
                                counter = new Counter();
                                counter.setCounterId(dis.readInt() + "");
                                counter.setTypeId(dis.readInt());
                                counter.setSize(dis.readInt());
                                group.getCounters().add(counter);
//                                logger.info("Found counter : " + counter.getCounterId() + ",type:" + counter.getTypeId() + ",size:" + counter.getSize());
                            }
                            //解析当前分组的数据
                            int rowCount = dis.readInt();
                            logger.info("There are " + rowCount + " rows ");
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + resultFile, false), csvEncoding));
                            //在生成入库文件时由于不需要列头，所以隐藏这个
                            if (printHeander) {
                                for (Map.Entry<String, String> entry : counters.entrySet()) {
                                    out.write(entry.getKey() + ",");
                                }
                                out.write("batch_id\r\n");
                            }
                            buffer = new byte[128];
                            for (int i = 0; i < rowCount; i++) {
                                //
                                counters.put("bsc", bsc);
                                counters.put("timestamp", timestamp);
                                for (Counter c : group.getCounters()) {
                                    if (!counters.containsKey(c.getCounterId()) && counters.size() > 0) {
                                        dis.skipBytes(c.getSize());
                                        continue;
                                    }
                                    String value = "";
                                    buffer = new byte[c.getSize()];
                                    if (c.getTypeId() == 0) {
                                        dis.read(buffer, 0, c.getSize());
                                        value = new String(buffer, 0, c.getSize(),"UTF-8").trim();
                                    } else if (c.getTypeId() == 103 || c.getTypeId() == 3) {
                                        value = dis.readDouble() + "";
                                    } else if (c.getSize() == 8) {
                                        value = dis.readLong() + "";
                                    } else if (c.getSize() == 4) {
                                        value = dis.readInt() + "";
                                    }
                                    if (value.toLowerCase().equals("nan"))
                                        value = nullValue;
                                    counters.put(c.getCounterId(), value);
                                }
                                //写文件
                                for (Map.Entry<String, String> entry : counters.entrySet()) {
                                    if (StringUtil.isNotEmpty(entry.getValue()))
                                        out.write(entry.getValue());
                                    else
                                        out.write("");
                                    out.write(",");
                                    entry.setValue("");
                                }
                                out.write(batchId);
                                out.write("\r\n");
                                dis.read(buffer, 0, 4);
                            }
                            out.close();
                            resultFiles.add(resultFile);
                            buffer = new byte[4];
                            logger.info("Parse " + code + " done.");
                        }
                    }
                    //如果所有分组都已解析则退出文件处理
                    if (workCount == counterGroups.size())
                        break;
                }
                //当解析多个类别指标时需要重新读入文件，否则会出现读不到分组号的情况
                dis.close();
            } else {
                logger.error("Error open mrf file " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        parseFiles();
    }
}
