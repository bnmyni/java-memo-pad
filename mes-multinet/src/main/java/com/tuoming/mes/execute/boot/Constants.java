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

package com.tuoming.mes.execute.boot;

/**
 * Created by James on 14-1-14.
 */
public class Constants {
    public final static String APP_STATUS = "hamster.status";
    //
    public final static String GUARD_PORT = "hamster.guard.port";
    public final static String MANAGE_PORT = "hamster.manager.port";
    public static final String PROMPT = "hsh> ";
    public final static String MANAGE_PROCESS_MAX_IDLE_TIME = "hamster.manager_max_idle_time";
    public final static String PROCESS_MAX_TIME = "hamster.process_max_time";
    public final static String TASK_MAX_NUM_ALLOWED = "hamster.max_task_count";
    public final static String OS_NAME = "os.name";
    public final static String USER_NAME = "user.name";
    public final static String OS_ARCH = "os.arch";
    public final static String OS_VERSION = "os.version";
    public final static String HAMSTER_APP_NAME = "hamster.app_name";
    public final static String DEFAULT_APP_NAME = " Hamster-Shell";
    public final static String BUILD_VERSION = "hamster.build_version";
    public final static String MAJOR_VERSION = "hamster.major_version";
    public final static String LAST_BUILD_TIME = "hamster.last_chanage";
    public final static String JAVA_HOME = "java.home";
    public final static String COMPANY = "hamster.company";
    public final static String JVM_NAME = "java.vm.name";
    public final static String JVM_VERSION = "java.version";
    public final static String JVM_VENDOR = "java.vm.vendor";
    public final static String DEFAULT_COMPANY = "PYRLONG";
    public final static String PROJECT_NAME = "PROJECT_NAME";


    //下面定义界面用到的现实信息常量
    public final static String LBL_BUTTON_NEXT_DEFAULT = "Next>";
    public final static String LBL_BUTTON_PREVIOUS_DEFAULT = "<Previous";
    public final static String LBL_DONE_DEFAULT = "Done";
    public final static String LBL_WIZARD_TITLE_DEFAULT = "Wizard";
    public final static String LBL_BUTTON_NEXT = "Next>";
    public final static String LBL_BUTTON_PREVIOUS = "<Previous";
    public final static String LBL_DONE = "Done";
    public final static String LBL_WIZARD_TITLE = "Wizard";


    public final static String LBL_DB_TYPE = "LBL_DB_TYPE";
    public final static String LBL_DB_TYPE_DEFAULT = "DataBase";

    public final static String LBL_DB_NAME = "LBL_DB_NAME";
    public final static String LBL_DB_NAME_DEFAULT = "Scheme";

    public final static String LBL_SERVER = "LBL_SERVER";
    public final static String LBL_SERVER_DEFAULT = "Server Name";

    public final static String LBL_SERVER_PORT = "LBL_SERVER_PORT";
    public final static String LBL_SERVER_PORT_DEFAULT = "Port";

    public final static String LBL_USER_NAME = "LBL_USER_NAME";
    public final static String LBL_USER_NAME_DEFAULT = "User name";

    public final static String LBL_PASSWORD = "LBL_PASSWORD";
    public final static String LBL_PASSWORD_DEFAULT = "Password";

    public final static String LBL_TEST = "LBL_TEST";
    public final static String LBL_TEST_DEFAULT = "Test & Save";

    public final static String MSG_CONNECTION_SUCCESS = "CONNECTION_SUCCESS";
    public final static String MSG_CONNECTION_SUCCESS_DEFAULT = "测试连接可用，已经保存!";

    public final static String TIP_DB_CONFIG = "  需要从本界面设置系统使用的主数据库连接信息，在进行相关设置之前，所需数据库环境应该已经安装完毕并且可以访问。\r\n ";

}
