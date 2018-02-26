package org.apache.uima.aae.controller;

import java.util.concurrent.CountDownLatch;

import org.apache.uima.aae.UimaASApplicationEvent.EventTrigger;

public class ControllerStatusListener implements ControllerCallbackListener  {
	CountDownLatch latch;
	public ControllerStatusListener(CountDownLatch latch) {
		this.latch = latch;
	}
	public void notifyOnTermination(String aMessage, EventTrigger cause) {
	}

	public void notifyOnInitializationFailure(AnalysisEngineController aController, Exception e) {
	}

	public void notifyOnInitializationSuccess(AnalysisEngineController aController) {
		System.out.println("------- Controller:"+aController.getName()+" Initialized");
		latch.countDown();
	}

	public void notifyOnInitializationFailure(Exception e) {
	}

	public void notifyOnInitializationSuccess() {
	}

	public void notifyOnReconnecting(String aMessage) {
	}

	public void notifyOnReconnectionSuccess() {
	}

}
