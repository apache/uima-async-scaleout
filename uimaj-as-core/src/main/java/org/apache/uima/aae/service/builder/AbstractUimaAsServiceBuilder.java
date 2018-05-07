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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.OutputChannel;
import org.apache.uima.aae.UimaASUtils;
import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController_impl;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.DelegateEndpoint;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.Endpoint_impl;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController_impl;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.ErrorHandler;
import org.apache.uima.aae.error.ErrorHandlerChain;
import org.apache.uima.aae.error.Threshold;
import org.apache.uima.aae.error.Thresholds;
import org.apache.uima.aae.error.Thresholds.Action;
import org.apache.uima.aae.error.UimaAsDelegateException;
import org.apache.uima.aae.error.handler.CpcErrorHandler;
import org.apache.uima.aae.error.handler.GetMetaErrorHandler;
import org.apache.uima.aae.error.handler.ProcessCasErrorHandler;
import org.apache.uima.aae.handler.Handler;
import org.apache.uima.aae.handler.input.MetadataRequestHandler_impl;
import org.apache.uima.aae.handler.input.MetadataResponseHandler_impl;
import org.apache.uima.aae.handler.input.ProcessRequestHandler_impl;
import org.apache.uima.aae.handler.input.ProcessResponseHandler;
import org.apache.uima.aae.service.AsynchronousUimaASService;
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.aae.service.delegate.AggregateAnalysisEngineDelegate;
import org.apache.uima.aae.service.delegate.AnalysisEngineDelegate;
import org.apache.uima.aae.service.delegate.CasMultiplierNature;
import org.apache.uima.aae.service.delegate.RemoteAnalysisEngineDelegate;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.FlowConstraints;
import org.apache.uima.as.client.DirectInputChannel;
import org.apache.uima.as.client.DirectListener;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectOutputChannel;
import org.apache.uima.as.client.Listener.Type;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType;
import org.apache.uima.resourceSpecifier.AnalysisEngineType;
import org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType;
import org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType;
import org.apache.uima.resourceSpecifier.CasMultiplierType;
import org.apache.uima.resourceSpecifier.CasPoolType;
import org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType;
import org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType;
import org.apache.uima.resourceSpecifier.DelegatesType;
import org.apache.uima.resourceSpecifier.EnvironmentVariableType;
import org.apache.uima.resourceSpecifier.EnvironmentVariablesType;
import org.apache.uima.resourceSpecifier.ImportType;
import org.apache.uima.resourceSpecifier.InputQueueType;
import org.apache.uima.resourceSpecifier.ProcessCasErrorsType;
import org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType;
import org.apache.uima.resourceSpecifier.ScaleoutType;
import org.apache.uima.resourceSpecifier.ServiceType;
import org.apache.uima.resourceSpecifier.TopDescriptorType;
import org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType;
import org.apache.xmlbeans.XmlDocumentProperties;

public abstract class AbstractUimaAsServiceBuilder implements ServiceBuilder {
	protected InProcessCache cache;
	protected AsynchAECasManager_impl casManager;
    protected ResourceManager resourceManager;
    private static final String NoParent= "NoParent";
    private  enum FlowControllerType {
		FIXED
	}
    
    protected abstract void addListenerForReplyHandling( AggregateAnalysisEngineController controller, Endpoint_impl endpoint, RemoteAnalysisEngineDelegate remoteDelegate) throws Exception;
    
    public AsyncPrimitiveErrorConfigurationType addDefaultErrorHandling(ServiceType s) {
    	AsyncPrimitiveErrorConfigurationType pec;
    	if ( s.getAnalysisEngine() == null ) {
    		s.addNewAnalysisEngine();
    	}
    	pec = s.getAnalysisEngine().addNewAsyncPrimitiveErrorConfiguration();
		// Create default error handling for process CAS requests
		// By default, any error will result in process termination
		ProcessCasErrorsType pcet = pec.addNewProcessCasErrors();
		pcet.setContinueOnRetryFailure("false");
		pcet.setMaxRetries(0);
		pcet.setThresholdAction("terminate");
		pcet.setThresholdCount(0);
		pcet.setThresholdWindow(0);
		pcet.setTimeout(0); // no timeout

		CollectionProcessCompleteErrorsType cpcet = pec.addNewCollectionProcessCompleteErrors();
		cpcet.setTimeout(0);
    	
    	return pec;
    }
    
    /**
     * A DD may optionally specify env vars for this process. If specified in DD, add
     * all env vars to this process environment.
     * 
     * @param dd - deployment descriptor object
     */
    public void addEnvironmentVariablesFromDD(AnalysisEngineDeploymentDescriptionDocument dd) {
    	
    	ServiceType st = getService(dd);
    	if ( st != null && st.getEnvironmentVariables() != null ) {
        	EnvironmentVariablesType vars =
        			st.getEnvironmentVariables();
        	for( EnvironmentVariableType envVar : vars.getEnvironmentVariableArray() ) {
        		System.getenv().put(envVar.getName(), envVar.getStringValue());
        	}
    	}
    }
    /**
     * Add JmsInputChannel to handle replies from remote delegates
     * 
     * @param controller
     * @param type
     * @throws Exception
     */
    private void addInputChannelForRemoteReplies(AnalysisEngineController controller, ChannelType type) throws Exception {
/*
 
     	DISABLE THIS FOR NOW. ENABLE WHEN READY TO TEST AGGRREGATE WITH
     	REMOTE DELEGATE
     	
     	
    	// This needs to be JmsInputChannel since it will process remote replies which are JMS based
    	InputChannel inputChannel = UimaAsJmsServiceBuilder.createInputChannel(type);
    	// setup bi-directional link between controller and an input channel
    	inputChannel.setController(controller);
    	controller.addInputChannel(inputChannel);
    			
    	inputChannel.setEndpointName("");
    	// add message handlers. These will handle GetMeta, Process, CPC replies
    	inputChannel.setMessageHandler(getMessageHandler(controller));
*/
 
    }
    /**
     * Check if a given delegate is defined in a DD. This is optional in a DD, but
     * if present there is a configuration which may override default scalout, error
     * handling (remotes only), and if the delegate is remote.
     * 
     * @param targetDelegateKey - key of the delegate to find
     * @param ddAggregate - DD aggregate instance
     * @return
     */
    private AnalysisEngineDelegate getMatchingDelegateFromDD(String targetDelegateKey, AggregateAnalysisEngineDelegate ddAggregate) {
		if ( ddAggregate.getDelegates() != null ) {
	    	for( AnalysisEngineDelegate delegate : ddAggregate.getDelegates() ) {
				if ( targetDelegateKey.equals(delegate.getKey())) {
					return delegate;
				}
			}
		}
		
		return null;
    }
    private void addDelegateDefaultErrorHandling(AnalysisEngineController controller, String delegatKey) {
    	ErrorHandlerChain erc = controller.getErrorHandlerChain();
    	for( ErrorHandler eh : erc ) {
    		if ( !eh.getEndpointThresholdMap().containsKey(delegatKey) ) {
    			// add default error handling
    			eh.getEndpointThresholdMap().put(delegatKey, Thresholds.newThreshold());
    		}
    	}	
    }
    private OutputChannel getOutputChannel(AnalysisEngineController controller ) throws Exception {
    	OutputChannel outputChannel = null;
//		if (controller.getOutputChannel(ENDPOINT_TYPE.DIRECT) == null) {
			outputChannel = UimaAsDirectServiceBuilder.createOutputChannel(controller);
			controller.addOutputChannel(outputChannel);
//		} else {
//			outputChannel = controller.getOutputChannel(ENDPOINT_TYPE.DIRECT);
//		}
    	
		return outputChannel;
    }
    private InputChannel getInputChannel(AnalysisEngineController controller) throws Exception {
    	InputChannel inputChannel;
//    	if ((controller.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null) {
    		inputChannel = UimaAsDirectServiceBuilder.createInputChannel(ChannelType.REQUEST_REPLY);
    		inputChannel.setController(controller); 
			
//		} else {
//			inputChannel = controller.getInputChannel(ENDPOINT_TYPE.DIRECT);
//		}
		return inputChannel;
    }
    private int getScaleout(AnalysisEngineDelegate d) {
		int scaleout = 1;  // default
		if ( d != null && d.getScaleout() > 1 ) {
			// fetch scaleout from the DD spec for this delegate
			scaleout = d.getScaleout();
		}
		return scaleout;
    }
    private ScaleoutType getScaleout(AnalysisEngineDeploymentDescriptionDocument d) {
    	ScaleoutType scaleoutConfiguration = getService(d).getAnalysisEngine().getScaleout();;
    	if ( scaleoutConfiguration == null ) {
    		scaleoutConfiguration = getService(d).getAnalysisEngine().addNewScaleout();
    		scaleoutConfiguration.setNumberOfInstances(1);  // default
    	}
    	/*
		if ( getService(d).getAnalysisEngine() != null &&
			getService(d).getAnalysisEngine().getScaleout() != null	) {
			// fetch scaleout from the DD spec for this delegate
			scaleoutConfiguration = getService(d).getAnalysisEngine().getScaleout();
		} else {
			
		}
		*/
		return scaleoutConfiguration;
    }
    
//	dd.getAnalysisEngineDeploymentDescription().getDeployment().getService().getAnalysisEngine().getScaleout();

