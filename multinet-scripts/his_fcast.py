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
        用于生成 rst_pm_  相关的表数据
    """
    mes = MESManager()
    context =  HashMap()
    context.put(Constant.KEY_GROUP_NAME,"")
    service = mes.getBean('SEBizService')
    service.hisDataFcast(context)