package org.apache.uima.aae.definition.connectors;

import java.util.Map;

import org.apache.uima.aae.Lifecycle;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer.ConsumerType;
import org.apache.uima.aae.message.MessageBuilder;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.aae.message.Target;

public interface UimaAsEndpoint extends Lifecycle {
	public enum EndpointType {
	  Direct("direct:"),
      JMS("jms:"), 
      NA("na:");
		
		private String name;
		
		private EndpointType(String aName) {
			name = aName;
		}
		
		public String getName() {
			return name;
		}
	};
	public MessageBuilder newMessageBuilder();
	public UimaAsProducer createProducer(Origin origin) throws Exception;
	//public UimaAsProducer createProducer(UimaAsConsumer consumer, Origin origin)  throws Exception;
	public UimaAsProducer createProducer(UimaAsConsumer consumer, String serviceUri) throws Exception; 
//	public UimaAsConsumer createConsumer(String targetUri, ConsumerType type, int consumerThreadCount) throws Exception;
	public UimaAsConsumer createConsumer(ConsumerType type, int consumerThreadCount) throws Exception;
	//public void dispatch(MessageContext messageContext) throws Exception;
	public void dispatch(MessageContext messageContext, String serviceUri) throws Exception;
	public UimaAsConsumer getConsumer(String targetUri, ConsumerType type);
	public EndpointType getType();
	public Origin getOrigin();
	public Map<Target,UimaAsConsumer> getConsumers();
}
