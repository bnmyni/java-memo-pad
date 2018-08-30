package com.tuoming.mes.strategy.service.thread;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.FcastNextDataDao;
import com.tuoming.mes.strategy.model.FcastNextIntervalSetting;

public class HisFacastNextThread implements Runnable {
	private Date collectTime;
	private FcastNextIntervalSetting fset;
	

	public HisFacastNextThread(Date collectTime, FcastNextIntervalSetting fset) {
		this.collectTime=collectTime;
		this.fset = fset;
	}


	@Override
	public void run() {
		FcastNextDataDao fcastNextDataDao = AppContext.getBean("fcastNextDataDao");
		fcastNextDataDao.removeResTable(Constant.NEXT_RES_PRE+fset.getSourceTable());
		fcastNextDataDao.createResTable(Constant.NEXT_RES_PRE+fset.getSourceTable(), fset.getQuerySql(), collectTime);
	}

}
