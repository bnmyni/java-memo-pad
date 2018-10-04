package com.tuoming.mes.strategy.service.handle.himpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.BusinessLogDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.model.SreportModel;
import com.tuoming.mes.strategy.model.TdMroManyCellModel;
import com.tuoming.mes.strategy.model.TdMroNCellModel;
import com.tuoming.mes.strategy.service.handle.DataOutPutHandle;

@Component("TdHWCellCollectOutPutHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
/**
 * 华为 TD MRO 多补一统计计算
 * @author Administrator
 *
 */
public class TdHWCellCollectOutPutHandle implements DataOutPutHandle {
    private static final Logger logger = LogFacade.getLog4j(TdHWCellCollectOutPutHandle.class);
    @Autowired
    @Qualifier("businessLogDao")
    private static BusinessLogDao businessLogDao;

    //本小区与邻区对应统计
    Map<String, TdMroNCellModel> map = new HashMap<String, TdMroNCellModel>();
    Map<String, String> allMap = new HashMap<String, String>();
    Map<String, SreportModel> relation = new HashMap<String, SreportModel>();
    BufferedWriter bw = null;
    // 记录本小区采样点下满足条件的邻区信息
    List<TdMroManyCellModel> ncCellInfoArray = new ArrayList<TdMroManyCellModel>();
    // 声明输出流
    BufferedWriter ncCellInfoFile = null;
    //指定输出csv的路径
    String targetPath = AppContext.CACHE_ROOT + "mr_td_hw/";

    @Override
    public void handle(List<String[]> dataList, Object... param) {
        this.targetPath = String.valueOf(param[0]);
        //遍历xml解析后结果
        for (String[] data : dataList) {
            //数据中存在NIL的场合，视为无效数据
            if (checkData(data)) {
                continue;
            }
            //本小区与邻区对应索引
            String t2tKey = data[TD_RNCID_INDEX] + Constant.KEY_SEPERATOR + data[TD_CELLID_INDEX] + Constant.KEY_SEPERATOR + data[TD_NCELLUARFCN_INDEX] + Constant.KEY_SEPERATOR + data[TD_NCELLSC_INDEX];
            //添加本小区与邻小区索引
            if (!map.containsKey(t2tKey)) {
                TdMroNCellModel mroNcell = new TdMroNCellModel();
                mroNcell.setScRncId(data[TD_RNCID_INDEX]);
                mroNcell.setScCellId(data[TD_CELLID_INDEX]);
                mroNcell.setNcUarfcn(data[TD_NCELLUARFCN_INDEX]);
                mroNcell.setNcSc(data[TD_NCELLSC_INDEX]);
                map.put(t2tKey, mroNcell);
            }

            //TD_MRO中，一个采样点下有多个相同邻区，所以此处将单个采样点下的邻区去重，解决单个邻区采样点个数高于采样点数的问题。
            String allkey = t2tKey + Constant.KEY_SEPERATOR + data[TD_IMSI_INDEX] + Constant.KEY_SEPERATOR + data[TD_TIMESTAMP_INDEX];
            if (!allMap.containsKey(allkey)) {
                allMap.put(allkey, "");
                //添加计算
                TdMroNCellModel mroNcell = map.get(t2tKey);
                //单个邻区采样点数
                mroNcell.addCount2();
                //单个邻区MR测量电平值 大于50的场合为满足条件邻区
                int pccpch_rscp = Integer.parseInt(data[TD_NCPCCPCHRSCP_INDEX]);
                if (pccpch_rscp > 50) {
                    //满足条件邻区个数加1
                    mroNcell.addCount3();

                    //添加邻区对应关系，满足条件邻区个数去重用
                    TdMroManyCellModel mroManyCell = new TdMroManyCellModel();
                    mroManyCell.setScRncId(data[TD_RNCID_INDEX]);
                    mroManyCell.setScCellId(data[TD_CELLID_INDEX]);
                    mroManyCell.setImsi(data[TD_IMSI_INDEX]);
                    mroManyCell.setTimestamp(data[TD_TIMESTAMP_INDEX]);
                    mroManyCell.setNcUarfcn(data[TD_NCELLUARFCN_INDEX]);
                    mroManyCell.setNcSc(data[TD_NCELLSC_INDEX]);
                    ncCellInfoArray.add(mroManyCell);
                }

            }

            //统计本小区下总采样点个数，按采样点标识去重
            String scCellKey = data[TD_RNCID_INDEX] + Constant.KEY_SEPERATOR + data[TD_CELLID_INDEX];
            if (!relation.containsKey(scCellKey)) {
                relation.put(scCellKey, new SreportModel());
            }
            //为本小区下添加采样点索引，做去重功能
            relation.get(scCellKey).setS_report(data[TD_CELLID_INDEX] + Constant.KEY_SEPERATOR + data[TD_IMSI_INDEX] + Constant.KEY_SEPERATOR + data[TD_TIMESTAMP_INDEX]);

        }
    }

    public boolean checkData(String[] param) {
        for (String p : param) {
            if (Constant.NIL.equals(p)) {
                return true;
            }
        }
        return false;

    }

    public void destroy() {
        return;
    }

    public void destroy(String targetPath) {
        this.targetPath = targetPath;
        try {
            // 当输出流没有被实现的场合
            if (bw == null) {
                // 创建文件名为：MR_MRO_TD_NCELL的csv文件
                String file = this.targetPath + "MRO_TD_NCELL_HZ.csv";
                // 当目录及文件不存在时，创建目录及文件
                FileOper.checkAndCreateForder(file);
                // 实例化该文件，设定编码格式为gdk
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Constant.CHARSET));
            }
            //循环写入行数据
            for (Entry<String, TdMroNCellModel> entry : map.entrySet()) {
                TdMroNCellModel model = entry.getValue();
                StringBuilder content = new StringBuilder();
                content.append(model.getScRncId()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getScCellId()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getNcUarfcn()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getNcSc()).append(Constant.CSV_SEPTRATOR)
                        .append(relation.get(model.getScRncId() + Constant.KEY_SEPERATOR + model.getScCellId()).getS_report()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getCount2()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getCount3());

                bw.write(content.toString());
                bw.newLine();
            }
            // 当输出流没有被实现的场合
            if (ncCellInfoFile == null) {
                // 创建文件名为：MR_MRO_TD_INVALID_CELL的csv文件
                String file = this.targetPath + "MRO_TD_NCELL_INFO.csv";
                // 当目录及文件不存在时，创建目录及文件
                FileOper.checkAndCreateForder(file);
                // 实例化该文件，设定编码格式为gdk
                ncCellInfoFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Constant.CHARSET));
            }
            //遍历本小区采样点Map
            for (TdMroManyCellModel model : ncCellInfoArray) {
                StringBuilder content = new StringBuilder();
                //拼接行数据：
                content.append(model.getScRncId()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getScCellId()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getImsi()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getTimestamp()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getNcUarfcn()).append(Constant.CSV_SEPTRATOR)
                        .append(model.getNcSc());
                //写入
                ncCellInfoFile.write(content.toString());
                //换行
                ncCellInfoFile.newLine();
            }

        } catch (Exception e) {
            businessLogDao.insertLog(5, "采集华为TD文件读取文件数据出现异常", 1);
            logger.error(e);
        } finally {
            //清空Map
            this.map = new HashMap<String, TdMroNCellModel>();
            this.relation = new HashMap<String, SreportModel>();
            this.ncCellInfoArray = new ArrayList<TdMroManyCellModel>();
            //关闭输出流
            try {
                if (bw != null) {
                    bw.close();
                }
                if (ncCellInfoFile != null) {
                    ncCellInfoFile.close();
                }
            } catch (IOException e) {
                businessLogDao.insertLog(5, "采集华为TD文件读取文件关闭流出现异常", 1);
                e.printStackTrace();
            }
        }
    }

}
