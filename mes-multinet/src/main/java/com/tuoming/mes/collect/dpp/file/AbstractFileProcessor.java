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

// Created On: 13-8-1 上午9:41
package com.tuoming.mes.collect.dpp.file;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pyrlong.Envirment;
import com.pyrlong.configuration.ConfigurationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.io.FileOper;
import com.tuoming.mes.collect.dpp.datatype.DPPConstants;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public abstract class AbstractFileProcessor implements FileProcessor {

    private static Logger logger = LogFacade.getLog4j(AbstractFileProcessor.class);
    protected Map<String, Map<String, String>> sourceFileList = null;
    protected boolean printHeander = false;
    //解析后CSV文件名
    protected List<String> resultFiles = new ArrayList<String>();
    protected String targetPath;
    protected String csvEncoding;

    public AbstractFileProcessor() {
        printHeander = ConfigurationManager.getDefaultConfig().getBoolean(DPPConstants.FILE_PRINT_HEADER);
        csvEncoding = ConfigurationManager.getDefaultConfig().getString(DPPConstants.CSV_FILE_ENCODING, "utf-8");
    }


    public void setTargetPath(String path) {
        targetPath = FileOper.formatePath(path);
    }

    /**
     * 标识文件是否处理过
     *
     * @param file
     */
    protected void markFileDone(String file) throws IOException {
        String path = file + DPPConstants.FILE_PARSED_EXTENSION;
        File donefile = new File(path);
        donefile.createNewFile();
    }

    protected boolean isFileDone(String file) {
        return FileOper.isFileExist(file + DPPConstants.FILE_PARSED_EXTENSION);
    }

    public void setFiles(List<String> files, Map<String, String> envs) {
        sourceFileList = new HashMap<String, Map<String, String>>();
        for (String file : files) {
            sourceFileList.put(file, envs);
        }
    }

    public void setFiles(List<String> files) {
        setFiles(files, Envirment.getEnvs());
    }

    @Override
    public List<String> getFiles() {
        return resultFiles;
    }

    @Override
    public void setFiles(Map<String, Map<String, String>> files) {
        this.sourceFileList = files;
        //每次更新要解析的文件后都清空列表？
        resultFiles = new ArrayList<String>();
    }
}
