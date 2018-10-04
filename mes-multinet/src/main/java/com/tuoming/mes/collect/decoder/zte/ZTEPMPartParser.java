package com.tuoming.mes.collect.decoder.zte;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import com.pyrlong.Envirment;
import com.pyrlong.util.StringUtil;

/**
 * 用以解析中兴性能xml文件，继承ZTEPMParser类，增加了过滤字段功能
 *
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("ZTEPMPartParser")
public class ZTEPMPartParser extends ZTEPMParser {
    private Map<String, Integer> columnIndex = new HashMap<String, Integer>();


    /**
     * 基于事件驱动的xml文件解析，解析结束标签时执行的方法
     */
    public void endElement(String namespaceURI, String localName,
                           String fullName) throws SAXException {
        if (fullName.equals(colDef)) {//判断解析的标签是否是smr(文件头)标签
            if (bufferedWriter != null) {//假如文件输出流不为null，则关闭该文件流
                try {
                    bufferedWriter.close();
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
            String smr = buf.toString().trim();//将当前标签体内的文本组成字符串
            String newFileName = getNewFileName().replace(".csv",
                    "_" + StringUtil.matchCount(smr, "\\s+") + ".csv");//按照规则生成要输出的文件名
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(targetPath + newFileName, false),
                        "utf-8"));//封装文件输出流
                resultFiles.add(newFileName);//记录生成的文件名
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }

            if (this.columnFilter != null && this.columnFilter.size() >= 1) {//假如数据库中配置了要解析的数据列
                String[] headers = smr.trim().split("\\s{1}");//将文件头按照单空格解析
                Map<String, Integer> tempColumnIndex = new HashMap<String, Integer>();
                for (String col : columnFilter) {//将要解析的列放入map中
                    columnIndex.put(col, null);
                    tempColumnIndex.put(col, null);
                }
                for (int i = 0, size = headers.length; i < size; i++) {//通过for循环记录要解析列的索引
                    if (tempColumnIndex.containsKey(headers[i])) {
                        columnIndex.put(headers[i], i);
                        tempColumnIndex.remove(headers[i]);
                    }
                    if (tempColumnIndex.isEmpty()) {
                        break;
                    }
                }
            }
            if (printHeander) {// 如果需要写列头
                try {
                    String batch_id = "batch_id";
                    for (Map.Entry<String, String> c : columnMap.entrySet()) {
                        bufferedWriter.write(c.getKey());//写入通用标签属性
                        bufferedWriter.write(Envirment.CSV_SEPARATOR);//写入csv分隔符
                    }
                    if (columnFilter != null && !columnFilter.isEmpty()) {//假如指定要解析的列，则写入要解析的列名
                        StringBuilder tempPartFileHead = new StringBuilder();
                        for (String columnName : this.columnFilter) {
                            tempPartFileHead.append(columnName);//字符串拼接要解析的列名
                            tempPartFileHead.append(Envirment.CSV_SEPARATOR);//字符串拼接csv分隔符
                        }
                        tempPartFileHead.append(batch_id);//字符串拼接批id
                        bufferedWriter.write(tempPartFileHead.toString());//将拼接字符串写入文件
                    } else {//否则写入所有列名
                        bufferedWriter.write(smr.replace(" ", Envirment.CSV_SEPARATOR)
                                .replace(".", "_") + batch_id);

                    }
                    bufferedWriter.write(Envirment.LINE_SEPARATOR);//写入分行符
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        } else if (fullName.equals(valueDef)) {//假如标签名等于v（值列）
            try {
                for (Map.Entry<String, String> c : columnMap.entrySet()) {//循环写入标签属性值
                    bufferedWriter.write(StringUtil.replaceAll(c.getValue(),
                            "[.|a-z|A-Z]+=", ""));//将值中=前面的内容替换为空字符串
                    bufferedWriter.write(Envirment.CSV_SEPARATOR);//写入csv分隔符
                }
                String v = buf.toString().trim();//获取当前标签内的字符串
                v = v.replace("NIL", "NULL");//将当前标签内的字符串中的NIL替换为NULL
                StringBuilder value = new StringBuilder();//定义值串
                if (columnFilter != null && !columnFilter.isEmpty()) {//假如要解析列不为空
                    String[] valArr = v.trim().split("\\s{1}");//空格分隔数据值
                    for (String c : columnFilter) {
                        if (columnIndex.get(c) != null) {
                            value.append(valArr[columnIndex.get(c)]);//for循环拼接要解析列的值
                        }
                        value.append(Envirment.CSV_SEPARATOR);//拼接csv文件分隔符
                    }
                    value.append(batch);//添加批ID的值
                    bufferedWriter.write(value.toString());//将解析的数据行写入文件中
                } else {
                    bufferedWriter.write(v.replace(" ", Envirment.CSV_SEPARATOR) + batch);//否则，将所有列数据写入文件中
                }
                bufferedWriter.write(Envirment.CSV_SEPARATOR + serverName);
                bufferedWriter.write(Envirment.LINE_SEPARATOR);//在文件中写入换行符
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        buf.setLength(0);//清空当前标签的文本数据
    }

}
