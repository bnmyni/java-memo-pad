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

public class MeasData {

	private NEId nEId = null;

	private List<MeasInfo> measInfo = null;

	public NEId getNEId() {
		return this.nEId;
	}

	public void setNEId(NEId value) {
		this.nEId = value;
	}

	public List<MeasInfo> getMeasInfo() {
		return this.measInfo;
	}

	public void setMeasInfo(List<MeasInfo> value) {
		this.measInfo = value;
	}

}
