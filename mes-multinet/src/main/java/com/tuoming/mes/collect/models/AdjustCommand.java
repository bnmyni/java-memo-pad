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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.pyrlong.util.DateUtil;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 业务调整指令存储对象 ,对应aos_adjust_command数据表中存储的内容
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_adjust_command")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AdjustCommand extends AbstractModel {
    /**
     * 记录唯一标识 ，通过系统服务接口保存对象时该值由系统自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    /**
     * 命令生成时间
     */
    @Column(name = "time_stamp", nullable = false)
    private Date timeStamp = DateUtil.currentDate();
    /**
     * 子系统名称，该字段属于预留字段，用于后续标识不同子系统生成的指令信息
     */
    @Column(name = "app_name", length = 64, nullable = false)
    @Index(name = "idx_aos_biz_command")
    private String appName;
    /**
     * 命令分组类别名，用于外部针对不同分组的指令进行灵活调度
     */
    @Column(name = "group_name", length = 164, nullable = false)
    @Index(name = "idx_aos_biz_command")
    private String groupName;
    /**
     * 分割符，配置多个指令时以这个符号分割 ，默认为 分号
     */
    @Column(name = "split_char", length = 8, nullable = true)
    private String splitChar = "#";
    /**
     * 指令内容，支持配置多个指令，指令直接可以按照配置的分割符进行分割
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "command", nullable = false)
    private String command;
    /**
     * 指令添加人/系统,用于标识是哪个人，那类系统增加的命令
     */
    @Column(name = "owner", length = 64, nullable = false)
    private String owner = "Not Set";
    /**
     * 指令影响对象的标识，一般为小区，可以用 lac_ci或bsc_ci形式标识
     */
    @Column(name = "ne_object", length = 100, nullable = true)
    private String neObject;
    /**
     * 命令接收对象类型
     */
    @Column(name = "object_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ObjectType objectType;
    /**
     * 目标对象标识列，以指定命令接收对象
     */
    @Column(name = "target_object", length = 100, nullable = false)
    private String targetObject;

    /**
     * 扩展字段，用于根据需要保存扩展信息
     */
    @Column(name = "extend1", length = 100, nullable = true)
    private String extend1;
    /**
     * 扩展字段，用于根据需要保存扩展信息
     */
    @Column(name = "extend2", length = 100, nullable = true)
    private String extend2;
    /**
     * 扩展字段，用于根据需要保存扩展信息
     */
    @Column(name = "extend3", length = 100, nullable = true)
    private String extend3;
    /**
     * 扩展字段，用于根据需要保存扩展信息
     */
    @Lob
    @Type(type="text")
    @Column(name = "extend4", nullable = true)
    private String extend4;

    @Column(name = "batch_id", nullable = true)
    private Long batchId;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getExtend5() {
        return extend5;
    }

    public void setExtend5(String extend5) {
        this.extend5 = extend5;
    }

    public String getNeObject() {
        return neObject;
    }

    public void setNeObject(String neObject) {
        this.neObject = neObject;
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

    /**
     * 扩展字段，用于根据需要保存扩展信息
     */
    @Column(name = "extend5", length = 100, nullable = true)
    private String extend5;

    /**
     * 指令下发状态，0：未下发，1：已下发  ,2:需要确认后手动下发
     */
    @Column(name = "applied", nullable = true)
    private int applied;
    /**
     * 命令是否成功执行
     */
    @Column(name = "sucessfull", nullable = true)
    private boolean sucessfull = false;
    /**
     * 命令下发时间
     */
    @Column(name = "sent_time", nullable = true)
    private Date sentTime;
    /**
     * 命令下发日志文件路径
     */
    @Column(name = "cmd_log", length = 300, nullable = true)
    private String cmdLog;
    /**
     * 指令排序标识，当需要在生成指令后调整执行顺序时可以通过这个字段调整
     */
    @Column(name = "order_id", nullable = true)
    private int orderId;
    /**
     * 指令对应说明文字
     */
    @Column(name = "remark", nullable = true)
    private String remark;

    public int getApplied() {
        return applied;
    }

    public void setApplied(int applied) {
        this.applied = applied;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSplitChar() {
        return splitChar;
    }

    public void setSplitChar(String splitChar) {
        this.splitChar = splitChar;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public int isApplied() {
        return applied;
    }

    public boolean isSucessfull() {
        return sucessfull;
    }

    public void setSucessfull(boolean sucessfull) {
        this.sucessfull = sucessfull;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public String getCmdLog() {
        return cmdLog;
    }

    public void setCmdLog(String cmdLog) {
        this.cmdLog = cmdLog;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
