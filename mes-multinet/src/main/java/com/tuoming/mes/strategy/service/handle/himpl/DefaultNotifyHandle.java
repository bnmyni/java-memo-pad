package com.tuoming.mes.strategy.service.handle.himpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tuoming.mes.collect.dao.BusinessLogDao;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.execute.dao.AdjustCommandService;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.NotifyDao;
import com.tuoming.mes.strategy.dao.SleepAreaSelDao;
import com.tuoming.mes.strategy.dao.WarningCollectionDao;
import com.tuoming.mes.strategy.model.NotifyModel;
import com.tuoming.mes.strategy.service.handle.NotifyHandle;
import com.tuoming.mes.strategy.service.impl.SleepAreaSelectServiceImpl;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 默认小区唤醒执行流程处理器，根据补偿小区不同指标，分析该小区是否该唤醒
 * @author Administrator
 *
 */
@Component("notifyHandle")
public class DefaultNotifyHandle implements NotifyHandle{
	private final static Logger logger = LogFacade.getLog4j(DefaultNotifyHandle.class);
	@Autowired
	@Qualifier("AdjustCommandService")
	private AdjustCommandService service;
	@Autowired
	@Qualifier("notifyDao")
	private NotifyDao notifyDao;
	@Autowired
	@Qualifier("sleepAreaSelDao")
	private SleepAreaSelDao sleepAreaSelDao;

	@Autowired
	@Qualifier("warningCollectionDao")
	private WarningCollectionDao warningCollectionDao;
	
	@Autowired
	@Qualifier("businessLogDao")
	private BusinessLogDao businessLogDao;
	
