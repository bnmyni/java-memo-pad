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

// Created On: 13-8-26 上午11:50
package com.tuoming.mes.execute.boot.console.command;

import org.apache.log4j.Logger;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.execute.boot.console.Command;
import com.tuoming.mes.execute.boot.console.ShellSession;

/**
 * 清屏命令
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public class Cls implements Command {

    private static Logger logger = LogFacade.getLog4j(Cls.class);

    @Override
    public Object execute(ShellSession session, String[] args) {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        return "";
    }

    @Override
    public String getDescription() {
        return "Clears the screen.";
    }

    @Override
    public void printHelp() {
        LogFacade.print("cls");
    }
}
