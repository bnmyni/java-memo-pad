/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
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

package com.pyrlong.net.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TelnetOptionHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.commons.net.telnet.WindowSizeOptionHandler;
import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.pyrlong.Constants;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.net.AbstractCommandClient;
import com.pyrlong.net.RemoteServer;

/**
 * Created by james on 14-5-20.
 */
public class TelnetCommandClient extends AbstractCommandClient {

    private org.apache.commons.net.telnet.TelnetClient telnetClient;
    private static Logger logger = LogFacade.getLog4j(TelnetCommandClient.class);
    private static int terminalWidth = ConfigurationManager.getDefaultConfig().getInteger(Constants.TERMINAL_WIDTH, 160);
    private static int terminalHeight = ConfigurationManager.getDefaultConfig().getInteger(Constants.TERMINAL_HEIGHT, 300);
    InputStream stdin;
    OutputStream stdout;

    public TelnetCommandClient(RemoteServer remoteServer, EventBus eventBus) {
        super(remoteServer, eventBus);
        telnetClient = new TelnetClient(remoteServer.getVtType());
        TelnetOptionHandler sizeOpt = new WindowSizeOptionHandler(terminalWidth, terminalHeight, false, false, true, false);
        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler(remoteServer.getVtType(), false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(false, false, false, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
        try {
            telnetClient.addOptionHandler(sizeOpt);
            //addOptionHandler(ttopt);
            //addOptionHandler(echoopt);
            //addOptionHandler(gaopt);
        } catch (Exception e) {
            logger.error("Error registering option handlers: " + e.getMessage());
            logger.error(e);
        }
        telnetClient.registerNotifHandler(new TelnetNotificationHandle());
    }

    @Override
    public void open() {
        try {
            telnetClient.setConnectTimeout(10000);
            telnetClient.connect(remoteServer.getIp(), remoteServer.getPort());
            LogFacade.info("Successful connect to " + remoteServer);
            stdin = telnetClient.getInputStream();
            stdout = telnetClient.getOutputStream();
            setConnected(true);
        } catch (IOException e) {
            logger.error(remoteServer + "——" + e.getMessage(), e);
        }
    }

    @Override
    public void commit() throws IOException {
        stdout.flush();
    }

    @Override
    public void write(int b) throws IOException {
        stdout.write(b);
    }

    @Override
    public int read() throws IOException {
    	return stdin.read();
    }

    @Override
    public void close() {
        try {
            telnetClient.disconnect();
        } catch (IOException e) {
            logger.error(remoteServer + "  " + e.getMessage(), e);
        }
        setConnected(false);
    }

    class TelnetNotificationHandle implements TelnetNotificationHandler {

        /**
         * SE    240(F0)     子选项结束 SB    250(FA)     子选项开始 IAC   255(FF)     选项协商的第一个字节 WILL 251(FB)     发送方激活选项(接收方同意激活选项) DO
         * 253(FD)     接收方同意（发送方想让接收方激活选项） WONT 252(FC)     接收方不同意 DONT 254(FE)     接受方回应WONT
         */

        @Override
        public void receivedNegotiation(final int negotiation_code, final int option_code) {
            String command = null;
            if (negotiation_code == TelnetNotificationHandler.RECEIVED_DO) {
                command = "DO";
            } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_DONT) {
                command = "DONT";
            } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WILL) {
                command = "WILL";
            } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WONT) {
                command = "WONT";
            }
            logger.debug("Received " + command + " for option code " + TelnetOption.getOption(option_code));
        }
    }
}
