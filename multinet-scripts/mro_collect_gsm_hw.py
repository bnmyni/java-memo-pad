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
    if service.sfCollectMr("GSM_HW",mrCollectTerm["GSM_HW"]):
        ftpLogCommandService = mes.getService("FtpLogCommandService")
        ftpLogCommandService.queryAll("MRO_GSM_HW",int(context.get(Constant.CURRENT_BATCH_MR)))
        sql = """UPDATE pm_gsm_mrf_4c0003f0_hw
SET cellid = SUBSTRING_INDEX(src_bcch_bcc_ncc, ':', 1),bcch = SUBSTRING_INDEX( SUBSTRING_INDEX(src_bcch_bcc_ncc, ':', 2),
	':' ,- 1 ), ncc = SUBSTRING_INDEX(src_bcch_bcc_ncc, ':' ,- 1),
 bcc = SUBSTRING_INDEX( SUBSTRING_INDEX(src_bcch_bcc_ncc, ':', - 2), ':', 1 ) where batch_id=%d""" %(int(context.get(Constant.CURRENT_BATCH_MR)))
        execute_sql("MainDB",sql)
    