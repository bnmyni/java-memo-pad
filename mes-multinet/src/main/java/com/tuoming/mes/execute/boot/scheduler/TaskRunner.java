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

import com.pyrlong.Envirment;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.ProcessHelper;
import com.pyrlong.util.scripts.AbstractEngine;
import com.tuoming.mes.execute.boot.AppContext;
import com.tuoming.mes.execute.boot.Application;
import com.tuoming.mes.execute.boot.models.TaskPlan;


/**
 * @author James Cheung
 */
public class TaskRunner extends Thread {
    private static Logger logger = LogFacade.getLog4j(TaskRunner.class);
    private TaskPlan task;

    public TaskRunner(TaskPlan task) {
        this.task = task;
    }

    public void run() {
        if (task != null) {
            try {
                //首先检查系统状态
                String status = Application.getStatus(task.getTaskName());
                //如果需要刷新系统
                if (status.equals("2")) {
                	logger.info("Restart System!");
                    Envirment.refreshConfig();
                    SchedulerManager.start();//重新启动调度系统
                } else if (status.equals("0")) {
                    logger.info("System stoped!");
                } else {
                    String name = task.getInterpreter();
                    if (task.getInterpreter().equals("dsl")) {
                    	logger.info("dsl mode!");
                        DSLUtil.getDefaultInstance().compute(task.getTaskCmd(), Envirment.getEnvs());
                    } else {
                    	logger.info("current engine "+name);
                        AbstractEngine engine = AbstractEngine.getEngine(name);
                        engine.eval("from pyrlong import *");
                        String code = task.getTaskCmd().trim();
                        logger.info("current code "+code);
                        if (code.endsWith("&")) {
                            code = code.substring(0, code.length() - 1);
                            code = " -w " + task.getTaskGroup().trim() + " -f " + code + " -t " + (task.getTimeout() > 0 ? task.getTimeout() : AppContext.PROCESS_MAX_TIME) + " -n " + task.getTaskName();
//                            code = "startProcess(\"" + task.getTaskGroup() + "_" + task.getTaskName() + "\",\"" + Envirment.getStartupScriptFile() + code + "\",True)";
                            ProcessHelper.startProcess(task.getTaskGroup() + "_" + task.getTaskName(), Envirment.getStartupScriptFile() + code, true);
                        } else if (code.endsWith("!")) {
                            code = code.substring(0, code.length() - 1);
                            String[] args = code.split(" ");
                            if (args.length == 2)
                                code = "evalGroupFileWith(\"" + task.getTaskGroup() + "\",\"" + args[0] + "\",\"" + args[1] + "\")";
                            else
                                code = "evalGroupFile(\"" + task.getTaskGroup() + "\",\"" + args[0] + "\")";
                            engine.eval(code);
                        }
                    }
                }
                AbstractTaskMonitor.getInstance().remove(task);
                logger.info("task " + task.getTaskName() + " done .");
            } catch (Exception e) {
                AbstractTaskMonitor.getInstance().remove(task);
                logger.error(e.getMessage(), e);
            }
        }
    }
}
