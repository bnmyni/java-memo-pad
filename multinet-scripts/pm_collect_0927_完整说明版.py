# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


def sfMs(startTimeStr, endTimeStr, collectTimeStr):
    startHour = int(startTimeStr.split(":")[0])
    startMin = int(startTimeStr.split(":")[1])
    endHour = int(endTimeStr.split(":")[0])
    endMin = int(endTimeStr.split(":")[1])
    collectTime = time.strptime(collectTimeStr,'%Y-%m-%d %H:%M:%S')
    currentHour = int(time.strftime('%H',collectTime))
    currentMin = int(time.strftime('%M',collectTime))
    if currentHour>startHour and currentHour<endHour:
        return True
    if currentHour==startHour and currentMin>=startMin:
        return True
    if currentHour==endHour and currentMin < endMin:
        return True
    return False
def isEnd(endTimeStr,collectTimeStr):
    endHour = int(endTimeStr.split(":")[0])
    endMin = int(endTimeStr.split(":")[1])
    collectTime = time.strptime(collectTimeStr,'%Y-%m-%d %H:%M:%S')
    currentHour = int(time.strftime('%H',collectTime))
    currentMin = int(time.strftime('%M',collectTime))
    if currentHour==endHour and currentMin>=endMin:
        return True
    return False
def isStart(startTimeStr,collectTimeStr):
    startHour = int(startTimeStr.split(":")[0])
    startMin = int(startTimeStr.split(":")[1])
    collectTime = time.strptime(collectTimeStr,'%Y-%m-%d %H:%M:%S')
    currentHour = int(time.strftime('%H',collectTime))
    currentMin = int(time.strftime('%M',collectTime))
    if currentHour==startHour and currentMin==startMin:
        return True
    return False    
def sfAlarm(startTimeStr,endTimeStr,collectTimeStr):
    startHour = int(startTimeStr.split(":")[0])
    collectTime = time.strptime(collectTimeStr,'%Y-%m-%d %H:%M:%S')
    currentHour = int(time.strftime('%H',collectTime))
    endHour = int(endTimeStr.split(":")[0])
    if currentHour>=startHour and currentHour<endHour :
        return True
    return False
    
def insertHis():
    inSql1 = """INSERT into mes_g2g_currentsleep_his SELECT * from mes_g2g_currentsleep"""
    execute_sql("MainDB",inSql1)
    inSql2 = """INSERT into mes_l2l_currentsleep_his SELECT * from mes_l2l_currentsleep"""
    execute_sql("MainDB",inSql2)
    inSql3 = """INSERT into mes_t2l_currentsleep_his SELECT * from mes_t2l_currentsleep"""
    execute_sql("MainDB",inSql3)
    inSql4 = """INSERT into mes_t2g_currentsleep_his SELECT * from mes_t2g_currentsleep"""
    execute_sql("MainDB",inSql4)
    inSql5 = """INSERT into mes_l2l_many_currentsleep_his SELECT * from mes_l2l_many_currentsleep"""
    execute_sql("MainDB",inSql5)
    inSql6 = """INSERT into mes_t2t_many_currentsleep_his SELECT * from mes_t2t_many_currentsleep"""
    execute_sql("MainDB",inSql6)

def delCurrentCell():
    delSql1 = """delete from mes_g2g_currentsleep where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_bscid 
    and extend3=src_cellid and extend5='g2g' and group_name='sleep' and applied=0)"""
    execute_sql("MainDB",delSql1)
    delSql2 = """delete from mes_l2l_currentsleep  where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_enodebid 
    and extend3=src_localcellid and applied=0 and extend5='l2l' and group_name='sleep' )"""
    execute_sql("MainDB",delSql2)
    delSql3 = """delete from mes_t2l_currentsleep where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_rnc 
    and extend3=src_lcid and extend5='t2l' and group_name='sleep' and applied=0)"""
    execute_sql("MainDB",delSql3)
    delSql4 = """delete from mes_t2g_currentsleep where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_rnc 
    and extend3=src_lcid and extend5='t2g' and group_name='sleep' and applied=0)"""
    execute_sql("MainDB",delSql4)

