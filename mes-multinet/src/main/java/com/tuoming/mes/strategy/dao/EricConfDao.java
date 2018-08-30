package com.tuoming.mes.strategy.dao;

import java.util.List;

import com.tuoming.mes.strategy.model.BscNameConf;

public interface EricConfDao {

	/**
	 * 查询eric的bsc配置
	 * 
	 * @param querySql
	 * @return
	 */
	List<BscNameConf> queryEricBsc();
}
