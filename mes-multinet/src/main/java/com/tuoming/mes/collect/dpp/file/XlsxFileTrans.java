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

//Created On: 13-9-13 上午10:53
package com.tuoming.mes.collect.dpp.file;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.logging.LogFacade;

/**
 * 基于Excel的表同步器实现
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */
public class XlsxFileTrans {

    private static Logger logger = LogFacade.getLog4j(XlsxFileTrans.class);


    public void updateXls(String file) throws IOException {
        Map map = new HashMap();
        map.put("XMFYBX-201301014818", null);
        Workbook wb = null;
        InputStream ifs = null;
        // 设置要读取的文件路径
        ifs = new FileInputStream(file);
        // HSSFWorkbook相当于一个excel文件，HSSFWorkbook是解析excel2007之前的版本（xls）
        wb = new HSSFWorkbook(ifs);
        int count = wb.getNumberOfSheets();
        for (int i = 0; i < count; i++) {
            Sheet sheet = wb.getSheetAt(i);
            if (sheet.getSheetName().equals("产品费用报表")) {
                System.out.println("Update sheet " + sheet.getSheetName());
                int firstRowNum = sheet.getFirstRowNum();
                int rowCount = sheet.getPhysicalNumberOfRows();
                System.out.println("Found rows " + rowCount + " first row is " + firstRowNum);
                for (int j = firstRowNum + 1; j < rowCount; j++) {
                    if (map.containsKey(sheet.getRow(j).getCell(10).getStringCellValue())) {
                        System.out.println("修改数据行:" + sheet.getRow(j).getCell(10).getStringCellValue());
                        sheet.getRow(j).getCell(0).setCellValue("是");
                        sheet.getRow(j).getCell(3).setCellValue("huanghhhhhh");
                        sheet.getRow(j).getCell(4).setCellValue("AAAAAAhuanghhhhhh");
                        sheet.getRow(j).getCell(5).setCellValue("jljlkjljkjhuanghhhhhh");
                    } else {
                        sheet.getRow(j).getCell(0).setCellValue("否");
                    }
                }
            } else {
                System.out.println("Skip sheet " + sheet.getSheetName());
            }
        }
        logger.info("update file  " + file + " done!");
        ifs.close();
        FileOutputStream fileOut = new FileOutputStream(file);
        wb.write(fileOut);
    }

    public void updateXlsm(String file) throws IOException {
        Workbook wb = null;
        InputStream ifs = null;
        // 设置要读取的文件路径
        ifs = new FileInputStream(file);

        logger.info("Import file  " + file + " done!");
        ifs.close();
    }


    private void refreashFile2(String file) throws IOException {
        Workbook wb = null;
        InputStream ifs = null;
        // 设置要b读取的文件路径
        ifs = new FileInputStream(file);
        // HSSFWorkbook相当于一个excel文件，HSSFWorkbook是解析excel2007之前的版本（xls）
        wb = new XSSFWorkbook(ifs);
        int count = wb.getNumberOfSheets();
        logger.info(String.format("There are %s sheets founded!", count));
        XSSFFormulaEvaluator.evaluateAllFormulaCells((XSSFWorkbook) wb);
        logger.info("File  " + file + " refreash!");
        ifs.close();
    }

    private void refreashFile(String file) throws IOException {
        Workbook wb = null;
        InputStream ifs = null;
        // 设置要读取的文件路径
        ifs = new FileInputStream(file);
        // HSSFWorkbook相当于一个excel文件，HSSFWorkbook是解析excel2007之前的版本（xls）
        wb = new HSSFWorkbook(ifs);
        int count = wb.getNumberOfSheets();
        logger.info(String.format("There are %s sheets founded!", count));
        HSSFFormulaEvaluator eval = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
        for (int i = 0; i < count; i++) {
            Sheet sheet = wb.getSheetAt(i);
            int firstRowNum = sheet.getFirstRowNum();
            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
            int firstCol = sheet.getRow(0).getFirstCellNum();
            for (int j = firstRowNum + 1; j < rowCount; j++) {
                for (int k = firstCol; k < colCount; k++) {
                    String val = "";
                    Cell cell = sheet.getRow(j).getCell(k);
                    if (cell == null) {
                        logger.info("Can not get cell:" + j + "," + k);
                        break;
                    } else {
                        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
                            eval.evaluateFormulaCell(cell);
                    }
                }
            }
        }
        logger.info("File  " + file + " refreash!");
        ifs.close();
    }

    public void refreashAll(List<String> files) {

    }
}
