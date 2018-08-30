package com.tuoming.mes.strategy.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import al.mid3.neusoft.DataPrediction;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.BigDataForecastDao;
import com.tuoming.mes.strategy.model.BigDataForecastSetting;
import com.tuoming.mes.strategy.service.BeforeAfterService;
import com.tuoming.mes.strategy.service.RBigDataForecastService;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;

@Service("rBigDataForecastService")
public class RBigDataForecastServiceImpl implements RBigDataForecastService {
	private static Logger logger = LogFacade.getLog4j(BeforeAfterService.class);
	// 查询需要根据多少天之前的数据进行预测
	private static final String appDays = ConfigurationManager
			.getDefaultConfig().getString("bigdata_days", "30");
	@Autowired
	@Qualifier(value = "bigDataForecastDao")
	private BigDataForecastDao bigDataForecastDao;
	
	@Autowired
	@Qualifier("businessLogDao")
	private BusinessLogDao businessLogDao;

	public void bigDataModel(String groupName, DataPrediction rBigDataPre) {
		businessLogDao.insertLog(8, "R语言预测建模开始", 0);
		List<BigDataForecastSetting> setList = bigDataForecastDao.queryForecastSet(groupName);
		
		//大数据建模开始前，清除上次建模数据
		rBigDataPre.end();
		//大数据建模开始
		rBigDataPre.start();

		for (BigDataForecastSetting set : setList) {
			// 可以补预测未预测的数据
			List<Date> foreTimeList = this.getForeTimeList(	set.getResTable(), new Date());
			for (Date forecastDate : foreTimeList) {
				// 作为存储数据时间
				String nextTime = this.getNextDay(forecastDate);
				
				// 获得查询语句中预测的时间范围
				String sqlTime = this.getForecastTime(forecastDate, appDays);
				List<Map<String, Object>> dataList = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),sqlTime));
				
