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
package org.apache.uima.adapter.jms.activemq;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

//import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageWrapper;
import org.springframework.jms.listener.SessionAwareMessageListener;

/**
 * This class main job is to receive a JMS message from Spring Listeners
 * via onMessage() and wrapping it in a MessageWrapper before enqueuing that
 * onto a BlockingPriorityQueue for process threads. Only two listener
 * types send messages to this class: Targeted Listener and Process Listener.
 * For targeted listener this class creates a dedicated semaphore with 1
 * permit. For process listener this class creates a dedicated semaphore
 * with a number of permits equal to scaleout (# of process threads).
 * The listener thread calling onMessage() will try to acquire its
 * semaphore and will block if no permits are available. The targeted listener
 * is identified by value returned from message.getJMSType(). This is set
 * in UimaDefaultMessageListenerContainer.doInvokeListener() before this
 * class onMessage() is called.
 *
 */
@SuppressWarnings("rawtypes")
public class PriorityMessageHandler implements SessionAwareMessageListener {

	private PriorityBlockingQueue<MessageWrapper> queue =
			new PriorityBlockingQueue<MessageWrapper>();
	private Semaphore targetedListenerSemaphore = null;
	private Semaphore processListenerSemaphore = null;
	
	public PriorityMessageHandler(int scaleout) {
		processListenerSemaphore =
				new Semaphore(scaleout);
		targetedListenerSemaphore =
				new Semaphore(1);
	}

    public BlockingQueue<MessageWrapper> getQueue() {
    	return queue;
    }
    /**
     * Wraps an incoming message in a protocol neutral envelope and adds priority
     * value to enable sorting in a Priority Queue. Messages specifically targetting
     * an instance of a service will be assigned high priority for processing.
     */
	public void onMessage(Message message, Session session) throws JMSException {
		Semaphore semaphore = null;
	    // System.out.println("................ PriorityMessageHandler.onMessage() - Thread ID:"+Thread.currentThread().getId());
		// the JMSType is set by targeted listener in
		//  UimaDefaultMessageListenerContainer.doInvokeListener(). The process
		// listener does not set this property.
		if ( "TargetMessage".equals(message.getJMSType())  )  {
			semaphore = targetedListenerSemaphore;  // 1 permit
		} else {
			semaphore = processListenerSemaphore;   // N permits
		}
		try {
			// throttle the listener so that it doesn't enqueue more work
			// than necessary.
			semaphore.acquire();
		} catch( InterruptedException e) {
			System.out.println("Semaphore Interrupted ");
		}
		// wrap JMS message and add a semaphore so that the process thread can signal the JMS
		// thread.
		MessageWrapper m = 
				new MessageWrapper(message, session, semaphore, message.getJMSPriority());
		// Add the message to a shared queue. This queue is shared with a custom thread
		// factory where messages are consumed. If a consumer thread is not available
		// the add() method will block to support throttling of incoming messages
		queue.add(m);

	}
			
			
			
}
