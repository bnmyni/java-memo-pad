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


import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import java.util.List;
import com.pyrlong.collection.CollectionsBase;
import com.pyrlong.collection.NameValueCollection;
import com.pyrlong.configuration.AdvanceObjectCollection;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.configuration.ConfigurationSection;
import com.pyrlong.configuration.handler.ConfigurationSectionHandlerBase;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.reflector.ClassReflector;
import com.pyrlong.util.StringUtil;
import com.pyrlong.xml.XmlOper;
import com.tuoming.mes.collect.dpp.dao.CommonDao;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.collect.dpp.models.Appsetting;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;


public class ConfigurationDbImportHandler extends
        ConfigurationSectionHandlerBase {
    private static Logger logger = LogFacade
            .getLog4j(ConfigurationDbImportHandler.class);
    CommonDao commonDao;

    public ConfigurationDbImportHandler() {
        if (ConfigurationManager.LIFE_CYCLE == ConfigurationManager.LIFECYCLE.WITHDB) {
            commonDao = AppContext.getBean("CommonDao");
            loadAppsetting();
            loadDbConnection();
        }
    }

    private void loadAppsetting() {
        List<Appsetting> appsettings = commonDao.listAll(Appsetting.class, "", "");
        NameValueCollection collection = ConfigurationManager.getDefaultConfig().getAppSetting();
        for (Appsetting appsetting : appsettings) {
            if (collection.contains(appsetting.getName()))
                collection.remove(appsetting.getName());
            collection.add(appsetting.getName(), appsetting.getValue());
            //logger.info(appsetting.getName() + "=" + appsetting.getResult());
        }
    }

    private void loadDbConnection() {
        ConnectionStringSettingsCollection connectionStringSettings = ConnectionStringSettingsCollection.getCollection();
        List<ConnectionStringSetting> conns = commonDao.listAll(ConnectionStringSetting.class, "", "");
        for (ConnectionStringSetting con : conns) {
            if (connectionStringSettings.contains(con.getName()))
                connectionStringSettings.remove(con.getName());
            connectionStringSettings.add(con.getName(), con);
        }
    }

    //加载自定义对象配置
    @Override
    public Object parseConfig(Element root, ConfigurationSection section,
                              ConfigurationManager manager) {
        if (ConfigurationManager.LIFE_CYCLE == ConfigurationManager.LIFECYCLE.WITHOUTDB)
            return ConfigurationManager.getDefaultConfig().getAdvanceObjectCollection();
        List<Element> objectElements = XmlOper.getElementsByName(root, "add");
        AdvanceObjectCollection collects = ConfigurationManager.getDefaultConfig().getAdvanceObjectCollection();
        for (Element e : objectElements) {
            if (e != null) {
                try {
                    //读取各个配置项
                    String name = XmlOper.getElementAttr(e, "name").trim();
                    String model = XmlOper.getElementAttr(e, "model").trim();
                    String filter = XmlOper.getElementAttr(e, "filter") + "";
                    String orderby = XmlOper.getElementAttr(e, "order") + "";
                    String group = XmlOper.getElementAttr(e, "group") + "";
                    //从数据库读取
                    //如果没有指定分组字段则所获取对象都保存到Name所指对象集合中
                    String groupName = name;
                    List<Object> objs = commonDao.listAll(ClassReflector.getClass(model), orderby, filter);
                    for (Object o : objs) {
                        if (StringUtil.isNotEmpty(group)) {
                            groupName = "" + ClassReflector.getPropertyValue(group, o);
                        }
                        CollectionsBase<Object> instances = collects.getObjectCollection(groupName);
                        if (instances == null) {
                            instances = new CollectionsBase<Object>();
                            collects.add(groupName, instances);
                        }
                        instances.add(o);
                    }
                } catch (Exception e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
        }
        return collects;
    }
}