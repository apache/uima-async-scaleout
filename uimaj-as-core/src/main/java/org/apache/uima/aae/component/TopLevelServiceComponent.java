package org.apache.uima.aae.component;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.uima.aae.controller.DelegateEndpoint;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType;
import org.apache.uima.resourceSpecifier.CasMultiplierType;
import org.apache.uima.resourceSpecifier.CasPoolType;
import org.apache.uima.resourceSpecifier.DeploymentType;
import org.apache.uima.resourceSpecifier.EnvironmentVariableType;
import org.apache.uima.resourceSpecifier.EnvironmentVariablesType;
import org.apache.uima.resourceSpecifier.ServiceType;
import org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType;

public class TopLevelServiceComponent extends AnalysisEngineComponent{
	private String name;
	private String description;
	private String version;
	private String vendor;
	
	private String protocol;
	private String provider;
	
	private int poolSize=1;
	private boolean processParentLast;
	private boolean disableJCasCache;
	private int initialHeapSize=500;
	
	private Endpoint endpoint;

	private List<EnvironmentVariable> envVariables = new ArrayList<>();
	private ComponentCasPool casPool = null;
	
	private AnalysisEngineComponent decoratedComponent;
	
	public TopLevelServiceComponent(AnalysisEngineComponent decorated, AnalysisEngineDeploymentDescriptionDocument dd) {
		super(decorated.getKey(),decorated.getResourceSpecifier());
		this.decoratedComponent = decorated;
		DeploymentType deployment = 
				dd.getAnalysisEngineDeploymentDescription().getDeployment();

		ServiceType service =
				dd.getAnalysisEngineDeploymentDescription().getDeployment().getService();

		if ( Objects.nonNull(service.getEnvironmentVariables()) ) {
			configureEnvironmentVariables(service.getEnvironmentVariables());
		}
		
		if ( Objects.nonNull(deployment.getCasPool()) ) {
			configureCasPool(deployment.getCasPool());
		}
		
		if ( Objects.nonNull(service.getAnalysisEngine())&&
		     Objects.nonNull(service.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration())	) {
				AsyncPrimitiveErrorConfigurationType topLevelErrorConfiguration =
						service.getAnalysisEngine().getAsyncPrimitiveErrorConfiguration();
				configureErrorHandling(topLevelErrorConfiguration);
		}
		withName(dd.getAnalysisEngineDeploymentDescription().getName()).
		withDescription(dd.getAnalysisEngineDeploymentDescription().getDescription()).
		withVersion(dd.getAnalysisEngineDeploymentDescription().getVersion()).
		withVendor(dd.getAnalysisEngineDeploymentDescription().getVendor()).
		withProtocol(deployment.getProtocol()).
		withProvider(deployment.getProvider());
		
		String brokerURL = service.getInputQueue().getBrokerURL();
		String queueName = service.getInputQueue().getEndpoint();
		int prefetch = service.getInputQueue().getPrefetch();
		configureEndpoint( queueName, brokerURL, prefetch);
		if ( Objects.nonNull(service.getAnalysisEngine()) ) {
			configureAnalysisEngine(service.getAnalysisEngine());

			if ( Objects.nonNull(service.getAnalysisEngine().getCasMultiplier()) ) {
				configureCasMultiplier(service.getAnalysisEngine().getCasMultiplier());
			}
		}
	}
	@Override
	public Endpoint getEndpoint() {
		return endpoint;
	}

