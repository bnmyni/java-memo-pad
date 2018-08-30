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

import com.pyrlong.exception.PyrlongException;

/**
 * Created by James on 14-1-14.
 */
public class DPPException extends PyrlongException {
    public DPPException(final String errorCode, final Object... args) {
        super(errorCode, args);
    }

    public DPPException(final String errorCode, final Throwable cause, final Object... args) {
        super(errorCode, cause, args);
    }

    public DPPException(final String message) {
        super(message);
    }

    public DPPException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
