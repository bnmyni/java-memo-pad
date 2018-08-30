package com.tuoming.mes.strategy.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.execute.dao.AdjustCommandService;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.NotifyDao;
import com.tuoming.mes.strategy.dao.SavePowerMontiorDao;
import com.tuoming.mes.strategy.dao.SleepAreaSelDao;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.model.SavePowerMontiorModel;
import com.tuoming.mes.strategy.service.SavePowerMontiorService;
import com.tuoming.mes.strategy.service.handle.NotifyHandle;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * 节能小区监控业务实现类
 * @author Administrator
 *
 */
@Service("savePowerMontiorService")
public class SavePowerMontiorServiceImpl implements SavePowerMontiorService{
	@Autowired
	@Qualifier("savePowerMontiorDao")
	private SavePowerMontiorDao savePowerMontiorDao;
	
	@Autowired
	@Qualifier("sleepAreaSelDao")
	private SleepAreaSelDao sleepAreaSelDao;
	
	@Autowired
	@Qualifier("sleepExeDao")
	private SleepExeDao sleepExeDao;
	
	@Autowired
	@Qualifier("notifyDao")
	private NotifyDao notifyDao;
	
	@Autowired
	@Qualifier("businessLogDao")
	private BusinessLogDao businessLogDao;

	/**
	 * 监控主流程
	 */
	public void mainMontior(Map<String, String> context) {
		businessLogDao.insertLog(15, "监控模块开始", 0);
		//一补一唤醒指令移至历史表
		sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.NOTIFY);
		//delete删除指令表中一补一数据
		sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.NOTIFY);
		//多补一唤醒指令移至历史表
		sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.NOTIFY_MANY);
		//delete删除指令表中多补一数据
		sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.NOTIFY_MANY);
		//取得当前时间
		Date collectTime = DateUtil.tranStrToDate((String)context.get(Constant.CURRENT_COLLECTTIME));//当前时间
		Date lastPeriod = DateUtil.getRelateInterval(collectTime, -15, Calendar.MINUTE);//前15min时间
		//监控流程配置
		List<SavePowerMontiorModel> setList = savePowerMontiorDao.querySetList(context.get(Constant.KEY_GROUP_NAME));
		for(SavePowerMontiorModel set : setList) {
			//按配置生成mes_XXX_nonsleep表数据
			savePowerMontiorDao.addMotiorCellState(set.getExeSql(), lastPeriod);
			savePowerMontiorDao.updateBlack(lastPeriod, set.getGroup());//监控小区差指标次数满足大于N(N默认为3）则更新到黑名单
			NotifyHandle notifyHandle = AppContext.getBean(set.getNotifyHandle());//调用唤醒流程
			notifyHandle.handle(set.getGroup(), lastPeriod);
		}
		businessLogDao.insertLog(15, "监控模块完成", 0);
	}

	/**
	 * 对监控主流程中计算出的需要唤醒的小区，执行唤醒命令
	 */
	public void executeNotify() {
		businessLogDao.insertLog(14, "唤醒指令下发开始", 0);
		AdjustCommandService adjust=AppContext.getBean("AdjustCommandService");
		//多补一小区唤醒指令下发
		adjust.sleepOrNotify(Constant.APP_MULTINET, Constant.NOTIFY_MANY);
		//一补一小区唤醒指令下发
		adjust.sleepOrNotify(Constant.APP_MULTINET, Constant.NOTIFY);
//		sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.NOTIFY);
//		sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.NOTIFY);
		businessLogDao.insertLog(14, "唤醒指令下发结束", 0);
	}

	@Override
	public void notifyAllArea() {
		sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.NOTIFY);
		sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.NOTIFY);
		NotifyHandle notifyHandle = AppContext.getBean(Constant.DEFAULT_NOTIFY_HANDLE);
		notifyHandle.handle(Constant.GROUP_NOTIFY_ALL, null);
		this.executeNotify();
	}
}
