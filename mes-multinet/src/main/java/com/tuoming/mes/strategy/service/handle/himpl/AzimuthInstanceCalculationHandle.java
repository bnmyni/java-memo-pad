package com.tuoming.mes.strategy.service.handle.himpl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pyrlong.configuration.ConfigurationManager;

/**
 * 方位角重叠覆盖度计算
 *
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("azimuthInstanceCalculationHandle")
public class AzimuthInstanceCalculationHandle extends AzimuthCalculationCommonHandle {

    private static final int INSTANCE_MAX = ConfigurationManager
            .getDefaultConfig()
            .getInteger("LTEMANYOVERDAG_SINGLE_NC_RATE", 30);

    /**
     * 根据距离和方位角计算小区是否符合节能小区
     *
     * @param instance 2个小区的距离
     * @return 节能小区返回True否则返回false
     */
    @Override
    public boolean validate(double instance) {
        return instance < INSTANCE_MAX;
    }
}
