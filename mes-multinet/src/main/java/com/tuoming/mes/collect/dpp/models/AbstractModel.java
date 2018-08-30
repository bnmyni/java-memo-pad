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

package com.tuoming.mes.collect.dpp.models;

import java.lang.reflect.Field;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.collect.Maps;
import com.pyrlong.reflector.ClassReflector;

/**
 * 抽象对象模型，提供模型通用操作方法
 */
public abstract class AbstractModel implements java.io.Serializable {

    private Field[] objFields;

    public Field[] getFields() {
        if (objFields == null) {
            objFields = ClassReflector.getFieldsIncludeParent(getClass());
        }
        return objFields;
    }

    public Object getModelKey() {
        Field[] fields = getFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                return ClassReflector.getFieldValue(this, f.getName());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    /**
     * 对当前对象内标识为需要加密的属性执行加密操作
     */
    public void encryption() {
        Field[] fields = getFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(EncodeField.class)) {
                Object value = ClassReflector.getFieldValue(this, f.getName());
                //将加密后的值赋给属性
                ClassReflector.setFieldValue(this, f.getName(), value);
            }
        }
    }

    public Map objectMap() {
        Map map = Maps.newHashMap();
        Field[] fields = getFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(JoinColumn.class)) {
                f.setAccessible(true);
                try {
                    if (f.isAnnotationPresent(ManyToOne.class)) {
                        map.put(f.getName(), getIdValue(f.get(this)));
                    } else if (!f.isAnnotationPresent(OneToMany.class)) {
                        map.put(f.getName(), f.get(this));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public Object getIdValue(Object o) {
        if (o == null) return null;
        Field[] fields = ((AbstractModel) o).getFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                return ClassReflector.getFieldValue(o, f.getName());
            }
        }
        return null;
    }

    /**
     * 对当前对象内标识为加密的属性执行解密操作
     */
    public void decryption() {
        Field[] fields = getFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(EncodeField.class)) {
                Object value = ClassReflector.getFieldValue(this, f);
                //将解密后的值赋给属性
                ClassReflector.setFieldValue(this, f.getName(), value);
            }
        }
    }
}
