package org.apache.uima.aae.service.builder;

import org.apache.uima.aae.component.AnalysisEngineComponent;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.service.UimaASService;

public interface UimaAsServiceWrapperCreator {
	public UimaASService create(AnalysisEngineController controller, AnalysisEngineController parentController, AnalysisEngineComponent component)  throws Exception ;
}
