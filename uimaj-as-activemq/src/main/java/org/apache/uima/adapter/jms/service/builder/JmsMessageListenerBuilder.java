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

import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController_impl;
import org.apache.uima.aae.error.ErrorHandlerChain;
import org.apache.uima.adapter.jms.activemq.ConcurrentMessageListener;
import org.apache.uima.adapter.jms.activemq.JmsInputChannel;
import org.apache.uima.adapter.jms.activemq.JmsOutputChannel;
import org.apache.uima.adapter.jms.activemq.TempDestinationResolver;
import org.apache.uima.adapter.jms.activemq.UimaDefaultMessageListenerContainer;
import org.apache.uima.as.client.Listener.Type;
import org.apache.uima.resource.ResourceManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class JmsMessageListenerBuilder {
	private AnalysisEngineController controller;
	private ActiveMQConnectionFactory connectionFactory;
	private int consumerCount=1;
	private InputChannel inputChannel;
	private Endpoint endpoint;
	private boolean isReplyListener = false;
	private String selector=null;
	private Destination destination=null;  // queue 
	private ThreadPoolTaskExecutor threadExecutor=null;
	private Type type;
	private TempDestinationResolver tempQueueDestinationResolver = null;
	
	public static void main(String[] args) {
		try {
			String endpointName = "PersonTitleAnnotatorQueue";
			String analysisEngineDescriptor = "C:/uima/releases/testing/uima/uima-as/2.9.0/target/uima-as-2.9.1-SNAPSHOT-bin/apache-uima-as-2.9.1-SNAPSHOT/examples/descriptors/analysis_engine/PersonTitleAnnotator.xml";
			String broker = "tcp://localhost:61616";
			String processSelector = "Command=2000 OR Command=2002";
			String getMetaSelector = "Command=2001";
			int workQueueSize = 1;
			int processScaleout = 4;
			int scaleout = 1;
			
			System.setProperty("BrokerURI",broker);
			ErrorHandlerChain errorHandlerChain = null;
			
			InProcessCache inProcessCache = new InProcessCache();
			
			ResourceManager resourceManager =
					UimaClassFactory.produceResourceManager();
			
			AsynchAECasManager_impl casManager = 
					new AsynchAECasManager_impl(resourceManager);
			casManager.setCasPoolSize(processScaleout);
			
			
			JmsInputChannel processInputChannel = new JmsInputChannel(ChannelType.REQUEST_REPLY);
			JmsInputChannel getMetaInputChannel = new JmsInputChannel(ChannelType.REQUEST_REPLY);
			
			JmsOutputChannel outputChannel = new JmsOutputChannel();
			outputChannel.setServerURI(broker);
			PrimitiveAnalysisEngineController_impl controller =
					new PrimitiveAnalysisEngineController_impl(null, endpointName, analysisEngineDescriptor, casManager, inProcessCache, workQueueSize, scaleout);
			
			controller.setOutputChannel(outputChannel);
			controller.setErrorHandlerChain(errorHandlerChain);
						
			
			ActiveMQConnectionFactory factory =
					ActiveMQFactory.newConnectionFactory(broker, 0);
			
			factory.setTrustAllPackages(true);
			ActiveMQDestination destination = 
					new ActiveMQQueue(endpointName);
			JmsMessageListenerBuilder processListenerBuilder = 
					new JmsMessageListenerBuilder();
			ThreadPoolTaskExecutor threadExecutor1 = new ThreadPoolTaskExecutor();
			
			threadExecutor1.setCorePoolSize(processScaleout);
			threadExecutor1.setMaxPoolSize(processScaleout);

			UimaDefaultMessageListenerContainer jmsProcessMessageListener =
			       processListenerBuilder.withController(controller)
			       			.withType(Type.ProcessCAS)
							.withConectionFactory(factory)
							.withThreadPoolExecutor(threadExecutor1)
							.withConsumerCount(processScaleout)
							.withInputChannel(processInputChannel)
							.withSelector(processSelector)
							.withDestination(destination)
							.build();
			
			JmsMessageListenerBuilder getMetaListenerBuilder = 
					new JmsMessageListenerBuilder();
			ThreadPoolTaskExecutor threadExecutor2 = new ThreadPoolTaskExecutor();
			threadExecutor2.setCorePoolSize(scaleout);
			threadExecutor2.setMaxPoolSize(scaleout);
			
			UimaDefaultMessageListenerContainer jmsGetMetaMessageListener =
					getMetaListenerBuilder.withController(controller)
							.withType(Type.GetMeta)
							.withConectionFactory(factory)
							.withThreadPoolExecutor(threadExecutor2)
							.withConsumerCount(scaleout)
							.withInputChannel(getMetaInputChannel)
							.withSelector(getMetaSelector)
							.withDestination(destination)
							.build();

			ThreadPoolTaskExecutor threadExecutor3 = new ThreadPoolTaskExecutor();
			threadExecutor3.setCorePoolSize(scaleout);
			threadExecutor3.setMaxPoolSize(scaleout);
			TempDestinationResolver resolver = new TempDestinationResolver(controller.getComponentName(),"");
			resolver.setConnectionFactory(factory);
			
			UimaDefaultMessageListenerContainer replyListener =
					getMetaListenerBuilder.withController(controller)
							.withType(Type.Reply)
							.withConectionFactory(factory)
							.withThreadPoolExecutor(threadExecutor3)
							.withConsumerCount(scaleout)
							.withTempDestinationResolver(resolver)
							.build();
			
			
			processInputChannel.setController(controller);
			processInputChannel.addListenerContainer(jmsProcessMessageListener);
			
			getMetaInputChannel.setController(controller);
			getMetaInputChannel.addListenerContainer(jmsGetMetaMessageListener);
			
			threadExecutor1.initialize();
			threadExecutor1.getThreadPoolExecutor().prestartAllCoreThreads();
			threadExecutor2.initialize();
			threadExecutor2.getThreadPoolExecutor().prestartAllCoreThreads();
			threadExecutor3.initialize();
			threadExecutor3.getThreadPoolExecutor().prestartAllCoreThreads();
			
			jmsProcessMessageListener.afterPropertiesSet();
			jmsProcessMessageListener.initialize();
			jmsProcessMessageListener.start();
			
			jmsGetMetaMessageListener.afterPropertiesSet();
			jmsGetMetaMessageListener.initialize();
			jmsGetMetaMessageListener.start();
			

			replyListener.afterPropertiesSet();
			replyListener.initialize();
			replyListener.start();
			
/*				
			synchronized(inProcessCache ) {
				inProcessCache.wait(5000);
				System.out.println("Stopping Listeners ....");
				jmsProcessMessageListener.setTerminating();
				jmsProcessMessageListener.stop();
				threadExecutor1.getThreadPoolExecutor().shutdownNow();
				threadExecutor1.shutdown();
				jmsProcessMessageListener.stop();
				jmsProcessMessageListener.closeConnection();
				jmsProcessMessageListener.destroy();
				System.out.println("Stopped Process Listener ....");
				
				jmsGetMetaMessageListener.setTerminating();
				jmsGetMetaMessageListener.stop();
				
				threadExecutor2.getThreadPoolExecutor().shutdownNow();
				threadExecutor2.shutdown();
				jmsGetMetaMessageListener.closeConnection();
				jmsGetMetaMessageListener.destroy();
				System.out.println("Stopped GetMeta Listener ....");
			}
			*/
		} catch( Exception e) {
			e.printStackTrace();
		}

	}

	public JmsMessageListenerBuilder withController(AnalysisEngineController controller ) {
		this.controller = controller;
		return this;
	}
	
	public JmsMessageListenerBuilder withTempDestinationResolver(TempDestinationResolver resolver ) {
		this.tempQueueDestinationResolver = resolver;
		return this;
	}
	public JmsMessageListenerBuilder withInputChannel(InputChannel inputChannel ) {
		this.inputChannel = inputChannel;
		return this;
	}
	public JmsMessageListenerBuilder withThreadPoolExecutor(ThreadPoolTaskExecutor threadExecutor) {
		this.threadExecutor = threadExecutor;
		return this;
	}
	public JmsMessageListenerBuilder withEndpoint(Endpoint endpoint ) {
		this.endpoint = endpoint;
		return this;
	}
	public JmsMessageListenerBuilder withSelector(String selector ) {
		this.selector = selector;
		return this;
	}
	public JmsMessageListenerBuilder withDestination(Destination destination ) {
		this.destination = destination;
		return this;
	}
	public JmsMessageListenerBuilder withConectionFactory(ActiveMQConnectionFactory connectionFactory ) {
		this.connectionFactory = connectionFactory;
		return this;
	}

	public JmsMessageListenerBuilder withConsumerCount(int howManyConsumers ) {
		this.consumerCount = howManyConsumers;
		return this;
	}
	public JmsMessageListenerBuilder asReplyListener() {
		this.isReplyListener = true;
		return this;
	}
	public JmsMessageListenerBuilder withType(Type t) {
		this.type = t;
		if ( Type.Reply.equals(t)) {
			asReplyListener();
		}
		return this;
	}
	private void validate() {
		
	}
	private boolean isRemoteCasMultiplier(Endpoint endpoint) {
	       return (endpoint != null && endpoint.isRemote()  && endpoint.isCasMultiplier() );
	}
	public UimaDefaultMessageListenerContainer build() throws Exception{
		UimaDefaultMessageListenerContainer listener = 
				new UimaDefaultMessageListenerContainer();
		/*
		 * 
		 * VALIDATE REQUIRED PROPERTIES
		 * 
		 */
		// make sure all required properties are set
		validate();
		if ( threadExecutor != null ) {
			threadExecutor.setThreadNamePrefix(controller.getComponentName()+"-"+type.name()+"Listener-Thread");
			listener.setTaskExecutor(threadExecutor);
			
		}
		
		listener.setConcurrentConsumers(consumerCount);
		listener.setController(controller);
		
		if ( selector != null ) {
			listener.setMessageSelector(selector);
		}
		
        if (isRemoteCasMultiplier(endpoint) ) {
        	// for remote CM's we need special handling. See description of a 
        	// possible race condition in ConcurrentMessageListener class.
    		ThreadGroup tg = Thread.currentThread().getThreadGroup();
            String prefix = endpoint.getDelegateKey()+" Reply Thread";
    		ConcurrentMessageListener concurrentListener = 
    				new ConcurrentMessageListener(consumerCount, (JmsInputChannel)inputChannel, "", tg,prefix);
    		// register this listener with inputchannel so that we can stop it. The listener on a remote CM 
    		// is ConcurrentMessageListener which imposes order of replies (parent last) before delegating 
    		// msgs to the inputchannel. When stopping the service, all listeners must be registered with 
    		// an inputchannel which is responsible for shutting down all listeners.
    		((JmsInputChannel)inputChannel).registerListener(listener);
            listener.setMessageListener(concurrentListener);
            concurrentListener.setAnalysisEngineController(controller);
        } else {
    		((JmsInputChannel)inputChannel).registerListener(listener);
    		listener.setMessageListener(inputChannel);
        }

		listener.setTargetEndpoint(endpoint);
		listener.setConnectionFactory(connectionFactory);
		// is this listener processing replies from a remote service. This can
		// only be true if the controller is an aggregate. Primitive controller
		// can only handle requests from remote services. An aggregate can send
		// requests and expects replies.
		if ( isReplyListener || Type.FreeCAS.equals(type)) {
			String e = Type.FreeCAS.equals(type) ? "FreeCASEndpoint" :endpoint.getDelegateKey();
			TempDestinationResolver resolver = new
					TempDestinationResolver(controller.getComponentName(), e);
			resolver.setListener(listener);
			resolver.setConnectionFactory(connectionFactory);
			listener.setDestinationResolver(resolver);
			listener.setDestinationName("");
			if ( Type.FreeCAS.equals(type)) {
				listener.setBeanName(controller.getComponentName()+"-"+type.name()+"Listener For FreeCas Listener");
			} else {
				listener.setBeanName(controller.getComponentName()+"-"+type.name()+"Listener For Delegate:"+endpoint.getDelegateKey());
			}
		} else if ( destination != null ) {
			listener.setDestinationName(((ActiveMQDestination)destination).getPhysicalName());
			listener.setDestination(destination);
			listener.setBeanName(controller.getComponentName()+"-"+type.name()+"Listener");

		}

		if ( type != null ) {
			listener.setType(type);
		}
		return listener;
	}
}
