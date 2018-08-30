package com.tuoming.mes.strategy.service.thread;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.TdOffKpiDao;
import com.tuoming.mes.strategy.dao.TdOffSleepAreaSelDao;
import com.tuoming.mes.strategy.model.TdOffKpiModel;
import com.tuoming.mes.strategy.model.TdOffSleepSelectModel;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 3G退网线程
 * @author Administrator
 *
 */
public class TdOffThread implements Runnable{
	
	private final static Logger logger = LogFacade.getLog4j(TdOffThread.class);
	TdOffSleepAreaSelDao tdOffSleepAreaSelDao = AppContext.getBean("tdOffSleepAreaSelDao");
	TdOffKpiDao tdOffKpiDao = AppContext.getBean("tdOffKpiDao");
	
	private String busytype;
	private String monthAgo;
	private String dayAgo;
	private String nowTime;
	private String rootPath;
	private List<Map<String, Object>> tdDic;
	private List<Map<String, Object>> gsmDic;
	private List<Map<String, Double>> lteDic;
	private List<Map<String,Object>> cellList;
	
	public TdOffThread(String busytype,String monthAgo,String dayAgo,String rootPath,
			List<Map<String, Object>> tdDic,List<Map<String, Object>> gsmDic,List<Map<String, Double>> lteDic,
			String nowTime,List<Map<String,Object>> cellList){
		this.busytype = busytype;
		this.monthAgo = monthAgo;
		this.nowTime = nowTime;
		this.rootPath = rootPath;
		this.tdDic = tdDic;
		this.gsmDic = gsmDic;
		this.lteDic = lteDic;
		this.dayAgo = dayAgo;
		this.cellList = cellList;
	}
	
