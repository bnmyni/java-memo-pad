package com.tuoming.mes.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import al.mid3.neusoft.DataPrediction;

import com.tuoming.mes.strategy.service.impl.RBigDataForecastServiceImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.services.serve.FtpLogCommandService;
import com.tuoming.mes.services.serve.SEBizService;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.EricConfDao;
import com.tuoming.mes.strategy.model.BscNameConf;
import com.tuoming.mes.strategy.service.BeforeAfterService;
import com.tuoming.mes.strategy.service.CheckServerAccessService;
import com.tuoming.mes.strategy.service.EnergyCellRefreshService;
import com.tuoming.mes.strategy.service.HisDataFCastService;
import com.tuoming.mes.strategy.service.KpiCalService;
import com.tuoming.mes.strategy.service.MroCollectService;
import com.tuoming.mes.strategy.service.OverDegreeCalService;
import com.tuoming.mes.strategy.service.SavePowerMontiorService;
import com.tuoming.mes.strategy.service.SleepAreaSelectService;
import com.tuoming.mes.strategy.service.TdOffSleepAreaSelectService;
import com.tuoming.mes.strategy.service.impl.BigDataForecastServiceImpl;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * 策略模块服务接口定义实现
 * 
 * @author Administrator
 *
 */
@Component("SEBizService")
public class SEBizServiceImpl implements SEBizService {
	private static final Logger logger = LogFacade
			.getLog4j(SEBizServiceImpl.class);

	public static HashMap<String, String> ericBscMap = new HashMap<String, String>();
	DataPrediction	rBigDataPre = new DataPrediction();


	@Override
	public void alysAll(Map context) {
		boolean sleepFlag = Boolean.parseBoolean((String) context
				.get(Constant.MULTINET_SLEEP_FLAG));
		if (sleepFlag) {
			SleepAreaSelectService selService = AppContext
					.getBean("sleepAreaSelectService");
			logger.info("休眠小区筛选开始......");
			selService.sleepSelect(context);
			logger.info("休眠小区筛选结束......");
			logger.info("休眠小区冲突处理开始......");
			selService.conflictDeal(context);
			logger.info("休眠小区冲突处理结束......");
			logger.info("休眠小区指令生成开始......");
			selService.executeSleep(context);
			// 多补一休眠指令生成
			selService.executeManySleep();
			logger.info("休眠小区指令生成结束......");
		} else {
			SavePowerMontiorService savePowerMontiorService = AppContext
					.getBean("savePowerMontiorService");
			logger.info("休眠小区唤醒监控流程开始......");
			savePowerMontiorService.mainMontior(context);
			logger.info("休眠小区唤醒监控流程结束......");
		}
	}

	/**
	 * 该实现方法提供给外部接口调用，用于根据历史数据预测第二天的数据
	 */
	public void hisDataFcast(Map context) {
		logger.info("历史数据预测开始......");
		String groupName = String.valueOf(context.get(Constant.KEY_GROUP_NAME));
		HisDataFCastService hisFcastService = AppContext
				.getBean("hisDataFCastService");
		hisFcastService.fCastNextData(groupName);
		logger.info("历史数据预测结束......");
	}

	/**
	 * 进行重叠覆盖度计算
	 */
	public void refreshCoverRate(String groupName) {
		logger.info("重叠覆盖度计算开始......");
		OverDegreeCalService overDegreeCalService = AppContext
				.getBean("overDegreeCalService");
		overDegreeCalService.calculate(groupName);
		logger.info("重叠覆盖度计算结束......");
	}

	/**
	 * 节能刷新，每15分钟执行一次
	 */
	public void refreshPredict(Map context) {
		logger.info("节能刷新开始......");
		EnergyCellRefreshService energy = AppContext
				.getBean("energyCellRefreshService");
		energy.refreshEnergyCell(context);
		logger.info("节能刷新结束......");
	}

	@Override
	public void executeSleepProcess(Map context) {
		logger.info("休眠命令下发开始......");
		SleepAreaSelectService selService = AppContext
				.getBean("sleepAreaSelectService");
		selService.dispatchSleepCommand();
		logger.info("休眠命令下发结束......");
	}

	@Override
	public void executeWakeupProcess(Map context) {
		logger.info("唤醒命令下发开始......");
		SavePowerMontiorService savePowerMontiorService = AppContext
				.getBean("savePowerMontiorService");
		savePowerMontiorService.executeNotify();
		logger.info("唤醒命令下发结束......");
	}

	@Override
	public void fcastNextData(Map context) {
		logger.info("下一时刻数据计算开始......");
		SleepAreaSelectService selService = AppContext
				.getBean("sleepAreaSelectService");
		selService.foreCastNextData(context);
		logger.info("下一时刻数据计算结束......");
	}

	@Override
	public void calKpi(String groupName) {
		logger.info(groupName + " kpi计算开始......");
		KpiCalService kpiCalService = AppContext.getBean("kpiCalService");
		kpiCalService.calKpi(groupName, null);
		logger.info(groupName + " kpi计算结束......");
	}

