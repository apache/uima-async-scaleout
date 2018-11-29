package org.apache.uima.aae.controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.uima.aae.UimaAsThreadFactory;
import org.apache.uima.aae.definition.connectors.Initializer;
import org.apache.uima.aae.definition.connectors.ListenerCallback;
import org.apache.uima.aae.spi.transport.vm.UimaVmQueue;

public class PrimitiveAeInitializer implements Initializer {

	private final PrimitiveAnalysisEngineController controller;
	private final int scaleout;
	private final CountDownLatch latchToCountNumberOfInitedThreads;
	private final CountDownLatch latchToCountNumberOfTerminatedThreads;
	private ExecutorService executor;
	
	public PrimitiveAeInitializer(PrimitiveAnalysisEngineController controller, int scaleout) {
		this.controller = controller;
		this.scaleout = scaleout;
		latchToCountNumberOfInitedThreads = new CountDownLatch(scaleout);
		latchToCountNumberOfTerminatedThreads = new CountDownLatch(scaleout);

	}
	
	@Override
	public ExecutorService initialize(ListenerCallback callback) throws Exception {
		ThreadGroup threadGroup = new ThreadGroup("VmThreadGroup" + 1 + "_" + controller.getComponentName());
		executor = new ThreadPoolExecutor(scaleout, scaleout, Long.MAX_VALUE, TimeUnit.DAYS, new UimaVmQueue());
		UimaAsThreadFactory tf = null;
		
//		ListenerCallback callback = 
//				new DirectListenerCallback(this);
		
		tf = new UimaAsThreadFactory().
				withCallback(callback).
				withThreadGroup(threadGroup).
				withPrimitiveController(controller).
				withTerminatedThreadsLatch(latchToCountNumberOfTerminatedThreads).
				withInitedThreadsLatch(latchToCountNumberOfInitedThreads);
		tf.setDaemon(true);
		((ThreadPoolExecutor)executor).setThreadFactory(tf);
		((ThreadPoolExecutor)executor).prestartAllCoreThreads();
		latchToCountNumberOfInitedThreads.await();
		if ( callback.failedInitialization() ) {
			throw callback.getException();
		}
		System.out.println("Executor Started - All Process Threads Initialized");
		
		return executor;
	}

}
