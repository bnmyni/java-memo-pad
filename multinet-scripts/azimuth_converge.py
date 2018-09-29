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
    querysql = '''select count(1) num from mes_zs_azimuth where isupdate =1'''
    tableData = query_table("MainDB",querysql)
    if tableData.getValue(0,"num")==3 :
        service.refreshCoverRate("AZIMUTH")
        upSql = '''update mes_zs_azimuth set isupdate=0 '''
        execute_sql("MainDB", upSql)
