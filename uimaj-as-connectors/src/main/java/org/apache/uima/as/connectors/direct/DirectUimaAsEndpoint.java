package org.apache.uima.as.connectors.direct;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.uima.UimaContext;
import org.apache.uima.aae.AsynchAECasManager;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.OutputChannel;
import org.apache.uima.aae.UimaEEAdminContext;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ServiceState;
import org.apache.uima.aae.controller.ControllerCallbackListener;
import org.apache.uima.aae.controller.ControllerLatch;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.EventSubscriber;
import org.apache.uima.aae.controller.LocalCache;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.definition.connectors.UimaAsProducer;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.ErrorContext;
import org.apache.uima.aae.error.ErrorHandlerChain;
import org.apache.uima.aae.jmx.JmxManagement;
import org.apache.uima.aae.jmx.ServiceErrors;
import org.apache.uima.aae.jmx.ServiceInfo;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.message.UimaAsOrigin;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.service.command.UimaAsMessageProcessor;
import org.apache.uima.aae.spi.transport.UimaMessageListener;
import org.apache.uima.as.client.DirectInputChannel;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectMessageContext;
import org.apache.uima.as.client.Listener;
import org.apache.uima.as.connectors.mockup.MockUpAnalysisEngineController;
import org.apache.uima.as.connectors.mockup.TestMessageProcessor;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceSpecifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class DirectUimaAsEndpoint implements UimaAsEndpoint {

	private Map<String,UimaAsConsumer> consumers = new ConcurrentHashMap<>();
	private Map<String,UimaAsProducer> producers = new ConcurrentHashMap<>();
	private final MessageProcessor processor;
	private final String name;
	
	public DirectUimaAsEndpoint(MessageProcessor processor, String name) {
		this.processor = processor;
		this.name = name;
	}
	public MessageContext createMessage(int command, int messageType, Endpoint endpoint) {
		DirectMessage message = 
				new DirectMessage().
				withCommand(command).
				withMessageType(messageType).
				withOrigin(processor.getController().getOrigin());
		
		MessageContext messageContext = 
				new DirectMessageContext(message,name,name);
		
		message.withCommand(command).
			withMessageType(messageType);
		
		if ( command == AsynchAEMessage.GetMeta && messageType == AsynchAEMessage.Response ) {
			message.withMetadata(null);
//			message.withMetadata(processor.getController().getResourceSpecifier().);
		}
		
			
		return messageContext;
	}
	public void dispatch(MessageContext messageContext) throws Exception {
		UimaAsProducer producer;
		if ( !producers.containsKey(messageContext.getEndpoint().getDelegateKey())) {
			producer = 
					createProducer((UimaAsConsumer)messageContext.getEndpoint().getDestination(), messageContext.getEndpoint().getDelegateKey());
		} else {
			producer = producers.get(messageContext.getEndpoint().getDelegateKey());
		}
		producer.dispatch((DirectMessage)messageContext.getRawMessage());
	}
	
	public UimaAsProducer createProducer(UimaAsConsumer consumer, String delegateKey) throws Exception {
		
		UimaAsProducer producer = new DirectUimaAsProducer(consumer);
		producers.put(delegateKey,producer);
		return producer;
	}
	
	public UimaAsProducer createProducer(String targetUri) throws Exception {
		
		UimaAsProducer producer = new DirectUimaAsProducer(targetUri);
		producers.put(targetUri, producer);
		return producer;
	}

	public UimaAsConsumer createConsumer(String targetUri, ConsumerType type, int consumerThreadCount) throws Exception {
		
		DirectUimaAsConsumer consumer = new DirectUimaAsConsumer(name, targetUri, type, consumerThreadCount);
		consumer.setMessageProcessor(processor);
		consumers.put(targetUri+type.name(), consumer);
		return consumer;
	}
	
	public UimaAsConsumer getConsumer(String targetUri, ConsumerType type) {
		return consumers.get(targetUri+type.name());
	}

	@Override
	public void start() throws Exception {
		for(Entry<String, UimaAsConsumer> entry : consumers.entrySet()) {
			entry.getValue().initialize(processor.getController());
			entry.getValue().start();
		}
		for(Entry<String, UimaAsProducer> entry : producers.entrySet()) {
			entry.getValue().start();
		}

	}
	@Override
	public void stop() throws Exception {
		for(Entry<String, UimaAsConsumer> entry : consumers.entrySet()) {
			entry.getValue().stop();
		}
		for(Entry<String, UimaAsProducer> entry : producers.entrySet()) {
			entry.getValue().stop();
		}
		
	}

	public static void main(String[] args) {
		try {
			MessageProcessor dummyProcessor = 
					new TestMessageProcessor(null);
					//new TestMessageProcessor(new MockUpAnalysisEngineController("MockupClient", 4));

//			MessageProcessor processor = 
	//				new UimaAsMessageProcessor(null);
			//TestMessageProcessor processor = new TestMessageProcessor(null);
			
			DirectUimaAsEndpoint endpoint = 
					new DirectUimaAsEndpoint(dummyProcessor, "Client");

			MockupService service = 
					endpoint.new MockupService();
			
			service.initialize();
			service.start();
			
			endpoint.createConsumer("direct:", ConsumerType.GetMeta, 1);
			endpoint.createConsumer("direct:", ConsumerType.ProcessCAS, 4);
			endpoint.createConsumer("direct:", ConsumerType.Cpc, 1);
			
			UimaAsProducer producer = 
					endpoint.createProducer( "direct:serviceA");
		
			endpoint.start();
			
			DirectMessage getMetaRequestMessage = 
					new DirectMessage().
					    withCommand(AsynchAEMessage.GetMeta).
					    withMessageType(AsynchAEMessage.Request).
					    withOrigin(new UimaAsOrigin("Client")).
					    withReplyDestination(endpoint.getConsumer("direct:", ConsumerType.GetMeta)).
					    withPayload(AsynchAEMessage.None);
			UimaAsConsumer target = 
					service.getEndpoint().getConsumer("direct:", ConsumerType.GetMeta);
			
			producer.dispatch(getMetaRequestMessage, target);
			
		} catch( Exception e) {
			e.printStackTrace();
		}

	}

	
	private class MockupService {
		UimaAsEndpoint endpoint;
		UimaAsProducer producer;
		
		public void initialize() throws Exception {
			
			MockUpAnalysisEngineController controller =
					new MockUpAnalysisEngineController("MockupService",4);
			MessageProcessor dummyProcessor = 
					new TestMessageProcessor(controller);
			
			endpoint = new DirectUimaAsEndpoint(dummyProcessor, "Service");
			UimaAsConsumer getMetaReplyConsumer = endpoint.createConsumer("direct:", ConsumerType.GetMeta, 1);
			UimaAsConsumer processCasReplyConsumer = endpoint.createConsumer("direct:", ConsumerType.ProcessCAS, 4);
			UimaAsConsumer cpcReplyConsumer = endpoint.createConsumer("direct:", ConsumerType.Cpc, 1);
			
			producer = endpoint.createProducer( "direct:serviceA");
			controller.addEndpoint(new UimaAsOrigin("Service"), endpoint);
		}
		public void process(MessageContext mc) throws Exception {
			
		}
		public void start() throws Exception {
			endpoint.start();
		}
		public void stop() throws Exception {
			endpoint.stop();
		}
		public UimaAsEndpoint getEndpoint() {
			return endpoint;
		}
		
		
		private class ServiceMessageProcessor implements MessageProcessor {
			MockupService service;
			public ServiceMessageProcessor(MockupService service) {
				this.service = service;
			}
			@Override
			public void process(MessageContext message) throws Exception {
				DirectMessage request = 
						(DirectMessage)message.getRawMessage();
				DirectMessage getMetaReply = 
						new DirectMessage().
						    withCommand(AsynchAEMessage.GetMeta).
						    withMessageType(AsynchAEMessage.Response).
						    withOrigin(message.getEndpoint().getMessageOrigin()).
						    withReplyDestination(request.getReplyDestination()).
						    withPayload(AsynchAEMessage.Metadata);
				MessageContext reply = new DirectMessageContext(getMetaReply, "", "");
				service.getEndpoint().dispatch(reply);
				
			}
			@Override
			public AnalysisEngineController getController() {
				// TODO Auto-generated method stub
				return null;
			}
			
		}
	}

}
