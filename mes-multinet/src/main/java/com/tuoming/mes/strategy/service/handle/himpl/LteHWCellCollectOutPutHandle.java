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
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.model.MroManyCellModel;
import com.tuoming.mes.strategy.model.MroNCellModel;
import com.tuoming.mes.strategy.model.SreportModel;
import com.tuoming.mes.strategy.service.handle.DataOutPutHandle;

@Component("LteHWCellCollectOutPutHandle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LteHWCellCollectOutPutHandle implements DataOutPutHandle {
    private static final Logger logger = LogFacade.getLog4j(LteHWCellCollectOutPutHandle.class);
    @Autowired
    @Qualifier("businessLogDao")
    private BusinessLogDao businessLogDao;

    private Map<String, MroNCellModel> map = new HashMap<>();
    private Map<String, SreportModel> relation = new HashMap<>();
    private BufferedWriter bw = null;
    // 记录本小区采样点下满足条件的邻区信息
    private List<MroManyCellModel> ncCellInfoArray = new ArrayList<MroManyCellModel>();
    private BufferedWriter ncCellInfoFile = null;

    @Override
    public void handle(List<String[]> dataList, Object... param) {
        String outPut = (String) param[0];
        boolean beginOut = (Boolean) param[1];
        boolean newFileFlag = (Boolean) param[2];
        if (dataList != null) {
            for (String[] data : dataList) {
                if (checkData(new String[]{data[MR_LteNcEarfcn_INDEX], data[MR_LteNcPci_INDEX], data[MR_LteScRSRP_INDEX], data[MR_LteNcRSRP_INDEX]})) {
                    continue;
                }
                String l2lKey = data[sid_INDEX] +
                        Constant.KEY_SEPERATOR + data[MR_LteNcEarfcn_INDEX] + Constant.KEY_SEPERATOR + data[MR_LteNcPci_INDEX];

                //object-id分为3种格式：CellId (BIT STRING (28)、CellId:Earfcn:SubFrameNbr、CellId:Earfcn:SubFrameNbr:PRBNbr
                String[] objArray = data[objectID_INDEX].split(":");
                //Enodebid
                String eNBid = null;
                //cellid
                String cellid = null;
                if (objArray.length > 2 || data[sid_INDEX].indexOf(Constant.KEY_SEPERATOR_MIDDEL) > 0) {
                    //Enodebid-cellid
                    eNBid = data[sid_INDEX].split(Constant.KEY_SEPERATOR_MIDDEL)[0];
                    cellid = data[sid_INDEX].split(Constant.KEY_SEPERATOR_MIDDEL)[1];
                } else if (objArray.length == 1) {
                    //ECI(28Bits)=eNBID(20 Bits)+CellID(8Bits)
                    Integer r = Integer.valueOf(data[sid_INDEX]);
                    String hexObjId = Integer.toHexString(r);
                    if (hexObjId.length() > 2) {
                        String hexENBid = hexObjId.substring(0, hexObjId.length() - 2);
                        String hexCellid = hexObjId.substring(hexObjId.length() - 2);
                        eNBid = String.valueOf(Integer.parseInt(hexENBid, 16));
                        cellid = String.valueOf(Integer.parseInt(hexCellid, 16));
                    }
                }
                if (map.get(l2lKey) == null) {
                    MroNCellModel mroNcell = new MroNCellModel();
                    mroNcell.setSid(data[sid_INDEX]);
                    mroNcell.setEnodebid(eNBid);
                    mroNcell.setLocalcellid(cellid);
                    mroNcell.setS_Earfcn_LTE(data[MR_LteNcEarfcn_INDEX]);
                    mroNcell.setS_Pci_LTE(data[MR_LteNcPci_INDEX]);
                    map.put(l2lKey, mroNcell);
                }
                MroNCellModel mroNcell = map.get(l2lKey);
                mroNcell.addCount2();
                mroNcell.addCount3(data[MR_LteScRSRP_INDEX], data[MR_LteNcRSRP_INDEX]);


                if (relation.get(data[sid_INDEX]) == null) {
                    relation.put(data[sid_INDEX], new SreportModel());
                }
                relation.get(data[sid_INDEX]).setS_report(data[MmeUeS1apId_INDEX] + data[timestamp_INDEX] + data[objectID_INDEX]);
                if (mroNcell.getCount2() > relation.get(data[sid_INDEX]).getS_report()) {
                    System.out.println(mroNcell.getCount2() + "====" + relation.get(data[sid_INDEX]).getS_report());
                }
                /*** LTE多补一  begin */
                //邻区信号接收功率
                int ncRSRP = Integer.parseInt(data[MR_LteNcRSRP_INDEX]);
                if (ncRSRP >= 30) {
                    MroManyCellModel mroManyCell = new MroManyCellModel();
                    mroManyCell.setEnodebid(eNBid);
                    mroManyCell.setLocalcellid(cellid);
                    mroManyCell.setMmeUeS1apId(data[MmeUeS1apId_INDEX]);
                    mroManyCell.setTimestamp(data[timestamp_INDEX]);
                    mroManyCell.setObjectID(data[objectID_INDEX]);
                    mroManyCell.setNcEarfcn(data[MR_LteNcEarfcn_INDEX]);
                    mroManyCell.setNcPci(data[MR_LteNcPci_INDEX]);
                    ncCellInfoArray.add(mroManyCell);

                }
                /*** LTE多补一  begin */
            }
        }
        if (beginOut) {
            logger.info("start out====");
            try {
                if (bw == null) {
                    String file = outPut + "MR_MRO_LteNCELL_" + ".csv";
                    FileOper.checkAndCreateForder(file);
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Constant.CHARSET));
                }
                for (Entry<String, MroNCellModel> entry : map.entrySet()) {
                    MroNCellModel model = entry.getValue();
                    StringBuilder content = new StringBuilder();
                    content.append(model.getEnodebid()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getLocalcellid()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getS_Earfcn_LTE()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getS_Pci_LTE()).append(Constant.CSV_SEPTRATOR)
                            .append(relation.get(model.getSid()).getS_report()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getCount2()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getCount3());

                    bw.write(content.toString());
                    bw.newLine();
                }
                /*** LTE多补一  begin */
                // 当输出流没有被实现的场合
                if (ncCellInfoFile == null) {
                    // 创建文件名为：MR_MRO_LTE_INVALID_CELL的csv文件
                    String file = outPut + "MR_MRO_LTE_NCELL_INFO" + System.currentTimeMillis() + ".csv";
                    logger.info("file: " + file);
                    // 当目录及文件不存在时，创建目录及文件
                    FileOper.checkAndCreateForder(file);
                    // 实例化该文件，设定编码格式为gdk
                    ncCellInfoFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Constant.CHARSET));
                } else if (newFileFlag) {
                    ncCellInfoFile.close();
                    // 创建文件名为：MR_MRO_LTE_INVALID_CELL的csv文件
                    String file = outPut + "MR_MRO_LTE_NCELL_INFO" + System.currentTimeMillis() + ".csv";
                    logger.info("file: " + file);
                    // 当目录及文件不存在时，创建目录及文件
                    FileOper.checkAndCreateForder(file);
                    // 实例化该文件，设定编码格式为gdk
                    ncCellInfoFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Constant.CHARSET));
                }
                //遍历本小区采样点Map
                for (MroManyCellModel model : ncCellInfoArray) {
                    StringBuilder content = new StringBuilder();
                    //拼接行数据：
                    content.append(model.getEnodebid()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getLocalcellid()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getMmeUeS1apId()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getTimestamp()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getObjectID()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getNcEarfcn()).append(Constant.CSV_SEPTRATOR)
                            .append(model.getNcPci());
                    //写入
                    ncCellInfoFile.write(content.toString());
                    //换行
                    ncCellInfoFile.newLine();
                }
                /*** LTE多补一  end */
            } catch (Exception e) {
                businessLogDao.insertLog(5, "采集华为LTE文件读取文件数据异常", 1);
                logger.error(e);
            } finally {
                this.map = new HashMap<String, MroNCellModel>();
                this.relation = new HashMap<String, SreportModel>();
                /*** LTE多补一  begin */
                //清空Map
                this.ncCellInfoArray = new ArrayList<MroManyCellModel>();
                /*** LTE多补一  end */
            }
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
        try {
            if (bw != null) {
                this.map = new HashMap<String, MroNCellModel>();
                this.relation = new HashMap<String, SreportModel>();
                bw.close();
                bw = null;
            }
            /*** LTE多补一  begin */
            if (ncCellInfoFile != null) {
                //关闭输出流
                ncCellInfoFile.close();
                ncCellInfoFile = null;
            }
            /*** LTE多补一  end */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
