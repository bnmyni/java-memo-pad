/*******************************************************************************
 * Copyright (c) 2015.  Pyrlong All rights reserved.
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

package com.tuoming.mes.collect.decoder.nrm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 北向接口xml文件数据文件解析,基于sax实现对文件的解析
 * <p/>
 */
@Scope("prototype")
@Component("NRMXmlLogParser")
public class NRMXmlLogParser extends AbstractFileProcessor implements ContentHandler {
	StringBuffer buf;
	private String currentFileName;
	private static Logger logger = Logger.getLogger(NRMXmlLogParser.class);
	long parseStartTime;
	BufferedWriter bufferedWriter;
	String currentIdx;
	String currentName;
	Map<String, List<String>> counterToSave;
	List<String> counterFilter = null;
	Map<String, String> counterCache = null;
	private String serverName;
	String batch = "";
	String starttime;
	Map<String, String> colNameMap = Maps.newHashMap();

	private void parseFiles() {
		for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet())
			try {
				parseStartTime = DateUtil.getTimeinteger();
				String targetFile = fileName.getKey().substring(0, fileName.getKey().length() - 3);
				//判断是否有.done结尾的文件，有代表已处理过。
				if (super.isFileDone(targetFile))
					continue;
				//解压缩，并且打印log：Decompress file XXXXXX.xml.gz to XXXXXX.xml
				CompressionUtils.decompress(fileName.getKey(), targetFile);
				parse(targetFile, fileName.getValue());
				markFileDone(targetFile);
				logger.info("Second used : " + (DateUtil.getTimeinteger() - parseStartTime) / 1000);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
	}

	protected void parse(String fileName, Map<String, String> envs) {
		try {
			// {"file1":["12","13"],"file2":["11","1"]}
			String exp = envs.get(MESConstants.FTP_COMMAND_RESULT_FILTER);//获取结果过滤字符串
			if (StringUtil.isNotEmpty(exp))
				counterToSave = (Map<String, List<String>>) DSLUtil.getDefaultInstance().compute(exp);//将结果过滤字符串转换为map对象

			counterCache = null;
			if (envs.containsKey(MESConstants.BATCH_KEY))
				batch = envs.get(MESConstants.BATCH_KEY);//假如环境map中包含BATCH,则获取BATCH对应的值
			serverName = envs.get("ftpServer");
			counterCache = Maps.newLinkedHashMap();//新建缓存map
			if (counterToSave != null) {//将要解析的列放入缓存map中，并赋默认值
				for (Map.Entry<String, List<String>> entry : counterToSave.entrySet()) {
					if (StringUtil.isMatch(fileName, entry.getKey())) {
						counterFilter = entry.getValue();
						for (String s : counterFilter) {
							counterCache.put(s, "0");// 重置默认值
						}
					}
				}
			}
			XMLReader reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");//创建xml解析器
			reader.setContentHandler(this);//设置xml解析器的内容处理器为当前对象
			reader.setErrorHandler(new ContentErrorHandler());//设置xml解析器的错误处理器为ContentErrorHandler对象
			currentFileName = fileName;
			File file = new File(currentFileName);
			String outPutFile = file.getName().replace(".xml", ".csv");//生成csv文件名
			try {
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + outPutFile, false), csvEncoding));//创建csv输出流
				resultFiles.add(outPutFile);//将输出文件放入结果list中
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
			reader.parse(fileName);//解析文件
		} catch (IOException e) {
			System.out.println("读入文档时错: " + e.getMessage());
		} catch (SAXException e) {
			System.out.println("解析文档时错: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		parseFiles();
	}

	@Override
	public void setDocumentLocator(Locator locator) {

	}

	@Override
	public void startDocument() throws SAXException {
		buf = new StringBuffer();
		logger.info("Start parsing file :" + currentFileName);
	}

	/**
	 * 文件解析完成时关闭输出流
	 */
	@Override
	public void endDocument() throws SAXException {
		if (bufferedWriter != null)
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		logger.info("End parse file :" + currentFileName);
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {

	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {

	}

	/**
	 * 解析指定xml开始标签时，向csv文件内写入标签属性
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("Pm") || localName.equals("Cm")) {//假如标签为Pm或Cm时
			try {
				// 写入固定字段
				bufferedWriter.write(starttime);
				bufferedWriter.write(",");
				bufferedWriter.write(StringUtil.replaceAll(atts.getValue(0), "[\\w+]+=", ""));
				bufferedWriter.write(",");
				bufferedWriter.write(StringUtil.replaceAll(atts.getValue(1), "[\\w+]+=", ""));
				bufferedWriter.write(",");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (localName.equals("Measurements")) {
			colNameMap.clear();//假如标签名为Measurements时，清空colNameMap
		} else if (localName.equals("V") || localName.equals("CV") || localName.equals("N")) {
			currentIdx = atts.getValue(0);//假如标签为V或CV或N时，currentIdx为第一个属性值
		}
	}

	/**
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws SAXException
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("N".equals(localName)) {//假如当前标签为N
			currentName = buf.toString().trim();
			colNameMap.put(currentIdx, currentName);
		} else if ("SN".equals(localName)) {//假如当前标签为SN时
			currentName = buf.toString().trim();
		} else if (localName.equals("SV") || localName.equals("V")) {//假如当前标签为SV或V时
			if (localName.equals("V"))
				currentName = colNameMap.get(currentIdx);
			if (counterFilter == null) {
				logger.warn("Please check the column which name is result_filter in table aos_ftp_command,this column must not be null");
			} else if (counterFilter.contains(currentName)) {
				String val = buf.toString().trim();
				if (val.indexOf(",") > 0)
					val = "~" + val + "~";
				counterCache.put(currentName, val);
			}else if(counterFilter.contains("#"+colNameMap.get(currentIdx)+"#")) {
				double val = FormatUtil.tranferStrToNum(buf.toString().trim());
				double originVal = FormatUtil.tranferStrToNum(counterCache.get(colNameMap.get(currentIdx)));
				counterCache.put(colNameMap.get(currentIdx), String.valueOf(val+originVal));
			}
		} else if (localName.equals("Pm") || localName.equals("Cm")) {//假如当前标签为pm或cm时，将解析数据写入文件
			try {
				//
				for (String s : counterFilter) {
					bufferedWriter.write(counterCache.get(s));
					bufferedWriter.write(",");
					counterCache.put(s, "0");// 重置默认值
				}
				//
				bufferedWriter.write(batch);
				bufferedWriter.write(",");
				bufferedWriter.write(serverName);
				bufferedWriter.write(Envirment.LINE_SEPARATOR);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (localName.equals("BeginTime") || localName.equals("DateTime")) {//解析begintime或datetime标签里的时间数据
			starttime = DateUtil.format(DateUtil.addHours(DateUtil.getDate(buf.toString().replace("T", " ")
					.replace("+0800", "")), ConfigurationManager.getDefaultConfig()
					.getInteger("pyrlong.aos.timezone", 0)), "yyyy-MM-dd HH:mm:ss");
		}
		buf.setLength(0);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buf.append(ch, start, length);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {

	}

	@Override
	public void skippedEntity(String name) throws SAXException {

	}
}
