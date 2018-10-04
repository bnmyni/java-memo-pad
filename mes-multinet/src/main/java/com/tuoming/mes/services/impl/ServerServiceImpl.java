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

package com.tuoming.mes.services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.net.CommandClient;
import com.pyrlong.net.CommandClientFactory;
import com.pyrlong.net.InterruptFoundException;
import com.pyrlong.net.RemoteServer;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dao.OperationLogDao;
import com.tuoming.mes.collect.dao.ServerCommandDao;
import com.tuoming.mes.collect.dao.ServerDao;
import com.tuoming.mes.collect.dpp.dao.BaseDao;
import com.tuoming.mes.collect.dpp.dao.impl.AbstractBaseService;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.collect.models.Server;
import com.tuoming.mes.collect.models.ServerCommand;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.services.serve.ServerService;

/**
 * @see com.tuoming.mes.services.serve.aos.services.ServerService
 * @see com.pyrlong.dpp.service.impl.AbstractBaseService
 */
@Scope("prototype")
@Component("ServerService")
public class ServerServiceImpl extends AbstractBaseService<Server, String> implements ServerService {
    private final static Logger logger = LogFacade.getLog4j(ServerServiceImpl.class);
    CommandClient remoteWrapper;
    private String outputFile;
    private boolean echo = false;
    private String tranencoding = null;
    private String aixencoding = null;
    private String prompt;
    private Server currentServer;
    private RemoteServer remoteServer;
    private ServerDao serverDao;
    private ServerCommandDao serverCommandDao;
    private OperationLogDao operationLogDao;
    private boolean inited = false;
    private boolean logined = false;
    private Map<String, String> envs;


    public ServerServiceImpl() {

    }

    public ServerServiceImpl(String serverName) throws IOException {
        init(serverName);
    }

    public ServerServiceImpl(Server server) throws IOException {
        init(server);
    }