	public void handle(String group, Date collectDate) {
		List<NotifyModel> setList = notifyDao.querySetList(group);
		Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
		for(NotifyModel model: setList) {
			if(model.isWeak()) {//假如当前配置的数据的补偿小区是差小区，则唤醒所有休眠小区
				List<Map<String, Object>> dataList = notifyDao.queryDataList(model.getQuerySql(),collectDate);
				int orderId = 0;
				for(Map<String, Object> data : dataList) {
					AdjustCommand command = this.buildNotifyCommand(model, data, orderId);
					if(command==null) {
						continue;
					}
			        service.save(command);
				}
			}else {//假如当前配置的数据的补偿小区不是差小区，则需要对该补偿小区的指标进行分析
				if(Constant.GSM.equalsIgnoreCase(group)) {//假如补偿小区是gsm制式
					List<Map<String, Object>> dicGsmList = sleepAreaSelDao.queryGsmDicList();
					String[] sqlArr = model.getQuerySql().split("#");
					List<Map<String, Object>> g2gData = notifyDao.queryDataList(sqlArr[0], collectDate);//查询补偿小区为gsm的非差指标小区，休眠小区为gsm的小区列表
					List<Map<String, Object>> t2gData = notifyDao.queryDataList(sqlArr[1], collectDate);
					Map<String, String> idMap = new LinkedHashMap<String, String>();//记录无重复的补偿小区标识
					Map<String, List<Map<String, Object>>> g2gMap = this.divideByGsmMakeUp(g2gData, idMap);
					Map<String, List<Map<String, Object>>> t2gMap = this.divideByGsmMakeUp(t2gData, idMap);
					int orderId = 0;
					for(String makeUpBs : idMap.keySet()) {
						Map<String, Object> current = null;
						if(g2gMap.get(makeUpBs)!=null) {
							current = g2gMap.get(makeUpBs).get(0);
						}
						if(t2gMap.get(makeUpBs)!=null) {
							current = t2gMap.get(makeUpBs).get(0);
						}
						double pdchczl = FormatUtil.tranferCalValue(current.get("pdchczl"));//pdch承载率
						double tbffud = FormatUtil.tranferCalValue(current.get("tbffud"));//tbf复用度
						double mxhwl = FormatUtil.tranferCalValue(current.get("mxhwl"));//每线话务量
						double wxzylyl = FormatUtil.tranferCalValue(current.get("wxzylyl"));//无线资源利用率
						Map<String, Double> dic= G2gSleepSelHandle.calGsmDic(dicGsmList, FormatUtil.tranferCalValue(current.get("tchxdcspz")));
						if(t2gMap.get(makeUpBs)!=null&&tbffud>thresholdDic.get("GSM_NOTIFYONE_TBFFYD")&&pdchczl>thresholdDic.get("GSM_NOTIFYONE_PDCHCZL")) {//判断GSM补偿小区TBF复用度是否大于2，且单PDCH承载小区大于45，如果是则为了保障用户业务感知，进行步骤2，唤醒对应的TDS节能小区，如果否不满足，则进行步骤3，继续判断；
							AdjustCommand command = this.buildNotifyCommand(model, current, orderId++);
					        service.save(command);
						}else if((mxhwl>FormatUtil.tranferCalValue(dic.get("m4"))
								&&wxzylyl>FormatUtil.tranferCalValue(dic.get("w4")))
								||(tbffud>thresholdDic.get("GSM_NOTIFYALL_TBFFYD")&&pdchczl>thresholdDic.get("GSM_NOTIFYALL_PDCHCZL"))) {//继续判断该补偿小区是否满足（每线话务量>M4,无线资源利用率>W4）或者（TBF复用度>4,单PDCH承载效率>30kbps),如果是满足，则进入步骤4唤醒该补偿小区对应的所有节能小区
							//2G恶化信息入库
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("pdchczl", pdchczl);
							dataMap.put("tbffud", tbffud);
							dataMap.put("mxhwl", mxhwl);
							dataMap.put("wxzylyl", wxzylyl);
							warningCollectionDao.insertDeteriorate(dataMap, group);

							if(g2gMap.get(makeUpBs)!=null) {
								for(Map<String, Object> g2gNotifyData : g2gMap.get(makeUpBs)) {
									AdjustCommand command = this.buildNotifyCommand(model, g2gNotifyData, orderId++);
									service.save(command);
								}
							}
							if(t2gMap.get(makeUpBs)!=null) {
								for(Map<String, Object> t2gNotifyData : t2gMap.get(makeUpBs)) {
									AdjustCommand command = this.buildNotifyCommand(model, t2gNotifyData, orderId++);
									service.save(command);
								}
							}
						}else if((mxhwl>FormatUtil.tranferCalValue(dic.get("m3"))
								&&wxzylyl>FormatUtil.tranferCalValue(dic.get("w3")))
								||(tbffud>thresholdDic.get("GSM_NOTIFY_TBFFYD3")&&pdchczl>thresholdDic.get("GSM_NOTIFY_PDCHCZL3"))) {//继续判断该补偿小区是否满足（每线话务量>M3,无线资源利用率>W3）或者（TBF复用度>4,单PDCH承载效率>20kbps)，如果是满足，则进入步骤6，唤醒一个对应的节能小区
							List<String> priorities = sleepAreaSelDao.queryPriorities();
							int tdPriority = priorities.indexOf(Constant.TD);
							int gsmPriority = priorities.indexOf(Constant.GSM);
							if(tdPriority>gsmPriority) {//假如td的唤醒优先级大于gsm的唤醒优先级    
								if(t2gMap.get(makeUpBs)!=null) {
									current = t2gMap.get(makeUpBs).get(0);
								}else {
									current = g2gMap.get(makeUpBs).get(0);
								}
							}else {
								if(g2gMap.get(makeUpBs)!=null) {
									current = g2gMap.get(makeUpBs).get(0);
								}else {
									current = t2gMap.get(makeUpBs).get(0);
								}
							}
							AdjustCommand command = this.buildNotifyCommand(model, current, orderId++);
							service.save(command);
						}
					}
				}else if(Constant.TD.equalsIgnoreCase(group)) {
					//只有T2T多补一的情况
					int orderId = 0;
					List<Map<String, Object>> t2tData= notifyDao.queryDataList(model.getQuerySql(), collectDate);
					List<Map<String, Object>> dicTdList = sleepAreaSelDao.queryTdDicList();
					List<String> idList = new ArrayList<String>();//记录无重复的补偿小区标识
					Map<String, List<Map<String, Object>>> t2tMap = this.divideByTdMakeUp(t2tData, idList);
					for(String makeUpBs: idList) {//判断该补偿小区是否满足（语音业务>T4,码资源利用率>C4，最大用户数>U4）,如果是满足，则进入步骤2唤醒该补偿小区对应的所有节能小区
						Map<String, Object> current = t2tMap.get(makeUpBs).get(0);
						double zbpz = FormatUtil.tranferCalValue(current.get("hzbpzs"));			//获取TD小区H载波配置数
						Map<String, Double> tdDic=T2tManySleepSelHandle.calTdDic(dicTdList, zbpz);
						double yyyw = FormatUtil.tranferCalValue(current.get("yyyw"));			//语音业务
						double mzylyl = FormatUtil.tranferCalValue(current.get("mzylyl"));		//码资源利用率
						double zdyhs = FormatUtil.tranferCalValue(current.get("zdyhs"));			//最大用户数
						double zrabyscs = FormatUtil.tranferCalValue(current.get("zrabyscs"));	//总RAB拥塞次数
						if(yyyw>FormatUtil.tranferCalValue(tdDic.get("t4"))
								&&mzylyl>FormatUtil.tranferCalValue(tdDic.get("c4"))
								&&zdyhs>FormatUtil.tranferCalValue(tdDic.get("u4"))) {//语音业务>T4,码资源利用率>C4，最大用户数>U4,唤醒所有休眠小区
							//3G恶化信息入库
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("yyyw", yyyw);
							dataMap.put("mzylyl", mzylyl);
							dataMap.put("zdyhs", zdyhs);
							dataMap.put("zrabyscs", zrabyscs);
							warningCollectionDao.insertDeteriorate(dataMap, group);

							for(Map<String, Object> data : t2tMap.get(makeUpBs)) {
								AdjustCommand command = this.buildNotifyCommand(model, data, orderId++);
								service.save(command);
							}
						}else if(yyyw<FormatUtil.tranferCalValue(tdDic.get("t3"))&&
								mzylyl<FormatUtil.tranferCalValue(tdDic.get("c3"))&&
								zrabyscs>FormatUtil.tranferCalValue(tdDic.get("u3"))) {//语音业务<T3，码资源利用率<C3，总RAB拥塞次数>U3,唤醒一个休眠小区
							AdjustCommand command = this.buildNotifyCommand(model, current, orderId++);
							service.save(command);
						}
						
					}
				}
				/********************Neusoft**************************/
				else if(Constant.LTE.equalsIgnoreCase(group)){//LTE为补偿小区的情况有两种
					String[] sqlArr = model.getQuerySql().split("#");
					List<Map<String, Object>> l2lData = notifyDao.queryDataList(sqlArr[0], collectDate);//查询补偿小区为LTE的非差指标小区，休眠小区为LTE的小区列表
					List<Map<String, Object>> t2lData = notifyDao.queryDataList(sqlArr[1], collectDate);//查询补偿小区为LTE的非差指标小区，休眠小区为TD的小区列表
					List<String> idList = new ArrayList<String>();//记录无重复的补偿小区标识
					Map<String, List<Map<String, Object>>> l2lMap = this.divideByLteMakeUp(l2lData, idList);
					Map<String, List<Map<String, Object>>> t2lMap = this.divideByLteMakeUp(t2lData, idList);
					
//					String[] sqlArr = model.getQuerySql().split("#");
//					List<String> idList = new ArrayList<String>();//记录无重复的补偿小区标识
//					List<Map<String, Object>> l2lData = new ArrayList<Map<String, Object>>();
//					List<Map<String, Object>> t2lData = new ArrayList<Map<String, Object>>();
//					Map<String, List<Map<String, Object>>> l2lMap = new HashMap<String, List<Map<String, Object>>>();
//					Map<String, List<Map<String, Object>>> t2lMap = new HashMap<String, List<Map<String, Object>>>();
//					if(sqlArr.length==2){
//						l2lData = notifyDao.queryDataList(sqlArr[0], collectDate);//查询补偿小区为LTE的非差指标小区，休眠小区为LTE的小区列表
//						t2lData = notifyDao.queryDataList(sqlArr[1], collectDate);//查询补偿小区为LTE的非差指标小区，休眠小区为TD的小区列表
//						l2lMap = this.divideByLteMakeUp(l2lData, idList);
//						t2lMap = this.divideByLteMakeUp(t2lData, idList);
//					}else{
//						l2lData = notifyDao.queryDataList(sqlArr[0], collectDate);
//						l2lMap = this.divideByLteMakeUp(l2lData, idList);
//					}
					
					Map<String, Double> dicLteMap = sleepAreaSelDao.queryLteDic();
					int orderId = 0;
					for(String makeUpBs: idList){
						Map<String, Object> current = null;
						if(l2lMap.get(makeUpBs)!=null) {
							current = l2lMap.get(makeUpBs).get(0);
						}
						if(t2lMap.get(makeUpBs)!=null) {
							current = t2lMap.get(makeUpBs).get(0);
						}
						double prblyl = FormatUtil.tranferCalValue(current.get("prblyl"));//prb利用率
						double zdyhs = FormatUtil.tranferCalValue(current.get("zdyhs"));//最大用户数
						double rabsbcs = FormatUtil.tranferCalValue(current.get("rabsbcs"));//RAB失败次数
					    if(prblyl>FormatUtil.tranferCalValue(dicLteMap.get("p2"))&&
					    		zdyhs>FormatUtil.tranferCalValue(dicLteMap.get("l2"))&&
					    		rabsbcs>thresholdDic.get("LTE_NOTIFY_RABSBCS")) {//PRB利用率>P2，最大用户数>L2，RAB失败次数>10
					    	//4G恶化信息入库
							Map<String, Object> dataMap = new HashMap<String, Object>();
							dataMap.put("prblyl", prblyl);
							dataMap.put("zdyhs", zdyhs);
							dataMap.put("rabsbcs", rabsbcs);
							warningCollectionDao.insertDeteriorate(dataMap, group);
							
					    	if(l2lMap.containsKey(makeUpBs)){
					    		for(Map<String, Object> data : l2lMap.get(makeUpBs)) {
									AdjustCommand command = this.buildNotifyCommand(model, data, orderId++);
									service.save(command);
								}
					    	}
					    	if(t2lMap.containsKey(makeUpBs)){
					    		for(Map<String, Object> data : t2lMap.get(makeUpBs)) {
									AdjustCommand command = this.buildNotifyCommand(model, data, orderId++);
									service.save(command);
								}
					    	}
						}else if(prblyl>FormatUtil.tranferCalValue(dicLteMap.get("p1"))&&
					    		zdyhs>FormatUtil.tranferCalValue(dicLteMap.get("l1"))){//PRB利用率>P1，最大用户数>L1
							List<String> priorities = sleepAreaSelDao.queryPriorities();
							int tdPriority = priorities.indexOf(Constant.TD);
							int ltePriority = priorities.indexOf(Constant.LTE);
							if(tdPriority>ltePriority) {//假如td的优先级大于lte的优先级，则先唤醒td
								if(t2lMap.containsKey(makeUpBs)) {
									current = t2lMap.get(makeUpBs).get(0);
								}else if(l2lMap.containsKey(makeUpBs)){
									current = l2lMap.get(makeUpBs).get(0);
								}
							}else {
								if(l2lMap.containsKey(makeUpBs)) {
									current = l2lMap.get(makeUpBs).get(0);
								}else if(t2lMap.containsKey(makeUpBs)){
									current = t2lMap.get(makeUpBs).get(0);
								}
							}
							AdjustCommand command = this.buildNotifyCommand(model, current, orderId++);
							service.save(command);
						}
						
					}					
				}

/*				else if(Constant.TD.equalsIgnoreCase(group)) {//更改为t2l后将不存在补偿小区为TD的情况
					int orderId = 0;
					List<Map<String, Object>> l2tData= notifyDao.queryDataList(model.getQuerySql(), collectDate);
					List<Map<String, Object>> dicTdList = sleepAreaSelDao.queryTdDicList();
					List<String> idList = new ArrayList<String>();//记录无重复的补偿小区标识
					Map<String, List<Map<String, Object>>> l2gMap = this.divideByTdMakeUp(l2tData, idList);
					for(String makeUpBs: idList) {//判断该补偿小区是否满足（语音业务>T4,码资源利用率>C4，最大用户数>U4）,如果是满足，则进入步骤2唤醒该补偿小区对应的所有节能小区
						Map<String, Object> current = l2gMap.get(makeUpBs).get(0);
						double zbpz = FormatUtil.tranferCalValue(current.get("hzbpzs"));//获取TD小区H载波配置数
						Map<String, Double> tdDic=T2gSleepSelHandle.calTdDic(dicTdList, zbpz);
						double yyyw = FormatUtil.tranferCalValue(current.get("yyyw"));
						double mzylyl = FormatUtil.tranferCalValue(current.get("mzylyl"));
						double zdyhs = FormatUtil.tranferCalValue(current.get("zdyhs"));
						double zrabyscs = FormatUtil.tranferCalValue(current.get("zrabyscs"));
						if(yyyw>FormatUtil.tranferCalValue(tdDic.get("t4"))
								&&mzylyl>FormatUtil.tranferCalValue(tdDic.get("c4"))
								&&zdyhs>FormatUtil.tranferCalValue(tdDic.get("u4"))) {//语音业务>T4,码资源利用率>C4，最大用户数>U4,唤醒所有休眠小区
							for(Map<String, Object> data : l2gMap.get(makeUpBs)) {
								AdjustCommand command = this.buildNotifyCommand(model, data, orderId++);
								service.save(command);
							}
						}else if(yyyw<FormatUtil.tranferCalValue(tdDic.get("t3"))&&
								mzylyl<FormatUtil.tranferCalValue(tdDic.get("c3"))&&
								zrabyscs>FormatUtil.tranferCalValue(tdDic.get("u3"))) {//语音业务<T3，码资源利用率<C3，总RAB拥塞次数>U3,唤醒一个休眠小区
							AdjustCommand command = this.buildNotifyCommand(model, current, orderId++);
							service.save(command);
						}
						
					}
				}else if(Constant.LTE.equalsIgnoreCase(group)) {
					int orderId = 0;
					List<Map<String, Object>> l2lData= notifyDao.queryDataList(model.getQuerySql(), collectDate);
					Map<String, Double> dicLteMap = sleepAreaSelDao.queryLteDic();
					List<String> idList = new ArrayList<String>();//记录无重复的补偿小区标识
					Map<String, List<Map<String, Object>>> l2lMap = this.divideByLteMakeUp(l2lData, idList);
					for(String makeUpBs: idList) {
						Map<String, Object> current = l2lMap.get(makeUpBs).get(0);
						double prblyl = FormatUtil.tranferCalValue(current.get("prblyl"));
						double zdyhs = FormatUtil.tranferCalValue(current.get("zdyhs"));
						double rabsbcs = FormatUtil.tranferCalValue(current.get("rabsbcs"));
					    if(prblyl>FormatUtil.tranferCalValue(dicLteMap.get("p2"))&&
					    		zdyhs>FormatUtil.tranferCalValue(current.get("l2"))&&
					    		rabsbcs>thresholdDic.get("LTE_NOTIFY_RABSBCS")) {//PRB利用率>P2，最大用户数>L2，RAB失败次数>10
					    	for(Map<String, Object> data : l2lMap.get(makeUpBs)) {
								AdjustCommand command = this.buildNotifyCommand(model, data, orderId++);
								service.save(command);
							}
						}else if(prblyl>FormatUtil.tranferCalValue(dicLteMap.get("p1"))&&
					    		zdyhs>FormatUtil.tranferCalValue(current.get("l1"))){//PRB利用率>P1，最大用户数>L1
							AdjustCommand command = this.buildNotifyCommand(model, current, orderId++);
							service.save(command);
						}
					}
				}*/
				/********************Neusoft**************************/
			}
		}
	}
	
