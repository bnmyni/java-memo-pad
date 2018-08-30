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
 * l2t休眠小区筛选
 * 判断LTE小区的下一时段期望业务量是否满足：上行数据流量<5M,下行数据流量<10M，最大用户数<10,单用户速率<384kbps；
 * 判断将满足（1）条件的LTE小区下一时段预测业务转加到对应的TDS补偿小区中后，TDS补偿小区是否满足：语音业务<Y2,数据流量<S2,
 * 码资源利用率<C2,最大用户数<U2;（注:如果出现一个补偿小区补偿多个小区业务时,应将所有补偿业务累加评估。）
 */
@Component("l2tSleepSelHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class L2tSleepSelHandle implements SleepSelHandle{
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
			List<Map<String, Object>> tdDicList,
			String cols) {
		String desc = "TD补偿LTE一补一节能小区筛选";
		String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);//生成文件存放路径
		String fileName = rootPath+Constant.PRE_SLEEP+System.currentTimeMillis()+CsvUtil.CSV_TYPE;
		PrintStream ps = null;
		List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(cols);
		try {
			ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
			Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
			for(Map<String, Object> data: dataList) {
				Map<String, Double> dic1 = T2gSleepSelHandle.calTdDic(tdDicList, FormatUtil.tranferCalValue(data.get("dest_hzbpzs")));//获取该小区门限字典
				data.put("src_sxsjllTHR", thresholdDic.get("L2T_SXSJLL_SLEEP"));
				data.put("src_xxsjllTHR", thresholdDic.get("L2T_XXSJLL_SLEEP"));
				data.put("src_zdyhsTHR", thresholdDic.get("L2T_ZDYHS_SLEEP"));
				data.put("dest_yyywTHR", dic1.get("y2"));
				data.put("dest_sjllTHR", dic1.get("s2"));
				data.put("dest_mzylylTHR", dic1.get("c2"));
				data.put("dest_zdyhsTHR", dic1.get("u2"));
				String sleepArea =  String.valueOf(data.get("src_enodebid"))+"_"+String.valueOf(data.get("src_localcellid"));//休眠小区标识
				if(sleepMap.containsKey(sleepArea)) {//假如该小区已经是休眠小区，则不处理
					continue;
				}
				double src_sxsjll = FormatUtil.tranferCalValue(data.get("src_sxsjll"));
				double src_xxsjll = FormatUtil.tranferCalValue(data.get("src_xxsjll"));
				double src_zdyhs = FormatUtil.tranferCalValue(data.get("src_zdyhs"));
//				double src_dyhsl = FormatUtil.tranferCalValue(data.get("src_dyhsl"));
				if(src_sxsjll<thresholdDic.get("L2T_SXSJLL_SLEEP")&&src_xxsjll<thresholdDic.get("L2T_XXSJLL_SLEEP")&&src_zdyhs<thresholdDic.get("L2T_ZDYHS_SLEEP")){//&&src_dyhsl<thresholdDic.get("L2T_DYHSL_SLEEP")) {//上行数据流量<5M,下行数据流量<10M，最大用户数<10,单用户速率<384kbps；
					String makeUpArea = String.valueOf(data.get("dest_lac"))+"_"+String.valueOf(data.get("dest_lcid"));//补偿小区标识
					if(!count.containsKey(makeUpArea)) {//假如该补偿小区没有被验证过，则获取该小区语音业务,码资源利用率,,最大用户数,数据流量的阀值
						double src_zbpz = FormatUtil.tranferCalValue(data.get("dest_hzbpzs"));//获取TD小区H载波配置数
						Map<String, Double> dic = T2gSleepSelHandle.calTdDic(tdDicList, src_zbpz);//获取该小区门限字典
						dic.put("dest_sjll", FormatUtil.tranferCalValue(data.get("dest_sjll")));//gsm邻区的每线话务量
						dic.put("dest_zdyhs", FormatUtil.tranferCalValue(data.get("dest_zdyhs")));//gsm邻区的pdch承载率
						count.put(makeUpArea, dic);
					}
					double yyyw_dest = FormatUtil.tranferCalValue(data.get("dest_yyyw"));//该参数无法累加
					double mzylyl_dest = FormatUtil.tranferCalValue(data.get("dest_mzylyl"));//该参数无法累加
					double sjll_lj = FormatUtil.tranferCalValue(data.get("src_sxsjll"))
							+FormatUtil.tranferCalValue(data.get("src_xxsjll"))
							+count.get(makeUpArea).get("dest_sjll");
					double zdyhs_lj = FormatUtil.tranferCalValue(data.get("src_zdyhs"))
							+count.get(makeUpArea).get("dest_zdyhs");
					data.put("dest_sjll_lj", sjll_lj);
					data.put("dest_zdyhs_lj", zdyhs_lj);
					if(yyyw_dest<count.get(makeUpArea).get("y2")
							&&mzylyl_dest<count.get(makeUpArea).get("c2")
							&&sjll_lj<count.get(makeUpArea).get("s2")
							&&zdyhs_lj<count.get(makeUpArea).get("u2")) {
						count.get(makeUpArea).put("dest_sjll", sjll_lj);
						count.get(makeUpArea).put("dest_zdyhs", zdyhs_lj);
						sleepMap.put(sleepArea, null);
						CsvUtil.writeRow(data, ps, colList);
					}
					
				}
			}
			kpiCalDao.insertL2TKpi(dataList);
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
