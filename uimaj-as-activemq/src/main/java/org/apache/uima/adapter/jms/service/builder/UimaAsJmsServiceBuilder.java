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
package org.apache.uima.adapter.jms.service.builder;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.UUID;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;
import org.apache.uima.aae.component.TopLevelServiceComponent;
import org.apache.uima.aae.OutputChannel;
import org.apache.uima.aae.UimaAsPriorityBasedThreadFactory;
import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.ControllerCallbackListener;
import org.apache.uima.aae.controller.Endpoint_impl;
import org.apache.uima.aae.error.ErrorHandler;
import org.apache.uima.aae.error.ErrorHandlerChain;
import org.apache.uima.aae.error.Threshold;
import org.apache.uima.aae.error.Thresholds;
import org.apache.uima.aae.error.handler.CpcErrorHandler;
import org.apache.uima.aae.error.handler.GetMetaErrorHandler;
import org.apache.uima.aae.error.handler.ProcessCasErrorHandler;
import org.apache.uima.aae.handler.Handler;
import org.apache.uima.aae.handler.HandlerBase;
import org.apache.uima.aae.handler.input.MetadataRequestHandler_impl;
import org.apache.uima.aae.handler.input.MetadataResponseHandler_impl;
import org.apache.uima.aae.handler.input.ProcessRequestHandler_impl;
import org.apache.uima.aae.handler.input.ProcessResponseHandler;
import org.apache.uima.aae.service.AsynchronousUimaASService;
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.aae.service.builder.AbstractUimaAsServiceBuilder;
import org.apache.uima.aae.service.delegate.AnalysisEngineDelegate;
import org.apache.uima.aae.service.delegate.RemoteAnalysisEngineDelegate;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.adapter.jms.activemq.JmsInputChannel;
import org.apache.uima.adapter.jms.activemq.JmsOutputChannel;
import org.apache.uima.adapter.jms.activemq.PriorityMessageHandler;
import org.apache.uima.adapter.jms.activemq.TempDestinationResolver;
import org.apache.uima.adapter.jms.activemq.UimaDefaultMessageListenerContainer;
import org.apache.uima.adapter.jms.service.UimaASJmsService;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.as.client.DirectInputChannel;
import org.apache.uima.as.client.Listener.Type;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType;
import org.apache.uima.resourceSpecifier.CasPoolType;
import org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType;
import org.apache.uima.resourceSpecifier.ProcessCasErrorsType;
import org.apache.uima.resourceSpecifier.ServiceType;
import org.apache.uima.util.Level;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class UimaAsJmsServiceBuilder extends AbstractUimaAsServiceBuilder{
//	private static final String NoParent= "NoParent";
//	private static enum FlowControllerType {
//		FIXED
//	}
//		static Map<String, Object> ddAEMap = new HashMap<String, Object>();
	
	/*
	private InProcessCache cache;
	private AsynchAECasManager_impl casManager;
    private ResourceManager resourceManager;
    */
    private int scaleout=1;
//    private AnalysisEngineController controller;
//    private List<ControllerStatusListener> listeners = new ArrayList<ControllerStatusListener>();
//    private ServiceMode mode = ServiceMode.Asynchronous;   // default 
//    private AnalysisEngineDescription topLevelAEDescriptor;
    
	public static void main(String[] args) {
		try {
			String tla = "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/descriptors/tutorial/ex4/MeetingDetectorTAEGovNameDetector.xml";
			String ptDescriptor = "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/descriptors/analysis_engine/PersonTitleAnnotator.xml";
			//			"C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/descriptors/tutorial/ex4/MeetingDetectorTAE.xml";

			//String dd1 = "C:/uima/releases/builds/uima-as/2.8.1/uimaj-as-activemq/src/test/resources/deployment/Deploy_TopLevelBlueJAggregateCM.xml";
			String dd2 = "C:/uima/releases/builds/uima-as/2.8.1/uimaj-as-activemq/src/test/resources/deployment/Deploy_TopAggregateWithInnerAggregateCM.xml";
			String dd = "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/deploy/as/Deploy_MeetingDetectorTAE.xml";
			
			String dd3 = "../uimaj-as-activemq/src/test/resources/deployment/Deploy_PersonTitleAnnotator.xml";
			String dd4 = "../uimaj-as-activemq/src/test/resources/deployment/Deploy_AggregateAnnotator.xml";

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/***   NEW CODE  */
	
	public UimaASService build(TopLevelServiceComponent topLevelComponent, ControllerCallbackListener callback)
			throws Exception {
		UimaASService service = null;
		
		// is this the only one resource specifier type supported  by the current uima-as?
		if (topLevelComponent.getResourceSpecifier() instanceof AnalysisEngineDescription) {
			AnalysisEngineDescription aeDescriptor = 
					(AnalysisEngineDescription) topLevelComponent.getResourceSpecifier();
			String endpoint = resolvePlaceholder(topLevelComponent.getEndpoint().getEndpoint());
			// Create a Top Level Service (TLS) wrapper. This wrapper may contain
			// references to multiple TLS service instances if the TLS is scaled
			// up.
			service = new UimaASJmsService().
					withName(aeDescriptor.
					getAnalysisEngineMetaData().getName())
					.withResourceSpecifier(aeDescriptor).
					withBrokerURL(topLevelComponent.getEndpoint().getServerURI()).
					withInputQueue(endpoint);

			this.buildAndDeploy(topLevelComponent, service, callback);
			

		}
		return service;
	}
	
	public UimaASService buildAndDeploy(TopLevelServiceComponent topLevelComponent, UimaASService service, ControllerCallbackListener callback) throws Exception {
		// create ResourceManager, CasManager, and InProcessCache
		initialize(service, topLevelComponent.getComponentCasPool(), Transport.Java); 

		AnalysisEngineController topLevelController = 
				createController(topLevelComponent, callback, service.getId());
		
		service.withInProcessCache(super.cache);
		System.setProperty("BrokerURI", "Direct");
		configureTopLevelService(topLevelController, service /*, topLevelComponent.getScaleout() */);
		return service;

	}

	
	private void configureTopLevelService(AnalysisEngineController topLevelController,	UimaASService service) throws Exception {
		// First create Connection Factory. This is needed by
		// JMS listeners.
		createConnectionFactory();
		// counts number of initialized threads
		CountDownLatch latchToCountNumberOfInitedThreads = 
				new CountDownLatch(service.getScaleout());
		// counts number of terminated threads
		CountDownLatch latchToCountNumberOfTerminatedThreads = 
				new CountDownLatch(service.getScaleout());
		OutputChannel outputChannel;
		;
		// Add one instance of JmsOutputChannel 
		if ( topLevelController.getOutputChannel(ENDPOINT_TYPE.JMS) == null ) {
			outputChannel = new JmsOutputChannel();
			outputChannel.setController(topLevelController);
			outputChannel.setServerURI(brokerURL);
			outputChannel.setServiceInputEndpoint(service.getEndpoint());
			topLevelController.addOutputChannel(outputChannel);
		} else {
			outputChannel = (JmsOutputChannel)topLevelController.getOutputChannel(ENDPOINT_TYPE.JMS);
			outputChannel.setServiceInputEndpoint(service.getEndpoint());
		}
		JmsInputChannel inputChannel;
		// Add one instance of JmsInputChannel
		if ( topLevelController.getInputChannel(ENDPOINT_TYPE.JMS) == null ) {
			inputChannel = new JmsInputChannel(ChannelType.REQUEST_REPLY);
			topLevelController.setInputChannel(inputChannel);
		} else {
			inputChannel = (JmsInputChannel)topLevelController.getInputChannel(ENDPOINT_TYPE.JMS);
		}
		
		inputChannel.setController(topLevelController);
		
		inputChannel.setMessageHandler(getMessageHandler(topLevelController));
		
		// Create service JMS listeners to handle Process, GetMeta and optional FreeCas
		// requests.
		
		// listener to handle process CAS requests
		UimaDefaultMessageListenerContainer processListener 
		    = createListener(Type.ProcessCAS, service.getScaleout(), inputChannel, outputChannel);
		inputChannel.addListenerContainer(processListener);
		
		
		
		 
		
		
		  String targetStringSelector = "";
		  if ( System.getProperty(UimaAsynchronousEngine.TargetSelectorProperty) != null ) {
			  targetStringSelector = System.getProperty(UimaAsynchronousEngine.TargetSelectorProperty);
		  } else {
			  // the default selector is IP:PID 
			  String ip = InetAddress.getLocalHost().getHostAddress();
			  targetStringSelector = ip+":"+topLevelController.getPID();
		  }
		  UimaDefaultMessageListenerContainer targetedListener = 
				  new UimaDefaultMessageListenerContainer();
		  targetedListener.setType(Type.Target);
		  // setup jms selector
		  if ( topLevelController.isCasMultiplier()) {
			  targetedListener.setMessageSelector(UimaAsynchronousEngine.TargetSelectorProperty+" = '"+targetStringSelector+"' AND"+UimaDefaultMessageListenerContainer.CM_PROCESS_SELECTOR_SUFFIX);//(Command=2000 OR Command=2002)");
	          } else {
				  targetedListener.setMessageSelector(UimaAsynchronousEngine.TargetSelectorProperty+" = '"+targetStringSelector+"' AND"+UimaDefaultMessageListenerContainer.PROCESS_SELECTOR_SUFFIX);//(Command=2000 OR Command=2002)");
	          }
		  
		  // use shared ConnectionFactory
          targetedListener.setConnectionFactory(processListener.getConnectionFactory());
          // mark the listener as a 'Targeted' listener
          targetedListener.setTargetedListener();
          targetedListener.setController(topLevelController);
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
                    new Object[] {targetedListener.getMessageSelector(), topLevelController.getComponentName() });
          }
		
        inputChannel.addListenerContainer(targetedListener);
		
		//listeners.add(processListener);
		// listener to handle GetMeta requests
		UimaDefaultMessageListenerContainer getMetaListener 
	        = createListener(Type.GetMeta, 1);
		inputChannel.addListenerContainer(getMetaListener);
		//listeners.add(getMetaListener);
		
		if ( topLevelController.isCasMultiplier()) {
			// listener to handle Free CAS requests
			UimaDefaultMessageListenerContainer freeCasListener 
		        = createListener(Type.FreeCAS, 1);
			inputChannel.addListenerContainer(freeCasListener);
			//listeners.add(freeCasListener);
		}
	}
	
	private UimaDefaultMessageListenerContainer createListener(Type type, int consumerCount, InputChannel inputChannel, OutputChannel outputChannel) throws Exception{
		PriorityMessageHandler h = null;
		
		ThreadPoolTaskExecutor jmsListenerThreadExecutor = 
				new ThreadPoolTaskExecutor();
		
		if ( Type.ProcessCAS.equals(type)) {
			outputChannel.setServerURI(getBrokerURL());
		      if ( controller.isPrimitive() ) {
				  h = new PriorityMessageHandler(consumerCount);
				  ThreadPoolTaskExecutor threadExecutor = 
						  new ThreadPoolTaskExecutor();
	              controller.setThreadFactory(threadExecutor);
	              
				  CountDownLatch latchToCountNumberOfTerminatedThreads = 
						  new CountDownLatch(consumerCount);
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
	
	
	
	
	/* OLD CODE */
	
	
	
	
	
	public static InputChannel createInputChannel(ChannelType type) {
		return new JmsInputChannel(type);
	}

	
	public OutputChannel createOutputChannel() {
		// TODO Auto-generated method stub
		return null;
	}
	protected void addListenerForReplyHandling( AggregateAnalysisEngineController controller, Endpoint_impl endpoint, RemoteAnalysisEngineDelegate remoteDelegate) throws Exception {
		String brokerURL =  resolvePlaceholder(remoteDelegate.getBrokerURI());
		int prefetch = remoteDelegate.getPrefetch();
		endpoint.setEndpoint(resolvePlaceholder(remoteDelegate.getQueueName())); 

		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// WITH HTTP BROKER URL THE PRFETCH MUST BE > 0 
		// OTHERWISE THE LISTENER DOES NOT GET MSGS
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		if (prefetch == 0 ) {
			prefetch = 1;
		}
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		JmsInputChannel inputChannel;
		if ((controller.getInputChannel(ENDPOINT_TYPE.JMS)) == null) {
			inputChannel = new JmsInputChannel(ChannelType.REQUEST_REPLY);
			Handler messageHandlerChain = getMessageHandler(controller);
			inputChannel.setMessageHandler(messageHandlerChain);
			controller.addInputChannel(inputChannel);
			inputChannel.setController(controller);
		} else {
			inputChannel = (JmsInputChannel) controller.getInputChannel(ENDPOINT_TYPE.JMS);
		}
		// make the name unique
		String qname = "rmtRtrnQ_"+controller.getComponentName().replaceAll("\\s","_")+"_"+endpoint.getDelegateKey()+"_"+UUID.randomUUID();
		endpoint.setReplyToEndpoint(qname);
		// remote always replies to a JMS temp queue
		endpoint.setTempReplyDestination(true);
		ThreadPoolTaskExecutor threadExecutor = new ThreadPoolTaskExecutor();
		int consumerCount = 1; // default reply queue consumer count
		// check if the DD includes reply queue scaleout for this remote delegate
		if ( remoteDelegate.getReplyScaleout() > 1 ) { 
			
			// in this context the scaleout just means how many consumer threads
			// this listener will start to handle messages arriving into the 
			// temp reply queue.
			consumerCount = remoteDelegate.getReplyScaleout();
			endpoint.setConcurrentReplyConsumers(remoteDelegate.getReplyScaleout());
			if ( endpoint.isCasMultiplier() ) {
				// for remote CM, the listener will use a single thread to receive
				// CASes. This is done to deal with a race condition described in
				// class ConcurrentMessageListener. 
				// if the remote is a cas multiplier, 
				threadExecutor.setCorePoolSize(1);
				threadExecutor.setMaxPoolSize(1);
			} else {
				threadExecutor.setCorePoolSize(consumerCount);
				threadExecutor.setMaxPoolSize(consumerCount);
			}
		}  else {
			threadExecutor.setCorePoolSize(consumerCount);
			threadExecutor.setMaxPoolSize(consumerCount);
		}
		JmsMessageListenerBuilder replyListenerBuilder = 
				new JmsMessageListenerBuilder();

		ActiveMQConnectionFactory factory =
				new ActiveMQConnectionFactory(brokerURL);
		factory.setTrustAllPackages(true);
		ActiveMQPrefetchPolicy pp = new ActiveMQPrefetchPolicy();
		pp.setQueuePrefetch(prefetch);
		
		factory.setPrefetchPolicy(pp);
		// Need a resolver to create temp reply queue. It will be created automatically
		// by Spring.
		TempDestinationResolver resolver = new TempDestinationResolver(controller.getComponentName(),remoteDelegate.getKey());
		resolver.setConnectionFactory(factory);
		threadExecutor.initialize();
		UimaDefaultMessageListenerContainer replyListener =
				replyListenerBuilder.withController(controller)
						.withType(Type.Reply)
						.withInputChannel(inputChannel)
						.withConectionFactory(factory)
						.withThreadPoolExecutor(threadExecutor)
						.withConsumerCount(consumerCount)
						.withTempDestinationResolver(resolver)
						.withEndpoint(endpoint)
						.build();		
		//replyListener.afterPropertiesSet();
		replyListener.start();
		
//		replyListener.setTargetEndpoint(endpoint);

		// there should be one instance of OutputChannel for JMS. Create it, if one does not exist 
		if ( controller.getOutputChannel(ENDPOINT_TYPE.JMS) == null) {
	  		JmsOutputChannel oc = new JmsOutputChannel();
			oc.setController(controller);
//			oc.setServerURI(brokerURL);
			oc.setControllerInputEndpoint("");
			oc.setServiceInputEndpoint("");
			oc.initialize();
			controller.addOutputChannel(oc);
		}
		endpoint.setServerURI(brokerURL);
		System.out.println("......... Service:"+controller.getComponentName()+" Reply Listener Started - Delegate:"+endpoint.getDelegateKey()+" Broker:"+endpoint.getServerURI()+" Endpoint:"+endpoint.getDestination());
	}

	public UimaASService buildAndDeploy(AnalysisEngineDeploymentDescriptionDocument doc, AnalysisEngineDelegate del,
			UimaASJmsService service, ControllerCallbackListener callback) throws Exception {
		// get top level CAS pool to
		CasPoolType cp = getCasPoolConfiguration(doc);

		super.addEnvironmentVariablesFromDD(doc);

		System.setProperty("BrokerURI", service.getBrokerURL());

		initialize(service, cp, Transport.JMS); 
		service.withInProcessCache(super.cache);

		int howMany = howManyInstances(doc);
		AnalysisEngineController topLevelController = createController(del, service.getResourceSpecifier(),
				service.getName(), null, howMany);
		
		// callback will be made when initialization succeeds or fails
		topLevelController.addControllerCallbackListener(callback);

		topLevelController.getServiceInfo().setBrokerURL(service.getBrokerURL());
		topLevelController.setServiceId(service.getId());
		// fetch service definition from DD
		ServiceType s = getService(doc);

		if ( topLevelController.getErrorHandlerChain() == null ) {
			ErrorHandlerChain errorHandlerChain = createServiceErrorHandlers();
			topLevelController.setErrorHandlerChain(errorHandlerChain);
		}
		AsyncPrimitiveErrorConfigurationType pec;
		if (s.getAnalysisEngine() != null && s.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration() != null) {
			pec = s.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration();
		} else {
			pec = addDefaultErrorHandling(s);
		}
		service.withConttroller(topLevelController).withErrorHandlerChain(null);

		configureTopLevelService(topLevelController, service, pec);

		service.build(howMany);

		return service;
	}

	/*
	protected ErrorHandlerChain getErrorHandlerChain(Map<String, ResourceSpecifier> delegates) {

		Map<String, Threshold> processThresholdMap = new HashMap<String, Threshold>();
		Map<String, Threshold> getMetaThresholdMap = new HashMap<String, Threshold>();
		Map<String, Threshold> cpcThresholdMap = new HashMap<String, Threshold>();
		// for each delegate add error handling configuration
		for( Entry<String, ResourceSpecifier> entry : delegates.entrySet() ) {
			// first fetch error handling from deployment descriptor for current delegate
			Object o = delegateMap.get(entry.getKey()); 
			
			GetMetadataErrorsType gmet = null;
			// the DD may not have error handlers specified. This is ok, we just use defaults
			if ( o != null ) {
				if (o instanceof DelegateAnalysisEngineType ) {
					if ( ((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration() != null && 
							((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getGetMetadataErrors() != null ) {
						gmet = ((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getGetMetadataErrors();
						getMetaThresholdMap.put(entry.getKey(), getThreshold(gmet.getErrorAction(), gmet.getMaxRetries()));
					}
					if ( ((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration() != null && 
							((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getProcessCasErrors() != null ) {
						ProcessCasErrorsType pcet = ((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getProcessCasErrors();
						processThresholdMap.put(entry.getKey(), getThreshold(pcet.getThresholdAction(),pcet.getMaxRetries()));
					}
					cpcThresholdMap.put(entry.getKey(), newThreshold());
				} else if ( o instanceof RemoteAnalysisEngineType ){
					if ( ((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration() != null && 
							((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getGetMetadataErrors() != null ) {
						gmet = ((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getGetMetadataErrors();
						getMetaThresholdMap.put(entry.getKey(), getThreshold(gmet.getErrorAction(), gmet.getMaxRetries()));
					}
					if ( ((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration() != null && 
							((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getProcessCasErrors() != null ) {
						ProcessCasErrorsType pcet = ((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getProcessCasErrors();
						processThresholdMap.put(entry.getKey(), getThreshold(pcet.getThresholdAction(),pcet.getMaxRetries()));
					}
					cpcThresholdMap.put(entry.getKey(), newThreshold());
				}
			}
		}
		List<org.apache.uima.aae.error.ErrorHandler> errorHandlers =
				new ArrayList<org.apache.uima.aae.error.ErrorHandler>();
		if ( getMetaThresholdMap.size() > 0 ) {
			errorHandlers.add(new GetMetaErrorHandler(getMetaThresholdMap));
		}
		if ( processThresholdMap.size() > 0 ) {
			errorHandlers.add(new ProcessCasErrorHandler(processThresholdMap));
		}
		if ( cpcThresholdMap.size() > 0 ) {
			errorHandlers.add(new CpcErrorHandler(cpcThresholdMap));
		}
		ErrorHandlerChain errorHandlerChain = new ErrorHandlerChain(errorHandlers);
		return errorHandlerChain;
	}
	
	protected ErrorHandlerChain getErrorHandlerChain(ResourceSpecifier delegate) {

		Map<String, Threshold> processThresholdMap = new HashMap<String, Threshold>();
		Map<String, Threshold> getMetaThresholdMap = new HashMap<String, Threshold>();
		Map<String, Threshold> cpcThresholdMap = new HashMap<String, Threshold>();
		Object o = delegateMap.get(""); 
		GetMetadataErrorsType gmet = null;
		if ( o != null ) {
			if (o instanceof DelegateAnalysisEngineType ) {
				gmet = ((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getGetMetadataErrors();
				getMetaThresholdMap.put("", getThreshold(gmet.getErrorAction(), gmet.getMaxRetries()));
				ProcessCasErrorsType pcet = ((DelegateAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getProcessCasErrors();
				processThresholdMap.put("", getThreshold(pcet.getThresholdAction(),pcet.getMaxRetries()));
				cpcThresholdMap.put("", newThreshold());
			} else if ( o instanceof RemoteAnalysisEngineType ){
				gmet = ((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getGetMetadataErrors();
				getMetaThresholdMap.put("", getThreshold(gmet.getErrorAction(), gmet.getMaxRetries()));
				ProcessCasErrorsType pcet = ((RemoteAnalysisEngineType)o).getAsyncAggregateErrorConfiguration().getProcessCasErrors();
				processThresholdMap.put("", getThreshold(pcet.getThresholdAction(),pcet.getMaxRetries()));
				cpcThresholdMap.put("", newThreshold());
			}
		}
		List<org.apache.uima.aae.error.ErrorHandler> errorHandlers = new ArrayList<org.apache.uima.aae.error.ErrorHandler>();
		if ( getMetaThresholdMap.size() > 0 ) {
			errorHandlers.add(new GetMetaErrorHandler(getMetaThresholdMap));
		}
		if ( processThresholdMap.size() > 0 ) {
			errorHandlers.add(new ProcessCasErrorHandler(processThresholdMap));
		}
		if ( cpcThresholdMap.size() > 0 ) {
			errorHandlers.add(new CpcErrorHandler(cpcThresholdMap));
		}
		ErrorHandlerChain errorHandlerChain = new ErrorHandlerChain(errorHandlers);
		
		return errorHandlerChain;
	
	}
	
	*/
	
	
	private void configureTopLevelService(AnalysisEngineController topLevelController, UimaASJmsService service,
			AsyncPrimitiveErrorConfigurationType pec) throws Exception {
		// ResourceSpecifier resourceSpecifier = service.getResourceSpecifier();
		if (!topLevelController.isPrimitive() && pec != null) {
		//if ( pec != null) {

			ErrorHandlerChain chain = topLevelController.getErrorHandlerChain();
			Iterator<ErrorHandler> handlers = chain.iterator();
			while (handlers.hasNext()) {
				ErrorHandler eh = handlers.next();
				Map<String, Threshold> map = eh.getEndpointThresholdMap();
				if (eh instanceof ProcessCasErrorHandler) {
					if (pec.getProcessCasErrors() != null) {
						map.put("", Thresholds.getThreshold(pec.getProcessCasErrors().getThresholdAction(),
								pec.getProcessCasErrors().getMaxRetries()));
					} else {
						map.put("", Thresholds.newThreshold());
					}
				} else if (eh instanceof GetMetaErrorHandler) {
					if (pec.getCollectionProcessCompleteErrors() != null) {
						map.put("", Thresholds.getThreshold("terminate", 0));
					}
				} else if (eh instanceof CpcErrorHandler) {
					map.put("", Thresholds.getThreshold("", 0));
				}
			}

		}

	}

	public UimaASService build(AnalysisEngineDeploymentDescriptionDocument dd, ControllerCallbackListener callback)
     throws Exception {
		
		

		// get the top level AnalysisEngine descriptor
		String aeDescriptorPath = getAEDescriptorPath(dd);
		// parse AE descriptor
		ResourceSpecifier resourceSpecifier = UimaClassFactory.produceResourceSpecifier(aeDescriptorPath);
		validateDD(dd, resourceSpecifier);
		ServiceType serviceDefinition = getService(dd);
		AnalysisEngineDelegate topLevelService;
		// in DD the analysisEngine specification is optional
		if (serviceDefinition.getAnalysisEngine() == null) {
			topLevelService = new AnalysisEngineDelegate("");
			topLevelService.setResourceSpecifier((AnalysisEngineDescription) resourceSpecifier);
		} else {
			topLevelService = parse(getService(dd).getAnalysisEngine());
		}
		UimaASJmsService service = null;

		String endpoint = resolvePlaceholder(serviceDefinition.getInputQueue().getEndpoint());
		String brokerURL = resolvePlaceholder(serviceDefinition.getInputQueue().getBrokerURL());

		if (resourceSpecifier instanceof AnalysisEngineDescription) {
			AnalysisEngineDescription aeDescriptor = (AnalysisEngineDescription) resourceSpecifier;
			// Create a Top Level Service (TLS) wrapper.
			service = new UimaASJmsService().withName(aeDescriptor.getAnalysisEngineMetaData().getName())
					.withResourceSpecifier(resourceSpecifier).withBrokerURL(brokerURL).withInputQueue(endpoint);

			this.buildAndDeploy(dd, topLevelService, service, callback);
		}
		return service;
	}

}
