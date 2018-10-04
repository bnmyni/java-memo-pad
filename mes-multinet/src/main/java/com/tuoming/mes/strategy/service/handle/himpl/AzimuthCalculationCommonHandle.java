package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;
import com.tuoming.mes.strategy.service.handle.OverDegreeCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;
import com.tuoming.mes.strategy.util.HarvenSin;

/**
 * Azimuth Calculation 计算通用实现
 * Copyright © 2008   卓望公司
 * package: com.tuoming.mes.strategy.service.handle.himpl
 * fileName: AzimuthCalculationCommonHandle.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/15 17:35
 */
public class AzimuthCalculationCommonHandle implements OverDegreeCalHandle {

    /**
     * 根据源经纬度和目的经纬度坐标求距离
     *
     * @param src_longitude  　　　源经度
     * @param src_latitude   　　　　源纬度
     * @param dest_longitude 　　　邻区经度
     * @param dest_latitude  　　　邻区纬度
     * @return double　2个点的距离
     */
    public static double getInstance(double src_longitude,
                                     double src_latitude, double dest_longitude, double dest_latitude) {
        double s = HarvenSin.distance(src_longitude, src_latitude, dest_longitude, dest_latitude);
        return Double.parseDouble(FormatUtil.formatTwoDec(s));
    }

    @Override
    public void handle(List<Map<String, Object>> dataList, OverlayDegreeSetting setting, PrintStream ps) {
        Map<String, Integer> count = new HashMap<>();
        for (Map<String, Object> data : dataList) {
            String key = DSLUtil.getDefaultInstance().buildString(setting.getSourceBs(), data);
            double srcLongitude = FormatUtil.tranferCalValue(data.get("src_longitude"));
            double srcLatitude = FormatUtil.tranferCalValue(data.get("src_latitude"));
            double destLongitude = FormatUtil.tranferCalValue(data.get("dest_longitude"));
            double destLatitude = FormatUtil.tranferCalValue(data.get("dest_latitude"));
            //计算源小区和邻区距离
            double instance = AzimuthCalculationHandle.getInstance(srcLongitude,
                    srcLatitude, destLongitude, destLatitude);
            if (!count.containsKey(key)) {
                count.put(key, 0);
            }
            if (count.get(key) < setting.getTopAmount() || setting.getTopAmount() < 0) {
                if (this.validate(instance)) {
                    data.put("rst_instance", instance);
                    CsvUtil.writeRow(data, ps, setting.getColumnList());
                    count.put(key, count.get(key) + 1);
                }
            }
        }
    }

    @Override
    public void finalOperation(OverlayDegreeSetting setting, PrintStream ps) {

    }

    /**
     * 根据距离和方位角计算小区是否符合节能小区,默认距离小于30时返回true
     *
     * @param instance 2个小区的距离
     * @return 节能小区返回True否则返回false
     */
    public boolean validate(double instance) {
        return instance < 30;
    }
}