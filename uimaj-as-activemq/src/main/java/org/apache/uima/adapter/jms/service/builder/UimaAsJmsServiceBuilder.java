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

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;
import org.apache.uima.aae.OutputChannel;
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
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.aae.service.builder.AbstractUimaAsServiceBuilder;
import org.apache.uima.aae.service.delegate.AnalysisEngineDelegate;
import org.apache.uima.aae.service.delegate.RemoteAnalysisEngineDelegate;
import org.apache.uima.adapter.jms.activemq.JmsInputChannel;
import org.apache.uima.adapter.jms.activemq.JmsOutputChannel;
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
			oc.setServerURI(brokerURL);
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

	private void configureTopLevelService(AnalysisEngineController topLevelController, UimaASJmsService service,
			AsyncPrimitiveErrorConfigurationType pec) throws Exception {
		// ResourceSpecifier resourceSpecifier = service.getResourceSpecifier();
		if (!topLevelController.isPrimitive() && pec != null) {

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
