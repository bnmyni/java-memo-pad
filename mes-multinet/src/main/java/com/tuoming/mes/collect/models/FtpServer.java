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

// Created On: 13-7-24 上午10:45
package com.tuoming.mes.collect.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_ftp_servers")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FtpServer extends AbstractModel {

	  @Column(name="url", length=300, nullable=false)
	  private String url;
	
	  @Column(name="port", nullable=false)
	  private Integer port = Integer.valueOf(21);
	
	  @Column(name="password", length=30, nullable=false)
	  private String password;
	
	  @Column(name="user_name", length=30, nullable=false)
	  private String uid;
	
	  @Id
	  @Column(name="name", length=64, nullable=false)
	  private String name;
	
	  @Column(name="control_encoding", length=64, nullable=true)
	  private String controlEncoding;
	
	  @Column(name="enabled")
	  private boolean enabled = false;
	
	  @Column(name="status")
	  private int status;
	
	  @Column(name="conn_timeout")
	  private int connectTimeout = 30;
	
	  @Column(name="passive_mode")
	  private boolean enterLocalPassiveMode = false;
	
	  @Column(name="connection_type")
	  private String connectionType;
	
	  @Column(name="data_timeout")
	  private int dataTimeout = 60;
	
	  public int getDataTimeout() {
	    return this.dataTimeout;
	  }
	
	  public void setDataTimeout(int dataTimeout) {
	    this.dataTimeout = dataTimeout;
	  }
	
	  public String getConnectionType() {
	    return this.connectionType;
	  }
	
	  public void setConnectionType(String connectionType) {
	    this.connectionType = connectionType;
	  }
	
	  public boolean isEnterLocalPassiveMode() {
	    return this.enterLocalPassiveMode;
	  }
	
	  public void setEnterLocalPassiveMode(boolean enterLocalPassiveMode) {
	    this.enterLocalPassiveMode = enterLocalPassiveMode;
	  }
	
	  public int getConnectTimeout() {
	    return this.connectTimeout;
	  }
	
	  public void setConnectTimeout(int connectTimeout) {
	    this.connectTimeout = connectTimeout;
	  }
	
	  public int getStatus() {
	    return this.status;
	  }
	
	  public void setStatus(int status) {
	    this.status = status;
	  }
	
	  public boolean isEnabled() {
	    return this.enabled;
	  }
	
	  public void setEnabled(boolean enabled) {
	    this.enabled = enabled;
	  }
	
	  public String getControlEncoding() {
	    return this.controlEncoding;
	  }
	
	  public void setControlEncoding(String controlEncoding) {
	    this.controlEncoding = controlEncoding;
	  }
	
	  public String getUrl() {
	    return this.url;
	  }
	
	  public void setUrl(String url) {
	    this.url = url;
	  }
	
	  public Integer getPort() {
	    return this.port;
	  }
	
	  public void setPort(Integer port) {
	    this.port = port;
	  }
	
	  public String getPassword() {
	    return this.password;
	  }
	
	  public void setPassword(String password) {
	    this.password = password;
	  }
	
	  public String getUid() {
	    return this.uid;
	  }
	
	  public void setUid(String uid) {
	    this.uid = uid;
	  }
	
	  public String getName() {
	    return this.name;
	  }
	
	  public void setName(String name) {
	    this.name = name;
	  }


    @Override
    public String toString() {
        return name;
    }

}
