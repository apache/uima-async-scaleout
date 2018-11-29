package org.apache.uima.aae.service.command;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.message.ServiceMessageProcessor;
import org.apache.uima.aae.message.UimaAsMessage;

public class UimaAsMessageProcessor implements ServiceMessageProcessor {

	private AnalysisEngineController controller;
	
	public UimaAsMessageProcessor(AnalysisEngineController ctlr) {
		controller = ctlr;
	}
	@Override
	public void process(MessageContext message ) throws Exception {
		UimaAsCommand command = 
				CommandFactory.newCommand(message, controller);
		command.execute();
	}
	@Override
	public AnalysisEngineController getController() {
		return controller;
	}

}
