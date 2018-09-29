package com.tuoming.mes.strategy.consts;

public abstract class Constant {
    // 制式
    public static final String GSM = "GSM";
    public static final String LTE = "LTE";
    public static final String TD = "TD";

    public static final String L2L = "l2l";
    public static final String L2T = "l2t";
    public static final String G2G = "g2g";
    public static final String T2G = "t2g";

    public static final String HW = "华为";
    public static final String ZTE = "中兴";
    public static final String ERIC = "爱立信";

    public static final String SLEEP = "SLEEP";
    public static final String NOTIFY = "NOTIFY";
    public static final String APP_MULTINET = "MULTINET";

    public static final long CURRENT_BATCH = 1;

    public static final String REASON_WEAK_ZB = "差指标";
    public static final String REASON_NOTIFY_FALI = "唤醒失败";
    public static final String REASON_SLEEP_FALI = "休眠失败";

    public static final String MONTIOR_COLLECT_STATE_URL = "MONTIOR_COLLECT_STATE_URL";
    public static final String PM = "PM";
    public static final String CM = "CM";
    public static final String MRO = "MRO";
    public static final String DAYUNIT = "DAYUNIT";
    public static final String MROUNIT = "MROUNIT";
    public static final String MINUTEUNIT = "MINUTEUNIT";
    public static final String MONTIOR_TYPE_COLLECT = "COLLECT";

    public static final String MR = "mr";
    public static final String AZIMUTH = "azimuth";

    public static final int FTP_COLLECT_BEGIN_LOGIN = 0;
    public static final int FTP_COLLECT_LOGIN_SUCCESS = 11;
    public static final int FTP_COLLECT_LOGIN_FAIL = 12;
    public static final int FTP_COLLECT_DOWNLOAD_END = 13;
    public static final int FTP_COLLECT_PARSE_END = 14;
    public static final int FTP_COLLECT_END = 1;
    public static final int Log_COLLECT_BEGIN_LINK = 0;
    public static final int Log_COLLECT_LINK_SUCCESS = 11;
    public static final int Log_COLLECT_LINK_FAIL = 12;
    public static final int Log_COLLECT_DONWLOAD_END = 13;
    public static final int Log_COLLECT_ANALYZE_END = 14;
    public static final int Log_COLLECT_END = 1;

    public static final String PRE_SLEEP = "sleep_";// 休眠小区筛选业务前缀,生成临时表或文件
    public static final String NEXT_RES_PRE = "rst_fcastnext_";// 下一时刻预测结果表前缀

    public static final String PER_LD_DAY = "day";
    public static final String PER_LD_HOUR = "hour";
    public static final String RES_SUFFIX_DAY = "_day";
    public static final String RES_SUFFIX_HOUR = "_hour";
    public static final String TIMEFORMAT_DAY = "'%Y-%m-%d'";
    public static final String TIMEFORMAT_HOUR = "'%Y-%m-%d %H'";
    public static final String TELNET = "TELNET";
    public static final String FTP = "FTP";
    public static final String MULTINET_SLEEP_FLAG = "MULTINET_SLEEP_FLAG";
    public static final String CURRENT_COLLECTTIME = "CURRENT_COLLECTTIME";
    public static final String CURRENT_TIME_DELAY = "CURRENT_TIME_DELAY";//延迟后任务开启系统时间
    public static final String KEY_GROUP_NAME = "GROUPNAME";// 要执行配置表中配置数据的组的key
    public static final String[] BUSTYPEARR = new String[]{Constant.L2L,
            Constant.T2L, Constant.T2G, Constant.G2G, Constant.T2T_MANY};
    public static final String KPI_CAL_REAL = "KPI_CAL_REAL";
    public static final String KPI_CAL_HIS = "KPI_CAL_HIS";
    public static final String KPI_CAL_PERF_CELL_MIN = "KPI_CAL_PERF_CELL_MIN";
    public static final String KPI_CAL_PERF_CELL_HOUR = "KPI_CAL_PERF_CELL_HOUR";
    public static final String KPI_CAL_PERF_CELL_DAY = "KPI_CAL_PERF_CELL_DAY";
    public static final String KPI_CAL_PERF_BSC_MIN = "KPI_CAL_PERF_BSC_MIN";
    public static final String KPI_CAL_PERF_BSC_HOUR = "KPI_CAL_PERF_BSC_HOUR";
    public static final String KPI_CAL_PERF_BSC_DAY = "KPI_CAL_PERF_BSC_DAY";
    public static final String KPI_CAL_PERF_ALL_MIN = "KPI_CAL_PERF_ALL_MIN";
    public static final String KPI_CAL_PERF_ALL_HOUR = "KPI_CAL_PERF_ALL_HOUR";
    public static final String KPI_CAL_PERF_ALL_DAY = "KPI_CAL_PERF_ALL_DAY";
    public static final String KPI_CAL_MINUTE_TIME = "KPI_CAL_MINUTE_TIME";

