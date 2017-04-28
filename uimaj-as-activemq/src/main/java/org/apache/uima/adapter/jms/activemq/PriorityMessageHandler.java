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

import org.apache.uima.aae.message.MessageWrapper;
import org.springframework.jms.listener.SessionAwareMessageListener;

@SuppressWarnings("rawtypes")
public class PriorityMessageHandler implements SessionAwareMessageListener {

	private PriorityBlockingQueue<MessageWrapper> queue =
			new PriorityBlockingQueue<>();


    public BlockingQueue<MessageWrapper> getQueue() {
    	return queue;
    }
    /**
     * Wraps an incoming message in a protocol neutral envelope and adds priority
     * value to enable sorting in a Priority Queue. Messages specifically targetting
     * an instance of a service will be assigned high priority for processing.
     */
	public void onMessage(Message message, Session session) throws JMSException {
		Semaphore semaphore = new Semaphore(0);
		MessageWrapper m = 
				new MessageWrapper(message, session, semaphore, message.getJMSPriority());
		// Add the message to a shared queue. This queue is shared with a custom thread
		// factory where messages are consumed. If a consumer thread is not available
		// the add() method will block to support throttling of incoming messages
		queue.add(m);
	}
			
			
			
}
