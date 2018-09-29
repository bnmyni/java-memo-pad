package com.tuoming.mes.strategy.service.handle;

import java.util.List;
import java.util.Map;

/**
 * 休眠小区筛选处理器
 * 判断节能小区性能指标
 * 判断补偿小区+休眠小区性能指标
 *
 * @author Administrator
 */
public interface SleepSelHandle {

    /**
     * 休眠小区筛选处理
     *
     * @param dataList
     * @param gsmDicList
     * @param tdDicList
     * @param cols
     * @return
     */
    String handle(List<Map<String, Object>> dataList,
                  List<Map<String, Object>> gsmDicList,
                  List<Map<String, Object>> tdDicList,
                  String cols);

}