    private int getReplyScaleout(AnalysisEngineDelegate d) {
		int scaleout = 1;  // default
		if ( d != null && d.getReplyScaleout() > 1 ) {
			// fetch scaleout from the DD spec for this delegate
			scaleout = d.getReplyScaleout();
		}
		return scaleout;
    }
    private void setDelegateDestinations(AnalysisEngineController controller, AsynchronousUimaASService service, DirectListener replyListener) {
		Map<String,Endpoint> aggregateDelegateEndpoints =
				((AggregateAnalysisEngineController_impl)controller.getParentController()).getDestinations();
		Endpoint endpoint = aggregateDelegateEndpoints.get(controller.getKey());
		endpoint.setReplyDestination(replyListener.getEndpoint());
		endpoint.setGetMetaDestination(service.getMetaRequestQueue());
		endpoint.setDestination(service.getProcessRequestQueue());

    }
    private DirectListener addDelegateReplyListener(AnalysisEngineController controller, AnalysisEngineDelegate d) throws Exception {
		DirectInputChannel parentInputChannel;
		// create parent controller's input channel if necessary
		if ((controller.getParentController().getInputChannel(ENDPOINT_TYPE.DIRECT)) == null) {
			// create delegate 
			parentInputChannel = new DirectInputChannel(ChannelType.REQUEST_REPLY).
					withController(controller.getParentController());
			Handler messageHandlerChain = getMessageHandler(controller.getParentController());
			parentInputChannel.setMessageHandler(messageHandlerChain);
			controller.getParentController().addInputChannel(parentInputChannel);
		} else {
			parentInputChannel = (DirectInputChannel) controller.
					getParentController().getInputChannel(ENDPOINT_TYPE.DIRECT);
		}
		int replyScaleout = 1;
		if ( d != null && d.getReplyScaleout() > 1) {
			replyScaleout = d.getReplyScaleout();
		}

		// USE FACTORY HERE. CHANGE DirectListener to interface
		// DirectListner replyListener = DirectListenerFactory.newReplyListener();
		DirectListener replyListener = new DirectListener(Type.Reply).
				withController(controller.getParentController()).
				withConsumerThreads(replyScaleout).
				withInputChannel(parentInputChannel).
				withQueue(new LinkedBlockingQueue<DirectMessage>()).
				withName(controller.getKey()).
				initialize();
		parentInputChannel.registerListener(replyListener);
		return replyListener;
    }

