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
package org.apache.uima.as.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.uima.aae.UimaAsThreadFactory;
import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.spi.transport.vm.UimaVmQueue;
import org.springframework.core.task.TaskExecutor;

public class DirectListener implements Listener, JavaQueueListener {
	private BlockingQueue<DirectMessage> inQueue;
	private DirectInputChannel ic;
	private int consumerCount = 1;
	private ExecutorService executor;
	private AnalysisEngineController controller;
	private int scaleout;
	private Thread msgHandlerThread;  
	private BlockingQueue<Runnable> workQueue = null;
	private CountDownLatch latchToCountNumberOfInitedThreads;
	private CountDownLatch latchToCountNumberOfTerminatedThreads;
	private Type type;
	private Transport transport = Transport.Java;
	//private boolean hasTaskExecutor = false;
	private String name=null;
	private  volatile boolean started = false;
	public DirectListener(Type t) {
		this.type = t;
		System.out.println("DirectListener c'tor - type:"+type.name());
	}
	
	public DirectListener(AnalysisEngineController ctlr, Type t, int scaleout) {
		this.controller = ctlr;
		this.scaleout = scaleout;
		this.type = t;
		System.out.println("DirectListener c'tor - type:"+type.name());
	}
	public Transport getTransport() {
	  return transport;
	}
	public BlockingQueue<DirectMessage> getEndpoint() {
		return inQueue;
	}
	public Type getType() {
		return type;
	}
	public void setController(AnalysisEngineController ctlr) {
		this.controller = ctlr;
	}
	public DirectListener withController(AnalysisEngineController ctlr) {
		this.controller = ctlr;
		return this;
	}
	public void setConsumerThreads(int howMany) {
		this.scaleout = howMany;
		latchToCountNumberOfInitedThreads = new CountDownLatch(howMany);
		latchToCountNumberOfTerminatedThreads = new CountDownLatch(howMany);
	}
	public DirectListener withConsumerThreads(int howMany) {
		this.scaleout = howMany;
		latchToCountNumberOfInitedThreads = new CountDownLatch(howMany);
		latchToCountNumberOfTerminatedThreads = new CountDownLatch(howMany);
		
		return this;
	}
	public DirectListener withName(String name) {
		this.name = name;
		return this;
	}
	public void setQueue(BlockingQueue<DirectMessage> inQueue) throws Exception {
		this.inQueue = inQueue;
	}
	public DirectListener withQueue(BlockingQueue<DirectMessage> inQueue) throws Exception {
		this.inQueue = inQueue;
		return this;
	}
	public void setConsumers(int howMany) {
		consumerCount = howMany;
	}

	public DirectListener withInputChannel(DirectInputChannel ic) {
		this.ic = ic;
		return this;
	}
	public void setInputChannel(DirectInputChannel ic) {
		this.ic = ic;
	}

	public DirectListener initialize() throws Exception {
		if (Type.ProcessCAS.equals(type)) {
			workQueue = new UimaVmQueue();
			if ( controller instanceof PrimitiveAnalysisEngineController ) {
				ThreadGroup threadGroup = new ThreadGroup("VmThreadGroup" + 1 + "_" + controller.getComponentName());
				executor = new ThreadPoolExecutor(scaleout, scaleout, Long.MAX_VALUE, TimeUnit.DAYS, workQueue);
				UimaAsThreadFactory tf = null;
				
				DirectListenerCallback callback = new DirectListenerCallback(this);
				
				tf = new UimaAsThreadFactory().
						withCallback(callback).
						withThreadGroup(threadGroup).
						withPrimitiveController((PrimitiveAnalysisEngineController)controller).
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
				 executor = Executors.newFixedThreadPool(consumerCount);
			}
		} else {
			 executor = Executors.newFixedThreadPool(consumerCount);
		}
		return this;
	}

	private boolean stopConsumingMessages(DirectMessage message ) {
		return message.getAsInt(AsynchAEMessage.Command) == AsynchAEMessage.PoisonPill;
		
	}
	public synchronized void start() {
		if ( started ) {
			return;
		}
		System.out.println(">>> "+controller.getComponentName()+" DirectListener.start()");
		msgHandlerThread = new Thread() {
			public void run() {
				started = true;
				boolean stop = false;
				
				while( !controller.isStopped() && !stop) {
					try {
						
						final DirectMessage message = inQueue.take(); //blocks if empty
			            if ( stopConsumingMessages(message)) {
						    System.out.println(">>> "+controller.getComponentName()+" Got END message - Stopping Queue Consumer");
							stop = true;
						} else {
							executor.submit(new Runnable() {
						        public void run() {
						        	
						            try {
						            	System.out.println(">>> "+controller.getComponentName()+" Got new message - processing on thread "+Thread.currentThread().getName()+" channel:"+getType());
										ic.onMessage(message);
						            } catch( Exception e) {
						            	e.printStackTrace();
						            }
						          }
						        });
						}

					} catch( InterruptedException e) {
						Thread.currentThread().interrupt();
						
						System.out.println(getType()+ " Listener Thread Interrupted - Stop Listening");
						stop = true;
				    } catch (Exception e) {
						e.printStackTrace();
						stop = true;
					} 
				}
			}
		};
		msgHandlerThread.start();
	}
	public void stop() {
		msgHandlerThread.interrupt();
		
		DirectMessage message = new DirectMessage().withCommand(AsynchAEMessage.PoisonPill);
		try {
			inQueue.put(message);
		} catch( InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		if ( executor != null ) {
			executor.shutdown();
			
		}
	}

	public TaskExecutor getTaskExecutor() {
		return null;
	}

	public String getName() {
		if ( name != null ) {
			return name;
		}
		return controller.getKey();
	}
	public class DirectListenerCallback {
		private DirectListener dl;
		private boolean initializationFailed = false;
		private Exception exception;
		
		public DirectListenerCallback(DirectListener l) {
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
