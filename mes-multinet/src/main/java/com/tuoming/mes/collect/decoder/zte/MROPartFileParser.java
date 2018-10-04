package com.tuoming.mes.collect.decoder.zte;

import org.apache.log4j.Logger;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * MRO文件解析实现类
 *
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("MROPartFileParser")
public class MROPartFileParser extends MROFileParser {
    private static Logger logger = Logger.getLogger(MROPartFileParser.class);
    private Map<String, Integer> columnIndex = new HashMap<String, Integer>();

    public void endElement(String namespaceURI, String localName,
                           String fullName) throws SAXException {
        if (fullName.equals(colDef)) {//判断解析的标签是否是smr(文件头)标签
            // 读到列定义，更新输出文件
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
                String[] headers = smr.trim().split("\\s+");//将文件头按照单空格解析
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
                    for (Map.Entry<String, String> c : columnMap.entrySet()) {
                        bufferedWriter.write(c.getKey());//写入通用标签属性
                        bufferedWriter.write(",");//写入csv分隔符
                    }
                    String batchSymbol = "batch_id";
                    if (columnFilter != null && !columnFilter.isEmpty()) {//假如指定要解析的列，则写入要解析的列名
                        StringBuilder tempPartFileHead = new StringBuilder();
                        for (String columnName : this.columnFilter) {
                            tempPartFileHead.append(columnName);//字符串拼接要解析的列名
                            tempPartFileHead.append(Envirment.CSV_SEPARATOR);//字符串拼接csv分隔符
                        }
                        tempPartFileHead.append(batchSymbol);//字符串拼接批id
                        bufferedWriter.write(tempPartFileHead.toString());//将拼接字符串写入文件
                    } else {//否则写入所有列名
                        bufferedWriter.write(smr.replace(" ", Envirment.CSV_SEPARATOR)
                                .replace(".", "_") + Envirment.CSV_SEPARATOR + batchSymbol);
                    }
                    bufferedWriter.write(Envirment.LINE_SEPARATOR);//写入分行符
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        } else if (fullName.equals(valueDef)) {//假如标签名等于v（值列）
            try {
                for (Map.Entry<String, String> c : columnMap.entrySet()) {
                    bufferedWriter.write(StringUtil.replaceAll(c.getValue(),
                            "[.|a-z|A-Z]+=", ""));
                    bufferedWriter.write(Envirment.CSV_SEPARATOR);
                }
                String v = buf.toString().trim();
                v = v.replace("NIL", "NULL");
                StringBuilder value = new StringBuilder();
                if (!columnFilter.isEmpty()) {
                    String[] valArr = v.trim().split("\\s+");
                    for (String c : columnFilter) {
                        if (columnIndex.get(c) != null) {
                            value.append(valArr[columnIndex.get(c)]);
                        }
                        value.append(Envirment.CSV_SEPARATOR);
                    }
                    value.append(batch);
                    bufferedWriter.write(value.toString());
                } else {
                    bufferedWriter.write(v.replace(" ", Envirment.CSV_SEPARATOR) + Envirment.CSV_SEPARATOR + batch);
                }
                bufferedWriter.write(Envirment.LINE_SEPARATOR);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        buf.setLength(0);
    }


    protected void parseFiles() {
        for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet())
            try {
                parseStartTime = DateUtil.getTimeinteger();
                String exp = fileName.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER);
                Object parserColumns = DSLUtil.getDefaultInstance().compute(exp);
                String columnSymbol = "$";
                if (parserColumns instanceof Map) {
                    columnMap.clear();
                    columnTag.clear();
                    Map<String, List<String>> parserColumnMap = (Map<String, List<String>>) parserColumns;
                    for (Entry<String, List<String>> entry : parserColumnMap.entrySet()) {
                        if (entry.getKey().startsWith(columnSymbol) && entry.getKey().endsWith(columnSymbol)) {
                            columnFilter = entry.getValue();
                        } else {
                            columnTag.put(entry.getKey(), "");
                            for (String attr : entry.getValue()) {
                                columnMap.put(entry.getKey() + "-" + attr, "");
                            }
                        }
                    }
                } else if (parserColumns instanceof List) {
                    columnFilter = (List<String>) parserColumns;
                } else {
                    columnFilter = null;
                }
                String targetFile = fileName.getKey().substring(0, fileName.getKey().length() - 3);
                if (super.isFileDone(targetFile))
                    continue;
                CompressionUtils.decompress(fileName.getKey(), targetFile);
                parse(targetFile, fileName.getValue());
                markFileDone(targetFile);
                logger.info("Second used : " + (DateUtil.getTimeinteger() - parseStartTime) / 1000);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
    }

}
