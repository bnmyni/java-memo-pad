package com.tuoming.mes.strategy.service;

/**
 * 检查采集服务器是否可访问
 *
 * @author Administrator
 */
public interface CheckServerAccessService {

    /**
     * 检查ftp服务器是否可访问
     */
    void checkFtpServerAccess();

    /**
     * 检查cmd服务器是否可访问
     */
    void checkCmdServerAccess();

}
