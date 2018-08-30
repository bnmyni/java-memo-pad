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

import java.util.HashMap;
import java.util.Map;

import com.tuoming.mes.execute.boot.console.Command;
import com.tuoming.mes.execute.boot.console.CommandSet;

public class BasicCommandSet implements CommandSet {

    public Map<String, Command> load() {
        Map<String, Command> cmds = new HashMap<String, Command>();
        cmds.put("open", new Open());
        cmds.put("cls", new Cls());
        cmds.put("set", new SetEnv());
        cmds.put("exit", new Exit());
        cmds.put("env", new Env());
        cmds.put("quit", new Exit());
        cmds.put("start", new Start());
        cmds.put("stop", new Stop());
        return cmds;
    }
}
