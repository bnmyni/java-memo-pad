package com.tuoming.mes.strategy.service.impl;

import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.rdbms.DataAdapterPool;
import com.tuoming.mes.collect.models.AdjustCommand;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.execute.dao.AdjustCommandService;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.EnergyCellRefreshDao;
import com.tuoming.mes.strategy.dao.NotifyDao;
import com.tuoming.mes.strategy.dao.SleepAreaSelDao;
import com.tuoming.mes.strategy.dao.SleepExeDao;
import com.tuoming.mes.strategy.dao.TdOffSleepAreaSelDao;
import com.tuoming.mes.strategy.model.EnergyCellRefreshSetting;
import com.tuoming.mes.strategy.model.NotifyModel;
import com.tuoming.mes.strategy.model.SleepExeSetting;
import com.tuoming.mes.strategy.model.TdOffSleepSelectModel;
import com.tuoming.mes.strategy.service.TdOffSleepAreaSelectService;
import com.tuoming.mes.strategy.service.handle.TdOffSleepExeHandle;
import com.tuoming.mes.strategy.service.thread.TdOffSelThread;
import com.tuoming.mes.strategy.util.CsvUtil;
import com.tuoming.mes.strategy.util.DateUtil;

/**
 * 3G智能退网功能
 *
 * @author Administrator
 */
@Service("tdOffSleepAreaSelectService")
public class TdOffSleepAreaSelectServiceImpl implements TdOffSleepAreaSelectService {
    private final static Logger logger = LogFacade.getLog4j(TdOffSleepAreaSelectServiceImpl.class);
    @Autowired
    @Qualifier("tdOffSleepAreaSelDao")
    private TdOffSleepAreaSelDao tdOffSleepAreaSelDao;
    @Autowired
    @Qualifier("energyCellRefreshDao")
    private EnergyCellRefreshDao energyCellRefreshDao;
    @Autowired
    @Qualifier("sleepAreaSelDao")
    private SleepAreaSelDao sleepAreaSelDao;
    @Autowired
    @Qualifier("sleepExeDao")
    private SleepExeDao sleepExeDao;
    @Autowired
    @Qualifier("notifyDao")
    private NotifyDao notifyDao;
    @Autowired
    @Qualifier("AdjustCommandService")
    private AdjustCommandService service;

