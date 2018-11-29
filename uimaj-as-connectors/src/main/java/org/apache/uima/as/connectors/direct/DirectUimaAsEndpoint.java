package org.apache.uima.as.connectors.direct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.PrimitiveAeInitializer;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.definition.connectors.Initializer;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.definition.connectors.UimaAsProducer;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageBuilder;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.aae.message.ServiceMessageProcessor;
import org.apache.uima.aae.message.Target;
import org.apache.uima.aae.message.UimaAsOrigin;
import org.apache.uima.aae.message.UimaAsTarget;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.connectors.mockup.MockUpAnalysisEngineController;
import org.apache.uima.as.connectors.mockup.TestClientMessageProcessor;
import org.apache.uima.as.connectors.mockup.TestMessageProcessor;


public class DirectUimaAsEndpoint implements UimaAsEndpoint {

	private Map<Target,UimaAsConsumer> consumers = new ConcurrentHashMap<>();
	private Map<Origin,UimaAsProducer> producers = new ConcurrentHashMap<>();
	
	private List<UimaAsProducer> getMetaRequestProducers = new ArrayList<>();
	private List<UimaAsProducer> processCASRequestProducers = new ArrayList<>();
	private List<UimaAsProducer> cpcRequestProducers = new ArrayList<>();
	private List<UimaAsProducer> freeCASRequestProducers = new ArrayList<>();

	private List<UimaAsProducer> getMetaResponseProducers = new ArrayList<>();
	private List<UimaAsProducer> processCASResponseProducers = new ArrayList<>();
	private List<UimaAsProducer> cpcResponseProducers = new ArrayList<>();

	private List<UimaAsConsumer> getMetaRequestConsumers = new ArrayList<>();
	private List<UimaAsConsumer> processCASRequestConsumers = new ArrayList<>();
	private List<UimaAsConsumer> cpcRequestConsumers = new ArrayList<>();
	private List<UimaAsConsumer> freeCASRequestConsumers = new ArrayList<>();

	private List<UimaAsConsumer> getMetaResponseConsumers = new ArrayList<>();
	private List<UimaAsConsumer> processCASResponseConsumers = new ArrayList<>();
	private List<UimaAsConsumer> cpcResponseConsumers = new ArrayList<>();
	
	private Map<String, Target> targets = new HashMap<>();
	
	private MessageProcessor processor;
	//private final String name;
	private final EndpointType type = EndpointType.Direct;
	
	private final Origin origin;
	// this ctor is used for Producer type endpoints. 
	public DirectUimaAsEndpoint(final String name) {
		
		origin = new UimaAsOrigin(name, EndpointType.Direct);
/*		
		if ( name.indexOf(EndpointType.Direct.getName()) > -1 ) {
			origin = new UimaAsOrigin(name);
		} else {
			origin = new UimaAsOrigin(EndpointType.Direct.getName()+name);
		}
		*/
//		this.name = name;
	}
	// this ctor is used for Consumer type endpoints
	public DirectUimaAsEndpoint(MessageProcessor processor,String name) {
		this(name);
		this.processor = processor;
		if ( this.processor == null ) {
			System.out.println("...........DirectUimaAsEndpoint(MessageProcessor processor,String name).......processor is null............");
			Thread.currentThread().dumpStack();
		}
	}
	public Origin getOrigin() {
		return origin;
	}
	@Override
	public Map<Target, UimaAsConsumer> getConsumers() {
		return consumers;
	}
	
	public String getName() {
		return origin.getName();
	}
	
	@Override
	public EndpointType getType() {
		return type;
	}
	
	@Override
	public MessageBuilder newMessageBuilder() {
		return new DirectMessageBuilder(this);
	}

	private ConsumerType consumerTypeForRequestCommand(int command) {
		ConsumerType type = null;
		switch(command) {
		case AsynchAEMessage.GetMeta:
			type = ConsumerType.GetMetaRequest;
			break;
		case AsynchAEMessage.Process:
			type = ConsumerType.ProcessCASRequest;
			break;
		case AsynchAEMessage.CollectionProcessComplete:
			type = ConsumerType.CpcRequest;
			break;
		case AsynchAEMessage.ReleaseCAS:
			type = ConsumerType.FreeCASRequest;
			break;
		}
		return type;
	}
	private ConsumerType consumerTypeForResponseCommand(int command) {
		ConsumerType type = null;
		switch(command) {
		case AsynchAEMessage.GetMeta:
			type = ConsumerType.GetMetaResponse;
			break;
		case AsynchAEMessage.Process:
			type = ConsumerType.ProcessCASResponse;
			break;
		case AsynchAEMessage.CollectionProcessComplete:
			type = ConsumerType.CpcResponse;
		}
		return type;
	}

