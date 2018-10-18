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
import org.apache.uima.aae.error.ErrorContext;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.aae.monitor.statistics.LongNumericStatistic;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.Level;

public class ProcessChildCasResponseCommand extends AbstractUimaAsCommand  {
//	private MessageContext mc;

	public ProcessChildCasResponseCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
//		this.mc = mc;
	}
	public void execute() throws Exception {
//		System.out.println(">>>>>>>>>>>>>>> in ProcessChildCasResponseCommand.execute(");
//		int payload = mc.getMessageIntProperty(AsynchAEMessage.Payload);
//		String casReferenceId = super.getCasReferenceId(this.getClass(), mc);
//		System.out.println(">>>>>>>>>>>>>>> Controller:"+controller.getComponentName()+
//				" in ProcessChildCasResponseCommand.execute() - Child CAS:"+casReferenceId+
//				" from "+mc
//                .getMessageStringProperty(AsynchAEMessage.MessageFrom)
//				);
//
//		if (casReferenceId == null) {
//			// LOG THIS
//			System.out.println(
//					"ProcessInputCasResponseCommand.execute() - CasReferenceId is missing in MessageContext - Ignoring Message");
//			return; // Nothing to do
//		}
//		// Save Process command in the client endpoint.
//		Endpoint clientEndpoint = controller.getClientEndpoint();
//		if (clientEndpoint != null) {
//			clientEndpoint.setCommand(AsynchAEMessage.Process);
//		}
//
//		// Cas was passed by reference meaning InProcessCache must have it
//		if (AsynchAEMessage.CASRefID == payload) {
//			executeDirectRequest(casReferenceId);
//		} else {
//			executeRemoteRequest(casReferenceId);
//		}
	}
	private void executeDirectRequest(String casReferenceId) throws AsynchAEException {
	    CacheEntry cacheEntry = null;

	    try {
	      casReferenceId = super.getMessageStringProperty(AsynchAEMessage.CasReference);
	      cacheEntry = controller.getInProcessCache().getCacheEntryForCAS(casReferenceId);
	      CasStateEntry casStateEntry = ((AggregateAnalysisEngineController)controller)
	              .getLocalCache().lookupEntry(casReferenceId);

	      CAS cas = cacheEntry.getCas();
	      String delegateKey = null;
	      if ( super.getEndpoint().getEndpoint() == null || super.getEndpoint().getEndpoint().trim().length()==0) {
	    	  String fromEndpoint = super
	                  .getMessageStringProperty(AsynchAEMessage.MessageFrom);
	    	  delegateKey = ((AggregateAnalysisEngineController) controller)
	                  .lookUpDelegateKey(fromEndpoint);
	      } else {
	          delegateKey = ((AggregateAnalysisEngineController) controller)
	                  .lookUpDelegateKey(super.getEndpoint().getEndpoint());
	      }
	      Delegate delegate = ((AggregateAnalysisEngineController) controller)
	              .lookupDelegate(delegateKey);
	      if (casStateEntry != null) {
	        casStateEntry.setReplyReceived();
	        casStateEntry.setLastDelegate(delegate);
	      }
	      delegate.removeCasFromOutstandingList(casReferenceId);

	      if (cas != null) {
	        cancelTimerAndProcess(casReferenceId, cas);
	      } else {
	        if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
	          UIMAFramework.getLogger(getClass()).logrb(
	                  Level.INFO,
	                  getClass().getName(),
	                  "handleProcessResponseWithCASReference",
	                  UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                  "UIMAEE_cas_not_in_cache__INFO",
	                  new Object[] { controller.getName(), casReferenceId,
	                		  super.getEndpoint().getEndpoint() });
	        }
	        throw new AsynchAEException("CAS with Reference Id:" + casReferenceId
	                + " Not Found in CasManager's CAS Cache");
	      }
	    } catch (Exception e) {

	      ErrorContext errorContext = new ErrorContext();
	      errorContext.add(AsynchAEMessage.Command, AsynchAEMessage.Process);
	      errorContext.add(AsynchAEMessage.CasReference, casReferenceId);
	      errorContext.add(AsynchAEMessage.Endpoint, super.getEndpoint());
	      controller.getErrorHandlerChain().handle(e, errorContext, controller);
	    } finally {
	      incrementDelegateProcessCount();
	      if (controller instanceof AggregateAnalysisEngineController) {
	        try {
	          String endpointName = super.getEndpoint().getEndpoint();
	          String delegateKey = ((AggregateAnalysisEngineController) controller)
	                  .lookUpDelegateKey(endpointName);
	          if (delegateKey != null) {
	            Endpoint endpoint = ((AggregateAnalysisEngineController) controller)
	                    .lookUpEndpoint(delegateKey, false);

	            // Check if the multiplier aborted during processing of this input CAS
	            if (endpoint != null && endpoint.isCasMultiplier() && cacheEntry.isAborted()) {
	              if (!controller.getInProcessCache().isEmpty()) {
	                controller.getInProcessCache().registerCallbackWhenCacheEmpty(
	                       controller.getEventListener());
	              } else {
	                // Callback to notify that the cache is empty
	            	  
	            	  // !!!!!!!!!!!!!!! WHY DO WE NEED TO CALL onCacheEmpty() IF CAS IS ABORTED?
	            	  // !!!!!!!!!!!!!!!!!!!!!! ?????????????????????????????????
//	                getController().getEventListener().onCacheEmpty();
	              }
	            }

	          }
	        } catch (Exception e) {
	          if (UIMAFramework.getLogger(getClass()).isLoggable(Level.WARNING)) {
	              UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(),
	                      "handleProcessResponseWithCASReference", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                      "UIMAEE_service_exception_WARNING", controller.getComponentName());

	            UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(),
	                    "handleProcessResponseWithCASReference", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                    "UIMAEE_exception__WARNING", e);
	          }
	        }
	      }
	    }

	}
	 private void incrementDelegateProcessCount() {
		    Endpoint endpoint = super.getEndpoint();
		    if (endpoint != null && controller instanceof AggregateAnalysisEngineController) {
		      try {
		        String delegateKey = ((AggregateAnalysisEngineController) controller)
		                .lookUpDelegateKey(endpoint.getEndpoint());
		        LongNumericStatistic stat = controller.getMonitor().getLongNumericStatistic(
		                delegateKey, Monitor.ProcessCount);
		        stat.increment();
		      } catch (Exception e) {
		        if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
		            UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(),
		                    "incrementDelegateProcessCount", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
		                    "UIMAEE_delegate_key_for_endpoint_not_found__INFO", new Object[] { controller.getComponentName(), endpoint.getEndpoint() });
		        }
		      }
		    }

		  }
	private void executeRemoteRequest(String casReferenceId) throws AsynchAEException {

	}
    private void cancelTimerAndProcess( String aCasReferenceId,
           CAS aCAS) throws AsynchAEException {
//      computeStats(aMessageContext, aCasReferenceId);
//      ((AggregateAnalysisEngineController) controller).process(aCAS, anInputCasReferenceId,
//              aNewCasReferenceId, aNewCasProducedBy);
//
//      super.invokeProcess(aCAS, aCasReferenceId, null, aMessageContext, null);

    }
	  protected void computeStats(MessageContext aMessageContext, String aCasReferenceId)
	          throws AsynchAEException {
	    if (aMessageContext.propertyExists(AsynchAEMessage.TimeInService)) {
	      long departureTime = controller.getTime(aCasReferenceId,
	              aMessageContext.getEndpoint().getEndpoint());
	      long currentTime = System.nanoTime();
	      long roundTrip = currentTime - departureTime;
	      long timeInService = aMessageContext.getMessageLongProperty(AsynchAEMessage.TimeInService);
	      long totalTimeInComms = currentTime - (departureTime - timeInService);

	      if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
	        UIMAFramework.getLogger(getClass()).logrb(
	                Level.FINE,
	                getClass().getName(),
	                "computeStats",
	                UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_show_roundtrip_time__FINE",
	                new Object[] { aCasReferenceId, aMessageContext.getEndpoint(),
	                    (double) roundTrip / (double) 1000000 });

	        UIMAFramework.getLogger(getClass()).logrb(
	                Level.FINE,
	                getClass().getName(),
	                "computeStats",
	                UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_show_time_spent_in_delegate__FINE",
	                new Object[] { aCasReferenceId, (double) timeInService / (double) 1000000,
	                    aMessageContext.getEndpoint() });

	        UIMAFramework.getLogger(getClass()).logrb(
	                Level.FINE,
	                getClass().getName(),
	                "computeStats",
	                UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_show_time_spent_in_comms__FINE",
	                new Object[] { aCasReferenceId, (double) totalTimeInComms / (double) 1000000,
	                    aMessageContext.getEndpoint() });
	      }
	    }

	    if (controller instanceof AggregateAnalysisEngineController) {
	      aggregateDelegateStats(aMessageContext, aCasReferenceId);
	    }
	  }
	  protected synchronized void aggregateDelegateStats(MessageContext aMessageContext,
	          String aCasReferenceId) throws AsynchAEException {
	    String delegateKey = "";
	    try {

	    	
	        if ( aMessageContext.getEndpoint().getEndpoint() == null || aMessageContext.getEndpoint().getEndpoint().trim().length()==0) {
	      	  String fromEndpoint = aMessageContext
	                    .getMessageStringProperty(AsynchAEMessage.MessageFrom);
	      	  delegateKey = ((AggregateAnalysisEngineController) controller)
	                    .lookUpDelegateKey(fromEndpoint);
	        } else {
	            delegateKey = ((AggregateAnalysisEngineController) controller)
	                    .lookUpDelegateKey(aMessageContext.getEndpoint().getEndpoint());
	        }
	 //     delegateKey = ((AggregateAnalysisEngineController) getController())
	   //           .lookUpDelegateKey(aMessageContext.getEndpoint().getEndpoint());
	      CacheEntry entry = controller.getInProcessCache().getCacheEntryForCAS(aCasReferenceId);
	      if (entry == null) {
	        throw new AsynchAEException("CasReferenceId:" + aCasReferenceId
	                + " Not Found in the Cache.");
	      }
	      CacheEntry inputCasEntry = null;
	      String inputCasReferenceId = entry.getInputCasReferenceId();
	      ServicePerformance casStats = ((AggregateAnalysisEngineController) controller)
	              .getCasStatistics(aCasReferenceId);
	      if (inputCasReferenceId != null
	              && controller.getInProcessCache().entryExists(inputCasReferenceId)) {
	        String casProducerKey = entry.getCasProducerKey();
	        if (casProducerKey != null
	                && ((AggregateAnalysisEngineController) controller)
	                        .isDelegateKeyValid(casProducerKey)) {
	          // Get entry for the input CAS
	          inputCasEntry = controller.getInProcessCache().getCacheEntryForCAS(
	                  inputCasReferenceId);
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
	        long idleTime = ((Long) aMessageContext.getMessageLongProperty(AsynchAEMessage.IdleTime))
	                .longValue();
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
	          entry.incrementTimeWaitingForCAS(timeWaitingForCAS);
	          delegateServicePerformance.incrementCasPoolWaitTime(timeWaitingForCAS
	                  - delegateServicePerformance.getRawCasPoolWaitTime());
	          if (inputCasEntry != null) {
	            inputCasEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
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
