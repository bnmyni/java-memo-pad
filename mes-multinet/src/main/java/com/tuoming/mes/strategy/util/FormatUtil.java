package com.tuoming.mes.strategy.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class FormatUtil {
	private static DecimalFormat format = new DecimalFormat("0.00");//保留两位小数
	
	/**
	 * 格式化两位小数
	 * @param val
	 * @return
	 */
	public static String formatTwoDec(double val) {
		return format.format(val);
	}
	
	
	/**
	 * 将要转换的数据转换为double类型，便于计算
	 * @param val
	 * @return
	 */
	public static double tranferCalValue(Object val) {
		if(val==null) {
			return 0;
		}
		if(val instanceof BigDecimal) {
			return ((BigDecimal)val).doubleValue();
		}
		if(val instanceof BigInteger) {
			return ((BigInteger)val).intValue();
		}
		if(val instanceof Integer) {
			return ((Integer)val).intValue();
		}
		if(val instanceof Double) {
			return ((Double)val).doubleValue();
		}
		if(val instanceof Float) {
			return ((Float)val).floatValue();
		}
		
		if(val instanceof String) {
			return Double.parseDouble((String) val);
		}
		return 0;
	}


	public static double tranferStrToNum(String str) {
		if(StringUtils.isEmpty(str)) {
			return 0;
		}
		if(str.indexOf(".")<0) {
			return Integer.parseInt(str);
		}
		return Double.parseDouble(str);
	}


	/**
	 * 
	 * @param timeList
	 * @return
	 */
	public static String tranferArrayToStr(List<Date> timeList) {
		StringBuilder str = new StringBuilder("(");
		for(Date d : timeList) {
			str.append("'").append(DateUtil.format(d)).append("',");
		}
		str.deleteCharAt(str.lastIndexOf(",")).append(")");
		return str.toString();
	}

}
