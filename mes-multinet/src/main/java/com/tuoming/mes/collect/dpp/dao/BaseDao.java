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

package com.tuoming.mes.collect.dpp.dao;

import java.util.List;

public interface BaseDao<T extends java.io.Serializable, PK extends java.io.Serializable> {

    public PK save(T model);

    public void saveOrUpdate(T model);

    public void saveAll(List<T> modelList);

    public void update(T model);

    public void merge(T model);

    public void delete(PK id);

    public void deleteObject(T model);

    public T get(PK id);

    public int countAll();

    public List<T> listAll();

    public List<T> listAll(int pn, int pageSize);

    public List<T> pre(PK pk, int pn, int pageSize);

    public List<T> next(PK pk, int pn, int pageSize);

    boolean exists(PK id);

    public void flush();

    public void clear();

}
