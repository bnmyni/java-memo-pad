package com.tuoming.mes.strategy.util;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opensymphony.oscache.util.StringUtil;
import com.pyrlong.Envirment;
import com.pyrlong.util.DateUtil;

public class CsvUtil {
	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	private static final String CSV_SEPARTER = ",";
	private static final String EMPTY_STRING = "NULL";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
	public static final String CSV_TYPE = ".csv";

	public static void writeRow(Map<String, Object> data, PrintStream ps, List<String> columnList) {
		if(data==null||data.isEmpty()) {
			return;
		}
		StringBuilder content = new StringBuilder();
		if(columnList==null||columnList.isEmpty()) {
			for(Entry<String, Object> entry:data.entrySet()) {
				content.append(CsvUtil.format(entry.getValue())).append(CSV_SEPARTER);
			}
		}else {
			for(String column:columnList) {
				content.append(CsvUtil.format(data.get(column.trim()))).append(CSV_SEPARTER);
			}
		}
		content.deleteCharAt(content.lastIndexOf(CSV_SEPARTER));
		ps.println(content.toString());
	}
	
	public static void writeRows(List<Map<String, Object>> dataList, PrintStream ps, List<String> columnList) {
		if(dataList==null||dataList.isEmpty()) {
			return;
		}
		for(Map<String, Object> data:dataList) {
			CsvUtil.writeRow(data, ps, columnList);
		}
	}

	private static String format(Object value) {
		if(value==null) {
			return EMPTY_STRING;
		}
		if(value instanceof String) {
			if(StringUtil.isEmpty(value.toString())) {
				return EMPTY_STRING;
			}
			return value.toString();
		}
		
		if(value instanceof Date) {
			return DateUtil.format((Date)value, "yyyy-MM-dd HH:mm:ss");
		}
		return value.toString();
	}

	public static String mkParentDir(String parentDir) {
		String path = Envirment.getHome() + "data/"+sdf.format(new Date())+"/"+ parentDir+"/";
		File f = new File(path);
		if(!f.exists()) {
			f.mkdirs();
		}
		return path;
	}

}
