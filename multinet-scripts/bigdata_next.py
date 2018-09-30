# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
from java.util import HashMap
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


if __name__ == '__main__':
    """
        该脚本用于计算历史kpi生成 mes_lte_kpi 表数据
    """
    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    service.calKpiByTime(context)

