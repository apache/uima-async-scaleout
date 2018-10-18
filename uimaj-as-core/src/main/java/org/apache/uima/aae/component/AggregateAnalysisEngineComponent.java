package org.apache.uima.aae.component;

import org.apache.uima.aae.definition.connectors.basic.BasicConnector;
import org.apache.uima.resource.ResourceSpecifier;

public class AggregateAnalysisEngineComponent extends AnalysisEngineComponent {

	public AggregateAnalysisEngineComponent(String key, ResourceSpecifier rs) {
		super(key, rs);
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public Object getConnector() {
		return new BasicConnector();
	}


}
