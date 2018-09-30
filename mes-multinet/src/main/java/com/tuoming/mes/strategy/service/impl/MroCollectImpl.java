package com.tuoming.mes.strategy.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.service.MroCollectService;
import com.tuoming.mes.strategy.service.handle.DataInputHandle;
import com.tuoming.mes.strategy.service.handle.DataOutPutHandle;
import com.tuoming.mes.strategy.util.FileUtil;

/**
 * 解析mro文件(test_mr_parse.py会拉起该方法)，并写入到数据rst_pm_l2l_hw_hz，rst_pm_l2l_hw_nc_info表中
 * Copyright © 2018-2028 aspire Inc. All rights reserved.
 * package: com.tuoming.mes.strategy.service.impl
 * fileName: MroCollectImpl.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/9/18 18:25
 */
@Service("MroCollectService")
public class MroCollectImpl implements MroCollectService {

    private static final Logger logger = LogFacade.getLog4j(MroCollectImpl.class);
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;
    // 模块代码
    private final int moduleType = 5;

    /**
     * 按照正则表达式(regex)取得指定路径(input)下所有层级子文件夹中的文件总个数
     *
     * @param input 扫描路径
     * @param regex 文件格式（正则表达式）
     * @return 返回符合文件格式的所有文件
     */
    private int getFileIndexList(String input, String regex, String rname) {
        File ldFile = new File(rname + Constant.scanDir + "0.csv");
        if (!ldFile.exists()) {
            putFileNameToFile(input, regex, rname);
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(ldFile)));
            while (br.ready()) {
                String line = br.readLine();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }
                return Integer.parseInt(line);
            }
        } catch (Exception e) {
            businessLogDao.insertLog(moduleType, "采集华为LTE文件获得文件出现异常", 1);
            e.printStackTrace();
            logger.error(e);
        } finally {
            closeBrAndInsertLog(br, "采集华为LTE文件关闭流出现异常");
        }
        return 0;
    }

    private void closeBrAndInsertLog(BufferedReader br, String msg) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                businessLogDao.insertLog(moduleType, "采集华为LTE文件关闭流出现异常", 1);
                e.printStackTrace();
            }
        }
    }

    /**
     * 按照正则表达式(regex)将指定路径(input)下所有层级子文件夹中的文件名记录到1~N(fileExt).csv中
     * 每500个文件名fileExt变量加1
     * 最后1~N.csv存入0.csv文件中
     *
     * @param input 需要扫描的路径
     * @param regex 目标文件格式（正则表达式）
     * @param rname 文件前缀
     */
    private static void putFileNameToFile(String input, String regex, String rname) {
        Map<String, List<String>> fileMap = FileUtil.getChildFile(input, regex);
        int fileExt = 1;
        int count = 0;
        BufferedWriter bw = null;
        String file = rname + Constant.scanDir + fileExt + ".csv";
        FileOper.checkAndCreateForder(file);
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
            for (Entry<String, List<String>> entry : fileMap.entrySet()) {
                count++;
                if (count == 500) {
                    fileExt++;
                    count = 0;
                    bw.close();
                    logger.info("file: " + file);
                    file = rname + Constant.scanDir + fileExt + ".csv";
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
                }
                for (String line : entry.getValue()) {
                    bw.write(line);
                    bw.newLine();
                }
                bw.write(Constant.END);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("scan error");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        file = rname + Constant.scanDir + File.separatorChar + "0.csv";
        bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
            bw.write(String.valueOf(fileExt));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("scan final result out error");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解析华为TD的MRO文件
     */
    @Override
    public void exeTdHwLocalAnaly(String dir, String regex) {

    }

    public void exeLteHwLocalAnaly2(int beginNum, int endNum) {
        DataInputHandle inHandle = AppContext.getBean("XmlDataInputHandle");
        DataOutPutHandle outHandle = AppContext.getBean("LteHWCellCollectOutPutHandle");
        int currentIndex = 0;
        int newNcfileNum = 0;
        for (int i = beginNum; i <= endNum; i++) {
            BufferedReader br = null;
            String fileName = Constant.scanDir + i + ".csv";
            File f = new File(fileName);
            if (!f.exists()) {
                logger.info(fileName + " file not exists!");
                continue;
            }
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                logger.info("start handle " + fileName + "  data");
                while (br.ready()) {
                    String path = br.readLine();
                    if (StringUtils.isEmpty(path)) {
                        continue;
                    }
                    if (path.endsWith(".tmp")) {
                        continue;
                    }
                    boolean beginOut = false;
                    List<String[]> dataList = null;
                    if (Constant.END.equals(path)) {
                        beginOut = true;
                        newNcfileNum++;
                    } else {
                        dataList = inHandle.readFile(path);
                        currentIndex++;
                    }
                    outHandle.handle(dataList, new Object[]{AppContext.CACHE_ROOT + "mr_lte_hw/" + i + "/", beginOut, newNcfileNum % 25 == 0});
                    if (currentIndex % 10000 == 0) {
                        logger.info("already handle " + currentIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        outHandle.destroy();
        logger.info("handle done " + currentIndex);
    }

    public void exeLteHwLocalAnaly3(int beginNum, int endNum) {
        for (int i = beginNum; i <= endNum; i++) {
            String outFilePath = "AppContext.CACHE_ROOT + \"mr_lte_hw/\" + i + \"/\"";
            String targetTable = "rst_pm_l2l_hw_hz";
            String lteTargetTable = "rst_pm_l2l_hw_nc_info";
            loadCsvFileDataToMysql(outFilePath, targetTable, lteTargetTable);
        }
    }


    /**
     * 执行mro数据文件解析，test_mr_paser.py脚本将调用该方法
     *
     * @param dir   mro数据文件目录
     * @param regex mro文件命名规则
     */
    @Override
    public void exeLteHwLocalAnaly(String dir, String regex) {
        String outFilePath = AppContext.CACHE_ROOT + "mr_lte_hw/";
        String targetTable = "rst_pm_l2l_hw_hz";
        String lteTargetTable = "rst_pm_l2l_hw_nc_info";
        String rname = "";
        executeAnaly(dir, regex, rname, outFilePath, targetTable, lteTargetTable);
    }

    /**
     * 该方法由exeLteHwLocalAnaly扩展而来，执行mro数据文件解析，test_mr_paser_*.py脚本将调用该方法
     *
     * @param dir   mro数据文件目录
     * @param regex mro文件命名规则
     */
    @Override
    public void exeLteHwLocalAnaly(String dir, String regex, String tableSuffix) {
        String outFilePath = AppContext.CACHE_ROOT + tableSuffix + "_mr_lte_hw/";
        String targetTable = "rst_l2l_hw_hz_" + tableSuffix;
        String lteTargetTable = "rst_l2l_hw_nc_" + tableSuffix;
        executeAnaly(dir, regex, tableSuffix, outFilePath, targetTable, lteTargetTable);
    }

    /**
     * 执行文件分析
     *
     * @param dir            mro数据文件目录
     * @param regex          mro文件命名规则
     * @param rname          扩展名
     * @param outFilePath    解析后的结果文件输出目录
     * @param targetTable    解析后文件命名非*LTE_NCELL_INFO*格式的csv文件写入的目标表
     * @param lteTargetTable 解析后文件命名为 *LTE_NCELL_INFO* 的csv文件写入的目标表
     */
    private void executeAnaly(String dir, String regex,
                              String rname, String outFilePath, String targetTable, String lteTargetTable) {
        businessLogDao.insertLog(moduleType, "采集华为LTE文件开始", Constant.LOG_RESULT);
        handleDataFile(dir, regex, rname, outFilePath);
        loadCsvFileDataToMysql(outFilePath, targetTable, lteTargetTable);
        businessLogDao.insertLog(moduleType, "采集华为LTE文件完成", Constant.LOG_RESULT);
    }

    private void handleDataFile(String dir, String regex, String rname, String outFilePath) {
        FileOper.delAllFile(rname + Constant.scanDir);
        int fileCount = getFileIndexList(dir, regex, rname);
        DataInputHandle inHandle = AppContext.getBean("XmlDataInputHandle");
        DataOutPutHandle outHandle = AppContext.getBean("LteHWCellCollectOutPutHandle");
        int currentIndex = 0;
        int newNcfileNum = 0;
        for (int i = 1; i <= fileCount; i++) {
            BufferedReader br = null;
            String fileName = rname + Constant.scanDir + i + ".csv";
            File f = new File(fileName);
            if (!f.exists()) {
                logger.info(fileName + " file not exists!");
                continue;
            }
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                logger.info("start handle " + fileName + "  data");
                while (br.ready()) {
                    String path = br.readLine();
                    if (StringUtils.isEmpty(path) || path.endsWith(".tmp")) {
                        continue;
                    }
                    boolean beginOut = false;
                    List<String[]> dataList = null;
                    if (Constant.END.equals(path)) {
                        beginOut = true;
                        newNcfileNum++;
                    } else {
//						logger.info("paser file "+path);
                        businessLogDao.insertLog(6, "解析开始", 0);
                        dataList = inHandle.readFile(path);
                        businessLogDao.insertLog(6, "解析结束", 0);
                        currentIndex++;
                    }
                    outHandle.handle(dataList, outFilePath, beginOut, newNcfileNum % 25 == 0);
                    if (currentIndex % 10000 == 0) {
                        logger.info("already handle " + currentIndex);
                    }
                }
            } catch (Exception e) {
                businessLogDao.insertLog(moduleType, "采集华为LTE文件读取文件出现异常", 1);
                e.printStackTrace();
                logger.info(e);
            } finally {
                closeBrAndInsertLog(br, "采集华为LTE文件关闭流出现异常");
            }
        }
        outHandle.destroy();
        logger.info("handle done " + currentIndex);
    }

    /**
     * 将Csv文件加载到的mysql数据库中
     *
     * @param outFilePath    csv文件输出路径
     * @param targetTable    解析后文件命名非*LTE_NCELL_INFO*格式的csv文件写入的目标表
     * @param lteTargetTable 解析后文件命名为 *LTE_NCELL_INFO* 的csv文件写入的目标表
     */
    private void loadCsvFileDataToMysql(String outFilePath, String targetTable, String lteTargetTable) {
        String fileExtend = ".csv";
        List<String> dbFiles = FileOper.getSubFiles(outFilePath, fileExtend);
        for (String dbFile : dbFiles) {
            try {
                if (dbFile.indexOf("LTE_NCELL_INFO") > 0) {
                    DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, lteTargetTable);
                } else {
                    DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, targetTable);
                }
            } catch (Exception e) {
                businessLogDao.insertLog(moduleType, "采集华为LTE文件数据入库出现异常", 1);
                logger.error(e);
            }
        }
    }
}
