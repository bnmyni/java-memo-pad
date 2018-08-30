package com.tuoming.mes.strategy.service.impl;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.net.ftp.FTPClientExt;
import com.tuoming.mes.collect.dao.FtpServerDao;
import com.tuoming.mes.collect.dao.ServerDao;
import com.tuoming.mes.collect.models.FtpServer;
import com.tuoming.mes.collect.models.Server;
import com.tuoming.mes.services.impl.FtpLogCommandServiceImpl;
import com.tuoming.mes.services.impl.ServerConnectPool;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.services.serve.ServerService;
import com.tuoming.mes.strategy.service.CheckServerAccessService;
import com.tuoming.mes.strategy.service.thread.CmdCheckServerThread;

@Service("checkServerAccessService")
public class CheckServerAccessServiceImp implements CheckServerAccessService {
	@Autowired
	@Qualifier("FtpServerDao")
	private FtpServerDao ftpServerDao;
	@Autowired
	@Qualifier("ServerDao")
	private ServerDao serverDao;
	
	private final static Logger logger = LogFacade.getLog4j(FtpLogCommandServiceImpl.class);
	

	/**
	 * ftp服务器每15分钟采集一次，不需要进行检查，该方法舍弃
	 */
	public void checkFtpServerAccess() {
		logger.info("ftpServer check start......");
		List<FtpServer> ftpServers = ftpServerDao.queryEnabledFtpServer();
		for(FtpServer server: ftpServers) {
			FTPClientExt ftpClientExt = new FTPClientExt();
			ftpClientExt.setUrl(server.getUrl());
			ftpClientExt.setName(server.getName());
			ftpClientExt.setPort(server.getPort());
			ftpClientExt.setUid(server.getUid());
			ftpClientExt.setPassword(server.getPassword());
			ftpClientExt.setControlEncoding(server.getControlEncoding());
			ftpClientExt.setEnterLocalPassiveMode(server.isEnterLocalPassiveMode());
			ftpClientExt.setConnectTimeout(server.getConnectTimeout());
			if (!ftpClientExt.connect()) {
				server.setStatus(100);
				ftpServerDao.update(server);
				return;
			} else if (server.getStatus() > 0) {
				server.setStatus(0);
				ftpServerDao.update(server);
			}
		}

	}

	public void checkCmdServerAccess() {
		List<Server> serverList = serverDao.getEnabledServers();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_CORE_POOL_SIZE, MESConstants.THREAD_CORE_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_MAX_POOL_SIZE, MESConstants.THREAD_MAX_POOL_SIZE_DEFAULT),
                ConfigurationManager.getDefaultConfig().getInteger(MESConstants.LOG_THREAD_KEEP_ALIVE_TIME_IN_SECOND, MESConstants.THREAD_KEEP_ALIVE_TIME_IN_SECOND_DEFAULT),
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		for(Server server:serverList) {
			threadPoolExecutor.execute(new CmdCheckServerThread(server));
		}
		threadPoolExecutor.shutdown();
		while(threadPoolExecutor.isShutdown() && threadPoolExecutor.getPoolSize() > 0) {
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
