package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.Lifecycle;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.message.Target;
import org.apache.uima.as.client.DirectMessage;

public interface UimaAsProducer extends Lifecycle {

	public void dispatch(DirectMessage message) throws Exception;
	public void dispatch(DirectMessage message, UimaAsConsumer target) throws Exception;

	public ConsumerType getType();
	public boolean requestProducer();
	public boolean responseProducer();
	
	public Target getTarget();
}
