package org.apache.uima.aae.definition.connectors;

import java.util.Map;

public interface UimaAsConnector {

	public UimaAsEndpoint createEndpoint(String uri, Map<String, Object> params) 
	throws Exception;
	
	
}
