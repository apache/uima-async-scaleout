package org.apache.uima.as.connectors.direct;


import java.util.EnumSet;

import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;
import org.apache.uima.aae.message.Target;
import org.apache.uima.aae.message.UimaAsTarget;
import org.apache.uima.aae.definition.connectors.UimaAsProducer;
import org.apache.uima.as.client.DirectMessage;

public class DirectUimaAsProducer implements UimaAsProducer{
	private Target target;
	private UimaAsConsumer consumer;
	EnumSet<ConsumerType> requestSet = EnumSet.of(ConsumerType.GetMetaRequest,ConsumerType.ProcessCASRequest,ConsumerType.CpcRequest);
	EnumSet<ConsumerType> responseSet = EnumSet.of(ConsumerType.GetMetaResponse,ConsumerType.ProcessCASResponse,ConsumerType.CpcResponse);
	
	public DirectUimaAsProducer(String targetUri) {
		
	}

	public DirectUimaAsProducer(String name, UimaAsConsumer consumer) {
		this.consumer = consumer;
		this.target = new UimaAsTarget(name, EndpointType.Direct);
		switch(consumer.getType()) {
		case GetMetaRequest:
			
			break;
		case GetMetaResponse:
			
			break;
			
		case ProcessCASRequest:
			
			break;
			
		case ProcessCASResponse:
			
			break;
			
		case CpcRequest:
			
			break;
			
		case CpcResponse:
			
			break;
			
		default:
		}
	}
	public Target getTarget() {
		return target;
	}
	public boolean requestProducer() {
		return requestSet.contains(consumer.getType());
	}
	public boolean responseProducer() {
		return responseSet.contains(consumer.getType());
	}
	@Override
	public void start() throws Exception {
		
	}
	@Override
	public void stop() throws Exception {
		
	}

	@Override
	public void dispatch(DirectMessage message) throws Exception {

		consumer.consume(message);
		
	}
	@Override
	public void dispatch(DirectMessage message, UimaAsConsumer target) throws Exception {
		// hand over message to the target consumer
		target.consume(message);
		
	}
	public static void main(String[] args) {

	}

	@Override
	public ConsumerType getType() {
		return consumer.getType();
	}
}
