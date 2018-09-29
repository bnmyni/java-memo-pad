package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.model.ManyOverDegCalModel;
import com.tuoming.mes.strategy.model.OverlayDegreeSetting;
import com.tuoming.mes.strategy.service.handle.OverDegreeCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 多补一覆盖度计算
 * Copyright © 2008   卓望公司
 * package: com.tuoming.mes.strategy.service.handle.himpl
 * fileName: ManyOverDegCalHandler.java
 * version: 1.0.0.0
 * author: sunke
 * date: 2018/09/15 18:06
 */
public class ManyOverDegCalHandler implements OverDegreeCalHandle {

    private Map<String, ManyOverDegCalModel> manyMap = new HashMap<String, ManyOverDegCalModel>();

    //多补一TD重叠覆盖度门限值(0-100,默认80)
    public int overDag = 80;
    //多补一TD单个邻区采样点占总采样点比例(0-100,默认30)
    public int singleNcRate = 30;


    @Override
    public void handle(List<Map<String, Object>> dataList, OverlayDegreeSetting setting, PrintStream ps) {
        for (Map<String, Object> data : dataList) {
            String key = DSLUtil.getDefaultInstance().buildString(setting.getSourceBs(), data);
            if (!manyMap.containsKey(key)) {
                manyMap.put(key, new ManyOverDegCalModel());
            }
            ManyOverDegCalModel manyInfoModel = manyMap.get(key);
            if (manyInfoModel.getCount() < setting.getTopAmount()) {
                double ncTotal = FormatUtil.tranferCalValue(data.get("count1"));
                double ncCount = FormatUtil.tranferCalValue(data.get("count2"));
                if (ncTotal > 1000 && ncCount / ncTotal * 100 > singleNcRate) {
                    assemblyQuerySql(data, manyInfoModel);
                    data.put("ncSingleRate", FormatUtil.formatTwoDec(ncCount / ncTotal));
                    manyInfoModel.addCellDataList(data);
                    manyInfoModel.addCount();
                }
            }
        }
    }


    /**
     * 当全部数据结束后，调用该方法将筛选结果写入文件
     */
    public void finalOperation(OverlayDegreeSetting setting, PrintStream ps) {
        for (Map.Entry<String, ManyOverDegCalModel> entry : manyMap.entrySet()) {
            ManyOverDegCalModel manyInfoModel = entry.getValue();
            if (manyInfoModel.getCount() > 0) {
                int trueCellCount = queryTrueCellCount(manyInfoModel.getQuerySql());

                List<Map<String, Object>> cellDataList = manyInfoModel.getCellDataList();
                if (trueCellCount / FormatUtil.tranferCalValue(cellDataList.get(0).get("count1")) * 100 > overDag) {
                    for (Map<String, Object> hisMap : cellDataList) {
                        double ncTotal = FormatUtil.tranferCalValue(hisMap.get("count1"));
                        hisMap.put("trueCellCount", trueCellCount);
                        hisMap.put("ncTotalRate", FormatUtil.formatTwoDec(trueCellCount / ncTotal));
                        CsvUtil.writeRow(hisMap, ps, setting.getColumnList());
                    }

                }
            }
        }
    }

    public int queryTrueCellCount(String sql) {
        return sql.length();
    }

    void assemblyQuerySql(Map<String, Object> data, ManyOverDegCalModel manyInfoModel) {

    }
}