	public ConsumerType type(MessageContext messageContext) throws Exception {
		int command =
				messageContext.getMessageIntProperty(AsynchAEMessage.Command);
		int msgType =
				messageContext.getMessageIntProperty(AsynchAEMessage.MessageType);
		
		ConsumerType type = null;
		
		if ( AsynchAEMessage.Request == msgType ) {
			type = consumerTypeForRequestCommand(command);
		} else if ( AsynchAEMessage.Response == msgType )  {
			type = consumerTypeForResponseCommand(command);
		} else {
			
		}
		
		
		return type;
		
	}
	/*
	public void dispatch(MessageContext messageContext) throws Exception {
		
		UimaAsProducer producer;
		if ( !producers.containsKey( ((DirectMessage)messageContext.getRawMessage()).getOrigin())) {
			producer = 
					createProducer((UimaAsConsumer)messageContext.getEndpoint().getReplyDestination(), ((DirectMessage)messageContext.getRawMessage()).getOrigin());
			System.out.println(".............. Creating new Producer for endpoint:"+(UimaAsConsumer)messageContext.getEndpoint().getReplyDestination());
		} else {
			producer = producers.get(((DirectMessage)messageContext.getRawMessage()).getOrigin());
			System.out.println(".............. Reusing existing producer with Origin:"+((DirectMessage)messageContext.getRawMessage()).getOrigin());
		}
		producer.dispatch((DirectMessage)messageContext.getRawMessage());
	}
	*/
	public void dispatch(MessageContext messageContext, String serviceUri) throws Exception {
		if ( !serviceUri.startsWith(EndpointType.Direct.getName())) {
			serviceUri = EndpointType.Direct.getName()+serviceUri;
		}
		Target target = targets.get(serviceUri);
		UimaAsProducer producer = producerFor(type(messageContext), target);
		if ( Objects.isNull(producer)) {
			producer = createProducer((UimaAsConsumer)messageContext.getMessageObjectProperty(AsynchAEMessage.ReplyToEndpoint), serviceUri);
		}
		producer.dispatch((DirectMessage)messageContext.getRawMessage());
	}
	
	public void dispatch(MessageContext messageContext, Origin origin) throws Exception {

		Target target = targets.get(origin.getName());
		UimaAsProducer producer = producerFor(type(messageContext), target);
/*
		UimaAsProducer producer;
		if ( !producers.containsKey( target ) ) {
			producer = 
					createProducer((UimaAsConsumer)messageContext.getEndpoint().getReplyDestination(), target);
		} else {
			producer = producers.get(target);
		}
		*/
		producer.dispatch((DirectMessage)messageContext.getRawMessage());
	}

