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

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.aae.InputChannel;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ENDPOINT_TYPE;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ServiceState;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.handler.Handler;
import org.apache.uima.aae.jmx.ServiceInfo;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.UIMAMessage;
import org.apache.uima.aae.service.command.CommandFactory;
import org.apache.uima.aae.service.command.UimaAsCommand;

public class DirectInputChannel implements InputChannel  {
	private static final Class<?> CLASS_NAME = DirectInputChannel.class;
	AnalysisEngineController controller;
	Handler handler;
	String endpointName;
	private ServiceInfo serviceInfo = null;
	private boolean isStopped;
	private List<Listener> listeners = new ArrayList<>();
	private ENDPOINT_TYPE type = ENDPOINT_TYPE.DIRECT;
	private ChannelType channelType;

	public DirectInputChannel(ChannelType type) {
		this.channelType = type;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public ENDPOINT_TYPE getType() {
		return type;
	}

	public DirectInputChannel withController(AnalysisEngineController controller) {
		this.controller = controller;
		return this;
	}

	public void setController(AnalysisEngineController controller) {
		this.controller = controller;
	}
	
	public void onMessage(DirectMessage message) {

		try {
			// every message is wrapped in the MessageContext
			MessageContext mc = new DirectMessageContext(message, "", getController().getComponentName());
			if (validMessage(mc)) {
				UimaAsCommand cmd = CommandFactory.newCommand(mc, controller);
				cmd.execute();
			}
		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	public AnalysisEngineController getController() {
		return controller;
	}

	/**
	 * Validate command contained in the header of the JMS Message
	 * 
	 * @param aMessage
	 *            - JMS Message received
	 * @param properties
	 *            - Map containing header properties
	 * @return - true if the command received is a valid one, false otherwise
	 * @throws Exception
	 */
	private boolean validCommand(MessageContext aMessage) throws Exception {
		if (aMessage.propertyExists(AsynchAEMessage.Command)) {
			int command = aMessage.getMessageIntProperty(AsynchAEMessage.Command);
			if (command != AsynchAEMessage.Process && command != AsynchAEMessage.GetMeta
					&& command != AsynchAEMessage.ReleaseCAS && command != AsynchAEMessage.Stop
					&& command != AsynchAEMessage.Ping && command != AsynchAEMessage.ServiceInfo
					&& command != AsynchAEMessage.CollectionProcessComplete) {
				System.out.println(CLASS_NAME + ".validCommand() - invalid command in message " + command);
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
				 * "validCommand", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_invalid_command_in_message__INFO", new Object[] { command,
				 * endpointName }); }
				 */
				return false;
			}
		} else {
			/*
			 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
			 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
			 * "validCommand", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
			 * "UIMAJMS_command_notin_message__INFO", new Object[] { endpointName }); }
			 */
			System.out.println(CLASS_NAME + ".validCommand() - command not in message");

			return false;
		}

		return true;
	}

