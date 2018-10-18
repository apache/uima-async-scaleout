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
package org.apache.uima.aae.service.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.OutputChannel;
import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;
import org.apache.uima.aae.component.TopLevelServiceComponent;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController_impl;
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
import org.apache.uima.aae.service.AsynchronousUimaASService;
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.aae.service.UimaAsServiceRegistry;
import org.apache.uima.aae.service.delegate.AnalysisEngineDelegate;
import org.apache.uima.aae.service.delegate.RemoteAnalysisEngineDelegate;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.as.client.DirectInputChannel;
import org.apache.uima.as.client.DirectListener;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectOutputChannel;
import org.apache.uima.as.client.Listener.Type;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType;
import org.apache.uima.resourceSpecifier.CasPoolType;
import org.apache.uima.resourceSpecifier.ServiceType;

public class UimaAsDirectServiceBuilder extends AbstractUimaAsServiceBuilder  {
	private static final String NoParent = "NoParent";
	/*
	 * private static enum FlowControllerType { FIXED }
	 */
	static Map<String, Object> ddAEMap = new HashMap<>();

	private int scaleout = 1;
	public static void main(String[] args) {
		try {
			String tla = "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/descriptors/tutorial/ex4/MeetingDetectorTAEGovNameDetector.xml";
			String ptDescriptor = "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/descriptors/analysis_engine/PersonTitleAnnotator.xml";
			// "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/descriptors/tutorial/ex4/MeetingDetectorTAE.xml";

			// String dd1 =
			// "C:/uima/releases/builds/uima-as/2.8.1/uimaj-as-activemq/src/test/resources/deployment/Deploy_TopLevelBlueJAggregateCM.xml";
			String dd2 = "C:/uima/releases/builds/uima-as/2.8.1/uimaj-as-activemq/src/test/resources/deployment/Deploy_TopAggregateWithInnerAggregateCM.xml";
			String dd = "C:/runtime/uima-as/2.8.1/apache-uima-as-2.8.1-SNAPSHOT/examples/deploy/as/Deploy_MeetingDetectorTAE.xml";

			String dd3 = "../uimaj-as-activemq/src/test/resources/deployment/Deploy_PersonTitleAnnotator.xml";
			String dd4 = "../uimaj-as-activemq/src/test/resources/deployment/Deploy_AggregateAnnotator.xml";
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public UimaASService build(TopLevelServiceComponent topLevelComponent, ControllerCallbackListener callback)
			throws Exception {
		AsynchronousUimaASService service = null;
		
		// is this the only one resource specifier type supported  by the current uima-as?
		if (topLevelComponent.getResourceSpecifier() instanceof AnalysisEngineDescription) {
			AnalysisEngineDescription aeDescriptor = 
					(AnalysisEngineDescription) topLevelComponent.getResourceSpecifier();
			String endpoint = resolvePlaceholder(topLevelComponent.getEndpoint().getEndpoint());
			// Create a Top Level Service (TLS) wrapper. This wrapper may contain
			// references to multiple TLS service instances if the TLS is scaled
			// up.
			service = new AsynchronousUimaASService(endpoint)
					.withName(aeDescriptor.getAnalysisEngineMetaData().getName())
					.withResourceSpecifier(topLevelComponent.getResourceSpecifier())
					.withScaleout(topLevelComponent.getScaleout());

			this.buildAndDeploy(topLevelComponent, service, callback);
			

		}
		return service;
	}
	public UimaASService buildAndDeploy(TopLevelServiceComponent topLevelComponent, AsynchronousUimaASService service, ControllerCallbackListener callback) throws Exception {
		// create ResourceManager, CasManager, and InProcessCache
		initialize(service, topLevelComponent.getComponentCasPool(), Transport.Java); 

		AnalysisEngineController topLevelController = 
				createController(topLevelComponent, callback, service.getId());
		
		//topLevelController.addControllerCallbackListener(callback);

		//topLevelController.setServiceId(service.getId());
		
		service.withInProcessCache(super.cache);
		System.setProperty("BrokerURI", "Direct");
		configureTopLevelService(topLevelController, service);//, topLevelComponent.getScaleout());
		return service;

	}
	
	private DirectOutputChannel outputChannel(AnalysisEngineController topLevelController) throws Exception {
		DirectOutputChannel outputChannel = null;
		if (topLevelController.getOutputChannel(ENDPOINT_TYPE.DIRECT) == null) {
			outputChannel = new DirectOutputChannel().withController(topLevelController);
			topLevelController.addOutputChannel(outputChannel);
		} else {
			outputChannel = (DirectOutputChannel) topLevelController.
					getOutputChannel(ENDPOINT_TYPE.DIRECT);
		}
		return outputChannel;
	}
	private DirectInputChannel inputChannel(AnalysisEngineController topLevelController) throws Exception {
		DirectInputChannel inputChannel;
		if ((topLevelController.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null) {
			inputChannel = new DirectInputChannel(ChannelType.REQUEST_REPLY).
					withController(topLevelController);
			Handler messageHandlerChain = getMessageHandler(topLevelController);
			inputChannel.setMessageHandler(messageHandlerChain);
			topLevelController.addInputChannel(inputChannel);
		} else {
			inputChannel = (DirectInputChannel) topLevelController.
					getInputChannel(ENDPOINT_TYPE.DIRECT);
		}
		return inputChannel;
	}
/*	
	private void configureTopLevelService(AnalysisEngineController topLevelController,
			AsynchronousUimaASService service,  int howMany) throws Exception {
*/
	private void configureTopLevelService(AnalysisEngineController topLevelController,
			AsynchronousUimaASService service) throws Exception {
		
		//addErrorHandling(topLevelController, pec);


		// create a single instance of OutputChannel for Direct communication if
		// necessary
		DirectOutputChannel outputChannel = outputChannel(topLevelController);

		DirectInputChannel inputChannel = inputChannel(topLevelController);

		if ( topLevelController instanceof AggregateAnalysisEngineController ) {
			((AggregateAnalysisEngineController_impl)topLevelController).
				setServiceEndpointName(service.getEndpoint());
		}
		BlockingQueue<DirectMessage> pQ = null; 
		BlockingQueue<DirectMessage> mQ = null; 

		// Lookup queue name in service registry. If this queue exists, the new service
		// being
		// created here will share the same queue to balance the load.
		UimaASService s;
		try {
			s = UimaAsServiceRegistry.getInstance().lookupByEndpoint(service.getEndpoint());
			if ( s instanceof AsynchronousUimaASService) {
				pQ = ((AsynchronousUimaASService) s).getProcessRequestQueue();
				mQ = ((AsynchronousUimaASService) s).getMetaRequestQueue();
			}

		} catch( Exception ee) {
			pQ = service.getProcessRequestQueue();
			mQ = service.getMetaRequestQueue();
		}

		scaleout = service.getScaleout();
		DirectListener processListener = new DirectListener(Type.ProcessCAS).withController(topLevelController)
				.withConsumerThreads(scaleout).withInputChannel(inputChannel).withQueue(pQ).
				initialize();

		DirectListener getMetaListener = new DirectListener(Type.GetMeta).withController(topLevelController)
				.withConsumerThreads(1).withInputChannel(inputChannel).
				withQueue(mQ).initialize();

		addFreeCASListener(service, topLevelController, inputChannel, outputChannel, scaleout );

		inputChannel.registerListener(getMetaListener);
		inputChannel.registerListener(processListener);

		service.withController(topLevelController);
		
	}
	
	
	
	
	
	
	
	
	
	
	/* 
	 * OLD CODE **********************************************************************************
	 */
	
	
	
	public UimaASService build(AnalysisEngineDeploymentDescriptionDocument dd, ControllerCallbackListener callback)
			throws Exception {
		// get the top level AnalysisEngine descriptor path
		String aeDescriptorPath = getAEDescriptorPath(dd);
		// parse AE descriptor
		ResourceSpecifier resourceSpecifier = UimaClassFactory.produceResourceSpecifier(aeDescriptorPath);
		validateDD(dd, resourceSpecifier);
		ServiceType serviceDefinition = getService(dd);
		AnalysisEngineDelegate topLevelService;
		// in DD the analysisEngine specification is optional
		if (serviceDefinition.getAnalysisEngine() == null) {
			topLevelService = new AnalysisEngineDelegate();
			topLevelService.setResourceSpecifier((AnalysisEngineDescription) resourceSpecifier);
		} else {
			topLevelService = parse(getService(dd).getAnalysisEngine());
		}
		// resolve if placeholder used
		String endpoint = resolvePlaceholder(serviceDefinition.getInputQueue().getEndpoint());
		
		int howMany = 1;  // default
		AsynchronousUimaASService service = null;
		
		// is this the only one resource specifier type supported  by the current uima-as?
		if (resourceSpecifier instanceof AnalysisEngineDescription) {
			AnalysisEngineDescription aeDescriptor = (AnalysisEngineDescription) resourceSpecifier;
			// Create a Top Level Service (TLS) wrapper. This wrapper may contain
			// references to multiple TLS service instances if the TLS is scaled
			// up.
			service = new AsynchronousUimaASService(endpoint)
					.withName(aeDescriptor.getAnalysisEngineMetaData().getName())
					.withResourceSpecifier(resourceSpecifier).withScaleout(howMany);

			this.buildAndDeploy(dd, topLevelService, service, callback);
			

		}
		return service;
	}

	public UimaASService buildAndDeploy(AnalysisEngineDeploymentDescriptionDocument doc, AnalysisEngineDelegate delegate,
			AsynchronousUimaASService service, ControllerCallbackListener callback) throws Exception {
		// get top level CAS pool to
		CasPoolType cp = getCasPoolConfiguration(doc);

		super.addEnvironmentVariablesFromDD(doc);

		System.setProperty("BrokerURI", "Direct");
		// NEED TO INJECT shared InProcessCache here
		//UimaAsServiceRegistry.getInstance().lookupById("");
		initialize(service, cp, Transport.Java); 
		
		service.withInProcessCache(super.cache);
		// Number of Analysis Engine instances
		int howMany = howManyInstances(doc);
		if ( howMany == 0 ) {
			throw new IllegalArgumentException("Number of instances should be greater than zero - check dd");
		}
		AnalysisEngineController topLevelController = createController(delegate, service.getResourceSpecifier(),
				service.getEndpoint(), null, howMany);
		topLevelController.addControllerCallbackListener(callback);

		topLevelController.setServiceId(service.getId());

		ServiceType s = getService(doc);

		AsyncPrimitiveErrorConfigurationType pec;
		if (s.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration() != null) {
			pec = s.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration();

		} else {
			pec = addDefaultErrorHandling(s);
		}

		configureTopLevelService(topLevelController, service, pec, howMany);

		return service;
	}

	private void addErrorHandling(AnalysisEngineController topLevelController, AsyncPrimitiveErrorConfigurationType pec) {
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
	private void configureTopLevelService(AnalysisEngineController topLevelController,
			AsynchronousUimaASService service, AsyncPrimitiveErrorConfigurationType pec, int howMany) throws Exception {
		addErrorHandling(topLevelController, pec);


		// create a single instance of OutputChannel for Direct communication if
		// necessary
		DirectOutputChannel outputChannel = null;
		if (topLevelController.getOutputChannel(ENDPOINT_TYPE.DIRECT) == null) {
			outputChannel = new DirectOutputChannel().withController(topLevelController);
			topLevelController.addOutputChannel(outputChannel);
		} else {
			outputChannel = (DirectOutputChannel) topLevelController.getOutputChannel(ENDPOINT_TYPE.DIRECT);
		}

		DirectInputChannel inputChannel;
		if ((topLevelController.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null) {
			inputChannel = new DirectInputChannel(ChannelType.REQUEST_REPLY).withController(topLevelController);
			Handler messageHandlerChain = getMessageHandler(topLevelController);
			inputChannel.setMessageHandler(messageHandlerChain);
			topLevelController.addInputChannel(inputChannel);
		} else {
			inputChannel = (DirectInputChannel) topLevelController.getInputChannel(ENDPOINT_TYPE.DIRECT);
		}

		if ( topLevelController instanceof AggregateAnalysisEngineController ) {
			((AggregateAnalysisEngineController_impl)topLevelController).setServiceEndpointName(service.getEndpoint());
		}
		BlockingQueue<DirectMessage> pQ = null; // service.getProcessRequestQueue();
		BlockingQueue<DirectMessage> mQ = null; //service.getMetaRequestQueue();

		// Lookup queue name in service registry. If this queue exists, the new service
		// being
		// created here will share the same queue to balance the load.
		UimaASService s;
		try {
			s = UimaAsServiceRegistry.getInstance().lookupByEndpoint(service.getEndpoint());
			if ( s instanceof AsynchronousUimaASService) {
//				if (s != null && s instanceof AsynchronousUimaASService) {
				pQ = ((AsynchronousUimaASService) s).getProcessRequestQueue();
				mQ = ((AsynchronousUimaASService) s).getMetaRequestQueue();
			}

		} catch( Exception ee) {
			pQ = service.getProcessRequestQueue();
			mQ = service.getMetaRequestQueue();
		}


		scaleout = howMany;
		DirectListener processListener = new DirectListener(Type.ProcessCAS).withController(topLevelController)
				.withConsumerThreads(scaleout).withInputChannel(inputChannel).withQueue(pQ).
				// withQueue(service.getProcessRequestQueue()).
				initialize();

		DirectListener getMetaListener = new DirectListener(Type.GetMeta).withController(topLevelController)
				.withConsumerThreads(1).withInputChannel(inputChannel).
				// withQueue(service.getMetaRequestQueue()).
				withQueue(mQ).initialize();

		addFreeCASListener(service, topLevelController, inputChannel, outputChannel, scaleout );

		inputChannel.registerListener(getMetaListener);
		inputChannel.registerListener(processListener);

		service.withController(topLevelController);
		
	}

	private void addFreeCASListener( AsynchronousUimaASService service, AnalysisEngineController controller, 
			DirectInputChannel inputChannel, DirectOutputChannel outputChannel, int scaleout ) throws Exception {
		DirectListener freCASChannelListener = null;
		if (controller.isCasMultiplier()) {
			freCASChannelListener = new DirectListener(Type.FreeCAS).withController(controller)
					.withConsumerThreads(scaleout).withInputChannel(inputChannel).withQueue(service.getFreeCasQueue())
					.initialize();
			inputChannel.registerListener(freCASChannelListener);
			outputChannel.setFreeCASQueue(service.getFreeCasQueue());
		}
	}
	public static InputChannel createInputChannel(ChannelType type) {
		return new DirectInputChannel(type);
	}

	
	public static OutputChannel createOutputChannel(AnalysisEngineController controller) {
		return  new DirectOutputChannel().withController(controller);
	}
	private void createDirectOutputChannel(AggregateAnalysisEngineController controller) throws Exception {
		// there should be one instance of OutputChannel for DIRECT. Create it, if one does not exist 
		if ( controller.getOutputChannel(ENDPOINT_TYPE.DIRECT) == null) {
			OutputChannel oc = createOutputChannel(controller);
			oc.initialize();
			controller.addOutputChannel(oc);
		}
	}
	/*
	private DirectInputChannel createDirectInputChannel(AggregateAnalysisEngineController controller) throws Exception {
		DirectInputChannel inputChannel;
	    if ( (controller.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null ) {
	    	inputChannel = (DirectInputChannel)createInputChannel(ChannelType.REQUEST_REPLY);
	    	((DirectInputChannel)inputChannel).withController(controller);
		    Handler messageHandlerChain = getMessageHandler(controller);
		    inputChannel.setMessageHandler(messageHandlerChain);
		    controller.addInputChannel(inputChannel);
	    } else {
	    	inputChannel = (DirectInputChannel)controller.getInputChannel(ENDPOINT_TYPE.DIRECT );
	    }
	    return inputChannel;
	}
*/
	@Override
	protected void addListenerForReplyHandling( AggregateAnalysisEngineController controller, Endpoint_impl endpoint, RemoteAnalysisEngineDelegate remoteDelegate) throws Exception {
		// there should be one instance of OutputChannel for DIRECT. Create it, if one does not exist 
		createDirectOutputChannel(controller);
		
	    DirectInputChannel inputChannel;
	    if ( (controller.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null ) {
	    	inputChannel = 
	    			new DirectInputChannel(ChannelType.REQUEST_REPLY).
	    			withController(controller);
		    Handler messageHandlerChain = getMessageHandler(controller);
		    inputChannel.setMessageHandler(messageHandlerChain);
		    controller.addInputChannel(inputChannel);
	    } else {
	    	inputChannel = (DirectInputChannel)controller.getInputChannel(ENDPOINT_TYPE.DIRECT );
	    }
	    BlockingQueue<DirectMessage> replyQueue = 
	    		new LinkedBlockingQueue<>();
	    DirectListener processListener = 
	    		new DirectListener(Type.Reply).
	    			withController(controller).
	    			withConsumerThreads(remoteDelegate.getReplyScaleout()).
	    			withInputChannel(inputChannel).
		            withQueue(replyQueue).
		            initialize(); 
	    inputChannel.registerListener(processListener);
	    
	    UimaASService service =
	    		UimaAsServiceRegistry.getInstance().lookupByEndpoint(endpoint.getEndpoint());
	    if ( service != null && service instanceof AsynchronousUimaASService ) {
		    endpoint.setGetMetaDestination(((AsynchronousUimaASService)service).getMetaRequestQueue());
		    endpoint.setDestination(((AsynchronousUimaASService)service).getProcessRequestQueue());
		    endpoint.setReplyDestination(replyQueue);
	    }
	    
	}

}
