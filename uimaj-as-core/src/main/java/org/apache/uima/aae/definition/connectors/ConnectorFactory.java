package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.definition.connectors.basic.BasicConnector;
import org.apache.uima.aae.definition.connectors.basic.DirectConnector;

public class ConnectorFactory {

	public static ComponentConnector newConnector(String protocol, String vendor) {
		ComponentConnector connector=null;
		switch(protocol.toLowerCase()) {
		case "jms":
			connector = getJmsConnector(vendor);
			break;
		case "direct":
			connector = new DirectConnector();
			break;
			
		default:
			connector = new BasicConnector();
		}
		return connector;
	}
	private static ComponentConnector getJmsConnector(String vendor) {
		return null;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
