package com.tuoming.mes.strategy.model;


public class TdMroNCellModel {
    private String scRncId;
    private String scCellId;
    private String ncUarfcn;
    private String ncSc;
    private int count1;
    private int count2;
    private int count3;

    public String getScRncId() {
        return scRncId;
    }

    public void setScRncId(String scRncId) {
        this.scRncId = scRncId;
    }

    public String getScCellId() {
        return scCellId;
    }

    public void setScCellId(String scCellId) {
        this.scCellId = scCellId;
    }

    public String getNcUarfcn() {
        return ncUarfcn;
    }

    public void setNcUarfcn(String ncUarfcn) {
        this.ncUarfcn = ncUarfcn;
    }

    public String getNcSc() {
        return ncSc;
    }

    public void setNcSc(String ncSc) {
        this.ncSc = ncSc;
    }

    public int getCount1() {
        return count1;
    }

    public void addCount1() {
        this.count1++;
    }

    public int getCount2() {
        return count2;
    }

    public void addCount2() {
        this.count2++;
    }

    public int getCount3() {
        return count3;
    }

    public void addCount3() {
        this.count3++;
    }

}
