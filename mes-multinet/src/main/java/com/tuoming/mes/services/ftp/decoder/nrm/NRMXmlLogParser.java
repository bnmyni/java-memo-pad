package com.tuoming.mes.services.ftp.decoder.nrm;

import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.dsl.tools.ExecuteContext;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.services.ftp.AbstractFileProcessor;
import com.tuoming.mes.services.ftp.io.CompressionUtils;
import com.tuoming.mes.services.ftp.io.FileOper;
import com.tuoming.mes.services.impl.SEBizServiceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@Scope("prototype")
@Component("NRMXmlLogParserFtp")
public class NRMXmlLogParser extends AbstractFileProcessor implements
		ContentHandler {
	private StringBuffer buf;
	private String currentFileName;
	private static Logger logger = Logger.getLogger(NRMXmlLogParser.class);
	long parseStartTime;
	BufferedWriter bufferedWriter;
	String currentIdx;
	String currentName;
	Map<String, List<String>> counterToSave;
	List<String> counterFilter = null;
	Map<String, String> counterCache = null;
	String batch = "";
	String starttime;
	Map<String, String> colNameMap = Maps.newHashMap();
	int counterFoundCount = 0;
	StringBuilder recordTitle = new StringBuilder();
	int recordCount = 0;
	String outPutFile;
	String serverName = "";
	List<String> decompressFiles = new ArrayList();
	boolean isCarrier = false;
	String userLabel = "";

	private void parseFiles() {
		System.out.println("Split is " + split);
		for (Map.Entry fileName : this.sourceFileList.entrySet()) {
			try {
				process((String) fileName.getKey(), (Map) fileName.getValue());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		for (String file : this.decompressFiles)
			FileOper.delFile(file);
	}

	public void process(String filePath, Map<String, String> env)
			throws Exception {
		try {
			this.parseStartTime = DateUtil.getTimeinteger().longValue();
			String targetFile = CompressionUtils.decompress(filePath);
			if (targetFile.contains("Carrier")) {
				isCarrier = true;
			} else {
				isCarrier = false;
			}
			this.decompressFiles.add(targetFile);
			parse(targetFile, env);
			logger.info("Second used : "
					+ ((DateUtil.getTimeinteger().longValue() - this.parseStartTime) / 1000L));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void parse(String fileName, Map<String, String> envs) {
		try {
			this.serverName = ((String) envs.get("ftpServer"));
			String exp = (String) envs.get("ftp_command_result_filter");
			if (StringUtil.isNotEmpty(exp)) {
				this.counterToSave = ((Map) DSLUtil.getDefaultInstance()
						.compute(exp));
			}
			this.counterCache = null;
			if (envs.containsKey("BATCH"))
				this.batch = ((String) envs.get("BATCH"));
			this.counterCache = Maps.newLinkedHashMap();
			if (this.counterToSave != null) {
				for (Map.Entry entry : this.counterToSave.entrySet()) {
					if (StringUtil.isMatch(fileName, (String) entry.getKey())) {
						this.counterFilter = ((List) entry.getValue());
						for (String s : this.counterFilter) {
							this.counterCache.put(s, "0");
						}
					}
				}
			}

			XMLReader reader = XMLReaderFactory
					.createXMLReader("org.python.apache.xerces.parsers.SAXParser");
			reader.setContentHandler(this);
			reader.setErrorHandler(new ContentErrorHandler());
			this.currentFileName = fileName;
			File file = new File(this.currentFileName);
			this.outPutFile = this.targetPath
					+ file.getName().replace(".xml", ".csv");
			try {
				this.bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								this.outPutFile, false), this.csvEncoding));
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
			reader.parse(fileName);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void run() {
		parseFiles();
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
		this.buf = new StringBuffer();
		logger.info("Start parsing file :" + this.currentFileName);
	}

	public void endDocument() throws SAXException {
		if (this.bufferedWriter == null)
			return;
		try {
			this.bufferedWriter.close();
			logger.info("End parse file :" + this.currentFileName + " "
					+ this.recordCount + " rows parsed!");
			File file = new File(this.outPutFile);
			if (this.recordCount == 0) {
				file.delete();
				return;
			}
			this.resultFiles.add(file.getName());
			this.recordCount = 0;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if ((localName.equals("Pm")) || (localName.equals("Cm"))) {
			this.recordTitle.setLength(0);

			this.recordTitle.append(this.starttime);
			this.recordTitle.append(split);
			this.recordTitle.append(StringUtil.replaceAll(atts.getValue(0),
					"[.|a-z|A-Z]+=", "").replace(",", split));
			this.recordTitle.append(split);
			if (isCarrier) {
				this.userLabel = StringUtil.replaceAll(atts.getValue(1),
						"[.|a-z|A-Z]+=", "");
				this.userLabel = this.userLabel.split("_")[0];
			}else{
				this.userLabel = StringUtil.replaceAll(atts.getValue(1),
						"[.|a-z|A-Z]+=", "");
			}
			this.recordTitle.append(StringUtil.replaceAll(atts.getValue(1),
					"[.|a-z|A-Z]+=", "").replace(",", split));
			this.recordTitle.append(split);
		} else if (localName.equals("Measurements")) {
			this.colNameMap.clear();
		} else if ((localName.equals("V")) || (localName.equals("CV"))
				|| (localName.equals("N"))) {
			this.currentIdx = atts.getValue(0);
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("N".equals(localName)) {
			this.currentName = this.buf.toString().trim();
			this.colNameMap.put(this.currentIdx, this.currentName);
		} else if ("SN".equals(localName)) {
			this.currentName = this.buf.toString().trim();
		} else if ((localName.equals("SV")) || (localName.equals("V"))) {
			if (localName.equals("V"))
				this.currentName = ((String) this.colNameMap
						.get(this.currentIdx));
			if (this.counterFilter == null)
				logger.warn("Please check the column which name is result_filter in table aos_ftp_command,this column must not be null");
			if (this.counterFilter.contains(this.currentName)) {
				String val = this.buf.toString().trim();
				if (val.indexOf(split) > 0)
					val = enclosed + val + enclosed;
				this.counterCache.put(this.currentName, val);
				this.counterFoundCount += 1;
			}
		} else if ((localName.equals("Pm")) || (localName.equals("Cm"))) {
			try {
				if (this.counterFoundCount > 0) {
					this.bufferedWriter.write(this.recordTitle.toString());
					for (String s : this.counterFilter) {
						this.bufferedWriter.write((String) this.counterCache
								.get(s));
						this.bufferedWriter.write(split);
						this.counterCache.put(s, "0");
					}

					this.bufferedWriter.write(this.batch);
					this.bufferedWriter.write(split);
					this.bufferedWriter.write(this.serverName);
					//this.bufferedWriter.write(SEBizServiceImpl.tdLstTcellHWMap.get(userLabel));
					this.bufferedWriter.write(Envirment.LINE_SEPARATOR);
					this.counterFoundCount = 0;
					this.recordCount += 1;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((localName.equals("BeginTime"))
				|| (localName.equals("DateTime"))) {
			this.starttime = DateUtil.format(DateUtil.addHours(
					DateUtil.getDate(this.buf.toString().replace("T", " ")
							.replace("+0800", "")),
					ConfigurationManager
							.getDefaultConfig()
							.getInteger("pyrlong.aos.timezone",
									Integer.valueOf(0)).intValue()),
					"yyyy-MM-dd HH:mm:ss");
		}
		this.buf.setLength(0);
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		this.buf.append(ch, start, length);
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void skippedEntity(String name) throws SAXException {
	}
}