	//计算15minKPI指标
	@Override
	public void calMinuteKpi(Map<String, String> context) {
		KpiCalService kpiCalService = AppContext.getBean("kpiCalService");
		String startTime = context.get(Constant.KPI_CAL_MINUTE_TIME);
		kpiCalService.calKpi(context.get(Constant.KEY_GROUP_NAME), startTime);
	}

	@Override
	public void dataHandle(String groupName) {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		logger.info(groupName + ":数据处理开始");
		service.executeBeforeOrAfter(groupName);
		logger.info(groupName + ":数据处理结束");
	}

	/**
	 * 构建节能上下文，该上下文主要包含信息：各个场景基于方位角或mr 和 当前采集时间
	 * 
	 * @return
	 */
	public Map<String, String> buildContext() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);// 采集时间不需要秒信息
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE,
				(cal.get(Calendar.MINUTE) / Constant.PM_COLLECT_LD)
						* Constant.PM_COLLECT_LD);
		SleepAreaSelectService selService = AppContext
				.getBean("sleepAreaSelectService");
		/*
		 * context中busytype的值由表mes_zs_coverage中base_overlay_degree决定
		 * base_overlay_degree:1 busytype:true 0 false
		 */
		Map<String, String> context = selService.queryScene();
		context.put(Constant.CURRENT_COLLECTTIME,
				DateUtil.format(cal.getTime()));
		context.put(Constant.CURRENT_BATCH_KEY,
				String.valueOf(cal.getTimeInMillis()));
		// 获取任务开启时间
		cal.add(Calendar.MINUTE, Constant.PM_COLLECT_DELAY);
		context.put(Constant.CURRENT_TIME_DELAY, DateUtil.format(cal.getTime()));
		cal.add(Calendar.MINUTE, -Constant.PM_COLLECT_LD * 2
				- Constant.PM_COLLECT_DELAY);
		context.put(Constant.KPI_CAL_MINUTE_TIME,
				DateUtil.format(cal.getTime()));

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		context.put(Constant.CURRENT_BATCH_MR,
				String.valueOf(cal.getTimeInMillis()));
		HisDataFCastService fcastService = AppContext
				.getBean("hisDataFCastService");
		Map<String, String> multinetPeriod = fcastService.getMultinetPeriod();
		context.put(Constant.START_PERIOD,
				multinetPeriod.get(Constant.START_PERIOD));
		context.put(Constant.BEGIN_PERIOD,
				multinetPeriod.get(Constant.BEGIN_PERIOD));
		context.put(Constant.T2L, "true");

		return context;
	}

	@Override
	public void notifyAllSleep() {
		logger.info("所有休眠小区唤醒开始......");
		SavePowerMontiorService notifyService = AppContext
				.getBean("savePowerMontiorService");
		notifyService.notifyAllArea();
		logger.info("所有休眠小区唤醒结束......");
	}

	//以小时或天为粒度对PM数据进行统计
	public void performanceCal(Map context) {
		logger.info("以小时或天为粒度对PM数据进行统计开始......");
		KpiCalService kpiCalService = AppContext.getBean("kpiCalService");
		kpiCalService.performanceCal(context);
		logger.info("以小时或天为粒度对PM数据进行统计结束......");
	}

	@Override
	public void checkCmdServerState() {
		CheckServerAccessService service = AppContext
				.getBean("checkServerAccessService");
		service.checkCmdServerAccess();
	}

	@Override
	public void updateAlarmInfo() {
		EnergyCellRefreshService energy = AppContext
				.getBean("energyCellRefreshService");
		energy.updateAlarmInfo();
	}

	@Override
	public void cleanServerFileByDay(int days) {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		service.cleanServerFile(days);

	}

	@Override
	public void reCollectFailCommand(int times) {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		service.reCollectFailCommand(times);
	}

	@Override
	public boolean sfCollectMr(String type, int days) {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		return service.sfCollectMr(type, days);
	}

	@Override
	public void cleanMrFile() {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		service.cleanMrFile();
	}

	@Override
	public boolean sfCalOverDegree(String type) {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		return service.sfCalOverDegree(type);
	}

	@Override
	public void calOverDegreeDone(String type) {
		BeforeAfterService service = AppContext.getBean("beforeAfterService");
		service.calOverDegreeDone(type);
	}

	@Override
	public void improveMrData(String groupName) {
		EnergyCellRefreshService energy = AppContext
				.getBean("energyCellRefreshService");
		energy.improveMrData(groupName);

	}

	@Override
	public void l2lhwMRParser(String dir, String regex) {
		logger.info("l2lhwMRParser开始......" + regex);
		logger.info(dir + "......");
		MroCollectService mroCollect = AppContext.getBean("MroCollectService");
		mroCollect.exeLteHwLocalAnaly(dir, regex);
		logger.info("l2lhwMRParser结束......");

	}

	public void l2lhwMRParser(String dir, String regex, String rname) {
		logger.info("l2lhwMRParser开始......" + rname + "....." + regex);
		logger.info(dir + "......");
		MroCollectService mroCollect = AppContext.getBean("MroCollectService");
		mroCollect.exeLteHwLocalAnaly(dir, regex, rname);
		logger.info("l2lhwMRParser结束......" + rname + ".....");

	}

	@Override
	public void query(String groupName, long batchid) {
		logger.info("FtpLogCommandServiceImpl开始......PM");
		EricConfDao ericConfDao = (EricConfDao) AppContext
				.getBean("ericConfDao");
		
		List<BscNameConf> bscList = ericConfDao.queryEricBsc();
		ericBscMap.clear();
		for (BscNameConf bscNameConf : bscList) {
			ericBscMap.put(bscNameConf.getSrcName(), bscNameConf.getBsc());
		}
		
		FtpLogCommandService server = (FtpLogCommandService) AppContext
				.getBean("FtpLogCommandServiceImpl");
		server.queryAll(groupName, batchid);
		logger.info("FtpLogCommandServiceImpl结束......PM");
	}

	/******************** 3G退网 *****************************/

	public void tdNetworkOffCal(Map<String, String> context) {
		logger.info("3G智能退网根据覆盖度或覆盖度及性能指标筛选开始..............");
		TdOffSleepAreaSelectService selService = AppContext
				.getBean("tdOffSleepAreaSelectService");
		selService.tdNetworkOff(context);
		logger.info("3G智能退网根据覆盖度或覆盖度及性能指标筛选结束..............");

	}

	public void tdNetworkOffExe() {
		TdOffSleepAreaSelectService selService = AppContext
				.getBean("tdOffSleepAreaSelectService");
		// 判断是否计算完成,是：进行进一步筛选
		if (selService.calFinish()) {
			// 查询是否执行休眠指令，
			int exe_status = selService.queryExecuteStatus();
			if (exe_status == 1) {// 执行休眠
				logger.info("3G智能退网删除黑白名单及告警小区开始..............");
				selService.tdNetworkOffFilter();
				logger.info("3G智能退网删除黑白名单及告警小区结束..............");
				logger.info("3G智能退网休眠指令生成及执行开始..............");
				selService.tdOffExecuteSleep();
				logger.info("3G智能退网休眠指令生成及执行结束..............");
			} else if (exe_status == 2) {// 取消执行,执行唤醒所有
				logger.info("3G智能退网取消执行，唤醒所有3G退网小区开始..............");
				selService.tdOffExecuteNotify();
				logger.info("3G智能退网取消执行，唤醒所有3G退网小区结束..............");
			} else if (exe_status == 9) { // 说明已执行过休眠流程，进行监控流程
				logger.info("3G智能退网监控静态休眠小区开始..............");
				selService.tdOffMonitor();
				logger.info("3G智能退网监控静态休眠小区结束..............");
			}
		}
	}

	/**
	 * 数据预测
	 */
	@Override
	public void bigDataForecast(Map<String, String> context) {
		logger.info("大数据预测下一时刻数据开始..................");
		String groupName = context.get(Constant.BIGDATA_GROUP);
		BigDataForecastServiceImpl forecastService = AppContext
				.getBean("bigDataForecastService");
		forecastService.bigDataForcast(groupName);
		logger.info("大数据预测下一时刻数据结束..................");
	}
	
	/**
	 * R语言大数据预测
	 */
	@Override
	public void rBigDataModel(Map<String, String> context) {
		logger.info("R语言大数据建模开始..................");
		String groupName = context.get(Constant.BIGDATA_GROUP);
		RBigDataForecastServiceImpl forecastService = AppContext.getBean("rBigDataForecastService");
		forecastService.bigDataModel(groupName,rBigDataPre);
		logger.info("R语言大数据建模结束..................");
	}
	
	/**
	 * R语言大数据预测
	 */
	@Override
	public void rBigDataForecast(Map<String, String> context) {
		logger.info("R语言大数据预测下一时刻数据开始..................");
		String groupName = context.get(Constant.BIGDATA_GROUP);
		RBigDataForecastServiceImpl forecastService = AppContext.getBean("rBigDataForecastService");
		forecastService.bigDataForcast(groupName,rBigDataPre);
		logger.info("R语言大数据预测下一时刻数据结束..................");
	}

	@Override
	public void calKpiByTime(Map<String, String> context) {
		String groupName = Constant.BIGDATA_KPI_CAL_HIS;
		Date nowTime = DateUtil.tranStrToDate(context
				.get(Constant.CURRENT_COLLECTTIME));
		String startTime = DateUtil.getBeforeMinStr(nowTime, -15);
		logger.info(groupName + " kpi计算开始......");
		KpiCalService kpiCalService = AppContext.getBean("kpiCalService");
		kpiCalService.calKpi(groupName, startTime);
		logger.info(groupName + " kpi计算结束......");
	}

}