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
    #  根据类型按照日期创建目录；如果为3G TD_HW， 如果是4G的为 LTE_HW
    if service.sfCollectMr("LTE_HW",mrCollectTerm["LTE_HW"]):
        ftpLogCommandService = mes.getService("FtpLogCommandService")
        ## 如果有多个线程修改  MRO0 --> MRO1...MRO.N
        # select * from mes_ftp_command a where a.group_name = 'LTE_MRO'
        ftpLogCommandService.queryAll("LTE_MRO",int(context.get(Constant.CURRENT_BATCH_MR)))

