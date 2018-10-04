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

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.logging.LogFacade;

/**
 * Created by James on 14/11/12.
 */
public class RecordType {
    static boolean printHeander = true;
    private static Logger logger = LogFacade.getLog4j(RecordType.class);
    BufferedWriter out;
    /**
     * 记录类型标识，对应定义文件内的编号
     */
    private int typeId;
    private String recordName;
    /**
     * 当前记录类型包括的数据字段名和取值方式的对应关系
     */
    private List<BytesValue> valueMap = Lists.newLinkedList();

    public RecordType(String name, int typeId, boolean printHeander) {
        this.typeId = typeId;
        this.printHeander = printHeander;
        this.recordName = name;
    }

    public String getRecordName() {
        return recordName;
    }

    public void addRecordValue(String name, BytesValue bytesValue) {
        bytesValue.setName(name);
        valueMap.add(bytesValue);
    }

    public void saveRecordToFile(String targetPath, DataInputStream dataInputStream, String batch, String bsc, int maxLength, String csvEncoding) throws IOException {
        if (out == null) {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + typeId + "_" + recordName + ".csv", false), csvEncoding));
            if (printHeander) {
                StringBuilder result = new StringBuilder();
                result.append("bsc").append(Envirment.CSV_SEPARATOR).append("record_type");
                for (BytesValue entry : valueMap) {
                    if (entry.isTemplate()) {
                        int i = 0;
                        for (i = 0; i < entry.getLength(); i++) {
                            for (BytesValue value : entry.getBytesValues()) {
                                result.append(",");
                                result.append(value.getName() + i);
                            }
                        }
                    } else {
                        result.append(",");
                        result.append(entry.getName());
                    }
                }
                result.append(",");
                result.append("batch_id");
                out.write(result.toString());
                System.out.println(result);
                out.write(Envirment.LINE_SEPARATOR);
            }
        }
        out.write(bsc);
        out.write(Envirment.CSV_SEPARATOR);
        out.write(getRecordSplitString(dataInputStream, maxLength));
        out.write(Envirment.CSV_SEPARATOR);
        out.write(batch);
        out.write(Envirment.LINE_SEPARATOR);
    }

    public void clear() {
        if (out != null) {
            try {
                out.close();
                logger.info(recordName + " clear");
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public String getRecordSplitString(DataInputStream dataInputStream, int maxlen) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append(typeId);
        int len = 0;
        for (BytesValue entry : valueMap) {
            if (entry.isTemplate()) {
                int i = 0;
                for (i = 0; i < entry.getLength(); i++) {
                    for (BytesValue value : entry.getBytesValues()) {
                        len += value.getLength();
                        if (len > maxlen) {
                            result.append(",0");
                        } else {
                            result.append(",");
                            result.append(value.getValue(dataInputStream));
                        }
                    }
                }
            } else {
                len += entry.getLength();
                result.append(",");
                String val = entry.getValue(dataInputStream);
                result.append(val);
            }
        }
        return result.toString();
    }

    public Map<String, String> getRecord(DataInputStream dataInputStream) throws IOException {
        Map<String, String> result = Maps.newLinkedHashMap();
        result.put("type_id", typeId + "");
        for (BytesValue entry : valueMap) {
            if (entry.isTemplate()) {
                int i = 0;
                for (i = 0; i < entry.getLength(); i++) {
                    for (BytesValue value : entry.getBytesValues()) {
                        result.put(entry.getName(), value.getValue(dataInputStream));
                    }
                }
            } else {
                result.put(entry.getName(), entry.getValue(dataInputStream));
            }

        }
        return result;
    }

}
