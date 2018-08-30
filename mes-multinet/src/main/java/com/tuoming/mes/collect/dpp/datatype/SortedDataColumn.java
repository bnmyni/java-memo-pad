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

package com.tuoming.mes.collect.dpp.datatype;


/**
 * 此类描述的是：  用于保存排序字段信息的对象
 *
 * @author: James Cheung
 * @version: 2.0
 */
public class SortedDataColumn {
    private DataColumn column;

    private SortType sortType;

    /**
     * @param column
     */
    public void setColumn(DataColumn column) {
        this.column = column;
    }

    /**
     * @return the column
     */
    public DataColumn getColumn() {
        return column;
    }

    /**
     * @param sortType
     */
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * @return the sortType
     */
    public SortType getSortType() {
        return sortType;
    }

}
