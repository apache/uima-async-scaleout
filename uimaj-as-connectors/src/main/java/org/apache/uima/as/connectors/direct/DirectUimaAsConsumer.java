package org.apache.uima.as.connectors.direct;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.definition.connectors.AbstractUimaAsConsumer;
import org.apache.uima.aae.definition.connectors.Initializer;
import org.apache.uima.aae.definition.connectors.ListenerCallback;
import org.apache.uima.aae.definition.connectors.UimaAsConsumer;
import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageProcessor;
import org.apache.uima.aae.message.Target;
import org.apache.uima.aae.message.UimaAsTarget;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.DirectMessageContext;

public class DirectUimaAsConsumer extends AbstractUimaAsConsumer {
	//private static final String DIRECT = "direct:";
	private BlockingQueue<DirectMessage> inQueue= new LinkedBlockingQueue<>();;
	private MessageProcessor processor;
	private boolean started = false;
	private ExecutorService executor;
	private final ConsumerType consumerType;
	private int consumerThreadCount = 1;
	private boolean doStop = false;
//	private final CountDownLatch latchToCountNumberOfInitedThreads;
//	private final CountDownLatch latchToCountNumberOfTerminatedThreads;
	private AnalysisEngineController controller;
	private final String name;
	private Initializer initializer;
	private DirectListenerCallback callback = new DirectListenerCallback(this);
	private Target target;
	private UimaAsConsumer delegate;
	
    public DirectUimaAsConsumer(String name, BlockingQueue<DirectMessage> inQueue, ConsumerType type, int consumerThreadCount) {
		this(name, type,consumerThreadCount);
		this.inQueue = inQueue;
	}

//	public DirectUimaAsConsumer(String name, ConsumerType type, int consumerThreadCount) {
//		this(name, type, consumerThreadCount);
//		
//	}

	public DirectUimaAsConsumer( String name, ConsumerType type, int consumerThreadCount) {
		if ( name.indexOf(EndpointType.Direct.getName()) > -1 ) {
			this.name = name;
		} else {
			this.name = EndpointType.Direct.getName()+name;
		}
		
		this.consumerType = type;
		this.consumerThreadCount = consumerThreadCount;
//		latchToCountNumberOfInitedThreads = new CountDownLatch(consumerThreadCount);
//		latchToCountNumberOfTerminatedThreads = new CountDownLatch(consumerThreadCount);
		target = new UimaAsTarget(name, EndpointType.Direct);
	}
	public void setInitializer(Initializer initializer) {
		this.initializer = initializer;
	}
	public Target getTarget() {
		return target;
	}
	public int getConsumerCount() {
		return consumerThreadCount;
	}
	public ConsumerType getType() {
		return consumerType;
	}
	public void delegateTo(UimaAsConsumer delegateConsumer) {
		delegate = delegateConsumer;
	}
	protected void setMessageProcessor(MessageProcessor processor) {
		this.processor = processor;
	}
	
	public void initialize() throws Exception {
		initializer = new DefaultInitializer(consumerThreadCount);
		executor = initializer.initialize(callback);
	}

	/**
	 * This method is called on a producer thread
	 * 
	 */
	@Override
	public void consume(DirectMessage message) throws Exception {
		// if this consumer has a delegate it does not handle
		// messages itself. Instead messages are passed to the
		// delegate consumer. An example of this is CPC Consumer
		// which must delegate CPC requests to ProcessCASConsumer
		// since CPC requires access to AE instance which is 
		// only associated with Process Cas Consumers.
		if ( Objects.nonNull(delegate)) {
			delegate.consume(message);
		} else {
			inQueue.add(message);
		}
	}

	public void initialize(AnalysisEngineController controller) throws Exception {
		this.controller = controller;
		// Consumer handling ProcessCAS must first initialize each
		// UIMA pipeline.
		executor = initializer.initialize(callback);
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
		if ( Objects.isNull(executor)) {
			try {
				initialize();
			} catch( Exception e) {
				e.printStackTrace();
				return;
			}
			
		}
		System.out.println(">>> "+name+" DirectConsumer.start() - Consumer Type:"+getType());
		new Thread() {
			@Override
			public void run() {
				started = true;

				
				while( !doStop ) {
					try {
						
						final DirectMessage message = inQueue.take(); //blocks if empty
						//System.out.println(">>> "+name+" DirectConsumer.run() - Consumer Type:"+getType()+" Got new message");
						System.out.println(getType()+" Consumer Received Message From:"+
						        message.getOrigin());

			            if ( stopConsumingMessages(message)) {  // special type of msg indicating end of processing
						    System.out.println(">>> "+name+" Got END message - Stopping Queue Consumer");
			            	doStop = true;
						} else {
							executor.submit(new Runnable() {
						        public void run() {
						        	
						            try {
						    			// every message is wrapped in the MessageContext
						    			MessageContext messageContext = 
						    					new DirectMessageContext(message, "", name);
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

	private class DefaultInitializer implements Initializer {
		private final int consumerThreadCount;
		
		public DefaultInitializer(int consumerThreadCount) {
			this.consumerThreadCount = consumerThreadCount;
		}
		
		@Override
		public ExecutorService initialize(ListenerCallback callback) throws Exception {
			return Executors.newFixedThreadPool(consumerThreadCount);
		}
		
	}
}
