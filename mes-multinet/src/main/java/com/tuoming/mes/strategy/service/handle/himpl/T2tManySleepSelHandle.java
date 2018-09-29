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
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

/**
 * t2t多补一休眠小区筛选
 * (1)、判断TDS小区的下一时段期望业务量是否满足：语音业务<Y1,数据流量<S1,码资源利用率<C1,最大用户数<U1；
 * (2)、判断将满足（1）条件的TDS小区下一时段预测业务转加到对应的TDS补偿小区中后，TDS补偿小区是否满足：语音业务<Y2,数据流量<S2,码资源利用率<C2,最大用户数<U2;
 * (3)、多补一的休眠或补偿小区不符合KIP阀值判断的场合，去除该休眠区的所有多补一数据
 * （注:如果出现一个补偿小区补偿多个小区业务时,应将所有补偿业务累加评估。）
 *
 * @author Administrator
 */
@Component("t2tManySleepSelHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class T2tManySleepSelHandle implements SleepSelHandle {
    @Autowired
    @Qualifier("kpiCalDao")
    private KpiCalDao kpiCalDao;
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;
    Map<String, Map<String, Double>> count = new HashMap<String, Map<String, Double>>();//记录补偿小区及其相对应的门限阀值
    Map<String, Integer> delCellMap = new HashMap<String, Integer>();//记录同一休眠小区是否已经被去除;


    @Override
    public String handle(List<Map<String, Object>> dataList,
                         List<Map<String, Object>> gsmDicList,
                         List<Map<String, Object>> tdDicList,
                         String cols) {
        String desc = "TD补偿TD多补一节能小区筛选";
        String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);//生成文件存放路径
        String fileName = rootPath + Constant.PRE_SLEEP + System.currentTimeMillis() + CsvUtil.CSV_TYPE;
        List<Map<String, Object>> filterDataList = new ArrayList<Map<String, Object>>();//记录被筛选后结果，待写入文件
        PrintStream ps = null;
        List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(cols);
        try {
            ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
            for (Map<String, Object> data : dataList) {
                String sleepArea = String.valueOf(data.get("src_lac")) + "_" + String.valueOf(data.get("src_lcid"));//休眠小区标识
                if (delCellMap.containsKey(sleepArea)) {//该小区已经被筛选去除的场合，则不处理
                    continue;
                }

                double src_zbpz = FormatUtil.tranferCalValue(data.get("src_hzbpzs"));//获取TD休眠小区H载波配置数
                double dest_zbpz = FormatUtil.tranferCalValue(data.get("dest_hzbpzs"));//获取TD补偿小区H载波配置数
                Map<String, Double> srcDic = this.calTdDic(tdDicList, src_zbpz);//获取休眠小区门限字典
                Map<String, Double> destDic = this.calTdDic(tdDicList, dest_zbpz);//获取补偿小区门限字典
                data.put("src_yyywTHR", srcDic.get("y1"));//语音业务量阀值
                data.put("src_sjllTHR", srcDic.get("s1"));//数据流量阀值
                data.put("src_mzylylTHR", srcDic.get("c1"));//码资源利用率阀值
                data.put("src_zdyhsTHR", srcDic.get("u1"));//最大用户数阀值
                data.put("dest_yyywTHR", destDic.get("y2"));
                data.put("dest_sjllTHR", destDic.get("s2"));
                data.put("dest_mzylylTHR", destDic.get("c2"));
                data.put("dest_zdyhsTHR", destDic.get("u2"));


                double src_yyyw = FormatUtil.tranferCalValue(data.get("src_yyyw"));//源小区语音业务量
                double src_sjll = FormatUtil.tranferCalValue(data.get("src_sjll"));//数据流量
                double src_mzylyl = FormatUtil.tranferCalValue(data.get("src_mzylyl"));//码资源利用率
                double src_zdyhs = FormatUtil.tranferCalValue(data.get("src_zdyhs"));//最大用户数

                //参照T2G中对休眠T的判断，只判断语音业务量、数据流量、码资源利用率
                //语音业务<Y1,数据流量<S1,码资源利用率<C1
                if (src_yyyw < srcDic.get("y1") && src_sjll < srcDic.get("s1") && src_mzylyl < srcDic.get("c1")) {

                    String makeUpArea = String.valueOf(data.get("dest_lac")) + "_" + String.valueOf(data.get("dest_lcid"));//补偿小区标识
                    if (!count.containsKey(makeUpArea)) {//假如该补偿小区没有被验证过，则获取该小区语音业务,码资源利用率,最大用户数,数据流量的阀值
                        destDic.put("dest_sjll", FormatUtil.tranferCalValue(data.get("dest_sjll")));//补偿小区数据流量
                        destDic.put("dest_zdyhs", FormatUtil.tranferCalValue(data.get("dest_zdyhs")));//补偿小区最大用户数
                        count.put(makeUpArea, destDic);
                    }
                    double yyyw_dest = FormatUtil.tranferCalValue(data.get("dest_yyyw"));//参照原L2t对TD补偿小区的筛选，语音业务量不进行累加
                    double mzylyl_dest = FormatUtil.tranferCalValue(data.get("dest_mzylyl"));//参照原L2t对TD补偿小区的筛选，码资源利用不进行累加
                    //合计：数据流量
                    double sjll_total = FormatUtil.tranferCalValue(data.get("src_sjll")) + count.get(makeUpArea).get("dest_sjll");
                    //合计：最大用户数
                    double zdyhs_total = FormatUtil.tranferCalValue(data.get("src_zdyhs")) + count.get(makeUpArea).get("dest_zdyhs");
                    data.put("dest_sjll_lj", sjll_total);
                    data.put("dest_zdyhs_lj", zdyhs_total);
                    //
                    if (yyyw_dest < count.get(makeUpArea).get("y2") //语音业务量
                            && mzylyl_dest < count.get(makeUpArea).get("c2") //码资源利用率
                            && sjll_total < count.get(makeUpArea).get("s2") //数据流量
                            && zdyhs_total < count.get(makeUpArea).get("u2")) { //最大用户数
                        count.get(makeUpArea).put("dest_sjll", sjll_total);
                        count.get(makeUpArea).put("dest_zdyhs", zdyhs_total);
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
                String sleepArea = String.valueOf(fData.get("src_lac")) + "_" + String.valueOf(fData.get("src_lcid"));//休眠小区标识
                //当该选结果不在被去除的Map中时，将该结果写入文件
                if (!delCellMap.containsKey(sleepArea)) {
                    CsvUtil.writeRow(fData, ps, colList);
                }
            }
            kpiCalDao.insertT2TKpi(dataList);
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

    /**
     * 计算td制式的数据字典
     *
     * @param dicTdList
     * @param src_zbpz
     */
    public static Map<String, Double> calTdDic(List<Map<String, Object>> dicTdList, double trx_h) {
        long trx = Math.round(trx_h);
        Map<String, Double> dicMap = new HashMap<String, Double>();
        for (Map<String, Object> dic : dicTdList) {
            int trx_h_max = (Integer) (dic.get("trx_h_max"));
            int trx_h_min = (Integer) (dic.get("trx_h_min"));
            if ((trx_h_min == trx_h_max && trx == trx_h_min) || (trx >= trx_h_min && trx <= trx_h_max)) {
                dicMap.put("y1", FormatUtil.tranferCalValue(dic.get("y1")));
                dicMap.put("y2", FormatUtil.tranferCalValue(dic.get("y2")));
                dicMap.put("s1", FormatUtil.tranferCalValue(dic.get("s1")));
                dicMap.put("s2", FormatUtil.tranferCalValue(dic.get("s2")));
                dicMap.put("c1", FormatUtil.tranferCalValue(dic.get("c1")));
                dicMap.put("c2", FormatUtil.tranferCalValue(dic.get("c2")));
                dicMap.put("c3", FormatUtil.tranferCalValue(dic.get("c3")));
                dicMap.put("c4", FormatUtil.tranferCalValue(dic.get("c4")));
                dicMap.put("u1", FormatUtil.tranferCalValue(dic.get("u1")));
                dicMap.put("u2", FormatUtil.tranferCalValue(dic.get("u2")));
                dicMap.put("u3", FormatUtil.tranferCalValue(dic.get("u3")));
                dicMap.put("u4", FormatUtil.tranferCalValue(dic.get("u4")));
                return dicMap;
            }

        }
        return null;
    }
}
