package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.Lifecycle;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.UimaAsMessage;
import org.apache.uima.as.client.DirectMessage;

public interface UimaAsProducer extends Lifecycle {

	public void dispatch(DirectMessage message) throws Exception;
	public void dispatch(DirectMessage message, UimaAsConsumer target) throws Exception;

}
