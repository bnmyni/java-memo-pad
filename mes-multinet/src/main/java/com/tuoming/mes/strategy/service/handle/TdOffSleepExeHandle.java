package com.tuoming.mes.strategy.service.handle;

import java.util.List;
import java.util.Map;
import com.tuoming.mes.strategy.model.SleepExeSetting;

public interface TdOffSleepExeHandle {
    public void tdOffHandle(List<Map<String, Object>> dataList, int top, SleepExeSetting set, Map<String, Integer> rncCount);
}
