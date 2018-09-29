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
       执行休眠小区筛选
    """
    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    context.put(Constant.MULTINET_SLEEP_FLAG,"true")
    #休眠小区筛选，冲突处理，指令生成
    service.alysAll(context)
