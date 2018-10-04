package com.tuoming.mes.strategy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.KpiCalDao;
import com.tuoming.mes.strategy.dao.PerformanceCalDao;
import com.tuoming.mes.strategy.model.KpiCalModel;
import com.tuoming.mes.strategy.model.PerformanceCalSetting;
import com.tuoming.mes.strategy.service.KpiCalService;
import com.tuoming.mes.strategy.service.handle.KpiCalHandle;
import com.tuoming.mes.strategy.util.DateUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

@Service("kpiCalService")
public class KpiCalServiceImpl implements KpiCalService {
    @Autowired
    @Qualifier("kpiCalDao")
    private KpiCalDao kpiCalDao;
    @Autowired
    @Qualifier("performanceCalDao")
    private PerformanceCalDao performanceCalDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    public static void main(String[] aa) {
        Calendar begin = Calendar.getInstance();
        begin.setTime(new Date());
        begin.add(Calendar.DAY_OF_MONTH, -30);
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.setTime(new Date());
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        List<Date> timeList = DateUtil.getMinInterval(begin, end, Constant.PM_COLLECT_LD);

        for (Date d : timeList) {
            System.out.println(DateUtil.format(d));
        }
    }

    public void calKpi(String groupName, String time) {
        businessLogDao.insertLog(9, "计算KPI开始", 0);
        List<KpiCalModel> setList = kpiCalDao.querySetList(groupName);
        Map<String, List<KpiCalModel>> setMap = new HashMap<String, List<KpiCalModel>>();
        for (KpiCalModel set : setList) {
            set.setStarttime(time);
            if (!setMap.containsKey(set.getResTable())) {
                setMap.put(set.getResTable(), new ArrayList<KpiCalModel>());
            }
            setMap.get(set.getResTable()).add(set);
        }
        Map<String, KpiCalModel> fileMap = new HashMap<String, KpiCalModel>();
        for (Entry<String, List<KpiCalModel>> entry : setMap.entrySet()) {
            boolean delFalg = entry.getValue().get(0).isDeleteFlag();
            if (delFalg) {
                //将原始数据添加到历史数据表
                kpiCalDao.insertHisData(entry.getKey());
                //删除原始数据
                kpiCalDao.delHisData(entry.getKey());
            }
            for (KpiCalModel model : entry.getValue()) {
                KpiCalHandle handle = AppContext.getBean(model.getCalHandle());
                //不同厂商、不同制式关键性能指标处理器
                String fileName = handle.handle(model);
                fileMap.put(fileName, model);
            }
        }
        //将解析的文件入库
        for (Entry<String, KpiCalModel> entry : fileMap.entrySet()) {
            try {
                DataAdapterPool.getDataAdapterPool(entry.getValue().getDbName()).getDataAdapter()
                        .loadfile(entry.getKey(), entry.getValue().getResTable());
            } catch (Exception e) {
                businessLogDao.insertLog(9, "文件解析入库出现异常", 1);
                e.printStackTrace();
            }
        }
        businessLogDao.insertLog(9, "计算KPI结束", 0);
    }

    public void performanceCal(Map<String, String> context) {
        String timeParticle = String.valueOf(context.get(Constant.TIMEPARTICLE));
        String groupName = String.valueOf(context.get(Constant.KEY_GROUP_NAME));
        Date collectTime = DateUtil.tranStrToDate(context.get(Constant.CURRENT_COLLECTTIME));
        List<PerformanceCalSetting> perflist = performanceCalDao
                .queryPerfmanceSetting(groupName);
        if (Constant.PER_LD_DAY.equalsIgnoreCase(timeParticle)) {// 判断是天粒度还是小时粒度
            String dayTime = this.genMinuteOfDay(collectTime);
            for (PerformanceCalSetting perf : perflist) {
                String table = perf.getResTable() + Constant.RES_SUFFIX_DAY;
                performanceCalDao.delOldResPerfTable(table);
                performanceCalDao.createResPerfTable(perf.getQuerySql(), table,
                        dayTime);
            }
        } else if (Constant.PER_LD_HOUR.equalsIgnoreCase(timeParticle)) {
            String hourTime = this.genMinuteOfHour(collectTime);
            for (PerformanceCalSetting perf : perflist) {
                String table = perf.getResTable() + Constant.RES_SUFFIX_HOUR;
                performanceCalDao.delOldResPerfTable(table);
                performanceCalDao.createResPerfTable(perf.getQuerySql(), table,
                        hourTime);
            }
        }
    }

    /**
     * 获取上一小时15分钟时间间隔的时刻
     *
     * @param collectTime
     * @return
     */
    private String genMinuteOfHour(Date collectTime) {
        Calendar begin = Calendar.getInstance();
        begin.setTime(collectTime);
        begin.add(Calendar.HOUR_OF_DAY, -1);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.setTime(collectTime);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        List<Date> dateList = DateUtil.getMinInterval(begin, end, Constant.PM_COLLECT_LD);
        return FormatUtil.tranferArrayToStr(dateList);
    }

    /**
     * 获取上一天15分钟时间间隔的时刻
     *
     * @param collectTime
     * @return
     */
    private String genMinuteOfDay(Date collectTime) {
        Calendar begin = Calendar.getInstance();
        begin.setTime(collectTime);
        begin.add(Calendar.DAY_OF_MONTH, -1);
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.setTime(collectTime);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        List<Date> timeList = DateUtil.getMinInterval(begin, end, Constant.PM_COLLECT_LD);
        return FormatUtil.tranferArrayToStr(timeList);
    }

}
