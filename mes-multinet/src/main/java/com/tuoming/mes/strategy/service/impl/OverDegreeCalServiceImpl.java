package com.tuoming.mes.strategy.service.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pyrlong.configuration.ConfigurationManager;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.strategy.dao.OverlayDegreeDao;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;
import com.tuoming.mes.strategy.service.OverDegreeCalService;
import com.tuoming.mes.strategy.service.handle.OverDegreeCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;

/**
 * 计算重叠覆盖度业务实现
 * @author Administrator
 *
 */
@Service("overDegreeCalService")
public class OverDegreeCalServiceImpl implements OverDegreeCalService {
	private static final int MAX_DATA_COUNT_PI = ConfigurationManager.getDefaultConfig().getInteger("MAX_DATA_COUNT_PI", 80000);
	private static final String PREFIX_LSB = "overdegree_";
	@Autowired
	@Qualifier("overlayDegreeDao")
	private OverlayDegreeDao overlayDegreeDao;

	/**
	 * 计算重叠覆盖度
	 * 
	 */
	public void calculate(String groupName) {
		overlayDegreeDao.updateDegreeSetting();
		/*** 多补一  begin */
//		List<OverlayDegreeSetting> overList =  overlayDegreeDao.queryCalConByGroup(groupName);
		//查询要计算重叠覆盖度的配置
		List<OverlayDegreeSetting> overList = new ArrayList<OverlayDegreeSetting>();
		//如果groupName为空的场合
		if(groupName == null || groupName.length() < 1 ){
			//优先计算一补一重叠覆盖度
			overList.addAll(overlayDegreeDao.queryCalConBySingle());
			//再计算多补一重叠覆盖度
			overList.addAll(overlayDegreeDao.queryCalConByMany());
		}else {
			//按指定组计算覆盖度
			overList.addAll(overlayDegreeDao.queryCalConByGroup(groupName));
		}
		/*** 多补一  begin */
		for(OverlayDegreeSetting setting:overList) {
			String lsbm = PREFIX_LSB+System.currentTimeMillis();//生成临时表名称
			String filePath = CsvUtil.mkParentDir(PREFIX_LSB);//生成结果文件存放路径的父目录
			String targetFile = filePath+setting.getResultTable()+"_"
					+System.currentTimeMillis()+CsvUtil.CSV_TYPE;//生成结果文件路径
			PrintStream ps = null;
			try {
				ps = new PrintStream(targetFile, CsvUtil.DEFAULT_CHARACTER_ENCODING);//创建结果输出流
				overlayDegreeDao.createLsb(lsbm, setting.getQuerySql());//根据配置sql生成临时表
				int dataCount = overlayDegreeDao.getTotalCount(lsbm);//计算临时表数据
				OverDegreeCalHandle handle = AppContext.getBean(setting.getServiceHandle());//生成处理器
				if(dataCount >=MAX_DATA_COUNT_PI) {//假如数据量过大，则分批处理
					int pc = dataCount%MAX_DATA_COUNT_PI==0?
							dataCount/MAX_DATA_COUNT_PI:dataCount/MAX_DATA_COUNT_PI+1;
					for(int i = 1; i <= pc; i++) {//计算每批数据开始与结束索引，分批计算数据
						int startIndex = (i-1)*MAX_DATA_COUNT_PI;
						int num = MAX_DATA_COUNT_PI;
						if(pc==i) {
							num = dataCount-(pc-1)*MAX_DATA_COUNT_PI;
						}
						List<Map<String, Object>> dataList = overlayDegreeDao.queryMetaData(lsbm, startIndex, num);
						handle.handle(dataList, setting, ps);
					}
				}else {
					List<Map<String, Object>> dataList = overlayDegreeDao.queryMetaData(lsbm);
					handle.handle(dataList, setting, ps);
				}
				/*** 多补一  begin */
				//当多补一的场合
				if("MANY".equals(setting.getGroup())){
					//处理最后一批待入库数据
					handle.finalOperation(setting, ps);
				}
				/*** 多补一  end */
				overlayDegreeDao.removeTable(setting.getResultTable());
				overlayDegreeDao.createRstTable(setting.getCreateSql());
				DataAdapterPool.getDataAdapterPool(setting.getDbName()).getDataAdapter().loadfile(targetFile, setting.getResultTable());
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				overlayDegreeDao.removeTable(lsbm);
				try {
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
