package com.tuoming.mes.strategy.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Administrator
 */
@Entity
@Table(name = "cm_td_lst_tcell_hw")
public class TdLstTcellHW implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userlabel", nullable = false)
    private String userLabel;
    @Column(name = "starttime", nullable = false)
    private Date starttime;
    @Column(name = "rnc", nullable = false)
    private String rnc;
    @Column(name = "lcid", nullable = false)
    private String lcid;
    @Column(name = "loaclcellid", nullable = false)
    private int loaclcellid;
    @Column(name = "mac", nullable = false)
    private int mac;
    @Column(name = "lac", nullable = false)
    private int lac;
    @Column(name = "activestate", nullable = false)
    private String activestate;
    @Column(name = "adminstate", nullable = false)
    private String adminstate;
    @Column(name = "nodebname", nullable = false)
    private String nodebname;
    @Column(name = "batch_id", nullable = false)
    private int batch_id;

    public String getUserLabel() {
        return userLabel;
    }

    public void setUserLabel(String userLabel) {
        this.userLabel = userLabel;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public String getRnc() {
        return rnc;
    }

    public void setRnc(String rnc) {
        this.rnc = rnc;
    }

    public String getLcid() {
        return lcid;
    }

    public void setLcid(String lcid) {
        this.lcid = lcid;
    }

    public int getLoaclcellid() {
        return loaclcellid;
    }

    public void setLoaclcellid(int loaclcellid) {
        this.loaclcellid = loaclcellid;
    }

    public int getMac() {
        return mac;
    }

    public void setMac(int mac) {
        this.mac = mac;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public String getActivestate() {
        return activestate;
    }

    public void setActivestate(String activestate) {
        this.activestate = activestate;
    }

    public String getAdminstate() {
        return adminstate;
    }

    public void setAdminstate(String adminstate) {
        this.adminstate = adminstate;
    }

    public String getNodebname() {
        return nodebname;
    }

    public void setNodebname(String nodebname) {
        this.nodebname = nodebname;
    }

    public int getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(int batch_id) {
        this.batch_id = batch_id;
    }

}
