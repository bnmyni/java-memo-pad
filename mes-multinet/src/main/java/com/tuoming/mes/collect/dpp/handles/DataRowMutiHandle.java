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
package com.tuoming.mes.collect.dpp.handles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.tuoming.mes.collect.dpp.datatype.DataRow;
import com.tuoming.mes.collect.dpp.datatype.DataRowHandler;

class DataRowHandleProcess implements Callable<Integer> {
    String key;
    DataRow row;
    DataRowHandler handle;

    public DataRowHandleProcess(String key, DataRow row, DataRowHandler handle) {
        this.key = key;
        this.row = row;
        this.handle = handle;
    }

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() throws Exception {
        handle.process(key, row);
        return -1;
    }
}

public class DataRowMutiHandle extends AbstractDataRowHandler {

    int processCount = 0;
    private List<DataRowHandler> handles = new ArrayList<DataRowHandler>();
    private ExecutorService exec = Executors.newCachedThreadPool();

    public void addHandler(DataRowHandler h) {
        handles.add(h);
    }

    @Override
    public void process(String key, DataRow row) {
        try {
            processCount++;
            List<Future<Integer>> threads = new ArrayList<Future<Integer>>();
            for (DataRowHandler hdl : handles) {
                DataRowHandleProcess prc = new DataRowHandleProcess(key, row, hdl);
                threads.add(exec.submit(prc));
            }
            if (processCount % 10000 == 0) System.out.println("process count:" + processCount);
            try {
                for (Future<Integer> p : threads)
                    p.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void loadTag(Object o) {

    }
}
