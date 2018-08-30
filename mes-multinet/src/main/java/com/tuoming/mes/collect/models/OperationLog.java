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

// Created On: 13-7-26 下午9:58
package com.tuoming.mes.collect.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_oper_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OperationLog extends AbstractModel {

    public OperationLog() {

    }

    public OperationLog(String objName, String opType, String body) {
        this.setObjectName(objName);
        this.setOperContent(body);
        this.setOperType(opType);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    /**
     * 命令生成时间
     */
    @Column(name = "oper_time", nullable = false)
    private Date operTime = DateUtil.currentDate();

    /**
     * 执行操作涉及的对象名
     */
    @Column(name = "object_name", length = 64, nullable = true)
    private String objectName;

    /**
     * 操作类型
     */
    @Column(name = "oper_type", length = 100, nullable = true)
    private String operType;

    public String getOperResult() {
        return operResult;
    }

    public void setOperResult(String operResult) {
        this.operResult = operResult;
    }

    @Column(name = "oper_result", length = 100, nullable = true)
    private String operResult;


    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    @Column(name = "oper_content", length = 1000, nullable = false)
    private String operContent;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append(objectName);
        sb.append("]-[");
        sb.append(operType);
        sb.append("]-[");
        sb.append(operContent);
        sb.append("]");
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getOperContent() {
        return operContent;
    }

    public void setOperContent(String operContent) {
        this.operContent = operContent;
    }
}