    private UimaASService createUimaASServiceWrapper(AnalysisEngineController controller, AnalysisEngineDelegate d) throws Exception {
    
    	AsynchronousUimaASService service = 
    			new AsynchronousUimaASService(controller.getComponentName()).withController(controller);
    	// Need an OutputChannel to dispatch messages from this service
    	OutputChannel outputChannel;
		if ( ( outputChannel = controller.getOutputChannel(ENDPOINT_TYPE.DIRECT)) == null) {
			outputChannel = getOutputChannel(controller);
		}
    	 
    	// Need an InputChannel to handle incoming messages
    	InputChannel inputChannel;
    	if ((inputChannel = controller.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null) {
    		inputChannel = getInputChannel(controller);
    		Handler messageHandlerChain = getMessageHandler(controller);
			inputChannel.setMessageHandler(messageHandlerChain);
			controller.addInputChannel(inputChannel);
    	}

		// add reply queue listener to the parent aggregate controller
		if ( !controller.isTopLevelComponent() ) {
			// For every delegate the parent controller needs a reply listener.
			DirectListener replyListener = 
					addDelegateReplyListener(controller, d);
			// add process, getMeta, reply queues to an endpoint
			setDelegateDestinations(controller, service, replyListener);
		}

		DirectListener processListener = new DirectListener(Type.ProcessCAS).
				withController(controller).
				withConsumerThreads(getScaleout(d)).
				withInputChannel((DirectInputChannel)inputChannel).
				withQueue(service.getProcessRequestQueue()).
				initialize();
		inputChannel.registerListener(processListener);

		DirectListener getMetaListener = new DirectListener(Type.GetMeta).
				withController(controller).
				withConsumerThreads(getReplyScaleout(d)).
				withInputChannel((DirectInputChannel)inputChannel).
				withQueue(service.getMetaRequestQueue()).initialize();
		inputChannel.registerListener(getMetaListener);

		if (controller.isCasMultiplier()) {
			DirectListener freCASChannelListener = 
					new DirectListener(Type.FreeCAS).
					withController(controller).
					withConsumerThreads(getScaleout(d)).
					withInputChannel((DirectInputChannel)inputChannel).
					withQueue(service.getFreeCasQueue()).
					initialize();
			inputChannel.registerListener(freCASChannelListener);
			((DirectOutputChannel)outputChannel).setFreeCASQueue(service.getFreeCasQueue());
		}
    	
    	return service;
    }
    private boolean isAggregate(AnalysisEngineDelegate d, ResourceSpecifier resourceSpecifier) {
    	boolean aggregate = false;
    	if (resourceSpecifier instanceof AnalysisEngineDescription ) {
    		AnalysisEngineDescription aeDescriptor = (AnalysisEngineDescription) resourceSpecifier;
    		if ( d != null ) {
    			if ((d instanceof AggregateAnalysisEngineDelegate) || 
    				(d.isAsync() && !d.isPrimitive()) ) {
    				aggregate = true;
    			}
    		} else if ( !aeDescriptor.isPrimitive() ) {
    			aggregate = true;
    		}
    	}
    	return aggregate;
    }

    private void createDelegateControllers( AnalysisEngineDelegate d, Map<String, ResourceSpecifier> delegates, AnalysisEngineController controller) throws Exception {
		for (Map.Entry<String, ResourceSpecifier> delegate : delegates.entrySet()) {
			// if error handling threshold has not been defined for the delegate, add 
			// default thresholds.
			addDelegateDefaultErrorHandling(controller, delegate.getKey());
			AnalysisEngineDelegate aed = null;
			int scaleout = 1;
			// fetch delegate configuration from a DD if it exists. The DD configuration 
			// is optional but may exist to support default overrides for scaleout, 
			// error handling(remotes only), and connectivity to a remote (broker,queue)
			if ( d != null ) {
				aed = getMatchingDelegateFromDD(delegate.getKey(), (AggregateAnalysisEngineDelegate)d);
			}
			// check if DD configuration exists and this delegate has been configured as a remote
   	        if ( aed != null && aed.isRemote() ) { 
   	        	Endpoint_impl endpoint = 
   	        			(Endpoint_impl)((AggregateAnalysisEngineController)controller).getDestinations().get(delegate.getKey());
   	        	if ( endpoint.getServerURI().equals("java")) {
   	        		endpoint.setJavaRemote();
   	        	}
   				configureRemoteDelegate((RemoteAnalysisEngineDelegate)aed, (AggregateAnalysisEngineController)controller, endpoint);
   			} else {
   				// Not a remote, but it still may have DD configuration to 
				if ( aed != null ) {
					scaleout = aed.getScaleout();
				}
				if ( controller.getOutputChannel(ENDPOINT_TYPE.DIRECT) == null) {
					OutputChannel oc = new DirectOutputChannel().withController(controller);
					oc.initialize();
					controller.addOutputChannel(oc);
				}
				    if ( (controller.getInputChannel(ENDPOINT_TYPE.DIRECT)) == null ) {
				    	DirectInputChannel inputChannel = 
				    			new DirectInputChannel(ChannelType.REQUEST_REPLY).
				    			withController(controller);
					    Handler messageHandlerChain = getMessageHandler(controller);
					    inputChannel.setMessageHandler(messageHandlerChain);
					    controller.addInputChannel(inputChannel);
				    }
   				ResourceSpecifier delegateResourceSpecifier = UimaClassFactory.produceResourceSpecifier(delegate.getValue().getSourceUrlString());
   				createController( aed, delegateResourceSpecifier, delegate.getKey(), controller, scaleout);
			}
		}

    }
    /**
     * Recursively walks through the AE descriptor creating instances of AnalysisEngineController
     * and linking them in parent-child tree. 
     * 
     * @param d - wrapper around delegate defined in DD (may be null)
     * @param resourceSpecifier - AE descriptor specifier
     * @param name - name of the delegate
     * @param parentController - reference to a parent controller. TopLevel has no parent
     * @param howManyInstances - scalout for the delegate
     * 
     * @return
     * @throws Exception
     */
    public AnalysisEngineController createController( AnalysisEngineDelegate d, ResourceSpecifier resourceSpecifier, String name, AnalysisEngineController parentController, int howManyInstances) throws Exception {
    	AnalysisEngineController controller = null;
     	System.out.println("---------Controller:"+name+" resourceSpecifier:"+resourceSpecifier.getClass().getName()+" ResourceCreationSpecifier:"+(resourceSpecifier instanceof ResourceCreationSpecifier) );

     	if ( isAggregate(d, resourceSpecifier)) {
    		AnalysisEngineDescription aeDescriptor = (AnalysisEngineDescription) resourceSpecifier;
    		// the following should be removed at some point. Its just
    		// catching unexpected behaviour which should not happen
    		// after the code is debugged
    		if ( !(d instanceof AggregateAnalysisEngineDelegate) ) {
    			System.out.println("............ what?!");
    			Thread.dumpStack();
    		}
    		AggregateAnalysisEngineDelegate aggregateDeleagte = null;
    		
    		if ( d != null ) {
    			aggregateDeleagte = (AggregateAnalysisEngineDelegate)d;
    		}
    		// add an endpoint for each delegate in this aggregate. The endpoint Map is required
    		// during initialization of an aggregate controller.
    		Map<String, Endpoint> endpoints = getDelegateEndpoints( aeDescriptor, aggregateDeleagte );
    		
    		controller = new AggregateAnalysisEngineController_impl(parentController, name, resourceSpecifier.getSourceUrlString(), casManager, cache, endpoints);
    		
    		if ( aggregateDeleagte != null ) {
        		controller.setErrorHandlerChain(aggregateDeleagte.getDelegateErrorHandlerChain());
    		}
    		addFlowController((AggregateAnalysisEngineController)controller, aeDescriptor);
     		
    		Map<String, ResourceSpecifier> delegates = aeDescriptor.getDelegateAnalysisEngineSpecifiers();
    	
    		createDelegateControllers(aggregateDeleagte, delegates, controller);
     	} else {
       		controller = new PrimitiveAnalysisEngineController_impl(parentController, name, resourceSpecifier.getSourceUrlString(),casManager, cache, 10, howManyInstances);
      	}
   	    if ( !controller.isTopLevelComponent() ) {
       		UimaASService service = createUimaASServiceWrapper(controller, d);
    	    service.start();
	    }

    	return controller;
    }
	private void configureRemoteDelegate( RemoteAnalysisEngineDelegate remote,AggregateAnalysisEngineController aggregateController, Endpoint_impl endpoint) throws Exception {
		// check if this remote is actually deployed in the same process (collocated). 
		// If is not local, then it must be a JMS service.
		if ( remote.isCollocated() ) {
			// 
		 //   addListenerForReplyHandling(aggregateController, endpoint, remote );

		} else {
			// Remote delegate can only be reached via JMS. To communicate with such delegate
			// the aggregate needs JMS based listener, JmsInputChannel and JmsOutputChannel.
			//String brokerURL = ((RemoteAnalysisEngineType)o).getInputQueue().getBrokerURL();
			//int prefetch = ((RemoteAnalysisEngineType)o).getInputQueue().getPrefetch();
			//endpoint.setRemote(true);
//		    addJmsListenerForReplyHandling(aggregateController, endpoint, remote );
			// Single instance of JMS input channel is needed to handle all remote delegate replies. 
			if ( aggregateController.getInputChannel(ENDPOINT_TYPE.JMS) == null ) {
				// configure JMS input channel and add it to the controller
				addInputChannelForRemoteReplies(aggregateController,ChannelType.REPLY);
			}
			// each remote delegate needs a dedicated JMS Listener to receive replies 
//		    addJmsListenerForReplyHandling(aggregateController, endpoint, remote );

		}
	    addListenerForReplyHandling(aggregateController, endpoint, remote );

	}
	protected void addFlowController(AggregateAnalysisEngineController aggregateController, AnalysisEngineDescription rs) throws Exception {
		String fcDescriptor=null;
		System.out.println(rs.getSourceUrlString());
		
		// first check if the AE aggregate descriptor defines a custom flow controller  
		if ( rs.getFlowControllerDeclaration() != null ) {
			if( rs.getFlowControllerDeclaration().getImport() == null ) {
				System.out.println("........................ What!!!!");
			}
		
			// the fc is either imported by name or a location
			fcDescriptor = rs.getFlowControllerDeclaration().getImport().getName();
		    if ( fcDescriptor == null ) {
		    	fcDescriptor = rs.getFlowControllerDeclaration().getImport().getLocation();
		    	
		    	fcDescriptor = UimaASUtils.fixPath(rs.getSourceUrlString(), fcDescriptor);
		    } else {
		    	throw new RuntimeException("*** Internal error - Invalid flowController specification - descriptor:"+rs.getFlowControllerDeclaration().getSourceUrlString());
		    }
		} else {
			FlowConstraints fc = rs.getAnalysisEngineMetaData().getFlowConstraints();
			if (FlowControllerType.FIXED.name().equals(fc.getFlowConstraintsType()) ) {
				fcDescriptor = ("*importByName:org.apache.uima.flow.FixedFlowController");
			}
		}
		((AggregateAnalysisEngineController_impl)aggregateController).setFlowControllerDescriptor(fcDescriptor);

	}

	protected Handler getMessageHandler(AnalysisEngineController controller) {
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
	protected void validateColocatedDelegates(String parentContextName, DelegatesType dt, ResourceSpecifier aggregateResourceSpecifier) throws Exception {
		StringBuilder context = new StringBuilder(parentContextName).append("/");
		for( DelegateAnalysisEngineType delegate : dt.getAnalysisEngineArray()) {
			StringBuilder delegateContext = new StringBuilder(context.toString());
			delegateContext.append(delegate.getKey());
			if ( delegate.getKey() == null ||  delegate.getKey().trim().isEmpty()) {
				StringBuilder sb = 
						new StringBuilder("*** ERROR ** The delegate key in the deployment descriptor not specified. The delegate key is required and must match a delegate in the referenced descriptor");
				throw new AsynchAEException(sb.toString()); 
			}

			String asyncValue = delegate.getAsync();
			if ( asyncValue == null || asyncValue.equalsIgnoreCase("false") ) {
				
				validateInternalReplyQueueScaleout(delegateContext.toString(), delegate);
				validateInputQueueScaleout(delegateContext.toString(), delegate);
			}
		    ResourceSpecifier delegateResourceSpecifier = getDelegateResourceSpecifier(delegate.getKey(), aggregateResourceSpecifier );
		    
			if ( delegateResourceSpecifier == null ) {
				StringBuilder sb = 
						new StringBuilder("*** ERROR ** The delegate in the deployment descriptor with key=").
						append(context.toString()).append(delegate.getKey()).append(" does not match any delegates in the referenced descriptor");
				throw new AsynchAEException(sb.toString());

			}
			if ( delegate.isSetCasMultiplier() && !isCasMultiplier(delegateResourceSpecifier) ) {
				StringBuilder sb = 
						new StringBuilder("*** ERROR ** The delegate in the deployment descriptor with key=").
						append(context.toString()).append(delegate.getKey()).append(" specifies a casMultiplier element, but the analysisEngine is not a CAS multiplier");
				throw new AsynchAEException(sb.toString());

			}
		    
			checkIfDelegateServiceAsyncValid(context.toString(), delegate, delegateResourceSpecifier);
		    

		    if ( delegate.getDelegates() != null ) {
		    	// check thresholds in aggregate error configuration
		    	validateDelegateErrorConfiguration(delegateContext.toString(), delegate.getKey(), delegate.getAsyncAggregateErrorConfiguration());
				
		    	// primitive delegate cannot have delegates
		    	if ( asyncValue != null && asyncValue.equalsIgnoreCase("false") ) {
					StringBuilder sb = 
							new StringBuilder("*** ERROR ** The delegate in the deployment descriptor with key=").
							append(context.toString()).append(delegate.getKey()).append(" specifies false for the async attribute, but contains a delegates element, which is not allowed in this case.");
					throw new AsynchAEException(sb.toString());

		    	}
		    	validateDelegates(delegateContext.toString(), delegate, delegateResourceSpecifier);
		    }
		}
		    
		    
			//checkDelegateKey(delegate.getKey(), delegateResourceSpecifier);
		
	}
	private void validateTopLevelErrorConfiguration( AsyncPrimitiveErrorConfigurationType primitiveErrorConfiguration) throws Exception  {
		if ( primitiveErrorConfiguration  != null ) {
			validateProcessErrorConfiguration("","",primitiveErrorConfiguration.getProcessCasErrors() );
		}
		 
	}
	private void validateDelegateErrorConfiguration(String context, String delegateKey, AsyncAggregateErrorConfigurationType aggregateErrorConfiguration) throws Exception  {
		if ( aggregateErrorConfiguration  != null ) {
			validateProcessErrorConfiguration(context,delegateKey,aggregateErrorConfiguration.getProcessCasErrors() );
		}
	}
	private void validateProcessErrorConfiguration(String context, String key, ProcessCasErrorsType processErrorCongfiguration) throws Exception{
		if ( processErrorCongfiguration != null &&
				 processErrorCongfiguration.getThresholdWindow() > 0 &&
						(processErrorCongfiguration.getThresholdCount() >
				         processErrorCongfiguration.getThresholdWindow() ) ) {
					StringBuilder sb = 
							new StringBuilder("*** ERROR ** The delegate in the deployment descriptor with key=").
							append(context).
							append( (context.endsWith(key) ? "" : key)).
							append(" specifies invalid error configuration. The 'processCasErrors/threasholdWindow' must be either 0 or larger than 'processCasErrors/thresholdCount'");
					throw new AsynchAEException(sb.toString());
			}
	}
	private boolean isCasMultiplier(ResourceSpecifier resourceSpecifier) {
		return ((AnalysisEngineDescription)resourceSpecifier).getAnalysisEngineMetaData().getOperationalProperties().getOutputsNewCASes();
		
	}
    private ResourceSpecifier getDelegateResourceSpecifier(String delegateKey, ResourceSpecifier aggregateResourceSpecifier ) throws Exception {
    	Map<String,ResourceSpecifier> delegateResourceSpecifiers =
    			((AnalysisEngineDescription)aggregateResourceSpecifier).getDelegateAnalysisEngineSpecifiers();
    	return  delegateResourceSpecifiers.get(delegateKey);

    }
	private void validateInternalReplyQueueScaleout(String context, DelegateAnalysisEngineType delegate) {
		if ( delegate.getInternalReplyQueueScaleout() != null ) {
			   StringBuilder sb = 
					new StringBuilder("*** WARN deployment descriptor for delegate analysis engine ").
					append(context).
					append(delegate.getKey()).
					append(" specifies 'internalReplyQueueScaleout=").
					append(delegate.getInternalReplyQueueScaleout()).
					append("' this is ignored for async=false");
			   System.out.println(sb.toString());
			}

	}
	private void validateInputQueueScaleout(String context, DelegateAnalysisEngineType delegate) {
		if ( delegate.getInputQueueScaleout() != null ) {
			   StringBuilder sb = 
					new StringBuilder("*** WARN deployment descriptor for delegate analysis engine ").
					append(context).
					append(delegate.getKey()).
					append(" specifies 'inputQueueScaleout=").
					append(delegate.getInputQueueScaleout()).
					append("' this is ignored for async=false");
			   System.out.println(sb.toString());
			   delegate.setInputQueueScaleout("1");  
		}

	}
	protected void validateRemoteDelegates(String parentContextName, DelegatesType dt, ResourceSpecifier resourceSpecifier) throws Exception {
		StringBuilder context = new StringBuilder(parentContextName).append("/");
		for( RemoteAnalysisEngineType remoteDelegate : dt.getRemoteAnalysisEngineArray() ) {
			StringBuilder delegateContext = new StringBuilder(context.toString());
			if ( remoteDelegate.getInputQueue() == null ) {
				   StringBuilder sb = 
							new StringBuilder("*** ERROR deployment descriptor for remote delegate analysis engine ").
							append(delegateContext).
							append(remoteDelegate.getKey()).
								append(" is missing required 'inputQueue' element");
				   System.out.println(sb.toString());
				   throw new AsynchAEException(sb.toString());
			}
			checkIfInputQueueEndpointIsDefined(remoteDelegate.getInputQueue(),
					new StringBuilder(" delegate ").append(remoteDelegate.getKey()).toString() );
			
			checkBrokerUrlForVmProtocol(remoteDelegate);
			
			checkDelegateKey(remoteDelegate.getKey(), resourceSpecifier);
			//remoteDelegate
	    	// check thresholds in aggregate error configuration
	    	validateDelegateErrorConfiguration(delegateContext.toString(), remoteDelegate.getKey(), remoteDelegate.getAsyncAggregateErrorConfiguration());

		}
		

	}

	private void checkBrokerUrlForVmProtocol(RemoteAnalysisEngineType remoteDelegate ) throws AsynchAEException {
		if( remoteDelegate.getInputQueue().getBrokerURL().equals("vm://localhost")) {
			   StringBuilder sb = 
						new StringBuilder("*** ERROR deployment descriptor for remote delegate analysis engine ").
							append(remoteDelegate.getKey()).
							append(" specifies invalid broker protocol ").append("vm://localhost");
			   System.out.println(sb.toString());
			   throw new AsynchAEException(sb.toString());

		}
	}
	private void addCasMultiplierProperties(Endpoint endpoint, AnalysisEngineDelegate aed  ) {
		if ( aed.isCasMultiplier() ) {
			endpoint.setIsCasMultiplier(true);
			endpoint.setProcessParentLast(aed.getCasMultiplier().isProcessParentLast());
			if ( aed.getCasMultiplier().getPoolSize() > 1) {
				endpoint.setShadowCasPoolSize(aed.getCasMultiplier().getPoolSize());
			}
			if ( aed.getCasMultiplier().getInitialHeapSize() > 0 ) {
				endpoint.setInitialFsHeapSize(aed.getCasMultiplier().getInitialHeapSize());
			}
			endpoint.setDisableJCasCache(aed.getCasMultiplier().disableJCasCache());		
		}	

	}
	private void addTimeouts(Endpoint endpoint, AnalysisEngineDelegate aed ) {
		endpoint.setMetadataRequestTimeout(aed.getGetMetaTimeout());
		endpoint.setProcessRequestTimeout(aed.getProcessTimeout());
		endpoint.setCollectionProcessCompleteTimeout(aed.getCpcTimeout());

	}
	private Endpoint getEndpoint(Entry<String,ResourceSpecifier> delegate, AggregateAnalysisEngineDelegate ddAggregate) {
		// assume this delegate is not remote
		boolean isRemote = false;
		// Check if there is a DD configuration for this delegate
		String serviceEndpoint = delegate.getKey();
		AnalysisEngineDelegate aed = getMatchingDelegateFromDD(delegate.getKey(), ddAggregate);
		if ( aed != null && aed.isRemote()) {
			isRemote = true;
			serviceEndpoint = resolvePlaceholder(((RemoteAnalysisEngineDelegate)aed).getQueueName());
		}
		// For each delegate create an Endpoint object. 
		Endpoint endpoint =  new DelegateEndpoint(). new Builder().
					  withDelegateKey(delegate.getKey()).
					  withEndpointName(serviceEndpoint).
					  setRemote(isRemote).
				      withResourceSpecifier(delegate.getValue()).
				      build();
		// if there is a DD configuration for this delegate, override defaults with
		// configured values.
		if (aed != null ) { 
			// if this service is a CasMultiplier, add CM properties to the endpoint object
			addCasMultiplierProperties(endpoint, aed);
			addTimeouts(endpoint, aed);
			if ( endpoint.isRemote() ) {
				configureRemoteEndpoint((RemoteAnalysisEngineDelegate)aed, endpoint);
			} else {
				configureColocatedEndpoint(aed, endpoint);
			}
		}
		return endpoint;
	}
	private void configureColocatedEndpoint(AnalysisEngineDelegate aed, Endpoint endpoint) {
		// co-located delegate may have optional scaleout info. Override
		// defaults if necessary
		if ( aed.getScaleout() > 1) {
			endpoint.setConcurrentRequestConsumers(aed.getScaleout());
		}
		if ( aed.getReplyScaleout() > 1) {
			endpoint.setConcurrentReplyConsumers(aed.getReplyScaleout());
		}

	}
	private void configureRemoteEndpoint(RemoteAnalysisEngineDelegate remoteDelegate, Endpoint endpoint ) {
		// a remote delegate must include broker and queue
		endpoint.setServerURI(resolvePlaceholder(remoteDelegate.getBrokerURI()));
		if ( remoteDelegate.getReplyScaleout() > 1 ) {
			endpoint.setConcurrentRequestConsumers(remoteDelegate.getReplyScaleout());
		}
		if (remoteDelegate.getSerialization() != null ) {
			SerialFormat sf = SerialFormat.valueOf(remoteDelegate.getSerialization().toUpperCase());
			endpoint.setSerialFormat(sf);
		}
		
	}
	protected Map<String, Endpoint> getDelegateEndpoints(AnalysisEngineDescription aeDescriptorAggregate, AggregateAnalysisEngineDelegate ddAggregate ) throws Exception { 
		Map<String, Endpoint> endpoints = new HashMap<>();
		
		Map<String, ResourceSpecifier> delegates = aeDescriptorAggregate.getDelegateAnalysisEngineSpecifiers();
		// Create an endpoint object for each delegate of this aggregate
		for (Map.Entry<String, ResourceSpecifier> delegate : delegates.entrySet()) {
			Endpoint endpoint = getEndpoint(delegate, ddAggregate);
		    endpoints.put(delegate.getKey(), endpoint);
		}
		return endpoints;
	}
	protected void validateDD(AnalysisEngineDeploymentDescriptionDocument dd, ResourceSpecifier resourceSpecifier) throws Exception {

		if (resourceSpecifier instanceof AnalysisEngineDescription) {
			if ( getService(dd) == null ) {
				StringBuilder sb = new StringBuilder("*** ERROR 'service' element is required but is missing in your deployment descriptor file");
				System.out.println(sb.toString());
				throw new IllegalArgumentException(sb.toString());
			}
			
			addAnalysisEngineTypeIfMissing(dd);

			if ( getService(dd).getTopDescriptor() == null ) {
				StringBuilder sb = new StringBuilder("*** ERROR 'import' element is required but is missing in your deployment descriptor file");
				System.out.println(sb.toString());
				throw new IllegalArgumentException(sb.toString());
	
			}
			
			// For C++ service frameworkImplementation must be
			// org.apache.uima.cpp and the <custom name="run_top_level_CPP_service_as_separate_process">
//			String frameworkImplementation = 
//					((AnalysisEngineDescription)resourceSpecifier).getFrameworkImplementation();
//			getService(dd).get
			
			
			ImportType serviceImport;
			if ( (serviceImport = getService(dd).getTopDescriptor().getImport()) == null ) {
				StringBuilder sb = new StringBuilder("*** ERROR 'topDescriptor' element is required but is missing in your deployment descriptor file");
				System.out.println(sb.toString());
				throw new IllegalArgumentException(sb.toString());

			}
			if ( !serviceImport.isSetLocation() && !serviceImport.isSetName() ) {
				StringBuilder sb = new StringBuilder("*** ERROR import missing location or name attribute");
				System.out.println(sb.toString());
				throw new IllegalArgumentException(sb.toString());

			}

			checkIfTopLevelInputQueueIsDefined(dd);
			
			checkIfTopDescriptorIsDefined(dd);
			
			checkIfTopServiceAsyncValid(dd);

			validateTopLevelErrorConfiguration(getService(dd).getAnalysisEngine().getAsyncPrimitiveErrorConfiguration());
			// verify delegate keys in DD if this is an aggregate uima-as service
			if (isAggregate(dd)) {  
				validateDelegates("",getService(dd).getAnalysisEngine(), resourceSpecifier);
				//validateDelegates(dd, resourceSpecifier);
			
			}
			// Make sure the service CAS Pool is the same size as scaleout
			adjustCasPoolOnScaleoutMismatch(dd);
		}
	}
	private void checkIfTopLevelInputQueueIsDefined(AnalysisEngineDeploymentDescriptionDocument dd) throws AsynchAEException {
		InputQueueType serviceQueue = getService(dd).getInputQueue();
		if ( serviceQueue == null ) {
			StringBuilder sb = new StringBuilder("*** ERROR top level service element must have an inputQueue");
			System.out.println(sb.toString());
			throw new AsynchAEException(sb.toString());
		}
		checkIfInputQueueEndpointIsDefined(getService(dd).getInputQueue()," top level service");
		
	}
	private void checkIfInputQueueEndpointIsDefined(InputQueueType serviceQueue, String label) throws AsynchAEException {
//		InputQueueType serviceQueue = getService(dd).getInputQueue();

		if ( serviceQueue.getEndpoint() == null || serviceQueue.getEndpoint().trim().isEmpty() ) {
			StringBuilder sb = new StringBuilder("*** ERROR missing endpoint name in inputQueue element for ").append(label);
			System.out.println(sb.toString());
			throw new AsynchAEException(sb.toString());
	
		}
	}
	private void checkIfTopDescriptorIsDefined(AnalysisEngineDeploymentDescriptionDocument dd)
			throws AsynchAEException {
		TopDescriptorType serviceDescriptor = getService(dd).getTopDescriptor();
		if ( serviceDescriptor == null ) {
			StringBuilder sb = new StringBuilder("*** ERROR top level service missing required topDescriptor element");
			System.out.println(sb.toString());
			throw new AsynchAEException(sb.toString());
		}
		
	}
	private void checkIfTopServiceAsyncValid(AnalysisEngineDeploymentDescriptionDocument dd) throws AsynchAEException {
		String asyncValue="";
		if ( (asyncValue = getService(dd).getAnalysisEngine().getAsync()) != null ) {
			if ( !asyncValue.equalsIgnoreCase("true") && !asyncValue.equalsIgnoreCase("false")) {
				StringBuilder sb = new StringBuilder("*** ERROR deployment descriptor for analysisEngine:  specifies async=").
						append(asyncValue).
						append(", but only true or false are allowed as values.");
				System.out.println(sb.toString());
				throw new AsynchAEException(sb.toString());
			}
			if (isPrimitive(dd) && asyncValue.equals("true")) {
				StringBuilder sb = new StringBuilder("*** ERROR deployment descriptor for top analysisEngine:  specifies async=").
						append(asyncValue).
						append(", but the analysis engine is primitive");
				System.out.println(sb.toString());
				throw new AsynchAEException(sb.toString());
				
			}
		}
	}

	private void checkIfDelegateServiceAsyncValid(String context, DelegateAnalysisEngineType delegate, ResourceSpecifier resourceSpecifier ) throws AsynchAEException {
		String asyncValue="";
		if ( (asyncValue = delegate.getAsync()) != null ) {
			boolean primitiveAe = true;
			if (resourceSpecifier instanceof AnalysisEngineDescription) {
				primitiveAe = ((AnalysisEngineDescription)resourceSpecifier).isPrimitive();
			}
			if (primitiveAe && asyncValue.equals("true")) {
				StringBuilder sb = new StringBuilder("*** ERROR deployment descriptor for delegate analysis engine:").
						append(context).
						append(delegate.getKey()).
						append(" specifies async=").
						append(asyncValue).
						append(", but the analysis engine is primitive.");
				System.out.println(sb.toString());
				throw new AsynchAEException(sb.toString());
				
			}
		}
	}

	private void addAnalysisEngineTypeIfMissing(AnalysisEngineDeploymentDescriptionDocument dd) {
		AnalysisEngineType aet = getService(dd).getAnalysisEngine();
		if ( aet == null ) {
			aet = getService(dd).addNewAnalysisEngine();
		}
	}
	//private void validateDelegates(AnalysisEngineDeploymentDescriptionDocument dd,ResourceSpecifier resourceSpecifier ) throws Exception {
	private void validateDelegates(String parentContextName, AnalysisEngineType aet,ResourceSpecifier resourceSpecifier ) throws Exception {
		//AnalysisEngineDescription aeDescriptor = (AnalysisEngineDescription) resourceSpecifier;
//		if (!aeDescriptor.isPrimitive()) {  
//		AnalysisEngineType aet = getService(dd).getAnalysisEngine();
//		if ( aet == null ) {
//			aet = getService(dd).addNewAnalysisEngine();
//		}

		//AnalysisEngineType aet = getService(dd).getAnalysisEngine();
		if ( aet.getDelegates() != null ) {
			DelegatesType dt = aet.getDelegates();
			if ( dt.getAnalysisEngineArray() != null ) {
				validateRemoteDelegates(parentContextName, dt, resourceSpecifier);
				
				validateColocatedDelegates(parentContextName, dt, resourceSpecifier);
			}
		}
	}
	private void adjustCasPoolOnScaleoutMismatch(AnalysisEngineDeploymentDescriptionDocument dd) {
		// get service Cas Pool spec defined in DD. This is optional so when missing
		// create a default instance with default settings
		CasPoolType serviceCasPoolConfiguration = 
				getCasPoolConfiguration(dd);
		// get service scaleout spec defined in DD. This is optional so when missing
		// create a default instance with default settings
		ScaleoutType serviceScaleoutConfiguration = 
				getScaleout(dd);

		if ( isPrimitive(getService(dd)) && 
				serviceCasPoolConfiguration.getNumberOfCASes() > serviceScaleoutConfiguration.getNumberOfInstances()) {
					// For Primitive services, the service Cas Pool should equal scaleout.
					// If it is larger, force it to be equal.

				StringBuilder sb = new StringBuilder();
				sb.append("*** WARN:  Top level Async Primitive specifies a scaleout of ")
					.append(serviceScaleoutConfiguration.getNumberOfInstances())
					.append(" , but also specifies a Cas Pool size of ")
					.append(serviceCasPoolConfiguration.getNumberOfCASes())
					.append(" .  The Cas Pool size is being forced to be the same as the scaleout.");
				System.out.println(sb.toString());
				serviceCasPoolConfiguration.
				     setNumberOfCASes(serviceScaleoutConfiguration.getNumberOfInstances());
		}
	}
	protected boolean validDelegate(ResourceSpecifier resourceSpecifier, String delegateKey) throws Exception {
		if (resourceSpecifier instanceof AnalysisEngineDescription) {
			AnalysisEngineDescription aeDescriptor = (AnalysisEngineDescription) resourceSpecifier;
			if (aeDescriptor.isPrimitive()) {
				
			} else {
				Map<String, ResourceSpecifier> delegates = aeDescriptor.getDelegateAnalysisEngineSpecifiers();
				for (Map.Entry<String, ResourceSpecifier> delegate : delegates.entrySet()) {
					String key = delegate.getKey();
					if ( delegateKey.equals(key)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	protected void checkDelegateKey(String key, ResourceSpecifier resourceSpecifier) throws Exception {
		if ( key == null || key.trim().isEmpty()) {
			StringBuilder sb = 
					new StringBuilder("*** ERROR ** The delegate key in the deployment descriptor not specified. The delegate key is required and must match a delegate in the referenced descriptor");
			throw new AsynchAEException(sb.toString()); 
		}
		if (!validDelegate(resourceSpecifier, key)) {

			StringBuilder sb = 
					new StringBuilder("*** ERROR ** The delegate in the deployment descriptor with key=").
					append(key).append(" does not match any delegates in the referenced descriptor");
			throw new AsynchAEException(sb.toString());
		}
	}
	protected boolean isDelegate(String parentController) {
		return NoParent.equals(parentController);
	}
	protected boolean isTopLevel(String parentController) {
		return NoParent.equals(parentController);
	}
	protected String getDelegateKey(String parentName, String name) {
		String key = name;
	//	if ( parentName != "NoParent" && parentName != "TLS") {

		if ( !parentName.equals("NoParent") && !parentName.equals("TLS")) {
			key = parentName+"/"+name;
		} 
//			else {
//			key = name;
//		}
		return key;
	}
	protected CasPoolType getCasPoolConfiguration(AnalysisEngineDeploymentDescriptionDocument doc) {
		CasPoolType cp;
		if ( (cp = doc.getAnalysisEngineDeploymentDescription().getDeployment().getCasPool())  == null ) {
			cp = doc.getAnalysisEngineDeploymentDescription().getDeployment().addNewCasPool();
			cp.setInitialFsHeapSize(512);
			cp.setNumberOfCASes(1);
			cp.setDisableJCasCache(false);
		} 
		return cp;
	}
	protected int howManyInstances(AnalysisEngineDeploymentDescriptionDocument doc) {
		int howMany = 1; //default
		TopLevelAnalysisEngineType topLevel =
				doc.getAnalysisEngineDeploymentDescription().
				    getDeployment().
				    getService().
				    getAnalysisEngine();
		if ( topLevel != null && topLevel.getScaleout() != null ) {
			howMany = topLevel.getScaleout().getNumberOfInstances();
		} 
		
		return howMany;
	}
	protected void initialize(UimaASService service, CasPoolType cp, Transport transport) {

		resourceManager = UimaClassFactory.produceResourceManager();
		casManager = new AsynchAECasManager_impl(resourceManager);
		casManager.setCasPoolSize(cp.getNumberOfCASes());
		casManager.setDisableJCasCache(cp.getDisableJCasCache());
		casManager.setInitialFsHeapSize(cp.getInitialFsHeapSize());

		if ( transport.equals(Transport.JMS)) {
			cache = new InProcessCache();
		} else if ( transport.equals(Transport.Java)) {
			if ( (cache = (InProcessCache)System.getProperties().get("InProcessCache")) == null) {
				cache = new InProcessCache();
				System.getProperties().put("InProcessCache", cache);
			} 
	
		}
//		if ( cache == null ) {
//			cache = new InProcessCache();
//		}
	}
	protected String getAEDescriptorPath(AnalysisEngineDeploymentDescriptionDocument doc) {
		ServiceType  s = getService(doc);
		if ( s.getTopDescriptor() == null ) {
			throw new RuntimeException("Missing <topDescriptor> element in the deployment descriptor");
		}
		String aeDescriptor = "";
		if ( s.getTopDescriptor().getImport().getName() != null ) {
			aeDescriptor = s.getTopDescriptor().getImport().getName();
		} else if ( s.getTopDescriptor().getImport().getLocation() != null ) {
			aeDescriptor = s.getTopDescriptor().getImport().getLocation();
		} else {
			throw new RuntimeException("Import missing location or name attribute in the deployment descriptor");
		}
		  XmlDocumentProperties dp = doc.documentProperties();
		  System.out.println(dp.getSourceName());
		  aeDescriptor = UimaASUtils.fixPath(dp.getSourceName(), aeDescriptor);

		return aeDescriptor;
	}
	protected ProcessCasErrorsType getTopLevelServiceErrorConfiguration(AnalysisEngineDeploymentDescriptionDocument doc) {
		ProcessCasErrorsType topLevelErrorHandlingConfig = null;
		ServiceType s = getService(doc);
		AsyncPrimitiveErrorConfigurationType pec;
		if ( s.getAnalysisEngine() != null && s.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration() != null) {
			pec = s.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration();
		} else {
			if ( s.getAnalysisEngine() == null ) {
				s.addNewAnalysisEngine();
			}
			pec = s.getAnalysisEngine().addNewAsyncPrimitiveErrorConfiguration();
			// Create default error handling for process CAS requests
			// By default, any error will result in process termination
			ProcessCasErrorsType pcet = pec.addNewProcessCasErrors();
			pcet.setContinueOnRetryFailure("false");
			pcet.setMaxRetries(0);
			pcet.setThresholdAction("terminate");
			pcet.setThresholdCount(0);
			pcet.setThresholdWindow(0);
			pcet.setTimeout(0);  // no timeout
			
			CollectionProcessCompleteErrorsType cpcet = pec.addNewCollectionProcessCompleteErrors();
			cpcet.setTimeout(0);

			
		}
		topLevelErrorHandlingConfig = pec.getProcessCasErrors();
		return topLevelErrorHandlingConfig;
	}
	public AnalysisEngineDeploymentDescriptionDocument getDD(String deploymentDescriptorPath ) throws Exception {
		return parseDD(deploymentDescriptorPath);
	}
	protected AnalysisEngineDeploymentDescriptionDocument parseDD(String descriptorPath) throws Exception {
		return AnalysisEngineDeploymentDescriptionDocument.Factory.parse(new File(descriptorPath));	

	}
	private CasMultiplierNature getCasMultiplier(CasMultiplierType cm) {
		String initialHeapSize = cm.getInitialFsHeapSize();
		int poolSize = cm.getPoolSize();
		String processParentLast = cm.getProcessParentLast();
		CasMultiplierNature cmn = new CasMultiplierNature();
		if ( initialHeapSize == null ) {
			initialHeapSize = "512";
		}
		cmn.setInitialHeapSize(Integer.parseInt(initialHeapSize));
		cmn.setPoolSize(poolSize);
		if ( processParentLast == null ) {
			processParentLast = "false";
		}
		cmn.setProcessParentLast(Boolean.parseBoolean(processParentLast));
		cmn.disableJCasCache(cm.getDisableJCasCache());
		return cmn;
	}
	private Map<String, Threshold> getThresholdMap(Class<? extends ErrorHandler> errorHandlerClass, ErrorHandlerChain errorHandlerChain ) {
		Map<String, Threshold> thresholdMap = null;
		for( ErrorHandler handler : errorHandlerChain ) {
			if ( errorHandlerClass.isInstance(handler)) {
				thresholdMap = handler.getEndpointThresholdMap();
				break;
			} 
		}
		if ( thresholdMap == null ) {
			thresholdMap = new HashMap<>();
		}
		return thresholdMap;
	}

	private void addRemoteDelegates(RemoteAnalysisEngineType[] remoteAnalysisEngineArray, AggregateAnalysisEngineDelegate aggregate ) {
		ErrorHandlerChain errorHandlerChain = null;
		
		// at this point the error handler chain must exist. See parse() method.
		errorHandlerChain = aggregate.getDelegateErrorHandlerChain();
		Map<String, Threshold> processThresholdMap =
				getThresholdMap(ProcessCasErrorHandler.class,errorHandlerChain);
		Map<String, Threshold> getMetaThresholdMap =
				getThresholdMap(GetMetaErrorHandler.class,errorHandlerChain);
		Map<String, Threshold> cpcThresholdMap =
				getThresholdMap(CpcErrorHandler.class,errorHandlerChain);
		
		for( RemoteAnalysisEngineType remote : remoteAnalysisEngineArray) {
			String key = remote.getKey();
			
			//Threshold defaultThreshold = Thresholds.newThreshold();
			RemoteAnalysisEngineDelegate delegate = new RemoteAnalysisEngineDelegate(key);
			String brokerURL = resolvePlaceholder(remote.getInputQueue().getBrokerURL());
			String queueName = resolvePlaceholder(remote.getInputQueue().getEndpoint());
			int prefetch = remote.getInputQueue().getPrefetch();
			delegate.setBrokerURI(brokerURL);
			delegate.setQueueName(queueName);
			delegate.setPrefetch(prefetch);
			if ( remote.isSetCasMultiplier()) {
				delegate.setCm(getCasMultiplier(remote.getCasMultiplier()));
			
			}
			int consumerCount = remote.getRemoteReplyQueueScaleout();
			String serialization = SerialFormat.XMI.getDefaultFileExtension(); // default if not specified

			if ( remote.getSerializer() != null ) {
				serialization = remote.getSerializer().getStringValue();
			}
			if ( serialization != null && serialization.trim().length() > 0) {
				delegate.setSerialization(serialization);
			}

			// if consumer count is defined override default which is 1
			if ( consumerCount > 0 ) {
				delegate.setScaleout(consumerCount);
			}
			
			if ( remote.getAsyncAggregateErrorConfiguration() != null ) {
				Thresholds.addDelegateErrorThreshold(delegate,remote.getAsyncAggregateErrorConfiguration().getGetMetadataErrors(),getMetaThresholdMap);
				Thresholds.addDelegateErrorThreshold(delegate,remote.getAsyncAggregateErrorConfiguration().getProcessCasErrors(),processThresholdMap);
				Thresholds.addDelegateErrorThreshold(delegate,remote.getAsyncAggregateErrorConfiguration().getCollectionProcessCompleteErrors(),cpcThresholdMap);


			} else {
				// Error configuration is optional in the DD. Add defaults.
				getMetaThresholdMap.put(key, Thresholds.newThreshold(Action.TERMINATE));
				processThresholdMap.put(key, Thresholds.newThreshold(Action.TERMINATE));
				cpcThresholdMap.put(key, Thresholds.newThreshold(Action.CONTINUE));
			}
			
			aggregate.addDelegate(delegate);
			
		}
	}
	private void addColocatedDelegates(DelegateAnalysisEngineType[] analysisEngineArray, AggregateAnalysisEngineDelegate aggregate ) {

		
		// at this point the error handler chain must exist. See parse() method.
		ErrorHandlerChain errorHandlerChain = aggregate.getDelegateErrorHandlerChain();
		Map<String, Threshold> processThresholdMap =
				getThresholdMap(ProcessCasErrorHandler.class,errorHandlerChain);
		Map<String, Threshold> getMetaThresholdMap =
				getThresholdMap(GetMetaErrorHandler.class,errorHandlerChain);
		Map<String, Threshold> cpcThresholdMap =
				getThresholdMap(CpcErrorHandler.class,errorHandlerChain);

		Threshold defaultThreshold = Thresholds.newThreshold();
		
		// Add default error handling to each co-located delegate
		for( DelegateAnalysisEngineType delegate : analysisEngineArray ) {
			String key = delegate.getKey();
			getMetaThresholdMap.put(key, defaultThreshold);
			processThresholdMap.put(key, defaultThreshold);
			cpcThresholdMap.put(key, defaultThreshold);
			// recursively iterate over delegates until no more aggregates found
			aggregate.addDelegate(parse(delegate));
		}
	}
	protected boolean isAggregate(AnalysisEngineDeploymentDescriptionDocument dd) {
		// this method (isAggregate) is just a convenience to test for positive outcome (aggregate) 
		// instead of saying !isPrimitive()
		
		return isPrimitive(dd) ? false : true;
	}
	private boolean isAggregate(AnalysisEngineType aet) {
		// Is this an aggregate? An aggregate has a property async=true or has delegates.
		System.out.println("......"+aet.getKey()+" aet.getAsync()="+aet.getAsync()+" aet.isSetAsync()="+aet.isSetAsync()+" aet.isSetDelegates()="+aet.isSetDelegates() );

		if ( "true".equals(aet.getAsync()) || aet.isSetDelegates() ) {
			return true;
		}
		return false;
	}
	protected ErrorHandlerChain createServiceErrorHandlers() {
		List<org.apache.uima.aae.error.ErrorHandler> errorHandlers = 
				new ArrayList<>();
		// There is a dedicated handler for each of GetMeta, ProcessCas, and CPC
		errorHandlers.add(new GetMetaErrorHandler(new HashMap<String, Threshold>()));
		errorHandlers.add(new ProcessCasErrorHandler(new HashMap<String, Threshold>()));
		errorHandlers.add(new CpcErrorHandler(new HashMap<String, Threshold>()));
		
		return new ErrorHandlerChain(errorHandlers);
	}
	private boolean hasCollocatedDelegates( AnalysisEngineType aet) {
		return ( aet.getDelegates() != null && aet.getDelegates().sizeOfAnalysisEngineArray() > 0 );
	}
	private boolean hasRemoteDelegates(AnalysisEngineType aet) {
		return( aet.getDelegates() != null && aet.getDelegates().sizeOfRemoteAnalysisEngineArray() > 0);
	}
	protected AnalysisEngineDelegate parse(AnalysisEngineType aet) {
		AnalysisEngineDelegate delegate = null;
		
		if ( isAggregate(aet) ) { 

			delegate = new AggregateAnalysisEngineDelegate(aet.getKey());
			
			ErrorHandlerChain errorHandlerChain = createServiceErrorHandlers();
			((AggregateAnalysisEngineDelegate)delegate).setDelegateErrorHandlerChain(errorHandlerChain);
			
			// The DD object maintains two arrays, one for co-located delegates and the other for remotes.
			// First handle co-located delegates.
			if ( hasCollocatedDelegates(aet) ) {
				DelegateAnalysisEngineType[] localAnalysisEngineArray =
						aet.getDelegates().getAnalysisEngineArray();
				addColocatedDelegates(localAnalysisEngineArray,(AggregateAnalysisEngineDelegate)delegate);
			}
			// Next add remote delegates of this aggregate
			if ( hasRemoteDelegates(aet) ) {
				RemoteAnalysisEngineType[] remoteAnalysisEngineArray =
						aet.getDelegates().getRemoteAnalysisEngineArray();
				addRemoteDelegates(remoteAnalysisEngineArray, (AggregateAnalysisEngineDelegate)delegate);
			}
		} else {
			// This is a primitive
			delegate = new AnalysisEngineDelegate(aet.getKey());
//			ErrorHandlerChain errorHandlerChain = createServiceErrorHandlers();
//			delegate.set
		}
		if ( Boolean.parseBoolean(aet.getAsync()) ) {
			delegate.setAsync(true);
		}
		
		if ( aet.getInputQueueScaleout() != null ) {
			delegate.setScaleout(Integer.valueOf(aet.getInputQueueScaleout()) );
		}
		if ( aet.getInternalReplyQueueScaleout() != null ) {
			delegate.setReplyScaleout(Integer.valueOf(aet.getInternalReplyQueueScaleout()) );
		}
		
		if ( aet.getScaleout() != null ) {
			delegate.setScaleout(aet.getScaleout().getNumberOfInstances() );
		}
		
		if ( aet.isSetCasMultiplier() ) {
			delegate.setCm(getCasMultiplier(aet.getCasMultiplier()));
		}

		return delegate;
	}

	protected ServiceType getService(AnalysisEngineDeploymentDescriptionDocument doc) {
		return doc.getAnalysisEngineDeploymentDescription().getDeployment().getService();
	}
	public static String resolvePlaceholder(String placeholderName) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(placeholderName);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement = getValue(matcher.group(1));
            if (replacement != null) {
               matcher.appendReplacement(buffer, "");
               buffer.append(replacement);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
	}
	public static String getValue(String placeholderName) {
		System.out.println(".....Finding Match For Placeholder:"+placeholderName);
		String value = System.getProperty(placeholderName);
		if (value == null) {
		// Check the environment
		   value = System.getenv(placeholderName);
		}
		return value;
	}
	protected boolean isPrimitive(AnalysisEngineDeploymentDescriptionDocument dd ) {
		return isPrimitive( dd.getAnalysisEngineDeploymentDescription().getDeployment().getService());
	}
	protected boolean isPrimitive(ServiceType dDserviceConfiguration) {
		AnalysisEngineType dDaEConfiguration =
				dDserviceConfiguration.getAnalysisEngine();
		
		return  dDaEConfiguration == null ||
				// async property not defined and delegates element not defined
				(dDaEConfiguration.getAsync() == null && dDaEConfiguration.getDelegates() == null ) ||
				(dDaEConfiguration.getAsync() != null && dDaEConfiguration.getAsync().equals("false") );
	}
	protected boolean isPrimitive(AnalysisEngineDescription aeDescriptor, ServiceType st) {
		if ( aeDescriptor.isPrimitive()) {
			return true;
		}
		if (st.getAnalysisEngine() != null && 
				(st.getAnalysisEngine().getAsync() != null && 
				st.getAnalysisEngine().getAsync().equals("true")) ||
				st.getAnalysisEngine().getDelegates() != null
			) {
			return false;
		}
		
		return true;
	}
	
	protected boolean isRemote(AnalysisEngineType type) {
		if ( type == null ) {
			return false;
		}
		return (type instanceof RemoteAnalysisEngineType);
	}
	protected boolean isRemote(DelegatesType dt, String delegateKey) {
		if ( dt == null || dt.getRemoteAnalysisEngineArray() == null ) {
			return false;  // there are no remote delegates
		}
		RemoteAnalysisEngineType[] remoteDelegates = dt.getRemoteAnalysisEngineArray();
		for( RemoteAnalysisEngineType remoteDelegate : remoteDelegates ) {
			if ( remoteDelegate.getKey().equals(delegateKey)) {
				return true;
			}
		}
		return false;
	}
	protected class AnalysisEngineDeployment {
		private Object deploymentConfiguration;
		
		public AnalysisEngineDeployment( Object aet ) {
			deploymentConfiguration = aet;
		}
		
		public boolean isRemote() {
			if ( deploymentConfiguration == null || deploymentConfiguration instanceof AnalysisEngineType ) {
				return false;
			}
			return true;
		}
		public boolean withConfiguration() {
			return deploymentConfiguration != null;
		}
		public AnalysisEngineType asAnalysisEngineType() {
			return (AnalysisEngineType)deploymentConfiguration;
		}
		public RemoteAnalysisEngineType asRemoteAnalysisEngineType() {
			return (RemoteAnalysisEngineType)deploymentConfiguration;
		}
		public int getScaleout() {
			int scaleout = 1; // default
	    	if ( deploymentConfiguration != null && deploymentConfiguration instanceof AnalysisEngineType ) {
	    		if ( ((AnalysisEngineType)deploymentConfiguration).getScaleout() != null ) {
		    		scaleout = ((AnalysisEngineType)deploymentConfiguration).getScaleout().getNumberOfInstances();
	    		}
	    	}
	    	return scaleout;
		}
	}
	
	public enum Serialization {
		XMI, Binary;
	}
	
}
