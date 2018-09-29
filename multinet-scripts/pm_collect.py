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
    #ftpLogCommandService = mes.getService("FtpLogCommandService")
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_ADD_BATCHID)#将pm历史数据更新到历史表 *_his中
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_CLEAN_CARRIER)#pm清空载频级别数据
    service.query(Constant.PM,Constant.CURRENT_BATCH)
    #ftpLogCommandService.queryAll(Constant.PM,Constant.CURRENT_BATCH)#PM采集完成
    #service.dataHandle(Constant.AFTER_COLLECT_DATA_UPDATE_NETELE)#更新网元名称
    #updatenetele()#更新ericsson网元名称
    service.dataHandle(Constant.AFTER_COLLECT_DATA_TD_UPDATE_RNC)#更新td hw 小区的rnc名称  
    delsql ="""delete from pm_td_carrier_hw  where rnc is null"""
    execute_sql("MainDB",delsql)
    delsql2 ="""delete from pm_td_utrancell_hw  where rnc = 'TD_PM'"""
    execute_sql("MainDB",delsql2)
    delsql3 ="""delete from pm_lte_eutrancelltdd_hw where userlabel  not like '%s%%' """%(str_decode('南宁'))
    execute_sql("MainDB",delsql3)
    #delLteSql = """DELETE FROM pm_lte_eutrancelltdd_hw    where NOT EXISTS (
    #SELECT 1 FROM mes_sd_lte_hw_dic  a  join cm_lte_enbfunction_hw b on a.enodebid=b.enodebid 
    #where b.managedelement=pm_lte_eutrancelltdd_hw.managedelement)"""
    #execute_sql("MainDB",delLteSql)

    service.dataHandle(Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_PM)#更新载频级别的数据到小区级别
    service.dataHandle(Constant.AFTER_COLLECT_DATA_HANDLE_PM)#更新pm_lte_eutrancelltdd_eric表生成localcellid
    #service.calKpi(Constant.KPI_CAL_REAL)#根据当前采集数据计算kpi 180720不需要每个时段都计算，增加数据库压力
    """
        START_PERIOD = mes_zs_open_period.start_period
        BEGIN_PERIOD = mes_zs_open_period.begin_period 
        CURRENT_COLLECTTIME = 当前时间/15 * 15 [即离当前时间以前最近的１５分钟倍数]
    """
    if sfMs(context.get(Constant.START_PERIOD),context.get(Constant.BEGIN_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
        if not isStart(context.get(Constant.START_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
            service.calKpi(Constant.BIGDATA_KPI_CAL_HIS)#根据当前采集数据计算kpi 180720不需要每个时段都计算，增加该表数据量，调整为节能时段一致。
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
        