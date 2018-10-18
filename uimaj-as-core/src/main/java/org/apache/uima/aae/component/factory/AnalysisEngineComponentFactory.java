package org.apache.uima.aae.component.factory;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.component.AggregateAnalysisEngineComponent;
import org.apache.uima.aae.component.AnalysisEngineComponent;
import org.apache.uima.aae.component.PrimitiveAnalysisEngineComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceSpecifier;

public class AnalysisEngineComponentFactory {
	
    private AnalysisEngineDescription getAeDescription(ResourceSpecifier rs) {
		return (AnalysisEngineDescription) rs;
    }
    
	public AnalysisEngineComponent produce(ResourceSpecifier rs, String key) throws Exception {
		AnalysisEngineDescription aeDescriptor = getAeDescription(rs);
		AnalysisEngineComponent component = null;
		
		if ( aeDescriptor.isPrimitive() ) {
			component = new PrimitiveAnalysisEngineComponent(key, rs);
		} else {
			component = 
					new AggregateAnalysisEngineComponent(key, rs);
			Map<String, ResourceSpecifier> delegates =
    				aeDescriptor.getDelegateAnalysisEngineSpecifiers();
    		for(Entry<String, ResourceSpecifier> delegateEntry: delegates.entrySet() ) {
    			component.add(produce(delegateEntry.getValue(), delegateEntry.getKey() ));
    		}
		}
		
		if ( aeDescriptor.getAnalysisEngineMetaData().getOperationalProperties().isMultipleDeploymentAllowed() ) {
			component.enableScaleout();
		}
		if ( aeDescriptor.getAnalysisEngineMetaData().getOperationalProperties().getOutputsNewCASes() ) {
			component.enableCasMultipler();
		}

		return component;
	}
	public static void main(String[] args ) {
		try {
			AnalysisEngineComponentFactory factory = 
					new AnalysisEngineComponentFactory();
			ResourceSpecifier resourceSpecifier = 
					UimaClassFactory.produceResourceSpecifier(args[0]);
			factory.produce(resourceSpecifier, "TopLevel");
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
