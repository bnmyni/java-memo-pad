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
    context.put(Constant.CURRENT_COLLECTTIME,"2016-03-20 06:45:00")       
    if isEnd(context.get(Constant.BEGIN_PERIOD),context.get(Constant.CURRENT_COLLECTTIME)):
        service.notifyAllSleep()
