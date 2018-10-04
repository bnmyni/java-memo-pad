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

package com.tuoming.mes.collect.decoder.ericsson.asn1.entity;

public class NEId {
    private String nEUserName = null;
    private String nEDistinguishedName = null;

    public String getNEUserName() {
        return this.nEUserName;
    }

    public void setNEUserName(String value) {
        this.nEUserName = value;
    }

    public String getNEDistinguishedName() {
        return this.nEDistinguishedName;
    }

    public void setNEDistinguishedName(String value) {
        this.nEDistinguishedName = value;
    }

}
