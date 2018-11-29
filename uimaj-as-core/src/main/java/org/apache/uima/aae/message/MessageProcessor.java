package org.apache.uima.aae.message;

public interface MessageProcessor {

	public void process(MessageContext message) throws Exception;
}
