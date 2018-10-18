package org.apache.uima.aae.component.dd;

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.apache.uima.aae.UimaASUtils;
import org.apache.uima.aae.UimaClassFactory;
import org.apache.uima.aae.component.AnalysisEngineComponent;
import org.apache.uima.aae.component.CasMultiplierComponent;
import org.apache.uima.aae.component.CasMultiplierNature;
import org.apache.uima.aae.component.RemoteAnalysisEngineComponent;
import org.apache.uima.aae.component.TopLevelServiceComponent;
import org.apache.uima.aae.component.factory.AnalysisEngineComponentFactory;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.AnalysisEngineType;
import org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType;
import org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType;
import org.apache.uima.resourceSpecifier.ServiceType;
import org.apache.xmlbeans.XmlDocumentProperties;

public class DeploymentDescriptorProcessor {
	
	private AnalysisEngineDeploymentDescriptionDocument dd = null;
	public DeploymentDescriptorProcessor() {
		
	}
	public DeploymentDescriptorProcessor(AnalysisEngineDeploymentDescriptionDocument dd) {
		this.dd = dd;
	}
	public AnalysisEngineComponent newComponent(String descriptorPath) throws Exception {
		this.dd = parseDD(descriptorPath);
		return newComponent();
	}
	
	public TopLevelServiceComponent newComponent() throws Exception {
		ServiceType service =
				dd.getAnalysisEngineDeploymentDescription().getDeployment().getService();
		XmlDocumentProperties dp = dd.documentProperties();
		System.out.println(dp.getSourceName());

		// get absolute path to resource specifier
		String  aeDescriptor = 
				UimaASUtils.fixPath(dp.getSourceName(), getDescriptor(service));

		// Get top level uima resource specifier
		ResourceSpecifier resourceSpecifier = 
				UimaClassFactory.produceResourceSpecifier(aeDescriptor);

		AnalysisEngineComponentFactory componentFactory =
				new AnalysisEngineComponentFactory();
		// Process top level AE resource specifier and all its delegates.
		// For aggregates, recursively walk through a delegate tree, producing 
		// a tree of AnalysisEngineComponent instances, one for every delegate.
		
		AnalysisEngineComponent aeComponent = 
				componentFactory.produce(resourceSpecifier, null);

		// Decorate above with top level component functionality
		TopLevelServiceComponent topLevelComponent = 
				new TopLevelServiceComponent(aeComponent, dd);

//		if ( aeComponent.isPrimitive()) {
//			// The AE descriptor is for a primitive AE
//		} else {
			// the AE descriptor is for an aggregate AE. Check DD to
			// see if its an async aggregate. Its async=true or
			// has delegates.
//		if ( isAggregate(service.getAnalysisEngine()) ) {
		if ( topLevelComponent.isAggregate()) {
				// All delegates will be colocated unless
				// a delegate is remote. That is determined 
				// below.
//				aeComponent.enableAsync();
				//if ( dd.getAnalysisEngineDeploymentDescription().getDeployment().getProtocol()
				DelegateAnalysisEngineType[] colocatedDelegates = null;
				if ( Objects.nonNull(service.getAnalysisEngine()) &&
					 Objects.nonNull(service.getAnalysisEngine().getDelegates()) ) {
					colocatedDelegates = service.getAnalysisEngine().
											getDelegates().
											getAnalysisEngineArray();
					
				}
				handleColocatedDelegates(colocatedDelegates, aeComponent.getChildren());
				
				RemoteAnalysisEngineType[] remoteDelegates = null;
				if ( Objects.nonNull(service.getAnalysisEngine()) &&
					 Objects.nonNull(service.getAnalysisEngine().getDelegates())) {
					remoteDelegates = service.getAnalysisEngine().
									getDelegates().
									getRemoteAnalysisEngineArray();
				}
				handleRemoteDelegates(remoteDelegates, aeComponent.getChildren());

//				service.getAnalysisEngine().
//		        getDelegates().
			//}
		} 

		
		return topLevelComponent;
	}
	public AnalysisEngineDeploymentDescriptionDocument parseDD(String descriptorPath) throws Exception {
		return AnalysisEngineDeploymentDescriptionDocument.Factory.parse(new File(descriptorPath));	

	}
	private boolean isAggregate(AnalysisEngineType aet) {
		return ("true".equals(aet.getAsync()) || aet.isSetAsync() || aet.isSetDelegates());
	} 
	private String getDescriptor(ServiceType service) {
		String aeDescriptor = service.getTopDescriptor().getImport().getLocation();
		if ( aeDescriptor == null ) {
			aeDescriptor = service.getTopDescriptor().getImport().getName();
		}
		return aeDescriptor;
	}
	
