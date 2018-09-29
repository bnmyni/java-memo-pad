# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

def isEnd(endTimeStr,collectTimeStr):
    endHour = int(endTimeStr.split(":")[0])
    endMin = int(endTimeStr.split(":")[1])
    collectTime = time.strptime(collectTimeStr,'%Y-%m-%d %H:%M:%S')
    currentHour = int(time.strftime('%H',collectTime))
    currentMin = int(time.strftime('%M',collectTime))
    if currentHour==endHour and currentMin>=endMin:
        return True
    return False
if __name__ == '__main__':

    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()       
    #if isEnd(context.get(Constant.BEGIN_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
    service.notifyAllSleep()
    
    inSql5 = """insert into mes_adjust_command_his select * from mes_adjust_command"""
    execute_sql("MainDB",inSql5)
    inSql6 = """truncate table mes_adjust_command"""
    execute_sql("MainDB",inSql6)
    
    logCommandService = mes.getService("LogCommandService")
    today = get_suffix('%Y-%m-%d %H:00:00')
    #today='2016-04-28 06:00:00'
    sql = """select cmd_log from mes_adjust_command_his where group_name='notify' and  applied=1  
    and extend5 like 'l2%%' and time_stamp>'%s' group by cmd_log"""%today
    table = query_table("MainDB",sql)
    for row in table.getRows():
        cmd_log = str(row.getValue("cmd_log"))
        info_print("cmd_log===%s"%cmd_log)
        mes.setEnv("serverName","")
        logCommandService.parse(cmd_log,"HW_LTE_SITE_BREAK")

    sql = """DELETE from cm_lte_break where type in ('%s','Success')"""%str_decode('执行成功')
    execute_sql("MainDB",sql)

    sql = """update cm_lte_break a  inner join param_lte_hw_login b on a.ip=b.ip set a.enodebname=b.nename  where a.enodebname is null"""
    execute_sql("MainDB",sql)
