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

package com.pyrlong.net;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.pyrlong.Constants;
import com.pyrlong.concurrent.CustomThreadFactory;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.exception.PyrlongException;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.CharacterSetToolkit;
import com.pyrlong.util.DateUtil;
import com.pyrlong.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by james on 14-5-16.
 */
public abstract class AbstractCommandClient extends OutputStream implements CommandClient {

    private static Logger logger = LogFacade.getLog4j(AbstractCommandClient.class);
    private static CustomThreadFactory threadFactory = new CustomThreadFactory(Constants.REMOTE_COMMAND_THREAD_NAME);//(DPPContext.MaxTaskThreadCount);
    private int byteBufferSize = ConfigurationManager.getDefaultConfig().getInteger(Constants.REMOTE_READ_BYTE_BUFFER_SIZE, 52428); //榛樿鏁版嵁鎺ユ敹缂撳啿鍖哄ぇ灏�
    protected RemoteServer remoteServer;
    private Boolean waitBlocked = false;
    private boolean connected = false;
    private boolean commandTimeout = false;
    private long byteReceived = 0L;
    private Writer outPutFileWriter;
    private OutputStream outFile;
    private boolean commandDone = false;
    private Map<String, Integer> keyWordCountMap;
    private Map<String, List<String>> keyWordValues;
    private List<String> keywords = new ArrayList<String>();
    private EventBus eventBus;
    private String lastCommand = "";
    ByteBuffer lastReceived = ByteBuffer.allocate(byteBufferSize);
    private String lastResponse = "";
    private StringBuilder sbResponse = new StringBuilder();
    int lastPromptFoundCount = 0;
    AtomicInteger currentPattern = new AtomicInteger(0);
    AtomicInteger currentLineNum = new AtomicInteger(-1);
    AtomicInteger lastResponseLineNum = new AtomicInteger(-1);
    private boolean interruptFlag = false;
    private int remoteWaitInterval = ConfigurationManager.getDefaultConfig().getInteger(Constants.REMOTE_WAIT_INTERVAL, 100);

