package com.tuoming.mes.strategy.service.handle.himpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.KpiCalDao;
import com.tuoming.mes.strategy.model.KpiCalModel;
import com.tuoming.mes.strategy.service.handle.KpiCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;

@Component("gsmHwKpiCalHandle")
public class GsmHwKpiCalHandle implements KpiCalHandle {
    @Autowired
    @Qualifier("kpiCalDao")
    private KpiCalDao kpiCalDao;

    @Override
    public String handle(KpiCalModel model) {
        String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);// 生成文件存放路径
        String fileName = rootPath + Constant.PRE_SLEEP
                + System.currentTimeMillis() + CsvUtil.CSV_TYPE;
        List<Map<String, Object>> hwDatas = kpiCalDao.quertDataBySql(model
                .getQuerySql(), model.getStarttime());
        PrintStream ps = null;
        try {
            List<String> colList = (List<String>) DSLUtil.getDefaultInstance()
                    .compute(model.getColList());
            ps = new PrintStream(new File(fileName),
                    CsvUtil.DEFAULT_CHARACTER_ENCODING);
            for (Map<String, Object> data : hwDatas) {
                // 无线资源利用率=(1278087438+1279270427)/(1278469488+1278469486)/0.75
                double wxzylyl = (FormatUtil.tranferCalValue(data
                        .get("counter_1278469488")) + FormatUtil
                        .tranferCalValue(data.get("counter_1278469486"))) == 0 ? 0 : (FormatUtil.tranferCalValue(data
                        .get("counter_1278087438")) + FormatUtil
                        .tranferCalValue(data.get("counter_1279270427")))
                        / (FormatUtil.tranferCalValue(data
                        .get("counter_1278469488")) + FormatUtil
                        .tranferCalValue(data.get("counter_1278469486")))
                        / 0.75;
                // TBF复用度=(1279270486+1279270490+1279270494)/1279270430
                double tbffyd = (FormatUtil.tranferCalValue(data
                        .get("counter_1279270486"))
                        + FormatUtil.tranferCalValue(data
                        .get("counter_1279270490")) + FormatUtil
                        .tranferCalValue(data.get("counter_1279270494")))
                        / FormatUtil.tranferCalValue(data
                        .get("counter_1279270430"));
                // 每线话务量=1278087438/(1278469501+1278469502/2)
                double mxhwl = FormatUtil.tranferCalValue(data
                        .get("counter_1278087438"))
                        / (FormatUtil.tranferCalValue(data
                        .get("counter_1278469501")) + FormatUtil
                        .tranferCalValue(data.get("counter_1278469502")) / 2);
                double hwl = FormatUtil.tranferCalValue(data
                        .get("counter_1278087438"));// 话务量
                // (1279178439+1279180454+1279177439+1279179453)*8/1279270427/3600
                double pdchczl = FormatUtil.tranferCalValue(data
                        .get("counter_1279270427")) == 0 ? 0 : (FormatUtil.tranferCalValue(data
                        .get("counter_1279178439"))// 单PDCH承载效率
                        + FormatUtil.tranferCalValue(data
                        .get("counter_1279180454"))
                        + FormatUtil.tranferCalValue(data
                        .get("counter_1279177439")) + FormatUtil
                        .tranferCalValue(data.get("counter_1279179453")))
                        * 8
                        / FormatUtil.tranferCalValue(data
                        .get("counter_1279270427")) / 900;
                double pdch = FormatUtil.tranferCalValue(data
                        .get("counter_1279270427"));
                // --------------------------------------------------------------------------------------------
                // 差小区：掉话数=1278072498
                double dhs = FormatUtil.tranferCalValue(data
                        .get("counter_1278072498"));
                // 掉话率=1278072498/1278087432
                double dhl = FormatUtil.tranferCalValue(data
                        .get("counter_1278087432")) == 0 ? 0 : FormatUtil.tranferCalValue(data
                        .get("counter_1278072498"))
                        / FormatUtil.tranferCalValue(data
                        .get("counter_1278087432"));
                // 指配成功率=1278075470
                double zpcgl = FormatUtil.tranferCalValue(data
                        .get("counter_1278075470")) / 100;
                // 信道初始配置数目（TCH）
                double tchxdcspz = FormatUtil.tranferCalValue(data
                        .get("counter_1278469485"));
                // RLC层流量(GB)=(1279178439+1279180454+1279177439+1279179453)/1024/1024
                double rlccll = (FormatUtil.tranferCalValue(data
                        .get("counter_1279178439"))
                        + FormatUtil.tranferCalValue(data
                        .get("counter_1279180454"))
                        + FormatUtil.tranferCalValue(data
                        .get("counter_1279177439")) + FormatUtil
                        .tranferCalValue(data.get("counter_1279179453"))) / 1024 / 1024;
                //上行TBF建立拥塞率=(1279175419+1279173419)/(1279175417+1279173417)
                double sxtbfjlysl = (FormatUtil.tranferCalValue(data
                        .get("counter_1279175417")) + FormatUtil.tranferCalValue(data
                        .get("counter_1279173417"))) == 0 ? 1 : (FormatUtil.tranferCalValue(data
                        .get("counter_1279175419")) + FormatUtil.tranferCalValue(data
                        .get("counter_1279173419"))) / (FormatUtil.tranferCalValue(data
                        .get("counter_1279175417")) + FormatUtil.tranferCalValue(data
                        .get("counter_1279173417")));
                //		下行TBF建立拥塞率=(1279176419+1279174419)/(1279176417+1279174417)
                double xxtbfjlysl = (FormatUtil.tranferCalValue(data
                        .get("counter_1279176417")) + FormatUtil.tranferCalValue(data
                        .get("counter_1279174417"))) == 0 ? 1 : (FormatUtil.tranferCalValue(data
                        .get("counter_1279176419")) + FormatUtil.tranferCalValue(data
                        .get("counter_1279174419"))) / (FormatUtil.tranferCalValue(data
                        .get("counter_1279176417")) + FormatUtil.tranferCalValue(data
                        .get("counter_1279174417")));
                //		切换成功率=(1278079528+1278081557)/(1278079527+1278081556)
                double qhcgl = (FormatUtil.tranferCalValue(data
                        .get("counter_1278079528")) + FormatUtil.tranferCalValue(data
                        .get("counter_1278081557"))) / (FormatUtil.tranferCalValue(data
                        .get("counter_1278079527")) + FormatUtil.tranferCalValue(data
                        .get("counter_1278081556")));
                //pdch占用个数
                double pdchzygs = FormatUtil.tranferCalValue(data.get("counter_1279270427"));
                data.put("hwl", hwl);
                data.put("wxzylyl", wxzylyl);
                data.put("tbffyd", tbffyd);
                data.put("mxhwl", mxhwl);
                data.put("pdchczl", pdchczl);
                data.put("dhs", dhs);
                data.put("dhl", dhl);
                data.put("zpcgl", zpcgl);
                data.put("tchxdcspz", tchxdcspz);
                data.put("rlccll", rlccll);
                data.put("pdchzygs", pdchzygs);
                data.put("sxtbfjlysl", sxtbfjlysl);
                data.put("xxtbfjlysl", xxtbfjlysl);
                data.put("qhcgl", qhcgl);
                data.put("pdch", pdch);
                CsvUtil.writeRow(data, ps, colList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return fileName;
    }

}
