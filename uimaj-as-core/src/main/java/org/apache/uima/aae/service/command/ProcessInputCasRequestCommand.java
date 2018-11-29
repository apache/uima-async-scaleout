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

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.InProcessCache.CacheEntry;
import org.apache.uima.aae.InProcessCache.UndefinedCacheEntry;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController_impl;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.handler.input.ProcessRequestHandler_impl;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.util.Level;

/**
 * Handles input CAS process request
 *
 */
public class ProcessInputCasRequestCommand extends AbstractUimaAsCommand  {
	private static final Class<?> CLASS_NAME = ProcessInputCasRequestCommand.class;

//	private MessageContext mc;
	// controls access to Aggregates semaphore which
	// throttles processing of CASes from a service input queue
	private Object lock = new Object();

	public ProcessInputCasRequestCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
//		this.mc = mc;
	}

	public void execute() throws Exception {

		int payload = super.getMessageIntProperty(AsynchAEMessage.Payload);
		String inputCasReferenceId = super.getCasReferenceId(this.getClass());
		if (inputCasReferenceId == null) {
			// LOG THIS
			System.out.println(
					"ProcessInputCasRequestCommand.execute() - CasReferenceId is missing in MessageContext - Ignoring Message");
			return; // Nothing to do
		}
		// Save Process command in the client endpoint.
		Endpoint clientEndpoint = controller.getClientEndpoint();
		if (clientEndpoint != null) {
			clientEndpoint.setCommand(AsynchAEMessage.Process);
		}
		System.out.println("Controller:"+controller.getComponentName()+
				" ProcessInputCasRequestCommand.execute()");
		// CAS was passed by reference meaning the global InProcessCache must have it. The client
		// created the entry for it already.
		if (AsynchAEMessage.CASRefID == payload) {
			executeDirectRequest(inputCasReferenceId);
		} else {
			executeRemoteRequest(inputCasReferenceId);
		}
	}

	private void blockIfControllerNotReady(CacheEntry casCacheEntry) throws AsynchAEException {
		if (!casCacheEntry.isWarmUp()) {
			controller.getControllerLatch().waitUntilInitialized();
		}

	}

	private void saveReplyTo()  throws Exception {
		// !!!!!!!!!!!!!!!!! HACK !!!!!!!!!!!!!!!!!!!
		// Save true replyTo endpoint to the service sending the request
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Object replyTo = super.getMessageObjectProperty(AsynchAEMessage.ReplyToEndpoint);
		super.getEndpoint().setReplyDestination(replyTo);

	}
	private void saveDelegateKey() throws Exception{
		String delegateKey = super.getMessageStringProperty(AsynchAEMessage.DelegateKey);
		if ( delegateKey == null ) {
//			delegateKey =  super.getMessageStringProperty(AsynchAEMessage.MessageFrom);
			delegateKey =  super.getMessageStringProperty(AsynchAEMessage.DelegateKey);
		}
		super.getEndpoint().setDelegateKey(delegateKey);

	}
	private void saveEndpointName() throws Exception {
		String endpointName = super.getMessageStringProperty(AsynchAEMessage.EndpointName);
		if (endpointName == null ) {
			Origin origin = (Origin)super.getMessageObjectProperty(AsynchAEMessage.MessageFrom);
			endpointName = origin.getName();
//			endpointName = super.getMessageStringProperty(AsynchAEMessage.MessageFrom);
		}
		super.getEndpoint().setEndpoint(endpointName);

	}
	private void addMessageOrigin(CacheEntry inputCasCacheEntry) {
		if (!controller.isPrimitive()) {
			   ((AggregateAnalysisEngineController) controller).addMessageOrigin(inputCasCacheEntry.getCasReferenceId(), super.getEndpoint());
			}

	}
	private void executeDirectRequest(String inputCasReferenceId) throws AsynchAEException {
		try {
			// this is a request from a colocated client, the InProcessCache
			// must have a entry for a given casReferenceId
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINE, CLASS_NAME.getName(), "executeDirectRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_analyzing_cas__FINE",
						new Object[] { inputCasReferenceId });
			}
			CacheEntry inputCasCacheEntry = super.getCacheEntryForCas(inputCasReferenceId);
			if (inputCasCacheEntry instanceof UndefinedCacheEntry) {
				// LOG THIS
				System.out.println("Controller:"+controller.getComponentName()+
						" ProcessInputCasRequestCommand.execute() - Cache Entry for CasReferenceId:"
								+ inputCasReferenceId + " Not Found in InProcessCache - Ignoring Message");
				return; // Nothing to do
			}


			blockIfControllerNotReady(inputCasCacheEntry);
			long arrivalTime = System.nanoTime();
			controller.saveTime(arrivalTime, inputCasCacheEntry.getCasReferenceId(), controller.getName());
			
			saveReplyTo();
			saveDelegateKey();
			saveEndpointName();
			addMessageOrigin(inputCasCacheEntry);	

			// Create a CasStateEntry in a local cache 
			CasStateEntry localStateEntry = getCasStateEntry(inputCasCacheEntry.getCasReferenceId());
			// associate client endpoint with the input CAS. We need to reply to this client
			localStateEntry.setClientEndpoint(super.getEndpoint());
			localStateEntry.setInputCasReferenceId(inputCasCacheEntry.getCasReferenceId());
			

			if (controller.isStopped()) {
				return;
			}
			// Use empty string as key. Top level component stats are stored under this key.
			controller.getMonitor().incrementCount("", Monitor.ProcessCount);
			// *****************************************************************
			// Process the CAS
			// *****************************************************************
			process(inputCasCacheEntry, false);

		} catch (AsynchAEException e) { 
		    controller.getErrorHandlerChain().handle(e, super.populateErrorContext(), controller);
			e.printStackTrace();
		} catch (Exception e) {
//			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.WARNING)) {
//				UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "executeDirectRequest",
//						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_service_exception_WARNING",
//						controller.getComponentName());
//
//				UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "executeDirectRequest",
//						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
//			}
//			if ( !(e instanceof AsynchAEException) ) {
//				e = new AsynchAEException(e);
//			}
			e = new AsynchAEException(e);
		    controller.getErrorHandlerChain().handle(e, super.populateErrorContext(), controller);

			e.printStackTrace();
		} finally {
			// Increment number of CASes processed by this service
			controller.getServicePerformance().incrementNumberOfCASesProcessed();
		} 
 
	}

	private void executeRemoteRequest(String inputCasReferenceId) throws AsynchAEException {
		
		CacheEntry inputCasCacheEntry = null;

		try {
			// The following applies to input CAS from remote clients only.
			// To prevent processing multiple messages with the same CasReferenceId, check
			// the CAS cache to see if the message with a given CasReferenceId is already
			// being processed. If it is, the message contains the same request possibly
			// issued by the caller due to a timeout. Also this mechanism helps with
			// dealing with scenario when this service is not up when the client sends
			// request. The client can keep re-sending the same request until its timeout
			// thresholds are exceeded. By that time, there may be multiple messages in
			// this service queue with the same CasReferenceId. When the service finally
			// comes back up, it will have multiple messages in its queue possibly from
			// the same client. Only the first message for any given CasReferenceId
			// should be processed.
			if (isCasInTheCacheAlready(inputCasReferenceId)) {
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(), "executeRemoteRequest",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_duplicate_request__INFO",
							new Object[] {inputCasReferenceId });
				}

				System.out.println(
						"ProcessInputCasRequestCommand.executeRemoteRequest() - Duplicate request recv'd for CasReferenceId:"
								+ inputCasReferenceId + " - Ignoring Message");
				return; // Nothing to do
			}
			long inTime = System.nanoTime();

			SerializationResult result = super.deserializeInputCAS(); 
			
			// Time how long we wait on Cas Pool to fetch a new CAS
			long t1 = controller.getCpuTime();

			inputCasCacheEntry = controller.getInProcessCache().register(result.getCas(), super.getMessageContext(), result.getDeserSharedData(),
					result.getReuseInfo(), inputCasReferenceId, result.getMarker(), result.acceptsDeltaCas());
			
			saveStats(inputCasCacheEntry, inTime, t1, result.getTimeWaitingForCAS());
			
			boolean waitForCompletion = false;

			// create an entry for the CAS in a local cache
			CasStateEntry cse = getCasStateEntry(inputCasReferenceId);
			
			if (!controller.isPrimitive()) {
				addCompletionSemaphore(inputCasCacheEntry);
				if (cse != null && !cse.isSubordinate()) {
					waitForCompletion = true;
				}
			}

			addMessageOrigin(inputCasCacheEntry);	

			if (controller.isStopped() ) {
				controller.dropCAS(inputCasCacheEntry.getCasReferenceId(), true);
				return;
			}
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINE, CLASS_NAME.getName(), "executeRemoteRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_deserialized_cas_ready_to_process_FINE",
						new Object[] { super.getEndpoint().getEndpoint() });
			}
			process(inputCasCacheEntry, waitForCompletion);

		} catch (Exception e) {
			super.handleError(e, inputCasCacheEntry);
		}

	}
	
	private void process(CacheEntry entry, boolean waitForCompletion) {
		controller.getMonitor().incrementCount("", Monitor.ProcessCount);

		// *****************************************************************
		// Process the CAS
		// *****************************************************************
		if (controller.isPrimitive()) {
			controller.process(entry.getCas(), entry.getCasReferenceId(), super.getEndpoint());
		} else {
			controller.process(entry.getCas(), entry.getCasReferenceId());

			/**
			 * Below comments apply to UIMA AS aggregate only. CAS has been handed off to a
			 * delegate. Now block the receiving thread until the CAS is processed or there
			 * is a timeout or error. Fetch this thread's ThreadLocal semaphore to block the
			 * thread. It will be unblocked when the aggregate is done with the CAS.
			 */

			if (waitForCompletion) {
				waitForCompletionSemaphore(entry);
			}
		}
	}

	private void waitForCompletionSemaphore(CacheEntry entry) {
		try {
			synchronized (lock) {
				while (!controller.isStopped()) {
					if (entry.getThreadCompletionSemaphore() != null) {
						boolean gotIt = entry.getThreadCompletionSemaphore().tryAcquire(500, TimeUnit.MILLISECONDS);
						if (gotIt) {
							break;
						}
					} else {
						break;
					}

				}
			}
		} catch (InterruptedException ex) {
		}

	}



	private void addCompletionSemaphore(CacheEntry entry) {

		synchronized (lock) {
			// lazily create a Semaphore on the first Process request. This semaphore
			// will throttle ingestion of CASes from service input queue.
			if (((AggregateAnalysisEngineController_impl) controller).semaphore == null) {
				((AggregateAnalysisEngineController_impl) controller).semaphore = new Semaphore(
						((AggregateAnalysisEngineController) controller).getServiceCasPoolSize() - 1);
			}
		}
		entry.setThreadCompletionSemaphore(((AggregateAnalysisEngineController_impl) controller).semaphore);
	}

	private boolean isCasInTheCacheAlready(String casReferenceId) {
		// the InProcessCache lookup either returns UndefindCacheEntry or CacheEntry
		// instance
		CacheEntry casCacheEntry = super.getCacheEntryForCas(casReferenceId);
		if (!(casCacheEntry instanceof UndefinedCacheEntry)) {
			// LOG THIS
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(), "executeRemoteRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_duplicate_request__INFO",
						new Object[] { casReferenceId });
			}

			System.out.println(
					"ProcessInputCasRequestCommand.executeRemoteRequest() - Duplicate request recv'd for CasReferenceId:"
							+ casReferenceId + " - Ignoring Message");
			return true;
		}
		return false;
	}
}
