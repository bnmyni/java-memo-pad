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

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;

/**
 * 获取系统版本信息的工具类
 *
 * @author James Cheung
 * @version 1.0
 */
public class Version {

    public static void print() {
        String os = System.getProperty(Constants.OS_NAME);
        String osUser = System.getProperty(Constants.USER_NAME);
        String osArch = System.getProperty(Constants.OS_ARCH); //操作系统构架
        String osVersion = System.getProperty(Constants.OS_VERSION); //操作系统版本

        System.out.println(" " + Envirment.LINE_SEPARATOR + ConfigurationManager.getDefaultConfig().getString(Constants.HAMSTER_APP_NAME, Constants.DEFAULT_APP_NAME) + getVersion());
        System.out.println(" Build " + ConfigurationManager.getDefaultConfig().getString(Constants.BUILD_VERSION, getVersion()) + " (" + ConfigurationManager.getDefaultConfig().getString(Constants.LAST_BUILD_TIME, "?") + ")");
        System.out.println(getVM());
        System.out.println(String.format(" JVM Home:%s", System.getProperty(Constants.JAVA_HOME)));
        System.out.println(String.format(" %s on %s %s - %s", osUser, os, osVersion, osArch));
        System.out.println();
    }

    public static void showSplash() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println();
        System.out.println("*****************************************************************************");
        System.out.println(" Welcome to " + getName() + " " + getVersion());
        System.out.println(" Type \"help\" , \"version\" , \"copyright\", or \"license\" for more information. ");
        System.out.println("*****************************************************************************");
        return;
    }

    public static String getName() {
        return ConfigurationManager.getDefaultConfig().getString(Constants.HAMSTER_APP_NAME, Constants.DEFAULT_APP_NAME);
    }

    public static String getCompanyName() {
        return ConfigurationManager.getDefaultConfig().getString(Constants.COMPANY, Constants.DEFAULT_COMPANY);
    }

    public static void copyright() {
        System.out.println("\n Copyright (c) 2012-2013 " + getCompanyName() + " All Rights Reserved. ");
        System.out.println(" The use and distribution terms for this software are contained in the file");
        System.out.println(" named license.txt, which can be found in the root of this distribution.");
        System.out.println(" By using this software in any fashion, you are agreeing to be bound by the");
        System.out.println(" terms of this license.\n");
    }

    /**
     * 获取当前运行环境中的JVM信息
     *
     * @return 当前JVM的版本信息
     */
    public static String getVM() {
        return String.format(" %s  %s (%s)", System.getProperty(Constants.JVM_NAME), System.getProperty(Constants.JVM_VERSION),
                System.getProperty(Constants.JVM_VENDOR));
    }

    /**
     * 获取当前系统版本的描述信息
     *
     * @return 当前版本的描述字符串，如 v1.0.0 (build1.0.0916)
     */
    public static String getVersion() {
        return "v" + ConfigurationManager.getDefaultConfig().getString(Constants.MAJOR_VERSION, "?");
    }
}