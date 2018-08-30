package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.dao.HisFCastSettingDao;
import com.tuoming.mes.strategy.model.HisDataFCastSetting;
import com.tuoming.mes.strategy.service.handle.HisDataFCastHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;
import com.tuoming.mes.strategy.util.FormatUtil;
@Component("hisDataFCastHandle")
public class HisDataFCastHandleImpl implements HisDataFCastHandle{
	private static final int WEEKS_THREE_MONTH = 15;//24;//预测满足3个月历史数据的业务
	private static final int WEEKS_TWO_MONTH = 11;//预测满足2个月历史数据的业务
	private static final int WEEKS_ONE_MONTH = 7;//预测满足1个月历史数据的业务
	private static final int WEEKS_NOONE_MONTH = -7;//预测不满足一个月的历史数据业务
	@Autowired
	@Qualifier("hisFCastSettingDao")
	private HisFCastSettingDao hisFCastSettingDao;

	public void handleThrMon(HisDataFCastSetting setting, PrintStream ps, Date date) {
		Map<String, List<Map<String, Object>>> rowMap = this.querySrcData(setting, WEEKS_THREE_MONTH, date);//查询满足三个月的历史数据
		for(Entry<String, List<Map<String, Object>>> entry:rowMap.entrySet()) {
			if(entry.getValue().isEmpty()) {
				continue;
			}
			Map<String, Object> rstMap = this.calThrMon(entry.getValue(), setting, date);//根据历史6个月的数据计算满足3个月的数据
			CsvUtil.writeRow(rstMap, ps, setting.getColumnList());
		}
	}

	/**
	 * 将配置sql转换可查询sql
	 * @param setting
	 * @param timeList
	 * @param foreCastDate
	 * @param ywbs
	 * @return
	 */
	private String getFormatSql(HisDataFCastSetting setting, List<Date> timeList, Date foreCastDate, int ywbs) {
		String sqlStr = setting.getQuerySql();
		String[] lsbSql = sqlStr.substring(sqlStr.indexOf("[")+1, sqlStr.lastIndexOf("]")).split("\\|");
		Matcher matcher = Pattern.compile("\\[.*?\\]",  Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(sqlStr);
		if(ywbs==WEEKS_NOONE_MONTH) {
			if(matcher.find()) {
				String d = matcher.group();
				sqlStr = sqlStr.replace(d, lsbSql[1]);
			}
		}else {
			if(matcher.find()) {
				String d = matcher.group();
				sqlStr = sqlStr.replace(d, lsbSql[0]);
			}
		}
		StringBuilder filterSql = new StringBuilder();
		if(ywbs==WEEKS_THREE_MONTH) {//获取历史数据满足3个月的数据
			filterSql.append(" DATE_ADD(min(").append(setting.getDateBs())
			.append("),INTERVAL 12 WEEK)<=STR_TO_DATE('").append(DateUtil.format(foreCastDate))
			.append("','%Y-%m-%d %H:%i:%s') ");
		}else if(ywbs==WEEKS_TWO_MONTH) {//获取历史数据满足2个月的数据
			filterSql.append(" DATE_ADD(min(").append(setting.getDateBs())
			.append("),INTERVAL 8 WEEK)<=STR_TO_DATE('").append(DateUtil.format(foreCastDate))
			.append("','%Y-%m-%d %H:%i:%s') and DATE_ADD(min(").append(setting.getDateBs())
			.append("),INTERVAL 12 WEEK)>STR_TO_DATE('")
			.append(DateUtil.format(foreCastDate)).append("','%Y-%m-%d %H:%i:%s') ");
		}else if(ywbs==WEEKS_ONE_MONTH) {//获取历史数据满足1个月的数据
			filterSql.append(" DATE_ADD(min(").append(setting.getDateBs())
			.append("),INTERVAL 4 WEEK)<=STR_TO_DATE('").append(DateUtil.format(foreCastDate))
			.append("','%Y-%m-%d %H:%i:%s') and DATE_ADD(min(").append(setting.getDateBs())
			.append("),INTERVAL 8 WEEK)>STR_TO_DATE('").append(DateUtil.format(foreCastDate))
			.append("','%Y-%m-%d %H:%i:%s') ");
		}
		sqlStr = sqlStr.replace("$TIMEINTERVAL$", filterSql.toString());
		StringBuilder filterLine = new StringBuilder();
		for(Date time:timeList) {//过滤不同时间点的数据
			filterLine.append("STR_TO_DATE('").append(DateUtil.format(time))
			.append("','%Y-%m-%d %H:%i:%s'),");
		}
		filterLine.deleteCharAt(filterLine.lastIndexOf(","));
		sqlStr = sqlStr.replace("$TIMELINE$", filterLine.toString());
		timeList = DateUtil.getHistoryDate(//查询6个月的历史同一时刻
				foreCastDate, 15);
		if(ywbs == WEEKS_NOONE_MONTH) {//假如是不满足一个月，则需要减去前三周
			for(int i=3; i>=1;i--) 
			timeList.remove(0);
		}
		filterLine = new StringBuilder();
		for(Date time:timeList) {//查询6个月内，最小的同一时刻
			filterLine.append("STR_TO_DATE('").append(DateUtil.format(time))
			.append("','%Y-%m-%d %H:%i:%s'),");
		}
		filterLine.deleteCharAt(filterLine.lastIndexOf(","));
		sqlStr = sqlStr.replace("$TIMELINE1$", filterLine.toString());
		return sqlStr;
	}

	/**
	 * 预测满足3个月历史数据的数据行
	 * @param value
	 * @param noCalColMap
	 * @param dateBs 
	 * @param date 
	 * @return
	 */
	private Map<String, Object> calThrMon(List<Map<String, Object>> dataList,
			HisDataFCastSetting setting, Date date) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> temp1 = new ArrayList<Map<String,Object>>();//用于存储第一个月的数据
		List<Map<String, Object>> temp2 = new ArrayList<Map<String,Object>>();//用于存储第二个月的数据
		List<Map<String, Object>> temp3 = new ArrayList<Map<String,Object>>();//用于存储第3到6个月的数据
		for (Map<String, Object> data : dataList) {//按照数据的时刻，将数据放入不同的时间段集合中
			Date beforeDate = (Date) data.get(setting.getDateBs());
			if (DateUtil.validateInFirstMonth(beforeDate, date)) {
				temp1.add(data);
			} else if (DateUtil.validateInSecondMonth(beforeDate, date)) {
				temp2.add(data);
			} else {
				temp3.add(data);
			}
		}
		Map<String, Object> map1 = this.exeCal(temp1, 0.3, setting.getNoCalColMap());//用0.3乘以第一个月数据的平均值
		Map<String, Object> map2 = this.exeCal(temp2, 0.2, setting.getNoCalColMap());//用0.2乘以第二个月数据的平均值
		Map<String, Object> map3 = this.exeCal(temp3, 0.1, setting.getNoCalColMap());//用0.1乘以第3-6月数据的平均值
		double qzs =  (map1==null?0:0.3)+(map2==null?0:0.2)+(map3==null?0:0.1);//求取实际使用总权重
		for(Entry<String, Object> entry:dataList.get(0).entrySet()) {//按指标循环将3个时间段的值相加
			String key = entry.getKey();
		    if(setting.getNoCalColMap().containsKey(key)) {
		    	map.put(key, entry.getValue());
		    }else {
		    	double value =0;
		    	if(map1!=null) {
		    		value+=FormatUtil.tranferCalValue(map1.get(key));
		    	}
		    	if(map2!=null) {
		    		value+=FormatUtil.tranferCalValue(map2.get(key));
		    		
		    	}
		    	if(map3!=null) {
		    		value+=FormatUtil.tranferCalValue(map3.get(key));
		    	}
		    	map.put(key, value);
		    }
		}    
		this.storeOtherRs(setting, map, qzs, date);
		return map;
	}

