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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.aae.message.UimaAsMessage;
import org.apache.uima.aae.message.UimaAsOrigin;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;

public class DirectMessage implements UimaAsMessage {
	
	private static final long serialVersionUID = 1L;
	private Map<String, Object> stateMap = 
			 new HashMap<>();

	private void store(String key, Object value) {
		stateMap.put(key, value);
	}
	public DirectMessage withCasReferenceId(String casReferenceId) {
		store(AsynchAEMessage.CasReference, casReferenceId);
		return this;
	}
	public DirectMessage withEndpointName(String endpointName) {
		store(AsynchAEMessage.EndpointName, endpointName);
		return this;
	}
	
	public DirectMessage withParentCasReferenceId(String parentCasReferenceId) {
		store(AsynchAEMessage.InputCasReference, parentCasReferenceId);
		return this;
	}
	public DirectMessage withSequenceNumber(long seqNo) {
		store(AsynchAEMessage.CasSequence, seqNo);
		return this;
	}
	public DirectMessage withStat(String statKey, long stat) {
		store(statKey, stat);
		return this;
	}
	public DirectMessage withError(Throwable t) {
		store(AsynchAEMessage.ErrorCause, t);
		return this;
	}
	public DirectMessage withFreeCASQueue(BlockingQueue<DirectMessage> freeCASQueue) {
		store(AsynchAEMessage.FreeCASQueue, freeCASQueue);
		return this;

	}
	public DirectMessage withCommand(int command) {
		store(AsynchAEMessage.Command, command);
		if ( command == AsynchAEMessage.Process ) {
			if (System.getProperty("UimaAsCasTracking") != null) {
				store("UimaAsCasTracking", "enable");
			}
		}

		return this;
	}
	public DirectMessage withMessageType(int messageType) {
		store(AsynchAEMessage.MessageType, messageType);
		return this;
	}
	public DirectMessage withOrigin(Origin origin) {
		store(AsynchAEMessage.MessageFrom, origin);
		return this;
	}
	public DirectMessage withPayload(int payloadType) {
		store(AsynchAEMessage.Payload, payloadType);
		return this;
	}

	public DirectMessage withReplyQueue(BlockingQueue<DirectMessage> replyQueue ) {
		store(AsynchAEMessage.ReplyToEndpoint, replyQueue);
		return this;
	}
	public DirectMessage withMetadata(ProcessingResourceMetaData aProcessingResourceMetadata) {
		store(AsynchAEMessage.AEMetadata, aProcessingResourceMetadata);
		return this;
	}
	public DirectMessage withSerializationType(int serializationType) {
		store(AsynchAEMessage.SERIALIZATION, serializationType);
		return this;
	}
	public DirectMessage withReplyDestination(Object replyDestination) {
		store(AsynchAEMessage.ReplyToEndpoint, replyDestination);
		return this;
	}
	public Origin getOrigin() {
		return (Origin)stateMap.get(AsynchAEMessage.MessageFrom);
	}
	public Object getReplyDestination() {
		return stateMap.get(AsynchAEMessage.ReplyToEndpoint);
	}
	public DirectMessage withDelegateKey(Object delegateKey) {
		store(AsynchAEMessage.DelegateKey, delegateKey);
		return this;
	}
	public String getAsString(String key) {
		return (String)stateMap.get(key);
	}
	public int getAsInt(String key) {
		return (Integer)stateMap.get(key);
	}
	public BlockingQueue<DirectMessage> getFreeQueue(String key) {
		return (BlockingQueue<DirectMessage>)stateMap.get(key);
	}
	public Object get(String key) {
		return stateMap.get(key);
	}
	public boolean propertyExists(String key) {
		return stateMap.containsKey(key);
	}
	@Override
	public String getMessageStringProperty(String aMessagePropertyName) throws AsynchAEException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getMessageIntProperty(String aMessagePropertyName) throws AsynchAEException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long getMessageLongProperty(String aMessagePropertyName) throws AsynchAEException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object getMessageObjectProperty(String aMessagePropertyName) throws AsynchAEException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean getMessageBooleanProperty(String aMessagePropertyName) throws AsynchAEException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Endpoint getEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getStringMessage() throws AsynchAEException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object getObjectMessage() throws AsynchAEException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public byte[] getByteMessage() throws AsynchAEException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object getRawMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMessageArrivalTime(long anArrivalTime) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public long getMessageArrivalTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getEndpointName() {
		// TODO Auto-generated method stub
		return null;
	}
}