def insertCurrentSleepFail(delay_time):
    delSql1 = """truncate table mes_g2g_currentsleep_fail"""
    execute_sql("MainDB",delSql1)
    inSql1 = """insert into mes_g2g_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='g2g' and group_name='sleep' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    info_print("inSql1===%s"%inSql1)
    execute_sql("MainDB",inSql1)

    delSql2 = """truncate table mes_l2l_currentsleep_fail"""
    execute_sql("MainDB",delSql2)
    inSql2 = """insert into mes_l2l_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='l2l' and group_name='sleep' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    info_print("inSql2===%s"%inSql2)
    execute_sql("MainDB",inSql2)

    delSql3 = """truncate table mes_t2l_currentsleep_fail"""
    execute_sql("MainDB",delSql3)
    inSql3 = """insert into mes_t2l_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='l2t' and group_name='sleep' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    info_print("inSql3===%s"%inSql3)
    execute_sql("MainDB",inSql3)

    delSql4 = """truncate table mes_t2g_currentsleep_fail"""
    execute_sql("MainDB",delSql4)
    inSql4 = """insert into mes_t2g_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='t2g' and group_name='sleep' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    info_print("inSql4===%s"%inSql4)
    execute_sql("MainDB",inSql4)
    
    delSql5 = """truncate table mes_l2l_many_currentsleep_fail"""
    execute_sql("MainDB",delSql5)
    inSql5 = """insert into mes_l2l_many_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='l2l_many' and group_name='SLEEP_MANY' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    execute_sql("MainDB",inSql5)
    
    delSql6 = """truncate table mes_t2t_many_currentsleep_fail"""
    execute_sql("MainDB",delSql6)
    inSql6 = """insert into mes_t2t_many_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='t2t_many' and group_name='SLEEP_MANY' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    execute_sql("MainDB",inSql6)

