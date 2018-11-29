package org.apache.uima.aae.message;

import org.apache.uima.aae.controller.AnalysisEngineController;

public interface ServiceMessageProcessor extends MessageProcessor {
	public AnalysisEngineController getController();

}
