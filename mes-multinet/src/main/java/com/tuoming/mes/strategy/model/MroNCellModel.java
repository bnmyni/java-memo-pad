package com.tuoming.mes.strategy.model;


public class MroNCellModel {
    private String sid;
    private String enodebid;
    private String localcellid;
    private String s_Earfcn_LTE;
    private String s_Pci_LTE;
    private int count1;
    private int count2;
    private int count3;

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

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getS_Earfcn_LTE() {
        return s_Earfcn_LTE;
    }

    public void setS_Earfcn_LTE(String s_Earfcn_LTE) {
        this.s_Earfcn_LTE = s_Earfcn_LTE;
    }

    public String getS_Pci_LTE() {
        return s_Pci_LTE;
    }

    public void setS_Pci_LTE(String s_Pci_LTE) {
        this.s_Pci_LTE = s_Pci_LTE;
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

    public void addCount3(String s_rsrp, String n_rsrp) {
        int srsrp = Integer.parseInt(s_rsrp);
        int nrsrp = Integer.parseInt(n_rsrp);
        if (nrsrp >= 30) {
            this.count3++;
        }
//		if(srsrp-nrsrp>6){
//			this.count3++;
//		}
    }


}