	/**
	 * Validates contents of the message. It checks if command, payload and message
	 * types contain valid data.
	 * 
	 * @param aMessage
	 *            - JMS Message to validate
	 * @return - true if message is valid, false otherwise
	 * @throws Exception
	 */
	public boolean validMessage(MessageContext aMessage) throws Exception {
		if (aMessage instanceof DirectMessageContext) {
			// Map properties = ((ActiveMQMessage) aMessage).getProperties();
			if (!validMessageType(aMessage)) {
				int msgType = 0;
				if (aMessage.propertyExists(AsynchAEMessage.MessageType)) {
					msgType = aMessage.getMessageIntProperty(AsynchAEMessage.MessageType);
				}
				System.out.println(CLASS_NAME + ".validMessage() - invalid message type " + msgType);
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
				 * "validMessage", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_invalid_msg_type__INFO", new Object[] {
				 * getController().getComponentName(), msgType }); }
				 */
				return false;
			}
			if (!validCommand(aMessage)) {
				int command = 0;
				if (aMessage.propertyExists(AsynchAEMessage.Command)) {
					command = aMessage.getMessageIntProperty(AsynchAEMessage.Command);
				}
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
				 * "validMessage", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_invalid_cmd_type__INFO", new Object[] {
				 * getController().getComponentName(), command }); }
				 */
				System.out.println(CLASS_NAME + ".validMessage() - invalid command in message " + command);

				return false;
			}
			if (!validPayload(aMessage)) {
				int payload = 0;
				if (aMessage.propertyExists(AsynchAEMessage.Payload)) {
					payload = aMessage.getMessageIntProperty(AsynchAEMessage.Payload);
				}
				System.out.println(CLASS_NAME + ".validMessage() - invalid payload type " + payload);
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
				 * "validMessage", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_invalid_payload_type__INFO", new Object[] {
				 * getController().getComponentName(), payload }); }
				 */
				return false;
			}

			if (isStaleMessage(aMessage)) {
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Validate message type contained in the JMS header.
	 * 
	 * @param aMessage
	 *            - jms message retrieved from queue
	 * @param properties
	 *            - map containing message properties
	 * @return - true if message Type is valid, false otherwise
	 * @throws Exception
	 */

	private boolean validMessageType(MessageContext aMessage) throws Exception {
		if (aMessage.propertyExists(AsynchAEMessage.MessageType)) {
			int msgType = aMessage.getMessageIntProperty(AsynchAEMessage.MessageType);
			if (msgType != AsynchAEMessage.Response && msgType != AsynchAEMessage.Request) {
				System.out.println(CLASS_NAME + ".validMessageType() - invalid message type " + msgType);
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
				 * "validMessageType", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_invalid_msgtype_in_message__INFO", new Object[] { msgType,
				 * endpointName }); }
				 */
				return false;

			}
		} else {
			System.out.println(CLASS_NAME + ".validMessageType() - message type not in message");
			/*
			 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
			 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
			 * "validMessageType", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
			 * "UIMAJMS_msgtype_notin_message__INFO", new Object[] { endpointName }); }
			 */
			return false;
		}

		return true;
	}

	private boolean isProcessRequest(MessageContext aMessage) throws Exception {
		if (aMessage.propertyExists(AsynchAEMessage.MessageType) && aMessage.propertyExists(AsynchAEMessage.Command)) {
			int msgType = aMessage.getMessageIntProperty(AsynchAEMessage.MessageType);
			int command = aMessage.getMessageIntProperty(AsynchAEMessage.Command);

			if (msgType != AsynchAEMessage.Request || command != AsynchAEMessage.Process) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean isRemoteRequest(MessageContext aMessage) throws Exception {

		// Dont do checkpoints if a message was sent from a Cas Multiplier
		if (aMessage.propertyExists(AsynchAEMessage.CasSequence)) {
			return false;
		}

		if (aMessage.propertyExists(AsynchAEMessage.MessageType) && aMessage.propertyExists(AsynchAEMessage.Command)
				&& aMessage.propertyExists(UIMAMessage.ServerURI)) {
			int msgType = aMessage.getMessageIntProperty(AsynchAEMessage.MessageType);
			int command = aMessage.getMessageIntProperty(AsynchAEMessage.Command);
			boolean isRemote = aMessage.getMessageStringProperty(UIMAMessage.ServerURI).startsWith("vm") == false;
			if (isRemote && msgType == AsynchAEMessage.Request
					&& (command == AsynchAEMessage.Process || command == AsynchAEMessage.CollectionProcessComplete)) {
				return true;
			}
		}
		return false;
	}

	private boolean acceptsDeltaCas(MessageContext aMessage) throws Exception {
		boolean acceptsDeltaCas = false;
		if (aMessage.propertyExists(AsynchAEMessage.AcceptsDeltaCas)) {
			acceptsDeltaCas = aMessage.getMessageBooleanProperty(AsynchAEMessage.AcceptsDeltaCas);
		}
		return acceptsDeltaCas;
	}

	public void abort() {
	}

	private String decodeIntToString(String aTypeToDecode, int aValueToDecode) {
		if (AsynchAEMessage.MessageType.equals(aTypeToDecode)) {
			switch (aValueToDecode) {
			case AsynchAEMessage.Request:
				return "Request";
			case AsynchAEMessage.Response:
				return "Response";
			}
		} else if (AsynchAEMessage.Command.equals(aTypeToDecode)) {
			switch (aValueToDecode) {
			case AsynchAEMessage.Process:
				return "Process";
			case AsynchAEMessage.GetMeta:
				return "GetMetadata";
			case AsynchAEMessage.CollectionProcessComplete:
				return "CollectionProcessComplete";
			case AsynchAEMessage.ReleaseCAS:
				return "ReleaseCAS";
			case AsynchAEMessage.Stop:
				return "Stop";
			case AsynchAEMessage.Ping:
				return "Ping";
			case AsynchAEMessage.ServiceInfo:
				return "ServiceInfo";
			}

		} else if (AsynchAEMessage.Payload.equals(aTypeToDecode)) {
			switch (aValueToDecode) {
			case AsynchAEMessage.XMIPayload:
				return "XMIPayload";
			case AsynchAEMessage.BinaryPayload:
				return "BinaryPayload";
			case AsynchAEMessage.CASRefID:
				return "CASRefID";
			case AsynchAEMessage.Metadata:
				return "Metadata";
			case AsynchAEMessage.Exception:
				return "Exception";
			// 5/2013 xcas not used
			// case AsynchAEMessage.XCASPayload:
			// return "XCASPayload";
			case AsynchAEMessage.None:
				return "None";
			}
		}
		return "UNKNOWN";
	}

	private boolean isStaleMessage(MessageContext aMessage) throws AsynchAEException {
		int command = aMessage.getMessageIntProperty(AsynchAEMessage.Command);
		int msgType = aMessage.getMessageIntProperty(AsynchAEMessage.MessageType);
		if (isStopped() || getController() == null || getController().getInProcessCache() == null) {
			// Shutting down
			return true;
		}
		if (command == AsynchAEMessage.Process && msgType == AsynchAEMessage.Response) {
			String casReferenceId = aMessage.getMessageStringProperty(AsynchAEMessage.CasReference);
			if (!getController().getInProcessCache().entryExists(casReferenceId)) {

				System.out.println(CLASS_NAME + ".isStaleMessage() - stale message rec'd - CasRefId:" + casReferenceId);
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb( Level.FINE, CLASS_NAME.getName(),
				 * "isStaleMessage", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_stale_message__FINE", new Object[] { endpointName, casReferenceId,
				 * aMessage.getMessageStringProperty(AsynchAEMessage.MessageFrom) });
				 * 
				 * }
				 */
				return true;
			}
		}
		return false;
	}

	/**
	 * Validates payload in the JMS Message.
	 * 
	 * @param aMessage
	 *            - JMS Message received
	 * @param properties
	 *            - Map containing header properties
	 * @return - true if the payload is valid, false otherwise
	 * @throws Exception
	 */
	private boolean validPayload(MessageContext aMessage) throws Exception {
		if (aMessage.propertyExists(AsynchAEMessage.Command)) {
			int command = aMessage.getMessageIntProperty(AsynchAEMessage.Command);
			if (command == AsynchAEMessage.GetMeta || command == AsynchAEMessage.CollectionProcessComplete
					|| command == AsynchAEMessage.Stop || command == AsynchAEMessage.Ping
					|| command == AsynchAEMessage.ServiceInfo || command == AsynchAEMessage.ReleaseCAS) {
				// Payload not included in GetMeta Request
				return true;
			}
		}

		if (aMessage.propertyExists(AsynchAEMessage.Payload)) {
			int payload = aMessage.getMessageIntProperty(AsynchAEMessage.Payload);
			if (payload != AsynchAEMessage.XMIPayload && payload != AsynchAEMessage.BinaryPayload
					&& payload != AsynchAEMessage.CASRefID && payload != AsynchAEMessage.Exception
					&& payload != AsynchAEMessage.Metadata) {
				/*
				 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
				 * "validPayload", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
				 * "UIMAJMS_invalid_payload_in_message__INFO", new Object[] { payload,
				 * endpointName }); }
				 */
				System.out.println(CLASS_NAME + ".validPayload() - Invalid Payload in message:" + payload);
				return false;
			}
		} else {
			/*
			 * if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
			 * UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
			 * "validPayload", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
			 * "UIMAJMS_payload_notin_message__INFO", new Object[] { endpointName }); }
			 */
			System.out.println(CLASS_NAME + ".validPayload() - Payload not in message");
			return false;
		}

		return true;
	}

	public void setMessageHandler(Handler aHandler) {
		handler = aHandler;
		// msgHandlerLatch.countDown();
	}

	public List<Listener> registerListener(Listener listener) {
		listeners.add(listener);
		return listeners;
	}

	public void setListener(DirectListener listener) {
		listeners.add(listener);
	}

	public void setListeners(List<DirectListener> list) {
		System.out.println("... DirectInputChannel adding listeners - howMany:" + list.size());
		listeners.addAll(list);

	}

	public List<Listener> getListeners() {
		List<Listener> ll = new ArrayList<>();
		for (Listener l : listeners) {
			ll.add(l);
		}
		return ll;
	}

	@Override
	public void stop(boolean shutdownNow) throws Exception {
		isStopped = true;
		for (Listener l : listeners) {
			l.stop();
		}
	}

	@Override
	public void stop(int channelsToStop, boolean shutdownNow) throws Exception {
		// TODO Auto-generated method stub
		for (Listener l : listeners) {
			l.stop();
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSessionAckMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ackMessage(MessageContext aMessageContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getServerUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServerUri(String aServerUri) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInputQueueName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceInfo getServiceInfo() {
		if (serviceInfo == null) {
			serviceInfo = new ServiceInfo(false, controller);
			serviceInfo.setBrokerURL("Direct");
			serviceInfo.setInputQueueName(getName());
			if (controller == null) {
				serviceInfo.setState(ServiceState.INITIALIZING.name());
			} else {
				if (controller.isCasMultiplier()) {
					serviceInfo.setCASMultiplier();
				}
			}
		}
		return serviceInfo;
	}

	@Override
	public boolean isStopped() {
		return isStopped;
	}

	public int getConcurrentConsumerCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void destroyListener(String anEndpointName, String aDelegateKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createListener(String aDelegateKey, Endpoint endpointToUpdate) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFailed(String aDelegateKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isListenerForDestination(String anEndpointName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeDelegateFromFailedList(String aDelegateKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTerminating() {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectListenersFromQueue() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectListenerFromQueue(Listener listener) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEndpointName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createListenerForTargetedMessages() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
