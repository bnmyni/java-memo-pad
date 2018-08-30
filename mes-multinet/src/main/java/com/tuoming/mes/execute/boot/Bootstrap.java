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
package com.tuoming.mes.execute.boot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.Convert;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.ProcessHelper;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.execute.boot.console.ShellSession;
import com.tuoming.mes.execute.boot.scheduler.SchedulerManager;

/**
 * Hamster的入口类实现 ，调用时需要注意当直接执行脚本文件时 脚本文件是有超时控制的，所以需要首先保证脚本文件内不存 在没有
 * 处理的死循环，然后根据脚本文件的执行时间灵活配置系统超时时间，配置方法为 conf/dpp.properties文件中的
 * <p>
 * pyrlong.hamster.processMaxTime=12000
 * </p>
 * 其配置单位为秒(S)
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.6
 */
public class Bootstrap {
	private static Logger logger;
	private static boolean stoped = false;
	private static boolean serverProcess = false;

	private static String instanceName = "default";

	/**
	 * 系统启动主函数
	 */
	public static void main(String[] args) {
		try {
			Options options = getCliOptions();
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			System.setProperty("groovy.source.encoding", "UTF-8");
			System.setProperty(LogFacade.FILESUFFIX, "bootstrap");

			if (cmd.hasOption("h")) {
				printHelp();
				return;
			}

			boolean withDb = true;

			if (cmd.hasOption("x")) {
				LogFacade.setDebug();
			}

			if (cmd.hasOption("n")) {
				instanceName = cmd.getOptionValue("n");
				System.setProperty(LogFacade.FILESUFFIX, instanceName);
			}
 

			if ("oss".equals(cmd.getOptionValue("m")) || cmd.hasOption("s"))
				withDb = false;

			if (cmd.hasOption("w")) {
				String projectName = cmd.getOptionValue("w");
				System.out.println("SET PROJECT NAME =" + projectName);
				System.setProperty(Constants.PROJECT_NAME, projectName);
			} else {
				System.setProperty(Constants.PROJECT_NAME, "default");
			}

			if (withDb)
				ConfigurationManager.LIFE_CYCLE = ConfigurationManager.LIFECYCLE.WITHDB;
			else
				ConfigurationManager.LIFE_CYCLE = ConfigurationManager.LIFECYCLE.WITHOUTDB;

			logger = LogFacade.getLog4j(Bootstrap.class);
			logger.debug(LogFacade.FILESUFFIX + "=" + System.getProperty(LogFacade.FILESUFFIX));
			AppContext.init(Envirment.DEFAULT_CONFIG_FILE);

			// 检查路径存在才加到里面去
			String projectHome = FileOper.formatePath(Envirment.getHome() + "scripts/" + System.getProperty(Constants.PROJECT_NAME));
			if (FileOper.isFileExist(projectHome))
				Envirment.appendPath(projectHome);
			System.setProperty("com.pyrlong.quartz.properties", Envirment.findFile("quartz.properties"));

			if (cmd.hasOption("m")) {
				if ("stop".equals(cmd.getOptionValue("m"))) {
					stop();
				} else if ("start".equals(cmd.getOptionValue("m"))) {
					// 启动调度
					if (isStarted()) {
						return;
					}
					SchedulerManager.start();
				}
			} else if (cmd.hasOption("f")) {
				String fil = cmd.getOptionValue("f");
				String fargs = "";
				if (cmd.hasOption("a"))
					fargs = cmd.getOptionValue("a");
				FileEvalThread thread = new FileEvalThread(new String[] { fil, fargs });
				thread.setDaemon(true);
				thread.start();
				Long startTime = DateUtil.getTimeinteger();
				Integer timeout = AppContext.PROCESS_MAX_TIME;
				if (cmd.hasOption("t")) {
					timeout = Convert.toInt(cmd.getOptionValue("t"));
				}
				// 循环检查是否执行结束
				while (!thread.isDone()) {
					Long diff = DateUtil.getTimeinteger() - startTime;
					if (diff > timeout && timeout > 0) {
						logger.warn("Process runs has exceeded the maximum time set, quit!");
						break;
					}
					Thread.sleep(1000);
				}
				System.exit(0);
			} else {
				Version.showSplash();
				final ShellSession shellSession = new ShellSession(Envirment.getEnvs());
				shellSession.run(DSLUtil.getDefaultInstance());
			}
			while (!stoped) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Bootstrap error :" + e.getMessage(), e);
		}
	}

