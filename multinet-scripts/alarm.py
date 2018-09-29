# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


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
    ftpLogCommandService = mes.getService("FtpLogCommandService")
    service = mes.getBean('SEBizService')
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_CLEAN_ALARM)
    ftpLogCommandService.queryAll("ALARM",Constant.CURRENT_BATCH)
    service.updateAlarmInfo()