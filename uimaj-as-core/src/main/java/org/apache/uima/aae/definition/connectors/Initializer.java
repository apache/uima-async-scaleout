package org.apache.uima.aae.definition.connectors;

import java.util.concurrent.ExecutorService;

public interface Initializer {
	public ExecutorService initialize(ListenerCallback callback) throws Exception;
}
