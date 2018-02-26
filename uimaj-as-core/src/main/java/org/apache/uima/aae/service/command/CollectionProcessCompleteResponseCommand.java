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
package org.apache.uima.aae.service.command;

import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.delegate.Delegate;
import org.apache.uima.aae.error.ErrorContext;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;

public class CollectionProcessCompleteResponseCommand  extends AbstractUimaAsCommand {
	private MessageContext mc;
	
	public CollectionProcessCompleteResponseCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller);
		this.mc = mc;
	}
	public void execute() throws Exception {
		Delegate delegate = super.getDelegate(mc);
	    try {
	          ((AggregateAnalysisEngineController)controller)
	                  .processCollectionCompleteReplyFromDelegate(delegate.getKey(), true);
	      } catch (Exception e) {
	        ErrorContext errorContext = new ErrorContext();
	        errorContext.add(AsynchAEMessage.Command, AsynchAEMessage.CollectionProcessComplete);
	        errorContext.add(AsynchAEMessage.Endpoint, mc.getEndpoint());
	        controller.getErrorHandlerChain().handle(e, errorContext, controller);
	      }
	}
}
