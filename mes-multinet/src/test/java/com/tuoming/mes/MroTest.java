package com.tuoming.mes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.strategy.service.MroCollectService;
import com.tuoming.mes.strategy.service.impl.MroCollectImpl;

public class MroTest {

	@Before
	public void setUp() throws Exception {
		LogFacade.initLogFacade("../conf/log4j.properties");
		ConfigurationManager.LIFE_CYCLE = ConfigurationManager.LIFECYCLE.WITHDB;
		ConfigurationManager.getDefaultConfig().openConfiguration("conf/hamster.xml");
	}

	@Test
	public void test() {
		MroCollectService mro=new MroCollectImpl();
		mro.exeLteHwLocalAnaly("D:/BJ_hannan/workspace_hn/ASK/LTE MR", "TD-LTE_MRO_HUAWEI_010184039165_491660_20151109201500");
	}

}
