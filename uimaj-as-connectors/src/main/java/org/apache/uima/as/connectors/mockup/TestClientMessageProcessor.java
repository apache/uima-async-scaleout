package org.apache.uima.as.connectors.mockup;

import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.as.client.DirectMessageContext;

public class TestClientMessageProcessor implements MessageProcessor {

	public TestClientMessageProcessor() {
		
	}
	@Override
	public void process(MessageContext message) throws Exception {
		if ( message instanceof DirectMessageContext ) {
			process((DirectMessageContext)message);
		}
	}
	
	private void process(DirectMessageContext message) {
		System.out.println("Client Message Processor Received Message From:"+
		        message.getMessage().getOrigin());

	}

}
