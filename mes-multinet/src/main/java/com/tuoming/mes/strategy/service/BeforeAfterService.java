package com.tuoming.mes.strategy.service;

public interface BeforeAfterService {
	/**
	 * 程序运行前和运行后执行的操作
	 * 
	 * @param groupName
	 */
	public void executeBeforeOrAfter(String groupName);
	
	/**
	 * 清除服务器下载的数据
	 * @param day
	 */
	public void cleanServerFile(int day);

	/**
	 * 重新发送失败的采集命令
	 */
	public void reCollectFailCommand(int times);
	
	/**
	 * 是否采集mr数据
	 * @param type
	 * @param days
	 * @return
	 */
	boolean sfCollectMr(String type, int days);

	/**
	 * 删除mr文件
	 */
	public void cleanMrFile();
	
	/**
	 * 是否计算重叠覆盖度
	 * @param type
	 * @return
	 */
	public boolean sfCalOverDegree(String type);
	
	public void calOverDegreeDone(String type);
}
