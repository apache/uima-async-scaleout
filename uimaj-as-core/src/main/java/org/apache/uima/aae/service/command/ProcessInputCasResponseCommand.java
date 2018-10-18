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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UIMARuntimeException;
import org.apache.uima.aae.InProcessCache.CacheEntry;
import org.apache.uima.aae.SerializerCache;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.UimaSerializer;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.delegate.Delegate;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.ErrorContext;
import org.apache.uima.aae.error.ServiceShutdownException;
import org.apache.uima.aae.error.UimaAsDelegateException;
import org.apache.uima.aae.error.UimaEEServiceException;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.aae.monitor.statistics.AnalysisEnginePerformanceMetrics;
import org.apache.uima.aae.monitor.statistics.LongNumericStatistic;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.cas.impl.AllowPreexistingFS;
import org.apache.uima.cas.impl.BinaryCasSerDes6;
import org.apache.uima.cas.impl.BinaryCasSerDes6.ReuseInfo;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.MarkerImpl;
import org.apache.uima.cas.impl.Serialization;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.cas.impl.XmiSerializationSharedData;
import org.apache.uima.util.Level;

public class ProcessInputCasResponseCommand  extends AbstractUimaAsCommand {

	//private MessageContext mc;

	public ProcessInputCasResponseCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
		//this.mc = mc;
	}

	public void execute() throws Exception {

		int payload = super.getMessageIntProperty(AsynchAEMessage.Payload);
		String casReferenceId = super.getCasReferenceId(this.getClass());
		String msgFrom = super.getMessageStringProperty(AsynchAEMessage.MessageFrom);

		System.out.println(">>>>>>>>>>>>>>> Controller:" + controller.getComponentName()
				+ " in ProcessInputCasResponseCommand.execute() - Input CAS:" + casReferenceId + " from "
				+ super.getMessageStringProperty(AsynchAEMessage.MessageFrom)+" Payload:"+payload);

		if (casReferenceId == null) {
			// LOG THIS
			System.out.println(
					"ProcessInputCasResponseCommand.execute() - CasReferenceId is missing in MessageContext - Ignoring Message");
			return; // Nothing to do
		}
		
		Delegate delegate =
				((AggregateAnalysisEngineController) controller).lookupDelegate(msgFrom);
		String key=delegate.getKey();
		// Cas was passed by reference meaning InProcessCache must have it

		if ( collocatedProcessReply(payload) ) {
			executeDirectRequest(casReferenceId);
			if (key != null) {
		          resetErrorCounts(key);
		    }
		}  else if ( remoteProcessReply(payload) ) {
			executeRemoteRequest(casReferenceId);
		} else if (failedProcessReply(payload)) {
	        if (key == null) {
	            key = ((Endpoint) super.getEndpoint()).getEndpoint();
	          }
	          handleProcessResponseWithException(key);
	        } else {
	          throw new AsynchAEException("Invalid Payload. Expected XMI or CasReferenceId Instead Got::"
	                  + payload);
	        }

	}
	private boolean failedProcessReply(int payload) {
		return (AsynchAEMessage.Exception == payload);
	}

	private boolean collocatedProcessReply(int payload) {
		return (AsynchAEMessage.CASRefID == payload);
	}
	private boolean remoteProcessReply(int payload) {
		return (AsynchAEMessage.XMIPayload == payload || AsynchAEMessage.BinaryPayload == payload);
	}
	private Object getCause() throws Exception {
		Object object = null;
		
		if ( ( object = super.getObjectMessage() ) == null ) {
	        // Could be a C++ exception. In this case the exception is just a String in the message
	        // cargo
	        if (super.getStringMessage() != null) {
	          object = new UimaEEServiceException(super.getStringMessage());
	        } else {
	        	object = super.getMessageObjectProperty(AsynchAEMessage.ErrorCause);
	        }
		}

      return object;
	}
	  private synchronized void handleProcessResponseWithException( String delegateKey) {
	    if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
	      UIMAFramework.getLogger(getClass())
	              .logrb(
	                      Level.FINE,
	                      getClass().getName(),
	                      "handleProcessResponseWithException",
	                      UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                      "UIMAEE_handling_exception_from_delegate_FINE",
	                      new Object[] { controller.getName(),
	                    		  super.getEndpoint().getEndpoint() });
	    }
	    boolean isCpCError = false;
	    String casReferenceId = null;

	    try {
	      // If a Process Request, increment number of docs processed
	      if (super.getMessageIntProperty(AsynchAEMessage.MessageType) == AsynchAEMessage.Response
	              && super.getMessageIntProperty(AsynchAEMessage.Command) == AsynchAEMessage.Process) {
	        // Increment number of CASes processed by a delegate
	        incrementDelegateProcessCount( );
	      }
	      Object object = getCause();
	      if (ignoreException(object)) {
	        return;
	      }

	      if (controller instanceof AggregateAnalysisEngineController
	              && super.propertyExists(AsynchAEMessage.Command)
	              && super.getMessageIntProperty(AsynchAEMessage.Command) == AsynchAEMessage.CollectionProcessComplete) {
	        isCpCError = true;
	        ((AggregateAnalysisEngineController) controller)
	                .processCollectionCompleteReplyFromDelegate(delegateKey, false);
	      } else {
	        casReferenceId = super.getMessageStringProperty(AsynchAEMessage.CasReference);
	      }

	      if (object != null && (object instanceof Exception || object instanceof Throwable)) {
	        String casid_msg = (casReferenceId == null) ? "" : " on CAS:" + casReferenceId;
	        String controllerName = "/" + controller.getComponentName();
	        if (!controller.isTopLevelComponent()) {
	          controllerName += ((BaseAnalysisEngineController) controller.getParentController())
	                  .getUimaContextAdmin().getQualifiedContextName();
	        }
	        Exception remoteException = new UimaAsDelegateException("----> Controller:"
	                + controllerName + " Received Exception " + casid_msg + " From Delegate:"
	                + delegateKey, (Exception) object);
	        ErrorContext errorContext = new ErrorContext();
	        errorContext.add(AsynchAEMessage.Command, super
	                .getMessageIntProperty(AsynchAEMessage.Command));
	        errorContext.add(AsynchAEMessage.MessageType, super
	                .getMessageIntProperty(AsynchAEMessage.MessageType));
	        if (!isCpCError) {
	          errorContext.add(AsynchAEMessage.CasReference, casReferenceId);
	        }
	        errorContext.add(AsynchAEMessage.Endpoint, super.getEndpoint());
	        controller.getErrorHandlerChain().handle(remoteException, errorContext,
	        		controller);
	      }
	    } catch (Exception e) {
	      ErrorContext errorContext = new ErrorContext();
	      errorContext.add(AsynchAEMessage.Command, AsynchAEMessage.Process);
	      errorContext.add(AsynchAEMessage.CasReference, casReferenceId);
	      errorContext.add(AsynchAEMessage.Endpoint, super.getEndpoint());
	      controller.getErrorHandlerChain().handle(e, errorContext, controller);
	    }

	  }
	  private boolean isException(Object object) {
		    return (object instanceof Exception || object instanceof Throwable);
		  }
	  private boolean isShutdownException(Object object) {
		    return (object instanceof Exception && object instanceof UimaEEServiceException
		            && ((UimaEEServiceException) object).getCause() != null && ((UimaEEServiceException) object)
		            .getCause() instanceof ServiceShutdownException);
		  }
	  private boolean ignoreException(Object object) {
		    if (object != null && isException(object) && !isShutdownException(object)) {
		      return false;
		    }
		    return true;
		  }


	  private void resetErrorCounts(String aDelegate) {
		    controller.getMonitor().resetCountingStatistic(aDelegate, Monitor.ProcessErrorCount);
		    controller.getMonitor().resetCountingStatistic(aDelegate, Monitor.ProcessErrorRetryCount);
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
			casReferenceId = super.getMessageStringProperty(AsynchAEMessage.CasReference);
			// find an entry for a given cas id in a global cache
			cacheEntry = super.getCacheEntryForCas(casReferenceId);
			if (cacheEntry == null) {
				throw new AsynchAEException("CasReferenceId:" + casReferenceId + " Not Found in the Cache.");
			}

			// find an entry for a given cas id in this aggregate's local cache
			CasStateEntry casStateEntry = controller.getLocalCache().lookupEntry(casReferenceId);
			// find delegate which sent the reply
			delegate = super.getDelegate();
			if (casStateEntry != null) {
				casStateEntry.setReplyReceived();
				casStateEntry.setLastDelegate(delegate);
			}
			// each delegate object manages a list of CAS ids that were
			// dispatched. Every time a CAS reply is received, we remove
			// its CAS id from the outstanding list.
			delegate.removeCasFromOutstandingList(casReferenceId);

			if (cacheEntry.getCas() != null) {
				computeStats(cacheEntry);
				((AggregateAnalysisEngineController) controller).process(cacheEntry.getCas(), casReferenceId);

			} else {
				if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(), "executeDirectRequest",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_cas_not_in_cache__INFO",
							new Object[] { controller.getName(), casReferenceId, super.getEndpoint().getEndpoint() });
				}
				throw new AsynchAEException(
						"CAS with Reference Id:" + casReferenceId + " Not Found in CasManager's CAS Cache");
			}
		} catch (Exception e) {
			super.handleError(e, cacheEntry);
		} finally {
			incrementDelegateProcessCount();
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
	 private Endpoint lookupEndpoint(String anEndpointName, String aCasReferenceId) {
		    return controller.getInProcessCache().getEndpoint(anEndpointName, aCasReferenceId);
		  }
	private void executeRemoteRequest(String casReferenceId) {
		System.out.println("Called empty executeRemoteRequest()");
		
		   CAS cas = null;
		    //String casReferenceId = null;
		    Endpoint endpointWithTimer = null;
		    try {
		      final int payload = super.getMessageIntProperty(AsynchAEMessage.Payload);
		      //casReferenceId = aMessageContext.getMessageStringProperty(AsynchAEMessage.CasReference);
		      endpointWithTimer = lookupEndpoint(super.getEndpoint().getEndpoint(),
		              casReferenceId);

		      if (endpointWithTimer == null) {
		        if (UIMAFramework.getLogger(getClass()).isLoggable(Level.WARNING)) {
		          UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(),
		                  "handleProcessResponseFromRemote", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
		                  "UIMAEE_invalid_endpoint__WARNING",
		                  new Object[] { super.getEndpoint().getEndpoint(), casReferenceId });
		        }
		        return;
		      }
		      String delegateKey = ((AggregateAnalysisEngineController) controller)
		              .lookUpDelegateKey(super.getEndpoint().getEndpoint());
		      Delegate delegate = ((AggregateAnalysisEngineController) controller)
		              .lookupDelegate(delegateKey);
		      boolean casRemovedFromOutstandingList = delegate.removeCasFromOutstandingList(casReferenceId);

		      // Check if this process reply message is expected. A message is expected if the Cas Id
		      // in the message matches an entry in the delegate's outstanding list. This list stores
		      // ids of CASes sent to the remote delegate pending reply.
		      if (!casRemovedFromOutstandingList) {
		        //handleUnexpectedMessage(casReferenceId, aMessageContext.getEndpoint());
		        return;   // out of band reply. Most likely the CAS previously timedout
		      }

		      // Increment number of CASes processed by this delegate
		      if (delegateKey != null) {
		        ServicePerformance delegateServicePerformance = ((AggregateAnalysisEngineController) controller)
		                .getServicePerformance(delegateKey);
		        if (delegateServicePerformance != null) {
		          delegateServicePerformance.incrementNumberOfCASesProcessed();
		        }
		      }

		      String xmi = super.getStringMessage();

		      // Fetch entry from the cache for a given Cas Id. The entry contains a CAS that will be used
		      // during deserialization
		      CacheEntry cacheEntry = controller.getInProcessCache().getCacheEntryForCAS(
		              casReferenceId);
		      // check if the client requested Performance Metrics for the CAS
		      if ( super.propertyExists(AsynchAEMessage.CASPerComponentMetrics) ) {
		        try {
		          // find top ancestor of this CAS. All metrics are accumulated there since
		          // this is what will be returned to the client
		          CacheEntry ancestor = 
		                  controller.
		                    getInProcessCache().
		                      getTopAncestorCasEntry(cacheEntry);
		          if ( ancestor != null ) {
		        	// fetch Performance Metrics from remote delegate reply
		            List<AnalysisEnginePerformanceMetrics> metrics = 
		                    UimaSerializer.deserializePerformanceMetrics(super.getMessageStringProperty(AsynchAEMessage.CASPerComponentMetrics));
		            List<AnalysisEnginePerformanceMetrics> adjustedMetrics =
		                    new ArrayList<AnalysisEnginePerformanceMetrics>();
		            for(AnalysisEnginePerformanceMetrics delegateMetric : metrics ) {
		              String adjustedUniqueName = ((AggregateAnalysisEngineController) controller).getJmxContext();

		              if ( adjustedUniqueName.startsWith("p0=")) {
		            	  adjustedUniqueName = adjustedUniqueName.substring(3);  // skip p0=
		              }
		              adjustedUniqueName = adjustedUniqueName.replaceAll(" Components", "");
		              if (!adjustedUniqueName.startsWith("/")) {
		            	  adjustedUniqueName = "/"+adjustedUniqueName;
		              }
		              adjustedUniqueName += delegateMetric.getUniqueName();
		              
		              boolean found = false;
		              AnalysisEnginePerformanceMetrics metric = null;
		              for( AnalysisEnginePerformanceMetrics met : ancestor.getDelegateMetrics() ) {
		            	  if ( met.getUniqueName().equals(adjustedUniqueName)) {
		            		  long at = delegateMetric.getAnalysisTime();
		            		  long count = delegateMetric.getNumProcessed();
		            		  metric = new AnalysisEnginePerformanceMetrics(delegateMetric.getName(),adjustedUniqueName,at,count);
		            		  found = true;
		            		  ancestor.getDelegateMetrics().remove(met);
		            		  break;
		            	  }
		              }
		              if ( !found ) {
		                  metric = new AnalysisEnginePerformanceMetrics(delegateMetric.getName(),adjustedUniqueName,delegateMetric.getAnalysisTime(),delegateMetric.getNumProcessed());
		              }
		              adjustedMetrics.add(metric);
		            }
		            
		            ancestor.addDelegateMetrics(delegateKey, adjustedMetrics, true);  // true=remote
		          }
		        } catch (Exception e) {
		          // An exception be be thrown here if the service is being stopped.
		          // The top level controller may have already cleaned up the cache
		          // and the getCacheEntryForCAS() will throw an exception. Ignore it
		          // here, we are shutting down.
		        }
		        
		      }
		      CasStateEntry casStateEntry = ((AggregateAnalysisEngineController) controller)
		              .getLocalCache().lookupEntry(casReferenceId);
		      if (casStateEntry != null) {
		        casStateEntry.setReplyReceived();
		        // Set the key of the delegate that returned the CAS
		        casStateEntry.setLastDelegate(delegate);
		      } else {
		        return; // Cache Entry Not found
		      }

		      cas = cacheEntry.getCas();
		      int totalNumberOfParallelDelegatesProcessingCas = casStateEntry
		              .getNumberOfParallelDelegates();
		      if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
		        UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(),
		                "handleProcessResponseFromRemote", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
		                "UIMAEE_number_parallel_delegates_FINE",
		                new Object[] { totalNumberOfParallelDelegatesProcessingCas, Thread.currentThread().getId(), Thread.currentThread().getName() });
		      }
		      if (totalNumberOfParallelDelegatesProcessingCas > 1) {
		    	  // Block this thread until CAS is dispatched to all delegates in parallel step. Fixes race condition where
		    	  // a reply comes from one of delegates in parallel step before dispatch sequence completes. Without
		    	  // this blocking the result of analysis are merged into a CAS.
		    	  casStateEntry.blockIfParallelDispatchNotComplete();
		      }
		      
		      if (cas == null) {
		        throw new AsynchAEException(Thread.currentThread().getName()
		                + "-Cache Does not contain a CAS. Cas Reference Id::" + casReferenceId);
		      }
		      if (UIMAFramework.getLogger().isLoggable(Level.FINEST)) {
		        UIMAFramework.getLogger(getClass()).logrb(Level.FINEST, getClass().getName(),
		                "handleProcessResponseFromRemote", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
		                "UIMAEE_rcvd_reply_FINEST",
		                new Object[] { super.getEndpoint().getEndpoint(), casReferenceId, xmi });
		      }
		      long t1 = controller.getCpuTime();
		      /* --------------------- */
		      /** DESERIALIZE THE CAS. */
		      /* --------------------- */
		      //all subsequent serialization must be complete CAS.
		      if ( !super.getMessageBooleanProperty(AsynchAEMessage.SentDeltaCas))  {
		    	cacheEntry.setAcceptsDeltaCas(false);
		      }

		      SerialFormat serialFormat = endpointWithTimer.getSerialFormat();
		      // check if the CAS is part of the Parallel Step
		      if (totalNumberOfParallelDelegatesProcessingCas > 1) {
		        // Synchronized because replies are merged into the same CAS.
		        synchronized (cas) {
		          if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINEST)) {
		            UIMAFramework.getLogger(getClass()).logrb(Level.FINEST, getClass().getName(),
		                    "handleProcessResponseFromRemote", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
		                    "UIMAEE_delegate_responded_count_FINEST",
		                    new Object[] { casStateEntry.howManyDelegatesResponded(), casReferenceId });
		          }
		          // If a delta CAS, merge it while checking that no pre-existing FSs are modified.
		          if (super.getMessageBooleanProperty(AsynchAEMessage.SentDeltaCas)) {
		            switch (serialFormat) {
		            case XMI:
		              int highWaterMark = cacheEntry.getHighWaterMark();
		              deserialize(xmi, cas, casReferenceId, highWaterMark, AllowPreexistingFS.disallow);
		              break;
		            case COMPRESSED_FILTERED:
		              deserialize(super.getByteMessage(), cas, cacheEntry, endpointWithTimer.getTypeSystemImpl(), AllowPreexistingFS.disallow);
		              break;
		            default:
		              throw new UIMARuntimeException(new Exception("Internal error"));
		            }
		          } else {
		            // If not a delta CAS (old service), take all of first reply, and merge in the new
		            // entries in the later replies. Ignoring pre-existing FS for 2.2.2 compatibility
		            // Note: can't be a compressed binary - that would have returned a delta
		            if (casStateEntry.howManyDelegatesResponded() == 0) {
		              deserialize(xmi, cas, casReferenceId);
		            } else { // process secondary reply from a parallel step
		              int highWaterMark = cacheEntry.getHighWaterMark();
		              deserialize(xmi, cas, casReferenceId, highWaterMark, AllowPreexistingFS.ignore);
		            }
		          }
		          casStateEntry.incrementHowManyDelegatesResponded();
		        }
		      } else { // Processing a reply from a non-parallel delegate (binary or delta xmi or xmi)
		        byte[] binaryData = super.getByteMessage();
		        ByteArrayInputStream istream = new ByteArrayInputStream(binaryData);
		        switch (serialFormat) {
		        case BINARY:
		          ((CASImpl)cas).reinit(istream);
		          break;
		        case COMPRESSED_FILTERED:
		          BinaryCasSerDes6 bcs = new BinaryCasSerDes6(cas, (MarkerImpl) cacheEntry.getMarker(), endpointWithTimer.getTypeSystemImpl(), cacheEntry.getCompress6ReuseInfo());          
		          bcs.deserialize(istream, AllowPreexistingFS.allow);
		          break;
		        case XMI:
		          if (super.getMessageBooleanProperty(AsynchAEMessage.SentDeltaCas)) {
		            int highWaterMark = cacheEntry.getHighWaterMark();
		            deserialize(xmi, cas, casReferenceId, highWaterMark, AllowPreexistingFS.allow);
		          } else {
		            deserialize(xmi, cas, casReferenceId);
		          }
		          break;
		        default:
		          throw new UIMARuntimeException(new Exception("Internal error"));
		        }
		      }
		      long timeToDeserializeCAS = controller.getCpuTime() - t1;

		      controller.getServicePerformance().incrementCasDeserializationTime(timeToDeserializeCAS);

		      ServicePerformance casStats = controller.getCasStatistics(casReferenceId);
		      casStats.incrementCasDeserializationTime(timeToDeserializeCAS);
		      LongNumericStatistic statistic;
		      if ((statistic = controller.getMonitor().getLongNumericStatistic("",
		              Monitor.TotalDeserializeTime)) != null) {
		        statistic.increment(timeToDeserializeCAS);
		      }

		      computeStats( casReferenceId);

		      // Send CAS for processing when all delegates reply
		      // totalNumberOfParallelDelegatesProcessingCas indicates how many delegates are processing CAS
		      // in parallel. Default is 1, meaning only one delegate processes the CAS at the same.
		      // Otherwise, check if all delegates responded before passing CAS on to the Flow Controller.
		      // The idea is that all delegates processing one CAS concurrently must respond, before the CAS
		      // is allowed to move on to the next step.
		      // HowManyDelegatesResponded is incremented every time a parallel delegate sends response.
		      if (totalNumberOfParallelDelegatesProcessingCas == 1
		              || receivedAllResponsesFromParallelDelegates(casStateEntry,
		                      totalNumberOfParallelDelegatesProcessingCas)) {
		        invokeProcess(cas, casReferenceId, null, null);
		      }

		    } catch (Exception e) {
		      // Check if the exception was thrown by the Cache while looking up
		      // the CAS. It may be the case if in the parallel step one thread
		      // drops the CAS in the Error Handling while another thread processes
		      // reply from another delegate in the Parallel Step. A race condition
		      // may occur here. If one thread drops the CAS due to excessive exceptions
		      // and Flow Controller is configured to drop the CAS, the other thread
		      // should not be allowed to move the CAS to process()method. The second
		      // thread will find the CAS missing in the cache and the logic below
		      // just logs the stale CAS and returns and doesnt attempt to handle
		      // the missing CAS exception.
		      if (e instanceof AsynchAEException && e.getMessage() != null
		              && e.getMessage().startsWith("Cas Not Found")) {
		        String key = "N/A";
		        if (endpointWithTimer != null) {
		          key = ((AggregateAnalysisEngineController) controller)
		                  .lookUpDelegateKey(endpointWithTimer.getEndpoint());
		        }
		        if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
		          UIMAFramework.getLogger(getClass()).logrb(Level.FINEST, getClass().getName(),
		                  "handleProcessResponseFromRemote", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
		                  "UIMAEE_stale_reply__INFO",
		                  new Object[] { controller.getComponentName(), key, casReferenceId });
		        }
		        // The reply came late. The CAS was removed from the cache.
		        return;
		      }
		      ErrorContext errorContext = new ErrorContext();
		      errorContext.add(AsynchAEMessage.Command, AsynchAEMessage.Process);
		      errorContext.add(AsynchAEMessage.CasReference, casReferenceId);
		      errorContext.add(AsynchAEMessage.Endpoint, super.getEndpoint());
		      controller.getErrorHandlerChain().handle(e, errorContext, controller);
		    } finally {
		      incrementDelegateProcessCount();
		    }
		
		
		
		
		
	}
	  public void invokeProcess(CAS aCAS, String anInputCasReferenceId, String aNewCasReferenceId,
	           String aNewCasProducedBy) throws AsynchAEException {
	    try {
	      // Use empty string as key. Top level component stats are stored under this key.
	      controller.getMonitor().incrementCount("", Monitor.ProcessCount);

	      if (controller instanceof AggregateAnalysisEngineController) {
	        boolean isNewCAS = super.propertyExists(AsynchAEMessage.CasSequence);
	        if (isNewCAS) {
	          ((AggregateAnalysisEngineController) controller).process(aCAS, anInputCasReferenceId,
	                  aNewCasReferenceId, aNewCasProducedBy);
	        } else {
	          ((AggregateAnalysisEngineController) controller).process(aCAS, anInputCasReferenceId);
	        }
	      } else if (controller instanceof PrimitiveAnalysisEngineController) {
	        ((PrimitiveAnalysisEngineController) controller).process(aCAS, anInputCasReferenceId,
	        		super.getEndpoint());
	      } else {
	        throw new AsynchAEException(
	                "Invalid Controller. Expected AggregateController or PrimitiveController. Got:"
	                        + controller.getClass().getName());
	      }
	    } catch (AsynchAEException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new AsynchAEException(e);
	    }

	  }
	  protected void computeStats(String aCasReferenceId)
	          throws Exception {
	    if (super.propertyExists(AsynchAEMessage.TimeInService)) {
	      long departureTime = controller.getTime(aCasReferenceId,
	    		  super.getEndpoint().getEndpoint());
	      long currentTime = System.nanoTime();
	      long roundTrip = currentTime - departureTime;
	      long timeInService = super.getMessageLongProperty(AsynchAEMessage.TimeInService);
	      long totalTimeInComms = currentTime - (departureTime - timeInService);

	      if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
	        UIMAFramework.getLogger(getClass()).logrb( Level.FINE, getClass().getName(),
	                "computeStats",  UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_show_roundtrip_time__FINE",
	                new Object[] { aCasReferenceId, super.getEndpoint(),
	                    (double) roundTrip / (double) 1000000 });

	        UIMAFramework.getLogger(getClass()).logrb(
	                Level.FINE,
	                getClass().getName(),
	                "computeStats",
	                UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_show_time_spent_in_delegate__FINE",
	                new Object[] { aCasReferenceId, (double) timeInService / (double) 1000000,
	                		super.getEndpoint() });

	        UIMAFramework.getLogger(getClass()).logrb(
	                Level.FINE,
	                getClass().getName(),
	                "computeStats",
	                UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                "UIMAEE_show_time_spent_in_comms__FINE",
	                new Object[] { aCasReferenceId, (double) totalTimeInComms / (double) 1000000,
	                		super.getEndpoint() });
	      }
	    }

	    if (controller instanceof AggregateAnalysisEngineController) {
	      aggregateDelegateStats(aCasReferenceId);
	    }
	  }
	  protected synchronized void aggregateDelegateStats(String aCasReferenceId) throws AsynchAEException {
	    String delegateKey = "";
	    try {

	    	
	        if ( super.getEndpoint().getEndpoint() == null || super.getEndpoint().getEndpoint().trim().length()==0) {
	      	  String fromEndpoint = super
	                    .getMessageStringProperty(AsynchAEMessage.MessageFrom);
	      	  delegateKey = ((AggregateAnalysisEngineController) controller)
	                    .lookUpDelegateKey(fromEndpoint);
	        } else {
	            delegateKey = ((AggregateAnalysisEngineController) controller)
	                    .lookUpDelegateKey(super.getEndpoint().getEndpoint());
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

	      if (super.propertyExists(AsynchAEMessage.TimeToSerializeCAS)) {
	        long timeToSerializeCAS = ((Long) super
	                .getMessageLongProperty(AsynchAEMessage.TimeToSerializeCAS)).longValue();
	        if (timeToSerializeCAS > 0) {
	          if (delegateServicePerformance != null) {
	            delegateServicePerformance.incrementCasSerializationTime(timeToSerializeCAS);
	          }
	        }
	      }
	      if (super.propertyExists(AsynchAEMessage.TimeToDeserializeCAS)) {
	        long timeToDeserializeCAS = ((Long) super
	                .getMessageLongProperty(AsynchAEMessage.TimeToDeserializeCAS)).longValue();
	        if (timeToDeserializeCAS > 0) {
	          if (delegateServicePerformance != null) {
	            delegateServicePerformance.incrementCasDeserializationTime(timeToDeserializeCAS);
	          }
	        }
	      }

	      if (super.propertyExists(AsynchAEMessage.IdleTime)) {
	        long idleTime = ((Long) super.getMessageLongProperty(AsynchAEMessage.IdleTime))
	                .longValue();
	        if (idleTime > 0 && delegateServicePerformance != null) {
	          Endpoint endp = super.getEndpoint();
	          if (endp != null && endp.isRemote()) {
	            delegateServicePerformance.incrementIdleTime(idleTime);
	          }
	        }
	      }

	      if (super.propertyExists(AsynchAEMessage.TimeWaitingForCAS)) {
	        long timeWaitingForCAS = ((Long) super
	                .getMessageLongProperty(AsynchAEMessage.TimeWaitingForCAS)).longValue();
	        if (timeWaitingForCAS > 0 && super.getEndpoint().isRemote()) {
	          entry.incrementTimeWaitingForCAS(timeWaitingForCAS);
	          delegateServicePerformance.incrementCasPoolWaitTime(timeWaitingForCAS
	                  - delegateServicePerformance.getRawCasPoolWaitTime());
	          if (inputCasEntry != null) {
	            inputCasEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
	          }
	        }
	      }
	      if (super.propertyExists(AsynchAEMessage.TimeInProcessCAS)) {
	        long timeInProcessCAS = ((Long) super
	                .getMessageLongProperty(AsynchAEMessage.TimeInProcessCAS)).longValue();
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
	 private synchronized boolean receivedAllResponsesFromParallelDelegates(
	          CasStateEntry aCasStateEntry, int totalNumberOfParallelDelegatesProcessingCas) {
	    if (aCasStateEntry.howManyDelegatesResponded() == totalNumberOfParallelDelegatesProcessingCas) {
	      aCasStateEntry.resetDelegateResponded();
	      return true;
	    }
	    return false;
	  }

	  private void deserialize(String xmi, CAS cas, String casReferenceId, int highWaterMark,
	          AllowPreexistingFS allow) throws Exception {
	    XmiSerializationSharedData deserSharedData;
	    deserSharedData = controller.getInProcessCache().getCacheEntryForCAS(casReferenceId)
	            .getDeserSharedData();
	    UimaSerializer uimaSerializer = SerializerCache.lookupSerializerByThreadId();
	    uimaSerializer.deserializeCasFromXmi(xmi, cas, deserSharedData, true, highWaterMark, allow);
	  }
	  
	  private void deserialize(byte[] bytes, CAS cas, CacheEntry cacheEntry, TypeSystemImpl remoteTs,
	          AllowPreexistingFS allow) throws Exception {
	    ByteArrayInputStream istream = new ByteArrayInputStream(bytes);
	    ReuseInfo reuseInfo = cacheEntry.getCompress6ReuseInfo();

	    Serialization.deserializeCAS(cas, istream, remoteTs, reuseInfo, allow);
	  }

	  private void deserialize(String xmi, CAS cas, String casReferenceId) throws Exception {
	    CacheEntry entry = controller.getInProcessCache().getCacheEntryForCAS(casReferenceId);
	    // Processing the reply from a standard, non-parallel delegate
	    XmiSerializationSharedData deserSharedData;
	    deserSharedData = entry.getDeserSharedData();
	    if (deserSharedData == null) {
	      deserSharedData = new XmiSerializationSharedData();
	      entry.setXmiSerializationData(deserSharedData);
	    }
	    UimaSerializer uimaSerializer = SerializerCache.lookupSerializerByThreadId();
	    uimaSerializer.deserializeCasFromXmi(xmi, cas, deserSharedData, true, -1);
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

	private void incrementDelegateProcessCount() {
		Endpoint endpoint = super.getEndpoint();
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

	protected void computeStats(CacheEntry cacheEntry) throws Exception {
		if (super.propertyExists(AsynchAEMessage.TimeInService)) {
			long departureTime = controller.getTime(cacheEntry.getCasReferenceId(),
					super.getEndpoint().getEndpoint());
			long currentTime = System.nanoTime();
			long roundTrip = currentTime - departureTime;
			long timeInService = super.getMessageLongProperty(AsynchAEMessage.TimeInService);
			long totalTimeInComms = currentTime - (departureTime - timeInService);

			if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_roundtrip_time__FINE",
						new Object[] { cacheEntry.getCasReferenceId(), super.getEndpoint(),
								(double) roundTrip / (double) 1000000 });

				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_time_spent_in_delegate__FINE",
						new Object[] { cacheEntry.getCasReferenceId(), (double) timeInService / (double) 1000000,
								super.getEndpoint() });

				UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "computeStats",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_show_time_spent_in_comms__FINE",
						new Object[] { cacheEntry.getCasReferenceId(), (double) totalTimeInComms / (double) 1000000,
								super.getEndpoint() });
			}
		}

		if (controller instanceof AggregateAnalysisEngineController) {
			aggregateDelegateStats( cacheEntry);
		}
	}

	protected synchronized void aggregateDelegateStats(CacheEntry cacheEntry)
			throws AsynchAEException {
		String delegateKey = "";
		try {

			if (super.getEndpoint().getEndpoint() == null
					|| super.getEndpoint().getEndpoint().trim().length() == 0) {
				String fromEndpoint = super.getMessageStringProperty(AsynchAEMessage.MessageFrom);
				delegateKey = ((AggregateAnalysisEngineController) controller).lookUpDelegateKey(fromEndpoint);
			} else {
				delegateKey = ((AggregateAnalysisEngineController) controller)
						.lookUpDelegateKey(super.getEndpoint().getEndpoint());
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

			if (super.propertyExists(AsynchAEMessage.TimeToSerializeCAS)) {
				long timeToSerializeCAS = ((Long) super
						.getMessageLongProperty(AsynchAEMessage.TimeToSerializeCAS)).longValue();
				if (timeToSerializeCAS > 0) {
					if (delegateServicePerformance != null) {
						delegateServicePerformance.incrementCasSerializationTime(timeToSerializeCAS);
					}
				}
			}
			if (super.propertyExists(AsynchAEMessage.TimeToDeserializeCAS)) {
				long timeToDeserializeCAS = ((Long) super
						.getMessageLongProperty(AsynchAEMessage.TimeToDeserializeCAS)).longValue();
				if (timeToDeserializeCAS > 0) {
					if (delegateServicePerformance != null) {
						delegateServicePerformance.incrementCasDeserializationTime(timeToDeserializeCAS);
					}
				}
			}

			if (super.propertyExists(AsynchAEMessage.IdleTime)) {
				long idleTime = ((Long) super.getMessageLongProperty(AsynchAEMessage.IdleTime)).longValue();
				if (idleTime > 0 && delegateServicePerformance != null) {
					Endpoint endp = super.getEndpoint();
					if (endp != null && endp.isRemote()) {
						delegateServicePerformance.incrementIdleTime(idleTime);
					}
				}
			}

			if (super.propertyExists(AsynchAEMessage.TimeWaitingForCAS)) {
				long timeWaitingForCAS = ((Long) super
						.getMessageLongProperty(AsynchAEMessage.TimeWaitingForCAS)).longValue();
				if (timeWaitingForCAS > 0 && super.getEndpoint().isRemote()) {
					cacheEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
					delegateServicePerformance.incrementCasPoolWaitTime(
							timeWaitingForCAS - delegateServicePerformance.getRawCasPoolWaitTime());
					if (parentCasEntry != null) {
						parentCasEntry.incrementTimeWaitingForCAS(timeWaitingForCAS);
					}
				}
			}
			if (super.propertyExists(AsynchAEMessage.TimeInProcessCAS)) {
				long timeInProcessCAS = ((Long) super
						.getMessageLongProperty(AsynchAEMessage.TimeInProcessCAS)).longValue();
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