    public void tdNetworkOff(Map<String, String> context) {
        //更新计算表，将计算状态更改为计算finish=1
        tdOffSleepAreaSelDao.updateCalStatus();

        String caltype = context.get("cal_type");
        //文件存放路径，
        String rootPath = CsvUtil.mkParentDir(Constant.TD_NETWORK_OFF + "_" + System.currentTimeMillis());
        //创建线程池
        List<ThreadPoolExecutor> threadPoolExecutors = new ArrayList<ThreadPoolExecutor>();
        for (String busytype : Constant.TD_BUSTYPEARR) {//场景遍历
            //3G退网筛选方式为覆盖度，筛选的小区作为永久降耗小区，小区信息存储到永久休眠小区表中
            if (caltype.equalsIgnoreCase("0")) {
                List<TdOffSleepSelectModel> dataList = tdOffSleepAreaSelDao.querySleepAreaSelSet(busytype, 0);
                TdOffSleepSelectModel model = dataList.get(0);
                if (model.isDelFlag()) {//删除原表中数据
                    tdOffSleepAreaSelDao.removeAllData(model.getResTable());
                }
                tdOffSleepAreaSelDao.updateSleepArea(model.getQuerySql());

            } else {//筛选方式为覆盖度+性能指标，启动线程处理
                /**
                 * 清空原表中数据
                 */
                List<TdOffSleepSelectModel> dataList = tdOffSleepAreaSelDao.querySleepAreaSelSet(busytype, 1);
                TdOffSleepSelectModel model = dataList.get(0);
                if (model.isDelFlag()) {
                    tdOffSleepAreaSelDao.removeAllData(model.getResTable().split("\\|")[0]);
                    tdOffSleepAreaSelDao.removeAllData(model.getResTable().split("\\|")[1]);
                }

                boolean isT2G = busytype.equalsIgnoreCase("azimuth_t2g") ? true : false;

                String monthAgo = DateUtil.getDay(-31);//取一个月前时间  yyyy-MM-dd HH:mm:ss
                String dayAgo = DateUtil.getDay(-1);
                String nowTime = DateUtil.getDay(0);
                //String monthAgo = DateUtil.getDay(-53);//取一个月前时间  yyyy-MM-dd HH:mm:ss
                //String dayAgo = DateUtil.getDay(-23);
                //String nowTime = DateUtil.getDay(-22);//当前时间
//				String monthAgo = DateUtil.getDay(-82);//取一个月前时间  yyyy-MM-dd HH:mm:ss
//				String dayAgo = DateUtil.getDay(-53);
//				String nowTime = DateUtil.getDay(-52);//当前时间

                //查询TD门限值
                List<Map<String, Object>> tdDic = tdOffSleepAreaSelDao.queryTdDic();
                //查询GSM降耗门限值
                List<Map<String, Object>> gsmDic = tdOffSleepAreaSelDao.queryGsmDic();
                //查询LTE门限
                List<Map<String, Object>> lteDic = tdOffSleepAreaSelDao.queryLteDic();
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                        ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_CORE_POOL_SIZE, 2),
                        ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                        ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

                //根据覆盖度个数查询启动线程的个数
                int count = 0;
                int dataNum = Integer.parseInt(tdOffSleepAreaSelDao.queryDataNum());
                int dataCount = tdOffSleepAreaSelDao.queryDataCount(isT2G);
                if (dataCount % dataNum == 0) {
                    count = dataCount / dataNum;
                } else {
                    count = dataCount / dataNum + 1;
                }

                for (int i = 0; i < count; i++) {
                    //小区按线程分组处理
                    List<Map<String, Object>> cellList = tdOffSleepAreaSelDao.queryMetaData(this.cellGroupBySql(isT2G, i * dataNum, dataNum));
                    threadPoolExecutor.execute(new TdOffSelThread(busytype, monthAgo, dayAgo, rootPath, tdDic, gsmDic, lteDic, nowTime, cellList, model));
                    //threadPoolExecutor.execute(new TdOffThread(busytype,monthAgo, dayAgo,rootPath,tdDic,gsmDic,lteDic,nowTime,cellList));
//					TdOffTest testtest = new TdOffTest(busytype,monthAgo, dayAgo,rootPath,tdDic,gsmDic,lteDic,nowTime,cellList,model);
//					testtest.tdOfftest();
                }
                threadPoolExecutor.shutdown();
                threadPoolExecutors.add(threadPoolExecutor);
            }
        }
        for (ThreadPoolExecutor threadPoolExecutor : threadPoolExecutors) {
            while (threadPoolExecutor.isShutdown() && threadPoolExecutor.getPoolSize() > 0) {
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //用覆盖度+性能指标计算时才会生成入库文件
        if (caltype.equals("1")) {
            /***************入库*******************/
            List<String> finalFile = FileOper.getSubFiles(rootPath, CsvUtil.CSV_TYPE, false);
            for (String filePath : finalFile) {
                if (filePath.endsWith(".tmp")) {
                    continue;
                }
                File file = new File(filePath);
                String fileName = file.getName();
                String bustype = fileName.substring(fileName.indexOf("azimuth"), fileName.indexOf("azimuth") + 11);
                String isStatic = fileName.split("_")[2];
                List<TdOffSleepSelectModel> dataList = tdOffSleepAreaSelDao.querySleepAreaSelSet(bustype, 1);
                TdOffSleepSelectModel model = dataList.get(0);
                String table = "";
                if (isStatic.equalsIgnoreCase("static")) {
                    table = model.getResTable().split("\\|")[1];
                } else {
                    table = model.getResTable().split("\\|")[0];
                }

                try {
                    DataAdapterPool.getDataAdapterPool(model.getDbName()).getDataAdapter()
                            .loadfile(filePath, table);
                } catch (Exception e) {
                    logger.warn(String.format("3G退网数据入库 %s 失败！", fileName));
                    e.printStackTrace();
                }
                logger.warn(String.format("3G退网数据入库 %s 结束！", fileName));
            }
        }

        /************完成入库，更新计算表完成时间*****************/
        tdOffSleepAreaSelDao.updateCalculate();
    }


    @Override
    public boolean calFinish() {
        return tdOffSleepAreaSelDao.calFinish();
    }

    @Override
    public void tdNetworkOffFilter() {
        for (String bustype : Constant.TD_BUSTYPEARR) {
            boolean isT2G = bustype.equalsIgnoreCase("azimuth_t2g") ? true : false;
            String groupName = Constant.AZIMUTH;
            // 根据组名进行查询
            List<EnergyCellRefreshSetting> settingList = energyCellRefreshDao
                    .queryRefCellSetting(Constant.TD_NETWORK_OFF + "_" + bustype, groupName);
            for (EnergyCellRefreshSetting model : settingList) {
                if (settingList == null) {
                    return;
                }
                String wlbm = energyCellRefreshDao.createTempTable(model.getQuerySql());
                //去除节能小区及补偿小区中黑白名单、告警
                if (isT2G) {
                    //删除不合格小区
                    energyCellRefreshDao.deleteSrcTdOffBwa(wlbm);
                    energyCellRefreshDao.deleteLinGsmBwa(wlbm);
                } else {
                    //删除不合格小区
                    energyCellRefreshDao.deleteSrcTdOffBwa(wlbm);
                    energyCellRefreshDao.deleteLinLteBwa(wlbm);
                }

                String rsTable = model.getResTable();
                energyCellRefreshDao.removeTable(rsTable);
                energyCellRefreshDao.createResTable(rsTable, wlbm);

                energyCellRefreshDao.addData(rsTable, wlbm);
                energyCellRefreshDao.removeTable(wlbm);
            }
        }

    }

    @Override
    public int queryExecuteStatus() {
        return tdOffSleepAreaSelDao.updateAndQueryExecuteStatus();
    }

    @Override
    public void tdOffExecuteSleep() {
        /**
         * 生成休眠指令
         **/
        //对已执行指令进行处理，先将指令移至历史表，再删除原表中纪录
        sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_SLEEP);
        sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_SLEEP);
        //查询每种制式下，每个单元小区休眠个数
        List<Map<String, Object>> dicList = sleepExeDao.querySleepDic();
        /**
         * 查询配置，永久降耗
         */
        List<SleepExeSetting> setList = sleepExeDao.querySleepExeSetList(Constant.TD_OFF_PERMANENCE);

