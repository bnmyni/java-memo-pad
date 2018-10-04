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

package com.tuoming.mes.execute.boot.models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.tuoming.mes.collect.dpp.models.AbstractModel;

/**
 * 系统调度任务的实体对象定义，对应数据库中的aos_task_plan表，该对象通过配置文件加载，如果需要加载数据库配置，需要在入口配置文件中 注册 <p>&lt;section name="db-import"
 * handle="com.pyrlong.dpp.configuration.handler.ConfigurationDbImportHandler"/&gt;</p> 并在配置小节内增加 <p> &lt;db-import/&gt;
 * &lt;add name="TaskPlans" model="com.pyrlong.hamster.models.TaskPlan" group=""//&gt; &lt;/db-import/&gt; <p/> </p>
 */
@Entity
@Table(name = "mes_task_plan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TaskPlan extends AbstractModel {
    /**
     * 描述当前任务任务名，任务名必须唯一
     */
    @Id
    @Column(name = "task_name", length = 120, nullable = false)
    private String taskName;
    /**
     * 任务分组名
     */
    @Column(name = "task_group", length = 120, nullable = false)
    private String taskGroup;
    /**
     * 任务是否启用
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;
    /**
     * 任务执行超时设置，可以针对这个任务单独设置超时时间，如果没有设置则使用全局设置
     */
    @Column(name = "timeout", nullable = false)
    private Long timeout = 0L;
    /**
     * 任务描述信息，该属性为了方便维护时区分每个任务的应用目的
     */
    @Column(name = "remark", length = 250, nullable = false)
    private String remark;
    /**
     * 任务调度逻辑配置，该配置格式为crontab格式 ，Cron表达式由6或7个由空格分隔的时间字段组成, Cron表达式时间字段    格式: [秒] [分] [小时] [日] [月] [周] [年] 常见的配置包括：
     * <p/>
     * "0 0 12 * * ? "	每天12点运行 "0 15 10 ? * *"	每天10:15运行 "0 15 10 * * ?"	每天10:15运行 "0 15 10 * * ? *"	每天10:15运行 "0 15 10
     * * * ? 2008"	在2008年的每天10：15运行 "0 * 14 * * ?"	每天14点到15点之间每分钟运行一次，开始于14:00，结束于14:59。 "0 0/5 14 * *
     * ?"	每天14点到15点每5分钟运行一次，开始于14:00，结束于14:55。 "0 0/5 14,18 * * ?"	每天14点到15点每5分钟运行一次，此外每天18点到19点每5钟也运行一次。 "0 0-5 14 * *
     * ?"	每天14:00点到14:05，每分钟运行一次。 "0 10,44 14 ? 3 WED"	3月每周三的14:10分到14:44，每分钟运行一次。 "0 15 10 ? *
     * MON-FRI"	每周一，二，三，四，五的10:15分运行。 "0 15 10 15 * ?"	每月15日10:15分运行。 "0 15 10 L * ?"	每月最后一天10:15分运行。 "0 15 10 ? *
     * 6L"	每月最后一个星期五10:15分运行。 "0 15 10 ? * 6L 2007-2009"	在2007,2008,2009年每个月的最后一个星期五的10:15分运行。 "0 15 10 ? *
     * 6#3"	每月第三个星期五的10:15分运行。 </P>
     */
    @Column(name = "cycle", length = 30, nullable = false)
    private String cycle;
    /**
     * 任务需要执行的操作描述，这里可以是一个表达式函数 当interpreter=jython时， 调用的函数定义在 pyrlong.py文件内，用户可以根据需要修改该文件进行扩展
     * 其他情况支持的函数通过配置文件中的<DSLConfiguration>小节配置
     */
    @Column(name = "task_cmd", length = 500, nullable = false)
    private String taskCmd;
    /**
     * 任务使用的解析器，目前支持两种: jython,dsl 其中jython支持多线程并发操作，dsl不支持多线程并发操作
     */
    @Column(name = "interpreter", length = 120, nullable = false)
    private String interpreter = "jython";
    /**
     * 标识任务是否单例任务，即 如果一个相同的任务已经位于任务队列或已经运行，该任务是否还会加入到任务队列中
     */
    @Column(name = "singleton", nullable = false)
    private Boolean singleton = false;
    @Transient
    private int state;

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public Boolean getSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public String getTaskCmd() {
        return taskCmd;
    }

    public void setTaskCmd(String taskCmd) {
        this.taskCmd = taskCmd;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    //创建任务计划
    public TaskPlan buildTask() {
        TaskPlan taskPlan = new TaskPlan();
        taskPlan.setCycle(getCycle());
        taskPlan.setEnabled(getEnabled());
        taskPlan.setSingleton(getSingleton());
        taskPlan.setTaskCmd(getTaskCmd());
        taskPlan.setTaskGroup(getTaskGroup());
        taskPlan.setTaskName(getTaskName());
        taskPlan.setTimeout(getTimeout());
        DSLUtil.getDefaultInstance().formatObject(taskPlan, Envirment.getEnvs());
        return taskPlan;
    }

    public String getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(String interpreter) {
        this.interpreter = interpreter;
    }
}