	private Map<String, List<Map<String, Object>>> divideByLteMakeUp(
			List<Map<String, Object>> dataList, List<String> idList) {
		Map<String, List<Map<String, Object>>> res = new HashMap<String, List<Map<String,Object>>>();
		for(Map<String, Object> data : dataList) {
			String makeUpBs = String.valueOf(data.get("dest_enodebid"))+"_"+String.valueOf(data.get("dest_cellid"));
			if(!res.containsKey(makeUpBs)) {
				idList.add(makeUpBs);
				res.put(makeUpBs, new ArrayList<Map<String,Object>>());
			}
			res.get(makeUpBs).add(data);
		}
		return res;
	}

	/**
	 * 将Td补偿小区分堆
	 * @param dataList
	 * @return
	 */
	private Map<String, List<Map<String, Object>>> divideByTdMakeUp(
			List<Map<String, Object>> dataList, List<String> idList) {
		Map<String, List<Map<String, Object>>> res = new HashMap<String, List<Map<String,Object>>>();
		for(Map<String, Object> data : dataList) {
			String makeUpBs = String.valueOf(data.get("dest_lac"))+"_"+String.valueOf(data.get("dest_lcid"));
			if(!res.containsKey(makeUpBs)) {
				idList.add(makeUpBs);
				res.put(makeUpBs, new ArrayList<Map<String,Object>>());
			}
			res.get(makeUpBs).add(data);
		}
		return res;
	}


