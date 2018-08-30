package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.KpiCalDao;
import com.tuoming.mes.strategy.service.handle.SleepSelHandle;
import com.tuoming.mes.strategy.service.impl.SleepAreaSelectServiceImpl;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 
 * @author Administrator
 * t2l休眠小区筛选
 * (1)判断TD小区的下一时段期望业务量是否满足：语音业务<Y2,数据流量<s2,码资源利用率<c2,最大用户数<u2；
 * (2)判断将满足（1）条件的TD小区下一时段预测业务转加到对应的LTE补偿小区中后，LTE补偿小区是否满足：上行数据流量<Y2,下行数据流量<S2,
 * 最大用户数<U2;
 */
@Component("t2lSleepSelHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class T2LSleepSelHandle implements SleepSelHandle{
	@Autowired
	@Qualifier("kpiCalDao")
	private KpiCalDao kpiCalDao;
	@Autowired
	@Qualifier("businessLogDao")
	private BusinessLogDao businessLogDao;
	Map<String, Map<String, Double>> count = new HashMap<String, Map<String,Double>>();//记录补偿小区及其相对应的门限阀值
	Map<String, Integer> sleepMap = new HashMap<String, Integer>();//记录同一休眠小区是否已经休眠;

	@Override
	public String handle(List<Map<String, Object>> dataList,
			List<Map<String, Object>> gsmDicList,
			List<Map<String, Object>> tdDicList, String cols) {
		String desc = "LTE补偿LTE一补一节能小区筛选";
		String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);//生成文件存放路径
		String fileName = rootPath+Constant.PRE_SLEEP+System.currentTimeMillis()+CsvUtil.CSV_TYPE;
		PrintStream ps = null;
		List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(cols);
		try {
			ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
			Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
			for(Map<String, Object> data: dataList) {
				Map<String, Double> dic1 = T2gSleepSelHandle.calTdDic(tdDicList, FormatUtil.tranferCalValue(data.get("src_hzbpzs")));//获取该小区门限字典
				data.put("src_yyywTHR", dic1.get("y2"));
				data.put("src_sjllTHR", dic1.get("s2"));
				data.put("src_mzylylTHR", dic1.get("c2"));
				data.put("src_zdyhsTHR", dic1.get("u2"));
				data.put("dest_sxsjllTHR", thresholdDic.get("T2L_SXSJLL_MAKEUP"));
				data.put("dest_xxsjllTHR", thresholdDic.get("T2L_XXSJLL_MAKEUP"));
				data.put("dest_zdyhsTHR", thresholdDic.get("T2L_ZDYHS_MAKEUP"));				
				String sleepArea =  String.valueOf(data.get("src_lac"))+"_"+String.valueOf(data.get("src_lcid"));//休眠小区标识
				if(sleepMap.containsKey(sleepArea)) {//假如该小区已经是休眠小区，则不处理
					continue;
				}
				double src_yyyw = FormatUtil.tranferCalValue(data.get("src_yyyw"));//语音业务
				double src_mzylyl = FormatUtil.tranferCalValue(data.get("src_mzylyl"));//码资源利用率
				double src_sjll = FormatUtil.tranferCalValue(data.get("src_sjll"));//数据流量
				double src_zdyhs = FormatUtil.tranferCalValue(data.get("src_zdyhs"));//最大用户数
				//节能小区指标判断，语音业务<Y2,数据流量<s2,码资源利用率<c2,最大用户数<u2
				if(src_yyyw<dic1.get("y2")&&src_mzylyl<dic1.get("c2")&&src_sjll<dic1.get("s2")&&src_zdyhs<dic1.get("u2")){
					String makeUpArea = String.valueOf(data.get("dest_enodebid"))+"_"+String.valueOf(data.get("dest_localcellid"));//补偿小区标识
					if(!count.containsKey(makeUpArea)) {//假如该补偿小区没有被验证过							
						count.put(makeUpArea, thresholdDic);
					}
					double sxsjll_lj = FormatUtil.tranferCalValue(data.get("dest_sxsjll")) 
							+ FormatUtil.tranferCalValue(data.get("src_sjll"));//上行数据流量累加
					double xxsjll_lj = FormatUtil.tranferCalValue(data.get("dest_xxsjll"))
							+ FormatUtil.tranferCalValue(data.get("src_sjll"));//下行数据流量累加
					double dest_zdyhs = FormatUtil.tranferCalValue(data.get("dest_zdyhs"));//最大用户数

					data.put("dest_xxsjll_lj", xxsjll_lj);
					data.put("dest_sxsjll_lj", sxsjll_lj);
					//补偿小区+节能小区指标判断，上行数据流量<Y2,下行数据流量<S2, 最大用户数<U2
					if(sxsjll_lj<count.get(makeUpArea).get("T2L_SXSJLL_MAKEUP")
							&&xxsjll_lj<count.get(makeUpArea).get("T2L_XXSJLL_MAKEUP")
							&&dest_zdyhs<count.get(makeUpArea).get("T2L_ZDYHS_MAKEUP")) {
						count.get(makeUpArea).put("dest_sxsjll", sxsjll_lj);
						count.get(makeUpArea).put("dest_xxsjll", xxsjll_lj);
						sleepMap.put(sleepArea, null);
						CsvUtil.writeRow(data, ps, colList);
					}					
				}
			}
			kpiCalDao.insertT2LKpi(dataList);
			businessLogDao.insertLog(11, desc, 0);
		} catch (Exception e) {
			businessLogDao.insertLog(11, desc, 1);
			e.printStackTrace();
		}finally {
			if(ps!=null) {
				ps.close();
			}
		}
		return fileName;
	}
	
}
