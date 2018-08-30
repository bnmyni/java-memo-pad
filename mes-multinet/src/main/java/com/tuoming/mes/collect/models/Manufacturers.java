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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 配置设备生产商\版本等信息 对应 aos_manufacturers 数据表配置
 * *
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_manufacturers")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Manufacturers extends AbstractModel {
    /**
     * 配置ID
     */
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;
    /**
     * 厂家名称
     */
    @Column(name = "name", length = 64, nullable = false)
    private String name;
    /**
     * 简称
     */
    @Column(name = "short_name", length = 32, nullable = false)
    private String shortName;
    /**
     * 版本
     */
    @Column(name = "version", length = 32, nullable = false)
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

	public String toString() {
		return name;
	}
    
}
