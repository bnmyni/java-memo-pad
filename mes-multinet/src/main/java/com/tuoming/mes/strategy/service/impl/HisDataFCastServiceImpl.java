package com.tuoming.mes.strategy.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.HisFCastSettingDao;
import com.tuoming.mes.strategy.model.HisDataFCastSetting;
import com.tuoming.mes.strategy.service.HisDataFCastService;
import com.tuoming.mes.strategy.service.thread.HisFacastThread;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;

@Service("hisDataFCastService")
public class HisDataFCastServiceImpl implements HisDataFCastService {
    private final static Logger logger = LogFacade.getLog4j(HisDataFCastServiceImpl.class);
    @Autowired
    @Qualifier("hisFCastSettingDao")
    private HisFCastSettingDao hisFCastSettingDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    /**
     * 历史数据预测
     */
    public void fCastNextData(String groupName) {
        businessLogDao.insertLog(7, "历史数据预测开始", 0);
        String parentDir = CsvUtil.mkParentDir(Constant.PREFIX_LSB);
        List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<ThreadPoolExecutor>();
        List<HisDataFCastSetting> conList = hisFCastSettingDao.queryFCastConByGroup(groupName);
        Date nextDay = DateUtil.getNextDay();
        for (HisDataFCastSetting setting : conList) {//循环遍历对不同的表进行预测
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_CORE_POOL_SIZE, 2),
                    ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                    ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            List<Date> timeList = DateUtil.getTimeList(nextDay, setting.getBeginHour(),
                    setting.getEndHour(), setting.getMinuteInterval());//计算出要预测的时间点
            for (Date date : timeList) {
                threadPoolExecutor.execute(new HisFacastThread(date, setting));
            }
            threadPoolExecutor.shutdown();
            threadPoolExecutors.add(threadPoolExecutor);
        }
        for (ThreadPoolExecutor threadPoolExecutor : threadPoolExecutors) {

            while (threadPoolExecutor.isShutdown() && threadPoolExecutor.getPoolSize() > 0) {

                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    businessLogDao.insertLog(7, "线程异常", 1);
                    e.printStackTrace();
                }
            }
        }

        List<String> finalFile = FileOper.getSubFiles(parentDir, CsvUtil.CSV_TYPE, false);
        Map<String, List<String>> finalFileMap = new HashMap<String, List<String>>();
        for (String filePath : finalFile) {
            if (filePath.endsWith(".tmp")) {
                continue;
            }
            File file = new File(filePath);
            String tableDb = file.getName().substring(0, file.getName().lastIndexOf("-"));
            if (finalFileMap.get(tableDb) == null) {
                finalFileMap.put(tableDb, new ArrayList<String>());
            }
            finalFileMap.get(tableDb).add(filePath);
        }
        String outFileDir = parentDir + "out/";
        FileOper.checkAndCreateForder(outFileDir);
        for (Entry<String, List<String>> entry : finalFileMap.entrySet()) {
            String[] meta = entry.getKey().split("-");
            String outFilename = outFileDir + entry.getKey() + CsvUtil.CSV_TYPE;
            for (String inFilename : entry.getValue()) {
                try {
                    FileOper.copyFile(inFilename, outFilename, true);
                } catch (IOException e) {
                    logger.warn(String.format("历史业务预测数据合并  %s 文件到  %s 文件失败！", inFilename, outFilename));
                    businessLogDao.insertLog(7, String.format("历史业务预测数据合并  %s 文件到  %s 文件失败！", inFilename, outFilename), 1);
                    e.printStackTrace();
                }
            }
            try {
                hisFCastSettingDao.removeRstTable(meta[0]);
                DataAdapterPool.getDataAdapterPool(meta[1]).getDataAdapter()
                        .loadfile(outFilename, meta[0]);
            } catch (Exception e) {
                logger.warn(String.format("历史业务预测数据入库 %s 失败！", outFilename));
                businessLogDao.insertLog(7, String.format("历史业务预测数据入库 %s 失败！", outFilename), 1);
                e.printStackTrace();
            }
            logger.warn(String.format("历史业务预测数据入库 %s 结束！", outFilename));
        }
        businessLogDao.insertLog(7, "历史数据预测结束", 0);
    }


    @Override
    public Map<String, String> getMultinetPeriod() {
        return hisFCastSettingDao.getMultinetPeriod();
    }

}
