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


import java.io.Serializable;
import java.util.List;

import com.tuoming.mes.collect.dpp.datatype.DataTable;

public interface CommonDao {

    public <T> T save(T model);

    public <T> void saveOrUpdate(T model);

    public <T> void update(T model);

    public <T> void merge(T model);

    public <T, PK extends Serializable> void delete(Class<T> entityClass, PK id);

    public <T> void deleteObject(T model);

    public <T, PK extends Serializable> T get(Class<T> entityClass, PK id);

    public <T> int countAll(Class<T> entityClass);

    public <T> List<T> listAll(Class<T> entityClass, String orderby, String filter);

    public DataTable queryTable(String dbName, String sql) throws Exception;

}
