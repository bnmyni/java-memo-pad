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
    if service.sfCalOverDegree("GSM_HW"):
        service.refreshCoverRate("G2G_HW")
        service.calOverDegreeDone("GSM_HW")
    if service.sfCalOverDegree("GSM_ERIC"):
        print "execute gsm eric"
        service.refreshCoverRate("G2G_ERIC")
        service.calOverDegreeDone("GSM_ERIC")
    if service.sfCalOverDegree("TD_HW"):
        service.refreshCoverRate("T2G_HW")
        service.calOverDegreeDone("TD_HW")
    if service.sfCalOverDegree("LTE_HW"):
        service.refreshCoverRate("L2L_HW")
        service.calOverDegreeDone("LTE_HW")