def updatenetele():
    delSql1 = """update pm_gsm_cellgprs_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql1)
    delSql2 = """update pm_gsm_cellqoseg_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql2)
    delSql3 = """update pm_gsm_cellqosg_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql3)
    delSql4 = """update pm_gsm_celtchf_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql4)
    delSql5 = """update pm_gsm_celtchfp_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql5)
    delSql6 = """update pm_gsm_celtchh_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql6)
    delSql7 = """update pm_gsm_cltch_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql7)
    delSql8 = """update pm_gsm_trafdlgprs_eric a INNER JOIN mes_bscname_config b on a.bscid=b.srcname set a.bscid=b.bsc"""
    execute_sql("MainDB",delSql8)


if __name__ == '__main__':

    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    """
     ["pm_gsm_cellgprs_eric",
     "pm_gsm_cellqoseg_eric",
     "pm_gsm_cellqosg_eric",
     "pm_gsm_celtchf_eric",
     "pm_gsm_celtchfp_eric",
     "pm_gsm_celtchh_eric",
     "pm_gsm_cltch_eric",
     "pm_gsm_mrf_4c0003ed_hw",
     "pm_gsm_trafdlgprs_eric",
     "pm_lte_eutrancelltdd_eric",
     "pm_lte_eutrancelltdd_hw",
     "pm_lte_eutrancelltdd_zte",
     "pm_td_carrier_hw_cell",
     "pm_td_carrier_zte_cell",
     "pm_td_utrancell_hw",
     "pm_td_utrancell_zte"] 为这些表生成历史表 pm_td_utrancell_zte_his 并清空所有这些表
    """
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_ADD_BATCHID)#将pm历史数据更新到历史表 *_his中
    """
     清空载频级别数据这个步骤在tds网络才需要，其他网络不需要
        a.tablename from mes_beforeafter_setting  a WHERE a.`groupname` = 'BEFORE_COLLECT_DATA_CLEAN_CARRIER';
        清空2个表["pm_td_carrier_hw","pm_td_carrier_zte"] 
    """
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_CLEAN_CARRIER)#pm清空载频级别数据
    """
     pm 采集流程
        1. 查询所有的 mes_bscname_config
        2. 查询 mes_ftp_command a and a.group_name = 'PM' and a.enabled = TRUE
        3. 每条 mes_ftp_command 记录生成一个线程
        4. 执行文件合并，生成csv文件并，将cvs文件数据导入到mes_ftp_command.target_table_map[1]表中
            在甘肃lte中对应的表是 pm_lte_eutrancelltdd_hw
    """
    service.query(Constant.PM,Constant.CURRENT_BATCH) 
    """
        1. mes_beforeafter_setting  a WHERE a.`groupname` = 'AFTER_COLLECT_DATA_TD_UPDATE_RNC';
        2. 执行查询结果的excuteSql 进行更新操作        
            UPDATE pm_td_utrancell_hw p JOIN cm_td_lst_tcell_hw c ON c.userlabel=p.userlabel SET p.rnc=c.rnc;
            UPDATE pm_td_carrier_hw p SET p.rnc=(SELECT rnc FROM cm_td_lst_tcell_hw c WHERE                 c.userlabel=SUBSTRING_INDEX(p.userlabel,'_',1));
    """
    service.dataHandle(Constant.AFTER_COLLECT_DATA_TD_UPDATE_RNC)#更新td hw 小区的rnc名称  
    """
        对于lte来说 pm_td_carrier_hw  pm_td_utrancell_hw 的删除和更新都是没有意义的
    """
    delsql ="""delete from pm_td_carrier_hw  where rnc is null"""
    execute_sql("MainDB",delsql)
    delsql2 ="""delete from pm_td_utrancell_hw  where rnc = 'TD_PM'"""
    execute_sql("MainDB",delsql2)
    """
        这里不能把 pm_lte_eutrancelltdd_hw 中的表数据清除了，否则没有数据
    """
    delsql3 ="""delete from pm_lte_eutrancelltdd_hw where userlabel  not like '%s%%' """%(str_decode('南宁'))
    execute_sql("MainDB",delsql3)
    """
      对于lte来说这个步骤没有任何意义 
        通过 pm_td_carrier_hw 表生成  pm_td_carrier_hw_cell 表
        通过 pm_td_carrier_zte 表生成 pm_td_carrier_zte_cell
        1.SELECT * FROM mes_beforeafter_setting  a WHERE a.`groupname` = 'AFTER_COLLECT_DATA_UPDATE_CARRIER_PM';
        2. 遍历查询结果，insert into row.tablename insert into executeSql
    """
    service.dataHandle(Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_PM)#更新载频级别的数据到小区级别
    """
        1.SELECT * FROM mes_beforeafter_setting  a WHERE a.`groupname` = 'AFTER_COLLECT_DATA_HANDLE_PM';
        2.执行 查询结果的executesql UPDATE pm_lte_eutrancelltdd_eric SET localcellid = SUBSTR(eutrancelltdd,- 1)
    """
    service.dataHandle(Constant.AFTER_COLLECT_DATA_HANDLE_PM)#更新pm_lte_eutrancelltdd_eric表生成localcellid
    #service.calKpi(Constant.KPI_CAL_REAL)#根据当前采集数据计算kpi 180720不需要每个时段都计算，增加数据库压力
    if sfMs(context.get(Constant.START_PERIOD),context.get(Constant.BEGIN_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
        if not isStart(context.get(Constant.START_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
            service.calKpi(Constant.KPI_CAL_REAL)#根据当前采集数据计算kpi 180720不需要每个时段都计算，增加该表数据量，调整为节能时段一致。
            insertHis()#将当前休眠小区记录插入历史表
            delCurrentCell()#删除上一时段未下发记录
            context.put(Constant.MULTINET_SLEEP_FLAG,"false")
            service.alysAll(context)#休眠小区筛选，冲突处理，指令生成，监控
            service.executeWakeupProcess(context)#休眠小区唤醒      
            insertCurrentSleepFail(context.get(Constant.CURRENT_TIME_DELAY))#针对上一任务执行失败的休眠小区，添加休眠失败记录，用于过滤分析
        service.refreshPredict(context)#节能小区刷新
        context.put(Constant.MULTINET_SLEEP_FLAG,"true")
        service.alysAll(context)#休眠小区筛选，冲突处理，指令生成，监控
        service.executeSleepProcess(context)#休眠指令下发
        inSql5 = """insert into mes_adjust_command_his select * from mes_adjust_command"""
        execute_sql("MainDB",inSql5)
        info_print("inSql1===%s"%inSql5)
        inSql6 = """truncate table mes_adjust_command"""
        execute_sql("MainDB",inSql6)
        info_print("inSql1===%s"%inSql6)
        #重新新发送执行失败的休眠、唤醒指令
        #inSql = """insert into mes_adjust_command(app_name,applied,batch_id,command,extend1,extend2,extend3,extend4,extend5,group_name,ne_object,
        #object_type,order_id,`owner`,remark,split_char,sucessfull,target_object,time_stamp)SELECT app_name,0 applied,batch_id,command,extend1,extend2,
        #extend3,extend4,extend5,group_name,ne_object,object_type,order_id,`owner`,remark,split_char,sucessfull,target_object,time_stamp FROM mes_adjust_command_his 
        #where sucessfull=0 and time_stamp>'%s'"""%(context.get(Constant.CURRENT_TIME_DELAY))
        #execute_sql("MainDB",inSql)
        #info_print("inSql===%s"%inSql)
        #service.executeWakeupProcess(context)
        #service.executeSleepProcess(context)
        #service.fcastNextData(context)#预测下一时刻数据
        #service.calKpi(Constant.KPI_CAL_HIS)#通过预测数据计算kpi

    ##service.calKpi(Constant.KPI_CAL_PERF_BSC_MIN)#绘制当前时段kpi数据
    ##service.calKpi(Constant.KPI_CAL_PERF_CELL_MIN)
    ##service.calKpi(Constant.KPI_CAL_PERF_ALL_MIN)

