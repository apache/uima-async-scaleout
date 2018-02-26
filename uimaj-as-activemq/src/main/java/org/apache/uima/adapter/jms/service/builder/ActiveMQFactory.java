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
package org.apache.uima.adapter.jms.service.builder;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.command.ActiveMQQueue;

public class ActiveMQFactory {
	private ActiveMQFactory() {
		
	}
	public static ActiveMQQueue newQueue(String withName) {
		return new ActiveMQQueue(withName);
	}
	
	public static ActiveMQPrefetchPolicy newPrefetchPolicy(int howMany) {
		ActiveMQPrefetchPolicy prefetchPolicy =
				new ActiveMQPrefetchPolicy();
		prefetchPolicy.setQueuePrefetch(howMany);
		return prefetchPolicy;
	}
	
	public static ActiveMQConnectionFactory newConnectionFactory(String broker, int prefetch) {
		ActiveMQConnectionFactory factory = 
				new ActiveMQConnectionFactory();
		factory.setBrokerURL(broker);
		factory.setPrefetchPolicy(newPrefetchPolicy(prefetch));

		return factory;
	}
}
