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

package com.tuoming.mes.collect.dpp.file;

import java.io.IOException;

import com.pyrlong.util.StringUtil;

public class TextLine {
    private String _line;
    private TextFileProcessor processor;

    public String toString() {
        return _line;
    }

    public TextLine(String line, TextFileProcessor processor) {
        _line = line;
        this.processor = processor;
    }

    public void setLine(String line) {
        _line = line;
    }


    public TextLine(String line) {
        _line = line;
    }

    public TextLine GetNextLine() throws IOException {
        if (processor != null) {
            return processor.getNextLine();
        }
        return null;
    }

    public boolean isMatch(String regx) {
        return StringUtil.isMatch(_line, regx);
    }

}
