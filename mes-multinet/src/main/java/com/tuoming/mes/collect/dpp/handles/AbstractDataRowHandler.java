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
package com.tuoming.mes.collect.dpp.handles;

import org.apache.log4j.Logger;

import com.pyrlong.logging.LogFacade;
import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataRowHandler;

/**
 * @author James Cheung
 */
public abstract class AbstractDataRowHandler implements DataRowHandler {
    private static Logger logger = LogFacade.getLog4j(AbstractDataRowHandler.class);
    // 用于在调用者与本实例间传递被调用对象
    private Object tag;
    private boolean useRealType = true;

    public abstract void process(String key, DataRow row);

    public abstract void close();

    public abstract void loadTag(Object o);

    public void setUseRealType(boolean itype) {
        useRealType = itype;
    }

    public boolean isUseRealType() {
        return useRealType;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
        loadTag(tag);
    }

}
