package com.tuoming.mes.strategy.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tuoming.mes.collect.dao.BusinessLogDao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.dao.TextLogParserDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.models.TextLogParser;
import com.tuoming.mes.services.serve.LogCommandService;
import com.tuoming.mes.services.serve.MESManager;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.BeforeAfterDao;
import com.tuoming.mes.strategy.model.BeforeAndAfterSetting;
import com.tuoming.mes.strategy.service.BeforeAfterService;
import com.tuoming.mes.strategy.util.DateUtil;

@SuppressWarnings("unchecked")
@Service("beforeAfterService")
public class BeforeAfterServiceImpl implements BeforeAfterService {
	private static Logger logger = LogFacade.getLog4j(BeforeAfterService.class);
	@Autowired
	@Qualifier(value = "beforeAfterDao")
	private BeforeAfterDao beforeAfterDao;

	@Override
	public void executeBeforeOrAfter(String groupName) {
		List<BeforeAndAfterSetting> list = beforeAfterDao.querySetting(
				groupName);
		//将载频级别的表改为小区级别的表(CM)
		if(groupName.equalsIgnoreCase(Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_CM)){
			for (BeforeAndAfterSetting set : list) {
				beforeAfterDao.deleteResultTable(set.getTablename());
				beforeAfterDao.createTableForCarrier(set);
			}
		//将载频级别的表改为小区级别的表(PM)
		}else if(groupName.equalsIgnoreCase(Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_PM)){
			for (BeforeAndAfterSetting set : list) {
//				beforeAfterDao.deleteResultTable(set.getTablename());
				beforeAfterDao.insertDataForCarrier(set);
			}
		//清除PM表中六个月前的数据(不包含MR的数据)
		}else if(groupName.equalsIgnoreCase(Constant.BEFORE_COLLECT_DATA_CLEAN_PM)){
			String befortime = DateUtil.getDay(-106);//获取六个月以前的日期数据
			List<Date> timeList = DateUtil.getTimeList(DateUtil.tranStrToDate(befortime), 0, 24, 15);
			for (BeforeAndAfterSetting set : list) {
				Map<String,String> tableMap = (Map<String, String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				
				for (Entry<String, String> entry: tableMap.entrySet()) {
					if(!timeList.isEmpty()){
						for (Date time:timeList){
							beforeAfterDao.deleteTableForPm(set.getExecutesql(),entry.getKey(), entry.getValue(), DateUtil.format(time));
						}
					}
					
				}
			}
		//q清除CM表中的数据
		}else if(groupName.startsWith(Constant.BEFORE_COLLECT_DATA_CLEAN_CM)){
			for (BeforeAndAfterSetting set : list) {
				List<String> tableList = (List<String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (int i = 0; i < tableList.size(); i++) {
					beforeAfterDao.deleteTableForCm(set.getExecutesql(),tableList.get(i));
				}
			}
		}else if(groupName.equalsIgnoreCase(Constant.BEFORE_COLLECT_DATA_CLEAN_CARRIER)){//清除载频级别PM表中的数据
			for (BeforeAndAfterSetting set : list) {
				List<String> tableList = (List<String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (int i = 0; i < tableList.size(); i++) {
					beforeAfterDao.deleteTableForCm(set.getExecutesql(),tableList.get(i));
				}
			}
		//将pm中的历史数据导入历史表中
		}else if(groupName.equalsIgnoreCase(Constant.BEFORE_COLLECT_DATA_ADD_BATCHID)){
			for (BeforeAndAfterSetting set : list) {
				List<String> tableList = (List<String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (int i = 0; i < tableList.size(); i++) {
					beforeAfterDao.removePmData(tableList.get(i));
				}
			}
		//清除mes_alarm中的数据
		}else if(groupName.equalsIgnoreCase(Constant.BEFORE_COLLECT_DATA_CLEAN_ALARM)){
			for (BeforeAndAfterSetting set : list) {
				List<String> tableList = (List<String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (int i = 0; i < tableList.size(); i++) {
					beforeAfterDao.deleteTableForCm(set.getExecutesql(),tableList.get(i));
				}
			}
		//清除性能统计结果表中的大于一个月的数据
		}else if(groupName.equalsIgnoreCase(Constant.BEFORE_COLLECT_DATA_CLEAN_PERF)){
			String time = DateUtil.getDay(-30);//获取4周以前的时间点
			for (BeforeAndAfterSetting set : list) {
				Map<String,String> tableMap = (Map<String, String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (Entry<String, String> entry: tableMap.entrySet()) {
					beforeAfterDao.deleteTableForPm(set.getExecutesql(),entry.getKey(), entry.getValue(), time);
				}
			}
		}else if(groupName.startsWith(Constant.AFTER_COLLECT_DATA_IMPORTDATA_CM)){
			int num = ConfigurationManager.getDefaultConfig().getInteger(Constant.CM_COLLECT_DIFFER_NUM, -500);
			for (BeforeAndAfterSetting set : list) {
				List<String> tableList = (List<String>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (String table : tableList) {
					if(this.enableImport(table, num)) {
						beforeAfterDao.updateData(table);
				    }
			    }
		    }
		}else if(Constant.AFTER_COLLECT_DATA_UPDATE_NETELE.equalsIgnoreCase(groupName)){
			for (BeforeAndAfterSetting set : list) {
				List<Map<String, String>> maps = (List<Map<String, String>>) DSLUtil.getDefaultInstance().compute(set.getTablename());
				for (Map<String, String> map : maps) {
					beforeAfterDao.updateSql(DSLUtil.getDefaultInstance().buildString(set.getExecutesql(), map));
				}
			}
		}else {//修改表中的不规范列
			for (BeforeAndAfterSetting set : list) {
				List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(set.getExecutesql());
				for (String str : colList) {
					beforeAfterDao.updateSql(str);
				}
			}
		}
	}

	private boolean enableImport(String table, int num) {
		int sourceCount = beforeAfterDao.queryDataCount(table);
		int bakCount = beforeAfterDao.queryDataCount(table+"_bak");
		if(bakCount-sourceCount>=num) {
			return true;
		}
		return false;
	}

	@Override
	public void cleanServerFile(int day) {
		String rootPath = Envirment.getHome()+"data";
		File f = new File(rootPath);
		for(File file: f.listFiles()) {
			if(this.enableDel(file.getName(), day)) {
				deleteDir(file);
			}
		}
		
	}
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
	
	private boolean enableDel(String dateStr, int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		try {
			Date date = sdf.parse(dateStr);
			Calendar currentTime = Calendar.getInstance();
			Calendar hisTime = Calendar.getInstance();
			hisTime.setTime(date);
			hisTime.add(Calendar.DAY_OF_MONTH, day);
			return hisTime.before(currentTime);
		} catch (ParseException e) {
			logger.info(dateStr+"文件夹非日期文件夹，无法删除.");
			return false;
		}
	}

	@Override
	public void reCollectFailCommand(int times) {
		while(times>0) {
			try {
				TextLogParserDao textLogParser = AppContext.getBean("TextLogParserDao");
				List<TextLogParser> list = textLogParser.listAll();
				Map<String, List<TextLogParser>> map = new HashMap<String, List<TextLogParser>>();
				for(TextLogParser parser: list) {
					if(parser.getEnabled()) {
						if(map.get(parser.getTargetTable())==null) {
							map.put(parser.getTargetTable(), new ArrayList<TextLogParser>());
						}
						map.get(parser.getTargetTable()).add(parser);
					}
				}
				MESManager mes = new MESManager();
				LogCommandService cmdService = (LogCommandService) mes.getService("LogCommandService");
				for(Entry<String, List<TextLogParser>> entry: map.entrySet()) {
					String bakTabelName = entry.getKey();
					
					if("cm_td_tcarrier_hw_bak".equalsIgnoreCase(bakTabelName)
							||"cm_td_tcarrier_zte_bak".equalsIgnoreCase(bakTabelName)) {//这两个文件采集的是载频级别的，而我们关注的是其小区级别的数据
						if(!this.enableImport(bakTabelName.substring(0,bakTabelName.length()-4)+"_cell", -50)) {
							logger.info(entry.getKey()+"重新采集开始!");
							beforeAfterDao.removeData(bakTabelName);
							for(TextLogParser t:entry.getValue()) {
								cmdService.queryAllByLogParser(t.getName(), Constant.CURRENT_BATCH);
							}
							List<BeforeAndAfterSetting> carrietList = beforeAfterDao.querySetting(
									Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_CM);
							for(BeforeAndAfterSetting set:carrietList) {
								if((bakTabelName.substring(0,bakTabelName.length()-4)+"_cell_bak").equalsIgnoreCase(set.getTablename())) {
									beforeAfterDao.deleteResultTable(set.getTablename());
									beforeAfterDao.createTableForCarrier(set);
								}
							}
							if(this.enableImport(bakTabelName.substring(0,bakTabelName.length()-4)+"_cell", -50)) {
								beforeAfterDao.updateData(bakTabelName.substring(0,bakTabelName.length()-4));
								logger.info(entry.getKey()+"重新采集成功!");
							}
							logger.info(entry.getKey()+"重新采集结束!");
						}
					}else{
						if(!this.enableImport(bakTabelName.substring(0,bakTabelName.length()-4), -50)) {
							logger.info(entry.getKey()+"重新采集开始!");
							beforeAfterDao.removeData(bakTabelName);
							for(TextLogParser t:entry.getValue()) {
								cmdService.queryAllByLogParser(t.getName(), Constant.CURRENT_BATCH);
							}
							if(this.enableImport(bakTabelName.substring(0,bakTabelName.length()-4), -50)) {
								beforeAfterDao.updateData(bakTabelName.substring(0,bakTabelName.length()-4));
								logger.info(entry.getKey()+"重新采集成功!");
							}
							logger.info(entry.getKey()+"重新采集结束!");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				times--;
			}
				
				
		}
	}
	
	public void calOverDegreeDone(String type) {
		String mrDir = AppContext.CACHE_ROOT+"mr/"+type;
		File mrFile = new File(mrDir);
		if(!mrFile.exists()) {
			mrFile.mkdirs();
		}
		String currentMonth = DateUtil.format(new Date(), "yyyy-MM");
		File f = new File(mrDir, currentMonth);
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean sfCalOverDegree(String type) {
		if(isMonthStart()) {
			return false;
		}
		String mrDir = AppContext.CACHE_ROOT+"mr/"+type;
		File mrFile = new File(mrDir);
		if(!mrFile.exists()) {
			return false;
		}
		String currentMonth = DateUtil.format(new Date(), "yyyy-MM");
		boolean collectDone = false;
		for(File f:mrFile.listFiles()) {
			if(f.isFile()&&currentMonth.equals(f.getName())) {
				return false;
			}
			if(f.isFile()&&f.getName().equals(currentMonth+"_COLLECT_DONE")) {
				collectDone = true;
			}
		}
		return collectDone;
	}
	
	private boolean isMonthStart() {
		String mon = DateUtil.format(new Date(),"MM");
		return "01".equals(mon);
	}

	@Override
	public boolean sfCollectMr(String type, int days) {
		//判断当前是时间是否为01月，是：返回false
		if(isMonthStart()) {
			return false;
		}
		String mrDir = AppContext.CACHE_ROOT+"mr/"+type;
		File mrFile = new File(mrDir);
		//判断目录是否存在，不存在：返回true
		if(!mrFile.exists()) {
			return true;
		}
		File[] childFile = mrFile.listFiles();
		int count = 0;
		//将当前时间的日设为01号
		Calendar curCal = Calendar.getInstance();
		curCal.set(Calendar.DAY_OF_MONTH, 1);
		curCal.set(Calendar.HOUR_OF_DAY, 0);
		curCal.set(Calendar.MINUTE, 0);
		curCal.set(Calendar.SECOND, 0);
		curCal.set(Calendar.MILLISECOND, 0);
		String currentMonth = DateUtil.format(curCal.getTime(), "yyyy-MM");
		for (File f:childFile) {
			//通过文件名称判断是否为想要读取的文件，文件名为当前月，返回false,即不读取当前月的数据
			if(f.isFile()&&f.getName().equals(currentMonth)) {
				return false;
			}
			if(count==days) {
				return false;
			}
			//对文件名称进行正则表达式校验,不满足校验规则的文件不读取
			String dirName = f.getName();
			if(!dirName.matches("\\d{4}-\\d{2}-\\d{2}")) {
				continue;
			}
			//文件时间大于当前时间，不读取此文件
			Date dirDate = com.pyrlong.util.DateUtil.getDate(dirName);
			if(dirDate.before(curCal.getTime())) {
				continue;
			}
			//读取此文件/文件夹中的文件，如果文件存在则文件个数count值加1
			if(this.existFile(f)) {
				count++;
			}
		}
		if(count<days) {
			return true;
		}else {
			/*
			 * 创建文件，通过判断某月是否存在相应文件夹，判断是否进行重叠覆盖度计算，
			 * 存在则已经对此月份的数据进行了采集，可以进行计算
			 */
			String collectDone = currentMonth+"_COLLECT_DONE";
			File doneFile = new File(mrDir+"/"+collectDone);
			if(!doneFile.exists()){
				try {
					doneFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		
	}

	public static boolean existFile(File f) {
        if (f.exists()) {
            File[] filelist = f.listFiles();
            if (filelist != null) {
                for (File cf : filelist) {
                    if (cf.isFile()) {
                    	return true;
                    }else if(cf.isDirectory()){
                    	return existFile(cf);
                    }
                }
            }
        } 
		return false;
	}

	@Override
	public void cleanMrFile() {
		String mrDir = AppContext.CACHE_ROOT+"mr/";
		FileOper.delAllFile(mrDir);
	}
	
}
