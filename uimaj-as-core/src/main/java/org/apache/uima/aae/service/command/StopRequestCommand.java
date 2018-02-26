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
import org.apache.uima.aae.controller.AggregateAnalysisEngineController_impl;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.util.Level;

public class StopRequestCommand extends AbstractUimaAsCommand {
	private MessageContext mc;

	public StopRequestCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller);
		this.mc = mc;
	}

	public void execute() throws Exception {

		try {
			String casReferenceId = mc.getMessageStringProperty(AsynchAEMessage.CasReference);
			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(), "execute",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_received_stop_request__INFO",
						new Object[] { controller.getComponentName(), casReferenceId });
			}

			if (controller.isPrimitive()) {
				controller.addAbortedCasReferenceId(casReferenceId);
			} else {
				CasStateEntry casStateEntry = super.getCasStateEntry(casReferenceId);
				// Mark the CAS as if it have failed. In this case we dont associate any
				// exceptions with this CAS so its really not a failure of a CAS or any
				// of its children. We simply use the same logic here as if the CAS failed.
				// The Aggregate replyToClient() method will know that this CAS was stopped
				// as opposed to failed by the fact that the CAS has no exceptions associated
				// with it. In such case the replyToClient() method returns an input CAS as if
				// it has been fully processed.
				casStateEntry.setFailed();
				((AggregateAnalysisEngineController_impl) controller).stopCasMultipliers();
			}
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
