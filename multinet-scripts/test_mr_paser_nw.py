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
    service.l2lhwMRParser("F:/LTE_HW_MRO1/","TD-LTE_MRO_HUAWEI_\\d+_(\\d+)_\\d+.xml.gz","nw")
    

