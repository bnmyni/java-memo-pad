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

// Created On: 13-8-26 下午7:02
package com.tuoming.mes.execute.boot.console.command;

import org.apache.log4j.Logger;

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.execute.boot.console.Command;
import com.tuoming.mes.execute.boot.console.ShellSession;

/**
 * 设置环境变量命令
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class SetEnv implements Command {

    private static Logger logger = LogFacade.getLog4j(SetEnv.class);

    @Override
    public Object execute(ShellSession session, String[] args) {
        if (args.length == 2) {
            set(args[0], args[1]);
        } else if (args.length == 1) {
            String[] paras = args[0].split("=");
            if (paras.length == 2)
                set(paras[0].trim(), paras[1].trim());
            else {
                printHelp();
            }
        } else {
            new Env().execute(session, new String[]{});
        }
        return null;
    }

    private void set(String name, String value) {
        ConfigurationManager.getDefaultConfig().set(name, value);
        Envirment.setEnv(name, value);
    }

    @Override
    public String getDescription() {
        return "Show or set  current environment variables.";
    }

    @Override
    public void printHelp() {
        LogFacade.print("Type [set] without parameters to display the current environment variables. ");
        LogFacade.print("[set] command invoked with just a variable name, no equal sign or value\n" +
                "will display the value of all variables given to the [set] command. For example:");
        LogFacade.print("set a=12");
    }
}
