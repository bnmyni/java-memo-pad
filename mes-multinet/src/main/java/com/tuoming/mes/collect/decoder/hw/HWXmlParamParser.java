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

package com.tuoming.mes.collect.decoder.hw;

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

import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.collect.decoder.nrm.ContentErrorHandler;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * Created by james on 14-10-17.
 */
@Scope("prototype")
@Component("HWXmlParamParser")
public class HWXmlParamParser extends AbstractFileProcessor implements ContentHandler {
    private StringBuffer buf;
    private String currentFileName;
    private static Logger logger = Logger.getLogger(HWXmlParamParser.class);
    long parseStartTime;
    BufferedWriter bufferedWriter;
    String currentIdx;
    static Map<String, List<String>> counterToSave;
    List<String> counterFilter = null;
    String batch = "";
    String starttime;
    String lineMark = "";

    public HWXmlParamParser() {

    }

    private void parseFiles() {
        for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet())
            try {
                String exp = fileName.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER);
                if (StringUtil.isNotEmpty(exp))
                    counterToSave = (Map<String, List<String>>) DSLUtil.getDefaultInstance().compute(exp);
                parseStartTime = DateUtil.getTimeinteger();
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
            counterFilter = null;
            if (envs.containsKey(MESConstants.BATCH_KEY))
                batch = envs.get(MESConstants.BATCH_KEY);
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

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {
        buf = new StringBuffer();
        logger.info("Start parsing file :" + currentFileName);
    }

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

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("class") && StringUtil.isNotEmpty(atts.getValue("parentName"))) {
            try {
                //生成一个新的输出文件
                File file = new File(currentFileName);
                String outPutFile = file.getName().replace(".xml", "") + "_" + atts.getValue("parentName") + "_" + atts.getValue("name") + ".csv";
                if (counterToSave != null && counterToSave.containsKey(atts.getValue("parentName") + "_" + atts.getValue("name")))
                    counterFilter = counterToSave.get(atts.getValue("parentName") + "_" + atts.getValue("name"));
                else
                    counterFilter = null;
                try {
                    if (bufferedWriter != null)
                        bufferedWriter.close();
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + outPutFile, false), csvEncoding));
                    resultFiles.add(outPutFile);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                } catch (FileNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (localName.equals("parameter")) {
            String name = atts.getValue("name");
            String val = atts.getValue("value");
            if (counterFilter == null || counterFilter.contains(name)) {
                try {
                    bufferedWriter.write(val);
                    bufferedWriter.write(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            currentIdx = atts.getValue(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("object")) {
            try {
                bufferedWriter.write(batch);
                bufferedWriter.write(Envirment.LINE_SEPARATOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
