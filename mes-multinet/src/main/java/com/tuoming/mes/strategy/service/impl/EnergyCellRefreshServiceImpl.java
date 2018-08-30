package com.tuoming.mes.strategy.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.services.impl.SEBizServiceImpl;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.AlarmInfoImpDao;
import com.tuoming.mes.strategy.dao.EnergyCellRefreshDao;
import com.tuoming.mes.strategy.dao.MrDetailDao;
import com.tuoming.mes.strategy.model.AlarmInfoModel;
import com.tuoming.mes.strategy.model.EnergyCellRefreshSetting;
import com.tuoming.mes.strategy.model.MrDetailModel;
import com.tuoming.mes.strategy.service.EnergyCellRefreshService;

/**
 * 节能小区更新实现
 * 
 * @author Administrator
 *
 */
@Service("energyCellRefreshService")
public class EnergyCellRefreshServiceImpl implements EnergyCellRefreshService {

	@Autowired
	@Qualifier("energyCellRefreshDao")
	private EnergyCellRefreshDao energyCellRefreshDao;
	@Autowired
	@Qualifier("alarmInfoImpDao")
	private AlarmInfoImpDao alarmInfoImpDao;
	@Autowired
	@Qualifier("MrDetailDao")
	private MrDetailDao mrDetailDao;
	@Autowired
	@Qualifier("businessLogDao")
	private BusinessLogDao businessLogDao;
	
	/**
	 * 刷新节能小区
	 * 
	 * Map<String,String> String 1:场景 2:Azimuth Mr
	 */
	public void refreshEnergyCell(Map<String, String> context) {
		businessLogDao.insertLog(10, "执行开始", 0);
		for (String bustype : Constant.BUSTYPEARR) {
			boolean isAzimuth = Boolean.parseBoolean(context.get(bustype));
			String groupName = Constant.MR;
			if (isAzimuth) {
				groupName = Constant.AZIMUTH;
			}
			// 根据组名进行查询
			List<EnergyCellRefreshSetting> settingList = energyCellRefreshDao
					.queryRefCellSetting(bustype, groupName);
			// 对分组后的数据进行遍历
			refreshEnergy(bustype, settingList);
		}
		businessLogDao.insertLog(10, "执行结束", 0);
	}

