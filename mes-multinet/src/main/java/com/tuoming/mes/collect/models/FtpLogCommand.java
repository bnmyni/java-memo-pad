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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 配置通过FTP采集数据的任务实体对象
 *
 * @author James Cheung
 * @version 1.0
 */
@Entity
@Table(name = "mes_ftp_command")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FtpLogCommand extends AbstractModel {
    /**
     * 指令名称，英文，用于程序调用 指令, 该值唯一
     */
    @Id
    @Column(name = "name", length = 120, nullable = false)
    private String commandName;

    /**
     * 远程目录
     */
    @Column(name = "remote_path", length = 300, nullable = false)
    private String remotePath;

    /**
     * 本地目录，此处可以配置本地绝对路径或者相对于data目录的路径
     * 支持表达式配置动态路径，可以按日期等信息分目录缓存
     */
    @Column(name = "local_path", length = 300, nullable = false)
    private String localPath;

    /**
     * 下载文件过滤器，用于选择性下载，留空则下载目录下所有文件
     */
    @Column(name = "filter", length = 300, nullable = false)
    private String filter;

    /**
     * 下载文件关联的解析器配置，用于解析下载的文件并入库，如果这个属性留空，
     * 则只下载文件不做其他操作
     */
    @Column(name = "log_parser", length = 100, nullable = false)
    private String logParser;

    /**
     * 下载完成文件后是否删除远程目录下同名文件
     */
    @Column(name = "delete_after_get", nullable = false)
    private Boolean deleteFileAfterGet;

    /**
     * 是否获取子目录文件
     */
    @Column(name = "get_sub_dir", nullable = false)
    private Boolean getSubDir;

    /**
     * 当前采集任务关联的服务器对象
     */
    @ManyToOne
    @JoinColumn(name = "server_name", nullable = false, insertable = true, updatable = true)
    private FtpServer ftpServer;

    /**
     * 是否生效
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    /**
     * 分组名
     */
    @Column(name = "group_name", length = 120, nullable = false)
    private String groupName;

    /**
     * 配置入库文件及目标表对应关系
     */
    @Column(name = "target_table_map", length = 1500, nullable = false)
    private String targetTableMap;

    /**
     * 目标数据库名称，对应数据库配置表内的名称
     */
    @Column(name = "target_db", length = 120, nullable = false)
    private String targetDb;

    /**
     * 迭代器，用于配置需要多次执行的指令，配置时可以通过配置一个返回DataTable的表达式或者返回Map对象的表达式
     */
    @Column(name = "iterator", length = 2000, nullable = true)
    private String iterator;

    /**
     * 解析结果过滤，通过这个配置来控制输出结果包括哪些信息
     */
    @Column(name = "result_filter", length = 2000, nullable = true)
    private String resultFilter;


    public String getResultFilter() {
        return resultFilter;
    }

    public void setResultFilter(String resultFilter) {
        this.resultFilter = resultFilter;
    }

    public String getIterator() {
        return iterator;
    }

    public void setIterator(String iterator) {
        this.iterator = iterator;
    }

    public FtpServer getFtpServer() {
        return ftpServer;
    }

    public void setFtpServer(FtpServer ftpServer) {
        this.ftpServer = ftpServer;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getLogParser() {
        return logParser;
    }

    public void setLogParser(String logParser) {
        this.logParser = logParser;
    }

    public Boolean getDeleteFileAfterGet() {
        return deleteFileAfterGet;
    }

    public void setDeleteFileAfterGet(Boolean deleteFileAfterGet) {
        this.deleteFileAfterGet = deleteFileAfterGet;
    }

    public Boolean getGetSubDir() {
        return getSubDir;
    }

    public void setGetSubDir(Boolean getSubDir) {
        this.getSubDir = getSubDir;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTargetTableMap() {
        return targetTableMap;
    }

    public void setTargetTableMap(String targetTableMap) {
        this.targetTableMap = targetTableMap;
    }

    public String getTargetDb() {
        return targetDb;
    }

    public void setTargetDb(String targetDb) {
        this.targetDb = targetDb;
    }
}
