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

package com.tuoming.mes.collect.dpp.dao.impl;

/**
 * Created by James on 13-12-30.
 */
public class PairedObject {
    private Object object1;
    private Object object2;

    public PairedObject(Object ob1, Object ob2) {
        this.object1 = ob1;
        this.object2 = ob2;
    }

    public Object getObject1() {
        return object1;
    }

    public void setObject1(final Object object1) {
        this.object1 = object1;
    }

    public Object getObject2() {
        return object2;
    }

    public void setObject2(final Object object2) {
        this.object2 = object2;
    }
}
