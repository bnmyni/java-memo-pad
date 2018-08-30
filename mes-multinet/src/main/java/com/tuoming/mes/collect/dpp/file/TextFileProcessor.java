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

package com.tuoming.mes.collect.dpp.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pyrlong.Envirment;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;

/**
 * 实现对文本文件的统一处理的入口类
 *
 * @author james
 */
public class TextFileProcessor implements Runnable {

    private static Logger logger = LogFacade.getLog4j(TextFileProcessor.class);
    Map<String, Map<String, String>> files = new HashMap<String, Map<String, String>>();
    private List<TextLineHandle> hadles = new ArrayList<TextLineHandle>();
    private BufferedReader stream;
    private TextLineHandle handle;

    public TextFileProcessor() {

    }

    public TextFileProcessor(String file) throws IOException {
        files.clear();
        files.put(file, Envirment.getEnvs());
    }

    public TextFileProcessor(String file, Map<String, String> env) {
        files.clear();
        files.put(file, env);
    }

    public void setFiles(Map<String, Map<String, String>> files) {
        this.files = files;
    }

    public TextFileProcessor(Map<String, Map<String, String>> files) {
        this.files = files;
    }

    public void addHandle(TextLineHandle handle) {
        hadles.add(handle);
    }

    public TextLineHandle GetHandle(TextLine line) {
        for (TextLineHandle hdl : hadles) {
            if (line.isMatch(hdl.getTrigger())) {
                logger.debug(line + " matches " + hdl.getTrigger());
                return hdl;
            }
        }
        return null;
    }

    // / <summary>
    // / 执行对指定文件的处理
    // / </summary>
    public void run() {
        for (Map.Entry<String, Map<String, String>> file : files.entrySet()) {
            try {
                logger.info("Parse file :" + file.getKey());
                if (stream != null)
                    stream.close();
                stream = new BufferedReader(new InputStreamReader(new FileInputStream(file.getKey()), FileOper.getFileEncoding(file.getKey())));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            String nextLine;
            try {
                Map<String, String> envs = file.getValue();
                while ((nextLine = stream.readLine()) != null) {
                    envs.put(DPPConstants.FILE_CURRENT_LINE, nextLine);
                    TextLine textLine = new TextLine(nextLine, this);
                    TextLineHandle handle = GetHandle(textLine);
                    if (handle != null)
                        this.handle = handle;
                    if (this.handle != null)
                        this.handle.processLine(textLine, envs);
                }
                if (this.handle != null)
                    this.handle.done();
            } catch (Exception ex) {
                logger.error("Parse file fail:" + file.getKey());
                logger.error(ex.getMessage(), ex);
            }
        }
        close();
    }

    public TextLine getNextLine() throws IOException {
        String nextLine = stream.readLine();
        return StringUtil.isNotEmpty(nextLine) ? new TextLine(nextLine, this) : null;
    }

    private void close() {
        try {
            if (stream != null)
                stream.close();
            handle.close();
        } catch (Exception e) {
        }
    }
}