    public AbstractCommandClient(RemoteServer remoteServer, EventBus eventBus) {
        this.remoteServer = remoteServer;
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Override
    public boolean isCommandTimeout() {
        return commandTimeout;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean state) {
        if (connected != state) {
            connected = state;
            connectedStateChanged(state);
            //eventBus.post(new Boolean(state));
        }
    }

    @Subscribe
    public void connectedStateChanged(Boolean state) {
        if (state) {
            startReceive();
        } else {
            try {
                if (this.outFile != null) {
                    this.outFile.close();
                    this.outPutFileWriter.close();
                    outFile = null;
                    outPutFileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearKeyWord() {
        keywords.clear();
        keyWordCountMap = new HashMap<String, Integer>();
    }

    @Override
    public void addKeywordToCount(String keyword) {
        if (keywords.contains(keyword))
            return;
        keywords.add(keyword);
    }

    @Override
    public Integer getKeywordCount(String keyword) {
        int count = 0;
        //濡傛灉娌℃湁鍙戠幇鍏抽敭瀛�鍒欏湪鏁翠釜鎸囦护杩斿洖淇℃伅閲岀湅鏄惁鏈夊叧閿瓧鍙戠幇
        if (keyWordCountMap != null && keyWordCountMap.containsKey(keyword)) {
            count = keyWordCountMap.get(keyword);
            if (count == 0)
                count = StringUtil.matchCount(getResponse(), keyword);
        }
        return count;
    }

    @Override
    public List<String> getKeyword(String key) {
        List<String> founded;
        if (keyWordValues.containsKey(key)) {
            founded = keyWordValues.get(key);
            if (founded.size() == 0) {
                founded = StringUtil.getMatchStrings(getResponse(), key);
            }
            return founded;
        }
        return new ArrayList<String>();
    }

    @Subscribe
    public synchronized void checkResponse(String responseLine) throws InterruptedException {
        checkInterrupt(responseLine);
        if (sbResponse.length() >= 2048000) {
            sbResponse.delete(0, 102400);
        }
        if (responseLine == null || currentLineNum.intValue() <= lastResponseLineNum.intValue())
            return;
        if (currentLineNum.get() > 0)
            lastResponseLineNum.set(currentLineNum.intValue());
        String line = responseLine.trim();
        if (StringUtil.isEmpty(line))
            return;
        lastResponse = responseLine;
        //妫�煡鎸囦护鏄惁缁撴潫
        boolean founded = false;
        List<String> plist = remoteServer.getPromptList();
        for (int i = currentPattern.intValue(); i < plist.size(); i++) {
            String curPpttern = plist.get(i);
            if (StringUtil.isEmpty(curPpttern)) {
                Thread.sleep(50);
                continue;
            }
            if (StringUtil.isMatch(line, curPpttern) || StringUtil.isMatch(lastResponse, curPpttern)) {
                currentPattern.set(i + 1);
                logger.info(String.format(" {%s}-{%s} found {%s}", remoteServer, lastCommand.trim(), curPpttern));
            } else {
                break;
            }
            if (currentPattern.intValue() >= plist.size()) {
                founded = true;
                break;
            }
        }
        commandDone = founded || commandDone;
        //杩欓噷妫�煡閰嶇疆鐨勬渶鍚庝竴涓粨鏉熸爣璇嗭紝濡傛灉澶氭妫�煡閮藉彂鐜版渶鍚庣殑缁撴潫鏍囪瘑锛屽垯璁や负鎸囦护宸茬粡缁撴潫
        if (!commandDone && plist.size() > 0) {
            if (StringUtil.isMatch(line, plist.get(plist.size() - 1)) || StringUtil.isMatch(lastResponse, plist.get(plist.size() - 1))) {
                lastPromptFoundCount++;
            }
        }
        if (lastPromptFoundCount >= 20) {
            commandDone = true;
        }
        if (commandDone)
            lastPromptFoundCount = 0;
        //鏇存柊绯荤粺鍏抽敭瀛楃粺璁�
        updateKeyWordCount(lastResponse);
    }

    private void checkInterrupt(String line) throws InterruptedException {
//    	logger.info("=============start===================");
//    	logger.info("line=="+line);
//    	logger.info("=============end===================");
        //涓柇鏍囪瘑妫�煡
        if (remoteServer.getInterruptCheck()) {
            if (StringUtil.isMatch(line, remoteServer.getInterruptPrompt())) {
            	interruptFlag = true;
                StringBuffer sb = new StringBuffer();
                sb.append(remoteServer.getName());
                sb.append(" Interrupt flag founded : ");
                sb.append(remoteServer.getInterruptPrompt());
                sb.append(" ,Close the connection to the server ");
                sb.append(remoteServer.getIp());
                sb.append(":");
                sb.append(remoteServer.getPort());
                sb.append(" Please check the log files for more information about the exception!");
                logger.warn(sb.toString());
                close();
                Thread.currentThread().sleep(3000);//绛�绉掞紝灏介噺璇诲彇涓�簺杩涗竴姝ョ殑鎻愮ず淇℃伅
                throw new InterruptFoundException(" Connect to server " + remoteServer + " has been closed, interrupt founded!");
            }
        }
    }

    private void updateKeyWordCount(String temp) {
        if (StringUtil.isEmpty(temp))
            return;
        for (String keyword : keywords) {
            if (StringUtil.isEmpty(keyword)) continue;
            int count = StringUtil.matchCount(temp, keyword);
            if (count > 0) {
                if (keyWordCountMap.containsKey(keyword)) {
                    count += keyWordCountMap.get(keyword);
                    keyWordCountMap.remove(keyword);
                }
                keyWordCountMap.put(keyword, count);
            }
            List<String> matches = StringUtil.getMatchStrings(temp, keyword);
            if (keyWordValues == null) keyWordValues = new HashMap<String, List<String>>();
            if (keyWordValues.containsKey(keyword) && keyWordValues.get(keyword) != null)
                keyWordValues.get(keyword).addAll(matches);
            else
                keyWordValues.put(keyword, matches);
        }
    }

    /**
     * 澶勭悊杈撳叆鎸囦护涓殑杞箟瀛楃
     *
     * @param cmd
     * @return
     */
    private String formatCommand(String cmd) {
        return cmd.replace("\\n", "\n").replace("\\r", "\r");
    }

    @Override
    public void waitfor(String prompt) throws InterruptedException {
        if (StringUtil.isNotEmpty(prompt)) {
            remoteServer.setPrompt(prompt);
            waitfor();
        }
    }

    @Override
    public void waitfor() throws InterruptedException {
        Long start = DateUtil.getTimeinteger();
        commandTimeout = false;
        while (!commandDone) {
        	if(interruptFlag) {
        		interruptFlag = false;
        		logger.warn(String.format("%s-%s 响应内容被打断", remoteServer,lastCommand));
        		throw new InterruptFoundException(String.format("%s-%s 响应内容被打断", remoteServer,lastCommand));
        	}
            try {
            	if(!connected) {
            		break;
            	}
                if (DateUtil.getTimeinteger() - start > remoteServer.getCommandTimeout()) {
                    commandTimeout = true;
                    logger.warn(String.format("%s - [%s]  wait [%s] over %s ms, last response is \n %s", remoteServer, lastCommand.trim(), remoteServer.getPrompt(), remoteServer.getCommandTimeout(), lastResponse));
                    break;
                }
                long lastNum = byteReceived;
                Thread.sleep(remoteWaitInterval);
                //濡傛灉澶勪簬绛夊緟鐘舵�锛屽苟涓旇鍙锋病鏈夋洿鏂板垯璁や负璇诲彇鍒颁簡闃诲琛�
                if (waitBlocked && lastNum == byteReceived) {
                	synchronized (lastReceived) {
                		String line = CharacterSetToolkit.byteToString(lastReceived, remoteServer.getEncoding());
                		checkResponse(line);
                		lastReceived.flip();
                		byte[] content = new byte[lastReceived.limit()];
                		lastReceived.get(content);
                		lastReceived.clear();
                		lastReceived.put(content);
                		//checkResponse(getResponse());
                		if (!commandDone)
                			lastResponseLineNum.set(currentLineNum.intValue() - 1);
					}
                }
            } catch (UnsupportedEncodingException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        currentPattern.set(0);
    }


    @Override
    public void setOutput(OutputStream out) throws UnsupportedEncodingException {
        if (outFile != null)
            synchronized (outFile) {
                try {
                    if (this.outPutFileWriter != null) {
                        this.outPutFileWriter.flush();
                        this.outFile.flush();
                        this.outPutFileWriter.close();
                        this.outFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.outFile = out;
            }
        else {
            this.outFile = out;
        }
        if (StringUtil.isNotEmpty(remoteServer.getEncoding()))
            this.outPutFileWriter = new OutputStreamWriter(outFile, remoteServer.getEncoding());
        else {
            this.outPutFileWriter = new OutputStreamWriter(outFile);
        }
    }

    /**
     * 鍚姩鎺ユ敹杩斿洖鏁版嵁鐨勭嚎绋�
     */
    protected void startReceive() {
        Thread t = threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentLineNum.set(1);
                    do {
                        if (!isConnected())
                            break;
                        waitBlocked = true;
                        int code = read();
                        byteReceived++;
                        waitBlocked = false;
                        if (remoteServer.isControl(code)) {
                            if (code == 8) {//濡傛灉璇诲埌閫�牸绗﹀彿鍒欑紦鍐蹭綅缃�涓�綅
                                synchronized (lastReceived) {
                                    if (lastReceived.position() > 0)
                                        lastReceived.position(lastReceived.position() - 1);
                                }
                            }
                            continue;
                        }
                        byte b = (byte) code;
                        if (code != -1) {
                            if (outFile != null && code != 255)
                                outFile.write(b);
                            if (remoteServer.isEchoOn())
                                System.out.write(b);
                        }
                        //璇诲埌鎹㈣绗﹀彿鏃跺啓鏂囦欢锛岃繖閲屾病鏈夎�铏慚ac绯荤粺鐨勬儏鍐�
                        //涓轰簡閬垮厤鍑虹幇瀛楃琚埅鏂殑鎯呭喌
                        synchronized (lastReceived) {
                            if (lastReceived.hasRemaining() && code != 255)
                                lastReceived.put(b);
                            if (code == 13 || code == 255 || !lastReceived.hasRemaining()) {
                                String line = CharacterSetToolkit.byteToString(lastReceived, remoteServer.getEncoding());
                                if (code == 13)
                                	currentLineNum.set(currentLineNum.intValue() + 1);
                                lastReceived.clear();
                                eventBus.post((line));
                                sbResponse.append(line);
                            }
                        }
                        //濡傛灉璇诲埌闈炴硶瀛楃锛屽垯閫�嚭锛屼釜鍒儏鍐典笅鐧婚檰澶辫触鎴栬�鏈嶅姟鍣ㄥ紓甯哥殑鏃跺�鏈嶅姟鍣ㄤ細涓嶆柇杩斿洖杩欎釜瀛楃锛屾墍浠ラ渶瑕佽繃婊ゆ帀
                        if (code == 255) {
                            logger.warn("255 return by " + remoteServer);
                            close();
                            if (commandDone)
                                break;
                            else {
                                throw new InterruptFoundException("Connect to server " + remoteServer + " has been closed, 255 founded!");
                            }
                        }
                    } while (true);
                } catch (IOException ex) {
                    logger.info("startReceive  :"+ex.getMessage(), ex);
                    connected = false;
                }
                if (connected)
                    close();
                logger.info(remoteServer + " : Data receiving thread has exited," + byteReceived + " bytes received!");
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public RemoteServer getRemoteServer() {
        return remoteServer;
    }

    @Override
    public void write(String cmd, String prompt) throws IOException, InterruptedException {
        String oldPrompt = remoteServer.getPrompt();
        remoteServer.setPrompt(prompt);
        write(cmd);
        waitfor();
        remoteServer.setPrompt(oldPrompt);
    }

    @Override
    public void write(String cmd) throws IOException {
        lastCommand = cmd;
        currentPattern.set(0);
        commandDone = false;
        sbResponse.setLength(0);
        cmd = formatCommand(cmd);
        logger.info("Send command [" + lastCommand.trim() + "] to " + remoteServer);
        if (isConnected()) {
            //蹇冩寚浠ゆ椂娓呯┖缂撳啿鍖� 
            synchronized (lastReceived) {
                lastReceived.clear();
            }
            keyWordCountMap = new HashMap<String, Integer>();
            keyWordValues = new HashMap<String, List<String>>();
            currentLineNum.set(currentLineNum.intValue()+1);
            lastResponseLineNum.set(currentLineNum.intValue() - 1);
            while (!waitBlocked) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            write(cmd.getBytes());
            commit();
        } else {
            throw new PyrlongException("Connection to " + remoteServer.getName() + " already closed!");
        }
    }

    public String getResponse() {
        return sbResponse.toString();
    }

    protected abstract void commit() throws IOException;

    public abstract void write(int b) throws IOException;

    public abstract int read() throws IOException;

    public abstract void close();

}