package com.tuoming.mes.strategy.service.handle.himpl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;
import com.tuoming.mes.strategy.service.handle.OverDegreeCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 计算重叠覆盖度默认业务逻辑处理器
 *
 * @author Administrator
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("defaultOverDegCalHandler")
public class DefaultOverDegCalHandler implements OverDegreeCalHandle {
    private Map<Object, Integer> count = new HashMap<>();//计数器，计算已经录入了几条符合条件的邻区

    @Override
    public void handle(List<Map<String, Object>> dataList,
                       OverlayDegreeSetting setting, PrintStream ps) {
        for (Map<String, Object> data : dataList) {//循环数据行，验证数据是否符合
            String key = DSLUtil.getDefaultInstance().buildString(setting.getSourceBs(), data);//计算源小区标识
            if (!count.containsKey(key)) {//初始化计数器
                count.put(key, 0);
            }
            if (count.get(key) < setting.getTopAmount()) {//同一个小区只取前5个
                double a = FormatUtil.tranferCalValue(data.get("count1"));//所有邻区的MR采集的采样点数
                double b = FormatUtil.tranferCalValue(data.get("count2"));//单个邻区的MR采集的点数
                double c = FormatUtil.tranferCalValue(data.get("count3"));//满足条件单个邻区的MR采集的点数
                if (this.validate(a, b, c)) {//验证改行结果是否满足条件a>1000&&b/a>0.1&&c/b>0.8
                    data.put("count2_bfb", FormatUtil.formatTwoDec(b / a));//将计算结果保存到map中
                    data.put("count3_bfb", FormatUtil.formatTwoDec(c / b));
                    data.put("count_avss_bfb", FormatUtil.formatTwoDec(c / a));
                    CsvUtil.writeRow(data, ps, setting.getColumnList());
                    count.put(key, count.get(key) + 1);
                }
            }
        }
    }


    /**
     * 验证该相邻小区是否符合录入条件
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    private boolean validate(double a, double b, double c) {
        return a > 1000 && c / a >= 0.7;
    }

    public void finalOperation(OverlayDegreeSetting setting, PrintStream ps) {
    }

}
