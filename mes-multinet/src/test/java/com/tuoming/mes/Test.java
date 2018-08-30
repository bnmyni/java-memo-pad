package com.tuoming.mes;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.services.serve.FtpLogCommandService;
import com.tuoming.mes.services.serve.SEBizService;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.service.EnergyCellRefreshService;
import com.tuoming.mes.strategy.service.HisDataFCastService;
import com.tuoming.mes.strategy.service.KpiCalService;
import com.tuoming.mes.strategy.service.MroCollectService;
import com.tuoming.mes.strategy.service.OverDegreeCalService;
import com.tuoming.mes.strategy.service.SavePowerMontiorService;
import com.tuoming.mes.strategy.service.SleepAreaSelectService;
import com.tuoming.mes.strategy.service.TdOffSleepAreaSelectService;
import com.tuoming.mes.strategy.service.impl.BigDataForecastServiceImpl;
import com.tuoming.mes.strategy.service.impl.MroCollectImpl;
import com.tuoming.mes.strategy.util.DateUtil;

public class Test {
	@Before
	public void setup() throws Exception {
		LogFacade.initLogFacade("../conf/log4j.properties");
		ConfigurationManager.LIFE_CYCLE = ConfigurationManager.LIFECYCLE.WITHDB;
		ConfigurationManager.getDefaultConfig().openConfiguration("conf/hamster.xml");
	}
	public static void main(String[] args) {
		LogFacade.initLogFacade("../conf/log4j.properties");
		ConfigurationManager.LIFE_CYCLE = ConfigurationManager.LIFECYCLE.WITHDB;
		ConfigurationManager.getDefaultConfig().openConfiguration("conf/hamster.xml");
		ConfigurationManager.getDefaultConfig().set("jdbc.password", "tmcluyan");
		ConfigurationManager.getDefaultConfig().set("jdbc.url", "jdbc:mysql://127.0.0.1:3306/multinet");
		ConfigurationManager.getDefaultConfig().set("jdbc.username", "root");
		
//		//覆盖度
//		OverDegreeCalService overDegreeCalService = AppContext.getBean("overDegreeCalService");
//		overDegreeCalService.calculate("AZIMUTH");
		
		//下一时刻预测
		
		//节能小区刷新
//		EnergyCellRefreshService energy = AppContext.getBean("energyCellRefreshService");
//		Map<String, String> context = new HashMap<String, String>();
//		context.put("t2l", "true");
//		energy.refreshEnergyCell(context);
		
		//小区筛选
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		Map<String, String> context = new HashMap<String, String>();
//		context.put("t2l", "true");
//		selService.sleepSelect(context);
		
		//冲突处理
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		Map<String, String> context = new HashMap<String, String>();
//		context.put("g2g", "true");
//		context.put("t2g", "true");
//		context.put("t2l", "true");
//		context.put("l2l", "true");
//		selService.conflictDeal(context);
		
		//指令生成
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		Map<String, String> context = new HashMap<String, String>();
//		context.put("g2g", "true");
//		context.put("t2g", "true");
//		context.put("t2l", "true");
//		context.put("l2l", "true");
//		selService.executeSleep(context);
//		}
		
		
		//指令下发
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		selService.dispatchSleepCommand();
//		SavePowerMontiorService savePowerMontiorService = AppContext.getBean("savePowerMontiorService");
//		savePowerMontiorService.executeNotify();
		//监控
//		SavePowerMontiorService savePowerMontiorService = AppContext.getBean("savePowerMontiorService");
//		Map<String, String> context = new HashMap<String, String>();
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.SECOND, 0);//采集时间不需要秒信息
//		cal.set(Calendar.MILLISECOND, 0);
//		cal.set(Calendar.MINUTE, (cal.get(Calendar.MINUTE)/Constant.PM_COLLECT_LD)*Constant.PM_COLLECT_LD);
//		context.put(Constant.CURRENT_COLLECTTIME, DateUtil.format(cal.getTime()));
//		savePowerMontiorService.mainMontior(context);
		
		//所有休眠小区唤醒
//		SavePowerMontiorService notifyService = AppContext.getBean("savePowerMontiorService");
//		notifyService.notifyAllArea();
		
		//3G退网
//		TdOffSleepAreaSelectService selService = AppContext.getBean("tdOffSleepAreaSelectService");
//		Map<String, String> context = new HashMap<String, String>();
//		context.put("cal_type","1");
//		selService.tdNetworkOff(context);
		
//		if(selService.calFinish()){			
//			//查询是否执行休眠指令，			
//			int exe_status = selService.queryExecuteStatus();
//			if(exe_status==1){//执行
//				System.out.println("3G智能退网删除黑白名单及告警小区开始..............");
//				selService.tdNetworkOffFilter();
//				System.out.println("3G智能退网删除黑白名单及告警小区结束..............");
//				System.out.println("3G智能退网休眠指令执行开始..............");
//				selService.tdOffExecuteSleep();
//				System.out.println("3G智能退网休眠指令执行结束..............");
//				System.out.println("3G智能退网监控开始..............");
//				selService.tdOffMonitor();
//				System.out.println("3G智能退网监控结束..............");
//			}else if(exe_status==2){//取消执行,执行唤醒
//				selService.tdOffExecuteNotify();				
//			}
//		}		
		
		
//		//数据预测
//		BigDataForecastServiceImpl forecastService = AppContext.getBean("bigDataForecastService");
//		forecastService.bigDataForcast(null);
		
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.SECOND, 0);//采集时间不需要秒信息
//		cal.set(Calendar.MILLISECOND, 0);
//		cal.set(Calendar.MINUTE, (cal.get(Calendar.MINUTE)/Constant.PM_COLLECT_LD)*Constant.PM_COLLECT_LD);
//		String startTime = DateUtil.format(cal.getTime());
//		KpiCalService kpiCalService = AppContext.getBean("kpiCalService");
//		kpiCalService.calKpi(Constant.BIGDATA_KPI_CAL_HIS, startTime);
		
		//原历史数据预测
