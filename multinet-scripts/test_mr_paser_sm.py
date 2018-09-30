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
    用于解析mro数据
    """
    mes = MESManager()
    service = mes.getBean('SEBizService')
    """
        第一个参数是mro文件的本地路径
        第二个参数是需要匹配的mro文件格式，支持正则表达式
        第三个参数是mro文件写入到数据库的表名后缀，可以是me,mm,mw,ne,nm,nw,se,sm,sw [现在只用到sm]
        
        版本中还提供了只有前面2个参数的方法，后续考虑合并优化
    """
    service.l2lhwMRParser("F:/LTE_HW_MRO8/","TD-LTE_MRO_HUAWEI_\\d+_(\\d+)_\\d+.xml.gz","sm")
    

