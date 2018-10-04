package com.tuoming.mes.strategy.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.strategy.model.NotifyModel;

public interface NotifyDao extends BaseDao<NotifyModel, Integer> {

    /**
     * 查询唤醒配置
     *
     * @param groupName
     * @return
     */
    public List<NotifyModel> querySetList(String groupName);

    /**
     * 根据配置sql查询要处理的数据
     *
     * @param querySql
     * @param collectDate
     * @param collectDate
     * @return
     */
    public List<Map<String, Object>> queryDataList(String querySql, Date collectDate);

}