	/**
	 * 执行历史业务预测计算
	 * @param temp1
	 * @param qz
	 * @param noCalColMap
	 * @return
	 */
	private Map<String, Object> exeCal(List<Map<String, Object>> temp1,
			 double qz, Map<String, String> noCalColMap) {
		if(temp1.isEmpty()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		for(Entry<String, Object> entry:temp1.get(0).entrySet()) {//循环指标，对不同指标进行进行平均值*权重
			String key = entry.getKey();
		    if(noCalColMap.containsKey(key)) {
		    	map.put(key, entry.getValue());
		    }else {
		    	double value = 0;
		    	for(Map<String, Object> data: temp1) {
		    		value += FormatUtil.tranferCalValue(data.get(key));
		    	}
		    	map.put(key, value*qz/temp1.size());
		    }
		}
		return map;
	}

	/**
	 * 将数据按行标识分堆
	 * @param dataList
	 * @param rowBs
	 * @return
	 */
	private Map<String, List<Map<String, Object>>> divideList(
			List<Map<String, Object>> dataList, String rowBs) {
		Map<String, List<Map<String, Object>>> rowMap = new HashMap<String, List<Map<String,Object>>>();
		for(Map<String, Object> data :dataList) {
			String rowid =  DSLUtil.getDefaultInstance().buildString(rowBs, data);
			if(rowMap.get(rowid)==null) {
				rowMap.put(rowid, new ArrayList<Map<String,Object>>());
			}
			rowMap.get(rowid).add(data);
		}
		return rowMap;
	}

	/**
	 * 计算满足两个月的历史预测
	 */
	public void handleTwoMon(HisDataFCastSetting setting, PrintStream ps, Date date) {
		Map<String, List<Map<String, Object>>> rowMap = this.querySrcData(setting, WEEKS_TWO_MONTH, date);
		for (Entry<String, List<Map<String, Object>>> entry : rowMap.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			Map<String, Object> rstMap = this.calTwoMon(entry.getValue(),
					setting, date);
			CsvUtil.writeRow(rstMap, ps, setting.getColumnList());
		}
	}

	private Map<String, Object> calTwoMon(List<Map<String, Object>> list,
			HisDataFCastSetting setting, Date date) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> noCalColMap = setting.getNoCalColMap();
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();// 代表满足两个月的
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();// 代表满足一个月的
		for (Map<String, Object> smap : list) {
			Date d = (Date) smap.get(setting.getDateBs());
			if (DateUtil.validateInFirstMonth(d, date)) {
				list1.add(smap);
			} else {
				list2.add(smap);
			}
		}
		Map<String, Object> map1 = this.exeCal(list1, 0.2, noCalColMap);
		Map<String, Object> map2 = this.exeCal(list2, 0.3, noCalColMap);
		double qzs =  (map1==null?0:0.2)+(map2==null?0:0.3);//求取实际使用总权重
		for (Entry<String, Object> so : list.get(0).entrySet()) {
			String key = so.getKey();
			if (noCalColMap.containsKey(key)) {
				map.put(key, so.getValue());
			} else {
				double value = 0;
				if (map1 != null) {
					value += FormatUtil.tranferCalValue(map1.get(key));
				}
				if (map2 != null) {
					value += FormatUtil.tranferCalValue(map2.get(key));
				}
				map.put(key, value);
			}
		}
		this.storeOtherRs(setting, map, qzs, date);
		return map;
	}
	
