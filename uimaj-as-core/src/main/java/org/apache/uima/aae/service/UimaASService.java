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
package org.apache.uima.aae.service;

import java.util.concurrent.BlockingQueue;

import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.cas.CAS;

public interface UimaASService {
	enum ServiceMode {
		Asynchronous,
		Synchronous
	};
	public static final int QUIESCE_AND_STOP = 1000;

	public static final int STOP_NOW = 1001;
	  
	public String getEndpoint();
	public String getId();
	public void start() throws Exception;
	public void stop() throws Exception;
	public void quiesce() throws Exception;
	public String getName();
	public CAS getCAS() throws Exception;
	public void process(CAS cas, String casReferenceId) throws Exception;
	public void sendGetMetaRequest() throws Exception;
	public void collectionProcessComplete() throws Exception;
	public void releaseCAS(String casReferenceId, BlockingQueue<DirectMessage> releaseCASQueue ) throws Exception;
	public AnalysisEngineMetaData getMetaData() throws Exception; 
	public void removeFromCache(String casReferenceId);

}
