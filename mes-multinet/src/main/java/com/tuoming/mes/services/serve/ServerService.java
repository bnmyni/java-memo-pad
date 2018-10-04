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

package com.tuoming.mes.services.serve;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import com.tuoming.mes.collect.dpp.dao.BaseService;
import com.tuoming.mes.collect.models.Manufacturers;
import com.tuoming.mes.collect.models.ObjectType;
import com.tuoming.mes.collect.models.Server;

/**
 * 服务器对象服务接口，每个ServerServer对象对应一个Server配置，提供针对服务器进行操作的一系列方法
 *
 * @author James Cheung
 * @version 1.0.1
 * @see com.tuoming.mes.collect.models.Server
 */
public interface ServerService extends BaseService<Server, String> {

    /**
     * 当前对象是否可用，
     *
     * @return 如果对应Server对象可用则返回True, 否则返回False
     */
    boolean isEnabled();

    /**
     * 获取当前实例对应的Server对象
     *
     * @return Server对象实例
     * @see com.tuoming.mes.collect.models.Server
     */
    Server getServer();

    /**
     * 设置当前服务器对象对应的终端类型，默认为VT100，也可以设置为VT220，一般这个参数不用单独设置，可以直接通过在数据库的 aos_servers表配置即可
     *
     * @param vtType
     */
    void setVtType(String vtType);

    /**
     * 设置当前服务器的提示符，该方法可以用于在运行过程中改变系统提示符，主要针对华为设备不同命令返回的提示符可能不同的问题
     *
     * @param prompt 提示符内容
     */
    void setPrompt(String prompt);

    /**
     * 这只控制台输出开关，如果需要将服务器输出信息打印到控制台则可以将该值设置为True
     *
     * @param val 开关 True/False
     */
    void setEcho(boolean val);

    /**
     * 测试到服务的连接是否正常
     *
     * @return 能正常连接返回True, 否则返回False
     */
    boolean test();

    /**
     * 登录到当前服务器对象
     *
     * @return 如果登录成功返回True否则返回false
     * @throws IOException
     * @throws InterruptedException
     */
    boolean login() throws Exception;

    /**
     * 使用给定的服务器名称初始化当前服务对象，本方法是调用本服务接口内其他方法之前需要执行的方法
     *
     * @param serverName 对应于当前系统aos_servers表配置内的服务器名
     * @throws IOException
     */
    boolean init(String serverName) throws IOException;

    /**
     * 使用给定的服务器对象初始化当前服务对象，本方法是调用本服务接口内其他方法之前需要执行的方法
     *
     * @param server 对应于当前系统aos_servers表配置内的服务器对象
     * @throws IOException
     */
    boolean init(Server server) throws IOException;

    /**
     * 设置指令超时时间，一般我们执行一个指令后需要等待指令执行结束，如果指令到这里指定的时间还没返回结束标识符，则我们会认为指令已经结束，继续执行下面的指令
     *
     * @param value
     */
    void setTimeout(int value);

    /**
     * 清空当前计数关键字列表
     */
    void clearKeywrod();

    /**
     * 增加一个计数关键字
     *
     * @param keyword 用于计数的关键字，支持 正则表达式
     */
    void addKeywordToCount(String keyword);

    /**
     * 获取指定计数关键字的计数值
     *
     * @param keyword addKeywordToCount 增加的计数关键字
     * @return 符合关键字当前出现的次数
     */
    Integer getKeywordCount(String keyword);

    /**
     * 获取一个关键字/正则表达式的匹配结果列表，注意这里的匹配会存在一定误差
     *
     * @param key 匹配的正则表达式
     * @return 符合条件的字符串列表
     */
    List<String> getKeyword(String key);

    /**
     * 通过发送一系列指令重置服务器连接到初始状态，执行如退出一些子操作环境的操作
     */
    void reset();

    /**
     * 完成操作后调用本方法结束到服务器的操作，断开到服务器的连接
     */
    void logout();

    /**
     * 向服务器发送指令，系统会自动在指令最后增加回车符
     *
     * @param cmd 指令内容
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    void sendCommand(String cmd) throws FileNotFoundException, InterruptedException;

    /**
     * 想服务器发送命令，本方法不会自动增加回车符
     *
     * @param cmd 要发送到指令内容
     */
    void write(String cmd);

    /**
     * 向服务器发送一个字符，可以通过这个命令发送单个控制字符
     *
     * @param ch
     */
    void write(int ch);

    /**
     * 获取服务器返回最后信息字符串，用于界面显示或执行结果反馈
     */
    String getResponse();

    /**
     * 执行重连操作，首先断开连接然后再重新执行连接、登录的操作过程，注意这个操作需要耗费服务器资源和时间，不是必要情况最好不使用这个方法
     *
     * @throws Exception
     */
    void reconnect() throws Exception;

    /**
     * 设置日志输出文件，与服务器的交互过程将记录到这个文件内
     *
     * @param filePath 日志文件保存路径
     * @throws Exception
     */
    void setOutputFile(String filePath) throws Exception;

    /**
     * 设置日志输出文件，与服务器的交互过程将记录到这个文件内
     *
     * @param filePath 日志文件保存路径
     * @param append   是否追加方式
     * @throws Exception
     */
    void setOutputFile(String filePath, boolean append) throws Exception;

    /**
     * 获取指定类型的服务器对象列表 ，如果获取华为BSC列表
     *
     * @param objectType    对象类型，这里一般为BSC或OMC
     * @param manufacturers 厂家对象，对应aos_manufacturers表内的对象定义
     * @return 符合条件的服务器列表
     */
    List<Server> getServers(ObjectType objectType, Manufacturers manufacturers);
}
