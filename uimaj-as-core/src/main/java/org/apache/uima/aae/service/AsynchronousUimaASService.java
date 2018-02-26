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
package org.apache.uima.aae.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InProcessCache.CacheEntry;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectMessageContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceSpecifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AsynchronousUimaASService extends AbstractUimaASService 
implements UimaASService {
	private static String CLIENT = "Client";
	Set<AsynchronousUimaASService> allInstancesOfThisClass =
			new HashSet<>();
	private int scaleout=1;
	private BlockingQueue<DirectMessage> processRequestQueue = 
		new LinkedBlockingQueue<>();
	private BlockingQueue<DirectMessage> getMetaRequestQueue = 
		new LinkedBlockingQueue<>();
	private BlockingQueue<DirectMessage> freeCasQueue = 
			new LinkedBlockingQueue<>();
	private BlockingQueue<DirectMessage> replyQueue = 
	    new LinkedBlockingQueue<>();
	private String tlc_bean_name;
	
    private AnnotationConfigApplicationContext context = 
    		new AnnotationConfigApplicationContext();
    
    private String endpoint;
    
	public AsynchronousUimaASService(String endpoint) { 
		this.endpoint = endpoint;
	}
	
	public void setTopLevelControlleBeanName(String beanName) {
		tlc_bean_name = beanName;
	}
	public AsynchronousUimaASService withResourceSpecifier(ResourceSpecifier resourceSpecifier ) {
		this.resourceSpecifier = resourceSpecifier;
		return this;
	}
	public ResourceSpecifier getResourceSpecifier() {
		return resourceSpecifier;
	}
	public String getBeanName() {
		return tlc_bean_name;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void addInstance(AsynchronousUimaASService instance) {
		allInstancesOfThisClass.add(instance);
	}
	public AnnotationConfigApplicationContext getContext() {
		return context;
	}
	
	public AsynchronousUimaASService withScaleout(int howMany) {
		this.scaleout = howMany;
		return this;
	}
	public int getScaleout() {
		return scaleout;
	}
	/**
	 * Allow to plugin an processQueue. This will be done when deploying multiple instances
	 * of the same service. In this case all service instance will share the same queues.
	 * @param q
	 * @return
	 */
	public AsynchronousUimaASService withProcessQueue(BlockingQueue<DirectMessage> q) {
		processRequestQueue = q;
		return this;
	}
	/**
	 * Allow to plugin an getMetaQueue. This will be done when deploying multiple instances
	 * of the same service. In this case all service instance will share the same queues.
	 * @param q
	 * @return
	 */
	public AsynchronousUimaASService withGetMetaQueue(BlockingQueue<DirectMessage> q) {
		getMetaRequestQueue = q;
		return this;
	}
	/**
	 * Allow to plugin an replyQueue. This will be done when deploying multiple instances
	 * of the same service. In this case all service instance will share the same queues.
	 * @param q
	 * @return
	 */
	public AsynchronousUimaASService withReplyQueue(BlockingQueue<DirectMessage> q) {
		replyQueue = q;
		return this;
	}

	public AsynchronousUimaASService withInProcessCache( InProcessCache inProcessCache ) {
		this.inProcessCache = inProcessCache;
		return this;
	}
	public void removeFromCache(String casReferenceId) {
		inProcessCache.remove(casReferenceId);
		System.out.println("AsynchronousUimaASService.removeFromCache()-cache size:"+inProcessCache.getSize());
	}
	private CacheEntry add2Cache( CAS cas, MessageContext messageContext, String casReferenceId ) throws Exception {
		return inProcessCache.register(cas, messageContext, casReferenceId);
	}
	public String getId() {
		return id;
	}
	
	public BlockingQueue<DirectMessage> getProcessRequestQueue() {
		return this.processRequestQueue;
	}
	public BlockingQueue<DirectMessage> getMetaRequestQueue() {
		return this.getMetaRequestQueue;
	}
	public BlockingQueue<DirectMessage> getReplyQueue() {
		return this.replyQueue;
	}
	public BlockingQueue<DirectMessage> getFreeCasQueue() {
		return this.freeCasQueue;
	}
	public AsynchronousUimaASService withController( AnalysisEngineController controller) {
		this.controller = controller;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public AsynchronousUimaASService withName( String aName) {
		this.name = aName;
		return this;
	}
	public CAS getCAS() throws Exception {
		return null;
	}

	public void process(CAS cas, String casReferenceId) throws Exception {

		DirectMessage processMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.Process).
				withMessageType(AsynchAEMessage.Request).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.CASRefID).
				withCasReferenceId(casReferenceId).
				withReplyQueue(replyQueue);

		DirectMessageContext messageContext = 
				new DirectMessageContext(processMessage, CLIENT,"");
		add2Cache(cas, messageContext, casReferenceId);
		
		processRequestQueue.add(processMessage);
	}

	public void sendGetMetaRequest() throws Exception {
		DirectMessage getMetaMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.GetMeta).
				withMessageType(AsynchAEMessage.Request).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.None).
				withReplyQueue(replyQueue);
		
		getMetaRequestQueue.add(getMetaMessage);
	}
	public void collectionProcessComplete() throws Exception {
		DirectMessage cpcMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.CollectionProcessComplete).
				withMessageType(AsynchAEMessage.Request).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.None).
				withReplyQueue(replyQueue);
		
		processRequestQueue.add(cpcMessage);
	}
	public void releaseCAS(String casReferenceId, BlockingQueue<DirectMessage> releaseCASQueue ) throws Exception {
		DirectMessage getMetaMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.ReleaseCAS).
				withMessageType(AsynchAEMessage.Request).
				withPayload(AsynchAEMessage.CASRefID).
				withCasReferenceId(casReferenceId).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.None);
		
		releaseCASQueue.add(getMetaMessage);
	}

	public AnalysisEngineMetaData getMetaData() throws Exception {
		DirectMessage getMetaMessage = 
				new DirectMessage().
				    withCommand(AsynchAEMessage.GetMeta).
				    withMessageType(AsynchAEMessage.Request).
				    withOrigin(CLIENT).
				    withReplyQueue(replyQueue).
				    withPayload(AsynchAEMessage.None);
		
		return getMetaData(getMetaMessage);
	}
	
	public AnalysisEngineMetaData getMetaData(DirectMessage getMetaMessage) throws InterruptedException {

	//	getMetaMessage.put(AsynchAEMessage.Endpoint, replyQueue);
		getMetaRequestQueue.add(getMetaMessage);

		DirectMessage dm = replyQueue.take();
		return (AnalysisEngineMetaData)dm.get(AsynchAEMessage.AEMetadata);
	}
	public void quiesce() throws Exception {
		controller.quiesceAndStop();
		context.destroy();
	}

	public void stop() throws Exception {
		for ( AsynchronousUimaASService instance : allInstancesOfThisClass) {
			instance.stop();
		}
		controller.getControllerLatch().release();
        controller.terminate();

        context.destroy();
	}

}
