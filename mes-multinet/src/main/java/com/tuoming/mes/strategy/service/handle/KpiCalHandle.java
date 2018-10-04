package com.tuoming.mes.strategy.service.handle;

import com.tuoming.mes.strategy.model.KpiCalModel;

/**
 * 关键性指标计算处理器
 *
 * @author Administrator
 */
public interface KpiCalHandle {

    /**
     * 关键性指标处理方法
     *
     * @param model
     * @return
     */
    String handle(KpiCalModel model);

}
