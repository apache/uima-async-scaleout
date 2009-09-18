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

package org.apache.uima.camel;

import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represent the UIMA-AS camel driver endpoint.
 */
public class UimaAsEndpoint extends DefaultEndpoint<Exchange>{

	private String brokerAddress;
	private String queue;
	private Integer casPoolSize;
	
	public UimaAsEndpoint(String uri, String brokerAddress, 
    		UimaAsComponent component) {
		super(uri, component);
		
		this.brokerAddress = brokerAddress;
	}
	
    @Override
    public void configureProperties(Map options) {
    	super.configureProperties(options);
    	queue = (String) options.remove("queue");
    	
    	if (options.containsKey("casPoolSize")) {
    		String casPoolSizeString = (String) options.remove("casPoolSize");
    		try {
    			casPoolSize = Integer.parseInt(casPoolSizeString);
    			if (casPoolSize < 1) {
    				System.out.println("Warning casPoolSize must be larger than zero, fallback to default!");
    				casPoolSize = null;
    			}
    		}
    		catch (NumberFormatException e) {
    			System.out.println("Warning cas pool size is invalid, fallback to default!");
    		}
    	}
    }
    
	public Consumer<Exchange> createConsumer(Processor arg0) throws Exception {
		return null;
	}

	public Producer<Exchange> createProducer() throws Exception {
		return new UimaAsProducer(brokerAddress, queue, casPoolSize, this);
	}

	public boolean isSingleton() {
		return false;
	}
}
