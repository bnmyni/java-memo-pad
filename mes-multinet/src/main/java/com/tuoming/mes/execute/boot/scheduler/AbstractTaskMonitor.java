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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.concurrent.UncaughtTaskExcepitonHandler;
import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.execute.boot.AppContext;
import com.tuoming.mes.execute.boot.models.TaskPlan;

/**
 * 任务调度监视器的抽象实现
 */
public abstract class AbstractTaskMonitor extends Thread {

    protected static Logger logger = LogFacade.getLog4j(AbstractTaskMonitor.class);
    static AbstractTaskMonitor TaskMonitorInstance;
    private static Executor executor = Executors.newCachedThreadPool(new CustomThreadFactory("com.pyrlong.hamster.task_monitor"));//(DPPContext.MAX_TASK_THREAD_COUNT);
    private boolean interrupted = false;

    /**
     * 获取一个当前环境内实现的监控器实例
     *
     * @return AbstractTaskMonitor类型的一个任务处理实例
     */
    public static AbstractTaskMonitor getInstance() {
        if (TaskMonitorInstance == null) {
            TaskMonitorInstance = new MapTaskMonitor();
            TaskMonitorInstance.setName("com.pyrlong.hamster.task_watcher");
        }
        return TaskMonitorInstance;
    }

    /**
     * 抽象方法，获取当前运行的任务列表
     *
     * @return 返回当前运行任务的计数
     */
    protected abstract int getRunningTaskCount();

    /**
     * 从任务队列获取一个任务执行，如果当前队列内没有需运行的任务，则返回null
     *
     * @return 获取一个需要运行的TaskPlan对象
     * @see com.tuoming.mes.execute.boot.models.TaskPlan
     */
    protected abstract TaskPlan getTaskToRun();

    /**
     * 将要执行的线程记录下来
     */
    protected abstract void addRunThread(String taskName, Thread run);

    /**
     * 循环检查当前任务执行状态，检查逻辑为：检查当前处于运行状态的任务数目，如果数目小于系统允许
     * 运行的数目则从任务队列中取出待执行任务开始执行。否则继续等待!
     *
     * @see com.tuoming.mes.execute.boot.scheduler.MapTaskMonitor#run()
     */
    @Override
    public void run() {
        logger.info("Start task monitor thread...");
        while (!interrupted) {
            /**
             * 当前存在任务调度后无法正常结束的情况,这里增加任务超时判断机制 *
             */
            try {
                // 此处每次迭代只启动一个新实例
                if (getRunningTaskCount() < AppContext.MAX_TASK_THREAD_COUNT) {
                    TaskPlan task = getTaskToRun();
                    if (task != null) {
                        logger.info(task.getTaskName() + " is not null ");
                        Thread command = new TaskRunner(task);
                        addRunThread(task.getTaskName(), command);
                        command.setName("task-instance-" + task.getTaskName());
                        command.setDaemon(true);
                        command.setUncaughtExceptionHandler(new UncaughtTaskExcepitonHandler(task.getTaskName()));
//                        executor.execute(command);
                        command.setPriority(MAX_PRIORITY);
                        command.start();
                        logger.info(task.getTaskName() + " thread execute");
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 执行指定的任务
     *
     * @see com.tuoming.mes.execute.boot.scheduler.MapTaskMonitor#runTask(com.tuoming.mes.execute.boot.models.TaskPlan)
     */
    public void runTask(TaskPlan task) throws Exception {
        if (task == null || !task.getEnabled())
            return;
        //如果当前任务设置为单例模式且上次运行还未退出，则直接跳过本次调用
        if (taskRunning(task) || (task.getSingleton() && taskWaitToRun(task))) {
            logger.info(task.getTaskName() + "  is singleton , skiped>>>>>>>>>>>>>>>>>...");
            return;
        }
        task.setState(0);//
        //将任务添加到等待执行的任务计划中
        addTask(task);
    }

    /**
     * 从系统记录中移除完成的任务
     *
     * @param task 已经运行完成的任务对象
     */
    public synchronized void remove(TaskPlan task) {

    }

    /**
     * 添加一个新的任务对象到执行队列
     *
     * @param task 需要加入执行队列的任务对象
     */
    public abstract void addTask(TaskPlan task);

    /**
     * 返回任务的状态，标识一个任务十分处于运行状态
     *
     * @param task 要检查的任务对象
     * @return True 任务正在运行
     */
    protected abstract boolean taskRunning(TaskPlan task);

    @Override
    public void interrupt() {
        interrupted = true;
        super.interrupt();
    }

    /**
     * 判断任务是否处于等待队列内
     *
     * @param task 要判断任务对象
     * @return
     */
    protected abstract boolean taskWaitToRun(TaskPlan task);

}
