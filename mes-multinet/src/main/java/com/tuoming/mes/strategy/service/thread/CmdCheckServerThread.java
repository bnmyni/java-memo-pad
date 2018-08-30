package com.tuoming.mes.strategy.service.thread;

import com.tuoming.mes.collect.models.Server;
import com.tuoming.mes.services.impl.ServerConnectPool;
import com.tuoming.mes.services.serve.ServerService;

public class CmdCheckServerThread implements Runnable {
	private Server server;

	public CmdCheckServerThread(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		try {
			ServerService serverService = ServerConnectPool.getServerServiceFromPool(server.getServerName(), null);
			if (!serverService.isEnabled()) {
				ServerConnectPool.releaseServer(serverService);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
