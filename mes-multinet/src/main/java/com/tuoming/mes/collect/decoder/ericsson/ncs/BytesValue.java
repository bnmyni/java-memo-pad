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

package com.tuoming.mes.collect.decoder.ericsson.ncs;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by James on 14/11/12.
 */
public abstract class BytesValue {

    private String name;

    /**
     * 记录起始位置
     */
    private int position;
    /**
     * 数据长度
     */
    private int length;
    /**
     * 记录描述
     */
    private String description;

    private boolean template = false;

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public abstract String getValue(byte[] bytes);

    protected BytesValue(int position, int length, String description) {
        this.position = position;
        this.length = length;
        this.description = description;
    }

    protected BytesValue(String name, int position, int length, String description) {
        this.position = position;
        this.length = length;
        this.description = description;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取当前记录再当前输入流下对应的数值
     *
     * @return 转换后的字符串
     */
    public String getValue(DataInputStream dataInputStream) throws IOException {
        byte[] buffer = new byte[length];
        dataInputStream.read(buffer, 0, length);
        if(this instanceof Identifier) {
        	List<Byte> list = new ArrayList<Byte>();
        	for(byte b:buffer){
        		if(b==0) {
        			continue;
        		}
        		list.add(b);
        	}
        	buffer = new byte[list.size()];
        	for(int i=0;i<list.size();i++){
        		buffer[i]=list.get(i);
        	}
        }
        return getValue(buffer);
    }

    public String getName() {
        return name;
    }

    public List<BytesValue> getBytesValues() {
        return null;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
