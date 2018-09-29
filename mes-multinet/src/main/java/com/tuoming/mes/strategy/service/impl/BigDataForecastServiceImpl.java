package com.tuoming.mes.strategy.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.BigDataForecastDao;
import com.tuoming.mes.strategy.model.BigDataForecastSetting;
import com.tuoming.mes.strategy.service.BeforeAfterService;
import com.tuoming.mes.strategy.service.BigDataForecastService;
import com.tuoming.mes.strategy.util.BigDataForecastUtil;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;

//@Service("bigDataForecastService")
public class BigDataForecastServiceImpl implements BigDataForecastService {
    private static Logger logger = LogFacade.getLog4j(BeforeAfterService.class);
    // 查询需要根据多少天之前的数据进行预测
    private static final String appDays = ConfigurationManager
            .getDefaultConfig().getString("bigdata_days", "30");
    @Autowired
    @Qualifier(value = "bigDataForecastDao")
    private BigDataForecastDao bigDataForecastDao;

    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    public void bigDataForcast(String groupName) {
        businessLogDao.insertLog(8, "数据预测开始", 0);
        String filePath = CsvUtil.mkParentDir(Constant.PREFIX_LSB + System.currentTimeMillis());
        List<BigDataForecastSetting> setList = bigDataForecastDao.queryForecastSet(groupName);
        // String appDays = bigDataForecastDao.queryDays();
        List<String> finalFile = new ArrayList<>();// 存放文件名称

        for (BigDataForecastSetting set : setList) {
            if (set.getGroupName().equalsIgnoreCase(Constant.GSM)) {
                Date nowDate = new Date();// 当前时间
                // 可以补预测未预测的数据
                List<Date> foreTimeList = this.getForeTimeList(set.getResTable(), nowDate);
                for (Date forecastDate : foreTimeList) {
                    // logger.info("GSM===forecastDate预测时间======"+forecastDate);
                    String nextTime = this.getNextDay(forecastDate);// 作为存储数据时间
                    String sqlTime = this
                            .getForecastTime(forecastDate, appDays);// 获得查询语句预测时间字符串

                    List<Map<String, Object>> dataList = bigDataForecastDao
                            .queryMetaData(this.getQuerySql(set.getQuerySql(),
                                    sqlTime));
                    if (set.getResTable().endsWith(Constant.VENDER_HW)) {

                        String fileName = this.gsmForecastHW(dataList, set,
                                filePath, nextTime);
                        // logger.info("GSM_HW进入预测方法nextTime--------"+nextTime+"--sqlTime---"+sqlTime+"---fileName---"+fileName);
                        finalFile.add(fileName);
                    } else {
                        String fileName = this.gsmForecast(dataList, set,
                                filePath, nextTime);
                        // logger.info("GSM进入预测方法nextTime--------"+nextTime+"--sqlTime---"+sqlTime+"---fileName---"+fileName);
                        finalFile.add(fileName);
                    }
                    this.loadFile(finalFile);
                    finalFile.clear();
                }

            } else if (set.getGroupName().equalsIgnoreCase(Constant.TD)) {
                Date nowDate = new Date();
                // 可以补预测未预测的数据
                List<Date> foreTimeList = this.getForeTimeList(
                        set.getResTable(), nowDate);
                for (Date forecastDate : foreTimeList) {
                    // logger.info("TD===forecastDate预测时间======"+forecastDate);
                    String nextTime = this.getNextDay(forecastDate);
                    String sqlTime = this
                            .getForecastTime(forecastDate, appDays);// 获得预测时间字符串

                    List<Map<String, Object>> dataList = bigDataForecastDao
                            .queryMetaData(this.getQuerySql(set.getQuerySql(),
                                    sqlTime));
                    String fileName = this.tdForecast(dataList, set, filePath,
                            nextTime);
                    // logger.info("TD进入预测方法nextTime--------"+nextTime+"--sqlTime---"+sqlTime+"---fileName---"+fileName);
                    finalFile.add(fileName);
                }
                this.loadFile(finalFile);
                finalFile.clear();

            } else if (set.getGroupName().equalsIgnoreCase(Constant.LTE)) {
                Date nowDate = new Date();
                // 可以补预测未预测的数据
                List<Date> foreTimeList = this.getForeTimeList(set.getResTable(), nowDate);
                for (Date forecastDate : foreTimeList) {
                    // logger.info("LTE===forecastDate预测时间======"+forecastDate);
                    String nextTime = this.getNextDay(forecastDate);
                    String sqlTime = this.getForecastTime(forecastDate, appDays);// 获得预测时间字符串

                    List<Map<String, Object>> dataList = bigDataForecastDao
                            .queryMetaData(this.getQuerySql(set.getQuerySql(), sqlTime));
                    String fileName = this.lteForecast(dataList, set, filePath, nextTime);
                    // logger.info("LTE进入预测方法nextTime--------"+nextTime+"--sqlTime---"+sqlTime+"---fileName---"+fileName);
                    finalFile.add(fileName);
                }
                this.loadFile(finalFile);
                finalFile.clear();
            }
        }
        businessLogDao.insertLog(8, "预测结束", 0);
    }

