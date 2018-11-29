package org.apache.uima.aae.component;

import org.apache.uima.aae.controller.AnalysisEngineController;

public interface ComponentVisitor {
	public AnalysisEngineController visit(AnalysisEngineController parent) throws Exception;
}
