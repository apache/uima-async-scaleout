package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.message.MessageProcessor;

public abstract class AbstractUimaAsConsumer implements UimaAsConsumer{
	
	protected abstract void setMessageProcessor(MessageProcessor processor);

}