				//预测时刻的前15分钟的时间点
				Date nowBefore15Min = this.getForeTime(forecastDate,1);
				// 获得查询语句中预测的时间范围
				String sqlTimeBf15 = this.getForecastTime(nowBefore15Min, appDays);
				List<Map<String, Object>> dataBf15minList = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),sqlTimeBf15));
				
				//预测时刻的前30分钟的时间点
				Date nowBefore30Min = this.getForeTime(forecastDate,2);
				// 获得查询语句中预测的时间范围
				String sqlTimeBf30 = this.getForecastTime(nowBefore30Min, appDays);
				List<Map<String, Object>> dataBf30minList = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),sqlTimeBf30));
				
				//预测时刻的前45分钟的时间点
				Date nowBefore45Min = this.getForeTime(forecastDate,3);
				// 获得查询语句中预测的时间范围
				String sqlTimeBf45 = this.getForecastTime(nowBefore45Min, appDays);
				List<Map<String, Object>> dataBf45minList = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),sqlTimeBf45));
				
				//预测时刻的前60分钟的时间点
				Date nowBefore60Min = this.getForeTime(forecastDate,4);
				// 获得查询语句中预测的时间范围
				String sqlTimeBf60 = this.getForecastTime(nowBefore60Min, appDays);
				List<Map<String, Object>> dataBf60minList = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),sqlTimeBf60));
				
				//大数据预测建模
				//预测GSM的场合
				if (set.getGroupName().equalsIgnoreCase(Constant.GSM)) {
					//预测GSM的HW场合
					if (set.getResTable().endsWith(Constant.VENDER_HW)) {
						this.gsmHWModel(dataList, dataBf15minList, dataBf30minList, dataBf45minList, dataBf60minList, set, rBigDataPre, nextTime);
					} else {
						this.gsmOtherModel(dataList, dataBf15minList, dataBf30minList, dataBf45minList, dataBf60minList, set, rBigDataPre, nextTime);
					}

				} else if (set.getGroupName().equalsIgnoreCase(Constant.TD)) {
					this.tdModel(dataList, dataBf15minList, dataBf30minList, dataBf45minList, dataBf60minList, set, rBigDataPre, nextTime);

				} else if (set.getGroupName().equalsIgnoreCase(Constant.LTE)) {
					this.lteModel(dataList, dataBf15minList, dataBf30minList, dataBf45minList, dataBf60minList, set, rBigDataPre, nextTime);
				}
			}
				
		}
		businessLogDao.insertLog(8, "R语言预测建模结束", 0);
	}
	
	public void bigDataForcast(String groupName, DataPrediction rBigDataPre) {
		businessLogDao.insertLog(8, "R语言预测下一时刻开始", 0);
		String filePath = CsvUtil.mkParentDir(Constant.PREFIX_LSB + System.currentTimeMillis());
		List<BigDataForecastSetting> setList = bigDataForecastDao.queryForecastSet(groupName);
		List<String> finalFile = new ArrayList<String>();// 存放文件名称

		for (BigDataForecastSetting set : setList) {
			//当前时刻的历史时间点
			Date forecastDate = this.getForeTime(new Date(),0);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nextDate = df.format(forecastDate);

			//当前前15分钟的时间点
			Date nowBefore15Min = this.getForeTime(forecastDate,1);
			
			// 获得查询语句中预测的时间范围
			List<Map<String, Object>> featrue15 = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),"'"+df.format(nowBefore15Min)+"'"));
			
			//当前前30分钟的时间点
			Date nowBefore30Min = this.getForeTime(forecastDate,2);
			// 获得查询语句中预测的时间范围
			List<Map<String, Object>> featrue30 = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),"'"+df.format(nowBefore30Min)+"'"));
			
			//当前前45分钟的时间点
			Date nowBefore45Min = this.getForeTime(forecastDate,3);
			// 获得查询语句中预测的时间范围
			List<Map<String, Object>> featrue45 = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),"'"+df.format(nowBefore45Min)+"'"));
			
			//当前前60分钟的时间点
			Date nowBefore60Min = this.getForeTime(forecastDate,4);
			// 获得查询语句中预测的时间范围
			List<Map<String, Object>> featrue60 = bigDataForecastDao.queryMetaData(this.getQuerySql(set.getQuerySql(),"'"+df.format(nowBefore60Min)+"'"));
			
			if (set.getGroupName().equalsIgnoreCase(Constant.GSM)) {
				
				if (set.getResTable().endsWith(Constant.VENDER_HW)) {

					String fileName = this.gsmForecastHW(featrue15, featrue30, featrue45, featrue60, set, filePath, nextDate, rBigDataPre);
					finalFile.add(fileName);
				} else {
					String fileName = this.gsmForecast(featrue15, featrue30, featrue45, featrue60, set, filePath, nextDate, rBigDataPre);
					finalFile.add(fileName);
				}
				this.loadFile(finalFile);
				finalFile.clear();

			} else if (set.getGroupName().equalsIgnoreCase(Constant.TD)) {
				String fileName = this.tdForecast(featrue15, featrue30, featrue45, featrue60, set, filePath, nextDate, rBigDataPre);
				finalFile.add(fileName);
				this.loadFile(finalFile);
				finalFile.clear();

			} else if (set.getGroupName().equalsIgnoreCase(Constant.LTE)) {
				String fileName = this.lteForecast(featrue15, featrue30, featrue45, featrue60, set, filePath, nextDate, rBigDataPre);
				finalFile.add(fileName);
				this.loadFile(finalFile);
				finalFile.clear();
			}
		}
		businessLogDao.insertLog(8, "R语言预测下一时刻结束", 0);
	}

	private void loadFile(List<String> loadFiles) {
		// 入库
		for (String fPath : loadFiles) {
			// logger.info("文件个数"+finalFile.size());
			if (fPath.endsWith(".tmp")) {
				continue;
			}
			File file = new File(fPath);
			String fileName = file.getName();
			String table = fileName.split("-")[0];
			String dbName = fileName.split("-")[1];

			try {
				DataAdapterPool.getDataAdapterPool(dbName).getDataAdapter()
						.loadfile(fPath, table);
			} catch (Exception e) {
				businessLogDao.insertLog(8, "预测数据入库出现异常，fileName["+fileName+"]", 1);
				logger.error(String.format("数据预测入库 %s 失败！", fileName),e);
			}
			logger.info(String.format("数据预测入库 %s 结束！", fileName));
		}
	}
	
	/**
	 * GSM-HW建模
	 * 
	 */
	private void gsmHWModel(List<Map<String, Object>> dataList, List<Map<String, Object>> dataBf15minList, List<Map<String, Object>> dataBf30minList,
			List<Map<String, Object>> dataBf45minList, List<Map<String, Object>> dataBf60minList, BigDataForecastSetting set, DataPrediction pre, String nextDay) {
		
		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("bsc")
					&& !colName.equalsIgnoreCase("starttime")
					&& !colName.equalsIgnoreCase("99999999")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
				
		String cellKey = "";
		// 单个小区每列当前时刻的N天集合
		Map<String, List<Double>> columnList_nowMap = new HashMap<String, List<Double>>();
		// 单个小区每列前15分钟的N天集合
		Map<String, List<Double>> columnList_bf15Map = new HashMap<String, List<Double>>();
		// 单个小区每列前30分钟的N天集合
		Map<String, List<Double>> columnList_bf30Map = new HashMap<String, List<Double>>();
		// 单个小区每列前45分钟的N天集合
		Map<String, List<Double>> columnList_bf45Map = new HashMap<String, List<Double>>();
		// 单个小区每列前60分钟的N天集合
		Map<String, List<Double>> columnList_bf60Map = new HashMap<String, List<Double>>();
		// 查询返回结果List<Map<一条数据值>>
		for (int a = 0; a<dataList.size(); a++) {
			Map<String, Object> nowMap = dataList.get(a);
			Map<String, Object> bf15Map = dataBf15minList.get(a);
			Map<String, Object> bf30Map = dataBf30minList.get(a);
			Map<String, Object> bf45Map = dataBf45minList.get(a);
			Map<String, Object> bf60Map = dataBf60minList.get(a);
						
			//数据库按bsc，99999999排序，发生变化代表新小区数据的开始
			if (!cellKey.equals(nowMap.get("bsc") + "@" + nowMap.get("99999999"))) {
				//如果不等于空，代表之前有小区的数据被整理，开始建模
				if (!"".equals(cellKey)) {
					for (String columnKey : colNameList) {
						List<Double> nowDataArray = columnList_nowMap.get(columnKey);
						List<Double> bf15DataArray = columnList_bf15Map.get(columnKey);
						List<Double> bf30DataArray = columnList_bf30Map.get(columnKey);
						List<Double> bf45DataArray = columnList_bf45Map.get(columnKey);
						List<Double> bf60DataArray = columnList_bf60Map.get(columnKey);
						if (nowDataArray.size() > 2) {
							//建模的key为预测时间 + @ + bsc + "@" + "99999999"
							pre.TrainModel( nextDay + "@" + cellKey, this.listToArray(nowDataArray), this.listToArray(bf60DataArray),
									this.listToArray(bf45DataArray), this.listToArray(bf30DataArray), this.listToArray(bf15DataArray), nowDataArray.size());
						}
					}
					//清空集合
					columnList_nowMap.clear();
					columnList_bf15Map.clear();
					columnList_bf30Map.clear();
					columnList_bf45Map.clear();
					columnList_bf60Map.clear();
				}

				cellKey = nowMap.get("bsc") + "@" + nowMap.get("99999999");
			}
			// 遍历一条数据的每一列
			for (String columnKey : nowMap.keySet()) {
				if ("bsc".equalsIgnoreCase(columnKey)
						|| "starttime".equalsIgnoreCase(columnKey)
						|| "99999999".equalsIgnoreCase(columnKey)
						|| "batch_id".equalsIgnoreCase(columnKey)) {
					continue;
				}
				if (!columnList_nowMap.containsKey(columnKey)) {
					columnList_nowMap.put(columnKey, new ArrayList<Double>());
					columnList_bf15Map.put(columnKey, new ArrayList<Double>());
					columnList_bf30Map.put(columnKey, new ArrayList<Double>());
					columnList_bf45Map.put(columnKey, new ArrayList<Double>());
					columnList_bf60Map.put(columnKey, new ArrayList<Double>());
				}
				columnList_nowMap.get(columnKey).add(Double.parseDouble(nowMap.get(columnKey).toString()));
				columnList_bf15Map.get(columnKey).add(Double.parseDouble(bf15Map.get(columnKey).toString()));
				columnList_bf30Map.get(columnKey).add(Double.parseDouble(bf30Map.get(columnKey).toString()));
				columnList_bf45Map.get(columnKey).add(Double.parseDouble(bf45Map.get(columnKey).toString()));
				columnList_bf60Map.get(columnKey).add(Double.parseDouble(bf60Map.get(columnKey).toString()));
			}

		}
		
	}
	
	/**
	 * GSM-其他厂商的建模
	 * 
	 */
	private void gsmOtherModel(List<Map<String, Object>> dataList, List<Map<String, Object>> dataBf15minList, List<Map<String, Object>> dataBf30minList,
			List<Map<String, Object>> dataBf45minList, List<Map<String, Object>> dataBf60minList, BigDataForecastSetting set, DataPrediction pre, String nextDay) {
		
		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("bscid")
					&& !colName.equalsIgnoreCase("period")
					&& !colName.equalsIgnoreCase("rpttime")
					&& !colName.equalsIgnoreCase("cellitem")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
				
		String cellKey = "";
		// 单个小区每列当前时刻的N天集合
		Map<String, List<Double>> columnList_nowMap = new HashMap<String, List<Double>>();
		// 单个小区每列前15分钟的N天集合
		Map<String, List<Double>> columnList_bf15Map = new HashMap<String, List<Double>>();
		// 单个小区每列前30分钟的N天集合
		Map<String, List<Double>> columnList_bf30Map = new HashMap<String, List<Double>>();
		// 单个小区每列前45分钟的N天集合
		Map<String, List<Double>> columnList_bf45Map = new HashMap<String, List<Double>>();
		// 单个小区每列前60分钟的N天集合
		Map<String, List<Double>> columnList_bf60Map = new HashMap<String, List<Double>>();
		// 查询返回结果List<Map<一条数据值>>
		for (int a = 0; a<dataList.size(); a++) {
			Map<String, Object> nowMap = dataList.get(a);
			Map<String, Object> bf15Map = dataBf15minList.get(a);
			Map<String, Object> bf30Map = dataBf30minList.get(a);
			Map<String, Object> bf45Map = dataBf45minList.get(a);
			Map<String, Object> bf60Map = dataBf60minList.get(a);
						
			//数据库按bscid，cellitem排序，发生变化代表新小区数据的开始
			if (!cellKey.equals(nowMap.get("bscid") + "@" + nowMap.get("cellitem"))) {
				//如果不等于空，代表之前有小区的数据被整理，开始建模
				if (!"".equals(cellKey)) {
					for (String columnKey : colNameList) {
						List<Double> nowDataArray = columnList_nowMap.get(columnKey);
						List<Double> bf15DataArray = columnList_bf15Map.get(columnKey);
						List<Double> bf30DataArray = columnList_bf30Map.get(columnKey);
						List<Double> bf45DataArray = columnList_bf45Map.get(columnKey);
						List<Double> bf60DataArray = columnList_bf60Map.get(columnKey);
						if (nowDataArray.size() > 2) {
							//建模的key为预测时间 + @ + bscid + "@" + "cellitem"
							pre.TrainModel( nextDay + "@" + cellKey, this.listToArray(nowDataArray), this.listToArray(bf60DataArray),
									this.listToArray(bf45DataArray), this.listToArray(bf30DataArray), this.listToArray(bf15DataArray), nowDataArray.size());
						}
					}
					//清空集合
					columnList_nowMap.clear();
					columnList_bf15Map.clear();
					columnList_bf30Map.clear();
					columnList_bf45Map.clear();
					columnList_bf60Map.clear();
				}

				cellKey = nowMap.get("bscid") + "@" + nowMap.get("cellitem");
			}
			// 遍历一条数据的每一列
			for (String columnKey : nowMap.keySet()) {
				if ("rpttime".equalsIgnoreCase(columnKey)
						|| "bscid".equalsIgnoreCase(columnKey)
						|| "cellitem".equalsIgnoreCase(columnKey)
						|| "period".equalsIgnoreCase(columnKey)
						|| "batch_id".equalsIgnoreCase(columnKey)) {
					continue;
				}
				if (!columnList_nowMap.containsKey(columnKey)) {
					columnList_nowMap.put(columnKey, new ArrayList<Double>());
					columnList_bf15Map.put(columnKey, new ArrayList<Double>());
					columnList_bf30Map.put(columnKey, new ArrayList<Double>());
					columnList_bf45Map.put(columnKey, new ArrayList<Double>());
					columnList_bf60Map.put(columnKey, new ArrayList<Double>());
				}
				columnList_nowMap.get(columnKey).add(Double.parseDouble(nowMap.get(columnKey).toString()));
				columnList_bf15Map.get(columnKey).add(Double.parseDouble(bf15Map.get(columnKey).toString()));
				columnList_bf30Map.get(columnKey).add(Double.parseDouble(bf30Map.get(columnKey).toString()));
				columnList_bf45Map.get(columnKey).add(Double.parseDouble(bf45Map.get(columnKey).toString()));
				columnList_bf60Map.get(columnKey).add(Double.parseDouble(bf60Map.get(columnKey).toString()));
			}

		}
		
	}
	
	/**
	 * TD-建模
	 * 
	 */
	private void tdModel(List<Map<String, Object>> dataList, List<Map<String, Object>> dataBf15minList, List<Map<String, Object>> dataBf30minList,
			List<Map<String, Object>> dataBf45minList, List<Map<String, Object>> dataBf60minList, BigDataForecastSetting set, DataPrediction pre, String nextDay) {
		
		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("starttime")
					&& !colName.equalsIgnoreCase("rnc")
					&& !colName.equalsIgnoreCase("dn")
					&& !colName.equalsIgnoreCase("managedelement")
					&& !colName.equalsIgnoreCase("rncfunction")
					&& !colName.equalsIgnoreCase("utrancell")
					&& !colName.equalsIgnoreCase("userlabel")
					&& !colName.equalsIgnoreCase("subnetwork")
					&& !colName.equalsIgnoreCase("begintime")
					&& !colName.equalsIgnoreCase("subnetwork1")
					&& !colName.equalsIgnoreCase("dc")
					&& !colName.equalsIgnoreCase("elementtype")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
				
		String cellKey = "";
		// 单个小区每列当前时刻的N天集合
		Map<String, List<Double>> columnList_nowMap = new HashMap<String, List<Double>>();
		// 单个小区每列前15分钟的N天集合
		Map<String, List<Double>> columnList_bf15Map = new HashMap<String, List<Double>>();
		// 单个小区每列前30分钟的N天集合
		Map<String, List<Double>> columnList_bf30Map = new HashMap<String, List<Double>>();
		// 单个小区每列前45分钟的N天集合
		Map<String, List<Double>> columnList_bf45Map = new HashMap<String, List<Double>>();
		// 单个小区每列前60分钟的N天集合
		Map<String, List<Double>> columnList_bf60Map = new HashMap<String, List<Double>>();
		// 查询返回结果List<Map<一条数据值>>
		for (int a = 0; a<dataList.size(); a++) {
			Map<String, Object> nowMap = dataList.get(a);
			Map<String, Object> bf15Map = dataBf15minList.get(a);
			Map<String, Object> bf30Map = dataBf30minList.get(a);
			Map<String, Object> bf45Map = dataBf45minList.get(a);
			Map<String, Object> bf60Map = dataBf60minList.get(a);
						
			//发生变化代表新小区数据的开始
			String currentCellKey = nowMap.get("rnc") + "@"+ nowMap.get("utrancell") + "@"+ nowMap.get("managedelement");
			if (nowMap.containsKey("userlabel")) {
				currentCellKey = currentCellKey + "@"+ nowMap.get("userlabel");
			}
			if (!cellKey.equals(currentCellKey)) {
				//如果不等于空，代表之前有小区的数据被整理，开始建模
				if (!"".equals(cellKey)) {
					for (String columnKey : colNameList) {
						List<Double> nowDataArray = columnList_nowMap.get(columnKey);
						List<Double> bf15DataArray = columnList_bf15Map.get(columnKey);
						List<Double> bf30DataArray = columnList_bf30Map.get(columnKey);
						List<Double> bf45DataArray = columnList_bf45Map.get(columnKey);
						List<Double> bf60DataArray = columnList_bf60Map.get(columnKey);
						if (nowDataArray.size() > 2) {
							//建模的key为预测时间 + @ + cellKey
							pre.TrainModel( nextDay + "@" + cellKey, this.listToArray(nowDataArray), this.listToArray(bf60DataArray),
									this.listToArray(bf45DataArray), this.listToArray(bf30DataArray), this.listToArray(bf15DataArray), nowDataArray.size());
						}
					}
					//清空集合
					columnList_nowMap.clear();
					columnList_bf15Map.clear();
					columnList_bf30Map.clear();
					columnList_bf45Map.clear();
					columnList_bf60Map.clear();
				}

				cellKey = nowMap.get("rnc") + "@" + nowMap.get("utrancell") + "@" + nowMap.get("managedelement");
				if (nowMap.containsKey("userlabel")) {
					cellKey = cellKey + "@"	+ nowMap.get("userlabel");
				}
			}
			// 遍历一条数据的每一列
			for (String columnKey : nowMap.keySet()) {
				if ("starttime".equalsIgnoreCase(columnKey)
						|| "rnc".equalsIgnoreCase(columnKey)
						|| "dn".equalsIgnoreCase(columnKey)
						|| "managedelement".equalsIgnoreCase(columnKey)
						|| "rncfunction".equalsIgnoreCase(columnKey)
						|| "utrancell".equalsIgnoreCase(columnKey)
						|| "userlabel".equalsIgnoreCase(columnKey)
						|| "subnetwork".equalsIgnoreCase(columnKey)
						|| "begintime".equalsIgnoreCase(columnKey)
						|| "subnetwork1".equalsIgnoreCase(columnKey)
						|| "dc".equalsIgnoreCase(columnKey)
						|| "elementtype".equalsIgnoreCase(columnKey)
						|| "batch_id".equalsIgnoreCase(columnKey)) {
					continue;
				}
				if (!columnList_nowMap.containsKey(columnKey)) {
					columnList_nowMap.put(columnKey, new ArrayList<Double>());
					columnList_bf15Map.put(columnKey, new ArrayList<Double>());
					columnList_bf30Map.put(columnKey, new ArrayList<Double>());
					columnList_bf45Map.put(columnKey, new ArrayList<Double>());
					columnList_bf60Map.put(columnKey, new ArrayList<Double>());
				}
				columnList_nowMap.get(columnKey).add(Double.parseDouble(nowMap.get(columnKey).toString()));
				columnList_bf15Map.get(columnKey).add(Double.parseDouble(bf15Map.get(columnKey).toString()));
				columnList_bf30Map.get(columnKey).add(Double.parseDouble(bf30Map.get(columnKey).toString()));
				columnList_bf45Map.get(columnKey).add(Double.parseDouble(bf45Map.get(columnKey).toString()));
				columnList_bf60Map.get(columnKey).add(Double.parseDouble(bf60Map.get(columnKey).toString()));
			}

		}
		
	}
	
	/**
	 * LTE-建模
	 * 
	 */
	private void lteModel(List<Map<String, Object>> dataList, List<Map<String, Object>> dataBf15minList, List<Map<String, Object>> dataBf30minList,
			List<Map<String, Object>> dataBf45minList, List<Map<String, Object>> dataBf60minList, BigDataForecastSetting set, DataPrediction pre, String nextDay) {
		
		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("starttime")
					&& !colName.equalsIgnoreCase("dn")
					&& !colName.equalsIgnoreCase("subnetwork")
					&& !colName.equalsIgnoreCase("subnetwork2")
					&& !colName.equalsIgnoreCase("managedelement")
					&& !colName.equalsIgnoreCase("enbfunction")
					&& !colName.equalsIgnoreCase("eutrancelltdd")
					&& !colName.equalsIgnoreCase("userlabel")
					&& !colName.equalsIgnoreCase("localcellid")
					&& !colName.equalsIgnoreCase("server_name")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
				
		String cellKey = "";
		// 单个小区每列当前时刻的N天集合
		Map<String, List<Double>> columnList_nowMap = new HashMap<String, List<Double>>();
		// 单个小区每列前15分钟的N天集合
		Map<String, List<Double>> columnList_bf15Map = new HashMap<String, List<Double>>();
		// 单个小区每列前30分钟的N天集合
		Map<String, List<Double>> columnList_bf30Map = new HashMap<String, List<Double>>();
		// 单个小区每列前45分钟的N天集合
		Map<String, List<Double>> columnList_bf45Map = new HashMap<String, List<Double>>();
		// 单个小区每列前60分钟的N天集合
		Map<String, List<Double>> columnList_bf60Map = new HashMap<String, List<Double>>();
		// 查询返回结果List<Map<一条数据值>>
		for (int a = 0; a<dataList.size(); a++) {
			Map<String, Object> nowMap = dataList.get(a);
			Map<String, Object> bf15Map = dataBf15minList.get(a);
			Map<String, Object> bf30Map = dataBf30minList.get(a);
			Map<String, Object> bf45Map = dataBf45minList.get(a);
			Map<String, Object> bf60Map = dataBf60minList.get(a);
						
			//发生变化代表新小区数据的开始
			String currentCellKey = nowMap.get("managedelement") + "@" + nowMap.get("eutrancelltdd") + "@" + nowMap.get("subnetwork");
			if (nowMap.containsKey("localcellid")) {
				currentCellKey = currentCellKey + "@" + nowMap.get("localcellid");
			}
			if (!cellKey.equals(currentCellKey)) {
				//如果不等于空，代表之前有小区的数据被整理，开始建模
				if (!"".equals(cellKey)) {
					for (String columnKey : colNameList) {
						List<Double> nowDataArray = columnList_nowMap.get(columnKey);
						List<Double> bf15DataArray = columnList_bf15Map.get(columnKey);
						List<Double> bf30DataArray = columnList_bf30Map.get(columnKey);
						List<Double> bf45DataArray = columnList_bf45Map.get(columnKey);
						List<Double> bf60DataArray = columnList_bf60Map.get(columnKey);
						if (nowDataArray.size() > 2) {
							//建模的key为预测时间 + @ + cellKey
							pre.TrainModel( nextDay + "@" + cellKey, this.listToArray(nowDataArray), this.listToArray(bf60DataArray),
									this.listToArray(bf45DataArray), this.listToArray(bf30DataArray), this.listToArray(bf15DataArray), nowDataArray.size());
						}
					}
					//清空集合
					columnList_nowMap.clear();
					columnList_bf15Map.clear();
					columnList_bf30Map.clear();
					columnList_bf45Map.clear();
					columnList_bf60Map.clear();
				}

				cellKey = nowMap.get("managedelement") + "@" + nowMap.get("eutrancelltdd") + "@" + nowMap.get("subnetwork");
				if (nowMap.containsKey("localcellid")) {
					cellKey = cellKey + "@" + nowMap.get("localcellid");
				}
			}
			// 遍历一条数据的每一列
			for (String columnKey : nowMap.keySet()) {
				if ("starttime".equalsIgnoreCase(columnKey)
						|| "dn".equalsIgnoreCase(columnKey)
						|| "subnetwork".equalsIgnoreCase(columnKey)
						|| "subnetwork2".equalsIgnoreCase(columnKey)
						|| "managedelement".equalsIgnoreCase(columnKey)
						|| "enbfunction".equalsIgnoreCase(columnKey)
						|| "eutrancelltdd".equalsIgnoreCase(columnKey)
						|| "userlabel".equalsIgnoreCase(columnKey)
						|| "localcellid".equalsIgnoreCase(columnKey)
						|| "server_name".equalsIgnoreCase(columnKey)
						|| "batch_id".equalsIgnoreCase(columnKey)) {
					continue;
				}
				if (!columnList_nowMap.containsKey(columnKey)) {
					columnList_nowMap.put(columnKey, new ArrayList<Double>());
					columnList_bf15Map.put(columnKey, new ArrayList<Double>());
					columnList_bf30Map.put(columnKey, new ArrayList<Double>());
					columnList_bf45Map.put(columnKey, new ArrayList<Double>());
					columnList_bf60Map.put(columnKey, new ArrayList<Double>());
				}
				columnList_nowMap.get(columnKey).add(Double.parseDouble(nowMap.get(columnKey).toString()));
				columnList_bf15Map.get(columnKey).add(Double.parseDouble(bf15Map.get(columnKey).toString()));
				columnList_bf30Map.get(columnKey).add(Double.parseDouble(bf30Map.get(columnKey).toString()));
				columnList_bf45Map.get(columnKey).add(Double.parseDouble(bf45Map.get(columnKey).toString()));
				columnList_bf60Map.get(columnKey).add(Double.parseDouble(bf60Map.get(columnKey).toString()));
			}

		}
		
	}

	/**
	 * GSM预测
	 * 
	 * @return
	 */
	private String gsmForecast(List<Map<String, Object>> featrue15, List<Map<String, Object>> featrue30, List<Map<String, Object>> featrue45,
			List<Map<String, Object>> featrue60, BigDataForecastSetting set, String filePath, String nextDay, DataPrediction pre) {
		String targetFile = filePath
				+ set.getResTable()
				+ "-"
				+ set.getDbName()
				+ "-"
				+ DateUtil.format(DateUtil.tranStrToDate(nextDay),
						"yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
		PrintStream ps = null;

		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("bscid")
					&& !colName.equalsIgnoreCase("period")
					&& !colName.equalsIgnoreCase("rpttime")
					&& !colName.equalsIgnoreCase("cellitem")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
		try {
			ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
			// 查询返回结果List<Map<一条数据值>>
			for (int a = 0; a<featrue15.size(); a++) {
				Map<String, Object> bf15Map = featrue15.get(a);
				Map<String, Object> bf30Map = featrue30.get(a);
				Map<String, Object> bf45Map = featrue45.get(a);
				Map<String, Object> bf60Map = featrue60.get(a);
				
				String cellKey = bf15Map.get("bscid") + "@" + bf15Map.get("cellitem");
				
				Map<String, Object> oneDataMap = new HashMap<String, Object>();
				oneDataMap.put("rpttime", nextDay);// 下一天上一时段时间 yyyy-mm-dd HH:MM:SS
				oneDataMap.put("bscid", cellKey.split("@")[0]);
				oneDataMap.put("cellitem", cellKey.split("@")[1]);
				
				for (String columnKey : colNameList) {
					Double f60 = Double.parseDouble(bf60Map.get(columnKey).toString());
					Double f45 = Double.parseDouble(bf45Map.get(columnKey).toString());
					Double f30 = Double.parseDouble(bf30Map.get(columnKey).toString());
					Double f15 = Double.parseDouble(bf15Map.get(columnKey).toString());
					
					double dataNext = pre.Predict(nextDay + "@" + cellKey, f60, f45, f30, f15, 0.4);
					oneDataMap.put(columnKey, dataNext);
				}
				
				// 添加单条数据
				CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
			}
		
		} catch (FileNotFoundException e) {
			businessLogDao.insertLog(8, "GSM数据预测出现异常", 1);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			businessLogDao.insertLog(8, "GSM数据预测出现异常", 1);
			e.printStackTrace();
		} finally {
			ps.close();
		}
		return targetFile;
	}

	/**
	 * GSM-HW预测
	 * 
	 */
	private String gsmForecastHW(List<Map<String, Object>> featrue15, List<Map<String, Object>> featrue30, List<Map<String, Object>> featrue45,
			List<Map<String, Object>> featrue60, BigDataForecastSetting set, String filePath, String nextDay, DataPrediction pre) {
		String targetFile = filePath
				+ set.getResTable()
				+ "-"
				+ set.getDbName()
				+ "-"
				+ DateUtil.format(DateUtil.tranStrToDate(nextDay),
						"yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
		PrintStream ps = null;

		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("bsc")
					&& !colName.equalsIgnoreCase("starttime")
					&& !colName.equalsIgnoreCase("99999999")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
		try {
			ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
			// 查询返回结果List<Map<一条数据值>>
			for (int a = 0; a<featrue15.size(); a++) {
				Map<String, Object> bf15Map = featrue15.get(a);
				Map<String, Object> bf30Map = featrue30.get(a);
				Map<String, Object> bf45Map = featrue45.get(a);
				Map<String, Object> bf60Map = featrue60.get(a);
				
				String cellKey = bf15Map.get("bsc") + "@" + bf15Map.get("99999999");
				
				Map<String, Object> oneDataMap = new HashMap<String, Object>();
				oneDataMap.put("starttime", nextDay);// 时段时间 yyyy-mm-dd HH:MM:SS
				oneDataMap.put("bsc", cellKey.split("@")[0]);
				oneDataMap.put("99999999", cellKey.split("@")[1]);
				
				for (String columnKey : colNameList) {
					Double f60 = Double.parseDouble(bf60Map.get(columnKey).toString());
					Double f45 = Double.parseDouble(bf45Map.get(columnKey).toString());
					Double f30 = Double.parseDouble(bf30Map.get(columnKey).toString());
					Double f15 = Double.parseDouble(bf15Map.get(columnKey).toString());
					
					double dataNext = pre.Predict(nextDay + "@" + cellKey, f60, f45, f30, f15, 0.4);
					oneDataMap.put(columnKey, dataNext);
				}
				
				// 添加单条数据
				CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
			}	
		} catch (FileNotFoundException e) {
			businessLogDao.insertLog(8, "华为GSM R语言预测出现异常", 1);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			businessLogDao.insertLog(8, "华为GSM R语言预测出现异常", 1);
			e.printStackTrace();
		} finally {
			ps.close();
			pre.end();
		}
		return targetFile;
	}

	/**
	 * TD预测
	 * 
	 * @return
	 */
	private String tdForecast(List<Map<String, Object>> featrue15, List<Map<String, Object>> featrue30, List<Map<String, Object>> featrue45,
			List<Map<String, Object>> featrue60, BigDataForecastSetting set, String filePath, String nextDay, DataPrediction pre) {
		String targetFile = filePath
				+ set.getResTable()
				+ "-"
				+ set.getDbName()
				+ "-"
				+ DateUtil.format(DateUtil.tranStrToDate(nextDay),
						"yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
		PrintStream ps = null;

		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {// 封装不需要预测的列
			if (!colName.equalsIgnoreCase("starttime")
					&& !colName.equalsIgnoreCase("rnc")
					&& !colName.equalsIgnoreCase("dn")
					&& !colName.equalsIgnoreCase("managedelement")
					&& !colName.equalsIgnoreCase("rncfunction")
					&& !colName.equalsIgnoreCase("utrancell")
					&& !colName.equalsIgnoreCase("userlabel")
					&& !colName.equalsIgnoreCase("subnetwork")
					&& !colName.equalsIgnoreCase("begintime")
					&& !colName.equalsIgnoreCase("subnetwork1")
					&& !colName.equalsIgnoreCase("dc")
					&& !colName.equalsIgnoreCase("elementtype")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
		
		try {
			ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
			// 查询返回结果List<Map<一条数据值>>
			for (int a = 0; a<featrue15.size(); a++) {
				Map<String, Object> bf15Map = featrue15.get(a);
				Map<String, Object> bf30Map = featrue30.get(a);
				Map<String, Object> bf45Map = featrue45.get(a);
				Map<String, Object> bf60Map = featrue60.get(a);
				
				Map<String, Object> oneDataMap = new HashMap<String, Object>();
				oneDataMap.put("starttime", nextDay);// 下一天上一时段时间  yyyy-mm-dd HH:MM:SS
				oneDataMap.put("rnc", bf15Map.get("rnc"));
				oneDataMap.put("utrancell", bf15Map.get("utrancell"));
				oneDataMap.put("managedelement", bf15Map.get("managedelement"));
				
				String cellKey = bf15Map.get("rnc") + "@" + bf15Map.get("utrancell") + "@"	+ bf15Map.get("managedelement");
				if (bf15Map.containsKey("userlabel")) {
					cellKey = cellKey + "@"	+ bf15Map.get("userlabel");
					oneDataMap.put("userlabel", bf15Map.get("userlabel"));
				}
				
				for (String columnKey : colNameList) {
					Double f60 = Double.parseDouble(bf60Map.get(columnKey).toString());
					Double f45 = Double.parseDouble(bf45Map.get(columnKey).toString());
					Double f30 = Double.parseDouble(bf30Map.get(columnKey).toString());
					Double f15 = Double.parseDouble(bf15Map.get(columnKey).toString());
					
					double dataNext = pre.Predict(nextDay + "@" + cellKey, f60, f45, f30, f15, 0.4);
					oneDataMap.put(columnKey, dataNext);
				}
				
				// 添加单条数据
				CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
			}
		} catch (FileNotFoundException e) {
			businessLogDao.insertLog(8, "TD数据预测出现异常", 1);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			businessLogDao.insertLog(8, "TD数据预测出现异常", 1);
			e.printStackTrace();
		} finally {
			ps.close();
		}
		return targetFile;
	}

	/**
	 * LTE预测
	 * 
	 * @return
	 */
	private String lteForecast(List<Map<String, Object>> featrue15, List<Map<String, Object>> featrue30, List<Map<String, Object>> featrue45,
			List<Map<String, Object>> featrue60, BigDataForecastSetting set, String filePath, String nextDay, DataPrediction pre) {
		String targetFile = filePath
				+ set.getResTable()
				+ "-"
				+ set.getDbName()
				+ "-"
				+ DateUtil.format(DateUtil.tranStrToDate(nextDay),
						"yyyy_MM_dd_HH_mm_ss") + CsvUtil.CSV_TYPE;
		PrintStream ps = null;
		// 获得列名称
		List<String> colNameList = new ArrayList<String>();
		for (String colName : set.getColumnList()) {
			if (!colName.equalsIgnoreCase("starttime")
					&& !colName.equalsIgnoreCase("dn")
					&& !colName.equalsIgnoreCase("subnetwork")
					&& !colName.equalsIgnoreCase("subnetwork2")
					&& !colName.equalsIgnoreCase("managedelement")
					&& !colName.equalsIgnoreCase("enbfunction")
					&& !colName.equalsIgnoreCase("eutrancelltdd")
					&& !colName.equalsIgnoreCase("userlabel")
					&& !colName.equalsIgnoreCase("localcellid")
					&& !colName.equalsIgnoreCase("server_name")
					&& !colName.equalsIgnoreCase("batch_id")) {
				colNameList.add(colName);
			}
		}
		
		try {
			ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);
			// 查询返回结果List<Map<一条数据值>>
			for (int a = 0; a<featrue15.size(); a++) {
				Map<String, Object> bf15Map = featrue15.get(a);
				Map<String, Object> bf30Map = featrue30.get(a);
				Map<String, Object> bf45Map = featrue45.get(a);
				Map<String, Object> bf60Map = featrue60.get(a);
				
				Map<String, Object> oneDataMap = new HashMap<String, Object>();
				oneDataMap.put("starttime", nextDay);// 下一天上一时段时间  yyyy-mm-dd HH:MM:SS
				oneDataMap.put("managedelement", bf15Map.get("managedelement"));
				oneDataMap.put("eutrancelltdd", bf15Map.get("eutrancelltdd"));
				oneDataMap.put("subnetwork", bf15Map.get("subnetwork"));
				
				String cellKey = bf15Map.get("managedelement") + "@" + bf15Map.get("eutrancelltdd") + "@"	+ bf15Map.get("subnetwork");
				if (bf15Map.containsKey("localcellid")) {
					cellKey = cellKey + "@"	+ bf15Map.get("localcellid");
					oneDataMap.put("localcellid", bf15Map.get("localcellid"));
				}
				
				for (String columnKey : colNameList) {
					Double f60 = Double.parseDouble(bf60Map.get(columnKey).toString());
					Double f45 = Double.parseDouble(bf45Map.get(columnKey).toString());
					Double f30 = Double.parseDouble(bf30Map.get(columnKey).toString());
					Double f15 = Double.parseDouble(bf15Map.get(columnKey).toString());
					
					double dataNext = pre.Predict(nextDay + "@" + cellKey, f60, f45, f30, f15, 0.4);
					oneDataMap.put(columnKey, dataNext);
				}
				
				// 添加单条数据
				CsvUtil.writeRow(oneDataMap, ps, set.getColumnList());
			}
		} catch (FileNotFoundException e) {
			businessLogDao.insertLog(8, "LTE数据预测出现异常", 1);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			businessLogDao.insertLog(8, "LTE数据预测出现异常", 1);
			e.printStackTrace();
		} finally {
			ps.close();
		}
		return targetFile;
	}

	/**
	 * 获得需要获取数据的时间集合，用于替换sql中的时间串
	 * 
	 * @param appDays
	 *            预测前多少天的天数
	 * @param forecastDate
	 *            预测的时刻
	 * @return
	 */
	private String getForecastTime(Date forecastDate, String appDays) {
		List<Date> dayList = DateUtil.getRelateDays(forecastDate,
				-Integer.parseInt(appDays));// 获得要预测的天数字符串
		String resTime = "";
		// String before15Min =
		// DateUtil.getMultiple15Min(forecastDate);//获得当前天前15min,HH:mm:SS
		String before15Min = DateUtil.format(forecastDate).substring(10);// 获得当前天前15min,HH:mm:SS
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder timeBuilder = new StringBuilder();
		for (Date day : dayList) {
			String dayStr = df.format(day).substring(0, 10);
			timeBuilder.append("'" + dayStr + before15Min + "',");
		}
		resTime = timeBuilder.toString();
		if (resTime.endsWith(",")) {
			resTime = resTime.substring(0, resTime.length() - 2);
		}
		resTime += "'";
		return resTime;
	}

	/**
	 * 获得需要预测的时刻集合 可以实现数据补预测，当出现未预测的时刻时，将返回多个待预测时刻，否则将只返回一个结果
	 * 
	 * @param table
	 *            根据那张表获得最后的预测时间
	 * @param nowDate
	 *            当前时间
	 * @return 待预测的时刻集合
	 */
	private Date getForeTime(Date nowDate, int i) {
		// 当前天的预测时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);
		cal.set(Calendar.SECOND, 0);// 采集时间不需要秒信息
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE,(cal.get(Calendar.MINUTE) / Constant.PM_COLLECT_LD)	* Constant.PM_COLLECT_LD);
		//从当前时刻向前取得5个时刻的时间戳
 		Date nowBefore15Min = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(cal.getTime(), 15*i));
		return nowBefore15Min;
	}

	/**
	 * 获得下一天前一个整15min时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param nowDate
	 * @return
	 */
	private String getNextDay(Date nowDate) {
		return DateUtil.format(DateUtil.getDelayDay(nowDate, 1));
	}

	private String getQuerySql(String sql, String timeStr) {
		return sql.replace("$TIMELINE$", timeStr);
	}

	/**
	 * 将list转为数组
	 * 
	 * @param list
	 * @return
	 */
	private double[] listToArray(List<Double> list) {
		double[] result = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = Double.parseDouble(list.get(i).toString());
		}
		return result;
	}
	
	
	/**
	 * 获得需要预测的时刻集合 可以实现数据补预测，当出现未预测的时刻时，将返回多个待预测时刻，否则将只返回一个结果
	 * 
	 * @param table
	 *            根据那张表获得最后的预测时间
	 * @param nowDate
	 *            当前时间
	 * @return 待预测的时刻集合
	 */
	private List<Date> getForeTimeList(String table, Date nowDate) {
		List<Date> resultList = new ArrayList<Date>();
		// 当前时间所属的时刻
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);
		cal.set(Calendar.SECOND, 0);// 采集时间不需要秒信息
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE,(cal.get(Calendar.MINUTE) / Constant.PM_COLLECT_LD)	* Constant.PM_COLLECT_LD);
		// 当前时刻的上刻钟
		Date nowBefore15Min = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(cal.getTime(), 15));
		
		// GSM除华为以外的表使用rpttime列
		String column = "starttime";
		if (table.split("_")[3].equalsIgnoreCase(Constant.GSM)	&& !table.endsWith(Constant.VENDER_HW)) {
			column = "rpttime";
		}
		// 查询最后预测到的时间，补数据时作为开始时间使用
		Date sqlTime = bigDataForecastDao.queryUniqueData(table, column);
		// 之前没有预测数据的场合下
		if (null == sqlTime || "".equals(sqlTime)) {
			// 上一时刻在0~6点的预测范围内的场合
			if(nowBefore15Min.getHours()>= 0 && nowBefore15Min.getHours()<=6){
				//取得当前时刻到7点间的所有时刻集合
				resultList = DateUtil.getTimeList(new Date(), nowBefore15Min.getHours(), 7, 15);
			} else{
				//获取下一天的0点开始到7点的所有时刻集合
				String befortime = DateUtil.getDay(1);
				resultList = DateUtil.getTimeList(DateUtil.tranStrToDate(befortime), 0, 7, 15);
			}
		} else {
			// 数据库预测的最后时间转为预测开始时间
			Date sqlBeforeDate = DateUtil.getBeforeDay(sqlTime);
			while (sqlBeforeDate.before(nowBefore15Min)) {
				sqlBeforeDate = DateUtil.tranStrToDate(DateUtil
						.getBeforeMinStr(sqlBeforeDate, -15));// 当前时间再取前15min
				if(sqlBeforeDate.getHours()>= 0 && sqlBeforeDate.getHours()<=6){
					resultList.add(sqlBeforeDate);
				}
			}
		}
		return resultList;
	}
	
	 public static void main(String[] args){
//	 List<Date> list = testList();
	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	 for(Date ddd:list){
//	 System.out.println(df.format(ddd));
//	 }
//		 System.out.println(df.format(getNextMin()));
	 }
}
