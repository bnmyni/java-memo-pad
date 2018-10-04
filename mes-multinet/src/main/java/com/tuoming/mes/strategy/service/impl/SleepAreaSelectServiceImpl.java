package com.tuoming.mes.strategy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.pyrlong.configuration.ConfigurationManager;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.execute.dao.AdjustCommandService;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.FcastNextDataDao;
import com.tuoming.mes.strategy.dao.SleepAreaSelDao;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.model.FcastNextIntervalSetting;
import com.tuoming.mes.strategy.model.SleepExeSetting;
import com.tuoming.mes.strategy.model.SleepSelectModel;
import com.tuoming.mes.strategy.service.SleepAreaSelectService;
import com.tuoming.mes.strategy.service.handle.SleepExeHandle;
import com.tuoming.mes.strategy.service.handle.SleepSelHandle;
import com.tuoming.mes.strategy.service.handle.himpl.G2gSleepSelHandle;
import com.tuoming.mes.strategy.service.thread.HisFacastNextThread;
import com.tuoming.mes.strategy.util.DateUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 休眠小区筛选业务实现类
 * 筛选每天0-6点的小区。
 *
 * @author Administrator
 */
@Service("sleepAreaSelectService")
public class SleepAreaSelectServiceImpl implements SleepAreaSelectService {
    private static Map<String, Map<String, Double>> dic = new HashMap<String, Map<String, Double>>();
    @Autowired
    @Qualifier("sleepAreaSelDao")
    private SleepAreaSelDao sleepAreaSelDao;
    @Autowired
    @Qualifier("sleepExeDao")
    private SleepExeDao sleepExeDao;
    @Autowired
    @Qualifier("fcastNextDataDao")
    private FcastNextDataDao fcastNextDataDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    public static Map<String, Double> getSleepNotifyDic() {
        Calendar cal = Calendar.getInstance();
        String dateBs = DateUtil.format(cal.getTime(), "yyyy-MM-dd");
        synchronized (dic) {
            if (dic.get(dateBs) == null) {
                dic.clear();
                SleepAreaSelDao dao = AppContext.getBean("sleepAreaSelDao");
                Map<String, Double> sleepNotifyDic = dao.querySleepNotifyDic();
                dic.put(dateBs, sleepNotifyDic);
            }
        }
        return dic.get(dateBs);
    }

    public static int getERLangBTCHN(double dest_hwl) {
        SleepAreaSelDao dao = AppContext.getBean("sleepAreaSelDao");
        int tchn = dao.queryERLangB(dest_hwl);
        return tchn;
    }

    /**
     * 休眠小区筛选之前，首先预测PM表下一时刻关键性指标
     */
    public void foreCastNextData(Map<String, String> context) {
        businessLogDao.insertLog(8, "预测开始", 0);
        Date collectTime = DateUtil.tranStrToDate((String) context.get(Constant.CURRENT_COLLECTTIME));
        List<FcastNextIntervalSetting> setList = fcastNextDataDao
                .queryForecastNextSet(context.get(Constant.KEY_GROUP_NAME));
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        for (FcastNextIntervalSetting fset : setList) {
            threadPoolExecutor.execute(new HisFacastNextThread(collectTime, fset));
        }
        threadPoolExecutor.shutdown();
        while (threadPoolExecutor.isShutdown() && threadPoolExecutor.getPoolSize() > 0) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                businessLogDao.insertLog(8, "预测线程出现异常", 1);
                e.printStackTrace();
            }
        }
        businessLogDao.insertLog(8, "预测完成", 0);
    }

