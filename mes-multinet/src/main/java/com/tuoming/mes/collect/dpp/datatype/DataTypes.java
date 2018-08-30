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

package com.tuoming.mes.collect.dpp.datatype;

import com.pyrlong.util.Convert;
import com.pyrlong.util.DateUtil;

/**
 * Created by james on 3/28/14.
 */
public class DataTypes {
    public static boolean isLong(int typeId) {
        return typeId == 4 || typeId == -5;
    }

    public static boolean isDecimal(int typeId) {
        return typeId == 3 || typeId == 2;
    }

    public static boolean isDouble(int typeId) {
        return typeId == 7 || typeId == 8;
    }

    public static boolean isInteger(int type) {
        return type == 5 || type == -6 || type == 4;
    }

    public static boolean isNum(int typeId) {
        return typeId == 3 || typeId == 4 || typeId == 5 || typeId == 7 || typeId == 8 || typeId == -5 || typeId == -6;
    }

    public static boolean isBoolean(int typeId) {
        return typeId == -7;
    }

    public static boolean isDate(int typeId) {
        return typeId == 91 || typeId == 92 || typeId == 93;
    }

    public static boolean isString(int typeId) {
        return typeId == 1 || typeId == 12 || typeId == -1;
    }

    public static Object convertTo(Object val, int type) throws Exception {
        if (val == null)
            return "";
        if (isBoolean(type))
            return Convert.toBoolean(val);
        if (isLong(type)) return Convert.toLong(val);
        if (isDouble(type)) {
            return Convert.toDouble(val);
        }
        if (isInteger(type)) return Convert.toInt(val);
        if (isDate(type)) {
            if (DateUtil.isDate(val))
                return val;
            else {
                return Convert.toDateTime(val + "");
            }
        }
        if (isDecimal(type)) return Convert.toDecimal(val);
        return val + "";
    }

}
