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
        pm 采集就是下载文件，将 pm_lte_eutrancelltdd_hw 数据备份到 pm_lte_eutrancelltdd_hw ，再将新的数据写入到 pm_lte_eutrancelltdd_hw_his 表中
    """
    mes = MESManager()
    service = mes.getBean('SEBizService')
    context = service.buildContext()
    """
     ["pm_gsm_cellgprs_eric",
     "pm_gsm_cellqoseg_eric",
     "pm_gsm_cellqosg_eric",
     "pm_gsm_celtchf_eric",
     "pm_gsm_celtchfp_eric",
     "pm_gsm_celtchh_eric",
     "pm_gsm_cltch_eric",
     "pm_gsm_mrf_4c0003ed_hw",
     "pm_gsm_trafdlgprs_eric",
     "pm_lte_eutrancelltdd_eric",
     "pm_lte_eutrancelltdd_hw",
     "pm_lte_eutrancelltdd_zte",
     "pm_td_carrier_hw_cell",
     "pm_td_carrier_zte_cell",
     "pm_td_utrancell_hw",
     "pm_td_utrancell_zte"] 为这些表生成历史表 pm_td_utrancell_zte_his 并清空所有这些表
     
      对应兰州的场景下，只有 pm_lte_eutrancelltdd_hw 是有用的，其他的都没有用
    """
    service.dataHandle(Constant.BEFORE_COLLECT_DATA_ADD_BATCHID)#将pm历史数据更新到历史表 *_his中
    
    """
     pm 采集流程
        1. 查询所有的 mes_bscname_config
        2. 查询 mes_ftp_command a and a.group_name = 'PM' and a.enabled = TRUE
        3. 每条 mes_ftp_command 记录生成一个线程
        4. 执行文件合并，生成csv文件并，将cvs文件数据导入到mes_ftp_command.target_table_map[1]表中
            在甘肃lte中对应的表是 pm_lte_eutrancelltdd_hw
    """
    service.query(Constant.PM,Constant.CURRENT_BATCH) 
    """
        每次都计算kpi
        根据　mes_sleepsel_setting　表的配置，　KPI_CAL_REAL　对应的是　mes_lte_real_kpi　表
        BIGDATA_KPI_CAL_HIS　对应的是　mes_lte_kpi　表
    """
    service.calKpi(Constant.BIGDATA_KPI_CAL_HIS)
