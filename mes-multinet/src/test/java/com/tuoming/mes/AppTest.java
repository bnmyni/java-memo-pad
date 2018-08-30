package com.tuoming.mes;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.services.serve.MESManager;
import com.tuoming.mes.services.serve.SEBizService;

/**
 * Unit test for simple App.
 */
public class AppTest

{
	@Before
	public void setup() throws Exception {
		LogFacade.initLogFacade("../conf/log4j.properties");
		ConfigurationManager.LIFE_CYCLE = ConfigurationManager.LIFECYCLE.WITHDB;
		ConfigurationManager.getDefaultConfig().openConfiguration("conf/hamster.xml");
	}

	@Test
	public void testPool() throws Exception {
		
		

//		MESManager aos = new MESManager();
//		LogCommandService logQuery = (LogCommandService) aos.getService("LogCommandService");
//		aos.setEnv("serverName", "BSC1");
//
//		logQuery.parse("D:/multinet_nac/data/LST BSCDSTPA.txt", "HW_LST_BSCDSTPA");
//		FtpLogCommandService service = AppContext.getBean("FtpLogCommandService");
//		service.query("ERIC_GSM_BARFIL_PM");
//		MESManager aos = new MESManager();
//		FtpLogCommandService s = (FtpLogCommandService) aos.getService("FtpLogCommandService");
//		s.queryAll("pm", 1);
//		OverDegreeCalService kpiCal = AppContext.getBean("overDegreeCalService");
//		kpiCal.calculate(null);
//		HisDataFCastService kpiCal = AppContext.getBean("hisDataFCastService");
//		kpiCal.fCastNextData();
		
//		EnergyCellRefreshService s = AppContext.getBean("energyCellRefreshService");
//		s.refreshEnergyCellByMr();
//		s.refreshEnergyCellByAzimuth();
//		OverDegreeCalService s = AppContext.getBean("overDegreeCalService");
//		s.calculate(null);
		MESManager aos = new MESManager();
		SEBizService service= (SEBizService) aos.getBean("SEBizService");
		Map context = new HashMap();
	    context.put("MULTINET_AZIMUTH_MR","false");
	    context.put("CURRENT_COLLECTTIME","2015-08-20 00:00:00");
	    service = (SEBizService) aos.getBean("SEBizService");
//	    service.refreshCoverRate(context);
	    service.refreshPredict(context);
	    service.executeSleepProcess(context);
		
	}
}
