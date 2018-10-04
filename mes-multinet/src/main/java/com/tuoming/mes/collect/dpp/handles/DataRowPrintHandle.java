/**
 * Copyright (c) 2013.  Pyrlong All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package com.tuoming.mes.collect.dpp.handles;

import com.pyrlong.util.StringUtil;
import com.tuoming.mes.collect.dpp.datatype.DataColumn;
import com.tuoming.mes.collect.dpp.datatype.DataRow;

/**
 * @author James Cheung
 */
public class DataRowPrintHandle extends AbstractDataRowHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.pyrlong.dpp.AbstractDataRowHandler#process(java.lang.String,
     * com.pyrlong.dpp.DataRow)
     */
    @Override
    public synchronized void process(String key, DataRow row) {
        if (row != null) {
            String line = "[";
            for (DataColumn dc : row.getColumns()) {
                line += dc.getCaptionName() + "=" + row.getValue(dc.getColumnName()) + "  ";
            }
            line = StringUtil.substring(line, 0, line.length() - 1);
            line += "]";
            System.out.println(line);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pyrlong.dpp.AbstractDataRowHandler#close()
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pyrlong.dpp.AbstractDataRowHandler#loadTag(java.lang.Object)
     */
    @Override
    public void loadTag(Object o) {
        // TODO Auto-generated method stub

    }

}
