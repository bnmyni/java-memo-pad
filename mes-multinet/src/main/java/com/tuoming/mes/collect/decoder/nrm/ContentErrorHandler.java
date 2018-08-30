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

package com.tuoming.mes.collect.decoder.nrm;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.tuoming.mes.collect.decoder.zte.AbstractContentHandler;

/**
 * MRS/MRO 文件解析 错误输出类
 */
public class ContentErrorHandler implements ErrorHandler {
    private static Logger logger = Logger.getLogger(AbstractContentHandler.class);

    public void warning(SAXParseException exception) {
        logger.error("*******WARNING******");
        logger.error("\t行:\t" + exception.getLineNumber());
        logger.error("\t列:\t" + exception.getColumnNumber());
        System.out.println("\t错误信息:\t" + exception.getMessage());
        System.out.println("********************");
    }

    public void error(SAXParseException exception) throws SAXException {
        System.out.println("******* ERROR ******");
        System.out.println("\t行:\t" + exception.getLineNumber());
        System.out.println("\t列:\t" + exception.getColumnNumber());
        System.out.println("\t错误信息:\t" + exception.getMessage());
        System.out.println("********************");
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println("******** FATAL ERROR ********");
        System.out.println("\t行:\t" + exception.getLineNumber());
        System.out.println("\t列:\t" + exception.getColumnNumber());
        System.out.println("\t错误信息:\t" + exception.getMessage());
        System.out.println("*****************************");
    }
}
