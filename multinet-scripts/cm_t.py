# -*- coding: utf-8 -*- 
from parameters import *
from tuoming import *
from com.tuoming.mes.strategy.consts import Constant
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

def delDiff_cell():
    sql = """create table temp_delDiff_cell select * from rst_l2l_mr_detail"""
    execute_sql("MainDB",sql)
        
    sql1 = """insert into temp_delDiff_cell select src_localcellid,src_enodebid,null,null,null,null,src_vender,dest_localcellid,dest_enodebid,dest_vender,1,1,0.8 from rst_lte_lte_azimuth"""
    execute_sql("MainDB",sql1)
    
    sql2 = """truncate table rst_l2l_mr_detail"""
    execute_sql("MainDB",sql2)
    
    sql2 = """insert into rst_l2l_mr_detail select src_localcellid,src_enodebid,max(subnetwork),userlable,omm,enodebname,src_vender,dest_localcellid,dest_enodebid,dest_vender,sstate,nstate,overdegree  
    from temp_delDiff_cell group by src_enodebid,src_localcellid,dest_enodebid,dest_localcellid order by userlable desc"""
    execute_sql("MainDB",sql2)
    
    sql2 = """drop table temp_delDiff_cell"""
    execute_sql("MainDB",sql2)
    
    sql3 = """update rst_l2l_mr_detail a INNER JOIN  cm_lte_enbfunction_hw b on a.src_enodebid=b.enodebid 
    join cm_lte_eutrancelltdd_hw c on c.subnetwork=b.subnetwork and c.managedelement=b.managedelement and a.src_localcellid=c.localcellId
    set a.subnetwork=b.subnetwork,a.userlable=c.userlabel,a.omm=b.omm,a.enodebname=b.enodebfunctionname"""
    execute_sql("MainDB",sql3)
    
##MR和位置信息融合 17:38 2016/6/24
def delDiff_cellg2g():
    sql = """create table temp_delDiff_cellg select * from rst_g2g_mr_detail"""
    execute_sql("MainDB",sql)
        
    sql1 = """insert into temp_delDiff_cellg select src_bsc,null,src_lac,src_ci,src_vender,dest_bsc,null,dest_lac,dest_ci,dest_vender,1,1,0.99 from rst_gsm_gsm_azimuth"""
    execute_sql("MainDB",sql1)
    
    sql2 = """truncate table rst_g2g_mr_detail"""
    execute_sql("MainDB",sql2)
    
    sql2 = """insert into rst_g2g_mr_detail select * from temp_delDiff_cellg"""
    execute_sql("MainDB",sql2)
    
    sql2 = """drop table temp_delDiff_cellg"""
    execute_sql("MainDB",sql2)
    
    sql3 = """update rst_g2g_mr_detail a INNER JOIN  cm_gsm_lst_gcell_hw b on a.src_bsc=b.bsc and a.src_ci=b.ci
    set a.src_cellid=b.cellindex"""
    execute_sql("MainDB",sql3)

    sql3 = """update rst_g2g_mr_detail a INNER JOIN  cm_gsm_lst_gcell_hw b on a.dest_bsc=b.bsc and a.dest_ci=b.ci
    set a.dest_cellid=b.cellindex"""
    execute_sql("MainDB",sql3)
    
    sql3 = """update rst_g2g_mr_detail a INNER JOIN  cm_gsm_rldep_eric b on a.src_bsc=b.bsc and a.src_ci=b.ci
    set a.src_cellid=b.cell"""
    execute_sql("MainDB",sql3)
    
    sql3 = """update rst_g2g_mr_detail a INNER JOIN  cm_gsm_rldep_eric b on a.dest_bsc=b.bsc and a.dest_ci=b.ci
    set a.dest_cellid=b.cell"""
    execute_sql("MainDB",sql3)        

