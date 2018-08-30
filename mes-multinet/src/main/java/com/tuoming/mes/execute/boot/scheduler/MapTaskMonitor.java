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

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tuoming.mes.execute.boot.models.TaskPlan;

/**
 * 基于HashMap实现的系统任务监视器对象
 *
 * @see com.tuoming.mes.execute.boot.scheduler.AbstractTaskMonitor
 */
public class MapTaskMonitor extends AbstractTaskMonitor {
    private Map<String, TaskPlan> taskRunning;//正在执行的任务
    private List<TaskPlan> taskWaitToRun;//等待执行的任务
    private Map<String, Object> taskWaitMap;//等待执行的任务
    private Map<String, Long> taskBeginMap;

    public MapTaskMonitor() {
        taskRunning = new LinkedHashMap<String, TaskPlan>();
        taskWaitMap = new LinkedHashMap<String, Object>();
        taskWaitToRun = new LinkedList<TaskPlan>();
        taskBeginMap = new LinkedHashMap<String, Long>();
    }

    @Override
    public synchronized void remove(TaskPlan task) {
        synchronized (taskRunning) {
            if (taskRunning.containsKey(task.getTaskName()))
                taskRunning.remove(task.getTaskName());
            if(taskBeginMap.containsKey(task.getTaskName())) {
            	taskBeginMap.remove(task.getTaskName());
            }
            logger.info("Task " + task.getTaskName() + " is removed ");
        }
        super.remove(task);
    }

    @Override
    protected int getRunningTaskCount() {
        return taskRunning.size();
    }

    @Override
    protected TaskPlan getTaskToRun() {
        TaskPlan task = null;
        synchronized (taskWaitToRun) {
            if (taskWaitToRun.size() > 0) {
                task = taskWaitToRun.get(0);
                taskRunning.put(task.getTaskName(), task);
                taskWaitToRun.remove(0);
                taskWaitMap.remove(task.getTaskName());
                logger.info("Get task " + task.getTaskName() + " to run.... ");
                logger.info(taskWaitToRun.size() + " tasks wait to run ...");
            }
        }
        return task;
    }

    @Override
    public void addTask(TaskPlan task) {
        synchronized (taskWaitToRun) {
            logger.info("add new task :" + task.getTaskName());
            taskWaitToRun.add(task);
            taskWaitMap.put(task.getTaskName(), null);
        }
    }

    @Override
    protected boolean taskWaitToRun(TaskPlan task) {
        synchronized (taskWaitMap) {
            return taskWaitMap.containsKey(task.getTaskName());
        }
    }

    @Override
    protected boolean taskRunning(TaskPlan task) {
        synchronized (taskRunning) {
        	
            if (taskRunning.containsKey(task.getTaskName())) {
            	if(taskBeginMap.containsKey(task.getTaskName())) {
            		long current = Calendar.getInstance().getTimeInMillis();
            		long before = taskBeginMap.get(task.getTaskName());
            		if(current-before>=task.getTimeout()-1000) {
            			logger.info(task.getTaskName()+" found timeout !");
            			remove(task);
            			return false;
            		}
            	}
                for (Map.Entry<String, TaskPlan> entry : taskRunning.entrySet()) {
                    logger.info(entry.getKey() + " is running...");
                }
                return true;
            }
            return false;
        }
    }

	@Override
	protected void addRunThread(String taskName, Thread run) {
		synchronized (taskRunning) {
			taskBeginMap.put(taskName, Calendar.getInstance().getTimeInMillis());
			logger.info(" save task thread and task time !");
		}
		
	}
}