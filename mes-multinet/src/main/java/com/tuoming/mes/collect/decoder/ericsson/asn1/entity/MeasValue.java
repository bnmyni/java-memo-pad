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

import java.util.List;

public class MeasValue {

	private String measObjInstId = null;
	private List<Long> measResults = null;
	private boolean suspectFlag = false;
	private String mo = null;


	public String getMo() {
		return mo;
	}

	public void setMo(String mo) {
		this.mo = mo;
	}

	public String getMeasObjInstId() {
		return this.measObjInstId;
	}

	public void setMeasObjInstId(String value) {
		this.measObjInstId = value;
	}

	public List<Long> getMeasResults() {
		return this.measResults;
	}

	public void setMeasResults(List<Long> value) {
		this.measResults = value;
	}

	public boolean getSuspectFlag() {
		return this.suspectFlag;
	}

	public void setSuspectFlag(boolean value) {
		this.suspectFlag = value;
	}

}