##多补一 Lte 2016.10.03
def delDiff_celll2lMany():

    ###多补一的场合，多个补偿小区其中有无法与cm数据匹配的场合，将该休眠小区的所有数据去除
    sql = """create table temp_many_delDiff_cell select * from  rst_l2l_many_detail md WHERE not exists(select a.enodebid,a.localcellid from rst_l2l_many_hw a WHERE not exists(
    select 1 from rst_l2l_many_detail b where a.enodebid = b.src_enodebid and a.localcellid = b.src_localcellid and a.mr_ltencearfcn = b.dest_ltencearfcn
    and a.mr_ltencpci = b.dest_ltencpci) and a.enodebid = md.src_enodebid and a.localcellid = md.src_localcellid group by a.enodebid, a.localcellid)"""
    execute_sql("MainDB",sql)
    
    sql1 = """truncate table rst_l2l_many_detail"""
    execute_sql("MainDB",sql1)
    
    sql2 = """insert into rst_l2l_many_detail select * from temp_many_delDiff_cell"""
    execute_sql("MainDB",sql2)
    
    sql2 = """drop table temp_many_delDiff_cell"""
    execute_sql("MainDB",sql2)
    
    sql3 = """update rst_l2l_many_detail a INNER JOIN  cm_lte_enbfunction_hw b on a.src_enodebid=b.enodebid 
    join cm_lte_eutrancelltdd_hw c on c.subnetwork=b.subnetwork and c.managedelement=b.managedelement and a.src_localcellid=c.localcellId
    set a.subnetwork=b.subnetwork,a.userlable=c.userlabel,a.omm=b.omm,a.enodebname=b.enodebfunctionname"""
    execute_sql("MainDB",sql3)
    
def truncate_state():
    sql1 = """truncate table cm_lte_dsp_cell_hw"""
    execute_sql("MainDB",sql1)
    info_print(sql1)

    sql2 = """truncate table cm_lte_lst_cell_hw"""
    execute_sql("MainDB",sql2)
    info_print(sql2)
    
    sql3 = """truncate table cm_gsm_lst_gcell_hw"""
    execute_sql("MainDB",sql3)
    info_print(sql3)
    
    sql4 = """truncate table cm_gsm_rlstp_eric"""
    execute_sql("MainDB",sql4)
    info_print(sql4)

    sql5 = """truncate table cm_td_lst_tcell_hw"""
    execute_sql("MainDB",sql5)
    info_print(sql5)

def update_cagroupcell():
    sql1 = """update cm_lte_lst_cagroupcell_hw_bak a join cm_lte_enbfunction_hw_bak b on a.ManagedElement=b.ManagedElement SET a.enodebname=b.enodebfunctionname ,a.enodebid=b.enodebid;"""
    execute_sql("MainDB",sql1)
    info_print(sql1)

def update_sps():
    sql1 = """update cm_lte_lst_cagroupcell_hw_bak a join cm_lte_enbfunction_hw_bak b on a.ManagedElement=b.ManagedElement  and a.subnetwork=b.subnetwork SET a.enodebname=b.enodebfunctionname ,a.enodebid=b.enodebid"""
    execute_sql("MainDB",sql1)
    info_print(sql1)
    
    sql2 = """update cm_lte_cellalgoswitch_hw_bak a join cm_lte_eutrancelltdd_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement and a.eutrancelltdd=b.eutrancelltdd SET a.userlabel=b.userlabel"""
    execute_sql("MainDB",sql2)
    info_print(sql2)

    sql3 = """update cm_lte_celllowpower_hw_bak a join cm_lte_eutrancelltdd_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement and a.eutrancelltdd=b.eutrancelltdd SET a.userlabel=b.userlabel"""
    execute_sql("MainDB",sql3)
    info_print(sql3)
    
    sql4 = """update cm_lte_cellrfshutdown_hw_bak a join cm_lte_eutrancelltdd_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement and a.eutrancelltdd=b.eutrancelltdd SET a.userlabel=b.userlabel"""
    execute_sql("MainDB",sql4)
    info_print(sql4)

    sql5 = """update cm_lte_cellshutdown_hw_bak a join cm_lte_eutrancelltdd_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement and a.eutrancelltdd=b.eutrancelltdd SET a.userlabel=b.userlabel"""
    execute_sql("MainDB",sql5)
    info_print(sql5)

    sql6 = """update cm_lte_interratcellshutdown_hw_bak a join cm_lte_eutrancelltdd_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement and a.eutrancelltdd=b.eutrancelltdd SET a.userlabel=b.userlabel"""
    execute_sql("MainDB",sql6)
    info_print(sql6)

    sql7 = """update cm_lte_enodebautopoweroff_hw_bak a join cm_lte_eutrancelltdd_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement SET a.userlabel=b.userlabel"""
    execute_sql("MainDB",sql7)
    info_print(sql7)

    sql8 = """update cm_lte_lst_cell_hw_bak a join cm_lte_enbfunction_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement SET a.enodebname=b.enodebfunctionname"""
    execute_sql("MainDB",sql8)
    info_print(sql8)
    
    sql9 = """update cm_lte_dsp_cell_hw_bak a join cm_lte_enbfunction_hw_bak b on  a.subnetwork=b.subnetwork and a.ManagedElement=b.ManagedElement SET a.enodebname=b.enodebfunctionname,a.enodebid=b.enodebid,a.sps=0"""
    execute_sql("MainDB",sql9)
    info_print(sql9)

