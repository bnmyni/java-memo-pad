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

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.pyrlong.util.scripts.AbstractEngine;
import com.tuoming.mes.collect.dpp.dao.SysStatusDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.models.SysStatus;

/**
 * Hamster系统工具类，提供一些系统运行常用的静态方法
 *
 * @author James Cheung
 */
public class Application {

    private final static String[] commandTypes = new String[]{".py", ".js", ".groovy", ".dsl", ".ruby"};
    private static Logger logger = LogFacade.getLog4j(Application.class);

    public static String getProjectName() {
        return System.getProperty(Constants.PROJECT_NAME);
    }

    public static void setStatus(String s) {
        SysStatusDao statusDao = AppContext.getBean(SysStatusDao.class);
        SysStatus status = statusDao.get(Constants.APP_STATUS);
        if (status == null) {
            status = new SysStatus();
            status.setName(Constants.APP_STATUS);
            status.setOrderId(0);
            status.setRemark("系统运行状态");
            status.setGroup(Constants.DEFAULT_APP_NAME);
            status.setCaption(Constants.APP_STATUS);
        }
        status.setCheckTime(DateUtil.currentDate());
        status.setResult(s);

        statusDao.saveOrUpdate(status);
    }

    public static String getStatus(String taskname) {
        return "1";
//		logger.info(" get status start! "+taskname);
//		SysStatusDao statusDao = AppContext.getBean(SysStatusDao.class);
//		logger.info(" get status bean end!"+taskname);
//		SysStatus status = statusDao.get(Constants.APP_STATUS);
//		logger.info(" get status end!"+taskname);
//		if (status == null)
//			return "1";// 默认认为系统是运行的
//		return status.getResult();
    }

    /**
     * 当前线程休眠指定时间，单位毫秒
     *
     * @param millis
     */
    public static void sleep(int millis) {
        try {
            Thread.currentThread().sleep(millis * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setup() throws IOException {
        String ver = "x32";
        if (Version.getVM().indexOf("64") > 0)
            ver = "x64";
        List<String> pathList = new ArrayList<String>();
        pathList.add(Envirment.getHome() + "bin");
        pathList.add(Envirment.getHome() + "extlibs");
        for (String path : pathList) {
            List<String> files = FileOper.getSubFiles(path, "." + ver);
            for (String file : files) {
                String newFile = file.replace("." + ver, "");
                if (FileOper.isFileExist(newFile))
                    FileOper.delFile(newFile);
                FileOper.copyTo(file, newFile);
            }
        }
    }

    /**
     * 执行一个dsl脚本文件
     *
     * @param file 要执行的脚本文件名
     */
    public static void load(String file) {
        try {
            String scriptFile = Envirment.findFile(file);
            DSLUtil.getDefaultInstance().evalFile(scriptFile, Envirment.getEnvs());
        } catch (Exception e) {
        }
    }

    /**
     * 基于当前运行上下文格式化字符串
     *
     * @param input 要格式化的字符串配置 ，配置格式类似
     *              <p>
     *              当前配置的Path=$path$
     *              </p>
     *              用户$$标识需要替换或执行的表达式内容
     * @return 格式化之后的字符串
     * @deprecated 请直接调用DSLUtil中对应方法
     */
    public static String buildString(String input) {
        return DSLUtil.getDefaultInstance().buildString(input, Envirment.getEnvs());
    }

    public static void evalFile(String scriptFile) {
        evalFile(getProjectName(), scriptFile);
    }

    /**
     * 执行一个脚本文件，系统自动根据扩展名判断脚本类型
     *
     * @param scriptFile 要执行的脚本文件名，如果该文件位于系统环境变量配置的path列表内，可以直接传递文件名，否则需要传递全路径
     */
    public static void evalFile(String groupName, String scriptFile) {
        evalFile(groupName, scriptFile, "");
    }

    /**
     * 执行脚本文件，带参数的方法
     *
     * @param scriptFile 要执行的脚本文件名，如果该文件位于系统环境变量配置的path列表内，可以直接传递文件名，否则需要传递全路径
     * @param args       脚本文件所用参数，参数格式为 A#B#C,目前参数只支持py脚本
     */
    public static void evalFile(String groupName, String scriptFile, String args) {
        try {
            if (StringUtil.isEmpty(scriptFile))
                return;
            String projectHome = FileOper.formatePath(Envirment.getHome() + "scripts/" + groupName);
            // 如果文件有扩展名并且位于工程目录
            String cmdFile = projectHome + scriptFile;
            System.out.println(cmdFile);
            // 如果没有找到,则在工程目录下分别找对应扩展名的文件
            if (!FileOper.isFileExist(cmdFile) && scriptFile.indexOf(".") < 0) {
                for (String ty : commandTypes) {
                    cmdFile = projectHome + scriptFile + ty;
                    if (FileOper.isFileExist(cmdFile))
                        break;
                }
            }
            // 在整个目录下找
            if (!FileOper.isFileExist(cmdFile)) {
                if (scriptFile.indexOf(".") > 0) {
                    cmdFile = Envirment.findFile(scriptFile);
                } else {
                    for (String ty : commandTypes) {
                        cmdFile = Envirment.findFile(scriptFile + ty);
                        if (FileOper.isFileExist(cmdFile))
                            break;
                    }
                }
            }
            // 如果还没找到则看作是表达式去执行
            if (!FileOper.isFileExist(cmdFile)) {
                DSLUtil.getDefaultInstance().compute(scriptFile);
                return;
            }
            // ==================================================
            logger.info("eval " + scriptFile);
            String engineName = "";
            if (cmdFile.endsWith(".py")) {
                engineName = "python";
            } else if (cmdFile.endsWith(".groovy")) {
                engineName = "groovy";
            } else if (cmdFile.endsWith(".ruby")) {
                engineName = "ruby";
            } else if (cmdFile.endsWith(".js")) {
                engineName = "js";
            }
            if (StringUtil.isNotBlank(engineName)) {
                AbstractEngine engine = AbstractEngine.getEngine(engineName);
                engine.setArgsVariables("args", args);
                engine.evalFile(projectHome, cmdFile);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
