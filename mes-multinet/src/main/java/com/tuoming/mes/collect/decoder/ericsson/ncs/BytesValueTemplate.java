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

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by James on 14/11/13.
 */
public class BytesValueTemplate extends BytesValue {

    protected BytesValueTemplate(int position, int length, String description) {
        super(position, length, description);
        setTemplate(true);
    }

    List<BytesValue> bytesValues = Lists.newLinkedList();

    public void addByteValue(BytesValue value) {
        bytesValues.add(value);
    }

    public List<BytesValue> getBytesValues() {
        return bytesValues;
    }

    @Override
    public String getValue(byte[] bytes) {
        return null;
    }
}
