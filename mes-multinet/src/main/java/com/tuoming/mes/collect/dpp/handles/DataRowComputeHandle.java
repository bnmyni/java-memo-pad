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

package com.tuoming.mes.collect.dpp.handles;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.pyrlong.collection.NamedObject;
import com.pyrlong.dsl.tools.DSLUtil;
import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.datatype.DataRow;

/**
 * Created by james on 14-5-22.
 */
public class DataRowComputeHandle extends AbstractDataRowHandler {

    List<NamedObject> express;
    DataRowComputeResultHandle handle;


    public DataRowComputeHandle(String expresss, DataRowComputeResultHandle handle) {
        this.express = Lists.newArrayList();
        this.express.add(new NamedObject("NoName", expresss));
        this.handle = handle;
    }

    public DataRowComputeHandle(NamedObject expresss, DataRowComputeResultHandle handle) {
        this.express = Lists.newArrayList();
        this.express.add(expresss);
        this.handle = handle;
    }


    public DataRowComputeHandle(List<NamedObject> expresss, DataRowComputeResultHandle handle) {
        this.express = expresss;
        this.handle = handle;
    }

    @Override
    public void process(String key, DataRow row) {
        try {
            if (row != null) {
                List<NamedObject> results = Lists.newArrayList();
                Map rowMap = row.getItemMap();
                for (NamedObject o : express) {
                    String trigger = o.getTag() + "";
                    boolean isTriggerNull = StringUtil.isEmpty(trigger);
                    if (!isTriggerNull) {
                        //如果配置了触发条件且不满足
                        try {
                            if (!(Boolean) DSLUtil.getDefaultInstance().compute(trigger, rowMap))
                                continue;
                        } catch (Exception ex) {
                            System.out.println("The trigger must return a boolean, \n" + ex.getMessage());
                        }
                    }
                    Object result = DSLUtil.getDefaultInstance().compute(o.getValue().toString(), rowMap);
                    rowMap.put(o.getName(), result);
                    results.add(new NamedObject(o.getName(), result));
                }
                if (handle != null)
                    handle.process(results, rowMap);
                else {
                    System.out.println("handle is null");
                }
            } else {
                System.out.println("Row is null ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void loadTag(Object o) {

    }
}
