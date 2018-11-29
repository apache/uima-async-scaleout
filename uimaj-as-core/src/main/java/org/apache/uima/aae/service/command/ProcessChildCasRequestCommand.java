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
import org.apache.uima.aae.controller.AggregateAnalysisEngineController_impl;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.Endpoint_impl;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.delegate.Delegate;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Marker;
import org.apache.uima.util.Level;

public class ProcessChildCasRequestCommand extends AbstractUimaAsCommand {
//	private MessageContext mc;

	public ProcessChildCasRequestCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
//		this.mc = mc;
	}

	public void execute() throws Exception {
		int payload = super.getMessageIntProperty(AsynchAEMessage.Payload);
		String casReferenceId = super.getCasReferenceId(this.getClass());
		String parentCasReferenceId = super.getMessageStringProperty(AsynchAEMessage.InputCasReference);
		System.out.println(">>>>>>>>>>>>>>> Controller:"+controller.getComponentName()+
				" in ProcessChildCasRequestCommand.execute() - Child CAS:"+casReferenceId+
				" Parent CAS:"+parentCasReferenceId+
				" from "+
				((Origin)super
                .getMessageObjectProperty(AsynchAEMessage.MessageFrom)).getName());

		if (parentCasReferenceId == null) {
            if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
                UIMAFramework.getLogger(getClass()).logrb(
                        Level.INFO,
                        getClass().getName(),
                        "execute",
                        UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
                        "UIMAEE_input_cas_invalid__INFO",
                        new Object[] { controller.getComponentName(), super.getEndpointName(),
                        		parentCasReferenceId });
              }

			// LOG THIS
			System.out.println(
					"ProcessChildCasRequestCommand.execute() - Parent CasReferenceId is missing in MessageContext - Ignoring Message");
			return; // Nothing to do

		}
		if (casReferenceId == null) {
			// LOG THIS
			System.out.println(
					"ProcessChildCasRequestCommand.execute() - CasReferenceId is missing in MessageContext - Ignoring Message");
			return; // Nothing to do
		}
		// Save Process command in the client endpoint.
		Endpoint clientEndpoint = controller.getClientEndpoint();
		if (clientEndpoint != null) {
			clientEndpoint.setCommand(AsynchAEMessage.Process);
		}

		// Cas was passed by reference meaning InProcessCache must have it
		if (AsynchAEMessage.CASRefID == payload) {
			System.out.println(
					"ProcessChildCasRequestCommand.execute() - Child CasReferenceId:"+casReferenceId+" From Co-located CM");

			if (super.getEndpoint() == null) {
				if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(), "executeDirectRequest",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_no_endpoint_for_reply__INFO",
							new Object[] { casReferenceId });
				}
				return;
			}

			executeDirectRequest(casReferenceId, parentCasReferenceId);
		} else {
			// Check if there is an XMI cargo in the message
			if (super.getMessageIntProperty(AsynchAEMessage.Payload) == AsynchAEMessage.XMIPayload
					&& super.getStringMessage() == null) {
				return; // No XMI just return
			}
			System.out.println(
					"ProcessChildCasRequestCommand.execute() - Child CasReferenceId:"+casReferenceId+" From Remote CM");

			executeRemoteRequest(casReferenceId, parentCasReferenceId);
		}
	}
	private void saveFreeCasDestination(CasStateEntry childCasStateEntry) throws Exception {
		if ( super.getMessageObjectProperty(AsynchAEMessage.FreeCASQueue) != null ) {
			Object freeCASQueue = 
				super.getMessageObjectProperty(AsynchAEMessage.FreeCASQueue);
			Endpoint freeCasNotificationEndpoint =
					new Endpoint_impl();
			freeCasNotificationEndpoint.setServerURI("java");
			freeCasNotificationEndpoint.setFreeCasEndpoint(true);
			freeCasNotificationEndpoint.setJavaRemote();
			freeCasNotificationEndpoint.setDestination(freeCASQueue);
			// the aggregate will send FREE CAS request to the delegate deployed
			// in the same JVM
			childCasStateEntry.setFreeCasNotificationEndpoint(freeCasNotificationEndpoint);
			System.out.println("............... Service:"+controller.getComponentName()+" Saved FreeCasQueue for CAS:"+childCasStateEntry.getCasReferenceId());
		}
	}
	private CasStateEntry getParentCasStateEntry(String parentCasReferenceId, Delegate delegateCasMultiplier ) throws AsynchAEException {
		// fetch parent CAS entry from this aggregate's local cache. This must
		// exist if we receive a child CAS generated from the parent
		CasStateEntry parentCasStateEntry = controller.getLocalCache().lookupEntry(parentCasReferenceId);
		if ( parentCasStateEntry == null ) {
			throw new AsynchAEException("Parent CAS "+parentCasReferenceId+ " Not Found in InprocessCache");
		}
		return parentCasStateEntry;
	}
	private void associateInputCASOriginWithChildCAS(String childCasReferenceId, String parentCasReferenceId, Delegate delegateCasMultiplier ) 
	throws AsynchAEException {
		
		Endpoint inputCasOrigin = fetchParentCasOrigin(parentCasReferenceId);
		if ( controller.isCasMultiplier()) {
			// associate this aggregate client reply endpoint with the child CAS
			// Since this aggregate is a CM, the child CAS maybe sent to this client.
			((AggregateAnalysisEngineController) controller).addMessageOrigin(childCasReferenceId, inputCasOrigin);
		}
		if (inputCasOrigin == null) {
			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(), "executeDirectRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_msg_origin_not_found__INFO",
						new Object[] { controller.getComponentName(), parentCasReferenceId });
			}

		} else {
			((AggregateAnalysisEngineController) controller).addMessageOrigin(childCasReferenceId, inputCasOrigin);
			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINEST)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.FINEST, getClass().getName(), "executeDirectRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_msg_origin_added__FINEST", new Object[] {
								controller.getComponentName(), childCasReferenceId, delegateCasMultiplier.getKey() });
			}
		}
	}
	private void executeDirectRequest(String childCasReferenceId, String parentCasReferenceId)  {
		CAS cas = null;
		try {
			// if the child CAS was produced by a co-located delegate CM
			// of this aggregate, the CasStateEntry instance has already
			// been created for the child by the CM controller in its 
			// process() method. If the child CAS was
			// produced by an *aggregate* CM delegate, a new CasStateEntry
			// needs to be created. The CasStateEntry is local to each
			// aggregate.
			CasStateEntry childCasStateEntry = getCasStateEntry(childCasReferenceId);

			// get the delegate CM where the child CAS was produced
			Delegate delegateCasMultiplier = getLastDelegate(childCasStateEntry);
			// associate the delegate CM with the child CAS
			setLastDelegate(delegateCasMultiplier, childCasStateEntry);

			// the parent CAS must be in this controller's local cache
			CasStateEntry parentCasStateEntry = 
					getParentCasStateEntry(parentCasReferenceId, delegateCasMultiplier );
			
			// if a child CAS has been created in a primitive CM delegate of
			// this aggregate the parent CAS id has already been assigned by
			// the CM. If the child CAS was created by a remote java CM delegate
			// we need to associate the child with its parent CAS here. Remote 
			// java CM is deployed in the same JVM but is considered as remote
			if (childCasStateEntry.getParentCasReferenceId() == null) {
				childCasStateEntry.setParentCasReferenceId(parentCasReferenceId);
			}
			// Check if the parent CAS is in a failed state first
			if (parentCasStateEntry.isFailed()) {
				// handle CAS release
				controller.process(null, childCasReferenceId);
				return;
			}
			// if this aggregate is a CM, we may send this child CAS to the aggregate's client.
			// Associate this aggregate client with the child CAS
			childCasStateEntry.setClientEndpoint(parentCasStateEntry.getClientEndpoint());
			// every child CAS is associated with an input CAS. An input CAS may be different
			// from the child CAS' parent CAS if this aggregate has more than one CM. An input 
			// CAS is a top ancestor from which child CASes are produced.
			childCasStateEntry.setInputCasReferenceId(parentCasStateEntry.getInputCasReferenceId());

			super.getEndpoint().setIsCasMultiplier(true);
			associateInputCASOriginWithChildCAS(childCasReferenceId, parentCasReferenceId, delegateCasMultiplier );

			Endpoint e = ((AggregateAnalysisEngineController_impl)controller).
					getDestinations().get(delegateCasMultiplier.getKey());

			System.out.println("??????????? ProcessChildCasRequest.executeDirectRequest()- Controller:"+controller.getComponentName() +" Delegate:"+delegateCasMultiplier.getKey() + " isJavaRemote="+e.isJavaRemote()+" CAS:"+childCasStateEntry.getCasReferenceId());
			// delegate of this aggregate can be deployed in the same JVM but its deployed
			// with a Java queue instead of JMS. Such service is called JavaRemote delegate.
			// This is an independent CM service and we need to send explicit FreeCAS request
			// for every child CAS received from there. Its like a remote but runs in the
			// same JVM as this aggregate.
			if ( e.isJavaRemote() ) {
				saveFreeCasDestination(childCasStateEntry);
				// Increment parent CAS child count. This is needed to determine when the parent
				// can be released. Only if child count is zero, the parent CAS can be released.
				parentCasStateEntry.incrementSubordinateCasInPlayCount();
				//System.out.println("..Controller:"+controller.getComponentName()+" Processing Child CAS:"+childCasReferenceId+" Incremented Parent CAS:"+parentCasReferenceId+ " Child Count to:"+parentCasStateEntry.getSubordinateCasInPlayCount());
			}
			long arrivalTime = System.nanoTime();
			controller.saveTime(arrivalTime, childCasReferenceId, controller.getName());

			// Save Process command in the client endpoint.
			Endpoint clientEndpoint = controller.getClientEndpoint();
			if (clientEndpoint != null) {
				clientEndpoint.setCommand(AsynchAEMessage.Process);
			}

			if (controller.isStopped()) {
				return;
			}
			cas = controller.getInProcessCache().getCasByReference(childCasReferenceId);
			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "executeDirectRequest",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_analyzing_cas__FINE",
						new Object[] { childCasReferenceId });
			}
			StringBuilder sb = new StringBuilder();
			sb.append("############ Controller:").append(controller.getComponentName()).
			   append(" Processing Child CAS:").append(childCasReferenceId).append(" From Parent CAS:").
			   append(parentCasReferenceId).append(" Child Count to:").append(parentCasStateEntry.getSubordinateCasInPlayCount());
			System.out.println(sb.toString());
			((AggregateAnalysisEngineController) controller).process(cas, parentCasReferenceId,
					childCasReferenceId, delegateCasMultiplier.getKey());

		} catch (Exception e) {
			handleException(e);
		}
	}
	private void handleException(Exception e) {
		if (UIMAFramework.getLogger(getClass()).isLoggable(Level.WARNING)) {
			UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "executeDirectRequest",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_service_exception_WARNING",
					controller.getComponentName());

			UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "executeDirectRequest",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
		}
		if ( !(e instanceof AsynchAEException) ) {
			e = new AsynchAEException(e);
		}
	    controller.getErrorHandlerChain().handle(e, super.populateErrorContext(), controller);

		e.printStackTrace();

	}
	private void executeRemoteRequest(String childCasReferenceId, String parentCasReferenceId)
			throws AsynchAEException {
		SerializationResult result = null;
		CacheEntry childCacheEntry = null;
		long inTime = System.nanoTime();

		try {
			CasStateEntry parentCasStateEntry = saveFreeCasEndpointInParentCasStateEntry(parentCasReferenceId);

			computeStats( parentCasReferenceId);

			super.getEndpoint().setDestination(null);

			// This CAS came in from a CAS Multiplier. Treat it differently than the
			// input CAS. In case the Aggregate needs to send this CAS to the
			// client, retrieve the client destination by looking up the client endpoint
			// using input CAS reference id. CASes generated by the CAS multiplier will have
			// the same Cas Reference id. Fetch Cache entry for the parent CAS
			CacheEntry parentCasCacheEntry = controller.getInProcessCache().getCacheEntryForCAS(parentCasReferenceId);
			Endpoint replyToEndpoint = parentCasCacheEntry.getMessageOrigin();
			// The message context contains a Cas Multiplier endpoint. Since we dont want to
			// send a generated CAS back to the CM, override with an endpoint provided by
			// the client of
			// this service. Client endpoint is attached to an input Cas cache entry.
			if (replyToEndpoint != null) {
				super.getEndpoint().setEndpoint(replyToEndpoint.getEndpoint());
				super.getEndpoint().setServerURI(replyToEndpoint.getServerURI());
			}
			// create local cache entry for the child CAS
			CasStateEntry childCasStateEntry = super.getCasStateEntry(childCasReferenceId);
			// associate parent CAS with the child
			childCasStateEntry.setParentCasReferenceId(parentCasReferenceId);
			Delegate delegate = getLastDelegate(childCasStateEntry);
			setLastDelegate(delegate, childCasStateEntry);
			
			// If there is one thread receiving messages from Cas Multiplier increment
			// number of child CASes of the parent CAS. If there are more threads
			// (consumers)
			// a special object ConcurrentMessageListener has already incremented the count.
			// This special object enforces order of processing for CASes
			// coming in from the Cas Multiplier.
			if (!delegate.hasConcurrentConsumersOnReplyQueue()) {
				parentCasStateEntry.incrementSubordinateCasInPlayCount();
			}

			boolean failed = false;
			// Time how long we wait on Cas Pool to fetch a new CAS
			long t1 = controller.getCpuTime();
			Exception cachedException = null;
			try {
				result = deserializeChildCAS(delegate.getKey(), super.getEndpoint());

			} catch (Exception e) {
				failed = true;
				cachedException = e;
			} finally {
				// create child CAS cache entry
				childCacheEntry = controller.getInProcessCache().register(result.getCas(), super.getMessageContext(),
						result.getDeserSharedData(), result.getReuseInfo(), childCasReferenceId, result.getMarker(),
						result.acceptsDeltaCas());
				childCacheEntry.setInputCasReferenceId(parentCasReferenceId);
				childCacheEntry.setFreeCasEndpoint(parentCasCacheEntry.getFreeCasEndpoint());
				saveStats(childCacheEntry, inTime, t1, result.getTimeWaitingForCAS());
				/*
				 * ********************************************************* Throw an exception
				 * if the deserialization above failed
				 * *********************************************************
				 */
				if (failed) {
					childCacheEntry.setFailed(true);
					throw cachedException;
				} else {
					// *************************************************************************
					// Check and set up for Delta CAS reply
					// *************************************************************************
					boolean acceptsDeltaCas = false;
					Marker marker = null;
					if (super.propertyExists(AsynchAEMessage.AcceptsDeltaCas)) {
						acceptsDeltaCas = super.getMessageBooleanProperty(AsynchAEMessage.AcceptsDeltaCas);
						if (acceptsDeltaCas) {
							marker = result.getCas().createMarker();
							result.setAcceptsDeltaCas(acceptsDeltaCas);
							result.setMarker(marker);
						}
					}
				}
				if (controller.isStopped()) {
					// The Controller is in shutdown state, release the CAS
					controller.dropCAS(childCacheEntry.getCasReferenceId(), true);
					return;
				}
				if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
					UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "executeRemoteRequest",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_deserialized_cas_ready_to_process_FINE",
							new Object[] { super.getEndpoint().getEndpoint() });
				}

				((AggregateAnalysisEngineController) controller).process(result.getCas(), parentCasReferenceId,
						childCasReferenceId, delegate.getKey());

			}
		} catch (Exception e) {
			super.handleError(e, childCacheEntry);
		}

	}
	private void setLastDelegate(Delegate delegate, CasStateEntry childCasStateEntry) {
		// Save the last delegate handling this CAS
		childCasStateEntry.setLastDelegate(delegate);
		controller.getInProcessCache().setCasProducer(childCasStateEntry.getCasReferenceId(), delegate.getKey());

	}
	private Delegate getLastDelegate(CasStateEntry childCasStateEntry) throws Exception {
		String cmEndpointName = 
				((Origin)super.getMessageObjectProperty(AsynchAEMessage.MessageFrom)).getName();
		String newCASProducedBy = ((AggregateAnalysisEngineController) controller).lookUpDelegateKey(cmEndpointName);
		Delegate delegate = ((AggregateAnalysisEngineController) controller).lookupDelegate(newCASProducedBy);

		return delegate;
	}

	private CasStateEntry saveFreeCasEndpointInParentCasStateEntry(String parentCasReferenceId)
			throws Exception {
		// Fetch the name of the Cas Multiplier's input queue
		// String cmEndpointName = aMessageContext.getEndpoint().getEndpoint();
		String cmEndpointName = super.getMessageStringProperty(AsynchAEMessage.MessageFrom);
		String newCASProducedBy = ((AggregateAnalysisEngineController) controller).lookUpDelegateKey(cmEndpointName);
		Endpoint casMultiplierEndpoint = ((AggregateAnalysisEngineController) controller)
				.lookUpEndpoint(newCASProducedBy, false);
		Endpoint freeCasEndpoint = super.getEndpoint();
		// Clone an endpoint where Free Cas Request will be sent
		freeCasEndpoint = (Endpoint) ((Endpoint_impl) freeCasEndpoint).clone();

		if (casMultiplierEndpoint != null) {
			// Save the URL of the broker managing the Free Cas Notification queue.
			// This is needed when we try to establish a connection to the broker.
			freeCasEndpoint.setServerURI(casMultiplierEndpoint.getServerURI());
		}
		CasStateEntry parentCasStateEntry = ((AggregateAnalysisEngineController) controller).getLocalCache()
				.lookupEntry(parentCasReferenceId);

		// Associate Free Cas Notification Endpoint with an input Cas
		parentCasStateEntry.setFreeCasNotificationEndpoint(freeCasEndpoint);
		return parentCasStateEntry;
	}

	protected void computeStats(String aCasReferenceId) throws Exception {
		if (super.propertyExists(AsynchAEMessage.TimeInService)) {
			long departureTime = controller.getTime(aCasReferenceId, super.getEndpoint().getEndpoint());
			long currentTime = System.nanoTime();
			long roundTrip = currentTime - departureTime;
			long timeInService = super.getMessageLongProperty(AsynchAEMessage.TimeInService);
			long totalTimeInComms = currentTime - (departureTime - timeInService);

			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_roundtrip_time__FINE",
						new Object[] { aCasReferenceId, super.getEndpoint(),
								(double) roundTrip / (double) 1000000 });

				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_time_spent_in_delegate__FINE",
						new Object[] { aCasReferenceId, (double) timeInService / (double) 1000000,
								super.getEndpoint() });

				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_time_spent_in_comms__FINE",
						new Object[] { aCasReferenceId, (double) totalTimeInComms / (double) 1000000,
								super.getEndpoint() });
			}
		}
		aggregateDelegateStats( aCasReferenceId);

	}

	protected synchronized void aggregateDelegateStats( String aCasReferenceId)
			throws AsynchAEException {
		String delegateKey = "";
		try {

			delegateKey = ((AggregateAnalysisEngineController) controller)
					.lookUpDelegateKey(super.getEndpoint().getEndpoint());
			CacheEntry entry = controller.getInProcessCache().getCacheEntryForCAS(aCasReferenceId);
			if (entry == null) {
				throw new AsynchAEException("CasReferenceId:" + aCasReferenceId + " Not Found in the Cache.");
			}
			CacheEntry inputCasEntry = null;
			String inputCasReferenceId = entry.getInputCasReferenceId();
			ServicePerformance casStats = ((AggregateAnalysisEngineController) controller)
					.getCasStatistics(aCasReferenceId);
			if (inputCasReferenceId != null && controller.getInProcessCache().entryExists(inputCasReferenceId)) {
				String casProducerKey = entry.getCasProducerKey();
				if (casProducerKey != null
						&& ((AggregateAnalysisEngineController) controller).isDelegateKeyValid(casProducerKey)) {
					// Get entry for the input CAS
					inputCasEntry = controller.getInProcessCache().getCacheEntryForCAS(inputCasReferenceId);
				}

			}
			ServicePerformance delegateServicePerformance = ((AggregateAnalysisEngineController) controller)
					.getServicePerformance(delegateKey);

			if (super.propertyExists(AsynchAEMessage.TimeToSerializeCAS)) {
				long timeToSerializeCAS = super
						.getMessageLongProperty(AsynchAEMessage.TimeToSerializeCAS);
				if (timeToSerializeCAS > 0) {
					if (delegateServicePerformance != null) {
						delegateServicePerformance.incrementCasSerializationTime(timeToSerializeCAS);
					}
				}
			}
			if (super.propertyExists(AsynchAEMessage.TimeToDeserializeCAS)) {
				long timeToDeserializeCAS = super
						.getMessageLongProperty(AsynchAEMessage.TimeToDeserializeCAS);
				if (timeToDeserializeCAS > 0) {
					if (delegateServicePerformance != null) {
						delegateServicePerformance.incrementCasDeserializationTime(timeToDeserializeCAS);
					}
				}
			}

			if (super.propertyExists(AsynchAEMessage.IdleTime)) {
				long idleTime = super.getMessageLongProperty(AsynchAEMessage.IdleTime);
				if (idleTime > 0 && delegateServicePerformance != null) {
					Endpoint endp = super.getEndpoint();
					if (endp != null && endp.isRemote()) {
						delegateServicePerformance.incrementIdleTime(idleTime);
					}
				}
			}

			if (super.propertyExists(AsynchAEMessage.TimeWaitingForCAS)) {
				long timeWaitingForCAS = super
						.getMessageLongProperty(AsynchAEMessage.TimeWaitingForCAS);
				if (timeWaitingForCAS > 0 && super.getEndpoint().isRemote()) {
					entry.incrementTimeWaitingForCAS(timeWaitingForCAS);
					delegateServicePerformance.incrementCasPoolWaitTime(
							timeWaitingForCAS - delegateServicePerformance.getRawCasPoolWaitTime());
					if (inputCasEntry != null) {
						inputCasEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
					}
				}
			}
			if (super.propertyExists(AsynchAEMessage.TimeInProcessCAS)) {
				long timeInProcessCAS = super
						.getMessageLongProperty(AsynchAEMessage.TimeInProcessCAS);
				Endpoint endp = super.getEndpoint();
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

				if (inputCasReferenceId != null) {
					ServicePerformance inputCasStats = ((AggregateAnalysisEngineController) controller)
							.getCasStatistics(inputCasReferenceId);
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
