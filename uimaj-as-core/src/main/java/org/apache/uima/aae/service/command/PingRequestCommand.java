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
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.util.Level;

public class PingRequestCommand extends AbstractUimaAsCommand  {
//	private MessageContext mc;

	public PingRequestCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
//		this.mc = mc;
	}

	public void execute() throws Exception {
		try {
			ENDPOINT_TYPE et = ENDPOINT_TYPE.DIRECT; // default
			if ( super.getEndpoint().isRemote() ) {
				et = ENDPOINT_TYPE.JMS;
			} 
				
			controller.getOutputChannel(et).sendReply(AsynchAEMessage.Ping, super.getEndpoint(), null, false);
		} catch (Exception e) {
			if (UIMAFramework.getLogger(this.getClass()).isLoggable(Level.WARNING)) {
				if (controller != null) {
					UIMAFramework.getLogger(this.getClass()).logrb(Level.WARNING, this.getClass().getName(), "execute",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_service_exception_WARNING",
							controller.getComponentName());
				}

				UIMAFramework.getLogger(this.getClass()).logrb(Level.WARNING, getClass().getName(), "execute",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
			}
		}

	}

}
