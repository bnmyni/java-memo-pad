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

//Created On: 13-9-13 下午2:07
package com.tuoming.mes.collect.dpp.handles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.Convert;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.DataColumn;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataTable;
import com.tuoming.mes.collect.dpp.datatype.DataTypes;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class DataRowToXlsxHandle extends AbstractDataRowHandler {

    private static Logger logger = LogFacade.getLog4j(DataRowToXlsxHandle.class);
    private Map<String, DataTable> tableLoaded = new HashMap<String, DataTable>();

    private SXSSFWorkbook currentBook;
    private Sheet currentSheet;
    int sheetCount = 0;
    CellStyle cellStyle;
    CellStyle headerStyle;
    int rowCount = 0;
    private String fileName;
    OutputStream out;
    CellStyle dateStyle;
    CellStyle doubuleStyle;
    CellStyle intStyle;

    public DataRowToXlsxHandle(String fileName) {
        this.fileName = fileName;
        try {
            out = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        // 声明一个工作薄
        currentBook = new SXSSFWorkbook(new XSSFWorkbook(), 100);
        //currentBook.setCompressTempFiles(true);
        // 生成一个样式
        headerStyle = currentBook.createCellStyle();
        // 设置这些样式
        headerStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        Font font = currentBook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        headerStyle.setFont(font);
        // 生成并设置另一个样式
        cellStyle = currentBook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        Font font2 = currentBook.createFont();
        font2.setFontHeightInPoints((short) 10);
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        cellStyle.setFont(font2);
        dateStyle = currentBook.createCellStyle();
        dateStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        dateStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dateStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        dateStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        dateStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        dateStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        dateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        dateStyle.setFont(font2);
        DataFormat format = currentBook.createDataFormat();
        dateStyle.setDataFormat(format.getFormat("yyyy-m-d h:mm:ss"));

        doubuleStyle = currentBook.createCellStyle();
        doubuleStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        doubuleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        doubuleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        doubuleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        doubuleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        doubuleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        doubuleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        doubuleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        doubuleStyle.setFont(font2);
        doubuleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));

        intStyle = currentBook.createCellStyle();
        intStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        intStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        intStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        intStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        intStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        intStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        intStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        intStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        intStyle.setFont(font2);
        intStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
    }

    String sheetName = "newSheet";

    public void setSheetName(String name) {
        sheetName = name;
    }

    private void newSheet(DataTable table) {
        if (StringUtil.isNotEmpty(table.getTableName()))
            sheetName = table.getTableName();
        sheetCount++;
        if (StringUtil.isEmpty(sheetName)) {
            sheetName = "Sheet" + sheetCount;
        }
        logger.info("Add new sheet " + sheetName);
        tableLoaded.put(sheetName, table);
        // 生成一个表格
        currentSheet = currentBook.createSheet();
        currentBook.setSheetName(sheetCount - 1, sheetName);
        // 设置表格默认列宽度为15个字节
        currentSheet.setDefaultColumnWidth((short) 30);
        //生成列头
        Row row = currentSheet.createRow(0);
        int idx = 0;
        for (DataColumn col : table.getColumns()) {
            if (!col.isDisplayed())
                continue;
            Cell cell = row.createCell(idx);
            cell.setCellStyle(headerStyle);
            RichTextString text = new XSSFRichTextString(col.getCaptionName());
            cell.setCellValue(text);
            currentSheet.setColumnWidth(idx, 3766);
            idx++;
        }
        rowCount = 1;
    }

    @Override
    public void process(String key, DataRow row) {
        if (!sheetName.equals(row.getTable().getTableName())) {
            newSheet(row.getTable());
        }
        Row dataRow = currentSheet.createRow(rowCount);
        int idx = 0;
        for (DataColumn col : row.getTable().getColumns()) {
            if (!col.isDisplayed())
                continue;
            Cell cell = dataRow.createCell(idx);
            Object val = row.getValue(col.getColumnIndex());
            if (DataTypes.isBoolean(col.getDataType())) {
                cell.setCellValue(Convert.toBoolean(val));
                cell.setCellStyle(cellStyle);
            } else if (DataTypes.isDate(col.getDataType())) {
                // cell.setCellValue((Date) val);
                //cell.setCellStyle(dateStyle);
                cell.setCellValue( DateUtil.getYmdhisStr((Date) val));
                cell.setCellStyle(cellStyle);
            } else if (DataTypes.isDouble(col.getDataType())) {
                cell.setCellValue(Convert.toDouble(val));
                cell.setCellStyle(doubuleStyle);
            } else if (DataTypes.isNum(col.getDataType())) {
                cell.setCellValue(Convert.toDouble(val));
                cell.setCellStyle(intStyle);
            } else if (DataTypes.isLong(col.getDataType())) {
                cell.setCellValue(Convert.toLong(val));
                cell.setCellStyle(intStyle);
            } else {
                cell.setCellValue(row.getValue(col.getColumnIndex()) + "");
                cell.setCellStyle(cellStyle);
            }
            idx++;
        }
        rowCount++;
    }

    @Override
    public void close() {
        FileOper.checkAndCreateForder(fileName);
        try {
            currentBook.write(out);
            out.close();
            currentBook = null;
            currentSheet = null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void loadTag(Object o) {

    }
}
