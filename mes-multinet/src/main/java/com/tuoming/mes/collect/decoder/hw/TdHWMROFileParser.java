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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.tuoming.mes.collect.decoder.nrm.ContentErrorHandler;
import com.tuoming.mes.strategy.service.handle.TdHWMROFileDecode;

/**
 * 解析MRO文件的实现类，该类基本废弃，请使用MROPartFileParser替代
 *
 * @since 1.0.1
 */
@Component("TdHWMROFileParser") 
public class TdHWMROFileParser implements ContentHandler,TdHWMROFileDecode {
    private static Logger logger = Logger.getLogger(TdHWMROFileParser.class);
    private StringBuffer buf;
    private Map<String, String> columnMap = new LinkedHashMap<String, String>();
    private Map<String, String> columnTag = new HashMap<String, String>();
    private String timeTag = "startTime";
    private String timeStamp = "TimeStamp";
    private String valueDef = "v";
    private List<String[]> resList = null;
    private static final int MR_TdScPccpchRscp  = 0;
    private static final int MR_UtranFreq  = 1;
    private static final int MR_ScrambleCode  = 3;
    private static final int MR_TdNcPccpchRscp  = 4;
    private static final int MR_TdNcellUarfcn  = 5;
    private static final int MR_TdNcellSc  = 6;


	public TdHWMROFileParser() {//构造函数，初始化该类要解析的标签属性
        columnMap.put("fileHeader-startTime", "");
        columnMap.put("rnc-id", "");
        columnMap.put("object-id", "");
        columnMap.put("object-TimeStamp", "");
        columnMap.put("object-IMSI", "");
        columnTag.put("fileHeader", "");
        columnTag.put("rnc", "");
        columnTag.put("object", "");
    }
	

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
		buf = new StringBuffer();
		resList = new ArrayList<String[]>();
//		logger.info("Start parsing file :" + currentFileName);
	}

	public void endDocument() throws SAXException {
//		logger.info("End parse file :" + currentFileName);
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
					if (attrName.endsWith(timeTag)||attrName.endsWith(timeStamp)) {
						value = value.replace("T", " ");
					}
					columnMap.put(attrName, value);
				}
			}
		}
	}


	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {
		if (fullName.equals(valueDef)) {
			String v = buf.toString().trim();
			String[] value = v.split(" ");
			//当V中数据少于11个的场合，视为数据无效
			if(value.length != 11) {
				return;
			}
			//当object-id中以“：”分割有多组数据时，cellid取第一组。没有“：”分割的场合视为该值就是cellid
			String cellid = columnMap.get("object-id");
			if(cellid.indexOf(":")>0) {
				cellid = cellid.split(":")[0];
			}
			//本小区PccpchRscp或邻小区PccpchRscp值为0时，该数据视为无效采样数据
			if(value[MR_TdScPccpchRscp].equals("0") || value[MR_TdNcPccpchRscp].equals("0")){
				return;
			}
			//当本小区与小区信息相同的场合，该邻小区为自身，数据舍弃
			if(value[MR_TdScPccpchRscp].equals(value[MR_TdNcPccpchRscp])
					&& value[MR_UtranFreq].equals(value[MR_TdNcellUarfcn])
					&& value[MR_ScrambleCode].equals(value[MR_TdNcellSc])){
				return;
			}
			//取得数据集
			String[] data = new String[10];
			data[0] = columnMap.get("rnc-id");
			data[1] = columnMap.get("object-TimeStamp");
			data[2] = columnMap.get("object-IMSI");
			data[3] = cellid;
			data[4] = value[MR_TdScPccpchRscp];
			data[5] = value[MR_UtranFreq];
			data[6] = value[MR_ScrambleCode];
			data[7] = value[MR_TdNcPccpchRscp];
			data[8] = value[MR_TdNcellUarfcn];
			data[9] = value[MR_TdNcellSc];
			resList.add(data);
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

	public List<String[]> parse(String fileName) {
		ByteArrayInputStream bais = null;
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setContentHandler(this);
			reader.setErrorHandler(new ContentErrorHandler());
			reader.parse(fileName);
			return resList;
		} catch (IOException e) {
			System.out.println("读入文档时错: " + e.getMessage());
		} catch (SAXException e) {
			System.out.println("解析文档时错: " + e.getMessage());
		}finally {
			if(bais!=null) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


}