    private void loadFile(List<String> loadFiles) {
        // 入库
        for (String fPath : loadFiles) {
            // logger.info("文件个数"+finalFile.size());
            if (fPath.endsWith(".tmp")) {
                continue;
            }
            File file = new File(fPath);
            String fileName = file.getName();
            String table = fileName.split("-")[0];
            String dbName = fileName.split("-")[1];

            try {
                DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter()
                        .loadfile(fPath, table);
            } catch (Exception e) {
                businessLogDao.insertLog(8, "预测数据入库出现异常，fileName[" + fileName + "]", 1);
                logger.error(String.format("数据预测入库 %s 失败！", fileName), e);
            }
            logger.info(String.format("数据预测入库 %s 结束！", fileName));
        }
    }

    /**
     * GSM预测
     *
     * @return
     */
    private String gsmForecast(List<Map<String, Object>> dataList,
                               BigDataForecastSetting set, String filePath, String nextDay) {
        String targetFile = filePath
                + set.getResTable()
                + "-"
                + set.getDbName()
                + "-"
                + DateUtil.format(DateUtil.tranStrToDate(nextDay),
                "yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
        PrintStream ps = null;

        // 获得列名称
        List<String> colNameList = new ArrayList<String>();
        for (String colName : set.getColumnList()) {
            if (!colName.equalsIgnoreCase("bscid")
                    && !colName.equalsIgnoreCase("period")
                    && !colName.equalsIgnoreCase("rpttime")
                    && !colName.equalsIgnoreCase("cellitem")
                    && !colName.equalsIgnoreCase("batch_id")) {
                colNameList.add(colName);
            }
        }
        String cellKey = "";
        Map<String, List<Double>> columnList_map = new HashMap<String, List<Double>>();// 单个小区每列的N天集合
        try {
            ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
            // 查询返回结果List<Map<一条数据值>>
            for (Map<String, Object> map : dataList) {
                if (!cellKey.equals(map.get("bscid") + "@"
                        + map.get("cellitem"))) {
                    if (!"".equals(cellKey)) {
                        Map<String, Object> oneDataMap = new HashMap<String, Object>();
                        oneDataMap.put("rpttime", nextDay);// 下一天上一时段时间
                        // yyyy-mm-dd
                        // HH:MM:SS
                        oneDataMap.put("bscid", cellKey.split("@")[0]);
                        oneDataMap.put("cellitem", cellKey.split("@")[1]);

                        for (String columnKey : colNameList) {
                            List<Double> dataArray = columnList_map
                                    .get(columnKey);
                            if (dataArray == null || dataArray.size() < 1) {
                                //当该字段没有历史值时将该值赋值为不满足节能值
                                oneDataMap.put(columnKey, new Double(999999999));
                            } else if (dataArray.size() >= 2) {
                                Double[] f_data = this.listToArray(dataArray);
                                double dataNext = BigDataForecastUtil
                                        .OutlierPrediction(f_data);
                                oneDataMap.put(columnKey, dataNext);
                            } else {
                                oneDataMap.put(columnKey, dataArray.get(0));
                            }

                        }
                        columnList_map.clear();
                        // 添加单条数据
                        CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
                    }

                    cellKey = map.get("bscid") + "@" + map.get("cellitem");
                }
                // 遍历一条数据的每一列
                for (String columnKey : map.keySet()) {
                    if ("rpttime".equalsIgnoreCase(columnKey)
                            || "bscid".equalsIgnoreCase(columnKey)
                            || "cellitem".equalsIgnoreCase(columnKey)
                            || "period".equalsIgnoreCase(columnKey)
                            || "batch_id".equalsIgnoreCase(columnKey)) {
                        continue;
                    }
                    if (!columnList_map.containsKey(columnKey)) {
                        columnList_map.put(columnKey, new ArrayList<Double>());
                    }
                    Object val = map.get(columnKey);
                    if (val != null) {
                        columnList_map.get(columnKey).add(
                                Double.parseDouble(val.toString()));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            businessLogDao.insertLog(8, "GSM数据预测出现异常", 1);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            businessLogDao.insertLog(8, "GSM数据预测出现异常", 1);
            e.printStackTrace();
        } finally {
            ps.close();
        }
        return targetFile;
    }

    /**
     * GSM-HW预测
     *
     * @param dataList
     * @param set
     * @param filePath
     * @param nextDay
     */
    private String gsmForecastHW(List<Map<String, Object>> dataList,
                                 BigDataForecastSetting set, String filePath, String nextDay) {
        String targetFile = filePath
                + set.getResTable()
                + "-"
                + set.getDbName()
                + "-"
                + DateUtil.format(DateUtil.tranStrToDate(nextDay),
                "yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
        PrintStream ps = null;

        // 获得列名称
        List<String> colNameList = new ArrayList<String>();
        for (String colName : set.getColumnList()) {
            if (!colName.equalsIgnoreCase("bsc")
                    && !colName.equalsIgnoreCase("starttime")
                    && !colName.equalsIgnoreCase("99999999")
                    && !colName.equalsIgnoreCase("batch_id")) {
                colNameList.add(colName);
            }
        }
        String cellKey = "";
        Map<String, List<Double>> columnList_map = new HashMap<String, List<Double>>();// 单个小区每列的N天集合
        try {
            ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
            // 查询返回结果List<Map<一条数据值>>
            for (Map<String, Object> map : dataList) {
                if (!cellKey.equals(map.get("bsc") + "@" + map.get("99999999"))) {
                    if (!"".equals(cellKey)) {
                        Map<String, Object> oneDataMap = new HashMap<String, Object>();
                        oneDataMap.put("starttime", nextDay);// 下一天上一时段时间
                        // yyyy-mm-dd
                        // HH:MM:SS
                        oneDataMap.put("bsc", cellKey.split("@")[0]);
                        oneDataMap.put("99999999", cellKey.split("@")[1]);
                        for (String columnKey : colNameList) {
                            List<Double> dataArray = columnList_map
                                    .get(columnKey);
                            if (dataArray.size() > 2) {
                                Double[] f_data = this.listToArray(dataArray);
                                double dataNext = BigDataForecastUtil
                                        .OutlierPrediction(f_data);
                                oneDataMap.put(columnKey, dataNext);
                            } else {
                                oneDataMap.put(columnKey, dataArray.get(0));
                            }
                        }

                        columnList_map.clear();
                        // 添加单条数据
                        CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
                    }

                    cellKey = map.get("bsc") + "@" + map.get("99999999");
                }
                // 遍历一条数据的每一列
                for (String columnKey : map.keySet()) {
                    if ("bsc".equalsIgnoreCase(columnKey)
                            || "starttime".equalsIgnoreCase(columnKey)
                            || "99999999".equalsIgnoreCase(columnKey)
                            || "batch_id".equalsIgnoreCase(columnKey)) {
                        continue;
                    }
                    if (!columnList_map.containsKey(columnKey)) {
                        columnList_map.put(columnKey, new ArrayList<Double>());
                    }
                    columnList_map.get(columnKey).add(
                            Double.parseDouble(map.get(columnKey).toString()));
                }

            }
        } catch (FileNotFoundException e) {
            businessLogDao.insertLog(8, "华为GSM数据预测出现异常", 1);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            businessLogDao.insertLog(8, "华为GSM数据预测出现异常", 1);
            e.printStackTrace();
        } finally {
            ps.close();
        }
        return targetFile;
    }

    /**
     * TD预测
     *
     * @return
     */
    private String tdForecast(List<Map<String, Object>> dataList,
                              BigDataForecastSetting set, String filePath, String nextDay) {
        String targetFile = filePath
                + set.getResTable()
                + "-"
                + set.getDbName()
                + "-"
                + DateUtil.format(DateUtil.tranStrToDate(nextDay),
                "yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
        PrintStream ps = null;

        // 获得列名称
        List<String> colNameList = new ArrayList<String>();
        for (String colName : set.getColumnList()) {// 封装不需要预测的列
            if (!colName.equalsIgnoreCase("starttime")
                    && !colName.equalsIgnoreCase("rnc")
                    && !colName.equalsIgnoreCase("dn")
                    && !colName.equalsIgnoreCase("managedelement")
                    && !colName.equalsIgnoreCase("rncfunction")
                    && !colName.equalsIgnoreCase("utrancell")
                    && !colName.equalsIgnoreCase("userlabel")
                    && !colName.equalsIgnoreCase("subnetwork")
                    && !colName.equalsIgnoreCase("begintime")
                    && !colName.equalsIgnoreCase("subnetwork1")
                    && !colName.equalsIgnoreCase("dc")
                    && !colName.equalsIgnoreCase("elementtype")
                    && !colName.equalsIgnoreCase("batch_id")) {
                colNameList.add(colName);
            }
        }
        String cellKey = "";
        Map<String, List<Double>> columnList_map = new HashMap<String, List<Double>>();// 单个小区每列的N天集合
        try {
            ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);

            // 查询返回结果List<Map<一条数据值>>
            for (Map<String, Object> map : dataList) {
                String currentCellKey = map.get("rnc") + "@"
                        + map.get("utrancell") + "@"
                        + map.get("managedelement");
                if (map.containsKey("userlabel")) {
                    currentCellKey = currentCellKey + "@"
                            + map.get("userlabel");
                }
                if (!cellKey.equals(currentCellKey)) {
                    if (!"".equals(cellKey)) {
                        Map<String, Object> oneDataMap = new HashMap<String, Object>();
                        oneDataMap.put("starttime", nextDay);// 下一天上一时段时间
                        // yyyy-mm-dd
                        // HH:MM:SS
                        oneDataMap.put("rnc", cellKey.split("@")[0]);
                        oneDataMap.put("utrancell", cellKey.split("@")[1]);
                        oneDataMap.put("managedelement", cellKey.split("@")[2]);
                        if (cellKey.split("@").length == 4) {
                            oneDataMap.put("userlabel", cellKey.split("@")[3]);
                        }
                        for (String columnKey : colNameList) {
                            List<Double> dataArray = columnList_map
                                    .get(columnKey);
                            if (dataArray.size() > 2) {
                                Double[] f_data = this.listToArray(dataArray);
                                double dataNext = BigDataForecastUtil
                                        .OutlierPrediction(f_data);
                                oneDataMap.put(columnKey, dataNext);
                            } else {
                                oneDataMap.put(columnKey, dataArray.get(0));
                            }
                        }

                        columnList_map.clear();
                        // 添加单条数据
                        CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
                    }
                    cellKey = map.get("rnc") + "@" + map.get("utrancell") + "@"
                            + map.get("managedelement");
                    if (map.containsKey("userlabel")) {
                        cellKey = map.get("rnc") + "@" + map.get("utrancell")
                                + "@" + map.get("managedelement") + "@"
                                + map.get("userlabel");
                    }
                }
                // 遍历一条数据的每一列
                for (String columnKey : map.keySet()) {// 排除不需要预测的列
                    if ("starttime".equalsIgnoreCase(columnKey)
                            || "rnc".equalsIgnoreCase(columnKey)
                            || "dn".equalsIgnoreCase(columnKey)
                            || "managedelement".equalsIgnoreCase(columnKey)
                            || "rncfunction".equalsIgnoreCase(columnKey)
                            || "utrancell".equalsIgnoreCase(columnKey)
                            || "userlabel".equalsIgnoreCase(columnKey)
                            || "subnetwork".equalsIgnoreCase(columnKey)
                            || "begintime".equalsIgnoreCase(columnKey)
                            || "subnetwork1".equalsIgnoreCase(columnKey)
                            || "dc".equalsIgnoreCase(columnKey)
                            || "elementtype".equalsIgnoreCase(columnKey)
                            || "batch_id".equalsIgnoreCase(columnKey)) {
                        continue;
                    }
                    if (!columnList_map.containsKey(columnKey)) {
                        columnList_map.put(columnKey, new ArrayList<Double>());
                    }
                    columnList_map.get(columnKey).add(
                            Double.parseDouble(map.get(columnKey).toString()));
                }

            }
        } catch (FileNotFoundException e) {
            businessLogDao.insertLog(8, "TD数据预测出现异常", 1);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            businessLogDao.insertLog(8, "TD数据预测出现异常", 1);
            e.printStackTrace();
        } finally {
            ps.close();
        }
        return targetFile;
    }

    /**
     * LTE预测
     *
     * @return
     */
    private String lteForecast(List<Map<String, Object>> dataList,
                               BigDataForecastSetting set, String filePath, String nextDay) {
        String targetFile = filePath
                + set.getResTable()
                + "-"
                + set.getDbName()
                + "-"
                + DateUtil.format(DateUtil.tranStrToDate(nextDay),
                "yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
        PrintStream ps = null;
        // 获得列名称
        List<String> colNameList = new ArrayList<String>();
        for (String colName : set.getColumnList()) {
            if (!colName.equalsIgnoreCase("starttime")
                    && !colName.equalsIgnoreCase("dn")
                    && !colName.equalsIgnoreCase("subnetwork")
                    && !colName.equalsIgnoreCase("subnetwork2")
                    && !colName.equalsIgnoreCase("managedelement")
                    && !colName.equalsIgnoreCase("enbfunction")
                    && !colName.equalsIgnoreCase("eutrancelltdd")
                    && !colName.equalsIgnoreCase("userlabel")
                    && !colName.equalsIgnoreCase("localcellid")
                    && !colName.equalsIgnoreCase("server_name")
                    && !colName.equalsIgnoreCase("batch_id")) {
                colNameList.add(colName);
            }
        }
        String cellKey = "";
        Map<String, List<Double>> columnList_map = new HashMap<String, List<Double>>();// 单个小区每列的N天集合
        try {
            ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
            // 查询返回结果List<Map<一条数据值>>
            for (Map<String, Object> map : dataList) {
                String currentCellKey = map.get("managedelement") + "@"
                        + map.get("eutrancelltdd") + "@"
                        + map.get("subnetwork");
                if (map.containsKey("localcellid")) {
                    currentCellKey = currentCellKey + "@"
                            + map.get("localcellid");
                }
                if (!cellKey.equals(currentCellKey)) {
                    if (!"".equals(cellKey)) {
                        Map<String, Object> oneDataMap = new HashMap<String, Object>();
                        oneDataMap.put("starttime", nextDay);// 下一天上一时段时间
                        // yyyy-mm-dd
                        // HH:MM:SS
                        oneDataMap.put("managedelement", cellKey.split("@")[0]);
                        oneDataMap.put("eutrancelltdd", cellKey.split("@")[1]);
                        oneDataMap.put("subnetwork", cellKey.split("@")[2]);
                        if (cellKey.split("@").length == 4) {
                            oneDataMap
                                    .put("localcellid", cellKey.split("@")[3]);
                        }
                        for (String columnKey : colNameList) {
                            List<Double> dataArray = columnList_map
                                    .get(columnKey);
                            if (dataArray.size() > 2) {
                                Double[] f_data = this.listToArray(dataArray);
                                double dataNext = BigDataForecastUtil
                                        .OutlierPrediction(f_data);
                                oneDataMap.put(columnKey, dataNext);
                            } else {
                                oneDataMap.put(columnKey, dataArray.get(0));
                            }
                        }
                        columnList_map.clear();
                        // 添加单条数据
                        CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
                    }
                    cellKey = map.get("managedelement") + "@"
                            + map.get("eutrancelltdd") + "@"
                            + map.get("subnetwork");
                    if (map.containsKey("localcellid")) {
                        cellKey = map.get("managedelement") + "@"
                                + map.get("eutrancelltdd") + "@"
                                + map.get("subnetwork") + "@"
                                + map.get("localcellid");
                    }
                }
                // 遍历一条数据的每一列
                for (String columnKey : map.keySet()) {
                    if ("starttime".equalsIgnoreCase(columnKey)
                            || "dn".equalsIgnoreCase(columnKey)
                            || "subnetwork".equalsIgnoreCase(columnKey)
                            || "subnetwork2".equalsIgnoreCase(columnKey)
                            || "managedelement".equalsIgnoreCase(columnKey)
                            || "enbfunction".equalsIgnoreCase(columnKey)
                            || "eutrancelltdd".equalsIgnoreCase(columnKey)
                            || "userlabel".equalsIgnoreCase(columnKey)
                            || "localcellid".equalsIgnoreCase(columnKey)
                            || "server_name".equalsIgnoreCase(columnKey)
                            || "batch_id".equalsIgnoreCase(columnKey)) {
                        continue;
                    }
                    if (!columnList_map.containsKey(columnKey)) {
                        columnList_map.put(columnKey, new ArrayList<Double>());
                    }
                    columnList_map.get(columnKey).add(
                            Double.parseDouble(map.get(columnKey).toString()));
                }

            }
        } catch (FileNotFoundException e) {
            businessLogDao.insertLog(8, "LTE数据预测出现异常", 1);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            businessLogDao.insertLog(8, "LTE数据预测出现异常", 1);
            e.printStackTrace();
        } finally {
            ps.close();
        }
        return targetFile;
    }

    /**
     * 获得需要获取数据的时间集合，用于替换sql中的时间串
     *
     * @param appDays      预测前多少天的天数
     * @param forecastDate 预测的时刻
     * @return
     */
    private String getForecastTime(Date forecastDate, String appDays) {
        List<Date> dayList = DateUtil.getRelateDays(forecastDate,
                -Integer.parseInt(appDays));// 获得要预测的天数字符串
        String resTime = "";
        // String before15Min =
        // DateUtil.getMultiple15Min(forecastDate);//获得当前天前15min,HH:mm:SS
        String before15Min = DateUtil.format(forecastDate).substring(10);// 获得当前天前15min,HH:mm:SS
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder timeBuilder = new StringBuilder();
        for (Date day : dayList) {
            String dayStr = df.format(day).substring(0, 10);
            timeBuilder.append("'" + dayStr + before15Min + "',");
        }
        resTime = timeBuilder.toString();
        if (resTime.endsWith(",")) {
            resTime = resTime.substring(0, resTime.length() - 2);
        }
        resTime += "'";
        return resTime;
    }

    /**
     * 获得需要预测的时刻集合 可以实现数据补预测，当出现未预测的时刻时，将返回多个待预测时刻，否则将只返回一个结果
     *
     * @param table   根据那张表获得最后的预测时间
     * @param nowDate 当前时间
     * @return 待预测的时刻集合
     */
    private List<Date> getForeTimeList(String table, Date nowDate) {
        List<Date> resultList = new ArrayList<Date>();
        // 当前天的预测时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);
        cal.set(Calendar.SECOND, 0);// 采集时间不需要秒信息
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE,
                (cal.get(Calendar.MINUTE) / Constant.PM_COLLECT_LD)
                        * Constant.PM_COLLECT_LD);
        Date nowBefore15Min = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(
                cal.getTime(), 15));
        // Date nowBefore15Min =
        // DateUtil.tranStrToDate(DateUtil.getMultiple15Min(DateUtil.getBeforeMinDate(nowDate,15)));
        String column = "starttime";
        if (table.split("_")[3].equalsIgnoreCase(Constant.GSM)
                && !table.endsWith(Constant.VENDER_HW)) {
            column = "rpttime";
        }
        Date sqlTime = bigDataForecastDao.queryUniqueData(table, column);// yyyy-MM-dd
        // HH:mm:ss
        if (null == sqlTime || "".equals(sqlTime)) {
            if (nowBefore15Min.getHours() >= 0 && nowBefore15Min.getHours() <= 6) {
                resultList = DateUtil.getTimeList(new Date(), nowBefore15Min.getHours(), 7, 15);
                //resultList.add(nowBefore15Min);
            } else {
                String befortime = DateUtil.getDay(1);//获取下一天0点开始到6点的数据
                resultList = DateUtil.getTimeList(DateUtil.tranStrToDate(befortime), 0, 7, 15);
            }
        } else {
            // 数据库预测的最后时间转为预测时间
            Date sqlBeforeDate = DateUtil.getBeforeDay(sqlTime);
            while (sqlBeforeDate.before(nowBefore15Min)) {
                // 当前时间再取前15min
                sqlBeforeDate = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(sqlBeforeDate, -15));
                if (sqlBeforeDate.getHours() >= 0 && sqlBeforeDate.getHours() <= 6) {
                    resultList.add(sqlBeforeDate);
                }
            }
        }
        return resultList;
    }

    /**
     * 获得下一天前一个整15min时间 yyyy-MM-dd HH:mm:ss
     *
     * @param nowDate
     * @return
     */
    private String getNextDay(Date nowDate) {
        // String afterDay =
        // DateUtil.format(DateUtil.getDelayDay(nowDate,1)).substring(0, 11);
        // String before15Min = DateUtil.getMultiple15Min(nowDate);
        // return afterDay + before15Min;
        return DateUtil.format(DateUtil.getDelayDay(nowDate, 1));
    }

    private String getQuerySql(String sql, String timeStr) {
        return sql.replace("$TIMELINE$", timeStr);
    }

    /**
     * 将list转为数组
     *
     * @param list
     * @return
     */
    private Double[] listToArray(List<Double> list) {
        Double[] result = new Double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = Double.parseDouble(list.get(i).toString());
        }
        return result;
    }

    private static List<Date> testList() {
        List<Date> resultList = new ArrayList<Date>();
        // 当前天的预测时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.SECOND, 0);// 采集时间不需要秒信息
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE,
                (cal.get(Calendar.MINUTE) / Constant.PM_COLLECT_LD)
                        * Constant.PM_COLLECT_LD);
        Date nowBefore15Min = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(
                cal.getTime(), 15));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sqlTime;
        try {
            //sqlTime = df.parse("2016-11-25 6:45:00");
            sqlTime = null;
            // yyyy-MM-dd
//		String sqlTime = "2016-11-15 05:00:00";
            // HH:mm:ss
            if (null == sqlTime || "".equals(sqlTime)) {
                if (nowBefore15Min.getHours() >= 0 && nowBefore15Min.getHours() <= 6) {
                    resultList = DateUtil.getTimeList(new Date(), nowBefore15Min.getHours(), 7, 15);
                    //resultList.add(nowBefore15Min);
                } else {
                    String befortime = DateUtil.getDay(1);//获取下一天0点开始到6点的数据
                    resultList = DateUtil.getTimeList(DateUtil.tranStrToDate(befortime), 0, 7, 15);
                }
            } else {
                // 数据库预测的最后时间转为预测时间
                Date sqlBeforeDate = DateUtil.getBeforeDay(sqlTime);
                while (sqlBeforeDate.before(nowBefore15Min)) {
                    sqlBeforeDate = DateUtil.tranStrToDate(DateUtil
                            .getBeforeMinStr(sqlBeforeDate, -15));// 当前时间再取前15min
                    if (sqlBeforeDate.getHours() >= 0 && sqlBeforeDate.getHours() <= 6) {
                        resultList.add(sqlBeforeDate);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultList;
    }

    public static void main(String[] args) {

        System.out.println(appDays);
        Date forecastDate = new Date();
        List<Date> dayList = DateUtil.getRelateDays(forecastDate, -Integer.parseInt(appDays));// 获得要预测的天数字符串
        for (Date date : dayList) {
            System.out.println(date);
        }
        String resTime = "";
        // String before15Min =
        // DateUtil.getMultiple15Min(forecastDate);//获得当前天前15min,HH:mm:SS
        String before15Min = DateUtil.format(forecastDate).substring(10);// 获得当前天前15min,HH:mm:SS
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder timeBuilder = new StringBuilder();
        for (Date day : dayList) {
            String dayStr = df.format(day).substring(0, 10);
            timeBuilder.append("'" + dayStr + before15Min + "',");
        }
        resTime = timeBuilder.toString();
        if (resTime.endsWith(",")) {
            resTime = resTime.substring(0, resTime.length() - 2);
        }
        resTime += "'";
        System.out.println(resTime);
    }
}
