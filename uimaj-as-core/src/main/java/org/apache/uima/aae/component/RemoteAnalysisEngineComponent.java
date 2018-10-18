package org.apache.uima.aae.component;

import java.util.Objects;

import org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType;
import org.apache.uima.resourceSpecifier.InputQueueType;
import org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType;
import org.apache.uima.resourceSpecifier.SerializerType;

public class RemoteAnalysisEngineComponent extends AnalysisEngineComponent {

	private Object connector;
	private AnalysisEngineComponent decoratedComponent;
	private SerializerType serializer;
	private int replyChannelScaleout;
	private InputQueueType remoteEndpoint;
	private AsyncAggregateErrorConfigurationType errorConfiguration;
	
	public RemoteAnalysisEngineComponent(AnalysisEngineComponent component, RemoteAnalysisEngineType remoteDelegate) {
		super(component.getKey(), null);
		decoratedComponent = component;
		errorConfiguration = 
				remoteDelegate.getAsyncAggregateErrorConfiguration();
		remoteEndpoint = 
				remoteDelegate.getInputQueue();
		replyChannelScaleout = remoteDelegate.getRemoteReplyQueueScaleout();
		serializer =
				remoteDelegate.getSerializer();
	}

	@Override
	public boolean isRemote() {
		return true;
	}
	public String getServer() {
		return remoteEndpoint.getBrokerURL();
	}

	public String getEndpointName() {
		return remoteEndpoint.getEndpoint();
	}
	public int getPrefetch() {
		return remoteEndpoint.getPrefetch();
	}
	
	@Override
	public Object getConnector() {
		return connector;
	}
	
	public int getProcessTimeout() {
		return errorConfiguration.getProcessCasErrors().getTimeout();
	}
	public int getMetaTimeout() {
		return errorConfiguration.getGetMetadataErrors().getTimeout();
	}
	public int getCollectionProcessCompleteTimeout() {
		return errorConfiguration.getCollectionProcessCompleteErrors().getTimeout();
	}
	public String getSupportedSerialization() {
		if ( Objects.isNull( serializer ) ) {
			 return "xmi";
		}
		return serializer.getStringValue().trim();
	}
	public int getReplyConsumerCount() {
		return replyChannelScaleout;
	}
	public RemoteAnalysisEngineComponent withConnector(Object connector) {
		this.connector = connector;
		return this;
	}
	
}
