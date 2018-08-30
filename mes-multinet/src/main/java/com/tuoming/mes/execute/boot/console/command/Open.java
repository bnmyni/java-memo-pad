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
//import com.pyrlong.net.Server;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.execute.boot.console.Command;
import com.tuoming.mes.execute.boot.console.ShellSession;
import com.tuoming.mes.services.serve.ServerService;

/**
 * 连接指定名称的服务器并执行命令交互操作
 */
public class Open implements Command {

    @Override
    public Object execute(ShellSession session, String[] args) {
        //如果没有指定要连接的服务器名称，则提示用户输入并返回
        if (args.length == 0) {
            System.out.println("Please specify the name of the server you want to connect, for example: open BSC11");
            printHelp();
        } else {
            ServerService server = AppContext.getBean(ServerService.class);
            try {
                server.init(args[0]);
                server.setEcho(true);
                server.login();
                char c;
                StringBuffer sb = new StringBuffer();
                while ((c = (char) System.in.read()) > 0) {
                    if (c == '\n') {
                        String cmd = sb.toString();
                        cmd = cmd.trim();
                        server.write(cmd + "\r\n");
                        if (cmd.toLowerCase().trim().equals("exit") || cmd.toLowerCase().trim().equals("quit")) {
                            break;
                        }
                        sb = new StringBuffer();
                    } else {
                        sb.append(c);
                    }
                }
                server.logout();
            } catch (Exception e) {
                server.logout();
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public String getDescription() {
        return "Open a connection to the specified server, support SSH / Telnet protocols, connection information in aos_servers";
    }

    @Override
    public void printHelp() {
        LogFacade.print("open [Server name in aos_servers]\n");
    }

}
