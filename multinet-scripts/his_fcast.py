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
    context =  HashMap()
    context.put(Constant.KEY_GROUP_NAME,"")
    service = mes.getBean('SEBizService')
    service.hisDataFcast(context)