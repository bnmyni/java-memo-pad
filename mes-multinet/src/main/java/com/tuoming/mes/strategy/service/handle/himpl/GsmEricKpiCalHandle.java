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

/**
 * Gsm制式爱立信关键性指标处理器
 * 
 * @author Administrator
 *
 */
@Component("gsmEricKpiCalHandle")
public class GsmEricKpiCalHandle implements KpiCalHandle {
	@Autowired
	@Qualifier("kpiCalDao")
	private KpiCalDao kpiCalDao;

	public String handle(KpiCalModel model) {
		List<Map<String, Object>> ericDatas = kpiCalDao.quertDataBySql(model
				.getQuerySql(), model.getStarttime());
		String rootPath = CsvUtil.mkParentDir(Constant.PRE_SLEEP);// 生成文件存放路径
		String fileName = rootPath + Constant.PRE_SLEEP
				+ System.currentTimeMillis() + CsvUtil.CSV_TYPE;
		PrintStream ps = null;
		try {
			ps = new PrintStream(new File(fileName),
					CsvUtil.DEFAULT_CHARACTER_ENCODING);
			List<String> colList = (List<String>) DSLUtil.getDefaultInstance()
					.compute(model.getColList());
			for (Map<String, Object> data : ericDatas) {// 循环数据，计算小区相应的指标
														// 每线话务量,无线资源利用率,话务量,TBF复用度,pdch承载率
				// ((CELTCHF.TFTRALACC+CELTCHH.THTRALACC)*1.0)/CELTCHH.THNSCAN
				double hwl = ((FormatUtil
						.tranferCalValue(data.get("tftralacc"))) + (FormatUtil
						.tranferCalValue(data.get("thtralacc"))))
						/ (FormatUtil.tranferCalValue(data.get("thnscan")));
				// "((((CELTCHF.TFTRALACC+CELTCHH.THTRALACC)*1.0)/CELTCHH.THNSCAN+CELLGPRS.ALLPDCHACTACC/CELLGPRS.ALLPDCHSCAN)*1.0)/
				// ((CLTCH.TAVAACC/CLTCH.TAVASCAN)*0.75)"
				double wxzylyl = FormatUtil.tranferCalValue(data.get("tnuchcnt"))==0?0:(((FormatUtil.tranferCalValue(data
						.get("tftralacc")) + FormatUtil.tranferCalValue(data
						.get("thtralacc")))
						/ FormatUtil.tranferCalValue(data.get("thnscan"))) + (FormatUtil
						.tranferCalValue(data.get("allpdchactacc")) / FormatUtil
						.tranferCalValue(data.get("allpdchscan"))))
						/ (FormatUtil.tranferCalValue(data.get("tnuchcnt")) * 0.75);
				// (CELTCHF.TFTRALACC+CELTCHH.ThTRALACC)/CLTCH.TAVAACC
				double mxhwl = (FormatUtil.tranferCalValue(data
						.get("tftralacc")) + FormatUtil.tranferCalValue(data
						.get("thtralacc")))
						/ FormatUtil.tranferCalValue(data.get("tavaacc"));
				// "(TRAFDLGPRS.DLTBFPBPDCH+TRAFDLGPRS.DLTBFPGPDCH+TRAFDLGPRS.DLTBFPEPDCH)
				// /(TRAFDLGPRS.DLBPDCH+TRAFDLGPRS.DLGPDCH+TRAFDLGPRS.DLEPDCH) "
				double tbffyd = (FormatUtil.tranferCalValue(data
						.get("dltbfpbpdch"))
						+ FormatUtil.tranferCalValue(data.get("dltbfpgpdch")) + FormatUtil
							.tranferCalValue(data.get("dltbfpepdch")))
						/ (FormatUtil.tranferCalValue(data.get("dlbpdch"))
								+ FormatUtil.tranferCalValue(data
										.get("dlgpdch")) + FormatUtil
									.tranferCalValue(data.get("dlepdch")));

				double pdchczl = (FormatUtil
						.tranferCalValue(data.get("allpdchactacc"))==0||FormatUtil
								.tranferCalValue(data.get("allpdchscan"))==0)?0:((FormatUtil.tranferCalValue(data
						.get("ulbgegdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp1egdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp2egdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp3egdata"))
						+ FormatUtil.tranferCalValue(data.get("dlbgegdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp1egdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp2egdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp3egdata"))
						+ FormatUtil.tranferCalValue(data.get("ulbggdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp1gdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp2gdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp3gdata"))
						+ FormatUtil.tranferCalValue(data.get("dlbggdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp1gdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp2gdata")) + FormatUtil
							.tranferCalValue(data.get("dlthp3gdata"))))
						/ ((FormatUtil
								.tranferCalValue(data.get("allpdchactacc")) / FormatUtil
								.tranferCalValue(data.get("allpdchscan")))*900);// 单PDCH承载效率
				//pdch等效话务量
				double pdch = FormatUtil.tranferCalValue(data.get("allpdchactacc")) / FormatUtil
						.tranferCalValue(data.get("allpdchscan"));
				
				// 掉话次数=CELTCHFP.TFCONGPGSMSUB
				double dhs = FormatUtil.tranferCalValue(data
						.get("tfcongpgsmsub"));
				// 掉话率=celtchfp.tfcongpgsmsub/(celtchf.tfmsestb+celtchh.thmsestb)
				double dhl = (FormatUtil.tranferCalValue(data.get("tfmsestb")) + FormatUtil
						.tranferCalValue(data.get("thmsestb")))==0?0:FormatUtil.tranferCalValue(data
						.get("tfcongpgsmsub"))
						/ (FormatUtil.tranferCalValue(data.get("tfmsestb")) + FormatUtil
								.tranferCalValue(data.get("thmsestb")));
				// 指配成功率=(celtchf.tfcassall+celtchh.thcassall)/cltch.tassatt
				double zpcgl = FormatUtil.tranferCalValue(data.get("tassatt"))==0?1:(FormatUtil.tranferCalValue(data
						.get("tfcassall")) + FormatUtil.tranferCalValue(data
						.get("thcassall")))
						/ FormatUtil.tranferCalValue(data.get("tassatt"));
				// 信道初始配置数目（TCH）
				double tchxdcspz = FormatUtil.tranferCalValue(data
						.get("tnuchcnt"));

				double rlccll = (FormatUtil.tranferCalValue(data //原始kbit 现在GB
						.get("ulbgegdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp1egdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp2egdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp3egdata"))
						+ FormatUtil.tranferCalValue(data.get("dlbgegdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp1egdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp2egdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp3egdata"))
						+ FormatUtil.tranferCalValue(data.get("ulbggdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp1gdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp2gdata"))
						+ FormatUtil.tranferCalValue(data.get("ulthp3gdata"))
						+ FormatUtil.tranferCalValue(data.get("dlbggdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp1gdata"))
						+ FormatUtil.tranferCalValue(data.get("dlthp2gdata")) + FormatUtil
						.tranferCalValue(data.get("dlthp3gdata"))) / 8 / 1024 / 1024;
				//上行tbf建立成功率：	1-(cellgprs2.prejtfi+cellgprs2.prejoth)/cellgprs2.pschreq								
				//下行tbf建立成功率	1-cellgprs.faildltbfest/cellgprs.dltbfest								
				//切换成功率	(ncellrel.hoversuc+necellrel.hoversuc)/(ncellrel.hovercnt+necellrel.hovercnt)								
				double sxtbfjlysl = 1-(FormatUtil.tranferCalValue(data.get("prejtfi"))
						+FormatUtil.tranferCalValue(data.get("prejoth")))/FormatUtil.tranferCalValue(data.get("pschreq"));
				double xxtbfjlysl = 1-FormatUtil.tranferCalValue(data.get("faildltbfest"))
						/FormatUtil.tranferCalValue(data.get("dltbfest"));
				double qhcgl = (FormatUtil.tranferCalValue(data.get("hoversuc"))
						+FormatUtil.tranferCalValue(data.get("hoversuc")))/(FormatUtil.tranferCalValue(data.get("hovercnt"))
						+FormatUtil.tranferCalValue(data.get("hovercnt")));
				//pdch占用个数
				double pdchzygs = FormatUtil.tranferCalValue(data.get("allpdchactacc"))/FormatUtil.tranferCalValue(data.get("allpdchscan"));						
				if (data.get("cgi") != null) {// 将计算出的指标存入map中，并输出到文件，eric没有明确定义lac和ci，需要通过cgi解析获得
					String[] cgi = String.valueOf(data.get("cgi")).split("-");
					data.put("lac", cgi[2]);
					data.put("ci", cgi[3]);
				}
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
