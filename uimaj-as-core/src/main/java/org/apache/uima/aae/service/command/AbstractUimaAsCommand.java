/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.uima.aae.service.command;

import java.io.ByteArrayInputStream;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InProcessCache.CacheEntry;
import org.apache.uima.aae.SerializerCache;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.UimaSerializer;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.LocalCache.CasStateEntry;
import org.apache.uima.aae.delegate.Delegate;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.ErrorContext;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.UIMAMessage;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.aae.monitor.statistics.DelegateStats;
import org.apache.uima.aae.monitor.statistics.LongNumericStatistic;
import org.apache.uima.aae.monitor.statistics.TimerStats;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Marker;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.cas.impl.BinaryCasSerDes6.ReuseInfo;
import org.apache.uima.cas.impl.Serialization;
import org.apache.uima.cas.impl.XmiSerializationSharedData;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.Level;

public abstract class AbstractUimaAsCommand implements UimaAsCommand {
	protected AnalysisEngineController controller;
	private Object mux = new Object();
	private final MessageContext messageContext;
	
	protected AbstractUimaAsCommand(AnalysisEngineController controller, MessageContext aMessageContext) {
		this.controller = controller;
		this.messageContext = aMessageContext;
	}

	protected String getCasReferenceId(Class<?> concreteClassName/*, MessageContext aMessageContext */) throws AsynchAEException {
		if (!messageContext.propertyExists(AsynchAEMessage.CasReference)) {
			if (UIMAFramework.getLogger(concreteClassName).isLoggable(Level.INFO)) {
				UIMAFramework.getLogger(concreteClassName).logrb(Level.INFO, concreteClassName.getName(),
						"getCasReferenceId", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
						"UIMAEE_message_has_cas_refid__INFO",
						new Object[] { messageContext.getEndpoint().getEndpoint() });
			}
			return null;
		}
		return messageContext.getMessageStringProperty(AsynchAEMessage.CasReference);
	}

	protected CacheEntry getCacheEntryForCas(String casReferenceId) {
		try {
			return controller.getInProcessCache().getCacheEntryForCAS(casReferenceId);
		} catch (AsynchAEException e) {
			return new InProcessCache.UndefinedCacheEntry();
		}
	}

	protected CasStateEntry createCasStateEntry(String casReferenceId) {
		return controller.getLocalCache().createCasStateEntry(casReferenceId);
	}

	protected CasStateEntry getCasStateEntry(String casReferenceId) {
		CasStateEntry casStateEntry = null;
		if ((casStateEntry = controller.getLocalCache().lookupEntry(casReferenceId)) == null) {
			// Create a new entry in the local cache for the CAS received from the remote
			casStateEntry = createCasStateEntry(casReferenceId);
		}
		return casStateEntry;
	}

	protected boolean isTopLevelAggregate() {
		return (controller.isTopLevelComponent() && controller instanceof AggregateAnalysisEngineController);
	}

	protected void handleError(Exception e, CacheEntry cacheEntry/*, MessageContext mc */) {
		if (UIMAFramework.getLogger(getClass()).isLoggable(Level.WARNING)) {
			UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "handleError",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_service_exception_WARNING",
					controller.getComponentName());

			UIMAFramework.getLogger(getClass()).logrb(Level.WARNING, getClass().getName(), "handleError",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
		}
		ErrorContext errorContext = new ErrorContext();
		errorContext.add(AsynchAEMessage.Endpoint, messageContext.getEndpoint());
		errorContext.add(AsynchAEMessage.Command, AsynchAEMessage.Process);
		errorContext.add(AsynchAEMessage.CasReference, cacheEntry.getCasReferenceId());
		controller.dropCAS(cacheEntry.getCas());
		controller.getErrorHandlerChain().handle(e, errorContext, controller);

	}

