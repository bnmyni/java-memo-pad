/*******************************************************************************
 * Copyright (c) 2013.  Pyrlong All rights reserved.
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

import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;

/**
 * Hamster应用运行的公共上下文环境对象，系统启动时需要首先调用本类中的init方法
 */
public class AppContext implements Serializable, Cloneable {
    /**
     * 用于调度器上下文内标识当前定时任务名称
     */
    public static final String TASK_PLAN_NAME = "TASK_PLAN_NAME";
    private static final long serialVersionUID = 2013992800707599153L;
    public static int MAX_TASK_THREAD_COUNT = 10;
    public static Integer PROCESS_MAX_TIME;
    private Logger logger = LogFacade.getLog4j(AppContext.class);
    public static Long ServerProcessMaxIdleTime = ConfigurationManager.getDefaultConfig().getLong(Constants.MANAGE_PROCESS_MAX_IDLE_TIME, 600000L);

    /**
     * 读取指定配置文件，初始化系统配置，一般默认读取conf目录下的hamster.xml文件作为系统配置的入口文件 初始化任务调度配置： pyrlong.hamster.maxTaskThreadCount——可以同时运行的任务数量，默认20
     * pyrlong.hamster.processMaxTime——单个处理进程超时时间，默认20分钟
     *
     * @param configFile
     *         配置文件名，一般为hamster.xml
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public static void init(String configFile) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        ConfigurationManager.getDefaultConfig().openConfiguration(configFile);
        // 基于配置文件初始化系统配置
        MAX_TASK_THREAD_COUNT = ConfigurationManager.getDefaultConfig().getInteger(Constants.TASK_MAX_NUM_ALLOWED, 20);
        PROCESS_MAX_TIME = ConfigurationManager.getDefaultConfig().getInteger(Constants.PROCESS_MAX_TIME, 1200000);
    }

}
