package org.apache.uima.aae.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.UimaASUtils;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController_impl;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.definition.connectors.Endpoints;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;
import org.apache.uima.aae.definition.connectors.basic.BasicConnector;
import org.apache.uima.aae.service.command.UimaAsMessageProcessor;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.FlowConstraints;
import org.apache.uima.resource.ResourceSpecifier;

public class AggregateAnalysisEngineComponent extends AnalysisEngineComponent {
	   private  enum FlowControllerType {
			FIXED
		}
	public AggregateAnalysisEngineComponent(String key, ResourceSpecifier rs) {
		super(key, rs);
	}
	
//	@Override
//	public AnalysisEngineController newAnalysisEngineController(AnalysisEngineController parentController,
//			String delegateKey, String resourceSpecifier, AsynchAECasManager_impl casManager, InProcessCache cache, int i, int scaleout ) throws Exception {
//		throw new UnsupportedOperationException("This method should only be called on PrimitiveAnalysisEngineComponent");
//
//	}
	@Override
	public AnalysisEngineController newAnalysisEngineController(AnalysisEngineController parentController,  AsynchAECasManager_impl casManager, InProcessCache cache ) throws Exception {

//	public AnalysisEngineController newAnalysisEngineController(AnalysisEngineController parentController, InProcessCache cache, AsynchAECasManager_impl casManager ) throws Exception {
		AnalysisEngineController controller;
		// add an endpoint for each delegate in this aggregate. The endpoint Map is
		// required
		// during initialization of an aggregate controller.
		Map<String, Endpoint> endpoints = new HashMap<>();

		for (AnalysisEngineComponent delegateComponent : getChildren()) {
			endpoints.put(delegateComponent.getKey(), delegateComponent.getEndpoint());
		}
		controller = new AggregateAnalysisEngineController_impl(parentController, getKey(),
				getResourceSpecifier().getSourceUrlString(), casManager, cache, endpoints);
		addFlowController((AggregateAnalysisEngineController) controller,
				(AnalysisEngineDescription) getResourceSpecifier());

		String aggregateId = (Objects.isNull(parentController) ? controller.getComponentName() : getKey());

		UimaAsEndpoint directEndpoint = Endpoints.newEndpoint(EndpointType.Direct, aggregateId,
				new UimaAsMessageProcessor(controller));
		controller.addEndpoint(directEndpoint);


		return controller;
	}
	protected void addFlowController(AggregateAnalysisEngineController aggregateController, AnalysisEngineDescription rs) throws Exception {
		String fcDescriptor=null;
		System.out.println(rs.getSourceUrlString());
		
		// first check if the AE aggregate descriptor defines a custom flow controller  
		if ( rs.getFlowControllerDeclaration() != null ) {
			if( rs.getFlowControllerDeclaration().getImport() == null ) {
				System.out.println("........................ What!!!!");
			}
		
			// the fc is either imported by name or a location
			fcDescriptor = rs.getFlowControllerDeclaration().getImport().getName();
		    if ( fcDescriptor == null ) {
		    	fcDescriptor = rs.getFlowControllerDeclaration().getImport().getLocation();
		    	
		    	fcDescriptor = UimaASUtils.fixPath(rs.getSourceUrlString(), fcDescriptor);
		    } else {
		    	throw new RuntimeException("*** Internal error - Invalid flowController specification - descriptor:"+rs.getFlowControllerDeclaration().getSourceUrlString());
		    }
		} else {
			FlowConstraints fc = rs.getAnalysisEngineMetaData().getFlowConstraints();
			if (FlowControllerType.FIXED.name().equals(fc.getFlowConstraintsType()) ) {
				fcDescriptor = ("*importByName:org.apache.uima.flow.FixedFlowController");
			}
		}
		((AggregateAnalysisEngineController_impl)aggregateController).setFlowControllerDescriptor(fcDescriptor);

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
