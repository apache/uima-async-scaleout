package org.apache.uima.as.connectors.mockup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.apache.uima.UimaContext;
import org.apache.uima.aae.AsynchAECasManager;
import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.OutputChannel;
import org.apache.uima.aae.UimaEEAdminContext;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineInstancePool;
import org.apache.uima.aae.controller.ControllerCallbackListener;
import org.apache.uima.aae.controller.ControllerLatch;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.EventSubscriber;
import org.apache.uima.aae.controller.LocalCache;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ServiceState;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.ErrorContext;
import org.apache.uima.aae.error.ErrorHandlerChain;
import org.apache.uima.aae.jmx.JmxManagement;
import org.apache.uima.aae.jmx.PrimitiveServiceInfo;
import org.apache.uima.aae.jmx.ServiceErrors;
import org.apache.uima.aae.jmx.ServiceInfo;
import org.apache.uima.aae.jmx.ServicePerformance;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.aae.message.UimaAsOrigin;
import org.apache.uima.aae.monitor.Monitor;
import org.apache.uima.aae.spi.transport.UimaMessageListener;
import org.apache.uima.as.client.DirectInputChannel;
import org.apache.uima.as.client.Listener;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class MockUpAnalysisEngineController implements PrimitiveAnalysisEngineController {

	private String name="N/A";
	private ThreadLocal<Long> threadLocalValue = new ThreadLocal<>();
	private volatile ControllerLatch latch = new ControllerLatch(this);
	private CyclicBarrier barrier;
	private Map<Origin, UimaAsEndpoint> endpoints = 
			new HashMap<>();
	private final Origin serviceOrigin;
	
	public MockUpAnalysisEngineController(String name, int scaleout) {
		this.name = name;
		serviceOrigin = new UimaAsOrigin(name);
		barrier = new CyclicBarrier(scaleout);
	}
	public Origin getOrigin() {
		return serviceOrigin;
	}
	public void addEndpoint(Origin origin, UimaAsEndpoint endpoint) {
		endpoints.put(origin, endpoint);
	}
	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addControllerCallbackListener(ControllerCallbackListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeControllerCallbackListener(ControllerCallbackListener aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMetadata(Endpoint anEndpoint) throws AsynchAEException {
		UimaAsEndpoint endpoint = 
				endpoints.get(anEndpoint.getMessageOrigin());//anEndpoint.getDelegateKey());
		MessageContext message = endpoint.createMessage(AsynchAEMessage.GetMeta,AsynchAEMessage.Response,anEndpoint);
		try {
			endpoint.dispatch(message);
		} catch( Exception e) {
			throw new AsynchAEException(e);
		}

	}

	@Override
	public ControllerLatch getControllerLatch() {
		return latch;
	}

	@Override
	public void setInputChannel(InputChannel anInputChannel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDirectInputChannel(DirectInputChannel anInputChannel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJmsInputChannel(InputChannel anInputChannel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputChannel getInputChannel(ENDPOINT_TYPE et) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputChannel getInputChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInputChannel(InputChannel anInputChannel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServiceEndpointName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceId(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServiceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleDelegateLifeCycleEvent(String anEndpoint, int aDelegateCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeAction(String anAction, String anEndpointName, ErrorContext anErrorContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setThreadFactory(ThreadPoolTaskExecutor factory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Listener> getAllListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveReplyTime(long snapshot, String aKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getReplyTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map getStats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UimaContext getChildUimaContext(String aDelegateEndpointName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dropCAS(String aCasReferenceId, boolean dropCacheEntry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropCAS(CAS aCAS) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InProcessCache getInProcessCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPrimitive() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Monitor getMonitor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComponentName() {
		return name;
	}

	@Override
	public void collectionProcessComplete(Endpoint anEndpoint) throws AsynchAEException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTopLevelComponent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initialize() throws AsynchAEException {
		System.out.println(".....Thread["+Thread.currentThread().getId()+"] "+ getComponentName()+" - Initializing AE");
	}

	@Override
	public void process(CAS aCas, String aCasId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(CAS aCAS, String anInputCasReferenceId, String aNewCasReferenceId,
			String newCASProducedBy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(CAS aCAS, String aCasReferenceId, Endpoint anEndpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveTime(long anArrivalTime, String aCasReferenceId, String anEndpointName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTime(String aCasReferenceId, String anEndpointName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandlerChain getErrorHandlerChain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addOutputChannel(OutputChannel anOutputChannel) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OutputChannel getOutputChannel(Endpoint anEndpoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputChannel getOutputChannel(ENDPOINT_TYPE et) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCasManager(AsynchAECasManager aCasManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AsynchAECasManager getCasManagerWrapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropStats(String aCasReferenceId, String anEndpointName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUimaEEAdminContext(UimaEEAdminContext anAdminContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UimaEEAdminContext getUimaEEAdminContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJMXDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getJmxContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServicePerformance getServicePerformance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrimitiveServiceInfo getServiceInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addServiceInfo(ServiceInfo aServiceInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceErrors getServiceErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeployDescriptor(String aDeployDescriptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cacheClientEndpoint(Endpoint anEndpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Endpoint getClientEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventSubscriber getEventListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JmxManagement getManagementInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyListenersWithInitializationStatus(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServicePerformance getCasStatistics(String aCasReferenceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCasMultiplier() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void releaseNextCas(String aCasReferenceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getIdleTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void beginProcess(int msgType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endProcess(int msgType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getIdleTimeBetweenProcessCalls(int msgType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCpuTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAnalysisTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void incrementSerializationTime(long cpuTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrementDeserializationTime(long cpuTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UimaMessageListener getUimaMessageListener(String aDelegateKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalCache getLocalCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerVmQueueWithJMX(Object o, String aName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AnalysisEngineController getParentController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAbortedCasReferenceId(String aCasReferenceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAwaitingCacheCallbackNotification() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void quiesceAndStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forceTimeoutOnPendingCases(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeState(ServiceState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceState getState() {
		// TODO Auto-generated method stub
		return ServiceState.INITIALIZING;
	}

	@Override
	public Map<String, String> getDeadClientMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dumpState(StringBuffer buffer, String lbl1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void warmUp(String warmUpDataPath, CountDownLatch warmUpLatch) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UimaContext getUimaContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUimaObject(String objectName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setErrorHandlerChain(ErrorHandlerChain ehc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResourceSpecifier getResourceSpecifier() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setAnalysisEngineInstancePool(AnalysisEngineInstancePool aPool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAEInstanceCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void initializeAnalysisEngine() throws ResourceInitializationException {
		threadLocalValue.set(Thread.currentThread().getId());
		try {
			barrier.await();
		} catch( InterruptedException | BrokenBarrierException e) {
			
		}
		getControllerLatch().release();
		
	}
	@Override
	public boolean threadAssignedToAE() {
		return Objects.nonNull(threadLocalValue.get());
	}
	
}