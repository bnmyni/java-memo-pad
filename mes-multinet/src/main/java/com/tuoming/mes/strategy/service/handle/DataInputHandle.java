package com.tuoming.mes.strategy.service.handle;

import java.util.List;

/**
 * 数据输入处理器
 * @author Administrator
 *
 */
public interface DataInputHandle {
	public List<String[]> readFile(String filePath);
}
