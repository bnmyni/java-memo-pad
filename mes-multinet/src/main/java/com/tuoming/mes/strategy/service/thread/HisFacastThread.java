package com.tuoming.mes.strategy.service.thread;

import org.apache.log4j.Logger;

import java.io.PrintStream;
import java.util.Date;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.model.HisDataFCastSetting;
import com.tuoming.mes.strategy.service.handle.HisDataFCastHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * 历史数据预测线程
 *
 * @author Administrator
 */
public class HisFacastThread implements Runnable {
    private final static Logger logger = LogFacade.getLog4j(HisFacastThread.class);
    private Date date;
    private HisDataFCastSetting setting;

    public HisFacastThread(Date date, HisDataFCastSetting setting) {
        this.setting = setting;
        this.date = date;
    }

    @Override
    public void run() {
        String filePath = CsvUtil.mkParentDir(Constant.PREFIX_LSB);
        String targetFile = filePath + Constant.PREFIX_LSB + setting.getSourceTable() + "-" + setting.getDbName() + "-"
                + DateUtil.format(date, "yyyy_MM_dd_HH_mm") + CsvUtil.CSV_TYPE;
        HisDataFCastHandle handle = AppContext.getBean("hisDataFCastHandle");
        PrintStream ps = null;
        try {
            ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
            logger.info(String.format("预测 %s 表 ，%s 时段， 历史数据预满足3个月的数据", setting.getSourceTable(), DateUtil.format(date)));
            handle.handleThrMon(setting, ps, date);
            logger.info(String.format("预测 %s 表 ，%s 时段， 历史数据预满足2个月的数据", setting.getSourceTable(), DateUtil.format(date)));
            handle.handleTwoMon(setting, ps, date);
            logger.info(String.format("预测 %s 表 ，%s 时段， 历史数据预满足1个月的数据", setting.getSourceTable(), DateUtil.format(date)));
            handle.handleOneMon(setting, ps, date);
            logger.info(String.format("预测 %s 表 ，%s 时段， 历史数据预不满足1个月的数据", setting.getSourceTable(), DateUtil.format(date)));
            handle.handleNoMon(setting, ps, date);
            logger.info(String.format("预测 %s 表 ，%s 时段， 结束", setting.getSourceTable(), DateUtil.format(date)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

}
