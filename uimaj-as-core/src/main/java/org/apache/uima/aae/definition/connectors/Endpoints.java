package org.apache.uima.aae.definition.connectors;

import java.lang.reflect.Constructor;

import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.service.command.UimaAsMessageProcessor;

public class Endpoints {

	public static UimaAsEndpoint newEndpoint(EndpointType type, String name) throws Exception {
		return newEndpoint(type, name, null);
	}

	public static UimaAsEndpoint newEndpoint(EndpointType type, String name, MessageProcessor processor) throws Exception {
		UimaAsEndpoint endpoint=null;
		Class<?> clz ;
		switch(type) {
			case Direct:
				clz = Class.forName("org.apache.uima.as.connectors.direct.DirectUimaAsEndpoint");
				Constructor<?> ctor = clz.getConstructor(new Class[] {MessageProcessor.class, String.class});
				endpoint = (UimaAsEndpoint)ctor.newInstance(new Object[] {processor,name});
			break;
			
			case JMS:
			
			break;
			
			default:
				
		}
		
		return endpoint;
	}
}
