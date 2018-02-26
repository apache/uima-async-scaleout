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

import org.apache.uima.as.client.DirectMessage;

public class UimaASClientDirectMessage implements UimaASClientMessage {
	private DirectMessage message;
	public UimaASClientDirectMessage(DirectMessage message) {
		this.message = message;
	}
	
	public int command() {
		return ((Integer)message.get(AsynchAEMessage.Command)).intValue();
	}

	@Override
	public String messageFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int serializationMethod() {
		// TODO Auto-generated method stub
		return -1;
	}

	public String casReferenceId() {
		return asString(AsynchAEMessage.CasReference);
	}

	public int payload() {
		return asInt(AsynchAEMessage.Payload);
	}

	public String serverIP() {
		return null;
	}

	public Object replyTo() {
		return null;
	}

	public boolean serializationSpecified() {
		// TODO Auto-generated method stub
		return false;
	}

	public String asText() {
		return (String)message.get(AsynchAEMessage.AEMetadata);
	}

	@Override
	public Object asObject(String key) {
		return message.get(key);//AsynchAEMessage.AEMetadata);
	}

	@Override
	public boolean contains(String key) {
		// TODO Auto-generated method stub
		return message.propertyExists(key);
	}

	@Override
	public String asString(String key) {
		// TODO Auto-generated method stub
		return message.getAsString(key);
	}

	@Override
	public int asInt(String key) {
		// TODO Auto-generated method stub
		return message.getAsInt(key);
	}
	
	
}
