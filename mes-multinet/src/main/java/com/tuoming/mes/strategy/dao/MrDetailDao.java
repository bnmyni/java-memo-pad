package com.tuoming.mes.strategy.dao;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.MrDetailModel;

public interface MrDetailDao extends BaseDao<MrDetailModel, Integer> {

    List<MrDetailModel> querySetList(String groupName);

    void removeAllData(String tableName);

    void insertData(String resTable, String querySql);


}
