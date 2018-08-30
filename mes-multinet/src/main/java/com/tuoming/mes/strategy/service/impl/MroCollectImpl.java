package com.tuoming.mes.strategy.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.service.MroCollectService;
import com.tuoming.mes.strategy.service.handle.DataInputHandle;
import com.tuoming.mes.strategy.service.handle.DataOutPutHandle;
import com.tuoming.mes.strategy.util.FileUtil;


@Service("MroCollectService")
public class MroCollectImpl implements MroCollectService{
	private static final Logger logger = LogFacade.getLog4j(MroCollectImpl.class);
	@Autowired
	@Qualifier("businessLogDao")
	private BusinessLogDao businessLogDao;
	
	@Override
	public void exeLteHwLocalAnaly(String dir,String regex) {
		businessLogDao.insertLog(5, "采集华为LTE文件开始", 0);
		FileOper.delAllFile( Constant.scanDir);
		int fileCount = getFileIndexList(dir,regex);
		DataInputHandle inHandle = AppContext.getBean("XmlDataInputHandle");
		DataOutPutHandle outHandle = AppContext.getBean("LteHWCellCollectOutPutHandle");
		int currentIndex = 0;
		int newNcfileNum = 0;
		for(int i =1; i<=fileCount;i++) {
			BufferedReader br = null;
			String fileName = Constant.scanDir+i+".csv";
			File f = new File(fileName);
			if(!f.exists()) {
				logger.info(fileName+" file not exists!");
				continue;
			}
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				logger.info("start handle "+fileName+"  data");
				while(br.ready()) {
					String path = br.readLine();
					if(StringUtils.isEmpty(path)) {
						continue;
					}
					if(path.endsWith(".tmp")){
						continue;
					}
					boolean beginOut = false;
					List<String[]> dataList = null;
					if(Constant.END.equals(path)) {
						beginOut = true;
						newNcfileNum++;
					}else {
//						logger.info("paser file "+path);
						businessLogDao.insertLog(6, "解析开始", 0);
						dataList = inHandle.readFile(path);
						businessLogDao.insertLog(6, "解析结束", 0);
						currentIndex++;
					}
					outHandle.handle(dataList, new Object[]{AppContext.CACHE_ROOT+"mr_lte_hw/", beginOut, newNcfileNum%25==0});
					if(currentIndex%10000==0) {
						logger.info("already handle "+currentIndex);
					}
				}
			} catch (Exception e) {
				businessLogDao.insertLog(5, "采集华为LTE文件读取文件出现异常", 1);
				e.printStackTrace();
				logger.info(e);
			}finally {
				if(br!=null) {
					try {						
						br.close();
					} catch (IOException e) {
						businessLogDao.insertLog(5, "采集华为LTE文件读取文件关闭流出现异常", 1);
						e.printStackTrace();
					}
				}
			}
		}
		outHandle.destroy();
		logger.info("handle done "+currentIndex);
	    List<String> dbFiles = FileOper.getSubFiles(AppContext.CACHE_ROOT+"mr_lte_hw/", ".csv");
    	for(String dbFile:dbFiles) {
    		try {
    			/*** LTE多补一  begin */
    			if(dbFile.indexOf("LTE_NCELL_INFO")> 0){
    				DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, "rst_pm_l2l_hw_nc_info");
    			} else {
	    			DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, "rst_pm_l2l_hw_hz");
    			}
    			/*** LTE多补一  end */
    		} catch (Exception e) {
    			businessLogDao.insertLog(5, "采集华为LTE文件数据入库出现异常", 1);
    			logger.error(e);
    		}
    	}
    	businessLogDao.insertLog(5, "采集华为LTE文件结束", 0);
