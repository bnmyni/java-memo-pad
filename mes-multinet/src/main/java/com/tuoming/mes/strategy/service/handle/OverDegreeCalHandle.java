package com.tuoming.mes.strategy.service.handle;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;

/**
 * 重叠覆盖度业务处理接口
 *
 * @author Administrator
 */
public interface OverDegreeCalHandle {

    void handle(List<Map<String, Object>> dataList, OverlayDegreeSetting setting, PrintStream ps);

    void finalOperation(OverlayDegreeSetting setting, PrintStream ps);

}
