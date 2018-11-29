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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InProcessCache.CacheEntry;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.definition.connectors.Endpoints;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.aae.message.Target;
import org.apache.uima.aae.message.UimaAsOrigin;
import org.apache.uima.aae.service.command.UimaAsMessageProcessor;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.as.client.DirectMessage;
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
	private UimaAsEndpoint directEndpoint;
	
	private boolean initialized = false;
    private AnnotationConfigApplicationContext context = 
    		new AnnotationConfigApplicationContext();
    
    private String endpoint;
    
	public AsynchronousUimaASService(String name) { 
		endpoint = super.name = name;
	}
	public AsynchronousUimaASService(UimaAsEndpoint endpoint) { 
		super(endpoint);
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
	public CacheEntry add2Cache( CAS cas, MessageContext messageContext, String casReferenceId ) throws Exception {
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
    private String getServiceUri(ConsumerType consumerType) {
    	return new StringBuilder(EndpointType.Direct.getName()).append(controller.getComponentName()).append(":").append(consumerType.name()).toString();
    }
	public void connect(UimaAsEndpoint clientEndpoint) throws Exception {
    	UimaAsEndpoint serviceEndpoint =
    			getDirectEndpoint();
    	Map<Target, UimaAsConsumer> serviceConsumers =
    			serviceEndpoint.getConsumers();

//    	Origin clientOrigin = new UimaAsOrigin(EndpointType.Direct.getName()+"Client");
 //   	Origin serviceOrigin = new UimaAsOrigin(controller.getComponentName(), EndpointType.Direct);
    	for( Entry<Target, UimaAsConsumer> serviceConsumer : serviceConsumers.entrySet() ) {
    		ConsumerType consumerType = serviceConsumer.getValue().getType();
    	//	StringBuilder sb = new StringBuilder(EndpointType.Direct.getName()).
		//			append(clientOrigin.getName());
    		switch(consumerType) {
    		case GetMetaRequest:
    			// creates client producer for the service consumer and links the two
    			clientEndpoint.createProducer(serviceConsumer.getValue(), controller.getComponentName()); //getServiceUri(consumerType));
    			// creates client consumer for GetMeta replies
    			UimaAsConsumer clientGetMetaResponseConsumer
    				= clientEndpoint.createConsumer( ConsumerType.GetMetaResponse, 1);
    		//	sb.append(":").append(ConsumerType.GetMetaResponse.name());
    			serviceEndpoint.createProducer(clientGetMetaResponseConsumer, "Client");
    			break;
    		case ProcessCASRequest:
    			clientEndpoint.createProducer(serviceConsumer.getValue(), controller.getComponentName()); //getServiceUri(consumerType));
    			UimaAsConsumer clientProcessCASResponseConsumer
					= clientEndpoint.createConsumer( ConsumerType.ProcessCASResponse, 1);
    		//	sb.append(":").append(ConsumerType.ProcessCASResponse.name());
    			serviceEndpoint.createProducer(clientProcessCASResponseConsumer, "Client");
    			
    			break;
    		case CpcRequest:
    			clientEndpoint.createProducer(serviceConsumer.getValue(), controller.getComponentName()); //getServiceUri(consumerType));
    			UimaAsConsumer clientCpcResponseConsumer
					= clientEndpoint.createConsumer( ConsumerType.CpcResponse, 1);
    		//	sb.append(":").append(ConsumerType.CpcResponse.name());
    			serviceEndpoint.createProducer(clientCpcResponseConsumer, "Client");
    			
    			break;
    			
       		case FreeCASRequest:
    			clientEndpoint.createProducer(serviceConsumer.getValue(), controller.getComponentName()); //getServiceUri(consumerType));
    			
    			break;

       		default:
    				
    		}
    	}
    }

	public void process(CAS cas, String casReferenceId) throws Exception {

		/*
		 * 10/19/19 JC replace with new methodology
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
		*/
		
		
		UimaAsEndpoint serviceEndpoint = super.getDirectEndpoint();
		MessageContext processMessage =
			serviceEndpoint.newMessageBuilder()
				.newProcessCASRequestMessage(new UimaAsOrigin(CLIENT, EndpointType.Direct))
				.withCasReferenceId(casReferenceId)
				.withPayload(AsynchAEMessage.CASRefID)
				.build();

		add2Cache(cas, processMessage, casReferenceId);
		
		super.getDirectEndpoint().dispatch(processMessage, "direct:service");
		
		
//		processRequestQueue.add((DirectMessage)processMessage.getRawMessage());
}

	public void sendGetMetaRequest() throws Exception {
		/*
		 * 
		
		DirectMessage getMetaMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.GetMeta).
				withMessageType(AsynchAEMessage.Request).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.None).
				withReplyQueue(replyQueue);
		 */
		UimaAsEndpoint serviceEndpoint = super.getDirectEndpoint();
		MessageContext getMetaMessage =
				serviceEndpoint.newMessageBuilder().
			newGetMetaRequestMessage(new UimaAsOrigin(CLIENT, EndpointType.Direct))
			.withPayload(AsynchAEMessage.None)
			.build();
		serviceEndpoint.dispatch(getMetaMessage,"direct:service");
		//getMetaRequestQueue.add((DirectMessage)getMetaMessage.getRawMessage());
	}
	public void collectionProcessComplete() throws Exception {
		/*
		DirectMessage cpcMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.CollectionProcessComplete).
				withMessageType(AsynchAEMessage.Request).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.None).
				withReplyQueue(replyQueue);
		*/
		UimaAsEndpoint directEndpoint = super.getDirectEndpoint();
		MessageContext cpcMessage =
				directEndpoint.newMessageBuilder().
			newCpCRequestMessage(new UimaAsOrigin(CLIENT, EndpointType.Direct))
			.withPayload(AsynchAEMessage.None)
			.build();

		super.getDirectEndpoint().dispatch(cpcMessage,"direct:service");
		//processRequestQueue.add((DirectMessage)cpcMessage.getRawMessage());
	}
	public void releaseCAS(String casReferenceId, BlockingQueue<DirectMessage> releaseCASQueue ) throws Exception {
		/*
		DirectMessage getMetaMessage = 
				new DirectMessage().
				withCommand(AsynchAEMessage.ReleaseCAS).
				withMessageType(AsynchAEMessage.Request).
				withPayload(AsynchAEMessage.CASRefID).
				withCasReferenceId(casReferenceId).
				withOrigin(CLIENT).
				withPayload(AsynchAEMessage.None);
		*/
		UimaAsEndpoint directEndpoint = super.getDirectEndpoint();
		MessageContext freeCasMessage =
				directEndpoint.newMessageBuilder().
			newReleaseCASRequestMessage(new UimaAsOrigin(CLIENT, EndpointType.Direct))
			.withPayload(AsynchAEMessage.CASRefID)
			.withCasReferenceId(casReferenceId)
			.build();
		UimaAsEndpoint serviceEndpoint = 
				controller.getEndpoint(EndpointType.Direct);
		Map<Target, UimaAsConsumer> serviceConsumers =
				serviceEndpoint.getConsumers();
		
		
		super.getDirectEndpoint().dispatch(freeCasMessage,"direct:service");
		//releaseCASQueue.add((DirectMessage)freeCasMessage.getRawMessage());
	}

	public AnalysisEngineMetaData getMetaData() throws Exception {
		/*
		DirectMessage getMetaMessage = 
				new DirectMessage().
				    withCommand(AsynchAEMessage.GetMeta).
				    withMessageType(AsynchAEMessage.Request).
				    withOrigin(CLIENT).
				    withReplyQueue(replyQueue).
				    withPayload(AsynchAEMessage.None);
		
		*/
		UimaAsEndpoint directEndpoint = super.getDirectEndpoint();
		MessageContext getMetaMessage =
				directEndpoint.newMessageBuilder().
			newGetMetaRequestMessage(new UimaAsOrigin(CLIENT, EndpointType.Direct))
			.withPayload(AsynchAEMessage.None)
			.build();
		return getMetaData(getMetaMessage);
//		getMetaRequestQueue.add((DirectMessage)getMetaMessage.getRawMessage());
//		return getMetaData((DirectMessage)getMetaMessage.getRawMessage());
	}
	
	///public AnalysisEngineMetaData getMetaData(DirectMessage getMetaMessage) throws InterruptedException {
	public AnalysisEngineMetaData getMetaData(MessageContext getMetaMessage) throws Exception {

	//	getMetaMessage.put(AsynchAEMessage.Endpoint, replyQueue);
		//getMetaRequestQueue.add(getMetaMessage);
		super.getDirectEndpoint().dispatch(getMetaMessage,"direct:service");
		DirectMessage dm = replyQueue.take();
		return (AnalysisEngineMetaData)dm.get(AsynchAEMessage.AEMetadata);
	}
	public void quiesce() throws Exception {
		controller.quiesceAndStop();
		context.destroy();
	}
/*
	private void parentDelegateConsumers(UimaAsEndpoint delegateDirectEndpoint,UimaAsEndpoint parentDirectEndpoint, ConsumerType type, int scaleout ) throws Exception {
		UimaAsConsumer consumer =
				delegateDirectEndpoint.createConsumer("direct:Client", type, scaleout);
		parentDirectEndpoint.createProducer(consumer, new UimaAsOrigin("direct:"+EndpointType.Direct));

	}
	*/
	
	
	/**
	 * This is called for aggregates with asynch delegates
	 */
	@Override
	public void initialize(MessageProcessor messageProcessor, AnalysisEngineController parentController) throws Exception {
		boolean addEndpoint=false;
		if ( !controller.isPrimitive() ) {
			System.out.println(".......... Aggregate:"+controller.getComponentName());
		}
		// create service producers for this client consumers
		UimaAsEndpoint parentServiceEndpoint = 
				parentController.getEndpoint(EndpointType.Direct);
		UimaAsEndpoint delegateDirectEndpoint; 
		if ( ( delegateDirectEndpoint = controller.getEndpoint(EndpointType.Direct) ) == null ){
			delegateDirectEndpoint = Endpoints.newEndpoint(EndpointType.Direct, getName(), messageProcessor);
			addEndpoint = true;
		} 
//		directEndpoint = new DirectUimaAsEndpoint(messageProcessor, "Service");
		String did = getName();
		UimaAsConsumer clientGetMetaRequestConsumer =
				delegateDirectEndpoint.createConsumer( ConsumerType.GetMetaRequest, 1);
		UimaAsConsumer clientProcessRequestConsumer =
				delegateDirectEndpoint.createConsumer( ConsumerType.ProcessCASRequest, 4);
		UimaAsConsumer clientCpcRequestConsumer =
				delegateDirectEndpoint.createConsumer( ConsumerType.CpcRequest, 1);
		// CPC request must be processed on Process CAS thread since this
		// request calls AE.collectionProcessComplete and AS instances are
		// pinned to Process CAS threads
		clientCpcRequestConsumer.delegateTo(clientProcessRequestConsumer);
		if ( addEndpoint ) {
			controller.addEndpoint(delegateDirectEndpoint);
		}

			
		parentServiceEndpoint.createProducer(clientGetMetaRequestConsumer, did);
		parentServiceEndpoint.createProducer(clientProcessRequestConsumer, did);
		parentServiceEndpoint.createProducer(clientCpcRequestConsumer, did);
		
	//	String sid = new StringBuilder("direct:").append(parentController.getKey()).toString();
		UimaAsConsumer parentGetMetaResponseConsumer = 
				parentServiceEndpoint.createConsumer(ConsumerType.GetMetaResponse, 1);
		UimaAsConsumer parentProcessCASResponseConsumer = 
				parentServiceEndpoint.createConsumer(ConsumerType.ProcessCASResponse, 1);
		UimaAsConsumer parentCpcResponseConsumer = 
				parentServiceEndpoint.createConsumer(ConsumerType.CpcResponse, 1);

		String pid = parentController.getKey();  // default
		if ( parentController.isTopLevelComponent() ) {
			pid = parentController.getComponentName();
		}
		delegateDirectEndpoint.createProducer(parentGetMetaResponseConsumer, pid);
		delegateDirectEndpoint.createProducer(parentProcessCASResponseConsumer, pid);
		delegateDirectEndpoint.createProducer(parentCpcResponseConsumer, pid);
		
		if ( controller.isCasMultiplier() ) {
			UimaAsConsumer delegateFreeCasRequestConsumer = 
					delegateDirectEndpoint.createConsumer(ConsumerType.FreeCASRequest, 1);
			parentServiceEndpoint.createProducer(delegateFreeCasRequestConsumer, parentController.getComponentName());
			UimaAsConsumer parentProcessCASRequestConsumer = 
					parentServiceEndpoint.createConsumer(ConsumerType.ProcessCASRequest, 1);

			delegateDirectEndpoint.createProducer(parentProcessCASRequestConsumer, parentController.getComponentName());
		}
		initialized = true;
	}
	/**
	 * This is used for top level service
	 */
	@Override
	public void initialize(MessageProcessor messageProcessor) throws Exception {
		// create service producers for this client consumers
		UimaAsEndpoint serviceEndpoint = 
				controller.getEndpoint(EndpointType.Direct);
		if ( Objects.isNull(serviceEndpoint) ) {
			serviceEndpoint = Endpoints.newEndpoint(EndpointType.Direct, controller.getComponentName(), new UimaAsMessageProcessor(controller));
			//controller.addEndpoint(serviceEndpoint);
		}
		serviceEndpoint.createConsumer( ConsumerType.GetMetaRequest, 1);
		UimaAsConsumer processConsumer =
				serviceEndpoint.createConsumer( ConsumerType.ProcessCASRequest, 1);
		UimaAsConsumer cpcConsumer =
				serviceEndpoint.createConsumer( ConsumerType.CpcRequest, 1);
		// CPC request should be handled by process consumer. Each AE instance is pinned to 
		// initialize thread and only process consumer guarantees this. So the CPC consumer
		// delegates all requests to process consumer.
		cpcConsumer.delegateTo(processConsumer);
		
		if ( controller.isCasMultiplier() ) {
			serviceEndpoint.createConsumer( ConsumerType.FreeCASRequest, 1);
		}
		
		
		/*
		if ( directEndpoint == null ) {
			directEndpoint = Endpoints.newEndpoint(EndpointType.Direct,"Service",messageProcessor);
		}
		//		directEndpoint = new DirectUimaAsEndpoint(messageProcessor, "Service");
		UimaAsConsumer clientGetMetaResponseConsumer =
				directEndpoint.createConsumer("direct:"+controller.getComponentName(), ConsumerType.GetMetaResponse, 1);
		UimaAsConsumer clientProcessResponseConsumer =
				directEndpoint.createConsumer("direct:"+controller.getComponentName(), ConsumerType.ProcessCASResponse, 4);
		UimaAsConsumer clientCpcResponseConsumer =
				directEndpoint.createConsumer("direct:"+controller.getComponentName(), ConsumerType.CpcResponse, 1);

		// create service producers for this client consumers
		UimaAsEndpoint serviceEndpoint = 
				controller.getEndpoint(EndpointType.Direct);
		if ( Objects.isNull(serviceEndpoint) ) {
			serviceEndpoint = Endpoints.newEndpoint(EndpointType.Direct, controller.getComponentName(), new UimaAsMessageProcessor(controller));
			//controller.addEndpoint(serviceEndpoint);
		}
		Origin origin = 
				new UimaAsOrigin("direct:"+controller.getComponentName());
		serviceEndpoint.createProducer(clientGetMetaResponseConsumer, "direct:"+controller.getComponentName());
		serviceEndpoint.createProducer(clientProcessResponseConsumer, "direct:"+controller.getComponentName());
		serviceEndpoint.createProducer(clientCpcResponseConsumer, "direct:"+controller.getComponentName());
		
		UimaAsConsumer c1 =
				serviceEndpoint.createConsumer("direct:"+controller.getComponentName()+":"+ConsumerType.GetMetaRequest.name(), ConsumerType.GetMetaRequest, 1);
		UimaAsConsumer c2 =
				serviceEndpoint.createConsumer("direct:"+controller.getComponentName()+":"+ConsumerType.ProcessCASRequest.name(), ConsumerType.ProcessCASRequest, 1);
		UimaAsConsumer c3 =
				serviceEndpoint.createConsumer("direct:"+controller.getComponentName()+":"+ConsumerType.CpcRequest.name(), ConsumerType.CpcRequest, 1);
		
		Origin clientOrigin = new UimaAsOrigin("direct:Client");
		directEndpoint.createProducer(c1, "direct:"+getName() );
		directEndpoint.createProducer(c2, "direct:"+getName() );
		directEndpoint.createProducer(c3, "direct:"+getName() );
		*/
		controller.addEndpoint(serviceEndpoint);
	
		initialized = true;
	}
	
	public void start() throws Exception {
		if ( !initialized ) {
			throw new RuntimeException("AsynchronousUimaASService.start() - initialize() must be called first");
		}
		super.start();
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
