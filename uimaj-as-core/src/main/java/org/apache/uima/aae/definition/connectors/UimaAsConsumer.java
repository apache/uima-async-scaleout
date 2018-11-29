package org.apache.uima.aae.definition.connectors;

import org.apache.uima.aae.Lifecycle;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.message.Target;
import org.apache.uima.as.client.DirectMessage;

public interface UimaAsConsumer extends Lifecycle {
	public enum ConsumerType {GetMetaRequest,GetMetaResponse,ProcessCASRequest,ProcessCASResponse, CpcRequest, CpcResponse, FreeCASRequest, Reply, Info};
	
	public void initialize() throws Exception;
	public void initialize(AnalysisEngineController controller) throws Exception;
	public void setInitializer(Initializer initializer);
	public int getConsumerCount();
	public void consume(DirectMessage message) throws Exception;
	
	public ConsumerType getType();
	
	public void delegateTo(UimaAsConsumer delegate);
	public Target getTarget();
	
}
