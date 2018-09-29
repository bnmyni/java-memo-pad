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
    # 如果使用R语言的大数据预测则使用 service.rBigDataModel(context)
    service.bigDataForecast(context)
    print "====== DELETE rst_bigdata_pm_gsm_cellqosg_eric ======"
    clearerrorsleep = """DELETE FROM rst_bigdata_pm_gsm_cellqosg_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep)
    print "====== DELETE rst_bigdata_pm_gsm_cellgprs_eric ======"
    clearerrorsleep0 = """DELETE FROM rst_bigdata_pm_gsm_cellgprs_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep0)
    print "====== DELETE rst_bigdata_pm_gsm_mrf_4c0003ed_hw ======"
    clearerrorsleep1 = """DELETE FROM rst_bigdata_pm_gsm_mrf_4c0003ed_hw WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep1)
    print "====== DELETE rst_bigdata_pm_gsm_cellqoseg_eric ======"
    clearerrorsleep2 = """DELETE FROM rst_bigdata_pm_gsm_cellqoseg_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep2)
    print "====== DELETE rst_bigdata_pm_td_carrier_hw_cell ======"
    clearerrorsleep3 = """DELETE FROM rst_bigdata_pm_td_carrier_hw_cell WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep3)
    print "====== DELETE rst_bigdata_pm_td_carrier_zte_cell ======"
    clearerrorsleep4 = """DELETE FROM rst_bigdata_pm_td_carrier_zte_cell WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep4)
    print "====== DELETE rst_bigdata_pm_td_utrancell_hw ======"
    clearerrorsleep5 = """DELETE FROM rst_bigdata_pm_td_utrancell_hw WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep5)
    print "====== DELETE rst_bigdata_pm_td_utrancell_zte ======"
    clearerrorsleep6 = """DELETE FROM rst_bigdata_pm_td_utrancell_zte WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep6)
    print "====== DELETE rst_bigdata_pm_lte_eutrancelltdd_hw ======"
    clearerrorsleep7 = """DELETE FROM rst_bigdata_pm_lte_eutrancelltdd_hw WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep7)
    print "====== DELETE rst_bigdata_pm_lte_eutrancelltdd_zte ======"
    clearerrorsleep8 = """DELETE FROM rst_bigdata_pm_lte_eutrancelltdd_zte WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep8)
    print "====== DELETE rst_bigdata_pm_lte_eutrancelltdd_eric ======"
    clearerrorsleep9 = """DELETE FROM rst_bigdata_pm_lte_eutrancelltdd_eric WHERE starttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep9)
    print "====== DELETE rst_bigdata_pm_gsm_celtchf_eric ======"
    clearerrorsleep10 = """DELETE FROM rst_bigdata_pm_gsm_celtchf_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep10)
    print "====== DELETE rst_bigdata_pm_gsm_celtchfp_eric ======"
    clearerrorsleep11 = """DELETE FROM rst_bigdata_pm_gsm_celtchfp_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep11)
    print "====== DELETE rst_bigdata_pm_gsm_celtchh_eric ======"
    clearerrorsleep12 = """DELETE FROM rst_bigdata_pm_gsm_celtchh_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep12)
    print "====== DELETE rst_bigdata_pm_gsm_cltch_eric ======"
    clearerrorsleep13 = """DELETE FROM rst_bigdata_pm_gsm_cltch_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep13)
    print "====== DELETE rst_bigdata_pm_gsm_trafdlgprs_eric ======"
    clearerrorsleep14 = """DELETE FROM rst_bigdata_pm_gsm_trafdlgprs_eric WHERE rpttime < CONCAT(date_sub(curdate(),interval 1 day),' 00:00:00')"""
    execute_sql("MainDB",clearerrorsleep14)
    
    

    
    