/*	public void conflictDeal(Map<String, String> context) {
        boolean l2lAzimuth = Boolean.parseBoolean(context.get(Constant.L2L));
		boolean g2gAzimuth = Boolean.parseBoolean(context.get(Constant.G2G));
		boolean t2gAzimuth = Boolean.parseBoolean(context.get(Constant.T2G));
		sleepAreaSelDao.deleteConflictsForLte(l2lAzimuth);//删除lte小区可能存在的休眠小区不唯一冲突
		List<String> priorityList = sleepAreaSelDao.queryPriorities();
		//判断同一gsm补偿小区，是否能满足同时补偿gsm和td制式小区
		this.filterT2gOrG2gByGsm(priorityList.indexOf(Constant.TD), priorityList.indexOf(Constant.GSM), l2lAzimuth, g2gAzimuth,t2gAzimuth);
		
		sleepAreaSelDao.deleteMakeUpBySleepForG2g(g2gAzimuth);
		sleepAreaSelDao.deleteMakeUpBySleepForL2l(l2lAzimuth);
		
		String first = priorityList.get(0);//第一优先级
		String second = priorityList.get(1);// 第二优先级
		// TD是第一优先级的情况下，无论其他两致式那个是第二优先级方法都相同
		if (Constant.TD.equalsIgnoreCase(first)) {
			// 如果T2G优先级比较高，则先将其他表中的补偿小区存在于T2G中的小区剔除
			sleepAreaSelDao.delMakeUpConflictInL2TByT2G(t2gAzimuth);
			// G2G优先级高的情况下，则将比其优先级底的其他表中的包含该表的休眠小区的剔除
			sleepAreaSelDao.delSleepConflictInG2GByT2G(g2gAzimuth, t2gAzimuth);
			sleepAreaSelDao.delSleepConflictInL2TByL2L(l2lAzimuth);
		} else if (Constant.GSM.equalsIgnoreCase(first)) {
			sleepAreaSelDao.delMakeUpConflictInT2GByG2G(g2gAzimuth, t2gAzimuth);
			sleepAreaSelDao.delSleepConflictInL2TByL2L(l2lAzimuth);
			if (Constant.TD.equalsIgnoreCase(second)) {
				sleepAreaSelDao.delMakeUpConflictInL2TByT2G(t2gAzimuth);
			} else {
				sleepAreaSelDao
						.delSleepConflictInT2GByL2T(t2gAzimuth);
			}
		} else if (Constant.LTE.equalsIgnoreCase(first)) {
			sleepAreaSelDao.delSleepConflictInL2TByL2L(l2lAzimuth);
			sleepAreaSelDao.delSleepConflictInT2GByL2T(t2gAzimuth);
			if (Constant.GSM.equalsIgnoreCase(second)) {
				sleepAreaSelDao.delMakeUpConflictInT2GByG2G(g2gAzimuth, t2gAzimuth);
			} else {
				sleepAreaSelDao
						.delSleepConflictInG2GByT2G(g2gAzimuth, t2gAzimuth);
			}
		}*/
    /** T2L Neusoft end */

    /**
     * 指定时间的数据采集完成之后，开始进行15分钟之后的休眠小区筛选
     * 通过方位角或mr计算的节能小区，通过上一个段的小区指标，过滤休眠小区
     *
     * @throws Exception
     */
    public void sleepSelect(Map<String, String> context) {
        businessLogDao.insertLog(11, "小区筛选开始", 0);
        for (String busytype : Constant.BUSTYPEARR) {
            // 从数据库配置表mes_sleepsel_setting中获取计算节能小区时使用的方法方位角或mr
            boolean isAzimuth = Boolean.parseBoolean(context.get(busytype));
            String groupName = Constant.MR;
            if (isAzimuth) {
                groupName = Constant.AZIMUTH;
            }
            List<SleepSelectModel> setList = sleepAreaSelDao.querySleepAreaSelSet(groupName + "_" + busytype);

            List<Map<String, Object>> dicTdList = sleepAreaSelDao.queryTdDicList();//查询TD门限
            List<Map<String, Object>> dicGsmList = sleepAreaSelDao.queryGsmDicList();//查询GSM门限
            for (SleepSelectModel model : setList) {
                if (model.isDelFlag()) {
                    //删除原休眠小区表
                    sleepAreaSelDao.removeAllData(model.getResTable());
                }
                List<Map<String, Object>> dataList = sleepAreaSelDao.queryMetaData(model.getQuerySql());
                SleepSelHandle handle = AppContext.getBean(model.getServiceHandle());
                String file = handle.handle(dataList, dicGsmList, dicTdList, model.getExportCols());
                try {
                    DataAdapterPool.getDataAdapterPool(model.getDbName()).getDataAdapter().loadfile(file, model.getResTable());
                } catch (Exception e) {
                    businessLogDao.insertLog(11, "文件入库异常", 1);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 同一个补偿小区同时补偿多个小区，需将多个节能小区业务量累加到补偿小区
     */
    public void conflictDeal(Map<String, String> context) {
        businessLogDao.insertLog(12, "小区冲突处理开始", 0);
        boolean l2lAzimuth = Boolean.parseBoolean(context.get(Constant.L2L));
        boolean g2gAzimuth = Boolean.parseBoolean(context.get(Constant.G2G));
        boolean t2gAzimuth = Boolean.parseBoolean(context.get(Constant.T2G));
        /** T2L Neusoft begin */
        boolean t2lAzimuth = Boolean.parseBoolean(context.get(Constant.T2L));
        List<String> priorityList = sleepAreaSelDao.queryPriorities();
        //判断同一gsm补偿小区，是否能满足同时补偿gsm和td制式小区
        this.filterT2gOrG2gByGsm(priorityList.indexOf(Constant.TD), priorityList.indexOf(Constant.GSM), l2lAzimuth, g2gAzimuth, t2gAzimuth);
        //判断同一lte补偿小区，是否能满足同时补偿lte和td制式小区，并且满足l2l多补一
        this.filterT2LOrL2LByLte(priorityList.indexOf(Constant.TD), priorityList.indexOf(Constant.LTE), t2lAzimuth, l2lAzimuth);

        //删除G2G中同时作为休眠小区和补偿小区的情况
        sleepAreaSelDao.deleteMakeUpBySleepForG2g(g2gAzimuth);
        //删除L2L中同时作为休眠小区和补偿小区的情况
        sleepAreaSelDao.deleteMakeUpBySleepForL2l(l2lAzimuth);
        //删除T2T中同时作为休眠或补偿小区的情况
        sleepAreaSelDao.deleteMakeUpBySleepForT2T();

        String first = priorityList.get(0);//第一优先级
        String second = priorityList.get(1);// 第二优先级

        if (Constant.TD.equalsIgnoreCase(first)) {
            //GSM作为第二优先级
            if (Constant.GSM.equalsIgnoreCase(second)) {//节能场景处理顺序t2g,t2l,g2g,l2l
                // 从G2G休眠小区中去除已经在T2G中作为补偿小区的GSM小区
                sleepAreaSelDao.delSleepConflictInG2GByT2G(g2gAzimuth, t2gAzimuth);
                //从L2L休眠小区中去除已经在T2L中作为补偿小区的LTE小区
                sleepAreaSelDao.delSleepConflictInL2LByT2L(l2lAzimuth, t2lAzimuth);
            } else {//LTE作为第二优先级,节能场景处理顺序t2l,t2g,l2l,g2g
                //从L2L休眠小区中去除已经在T2L中作为补偿小区的LTE小区
                sleepAreaSelDao.delSleepConflictInL2LByT2L(l2lAzimuth, t2lAzimuth);
                //从G2G休眠小区中去除已经在T2G中作为补偿小区的GSM小区
                sleepAreaSelDao.delSleepConflictInG2GByT2G(g2gAzimuth, t2gAzimuth);
            }
        } else if (Constant.GSM.equalsIgnoreCase(first)) {
            if (Constant.TD.equalsIgnoreCase(second)) {//TD第二优先级,节能场景处理顺序g2g,t2g,t2l,l2l
                //从T2G补偿小区中去除G2G中作为休眠小区的的GSM小区
                sleepAreaSelDao.delMakeUpConflictInT2GByG2G(t2gAzimuth, g2gAzimuth);
                //从L2L休眠小区中去除T2L中作为补偿小区的的LTE小区
                sleepAreaSelDao.delSleepConflictInL2LByT2L(l2lAzimuth, t2lAzimuth);
            } else {//LTE第二优先级	,节能场景处理顺序g2g,l2l,t2g,t2l
                //从T2G补偿小区去除G2G中作为休眠小区的GSM小区
                sleepAreaSelDao.delMakeUpConflictInT2GByG2G(t2gAzimuth, g2gAzimuth);
                //从T2L补偿小区去除L2L中作为休眠小区的LTE小区
                sleepAreaSelDao.delMakeUpConflictInT2LByL2L(t2gAzimuth, t2lAzimuth);
            }
        } else if (Constant.LTE.equalsIgnoreCase(first)) {
            //从T2L补偿小区去除L2L中作为休眠小区的LTE小区
            sleepAreaSelDao.delMakeUpConflictInT2LByL2L(t2lAzimuth, l2lAzimuth);
            if (Constant.GSM.equalsIgnoreCase(second)) {//节能场景处理顺序l2l,g2g,t2l,t2g
                //从T2G补偿小区去除G2G中作为休眠小区的GSM小区
                sleepAreaSelDao.delMakeUpConflictInT2GByG2G(t2gAzimuth, g2gAzimuth);
            } else {//节能场景处理顺序l2l,t2l,t2g,g2g
                //从G2G休眠小区去除T2G中作为补偿小区的GSM小区
                sleepAreaSelDao.delSleepConflictInG2GByT2G(g2gAzimuth, t2gAzimuth);
            }
        }

        //T2T多补一的补偿小区去除T2G、T2L中作为休眠的TD小区
        sleepAreaSelDao.delMakeUpConflictInT2TByT2GT2L(t2gAzimuth, t2lAzimuth);

        businessLogDao.insertLog(12, "小区冲突处理完成", 0);
    }

    /**
     * 过滤掉Gsm小区的每线话务量不同时满足T2g和G2g的小区
     *
     * @param tdPriority
     * @param gsmPriority
     * @param isAzimuth
     * @param sleepDate
     */
    private void filterT2gOrG2gByGsm(int tdPriority, int gsmPriority, boolean l2lAzimuth, boolean g2gAzimuth, boolean t2gAzimuth) {
        Map<String, List<Map<String, Object>>> dataMap = sleepAreaSelDao.queryMakeUpGsmAndTd(g2gAzimuth, t2gAzimuth, tdPriority, gsmPriority);
        List<Map<String, Object>> gsmDicList = sleepAreaSelDao.queryGsmDicList();
        Map<String, Map<String, Double>> count = new HashMap<String, Map<String, Double>>();//记录补偿小区及其相对应的门限阀值
        Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
        for (Entry<String, List<Map<String, Object>>> entry : dataMap.entrySet()) {
            for (Map<String, Object> data : entry.getValue()) {
                if (!count.containsKey(entry.getKey())) {//假如该补偿小区没有被验证过，则获取该小区无线资源利用率和每线话务量的阀值
                    Map<String, Double> dic = G2gSleepSelHandle.calGsmDic(gsmDicList, FormatUtil.tranferCalValue(data.get("dest_tchxdcspz")));
                    dic.put("dest_mxhwl", FormatUtil.tranferCalValue(data.get("dest_mxhwl")));
                    dic.put("dest_pdchczl", FormatUtil.tranferCalValue(data.get("dest_pdchczl")));
                    count.put(entry.getKey(), dic);
                }
                String bus_type = String.valueOf(data.get("bus_type"));
                double mxhwl_lj = 0;
                double pdchczl_lj = 0;
                if (Constant.G2G.equalsIgnoreCase(bus_type)) {
                    mxhwl_lj = FormatUtil.tranferCalValue(data.get("src_mxhwl")) + count.get(entry.getKey()).get("dest_mxhwl");
                    pdchczl_lj = FormatUtil.tranferCalValue(data.get("src_pdchczl")) + count.get(entry.getKey()).get("dest_mxhwl");
                } else {
                    mxhwl_lj = (FormatUtil.tranferCalValue(data.get("src_yyyw")) / FormatUtil.tranferCalValue(data.get("dest_tchxdcspz")))
                            + count.get(entry.getKey()).get("dest_mxhwl");
                    pdchczl_lj = (FormatUtil.tranferCalValue(data.get("src_sjll")) * 1024 * 8 / FormatUtil.tranferCalValue(data.get("dest_pdchzygs")) / 900)
                            + count.get(entry.getKey()).get("dest_pdchczl");
                }
                count.get(entry.getKey()).put("dest_mxhwl", mxhwl_lj);
                count.get(entry.getKey()).put("dest_pdchczl", pdchczl_lj);
                if (mxhwl_lj >= count.get(entry.getKey()).get("m2") || pdchczl_lj >= thresholdDic.get("T2G_DPDCHCZL_MAKEUP")) {
                    if (Constant.G2G.equalsIgnoreCase(bus_type)) {
                        sleepAreaSelDao.deleteG2gByList(data, g2gAzimuth);
                    } else {
                        sleepAreaSelDao.deleteT2gByList(data, t2gAzimuth);
                    }
                }
            }
        }

    }

    /**
     * @param tdPriority
     * @param ltePriority
     * @param l2lAzimuth
     * @param t2gAzimuth
     */
    private void filterT2LOrL2LByLte(int tdPriority, int ltePriority, boolean t2lAzimuth, boolean l2lAzimuth) {
        //取得相同邻区的l2l、t2l、l2l多补一的数据
        Map<String, List<Map<String, Object>>> dataMap = sleepAreaSelDao.queryMakeUpLteAndTd(t2lAzimuth, l2lAzimuth, tdPriority, ltePriority);
        Map<String, Map<String, Double>> count = new HashMap<String, Map<String, Double>>();//记录不同补偿小区的kpi累计值
        Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();//门限阀值
        //按相同邻区遍历
        for (Entry<String, List<Map<String, Object>>> entry : dataMap.entrySet()) {
            //遍历相同邻区下的数据，对kpi值做累加处理
            for (Map<String, Object> data : entry.getValue()) {
                if (!count.containsKey(entry.getKey())) {//假如该补偿小区没有被验证过，则获取该小区上行数据流量，下行数据流量，最大用户数
                    Map<String, Double> dic = new HashMap<String, Double>();
                    dic.put("dest_sxsjll", FormatUtil.tranferCalValue(data.get("dest_sxsjll")));
                    dic.put("dest_xxsjll", FormatUtil.tranferCalValue(data.get("dest_xxsjll")));
                    dic.put("dest_zdyhs", FormatUtil.tranferCalValue(data.get("dest_zdyhs")));
                    count.put(entry.getKey(), dic);
                }
                String bus_type = String.valueOf(data.get("bus_type"));
                double sxsjll_lj = 0;//上行数据流量累加值
                double xxsjll_lj = 0;//下行数据流量累加值
                double dest_zdyhs = FormatUtil.tranferCalValue(data.get("dest_zdyhs"));//最大用户数
                if (Constant.T2L.equalsIgnoreCase(bus_type)) {
                    sxsjll_lj = FormatUtil.tranferCalValue(data.get("src_sjll")) + count.get(entry.getKey()).get("dest_sxsjll");
                    xxsjll_lj = FormatUtil.tranferCalValue(data.get("src_sjll")) + count.get(entry.getKey()).get("dest_xxsjll");
                } else {
                    sxsjll_lj = FormatUtil.tranferCalValue(data.get("src_sxsjll")) + count.get(entry.getKey()).get("dest_sxsjll");
                    xxsjll_lj = FormatUtil.tranferCalValue(data.get("src_xxsjll")) + count.get(entry.getKey()).get("dest_xxsjll");
                }
                count.get(entry.getKey()).put("dest_sxsjll", sxsjll_lj);
                count.get(entry.getKey()).put("dest_xxsjll", xxsjll_lj);
                count.get(entry.getKey()).put("dest_zdyhs", dest_zdyhs);
                //判断指标：上行流量，下行流量，最大用户数是否满足门限
                if (sxsjll_lj >= thresholdDic.get("T2L_SXSJLL_MAKEUP") || xxsjll_lj >= thresholdDic.get("T2L_XXSJLL_MAKEUP")
                        || dest_zdyhs >= thresholdDic.get("T2L_ZDYHS_MAKEUP")) {
                    if (Constant.T2L.equalsIgnoreCase(bus_type)) {
                        //该不满足累计kip判断的数据为t2l的场合，对t2l数据进行删除
                        sleepAreaSelDao.deleteT2lByList(data, t2lAzimuth);
                    } else if (Constant.L2L.equalsIgnoreCase(bus_type)) {
                        //该不满足累计kip判断的数据为l2l的场合，对l2l数据进行删除
                        sleepAreaSelDao.deleteL2lByList(data, l2lAzimuth);
                    } else {
                        //该不满足累计kip判断的数据为l2l多补一的场合，对l2l多补一数据进行删除
                        sleepAreaSelDao.deleteL2lManyByList(data);

                    }
                }
            }
        }

    }

    public void executeSleep(Map<String, String> context) {
        businessLogDao.insertLog(13, "一补一指令生成开始", 0);
        //对已执行指令进行处理，先将指令移至历史表，再删除原表中纪录
        sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.SLEEP);
        sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.SLEEP);
        //查询每种制式下，允许休眠最大个数
        List<Map<String, Object>> dicList = sleepExeDao.querySleepDic();
        //查询休眠小区执行流程配置
        List<SleepExeSetting> setList = sleepExeDao.querySleepExeSetList(context.get(Constant.KEY_GROUP_NAME));
        for (SleepExeSetting set : setList) {
            List<Map<String, Object>> dataList = null;
            List<Map<String, Object>> t2lDataList = null;
            /** T2L Neusoft begin */
            if (Constant.TD.equalsIgnoreCase(set.getZs())) {//td有t2g和t2l两种场景，将数据合并处理
                String[] sqlArr = set.getQuerySql().split("#");
                dataList = sleepExeDao.querySleepAreaBySql(sqlArr[0], Boolean.parseBoolean(context.get(Constant.T2G)));
                t2lDataList = sleepExeDao.querySleepAreaBySql(sqlArr[1], Boolean.parseBoolean(context.get(Constant.T2L)));
                dataList.addAll(t2lDataList);
            } else {
                dataList = sleepExeDao.querySleepAreaBySql(set.getQuerySql(), Boolean.parseBoolean(context.get(set.getGroup())));
            }
            SleepExeHandle handle = AppContext.getBean(set.getServiceHandle());
            Map<String, Integer> unitCount = sleepExeDao.queryCellAmount(set.getZs());
            handle.handle(dataList, this.getSleepAmountByZs(dicList, set), set, unitCount);

/*			if(Constant.LTE.equalsIgnoreCase(set.getZs())) {//lte有l2t和l2l两种场景
                String[] sqlArr = set.getQuerySql().split("#");
				dataList = sleepExeDao.querySleepAreaBySql(sqlArr[0], Boolean.parseBoolean(context.get(Constant.L2L)));
				l2tDataList = sleepExeDao.querySleepAreaBySql(sqlArr[1], Boolean.parseBoolean(context.get(Constant.L2T)));
				dataList.addAll(l2tDataList);
			}else {
				dataList = sleepExeDao.querySleepAreaBySql(set.getQuerySql(), Boolean.parseBoolean(context.get(set.getGroup())));
			}
			SleepExeHandle handle = AppContext.getBean(set.getServiceHandle());
			Map<String, Integer> unitCount = sleepExeDao.queryCellAmount(set.getZs());
			handle.handle(dataList, this.getSleepAmountByZs(dicList, set), set, unitCount);*/
            /** T2L Neusoft end */
        }
        businessLogDao.insertLog(13, "一补一指令生成结束", 0);
    }

    /**
     * 多补一休眠指令生成
     */
    public void executeManySleep() {
        businessLogDao.insertLog(13, "多补一指令生成开始", 0);
        //对已执行指令进行处理，先将指令移至历史表，再删除原表中纪录
        sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.SLEEP_MANY);
        sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.SLEEP_MANY);
        //查询每种制式下，允许休眠最大个数
        List<Map<String, Object>> dicList = sleepExeDao.querySleepDic();
        //查询休眠小区执行流程配置
        List<SleepExeSetting> setList = sleepExeDao.querySleepExeSetList("many");
        for (SleepExeSetting set : setList) {
            List<Map<String, Object>> dataList = null;
            //查询休眠列表
            dataList = sleepExeDao.querySleepAreaBySql(set.getQuerySql(), false);
            SleepExeHandle handle = AppContext.getBean(set.getServiceHandle());
            //该制式已休眠的小区数量
            Map<String, Integer> unitCount = sleepExeDao.queryCellAmount(set.getZs());
            handle.handle(dataList, this.getSleepAmountByZs(dicList, set), set, unitCount);
        }
        businessLogDao.insertLog(13, "一补一指令生成结束", 0);
    }

    /**
     * 获取该制式下的休眠小区格式
     *
     * @param dicList
     * @param set
     * @return
     */
    private int getSleepAmountByZs(List<Map<String, Object>> dicList, SleepExeSetting set) {
        for (Map<String, Object> dic : dicList) {
            if (set.getZs().equalsIgnoreCase(String.valueOf(dic.get("zs")))) {
                return (Integer) dic.get("top");
            }
        }
        return 0;
    }

    @Override
    public void dispatchSleepCommand() {
        businessLogDao.insertLog(14, "休眠指令下发开始", 0);
        AdjustCommandService adjust = AppContext.getBean("AdjustCommandService");
        adjust.sleepOrNotify(Constant.APP_MULTINET, Constant.SLEEP);
        //多补一小区休眠指令下发
        adjust.sleepOrNotify(Constant.APP_MULTINET, Constant.SLEEP_MANY);
//		sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.SLEEP);
//		sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.SLEEP);
        businessLogDao.insertLog(14, "休眠指令下发结束", 0);
    }

    /**
     * 获取休眠与补偿制式对应是否用方位角计算
     *
     * @return
     */
    @Override
    public Map<String, String> queryScene() {
        Map<String, String> context = new HashMap<>();
        List<Map<String, Integer>> list = sleepAreaSelDao.queryScene();
        for (Map<String, Integer> ctx : list) {
            context.put(String.valueOf(ctx.get("busytype")), ctx.get("base_overlay_degree") == 0 ? "false" : "true");
        }
        return context;
    }

}
