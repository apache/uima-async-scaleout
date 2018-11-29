package org.apache.uima.aae.component;

import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController_impl;
import org.apache.uima.aae.definition.connectors.basic.BasicConnector;
import org.apache.uima.resource.ResourceSpecifier;

public class PrimitiveAnalysisEngineComponent extends AnalysisEngineComponent {

	public PrimitiveAnalysisEngineComponent(String key, ResourceSpecifier rs) {
		super(key, rs);
	}
	/*
	@Override
	public boolean isScaleable() {
		return false;
	}

	@Override
	public boolean isCasMultiplier() {
		return false;
	}

	@Override
	public boolean isCasConsumer() {
		return false;
	}
*/
	@Override
	public boolean isPrimitive() {
		return true;
	}
/*
	@Override
	public boolean isRemote() {
		return false;
	}
	*/
	@Override
	public Object getConnector() {
		return new BasicConnector();
	}
//	@Override
//	public AnalysisEngineController newAnalysisEngineController(AnalysisEngineController parentController,
//			AsynchAECasManager_impl casManager, InProcessCache cache ) throws Exception {
//		throw new UnsupportedOperationException("This method should only be called on AggregateAnalysisEngineComponent");
//	}
	@Override
	public AnalysisEngineController newAnalysisEngineController(AnalysisEngineController parentController,
			 AsynchAECasManager_impl casManager, InProcessCache cache ) throws Exception {
		return new PrimitiveAnalysisEngineController_impl(parentController, getKey(), getResourceSpecifier().getSourceUrlString(),casManager, cache, 10, getScaleout());
	}

}