	@Override
	public void run() {			
		//查询覆盖度，按小区循环遍历，每次遍历只处理同一个小区的数据
		boolean isT2G = busytype.equalsIgnoreCase("azimuth_t2g")?true:false;
		
		List<Map<String,Object>> p_sleepArea = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> s_sleepArea = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> cell:cellList){
			String src_rnc = cell.get("src_rnc").toString();
			String src_cellid = cell.get("src_cellid").toString();
			String src_vender = cell.get("src_vender").toString();
			String dest_vender= cell.get("dest_vender").toString();
			String dest_key1 = "";
			String dest_key2 = "";
			String vender = "";
			
			
			if(src_vender.equals(Constant.HW)){
				vender = Constant.VENDER_HW;
			}else if(src_vender.equals(Constant.ERIC)){
				vender = Constant.VENDER_ERIC;
			}else if(src_vender.equals(Constant.ZTE)){
				vender = Constant.VENDER_ZTE;
			}
			
			//查询配置表
			TdOffKpiModel sleep_model = tdOffKpiDao.querySleepAreaKpiSet("azimuth_td_"+vender);			
				
//			List<Map<String,Object>> tdList = tdOffSleepAreaSelDao.queryMetaData(this.getQuerySql(sleep_model.getQuerySql(), monthAgo, dayAgo, "NNRNC27", "14121"));			
			List<Map<String,Object>> tdList = tdOffSleepAreaSelDao.queryMetaDataTest(this.getQuerySql(sleep_model.getQuerySql()), monthAgo, dayAgo, "NNRNC27", "14121");			

			//List<Map<String,Object>> tdList = tdOffSleepAreaSelDao.queryMetaData(this.getQuerySql(sleep_model.getQuerySql(), monthAgo, dayAgo, "NNRNC27", "14121"));
			if(tdList == null||tdList.size()<=0){//小区不符合条件
				continue;
			}else if(tdList.get(0).containsValue(null)){  
				continue;
			}
			logger.info("--------节能小区判断开始--------");
			Map<String, Map<String,Object>> tdMap = new HashMap<String, Map<String,Object>>();
			
			for(Map<String,Object> data: tdList){ 
				tdMap.put(DateUtil.format((Date)data.get("starttime")), data); 
				//将历史语音业务量最大值、历史数据流量最大值添加到小区数据中
				cell.put("yyyw_max", data.get("yyyw_max"));
				cell.put("sjll_max", data.get("sjll_max"));
			}
			/************节能小区判断  start************/	
			List<String> interval15 = DateUtil.getIntervalTime(null, null, 15);//0-24点间隔15min时间列表
			
			boolean permanenceFlag = true;//永久降耗标识										
			Map<String,Integer> timeRange = new HashMap<String,Integer>();//存储满足条件的开始结束时间
			Map<String,String> timeMap = new HashMap<String,String>();
			List<Map<String,String>> timeRangeList = new ArrayList<Map<String,String>>();
			/**
			 * 30天数据按每15min时刻表进行分组计数
			 */
			for(String timeStr:interval15){	//一天每15min间隔时间列表			
				for(String dateStr: DateUtil.getRelateDays(nowTime, 30)){//日期列表，日期格式：yyyy-mm-dd					
					if(tdMap.containsKey(dateStr+ " "+timeStr)){//30天每15min数据中有某天某时刻数据
						//静态降耗判断性能指标
						if(this.tdPerformance(tdMap.get(dateStr+ " "+timeStr), tdDic, false)){//TD满足门限
							if(!timeRange.containsKey(timeStr)){
								timeRange.put(timeStr, 1);
							}else{
								int cnt = timeRange.get(timeStr);
								timeRange.put(timeStr, cnt+1);
							}
						}
						//永久降耗判断性能指标
						if(permanenceFlag && !this.tdPerformance(tdMap.get(dateStr+ " "+timeStr), tdDic, true)){
							permanenceFlag = false;
						}
					}else{//30天每15min数据中不存在某天某时刻数据，默认满足阀值	
						if(!timeRange.containsKey(timeStr)){
							timeRange.put(timeStr, 1);
						}else{
							int cnt = timeRange.get(timeStr);
							timeRange.put(timeStr, cnt+1);
						}
					}
				}
				/**
				 * 按每15min时刻表分组后的数据，
				 * 当计数=30，则说明30天此15min数据都满足门限；
				 * 将满足门限的时刻记录，进行连续时间>=6h判断
				 */
				if(timeRange.get(timeStr) == 30 && !timeMap.containsKey("sTime")){
					timeMap.put("sTime", timeStr);
				}else if(timeRange.get(timeStr) == 30 && timeMap.containsKey("sTime")){
					timeMap.put("lastTime", timeStr);
				}else if(timeRange.get(timeStr) < 30){
					if( timeMap.containsKey("lastTime")){
						timeMap.put("eTime", timeMap.get("lastTime"));
					} else {
						timeMap = new HashMap<String,String>();
					}					
				}
				/**
				 * 开始时间和结束时间同时存在
				 */
				if(timeMap.containsKey("sTime") && timeMap.containsKey("eTime")){					
					timeRangeList.add(timeMap);
					timeMap = new HashMap<String,String>();//将Map清空
				}
			}
			//处理时间段，判断开始时间结束时间>=6h
			if(timeRangeList != null && timeRangeList.size()>0){
				//对跨天时间进行处理
				timeRangeList = this.changeTimeRange(timeRangeList);
				timeRangeList = DateUtil.exceed6Hour(timeRangeList);
			}			
						
			logger.info("--------节能小区判断结束--------");
			/**    补偿小区判断  start
			 * 判断补偿小区,通过节能小区筛选没有可降耗小区，则不再进行补偿小区判断
			 */
			if(permanenceFlag ||(!timeRangeList.isEmpty() && timeRangeList.size()>0)){
				logger.info("--------补偿小区判断开始--------");
				if(dest_vender.equals(Constant.HW)){
					vender = Constant.VENDER_HW;
				}else if(dest_vender.equals(Constant.ERIC)){
					vender = Constant.VENDER_ERIC;
				}else if(dest_vender.equals(Constant.ZTE)){
					vender = Constant.VENDER_ZTE;
				}
				//查询配置表
				String groupName = isT2G?"azimuth_gsm_"+vender:"azimuth_lte_"+vender;
				TdOffKpiModel model = tdOffKpiDao.querySleepAreaKpiSet(groupName);
				if(isT2G){				
					dest_key1 = cell.get("dest_lac").toString();
					dest_key2 = cell.get("dest_ci").toString();
				}else{
					dest_key1 = cell.get("dest_enodebid").toString();
					dest_key2 = cell.get("dest_localcellid").toString();
				}
				/**
				 * 补偿小区永久降耗处理
				 */
				if(permanenceFlag){//有永久降耗
					//List<Map<String,Object>> makeUpList = tdOffSleepAreaSelDao.queryMetaData(this.getQuerySql(model.getQuerySql(), monthAgo, dayAgo,dest_key1, dest_key2));
					List<Map<String,Object>> makeUpList = tdOffSleepAreaSelDao.queryMetaDataTest(model.getQuerySql(), monthAgo, dayAgo,dest_key1, dest_key2);

					if(makeUpList.isEmpty()||makeUpList.size()<=0){//没有补偿小区
						continue;
					}else if(makeUpList.get(0).containsValue(null)){
						continue;
					}
					Map<String, Map<String,Object>> makeUpMap = new HashMap<String, Map<String,Object>>();
					for(Map<String,Object> data: makeUpList){
						makeUpMap.put(DateUtil.format((Date)data.get("starttime")), data);
					}					
					
					boolean pFlag = true;
					for(String timeStr:interval15){				
						for(String dateStr: DateUtil.getRelateDays(nowTime, 30)){//日期列表，日期格式：yyyy-mm-dd					
							if(makeUpMap.containsKey(timeStr)){
								//永久降耗指标判断
								if(isT2G){
									if(pFlag && !this.gsmPerformance(tdMap.get(dateStr+ " "+timeStr), gsmDic, true)){
										pFlag = false;
									}
								}else{
									if(pFlag && !this.ltePerformance(tdMap.get(dateStr+ " "+timeStr), lteDic, true)){
										pFlag = false;
									}
								}								
							}
						}
					}	
						
					if(pFlag){
						p_sleepArea.add(cell);
					}					
				}
				
				/**
				 * 补偿小区静态降耗处理
				 */
				if(!timeRangeList.isEmpty() && timeRangeList.size()>0){//有静态降耗,按休眠小区筛选的时段查询补偿小区
					List<Map<String,String>> timeList = new ArrayList<Map<String,String>>();
					for(Map<String,String> rangeMap:timeRangeList){
						//List<Map<String,Object>> makeUpList = tdOffSleepAreaSelDao.queryMetaData(this.getQuerySql(model.getQuerySql(), rangeMap.get("sTime"), rangeMap.get("eTime"), dest_key1, dest_key2));						
						List<Map<String,Object>> makeUpList = tdOffSleepAreaSelDao.queryMetaDataTest(model.getQuerySql(), rangeMap.get("sTime"), rangeMap.get("eTime"), dest_key1, dest_key2);						

						if(makeUpList.isEmpty()||makeUpList.size()<=0){//没有补偿小区
							continue;
						}else if(makeUpList.get(0).containsValue(null)){
							continue;
						}
						Map<String,String> makeUpTimeMap = new HashMap<String,String>();
						
						Map<String, Map<String,Object>> makeUpMap = new HashMap<String, Map<String,Object>>();
						for(Map<String,Object> data: makeUpList){
							makeUpMap.put(DateUtil.format((Date)data.get("starttime")), data);
						}
						
						List<String> makeUp_interval15 = DateUtil.getIntervalTime(rangeMap.get("sTime"), rangeMap.get("eTime"), 15);
						Map<String,Integer> makeUpRange = new HashMap<String,Integer>();
						for(String timeStr:makeUp_interval15){				
							for(String dateStr: DateUtil.getRelateDays(nowTime, 30)){//日期列表，日期格式：yyyy-mm-dd					
								if(makeUpMap.containsKey(dateStr+ " "+timeStr)){
									/**
									 * 判断性能指标
									 */
									if(isT2G){
										if(this.gsmPerformance(makeUpMap.get(dateStr+ " "+timeStr), gsmDic, false)){
											if(!makeUpRange.containsKey(timeStr)){
												makeUpRange.put(timeStr, 1);
											}else{
												int cnt = makeUpRange.get(timeStr);
												makeUpRange.put(timeStr, cnt+1);
											}
										}
									}else{
										if(this.ltePerformance(makeUpMap.get(dateStr+ " "+timeStr), lteDic, false)){
											if(!makeUpRange.containsKey(timeStr)){
												makeUpRange.put(timeStr, 1);
											}else{
												int cnt = makeUpRange.get(timeStr);
												makeUpRange.put(timeStr, cnt+1);
											}
										}
									}									
									
								}else{//当Map中不存在，默认满足阀值
									if(makeUpRange.containsKey(timeStr)){
										int cnt = makeUpRange.get(timeStr);
										makeUpRange.put(timeStr, cnt+1);
									}else{
										makeUpRange.put(timeStr, 1);
									}									
								}
							}
							/**
							 * 按每15min时刻表分组后的数据，
							 * 当计数=30，则说明30天此15min数据都满足门限；
							 * 将满足门限的时刻记录，进行连续时间>=6h判断
							 */
							if(makeUpRange.get(timeStr) == 30 && !timeMap.containsKey("sTime")){
								timeMap.put("sTime", timeStr);
							}else if(makeUpRange.get(timeStr) == 30 && timeMap.containsKey("sTime")){
								timeMap.put("lastTime", timeStr);
							}else if(makeUpRange.get(timeStr) < 30){
								if(timeMap.containsKey("lastTime")){
									timeMap.put("eTime", timeMap.get("lastTime"));
								}else{
									timeMap = new HashMap<String,String>();//清空Map
								}								
							}
							if(timeList !=null && timeList.size()>0){
								//判断时间段>=6h
								timeList = DateUtil.exceed6Hour(timeList);
								for(Map<String,String> map:timeList){
									cell.put("sTime", map.get("sTime"));
									cell.put("eTime", map.get("eTime"));
									s_sleepArea.add(cell);	
								}
							}	
						}
					}
				}				
			}			
			logger.info("--------补偿小区判断结束--------");							
		}
		
