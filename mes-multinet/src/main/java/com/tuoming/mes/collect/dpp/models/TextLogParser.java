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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA. User: james Date: 7/2/13 Time: 3:07 PM 负责处理分割文件方式解析的解析器配置对象
 */
@Entity
@Table(name = "mes_log_parser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TextLogParser extends AbstractModel {
    /**
     * 解析器触发条件 ，只有满足该匹配才能触发解析器进行解析操作
     */
    @Column(name = "regex_trigger", length = 64, nullable = false)
    protected String trigger = "";
    /**
     * 解析器名称 用于唯一标识一个解析器对象
     */
    @Id
    @Column(name = "name", length = 255, nullable = false)
    private String name = "";
    /**
     * 标识一行数据是否为数据行，此属性只在对Split、Formate方式解析有效
     */
    @Column(name = "value_regex", length = 255, nullable = true)
    private String valueLineRegex = null;
    /**
     * 标识一行是否为标题行   ，此属性只在对Split、Formate方式解析有效 应用于formate时该属性可以用逗号分割配置多个
     */
    @Column(name = "header_regex", length = 255, nullable = true)
    private String headerRegex = null;
    /**
     * 分隔符，，此属性只在对Split方式解析有效
     */
    @Column(name = "split_regex", length = 32, nullable = true)
    private String splitRegex = null;
    /**
     * 忽略数据行标识，符合这个匹配条件的数据行将不被处理
     */
    @Column(name = "ignore_regex", length = 255, nullable = true)
    private String ignoreRegex = null;
    /**
     * 本解析器是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = false;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parser_name")
    @OrderBy(value = "orderId ASC")
    private List<TextItemRule> itemRuleList;
    @Column(name = "target_db", length = 64, nullable = true)
    private String targetDb;
    @Column(name = "target_table", length = 250, nullable = true)
    private String targetTable;
    @Column(name = "parse_handle", length = 80, nullable = false)
    private String parseHandle;
    @Column(name = "line_formater", length = 200, nullable = true)
    private String lineFormater;
    @Column(name = "have_unicode", nullable = false)
    private boolean haveUnicode = true;

    public String getTargetDb() {
        return targetDb;
    }

    public void setTargetDb(final String targetDb) {
        this.targetDb = targetDb;
    }

    public String getLineFormater() {
        return lineFormater;
    }

    public void setLineFormater(String lineFormater) {
        this.lineFormater = lineFormater;
    }

    public String getParseHandle() {
        return parseHandle;
    }

    public void setParseHandle(String parseHandle) {
        this.parseHandle = parseHandle;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public List<TextItemRule> getItemRuleList() {
        return itemRuleList;
    }

    public void setItemRuleList(List<TextItemRule> itemRuleList) {
        this.itemRuleList = itemRuleList;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueLineRegex() {
        return valueLineRegex;
    }

    public void setValueLineRegex(String valueLineRegex) {
        this.valueLineRegex = valueLineRegex;
    }

    public String getHeaderRegex() {
        return headerRegex;
    }

    public void setHeaderRegex(String headerRegex) {
        this.headerRegex = headerRegex;
    }

    public String getSplitRegex() {
        return splitRegex;
    }

    public void setSplitRegex(String splitRegex) {
        this.splitRegex = splitRegex;
    }

    public String getIgnoreRegex() {
        return ignoreRegex;
    }

    public void setIgnoreRegex(String ignoreRegex) {
        this.ignoreRegex = ignoreRegex;
    }

    public boolean isHaveUnicode() {
        return haveUnicode;
    }

    public void setHaveUnicode(boolean haveUnicode) {
        this.haveUnicode = haveUnicode;
    }
}
