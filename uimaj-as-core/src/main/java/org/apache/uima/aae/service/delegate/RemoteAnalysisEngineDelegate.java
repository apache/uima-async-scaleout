/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.aae.service.delegate;

import org.apache.uima.aae.service.builder.AbstractUimaAsServiceBuilder.Serialization;

public class RemoteAnalysisEngineDelegate extends AnalysisEngineDelegate {
	private String queueName;
	private String brokerURI;
	private int prefetch=0;
	private String serialization = Serialization.XMI.toString();
	public RemoteAnalysisEngineDelegate( String key ) {
		super(key);
		super.setRemote(true);
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getBrokerURI() {
		return brokerURI;
	}
	public void setBrokerURI(String brokerURI) {
		this.brokerURI = brokerURI;
	}
	public boolean isCollocated() {
		return brokerURI.equalsIgnoreCase("java");
	}
	public String getSerialization() {
		return serialization;
	}
	public void setSerialization(String serialization) {
		this.serialization = serialization;
	}
	public int getPrefetch() {
		return prefetch;
	}
	public void setPrefetch(int prefetch) {
		this.prefetch = prefetch;
	}
}
