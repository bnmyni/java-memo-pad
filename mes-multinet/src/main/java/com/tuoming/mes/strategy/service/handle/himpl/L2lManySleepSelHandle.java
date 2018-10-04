package com.tuoming.mes.strategy.service.handle.himpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.KpiCalDao;
import com.tuoming.mes.strategy.service.handle.SleepSelHandle;
import com.tuoming.mes.strategy.service.impl.SleepAreaSelectServiceImpl;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * 多补一休眠小区筛选规则
 * 上行数据流量<10M，下行数据流量<20M,PRB利用率<20%,最大用户数<20；
 * LTE补偿小区是否满足：上行数据流量<20M，下行数据流量<40M,最大用户数<40;
 */
@Component("l2lManySleepSelHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class L2lManySleepSelHandle implements SleepSelHandle {
    Map<String, Map<String, Double>> count = new HashMap<String, Map<String, Double>>();//记录补偿小区及其相对应的门限阀值
    Map<String, Integer> delCellMap = new HashMap<String, Integer>();//记录同一休眠小区是否已经被去除;
    @Autowired
    @Qualifier("kpiCalDao")
    private KpiCalDao kpiCalDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    @Override
    public String handle(List<Map<String, Object>> dataList,
                         List<Map<String, Object>> gsmDicList,
                         List<Map<String, Object>> tdDicList,
                         String cols) {
        String desc = "LTE补偿LTE多补一节能小区筛选";
        String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);//生成文件存放路径
        String fileName = rootPath + Constant.PRE_SLEEP + System.currentTimeMillis() + CsvUtil.CSV_TYPE;
        PrintStream ps = null;
        List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(cols);//获取文件输出列
        List<Map<String, Object>> filterDataList = new ArrayList<Map<String, Object>>();//记录被筛选后结果，待写入文件
        try {
            ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
            Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
            for (Map<String, Object> data : dataList) {
                String sleepArea = String.valueOf(data.get("src_enodebid")) + "_" + String.valueOf(data.get("src_localcellid"));//休眠小区标识
                if (delCellMap.containsKey(sleepArea)) {//该小区已经被筛选去除的场合，则不处理
                    continue;
                }

                //补偿小区上行数据流量门限值:60
                data.put("src_sxsjllTHR", thresholdDic.get("L2L_SXSJLL_MAKEUP"));
                //补偿小区下行数据流量门限值:100
                data.put("src_xxsjllTHR", thresholdDic.get("L2L_XXSJLL_MAKEUP"));
                //节能小区prb利用率门限值:0.2
                data.put("src_prblylTHR", thresholdDic.get("L2L_PRBLYL_SLEEP"));
                //节能小区最大用户数们门限值:20
                data.put("src_zdyhsTHR", thresholdDic.get("L2L_ZDYHS_SLEEP"));
                //补偿小区上行数据流量门限值:60
                data.put("dest_sxsjllTHR", thresholdDic.get("L2L_SXSJLL_MAKEUP"));
                //补偿小区下行数据流量门限值:100
                data.put("dest_xxsjllTHR", thresholdDic.get("L2L_XXSJLL_MAKEUP"));
                //补偿小区最大用户数们门限值:60
                data.put("dest_zdyhsTHR", thresholdDic.get("L2L_ZDYHS_MAKEUP"));

                //上行数据量
                double src_sxsjl = FormatUtil.tranferCalValue(data.get("src_sxsjll"));
                //下行数据量
                double src_xxsjl = FormatUtil.tranferCalValue(data.get("src_xxsjll"));
                //prb利用率
                double src_prblyl = FormatUtil.tranferCalValue(data.get("src_prblyl"));
                //最大用户数
                double src_zdyhs = FormatUtil.tranferCalValue(data.get("src_zdyhs"));

                //上行数据流量<30M，下行数据流量<40M,PRB利用率<20%,最大用户数<20；
                if (src_sxsjl < thresholdDic.get("L2L_SXSJLL_SLEEP") && src_xxsjl < thresholdDic.get("L2L_XXSJLL_SLEEP")
                        && src_prblyl < thresholdDic.get("L2L_PRBLYL_SLEEP") && src_zdyhs < thresholdDic.get("L2L_ZDYHS_SLEEP")) {
                    String makeUpArea = String.valueOf(data.get("dest_enodebid")) + "_" + String.valueOf(data.get("dest_cellid"));//补偿小区标识
                    if (!count.containsKey(makeUpArea)) {
                        Map<String, Double> ljzbMap = new HashMap<String, Double>();//累加指标map，记录同一补偿小区，累加对应节能小区的指标值
                        ljzbMap.put("dest_sxsjll", FormatUtil.tranferCalValue(data.get("dest_sxsjll")));
                        ljzbMap.put("dest_xxsjll", FormatUtil.tranferCalValue(data.get("dest_xxsjll")));
                        ljzbMap.put("dest_zdyhs", FormatUtil.tranferCalValue(data.get("dest_zdyhs")));
                        count.put(makeUpArea, ljzbMap);
                    }
                    //上行数据流量累计
                    double sxsjll_lj = FormatUtil.tranferCalValue(data.get("src_sxsjll")) + count.get(makeUpArea).get("dest_sxsjll");
                    //下行数据流量累计
                    double xxsjll_lj = FormatUtil.tranferCalValue(data.get("src_xxsjll")) + count.get(makeUpArea).get("dest_xxsjll");
                    //最大用户数累计
                    double zdyhs_lj = FormatUtil.tranferCalValue(data.get("src_zdyhs")) + count.get(makeUpArea).get("dest_zdyhs");
                    data.put("dest_sxsjll_lj", sxsjll_lj);
                    data.put("dest_xxsjll_lj", xxsjll_lj);
                    data.put("dest_zdyhs_lj", zdyhs_lj);

                    //上行数据流量累计值<60M，下行数据流量累计值<100M,最大用户数累计值<60；
                    if (sxsjll_lj < thresholdDic.get("L2L_SXSJLL_MAKEUP") && xxsjll_lj < thresholdDic.get("L2L_XXSJLL_MAKEUP") && zdyhs_lj < thresholdDic.get("L2L_ZDYHS_MAKEUP")) {
                        count.get(makeUpArea).put("dest_sxsjll", sxsjll_lj);
                        count.get(makeUpArea).put("dest_xxsjll", xxsjll_lj);
                        count.get(makeUpArea).put("dest_zdyhs", zdyhs_lj);

                        //记录筛选结果
                        filterDataList.add(data);
                    } else {
                        //补偿小区KPI累加不满足的场合，将该休眠小区记录到被筛选去除的Map中
                        delCellMap.put(sleepArea, 1);
                    }
                } else {
                    //休眠小区KPI不满足的场合，将该休眠小区记录到被筛选去除的Map中
                    delCellMap.put(sleepArea, 0);
                }
            }
            //循环写入筛选后的结果
            for (Map<String, Object> fData : filterDataList) {
                String sleepArea = String.valueOf(fData.get("src_enodebid")) + "_" + String.valueOf(fData.get("src_localcellid"));//休眠小区标识
                //当该选结果不在被去除的Map中时，将该结果写入文件
                if (!delCellMap.containsKey(sleepArea)) {
                    CsvUtil.writeRow(fData, ps, colList);
                }
            }
            kpiCalDao.insertL2lKpi(dataList);
            businessLogDao.insertLog(11, desc, 0);
        } catch (Exception e) {
            businessLogDao.insertLog(11, desc, 1);
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return fileName;
    }

}
