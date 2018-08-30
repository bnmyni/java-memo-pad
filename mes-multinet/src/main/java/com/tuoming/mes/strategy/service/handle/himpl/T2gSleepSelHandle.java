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
 * （1）、判断TDS小区的下一时段期望业务量是否满足：语音业务<Y1,数据流量<S1,码资源利用率<C1,最大用户数<U1，单用户速率<150kps；
(2)、判断将满足（1）条件的TDS小区下一时段预测业务转加到对应的GSM补偿小区中后，GSM补偿小区是否满足：每线话务量<M2，单PDCH承载效率<20kpbs;
 * @author Administrator
 *
 */
@Component("t2gSleepSelHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class T2gSleepSelHandle implements SleepSelHandle{
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
		String desc = "GSM补偿TD一补一节能小区筛选";
		String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);//生成文件存放路径
		String fileName = rootPath+Constant.PRE_SLEEP+System.currentTimeMillis()+CsvUtil.CSV_TYPE;
		PrintStream ps = null;
		List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(cols);
		try {
			ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
			Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
			for(Map<String, Object> data: dataList) {
				double src_zbpz = FormatUtil.tranferCalValue(data.get("src_hzbpzs"));//获取TD小区H载波配置数
				Map<String, Double> tdDic = this.calTdDic(tdDicList, src_zbpz);//获取该小区门限字典
				data.put("src_yyywTHR", tdDic.get("y1"));
				data.put("src_sjllTHR",tdDic.get("s1"));
				data.put("src_mzylylTHR", tdDic.get("c1"));
				data.put("src_zdyhsTHR", tdDic.get("u1"));
				data.put("dest_mxhwlTHR", tdDic.get("m2"));
				data.put("dest_dpdchczxlTHR", thresholdDic.get("T2G_DPDCHCZL_MAKEUP"));
//				语音业务<Y1,数据流量<S1,码资源利用率<C1,最大用户数<U1，单用户速率<150kps；
				String sleepArea =  String.valueOf(data.get("src_lac"))+"_"+String.valueOf(data.get("src_lcid"));//休眠小区标识
				if(sleepMap.containsKey(sleepArea)) {//假如该小区已经是休眠小区，则不处理
					continue;
				}
//				double src_zbpz = FormatUtil.tranferCalValue(data.get("src_hzbpzs"));//获取TD小区H载波配置数
//				Map<String, Double> tdDic = this.calTdDic(tdDicList, src_zbpz);//获取该小区门限字典
				double src_yyyw = FormatUtil.tranferCalValue(data.get("src_yyyw"));//源小区语音业务量
				double src_sjll = FormatUtil.tranferCalValue(data.get("src_sjll"));//数据流量
				double src_mzylyl = FormatUtil.tranferCalValue(data.get("src_mzylyl"));//码资源利用率
				double src_zdyhs = FormatUtil.tranferCalValue(data.get("src_zdyhs"));//最大用户数
//				double src_dyhsl = FormatUtil.tranferCalValue(data.get("src_dyhsl"));//单用户速率
				
				double dest_hwl = FormatUtil.tranferCalValue(data.get("dest_hwl"));//话务量
				int tchn = SleepAreaSelectServiceImpl.getERLangBTCHN(dest_hwl);//信道数
				
				if(src_yyyw<tdDic.get("y1")&&src_sjll<tdDic.get("s1")
						&&src_mzylyl<tdDic.get("c1")){//&&src_dyhsl<tdDic.get("u1")&&src_dyhsl<thresholdDic.get("T2G_DYHSL_SLEEP")) {
					String makeUpArea = String.valueOf(data.get("dest_lac"))+"_"+String.valueOf(data.get("dest_ci"));//补偿小区标识
					if(!count.containsKey(makeUpArea)) {//假如该补偿小区没有被验证过，则获取该小区无线资源利用率和每线话务量的阀值
						Map<String, Double> dic= G2gSleepSelHandle.calGsmDic(gsmDicList, FormatUtil.tranferCalValue(data.get("dest_tchxdcspz")));
						dic.put("dest_mxhwl", FormatUtil.tranferCalValue(data.get("dest_mxhwl")));//gsm邻区的每线话务量
						dic.put("dest_pdchczl", FormatUtil.tranferCalValue(data.get("dest_pdchczl")));//gsm邻区的pdch承载率
						count.put(makeUpArea, dic);
					}
					//每线话务量<M2，单PDCH承载效率<20kpbs;（注:如果出现一个补偿小区补偿多个小区业务时,应将所有补偿业务累加评估。）
					if(count.get(makeUpArea)!=null) {
						double mxhwl_lj = (FormatUtil.tranferCalValue(data.get("src_yyyw"))+FormatUtil.tranferCalValue(data.get("dest_hwl")))/FormatUtil.tranferCalValue(data.get("dest_tchxdcspz"));
						//double pdchczl_lj = (FormatUtil.tranferCalValue(data.get("src_sjll"))*1024*8/FormatUtil.tranferCalValue(data.get("dest_pdchzygs"))/900)+count.get(makeUpArea).get("dest_pdchczl");
						double pdchczl_lj = (FormatUtil.tranferCalValue(data.get("src_sjll"))*1024*8+FormatUtil.tranferCalValue(data.get("dest_rlccll"))*1024*1024*8)/(FormatUtil.tranferCalValue(data.get("dest_tchxdcspz"))-tchn)/900;//流量单位Kbit
						data.put("dest_mxhwl_lj", mxhwl_lj);
						data.put("dest_pdchczl_lj", pdchczl_lj);
						if(mxhwl_lj<count.get(makeUpArea).get("m2")&&pdchczl_lj<thresholdDic.get("T2G_DPDCHCZL_MAKEUP")){
							count.get(makeUpArea).put("dest_mxhwl", mxhwl_lj);
							count.get(makeUpArea).put("dest_pdchczl", pdchczl_lj);
							sleepMap.put(sleepArea, null);
							CsvUtil.writeRow(data, ps, colList);
						}
					}
				}
				
			}
			kpiCalDao.insertT2GKpi(dataList);
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
	/**
	 * 计算td制式的数据字典
	 * @param dicTdList
	 * @param src_zbpz
	 */
	public static Map<String, Double> calTdDic(List<Map<String, Object>> dicTdList, double trx_h) {
		long trx = Math.round(trx_h);
		Map<String, Double> dicMap = new HashMap<String, Double>();
		for(Map<String, Object> dic: dicTdList) {
			int trx_h_max = (Integer)(dic.get("trx_h_max"));
			int trx_h_min = (Integer)(dic.get("trx_h_min"));
			if((trx_h_min==trx_h_max&&trx==trx_h_min)||(trx>=trx_h_min&&trx<=trx_h_max)) {
				dicMap.put("y1", FormatUtil.tranferCalValue(dic.get("y1")));
				dicMap.put("y2", FormatUtil.tranferCalValue(dic.get("y2")));
				dicMap.put("s1", FormatUtil.tranferCalValue(dic.get("s1")));
				dicMap.put("s2", FormatUtil.tranferCalValue(dic.get("s2")));
				dicMap.put("c1", FormatUtil.tranferCalValue(dic.get("c1")));
				dicMap.put("c2", FormatUtil.tranferCalValue(dic.get("c2")));
				dicMap.put("c3", FormatUtil.tranferCalValue(dic.get("c3")));
				dicMap.put("c4", FormatUtil.tranferCalValue(dic.get("c4")));
				dicMap.put("u1", FormatUtil.tranferCalValue(dic.get("u1")));
				dicMap.put("u2", FormatUtil.tranferCalValue(dic.get("u2")));
				dicMap.put("u3", FormatUtil.tranferCalValue(dic.get("u3")));
				dicMap.put("u4", FormatUtil.tranferCalValue(dic.get("u4")));
				return dicMap;
			}
			
		}
		return null;
	}
}
