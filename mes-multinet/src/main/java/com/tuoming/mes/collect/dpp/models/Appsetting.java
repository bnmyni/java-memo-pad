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

package com.tuoming.mes.collect.dpp.models;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created with IntelliJ IDEA. User: james Date: 6/11/13 Time: 9:30 PM
 */
@Entity
@Table(name = "mes_appsetting")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Appsetting extends AbstractModel implements Comparable {
    @Id
    @Column(name = "name", length = 80, nullable = false)
    public String name;

    @NotEmpty(message = "{object.notempty.illegal}")
    @Lob
    @Basic( fetch = FetchType.LAZY )
    @Column( name = "value", nullable = true )
    public String value;

    @Column(name = "remark", length = 255, nullable = false)
    public String remark;
    @NotEmpty(message = "{object.notempty.illegal}")
    @Column(name = "class_name", length = 32, nullable = false)
    public String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String key) {
        this.name = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Appsetting) {
            return name.compareTo(((Appsetting) o).getName());
        }
        return 0;
    }
}
