# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

##更新字段
def upCM_LTE_DSP_CELLPHYTOPO():
    upSql1 = """update CM_LTE_DSP_CELLPHYTOPO set CN=LEFT (AFINFO, 1) , SRN= SUBSTRING(AFINFO, 3, length(AFINFO) - 4) ,SN=RIGHT (AFINFO, 1)"""
    execute_sql("MainDB",upSql1)
def upCM_TD_DSP_LOCELL():
    upSql1 = """update CM_TD_DSP_LOCELL set sectorid=round(LCID/6,0) where lcid>=6"""
    execute_sql("MainDB",upSql1)
    upSql2 = """update cm_td_dsp_locell set sectorid=lcid where lcid<6"""
    execute_sql("MainDB",upSql2)
def upCM_TD_DSP_BRDMFRINFO():
    upSql1 = """update cm_td_dsp_brdmfrinfo set sectorid=mod(srn,10) where  NODEBNAME not like '%微_TD%'""" ##微蜂窝可能有N个RRU
    execute_sql("MainDB",upSql1)
    

##清空数据
def truncate_state_rru():
    sql1 = """truncate table cm_td_dsp_brdmfrinfo"""
    execute_sql("MainDB",sql1)
    info_print(sql1)

    sql2 = """truncate table cm_td_dsp_locell"""
    execute_sql("MainDB",sql2)
    info_print(sql2)
    
    sql3 = """truncate table cm_td_lst_rru"""
    execute_sql("MainDB",sql3)
    info_print(sql3)
    
    sql4 = """truncate table cm_lte_dsp_brdmfrinfo"""
    execute_sql("MainDB",sql4)
    info_print(sql4)

    sql5 = """truncate table cm_lte_dsp_cellphytopo"""
    execute_sql("MainDB",sql5)
    info_print(sql5)

    sql6 = """truncate table cm_lte_lst_rru"""
    execute_sql("MainDB",sql6)
    info_print(sql6)
    
if __name__ == '__main__':
    
    mes = MESManager()
    service = mes.getBean('SEBizService')

    ##清空单模双模参数
    truncate_state_rru()
    
    logCommandService = mes.getService("LogCommandService")
    service.dataHandle("BEFORE_COLLECT_DATA_CLEAN_CM_FIRST")
    logCommandService.queryAll("FIRST", Constant.CURRENT_BATCH) ##采集LST NE
    service.dataHandle("AFTER_COLLECT_DATA_IMPORTDATA_CM_FIRST")
    
    logCommandService.queryAll("TEST", Constant.CURRENT_BATCH) ##LST RRU,DSP LOCELL,DSP CELLPHYTOPO
    logCommandService.queryAll("TEST11", Constant.CURRENT_BATCH)  ##DSP BRDMFRINFO
    
    ##更新字段
    upCM_LTE_DSP_CELLPHYTOPO()
    upCM_TD_DSP_LOCELL()
    upCM_TD_DSP_BRDMFRINFO()
   
    
