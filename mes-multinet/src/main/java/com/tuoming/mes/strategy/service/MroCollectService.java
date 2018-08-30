package com.tuoming.mes.strategy.service;

/**
 * Mro文件主小区汇总
 * @author Administrator
 *
 */
public interface MroCollectService {
	
	public void exeLteHwLocalAnaly(String dir,String regex);
	
	/**
	 * 解析华为TD的MRO文件
	 */
	public void exeTdHwLocalAnaly(String dir,String regex);
	
	
	public void exeLteHwLocalAnaly2(int b,int e);
	
	public void exeLteHwLocalAnaly3(int b,int e);
	public void exeLteHwLocalAnaly(String dir,String regex,String rname);
	
 
}
