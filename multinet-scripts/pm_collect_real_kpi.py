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
      REAL kpi计算开始
        1. SELECT * FROM mes_kpical_set a WHERE a.`group_name` = 'KPI_CAL_REAL'
        2. 会生成 mes_gsm_real_kpi mes_td_real_kpi  mes_lte_real_kpi 三张表
        3. if mes_kpical_set.delete_flag 
                a. mes_gsm_real_kpi mes_td_real_kpi  mes_lte_real_kpi 备份到历史表 mes_lte_real_kpi_his
                b. 删除 mes_gsm_real_kpi mes_td_real_kpi  mes_lte_real_kpi 表数据
        4. 通过 mes_kpical_set.calhandle处理,lte的使用 lteKpiCalHandle 处理生成文件
            lteKpiCalHandle 处理流程
                通过 mes_kpical_set.querysql 查询数据
                经过运算后，生成文件
        5. 将生成的文件数据导入到 mes_kpical_set.res_table 中,lte表为 mes_lte_real_kpi
    """ 
    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    service.calKpi(Constant.KPI_CAL_REAL)    
        

