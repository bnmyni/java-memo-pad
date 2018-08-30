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

package com.tuoming.mes.execute.boot.scheduler;

import org.apache.log4j.Logger;

import com.pyrlong.logging.LogFacade;
import com.pyrlong.quartz.Job;
import com.pyrlong.quartz.JobExecutionContext;
import com.pyrlong.quartz.JobExecutionException;
import com.tuoming.mes.execute.boot.AppContext;
import com.tuoming.mes.execute.boot.models.TaskPlan;

/**
 * 实现quartz的job接口的任务执行对象
 *
 * @see com.pyrlong.quartz.Job
 */
public class TaskProcessorJob implements Job {
    private static Logger logger = LogFacade.getLog4j(TaskProcessorJob.class);

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TaskPlan task = (TaskPlan) ctx.getJobDetail().getJobDataMap().get(AppContext.TASK_PLAN_NAME);
        TaskPlan taskPlan = task.buildTask();
        logger.info("Try to start task:" + taskPlan.getTaskName());
        // 如果当前任务设置为单例模式且上次运行还未退出，则直接跳过本次调用
        try {
            AbstractTaskMonitor.getInstance().runTask(taskPlan);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
