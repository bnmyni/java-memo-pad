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
 
def isStart(startTimeStr,collectTimeStr):
    startHour = int(startTimeStr.split(":")[0])
    startMin = int(startTimeStr.split(":")[1])
    collectTime = time.strptime(collectTimeStr,'%Y-%m-%d %H:%M:%S')
    currentHour = int(time.strftime('%H',collectTime))
    currentMin = int(time.strftime('%M',collectTime))
    #  这个几乎总是返回False
    if currentHour==startHour and currentMin==startMin:
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
 

if __name__ == '__main__':

    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
     
    if sfMs(context.get(Constant.START_PERIOD),context.get(Constant.BEGIN_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
        if not isStart(context.get(Constant.START_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
        #根据当前采集数据计算kpi 180720不需要每个时段都计算，增加该表数据量，调整为节能时段一致。
        """
            1. SELECT * FROM mes_kpical_set a WHERE a.`group_name` = 'KPI_CAL_REAL'
            2. 会生成 mes_gsm_real_kpi mes_td_real_kpi  mes_lte_real_kpi 三张表
            3. if mes_kpical_set.delete_flag 
                    a. mes_gsm_real_kpi mes_td_real_kpi  mes_lte_real_kpi 备份到历史表 mes_lte_real_kpi_his
                    b. 删除 mes_gsm_real_kpi mes_td_real_kpi  mes_lte_real_kpi 表数据
            4. 通过 mes_kpical_set.calhandle处理,lte的使用 lteKpiCalHandle 处理生成文件
                lteKpiCalHandle 处理流程
                    通过 mes_kpical_set.querysql 查询数据
                    经过运算后，生成文件
            5. 将生成的文件数据导入到 mes_kpical_set.res_table 中,lte表为 mes_lte_real_kpi
        """        
            service.calKpi(Constant.KPI_CAL_REAL)
            # mes_*_currentsleep 表数据移动到 mes_*_currentsleep_his中
            insertHis()
            # 移除 mes_*_currentsleep 表中上一时段未下发记录
            delCurrentCell()
            """
                MULTINET_SLEEP_FLAG 如果设置为 true 则 alysAll() 执行 筛选, 冲突处理 指令生成 三个步骤
                如果　MULTINET_SLEEP_FLAG = false 则只执行　监控
            """            
            context.put(Constant.MULTINET_SLEEP_FLAG, "false")
            #　由于MULTINET_SLEEP_FLAG=false alysAll()只执行 监控           
            service.alysAll(context)
            #休眠小区唤醒 
            service.executeWakeupProcess(context)   
            #针对上一任务执行失败的休眠小区，添加休眠失败记录，用于过滤分析
            insertCurrentSleepFail(context.get(Constant.CURRENT_TIME_DELAY))
            
        #　节能小区刷新
        service.refreshPredict(context)
        context.put(Constant.MULTINET_SLEEP_FLAG,"true")
        #　休眠小区筛选，冲突处理，指令生成，监控
        service.alysAll(context)
        #休眠指令下发
        service.executeSleepProcess(context)
        
        
        inSql5 = """insert into mes_adjust_command_his select * from mes_adjust_command"""
        execute_sql("MainDB",inSql5)
        info_print("备份mes_adjust_command表数据===%s" % inSql5)
        inSql6 = """truncate table mes_adjust_command"""
        execute_sql("MainDB",inSql6)
        info_print("清空mes_adjust_command表数据===%s"%inSql6)
        

