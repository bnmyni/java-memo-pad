package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;
import com.tuoming.mes.strategy.service.handle.OverDegreeCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 方位角重叠覆盖度计算
 * @author Administrator
 *
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("azimuthInstanceCalculationHandle")
public class AzimuthInstanceCalculationHandle implements OverDegreeCalHandle{
	private Map<String, Integer> count = new HashMap<String, Integer>();//计数器，计算已经录入了几条符合条件的邻区

	private static final int instance_max = ConfigurationManager.getDefaultConfig().getInteger("LTEMANYOVERDAG_SINGLE_NC_RATE", 30);
	
	public void handle(List<Map<String, Object>> dataList,
			OverlayDegreeSetting setting, PrintStream ps) {
		for(Map<String, Object> data : dataList) {
			String key =  DSLUtil.getDefaultInstance().buildString(setting.getSourceBs(), data);
			double src_longitude = FormatUtil.tranferCalValue(data.get("src_longitude"));//源经度
			double src_latitude = FormatUtil.tranferCalValue(data.get("src_latitude"));//源纬度
			double dest_longitude = FormatUtil.tranferCalValue(data.get("dest_longitude"));//邻区经度
			double dest_latitude = FormatUtil.tranferCalValue(data.get("dest_latitude"));//邻区纬度
			double instance = AzimuthCalculationHandle.getInstance(src_longitude, src_latitude, dest_longitude, dest_latitude);//计算源小区和邻区距离
			if(!count.containsKey(key)) {//初始化计数器
				count.put(key, 0);
			}
			if(count.get(key)<setting.getTopAmount()||setting.getTopAmount()<0) {//假如不满足要记录小区的数目
				if(this.validate(instance)) {
					data.put("rst_instance", instance);//把距离存入计算结果
					CsvUtil.writeRow(data, ps, setting.getColumnList());
					count.put(key, count.get(key)+1);
				}
			}
		}
	}
	
	public void finalOperation(OverlayDegreeSetting setting, PrintStream ps){
		return;
	}

	/**
	 * 根据距离和方位角计算小区是否符合节能小区
	 * @param instance
	 * @param src_azimuth
	 * @param dest_azimuth
	 * @return
	 */
	private boolean validate(double instance) {
		if(instance<instance_max){
			return true;
		}
		return false;
	}

	/**
	 * 根据源经纬度和目的经纬度坐标求距离
	 * @param src_longitude  源经度
	 * @param src_latitude  源纬度
	 * @param dest_longitude  邻区经度
	 * @param dest_latitude  邻区纬度
	 * @return double
	 */
	public static double getInstance(double src_longitude,
			double src_latitude, double dest_longitude, double dest_latitude) {
		double earth_padius = 6378137.0;
		double radLat1 = 3.141592625 * src_latitude / 180;
		double radLat2 = 3.141592625 * dest_latitude / 180;
		double a = radLat1 - radLat2;
		double b = 3.141592625 * src_longitude / 180 - 3.141592625
				* dest_longitude / 180;
		double s1 = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		double s2 = s1 * earth_padius;
		return Double.parseDouble(FormatUtil.formatTwoDec(s2));
	}
}
