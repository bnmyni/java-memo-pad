# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


if __name__ == '__main__':

    mes = MESManager()
    service = mes.getBean('SEBizService')
    logCommandService = mes.getService("LogCommandService")
    context = service.buildContext()
    mes.setEnv("serverName","LTE_OMC165")
    logCommandService.queryAll("FIRST", Constant.CURRENT_BATCH)
    #logCommandService.queryAll("TEST", Constant.CURRENT_BATCH)
    #logCommandService.queryAll("TEST11", Constant.CURRENT_BATCH)
    #ftpLogCommandService = mes.getService("FtpLogCommandService")
    #ftpLogCommandService.queryAll("PM",Constant.CURRENT_BATCH)
    #mes.setEnv("serverName","LTE_OMC30")
    #logCommandService.parse("e:/hamster/mes/nac/data/2016_07_20/Log/LTE_OMC7/LTE_OMC7_HW_TD_DSP_BRDMFRINFO_201607201605.log","HW_TD_DSP_BRDMFRINFO_H")
    #logCommandService.parse("E:/hamster/mes/nac/data/2016_06_30/Log/LTE_OMC30/LTE_OMC30_HW_TD_LSTRRU_201606301659.log","HW_TD_LSTRRU")
    #context.put(Constant.CURRENT_COLLECTTIME,'2016-06-02 03:15:00')

    #service.executeSleepProcess(context)
    #info_print("%s===%s"%(context.get(Constant.CURRENT_TIME_DELAY),context.get(Constant.KPI_CAL_MINUTE_TIME)))
    #service.refreshPredict(context)
    #context.put(Constant.MULTINET_SLEEP_FLAG,"true")
    #service.alysAll(context)
    #service.executeSleepProcess(context)
    #service.executeSleepProcess(context)
    #service.executeWakeupProcess(context)
    
    
    
    