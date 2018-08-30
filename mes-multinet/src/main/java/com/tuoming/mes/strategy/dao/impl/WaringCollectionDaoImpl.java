package com.tuoming.mes.strategy.dao.impl;

import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.consts.Constant;
import com.tuoming.mes.strategy.dao.WarningCollectionDao;
import com.tuoming.mes.strategy.model.SleepExeSetting;
import com.tuoming.mes.strategy.util.DateUtil;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by geyu on 2016/9/7.
 */
@Repository("warningCollectionDao")
public class WaringCollectionDaoImpl extends AbstractBaseDao<SleepExeSetting, Integer> implements WarningCollectionDao {

    /**
     * 添加告警信息
     * @param data
     * @param reason
     */
    @Override
    public void insertAlarm(Map<String, Object> data, String reason){
        String bus_type = String.valueOf(data.get("bus_type"));
        StringBuilder insSql = new StringBuilder();
        if(Constant.T2G.equals(bus_type)||Constant.T2L.equals(bus_type)) { //类型为T2G或T2L告警信息
            insSql.append("insert into mes_subscribe_td_selfalarm (id,rnc,lcid,lac,reason,starttime) values(?,?,?,?,?,?)");
            this.getSession().createSQLQuery(insSql.toString())
                    .setString(0, UUID.randomUUID().toString())
                    .setString(1, String.valueOf(data.get("src_rnc")))
                    .setInteger(2, Integer.parseInt(String.valueOf(data.get("src_lcid"))))
                    .setInteger(3, Integer.parseInt(String.valueOf(data.get("src_lac"))))
                    .setString(4, reason)
                    .setTimestamp(5, DateUtil.tranStrToDate(String.valueOf(data.get("starttime")))).executeUpdate();
        }else if(Constant.G2G.equals(bus_type)) { //类型为G2G告警信息
            insSql.append("insert into mes_subscribe_gsm_selfalarm (id,bscid,cellid,lac,ci,reason,starttime) values(?,?,?,?,?,?,?)");
            this.getSession().createSQLQuery(insSql.toString())
                    .setString(0, UUID.randomUUID().toString())
                    .setString(1, String.valueOf(data.get("src_bscid")))
                    .setString(2, String.valueOf(data.get("src_cellid")))
                    .setInteger(3, (Integer)data.get("src_lac"))
                    .setInteger(4, Integer.parseInt(String.valueOf(data.get("src_ci"))))
                    .setString(5, reason)
                    .setTimestamp(6, DateUtil.tranStrToDate(String.valueOf(data.get("starttime")))).executeUpdate();
        }else if(Constant.L2L.equals(bus_type)) { //类型为L2L告警信息
            insSql.append("insert into mes_subscribe_lte_selfalarm (id,enodebid,localcellid,reason,starttime) values(?,?,?,?,?)");
            this.getSession().createSQLQuery(insSql.toString())
                    .setString(0, UUID.randomUUID().toString())
                    .setInteger(1, Integer.parseInt(String.valueOf(data.get("src_enodebid"))))
                    .setInteger(2, Integer.parseInt(String.valueOf(data.get("src_localcellid"))))
                    .setString(3, reason)
                    .setTimestamp(4, DateUtil.tranStrToDate(String.valueOf(data.get("starttime"))))
                    .executeUpdate();
        }
    }

    /**
     * 添加指标恶化信息
     * @param data
     * @param type
     */
    @Override
    public void insertDeteriorate(Map<String, Object> data, String type) {
        StringBuilder insSql = new StringBuilder();
        if(Constant.GSM.equalsIgnoreCase(type)) {
            insSql.append("insert into mes_subscribe_gsm_deteriorate (id,pdchczl,tbffud,mxhwl,wxzylyl,starttime) values(?,?,?,?,?,?)");
            this.getSession().createSQLQuery(insSql.toString())
                    .setString(0, UUID.randomUUID().toString())
                    .setDouble(1, Double.parseDouble(String.valueOf(data.get("pdchczl"))))
                    .setDouble(2, Double.parseDouble(String.valueOf(data.get("tbffud"))))
                    .setDouble(3, Double.parseDouble(String.valueOf(data.get("mxhwl"))))
                    .setDouble(4, Double.parseDouble(String.valueOf(data.get("wxzylyl"))))
                    .setTimestamp(5, new Date()).executeUpdate();
        } else if(Constant.TD.equalsIgnoreCase(type)){
            insSql.append("insert into mes_subscribe_td_deteriorate (id,yyyw,mzylyl,zdyhs,zrabyscs,starttime) values(?,?,?,?,?,?)");
            this.getSession().createSQLQuery(insSql.toString())
                    .setString(0, UUID.randomUUID().toString())
                    .setDouble(1, Double.parseDouble(String.valueOf(data.get("yyyw"))))
                    .setDouble(2, Double.parseDouble(String.valueOf(data.get("mzylyl"))))
                    .setDouble(3, Double.parseDouble(String.valueOf(data.get("zdyhs"))))
                    .setDouble(4, Double.parseDouble(String.valueOf(data.get("zrabyscs"))))
                    .setTimestamp(5, new Date()).executeUpdate();
        } else if(Constant.LTE.equalsIgnoreCase(type)){
            insSql.append("insert into mes_subscribe_lte_deteriorate (id,prblyl,zdyhs,rabsbcs,starttime) values(?,?,?,?,?)");
            this.getSession().createSQLQuery(insSql.toString())
                    .setString(0, UUID.randomUUID().toString())
                    .setDouble(1, Double.parseDouble(String.valueOf(data.get("prblyl"))))
                    .setDouble(2, Double.parseDouble(String.valueOf(data.get("zdyhs"))))
                    .setDouble(3, Double.parseDouble(String.valueOf(data.get("rabsbcs"))))
                    .setTimestamp(4, new Date()).executeUpdate();
        }
    }

}