	private void configureEndpoint(String name, String server, int prefetch) {
		endpoint = new DelegateEndpoint().new Builder().withDelegateKey(getKey()).withEndpointName(name)
				.setRemote(isRemote()).setServerURI(server).withResourceSpecifier(getResourceSpecifier()).build();
		if (isCasMultiplier()) {
			endpoint.setIsCasMultiplier(true);
			endpoint.setProcessParentLast(processParentLast);
			if (poolSize > 1) {
				endpoint.setShadowCasPoolSize(poolSize);
			}
			if (initialHeapSize > 0) {
				endpoint.setInitialFsHeapSize(initialHeapSize);
			}
			endpoint.setDisableJCasCache(disableJCasCache);
		}
	}
	private void configureEnvironmentVariables(EnvironmentVariablesType evt) {
		for( EnvironmentVariableType ev : evt.getEnvironmentVariableArray() ) {
			EnvironmentVariable envVariable = new EnvironmentVariable(ev.getName(), ev.getStringValue());
			envVariables.add(envVariable);
		}
	}
	private int convertStringToint(String value, int defaultValue) {
		try {
			return Integer.valueOf(value);
		} catch( Exception e ) {
			return defaultValue;
		}
	}
	private void configureErrorHandling(AsyncPrimitiveErrorConfigurationType topLevelErrorConfiguration) {
		
	}
	private void configureAnalysisEngine(TopLevelAnalysisEngineType tlae) {
		int replyQueueScaleout = convertStringToint(tlae.getInternalReplyQueueScaleout(),1);
		int inputQueueScaleout = convertStringToint(tlae.getInputQueueScaleout(),1);
		boolean async = false;  // default
		int scaleout = Objects.nonNull(tlae.getScaleout())? tlae.getScaleout().getNumberOfInstances() : 1;
		if ( Objects.nonNull(tlae.getAsync()) ) {
			async = Boolean.parseBoolean(tlae.getAsync());
		}
		
		decoratedComponent.withScaleout(scaleout)
		                  .withReplyThreadPoolSize(replyQueueScaleout)
		                  .withRequestThreadPoolSize(inputQueueScaleout);
		// Component is async iff 'async=true' or dd has delegates
		if ( !decoratedComponent.isPrimitive() && (async || Objects.nonNull(tlae.getDelegates()))  ) {
			decoratedComponent.enableAsync();
		}
		                  
//		String async = tlae.getAsync();  // true or false
//		String replyQueueScaleout = tlae.getInternalReplyQueueScaleout();
//		String key = tlae.getKey();
//		int scaleout = tlae.getScaleout().getNumberOfInstances();
	}
	public AggregateAnalysisEngineComponent aggregateComponent() throws InvalidObjectException{
		if ( !isAggregate() ) {
			throw new InvalidObjectException("This component is not an aggregate");
		}
		return (AggregateAnalysisEngineComponent)decoratedComponent;
	}
	public boolean isAggregate() {
		return decoratedComponent instanceof AggregateAnalysisEngineComponent;
	}
	@Override
	public boolean isPrimitive() {
		return !isAggregate();
	}
	private void configureCasPool(CasPoolType casPoolType) {
		boolean disableJCasCache = casPoolType.getDisableJCasCache();
		int initialHeapSize = casPoolType.getInitialFsHeapSize();
		int poolSize = casPoolType.getNumberOfCASes();
		
		casPool = new ComponentCasPool(disableJCasCache, initialHeapSize, poolSize);
		
	}
	private void configureCasMultiplier(CasMultiplierType casMultiplierType ) {
		poolSize = casMultiplierType.getPoolSize();
		disableJCasCache = casMultiplierType.getDisableJCasCache();  
		initialHeapSize = Integer.parseInt(casMultiplierType.getInitialFsHeapSize());
		processParentLast = Boolean.parseBoolean(casMultiplierType.getProcessParentLast());   // true or false
	}
	public void addEnvVariable(String name, String value) {
		envVariables.add(new EnvironmentVariable(name, value));
	}
	public TopLevelServiceComponent withName(String name) {
		this.name = name;
		return this;
	}
	public TopLevelServiceComponent withDescription(String description) {
		this.description = description;
		return this;
	}
	public TopLevelServiceComponent withVersion(String version) {
		this.version = version;
		return this;
	}
	public TopLevelServiceComponent withVendor(String vendor) {
		this.vendor = vendor;
		return this;
	}
	public TopLevelServiceComponent withProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}
	public TopLevelServiceComponent withProvider(String provider) {
		this.provider = provider;
		return this;
	}
	
	
	public ComponentCasPool getComponentCasPool() {
		if ( Objects.isNull(casPool)) {
			return new ComponentCasPool(false, 1000, 1);
		} else {
			return casPool;
		}
			
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getVersion() {
		return version;
	}
	public String getVendor() {
		return vendor;
	}
	public String getProtocol() {
		return protocol;
	}
	public String getProvider() {
		return provider;
	}

	@Override
	public Object getConnector() {
		return null;
	}
	
	public class EnvironmentVariable {
		String name;
		String value;
		
		public EnvironmentVariable( String name, String value ) {
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}


	}
}
