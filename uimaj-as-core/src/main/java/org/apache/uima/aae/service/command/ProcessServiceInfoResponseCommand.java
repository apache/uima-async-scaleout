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

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.util.Level;

public class ProcessServiceInfoResponseCommand extends AbstractUimaAsCommand {
//	private MessageContext mc;

	public ProcessServiceInfoResponseCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
	//	this.mc = mc;
	}

	public void execute() throws Exception {

	    String casReferenceId = null;
	    try {
	      casReferenceId = super.getMessageStringProperty(AsynchAEMessage.CasReference);
	      if ( casReferenceId == null ) {
	    	  return;  // nothing to do
	      }
	      Endpoint freeCasEndpoint = super.getEndpoint();
	      CasStateEntry casStateEntry = ((AggregateAnalysisEngineController) controller)
	              .getLocalCache().lookupEntry(casReferenceId);
	      if (casStateEntry != null) {
	        casStateEntry.setFreeCasNotificationEndpoint(freeCasEndpoint);
	        //  Fetch host IP where the CAS is being processed. When the UIMA AS service
	        //  receives a CAS it immediately sends ServiceInfo Reply message containing 
	        //  IP of the host where the service is running.
	        String serviceHostIp = super.getMessageStringProperty(AsynchAEMessage.ServerIP);
	        if ( serviceHostIp != null ) {
	          casStateEntry.setHostIpProcessingCAS(serviceHostIp);
	        }
	      }
	    } catch (Exception e) {
	    	UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(),
	                "handleServiceInfoReply", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_exception__WARNING", e);
	    	return;
	    }
	}
}
