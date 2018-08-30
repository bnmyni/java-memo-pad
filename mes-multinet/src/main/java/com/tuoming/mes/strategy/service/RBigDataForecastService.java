package com.tuoming.mes.strategy.service;

import al.mid3.neusoft.DataPrediction;

public interface RBigDataForecastService {
	/**
	 * R语言大数据建模
	 * @param groupName
	 */
	public void bigDataModel(String groupName, DataPrediction rBigDataPre);
	
	/**
	 * R语言大数据预测
	 * @param groupName
	 */
	public void bigDataForcast(String groupName, DataPrediction rBigDataPre);
}