	/**
	 *  
	 * 删除节能小区及补偿小区中黑白名单及告警小区
	 * 删除节能小区中上一时刻已经成功休眠的小区
	 * 删除节能小区中上一时刻作为补偿小区的小区
	 * 删除补偿小区中上一时刻在其他场景中已休眠的小区
	 * 
	 * @param busType
	 * @param setList
	 */
	private void refreshEnergy(String busType,
			List<EnergyCellRefreshSetting> setList) {
		if (setList.isEmpty()) {
			return;
		}
		List<String> lsbList = new ArrayList<String>();
		try {
			// 创建满足mr重叠覆盖度的源区-邻区临时表，并且将创建的表名传递回来
			if (Constant.G2G.equalsIgnoreCase(busType)) {
				for (EnergyCellRefreshSetting set : setList) {
					//创建临时表
					String wlbm = energyCellRefreshDao.createTempTable(set
							.getQuerySql());
					lsbList.add(wlbm);
					// 按正常配置删除不合格小区
					energyCellRefreshDao.deleteSrcGsmBwa(wlbm);//删除源小区黑白名单及告警小区
					energyCellRefreshDao.deleteLinGsmBwa(wlbm);//删除邻小区黑白名单及告警小区
					energyCellRefreshDao.deleteG2gCellInBySleep(wlbm);//删除已休眠小区
				}
			} else if (Constant.L2L.equalsIgnoreCase(busType)) {
				for (EnergyCellRefreshSetting set : setList) {
					String wlbm = energyCellRefreshDao.createTempTable(set
							.getQuerySql());
					lsbList.add(wlbm);
					// 按正常配置删除不合格小区
					energyCellRefreshDao.deleteSrcLteBwa(wlbm);
					/** 多补一 begin */
					//华为Lte 多补一的场合
					if(set.getResTable()!= null && set.getResTable().length() > 0 && set.getResTable().indexOf("many") > 0){
						//多补一中，去除某邻区，该邻区对应的休眠小区及其他邻区一同去除
						energyCellRefreshDao.deleteManyNcLteBwa(wlbm);
						energyCellRefreshDao.deleteL2lManyCellInBySleep(wlbm);
					} else {
						energyCellRefreshDao.deleteLinLteBwa(wlbm);
						energyCellRefreshDao.deleteL2lCellInBySleep(wlbm);
					}
					/** 多补一 end */
				}
			} else if (Constant.T2G.equalsIgnoreCase(busType)) {
				for (EnergyCellRefreshSetting set : setList) {
					String wlbm = energyCellRefreshDao.createTempTable(set
							.getQuerySql());
					lsbList.add(wlbm);
					// 按正常配置删除不合格小区
					energyCellRefreshDao.deleteSrcTdBwa(wlbm);
					energyCellRefreshDao.deleteLinGsmBwa(wlbm);
					energyCellRefreshDao.deleteT2gCellInBySleep(wlbm);
				}
			}else if(Constant.T2L.equalsIgnoreCase(busType)){
				for(EnergyCellRefreshSetting set : setList){
					String wlbm = energyCellRefreshDao.createTempTable(set
							.getQuerySql());
					lsbList.add(wlbm);
					//删除不合格小区
					energyCellRefreshDao.deleteSrcTdBwa(wlbm);
					energyCellRefreshDao.deleteLinLteBwa(wlbm);
					energyCellRefreshDao.deleteT2lCellInBySleep(wlbm);
				}
			} else if (Constant.T2T_MANY.equalsIgnoreCase(busType)){
				/** 多补一 begin */
				for (EnergyCellRefreshSetting set : setList) {
					String wlbm = energyCellRefreshDao.createTempTable(set.getQuerySql());
					lsbList.add(wlbm);
					//由于T2TMR覆盖度需与方位角联合匹配，此处需将没有匹配到的数据去除
					energyCellRefreshDao.deleteTDisNullBwa(wlbm);
					// 按正常配置删除不合格小区
					energyCellRefreshDao.deleteSrcTDManyBwa(wlbm);
					//多补一中，去除某邻区，该邻区对应的休眠小区及其他邻区一同去除
					energyCellRefreshDao.deleteLinTdBwa(wlbm);
					energyCellRefreshDao.deletet2tManyCellInBySleep(wlbm);
				}
				/** 多补一 end */
			}
			int rowsNum = 0;
			String rsTable = "";
			for (String lsb : lsbList) {
				
				if (!rsTable.equals(setList.get(rowsNum).getResTable())) {
					rsTable = setList.get(rowsNum).getResTable();
					energyCellRefreshDao.removeTable(rsTable);
					energyCellRefreshDao.createResTable(rsTable, lsb);
				}
				energyCellRefreshDao.addData(rsTable, lsb);
				rowsNum ++;
			}
			/******************Neusoft********************/			
		} catch (Exception e) {
			businessLogDao.insertLog(10, "出现异常", 1);
			e.printStackTrace();
		} finally {
			for (String wlbm : lsbList) {
				energyCellRefreshDao.removeTable(wlbm);// 删除临时结果表
			}
		}
	}
	
	

	public void updateAlarmInfo() {
		List<AlarmInfoModel> setList = alarmInfoImpDao.queryAlarmSet(null);
		Map<String, List<AlarmInfoModel>> alarmMap = new HashMap<String, List<AlarmInfoModel>>();
		for(AlarmInfoModel set:setList) {
			if(alarmMap.get(set.getGroup())==null) {
				alarmMap.put(set.getGroup(), new ArrayList<AlarmInfoModel>());
			}
			alarmMap.get(set.getGroup()).add(set);
		}
		
		for(Entry<String, List<AlarmInfoModel>> entry: alarmMap.entrySet()) {
			alarmInfoImpDao.removeData(entry.getValue().get(0).getResTable());
			for(AlarmInfoModel model:entry.getValue()) {
				alarmInfoImpDao.updateAlarmInfo(model.getExeSql());
			}
		}
	}

	@Override
	public void improveMrData(String groupName) {
		List<MrDetailModel> setList = mrDetailDao.querySetList(groupName);
		Map<String, List<MrDetailModel>> map = new HashMap<String, List<MrDetailModel>>();
		for(MrDetailModel detail:setList) {
			if(map.get(detail.getResTable())==null) {
				map.put(detail.getResTable(), new ArrayList<MrDetailModel>());
			}
			map.get(detail.getResTable()).add(detail);
		}
 		
		for(Entry<String, List<MrDetailModel>> entry:map.entrySet()) {
			String tableName = entry.getKey();
			mrDetailDao.removeAllData(tableName);
			for(MrDetailModel detail:entry.getValue()) {
				mrDetailDao.insertData(detail.getResTable(), detail.getQuerySql());
			}
		}
	}
}