if __name__ == '__main__':
    
    mes = MESManager()
    service = mes.getBean('SEBizService')
    #service.dataHandle("BEFORE_COLLECT_DATA_CLEAN_CM_FTP")
    #ftpLogCommandService = mes.getService("FtpLogCommandService")
    #ftpLogCommandService.queryAll(Constant.CM,Constant.CURRENT_BATCH)
    #update_cagroupcell() ##更新cm_lte_lst_cagroupcell_hw_bak
    #update_sps() ##更新lst cell和dsl cell
    #service.dataHandle("AFTER_COLLECT_DATA_IMPORTDATA_CM_FTP_LTE")
    
    logCommandService = mes.getService("LogCommandService")
    service.dataHandle("BEFORE_COLLECT_DATA_CLEAN_CM_FIRST")
    logCommandService.queryAll("FIRST", Constant.CURRENT_BATCH)
    service.dataHandle("AFTER_COLLECT_DATA_IMPORTDATA_CM_FIRST")
    #service.dataHandle("BEFORE_COLLECT_DATA_CLEAN_CM_SECOND")
    #logCommandService.queryAll("SECOND", Constant.CURRENT_BATCH)
    #service.dataHandle("AFTER_COLLECT_DATA_IMPORTDATA_CM_SECOND")
    
    service.dataHandle("BEFORE_COLLECT_DATA_CLEAN_CM_PARA")
    logCommandService.queryAll("PARA", Constant.CURRENT_BATCH)
    service.dataHandle("AFTER_COLLECT_DATA_HANDLE_CM_DSPLTE")
    #service.dataHandle(Constant.AFTER_COLLECT_DATA_UPDATE_CARRIER_CM)
    #service.dataHandle("AFTER_COLLECT_DATA_IMPORTDATA_CM_PARA")
    #inSql1 = """UPDATE cm_td_lst_tcell_hw SET nodebname=REPLACE(nodebname,'"','')"""
    #execute_sql("MainDB",inSql1)
    #inSql2 = """DELETE from cm_lte_eutrancelltdd_hw where userlabel like '%s%%' or userlabel like  '%s%%' """%(str_decode('河池'),str_decode('崇左'))
    #execute_sql("MainDB",inSql2)
    #inSql3 =  """DELETE from cm_lte_enbfunction_hw where userlabel like '%s%%' or userlabel like  '%s%%' """%(str_decode('河池'),str_decode('崇左'))
    #execute_sql("MainDB",inSql3)
    #service.reCollectFailCommand(2)
    #service.improveMrData("")
    #delDiff_cell()
    #delDiff_cellg2g()
    ##多补一 Lte 2016.10.03
    #delDiff_celll2lMany()
