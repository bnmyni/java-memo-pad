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

package com.pyrlong.exception;


import com.pyrlong.Envirment;
import com.pyrlong.localization.ErrorMessage;
import com.pyrlong.localization.LocalizationManager;
import com.pyrlong.logging.LogFacade;
import com.pyrlong.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.Formatter;

public class PyrlongException extends RuntimeException {
    protected String errorCode;
    private final static Logger logger = LogFacade.getLog4j(PyrlongException.class);
    ErrorMessage errorMessage;
    Object[] args;

    public PyrlongException(String errorCode, Object... args) {
        super("ERROR: " + errorCode);
        this.args = args;
        errorMessage = LocalizationManager.getErrorMessage(errorCode);
        this.errorCode = errorCode;
    }

    public PyrlongException(String errorCode, Exception cause, Object... args) {
        super(cause.getMessage(), cause);
        this.args = args;
        errorMessage = LocalizationManager.getErrorMessage(errorCode);
        this.errorCode = errorCode;
    }

    public PyrlongException(String message) {
        super(message);
        this.errorCode = message;
    }

    public PyrlongException(String message, Throwable cause) {
        super(message, cause);
    }


    public void logMe() {
        logger.error(toString(), this);
    }

    public String toString() {
        if (errorMessage!=null&&StringUtil.isNotEmpty(errorCode)) {
            StringBuilder sb = new StringBuilder();
            sb.append(Envirment.LINE_SEPARATOR);
            sb.append("--------------------------------------------------------------------------------------");
            sb.append(Envirment.LINE_SEPARATOR);
            sb.append("ERROR : ");
            sb.append(errorMessage.getCode());
            sb.append(Envirment.LINE_SEPARATOR);
            sb.append("--------------------------------------------------------------------------------------");
            sb.append(Envirment.LINE_SEPARATOR);
            sb.append("    ");
            sb.append(String.format(errorMessage.getMessage(), args));
            sb.append(" : ");
            if(getCause()!=null)
            sb.append(getCause().getMessage());
            sb.append(" ");
            sb.append(errorMessage.getHelp());
            sb.append(Envirment.LINE_SEPARATOR);
            sb.append("--------------------------------------------------------------------------------------");
            return sb.toString();
        } else {
            return getLocalizedMessage();
        }
    }
}
