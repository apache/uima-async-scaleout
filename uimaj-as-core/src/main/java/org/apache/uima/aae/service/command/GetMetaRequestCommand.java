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
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.util.Level;

public class GetMetaRequestCommand extends AbstractUimaAsCommand  {
	private MessageContext mc;
	
	public GetMetaRequestCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller);
		this.mc = mc;
	}
	public void execute() throws Exception {
        Endpoint endpoint = mc.getEndpoint();
        if (controller.isTopLevelComponent()) {
          endpoint.setCommand(AsynchAEMessage.GetMeta);
          controller.cacheClientEndpoint(endpoint);
        }
        if (UIMAFramework.getLogger(this.getClass()).isLoggable(Level.FINEST)) {
          UIMAFramework.getLogger(this.getClass()).logrb(Level.FINEST, this.getClass().getName(), "execute",
                  UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
                  "UIMAEE_handling_metadata_request__FINEST",
                  new Object[] { endpoint.getEndpoint() });
        }
        controller.getControllerLatch().waitUntilInitialized();
        // Check to see if the controller hasnt been aborted while we were waiting on the latch
        if (!controller.isStopped()) {
System.out.println("............................ Service:"+controller.getComponentName()+" Dispatching Metadata to Client");
        	controller.sendMetadata(endpoint);
        }
	}

}
