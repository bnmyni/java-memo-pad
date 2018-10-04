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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 数据库查询采集任务配置对象，对应数据表 aos_db_command
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_db_command")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DbQueryCommand extends AbstractModel {


    /**
     * 配置名称 ，该名称需唯一，且不能超过64个字符
     */
    @Id
    @Column(name = "name", length = 64)
    private String queryName;
    /**
     * 需要在任务执行前执行的操作   ，配置长度不能超过2000
     */
    @Column(name = "pre_action", length = 2000)
    private String preAction;
    /**
     * 需要在任务结束之后执行的操作 ，配置长度不能超过2000
     */
    @Column(name = "after_action", length = 2000)
    private String afterAction;
    /**
     * 要执行的查询语句, 语句支持通过表达式配置，配置长度不能超过4000
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "query_sql", nullable = true)
    private String querySql;
    /**
     * 要查询的数据库名称，对应系统连接配置中的连接名称
     *
     * @see com.pyrlong.dpp.models.ConnectionStringSetting
     */
    @Column(name = "source_db", length = 64, nullable = true)
    private String sourceDbName;
    /**
     * 目标表名
     */
    @Column(name = "target_table", length = 500, nullable = true)
    private String targetTableName;

    @Column(name = "target_db", length = 64, nullable = true)
    private String targetDb;
    /**
     * 说明
     */
    @Column(name = "remark", length = 256)
    private String remark;
    /**
     * 迭代器，用于配置需要多次执行的指令，配置时可以通过配置一个返回DataTable的表达式或者返回Map对象的表达式
     */
    @Column(name = "iterator", length = 2000, nullable = true)
    private String iterator;
    @Column(name = "enabled", nullable = true)
    private boolean enabled = true;
    /**
     * 分组信息，这里支持逗号分割的多个分组，主要用于对各个不同任务灵活调度
     */
    @Column(name = "group_name", length = 120)
    private String groupName;

    public String getTargetDb() {
        return targetDb;
    }

    public void setTargetDb(final String targetDb) {
        this.targetDb = targetDb;
    }

    public String getIterator() {
        return iterator;
    }

    public void setIterator(String iterator) {
        this.iterator = iterator;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPreAction() {
        return preAction;
    }

    public void setPreAction(String preAction) {
        this.preAction = preAction;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getAfterAction() {
        return afterAction;
    }

    public void setAfterAction(String afterAction) {
        this.afterAction = afterAction;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public String getSourceDbName() {
        return sourceDbName;
    }

    public void setSourceDbName(String sourceDbName) {
        this.sourceDbName = sourceDbName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
