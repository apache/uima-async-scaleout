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

package org.apache.uima.aae;

import java.util.List;

import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.handler.Handler;
import org.apache.uima.aae.jmx.ServiceInfo;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.MessageWrapper;
import org.apache.uima.as.client.Listener;

public interface InputChannel extends Channel {
  // The REPLY type is only for handling replies from remote services.
  public enum ChannelType {REPLY, REQUEST_REPLY };
	
  public void setController(AnalysisEngineController controller) throws Exception;
  public void setEndpointName(String name);
  public void setMessageHandler(Handler handler);
  
  public ChannelType getChannelType();
  public int getSessionAckMode();

  public void ackMessage(MessageContext aMessageContext);

  public String getServerUri();

  public void setServerUri(String aServerUri);

  public String getInputQueueName();

  public ServiceInfo getServiceInfo();

  public boolean isStopped();

 // public int getConcurrentConsumerCount();

  public void destroyListener(String anEndpointName, String aDelegateKey);

  public void createListener(String aDelegateKey, Endpoint endpointToUpdate) throws Exception;

  public void createListenerForTargetedMessages() throws Exception;
  
  public List<Listener> getListeners();
  
//  public void addListener(Listener listener);
  public List<Listener> registerListener(Listener messageListener);
  
  public void disconnectListenersFromQueue() throws Exception;

  public void disconnectListenerFromQueue(Listener listener) throws Exception;

  public boolean isFailed(String aDelegateKey);

  public boolean isListenerForDestination(String anEndpointName);
  
  public void removeDelegateFromFailedList( String aDelegateKey );

  public void setTerminating();
  
  public void terminate();
  
 
  public void onMessage(MessageWrapper message);
  
  public ENDPOINT_TYPE getType();
}
