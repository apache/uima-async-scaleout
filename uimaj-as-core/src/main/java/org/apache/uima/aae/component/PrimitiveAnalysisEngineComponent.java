package org.apache.uima.aae.component;

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

}
