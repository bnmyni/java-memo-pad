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

package com.tuoming.mes.collect.dpp.configuration;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.configuration.ConfigurationSection;
import com.pyrlong.configuration.handler.ConfigurationSectionHandlerBase;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.xml.XmlOper;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;

public class ConnectionStringSettingHandler extends ConfigurationSectionHandlerBase {
    private static Logger logger = LogFacade.getLog4j(ConnectionStringSettingHandler.class);

    @Override
    public Object parseConfig(Element root, ConfigurationSection section, ConfigurationManager manager) {
        ConnectionStringSettingsCollection conns = (ConnectionStringSettingsCollection) manager.getSection(section.getName());
        if (conns == null)
            conns = new ConnectionStringSettingsCollection();
        conns.setDefaultConfig(XmlOper.getElementAttr(root, "default"));
        List<Element> configs = XmlOper.getElementsByName(root, "add");
        for (Element e : configs) {
            if (e != null) {
                ConnectionStringSetting newConn = new ConnectionStringSetting();
                newConn.setName(XmlOper.getElementAttr(e, "name"));
                newConn.setDriverClass(XmlOper.getElementAttr(e, "driverClass"));
                newConn.setUsername(XmlOper.getElementAttr(e, "username"));
                newConn.setPassword(XmlOper.getElementAttr(e, "password"));
                newConn.setUrl(XmlOper.getElementAttr(e, "url"));
                //newConn.setServer(XmlOper.getElementAttr(e, "server"));
                conns.add(newConn.getName(), newConn);
                logger.info("Add DB " + newConn.getName());
            }
        }
        return conns;
    }


}
