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

package com.tuoming.mes.collect.decoder.ericsson;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.decoder.nrm.ContentErrorHandler;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * Created by James on 15/3/13.
 */
@Scope("prototype")
@Component("EriXmlParser")
public class EriXmlParser extends AbstractFileProcessor implements ContentHandler {
    private static Logger logger = Logger.getLogger(EriXmlParser.class);
    String currentFileName;
    BufferedWriter bufferedWriter;
    long parseStartTime;
    Map<String, List<String>> counterToSave;
    List<String> counterFilter = null;
    String batch;
    String neMeContext;
    String timeStamp;
    List<String> currentCounterList = Lists.newLinkedList();
    int currentCounterIdx = 0;
    Map<String, BufferedWriter> writerMap = Maps.newHashMap();
    private StringBuffer buf;

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
        for (Map.Entry<String, BufferedWriter> entry : writerMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        try {
            if ("mv".equals(localName)) {
                bufferedWriter = getWriter(currentCounterList);
                if (bufferedWriter != null) {
                    bufferedWriter.write(timeStamp);
                    bufferedWriter.write(",");
                    bufferedWriter.write(neMeContext);
                    bufferedWriter.write(",");
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            String val = buf.toString().trim();
            if ("nedn".equals(localName)) {
                neMeContext = val;
            } else if ("mts".equals(localName)) {
                timeStamp = val.substring(0, 4) + "-" + val.substring(4, 6) + "-" + val.substring(6, 8) + " " + val.substring(8, 10) + ":" + val.substring(10, 12) + ":" + val.substring(12, 14);
            } else if ("mt".equals(localName)) {
                currentCounterList.add(val);
            } else if ("md".equals(localName)) {
                currentCounterList.clear();
            } else if ("moid".equals(localName)) {
                if (bufferedWriter != null) {
                    if (val.indexOf(",") > 0)
                        val = "~" + val + "~";
                    bufferedWriter.write(val);
                    bufferedWriter.write(",");
                }
            } else if ("r".equals(localName)) {
                String colName = currentCounterList.get(currentCounterIdx);
                if ((counterFilter == null || counterFilter.size() == 0 || counterFilter.contains(colName)) && bufferedWriter != null) {
                    if (val.indexOf(",") > 0)
                        val = "~" + val + "~";
                    bufferedWriter.write(val);
                    bufferedWriter.write(",");
                }
                currentCounterIdx++;
            } else if ("mv".equals(localName)) {
                if (bufferedWriter != null)
                    bufferedWriter.write(Envirment.LINE_SEPARATOR);
                currentCounterIdx = 0;
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
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

    private BufferedWriter getWriter(List<String> counterList) {
        //File file = new File(currentFileName);
        String outPutFile = counterList.get(0);
        counterFilter = null;
        for (String s : counterList) {
            if (counterToSave != null && counterToSave.containsKey(s)) {
                counterFilter = counterToSave.get(s);
                outPutFile = s;
                break;
            }
        }
        if (outPutFile == null)
            return null;
        try {
            if (writerMap.containsKey(outPutFile))
                return writerMap.get(outPutFile);
            BufferedWriter writer;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + outPutFile + ".csv", false), csvEncoding));
            writerMap.put(outPutFile, writer);
            resultFiles.add(outPutFile + ".csv");
            return writer;
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private void parseFiles() {
        for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet())
            try {
                parseStartTime = DateUtil.getTimeinteger();
                String targetFile = fileName.getKey();// fileName.getKey().substring(0, fileName.getKey().length() - 3);
                //if (super.isFileDone(targetFile))
                //    continue;
                //CompressionUtils.decompress(fileName.getKey(), targetFile);
                parse(targetFile, fileName.getValue());
                markFileDone(targetFile);
                logger.info("Second used : " + (DateUtil.getTimeinteger() - parseStartTime) / 1000);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
    }

    protected void parse(String fileName, Map<String, String> envs) {
        try {
            String exp = envs.get(MESConstants.FTP_COMMAND_RESULT_FILTER);
            if (StringUtil.isNotEmpty(exp))
                counterToSave = (Map<String, List<String>>) DSLUtil.getDefaultInstance().compute(exp);

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
}
