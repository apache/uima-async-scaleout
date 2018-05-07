package org.apache.uima.adapter.jms.service.builder;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.adapter.jms.activemq.UimaDefaultMessageListenerContainer;

public class JmsProcessListener {

	AnalysisEngineController controller;
	
	JmsProcessListener(AnalysisEngineController controller) {
		this.controller = controller;
	}
	public void create() throws Exception {
		JmsMessageListenerBuilder listenerBuilder = 
				new JmsMessageListenerBuilder();
/*
		UimaDefaultMessageListenerContainer messageListener =
				listenerBuilder.withController(controller)
		       			.withType(type)
						.withConectionFactory(factory)
						.withThreadPoolExecutor(threadExecutor)
						.withConsumerCount(consumerCount)
						.withInputChannel(inputChannel)
						.withPriorityMessageHandler(h)
						.withSelector(getSelector(type))
						.withDestination(destination)
						.build();
		messageListener.setReceiveTimeout(500);
	*/
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
