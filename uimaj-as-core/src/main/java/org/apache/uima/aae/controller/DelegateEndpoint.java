package org.apache.uima.aae.controller;

import org.apache.uima.cas.SerialFormat;
import org.apache.uima.resource.ResourceSpecifier;

public class DelegateEndpoint {
	public class Builder {
		private Endpoint_impl e1 = new Endpoint_impl();
		private String key;
		private String uri;
		private String endpointName;
		private Object replyTo;
		private String replyDestinatioName;
		private int getMetaTimeout=0;
		private int processTimeout=0;
		private String getMetaActionOnError;
		private String processActionOnError;
		private int getMetaMaxRetries=0;
		private int processsMaxRetries=0;
		private boolean isTempReplyDestination;
		private int collectionProcessCompleteTimeout=0;
		private String serializer=SerialFormat.XMI.getDefaultFileExtension();
		private int scaleout=1;
		private String descriptor;
		private ResourceSpecifier rs;
		private boolean isRemote;
		
		public Builder setRemote(boolean isRemote) {
			this.isRemote = isRemote;
			if ( !isRemote ) {
				setServerURI("java");
			}
			return this;
		}
		public Builder withResourceSpecifier(ResourceSpecifier rs) {
			this.rs = rs;
			setDescriptor(rs.getSourceUrlString());
			return this;
		}
		public Builder setTempDestination(boolean isTempReplyDestination) {
			this.isTempReplyDestination = isTempReplyDestination;
			return this;
		}
		public Builder setDescriptor(String descriptor) {
			this.descriptor = descriptor;
			return this;
		}

		public Builder setScaleout(int scaleout) {
			this.scaleout = scaleout;
			return this;
		}

		public Builder withDelegateKey(String key) {
			this.key = key;
			return this;
		}

		public Builder setServerURI(String uri) {
			this.uri = uri;
			return this;
		}

		public Builder withEndpointName(String endpointName) {
			this.endpointName = endpointName;
			return this;
		}

		public Builder setReplyToDestination(Object replyTo) {
			this.replyTo = replyTo;
			return this;
		}

		public Builder setReplyDestinationName(String replyDestinatioName) {
			this.replyDestinatioName = replyDestinatioName;
			return this;
		}

		public Builder setGetMetaErrorHandlingParams(int timeout, int maxRetries, String action) {
			this.getMetaTimeout = timeout;
			this.getMetaActionOnError = action;
			this.getMetaMaxRetries = maxRetries;
			return this;
		}

		public Builder setProcessErrorHandlingParams(int timeout, int maxRetries, String action) {
			this.processTimeout = timeout;
			this.processActionOnError = action;
			this.processsMaxRetries = maxRetries;
			return this;
		}

		public Builder setCollectionProcessCompleteTimeout(int timeout) {
			this.collectionProcessCompleteTimeout = timeout;
			return this;
		}

		public Builder setSerializer(String serializer) {
			this.serializer = serializer;
			return this;
		}

		public Endpoint_impl build() {
			e1.setDelegateKey(key);
			e1.setServerURI(uri);
			e1.setEndpoint(endpointName);
			e1.setDescriptor(descriptor);
			e1.setConcurrentRequestConsumers(scaleout);
			e1.setDestination(null); 
			e1.setReplyToEndpoint(replyDestinatioName);
			e1.setMetadataRequestTimeout(getMetaTimeout);
			e1.setProcessRequestTimeout(processTimeout);
			e1.setTempReplyDestination(isTempReplyDestination);
			e1.setCollectionProcessCompleteTimeout(0);
			e1.setSerializer(SerialFormat.XMI.getDefaultFileExtension());
			e1.setRemote(isRemote);
//			if ( uri != null && uri.length() > 0 && (uri.contains("tcp:") || uri.contains("http:") )) {
//				e1.setRemote(true);
//			} else {
//				e1.setRemote(false);
//			}
			e1.setResourceSpecifier(rs);
			return e1;
		}

	}
}
