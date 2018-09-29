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
    inSql3 = """INSERT into mes_l2t_currentsleep_his SELECT * from mes_l2t_currentsleep"""
    execute_sql("MainDB",inSql3)
    inSql4 = """INSERT into mes_t2g_currentsleep_his SELECT * from mes_t2g_currentsleep"""
    execute_sql("MainDB",inSql4)

def delCurrentCell():
    delSql1 = """delete from mes_g2g_currentsleep where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_bscid 
    and extend3=src_cellid and extend5='g2g' and group_name='sleep' and applied=0)"""
    execute_sql("MainDB",delSql1)
    delSql2 = """delete from mes_l2l_currentsleep  where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_enodebid 
    and extend3=src_localcellid and applied=0 and extend5='l2l' and group_name='sleep' )"""
    execute_sql("MainDB",delSql2)
    delSql3 = """delete from mes_l2t_currentsleep where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_enodebid 
    and extend3=src_localcellid and extend5='l2t' and group_name='sleep' and applied=0)"""
    execute_sql("MainDB",delSql3)
    delSql4 = """delete from mes_t2g_currentsleep where EXISTS (SELECT 1 from mes_adjust_command where extend2=src_rnc 
    and extend3=src_lcid and extend5='t2g' and group_name='sleep' and applied=0)"""
    execute_sql("MainDB",delSql4)
    
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
    updatenetele()#更新ericsson网元名称

