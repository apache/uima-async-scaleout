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
package org.apache.uima.as.client;

import java.util.concurrent.BlockingQueue;

import org.apache.uima.aae.OutputChannel;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.UimaEEServiceException;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;

public class DirectOutputChannel implements OutputChannel {
	private AnalysisEngineController controller;
	private volatile boolean aborting = false;
	private BlockingQueue<DirectMessage> freeCASQueue;

	public DirectOutputChannel withController(AnalysisEngineController controller) {
		this.controller = controller;
		return this;
	}

	public void setFreeCASQueue(BlockingQueue<DirectMessage> queue) {
		freeCASQueue = queue;
	}

	public ENDPOINT_TYPE getType() {
		return ENDPOINT_TYPE.DIRECT;
	}

	@Override
	public void stop(boolean shutdownNow) throws Exception {
		aborting = true;
	}

	@Override
	public void stop(int channelsToStop, boolean shutdownNow) throws Exception {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setController(AnalysisEngineController aController) {
		withController(aController);
	}

	@Override
	public void initialize() throws AsynchAEException {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendRequest(int aCommand, String aCasReferenceId, Endpoint anEndpoint) throws AsynchAEException {

		BlockingQueue<DirectMessage> requestQueue = null;

		String origin = controller.getServiceEndpointName();
		if ( !controller.isTopLevelComponent()) {
			origin = controller.getKey();
		}
		if ( origin == null ) {
			origin = controller.getComponentName();
		}
		DirectMessage dm = new DirectMessage()
				.withMessageType(AsynchAEMessage.Request)
				.withCommand(aCommand)
				.withDelegateKey(anEndpoint.getDelegateKey())
				.withOrigin(origin);
/*				
				dm.put(AsynchAEMessage.MessageType, AsynchAEMessage.Request);
		dm.put(AsynchAEMessage.Command, aCommand);
		dm.put(AsynchAEMessage.MessageFrom, controller.getServiceEndpointName());
	*/
	//	InputChannel ic = controller.getInputChannel();
	//	List<Listener> listeners = ic.getListeners();

		switch (aCommand) {
		case AsynchAEMessage.CollectionProcessComplete:
			requestQueue = (BlockingQueue<DirectMessage>) anEndpoint.getDestination();
			dm.withReplyDestination(anEndpoint.getReplyDestination());
			break;
		case AsynchAEMessage.ReleaseCAS:
			System.out.println(">>>>>>>>>>>>>>>>>>> DirectOutputChannel.sendRequest() - Sending ReleaseCAS Request");
			dm.withCasReferenceId( aCasReferenceId);
			requestQueue = (BlockingQueue<DirectMessage>) anEndpoint.getDestination();
			break;
		case AsynchAEMessage.GetMeta:
			// every delegate has an endpoint containing a replyTo queue where
			// a listener waiting for reply msgs.
			dm.withReplyDestination( anEndpoint.getReplyDestination());

			requestQueue = (BlockingQueue<DirectMessage>) anEndpoint.getMetaDestination();
			System.out.println(">>>>>>>>>>>>>>>>>>> Service:" + controller.getComponentName()
			+ " DirectOutputChannel.sendRequest() - Sending GetMeta Request to delegate:"+anEndpoint.getDelegateKey()+" target queue.hashcode():"+requestQueue.hashCode());
			// delegate = startGetMetaTimerAndGetDelegate(anEndpoint);
//			dm.withDelegateKey(anEndpoint.getDelegateKey());
			break;
		case AsynchAEMessage.Stop:
			requestQueue = (BlockingQueue<DirectMessage>) anEndpoint.getDestination();
			dm.withCasReferenceId(aCasReferenceId);
			break;

		case AsynchAEMessage.Process:
			// Delegate must reply to a given endpoint
			dm.withReplyDestination(anEndpoint.getReplyDestination());
			// passing CAS by reference
			dm.withPayload(AsynchAEMessage.CASRefID);
			// id of a CAS which is needed to find it in a global cache
			dm.withCasReferenceId(aCasReferenceId);
			dm.withEndpointName(anEndpoint.getEndpoint());
			requestQueue = (BlockingQueue<DirectMessage>) anEndpoint.getDestination();
			break;
			
			default:
				dm.withPayload(AsynchAEMessage.None);

		};

		requestQueue.add(dm);
	}

	@Override
	public void sendReply(int aCommand, Endpoint anEndpoint, String aCasReferenceId, boolean notifyOnJmsException)
			throws AsynchAEException {
		System.out.println(".... Controller:" + controller.getComponentName()
				+ " DirectOutputChannel.sendReply() - CAS Id:" + aCasReferenceId+" Origin:"+controller.getServiceEndpointName());
		@SuppressWarnings("unchecked")
		BlockingQueue<DirectMessage> replyQueue =
				(BlockingQueue<DirectMessage>) anEndpoint.getReplyDestination();
		DirectMessage replyMessage = new DirectMessage().withCommand(aCommand).withMessageType(AsynchAEMessage.Response)
				.withOrigin(getOrigin()).withPayload(AsynchAEMessage.None);

		replyQueue.add(replyMessage);

	}
	private boolean sendInputCasAsParent() {
		return controller.isTopLevelComponent() || !controller.isPrimitive();
	}
	
	private boolean isChildCas(CasStateEntry casStateEntry) {
		return controller.isCasMultiplier() && casStateEntry.isSubordinate();
	}
//	public void sendReply(CacheEntry entry, Endpoint anEndpoint) throws AsynchAEException {
	public void sendReply(CasStateEntry casStateEntry, Endpoint anEndpoint) throws AsynchAEException {
		@SuppressWarnings("unchecked")
		BlockingQueue<DirectMessage> replyQueue = (BlockingQueue<DirectMessage>) anEndpoint.getReplyDestination();

		DirectMessage message;
		ServicePerformance casStats = 
				controller.getCasStatistics(casStateEntry.getCasReferenceId());
		
		if ( isChildCas(casStateEntry)) {
			String casAncestor;
			// return input CAS to the client since it is the true parent
			// of all CASes created in this CM. If this controller is an aggregate
			// it may have multiple CM delegates and the outgoing CAS may have
			// been created by a parent CAS created in this aggregate. The client
			// in this cas does not know about this parent. Its local to this 
			// controller. In this case we send input CAS to this controller as
			// as a parent of the outgoing child CAS.
			if ( sendInputCasAsParent() ) {
				casAncestor = casStateEntry.getInputCasReferenceId();
			} else {
				// this should only be for colocated delegate CM. For java remote the above should run
				casAncestor = casStateEntry.getParentCasReferenceId();
			}
			// This CM will send a child CAS to the Client for processing. The 
			message = new DirectMessage().withCommand(AsynchAEMessage.Process)
					.withMessageType(AsynchAEMessage.Request).withOrigin(getOrigin())
					.withPayload(AsynchAEMessage.CASRefID).withCasReferenceId(casStateEntry.getCasReferenceId())
					.withParentCasReferenceId(casAncestor).withSequenceNumber(casStateEntry.getSequenceNumber())
					.withFreeCASQueue(freeCASQueue);
			System.out.println("..... Service:" + controller.getComponentName()
					+ " Sending Child CAS and FreeCAS Queue:" + freeCASQueue.hashCode()+" For CAS:"+casStateEntry.getCasReferenceId());

		} else {
			message = new DirectMessage().withCommand(AsynchAEMessage.Process)
					.withMessageType(AsynchAEMessage.Response).withOrigin(getOrigin())//controller.getServiceEndpointName())
					.withPayload(AsynchAEMessage.CASRefID)
					.withCasReferenceId(casStateEntry.getCasReferenceId());
		}
		// add timing stats associated with the CAS processing
		message.withStat(AsynchAEMessage.TimeToSerializeCAS, casStats.getRawCasSerializationTime())
			.withStat(AsynchAEMessage.TimeToDeserializeCAS, casStats.getRawCasDeserializationTime())
			.withStat(AsynchAEMessage.TimeInProcessCAS, casStats.getRawAnalysisTime())
			.withStat(AsynchAEMessage.IdleTime,
				controller.getIdleTimeBetweenProcessCalls(AsynchAEMessage.Process));

		replyQueue.add(message);

	}

	private String getTopParentCasReferenceId(String casReferenceId) {
		if (!controller.getLocalCache().containsKey(casReferenceId)) {
			return null;
		}
		CasStateEntry casStateEntry = controller.getLocalCache().lookupEntry(casReferenceId);

		if (casStateEntry.isSubordinate()) {
			// Recurse until the top CAS reference Id is found
			return getTopParentCasReferenceId(casStateEntry.getParentCasReferenceId());
		}
		// Return the top ancestor CAS id
		return casStateEntry.getCasReferenceId();
	}

	@Override
	public void sendReply(ProcessingResourceMetaData aProcessingResourceMetadata, Endpoint anEndpoint,
			boolean serialize) throws AsynchAEException {
		System.out.println("Service:" + controller.getName()
				+ " DirectOutputChannel.sendReply() - sending GetMeta Reply - MsgFrom:"+anEndpoint.getDelegateKey());//controller.getServiceEndpointName()+" Delegate Key:"+anEndpoint.getDelegateKey());//controller.getKey());
		String msgFrom = controller.getName(); //controller.getServiceEndpointName();
//		if ( !controller.isTopLevelComponent()) {
//			msgFrom = controller.getKey();
//		}
//		if ( msgFrom == null ) {
//			msgFrom = anEndpoint.getDelegateKey();
//		}
		DirectMessage getMetaReplyMessage = new DirectMessage().withCommand(AsynchAEMessage.GetMeta)
				.withMessageType(AsynchAEMessage.Response).withOrigin(msgFrom)
				.withPayload(AsynchAEMessage.Metadata);

		// BlockingQueue<DirectMessage> replyQueue =
		// (BlockingQueue<DirectMessage>)anEndpoint.getDestination();
		@SuppressWarnings("unchecked")
		BlockingQueue<DirectMessage> replyQueue = (BlockingQueue<DirectMessage>) anEndpoint.getReplyDestination();

		// DirectMessage dm = new DirectMessage();
		// dm.put(AsynchAEMessage.MessageType,AsynchAEMessage.Response);
		// dm.put(AsynchAEMessage.Command,AsynchAEMessage.GetMeta);
		// dm.put(AsynchAEMessage.Payload, AsynchAEMessage.Metadata);
		// dm.put(AsynchAEMessage.MessageFrom, controller.getServiceId());
		getMetaReplyMessage.withSerializationType(AsynchAEMessage.None);
		getMetaReplyMessage.withMetadata(aProcessingResourceMetadata);
		replyQueue.add(getMetaReplyMessage);
	}

	private UimaEEServiceException wrapErrorInUimaEEServiceException(Throwable t) {
		if (!(t instanceof UimaEEServiceException)) {
			UimaEEServiceException wrapper;
			// Strip off AsyncAEException and replace with UimaEEServiceException
			if (t instanceof AsynchAEException && t.getCause() != null) {
				wrapper = new UimaEEServiceException(t.getCause());
			} else {
				wrapper = new UimaEEServiceException(t);
			}
			return wrapper;
		} else {
			return (UimaEEServiceException) t;
		}
	}

	private String getOrigin() {
		String origin = controller.getServiceEndpointName();

		if ( !controller.isTopLevelComponent()) {
			origin = controller.getKey();
		}
		return origin;
	}
	public void sendReply(Throwable t, String aCasReferenceId, String aParentCasReferenceId, Endpoint anEndpoint,
			int aCommand) throws AsynchAEException {
		anEndpoint.setReplyEndpoint(true);
		// the client expects an error wrapped in UimaEEServiceException
		UimaEEServiceException errorWrapper = wrapErrorInUimaEEServiceException(t);
		
		// construct reply msg containing the error
		DirectMessage errorReplyMessage = new DirectMessage().withCommand(AsynchAEMessage.Process)
				.withMessageType(AsynchAEMessage.Response).withOrigin(getOrigin())
				.withCasReferenceId(aCasReferenceId).withParentCasReferenceId(aParentCasReferenceId)
				.withError(errorWrapper).withPayload(AsynchAEMessage.Exception);
		@SuppressWarnings("unchecked")
		BlockingQueue<DirectMessage> replyQueue = (BlockingQueue<DirectMessage>) anEndpoint.getReplyDestination();
		controller.dropStats(aCasReferenceId, getName());

		replyQueue.add(errorReplyMessage);

	}

	@Override
	public void bindWithClientEndpoint(Endpoint anEndpoint) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServerURI(String aServerURI) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		aborting = true;
	}

	@Override
	public void cancelTimers() {
		// TODO Auto-generated method stub

	}

	public boolean isStopping() {
		return aborting;
	}
}
