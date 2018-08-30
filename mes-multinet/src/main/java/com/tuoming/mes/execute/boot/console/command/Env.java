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

// Created On: 13-8-18 下午4:30
package com.tuoming.mes.execute.boot.console.command;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.pyrlong.Envirment;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.execute.boot.console.Command;
import com.tuoming.mes.execute.boot.console.ShellSession;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class Env implements Command {

    private static Logger logger = LogFacade.getLog4j(Env.class);

    @Override
    public Object execute(ShellSession session, String[] args) {
        Map<String, String> envMap = Envirment.getEnvs();
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        System.out.println("================================================");
        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        return "";
    }

    @Override
    public String getDescription() {
        return "Displays the current system environment variable settings";
    }

    @Override
    public void printHelp() {
        LogFacade.print("env");
    }
}