    @Autowired
    @Qualifier("OperationLogDao")
    public void setServerDao(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    @Autowired
    @Qualifier("ServerCommandDao")
    public void setServerCommandDao(ServerCommandDao serverCommandDao) {
        this.serverCommandDao = serverCommandDao;
    }

    @Autowired
    @Qualifier("ServerDao")
    public void setBaseDao(BaseDao<Server, String> neServerDao) {
        this.baseDao = neServerDao;
        this.serverDao = (ServerDao) neServerDao;
    }

    @Override
    public void setVtType(String vtType) {
        this.remoteWrapper.getRemoteServer().setVtType(vtType);
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
        remoteServer.setPrompt(prompt);
    }

    @Override
    public void setEcho(boolean val) {
        echo = val;
        if (remoteWrapper != null) remoteWrapper.getRemoteServer().setEchoOn(val);
    }

    @Override
    public boolean test() {
        try {
            if (currentServer.getProtocol() == null || currentServer.getProtocol().equals("telnet")) {
                CommandClient wrapper = CommandClientFactory.getClient(CommandClientFactory.ClientNames.telnet, remoteServer);
                wrapper.open();
                wrapper.close();
            } else if (currentServer.getProtocol().equals("ssh2")) {
                CommandClient wrapper = CommandClientFactory.getClient(CommandClientFactory.ClientNames.ssh2, remoteServer);
                wrapper.open();
                wrapper.close();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 登录到服务器
     *
     * @return
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Override
    public synchronized boolean login() throws Exception {
        logger.info("logined :" + logined);
        //避免重复登录
        if (logined)
            return true;
        try {
            logined = sendConfigCmd(MESConstants.LOGIN_COMMAND_NAME);
            if (!logined) {
                currentServer.setStatus(101);//登陆失败
            } else {
                currentServer.setStatus(0);//正常
            }
            serverDao.update(currentServer);
            return logined;
        } catch (Exception e) {
            currentServer.setStatus(101);//登陆失败
            serverDao.update(currentServer);
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isEnabled() {
        return logined && inited && currentServer.isEnabled();
    }

    @Override
    public Server getServer() {
        if (currentServer == null)
            currentServer = new Server();
        return currentServer;
    }

    @Override
    public synchronized boolean init(String serverName) throws IOException {

        currentServer = serverDao.get(serverName);
        return init(currentServer);
    }

    public void setTimeout(int value) {
        if (remoteWrapper != null)
            remoteServer.setCommandTimeout(value);
    }

    @Override
    public synchronized boolean init(Server server) throws IOException {
        try {
            if (inited)
                return true;
            if (server == null || !server.isEnabled()) {
                logger.warn("Server is null or not enabled :  " + server);
                return inited;
            }
            currentServer = server;
            //*****读取表mes_servers获得服务器信息，实例化远程服务器配置
            remoteServer = server.getRemoteServerInstace();
            setPrompt(server.getPrompt());
            envs = getEnvCopy();
            updateEnv(server, envs);
            if (currentServer.getProtocol() == null || currentServer.getProtocol().equals("telnet")) {
                remoteWrapper = CommandClientFactory.getClient(CommandClientFactory.ClientNames.telnet, remoteServer);
                remoteWrapper.open();
            } else if (currentServer.getProtocol().equals("ssh2")) {
                remoteWrapper = CommandClientFactory.getClient(CommandClientFactory.ClientNames.ssh2, remoteServer);
                remoteWrapper.open();
            }
            inited = true;
        } catch (Exception ex) {
            server.setStatus(100);//连接失败
            serverDao.update(server);
            logger.fatal(server + " : " + ex.getMessage(), ex);
            logout();
            inited = false;
        }
        return inited;
    }

    @Override
    public void clearKeywrod() {
        remoteWrapper.clearKeyWord();
    }

    @Override
    public void addKeywordToCount(String keyword) {
        remoteWrapper.addKeywordToCount(keyword);
    }

    @Override
    public Integer getKeywordCount(String keyword) {
        return remoteWrapper.getKeywordCount(keyword);
    }

    @Override
    public List<String> getKeyword(String key) {
        return remoteWrapper.getKeyword(key);
    }

    @Override
    public void reset() {
        try {
            sendConfigCmd("reset");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public synchronized boolean sendConfigCmd(String action) throws IOException, InterruptedException {
        List<ServerCommand> cmds = serverCommandDao.getNamedCommands(currentServer.getServerGroup(), action);
        logger.info("server command group " + currentServer.getServerGroup() + " ,action :" + action + ",login cmd " + cmds.size());
        if (cmds != null) {
            for (ServerCommand c : cmds) {
                if (c != null) {
                    LogFacade.info("Start Send cmd: " + c.getCommand() + " and prompt: " + c.getPrompt());
                    remoteWrapper.waitfor(c.getPrompt());
                    remoteWrapper.write(DSLUtil.getDefaultInstance().buildString(c.getCommand(), envs) + "\r\n");
                    LogFacade.info("End Send : " + c.getCommand());
                }
            }
            if (cmds.size() > 0 && StringUtil.isNotEmpty(currentServer.getPrompt())) {
                remoteWrapper.waitfor(currentServer.getPrompt());
            } else
                Thread.sleep(1000);
            //如果登陆过程没有超时
            if (!remoteWrapper.isCommandTimeout())
                return true;
        }
        return false;
    }

    /**
     * 退出服务器登录
     */
    @Override
    public void logout() {
        try {
            inited = false;
            sendConfigCmd(MESConstants.LOGOUT_COMMAND_NAME);
            remoteWrapper.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            LogFacade.error(ex.getMessage());
        }
    }

    /**
     * 发送指定命令到服务器并将命令执行结果保存到指定文件内
     *
     * @param cmd
     * @throws java.io.FileNotFoundException
     * @throws InterruptedException
     */
    @Override
    public synchronized void sendCommand(String cmd) {
        try {
            remoteWrapper.write(cmd + "\r\n", prompt);
        } catch (InterruptFoundException e) {
            throw e;
        } catch (Exception ex) {
            //遇到异常则尝试重连再发送
            try {
                logger.info("reconnect to " + this.getServer());
                reconnect();
                remoteWrapper.write(cmd + "\r\n", prompt);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 提供一种外部自己处理返回数据的方式
     *
     * @param cmd
     */
    @Override
    public synchronized void write(String cmd) {
        try {
            remoteWrapper.write(cmd);
        } catch (IOException e) {
            logger.error(currentServer + "---" + e.getMessage(), e);
        }
    }

    /**
     * 发送一个ASCII码到服务器
     *
     * @param ch
     */
    @Override
    public void write(int ch) {
        try {
            remoteWrapper.write(ch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getResponse() {
        return remoteWrapper.getResponse();
    }

    @Override
    public synchronized void reconnect() throws Exception {

        logout();
        remoteWrapper.open();
        logger.info("remoteWrapper open success ");
        Thread.sleep(ConfigurationManager.getDefaultConfig().getInteger(MESConstants.SERVER_SLEEP_BEFORE_RECONNECT_IN_MS, 2000));
        setOutputFile(outputFile, true);
        logger.info("setOutputFile success ");
        login();
        logger.info("login success ");
    }

    @Override
    public void setOutputFile(String filePath) throws Exception {
        setOutputFile(filePath, false);
    }

    @Override
    public void setOutputFile(String filePath, boolean append) throws Exception {
        this.outputFile = filePath;
        FileOper.checkAndCreateForder(filePath);
        if (remoteWrapper == null) {
            throw new Exception("RemoteWrapper must be initialized before use!");
        } else {
            remoteWrapper.setOutput(new FileOutputStream(filePath, append));
        }
    }

    @Override
    public List<Server> getServers(ObjectType objectType, Manufacturers manufacturers) {
        return this.serverDao.getNeServers(objectType, manufacturers);
    }
}
