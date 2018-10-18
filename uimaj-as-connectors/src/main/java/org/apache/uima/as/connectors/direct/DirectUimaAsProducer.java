package org.apache.uima.as.connectors.direct;


import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsProducer;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.UimaAsMessage;
import org.apache.uima.as.client.DirectMessage;

public class DirectUimaAsProducer implements UimaAsProducer{

	private UimaAsConsumer consumer;
	
	public DirectUimaAsProducer(String targetUri) {
		
	}
	public DirectUimaAsProducer(UimaAsConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub

	}
}
