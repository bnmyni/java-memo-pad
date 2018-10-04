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

package com.tuoming.mes.collect.dpp.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

import java.util.ArrayList;
import java.util.List;

public class ConditionQuery {


    private List<Criterion> criterions = new ArrayList<Criterion>();

    public void add(Criterion criterion) {
        criterions.add(criterion);
    }

    public void build(Criteria criteria) {
        for (Criterion criterion : criterions) {
            criteria.add(criterion);
        }
    }

}
