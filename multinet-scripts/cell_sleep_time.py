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
    
def betweenDates(date1,date2):
    date1=time.mktime(time.strptime(date1,'%Y-%m-%d %H:%M:%S'))
    date2=time.mktime(time.strptime(date2,'%Y-%m-%d %H:%M:%S'))
    return date2-date1
    
def energyValues(today):
    energyDic = {'GSM':1.67,'TD':0.4825,'LTE':0.595}
    sql = """select vender,round(avg(seconds)/3600,2) avg_value,round(max(seconds)/3600,2) max_value,round(min(seconds)/3600,2) min_value ,count(*) cell_num 
    from between_dates  where date>='%s' GROUP BY vender"""%today
    info_print("energyValues_sql===%s"%sql)
    table = query_table("MainDB",sql)
    for row in table.getRows():
        info_print("%s===%s"%(row.getValue("avg_value"),row.getValue("max_value")))
        vender = str(row.getValue("vender"))
        avg_value = float(str(row.getValue("avg_value")))
        max_value = float(str(row.getValue("max_value")))
        min_value = float(str(row.getValue("min_value")))
        cell_num = int(row.getValue("cell_num"))
        power = energyDic[vender]
        energy_value = avg_value*cell_num/6*power
        
        inSql = """insert into sleep_energy(date,vender,avg_value,max_value,min_value,cell_num,energy_value) 
        values('%s','%s',%s,%s,%s,%s,%s)"""%(today,vender,avg_value,max_value,min_value,cell_num,energy_value)
        execute_sql("MainDB",inSql)
    
if __name__ == '__main__':
    inSql = """insert into mes_adjust_command_his select * from mes_adjust_command"""
    execute_sql("MainDB",inSql)
    info_print("inSql1===%s"%inSql)
    delSql = """truncate table mes_adjust_command"""
    execute_sql("MainDB",delSql)
    info_print("delSql===%s"%delSql)
    
    #delSql = """truncate table between_dates"""
    #execute_sql("MainDB",delSql)
    #info_print("delSql===%s"%delSql)
    
    today = get_suffix('%Y-%m-%d 00:00:00')
    typeDic = {'g2g':'GSM','t2g':'TD','l2l':'LTE','l2t':'LTE','l2l_many':'LTE','t2t_many':'TD','t2l':'TD'}
    for (val,vender) in typeDic.items():
        dic = {}
        seconds = 0
        sent_time_tmp = None
        sql = """select extend2,extend3,group_name,sent_time from mes_adjust_command_his where applied=1 and sucessfull=1 
        and extend5='%s' and time_stamp>'%s' order by extend2,extend3,sent_time asc"""%(val,today)
        table = query_table("MainDB",sql)
        for row in table.getRows():
            ci = str(row.getValue("extend2"))
            cellid = str(row.getValue("extend3"))
            group_name = str(row.getValue("group_name"))
            sent_time = str(row.getValue("sent_time"))
            key = ci+"_"+cellid
            if group_name == 'SLEEP':
                sent_time_tmp = sent_time
                if dic.has_key(key): 
                    seconds = dic[key]
                else:
                    dic[key] = 0
                    seconds= 0
            elif group_name=='NOTIFY':
                if sent_time_tmp!="" and sent_time_tmp!=None:
                    seconds = seconds+betweenDates(sent_time_tmp,sent_time)
                else:
                    continue
                    #seconds = seconds+betweenDates(today,sent_time)
                dic[key] = seconds
                seconds = 0
                sent_time_tmp = None
            elif group_name == 'SLEEP_MANY':
                sent_time_tmp = sent_time
                if dic.has_key(key): 
                    seconds = dic[key]
                else:
                    dic[key] = 0
                    seconds= 0
            elif group_name=='NOTIFY_MANY':
                if sent_time_tmp!="" and sent_time_tmp!=None:
                    seconds = seconds+betweenDates(sent_time_tmp,sent_time)
                else:
                    continue
                    #seconds = seconds+betweenDates(today,sent_time)
                dic[key] = seconds
                seconds = 0
                sent_time_tmp = None
            elif group_name == 'SLEEP_TD_OFF':
                sent_time_tmp = sent_time
                if dic.has_key(key): 
                    seconds = dic[key]
                else:
                    dic[key] = 0
                    seconds= 0
            elif group_name=='NOTIFY_TD_OFF':
                if sent_time_tmp!="" and sent_time_tmp!=None:
                    seconds = seconds+betweenDates(sent_time_tmp,sent_time)
                else:
                    continue
                    #seconds = seconds+betweenDates(today,sent_time)
                dic[key] = seconds
                seconds = 0
                sent_time_tmp = None
    
        for key,value in dic.items():
            arr = key.split("_")
            sql = "insert into between_dates(date,vender,ci,cellid,seconds,NEtype) values('%s','%s','%s','%s','%s','%s')"%(today,vender,arr[0],arr[1],value,val)
            execute_sql("MainDB",sql)
            
    energyValues(today)
