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
    context = service.buildContext()
    # service.rBigDataForecast(context)
    service.calKpiByTime(context)