	protected void saveStats(CacheEntry entry, long inTime, long t1, long timeWaitingForCAS) {
		long timeToDeserializeCAS = controller.getCpuTime() - t1;
		controller.incrementDeserializationTime(timeToDeserializeCAS);
		entry.incrementTimeToDeserializeCAS(timeToDeserializeCAS);
		entry.incrementTimeWaitingForCAS(timeWaitingForCAS);

		LongNumericStatistic statistic;
		if ((statistic = controller.getMonitor().getLongNumericStatistic("", Monitor.TotalDeserializeTime)) != null) {
			statistic.increment(timeToDeserializeCAS);
		}
		if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
			UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "saveStats",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_deserialize_cas_time_FINE",
					new Object[] { (double) timeToDeserializeCAS / 1000000.0 });
		}

		// Update Stats
		ServicePerformance casStats = controller.getCasStatistics(entry.getCasReferenceId());
		casStats.incrementCasDeserializationTime(timeToDeserializeCAS);
		if (controller.isTopLevelComponent()) {
			synchronized (mux) {
				controller.getServicePerformance().incrementCasDeserializationTime(timeToDeserializeCAS);
			}
		}
		controller.saveTime(inTime, entry.getCasReferenceId(), controller.getName());
		if (!controller.isPrimitive()) {
			DelegateStats stats = new DelegateStats();
			if (entry.getStat() == null) {
				entry.setStat(stats);
				// Add entry for self (this aggregate). MessageContext.getEndpointName()
				// returns the name of the queue receiving the message.
				stats.put(controller.getServiceEndpointName(), new TimerStats());
			} else {
				if (!stats.containsKey(controller.getServiceEndpointName())) {
					stats.put(controller.getServiceEndpointName(), new DelegateStats());
				}
			}
		}

	}

	protected ErrorContext populateErrorContext(/*MessageContext aMessageCtx */) {
		ErrorContext errorContext = new ErrorContext();
		if (messageContext != null) {
			try {
				if (messageContext.propertyExists(AsynchAEMessage.Command)) {
					errorContext.add(AsynchAEMessage.Command,
							messageContext.getMessageIntProperty(AsynchAEMessage.Command));
				}

				if (messageContext.propertyExists(AsynchAEMessage.MessageType)) {
					errorContext.add(AsynchAEMessage.MessageType,
							messageContext.getMessageIntProperty(AsynchAEMessage.MessageType));
				}

				if (messageContext.propertyExists(AsynchAEMessage.CasReference)) {
					errorContext.add(AsynchAEMessage.CasReference,
							messageContext.getMessageStringProperty(AsynchAEMessage.CasReference));
				}
				errorContext.add(UIMAMessage.RawMsg, messageContext.getRawMessage());
			} catch (Exception e) { /* ignore */
			}
		}
		return errorContext;
	}
	protected Endpoint getEndpoint() {
		return messageContext.getEndpoint();
	}
	protected int getMessageIntProperty(String propertyName) throws Exception {
		return messageContext.getMessageIntProperty(propertyName);
	}
	protected String getMessageStringProperty(String propertyName) throws Exception {
		return messageContext.getMessageStringProperty(propertyName);
	}
	protected ResourceMetaData getResourceMetaData() throws Exception {
		return (ResourceMetaData)messageContext.getMessageObjectProperty(AsynchAEMessage.AEMetadata);
	}
	protected String getStringMessage() throws Exception {
		return messageContext.getStringMessage();
	}
	protected Object getMessageObjectProperty(String propertyName) throws Exception {
		return messageContext.getMessageObjectProperty(propertyName);
	}
	protected boolean getMessageBooleanProperty(String propertyName) throws Exception {
		return messageContext.getMessageBooleanProperty(propertyName);
	}
	protected long getMessageLongProperty( String propertyName) throws Exception {
		return messageContext.getMessageLongProperty(propertyName);
	}
	protected boolean propertyExists(String propertyName) throws Exception {
		return messageContext.propertyExists(propertyName);
	}
	protected String getEndpointName() {
		return messageContext.getEndpointName();
	}
	protected Object getObjectMessage() throws Exception {
		return messageContext.getObjectMessage();
	}
	protected byte[] getByteMessage() throws Exception {
		return messageContext.getByteMessage();
	}
	protected MessageContext getMessageContext() {
		return messageContext;
	}
	protected Endpoint fetchParentCasOrigin(String parentCasId) throws AsynchAEException {
		Endpoint endpoint = null;
		String parentId = parentCasId;
		// Loop through the parent tree until an origin is found
		while (parentId != null) {
			// Check if the current parent has an associated origin. Only input CAS
			// has an origin of the request. The origin is an endpoint of a client
			// who sent an input CAS for processing
			endpoint = ((AggregateAnalysisEngineController) controller).getMessageOrigin(parentId);
			// Check if there is an origin. If so, we are done
			if (endpoint != null) {
				break;
			}
			// The current parent has no origin, get its parent and try again
			CacheEntry entry = controller.getInProcessCache().getCacheEntryForCAS(parentId);
			parentId = entry.getInputCasReferenceId();
		}
		return endpoint;
	}

	protected CAS getNewCAS(CASFactory factory, String casRequestorOrigin) { 
		CAS cas = null;
		// Aggregate time spent waiting for a CAS in the service cas pool
		controller.getServicePerformance().beginWaitOnCASPool();
		if (UIMAFramework.getLogger(this.getClass()).isLoggable(Level.FINE)) {
			UIMAFramework.getLogger(this.getClass()).logrb(Level.FINE, this.getClass().getName(), "deserializeChildCAS",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_request_cas_granted__FINE",
					new Object[] { casRequestorOrigin });
		}
		cas = factory.newCAS();

		if (UIMAFramework.getLogger(getClass()).isLoggable(Level.FINE)) {
			UIMAFramework.getLogger(getClass()).logrb(Level.FINE, getClass().getName(), "deserializeChildCAS",
					UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_request_cas_granted_cm__FINE",
					new Object[] { casRequestorOrigin });
		}

		controller.getServicePerformance().endWaitOnCASPool();

		ServicePerformance sp = controller.getServicePerformance();
		sp.incrementCasPoolWaitTime(sp.getTimeWaitingForCAS());

		return cas;
	}

	protected SerializationResult deserializeChildCAS(String casMultiplierDelegateKey, Endpoint endpoint
			/*MessageContext mc*/) throws Exception {
		SerializationResult result = new SerializationResult();

		// Aggregate time spent waiting for a CAS in the shadow cas pool
		((AggregateAnalysisEngineController) controller).getDelegateServicePerformance(casMultiplierDelegateKey)
				.beginWaitOnShadowCASPool();

		long t1 = controller.getCpuTime();
		CAS cas = getNewCAS(new ChildCASFactory(casMultiplierDelegateKey), casMultiplierDelegateKey);
		result.setCas(cas);

		result.setTimeWaitingForCAS(controller.getCpuTime() - t1);

		((AggregateAnalysisEngineController) controller).getDelegateServicePerformance(casMultiplierDelegateKey)
				.endWaitOnShadowCASPool();

		// Check if we are still running
		if (controller.isStopped()) {
			// The Controller is in shutdown state.
			controller.dropCAS(cas);
			return null;
		}
		// Create deserialized wrapper for XMI, BINARY, COMPRESSED formats. To add
		// a new serialization format add a new class which implements
		// UimaASDeserializer and modify DeserializerFactory class.
		UimaASDeserializer deserializer = DeserializerFactory.newDeserializer(endpoint, messageContext);
		deserializer.deserialize(result);

		return result;
	}

	protected SerializationResult deserializeInputCAS()
			throws Exception {
		SerializationResult result = new SerializationResult();
		String origin = messageContext.getEndpoint().getEndpoint();
		Endpoint endpoint = messageContext.getEndpoint();
		
		// Time how long we wait on Cas Pool to fetch a new CAS
		long t1 = controller.getCpuTime();
		CAS cas = getNewCAS(new InputCASFactory(), origin);
		result.setCas(cas);

		result.setTimeWaitingForCAS(controller.getCpuTime() - t1);

		// Check if we are still running
		if (controller.isStopped()) {
			// The Controller is in shutdown state.
			controller.dropCAS(cas);
			return null;
		}

		UimaASDeserializer deserializer = DeserializerFactory.newDeserializer(endpoint, messageContext);
		deserializer.deserialize(result);

		return result;
	}
	protected Delegate getDelegate(/* MessageContext mc */) throws AsynchAEException {
		String delegateKey = null;
		if (messageContext.getEndpoint().getEndpoint() == null || messageContext.getEndpoint().getEndpoint().trim().length() == 0) {
			String fromEndpoint = messageContext.getMessageStringProperty(AsynchAEMessage.MessageFrom);
			delegateKey = ((AggregateAnalysisEngineController) controller)
					.lookUpDelegateKey(fromEndpoint);
		} else {
			delegateKey = ((AggregateAnalysisEngineController) controller)
					.lookUpDelegateKey(messageContext.getEndpoint().getEndpoint());
		}
		return ((AggregateAnalysisEngineController) controller).lookupDelegate(delegateKey);
	}

	public static class DeserializerFactory {
		// only static reference allowed
		private DeserializerFactory() {
		}
		public static UimaASDeserializer newDeserializer(Endpoint endpoint, MessageContext mc) throws AsynchAEException {
			switch (endpoint.getSerialFormat()) {
			case XMI:
				return new XMIDeserializer(mc.getStringMessage());
			case BINARY:
				return new BinaryDeserializer(mc.getByteMessage(), endpoint);
			case COMPRESSED_FILTERED:
				return new CompressedFilteredDeserializer(mc.getByteMessage(), endpoint);
			default:
				throw new AsynchAEException("Never Happen");

			}
		}
	}

	public static interface UimaASDeserializer {
		public void deserialize(SerializationResult result) throws Exception;
	}

	public static class CompressedFilteredDeserializer implements UimaASDeserializer {
		byte[] binarySource;
		Endpoint endpoint;

		public CompressedFilteredDeserializer(byte[] binarySource, Endpoint endpoint) {
			this.binarySource = binarySource;
			this.endpoint = endpoint;
		}

		public void deserialize(SerializationResult result) throws Exception {
			ByteArrayInputStream bais = new ByteArrayInputStream(binarySource);
			ReuseInfo reuseInfo = Serialization
					.deserializeCAS(result.getCas(), bais, endpoint.getTypeSystemImpl(), null).getReuseInfo();
			result.setReuseInfo(reuseInfo);
		}
	}

	public static class BinaryDeserializer implements UimaASDeserializer {
		byte[] binarySource;
		Endpoint endpoint;

		public BinaryDeserializer(byte[] binarySource, Endpoint endpoint) {
			this.binarySource = binarySource;
			this.endpoint = endpoint;
		}

		public void deserialize(SerializationResult result) throws Exception {
			UimaSerializer uimaSerializer = SerializerCache.lookupSerializerByThreadId();
			// BINARY format may be COMPRESSED etc, so update it upon reading
			SerialFormat serialFormat = 
					uimaSerializer.deserializeCasFromBinary(binarySource, result.getCas());
			// BINARY format may be COMPRESSED etc, so update it upon reading
			endpoint.setSerialFormat(serialFormat);
		}
	}

	public static class XMIDeserializer implements UimaASDeserializer {
		String xmi;

		public XMIDeserializer(String xmi) {
			this.xmi = xmi;
		}

		public void deserialize(SerializationResult result) throws Exception {
			UimaSerializer uimaSerializer = SerializerCache.lookupSerializerByThreadId();
			XmiSerializationSharedData deserSharedData = new XmiSerializationSharedData();
			uimaSerializer.deserializeCasFromXmi(xmi, result.getCas(), deserSharedData, true, -1);
			result.setDeserSharedData(deserSharedData);
		}

	}

	public static class SerializationResult {
		Marker marker = null;
		CAS cas = null;
		XmiSerializationSharedData deserSharedData = null;
		ReuseInfo reuseInfo = null;
		boolean acceptsDeltaCas = false;
		long timeWaitingForCAS = 0;

		public long getTimeWaitingForCAS() {
			return timeWaitingForCAS;
		}

		public void setTimeWaitingForCAS(long timeWaitingForCAS) {
			this.timeWaitingForCAS = timeWaitingForCAS;
		}

		public boolean acceptsDeltaCas() {
			return acceptsDeltaCas;
		}

		public void setAcceptsDeltaCas(boolean acceptsDeltaCas) {
			this.acceptsDeltaCas = acceptsDeltaCas;
		}

		public Marker getMarker() {
			return marker;
		}

		public void setMarker(Marker marker) {
			this.marker = marker;
		}

		public CAS getCas() {
			return cas;
		}

		public void setCas(CAS cas) {
			this.cas = cas;
		}

		public XmiSerializationSharedData getDeserSharedData() {
			return deserSharedData;
		}

		public void setDeserSharedData(XmiSerializationSharedData deserSharedData) {
			this.deserSharedData = deserSharedData;
		}

		public ReuseInfo getReuseInfo() {
			return reuseInfo;
		}

		public void setReuseInfo(ReuseInfo reuseInfo) {
			this.reuseInfo = reuseInfo;
		}

	}

	public interface CASFactory {
		public CAS newCAS();

	}

	public class ChildCASFactory implements CASFactory {
		String casMultiplierDelegateKey;

		public ChildCASFactory(String casMultiplierDelegateKey) {
			this.casMultiplierDelegateKey = casMultiplierDelegateKey;
		}

		public CAS newCAS() {
			return controller.getCasManagerWrapper().getNewCas(casMultiplierDelegateKey);
		}

	}

	public class InputCASFactory implements CASFactory {
		public CAS newCAS() {
			return controller.getCasManagerWrapper().getNewCas();
		}

	}
}
