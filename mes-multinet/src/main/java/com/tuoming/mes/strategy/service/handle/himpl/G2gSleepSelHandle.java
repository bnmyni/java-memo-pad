package com.tuoming.mes.strategy.service.handle.himpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintStream;
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
 * （1）、判断GSM1800小区的下一时段期望业务量是否满足：每线话务量<0.2,无线资源利用率<30%,话务量<3erl,TBF复用度<4；
 * (2)、判断将满足（1）条件的GSM1800小区下一时段预测业务转加到对应的GSM900补偿小区中后，GSM900补偿小区是否满足：每线话务量<M2，无线资源利用率<W2;
 *
 * @author Administrator
 */
@Component("g2gSleepSelHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class G2gSleepSelHandle implements SleepSelHandle {
    Map<String, Map<String, Double>> count = new HashMap<String, Map<String, Double>>();//记录补偿小区及其相对应的门限阀值
    Map<String, Integer> sleepMap = new HashMap<String, Integer>();//记录同一休眠小区是否已经休眠;
    @Autowired
    @Qualifier("kpiCalDao")
    private KpiCalDao kpiCalDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    /**
     * 根据tch计算该gsm小区使用的门限字典
     *
     * @param dicGsmLists
     * @return
     */
    public static Map<String, Double> calGsmDic(List<Map<String, Object>> dicGsmLists, double tch) {
        Map<String, Double> dicMap = new HashMap<String, Double>();
        for (Map<String, Object> dic : dicGsmLists) {
            double tch_min = FormatUtil.tranferCalValue(dic.get("tch_min"));
            double tch_max = Integer.MAX_VALUE;
            if (dic.get("tch_max") != null) {
                tch_max = FormatUtil.tranferCalValue(dic.get("tch_max"));
            }
            if (tch >= tch_min && tch < tch_max) {
                dicMap.put("m2", FormatUtil.tranferCalValue(dic.get("m2")));
                dicMap.put("w2", FormatUtil.tranferCalValue(dic.get("w2")));
                dicMap.put("m3", FormatUtil.tranferCalValue(dic.get("m3")));
                dicMap.put("w3", FormatUtil.tranferCalValue(dic.get("w3")));
                dicMap.put("m4", FormatUtil.tranferCalValue(dic.get("m4")));
                dicMap.put("w4", FormatUtil.tranferCalValue(dic.get("w4")));
                return dicMap;
            }
        }
        return null;
    }

    public String handle(List<Map<String, Object>> dataList,
                         List<Map<String, Object>> gsmDicList,
                         List<Map<String, Object>> tdDicList,
                         String cols) {
        String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);//生成文件存放路径
        String fileName = rootPath + Constant.PRE_SLEEP + System.currentTimeMillis() + CsvUtil.CSV_TYPE;
        PrintStream ps = null;
        List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(cols);
        String desc = "GSM900补偿GSM1800一补一节能小区筛选";
        try {
            ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
            Map<String, Double> thresholdDic = SleepAreaSelectServiceImpl.getSleepNotifyDic();
            for (Map<String, Object> data : dataList) {
                Map<String, Double> dic1 = this.calGsmDic(gsmDicList, FormatUtil.tranferCalValue(data.get("dest_tchxdcspz")));
                data.put("dest_mxhwlTHR", dic1.get("w2"));
                data.put("dest_wxzylylTHR", dic1.get("m2"));
                data.put("src_mxhwlTHR", thresholdDic.get("G2G_MXHWL_SLEEP"));
                data.put("src_wxzylylTHR", thresholdDic.get("G2G_WXZYLYL_SLEEP"));
                data.put("src_hwlTHR", thresholdDic.get("G2G_HWL_SLEEP"));
                data.put("src_tbffydTHR", thresholdDic.get("G2G_TBFFYD_SLEEP"));
                data.put("dest_mxhwlTHR", thresholdDic.get("G2G_WXZYLYL_SLEEP"));
                data.put("src_wxzylylTHR", thresholdDic.get("G2G_WXZYLYL_SLEEP"));
                String sleepArea = String.valueOf(data.get("src_lac")) + "_" + String.valueOf(data.get("src_ci"));//休眠小区标识
                if (sleepMap.containsKey(sleepArea)) {//假如该小区已经是休眠小区，则不处理
                    continue;
                }
                double src_mxhwl = FormatUtil.tranferCalValue(data.get("src_mxhwl"));
                double src_wxzylyl = FormatUtil.tranferCalValue(data.get("src_wxzylyl"));
                double src_hwl = FormatUtil.tranferCalValue(data.get("src_hwl"));
                double src_tbffud = FormatUtil.tranferCalValue(data.get("src_tbffud"));
                if (!(src_mxhwl < thresholdDic.get("G2G_MXHWL_SLEEP") && src_wxzylyl < thresholdDic.get("G2G_WXZYLYL_SLEEP") && src_hwl < thresholdDic.get("G2G_HWL_SLEEP") && src_tbffud < thresholdDic.get("G2G_TBFFYD_SLEEP"))) {//每线话务量<0.2,无线资源利用率<30%,话务量<3erl,TBF复用度<4
                    continue;
                }
                String makeUpArea = String.valueOf(data.get("dest_lac")) + "_" + String.valueOf(data.get("dest_ci"));//补偿小区标识
                if (!count.containsKey(makeUpArea)) {//假如该补偿小区没有被验证过，则获取该小区无线资源利用率和每线话务量的阀值
                    Map<String, Double> dic = this.calGsmDic(gsmDicList, FormatUtil.tranferCalValue(data.get("dest_tchxdcspz")));
                    dic.put("dest_mxhwl", FormatUtil.tranferCalValue(data.get("dest_mxhwl")));
                    dic.put("dest_wxzylyl", FormatUtil.tranferCalValue(data.get("dest_wxzylyl")));
                    dic.put("dest_hwl", FormatUtil.tranferCalValue(data.get("dest_hwl")));
                    count.put(makeUpArea, dic);
                }
                //判断该补偿小区无线资源利用率+节能小区累计无线资源利用率<w2; 每线话务量+节能小区累计每线话务量<m2
                if (count.get(makeUpArea) != null) {
                    double mxhwl_lj = (FormatUtil.tranferCalValue(data.get("src_hwl")) + count.get(makeUpArea).get("dest_hwl")) / FormatUtil.tranferCalValue(data.get("dest_tchxdcspz"));
                    double wxzylyl_lj = (FormatUtil.tranferCalValue(data.get("src_hwl")) + count.get(makeUpArea).get("dest_hwl") + FormatUtil.tranferCalValue(data.get("src_pdch")) + FormatUtil.tranferCalValue(data.get("dest_pdch"))) / FormatUtil.tranferCalValue(data.get("dest_tchxdcspz")) / 0.67;
                    data.put("dest_mxhwl_lj", mxhwl_lj);
                    data.put("dest_wxzylyl_lj", wxzylyl_lj);
                    if (mxhwl_lj < count.get(makeUpArea).get("m2") && wxzylyl_lj < count.get(makeUpArea).get("w2")) {
                        count.get(makeUpArea).put("dest_mxhwl", mxhwl_lj);
                        count.get(makeUpArea).put("dest_wxzylyl", wxzylyl_lj);
                        sleepMap.put(sleepArea, null);
                        CsvUtil.writeRow(data, ps, colList);
                    }
                }

            }
            kpiCalDao.insertGsmKpi(dataList);
            //记录日志
            businessLogDao.insertLog(11, desc, 0);
        } catch (Exception e) {
            //记录日志
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
