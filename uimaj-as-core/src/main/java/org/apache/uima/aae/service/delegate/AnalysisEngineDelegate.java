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

import org.apache.uima.analysis_engine.AnalysisEngineDescription;

public class AnalysisEngineDelegate {
	private String key;
	private boolean remote=false;
	private int scaleout=1;
	private int replyScaleout=1;
	private boolean primitive;
	private boolean async=false;
	private AnalysisEngineDescription resourceSpecifier=null;
	private CasMultiplierNature cm=null;
	private int getMetaTimeout = 0;
	private int processTimeout=0;
	private int cpcTimeout=0;
	
	public AnalysisEngineDelegate() {
		this("");
	}
	public AnalysisEngineDelegate(String key) {
		setKey(key);
	}
	public boolean isCasMultiplier() {
		return cm != null;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isRemote() {
		return remote;
	}
	public void setRemote(boolean remote) {
		this.remote = remote;
	}
	public int getScaleout() {
		return scaleout;
	}
	public void setScaleout(int scaleout) {
		this.scaleout = scaleout;
	}
	public boolean isPrimitive() {
		return primitive;
	}
	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}
	public AnalysisEngineDescription getResourceSpecifier() {
		return resourceSpecifier;
	}
	public void setResourceSpecifier(AnalysisEngineDescription resourceSpecifier) {
		this.resourceSpecifier = resourceSpecifier;
	}
	public CasMultiplierNature getCasMultiplier() {
		return cm;
	}
	public void setCm(CasMultiplierNature cm) {
		this.cm = cm;
	}
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}
	public int getGetMetaTimeout() {
		return getMetaTimeout;
	}
	public void setGetMetaTimeout(int getMetaTimeout) {
		this.getMetaTimeout = getMetaTimeout;
	}
	public int getProcessTimeout() {
		return processTimeout;
	}
	public void setProcessTimeout(int processTimeout) {
		this.processTimeout = processTimeout;
	}
	public int getCpcTimeout() {
		return cpcTimeout;
	}
	public void setCpcTimeout(int cpcTimeout) {
		this.cpcTimeout = cpcTimeout;
	}
	public int getReplyScaleout() {
		return replyScaleout;
	}
	public void setReplyScaleout(int setReplyScaleout) {
		this.replyScaleout = setReplyScaleout;
	}
}
