package com.tuoming.mes.strategy.service;

/**
 * Mro文件主小区汇总
 *
 * @author Administrator
 */
public interface MroCollectService {

    void exeLteHwLocalAnaly(String dir, String regex);

    void exeLteHwLocalAnaly(String dir, String regex, String rname);

    /**
     * 解析华为TD的MRO文件
     */
    void exeTdHwLocalAnaly(String dir, String regex);


    void exeLteHwLocalAnaly2(int b, int e);

    void exeLteHwLocalAnaly3(int b, int e);


}
