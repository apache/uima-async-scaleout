package org.apache.uima.aae.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.aae.controller.DelegateEndpoint;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.DelegateEndpoint.Builder;
import org.apache.uima.resource.ResourceSpecifier;

public abstract class AnalysisEngineComponent  {

	protected List<AnalysisEngineComponent> delegateList = new ArrayList<>();
	private boolean isCasMultiplier = false;
	private boolean scaleable = false;
	private boolean async = false;
	private String componentKey;
	private int scaleout = 1;
	private ResourceSpecifier resourceSpecifier;
	private CasMultiplierNature casMultiplier;
	private int requestThreadPoolSize=1;
	private int responseThreadPoolSize=1;
	private Endpoint endpoint = null;
	
	
	public abstract Object getConnector();

	public AnalysisEngineComponent() {}
	public AnalysisEngineComponent(String key, ResourceSpecifier rs) {
		componentKey = key;
		resourceSpecifier = rs;
	}

	
	public CasMultiplierNature getCasMultiplierNature() {
		return casMultiplier;
	}
	public ResourceSpecifier getResourceSpecifier() {
		return resourceSpecifier;
	}

	public boolean isScaleable() {
		return scaleable;
	}

	public int getScaleout() {
		return scaleout;
	}
	
	public boolean isCasMultiplier() {
		return isCasMultiplier;
	}
	public boolean isAsync() {
		return async;
	}
	public boolean isCasConsumer() {
		return false;
	}

	public boolean isPrimitive() {
		return true;
	}

	public boolean isRemote() {
		return false;
	}
	public String getKey() {
		return componentKey;
		
	}
	public Endpoint getEndpoint() {
		if ( endpoint == null ) {
			String serviceEndpoint = getKey();
			String server = "java";
			if ( this instanceof RemoteAnalysisEngineComponent ) {
				serviceEndpoint = ((RemoteAnalysisEngineComponent)this).getEndpointName();
				server = ((RemoteAnalysisEngineComponent)this).getServer();
			} 
			endpoint =  new DelegateEndpoint(). new Builder().
					  withDelegateKey(getKey()).
					  withEndpointName(serviceEndpoint).
					  setRemote(isRemote()).
					  setServerURI(server).
				      withResourceSpecifier(getResourceSpecifier()).
				      build();
			if ( isCasMultiplier ) {
				endpoint.setIsCasMultiplier(true);
				endpoint.setProcessParentLast(casMultiplier.processParentLast());
				if ( casMultiplier.getPoolSize() > 1) {
					endpoint.setShadowCasPoolSize(casMultiplier.getPoolSize());
				}
				if ( casMultiplier.getInitialFsHeapSize() > 0 ) {
					endpoint.setInitialFsHeapSize( (int) casMultiplier.getInitialFsHeapSize());
				}
				endpoint.setDisableJCasCache(casMultiplier.disableJCasCache());		
			}
			if ( isRemote()) {
				endpoint.setMetadataRequestTimeout(((RemoteAnalysisEngineComponent)this).getMetaTimeout());
				endpoint.setProcessRequestTimeout(((RemoteAnalysisEngineComponent)this).getProcessTimeout());
				endpoint.setCollectionProcessCompleteTimeout(((RemoteAnalysisEngineComponent)this).getCollectionProcessCompleteTimeout());
			}
		}

		return endpoint;
	}
	public void add(AnalysisEngineComponent component) {
		delegateList.add(component);
	}
	public AnalysisEngineComponent getChild(int index) {
		return delegateList.get(index);
	}
	public List<AnalysisEngineComponent> getChildren() {
		return delegateList;
	}
	public AnalysisEngineComponent enableScaleout() {
		scaleable = true;
		return this;
	}
	public AnalysisEngineComponent withScaleout(int howManyInstances) {
		scaleout = howManyInstances;
		return this;
	}

	public AnalysisEngineComponent withRequestThreadPoolSize(int howManyThreads) {
		requestThreadPoolSize = howManyThreads;
		return this;
	}

	public AnalysisEngineComponent withReplyThreadPoolSize(int howManyThreads) {
		responseThreadPoolSize = howManyThreads;
		return this;
	}

	public AnalysisEngineComponent enableCasMultiplierNatureWith(CasMultiplierNature cm) {
		this.casMultiplier = cm;
		return this;
	}
	public AnalysisEngineComponent enableCasMultipler() {
		isCasMultiplier = true;
		return this;
	}
	public AnalysisEngineComponent enableAsync() {
		async = true;
		return this;
	}

}
