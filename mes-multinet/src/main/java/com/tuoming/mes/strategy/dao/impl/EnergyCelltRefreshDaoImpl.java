package com.tuoming.mes.strategy.dao.impl;

import org.springframework.stereotype.Repository;

import java.util.List;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseDao;
import com.tuoming.mes.strategy.dao.EnergyCellRefreshDao;
import com.tuoming.mes.strategy.model.EnergyCellRefreshSetting;

/**
 * 节能小区列表刷新Dao实现
 *
 * @author Administrator
 */
@SuppressWarnings("unchecked")
@Repository("energyCellRefreshDao")
public class EnergyCelltRefreshDaoImpl extends
        AbstractBaseDao<EnergyCellRefreshSetting, Integer> implements
        EnergyCellRefreshDao {

    @Override
    public List<EnergyCellRefreshSetting> queryRefCellSetting(String busyType,
                                                              String groupName) {
        // TODO Auto-generated method stub
        String sql = HQL_LIST_ALL
                + " where enabled = 1 and bustype = ? and groupname=? order by resTable";
        return this.getSession().createQuery(sql).setString(0, busyType)
                .setString(1, groupName).list();
    }

    @Override
    public String createTempTable(String sql) {
        // TODO Auto-generated method stub
        String tablename = "ref_" + System.currentTimeMillis();
        String sql1 = "create table " + tablename + " as " + sql;
        this.getSession().createSQLQuery(sql1).executeUpdate();
        String alterSqlA = "alter table " + tablename
                + " modify column src_vender varchar(50)";
        String alterSqlB = "alter table " + tablename
                + " modify column dest_vender varchar(50)";
        this.getSession().createSQLQuery(alterSqlA).executeUpdate();
        this.getSession().createSQLQuery(alterSqlB).executeUpdate();
        return tablename;
    }

    @Override
    public void removeTable(String tableName) {
        // TODO Auto-generated method stub
        try {

            String sql = "drop table if exists " + tableName;
            this.getSession().createSQLQuery(sql).executeUpdate();
        } catch (Exception e) {
            System.out.println("没有找到目标表！！");
        }
    }

    @Override
    public void createRstGtoGMrTable(String tableName) {
        // TODO Auto-generated method stub
        String sql = "create table " + tableName + " ("
                + "src_bsc varchar(50)," + "src_cellid varchar(50),"
                + "src_lac int(11)," + "src_ci int(11),src_vender varchar(20),"
                + "dest_bsc varchar(50)," + "dest_cellid varchar(50),"
                + "dest_lac int(11),"
                + "dest_ci int(11),dest_vender varchar(20),overdegree double"
                + ")";
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void createRstTtoGMrTable(String tableName) {
        // TODO Auto-generated method stub
        String sql = "create table " + tableName + " ("
                + "src_rnc varchar(50)," + "src_lcid int(11),"
                + "src_lac int(11)," + "src_vender varchar(50),"
                + "dest_bsc varchar(50)," + "dest_cellid varchar(50),"
                + "dest_lac int(11),"
                + "dest_ci int(11),dest_vender varchar(50),overdegree double"
                + ")";
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void createRstLtoLMrTable(String tableName) {
        // TODO Auto-generated method stub
        String sql = "create table "
                + tableName
                + " (omm varchar(50),userlabel varchar(50),subnetwork varchar(50),"
                + "src_enodebid int(11),"
                + "src_localcellid int(11),src_vender varchar(20),"
                + "dest_enodebid varchar(50),"
                + "dest_localcellid int(11),dest_vender varchar(20),overdegree double"
                + ")";
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void insertToRealTableForL2L(String real, String wlbm) {
        // TODO Auto-generated method stub
        // 看各个制式的sql语句情况改变
        String sql = "insert into "
                + real
                + "(src_enodebid,src_localcellid,src_vender,omm,userlabel,subnetwork"
                + ",dest_enodebid,dest_localcellid,dest_vender,overdegree"
                + ") select src_enodebid,src_localcellid,src_vender,omm,userlabel,subnetwork,dest_enodebid,dest_localcellid,dest_vender,overdegree from "
                + wlbm;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void insertToRealTableForG2G(String real, String wlbm) {
        // TODO Auto-generated method stub
        // 看各个制式的sql语句情况改变
        String sql = "insert into "
                + real
                + "(src_bsc,src_cellid,src_lac,src_ci,src_vender"
                + ",dest_bsc,dest_cellid,dest_lac,dest_ci,dest_vender,overdegree"
                + ") select src_bsc,src_cellid,src_lac,src_ci,src_vender"
                + ",dest_bsc,dest_cellid,dest_lac,dest_ci,dest_vender,overdegree from "
                + wlbm;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void insertToRealTableForT2G(String real, String wlbm) {
        // TODO Auto-generated method stub
        // 看各个制式的sql语句情况改变
        String sql = "insert into "
                + real
                + "(src_rnc,src_lcid,src_lac,src_vender"
                + ",dest_bsc,dest_cellid,dest_lac,dest_ci,dest_vender,overdegree"
                + ") select src_rnc,src_lcid,src_lac,src_vender"
                + ",dest_bsc,dest_cellid,dest_lac,dest_ci,dest_vender,overdegree from "
                + wlbm;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    public void createRstAzimuthTable(String tableName, String tempName) {
        String sql = "create table " + tableName + " as select * from "
                + tempName;
        this.getSession().createSQLQuery(sql).executeUpdate();
    }

    @Override
    /**
     * 黑白名单及告警小区不可作为节能小区
     */
    public void deleteSrcGsmBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_ci) in (select lac,ci from mes_gsm_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_ci) in (select lac,ci from mes_gsm_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_ci) in (select lac,ci from mes_gsm_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();
    }

    @Override
    public void deleteLinGsmBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (dest_lac,dest_ci) in (select lac,ci from mes_gsm_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (dest_lac,dest_ci) in (select lac,ci from mes_gsm_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (dest_lac,dest_ci) in (select lac,ci from mes_gsm_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();

    }

    @Override
    public void deleteSrcTdBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select lac,lcid from mes_td_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select lac,lcid from mes_td_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select lac,lcid from mes_td_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();
    }

    @Override
    public void deleteSrcTdOffBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_ci) in (select lac,lcid from mes_td_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_ci) in (select lac,lcid from mes_td_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_ci) in (select lac,lcid from mes_td_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();
    }

    @Override
    public void deleteLinTdBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select a.src_lac,a.src_lcid from (")
                .append("select src_lac,src_lcid from ")
                .append(tempName)
                .append(" where (dest_lac,dest_lcid) in (select lac,lcid from mes_td_white )")
                .append(" GROUP BY src_lac,src_lcid) a)");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select a.src_lac,a.src_lcid from (")
                .append("select src_lac,src_lcid from ")
                .append(tempName)
                .append(" where (dest_lac,dest_lcid) in (select lac,lcid from mes_td_black )")
                .append(" GROUP BY src_lac,src_lcid) a)");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select a.src_lac,a.src_lcid from (")
                .append("select src_lac,src_lcid from ")
                .append(tempName)
                .append(" where (dest_lac,dest_lcid) in (select lac,lcid from mes_td_alarminfo )")
                .append(" GROUP BY src_lac,src_lcid) a)");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();

    }

    @Override
    public void deleteSrcLteBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_enodebid,src_localcellid) in (select enodebid,localcellid from mes_lte_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_enodebid,src_localcellid) in (select enodebid,localcellid from mes_lte_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_enodebid,src_localcellid) in (select enodebid,localcellid from mes_lte_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();

    }

    @Override
    public void deleteLinLteBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (dest_enodebid,dest_localcellid) in (select enodebid,localcellid from mes_lte_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (dest_enodebid,dest_localcellid) in (select enodebid,localcellid from mes_lte_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (dest_enodebid,dest_localcellid) in (select enodebid,localcellid from mes_lte_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();

    }

    @Override
    public void createResTable(String rsTable, String lsb) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ").append(rsTable)
                .append(" as select * from ").append(lsb).append(" where 1=2");
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
    }

    @Override
    public void addData(String rsTable, String lsb) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(rsTable).append("  select * from ")
                .append(lsb);
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
    }

    @Override
    public void deleteG2gCellInBySleep(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_bsc,src_cellid) IN (SELECT src_bscid,src_cellid FROM  mes_g2g_currentsleep) and (src_bsc,src_cellid) not IN (SELECT src_bscid,src_cellid from mes_g2g_currentsleep_fail)");//针对上一粒度执行休眠失败的小区不做过滤，重新分析
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (src_bsc,src_cellid) IN (SELECT dest_bscid,dest_cellid FROM  mes_g2g_currentsleep) ");
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_bsc,dest_cellid) IN (SELECT src_bscid,src_cellid FROM  mes_g2g_currentsleep) ");
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
        StringBuilder sql4 = new StringBuilder();
        sql4.append("DELETE FROM ").append(wlbm).append(" WHERE (src_bsc,src_cellid) IN (SELECT dest_bscid,dest_cellid FROM  mes_t2g_currentsleep) ");
        this.getSession().createSQLQuery(sql4.toString()).executeUpdate();
    }

    @Override
    public void deleteL2lCellInBySleep(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_currentsleep) and (src_enodebid,src_localcellid) not IN (select src_enodebid,src_localcellid from mes_l2l_currentsleep_fail)");
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_l2l_currentsleep) ");
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_enodebid,dest_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_currentsleep)");
        StringBuilder sql4 = new StringBuilder();
        /******************Neusoft*******************/
        //sql4.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_enodebid,dest_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2t_currentsleep)");
        sql4.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_t2l_currentsleep)");

        //StringBuilder sql5 = new StringBuilder();
        //sql5.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_t2l_currentsleep) and (src_enodebid,src_localcellid) not IN (select src_enodebid,src_localcellid from mes_l2t_currentsleep_fail)");
        //多补一l2l中成功休眠的小区不能作为一补一l2l的休眠小区
        StringBuilder sql5 = new StringBuilder();
        sql5.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_many_currentsleep) and (src_enodebid,src_localcellid) not IN (select src_enodebid,src_localcellid from mes_l2l_many_currentsleep_fail)");
        //多补一l2l中成功休眠的小区不能作为一补一l2l的补偿小区
        StringBuilder sql6 = new StringBuilder();
        sql6.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_enodebid,dest_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_many_currentsleep)");

        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql4.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql5.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql6.toString()).executeUpdate();
    }

    @Override
    public void deleteT2gCellInBySleep(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2g_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2g_currentsleep_fail)");
        /****************Neusoft***************************/
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2l_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2l_currentsleep_fail)");
        //删除t2t多补一
        StringBuilder sql4 = new StringBuilder();
        sql4.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2t_many_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2t_many_currentsleep_fail)");
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_bsc,dest_cellid) in (SELECT src_bscid,src_cellid from mes_g2g_currentsleep)");
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql4.toString()).executeUpdate();
    }

    @Override
    public void deleteL2tCellInBySleep(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_currentsleep) and (src_enodebid,src_localcellid) not IN (select src_enodebid,src_localcellid from mes_l2l_currentsleep_fail)");
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_l2l_currentsleep) ");
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_rnc,dest_lcid) in (SELECT src_rnc,src_lcid from mes_t2g_currentsleep)");
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
    }

    /************************Neusoft******************************/
    @Override
    public void deleteT2lCellInBySleep(String wlbm) {
        //删除源小区中t2l场景已休眠小区
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2l_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2l_currentsleep_fail)");
        //删除补偿小区中l2l场景已休眠小区
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (dest_enodebid,dest_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_currentsleep)");
        //删除源小区中t2g场景已休眠小区,不包含休眠失败的
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2g_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2g_currentsleep_fail)");
        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
    }

    /**
     * 删除邻区在Lte制式下黑白名单的对应主小区，告警信息的节能小区
     *
     * @param tempName
     */
    @Override
    public void deleteManyNcLteBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_enodebid,src_localcellid) in (select a.src_enodebid,a.src_localcellid from (")
                .append("select src_enodebid,src_localcellid from  ")
                .append(tempName)
                .append("  where (dest_enodebid,dest_localcellid) in (select enodebid,localcellid from mes_lte_white )")
                .append(" GROUP BY src_enodebid,src_localcellid) a)");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_enodebid,src_localcellid) in (select a.src_enodebid,a.src_localcellid from (")
                .append("select src_enodebid,src_localcellid from  ")
                .append(tempName)
                .append(" where (dest_enodebid,dest_localcellid) in (select enodebid,localcellid from mes_lte_black )")
                .append(" GROUP BY src_enodebid,src_localcellid) a)");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_enodebid,src_localcellid) in (select a.src_enodebid,a.src_localcellid from (")
                .append("select src_enodebid,src_localcellid from  ")
                .append(tempName)
                .append(" where (dest_enodebid,dest_localcellid) in (select enodebid,localcellid from mes_lte_alarminfo )")
                .append(" GROUP BY src_enodebid,src_localcellid) a)");
        ;
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();

    }

    @Override
    public void deleteL2lManyCellInBySleep(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_currentsleep) and (src_enodebid,src_localcellid) not IN (select src_enodebid,src_localcellid from mes_l2l_currentsleep_fail)");
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_l2l_currentsleep) ");
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ")
                .append(wlbm)
                .append(" where (src_enodebid,src_localcellid) in (select a.src_enodebid,a.src_localcellid from (")
                .append("select src_enodebid,src_localcellid from  ")
                .append(wlbm)
                .append(" WHERE (dest_enodebid,dest_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_currentsleep)")
                .append(" GROUP BY src_enodebid,src_localcellid) a)");
        StringBuilder sql4 = new StringBuilder();
        sql4.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_t2l_currentsleep)");
        StringBuilder sql5 = new StringBuilder();
        sql5.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_many_currentsleep) and (src_enodebid,src_localcellid) not IN (select src_enodebid,src_localcellid from mes_l2l_many_currentsleep_fail)");
        StringBuilder sql6 = new StringBuilder();
        sql6.append("DELETE FROM ").append(wlbm).append(" WHERE (src_enodebid,src_localcellid) in (SELECT dest_enodebid,dest_localcellid from mes_l2l_many_currentsleep)");
        StringBuilder sql7 = new StringBuilder();
        sql7.append("DELETE FROM ")
                .append(wlbm)
                .append(" where (src_enodebid,src_localcellid) in (select a.src_enodebid,a.src_localcellid from (")
                .append("select src_enodebid,src_localcellid from  ")
                .append(wlbm)
                .append(" WHERE (dest_enodebid,dest_localcellid) in (SELECT src_enodebid,src_localcellid from mes_l2l_many_currentsleep)")
                .append(" GROUP BY src_enodebid,src_localcellid) a)");

        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql4.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql5.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql6.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql7.toString()).executeUpdate();
    }

    /**
     * 删除源小区为空的无效数据
     *
     * @param tempName
     */
    @Override
    public void deleteTDisNullBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append("  where (src_rnc,src_lcid) in (select a.* from (select src_rnc,src_lcid from ")
                .append(tempName).append(" WHERE src_lac is NULL group by src_rnc,src_lcid) a )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
    }

    /**
     * 删除源小区在td多补一制式下黑白名单，告警信息的节能小区
     *
     * @param tempName
     */
    @Override
    public void deleteSrcTDManyBwa(String tempName) {
        StringBuilder sqlWhite = new StringBuilder();
        sqlWhite.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select lac,lcid from mes_td_white )");
        StringBuilder sqlBlack = new StringBuilder();
        sqlBlack.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select lac,lcid from mes_td_black )");
        StringBuilder sqlAlarm = new StringBuilder();
        sqlAlarm.append("delete from ")
                .append(tempName)
                .append(" where (src_lac,src_lcid) in (select lac,lcid from mes_td_alarminfo )");
        this.getSession().createSQLQuery(sqlWhite.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlBlack.toString()).executeUpdate();
        this.getSession().createSQLQuery(sqlAlarm.toString()).executeUpdate();
    }

    /**
     * 从节能列表中删除T2T多补一场景当前休眠的小区，以及作为补偿的小区
     *
     * @param wlbm
     */
    @Override
    public void deletet2tManyCellInBySleep(String wlbm) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2g_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2g_currentsleep_fail)");
        StringBuilder sql2 = new StringBuilder();
        sql2.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2l_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2l_currentsleep_fail)");
        StringBuilder sql3 = new StringBuilder();
        sql3.append("DELETE FROM ")
                .append(wlbm)
                .append(" where (src_rnc,src_lcid) in (select a.src_rnc,a.src_lcid from (")
                .append("select src_rnc,src_lcid from  ")
                .append(wlbm)
                .append(" WHERE (dest_rnc,dest_lcid) in (SELECT src_rnc,src_lcid from mes_t2g_currentsleep)")
                .append(" GROUP BY src_rnc,src_lcid) a)");

        StringBuilder sql4 = new StringBuilder();
        sql4.append("DELETE FROM ")
                .append(wlbm)
                .append(" where (src_rnc,src_lcid) in (select a.src_rnc,a.src_lcid from (")
                .append("select src_rnc,src_lcid from  ")
                .append(wlbm)
                .append(" WHERE (dest_rnc,dest_lcid) in (SELECT src_rnc,src_lcid from mes_t2l_currentsleep)")
                .append(" GROUP BY src_rnc,src_lcid) a)");

        StringBuilder sql5 = new StringBuilder();
        sql5.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT src_rnc,src_lcid from mes_t2t_many_currentsleep) and (src_rnc,src_lcid) not IN (select src_rnc,src_lcid from mes_t2t_many_currentsleep_fail)");
        StringBuilder sql6 = new StringBuilder();
        sql6.append("DELETE FROM ").append(wlbm).append(" WHERE (src_rnc,src_lcid) in (SELECT dest_rnc,dest_lcid from mes_t2t_many_currentsleep)");
        StringBuilder sql7 = new StringBuilder();
        sql7.append("DELETE FROM ")
                .append(wlbm)
                .append(" where (src_rnc,src_lcid) in (select a.src_rnc,a.src_lcid from (")
                .append("select src_rnc,src_lcid from  ")
                .append(wlbm)
                .append(" WHERE (dest_rnc,dest_lcid) in (SELECT src_rnc,src_lcid from mes_t2t_many_currentsleep)")
                .append(" GROUP BY src_rnc,src_lcid) a)");

        this.getSession().createSQLQuery(sql.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql2.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql3.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql4.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql5.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql6.toString()).executeUpdate();
        this.getSession().createSQLQuery(sql7.toString()).executeUpdate();


    }

}