    public static final String AFTER_COLLECT_DATA_UPDATE_CARRIER_CM = "AFTER_COLLECT_DATA_UPDATE_CARRIER_CM";
    public static final String AFTER_COLLECT_DATA_UPDATE_CARRIER_PM = "AFTER_COLLECT_DATA_UPDATE_CARRIER_PM";
    public static final String BEFORE_COLLECT_DATA_CLEAN_PM = "BEFORE_COLLECT_DATA_CLEAN_PM";
    public static final String BEFORE_COLLECT_DATA_CLEAN_CM = "BEFORE_COLLECT_DATA_CLEAN_CM";
    public static final String BEFORE_COLLECT_DATA_CLEAN_MR = "BEFORE_COLLECT_DATA_CLEAN_MR";
    public static final String AFTER_COLLECT_DATA_HANDLE_PM = "AFTER_COLLECT_DATA_HANDLE_PM";
    public static final String BEFORE_COLLECT_DATA_ADD_BATCHID = "BEFORE_COLLECT_DATA_ADD_BATCHID";
    public static final String BEFORE_COLLECT_DATA_CLEAN_CARRIER = "BEFORE_COLLECT_DATA_CLEAN_CARRIER";
    public static final String BEFORE_COLLECT_DATA_CLEAN_ALARM = "BEFORE_COLLECT_DATA_CLEAN_ALARM";
    public static final String AFTER_COLLECT_DATA_HANDLE_MR = "AFTER_COLLECT_DATA_HANDLE_MR";
    public static final String BEFORE_COLLECT_DATA_CLEAN_PERF = "BEFORE_COLLECT_DATA_CLEAN_PERF";
    public static final String AFTER_COLLECT_DATA_HANDLE_CM = "AFTER_COLLECT_DATA_HANDLE_CM";
    public static final String AFTER_COLLECT_DATA_IMPORTDATA_CM = "AFTER_COLLECT_DATA_IMPORTDATA_CM";
    public static final String CM_COLLECT_DIFFER_NUM = "CM_COLLECT_DIFFER_NUM";

    public static final int PM_COLLECT_LD = 15;// PM采集粒度
    public static final int PM_COLLECT_DELAY = 6;// PM采集延迟时间
    public static final String START_PERIOD = "start_period";// 节能开始时间
    public static final String BEGIN_PERIOD = "begin_period";// 节能结束时间
    public static final String GROUP_NOTIFY_ALL = "notify_all";
    public static final String DEFAULT_NOTIFY_HANDLE = "notifyHandle";
    public static final String MES_MRO_COLLECT_START = "MES_MRO_COLLECT_START";// mro采集开始日期
    public static final String MES_MRO_COLLECT_END = "MES_MRO_COLLECT_END";// mro采集结束日期
    public static final String TIMEPARTICLE = "TIMEPARTICLE";
    public static final String ENABLE_MONTIOR = "ENABLE_MONTIOR";
    public static final String ENABLE_EXE_NOTIFY = "ENABLE_EXE_NOTIFY";
    public static final String ENABLE_EXE_SLEEP = "ENABLE_EXE_SLEEP";
    public static final String CURRENT_BATCH_KEY = "CURRENT_BATCH_KEY";
    public static final String AFTER_COLLECT_DATA_TD_UPDATE_RNC = "AFTER_COLLECT_DATA_TD_UPDATE_RNC";
    public static final String AFTER_COLLECT_DATA_UPDATE_NETELE = "AFTER_COLLECT_DATA_UPDATE_NETELE";
    public static final String CURRENT_BATCH_MR = "CURRENT_BATCH_MR";
    public static final String PREFIX_LSB = "rst_";

    public static final String NIL = "NIL";
    public static final String ZERO = "0";
    public static final String CSV_SEPTRATOR = ",";
    public static final String BLANK = "";
    public static final String KEY_SEPERATOR = "_";
    public static final String KEY_SEPERATOR_MIDDEL = "-";
    public static final String CHARSET = "gbk";
    public static final String Y = "Y";
    public static final String N = "N";
    public static final String scanDir = "scanDir/";
    public static final String END = "END";

    /****************Neusoft*********************/
    //t2l覆盖度
    public static final String TAB_SUFIX = "_exceed";
    public static final String INSTANCE_AZIMUTH = "_instance_azimuth";
    public static final String T2L = "t2l";
    public static final String[] TD_BUSTYPEARR = new String[]{"azimuth_t2g", "azimuth_t2l"};
    public static final String TD_NETWORK_OFF = "td_off";
    public static final String TD_OFF_PERMANENCE = "td_off_permanence";
    public static final String TD_OFF_STATIC = "td_off_static";
    public static final String TD_PERMANENCE_FILE = "td_off_permanence_";//永久降耗文件名
    public static final String TD_STATIC_FILE = "td_off_static_";//静态降耗文件名
    public static final String TD_DYNAMIC_FILE = "td_off_dynamic_";//动态降耗文件名
    public static final String PERMANENCE_AREA = "PERMANENCE_AREA";//永久降耗小区标识
    public static final String STATIC_AREA = "STATIC_AREA";//静态降耗小区标识
    public static final String TD_NETWORK_OFF_SLEEP = "SLEEP_TD_OFF";
    public static final String TD_NETWORK_OFF_NOTIFY = "NOTIFY_TD_OFF";
    public static final String VENDER_HW = "hw";
    public static final String VENDER_ERIC = "eric";
    public static final String VENDER_ZTE = "zte";
    //多补一
    public static final String SLEEP_MANY = "SLEEP_MANY";
    public static final String NOTIFY_MANY = "NOTIFY_MANY";
    public static final String L2L_MANY = "l2l_many";
    public static final String T2T_MANY = "t2t_many";

    //数据预测
    public static final String BIGDATA_KPI_CAL_HIS = "BIGDATA_KPI_CAL_HIS";
    public static final String BIGDATA_GROUP = "GROUP_NAME";//数据预测的制式

    public static final String ALARM = "ALARM";

    public static final Integer LOG_RESULT = 0;

}
