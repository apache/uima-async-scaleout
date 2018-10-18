package org.apache.uima.as.connectors.direct;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.uima.aae.UimaAsThreadFactory;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.definition.connectors.AbstractUimaAsConsumer;
import org.apache.uima.aae.definition.connectors.ListenerCallback;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.spi.transport.vm.UimaVmQueue;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectMessageContext;

public class DirectUimaAsConsumer extends AbstractUimaAsConsumer {

	private BlockingQueue<DirectMessage> inQueue= new LinkedBlockingQueue<>();;
	private MessageProcessor processor;
	private boolean started = false;
	private ExecutorService executor;
	private final ConsumerType consumerType;
	private int consumerThreadCount = 1;
	private boolean doStop = false;
	private final CountDownLatch latchToCountNumberOfInitedThreads;
	private final CountDownLatch latchToCountNumberOfTerminatedThreads;
	private AnalysisEngineController controller;
	private final String name;
    public DirectUimaAsConsumer(String name, BlockingQueue<DirectMessage> inQueue, ConsumerType type, int consumerThreadCount) {
		this(name, type,consumerThreadCount);
		this.inQueue = inQueue;
	}

	public DirectUimaAsConsumer(String name,String targetUri, ConsumerType type, int consumerThreadCount) {
		this(name, type,consumerThreadCount);
		
	}

	private DirectUimaAsConsumer( String name, ConsumerType type, int consumerThreadCount) {
		this.name = name;
		this.consumerType = type;
		this.consumerThreadCount = consumerThreadCount;
		latchToCountNumberOfInitedThreads = new CountDownLatch(consumerThreadCount);
		latchToCountNumberOfTerminatedThreads = new CountDownLatch(consumerThreadCount);
	}
	
	public ConsumerType getType() {
		return consumerType;
	}
	
	protected void setMessageProcessor(MessageProcessor processor) {
		this.processor = processor;
	}
	
	public void initialize() throws Exception {
		
	}

	/**
	 * This method is called on a producer thread
	 * 
	 */
	@Override
	public void consume(DirectMessage message) throws Exception {
		inQueue.add(message);
	}
	
	private void initializeUimaPipeline() throws Exception {
//		workQueue = new UimaVmQueue();
		if ( controller.isPrimitive() ) {
			ThreadGroup threadGroup = new ThreadGroup("VmThreadGroup" + 1 + "_" + controller.getComponentName());
			executor = new ThreadPoolExecutor(consumerThreadCount, consumerThreadCount, Long.MAX_VALUE, TimeUnit.DAYS, new UimaVmQueue());
			UimaAsThreadFactory tf = null;
			
			DirectListenerCallback callback = new DirectListenerCallback(this);
			
			tf = new UimaAsThreadFactory().
					withCallback(callback).
					withThreadGroup(threadGroup).
					withPrimitiveController((PrimitiveAnalysisEngineController)processor.getController()).
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
		} else {
			 executor = Executors.newFixedThreadPool(consumerThreadCount);
		}
	}
	public void initialize(AnalysisEngineController controller) throws Exception {
		this.controller = controller;
		// Consumer handling ProcessCAS must first initialize each
		// UIMA pipeline.
		if (ConsumerType.ProcessCAS.equals(consumerType)) {
			if ( Objects.isNull(controller)) {
				 executor = Executors.newFixedThreadPool(consumerThreadCount);
			} else {
				initializeUimaPipeline();
			}

		} else {
			 executor = Executors.newFixedThreadPool(consumerThreadCount);
		}
	}
	
	private boolean stopConsumingMessages(DirectMessage message ) throws Exception{
		return (message.getMessageIntProperty(AsynchAEMessage.Command) == AsynchAEMessage.PoisonPill);
		
	}
	public void stop() {
		doStop = true;
	}
	public synchronized void start() {
		if ( started ) {
			return;
		}
		System.out.println(">>> "+name+" DirectConsumer.start() - Consumer Type:"+getType());
		new Thread() {
			@Override
			public void run() {
				started = true;

				
				while( !doStop ) {
					try {
						
						final DirectMessage message = inQueue.take(); //blocks if empty
						System.out.println(">>> "+name+" DirectConsumer.run() - Consumer Type:"+getType()+" Got new message");
						
			            if ( stopConsumingMessages(message)) {  // special type of msg indicating end of processing
						    System.out.println(">>> "+name+" Got END message - Stopping Queue Consumer");
			            	doStop = true;
						} else {
							executor.submit(new Runnable() {
						        public void run() {
						        	
						            try {
						            	//System.out.println(">>> "+controller.getComponentName()+" Got new message - processing on thread "+Thread.currentThread().getName()+" channel:"+getType());
										//ic.onMessage(message);
						            	
						    			// every message is wrapped in the MessageContext
						    			MessageContext messageContext = 
						    					new DirectMessageContext(message, "", controller.getComponentName());

										processor.process(messageContext);
						            } catch( Exception e) {
						            	e.printStackTrace();
						            }
						          }
						        });
						}

					} catch( InterruptedException e) {
						Thread.currentThread().interrupt();
						
						//System.out.println(getType()+ " Listener Thread Interrupted - Stop Listening");
						doStop = true;
				    } catch (Exception e) {
						e.printStackTrace();
						doStop = true;
					} 
				}
			}
		}.start();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public class DirectListenerCallback implements ListenerCallback {
		private UimaAsConsumer dl;
		private boolean initializationFailed = false;
		private Exception exception;
		
		public DirectListenerCallback(UimaAsConsumer l) {
			this.dl = l;
		}
		
		public void onInitializationError(Exception e) {
			initializationFailed = true;
			exception = e;
		}
		public boolean failedInitialization() {
			return initializationFailed;
		}
		public Exception getException() {
			return exception;
		}
	}

}
