package org.apache.uima.aae.definition.connectors.jms;

import org.apache.uima.aae.definition.connectors.ComponentConnector;

public class ActiveMqConnector implements ComponentConnector {
	private final Object connection;
	
	ActiveMqConnector(String queue, String broker, int prefetch) {
		connection = new ActiveMqConnection(queue, broker, prefetch);
	}
	@Override
	public Object getConnectionInfo() {
		return connection;
	}

	public class ActiveMqConnection {
		private final String queueName;
		private final String brokerUrl;
		private final int prefetch;
		
		ActiveMqConnection(String queue, String broker, int prefetch) {
			this.queueName = queue;
			this.brokerUrl = broker;
			this.prefetch = prefetch;
		}
		public String getQueueName() {
			return queueName;
		}
		public String getBrokerUrl() {
			return brokerUrl;
		}
		public int getPrefetch() {
			return prefetch;
		}
	}
}
