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
if __name__ == '__main__':

    mes = MESManager()
    #ftpLogCommandService = mes.getService("FtpLogCommandService")
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    #service.dataHandle(Constant.BEFORE_COLLECT_DATA_ADD_BATCHID)#将pm历史数据更新到历史表
    #service.dataHandle(Constant.BEFORE_COLLECT_DATA_CLEAN_CARRIER)#pm清空载频级别数据
    #ftpLogCommandService.queryAll(Constant.PM,Constant.CURRENT_BATCH)#PM采集完成
    #service.dataHandle(Constant.AFTER_COLLECT_DATA_UPDATE_NETELE)#更新网元名称
    #service.dataHandle(Constant.AFTER_COLLECT_DATA_TD_UPDATE_RNC)#更新td hw 小区的rnc名称  
    #delsql ="""delete from pm_td_carrier_hw  where rnc is null"""
    #execute_sql("MainDB",delsql)
    #delsql2 ="""delete from pm_td_utrancell_hw  where rnc = 'TD_PM'"""
    #execute_sql("MainDB",delsql2)
    #delLteSql = """DELETE FROM pm_lte_eutrancelltdd_hw    where NOT EXISTS (
    #SELECT 1 FROM mes_sd_lte_hw_dic  a  join cm_lte_enbfunction_hw b on a.enodebid=b.enodebid 
    #where b.managedelement=pm_lte_eutrancelltdd_hw.managedelement)"""
    #execute_sql("MainDB",delLteSql)

    #service.dataHandle(Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_PM)#更新载频级别的数据到小区级别
    #service.dataHandle(Constant.AFTER_COLLECT_DATA_HANDLE_PM)#更新pm_lte_eutrancelltdd_eric表生成localcellid
    context.put(Constant.CURRENT_COLLECTTIME,"2016-04-22 04:30:00")    
    if sfMs(context.get(Constant.START_PERIOD),context.get(Constant.BEGIN_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
        #if not isStart(context.get(Constant.START_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
            #context.put(Constant.MULTINET_SLEEP_FLAG,"false")
            #service.calKpi(Constant.KPI_CAL_REAL)#根据当前采集数据计算kpi
            #service.alysAll(context)
            #service.executeWakeupProcess(context)        
        #service.fcastNextData(context)#预测下一时刻数据
        #service.calKpi(Constant.KPI_CAL_HIS)#通过预测数据计算kpi
        service.refreshPredict(context)
        #context.put(Constant.MULTINET_SLEEP_FLAG,"true")
        #service.alysAll(context)
        #service.executeSleepProcess(context)
         
        #inSql = """insert into mes_adjust_command(app_name,applied,batch_id,command,extend1,extend2,extend3,extend4,extend5,group_name,ne_object,
        #object_type,order_id,`owner`,remark,split_char,sucessfull,target_object,time_stamp)SELECT app_name,0 applied,batch_id,command,extend1,extend2,
        #extend3,extend4,extend5,group_name,ne_object,object_type,order_id,`owner`,remark,split_char,sucessfull,target_object,time_stamp FROM mes_adjust_command_his 
        #where sucessfull=0 and time_stamp>'%s'"""%(context.get(Constant.CURRENT_COLLECTTIME))
        #execute_sql("MainDB",inSql)
        #info_print("inSql===%s"%inSql)
        #service.executeWakeupProcess(context)
        #service.executeSleepProcess(context)

    ##service.calKpi(Constant.KPI_CAL_PERF_BSC_MIN)#绘制当前时段kpi数据
    ##service.calKpi(Constant.KPI_CAL_PERF_CELL_MIN)
    ##service.calKpi(Constant.KPI_CAL_PERF_ALL_MIN)

