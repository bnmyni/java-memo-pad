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

import com.pyrlong.collection.NameObjectCollectionBase;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;
import com.tuoming.mes.collect.dpp.models.ConnectionStringSetting;

public class ConnectionStringSettingsCollection extends
        NameObjectCollectionBase<ConnectionStringSetting> {
    private String defaultConfig;

    public static ConnectionStringSettingsCollection getCollection() {
        ConnectionStringSettingsCollection connectionStringSettings = (ConnectionStringSettingsCollection)
                ConfigurationManager.getDefaultConfig().getSection(DPPConstants.DB_CONNECTION_SECTION_NAME);
        if (connectionStringSettings == null) {
            connectionStringSettings = new ConnectionStringSettingsCollection();
            ConfigurationManager.getDefaultConfig().addSection(DPPConstants.DB_CONNECTION_SECTION_NAME, connectionStringSettings);

        }
        return connectionStringSettings;
    }

    public static ConnectionStringSetting getConnectionSetting(String name) {
        synchronized (ConnectionStringSettingsCollection.class) {
            ConnectionStringSettingsCollection connectionStringSettings = (ConnectionStringSettingsCollection)
                    ConfigurationManager.getDefaultConfig().getSection(DPPConstants.DB_CONNECTION_SECTION_NAME);
            if (connectionStringSettings != null && connectionStringSettings.containsKey(name)) {
                return connectionStringSettings.get(name);
            }
            if (name.equals(DPPConstants.DB_DEFAULT_NAME)) {
                ConnectionStringSetting conn = new ConnectionStringSetting();
                conn.setName(DPPConstants.DB_DEFAULT_NAME);
                conn.setPassword(ConfigurationManager.getDefaultConfig().getString("jdbc.password", "not set"));
                conn.setUrl(ConfigurationManager.getDefaultConfig().getString("jdbc.url", "jdbc:mysql://127.0.0.1:3306/not set"));
                conn.setDriverClass(ConfigurationManager.getDefaultConfig().getString("jdbc.driverClassName", "com.mysql.jdbc.Driver"));
                conn.setUsername(ConfigurationManager.getDefaultConfig().getString("jdbc.username", "not set"));
                return conn;
            }
            return null;
        }
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public ConnectionStringSetting getDefault() {
        if (!StringUtil.isEmpty(defaultConfig) && containsKey(defaultConfig)) {
            return get(defaultConfig);
        }
        return null;
    }
}