	private void markAllDelegatesAsAsync(List<AnalysisEngineComponent> resourceSpecifierDelegates) {
		for ( AnalysisEngineComponent aec : resourceSpecifierDelegates ) {
			if ( !aec.isPrimitive() ) {
				handleColocatedDelegates(null, aec.getChildren());
			}
			aec.enableAsync();
		}
		
	}
	private void handleColocatedDelegates(DelegateAnalysisEngineType[] ddDelegates, List<AnalysisEngineComponent> resourceSpecifierDelegates ) {
		if ( Objects.isNull(ddDelegates)) {
			// the dd does not include delegates but is configured as an asynch service
			// so process resource specifiers recursively marking each part of a pipeline
			// as asynch so that it is deployed as a collocated asynch service.
			handleDefaultColocatedDelegates(resourceSpecifierDelegates);
			//markAllDelegatesAsAsync(resourceSpecifierDelegates);
			return; 
		}
		// go through all delegates defined in the deployment descriptor (dd)
		for( DelegateAnalysisEngineType ddDelegate : ddDelegates ) {
			// find a matching delegate in the AE resource specifier
			for( AnalysisEngineComponent resourceSpecifierDelegate : resourceSpecifierDelegates ) {
				if ( ddDelegate.getKey().equals(resourceSpecifierDelegate.getKey())) {
					if (  resourceSpecifierDelegate.isCasMultiplier() && Objects.nonNull(ddDelegate.getCasMultiplier())) {
						// plugin cas multiplier settings from dd
						CasMultiplierNature casMultiplier =
								new CasMultiplierComponent(ddDelegate.getCasMultiplier().getDisableJCasCache(), 
										                   TypeConverter.convertStringToLong(ddDelegate.getCasMultiplier().getInitialFsHeapSize(), 1000), 
										                   ddDelegate.getCasMultiplier().getPoolSize(),
										                   TypeConverter.convertStringToBoolean(ddDelegate.getCasMultiplier().getProcessParentLast(),true) );
						resourceSpecifierDelegate.enableCasMultiplierNatureWith(casMultiplier);
						
					}
					resourceSpecifierDelegate.enableAsync();   // delegate is async
					
					resourceSpecifierDelegate.
					      withScaleout(Objects.isNull(ddDelegate.getScaleout()) ? 1 :ddDelegate.getScaleout().getNumberOfInstances()).
					      withRequestThreadPoolSize( TypeConverter.convertStringToInt(ddDelegate.getInputQueueScaleout(), 1)).
					      withReplyThreadPoolSize( TypeConverter.convertStringToInt(ddDelegate.getInternalReplyQueueScaleout(),1));
										
					if ( isAggregate(ddDelegate) ) {
						
						resourceSpecifierDelegate.enableAsync();
						for ( AnalysisEngineComponent aec : resourceSpecifierDelegate.getChildren() ) {
							aec.enableAsync();
						}
						if ( ddDelegate.getDelegates() != null ) {
							// recursively process collocated delegates
							handleColocatedDelegates(ddDelegate.getDelegates().getAnalysisEngineArray() , resourceSpecifierDelegate.getChildren());
							handleRemoteDelegates(ddDelegate.getDelegates().getRemoteAnalysisEngineArray(), resourceSpecifierDelegate.getChildren());
						}
						
					} 
					break;  // found a match and completed processing it. We are done with it.
				}
			}
		}
	}

	private void handleDefaultColocatedDelegates(List<AnalysisEngineComponent> resourceSpecifierDelegates) {
		// find a matching delegate in the AE resource specifier
		for (AnalysisEngineComponent resourceSpecifierDelegate : resourceSpecifierDelegates) {
			if (resourceSpecifierDelegate.isCasMultiplier()) {
				// plugin cas multiplier settings from dd
				CasMultiplierNature casMultiplier = new CasMultiplierComponent(false, 1000, 1, true);
				resourceSpecifierDelegate.enableCasMultiplierNatureWith(casMultiplier);
			}
			resourceSpecifierDelegate.withScaleout(1).
				withRequestThreadPoolSize(1).
				withReplyThreadPoolSize(1).
				enableAsync();
			
			if (!resourceSpecifierDelegate.isPrimitive()) {
				handleDefaultColocatedDelegates(resourceSpecifierDelegate.getChildren());
			}
		}
	}
	private void handleRemoteDelegates(RemoteAnalysisEngineType[] remoteDelegates, List<AnalysisEngineComponent> resourceSpecifierDelegates ) {
		if ( Objects.isNull(remoteDelegates) ) {
			return;
		}
		for( RemoteAnalysisEngineType remoteDelegateType : remoteDelegates ) {
			// find a matching delegate in the AE resource specifier
			for( AnalysisEngineComponent resourceSpecifierDelegate : resourceSpecifierDelegates ) {
				if ( remoteDelegateType.getKey().equals(resourceSpecifierDelegate.getKey())) {
					// find an index of the current component in the list. We 
					// will decorate this component as a remote, and replace
					// it in the list.
					int index = 
							resourceSpecifierDelegates.indexOf(resourceSpecifierDelegate);
					// Decorate existing component with remote flavor
					RemoteAnalysisEngineComponent remoteDelegate = 
							new RemoteAnalysisEngineComponent(resourceSpecifierDelegate, remoteDelegateType);
					
					//replace component with decorated remote
					resourceSpecifierDelegates.set(index, remoteDelegate);
				}
				
			}
			
		}
	}
/*	
	public void parse(DelegateAnalysisEngineType colocatedDelegate, AnalysisEngineComponent component) {
		if ( isAggregate(colocatedDelegate) ) {
			DelegateAnalysisEngineType[] colocatedDelegates = 
					colocatedDelegate.getDelegates().getAnalysisEngineArray();
			handleColocatedDelegates(colocatedDelegates, component.getChildren());
		} else {
			
		}
	}
*/
	public static void main(String[] args) {
		try {
			DeploymentDescriptorProcessor ddp = 
					new DeploymentDescriptorProcessor();
			ddp.newComponent(args[0]);
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	private static class TypeConverter {
		private static int convertStringToInt(String value, int defaultValue) {
			int returnValue = defaultValue;
			try {
				returnValue = Integer.parseInt(value);
			} catch( Exception e) {
			}
			return returnValue;
		}
		private static boolean convertStringToBoolean(String value, boolean defaultValue) {
			boolean returnValue = defaultValue;
			try {
				returnValue = Boolean.parseBoolean(value);
			} catch( Exception e) {
			}
			return returnValue;
		}
		private static long convertStringToLong(String value, long defaultValue) {
			long returnValue = defaultValue;
			try {
				returnValue = Long.parseLong(value);
			} catch( Exception e) {
			}
			return returnValue;
		}
	}
}
