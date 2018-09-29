from tuoming import *
from com.tuoming.mes.services.serve import MESManager
import sys 
reload(sys)
sys.setdefaultencoding('utf-8')

dbName="MainDB"
mrCollectTerm = {"GSM_HW":3,"GSM_ERIC":3,"LTE_HW":3,"TD_HW":3}

def getCurrentDay():
    return int(time.strftime('%d'))
