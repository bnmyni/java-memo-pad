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

package com.tuoming.mes.collect.dpp.file.openxml;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;

/**
 * Created by james on 14-6-12.
 */
public class LargeXlsxWorkBook extends DefaultHandler {
    private String lastContents;
    private String lastTagName;
    private boolean nextIsString;
    private SharedStringsTable sst;
    private int curRow = 0;
    private DataRow dataRow;
    private DataTable dataTable;
    private int curCol = 0;
    private Queue<String> sheetIndex = Queues.newConcurrentLinkedQueue();
    private Map<String, String> sheetMap = Maps.newHashMap();
    private XlsxDataRowHandler rowHandler;

    public void setHandler(XlsxDataRowHandler rowHandler) {
        this.rowHandler = rowHandler;
    }

    //只遍历一个sheet，其中sheetId为要遍历的sheet索引，从1开始，1-3
    public void processOneSheet(String filename, int sheetId) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        // rId2 found by processing the Workbook
        // 根据 rId# 或 rSheet# 查找sheet
        dataTable = new DataTable("sheet" + sheetId);
        InputStream sheet2 = r.getSheet("rId" + sheetId);
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    /**
     * 遍历 excel 文件
     */
    public void processAllSheets(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        //读取workbook
        InputStream workbook = r.getWorkbookData();
        InputSource wbSource = new InputSource(workbook);
        parser.parse(wbSource);
        workbook.close();
        //读取sheet
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            curRow = 0;
            String idx = sheetIndex.peek();
            dataTable = new DataTable(sheetMap.get(idx));
            if (rowHandler != null)
                rowHandler.startProcessSheet(idx, dataTable.getTableName());
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
        if (rowHandler != null)
            rowHandler.close();
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst)
            throws SAXException {
        XMLReader parser = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        //sheet
        if (name.equals("sheet")) {
            sheetMap.put(attributes.getValue("sheetId"), attributes.getValue("name"));
            sheetIndex.add(attributes.getValue("sheetId"));
        } else if (name.equals("c")) {// c => 单元格
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            String cellType = attributes.getValue("t");
            if (cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }
        // 置空
        lastContents = "";
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        //得到单元格内容的值
        lastContents += new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // 根据SST的索引值的到单元格的真正要存储的字符串
        // 这时characters()方法可能会被调用多次
        if (nextIsString && StringUtil.isNotEmpty(lastContents)) {
            try {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
        // 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符

        if (name.equals("v") || name.equals("t") || ("c".equals(name) && "c".equals(lastTagName))) {
            String value = lastContents.trim();
            String colName = "COL_" + curCol;
            if (!dataTable.getColumns().contains(colName)) {
                try {
                    dataTable.addColumn(colName, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (dataRow == null) {
                try {
                    dataRow = dataTable.newRow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dataRow.setValue(colName, value);
            curCol++;
        } else if (name.equals("row") && dataRow != null) {
            if (curRow > 0) {
                if (rowHandler != null)
                    rowHandler.process(null, dataRow);
            } else {
                System.out.println(dataRow.toString());
            }
            dataRow = null;
            curRow++;
            curCol = 0;
        }
        // 置空
        lastContents = "";
        lastTagName = name;
    }
}
