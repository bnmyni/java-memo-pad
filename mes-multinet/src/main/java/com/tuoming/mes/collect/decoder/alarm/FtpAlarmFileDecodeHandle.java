package com.tuoming.mes.collect.decoder.alarm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.decoder.zte.AbstractContentHandler;
import com.tuoming.mes.services.serve.MESConstants;

/**
 * 解析告警文件
 * @author Administrator
 *
 */
@Scope("prototype")
@Component("FtpAlarmFileDecodeHandle")
public class FtpAlarmFileDecodeHandle extends AbstractContentHandler {
	private String rowTag = "alarmInfo";//行标签
	private StringBuilder rowContent = new StringBuilder();
	private static final String ZS = "zs";//该告警信息属于何种制式
	private static final String DN = "dn";//通过该字段确定该告警信息属于何种制式

	protected void parseFiles() {
		for (Map.Entry<String, Map<String, String>> fileName : sourceFileList.entrySet())
			try {
				parseStartTime = DateUtil.getTimeinteger();
				String exp = fileName.getValue().get(MESConstants.FTP_COMMAND_RESULT_FILTER);
				columnFilter = (List<String>) DSLUtil.getDefaultInstance().compute(exp);
				String targetFile = fileName.getKey();
				if (super.isFileDone(targetFile))
					continue;
				parse(targetFile, fileName.getValue());
				markFileDone(targetFile);
				logger.info("Second used : " + (DateUtil.getTimeinteger() - parseStartTime) / 1000);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetPath + getNewFileName(), false), "utf-8"));
			for(String head:columnFilter) {
				columnMap.put(head, "");
			}
			columnMap.put(ZS, "");
			if(printHeander) {
				for(String head:columnMap.keySet()) {
					rowContent.append(head).append(Envirment.CSV_SEPARATOR);
				}
				try {
					bufferedWriter.write(rowContent.deleteCharAt(rowContent.lastIndexOf(Envirment.CSV_SEPARATOR))
							.append(Envirment.LINE_SEPARATOR).toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			resultFiles.add(getNewFileName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String fullName, Attributes attributes) throws SAXException {
		if(rowTag.equalsIgnoreCase(localName)) {
			rowContent.setLength(0);
		}
	}

	public void endElement(String namespaceURI, String localName, String fullName) throws SAXException {
		if(columnFilter.contains(localName)) {
			columnMap.put(localName, buf.toString().replaceAll("\r", "").replaceAll("\n", ""));
		}else if(rowTag.equalsIgnoreCase(localName)) {
			try {
				for(Entry<String, String> entry:columnMap.entrySet()) {
					if(ZS.equals(entry.getKey())) {
						String dn = columnMap.get("dn");
						if(StringUtils.isEmpty(dn))
							return;
						if(dn.toUpperCase().indexOf("RNC")>=0) {
							rowContent.append("td").append(Envirment.CSV_SEPARATOR);
						}else if(dn.toUpperCase().indexOf("BSC")>=0) {
							rowContent.append("gsm").append(Envirment.CSV_SEPARATOR);
						}else {
							rowContent.append("lte").append(Envirment.CSV_SEPARATOR);
						}
					}else {
						rowContent.append("~").append(columnMap.get(entry.getKey())).append("~").append(Envirment.CSV_SEPARATOR);
					}
				}
				bufferedWriter.write(rowContent.deleteCharAt(rowContent.lastIndexOf(Envirment.CSV_SEPARATOR))
						.append(Envirment.LINE_SEPARATOR).toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		buf.setLength(0);
	}

	@Override
	protected String getNewFileName() {
		File f = new File(currentFileName);
		String fileName = f.getName();
		return fileName.substring(0, fileName.lastIndexOf(".")-1)+".csv";
	}
}
