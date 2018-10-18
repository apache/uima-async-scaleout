package org.apache.uima.as.connectors.direct;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.aae.definition.connectors.UimaAsConnector;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;

public class DirectUimaAsConnector implements UimaAsConnector {

	private final Map<String, UimaAsEndpoint> endpoints = 
			new HashMap<>();
	

	@Override
	public UimaAsEndpoint createEndpoint(String uri, Map<String, Object> params) throws Exception {
		UimaAsEndpoint endpoint = new DirectUimaAsEndpoint();
		endpoints.putIfAbsent(uri, endpoint);
		return endpoint;
	}

	public static void main(String[] args) {

	}

}
