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
package org.apache.uima.adapter.jms.client;

import java.util.concurrent.ConcurrentHashMap;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.client.UimaASProcessStatusImpl;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.UimaMessageValidator;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngineCommon_impl.ClientRequest;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngineCommon_impl.SharedConnection;
import org.apache.uima.adapter.jms.message.PendingMessage;
import org.apache.uima.adapter.jms.message.PendingMessageImpl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.util.Level;
import org.apache.uima.util.impl.ProcessTrace_impl;

/**
 * Initializes JMS session and creates JMS MessageProducer to be used for
 * sending messages to a given destination. It extends BaseMessageSender which
 * starts the worker thread and is tasked with sending messages. The application
 * threads share a common 'queue' with the worker thread. The application
 * threads add messages to the pendingMessageList 'queue' and the worker thread
 * consumes them.
 * 
 */
public class ActiveMQMessageSender extends BaseMessageSender {
	private static final Class<?> CLASS_NAME = ActiveMQMessageSender.class;

	private volatile Connection connection = null;

	private Session session = null;

	private MessageProducer producer = null;

	private String destinationName = null;

	private ConcurrentHashMap<Destination, MessageProducer> producerMap = new ConcurrentHashMap<Destination, MessageProducer>();

	public ActiveMQMessageSender(Connection aConnection, String aDestinationName,
			BaseUIMAAsynchronousEngineCommon_impl engine) throws Exception {
		super(engine);
		connection = aConnection;
		destinationName = aDestinationName;
	}

	public synchronized MessageProducer getMessageProducer(Destination destination) throws Exception {
		if (producerMap.containsKey(destination)) {
			return (MessageProducer) producerMap.get(destination);
		}
		createSession();
		MessageProducer mProducer = session.createProducer(destination);
		mProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		producerMap.put(destination, mProducer);
		return mProducer;
	}

