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
package org.apache.uima.adapter.jms.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.aae.UimaAsPriorityBasedThreadFactory;
import org.apache.uima.aae.UimaAsThreadFactory;
import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController_impl;
import org.apache.uima.aae.error.ErrorHandlerChain;
import org.apache.uima.aae.handler.HandlerBase;
import org.apache.uima.aae.handler.input.MetadataRequestHandler_impl;
import org.apache.uima.aae.handler.input.MetadataResponseHandler_impl;
import org.apache.uima.aae.handler.input.ProcessRequestHandler_impl;
import org.apache.uima.aae.handler.input.ProcessResponseHandler;
import org.apache.uima.aae.service.AbstractUimaASService;
import org.apache.uima.aae.service.ScaleoutSpecification;
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.adapter.jms.activemq.JmsInputChannel;
import org.apache.uima.adapter.jms.activemq.JmsOutputChannel;
import org.apache.uima.adapter.jms.activemq.PriorityMessageHandler;
import org.apache.uima.adapter.jms.activemq.UimaDefaultMessageListenerContainer;
import org.apache.uima.adapter.jms.service.builder.ActiveMQFactory;
import org.apache.uima.adapter.jms.service.builder.JmsMessageListenerBuilder;
import org.apache.uima.as.client.Listener.Type;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.Level;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class UimaASJmsService extends AbstractUimaASService 
implements UimaASService {
	ActiveMQConnectionFactory factory = null;
	private String brokerURL;
	private String queueName;

	private JmsOutputChannel outputChannel;
	private JmsInputChannel inputChannel;
	private CountDownLatch latchToCountNumberOfTerminatedThreads;
	private CountDownLatch latchToCountNumberOfInitedThreads;
	
	ErrorHandlerChain errorHandlerChain;
	private List<UimaDefaultMessageListenerContainer> listeners = 
			new ArrayList<>();
	
	public static void main(String[] args) {
		try {
			
			String queueName = "PersonTitleAnnotatorQueue";
			String analysisEngineDescriptor = "C:/uima/releases/testing/uima/uima-as/2.9.0/target/uima-as-2.9.1-SNAPSHOT-bin/apache-uima-as-2.9.1-SNAPSHOT/examples/descriptors/analysis_engine/PersonTitleAnnotator.xml";
			String brokerURL = "tcp://localhost:61616";
			UimaASJmsService service = 
					new UimaASJmsService();

			
			ScaleoutSpecification spec = 
					new ScaleoutSpecification();
			spec.withProcessScaleout(4).withGetMetaScaleout(1).withFreeCasScaleout(1);
			
			ErrorHandlerChain errorHandlerChain = null;
			
			InProcessCache inProcessCache = new InProcessCache();
			
			ResourceManager resourceManager =
					UimaClassFactory.produceResourceManager();
			
			AsynchAECasManager_impl casManager = 
					new AsynchAECasManager_impl(resourceManager);
			casManager.setCasPoolSize(4);

			PrimitiveAnalysisEngineController_impl controller =
					new PrimitiveAnalysisEngineController_impl(null, queueName, analysisEngineDescriptor, casManager, inProcessCache, 1, 4);
			
			controller.setErrorHandlerChain(errorHandlerChain);
						

			service.withConttroller(controller)
				.withErrorHandlerChain(errorHandlerChain)
				.withBrokerURL(brokerURL)
				.withInputChannel()
				.withInputQueue(queueName)
				.withOutputChannel()
			    .build(4);
			
			service.start();
			
		} catch( Exception e) {
			e.printStackTrace();
		}

	}
	public UimaASJmsService withConttroller(AnalysisEngineController controller) {
		this.controller = controller;
		return this;
	}
	
	public UimaASJmsService withInProcessCache(InProcessCache cache) {
		this.inProcessCache = cache;
		return this;
	}
	public UimaASJmsService withResourceSpecifier(ResourceSpecifier resourceSpecifier) {
		this.resourceSpecifier = resourceSpecifier;
		return this;
	}
	
	public UimaASJmsService withBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
		return this;
	}
	public UimaASJmsService withName(String name) {
		this.name = name;
		return this;
	}
	public UimaASJmsService withInputQueue(String queueName) {
		this.queueName = queueName;
		return this;
	}
	public UimaASJmsService withErrorHandlerChain(ErrorHandlerChain errorHandlerChain) {
		this.errorHandlerChain = errorHandlerChain;
		return this;
	}
	
	private UimaASJmsService withInputChannel(){
		inputChannel = new JmsInputChannel(ChannelType.REQUEST_REPLY);
		return this;
	}
	private UimaASJmsService withOutputChannel() {
		outputChannel = new JmsOutputChannel();
		outputChannel.setController(controller);

		return this;
	}
	private void createConnectionFactory() {
		if ( factory == null ) {
			factory = ActiveMQFactory.newConnectionFactory(brokerURL, 0);
			factory.setTrustAllPackages(true);
		}
	}
	public String getBrokerURL() {
		return brokerURL;
	}
	private String getSelector(Type type) {
		String selector = null;
		switch(type) {
		case ProcessCAS:
			selector = "Command=2000 OR Command=2002";
			break;
			
		case GetMeta:
			selector = "Command=2001";
			break;
			
		case FreeCAS:
		case Unknown:
		case Reply:
			break;
		}
		return selector;  // OK to return NULL. This means no selector will be used
	}
	private boolean isTempQueueListener(Type type) {
		if ( Type.ProcessCAS.equals(type) || Type.GetMeta.equals(type)) {
			return false;
		}
		return true;
	}
	private UimaDefaultMessageListenerContainer createListener(Type type, int consumerCount) throws Exception{
		if ( inputChannel == null ) {
			withInputChannel();
		}
		if ( outputChannel == null ) {
			withOutputChannel();
		}
		PriorityMessageHandler h = null;
		
		ThreadPoolTaskExecutor jmsListenerThreadExecutor = 
				new ThreadPoolTaskExecutor();
		
		
		if ( Type.ProcessCAS.equals(type)) {
			outputChannel.setServerURI(getBrokerURL());
/*
			
			 // Create a Custom Thread Factory. Provide it with an instance of
		      // PrimitiveController so that every thread can call it to initialize
		      // the next available instance of a AE.
//		      ThreadFactory tf = new UimaAsThreadFactory().
//		    		  withThreadGroup(Thread.currentThread().getThreadGroup()).
//		    		  withPrimitiveController((PrimitiveAnalysisEngineController) controller).
//		    		  withTerminatedThreadsLatch(latchToCountNumberOfTerminatedThreads).
//		    		  withInitedThreadsLatch(latchToCountNumberOfInitedThreads);
//		      ((UimaAsThreadFactory)tf).setDaemon(true);
		      
		*/      
		      if ( controller.isPrimitive() ) {
				  h = new PriorityMessageHandler(consumerCount);
				  ThreadPoolTaskExecutor threadExecutor = 
						  new ThreadPoolTaskExecutor();
	              controller.setThreadFactory(threadExecutor);
	              
				  latchToCountNumberOfTerminatedThreads = new CountDownLatch(consumerCount);
			      // Create a Custom Thread Factory. Provide it with an instance of
			      // PrimitiveController so that every thread can call it to initialize
			      // the next available instance of a AE.
				  ThreadFactory tf = 
						  new UimaAsPriorityBasedThreadFactory(Thread.currentThread().
								  getThreadGroup(), controller, latchToCountNumberOfTerminatedThreads)
				          .withQueue(h.getQueue()).withChannel(controller.getInputChannel(ENDPOINT_TYPE.JMS));
				     
				  
				  ((UimaAsPriorityBasedThreadFactory)tf).setDaemon(true);
				  // This ThreadExecutor will use custom thread factory instead of default one
				   threadExecutor.setThreadFactory(tf);
				   threadExecutor.setCorePoolSize(consumerCount);
				   threadExecutor.setMaxPoolSize(consumerCount);

				  // Initialize the thread pool
				  threadExecutor.initialize();

				  // Make sure all threads are started. This forces each thread to call
				  // PrimitiveController to initialize the next instance of AE
				  threadExecutor.getThreadPoolExecutor().prestartAllCoreThreads();
			      // This ThreadExecutor will use custom thread factory instead of default one
//			      threadExecutor.setThreadFactory(tf);
			     
		    	  
		      }
			
		} 
		jmsListenerThreadExecutor.setCorePoolSize(consumerCount);
		jmsListenerThreadExecutor.setMaxPoolSize(consumerCount);
		jmsListenerThreadExecutor.initialize();
		
		
//		threadExecutor.setCorePoolSize(consumerCount);
//		threadExecutor.setMaxPoolSize(consumerCount);
		
		// destination can be NULL if this listener is meant for a 
		// a temp queue. Such destinations are created on demand 
		// using destination resolver which is plugged into the 
		// listener. The resolver creates a temp queue lazily on
		// listener startup.
		ActiveMQDestination destination = null;
		
		if ( !isTempQueueListener(type) ) {
			destination = new ActiveMQQueue(queueName);
		}
		JmsMessageListenerBuilder listenerBuilder = 
				new JmsMessageListenerBuilder();

		UimaDefaultMessageListenerContainer messageListener =
				listenerBuilder.withController(controller)
		       			.withType(type)
						.withConectionFactory(factory)
						.withThreadPoolExecutor(jmsListenerThreadExecutor)
						.withConsumerCount(consumerCount)
						.withInputChannel(inputChannel)
						.withPriorityMessageHandler(h)
						.withSelector(getSelector(type))
						.withDestination(destination)
						.build();
		messageListener.setReceiveTimeout(500);
//		messageListener.setMessageListener(h);
		return messageListener;
	}
	public HandlerBase getMessageHandler(AnalysisEngineController controller) {
		MetadataRequestHandler_impl metaHandler = new MetadataRequestHandler_impl("MetadataRequestHandler");
		metaHandler.setController(controller);
		ProcessRequestHandler_impl processHandler = new ProcessRequestHandler_impl("ProcessRequestHandler");
		processHandler.setController(controller);
		metaHandler.setDelegate(processHandler);
		if ( !controller.isPrimitive() ) {
			MetadataResponseHandler_impl metaResponseHandler = 
					new MetadataResponseHandler_impl("MetadataResponseHandler");
			metaResponseHandler.setController(controller);
			processHandler.setDelegate(metaResponseHandler);
			
			ProcessResponseHandler processResponseHandler = 
					new ProcessResponseHandler("ProcessResponseHandler");
			processResponseHandler.setController(controller);
			metaResponseHandler.setDelegate(processResponseHandler);
			
		}
		return metaHandler;
	}
	public UimaASJmsService build(int consumerCount) throws Exception {
		// First create Connection Factory. This is needed by
		// JMS listeners.
		createConnectionFactory();
		// counts number of initialized threads
		latchToCountNumberOfInitedThreads = new CountDownLatch(consumerCount);
		// counts number of terminated threads
		latchToCountNumberOfTerminatedThreads = new CountDownLatch(consumerCount);
		// Add one instance of JmsOutputChannel 
		if ( controller.getOutputChannel(ENDPOINT_TYPE.JMS) == null ) {
			withOutputChannel();
			outputChannel.setServerURI(brokerURL);
			outputChannel.setServiceInputEndpoint(queueName);
			controller.addOutputChannel(outputChannel);
		} else {
			outputChannel = (JmsOutputChannel)controller.getOutputChannel(ENDPOINT_TYPE.JMS);
			outputChannel.setServiceInputEndpoint(queueName);
		}
		// Add one instance of JmsInputChannel
		if ( controller.getInputChannel(ENDPOINT_TYPE.JMS) == null ) {
			withInputChannel();   // one input channel instance
			controller.setInputChannel(inputChannel);
		} else {
			inputChannel = (JmsInputChannel)controller.getInputChannel(ENDPOINT_TYPE.JMS);
		}
		
		inputChannel.setController(controller);
		
		inputChannel.setMessageHandler(getMessageHandler(controller));
		
		// Create service JMS listeners to handle Process, GetMeta and optional FreeCas
		// requests.
		
		// listener to handle process CAS requests
		UimaDefaultMessageListenerContainer processListener 
		    = createListener(Type.ProcessCAS, consumerCount);
		inputChannel.addListenerContainer(processListener);
		
		
		
		 
		
		
		  String targetStringSelector = "";
		  if ( System.getProperty(UimaAsynchronousEngine.TargetSelectorProperty) != null ) {
			  targetStringSelector = System.getProperty(UimaAsynchronousEngine.TargetSelectorProperty);
		  } else {
			  // the default selector is IP:PID 
			  String ip = InetAddress.getLocalHost().getHostAddress();
			  targetStringSelector = ip+":"+controller.getPID();
		  }
		  UimaDefaultMessageListenerContainer targetedListener = 
				  new UimaDefaultMessageListenerContainer();
		  targetedListener.setType(Type.Target);
		  // setup jms selector
		  if ( controller.isCasMultiplier()) {
			  targetedListener.setMessageSelector(UimaAsynchronousEngine.TargetSelectorProperty+" = '"+targetStringSelector+"' AND"+UimaDefaultMessageListenerContainer.CM_PROCESS_SELECTOR_SUFFIX);//(Command=2000 OR Command=2002)");
	          } else {
				  targetedListener.setMessageSelector(UimaAsynchronousEngine.TargetSelectorProperty+" = '"+targetStringSelector+"' AND"+UimaDefaultMessageListenerContainer.PROCESS_SELECTOR_SUFFIX);//(Command=2000 OR Command=2002)");
	          }
		  
		  // use shared ConnectionFactory
          targetedListener.setConnectionFactory(processListener.getConnectionFactory());
          // mark the listener as a 'Targeted' listener
          targetedListener.setTargetedListener();
          targetedListener.setController(controller);
          // there will only be one delivery thread. Its job will be to
          // add a targeted message to a BlockingQueue. Such thread will block
          // in an enqueue if a dequeue is not available. This will be prevent
          // the overwhelming the service with messages.
  		  ThreadPoolTaskExecutor threadExecutor = new ThreadPoolTaskExecutor();
		  threadExecutor.setCorePoolSize(1);
		  threadExecutor.setMaxPoolSize(1);
		  targetedListener.setTaskExecutor(threadExecutor);
          targetedListener.setConcurrentConsumers(1);
		  if ( processListener.getMessageListener() instanceof PriorityMessageHandler ) {
			  // the targeted listener will use the same message handler as the
			  // Process listener. This handler will add a message wrapper 
			  // to enable prioritizing messages. 
			  targetedListener.setMessageListener(processListener.getMessageListener());
		  }
		  // Same queue as the Process queue
		  targetedListener.setDestination(processListener.getDestination());
          //registerListener(targetedListener);
 //         targetedListener.afterPropertiesSet();
		  threadExecutor.initialize();
		  
          //targetedListener.initialize();
          //targetedListener.start();
          if (UIMAFramework.getLogger(getClass()).isLoggable(Level.INFO)) {
            UIMAFramework.getLogger(getClass()).logrb(Level.INFO, getClass().getName(),
                    "createListenerForTargetedMessages", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                    "UIMAJMS_TARGET_LISTENER__INFO",
                    new Object[] {targetedListener.getMessageSelector(), controller.getComponentName() });
          }
		
        inputChannel.addListenerContainer(targetedListener);
		
		listeners.add(processListener);
		// listener to handle GetMeta requests
		UimaDefaultMessageListenerContainer getMetaListener 
	        = createListener(Type.GetMeta, 1);
		inputChannel.addListenerContainer(getMetaListener);
		listeners.add(getMetaListener);
		
		if ( controller.isCasMultiplier()) {
			// listener to handle Free CAS requests
			UimaDefaultMessageListenerContainer freeCasListener 
		        = createListener(Type.FreeCAS, 1);
			inputChannel.addListenerContainer(freeCasListener);
			listeners.add(freeCasListener);
		}
		
		return this;
	}
	public void quiesce() throws Exception {
		controller.quiesceAndStop();
	}

	public void stop() throws Exception {
	//	controller.stop();
		
		controller.terminate();
/*
		for( UimaDefaultMessageListenerContainer listener : listeners ) {
			listener.setTerminating();
			listener.stop();
			// wait for all process threads to exit
			if ( controller.isPrimitive() && Type.ProcessCAS.equals(listener.getType())) {
				latchToCountNumberOfTerminatedThreads.await();
			}
			if ( listener.getTaskExecutor() != null ) {
				if ( listener.getTaskExecutor() instanceof ThreadPoolTaskExecutor ) {
					ThreadPoolTaskExecutor threadExecutor =
							(ThreadPoolTaskExecutor)listener.getTaskExecutor();
					threadExecutor.getThreadPoolExecutor().shutdownNow();
					threadExecutor.shutdown();
				}
			}
//			listener.closeConnection();
//			listener.destroy();
			System.out.println("Stopped Process Listener ....");
		}
*/
	}
	@Override
	public String getEndpoint() {
		return queueName;
	}
	public ResourceSpecifier getResourceSpecifier( ){
		return resourceSpecifier;
	}
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}
	@Override
	public String getName() {
		return name;
	}

}
