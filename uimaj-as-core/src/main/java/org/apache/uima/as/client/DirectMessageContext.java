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

import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.Endpoint_impl;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.cas.SerialFormat;

public class DirectMessageContext implements MessageContext {
	private Endpoint endpoint;

	private long messageArrivalTime = 0L;

	private String endpointName;

	DirectMessage message;
	
	public DirectMessageContext() {
	}

	public DirectMessageContext(DirectMessage message, String anEndpointName, String controllerName ) {
		this();
		this.message = message;
		endpoint =  new Endpoint_impl();
		endpointName = anEndpointName;
		endpoint.setSerialFormat(SerialFormat.UNKNOWN);
		endpoint.setServerURI("java");
		endpoint.setEndpoint(anEndpointName);
		endpoint.setReplyDestination(message.get(AsynchAEMessage.ReplyToEndpoint));
		endpoint.setDelegateKey(message.getAsString(AsynchAEMessage.DelegateKey));
		StringBuilder sb = new StringBuilder();
		if ( controllerName != null && !controllerName.trim().isEmpty()) {
			sb.append("Service:"+controllerName+" ");
			sb.append("Delegate Key:"+endpoint.getDelegateKey()+"\t");

		}
		sb.append(this.getClass().getSimpleName()).append("\n").append(" - message from:").
		append(message.get(AsynchAEMessage.MessageFrom)).append("\n").
		append(" - ServerURI:").append("java").append("\n");
		if ( message.propertyExists(AsynchAEMessage.CasReference) ) {
			sb.append(" - CasReferenceId:").
			append(message.getAsString(AsynchAEMessage.CasReference))
			.append("\n");
		}
		
		System.out.println(sb.toString());
	}

	public String getMessageStringProperty(String aMessagePropertyName) throws AsynchAEException {
		return (String)message.get(aMessagePropertyName);
	}

	public int getMessageIntProperty(String aMessagePropertyName) throws AsynchAEException {
		return (Integer)message.get(aMessagePropertyName);
	}

	public long getMessageLongProperty(String aMessagePropertyName) throws AsynchAEException {
		return (Long)message.get(aMessagePropertyName);
	}

	public Object getMessageObjectProperty(String aMessagePropertyName) throws AsynchAEException {
		return message.get(aMessagePropertyName);
	}
	
	public boolean getMessageBooleanProperty(String aMessagePropertyName) throws AsynchAEException {
		return (Boolean)message.get(aMessagePropertyName);
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	private Object getMessageCargo() {
		return message.get("Cargo");
	}
	public String getStringMessage() throws AsynchAEException {
		return (String)getMessageCargo();
	}

	public Object getObjectMessage() throws AsynchAEException {
		return getMessageCargo();
	}

	public byte[] getByteMessage() throws AsynchAEException {
		return null;   // this class returns XMI only
	}

	public Object getRawMessage() {
		return message;
	}

	public boolean propertyExists(String aKey) throws AsynchAEException {
		return message.propertyExists(aKey);
	}

	public void setMessageArrivalTime(long anArrivalTime) {
		messageArrivalTime = anArrivalTime;
	}

	public long getMessageArrivalTime() {
		return messageArrivalTime;
	}

	public String getEndpointName() {
		return endpointName;
	}
}