	private UimaAsProducer matchProducer(Target target, List<UimaAsProducer> producerList) {
		UimaAsProducer producer = null;
		for( UimaAsProducer p : producerList) {
			if ( p.getTarget().equals(target)) {
				producer = p;
			}
		}
		
		return producer;
	}
	private UimaAsConsumer matchConsumer(Target target, List<UimaAsConsumer> consumerList) {
		UimaAsConsumer consumer = null;
		for( UimaAsConsumer c : consumerList) {
			if ( c.getTarget().equals(target)) {
				consumer = c;
			}
		}
		
		return consumer;
	}
	private UimaAsProducer producerFor(ConsumerType type, Target target) {
		UimaAsProducer producer = null;
		
		switch(type) {
		case GetMetaRequest:
			producer = matchProducer( target, getMetaRequestProducers);
			break;
		case GetMetaResponse:
			producer =  matchProducer( target,getMetaResponseProducers);
			break;
			
		case ProcessCASRequest:
			producer =  matchProducer( target,processCASRequestProducers);
			break;
			
		case ProcessCASResponse:
			producer =  matchProducer( target,processCASResponseProducers);
			break;
			
		case CpcRequest:
			producer =  matchProducer( target,cpcRequestProducers);
			break;
			
		case CpcResponse:
			producer =  matchProducer( target,cpcResponseProducers);
			break;
			
		case FreeCASRequest:
			producer =  matchProducer( target,freeCASRequestProducers);
			break;

		default:
		}
		return producer;
	}
	private void categorizeProducer(UimaAsProducer producer) {
		
		switch(producer.getType()) {
		case GetMetaRequest:
			getMetaRequestProducers.add(producer);
			break;
			
		case GetMetaResponse:
			getMetaResponseProducers.add(producer);
			break;
			
		case ProcessCASRequest:
			processCASRequestProducers.add(producer);
			break;
			
		case ProcessCASResponse:
			processCASResponseProducers.add(producer);
			break;
			
		case CpcRequest:
			cpcRequestProducers.add(producer);
			break;
			
		case CpcResponse:
			cpcResponseProducers.add(producer);
			break;
			
		case FreeCASRequest:
			freeCASRequestProducers.add(producer);
			break;
	
		default:
		}
	}
	private void categorizeConsumer(UimaAsConsumer consumer) {
		
		switch(consumer.getType()) {
		case GetMetaRequest:
			getMetaRequestConsumers.add(consumer);
			break;
			
		case GetMetaResponse:
			getMetaResponseConsumers.add(consumer);
			break;
			
		case ProcessCASRequest:
			processCASRequestConsumers.add(consumer);
			break;
			
		case ProcessCASResponse:
			processCASResponseConsumers.add(consumer);
			break;
			
		case CpcRequest:
			cpcRequestConsumers.add(consumer);
			break;
			
		case CpcResponse:
			cpcResponseConsumers.add(consumer);
			break;
			
		case FreeCASRequest:
			freeCASRequestConsumers.add(consumer);
			break;
		default:
		}
	}
	protected UimaAsConsumer consumerFor(ConsumerType type, Target target) {
		UimaAsConsumer consumer = null;
		
		switch(type) {
		case GetMetaRequest:
			consumer = matchConsumer( target, getMetaRequestConsumers);
			break;
		case GetMetaResponse:
			consumer =  matchConsumer( target,getMetaResponseConsumers);
			break;
			
		case ProcessCASRequest:
			consumer =  matchConsumer( target,processCASRequestConsumers);
			break;
			
		case ProcessCASResponse:
			consumer =  matchConsumer( target,processCASResponseConsumers);
			break;
			
		case CpcRequest:
			consumer =  matchConsumer( target,cpcRequestConsumers);
			break;
			
		case CpcResponse:
			consumer =  matchConsumer( target,cpcResponseConsumers);
			break;
		case FreeCASRequest:
			consumer =  matchConsumer( target,freeCASRequestConsumers);
			break;

		default:
		}
		return consumer;
	}
	/*
	public UimaAsProducer createProducer(UimaAsConsumer consumer, Origin origin) throws Exception {
		
		UimaAsProducer producer = new DirectUimaAsProducer(consumer);
		targets.put(origin.getName(), producer.getTarget());

		producers.put(origin,producer);
		categorizeProducer(producer);
		return producer;
	}
	*/
	public UimaAsProducer createProducer(UimaAsConsumer consumer, String serviceUri) throws Exception {
		
		UimaAsProducer producer = new DirectUimaAsProducer(serviceUri, consumer);
		StringBuilder sb = new StringBuilder();
		if ( serviceUri != null && serviceUri.startsWith(EndpointType.Direct.getName())) {
			sb.append(serviceUri).append(":").append(consumer.getType().toString());
			
		} else {
			sb.append(EndpointType.Direct.getName()).append(serviceUri).append(":").append(consumer.getType().toString());
			
		}
		targets.put(sb.toString(), producer.getTarget());

		categorizeProducer(producer);
		return producer;
	}
	
	public UimaAsProducer createProducer(Origin origin) throws Exception {
		// THIS SHOULD NOT BE CALLED
		UimaAsProducer producer = new DirectUimaAsProducer(origin.getName());
		targets.put(origin.getName(), producer.getTarget());
		producers.put(origin, producer);
		return producer;
	}

