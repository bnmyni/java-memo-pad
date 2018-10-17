package com.tuoming.mes.strategy.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.MrDetailDao;
import com.tuoming.mes.strategy.model.MrDetailModel;
import com.tuoming.mes.strategy.service.handle.himpl.AzimuthCalculationHandle;
import com.tuoming.mes.strategy.util.FormatUtil;

@Repository("MrDetailDao")
public class MrDetailDaoImpl extends AbstractBaseDao<MrDetailModel, Integer> implements MrDetailDao {

    /**
     * 通过groupName 查询mes_mrdetail_model配置数据
     *
     * @param groupName 需要查询的groupName
     * @return 返回 groupName 对应的配置数据
     */
    @Override
    public List<MrDetailModel> querySetList(String groupName) {
        String hql = HQL_LIST_ALL + " WHERE enabled = 1 ";
        if (!StringUtils.isEmpty(groupName)) {
            hql += " and groupName like '" + groupName + "%'";
        }
        return this.getSession().createQuery(hql).list();
    }

    /**
     * 清空指定表数据
     *
     * @param tableName 需要清空的表
     */
    @Override
    public void removeAllData(String tableName) {
        this.getSession().createSQLQuery("truncate table " + tableName).executeUpdate();
    }

    /**
     * 通过 querySql 查询数据，查询结果通过经纬度计算出2个基站的距离，并将基站距离小于 500的数据写入到 resTable 中
     *
     * @param resTable 目标表
     * @param querySql 查询sql
     */
    @Override
    public void insertData(final String resTable, String querySql) {
        // 执行querySql并将查询结果通过map返回
        List<Map<String, Object>> dataList = this.getSession().createSQLQuery(querySql)
                .setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
        final List<Map<String, Object>> resList = new ArrayList<>();
        // 对数据进行遍历，筛选2个小区距离小于500米的数据
        for (Map<String, Object> data : dataList) {
            double slong = FormatUtil.tranferCalValue(data.get("srclong"));
            double slat = FormatUtil.tranferCalValue(data.get("srclat"));
            double dlong = FormatUtil.tranferCalValue(data.get("destlong"));
            double dlat = FormatUtil.tranferCalValue(data.get("destlat"));
            double instance = AzimuthCalculationHandle.getInstance(slong, slat, dlong, dlat);
            if (instance < 500) {
                resList.add(data);
            }
        }
        if (!resList.isEmpty()) {
            writeDataToResTable(resTable, resList);
        }
    }

    /**
     * 数据写入
     *
     * @param resTable 目标表
     * @param resList  需要写入的数据
     */
    private void writeDataToResTable(final String resTable, final List<Map<String, Object>> resList) {
        if (resTable.toLowerCase().indexOf("g2g") > 0) {
            writeG2GDataToResTable(resTable, resList);
        } else if (resTable.toLowerCase().indexOf("l2l") > 0) {
            writeL2LDataToResTable(resTable, resList);
        }
    }

    /**
     * l2l 数据写入
     *
     * @param resTable 目标表
     * @param resList  需要写入的数据
     */
    private void writeG2GDataToResTable(final String resTable, final List<Map<String, Object>> resList) {
        this.getSession().doWork(new Work() {

            public void execute(Connection connection) throws SQLException {
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append(resTable)
                        .append("(src_bsc,src_cellid,src_lac,src_ci,src_vender,dest_bsc,dest_cellid,")
                        .append("dest_lac,dest_ci,dest_vender,sstate,nstate,overdegree)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : resList) {
                    pst.setObject(1, map.get("src_bsc"));
                    pst.setObject(2, map.get("src_cellid"));
                    pst.setObject(3, map.get("src_lac"));
                    pst.setObject(4, map.get("src_ci"));
                    pst.setObject(5, map.get("src_vender"));
                    pst.setObject(6, map.get("dest_bsc"));
                    pst.setObject(7, map.get("dest_cellid"));
                    pst.setObject(8, map.get("dest_lac"));
                    pst.setObject(9, map.get("dest_ci"));
                    pst.setObject(10, map.get("dest_vender"));
                    pst.setObject(11, map.get("sstate"));
                    pst.setObject(12, map.get("nstate"));
                    pst.setObject(13, map.get("overdegree"));
                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

    /**
     * g2g 数据写入
     *
     * @param resTable 目标表
     * @param resList  需要写入的数据
     */
    private void writeL2LDataToResTable(final String resTable, final List<Map<String, Object>> resList) {
        this.getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                StringBuilder sql = new StringBuilder();
                sql.append("insert into ").append(resTable)
                        .append("(src_localcellid,src_enodebid,subnetwork,userlable,omm,enodebname,")
                        .append("src_vender,dest_localcellid,dest_enodebid,dest_vender,sstate,nstate,overdegree)")
                        .append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                PreparedStatement pst = connection.prepareStatement(sql.toString());
                for (Map<String, Object> map : resList) {
                    pst.setObject(1, map.get("src_localcellid"));
                    pst.setObject(2, map.get("src_enodebid"));
                    pst.setObject(3, map.get("subnetwork"));
                    pst.setObject(4, map.get("userlabel"));
                    pst.setObject(5, map.get("omm"));
                    pst.setObject(6, map.get("enodebname"));
                    pst.setObject(7, map.get("src_vender"));
                    pst.setObject(8, map.get("dest_localcellid"));
                    pst.setObject(9, map.get("dest_enodebid"));
                    pst.setObject(10, map.get("dest_vender"));
                    pst.setObject(11, map.get("sstate"));
                    pst.setObject(12, map.get("nstate"));
                    pst.setObject(13, map.get("overdegree"));
                    pst.addBatch();
                }
                pst.executeBatch();
                pst.clearBatch();
                pst.close();
            }
        });
    }

}
