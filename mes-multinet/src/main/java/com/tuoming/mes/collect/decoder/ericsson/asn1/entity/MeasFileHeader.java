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

import java.sql.Timestamp;

public class MeasFileHeader {
	private Integer fileFormatVersion = null;
	private String senderName = null;
	private String senderType = null;
	private String vendorName = null;
	private Timestamp collectionBeginTime = null;
	private String bscName = null;

	public String getBscName() {
		return bscName;
	}

	public void setBscName(String bscName) {
		this.bscName = bscName;
	}

	public Integer getFileFormatVersion() {
		return this.fileFormatVersion;
	}

	public void setFileFormatVersion(Integer value) {
		this.fileFormatVersion = value;
	}

	public String getSenderName() {
		return this.senderName;
	}

	public void setSenderName(String value) {
		this.senderName = value;
	}

	public String getSenderType() {
		return this.senderType;
	}

	public void setSenderType(String value) {
		this.senderType = value;
	}

	public String getVendorName() {
		return this.vendorName;
	}

	public void setVendorName(String value) {
		this.vendorName = value;
	}

	public Timestamp getCollectionBeginTime() {
		return this.collectionBeginTime;
	}

	public void setCollectionBeginTime(Timestamp value) {
		this.collectionBeginTime = value;
	}

}
