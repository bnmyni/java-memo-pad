# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

def tdNetworkOffCal(context):
    selSql = """select * from mes_td_calculate_table where id=(select MAX(id) from mes_td_calculate_table) and starttime is not null and endtime is null and finish=0"""
    table = query_table("MainDB",selSql)
    for row in table.getRows():
    	caltype = str(row.getValue("cal_type"))
    	context.put("cal_type",caltype)
    	return True
    return False
    
def insertCurrentSleepFail(delay_time):
    delSql3 = """truncate table mes_td_off_t2l_currentsleep_fail"""
    execute_sql("MainDB",delSql3)
    inSql3 = """insert into mes_td_off_t2l_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='t2l' and group_name='sleep_td_off' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    execute_sql("MainDB",inSql3)

    delSql4 = """truncate table mes_td_off_t2g_currentsleep_fail"""
    execute_sql("MainDB",delSql4)
    inSql4 = """insert into mes_t2g_currentsleep_fail SELECT sent_time,extend2,extend3 FROM  mes_adjust_command_his 
    where extend5='t2g' and group_name='sleep_td_off' and time_stamp>'%s' group by extend2,extend3 having max(sucessfull)=0"""%delay_time
    execute_sql("MainDB",inSql4)

if __name__ == '__main__':
    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    print "TD network off start-------"
    if tdNetworkOffCal(context):
        service.tdNetworkOffCal(context)#计算
    service.tdNetworkOffExe()#执行、监控、取消执行   
    print "---------TD network off end---------"
    insertCurrentSleepFail(context.get(Constant.CURRENT_TIME_DELAY))#针对上一任务执行失败的休眠小区，添加休眠失败记录，用于过滤分析