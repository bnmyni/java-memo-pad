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

// Created On: 13-7-31 下午4:41
package com.tuoming.mes.collect.dpp.file;

import java.util.List;
import java.util.Map;

/**
 * 这里描述本类的功能及使用场景
 *
 * @author James Cheung
 * @version 1.0
 * @since 1.0
 */

public interface FileProcessor {
    /**
     * 设置要解析的文件列表
     *
     * @param files
     */
    public void setFiles(Map<String, Map<String, String>> files);

    void setFiles(List<String> files, Map<String, String> envs);

    /**
     * 设置生成文件的本地存储路径
     *
     * @param targetPath
     */
    public void setTargetPath(String targetPath);

    /**
     * 开始解析
     */
    public void run();

    /**
     * 获取解析结果文件列表
     *
     * @return
     */
    public List<String> getFiles();

    public void setFiles(List<String> files);

}