		//写文件，将静态、永久小区信息写到不同的文件中		
		List<TdOffSleepSelectModel> dataList = tdOffSleepAreaSelDao.querySleepAreaSelSet(busytype, 1);
		TdOffSleepSelectModel model = dataList.get(0);		
		PrintStream p_ps = null;
		PrintStream s_ps = null;
		try{
			String p_fileName = rootPath+Constant.TD_PERMANENCE_FILE+busytype+System.currentTimeMillis()+CsvUtil.CSV_TYPE;//永久降耗小区文件
			String s_fileName = rootPath+Constant.TD_STATIC_FILE+busytype+System.currentTimeMillis()+CsvUtil.CSV_TYPE;//静态降耗小区文件
			logger.info("3G退网永久降耗小区生成的文件【"+p_fileName+"】");
			logger.info("3G退网静态降耗小区生成的文件【"+s_fileName+"】");
			
			p_ps = new PrintStream(p_fileName, CsvUtil.DEFAULT_CHARACTER_ENCODING);
			s_ps = new PrintStream(s_fileName, CsvUtil.DEFAULT_CHARACTER_ENCODING);
			logger.info("--------3G退网降耗小区写文件开始--------");
			
			if(!p_sleepArea.isEmpty() && p_sleepArea.size() > 0){				
				List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(model.getExportCols().split("\\|")[0]);
				//小区信息写入文件
				CsvUtil.writeRows(p_sleepArea, p_ps, colList);
			}
			if(!s_sleepArea.isEmpty() && s_sleepArea.size() > 0){				
				List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(model.getExportCols().split("\\|")[1]);
				//小区信息写入文件
				CsvUtil.writeRows(s_sleepArea, s_ps, colList);
			}	
			logger.info("--------3G退网降耗小区写文件结束--------");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(p_ps!=null) {
				p_ps.close();
			}
			if(s_ps!=null) {
				s_ps.close();
			}
		}		
	}

	
	/**
	 * 
	 * @param sql
	 * @param isStatic  是：取静态降耗门限  否：取永久降耗门限
	 * @param sTime  开始时间
	 * @param eTime  结束时间
	 * @param cellKey1  小区标识
	 * @param cellKey2 小区标识
	 * @return
	 */
	private String getQuerySql(String sql,String sTime, String eTime, String cellKey1, String cellKey2){
//		Matcher matcher = Pattern.compile("\\[.*?\\]",  Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(sql);
//		if(matcher.find()) {
//			String d = matcher.group();
//			sql = sql.replace(d, sql);
//		}
		sql = sql.replace("$STARTTIME$", sTime).replace("$ENDTIME$", eTime)
		  .replace("$CELLKEY1$", cellKey1).replace("$CELLKEY2$", cellKey2);
//		sql = sql.replace("$STARTTIME$", "?").replace("$ENDTIME$", "?")
//				  .replace("$CELLKEY1$", "?").replace("$CELLKEY2$", "?");

		return sql;
	}
	
	private String getQuerySql(String sql){
		return sql.replace("$STARTTIME$", "?").replace("$ENDTIME$", "?")
				  .replace("$CELLKEY1$", "?").replace("$CELLKEY2$", "?");
	}
	
	/**
	 * 判断TD小区性能指标是否满足门限
	 * @param data
	 * @param dic
	 * @param type true：永久门限  false:静态门限
	 * @return
	 */
	private boolean tdPerformance(Map<String,Object> data,List<Map<String, Object>> dic, boolean type){				
		double hzbpzs = FormatUtil.tranferCalValue(data.get("hzbpzs"));
		Map<String,Double> tdTHR = this.calTdDic(dic,hzbpzs);
		
		double yyyw = FormatUtil.tranferCalValue(data.get("yyyw"));
		double sjll = FormatUtil.tranferCalValue(data.get("sjll")); 
		double zdyhs = FormatUtil.tranferCalValue(data.get("zdyhs"));
		double mzylyl = FormatUtil.tranferCalValue(data.get("mzylyl"));
		if(type){
			//永久性能指标门限判断，语音话务量<=H1,数据业务量<=M1，码资源利用率<=K1，最大用户数<=N1
			if(yyyw<=tdTHR.get("p_hwlTHR")&&sjll<=tdTHR.get("p_sjywlTHR")
					&&zdyhs<=tdTHR.get("p_zdyhsTHR")&&mzylyl<=tdTHR.get("p_mzylylTHR")){
				return true;
			}
		}else{
			//静态性能指标门限判断，语音话务量<=H2,数据业务量<=M2，码资源利用率<=K2，最大用户数<=N2
			if(yyyw<=tdTHR.get("s_hwlTHR")&&sjll<=tdTHR.get("s_sjywlTHR")
					&&zdyhs<=tdTHR.get("s_zdyhsTHR")&&mzylyl<=tdTHR.get("s_mzylylTHR")){
				return true;
			}
		}		
		return false;
	} 
	/**
	 * 判断GSM小区性能指标是否满足门限
	 * @param data
	 * @param dic
	 * @param type true：永久门限  false:静态门限
	 * @return
	 */
	private boolean gsmPerformance(Map<String,Object> data,List<Map<String, Object>> dic, boolean type){				
		double tchxdcspz = FormatUtil.tranferCalValue(data.get("tchxdcspz"));
		Map<String,Double> gsmTHR = this.calGsmDic(dic,tchxdcspz);
		
		double mxhwl = FormatUtil.tranferCalValue(data.get("mxhwl"));
		double pdchczl = FormatUtil.tranferCalValue(data.get("pdchczl")); 
		if(type){
			//永久性能指标门限判断，每线话务量<M1,单PDCH承载效率<20kbps
			if(mxhwl<gsmTHR.get("p_mxhwlTHR")&&pdchczl<gsmTHR.get("p_pdchczlTHR")){
				return true;
			}
		}else{
			//永久性能指标门限判断，每线话务量<M2,单PDCH承载效率<20kbps
			if(mxhwl<gsmTHR.get("s_mxhwlTHR")&&pdchczl<gsmTHR.get("s_pdchczlTHR")){
				return true;
			}
		}		
		return false;
	}
	/**
	 * 判断LTE小区性能指标是否满足门限
	 * @param data
	 * @param dic
	 * @param type true：永久门限  false:静态门限
	 * @return
	 */
	private boolean ltePerformance(Map<String,Object> data,List<Map<String, Double>> dic, boolean type){		
		double sxsjll = FormatUtil.tranferCalValue(data.get("sxsjll"));
		double xxsjl = FormatUtil.tranferCalValue(data.get("xxsjll")); 
		double zdyhs = FormatUtil.tranferCalValue(data.get("zdyhs"));
		Map<String,Double> lteTHR = lteDic.get(0);
		if(type){
			//永久性能指标门限判断，上行数据流量<20M,下行数据流量<20M,最大用户数<40
			if(sxsjll<lteTHR.get("p_mxhwl")&&xxsjl<lteTHR.get("p_xxsjl")&&zdyhs<lteTHR.get("p_zdyhs")){
				return true;
			}
		}else{
			//静态性能指标门限判断，上行数据流量<20M,下行数据流量<20M,最大用户数<40
			if(sxsjll<lteTHR.get("s_mxhwl")&&xxsjl<lteTHR.get("s_xxsjl")&&zdyhs<lteTHR.get("s_zdyhs")){
				return true;
			}
		}		
		return false;
	}
	/**
	 * 当出现跨天的连续时间，则将时间进行合并
	 * @param timeRangeList
	 * @return
	 */
	private List<Map<String,String>> changeTimeRange(List<Map<String,String>> timeRangeList){
		if(timeRangeList.size()>1){
			Map<String,String> sMap = timeRangeList.get(0);
			Map<String,String> eMap = timeRangeList.get(timeRangeList.size()-1);

			if(sMap.get("sTime").equals("00:00:00") && eMap.get("eTime").equals("23:45:00")){
				String sTime = eMap.get("sTime");
				timeRangeList.remove(timeRangeList.size()-1);
				timeRangeList.get(0).put("sTime", sTime);			
			}
		}		
		return timeRangeList;
	}
	/**
	 * 根据H载波配置数获得阀值
	 * @param dic
	 * @param hzbpzs
	 * @return
	 */
	private Map<String, Double> calTdDic(List<Map<String,Object>> dic, double hzbpzs) {
		Map<String, Double> map = new HashMap<String, Double>();
		long trx = Math.round(hzbpzs);
		for(Map<String, Object> data:dic){
			int trx_max = (Integer)data.get("trx_h_max");
			int trx_min = (Integer)data.get("trx_h_min");
			if((trx==trx_max&&trx==trx_min) || (trx>=trx_min && trx<= trx_max)){
				map.put("p_zdyhsTHR", FormatUtil.tranferCalValue(data.get("p_zdyhs")));
				map.put("p_hwlTHR", FormatUtil.tranferCalValue(data.get("p_hwl")));
				map.put("p_sjywlTHR", FormatUtil.tranferCalValue(data.get("p_sjywl")));
				map.put("p_mzylylTHR", FormatUtil.tranferCalValue(data.get("p_mzylyl")));
				map.put("s_zdyhsTHR", FormatUtil.tranferCalValue(data.get("s_zdyhs")));
				map.put("s_hwlTHR", FormatUtil.tranferCalValue(data.get("s_hwl")));
				map.put("s_sjywlTHR", FormatUtil.tranferCalValue(data.get("s_sjywl")));
				map.put("s_mzylylTHR", FormatUtil.tranferCalValue(data.get("s_mzylyl")));
				return map;
			}
		}		
		return null;
	}
	
	/**
	 * 根据tch计算该gsm小区使用的门限字典
	 * @param dicGsmLists
	 * @return
	 */
	private Map<String, Double> calGsmDic(List<Map<String, Object>> dicGsmLists, double tch) {
		Map<String, Double> dicMap = new HashMap<String, Double>();
		for(Map<String, Object> dic: dicGsmLists) {
			double tch_min = FormatUtil.tranferCalValue(dic.get("tch_min"));
			double tch_max = Integer.MAX_VALUE;
			if(dic.get("tch_max")!=null) {
				tch_max = FormatUtil.tranferCalValue(dic.get("tch_max"));
			}
			if(tch>=tch_min&&tch<tch_max) {
				dicMap.put("p_mxhwlTHR", FormatUtil.tranferCalValue(dic.get("p_mxhwl")));
				dicMap.put("p_pdchczlTHR", FormatUtil.tranferCalValue(dic.get("p_pdchczl")));
				dicMap.put("s_mxhwlTHR", FormatUtil.tranferCalValue(dic.get("s_mxhwl")));
				dicMap.put("s_pdchczlTHR", FormatUtil.tranferCalValue(dic.get("s_pdchczl")));
				return dicMap;
			}
		}
		return null;
	}
}
