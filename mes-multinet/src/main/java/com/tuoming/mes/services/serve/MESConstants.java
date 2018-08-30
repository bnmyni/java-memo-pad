/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tuoming.mes.services.serve;


/**
 * 应用于本工程的常量定义列表
 */
public class MESConstants {
    //=============配置名定义==============
    //命名规则为 工程名.配置名

    public final static String BATCH_KEY = "BATCH";

    //多线程默认配置
    public final static int THREAD_CORE_POOL_SIZE_DEFAULT = 5;
    public final static int THREAD_MAX_POOL_SIZE_DEFAULT = 10;
    public final static int THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT = 10;
    //系统封装的的自动优化多线程方法允许的并发线程数量等常量配置
    public final static String MANAGER_THREAD_NAME = "aos.manager";
    public final static String MANAGER_THREAD_CORE_POOL_SIZE = "aos.manager.thread_pool_size";
    public final static String MANAGER_THREAD_MAX_POOL_SIZE = "aos.manager.thread_maximum_pool_size";
    public final static String MANAGER_THREAD_KEEP_ALIVE_TIME_IN_SECOND = "aos.manager.thread_keep_alive_time_second";
    //指令下发多线程配置
    public final static String ADJUST_THREAD_NAME = "aos.adjust_command_pool";
    public final static String ADJUST_THREAD_CORE_POOL_SIZE = "aos.adjust.thread_pool_size";
    public final static String ADJUST_THREAD_MAX_POOL_SIZE = "aos.adjust.thread_maximum_pool_size";
    public final static String ADJUST_THREAD_KEEP_ALIVE_TIME_IN_SECOND = "aos.adjust.thread_keep_alive_time_second";
    public final static String ADJUST_SEND_FLAG = "aos.send_flag";
    //数据库查询配置
    public final static String QUERY_THREAD_NAME = "aos.query_command_pool";
    public final static String QUERY_THREAD_CORE_POOL_SIZE = "aos.query.thread_pool_size";
    public final static String QUERY_THREAD_MAX_POOL_SIZE = "aos.query.thread_maximum_pool_size";
    public final static String QUERY_THREAD_KEEP_ALIVE_TIME_IN_SECOND = "aos.query.thread_keep_alive_time_second";
    //FTP采集配置
    public final static String FTP_DELETE_LOCAL_FILE = "aos.delete_local_before_download";
    public final static String FTP_THREAD_NAME = "aos.ftp_command_pool";
    public final static String FTP_THREAD_CORE_POOL_SIZE = "aos.ftp.thread_pool_size";
    public final static String FTP_THREAD_MAX_POOL_SIZE = "aos.ftp.thread_maximum_pool_size";
    public final static String FTP_THREAD_KEEP_ALIVE_TIME_IN_SECOND = "aos.ftp.thread_keep_alive_time_second";
    //命令采集配置
    public final static String LOG_PARSER = "logParser";
    public final static String LOGFILE = "LOGFILE";
    public final static String LOG_THREAD_NAME = "aos.log_command_pool";
    public final static String LOG_THREAD_CORE_POOL_SIZE = "aos.log.thread_pool_size";
    public final static String LOG_THREAD_MAX_POOL_SIZE = "aos.log.thread_maximum_pool_size";
    public final static String LOG_THREAD_KEEP_ALIVE_TIME_IN_SECOND = "aos.log.thread_keep_alive_time_second";
    //
    public final static String NAC_THREAD_NAME = "aos.nac_alys_pool";
    public final static String NAC_THREAD_CORE_POOL_SIZE = "aos.nac.thread_pool_size";
    public final static String NAC_THREAD_MAX_POOL_SIZE = "aos.nac.thread_maximum_pool_size";
    public final static String NAC_THREAD_KEEP_ALIVE_TIME_IN_SECOND = "aos.nac.thread_keep_alive_time_second";
    //ServerService
    public final static String LOGIN_COMMAND_NAME = "login";
    public final static String LOGOUT_COMMAND_NAME = "logout";
    public final static String SERVER_SLEEP_BEFORE_RECONNECT_IN_MS = "aos.server.sleep_before_reconnect_in_ms";


    public static final String FTP_COMMAND_RESULT_FILTER="ftp_command_result_filter";


    //服务器连接池配置项
    public static final String SERVER_POOL_MAX_ACTIVE="aos.server_pool_max_active";
    public static final String SERVER_POOL_MAX_IDLE="aos.server_pool_max_idle";
    public static final String SERVER_POOL_MAX_WAIT="aos.server_pool_max_wait";
    public static final String SERVER_POOL_ACTIVATED="aos.server_pool_activated";



}