	public UimaAsConsumer createConsumer( ConsumerType type, int consumerThreadCount) throws Exception {
		String cid = new StringBuilder(getName()).append(":").append(type.name()).toString();
		UimaAsConsumer consumer;
		Target target;
		if ( (target = targets.get(cid)) == null ) {
			consumer =
					new DirectUimaAsConsumer(getName(), type, consumerThreadCount);
			((DirectUimaAsConsumer)consumer).setMessageProcessor(processor);
			categorizeConsumer(consumer);
			targets.put(cid, consumer.getTarget());
			consumers.put(consumer.getTarget(), consumer);
		} else {
			consumer = consumers.get(target);
		}

		System.out.println(".......... Added new consumer - key:"+consumer.getTarget().getName()+":"+type.name()+" Consumer Count:"+consumers.size());
		return consumer;
	}
	
	public UimaAsConsumer getConsumer(String targetUri, ConsumerType type) {
//		return consumers.get(targetUri+type.name());
		Target target = targets.get(new StringBuilder(targetUri).append(":").append(type.name()).toString());
		return consumerFor(type, target);
	}

	

	@Override
	public void start() throws Exception {
		System.out.println("Consumer Count:"+consumers.size());
		for(Entry<Target, UimaAsConsumer> entry : consumers.entrySet()) {
			if ( ConsumerType.ProcessCASRequest.equals(entry.getValue().getType())) {
				if ( processor == null ) {
					System.out.println(".... Processor is null");
				}
			
				AnalysisEngineController controller = ((ServiceMessageProcessor)processor).getController();
				if ( controller.isPrimitive() ) {
					Initializer initializer = 
							new PrimitiveAeInitializer((PrimitiveAnalysisEngineController)controller, entry.getValue().getConsumerCount());
					entry.getValue().setInitializer(initializer);
					entry.getValue().initialize(controller);
				}
			}
			entry.getValue().start();
		}
		for(Entry<Origin, UimaAsProducer> entry : producers.entrySet()) {
			entry.getValue().start();
		}

	}
	@Override
	public void stop() throws Exception {
		for(Entry<Target, UimaAsConsumer> entry : consumers.entrySet()) {
			entry.getValue().stop();
		}
		for(Entry<Origin, UimaAsProducer> entry : producers.entrySet()) {
			entry.getValue().stop();
		}
		
	}

	public static void main(String[] args) {
		try {
			MessageProcessor dummyProcessor = 
					new TestClientMessageProcessor();
			
			DirectUimaAsEndpoint endpoint = 
					new DirectUimaAsEndpoint(dummyProcessor, "Client");

			MockupService service = 
					endpoint.new MockupService();
			
			service.initialize();
			service.start();
			
//			endpoint.createConsumer("direct:", ConsumerType.GetMetaResponse, 1);
//			endpoint.createConsumer("direct:", ConsumerType.ProcessCASResponse, 4);
//			endpoint.createConsumer("direct:", ConsumerType.CpcResponse, 1);
			
			UimaAsProducer producer = 
					endpoint.createProducer( new UimaAsOrigin(EndpointType.Direct.getName()+"serviceA", EndpointType.Direct));
		
			endpoint.start();
			
			MessageContext getMetaRequestMessage = 
				endpoint.newMessageBuilder()
				   .newGetMetaRequestMessage(new UimaAsOrigin("Client",EndpointType.Direct))
				   .withPayload(AsynchAEMessage.None)
				   .withReplyDestination(endpoint.getConsumer("direct:", ConsumerType.GetMetaResponse))
				   .build();

			UimaAsConsumer target = 
					service.getEndpoint().getConsumer("direct:", ConsumerType.GetMetaRequest);
			
			producer.dispatch((DirectMessage)getMetaRequestMessage.getRawMessage(), target);
			
		} catch( Exception e) {
			e.printStackTrace();
		}

	}


	private class MockupService {
		UimaAsEndpoint endpoint;
		
		public void initialize() throws Exception {
			
			MockUpAnalysisEngineController controller =
					new MockUpAnalysisEngineController("MockupService",4);
			TestMessageProcessor dummyProcessor = 
					new TestMessageProcessor(controller);
			
			endpoint = new DirectUimaAsEndpoint(dummyProcessor, "Service");
//			UimaAsConsumer getMetaRequestConsumer = endpoint.createConsumer("direct:", ConsumerType.GetMetaRequest, 1);
//			UimaAsConsumer processCasRequestConsumer = endpoint.createConsumer("direct:", ConsumerType.ProcessCASRequest, 4);
//			UimaAsConsumer cpcRequestConsumer = endpoint.createConsumer("direct:", ConsumerType.CpcRequest, 1);
			
			controller.addEndpoint(EndpointType.Direct, endpoint);
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


	}









}
