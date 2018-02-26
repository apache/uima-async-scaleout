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
package org.apache.uima.adapter.jms.message;

import java.util.HashMap;
import java.util.Map;

public class PendingMessageImpl implements PendingMessage {

	private static final long serialVersionUID = 3512718154731557413L;
	private final int messageType;
	private Map<String, Object> cache = new HashMap<>();

	public PendingMessageImpl(int aMessageType) {
		messageType = aMessageType;
	}

	public int getMessageType() {
		return messageType;
	}

	public Object getProperty(String propertyKey) {

		return cache.get(propertyKey);
	}

	public void addProperty(String propertyKey, Object property) {
		cache.put(propertyKey, property);
	}
	public String getPropertyAsString(String key) {
		return (String)getProperty(key);
	}
	public int getPropertyAsInt(String key) {
		return (Integer)getProperty(key);
	}
	public byte[] getPropertyAsBytesArray(String key) {
		return (byte[])getProperty(key);
	}
}
