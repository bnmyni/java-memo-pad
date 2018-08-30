package com.tuoming.mes.services.ftp.decoder.ericsson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.decoder.ericsson.asn1.EricssonStsDecoder;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasData;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasDataCollection;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasInfo;
import com.tuoming.mes.collect.decoder.ericsson.asn1.entity.MeasValue;
import com.tuoming.mes.services.ftp.AbstractFileProcessor;
import com.tuoming.mes.services.ftp.MESException;
import com.tuoming.mes.services.impl.SEBizServiceImpl;

@Scope("prototype")
@Component("EriAsnFileHandleFtp")
public class EriAsnFileHandle extends AbstractFileProcessor
{
  private static Logger logger = Logger.getLogger(EriAsnFileHandle.class);

  private MeasDataCollection mdc = null;
  private EricssonStsDecoder esd = null;
  private BufferedWriter bw = null;
  private List<String> objectTypes = new ArrayList();
  private Map<String, String> objectTypeParsed = Maps.newHashMap();

  public List<String> convertToCSVFiles(List<String> fileList)
    throws IOException, ParseException
  {
    setFiles(fileList);
    return convertToCSVFiles();
  }

  public List<String> convertToCSVFiles() throws IOException, ParseException {
    if (this.sourceFileList == null) {
      return this.resultFiles;
    }
    for (Map.Entry fileSet : this.sourceFileList.entrySet()) {
      try {
        process((String)fileSet.getKey(), (Map)fileSet.getValue());
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
    return this.resultFiles;
  }

  public void process(String filePath, Map<String, String> env)
    throws Exception
  {
    String dataPath = this.targetPath;

    String separator = split;

    Map map = new HashMap();
    try {
      String fileName = filePath;

      String exp = (String)env.get("ftp_command_result_filter");
      if (StringUtil.isEmpty(exp))
        exp = "[]";
      this.objectTypes = ((List)DSLUtil.getDefaultInstance().compute(exp));

      this.mdc = new MeasDataCollection();

      File file = new File(fileName);

      String timestamp = file.getName();
      timestamp = timestamp.substring(1, 5) + "-" + timestamp.substring(5, 7) + "-" + timestamp.substring(7, 9) + " " + timestamp.substring(10, 12) + ":" + 
        timestamp.substring(12, 14) + ":00";
      timestamp = DateUtil.format(DateUtil.addHours(DateUtil.getDate(timestamp), ConfigurationManager.getDefaultConfig().getInteger("hamster.timezone", Integer.valueOf(8)).intValue()), "yyyy-MM-dd HH:mm:ss");

      this.esd = new EricssonStsDecoder(file, this.mdc);
      this.esd.decode();

      String bscName = this.mdc.getMeasFileHeader().getBscName();
      logger.debug("=====bsc:" + bscName + "========");
      if (map.get(bscName) == null) {
        map.put(bscName, new ArrayList());
      }
      List list = (List)map.get(bscName);
      List<MeasData> measList = this.mdc.getMeasData();
      for (MeasData md : measList) {
        List<MeasInfo> measInfoList = md.getMeasInfo();
        for (MeasInfo measInfo : measInfoList) {
          String csvFileName = "";
          File csv = null;
          String objectName = measInfo.getObjectType();

          String tempKey = bscName + objectName + timestamp;
          if ((objectName != null) && (this.objectTypes.contains(objectName)) && (!(this.objectTypeParsed.containsKey(tempKey))))
          {
            csvFileName = new String(measInfo.getObjectType() + ".csv");
            this.objectTypeParsed.put(tempKey, null);

            csv = new File(dataPath + csvFileName);
            list.add(objectName);
            if (!(this.resultFiles.contains(csvFileName)))
            {
              this.resultFiles.add(csvFileName);
            }

            boolean isExists = csv.exists();
            logger.debug("--导出CSV文件路径: " + csv.getAbsolutePath());
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(csv, true), this.csvEncoding);
            this.bw = new BufferedWriter(out);
            StringBuffer typeSb = null;
            if ((!(isExists)) && (this.printHeander)) {
              List<String> measTypes = measInfo.getMeasTypes();
              typeSb = new StringBuffer("BSC").append(separator).append("PERIOD").append(separator).append("TIME_STAMP").append(separator).append("NE_NAME");
              for (String type : measTypes) {
                typeSb.append(separator).append(type);
              }
              this.bw.write(typeSb.toString());
              this.bw.newLine();
            }

            List<MeasValue> measValues = measInfo.getMeasValues();
            logger.debug("--导出记录总数据:" + measValues.size());
            StringBuffer valueSb = null;
            int i = 0;
            for (MeasValue value : measValues) {
              String mo = value.getMo();
              if (mo.indexOf("-") > 0) {
                mo = mo.replace("-", separator);
              }
              List<Long> resultValue = value.getMeasResults();
              valueSb = new StringBuffer();
              valueSb.append(SEBizServiceImpl.ericBscMap.get(bscName)).append(separator).append(measInfo.getGranularityPeriod()).append(separator)
                .append(timestamp)
                .append(separator)
                .append(mo);
              for (Long value1 : resultValue) {
                valueSb.append(separator).append(value1.longValue());
              }
              this.bw.append(valueSb.toString());
              this.bw.newLine();
              if (i == 1000) {
                i = 0;
                this.bw.flush();
              }
            }
            this.bw.flush();
            this.bw.close(); }
        }
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      throw new MESException("AOS-5001", new Object[] { filePath + ex.getMessage() });
    }
  }

  public void run()
  {
    try {
      convertToCSVFiles();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } catch (ParseException e) {
      logger.error(e.getMessage(), e);
    }
  }
}