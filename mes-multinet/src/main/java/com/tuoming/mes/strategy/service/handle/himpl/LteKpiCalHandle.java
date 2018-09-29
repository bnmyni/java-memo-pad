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

@Component("lteKpiCalHandle")
public class LteKpiCalHandle implements KpiCalHandle {
    @Autowired
    @Qualifier("kpiCalDao")
    private KpiCalDao kpiCalDao;

    @Override
    public String handle(KpiCalModel model) {
        String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);// 生成文件存放路径
        String fileName = rootPath + Constant.PRE_SLEEP + System.currentTimeMillis() + CsvUtil.CSV_TYPE;
        List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(model.getColList());
        List<Map<String, Object>> dataList = kpiCalDao.quertDataBySql(model.getQuerySql(), model.getStarttime());
        PrintStream ps = null;
        try {
            ps = new PrintStream(new File(fileName), CsvUtil.DEFAULT_CHARACTER_ENCODING);
            for (Map<String, Object> data : dataList) {
                // ：上行数据流量
                double sxsjll = FormatUtil.tranferCalValue(data.get("pdcp_upoctul")) / 1024;
                // 下行数据流量
                double xxsjll = FormatUtil.tranferCalValue(data.get("pdcp_upoctdl")) / 1024;
                // 最大用户数
                double zdyhs = FormatUtil.tranferCalValue(data.get("rrc_connmax"));
                // 单用户速率
                double dyhsl = (FormatUtil.tranferCalValue(data
                        .get("pdcp_upoctul")) + FormatUtil.tranferCalValue(data.get("pdcp_upoctdl")))
                        / FormatUtil.tranferCalValue(data.get("rrc_connmax"));
                // PRB利用率
                double prblyl = (FormatUtil.tranferCalValue(data.get("rru_puschprbtotmeanul")) + FormatUtil
                        .tranferCalValue(data.get("rru_pdschprbtotmeandl")))
                        / (FormatUtil.tranferCalValue(data.get("rru_puschprbmeantot")) + FormatUtil
                        .tranferCalValue(data.get("rru_pdschprbmeantot")));
                // rrc建立成功率=(rrc.succconnestab/(rrc.attconnestab-rrc.attconnreestab)) 
                double rrcjlcgl = FormatUtil.tranferCalValue(data
                        .get("rrc_attconnestab")) == 0 ? 1 : FormatUtil.tranferCalValue(data
                        .get("rrc_succconnestab"))
                        / FormatUtil.tranferCalValue(data
                        .get("rrc_attconnestab"));
                // erab建立成功率= erab.nbrsuccestab/erab.nbrattestab
                double erabjlcgl = FormatUtil.tranferCalValue(data
                        .get("erab_nbrattestab")) == 0 ? 1 : FormatUtil.tranferCalValue(data
                        .get("erab_nbrsuccestab"))
                        / FormatUtil.tranferCalValue(data
                        .get("erab_nbrattestab"));
                // lte业务掉话率=（erab.nbrreqrelenb
                // -erab.nbrreqrelenb.normal+erab.hofail）/(erab.nbrsuccestab+erab.nbrleft)
                double lteywdhl = (FormatUtil.tranferCalValue(data
                        .get("erab_nbrreqrelenb"))
                        - FormatUtil.tranferCalValue(data
                        .get("erab_nbrreqrelenb_normal")) + FormatUtil
                        .tranferCalValue(data.get("erab_hofail")))
                        / (FormatUtil.tranferCalValue(data
                        .get("erab_nbrsuccestab")) + FormatUtil
                        .tranferCalValue(data.get("erab_nbrleft")));
                // 掉话次数=erab.nbrreqrelenb -erab.nbrreqrelenb.normal +erab.hofail
                double dhcs = FormatUtil.tranferCalValue(data
                        .get("erab_nbrreqrelenb"))
                        - FormatUtil.tranferCalValue(data
                        .get("erab_nbrreqrelenb_normal"))
                        + FormatUtil.tranferCalValue(data.get("erab_hofail"));
                // rab失败次数
                double rabsbcs = FormatUtil.tranferCalValue(data
                        .get("erab_nbrfailestab"));
                //上行PRB利用率 prb_utilization_ul=(rru_puschprbtotmeanul)/(rru_puschprbmeantot)"sxprblyl",
                double sxprblyl = FormatUtil.tranferCalValue(data
                        .get("rru_puschprbtotmeanul")) / FormatUtil.tranferCalValue(data
                        .get("rru_puschprbmeantot"));
                //下行PRB利用率 prb_utilization_dl= (rru_pdschprbtotmeandl)/(rru_pdschprbmeantot)"xxprblyl",
                double xxprblyl = FormatUtil.tranferCalValue(data
                        .get("rru_pdschprbtotmeandl")) / FormatUtil.tranferCalValue(data
                        .get("rru_pdschprbmeantot"));
                //系统内切换成功率=(HO.SuccOutInterEnbS1+HO.SuccOutInterEnbX2+HO.SuccOutIntraEnb)/(HO.AttOutInterEnbS1+HO.AttOutInterEnbX2+HO.AttOutIntraEnb)*100%
                double xtnqhcgl = (FormatUtil.tranferCalValue(data.get("ho_succoutinterenbs1")) + FormatUtil.tranferCalValue(data
                        .get("ho_succoutinterenbx2")) + FormatUtil.tranferCalValue(data.get("ho_succoutintraenb"))) / (FormatUtil.tranferCalValue(data
                        .get("ho_attoutinterenbs1")) + FormatUtil.tranferCalValue(data
                        .get("ho_attoutinterenbx2")) + FormatUtil.tranferCalValue(data
                        .get("ho_attoutintraenb")));
                data.put("sxsjll", sxsjll);
                data.put("xxsjll", xxsjll);
                data.put("zdyhs", zdyhs);
                data.put("dyhsl", dyhsl);
                data.put("prblyl", prblyl);

                data.put("rrcjlcgl", rrcjlcgl);
                data.put("erabjlcgl", erabjlcgl);
                data.put("lteywdhl", lteywdhl);
                data.put("dhcs", dhcs);
                data.put("rabsbcs", rabsbcs);

                data.put("sxprblyl", sxprblyl);
                data.put("xxprblyl", xxprblyl);

                data.put("xtnqhcgl", xtnqhcgl);
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
