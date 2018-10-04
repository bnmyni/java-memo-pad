package com.tuoming.mes.strategy.service.handle;

import java.util.List;

/**
 * 数据输出处理器
 *
 * @author Administrator
 */
public interface DataOutPutHandle {
    static final int MMCODE_INDEX = 0;
    static final int MmeGroupId_INDEX = 1;
    static final int timestamp_INDEX = 2;
    static final int sid_INDEX = 3;
    static final int MR_LteScRSRP_INDEX = 4;
    static final int MR_LteNcRSRP_INDEX = 5;
    static final int MR_LteNcEarfcn_INDEX = 6;
    static final int MR_LteNcPci_INDEX = 7;
    static final int MmeUeS1apId_INDEX = 8;
    static final int objectID_INDEX = 9;

    //华为TD MRO解析后数据索引
    static final int TD_RNCID_INDEX = 0;
    static final int TD_TIMESTAMP_INDEX = 1;
    static final int TD_IMSI_INDEX = 2;
    static final int TD_CELLID_INDEX = 3;
    static final int TD_SCPCCPCHRSCP_INDEX = 4;
    static final int TD_UTRANFREQ_INDEX = 5;
    static final int TD_SCRAMBLECODE_INDEX = 6;
    static final int TD_NCPCCPCHRSCP_INDEX = 7;
    static final int TD_NCELLUARFCN_INDEX = 8;
    static final int TD_NCELLSC_INDEX = 9;

    public void handle(List<String[]> dataList, Object... param);

    public void destroy();

}