//		for(Entry<String, List<String>> entry:fileMap.entrySet()) {
//			logger.info("start handle "+entry.getKey()+" timeperiod data");
//			
//			boolean beginOut = false;
//			for(int i = 0,size = entry.getValue().size(); i < size; i++) {
//				List<String[]> dataList = inHandle.readFile(entry.getValue().get(i));
//				if(i==size-1) {
//					beginOut = true;
//				}
//				outHandle.handle(dataList, new Object[]{AppContext.CACHE_ROOT, entry.getKey(), beginOut});
//			}
//			fileMap.put(entry.getKey(), null);
//			List<String> dbFiles = FileOper.getSubFiles(AppContext.CACHE_ROOT+entry.getKey(), ".csv",true);
//			for(String dbFile:dbFiles) {
//				try {
//					DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter()
//					.loadfile(dbFile, "rst_pm_l2l_hw_hz");
//				} catch (Exception e) {
//					logger.error(e);
//				}
//			}
////			dao.updateData(entry.getKey());
////			Init init = AppContext.getBean("CommonDataInit");
////			List<Map<String, String>> dataList = dao.queryData(entry.getKey());
////			dao.insrtData(dataList, entry.getKey());
//		}
	}
	
	/**
	 * 按照正则表达式(regex)取得指定路径(input)下所有层级子文件夹中的文件总个数
	 * @param file
	 * @param fileRegex
	 * @return
	 */
	public int getFileIndexList(String input, String regex) {
		File ldFile = new File(Constant.scanDir+"0.csv");
		if(!ldFile.exists()) {
			scanFile(input,regex);
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(ldFile)));
			while(br.ready()) {
				String line = br.readLine();
				if(StringUtils.isEmpty(line)) {
					continue;
				}
				return Integer.parseInt(line);
			}
		} catch (Exception e) {
			businessLogDao.insertLog(5, "采集华为LTE文件获得文件出现异常", 1);
			e.printStackTrace();
			logger.error(e);
		}finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					businessLogDao.insertLog(5, "采集华为LTE文件关闭流出现异常", 1);
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public int getFileIndexList(String input, String regex,String rname) {
		File ldFile = new File(rname + Constant.scanDir+"0.csv");
		if(!ldFile.exists()) {
			scanFile(input,regex,rname);
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(ldFile)));
			while(br.ready()) {
				String line = br.readLine();
				if(StringUtils.isEmpty(line)) {
					continue;
				}
				return Integer.parseInt(line);
			}
		} catch (Exception e) {
			businessLogDao.insertLog(5, "采集华为LTE文件获得文件出现异常", 1);
			e.printStackTrace();
			logger.error(e);
		}finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					businessLogDao.insertLog(5, "采集华为LTE文件关闭流出现异常", 1);
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	/**
	 * 按照正则表达式(regex)将指定路径(input)下所有层级子文件夹中的文件名记录到1~N(fileExt).csv中
	 * 每500个文件名fileExt变量加1
	 * 最后1~N.csv存入0.csv文件中
	 * @param file
	 * @param fileRegex
	 * @return
	 */
	private static void scanFile(String input, String regex) {
		Map<String, List<String>> fileMap = FileUtil.getChildFile(input, regex);
		int fileExt = 1;
		int count = 0;
		BufferedWriter bw = null;
		String file = Constant.scanDir + fileExt+".csv";
		FileOper.checkAndCreateForder(file);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
			for(Entry<String, List<String>> entry:fileMap.entrySet()) {
				count++;
				if(count==500) {
					fileExt++;
					count = 0;
					bw.close();
					logger.info("file: "+file);
					file = Constant.scanDir+fileExt+".csv";
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
				}
				for(String line:entry.getValue()) {
					bw.write(line);
					bw.newLine();
				}
				bw.write(Constant.END);
				bw.newLine();
			} 
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("scan error");
		}finally {
			if(bw!=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		file = Constant.scanDir+File.separatorChar +"0.csv";
		bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
			bw.write(String.valueOf(fileExt));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("scan final result out error");
		}finally {
			if(bw!=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void scanFile(String input, String regex,String rname) {
		Map<String, List<String>> fileMap = FileUtil.getChildFile(input, regex);
		int fileExt = 1;
		int count = 0;
		BufferedWriter bw = null;
		String file = rname + Constant.scanDir + fileExt+".csv";
		FileOper.checkAndCreateForder(file);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
			for(Entry<String, List<String>> entry:fileMap.entrySet()) {
				count++;
				if(count==500) {
					fileExt++;
					count = 0;
					bw.close();
					logger.info("file: "+file);
					file = rname + Constant.scanDir+fileExt+".csv";
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
				}
				for(String line:entry.getValue()) {
					bw.write(line);
					bw.newLine();
				}
				bw.write(Constant.END);
				bw.newLine();
			} 
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("scan error");
		}finally {
			if(bw!=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		file = rname + Constant.scanDir+File.separatorChar +"0.csv";
		bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file))));
			bw.write(String.valueOf(fileExt));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("scan final result out error");
		}finally {
			if(bw!=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 解析华为TD的MRO文件
	 */
	@Override
	public void exeTdHwLocalAnaly(String dir,String regex){
		
	}
	
	public void exeLteHwLocalAnaly2(int beginNum,int endNum) {
		DataInputHandle inHandle = AppContext.getBean("XmlDataInputHandle");
		DataOutPutHandle outHandle = AppContext.getBean("LteHWCellCollectOutPutHandle");
		int currentIndex = 0;
		int newNcfileNum = 0;
		for(int i =beginNum; i<=endNum;i++) {
			BufferedReader br = null;
			String fileName = Constant.scanDir+i+".csv";
			File f = new File(fileName);
			if(!f.exists()) {
				logger.info(fileName+" file not exists!");
				continue;
			}
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				logger.info("start handle "+fileName+"  data");
				while(br.ready()) {
					String path = br.readLine();
					if(StringUtils.isEmpty(path)) {
						continue;
					}
					if(path.endsWith(".tmp")){
						continue;
					}
					boolean beginOut = false;
					List<String[]> dataList = null;
					if(Constant.END.equals(path)) {
						beginOut = true;
						newNcfileNum++;
					}else {
//						logger.info("paser file "+path);
						dataList = inHandle.readFile(path);
						currentIndex++;
					}
					outHandle.handle(dataList, new Object[]{AppContext.CACHE_ROOT+"mr_lte_hw/"+i+"/", beginOut, newNcfileNum%25==0});
					if(currentIndex%10000==0) {
						logger.info("already handle "+currentIndex);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
			}finally {
				if(br!=null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		outHandle.destroy();
		logger.info("handle done "+currentIndex);
	}
	
	public void exeLteHwLocalAnaly3(int beginNum,int endNum) {
		for(int i = beginNum;i<=endNum;i++){
			List<String> dbFiles = FileOper.getSubFiles(AppContext.CACHE_ROOT+"mr_lte_hw/"+i+"/", ".csv");
	    	for(String dbFile:dbFiles) {
	    		try {
	    			/*** LTE多补一  begin */
	    			if(dbFile.indexOf("LTE_NCELL_INFO")> 0){
	    				DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, "rst_pm_l2l_hw_nc_info");
	    			} else {
		    			DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, "rst_pm_l2l_hw_hz");
	    			}
	    			/*** LTE多补一  end */
	    		} catch (Exception e) {
	    			logger.error(e);
	    		}
	    	}
		}
	}
	
	public void exeLteHwLocalAnaly(String dir,String regex,String rname) {
		FileOper.delAllFile(rname + Constant.scanDir);
		int fileCount = getFileIndexList(dir,regex,rname);
		DataInputHandle inHandle = AppContext.getBean("XmlDataInputHandle");
		DataOutPutHandle outHandle = AppContext.getBean("LteHWCellCollectOutPutHandle");
		int currentIndex = 0;
		int newNcfileNum = 0;
		for(int i =1; i<=fileCount;i++) {
			BufferedReader br = null;
			String fileName = rname + Constant.scanDir+i+".csv";
			File f = new File(fileName);
			if(!f.exists()) {
				logger.info(fileName+" file not exists!");
				continue;
			}
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				logger.info("start handle "+fileName+"  data");
				while(br.ready()) {
					String path = br.readLine();
					if(StringUtils.isEmpty(path)) {
						continue;
					}
					if(path.endsWith(".tmp")){
						continue;
					}
					boolean beginOut = false;
					List<String[]> dataList = null;
					if(Constant.END.equals(path)) {
						beginOut = true;
						newNcfileNum++;
					}else {
//						logger.info("paser file "+path);
						businessLogDao.insertLog(6, "解析开始", 0);
						dataList = inHandle.readFile(path);
						businessLogDao.insertLog(6, "解析结束", 0);
						currentIndex++;
					}
					outHandle.handle(dataList, new Object[]{AppContext.CACHE_ROOT+rname+"_mr_lte_hw/", beginOut, newNcfileNum%25==0});
					if(currentIndex%10000==0) {
						logger.info("already handle "+currentIndex);
					}
				}
			} catch (Exception e) {
				businessLogDao.insertLog(5, "采集华为LTE文件读取文件出现异常", 1);
				e.printStackTrace();
				logger.info(e);
			}finally {
				if(br!=null) {
					try {
						br.close();
					} catch (IOException e) {
						businessLogDao.insertLog(5, "采集华为LTE文件关闭流出现异常", 1);
						e.printStackTrace();
					}
				}
			}
		}
		outHandle.destroy();
		logger.info("handle done "+currentIndex);
	    List<String> dbFiles = FileOper.getSubFiles(AppContext.CACHE_ROOT+rname+"_mr_lte_hw/", ".csv");
    	for(String dbFile:dbFiles) {
    		try {
    			/*** LTE多补一  begin */
    			if(dbFile.indexOf("LTE_NCELL_INFO")> 0){
    				DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, "rst_l2l_hw_nc_"+rname);
    			} else {
	    			DataAdapterPool.getDataAdapterPool("MainDB").getDataAdapter().loadfile(dbFile, "rst_l2l_hw_hz_"+rname);
    			}
    			/*** LTE多补一  end */
    		} catch (Exception e) {
    			businessLogDao.insertLog(5, "采集华为LTE文件数据入库出现异常", 1);
    			logger.error(e);
    		}
    	}
    	businessLogDao.insertLog(5, "采集华为LTE文件完成", 0);
	}
}