	/**
	 * This is called when a new Connection is created after broker is restarted
	 */
	public void setConnection(Connection aConnection) {
		connection = aConnection;
		cleanup();
		try {
			initializeProducer();
		} catch (Exception e) {
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, getClass().getName(), "setConnection",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
			}
		}

	}

	private String getBrokerURL() {
		try {
			return ((ActiveMQConnection) connection).getBrokerInfo().getBrokerURL();
		} catch (Exception ex) { /* handle silently. */
		}
		return "";
	}

	private void createSession() throws Exception {
		String broker = getBrokerURL();
		try {
			if (session == null || engine.producerInitialized == false) {
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			}
		} catch (JMSException e) {
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(), "createSession",
						JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_client_failed_creating_session_INFO",
						new Object[] { destinationName, broker });
			}
			if (connection == null) {
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(), "createSession",
							JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_client_connection_not_ready_INFO",
							new Object[] { broker });
				}
			} else if (((ActiveMQConnection) connection).isClosed() || ((ActiveMQConnection) connection).isClosing()) {
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(), "createSession",
							JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_client_connection_closed_INFO",
							new Object[] { destinationName, broker });
				}
			}
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Returns the full name of the destination queue
	 */
	protected String getDestinationEndpoint() throws Exception {
		return ((ActiveMQDestination) producer.getDestination()).getPhysicalName();
	}

	/**
	 * Creates a jms session object used to instantiate message producer
	 */
	protected void initializeProducer() throws Exception {
		createSession();
		producer = getMessageProducer(session.createQueue(destinationName));
	}

	/**
	 * Returns jsm MessageProducer
	 */
	public MessageProducer getMessageProducer() {
		if (engine.running && engine.producerInitialized == false) {
			try {
				SharedConnection con = engine.lookupConnection(getBrokerURL());
				if (con != null) {
					setConnection(con.getConnection());
					initializeProducer();
					engine.producerInitialized = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, getClass().getName(), "getMessageProducer",
							UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
				}
			}
		}
		return producer;
	}

	public TextMessage createTextMessage() throws Exception {
		synchronized (ActiveMQMessageSender.class) {
			if (session == null) {
				// Force initialization of Producer
				initializeProducer();
			}
			// return session.createTextMessage("");
			TextMessage msg = null;
			try {
				msg = session.createTextMessage("");
			} catch (IllegalStateException e) {
				// stale Session
				session = null;
				initializeProducer();
				msg = session.createTextMessage("");
			}
			return msg;
		}

	}

	public BytesMessage createBytesMessage() throws Exception {
		synchronized (ActiveMQMessageSender.class) {
			if (session == null) {
				// Force initialization of Producer
				initializeProducer();
			}
			BytesMessage msg = null;
			try {
				msg = session.createBytesMessage();
			} catch (IllegalStateException e) {
				// stale Session
				session = null;
				initializeProducer();
				msg = session.createBytesMessage();
			}
			return msg;
		}

		// return session.createBytesMessage();
	}

	/**
	 * Cleanup any jms resources used by the worker thread
	 */
	protected void cleanup() {
		try {
			if (session != null) {
				session.close();
				session = null;
			}
			if (producer != null) {
				producer.close();
				producer = null;
			}
		} catch (Exception e) {
			// Ignore we are shutting down
		} finally {
			producerMap.clear();
		}
	}

	protected void dispatchMessage(PendingMessage pm, BaseUIMAAsynchronousEngineCommon_impl engine,
			boolean casProcessRequest) throws Exception {
		SharedConnection sc = engine.lookupConnection(engine.getBrokerURI());
		ClientRequest cacheEntry = null;
		boolean doCallback = false;
		boolean addTimeToLive = true;
		Session jmsSession = null;

		// Check the environment for existence of NoTTL tag. If present,
		// the deployer of the service wants to disable message expiration.
		if (System.getProperty("NoTTL") != null) {
			addTimeToLive = false;
		}
		try {
			// long t1 = System.currentTimeMillis();
			jmsSession = sc.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Request JMS Message from the concrete implementation
			Message message = null;
			// Determine if this a CAS Process Request
			// boolean casProcessRequest = isProcessRequest(pm);
			// Only Process request can be serialized as binary
			if (casProcessRequest && (engine.getSerialFormat() != SerialFormat.XMI)) {
				message = jmsSession.createBytesMessage();
			} else {
				message = jmsSession.createTextMessage();
			}
			// get the producer initialized from a valid connection
			// producer = getMessageProducer();

			Destination d = null;
			String selector = null;
			// UIMA-AS ver 2.10.0 + sends Free Cas request to a service targeted queue
			// instead of a temp queue. Regular queues can be recovered in case of
			// a broker restart. The test below will be true for UIMA-AS v. 2.10.0 +.
			// Code in JmsOutputChannel will add the selector if the service is a CM.
			if (pm.getPropertyAsString(AsynchAEMessage.TargetingSelector) != null) {
				selector = (String) pm.getPropertyAsString(AsynchAEMessage.TargetingSelector);
			}
			if (selector == null && (pm.getMessageType() == AsynchAEMessage.ReleaseCAS
					|| pm.getMessageType() == AsynchAEMessage.Stop)) {
				d = (Destination) pm.getProperty(AsynchAEMessage.Destination);

			} else {
				d = jmsSession.createQueue(destinationName);
			}
			MessageProducer mProducer = jmsSession.createProducer(d);
			mProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// System.out.println(">>>>>>> Time to create and initialize JMS
			// Sesssion:"+(System.currentTimeMillis()-t1));
			super.initializeMessage(pm, message);
			String destination = ((ActiveMQDestination) d).getPhysicalName();
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINE)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINE, CLASS_NAME.getName(), "run",
						JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_sending_msg_to_endpoint__FINE",
						new Object[] {
								UimaMessageValidator.decodeIntToString(AsynchAEMessage.Command,
										message.getIntProperty(AsynchAEMessage.Command)),
								UimaMessageValidator.decodeIntToString(AsynchAEMessage.MessageType,
										message.getIntProperty(AsynchAEMessage.MessageType)),
								destination });
			}
			if (casProcessRequest) {
				cacheEntry = (ClientRequest) engine.getCache()
						.get(pm.getPropertyAsString(AsynchAEMessage.CasReference));
				if (cacheEntry != null) {
					// CAS cas = cacheEntry.getCAS();
					// enable logging
					if (System.getProperty("UimaAsCasTracking") != null) {
						message.setStringProperty("UimaAsCasTracking", "enable");
					}
					// Target specific service instance if targeting for the CAS is provided
					// by the client application
					if (cacheEntry.getTargetServiceId() != null) {
						// System.out.println("------------Client Sending CAS to Service Instance With
						// Id:"+cacheEntry.getTargetServiceId());;
						message.setStringProperty(UimaAsynchronousEngine.TargetSelectorProperty,
								cacheEntry.getTargetServiceId());
					}
					// Use Process Timeout value for the time-to-live property in the
					// outgoing JMS message. When this time is exceeded
					// while the message sits in a queue, the JMS Server will remove it from
					// the queue. What happens with the expired message depends on the
					// configuration. Most JMS Providers create a special dead-letter queue
					// where all expired messages are placed. NOTE: In ActiveMQ expired msgs in the
					// DLQ
					// are not auto evicted yet and accumulate taking up memory.
					long timeoutValue = cacheEntry.getProcessTimeout();

					if (timeoutValue > 0 && addTimeToLive) {
						// Set high time to live value
						message.setJMSExpiration(10 * timeoutValue);
					}
					if (pm.getMessageType() == AsynchAEMessage.Process) {
						cacheEntry.setCASDepartureTime(System.nanoTime());
					}
					cacheEntry.setCASDepartureTime(System.nanoTime());

					doCallback = true;

				} else {
					if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {

						UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(), "run",
								JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_failed_cache_lookup__WARNING",
								new Object[] { pm.getPropertyAsString(AsynchAEMessage.CasReference),
										UimaMessageValidator.decodeIntToString(AsynchAEMessage.Command,
												message.getIntProperty(AsynchAEMessage.Command)),
										UimaMessageValidator.decodeIntToString(AsynchAEMessage.MessageType,
												message.getIntProperty(AsynchAEMessage.MessageType)),
										destination });
					}
					return; // no cache entry, done here
				}

			}
			// start timers
			if (casProcessRequest) {
				CAS cas = cacheEntry.getCAS();

				// Add the cas to a list of CASes pending reply. Also start the timer if
				// necessary
				engine.serviceDelegate.addCasToOutstandingList(cacheEntry.getCasReferenceId(), cas.hashCode(),
						engine.timerPerCAS); // true=timer per cas
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINE)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINE, CLASS_NAME.getName(), "sendCAS",
							JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_cas_added_to_pending_FINE",
							new Object[] { cacheEntry.getCasReferenceId(), String.valueOf(cas.hashCode()),
									engine.serviceDelegate.toString() });
				}

			} else if (pm.getMessageType() == AsynchAEMessage.GetMeta
					&& engine.serviceDelegate.getGetMetaTimeout() > 0) {
				// timer for PING has been started in sendCAS()
				if (!engine.serviceDelegate.isAwaitingPingReply()) {
					engine.serviceDelegate.startGetMetaRequestTimer();
				}
			} else {
				doCallback = false; // dont call onBeforeMessageSend() callback on CPC
			}
			// Dispatch asynchronous request to Uima AS service
			mProducer.send(message);

			if (doCallback) {
				UimaASProcessStatus status = new UimaASProcessStatusImpl(new ProcessTrace_impl(), cacheEntry.getCAS(),
						cacheEntry.getCasReferenceId());
				// Notify engine before sending a message
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINE)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINE, CLASS_NAME.getName(), "run",
							JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_calling_onBeforeMessageSend__FINE",
							new Object[] { pm.getPropertyAsString(AsynchAEMessage.CasReference),
									String.valueOf(cacheEntry.getCAS().hashCode()) });
				}
				// Note the callback is a misnomer. The callback is made *after* the send now
				// Application receiving this callback can consider the CAS as delivere to a
				// queue
				engine.onBeforeMessageSend(status);

			}
		} catch (Exception e) {
			if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
				UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, getClass().getName(), "dispatchMessage",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__WARNING", e);
			}
		} finally {
			if (jmsSession != null) {
				try {
					jmsSession.close();
				} catch (Exception eee) {

				}
			}
		}

	}
}