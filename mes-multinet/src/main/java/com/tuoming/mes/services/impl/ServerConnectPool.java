/*******************************************************************************
 * Copyright (c) 2014.  Pyrlong All rights reserved.
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

package com.tuoming.mes.services.impl;

import java.util.Map;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.datatype.AppContext;
import com.tuoming.mes.services.serve.MESConstants;
import com.tuoming.mes.services.serve.ServerService;

/**
 * Created by James on 12/15/14.
 */
public class ServerConnectPool implements PoolableObjectFactory {

    private static Map<String, GenericObjectPool> serverConnectPoolMap = Maps.newHashMap();
    private final static Logger logger = LogFacade.getLog4j(ServerConnectPool.class);
    private static boolean useServerPool = false;

    static {
        //读取当前是否启用了连接池，默认未启用
        useServerPool = ConfigurationManager.getDefaultConfig().getBoolean(MESConstants.SERVER_POOL_ACTIVATED, false);
    }

    public static ServerService getServerServiceFromPool(String name, String logfile) throws Exception {
        if (useServerPool) {
            ServerService serverService = (ServerService) getPool(name).borrowObject();
            return serverService;
        } else
            return newServiceInstance(name, logfile);
    }

    public static void releaseServer(ServerService service) throws Exception {
        if (useServerPool) {
            getPool(service.getServer().getServerName()).returnObject(service);
            logger.info("Release server " + service.getServer());
        } else {
            logger.info("Disconnect from server " + service.getServer());
            service.logout();
        }
    }

    public static ServerService newServiceInstance(String name, String logfile) throws Exception {
        ServerService serverService = AppContext.getBean(ServerService.class);
        if (StringUtil.isEmpty(logfile))
            logfile =  AppContext.CACHE_ROOT + DateUtil.currentDateString("yyyyMMdd") + "/" + name + "_" + DateUtil.getTimeinteger() + ".log";
        if (!serverService.init(name))
            logger.warn("Server " + name + " init failed!!");
        serverService.setOutputFile(logfile,true);
        serverService.login();
        return serverService;
    }

    public static void releaseAll(String name) {
        getPool(name).clear();
    }

    public static void close(String name) throws Exception {
        getPool(name).close();
        serverConnectPoolMap.remove(name);
    }

    /**
     * 获取指定名称服务器对应的连接池对象
     *
     * @param name 服务器唯一名称
     * @return 对应的连接池对象
     */
    public static GenericObjectPool getPool(String name) {
        if (serverConnectPoolMap.containsKey(name)) {
            return serverConnectPoolMap.get(name);
        } else {
            ServerConnectPool factory = new ServerConnectPool(name);
            GenericObjectPool pool = new GenericObjectPool(factory);
            pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
            //pool.setTestOnBorrow(true);//设置是否在获取对象时候检查有效性
            pool.setMaxActive(ConfigurationManager.getDefaultConfig().getInteger(MESConstants.SERVER_POOL_MAX_ACTIVE, 5)); // 能从池中借出的对象的最大数目
            pool.setMaxIdle(ConfigurationManager.getDefaultConfig().getInteger(MESConstants.SERVER_POOL_MAX_IDLE, 5)); // 池中可以空闲对象的最大数目
            pool.setMaxWait(ConfigurationManager.getDefaultConfig().getInteger(MESConstants.SERVER_POOL_MAX_WAIT, 120000)); // 对象池空时调用borrowObject方法，最多等待多少毫秒
            pool.setTimeBetweenEvictionRunsMillis(600000);// 间隔每过多少毫秒进行一次后台对象清理的行动
            pool.setNumTestsPerEvictionRun(-1);// －1表示清理时检查所有线程
            pool.setMinEvictableIdleTimeMillis(30000);// 设定在进行后台对象清理时，休眠时间超过了30000毫秒的对象为过期
            serverConnectPoolMap.put(name, pool);
            return pool;
        }
    }

    private String serverName;

    public ServerConnectPool(String name) {
        serverName = name;
    }

    @Override
    public Object makeObject() throws Exception {
        logger.info(">>>>>Create new instance for server " + serverName);
        ServerService serverService = newServiceInstance(serverName, null);
        return serverService;
    }

    @Override
    public void destroyObject(Object obj) throws Exception {
        logger.info("<<<<<<<Destroy instance for server " + serverName);
        ((ServerService) obj).logout();
    }

    @Override
    public boolean validateObject(Object obj) {
        return true;
    }

    @Override
    public void activateObject(Object obj) throws Exception {
        logger.info("Use [" + obj.hashCode() + "] for " + serverName);
    }

    @Override
    public void passivateObject(Object obj) throws Exception {
        ((ServerService) obj).reset();
    }

	public static ServerService reconnect(String serverName2, String logFileName, ServerService serverService) throws Exception {
		try {
			releaseServer(serverService);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage(),e);
		}
		return getServerServiceFromPool(serverName2, logFileName);
	}
}
