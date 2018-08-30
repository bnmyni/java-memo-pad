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

package com.tuoming.mes.collect.decoder.zte;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.collect.decoder.nrm.ContentErrorHandler;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * 解析MRS/MRO xml格式文件的基础类 <br/>
 * 本类实现了对超大xml格式文件顺序读取及解析
 *
 * @since 1.0.1
 */
public abstract class AbstractContentHandler extends AbstractFileProcessor implements ContentHandler {
	protected String batch = "";
	protected String serverName = "";
	protected StringBuffer buf;
	protected Map<String, String> columnMap = new LinkedHashMap<String, String>();
	protected Map<String, String> columnTag = new HashMap<String, String>();
	protected BufferedWriter bufferedWriter;
	protected String resultFilePrefix = "MR_";
	protected final static Logger logger = Logger.getLogger(AbstractContentHandler.class);
	protected String currentFileName = "";
	protected String timeTag = "startTime";
	protected String colDef = "smr";
	protected String valueDef = "v";
	protected long parseStartTime;
	protected List<String> columnFilter;

	public AbstractContentHandler() {

	}

	protected void setColumnFilter(String confgName) {

	}

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
		buf = new StringBuffer();
		logger.info("Start parsing file :" + currentFileName);
	}

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

	public void startElement(String namespaceURI, String localName, String fullName, Attributes attributes) throws SAXException {
		String attrName = null;
		if (columnTag.containsKey(fullName)) {
			for (int i = 0; i < attributes.getLength(); i++) {
				attrName = fullName + "-" + attributes.getLocalName(i);
				if (columnMap.containsKey(attrName)) {
					String value = attributes.getValue(i);
					if (attrName.endsWith(timeTag)) {
						value = DateUtil.format(DateUtil.addHours(DateUtil.getDate(value.replace("T", " ").replace("+0800", "")), ConfigurationManager.getDefaultConfig().getInteger("pyrlong.aos.timezone", 0)), "yyyy-MM-dd HH:mm:ss");
					}
					columnMap.put(attrName, value);
				}
			}
		}
	}

	protected abstract String getNewFileName();

	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {
		if (fullName.equals(colDef)) {
			// 读到列定义，更新输出文件
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
			String smr = buf.toString().trim();
			String newFileName = getNewFileName().replace(".csv", "_" + StringUtil.matchCount(smr, "\\s+") + ".csv");
			try {
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + newFileName, false), "utf-8"));
				resultFiles.add(newFileName);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
			// 如果需要写列头
			if (printHeander) {
				try {
					for (Map.Entry<String, String> c : columnMap.entrySet()) {
						bufferedWriter.write(c.getKey());
						bufferedWriter.write(",");
					}
					bufferedWriter.write(smr.replace(" ", ",").replace(".", "_"));
					bufferedWriter.write(Envirment.LINE_SEPARATOR);
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		} else if (fullName.equals(valueDef)) {
			try {
				for (Map.Entry<String, String> c : columnMap.entrySet()) {
					bufferedWriter.write(StringUtil.replaceAll(c.getValue(), "[.|a-z|A-Z]+=", ""));
					bufferedWriter.write(",");
				}
				String v = buf.toString().trim();
				bufferedWriter.write(v.replace(" ", ",").replace("NIL", "NULL"));
				bufferedWriter.write(Envirment.LINE_SEPARATOR);
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		buf.setLength(0);
	}

	public void characters(char[] chars, int start, int length) throws SAXException {
		buf.append(chars, start, length);
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

	protected void parseFiles() {
		for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet())
			try {
				parseStartTime = DateUtil.getTimeinteger();
				String exp = fileName.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER);
				columnFilter = (List<String>) DSLUtil.getDefaultInstance().compute(exp);
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

	protected void parse(String fileName, Map<String, String> envs) {
		try {
			if (envs.containsKey(MESConstants.BATCH_KEY))
				batch = envs.get(MESConstants.BATCH_KEY);
			if(envs.containsKey("ftpServer")) {
				serverName = envs.get("ftpServer");
			}
			XMLReader reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setContentHandler(this);
			reader.setErrorHandler(new ContentErrorHandler());
			currentFileName = fileName;
			reader.parse(fileName);
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
}
