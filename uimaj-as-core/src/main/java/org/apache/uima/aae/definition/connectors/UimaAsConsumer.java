package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.Lifecycle;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.as.client.DirectMessage;

public interface UimaAsConsumer extends Lifecycle {
	public enum ConsumerType {GetMeta,ProcessCAS,Cpc,FreeCAS,Reply,Info};
	
	public void initialize() throws Exception;
	public void initialize(AnalysisEngineController controller) throws Exception;
	
	public void consume(DirectMessage message) throws Exception;
	
	public ConsumerType getType();
	
}
