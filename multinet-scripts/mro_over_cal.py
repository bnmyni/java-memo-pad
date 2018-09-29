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
    """
        先计算方位角AZIMUTH生成rst_lte_lte_azimuth表，
        再计算L2L_HW生成rst_l2l_mro_hw表，
        再计算MANY，生成rst_l2l_many_hw表
    """
    service.refreshCoverRate("AZIMUTH")
    service.refreshCoverRate("L2L_HW")
    service.refreshCoverRate("MANY")