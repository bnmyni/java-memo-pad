package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.dao.OverlayDegreeDao;
import com.tuoming.mes.strategy.model.ManyOverDegCalModel;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;
import com.tuoming.mes.strategy.service.handle.OverDegreeCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * Lte华为MRO计算重叠覆盖度逻辑处理器
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("manyLteOverDegCalHandler")
public class ManyLteOverDegCalHandler implements OverDegreeCalHandle{
	//记录符合单个邻区占总采样点满足条件的数据
	private Map<String, ManyOverDegCalModel> manyMap = new HashMap<String, ManyOverDegCalModel>();
	
	@Autowired
	@Qualifier("overlayDegreeDao")
	private OverlayDegreeDao overlayDegreeDao;
	
	//多补一LTE重叠覆盖度门限值(0-100,默认80)
	private static final int overDag = ConfigurationManager.getDefaultConfig().getInteger("LTEMANYOVERDAG_OVERDAG", 80);
	//多补一LTE单个邻区采样点占总采样点比例(0-100,默认30)
	private static final int singleNc_rate = ConfigurationManager.getDefaultConfig().getInteger("LTEMANYOVERDAG_SINGLE_NC_RATE", 30);

	@Override
	public void handle(List<Map<String, Object>> dataList,	OverlayDegreeSetting setting, PrintStream ps) {
		//循环数据行，验证数据是否符合
		for(Map<String, Object> data : dataList) {
			//计算源小区标识
			String key = DSLUtil.getDefaultInstance().buildString(setting.getSourceBs(), data);
			//当源小区不存在的场合
			if(!manyMap.containsKey(key)) {
				//单小区多补一覆盖筛选集合
				ManyOverDegCalModel manyInfoModel = new ManyOverDegCalModel(); 
				manyMap.put(key, manyInfoModel);
			}
			
			ManyOverDegCalModel manyInfoModel = manyMap.get(key);
			//相同本小区，邻小区取前10个
			if(manyInfoModel.getCount() < setting.getTopAmount()) {
				//所有邻区的MR采集的采样点总数
				double ncTotal = FormatUtil.tranferCalValue(data.get("count1"));
				//单个邻区的MR采样点总数
				double ncCount = FormatUtil.tranferCalValue(data.get("count2"));
				//总邻区采样点数>1000,并且单个邻区占总采样点比例>30%的场合
				if(ncTotal>1000 && ncCount / ncTotal * 100 > singleNc_rate) {
					//查询条件存在的场合
					if(manyInfoModel.getQuerySql().length() > 0 ){
						manyInfoModel.addQuerySql(" or ");
					}else{
						manyInfoModel.addQuerySql(" enodebid=");
						manyInfoModel.addQuerySql(data.get("enodebid"));
						manyInfoModel.addQuerySql(" and localcellid=");
						manyInfoModel.addQuerySql(data.get("localcellid"));
						manyInfoModel.addQuerySql(" and (");
					}
					//添加查询条件nearfcn and pci
					manyInfoModel.addQuerySql("(nearfcn=");
					manyInfoModel.addQuerySql(data.get("nearfcn"));
					manyInfoModel.addQuerySql(" and pci=");
					manyInfoModel.addQuerySql(data.get("pci"));
					manyInfoModel.addQuerySql(")");
					
					//单个邻区占总采样点比例
					data.put("ncSingleRate",  FormatUtil.formatTwoDec(ncCount/ncTotal));
					//将该小区信息储存，待写入文件
					manyInfoModel.addCellDataList(data);
					//计算器+1
					manyInfoModel.addCount();
				}
			} 
		}
		
	}
	
	/**
	 * 当全部数据结束后，调用该方法将筛选结果写入文件
	 */
	public void finalOperation(OverlayDegreeSetting setting, PrintStream ps){
		for(Entry<String, ManyOverDegCalModel> entry:manyMap.entrySet()) {
			ManyOverDegCalModel manyInfoModel = entry.getValue();
			//当有可补偿邻区采样点时，进行比例判断
			if(manyInfoModel.getCount() > 0){
				//查询所有可被补偿邻区的MRO采集点汇总去重后的采集点数量
				int trueCellCount = overlayDegreeDao.queryTrueLteCellCoint(manyInfoModel.getQuerySql());
				
				List<Map<String, Object>> cellDataList = manyInfoModel.getCellDataList();
				//所有可被补偿邻区采样点占总采样点比例>80%的场合
				if(trueCellCount / FormatUtil.tranferCalValue(cellDataList.get(0).get("count1")) * 100 > overDag){
					//将前批数据写入文件
					for(Map<String, Object> hisMap : cellDataList){
						//所有邻区的MR采集的采样点总数
						double ncTotal = FormatUtil.tranferCalValue(hisMap.get("count1"));
						
						hisMap.put("trueCellCount", trueCellCount);
						//所有可被补偿邻区采样点占总采样点比例
						hisMap.put("ncTotalRate", FormatUtil.formatTwoDec(trueCellCount/ncTotal));
						//写入文件
						CsvUtil.writeRow(hisMap, ps, setting.getColumnList());
					}
					
				}
			}
		}
	}


}
