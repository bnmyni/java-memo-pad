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

package com.tuoming.mes.collect.models;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.pyrlong.net.RemoteServer;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.models.AbstractModel;


/**
 * 服务器对象定义
 */

//@MappedSuperclass
@Entity
@Table(name = "mes_servers")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Server extends AbstractModel {
    /**
     * 服务器名称，该名称必须唯一
     */
    @Id
    @Column(name = "name", length = 128, nullable = false)
    private String serverName;
    /**
     * 服务器IP
     */
    @Column(name = "ip", length = 32, nullable = false)
    private String ip;
    /**
     * 服务器端口，目前该配置只对telnet有效，ssh使用默认22端口
     */
    @Column(name = "port", nullable = false)
    private Integer port;
    /**
     * 服务器默认的命令提示符，该参数可以在运行时修改
     *
     * @see com.tuoming.mes.services.serve.aos.services.ServerService#setPrompt(String)
     */
    @Column(name = "prompt", length = 32, nullable = true)
    private String prompt;

    /**
     * 服务器中断标识，比如登录失败或者连接断开等，遇到此标识时连接退出并抛出异常
     */
    @Column(name = "interrupt_prompt", length = 100, nullable = true)
    private String interruptPrompt;
    /**
     * 登录用户名，针对ssh配置，telnet时本配置无效
     */
    @Column(name = "user_name", length = 64, nullable = true)
    private String username;
    /**
     * 密码，针对ssh配置，telnet时本配置无效
     */
    @Column(name = "password", length = 64, nullable = true)
    private String password;
    @Column(name = "server_group", length = 128, nullable = false)
    private String serverGroup;
    /**
     * 协议类型，目前支持telnet/ssh2
     */
    @Column(name = "protocol", length = 32, nullable = false)
    private String protocol;
    /**
     * 服务器输出数据流编码信息，非中文环境不用设置
     */
    @Column(name = "encoding", length = 32, nullable = true)
    private String encoding = null;
    /**
     * 服务器对象类型
     */
    @Column(name = "object_type", nullable = false)
    @Enumerated(EnumType.STRING)
    /**
     * 对象类型，一般为OMC/BSC
     */
    private ObjectType objectType;
    /**
     * 用于控制本对象的配置是否生效
     */
    @Column(name = "enabled", nullable = true)
    private boolean enabled;
    /**
     * 服务器制造商，对应aos_manufacturers表配置
     */
    @ManyToOne
    @JoinColumn(name = "manufacturers", nullable = false, insertable = true, updatable = true)
    private Manufacturers manufacturers;
    /**
     * 针对本对象需要的，特殊环境变量 当执行涉及本小区的操作时可以使用该设置覆盖系统公共设置 ,该字段配置格式为： a=10;b=1;c=22
     */
    @Column(name = "custom_env", length = 300, nullable = true)
    private String customEnv;
    @Column(name = "extend1", length = 300, nullable = true)
    private String extend1;
    @Column(name = "extend2", length = 300, nullable = true)
    private String extend2;
    @Column(name = "extend3", length = 300, nullable = true)
    private String extend3;
    @Column(name = "extend4", length = 300, nullable = true)
    private String extend4;
    @Column(name = "extend5", length = 300, nullable = true)
    private String extend5;
    @Column(name = "vt_type", length = 20, nullable = true)
    private String vtType = "VT220";
    @Column(name = "echo_on", nullable = true)
    private Boolean echoOn = false;
    /**
     * 对象状态标识，由程序自己维护，0 正常，其他数字分别代表不同的异常情况
     */
    @Column(name = "status", nullable = true)
    private int status;

    public String getInterruptPrompt() {
        return interruptPrompt;
    }

    public void setInterruptPrompt(String interruptPrompt) {
        this.interruptPrompt = interruptPrompt;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }

    public String getExtend5() {
        return extend5;
    }

    public void setExtend5(String extend5) {
        this.extend5 = extend5;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public String getExtend4() {
        return extend4;
    }

    public void setExtend4(String extend4) {
        this.extend4 = extend4;
    }

    public String getVtType() {
        return vtType;
    }

    public void setVtType(String vtType) {
        this.vtType = vtType;
    }

    public Boolean getEchoOn() {
        return echoOn;
    }

    public void setEchoOn(Boolean echoOn) {
        this.echoOn = echoOn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Manufacturers getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(Manufacturers manufacturers) {
        this.manufacturers = manufacturers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getCustomEnv() {
        return customEnv;
    }

    public void setCustomEnv(String customEnv) {
        this.customEnv = customEnv;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String usename) {
        this.username = usename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String name) {
        this.serverName = name;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return getServerName() + "[" + getIp() + ":" + getPort() + "]";
    }


    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public RemoteServer getRemoteServerInstace() {
        RemoteServer server = new RemoteServer();
        server.setEchoOn(echoOn);
        server.setIp(ip);
        server.setName(serverName);
        server.setPort(port);
        server.setPwd(password);
        server.setUid(username);
        server.setPrompt(prompt);
        server.setVtType(vtType);
        if (StringUtil.isNotEmpty(encoding))
            server.setEncoding(encoding);
        server.setInterruptPrompt(interruptPrompt);
        return server;
    }
}
