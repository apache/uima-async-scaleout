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
package org.apache.uima.aae.message;

import java.util.concurrent.Semaphore;

/**
 * This class supports sorting of messages in a Priority Queue based on priority.
 */
public class MessageWrapper implements Comparable<MessageWrapper>{

	int priority = 0;
	Object message;
	Object session;
	Semaphore semaphore;
	
	public MessageWrapper(Object m, Object s, Semaphore sem, int priority) {
		message = m;
		session = s;
		semaphore = sem;
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}
	public Object getMessage() {
		return message;
	}
	public Object getSession() {
		return session;
	}
	public Semaphore getSemaphore() {
		return semaphore;
	}
	@Override
	public int compareTo(MessageWrapper o) {
		if ( this.getPriority() == o.getPriority() ) {
			  return 0;
		} else {
            return this.getPriority() > o.getPriority() ? 1 : -1;
		}
	}
	
	
}
