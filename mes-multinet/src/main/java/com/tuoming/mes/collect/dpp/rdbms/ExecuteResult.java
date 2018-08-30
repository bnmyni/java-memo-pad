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

package com.tuoming.mes.collect.dpp.rdbms;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: James
 * Date: 13-10-18
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteResult {
    private ResultSet resultSet;
    private Statement statement;

    public ExecuteResult(ResultSet resultSet, Statement statement) {
        this.resultSet = resultSet;
        this.statement = statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void close() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception ex) {
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ex) {
            }
        }
    }
}