	/**
	 * 将计算结果存入结果行中，以便于导入文件
	 * @param setting
	 * @param map
	 * @param qz
	 */
	private void storeOtherRs(HisDataFCastSetting setting,
			Map<String, Object> map, double qz, Date date) {
		map.put(setting.getDateBs(), date);
		map.put("qz", qz);
	}

	/**
	 * 计算满足一个月的历史预测
	 */
	public void handleOneMon(HisDataFCastSetting setting, PrintStream ps, Date date) {
		Map<String, List<Map<String, Object>>> rowMap = this.querySrcData(setting, WEEKS_ONE_MONTH, date);
		for (Entry<String, List<Map<String, Object>>> entry : rowMap.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			Map<String, Object> rstMap = this.calOnePeriod(entry.getValue(),
					setting, 0.4, date);
			CsvUtil.writeRow(rstMap, ps, setting.getColumnList());
		}
	}

	private Map<String, Object> calOnePeriod(List<Map<String, Object>> list,
			HisDataFCastSetting setting, double qz, Date date) {
		Map<String, Object> map = this.exeCal(list, qz, setting.getNoCalColMap());
		this.storeOtherRs(setting, map, qz, date);
		return map;
	}
	
   /** 
	 * 查询要计算的源数据，并将其按照业务主键分堆
	 * @param setting
	 * @param ps
	 * @param WeekNum
	 */
	private Map<String, List<Map<String, Object>>> querySrcData(
			HisDataFCastSetting setting, int ywbs, Date forcastDate) {
		List<Date> timeList = null;
		if(ywbs == WEEKS_NOONE_MONTH) {//假如是不满足一月，则求取过去7天预测时刻数据列表
			timeList = DateUtil.getRelateDays(forcastDate, WEEKS_NOONE_MONTH);
		}else {//求取满足条件的时刻列表，满足3个月则求取24个周相同的时刻表，满足2个月则为11个周相同的时刻表
			timeList = DateUtil.getHistoryDate(
					forcastDate, ywbs);
		}
		List<Map<String, Object>> dataList = hisFCastSettingDao
				.queryMetaData(this.getFormatSql(setting, timeList,
						forcastDate, ywbs));
		Map<String, List<Map<String, Object>>> rowMap = this.divideList(
				dataList, setting.getRowBs());
		return rowMap;
	}

	/**
	 * 预测不满足一个月的数据
	 */
	public void handleNoMon(HisDataFCastSetting setting, PrintStream ps, Date date) {
		Map<String, List<Map<String, Object>>> rowMap =  this.querySrcData(setting, WEEKS_NOONE_MONTH,date);//查询7天内的数据
		for (Entry<String, List<Map<String, Object>>> entry : rowMap.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			Map<String, Object> rstMap = this.calOnePeriod(entry.getValue(),//计算7天数据的平均值然后乘以0.3
					setting, 0.3, date);
			CsvUtil.writeRow(rstMap, ps, setting.getColumnList());
		}
	}


}
