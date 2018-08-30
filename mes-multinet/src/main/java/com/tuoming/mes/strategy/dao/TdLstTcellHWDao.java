package com.tuoming.mes.strategy.dao;

import java.util.List;

import com.tuoming.mes.strategy.model.TdLstTcellHW;

public interface TdLstTcellHWDao {

	/**
	 * 查询cm_td_lst_tcell_hw
	 * 
	 * @param querySql
	 * @return
	 */
	List<TdLstTcellHW> queryList();
}
