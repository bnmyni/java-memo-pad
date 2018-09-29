# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import time
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

if __name__ == '__main__':
    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    context.put(Constant.KEY_GROUP_NAME,"")
    context.put(Constant.TIMEPARTICLE,Constant.PER_LD_HOUR)
    service.performanceCal(context)
    service.calKpi(Constant.KPI_CAL_PERF_CELL_HOUR)	
    service.calKpi(Constant.KPI_CAL_PERF_BSC_HOUR)	
    service.calKpi(Constant.KPI_CAL_PERF_ALL_HOUR)	