//		HisDataFCastService hisFcastService = AppContext.getBean("hisDataFCastService");
//		hisFcastService.fCastNextData(null);
		
		/****************多补一测试*******************/
//		//节能小区刷新
//		EnergyCellRefreshService energy = AppContext.getBean("energyCellRefreshService");
//		Map<String, String> context = new HashMap<String, String>();
//		energy.refreshEnergyCell(context);
		
//		//小区筛选
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		Map<String, String> context = new HashMap<String, String>();
//		selService.sleepSelect(context);
		
//		//冲突处理?
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		Map<String, String> context = new HashMap<String, String>();
////		context.put("g2g", "true");
////		context.put("t2g", "true");
//		context.put("t2l", "true");
//		selService.conflictDeal(context);
		
//		//指令生成
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		selService.executeManySleep();
		
//		//指令下发
//		SleepAreaSelectService selService = AppContext.getBean("sleepAreaSelectService");
//		selService.dispatchSleepCommand();
//		SavePowerMontiorService savePowerMontiorService = AppContext.getBean("savePowerMontiorService");
//		savePowerMontiorService.executeNotify();
		
//		//监控
//		SavePowerMontiorService savePowerMontiorService = AppContext.getBean("savePowerMontiorService");
//		Map<String, String> context = new HashMap<String, String>();
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.SECOND, 0);//采集时间不需要秒信息
//		cal.set(Calendar.MILLISECOND, 0);
//		cal.set(Calendar.MINUTE, (cal.get(Calendar.MINUTE)/Constant.PM_COLLECT_LD)*Constant.PM_COLLECT_LD);
//		context.put(Constant.CURRENT_COLLECTTIME, DateUtil.format(cal.getTime()));
//		savePowerMontiorService.mainMontior(context);
		
		
		//解析MRO文件
//		FtpLogCommandService ftpLogCommandService = AppContext.getBean("FtpLogCommandService");
//		Map<String, String> context = new HashMap<String, String>();
//		Calendar cal = Calendar.getInstance();
//		context.put(Constant.CURRENT_BATCH_MR, String.valueOf(cal.getTimeInMillis()));
//		ftpLogCommandService.queryAll("MRO_TD_HW",Long.parseLong(context.get(Constant.CURRENT_BATCH_MR)));
		
		//解析PM文件
		FtpLogCommandService ftpLogCommandService = AppContext.getBean("FtpLogCommandService");
		ftpLogCommandService.queryAll("PM",Constant.CURRENT_BATCH);
//		//解析CM文件
//		ftpLogCommandService.queryAll(Constant.CM,Constant.CURRENT_BATCH);
//		
/*//		String command = "asda#celLID#d#CELLID#sf";
//		Matcher m = Pattern.compile("#cellid#", Pattern.DOTALL|Pattern.CASE_INSENSITIVE).matcher(command);
//		System.out.println(m.replaceAll("11"));
//		System.out.println("==="+Integer.MIN_VALUE);
//		System.out.println("==="+Integer.MAX_VALUE);
		File f = new File("D:/gather_traffica_data.py");
		long size = f.length(); //   大小   bytes
		long modify = f.lastModified(); //   修改时间
		long t1=System.currentTimeMillis();
		long t2 =System.currentTimeMillis()-15*1000;
		System.out.println(t1+"---"+java.util.Calendar.getInstance().getTimeInMillis()); 
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(1461747600000l));
		String sd1 = sdf.format(new Date((t2)));  
		BigDecimal num =new BigDecimal("359071370111130213101");
		System.out.println(sd+"=="+num);  
//		MroCollectService mro=new MroCollectImpl();
//		mro.exeLteHwLocalAnaly("D:/BJ_hannan/workspace_hn/ASK/LTE MR", "TD-LTE_MRO_HUAWEI_010184039165_491660_20151109201500");
*/	}
	
	@org.junit.Test
	public void aaaa() throws ParseException{
		MroCollectService mro=new MroCollectImpl();
		mro.exeLteHwLocalAnaly("D:/BJ_hannan/workspace_hn/ASK/LTE MR", "TD-LTE_MRO_HUAWEI_010184039165_491660_20151109201500");
//		k.performanceCal(null, sdf.parse("2013-12-12 14:45:00"), "day");
//		k.calKpi(null);
//		Date d = sdf.parse("2013-12-11 14:45:00");
//		String str = sdf.format(getBeforeDay(d));
//		System.out.println(str);
	}
	
	/**
	 * 获取日期的前一天
	 * 
	 * @return
	 */
	public static Date getBeforeDay(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取时间的前一小时
	 * 
	 * @return
	 */
	public static Date getBeforeHour(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		return calendar.getTime();
	}

}
