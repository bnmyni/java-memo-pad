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

package com.pyrlong.configuration;

import com.google.common.collect.Maps;
import com.pyrlong.Envirment;
import com.pyrlong.collection.NameObjectCollectionBase;
import com.pyrlong.collection.NameValueCollection;
import com.pyrlong.configuration.handler.ConfigurationSectionHandler;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.Convert;
import com.pyrlong.util.StringUtil;
import com.pyrlong.xml.XmlOper;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * @author James Cheung
 *         <p/>
 *         澶勭悊绯荤粺鍙傛暟閰嶇疆鐨勫叕鍏辩被,鎻愪緵甯哥敤閰嶇疆鏂瑰紡鐨勫皝瑁呭強鑷畾涔夐厤缃璞＄殑
 * @version 1.0
 */
public final class ConfigurationManager {
    private static ConfigurationManager defaultConfig = new ConfigurationManager();
    private static Logger logger = LogFacade.getLog4j(ConfigurationManager.class);
    private NameObjectCollectionBase<Object> configSections;
    private String configFile = "conf/hamster.xml";
    private Map<String, Object> loadedConfigFile = Maps.newHashMap();
    Element docRoot;
    ConfigurationSectionCollection sections;
    public static LIFECYCLE LIFE_CYCLE = LIFECYCLE.WITHDB;

    public enum LIFECYCLE {
        WITHDB, WITHOUTDB
    }

    public ConfigurationManager() {
        configSections = new NameObjectCollectionBase<Object>();
    }

    public static ConfigurationManager getDefaultConfig() {
        return defaultConfig;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void addSection(String sectionName, Object section) {
        if (!configSections.containsKey(sectionName)) {
            configSections.add(sectionName, section);
        }
    }

    public synchronized Object getSection(String sectionName) {
        try {
            if (StringUtil.isEmpty(sectionName)) {
                return null;
            }
            return configSections.get(sectionName);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static void main(String[] args) {
        ConfigurationManager instance =  new ConfigurationManager();

        String path = "C:\\Users\\sunke\\Documents\\WeChat Files\\AiyaBnmyni\\Files\\hamster.xml";
        instance.openConfiguration(path);
    }

    /**
     * @param configFile
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void openConfiguration(String configFile) {
        // 棣栧厛鍒ゆ柇閰嶇疆鏂囦欢鏄惁宸茬粡鍔犺浇杩囷紝閬垮厤閲嶅鍔犺浇锛�
        if (loadedConfigFile.containsKey(configFile)) return;
        this.configFile = configFile;
        ConfigurationSectionCollection sectionList = new ConfigurationSectionCollection();
        String findConfigFile = Envirment.findFile(configFile);
        logger.info("Load config file : " + findConfigFile);
        try {
            Document doc = XmlOper.openDocument(findConfigFile);
            if (doc != null) {
                loadedConfigFile.put(configFile, null);
                Element root = XmlOper.getDocumentRoot(doc);
                if (root != null) {
                    List<Element> sectionRoot = XmlOper.getElementsByName(root, "configSections");
                    List<Element> sections = XmlOper.getElementsByName(sectionRoot.get(0), "section");
                    for (Element e : sections) {
                        ConfigurationSection section = new ConfigurationSection();
                        section.setName(XmlOper.getElementAttr(e, "name"));
                        section.setHandle(XmlOper.getElementAttr(e, "handle"));
                        sectionList.add(section.getName(), section);
                    }
                    this.sections = sectionList;
                    docRoot = root;
                    loadSections();
                }
            }
        } catch (Exception ex) {
            logger.fatal("Error read file " + findConfigFile);
            logger.error(ex.getMessage(), ex);
            System.exit(0);
        }
    }

    public void loadSections() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (sections == null || docRoot == null) {
            logger.warn("sections or docRoot not Initialization!");
            return;
        }
        for (ConfigurationSection section : sections) {
            if (section != null) {
                logger.info("Load section " + section.getName());
                ConfigurationSectionHandler handle = (ConfigurationSectionHandler) Class.forName(section.getHandle()).newInstance();
                Object o = handle.parseConfig(XmlOper.getFirstElementByName(docRoot, section.getName()), section, this);
                if (o != null)
                    configSections.add(section.getName(), o);
            }
        }
        updateEnv();
    }

    private void updateEnv() {
        NameObjectCollectionBase<String> list = (NameObjectCollectionBase<String>) getAppSetting();
        Map<String, String> appSetting = list.getObjectMap();
        Envirment.updateEnv(appSetting);
        AdvanceObjectCollection collection = ConfigurationManager.getDefaultConfig().getAdvanceObjectCollection();
    }

    public NameValueCollection getAppSetting() {
        try {
            Object section = getSection("appSettings");
            if (section == null) {
                section = new NameValueCollection();
                configSections.add("appSettings", section);
                NameValueCollection mapCol = (NameValueCollection) section;
                PropertyConfigurationCollection collection = (PropertyConfigurationCollection) getSection("properties");
                if (collection != null) {
                	for (Properties p : collection) {
                		Enumeration<Object> en = p.keys();
                		while (en.hasMoreElements()) {
                			String name = en.nextElement().toString();
                			String value = p.getProperty(name);
                			mapCol.add(name, value);
                		}
                	}
                }
                return mapCol;
            }
            return (NameValueCollection) section;
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
            return new NameValueCollection();
        }
    }

    public void set(String name, String value) {
        if (getAppSetting().containsKey(name))
            getAppSetting().remove(name);
        getAppSetting().add(name, value);
    }

    public Boolean getBoolean(String configName) {
        return getBoolean(configName, false);
    }

    public Boolean getBoolean(String configName, Boolean defalutValue) {
        if (getAppSetting().containsKey(configName))
            return getAppSetting().get(configName).toLowerCase().equals("true") || getAppSetting().get(configName).toLowerCase().equals("1");
        return defalutValue;
    }

    public void setValue(String name, String value) {
        getAppSetting().add(name, value);
    }

    public Double getDouble(String configName, Double defaultValue) {
        if (getAppSetting().containsKey(configName)) return Convert.toDouble(getAppSetting().get(configName));
        return defaultValue;
    }

    public String getString(String configName, String defaultValue) {
        if (getAppSetting().containsKey(configName))
            return getAppSetting().get(configName);
        return defaultValue;
    }

    public Long getLong(String configName, Long defaultValue) {
        if (getAppSetting().containsKey(configName)) return Convert.toLong(getAppSetting().get(configName));
        return defaultValue;
    }

    public Integer getInteger(String configName, Integer defaultValue) {
        if (getAppSetting().containsKey(configName)) {
            return Convert.toInt(getAppSetting().get(configName));
        }
        return defaultValue;
    }

    public Properties getProperty(String name) {
        try {
            PropertyConfigurationCollection collection = (PropertyConfigurationCollection) getSection("properties");
            return collection.get(name);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public AdvanceObjectCollection getAdvanceObjectCollection() {
        try {
            Object section = getSection("advance-objects");
            if (section == null) {
                section = new AdvanceObjectCollection();
                configSections.add("advance-objects", section);
            }
            return (AdvanceObjectCollection) section;
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
            return null;
        }
    }
}
