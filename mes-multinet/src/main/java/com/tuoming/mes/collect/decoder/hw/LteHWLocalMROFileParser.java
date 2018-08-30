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

import com.tuoming.mes.strategy.consts.Constant;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.tuoming.mes.collect.decoder.nrm.ContentErrorHandler;
import com.tuoming.mes.strategy.service.handle.FileDecode;

/**
 * 解析MRO文件的实现类，该类基本废弃，请使用MROPartFileParser替代
 *
 * @since 1.0.1
 */
@Component("LteHWLocalMROFileParser")
public class LteHWLocalMROFileParser implements ContentHandler,FileDecode {
    private static Logger logger = Logger.getLogger(LteHWLocalMROFileParser.class);
    private StringBuffer buf;
    private Map<String, String> columnMap = new LinkedHashMap<String, String>();
    private Map<String, String> columnTag = new HashMap<String, String>();
    private String timeTag = "startTime";
    private String timeStamp = "TimeStamp";
    private String valueDef = "v";
    private List<String[]> resList = null;
    private static final int MR_LteScRSRP  = 0;
    private static final int MR_LteNcRSRP  = 1;
    private static final int MR_LteScEarfcn  = 4;
    private static final int MR_LteScPci  = 5;
    private static final int MR_LteNcEarfcn  = 6;
    private static final int MR_LteNcPci  = 7;


	public LteHWLocalMROFileParser() {//构造函数，初始化该类要解析的标签属性
        columnMap.put("fileHeader-startTime", "");
        columnMap.put("object-id", "");
        columnMap.put("object-TimeStamp", "");
        columnMap.put("object-MmeUeS1apId", "");
        columnMap.put("object-MmeCode", "");
        columnMap.put("object-MmeGroupId", "");
        columnTag.put("fileHeader", "");
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
			if(value.length<=30) {
				return;
			}
			String eci = columnMap.get("object-id");
			String point = "0";
			//object-id分为3种格式：CellId (BIT STRING (28)、CellId:Earfcn:SubFrameNbr、CellId:Earfcn:SubFrameNbr:PRBNbr
			String [] objArray = eci.split(":");
			if(objArray.length > 2) {
				//SubFrameNbr
				point = objArray[2];
				//CellId
				eci = objArray[0];
			}
			if(point.equals("7") || value[MR_LteScRSRP].equals("0") || value[MR_LteNcRSRP].equals("0")){
				return;
			}
			if(value[MR_LteScEarfcn].equals(value[MR_LteNcEarfcn])&&value[MR_LteScPci].equals(value[MR_LteNcPci])){
				return;
			}
			//排除D频段补偿F频段的场合:sc-f nc-d
			if(value[MR_LteScEarfcn] == null || value[MR_LteScEarfcn].length() < 1 || Constant.NIL.equals(value[MR_LteScEarfcn])
					|| value[MR_LteNcEarfcn] == null || value[MR_LteNcEarfcn].length() < 1 || Constant.NIL.equals(value[MR_LteNcEarfcn])
					|| (Integer.valueOf(value[MR_LteNcEarfcn]) < 38250 && Integer.valueOf(value[MR_LteScEarfcn]) > 38250 && Integer.valueOf(value[MR_LteScEarfcn]) < 38650)){
				return;
			}
			String[] data = new String[10];
			data[0] = columnMap.get("object-MmeCode");
			data[1] = columnMap.get("object-MmeGroupId");
			data[2] = columnMap.get("object-TimeStamp");
			data[3] = eci;
			data[4] = value[MR_LteScRSRP];
			data[5] = value[MR_LteNcRSRP];
			data[6] = value[MR_LteNcEarfcn];
			data[7] = value[MR_LteNcPci];
			data[8] = columnMap.get("object-MmeUeS1apId");
			data[9] = columnMap.get("object-id");
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

	public List<String[]> parse(byte[] b) {
		ByteArrayInputStream bais = null;
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setContentHandler(this);
			reader.setErrorHandler(new ContentErrorHandler());
			bais = new ByteArrayInputStream(b);
			InputSource in = new InputSource(bais);
			reader.parse(in);
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
