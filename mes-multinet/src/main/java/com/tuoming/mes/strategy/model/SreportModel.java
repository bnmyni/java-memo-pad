package com.tuoming.mes.strategy.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import com.tuoming.mes.strategy.consts.Constant;


/**
 * 服务小区级别的采集点数
 *
 * @author Administrator
 */
public class SreportModel implements Serializable {

    private Set<String> s_report = new HashSet<String>();
    private Set<String> s_OCAreport_all = new HashSet<String>();
    private int s_OCAreport;
    private int s_OSreport;

    public int getS_OCAreport_all() {
        return s_OCAreport_all.size();
    }

    public void setS_OCAreport_all(String reportBs, String rsrpStr) {
        if (Constant.NIL.equals(rsrpStr)) {
            return;
        }
        int rsrp = Integer.parseInt(rsrpStr);
        if (rsrp > 31) {
            this.s_OCAreport_all.add(reportBs);
        }
    }

    public int getS_OCAreport() {
        return s_OCAreport;
    }

    public void setS_OCAreport(String srsrpStr, String nrsrpStr) {
        if (Constant.NIL.equals(srsrpStr) || Constant.NIL.equals(nrsrpStr)) {
            return;
        }
        int srsrp = Integer.parseInt(srsrpStr);
        int nrsrp = Integer.parseInt(nrsrpStr);
        if (srsrp > 31 && srsrp - nrsrp <= 6) {
            this.s_OCAreport++;
        }
    }

    public int getS_OSreport() {
        return s_OSreport;
    }

    public void setS_OSreport(String srsrpStr, String nrsrpStr) {
        if (Constant.NIL.equals(srsrpStr) || Constant.NIL.equals(nrsrpStr)) {
            return;
        }
        int srsrp = Integer.parseInt(srsrpStr);
        int nrsrp = Integer.parseInt(nrsrpStr);
        if (srsrp - nrsrp < 6) {
            this.s_OSreport++;
        }
    }

    public double getS_OCAscale() {
        if (s_OCAreport_all.size() == 0) {
            return 0;
        }
        return s_OCAreport / s_OCAreport_all.size();
    }

    public int getS_report() {
        return s_report.size();
    }

    public void setS_report(String sreport) {
        this.s_report.add(sreport);
    }
}
