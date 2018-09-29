# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

if __name__ == '__main__':

    mes = MESManager()
    
    service = mes.getBean('SEBizService')
    service.cleanServerFileByDay(4)
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_CLEAN_PM)
    #service.dataHandle(Constant.BEFORE_COLLECT_DATA_CLEAN_PERF)
    cleargsmnonsleep = """truncate table mes_gsm_nonsleep"""
    cleartdnonsleep = """truncate table mes_td_nonsleep"""
    clearltenonsleep = """truncate table mes_lte_nonsleep"""
    execute_sql("MainDB",cleargsmnonsleep)
    execute_sql("MainDB",clearltenonsleep)
    execute_sql("MainDB",cleartdnonsleep)
    if getCurrentDay() == 41:
        execute_sql("MainDB","truncate table pm_gsm_mrall_eric")
        execute_sql("MainDB","truncate table pm_gsm_mrf_4c0003f0_hw")
        execute_sql("MainDB","truncate table pm_lte_mro_hw")
        execute_sql("MainDB","truncate table pm_td_mro_gsmneighbour_hw")
        execute_sql("MainDB","truncate table pm_td_mro_inter_hw")
        execute_sql("MainDB","truncate table pm_td_mro_intra_hw")
        service.cleanMrFile()		
    service.cleanServerFileByDay(4)
    clearerrorsleep = """DELETE g2g.* FROM mes_g2g_currentsleep AS g2g ,mes_gsm_black AS b WHERE g2g.src_bscid = b.bsc AND b.lac = g2g.dest_lac AND g2g.src_ci = b.ci"""
    execute_sql("MainDB",clearerrorsleep)
    clearerrorsleep2 = """DELETE t2g.* FROM mes_t2g_currentsleep AS t2g ,mes_td_black AS b WHERE t2g.src_rnc = b.rnc AND t2g.src_lcid = b.lcid"""
    execute_sql("MainDB",clearerrorsleep2)
    clearerrorsleep3 = """DELETE t2g.* FROM mes_t2g_permanence_currentsleep AS t2g ,mes_td_black AS b WHERE t2g.src_rnc = b.rnc AND t2g.src_lcid = b.lcid"""
    execute_sql("MainDB",clearerrorsleep3)
    clearerrorsleep4 = """DELETE t2g.* FROM mes_t2g_static_currentsleep AS t2g ,mes_td_black AS b WHERE t2g.src_rnc = b.rnc AND t2g.src_lcid = b.lcid"""
    execute_sql("MainDB",clearerrorsleep4)
    clearerrorsleep5 = """DELETE t2l.* FROM mes_t2l_currentsleep AS t2l ,mes_td_black AS b WHERE t2l.src_rnc = b.rnc AND t2l.src_lcid = b.lcid"""
    execute_sql("MainDB",clearerrorsleep5)
    clearerrorsleep6 = """DELETE t2l.* FROM mes_t2l_permanence_currentsleep AS t2l ,mes_td_black AS b WHERE t2l.src_rnc = b.rnc AND t2l.src_lcid = b.lcid"""
    execute_sql("MainDB",clearerrorsleep6)
    clearerrorsleep7 = """DELETE t2l.* FROM mes_t2l_static_currentsleep AS t2l ,mes_td_black AS b WHERE t2l.src_rnc = b.rnc AND t2l.src_lcid = b.lcid"""
    execute_sql("MainDB",clearerrorsleep7)
    clearerrorsleep8 = """DELETE l2l.* FROM mes_l2l_currentsleep AS l2l ,mes_lte_black AS b WHERE l2l.src_enodebid = b.enodebid AND l2l.src_localcellid = b.localcellid"""
    execute_sql("MainDB",clearerrorsleep8)
    clearerrorsleep9 = """DELETE l2l.* FROM mes_l2l_many_currentsleep AS l2l ,mes_lte_black AS b WHERE l2l.src_enodebid = b.enodebid AND l2l.src_localcellid = b.localcellid"""
    execute_sql("MainDB",clearerrorsleep9)