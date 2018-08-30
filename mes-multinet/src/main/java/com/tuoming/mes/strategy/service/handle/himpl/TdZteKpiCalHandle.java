package com.tuoming.mes.strategy.service.handle.himpl;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.KpiCalDao;
import com.tuoming.mes.strategy.model.KpiCalModel;
import com.tuoming.mes.strategy.service.handle.KpiCalHandle;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.FormatUtil;
@Component("tdZteKpiCalHandle")
public class TdZteKpiCalHandle implements KpiCalHandle{
	@Autowired
	@Qualifier("kpiCalDao")
	private KpiCalDao kpiCalDao;

	public String handle(KpiCalModel model) {
		String rootPath = CsvUtil
				.mkParentDir(Constant.PRE_SLEEP);// 生成文件存放路径
		String fileName = rootPath + Constant.PRE_SLEEP
				+ System.currentTimeMillis() + CsvUtil.CSV_TYPE;
		List<String> colList = (List<String>) DSLUtil.getDefaultInstance().compute(model.getColList());
		List<Map<String, Object>> dataList = kpiCalDao.quertDataBySql(model.getQuerySql(), model.getStarttime());
		PrintStream ps = null;
		try {
			ps = new PrintStream(new File(fileName),
					CsvUtil.DEFAULT_CHARACTER_ENCODING);
			for (Map<String, Object> data : dataList) {
				double yyyw = FormatUtil.tranferCalValue(data
						.get("rlc_cstraffic"));// 语音业务
				double sjll = (FormatUtil.tranferCalValue(data
						.get("rlc_psuloct")) + FormatUtil.tranferCalValue(data
						.get("rlc_psdloct"))) / 1024;// 数据流量
				/*
				 * 码资源利用率=（CR.MeanNbrAssnBruUl.R4+CR.MeanNbrAssnBruUl.Ctrl+CR.
				 * MeanNbrAssnBruDl.R4
				 * +CR.MeanNbrAssnBruDl.Mbms+CR.MeanNbrAssnBruDl.Ctrl
				 * +CR.MeanNbrAssnBruDl
				 * .Hsdpa)/(CR.NbrAvailBruUl+CR.NbrAvailBruDl)/0.75
				 */
				double mzylyl = (FormatUtil.tranferCalValue(data
						.get("cr_meannbrassnbruul_r4"))
						+ FormatUtil.tranferCalValue(data
								.get("cr_meannbrassnbruul_ctrl"))
						+ FormatUtil.tranferCalValue(data
								.get("cr_meannbrassnbrudl_r4"))
						+ FormatUtil.tranferCalValue(data
								.get("cr_meannbrassnbrudl_mbms"))
						+ FormatUtil.tranferCalValue(data
								.get("cr_meannbrassnbrudl_ctrl")) + FormatUtil
							.tranferCalValue(data
									.get("cr_meannbrassnbrudl_hsdpa")))
						/ (FormatUtil.tranferCalValue(data
								.get("cr_nbravailbruul")) + FormatUtil
								.tranferCalValue(data.get("cr_nbravailbrudl")))
						/ 0.75;
				double zdyhs = FormatUtil.tranferCalValue(data
						.get("hsdpa_maxnbruser"));
				double dyhsl = sjll / zdyhs / 15 / 60;// 单用户速率=业务流量/HSDPA最大用户数/15/60
				// CS域接通率=(rrc.succconnestab/rrc.attconnestab)*(rab.succestabcspercell/rab.attestabcspercell)
				double csyjtl = (FormatUtil.tranferCalValue(data
						.get("rrc_succconnestab")) / FormatUtil
						.tranferCalValue(data.get("rrc_attconnestab")))
						* (FormatUtil.tranferCalValue(data
								.get("rab_succestabcspercell")) / FormatUtil
								.tranferCalValue(data
										.get("rab_attestabcspercel")));
				// 电路域掉话次数=rab.relreqcspercell
				double dhcs = FormatUtil.tranferCalValue(data
						.get("rab_relreqcspercell"));
				// 电路域掉话率=rab.relreqcspercell/rab.succestabcspercell
				double dhl = FormatUtil.tranferCalValue(data
						.get("rab_relreqcspercell"))
						/ FormatUtil.tranferCalValue(data
								.get("rab_succestabcspercell"));
				// 分组域掉线次数=rab.relreqpspercell/rab.succestabpspercell
				double fzydxcs = FormatUtil.tranferCalValue(data
						.get("rab_relreqpspercell"))
						/ FormatUtil.tranferCalValue(data
								.get("rab_succestabpspercell"));
				// 总RAB拥塞次数 = rab_failestabcspercell + rab_failestabpspercell;
				double zrabyccs = FormatUtil.tranferCalValue(data
						.get("rab_failestabcspercell"))
						/ FormatUtil.tranferCalValue(data
								.get("rab_failestabpspercell"));
				// RRC连接请求次数=rrc.attconnestab
				double rrcljqcs = FormatUtil.tranferCalValue(data
						.get("rrc_attconnestab"));
				// 请求建立的电路域RAB数=RAB.AttEstabCsPerCell
				double dlyrabs = FormatUtil.tranferCalValue(data
						.get("rab_attestabcspercell"));
				// 掉线率=rab.relreqpspercell/rab.succestabpspercell
				double dxl = FormatUtil.tranferCalValue(data
						.get("rab_relreqpspercell"))
						/ FormatUtil.tranferCalValue(data
								.get("rab_succestabpspercell"));
				//ps域流量 = rlc.psuloct+rlc.psdloct
				double psyll = FormatUtil.tranferCalValue(data
						.get("rlc_psuloct"))
						/ FormatUtil.tranferCalValue(data
								.get("rlc_psdloct"));
				//切换成功率=(BHO.SuccOutIntraRncIntraFreqPerCell+BHO.SuccOutIntraRncInterFreqPerCell)/(BHO.AttOutIntraRncIntraFreqPerCell+BHO.AttOutIntraRncInterFreqPerCell)
				double qhcgl = (FormatUtil.tranferCalValue(data.get("bho_succoutintrarncintrafreqpercell"))+FormatUtil.tranferCalValue(data.get("bho_succoutintrarncinterfreqpercell")))/
						(FormatUtil.tranferCalValue(data.get("bho_attoutintrarncintrafreqpercell"))+FormatUtil.tranferCalValue(data.get("bho_attoutintrarncinterfreqpercell")));
				data.put("yyyw", yyyw);
				data.put("sjll", sjll);
				data.put("mzylyl", mzylyl);
				data.put("zdyhs", zdyhs);
				data.put("dyhsl", dyhsl);
				data.put("csyjtl", csyjtl);
				data.put("dhcs", dhcs);
				data.put("dhl", dhl);
				data.put("fzydxcs", fzydxcs);
				data.put("zrabyccs", zrabyccs);
				data.put("psyll", psyll);
				data.put("rrcljqcs", rrcljqcs);
				data.put("dlyrabs", dlyrabs);
				data.put("dxl", dxl);
				data.put("qhcgl", qhcgl);
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
