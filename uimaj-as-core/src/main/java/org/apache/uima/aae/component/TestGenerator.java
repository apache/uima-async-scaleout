package org.apache.uima.aae.component;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.service.delegate.AggregateAnalysisEngineDelegate;
import org.apache.uima.aae.service.delegate.AnalysisEngineDelegate;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.AnalysisEngineType;
import org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType;
import org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType;
import org.apache.uima.resourceSpecifier.ServiceType;

public class TestGenerator {

	public AnalysisEngineDeploymentDescriptionDocument parseDD(String descriptorPath) throws Exception {
		return AnalysisEngineDeploymentDescriptionDocument.Factory.parse(new File(descriptorPath));	

	}
	private boolean isAggregate(AnalysisEngineType aet) {
		return ("true".equals(aet.getAsync()) || aet.isSetAsync() || aet.isSetDelegates());
	}
    private boolean isAggregate( ResourceSpecifier resourceSpecifier) {
    	boolean aggregate = false;
    	if (resourceSpecifier instanceof AnalysisEngineDescription ) {
    		AnalysisEngineDescription aeDescriptor = 
    				(AnalysisEngineDescription) resourceSpecifier;
     		
    		if ( !aeDescriptor.isPrimitive() ) {
    			aggregate = true;
    		}
    		//    		if ( d != null ) {
//    			if ((d instanceof AggregateAnalysisEngineDelegate) || 
//    				(d.isAsync() && !d.isPrimitive()) ) {
//    				aggregate = true;
//    			}
//    		} else if ( !aeDescriptor.isPrimitive() ) {
//    			aggregate = true;
//    		}
    	}
    	return aggregate;
    }

    private AnalysisEngineType findMatchInDD(String key) throws Exception {
    	
    	return null;
    }
    private AnalysisEngineDescription getAeDescription(ResourceSpecifier rs) {
		return (AnalysisEngineDescription) rs;
    }
	public AnalysisEngineComponent parse(ResourceSpecifier rs, String key) throws Exception {
		AnalysisEngineDescription aeDescriptor = getAeDescription(rs);
		AnalysisEngineComponent component = null;
		
		//AnalysisEngineType aet = findMatchInDD(rs.)
		if ( isAggregate(rs) ) {
			component = 
					new AggregateAnalysisEngineComponent(key, rs);
			
			Map<String, ResourceSpecifier> delegates =
    				aeDescriptor.getDelegateAnalysisEngineSpecifiers();
			
    		for(Entry<String, ResourceSpecifier> delegateEntry: delegates.entrySet() ) {
    			component.add(parse(delegateEntry.getValue(), delegateEntry.getKey() ));
    		}

    		
    		
/*    		
    		
    		
    		
    		
    		
    		// The DD object maintains two arrays, one for co-located delegates and the other for remotes.
			// First handle co-located delegates.
			if ( aet.getDelegates().getAnalysisEngineArray().length > 0 ) {
				DelegateAnalysisEngineType[] localAnalysisEngineArray =
						aet.getDelegates().getAnalysisEngineArray();
				
				// Add default error handling to each co-located delegate
				for( DelegateAnalysisEngineType delegate : localAnalysisEngineArray ) {
					String key = delegate.getKey();
					// recursively iterate over delegates until no more aggregates found
					aggregate.add(walk(delegate));
				}

				
				
				addColocatedDelegates(localAnalysisEngineArray,(AggregateAnalysisEngineDelegate)delegate);
			}
			// Next add remote delegates of this aggregate
			if ( hasRemoteDelegates(aet) ) {
				RemoteAnalysisEngineType[] remoteAnalysisEngineArray =
						aet.getDelegates().getRemoteAnalysisEngineArray();
				addRemoteDelegates(remoteAnalysisEngineArray, (AggregateAnalysisEngineDelegate)delegate);
			}
*/
		} else {
			component = new PrimitiveAnalysisEngineComponent(key, rs);
		}
		
		if ( aeDescriptor.getAnalysisEngineMetaData().getOperationalProperties().isMultipleDeploymentAllowed() ) {
			component.enableCasMultipler();
		}
		if ( aeDescriptor.getAnalysisEngineMetaData().getOperationalProperties().getOutputsNewCASes() ) {
			component.enableScaleout();
		}

		return component;
	}
	public static void main(String[] args) {
		try {
			
			TestGenerator generator = new TestGenerator();
			AnalysisEngineDeploymentDescriptionDocument dd = 
					generator.parseDD(args[0]);
			ServiceType service =
					dd.getAnalysisEngineDeploymentDescription().getDeployment().getService();
			ResourceSpecifier resourceSpecifier = 
					UimaClassFactory.produceResourceSpecifier(service.getTopDescriptor().getImport().getLocation());
			generator.walk(resourceSpecifier, null);  // null= top level
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

}
