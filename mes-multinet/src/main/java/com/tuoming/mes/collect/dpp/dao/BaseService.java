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
import java.util.Map;


public interface BaseService<M extends java.io.Serializable, PK extends java.io.Serializable> {

    public Map<String, String> getEnvCopy();

    public void setEnv(Map<String, String> envs);

    public M save(M model);

    public void saveOrUpdate(M model);

    public void update(M model);

    public void merge(M model);

    public void delete(PK id);

    public void deleteObject(M model);

    public M get(PK id);

    public int countAll();

    public List<M> listAll();


}
