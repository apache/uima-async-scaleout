package org.apache.uima.aae.service.builder;

import java.util.List;

import org.apache.uima.aae.component.AnalysisEngineComponent;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.service.UimaASService;

public interface ControllerBuilder {
	public AnalysisEngineController build(AnalysisEngineComponent component) throws Exception;
	public List<UimaASService> getServiceList();
}
