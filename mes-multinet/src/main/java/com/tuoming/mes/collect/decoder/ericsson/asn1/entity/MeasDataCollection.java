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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MeasDataCollection {

	private static Log log = LogFactory.getLog(MeasDataCollection.class);
	private MeasFileHeader measFileHeader = null;
	private List<MeasData> measDataList = null;
	private Timestamp measFileFooter = null;

	public MeasFileHeader getMeasFileHeader() {
		return this.measFileHeader;
	}

	public void setMeasFileHeader(MeasFileHeader value) {
		this.measFileHeader = value;
	}

	public List<MeasData> getMeasData() {
		return this.measDataList;
	}

	public void setMeasData(List<MeasData> value) {
		this.measDataList = value;
	}

	public Timestamp getMeasFileFooter() {
		return this.measFileFooter;
	}

	public void setMeasFileFooter(Timestamp value) {
		this.measFileFooter = value;
	}
	
	public void printAllRecords(){
		log.info("--------- Print all decoded records ---------");
		
		// Print MeasFileHeader elements
		log.info("FileFormatVersion : " + measFileHeader.getFileFormatVersion());
		log.info("SenderName : " + measFileHeader.getSenderName());
		log.info("SenderType : " + measFileHeader.getSenderType());
		log.info("VendorName : " + measFileHeader.getVendorName());
		log.info("CollectionBeginTime : " + measFileHeader.getCollectionBeginTime());
		
		// Print MeasData elements
		for(MeasData measData : measDataList){
			NEId neId = measData.getNEId();
			log.info("NEUserName : " + neId.getNEUserName());
			log.info("NEDistinguishedName : " + neId.getNEDistinguishedName());
			
			List<MeasInfo> measInfoList = measData.getMeasInfo();
			for(MeasInfo measInfo : measInfoList){
				log.info("MeasStartTime : " + measInfo.getMeasStartTime());
				log.info("GranularityPeriod : " + measInfo.getGranularityPeriod());
				
				List<String> measTypesList = measInfo.getMeasTypes();
				for(String measType : measTypesList){
					log.info("MeasType : " + measType);
				}
				
				List<MeasValue> measValuesList = measInfo.getMeasValues();
				for(MeasValue measValue : measValuesList){
					log.info("MeasObjInstId : " + measValue.getMeasObjInstId());
					List<Long> measResultsList = measValue.getMeasResults();
					for(long measResult : measResultsList){
						log.info("MeasResult : " + measResult);
					}
					log.info("SuspectFlag : " + measValue.getSuspectFlag());
				}
			}
		}
		
		
		// Print MeasFileFooter elements
		log.info("MeasFileFooter : " + measFileFooter);
		log.info("--------- Ends of all decoded records ---------");
	}
	
}