        if (setList != null && setList.size() > 0) {
            SleepExeSetting set = setList.get(0);

            String[] sqlArr = set.getQuerySql().split("#");
            List<Map<String, Object>> dataList = sleepExeDao.querySleepAreaBySql(sqlArr[0], true);//T2G休眠
            List<Map<String, Object>> t2lDataList = sleepExeDao.querySleepAreaBySql(sqlArr[1], true);//T2L休眠
            dataList.addAll(t2lDataList);
            TdOffSleepExeHandle handle = AppContext.getBean(set.getServiceHandle());
            Map<String, Integer> unitCount = sleepExeDao.queryCellAmount(set.getZs());
            handle.tdOffHandle(dataList, this.getSleepAmountByZs(dicList, set), set, unitCount);
        }


        /**
         * 静态降耗，查询配置
         */
        String nowTime = DateUtil.format(new Date()).substring(11);
        setList = sleepExeDao.querySleepExeSetList(Constant.TD_OFF_STATIC);
        if (setList != null && setList.size() > 0) {
            SleepExeSetting set = setList.get(0);
            String[] sqlArr = set.getQuerySql().split("#");
            List<Map<String, Object>> dataList = sleepExeDao.querySleepAreaBySql(this.getQuerySql(sqlArr[0], nowTime), true);//T2G休眠
            List<Map<String, Object>> t2lDataList = sleepExeDao.querySleepAreaBySql(this.getQuerySql(sqlArr[1], nowTime), true);//T2L休眠
            dataList.addAll(t2lDataList);
            TdOffSleepExeHandle handle = AppContext.getBean(set.getServiceHandle());
            Map<String, Integer> unitCount = sleepExeDao.queryCellAmount(set.getZs());
            handle.tdOffHandle(dataList, this.getSleepAmountByZs(dicList, set), set, unitCount);
        }


