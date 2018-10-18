package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.Lifecycle;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.message.MessageContext;

public interface UimaAsEndpoint extends Lifecycle {
	public UimaAsProducer createProducer(String targetUri) throws Exception;
	public UimaAsProducer createProducer(UimaAsConsumer consumer, String delegateKey)  throws Exception;
	public UimaAsConsumer createConsumer(String targetUri, ConsumerType type, int consumerThreadCount) throws Exception;
	public void dispatch(MessageContext messageContext) throws Exception;
	public UimaAsConsumer getConsumer(String targetUri, ConsumerType type);
	public MessageContext createMessage(int command, int messageType, Endpoint endpoint);
}
