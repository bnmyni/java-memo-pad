package com.tuoming.mes.strategy.service.handle;

import java.util.List;


public interface FileDecode {
	
	public List<String[]> parse(byte[] b);

}
