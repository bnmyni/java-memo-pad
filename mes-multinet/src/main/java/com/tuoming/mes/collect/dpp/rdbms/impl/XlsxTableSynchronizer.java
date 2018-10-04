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

//Created On: 13-9-13 上午10:53
package com.tuoming.mes.collect.dpp.rdbms.impl;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.file.openxml.LargeXlsxWorkBook;
import com.tuoming.mes.collect.dpp.file.openxml.XlsxRowToCsvHandle;
import com.tuoming.mes.collect.dpp.handles.DataRowToXlsxHandle;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapter;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.dpp.rdbms.TableSynchronizer;

/**
 * 基于Excel的表同步器实现
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class XlsxTableSynchronizer implements TableSynchronizer {

    private static Logger logger = LogFacade.getLog4j(XlsxTableSynchronizer.class);
    String split = ConfigurationManager.getDefaultConfig().getString("pyrlong.dpp.csv_split", ",");
    private String backupPath = AppContext.CACHE_ROOT + "backup/" + DateUtil.currentDateString("yyyy_MM_dd/");
    private String backupFilePrefix = "";

    public void setBackupFilePrefix(String prefix) {
        backupFilePrefix = prefix;
    }

    public void setBackupPath(String path) {
        FileOper.checkAndCreateForder(path);
        backupPath = path;
    }

    @Override
    public String backupData(String dbName, Collection<String> tables) {
        String backDir = backupPath;
        String file = backDir + backupFilePrefix + DateUtil.currentDateString("yyyyMMdd_HHmmss") + ".xlsx";
        FileOper.checkAndCreateForder(file);
        try {
            logger.info("Start data backup");
            DataAdapter dataAdapter = DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter();
            DataRowToXlsxHandle handle = new DataRowToXlsxHandle(file);
            for (String t : tables) {
                String sql = "select * from " + t;
                logger.info("Backup " + t + " to " + file);
                dataAdapter.executeQuery("#" + t, sql, handle);
            }
            handle.close();
            logger.info("Data backup is complete");
            return file;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private void csv2db(DataAdapter dataAdapter, String tableName, String fileName, boolean clearOldData) {
        dataAdapter.lockToUpdate(tableName);
        //清空表
        if (clearOldData)
            dataAdapter.executeNonQuery(String.format("truncate table %s ", tableName));
        dataAdapter.loadfile(fileName, tableName);
        dataAdapter.unlock(tableName);
    }


    @Override
    public void restoreData(String dbName, Collection<String> tables, Collection<String> files) {
        restoreData(dbName, tables, files, false);
    }

    @Override
    public void restoreData(String dbName, Collection<String> tables, Collection<String> files, boolean clearOldData) {
        backupData(dbName, tables);
        try {
            DataAdapter dataAdapter = DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter();
            dataAdapter.disableKeyCheck();
            for (String file : files) {
                try {
                    logger.info("Import file : " + file);
                    file = Envirment.findFile(file);
                    if (file.endsWith(".xlsx")) {
                        LargeXlsxWorkBook largeXlsxWorkBook = new LargeXlsxWorkBook();
                        XlsxRowToCsvHandle handle = new XlsxRowToCsvHandle();
                        largeXlsxWorkBook.setHandler(handle);
                        largeXlsxWorkBook.processAllSheets(file);
                        Map<String, String> outputFiles = handle.getOutputFiles();
                        for (Map.Entry<String, String> entry : outputFiles.entrySet()) {
                            if (entry.getKey().startsWith("#"))
                                continue;
                            if (tables.size() > 0) {
                                for (String table : tables) {
                                    if (table.toLowerCase().equals(entry.getKey().toLowerCase())) {
                                        csv2db(dataAdapter, entry.getKey(), entry.getValue(), clearOldData);
                                    }
                                }
                            } else {
                                csv2db(dataAdapter, entry.getKey(), entry.getValue(), clearOldData);
                            }
                        }
                    } else {
                        Workbook wb = null;
                        InputStream ifs = null;
                        // 设置要读取的文件路径
                        ifs = new FileInputStream(file);
                        // HSSFWorkbook相当于一个excel文件，HSSFWorkbook是解析excel2007之前的版本（xls）
                        wb = new HSSFWorkbook(ifs);
                        int count = wb.getNumberOfSheets();
                        logger.info(String.format("There are %s sheets founded!", count));
                        for (int i = 0; i < count; i++) {
                            Sheet sheet = wb.getSheetAt(i);
                            if (tables.size() > 0) {
                                for (String table : tables) {
                                    if (table.toLowerCase().equals(sheet.getSheetName().toLowerCase())) {
                                        restore(dataAdapter, sheet, table, clearOldData);
                                    }
                                }
                            } else if (!sheet.getSheetName().startsWith("#")) {
                                restore(dataAdapter, sheet, sheet.getSheetName(), clearOldData);
                            }
                        }
                        logger.info("Import file  " + file + " done!");
                        ifs.close();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            dataAdapter.enableKeyCheck();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }
    }

    private void restore(DataAdapter dataAdapter, Sheet sheet, String table, boolean clearOldData) throws IOException {
        String csvTempFile = AppContext.getCacheFileName("restore" + Envirment.PATH_SEPARATOR + table + ".csv");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvTempFile, false), "utf-8"));
        //通过Sheet名和表进行匹配，据此生成csv文件导入数据库 ，默认文件第一行为说明行，不计做数据
        int firstRowNum = sheet.getFirstRowNum();
        int rowCount = sheet.getPhysicalNumberOfRows();
        if (rowCount <= 1)
            return;
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
        int firstCol = sheet.getRow(0).getFirstCellNum();
        logger.info("Import table " + table);
        logger.info(colCount + " columns founded");
        for (int j = firstRowNum + 1; j < rowCount; j++) {
            StringBuilder line = new StringBuilder();
            for (int k = firstCol; k < colCount; k++) {
                String val = "";
                Cell cell = sheet.getRow(j).getCell(k);
                if (cell == null) {
                    logger.info("Can not get cell:" + j + "," + k);
                    break;
                }
                switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            double d = cell.getNumericCellValue();
                            Date date = HSSFDateUtil.getJavaDate(d);
                            SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            val = dformat.format(date);
                        } else {
                            NumberFormat nf = NumberFormat.getInstance();
                            nf.setGroupingUsed(false);
                            val = nf.format(cell.getNumericCellValue());//数值类型的数据为double，所以需要转换一下
                        }
                        break;
                    case HSSFCell.CELL_TYPE_STRING:
                        val = cell.getStringCellValue();
                        break;
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        val = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA:
                        val = String.valueOf(cell.getCellFormula());
                        break;
                    default:
                        val = "";
                        break;
                }
                if (val.toString().indexOf(",") >= 0)
                    val = "~" + val + "~";
                val = val.replace("\\", "\\\\");
                line.append(val);
                line.append(split);
            }
            // 写入文件
            out.write(line.substring(0, line.length() - 1));
            out.write(Envirment.LINE_SEPARATOR);
        }
        out.close();
        csv2db(dataAdapter, table, csvTempFile, clearOldData);
    }

    @Override
    public void restoreData(String dbName, Collection<String> files) {
        restoreData(dbName, files, false);
    }

    @Override
    public void restoreData(String dbName, Collection<String> files, boolean clearOldData) {
        restoreData(dbName, new ArrayList<String>(), files, clearOldData);
    }
}
