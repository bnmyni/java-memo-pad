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

package com.tuoming.mes.execute.boot.console.command;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.execute.boot.console.Command;
import com.tuoming.mes.execute.boot.console.ShellSession;

/**
 * 系统推出命令
 */
public class Exit implements Command {

    public Object execute(ShellSession session, String[] args) {
        System.exit(0);
        return null;
    }


    public String getDescription() {
        return "exits the command shell";
    }

    public void printHelp() {
        LogFacade.print("exit");
    }
}
