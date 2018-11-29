package org.apache.uima.aae.message;

import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;

public interface Origin {
	public String getUniqueId();
	public String getName();
	public EndpointType getType();
}
