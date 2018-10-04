package com.tuoming.mes.strategy.model;

import java.io.Serializable;
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
@Table(name = "mes_bscname_config")
public class BscNameConf implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "srcname", nullable = false)
    private String srcName;
    @Column(name = "bsc", nullable = false)
    private String bsc;
    @Column(name = "type", nullable = false)
    private int type;
    @Column(name = "srcname1", nullable = false)
    private String srcName1;

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getBsc() {
        return bsc;
    }

    public void setBsc(String bsc) {
        this.bsc = bsc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSrcName1() {
        return srcName1;
    }

    public void setSrcName1(String srcName1) {
        this.srcName1 = srcName1;
    }


}
