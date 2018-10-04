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

import com.pyrlong.collection.CollectionsBase;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.quartz.CronTrigger;
import com.pyrlong.quartz.JobDetail;
import com.pyrlong.quartz.Scheduler;
import com.pyrlong.quartz.SchedulerException;
import com.pyrlong.quartz.impl.StdSchedulerFactory;
import com.tuoming.mes.execute.boot.AppContext;
import com.tuoming.mes.execute.boot.Application;
import com.tuoming.mes.execute.boot.models.TaskPlan;

import static com.pyrlong.quartz.CronScheduleBuilder.cronSchedule;
import static com.pyrlong.quartz.JobBuilder.newJob;
import static com.pyrlong.quartz.TriggerBuilder.newTrigger;

/**
 */
public class SchedulerManager {

    private static Logger logger = LogFacade.getLog4j(SchedulerManager.class);
    private static Scheduler sched = null;
    private static AbstractTaskMonitor monitor = AbstractTaskMonitor.getInstance();

    private SchedulerManager() {

    }

    /**
     */
    public static void start() {
        shutdown();
        scheduleTaskPlans();
        Application.setStatus("1");//设置状态为已启动
    }


    private static void scheduleTaskPlans() {
        String configName = "TaskPlans";
        StdSchedulerFactory factory = new StdSchedulerFactory();
        try {
            CollectionsBase<Object> taskPlans = ConfigurationManager.getDefaultConfig().getAdvanceObjectCollection().get(configName);
            sched = factory.getScheduler();
            sched.start();
            // 循环将配置加入到调度器内
            for (Object o : taskPlans) {
                TaskPlan plan = (TaskPlan) o;

                //将任务
                JobDetail detail = newJob(TaskProcessorJob.class).withIdentity(plan.getTaskName(), plan.getTaskGroup()).build();
                detail.getJobDataMap().put(AppContext.TASK_PLAN_NAME, plan);
                CronTrigger trigger = newTrigger().withIdentity(plan.getTaskName(), plan.getTaskGroup()).withSchedule(cronSchedule(plan.getCycle())).build();
                sched.scheduleJob(detail, trigger);
            }
            // 启动任务监听线程
            monitor.start();
        } catch (Exception e) {
            logger.error(e.toString(), e);
            System.exit(-1);
        }
    }

    public static void shutdown() {
        if (sched != null) {
            try {
                sched.shutdown(false);
                sched = null;
                monitor.interrupt();
            } catch (SchedulerException e) {
                logger.error(e.toString(), e);
            }
            Application.setStatus("0");//设置状态为已停止
        }
    }
}
