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

import com.pyrlong.util.ConvertBinaryUtil;

/**
 * Created by James on 14/11/12.
 */
public class Numeral extends BytesValue {

    protected Numeral(String name, int position, int length, String description) {
        super(name, position, length, description);
    }

    protected Numeral(int position, int length, String description) {
        super(position, length, description);
    }


    @Override
    public String getValue(byte[] bytes) {
        byte[] newBytes = new byte[bytes.length];
        int j = 0;
        int i;
        for (i = bytes.length; i > 0; i--) {
            newBytes[j++] = bytes[i - 1];
        }
        return ConvertBinaryUtil.bytesToInt(newBytes) + "";
    }
}
