package org.apache.uima.aae.message;

import org.apache.uima.aae.controller.AnalysisEngineController;

public interface MessageProcessor {

	public void process(MessageContext message) throws Exception;
	public AnalysisEngineController getController();
}
