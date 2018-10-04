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

package com.tuoming.mes.collect.dpp.datatype;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.io.FileOper;

@Component("DPPAppContext")
public class AppContext implements ApplicationContextAware {

    public static String CACHE_ROOT = Envirment.getHome() + "data/";
    private static ApplicationContext applicationContext; // Spring应用上下文环境

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            String configFile = "../conf/applicationContext.xml";
            if (!FileOper.isFileExist(configFile))
                configFile = "conf/applicationContext.xml";
            if (!FileOper.isFileExist(configFile))
                configFile = "applicationContext.xml";
            applicationContext = new FileSystemXmlApplicationContext(configFile);
        }
        CACHE_ROOT = FileOper.formatePath(ConfigurationManager.getDefaultConfig().getString(DPPConstants.CACHE_ROOT, CACHE_ROOT));
        return applicationContext;
    }

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     *
     * @param context
     * @throws org.springframework.beans.BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static String getCacheFileName(String fileName) {
        String path = AppContext.CACHE_ROOT + DateUtil.getNow("yyyy_MM_dd") + Envirment.PATH_SEPARATOR + fileName;
        FileOper.checkAndCreateForder(path);
        return path;
    }

    public static String getCacheFileName(String fileName, boolean dateNoRelation) {
        String path = AppContext.CACHE_ROOT + DateUtil.getNow("yyyy_MM_dd") + Envirment.PATH_SEPARATOR + fileName;
        if (dateNoRelation) {
            path = AppContext.CACHE_ROOT + fileName;
        }
        String foderName = path;
        if (path.indexOf("/dynaic_dir/$") > 0) {
            foderName = path.substring(0, path.indexOf("/dynaic_dir/") + 12);
        }
        FileOper.checkAndCreateForder(foderName);
        return path;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws org.springframework.beans.BeansException
     */
    @SuppressWarnings("unchecked")
    public synchronized static <T> T getBean(String name) throws BeansException {
        return (T) getApplicationContext().getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws org.springframework.beans.BeansException
     */
    public synchronized static <T> T getBean(Class<T> clz) throws BeansException {
        @SuppressWarnings("unchecked")
        T result = (T) getApplicationContext().getBean(clz);
        return result;
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public synchronized static boolean containsBean(String name) {
        return getApplicationContext().containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getAliases(name);
    }

}
