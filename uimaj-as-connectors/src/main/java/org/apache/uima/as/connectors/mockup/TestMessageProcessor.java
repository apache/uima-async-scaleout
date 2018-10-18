package org.apache.uima.as.connectors.mockup;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.service.command.CommandFactory;
import org.apache.uima.aae.service.command.UimaAsCommand;

public class TestMessageProcessor implements MessageProcessor {
	AnalysisEngineController controller;
	
	public TestMessageProcessor(AnalysisEngineController ctlr) {
		this.controller = ctlr;
	}
	@Override
	public void process(MessageContext message) throws Exception {
		UimaAsCommand command = 
				CommandFactory.newCommand(message, controller);
		command.execute();
	}

	@Override
	public AnalysisEngineController getController() {
		// TODO Auto-generated method stub
		return controller;
	}
	
}