package com.tuoming.mes.strategy.model;


public class MroManyCellModel {
    //本小区enodebid
    private String enodebid;
    //本小区localcellid
    private String localcellid;
    //本小区采样点标识
    private String mmeUeS1apId;
    //本小区采样时间
    private String timestamp;
    //本小区objectID
    private String objectID;
    //邻区载波频点
    private String ncEarfcn;
    //物理小区标识
    private String ncPci;

    public String getEnodebid() {
        return enodebid;
    }

    public void setEnodebid(String enodebid) {
        this.enodebid = enodebid;
    }

    public String getLocalcellid() {
        return localcellid;
    }

    public void setLocalcellid(String localcellid) {
        this.localcellid = localcellid;
    }

    public String getMmeUeS1apId() {
        return mmeUeS1apId;
    }

    public void setMmeUeS1apId(String mmeUeS1apId) {
        this.mmeUeS1apId = mmeUeS1apId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getNcEarfcn() {
        return ncEarfcn;
    }

    public void setNcEarfcn(String ncEarfcn) {
        this.ncEarfcn = ncEarfcn;
    }

    public String getNcPci() {
        return ncPci;
    }

    public void setNcPci(String ncPci) {
        this.ncPci = ncPci;
    }

}
