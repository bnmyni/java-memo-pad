package com.tuoming.mes.collect.decoder.hw;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.util.io.CompressionUtils;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.file.AbstractFileProcessor;
import com.tuoming.mes.strategy.service.handle.TdHWMROFileDecode;
import com.tuoming.mes.strategy.service.handle.himpl.TdHWCellCollectOutPutHandle;

/**
 * 华为 TD MRO文件解析
 */
@Scope("prototype")
@Component("TdHWMROFileHandler")
public class TdHWMROFileHandler extends AbstractFileProcessor {

    private static Logger logger = Logger.getLogger(TdHWMROFileHandler.class);
    //解析器路径配置
    private static final String overDag = ConfigurationManager.getDefaultConfig().getString("HW_TD_MRO_PROCESSOR","NOT FOUND");

    @Override
    /**
     * 运行主方法
     */
    public void run() {
            convertToCSVFiles();
    }
    
    /**
     * 解析TD华为MRO文件至xml文件。
     * 将xml解析并进行统计，计算出总采样点数、单个邻区总采样点数、单个邻区满足信号强度的采样点数。
     * 同时记录主小区与邻小区同采样点的对应关系表。
     */
    public void convertToCSVFiles() {
//        DataOutPutHandle outHandle = AppContext.getBean("TdHWCellCollectOutPutHandle");
    	TdHWCellCollectOutPutHandle outHandle = new TdHWCellCollectOutPutHandle();
        //遍历文件集合
        for (Map.Entry<String, Map<String, String>> fileSet : sourceFileList.entrySet()) {
            try {
            	//取得完成路径文件名
                String fileName = fileSet.getKey();
                //判断是否有.done结尾的文件，有代表已处理过。
                if (isFileDone(fileName))
                    continue;
                //解压文件
                String targetFile = fileName.substring(0, fileName.length() - 3);
                CompressionUtils.decompress(fileName, targetFile);
                //取得文件所在路径
                String parsFilePath = targetFile.substring(0, targetFile.lastIndexOf("/"));
                //取得文件名
                String parsFileName = targetFile.substring(targetFile.lastIndexOf("/") + 1,targetFile.length());
                
                //解析二进制文件
                if(!parsTdHWMROFile(parsFilePath,parsFileName)){
                	//不成功的场合
                	renameErrorDone(fileName);
                	renameErrorDone(targetFile);
                	renameErrorDone(targetFile + ".xml");
                }else {
                	//解析XML文件
                    TdHWMROFileDecode fileDecode = new TdHWMROFileParser();
                    List<String[]> dataList = fileDecode.parse(targetFile + ".xml");
                    //对解析结果做统计计算
                    outHandle.handle(dataList, new Object[]{targetPath});

                    renameDone(fileName);
                    renameDone(targetFile);
                    renameDone(targetFile + ".xml");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        //将统计计算输出到csv中间文件
        outHandle.destroy(targetPath);
        //统计汇总表
        resultFiles.add("MRO_TD_NCELL_HZ.csv");
        //主邻及采样点关系记录表
        resultFiles.add("MRO_TD_NCELL_INFO.csv");
    }

    /**
     * 解析TD华为MRO文件，将二进制MRO文件解析成xml文件
     * @param filePath
     * @param fileName
     */
    private boolean parsTdHWMROFile(String filePath, String fileName){
    	//解析器是否存在
    	if("NOT FOUND".equals(overDag)){
    		logger.warn("HW_TD_MRO_PROCESSOR not found");
    		return false;
    	}
    	//文件及路径是否存在
    	if(filePath == null || fileName == null || filePath.length() < 1 || fileName.length() < 1){
    		logger.warn("解析路径或文件名为空");
    		return false;
    	}
    	//返回与当前 Java 应用程序相关的运行时对象
    	Runtime run = Runtime.getRuntime();
		boolean successFlag = false;
		try {
			// 启动另一个进程来执行命令   
			Process p = run.exec(overDag +" "+filePath+" "+fileName);
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
            String lineStr;   
            while ((lineStr = inBr.readLine()) != null){   
                //获得命令执行后在控制台的输出信息   
            	//根据输出信息判断文件是否解析成功
               if(lineStr.indexOf("PARSE FILE SUCCESS") >= 0){
            	   successFlag = true;
               }
            }
            //检查命令是否执行失败。   
            if (p.waitFor() != 0) {
            	if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束
            		logger.warn("命令执行失败!");
            }else {
            	if(successFlag){
            		logger.info("解析成功："+filePath + "/" + fileName);
            	}else{
            		logger.info("解析失败："+filePath + "/" + fileName);
        		}
           }
			
           return successFlag;
		}catch (Exception ex) {   
			logger.warn(ex.getMessage(), ex);
            return false;
       }
    }
    
    /**
     * 将文件更名*.done，标识出已处理文件
     * @param file
     * @return
     */
    private boolean renameDone(String file){
    	 String path = file + DPPConstants.FILE_PARSED_EXTENSION;
         File donefile = new File(path);
         return new File(file).renameTo(donefile);
    }
    /**
     * 将文件更名*.error.done，标识出解析二进制失败文件
     * @param file
     * @return
     */
    private boolean renameErrorDone(String file){
    	 String path = file +".error"+ DPPConstants.FILE_PARSED_EXTENSION;
         File donefile = new File(path);
         return new File(file).renameTo(donefile);
    }
}