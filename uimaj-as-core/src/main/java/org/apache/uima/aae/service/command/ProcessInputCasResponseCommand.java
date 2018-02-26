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
import org.apache.uima.aae.InProcessCache.CacheEntry;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.delegate.Delegate;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.aae.monitor.statistics.LongNumericStatistic;
import org.apache.uima.util.Level;

public class ProcessInputCasResponseCommand  extends AbstractUimaAsCommand {

	private MessageContext mc;

	public ProcessInputCasResponseCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller);
		this.mc = mc;
	}

	public void execute() throws Exception {

		int payload = mc.getMessageIntProperty(AsynchAEMessage.Payload);
		String casReferenceId = super.getCasReferenceId(this.getClass(), mc);
		System.out.println(">>>>>>>>>>>>>>> Controller:" + controller.getComponentName()
				+ " in ProcessInputCasResponseCommand.execute() - Input CAS:" + casReferenceId + " from "
				+ mc.getMessageStringProperty(AsynchAEMessage.MessageFrom));

		if (casReferenceId == null) {
			// LOG THIS
			System.out.println(
					"ProcessInputCasResponseCommand.execute() - CasReferenceId is missing in MessageContext - Ignoring Message");
			return; // Nothing to do
		}
		// Cas was passed by reference meaning InProcessCache must have it
		if (AsynchAEMessage.CASRefID == payload) {
			executeDirectRequest(casReferenceId);
		} else {
			executeRemoteRequest(casReferenceId);
		}

	}

	/**
	 * Handles process CAS reply from a delegate colocated in the same process
	 * 
	 * @param casReferenceId
	 * @throws AsynchAEException
	 */
	private void executeDirectRequest(String casReferenceId) throws AsynchAEException {
		CacheEntry cacheEntry = null;
		Delegate delegate = null;
		try {
			casReferenceId = mc.getMessageStringProperty(AsynchAEMessage.CasReference);
			// find an entry for a given cas id in a global cache
			cacheEntry = super.getCacheEntryForCas(casReferenceId);
			if (cacheEntry == null) {
				throw new AsynchAEException("CasReferenceId:" + casReferenceId + " Not Found in the Cache.");
			}

			// find an entry for a given cas id in this aggregate's local cache
			CasStateEntry casStateEntry = controller.getLocalCache().lookupEntry(casReferenceId);
			// find delegate which sent the reply
			delegate = super.getDelegate(mc);
			if (casStateEntry != null) {
				casStateEntry.setReplyReceived();
				casStateEntry.setLastDelegate(delegate);
			}
			// each delegate object manages a list of CAS ids that were
			// dispatched. Every time a CAS reply is received, we remove
			// its CAS id from the outstanding list.
			delegate.removeCasFromOutstandingList(casReferenceId);

			if (cacheEntry.getCas() != null) {
				computeStats(mc, cacheEntry);
				((AggregateAnalysisEngineController) controller).process(cacheEntry.getCas(), casReferenceId);

			} else {
				if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(), "executeDirectRequest",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_cas_not_in_cache__INFO",
							new Object[] { controller.getName(), casReferenceId, mc.getEndpoint().getEndpoint() });
				}
				throw new AsynchAEException(
						"CAS with Reference Id:" + casReferenceId + " Not Found in CasManager's CAS Cache");
			}
		} catch (Exception e) {
			super.handleError(e, cacheEntry, mc);
		} finally {
			incrementDelegateProcessCount(mc);
			if (delegate != null) {
				handleAbortedCasMultiplier(delegate, cacheEntry);
			}
		}

	}

	private void blockIfControllerNotReady(CacheEntry casCacheEntry) throws AsynchAEException {
		if (!casCacheEntry.isWarmUp()) {
			controller.getControllerLatch().waitUntilInitialized();
		}

	}

	private void executeRemoteRequest(String casReferenceId) {

	}

	private void handleAbortedCasMultiplier(Delegate delegate, CacheEntry cacheEntry) {
		try {
			// Check if the multiplier aborted during processing of this input CAS
			if (delegate.getEndpoint() != null && delegate.getEndpoint().isCasMultiplier() && cacheEntry.isAborted()) {
				if (!((AggregateAnalysisEngineController) controller).getInProcessCache().isEmpty()) {
					((AggregateAnalysisEngineController) controller).getInProcessCache()
							.registerCallbackWhenCacheEmpty(controller.getEventListener());
				} else {
					// Callback to notify that the cache is empty

					// !!!!!!!!!!!!!!! WHY DO WE NEED TO CALL onCacheEmpty() IF CAS IS ABORTED?
					// !!!!!!!!!!!!!!!!!!!!!! ?????????????????????????????????
					// getController().getEventListener().onCacheEmpty();
				}
			}

		} catch (Exception e) {
			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.WARNING)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "executeDirectRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_service_exception_WARNING",
						controller.getComponentName());

				UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "executeDirectRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
			}
		}
	}

	private void incrementDelegateProcessCount(MessageContext aMessageContext) {
		Endpoint endpoint = aMessageContext.getEndpoint();
		if (endpoint != null && controller instanceof AggregateAnalysisEngineController) {
			try {
				String delegateKey = ((AggregateAnalysisEngineController) controller)
						.lookUpDelegateKey(endpoint.getEndpoint());
				LongNumericStatistic stat = controller.getMonitor().getLongNumericStatistic(delegateKey,
						Monitor.ProcessCount);
				stat.increment();
			} catch (Exception e) {
				if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(),
							"incrementDelegateProcessCount", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
							"UIMAEE_delegate_key_for_endpoint_not_found__INFO",
							new Object[] { controller.getComponentName(), endpoint.getEndpoint() });
				}
			}
		}

	}

	protected void computeStats(MessageContext aMessageContext, CacheEntry cacheEntry) throws AsynchAEException {
		if (aMessageContext.propertyExists(AsynchAEMessage.TimeInService)) {
			long departureTime = controller.getTime(cacheEntry.getCasReferenceId(),
					aMessageContext.getEndpoint().getEndpoint());
			long currentTime = System.nanoTime();
			long roundTrip = currentTime - departureTime;
			long timeInService = aMessageContext.getMessageLongProperty(AsynchAEMessage.TimeInService);
			long totalTimeInComms = currentTime - (departureTime - timeInService);

			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_roundtrip_time__FINE",
						new Object[] { cacheEntry.getCasReferenceId(), aMessageContext.getEndpoint(),
								(double) roundTrip / (double) 1000000 });

				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_time_spent_in_delegate__FINE",
						new Object[] { cacheEntry.getCasReferenceId(), (double) timeInService / (double) 1000000,
								aMessageContext.getEndpoint() });

				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_time_spent_in_comms__FINE",
						new Object[] { cacheEntry.getCasReferenceId(), (double) totalTimeInComms / (double) 1000000,
								aMessageContext.getEndpoint() });
			}
		}

		if (controller instanceof AggregateAnalysisEngineController) {
			aggregateDelegateStats(aMessageContext, cacheEntry);
		}
	}

	protected synchronized void aggregateDelegateStats(MessageContext aMessageContext, CacheEntry cacheEntry)
			throws AsynchAEException {
		String delegateKey = "";
		try {

			if (aMessageContext.getEndpoint().getEndpoint() == null
					|| aMessageContext.getEndpoint().getEndpoint().trim().length() == 0) {
				String fromEndpoint = aMessageContext.getMessageStringProperty(AsynchAEMessage.MessageFrom);
				delegateKey = ((AggregateAnalysisEngineController) controller).lookUpDelegateKey(fromEndpoint);
			} else {
				delegateKey = ((AggregateAnalysisEngineController) controller)
						.lookUpDelegateKey(aMessageContext.getEndpoint().getEndpoint());
			}
			CacheEntry parentCasEntry = null;
			String parentCasReferenceId = cacheEntry.getInputCasReferenceId();
			ServicePerformance casStats = ((AggregateAnalysisEngineController) controller)
					.getCasStatistics(cacheEntry.getCasReferenceId());
			if (parentCasReferenceId != null && controller.getInProcessCache().entryExists(parentCasReferenceId)) {
				String casProducerKey = cacheEntry.getCasProducerKey();
				if (casProducerKey != null
						&& ((AggregateAnalysisEngineController) controller).isDelegateKeyValid(casProducerKey)) {
					// Get entry for the parent CAS
					parentCasEntry = controller.getInProcessCache().getCacheEntryForCAS(parentCasReferenceId);
				}

			}
			ServicePerformance delegateServicePerformance = ((AggregateAnalysisEngineController) controller)
					.getServicePerformance(delegateKey);

			if (aMessageContext.propertyExists(AsynchAEMessage.TimeToSerializeCAS)) {
				long timeToSerializeCAS = ((Long) aMessageContext
						.getMessageLongProperty(AsynchAEMessage.TimeToSerializeCAS)).longValue();
				if (timeToSerializeCAS > 0) {
					if (delegateServicePerformance != null) {
						delegateServicePerformance.incrementCasSerializationTime(timeToSerializeCAS);
					}
				}
			}
			if (aMessageContext.propertyExists(AsynchAEMessage.TimeToDeserializeCAS)) {
				long timeToDeserializeCAS = ((Long) aMessageContext
						.getMessageLongProperty(AsynchAEMessage.TimeToDeserializeCAS)).longValue();
				if (timeToDeserializeCAS > 0) {
					if (delegateServicePerformance != null) {
						delegateServicePerformance.incrementCasDeserializationTime(timeToDeserializeCAS);
					}
				}
			}

			if (aMessageContext.propertyExists(AsynchAEMessage.IdleTime)) {
				long idleTime = ((Long) aMessageContext.getMessageLongProperty(AsynchAEMessage.IdleTime)).longValue();
				if (idleTime > 0 && delegateServicePerformance != null) {
					Endpoint endp = aMessageContext.getEndpoint();
					if (endp != null && endp.isRemote()) {
						delegateServicePerformance.incrementIdleTime(idleTime);
					}
				}
			}

			if (aMessageContext.propertyExists(AsynchAEMessage.TimeWaitingForCAS)) {
				long timeWaitingForCAS = ((Long) aMessageContext
						.getMessageLongProperty(AsynchAEMessage.TimeWaitingForCAS)).longValue();
				if (timeWaitingForCAS > 0 && aMessageContext.getEndpoint().isRemote()) {
					cacheEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
					delegateServicePerformance.incrementCasPoolWaitTime(
							timeWaitingForCAS - delegateServicePerformance.getRawCasPoolWaitTime());
					if (parentCasEntry != null) {
						parentCasEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
					}
				}
			}
			if (aMessageContext.propertyExists(AsynchAEMessage.TimeInProcessCAS)) {
				long timeInProcessCAS = ((Long) aMessageContext
						.getMessageLongProperty(AsynchAEMessage.TimeInProcessCAS)).longValue();
				Endpoint endp = aMessageContext.getEndpoint();
				if (endp != null && endp.isRemote()) {
					if (delegateServicePerformance != null) {
						// calculate the time spent in analysis. The remote service returns total time
						// spent in the analysis. Compute the delta.
						long dt = timeInProcessCAS - delegateServicePerformance.getRawAnalysisTime();
						// increment total time in analysis
						delegateServicePerformance.incrementAnalysisTime(dt);
						controller.getServicePerformance().incrementAnalysisTime(dt);
					}
				} else {
					controller.getServicePerformance().incrementAnalysisTime(timeInProcessCAS);
				}
				casStats.incrementAnalysisTime(timeInProcessCAS);

				if (parentCasReferenceId != null) {
					ServicePerformance inputCasStats = ((AggregateAnalysisEngineController) controller)
							.getCasStatistics(parentCasReferenceId);
					// Update processing time for this CAS
					if (inputCasStats != null) {
						inputCasStats.incrementAnalysisTime(timeInProcessCAS);
					}
				}

			}
		} catch (AsynchAEException e) {
			throw e;
		} catch (Exception e) {
			throw new AsynchAEException(e);
		}
	}
}