	/**
	 * 将gsm补偿小区分堆
	 * @param dataList
	 * @return
	 */
	private Map<String, List<Map<String, Object>>> divideByGsmMakeUp(
			List<Map<String, Object>> dataList, Map<String, String> idMap) {
		Map<String, List<Map<String, Object>>> res = new HashMap<String, List<Map<String,Object>>>();
		for(Map<String, Object> data : dataList) {
			String makeUpBs = String.valueOf(data.get("dest_lac"))+"_"+String.valueOf(data.get("dest_ci"));
			if(!idMap.containsKey(makeUpBs)) {
				idMap.put(makeUpBs, makeUpBs);
			}
			if(!res.containsKey(makeUpBs)) {
				res.put(makeUpBs, new ArrayList<Map<String,Object>>());
			}
			res.get(makeUpBs).add(data);
		}
		return res;
	}

	/**
	 * 根据结果和配置生成唤醒指令
	 * @param model
	 * @param data
	 * @param orderId
	 * @return
	 */
	private AdjustCommand buildNotifyCommand(NotifyModel model, Map<String, Object> data, int orderId) {
		String unitBs = null;
		ObjectType objectType = ObjectType.BSC;
		String spiltChar = "@";
		String commandMap = null;
		String queryCommandMap = null;
		String ne = null;
		String cellid = null;
		String desc = "唤醒指令生成，";
		/******************Neusoft***************************/
		//if(Constant.T2G.equalsIgnoreCase(String.valueOf(data.get("bus_type")))) {//当监控gsm小区设备时会同时判断是否唤醒td制式和gsm制式小区设备，所以将gsm和td设备的唤醒命令配置在一起，使用#分隔
		if(Constant.T2G.equalsIgnoreCase(String.valueOf(data.get("bus_type")))||Constant.T2L.equalsIgnoreCase(String.valueOf(data.get("bus_type")))){	
		/******************Neusoft***************************/
		unitBs = String.valueOf(data.get("src_rnc"));
			ne = String.valueOf(data.get("src_rnc"));
	    	cellid = String.valueOf(data.get("src_lcid"));
			if(model.getCommandMap().indexOf(spiltChar)>0) {
				String[] cMaps = model.getCommandMap().split(spiltChar);
				commandMap = cMaps[cMaps.length - 1];
				String[] qcMaps = model.getQueryCommandMap().split(spiltChar);
				queryCommandMap = qcMaps[qcMaps.length - 1];
			}
			desc += "小区信息:[RNC:"+unitBs+"] [CELLID:"+cellid+"]";
	    }else if(Constant.G2G.equalsIgnoreCase(String.valueOf(data.get("bus_type")))) {
	    	if(model.getCommandMap().indexOf(spiltChar)>0) {
	    		commandMap = model.getCommandMap().split(spiltChar)[0];
	    		queryCommandMap = model.getQueryCommandMap().split(spiltChar)[0];
	    	}
	    	unitBs = String.valueOf(data.get("src_bscid"));
	    	ne = String.valueOf(data.get("src_bscid"));
	    	cellid = String.valueOf(data.get("src_cellid"));
	    	desc += "小区信息:[BSCID:"+unitBs+"] [CELLID:"+cellid+"]";
	   /** 多补一 begin */
	   // }else if(Constant.L2L.equalsIgnoreCase(String.valueOf(data.get("bus_type")))||Constant.L2T.equalsIgnoreCase(String.valueOf(data.get("bus_type")))) {
		}else if(Constant.L2L.equalsIgnoreCase(String.valueOf(data.get("bus_type")))||Constant.L2L_MANY.equalsIgnoreCase(String.valueOf(data.get("bus_type")))) {
	   /** 多补一 end */
	    	unitBs = String.valueOf(data.get("omm"));
	    	ne = String.valueOf(data.get("src_enodebid"));
	    	cellid = String.valueOf(data.get("src_localcellid"));
	    	objectType = ObjectType.OMC;
	    	if(model.getCommandMap().indexOf(spiltChar)>0){
		    	if(data.get("cagroupid")!=null&&data.get("cagroupid")!="") {
					commandMap = model.getCommandMap().split(spiltChar)[1];
					queryCommandMap = model.getQueryCommandMap().split(spiltChar)[0];
				}else{
					commandMap = model.getCommandMap().split(spiltChar)[0];
					queryCommandMap = model.getQueryCommandMap().split(spiltChar)[0];
				}
	    	}
	    	desc = "小区信息:[OMM:"+unitBs+"] [ENODEBID:"+ne+"] [CELLID:"+cellid+"]";
	    } else if(Constant.T2T_MANY.equalsIgnoreCase(String.valueOf(data.get("bus_type")))){
	    	//T2T多补一的场合
			unitBs = String.valueOf(data.get("src_rnc"));
			ne = String.valueOf(data.get("src_rnc"));
		    cellid = String.valueOf(data.get("src_lcid"));
			commandMap = model.getCommandMap();
			queryCommandMap = model.getQueryCommandMap();
			desc = "小区信息:[RNC:"+unitBs+"] [CELLID:"+cellid+"]";
		}
		if(commandMap==null||queryCommandMap==null){
			commandMap = model.getCommandMap();
			queryCommandMap = model.getQueryCommandMap();
		}
		AdjustCommand command = new AdjustCommand();//构建休眠小区命令对象
        command.setTimeStamp(DateUtil.currentDate());
        command.setApplied(0);
        command.setOrderId(orderId);
        command.setAppName(Constant.APP_MULTINET);
        /** 多补一 begin */
        if(Constant.L2L_MANY.equalsIgnoreCase(String.valueOf(data.get("bus_type"))) //l2l多补一
        		||Constant.T2T_MANY.equalsIgnoreCase(String.valueOf(data.get("bus_type")))){ //t2t多补一
        	command.setGroupName(Constant.NOTIFY_MANY);
        }else{
        	command.setGroupName(Constant.NOTIFY);
        }
        /** 多补一 end */
        command.setOwner(Constant.APP_MULTINET);
        command.setTargetObject(unitBs);
        command.setObjectType(objectType);
        command.setBatchId(Constant.CURRENT_BATCH);
        String commandText = (String)((Map)DSLUtil.getDefaultInstance().compute(commandMap, data))
				.get(data.get("src_vender"));
        String queryCommand = (String)((Map)DSLUtil.getDefaultInstance().compute(queryCommandMap, data))
				.get(data.get("src_vender"));
        command.setCommand(commandText);//将休眠命令存入命令表中
        command.setExtend1(queryCommand);
        command.setExtend2(ne);
        command.setExtend3(cellid);
        data.put("command", commandText);
        try{
        	data.put("starttime", com.tuoming.mes.strategy.util.DateUtil.format((Date)data.get("starttime")));//将要发送命令的小区数据记录到拓展字段中，用于记录告警信息等数据        	
        	businessLogDao.insertLog(15, desc, 0);
        } catch (Exception e) {
        	logger.error("===============unitBs["+unitBs+"] ne["+ne+"] cellid["+cellid+"] bus_type["+String.valueOf(data.get("bus_type"))+"] starttime["+data.get("starttime")+"]");
        	businessLogDao.insertLog(15, desc, 1);
        	e.printStackTrace();
		}
        command.setExtend4(String.valueOf(JSONSerializer.toJSON(data)));
        /** 多补一 begin */
        command.setExtend5(String.valueOf(data.get("bus_type")));
        /** 多补一 end */
		return command;
	}
}