        for (String bustype : Constant.TD_BUSTYPEARR) {
            boolean isT2G = bustype.equalsIgnoreCase("azimuth_t2g") ? true : false;
            /**
             * 处理动态休眠数据
             */
            tdOffSleepAreaSelDao.removeOverByTdOff(isT2G);//覆盖度表删除3G退网数据
            tdOffSleepAreaSelDao.removeSleepByTdOff(isT2G);//休眠小区表中删除3G退网数据
            tdOffSleepAreaSelDao.addTdOffFromDynamicSleep(isT2G);//删除动态休眠成功表中包含的3G退网小区，并将此小区添加到永久或静态休眠成功表中
        }

        /**
         * 执行休眠指令
         * */
        logger.info("指令执行开始--------------");
        this.tdOffSleepOrNotify(Constant.TD_NETWORK_OFF_SLEEP);
    }

    /**
     * 监控唤醒流程
     */
    @Override
    public void tdOffMonitor() {
        String nowTime = DateUtil.format(new Date()).substring(11);
        for (String bustype : Constant.TD_BUSTYPEARR) {
            boolean isT2G = bustype.equalsIgnoreCase("azimuth_t2g") ? true : false;
            String groupName = "";
            if (isT2G) {
                groupName = Constant.TD_NETWORK_OFF + "_gsm_static";
            } else {
                groupName = Constant.TD_NETWORK_OFF + "_lte_static";
            }

            /**
             * 生成唤醒指令
             */
            //对已执行指令进行处理，先将指令移至历史表，再删除原表中纪录
            sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_NOTIFY);
            sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_NOTIFY);
            //查询唤醒配置表
            List<NotifyModel> setList = notifyDao.querySetList(groupName);
            for (NotifyModel model : setList) {
                List<Map<String, Object>> notifyData = tdOffSleepAreaSelDao.queryMetaData(this.getQuerySql(model.getQuerySql(), nowTime));
                for (Map<String, Object> map : notifyData) {
                    AdjustCommand command = this.buildNotifyCommand(model, map, 0);
                    if (command == null) {
                        continue;
                    }
                    service.save(command);
                }
            }
            /**
             * 唤醒指令下发
             */
            AdjustCommandService adjust = AppContext.getBean("AdjustCommandService");
            adjust.sleepOrNotify(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_NOTIFY);
        }
    }

    /**
     * 唤醒所有3G退网数据
     */
    @Override
    public void tdOffExecuteNotify() {
        for (String bustype : Constant.TD_BUSTYPEARR) {
            boolean isT2G = bustype.equalsIgnoreCase("azimuth_t2g") ? true : false;
            String groupName = "";
            if (isT2G) {
                groupName = Constant.TD_NETWORK_OFF + "_gsm";
            } else {
                groupName = Constant.TD_NETWORK_OFF + "_lte";
            }
            /**
             * 唤醒指令生成
             */
            //对已执行指令进行处理，先将指令移至历史表，再删除原表中纪录
            sleepExeDao.insertHisCommand(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_NOTIFY);
            sleepExeDao.delCommand(Constant.APP_MULTINET, Constant.TD_NETWORK_OFF_NOTIFY);
            List<NotifyModel> setList = notifyDao.querySetList(groupName);
            for (NotifyModel model : setList) {
                List<Map<String, Object>> dataList = tdOffSleepAreaSelDao.queryMetaData(model.getQuerySql());
                for (Map<String, Object> data : dataList) {
                    AdjustCommand command = this.buildNotifyCommand(model, data, 0);
                    if (command == null) {
                        continue;
                    }
                    service.save(command);
                }
            }

            tdOffSleepAreaSelDao.addOverByTdOff(isT2G);//将3G退网数据还原到覆盖度表

            /**
             * 执行唤醒指令
             */
            this.tdOffSleepOrNotify(Constant.TD_NETWORK_OFF_NOTIFY);
        }

        /**
         * 更新执行配置表中结束时间
         */
        tdOffSleepAreaSelDao.updateExeTime();
    }


    /**
     * 休眠指令下发
     *
     * @param sleepOrNotify SLEEP_TD_OFF：休眠，NOTIFY_TD_OFF：唤醒
     */
    private synchronized void tdOffSleepOrNotify(String sleepOrNotify) {
        AdjustCommandService adjust = AppContext.getBean("AdjustCommandService");
        adjust.sleepOrNotify(Constant.APP_MULTINET, sleepOrNotify);
    }

    /**
     * @return
     */
    private String cellGroupBySql(boolean isT2G, int startIndex, int num) {
        StringBuilder sql1 = new StringBuilder();
        StringBuilder sql2 = new StringBuilder();
        sql1.append("SELECT * from ")
                .append(" rst_td_gsm_azimuth limit ")
                .append(startIndex)
                .append(",")
                .append(num);
        sql2.append("SELECT * from")
                .append(" rst_td_lte_azimuth limit ")
                .append(startIndex)
                .append(",")
                .append(num);
        return isT2G ? sql1.toString() : sql2.toString();
    }


    /**
     * 获取该制式下的休眠小区格式
     *
     * @param dicList
     * @param set
     * @return
     */
    private int getSleepAmountByZs(List<Map<String, Object>> dicList, SleepExeSetting set) {
        for (Map<String, Object> dic : dicList) {
            if (set.getZs().equalsIgnoreCase(String.valueOf(dic.get("zs")))) {
                return (Integer) dic.get("top");
            }
        }
        return 0;
    }

    /**
     * 根据结果和配置生成唤醒指令
     *
     * @param model
     * @param data
     * @param orderId
     * @return
     */
    private AdjustCommand buildNotifyCommand(NotifyModel model, Map<String, Object> data, int orderId) {
        String unitBs = null;
        ObjectType objectType = ObjectType.BSC;
        String commandMap = model.getCommandMap();
        String queryCommandMap = model.getQueryCommandMap();
        String ne = null;
        String cellid = null;

        unitBs = String.valueOf(data.get("src_rnc"));
        ne = String.valueOf(data.get("src_rnc"));
        cellid = String.valueOf(data.get("src_lcid"));

        AdjustCommand command = new AdjustCommand();//构建休眠小区命令对象
        command.setTimeStamp(DateUtil.tranStrToDate(DateUtil.getCurrentDate()));
        command.setApplied(0);
        command.setOrderId(orderId);
        command.setAppName(Constant.APP_MULTINET);
        command.setGroupName(Constant.TD_NETWORK_OFF_NOTIFY);
        command.setOwner(Constant.APP_MULTINET);
        command.setTargetObject(unitBs);
        command.setObjectType(objectType);
        command.setBatchId(Constant.CURRENT_BATCH);
        String commandText = (String) ((Map) DSLUtil.getDefaultInstance().compute(commandMap, data))
                .get(data.get("src_vender"));
        String queryCommand = (String) ((Map) DSLUtil.getDefaultInstance().compute(queryCommandMap, data))
                .get(data.get("src_vender"));
        command.setCommand(commandText);//将休眠命令存入命令表中
        command.setExtend1(queryCommand);
        command.setExtend2(ne);
        command.setExtend3(cellid);
        data.put("command", commandText);
        data.put("starttime", DateUtil.getCurrentDate());
        command.setExtend4(String.valueOf(JSONSerializer.toJSON(data)));
        command.setExtend5(String.valueOf(data.get("bus_type")));
        return command;
    }

    private String getQuerySql(String sql, String nowTime) {
        return sql.replace("$NOTIFYTIME$", nowTime.trim());
    }
}
