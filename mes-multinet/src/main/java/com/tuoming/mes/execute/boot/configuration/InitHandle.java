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

package com.tuoming.mes.execute.boot.configuration;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.configuration.ConfigurationSection;
import com.pyrlong.configuration.handler.ConfigurationSectionHandlerBase;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.pyrlong.xml.XmlOper;
import com.tuoming.mes.execute.boot.Application;

/**
 * 用于处理系统启动时需要执行的初始化操作
 * Created by james on 14-2-11.
 */
public class InitHandle extends ConfigurationSectionHandlerBase {
    private static Logger logger = LogFacade.getLog4j(InitHandle.class);

    @Override
    public Object parseConfig(Element root, ConfigurationSection section, ConfigurationManager manager) {
        List<Element> objectElements = XmlOper.getElementsByName(root, "add");
        for (Element e : objectElements) {
            if (e != null) {
                try {
                    //读取各个配置项
                    String group = XmlOper.getElementAttr(e, "group").trim();
                    String action = XmlOper.getElementAttr(e, "action").trim();
                    //用于控制不同工程的初始化，只有当没有指定工程的时候或者指定的工程与当前工程相同时才执行
                    if (StringUtil.isEmpty(group) || group.equals(Application.getProjectName()))
                        Application.evalFile(group, action);
                } catch (Exception e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
        }
        return null;
    }
}
