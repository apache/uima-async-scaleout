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
package org.apache.uima.as.dispatcher;

import java.util.concurrent.BlockingQueue;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.client.UimaASProcessStatusImpl;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.UimaAsOrigin;
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngineCommon_impl;
import org.apache.uima.adapter.jms.message.PendingMessage;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.Level;
import org.apache.uima.util.impl.ProcessTrace_impl;

public class LocalDispatcher implements Runnable  {
	private static final Class<LocalDispatcher> CLASS_NAME = LocalDispatcher.class;

	private BlockingQueue<PendingMessage> messageQueue = null;
	private BaseUIMAAsynchronousEngineCommon_impl client;
	private UimaASService service;
	private UimaAsEndpoint clientEndpoint;
	
	public LocalDispatcher(BaseUIMAAsynchronousEngineCommon_impl client, UimaASService service,
			BlockingQueue<PendingMessage> pendingMessageQueue, UimaAsEndpoint clientEndpoint) {
		this.service = service;
		this.client = client;
		this.messageQueue = pendingMessageQueue;
		this.clientEndpoint = clientEndpoint;
	}
	
	private boolean reject(PendingMessage pm) {
		return false;
	}

	private void dispatch(PendingMessage pm) throws Exception {
		boolean doCallback = false;
		StringBuilder serviceUri = new StringBuilder("direct").append(":").append(service.getName());
		
		switch (pm.getMessageType()) {
		case AsynchAEMessage.GetMeta:
			serviceUri.append(":").append(ConsumerType.GetMetaRequest.name()).toString();
			MessageContext getMetaMessage =
				clientEndpoint.newMessageBuilder().
					newGetMetaRequestMessage(new UimaAsOrigin("Client", EndpointType.Direct))
//					newGetMetaRequestMessage(new UimaAsOrigin("direct:Client", EndpointType.Direct))
					.withPayload(AsynchAEMessage.None)
					.build();
			clientEndpoint.dispatch(getMetaMessage, serviceUri.toString());
			
			System.out.println("LocalDispatcher.dispatch()-dispatched getMeta Request");
			break;

		case AsynchAEMessage.Process:
			doCallback = true;
			serviceUri.append(":").append(ConsumerType.ProcessCASRequest.name()).toString();
			
			MessageContext processMessage =
					clientEndpoint.newMessageBuilder().
						newProcessCASRequestMessage(new UimaAsOrigin("Client", EndpointType.Direct))
//						newProcessCASRequestMessage(new UimaAsOrigin("direct:Client", EndpointType.Direct))
						.withPayload(AsynchAEMessage.CASRefID)
						.withCasReferenceId(pm.getPropertyAsString(AsynchAEMessage.CasReference))
						.build();

			service.add2Cache((CAS) pm.getProperty(AsynchAEMessage.CAS), processMessage, pm.getPropertyAsString(AsynchAEMessage.CasReference));

			clientEndpoint.dispatch(processMessage, serviceUri.toString());
				
			System.out.println("LocalDispatcher.dispatch()-dispatched process Request");

				
			break;

		case AsynchAEMessage.CollectionProcessComplete:
			serviceUri.append(":").append(ConsumerType.CpcRequest.name()).toString();

			MessageContext cpcMessage =
			   clientEndpoint.newMessageBuilder().
				  newCpCRequestMessage(new UimaAsOrigin("Client", EndpointType.Direct))
//				  newCpCRequestMessage(new UimaAsOrigin("direct:Client", EndpointType.Direct))
				  .withPayload(AsynchAEMessage.None)
				  .build();
		    clientEndpoint.dispatch(cpcMessage, serviceUri.toString());

			System.out.println("LocalDispatcher.dispatch()-dispatched CPC Request");
			break;

		case AsynchAEMessage.ReleaseCAS:
			serviceUri.append(":").append(ConsumerType.FreeCASRequest.name()).toString();

			MessageContext freeCASMessage =
			   clientEndpoint.newMessageBuilder().
				  newCpCRequestMessage(new UimaAsOrigin("Client", EndpointType.Direct))
//				  newCpCRequestMessage(new UimaAsOrigin("direct:Client", EndpointType.Direct))
				  .withPayload(AsynchAEMessage.CASRefID)
				  .withCasReferenceId(pm.getPropertyAsString(AsynchAEMessage.CasReference))
				  .build();
		    clientEndpoint.dispatch(freeCASMessage, serviceUri.toString());

			System.out.println("LocalDispatcher.dispatch()-dispatched Free CAS Request");
			break;

		
		}
        if ( doCallback ) {
            UimaASProcessStatus status = new UimaASProcessStatusImpl(new ProcessTrace_impl(),(CAS)pm.getProperty(AsynchAEMessage.CAS),
                    pm.getPropertyAsString(AsynchAEMessage.CasReference));
            // Notify engine before sending a message
            if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINE)) {
                UIMAFramework.getLogger(CLASS_NAME).logrb(
                        Level.FINE,
                        CLASS_NAME.getName(),
                        "run",
                        JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                        "UIMAJMS_calling_onBeforeMessageSend__FINE",
                        new Object[] {
                          pm.getPropertyAsString(AsynchAEMessage.CasReference),
                          String.valueOf( ((CAS)(pm.getProperty(AsynchAEMessage.CAS))).hashCode())
                        });
              }  
            // Note the callback is a misnomer. The callback is made *after* the send now
            // Application receiving this callback can consider the CAS as delivere to a queue
            client.onBeforeMessageSend(status);
          
          
          }
	}
	public void run() {

		while (client.isRunning()) {
			PendingMessage pm = null;
			try {
				System.out.println("Client LocalDispatcher.run()- waiting for new message ... queue hashcode:"+messageQueue.hashCode());
				pm = messageQueue.take();
				System.out.println("Client LocalDispatcher.run()-got new message to dispatch");
			} catch (InterruptedException e) {
				
				return;
			}
			// we may have waited in the take() above, so check if the client is still running
			if (!client.isRunning() ) {
				break; 
			}
			
			boolean rejectRequest = reject(pm);
			if (!rejectRequest && client.isRunning()) {
				if (client.getServiceDelegate().isAwaitingPingReply()
						&& pm.getMessageType() == AsynchAEMessage.GetMeta) {
					if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
						UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, getClass().getName(), "run",
								JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_client_dispatching_getmeta_ping__INFO",
								new Object[] {});
					}
				}
				try {
					client.beforeDispatch(pm);
					dispatch(pm);
				} catch (Exception e) {
					if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
						UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, getClass().getName(), "run",
								UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
					}
				}
			}
		}
	}

}