	private static boolean isStarted() {
		try {
			String fileName = "lock.pid";
			File flagFile = new File(fileName);
			if (!flagFile.exists())
				flagFile.createNewFile();
			FileChannel channel = new FileOutputStream(fileName).getChannel();
			FileLock lock = channel.tryLock(0, Long.MAX_VALUE, false);
			if (lock == null) {
				logger.warn("Application already started!!");
				return true;
			} else {
				String name = Thread.currentThread().getName();
				ByteBuffer sendBuffer = ByteBuffer.wrap((name + "-" + getCurrentThreadID()).getBytes("UTF-8"));
				channel.write(sendBuffer);
				return false;
			}
		} catch (IOException ex) {
			logger.warn("Application already started!!");
			return true;
		}
	}

	/**
	 * 重新规划系统命令参数格式要求，目前有两种启动方式，如下： 1.功能选项 使用 -m 启动模式名，目前支持
	 * start\stop\server\oss等启动模式 2.执行文件 使用 -f 脚本文件名 [-a 参数列表] [-t 超时时间(秒)]
	 */
	private static Options getCliOptions() {
		Options options = new Options();
		options.addOption("m", true, "启动模式名，目前支持 start\\stop\\server\\oss等启动模式");
		options.addOption("f", true, "执行给定的脚本");
		options.addOption("a", true, "脚本参数列表，可选项,与-f 参数配合使用，，用于指定脚本执行参数");
		options.addOption("t", true, "超时时间，与-f参数配合使用，用于指定脚本执行的最长时间");
		options.addOption("x", false, "启动调试模式，启动此模式后，日志级别会设置为debug级别");
		options.addOption("w", true, "启动工作区标识，用于指定本次启动相对于scripts的默认工程路径");
		options.addOption("i", false, "启动配置界面");
		options.addOption("s", true, "启用客户端模式，连接指定的ip");
		options.addOption("p", true, "启用客户端模式时，要连接的端口");
		options.addOption("n", true, "本次任务名称，用于标识本次启动的特征");
		options.addOption("h", false, "显示命令帮助信息");
		return options;
	}

	private static void printHelp() {
		System.out.println("启动调试模式 ，命令格式为 -x ");
		System.out.println("启动配置界面，命令格式为 -i ");
		System.out.println("指定本次启动实例名称,命令格式为 -n");
		System.out.println("显示帮助信息,命令格式为 -h");
		System.out.println("按照不同模式启动程序，命令格式为: -m start|stop|server|oss");
		System.out.println("执行指定脚本,命令格式为: -f file [-a args...] [-t timeout in mil sec]");
	}

	private static Integer getCurrentThreadID() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName();
		return Integer.parseInt(name.substring(0, name.indexOf("@")));
	}

	/**
	 * 停止调度服务
	 */
	public static void stop() {
		try {
			stoped = true;
			logger.info("Main thread exit..");
			SchedulerManager.shutdown();
			ProcessHelper.shutdown();// 退出已打开进程
		} catch (Exception e) {
			logger.error("ERROR:" + e.getMessage(), e);
		}
		System.exit(0);
	}
}

/**
 * 用于处理执行脚本文件的线程实现类
 */
class FileEvalThread extends Thread {
	String[] args = new String[0];
	boolean done = false;
	private String groupName = "";

	public FileEvalThread(String[] args) {
		this.args = args;
		groupName = System.getProperty(Constants.PROJECT_NAME);
	}

	public boolean isDone() {
		return done;
	}

	public void run() {
		if (args.length > 1) {
			Application.evalFile(groupName, args[0], args[1]);
		} else {
			Application.evalFile(groupName, args[0]);
		}
		done = true;
	}
}
