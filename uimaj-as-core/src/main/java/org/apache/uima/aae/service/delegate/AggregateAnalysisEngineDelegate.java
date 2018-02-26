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

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.aae.error.ErrorHandlerChain;

public class AggregateAnalysisEngineDelegate extends AnalysisEngineDelegate {
	private ErrorHandlerChain delegateErrorHandlerChain;
    private List<AnalysisEngineDelegate> delegates = new LinkedList<>();
    
    public AggregateAnalysisEngineDelegate(String key) {
    	super(key);
    	setPrimitive(false);
    }
    public void addDelegate(AnalysisEngineDelegate delegate) {
    	delegates.add(delegate);
    }
    public List<AnalysisEngineDelegate> getDelegates() {
    	return delegates;
    }
	public ErrorHandlerChain getDelegateErrorHandlerChain() {
		return delegateErrorHandlerChain;
	}

	public void setDelegateErrorHandlerChain(ErrorHandlerChain delegateErrorHandlerChain) {
		this.delegateErrorHandlerChain = delegateErrorHandlerChain;
	}
}
