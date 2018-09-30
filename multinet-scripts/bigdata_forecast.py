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
    service.bigDataForecast(context)
    """
        如果所有的厂商制式都有，则需要清理所有 rst_bigdata_pm 打头的表
    """
    sql = """DELETE FROM rst_bigdata_pm_lte_eutrancelltdd_hw WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",sql)
    
    
    

    
    