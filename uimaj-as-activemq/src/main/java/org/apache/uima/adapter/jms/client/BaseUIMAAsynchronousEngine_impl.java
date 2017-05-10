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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.uima.UIMAFramework;
import org.apache.uima.UIMA_IllegalArgumentException;
import org.apache.uima.UIMA_IllegalStateException;
import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.aae.VersionCompatibilityChecker;
import org.apache.uima.aae.UimaASApplicationEvent.EventTrigger;
import org.apache.uima.aae.UimaASApplicationExitEvent;
import org.apache.uima.aae.UimaAsVersion;
import org.apache.uima.aae.client.UimaASStatusCallbackListener;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.ControllerCallbackListener;
import org.apache.uima.aae.controller.ControllerLifecycle;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.controller.UimacppServiceController;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.aae.error.UimaASMetaRequestTimeout;
import org.apache.uima.aae.jmx.JmxManager;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.UIMAMessage;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.adapter.jms.activemq.ConnectionFactoryIniter;
import org.apache.uima.adapter.jms.activemq.SpringContainerDeployer;
import org.apache.uima.adapter.jms.activemq.UimaEEAdminSpringContext;
import org.apache.uima.adapter.jms.service.Dd2spring;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.impl.UimaVersion;
import org.apache.uima.internal.util.UUIDGenerator;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class BaseUIMAAsynchronousEngine_impl extends BaseUIMAAsynchronousEngineCommon_impl
        implements UimaAsynchronousEngine, MessageListener, ControllerCallbackListener, ApplicationListener<ApplicationEvent>{
  private static final Class CLASS_NAME = BaseUIMAAsynchronousEngine_impl.class;

  private MessageSender sender = null;

  private MessageProducer producer;

  private Session session = null;

  private Session consumerSession = null;

  private volatile boolean serviceInitializationException;

  private volatile boolean serviceInitializationCompleted;

  private Semaphore serviceSemaphore = new Semaphore(1);

  private Queue consumerDestination = null;

  private Session producerSession = null;

  private JmxManager jmxManager = null;

  private String applicationName = "UimaASClient";

 private String endpoint;
 
 protected static Semaphore sharedConnectionSemaphore = new Semaphore(1);

  protected static Object connectionMux = new Object();

  protected InitialContext jndiContext;
  
  private ObjectName clientJmxObjectName = null;
  
  private String amqUser = null;
  
  private String amqPassword = null;
  
  protected static Lock globalLock = new ReentrantLock();
  
  private String serviceTargetSelector = null;
  
  protected volatile boolean stopped = false;
  public BaseUIMAAsynchronousEngine_impl() {
	  super();
    UIMAFramework.getLogger(CLASS_NAME).log(Level.INFO,
            "UIMA Version " + UIMAFramework.getVersionString() +
    " UIMA-AS Version " + UimaAsVersion.getVersionString());
  }


  protected TextMessage createTextMessage() throws ResourceInitializationException {
    return new ActiveMQTextMessage();
  }

  protected BytesMessage createBytesMessage() throws ResourceInitializationException {
    return new ActiveMQBytesMessage();
  }

  /**
   * Called at the end of collectionProcessingComplete - WAS closes receiving thread here
   */
  protected void cleanup() throws Exception {
  }

  /**
   * Return a name of the queue to which the JMS Producer is connected to.
   */
  public String getEndPointName() throws ResourceProcessException {
    try {
      return clientSideJmxStats.getEndpointName();
    } catch (Exception e) {
      throw new ResourceProcessException(e);
    }
  }

  protected void setMetaRequestMessage(Message msg) throws Exception {
    msg.setStringProperty(AsynchAEMessage.MessageFrom, consumerDestination.getQueueName());

    msg.setStringProperty(UIMAMessage.ServerURI, brokerURI);
    msg.setIntProperty(AsynchAEMessage.MessageType, AsynchAEMessage.Request);
    msg.setIntProperty(AsynchAEMessage.Command, AsynchAEMessage.GetMeta);
    msg.setJMSReplyTo(consumerDestination);
    if (msg instanceof TextMessage) {
      ((ActiveMQTextMessage) msg).setText("");
    }
  }

  /**
   * Initialize JMS Message with properties relevant to Process CAS request.
   */
  protected void setCASMessage(String aCasReferenceId, CAS aCAS, Message msg)
          throws ResourceProcessException {
    try {
      setCommonProperties(aCasReferenceId, msg, SerialFormat.XMI);
      ((TextMessage) msg).setText(serializeCAS(aCAS));
    } catch (Exception e) {
      throw new ResourceProcessException(e);
    }
  }

  protected void setCASMessage(String aCasReferenceId, String aSerializedCAS, Message msg)
          throws ResourceProcessException {
    try {
      setCommonProperties(aCasReferenceId, msg, SerialFormat.XMI);
      ((TextMessage) msg).setText(aSerializedCAS);
    } catch (Exception e) {
      throw new ResourceProcessException(e);
    }
  }

  protected void setCASMessage(String aCasReferenceId, byte[] aSerializedCAS, Message msg)
          throws ResourceProcessException {
    try {
      setCommonProperties(aCasReferenceId, msg, SerialFormat.BINARY);
      ((BytesMessage) msg).writeBytes(aSerializedCAS);
    } catch (Exception e) {
      throw new ResourceProcessException(e);
    }
  }

  protected void setCommonProperties(String aCasReferenceId, Message msg,
          SerialFormat serialFormat) throws ResourceProcessException {
    try {
      msg.setStringProperty(AsynchAEMessage.MessageFrom, consumerDestination.getQueueName());
      // check if this message should target specific service instance
      if ( serviceTargetSelector != null ) {
          msg.setStringProperty(UimaAsynchronousEngine.TargetSelectorProperty,serviceTargetSelector);
      }
      msg.setStringProperty(UIMAMessage.ServerURI, brokerURI);
      msg.setIntProperty(AsynchAEMessage.MessageType, AsynchAEMessage.Request);
      msg.setIntProperty(AsynchAEMessage.Command, AsynchAEMessage.Process);
      msg.setStringProperty(AsynchAEMessage.CasReference, aCasReferenceId);
      msg.setIntProperty(AsynchAEMessage.Payload, (serialFormat == SerialFormat.XMI) ? AsynchAEMessage.XMIPayload : AsynchAEMessage.BinaryPayload);
      msg.setBooleanProperty(AsynchAEMessage.AcceptsDeltaCas, true);
      msg.setJMSReplyTo(consumerDestination);

    } catch (Exception e) {
      throw new ResourceProcessException(e);
    }

  }
  private void stopConnection() {
	
	SharedConnection sharedConnection;
    // there is a dedicated shared connection for each broker URI
	if ( brokerURI != null && (sharedConnection = lookupConnection(brokerURI)) != null) {
    	try {
    		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
        		String msg = ":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - acquiring semaphore";
    	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
    	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
    	                  new Object[] { msg });
    	    }
    		// use broker dedicated semaphore to lock the code for updates
        	sharedConnection.getSemaphore().acquire();
    		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
        	  String msg =":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - acquired semaphore";
  	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
  	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
  	                  new Object[] { msg });
  	        }
        	 
        	// Remove a client from registry
    	      sharedConnection.unregisterClient(this);
    		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
          		String msg = ":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - 1";
    	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
    	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
    	                  new Object[] { msg });
   	        }

    	      ActiveMQConnection amqc = (ActiveMQConnection)sharedConnection.getConnection();
           		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
            		String msg = ":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - 2";
        	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
        	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
        	                  new Object[] { msg });
       	        }

        		if (initialized) {
               		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
        	      		String msg = ":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - 3";
            	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
            	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
            	                  new Object[] { msg });
           	        }

   	    	     try {
   	    	    	if ( amqc != null ) {
   	               		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
   	   	    	    		String msg = "................ Closing Client Connection client ID:"+amqc.getConnectionInfo().getClientId()+" Client Count:"+sharedConnection.getClientCount();
   	            	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
   	            	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
   	            	                  new Object[] { msg });
   	           	        }
   	    	    		
   	    	    	} else {
   	               		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
   	   	    	      		String msg = ":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - 5";
   	            	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
   	            	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
   	            	                  new Object[] { msg });
   	           	        }

   	    	    	}
    	    		if ( amqc != null && amqc.isStarted() && 
    	    			 ((ActiveMQSession)consumerSession).isRunning() ) {
   	   	           		consumerSession.close();
     	    	   	    ((ActiveMQMessageConsumer)consumer).stop();
     	    	   	    consumer.close();
   	               		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
   	     	         		String msg = ":::::::::::::::: "+Thread.currentThread().getId()+" stopConnection() - 6";
   	            	          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "stopConnection",
   	            	                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
   	            	                  new Object[] { msg });
   	           	        }
   	   	        		if ( sharedConnection.getClientCount() <= 1 ) {
   	   	        			
   	   	        		   sharedConnection.destroy();
   	   	        		   amqc.close();
   	   	        		}
    	    		} else if ( sharedConnection.getClientCount() <= 1 ) {
	   	        			
	   	        		   sharedConnection.destroy();
	   	        		   amqc.close();
	   	        		}
   	   	         } catch (Exception exx) {exx.printStackTrace();}
    	   	  }
    	      // Delete client's temp reply queue from AMQ Broker 
    	      if ( amqc != null && !amqc.isClosed() && !amqc.isClosing() && consumerDestination != null && 
    	           consumerDestination instanceof ActiveMQTempDestination ) {
    	        try {
    	        	if ( !amqc.isClosed() && !amqc.isTransportFailed()) {
    	    	          amqc.deleteTempDestination((ActiveMQTempDestination)consumerDestination);
    	        	}
    	        } catch( Exception e) {
    	          e.printStackTrace();
    	        }
    	      }
    		
    	} catch (Exception e) {
    		
    	} finally {
    		sharedConnection.destroy();
    		if ( sharedConnection != null  ) {
    			sharedConnection.getSemaphore().release();
    		}

    	}
    }
  }
	public void stop() {
		try {
		     
		      //stopped = true;
			  System.out.println(this.getClass().getName()+".stop() - Stopping UIMA-AS Client");
			  stopConnection();

		      super.doStop();
		      if (!running) {
		        return;
		      }
		      running = false;
			  if (super.serviceDelegate != null) {
				// Cancel all timers and purge lists
				super.serviceDelegate.cleanup();
			  }

		      if (sender != null) {
				    System.out.println("BaseUIMAAsynchronousEngine.stop() calling sender doStop()............................");
		        sender.doStop();
		      } else {
				    System.out.println("BaseUIMAAsynchronousEngine.stop() sender is NULL............................");
		      }
			  try {
//				  System.out.println(this.getClass().getName()+".stop() - Stopping UIMA-AS Client");
//				stopConnection();
				// Undeploy all containers
				undeploy();
		 	    clientCache.clear();
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(
								Level.INFO)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO,
									CLASS_NAME.getName(), "stop",
									JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
									"UIMAJMS_undeployed_containers__INFO");
				}
				// unregister client
				if (jmxManager != null) {
					jmxManager.unregisterMBean(clientJmxObjectName);
					jmxManager.destroy();
				}
			  } catch (Exception e) {
				if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(
								Level.WARNING)) {
					UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING,
									CLASS_NAME.getName(), "stop",
									JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
									"UIMAJMS_exception__WARNING", e);
				}
			  }
				
	//		}
		} catch( Exception e) {
			e.printStackTrace();
		} 
	}

  public void setCPCMessage(Message msg) throws Exception {
    msg.setStringProperty(AsynchAEMessage.MessageFrom, consumerDestination.getQueueName());
    msg.setStringProperty(UIMAMessage.ServerURI, brokerURI);
    msg.setIntProperty(AsynchAEMessage.MessageType, AsynchAEMessage.Request);
    msg.setIntProperty(AsynchAEMessage.Command, AsynchAEMessage.CollectionProcessComplete);
    msg.setIntProperty(AsynchAEMessage.Payload, AsynchAEMessage.None);
    msg.setBooleanProperty(AsynchAEMessage.RemoveEndpoint, true);
    msg.setJMSReplyTo(consumerDestination);
    if (msg instanceof TextMessage) {
      ((TextMessage) msg).setText("");
    }
  }

	private boolean connectionClosedOrInvalid() {
			SharedConnection sharedConnection = lookupConnection(brokerURI);
			if (sharedConnection == null
					|| sharedConnection.getConnection() == null
					|| ((ActiveMQConnection) sharedConnection.getConnection())
							.isClosed()
					|| ((ActiveMQConnection) sharedConnection.getConnection())
							.isClosing()
					|| ((ActiveMQConnection) sharedConnection.getConnection())
							.isTransportFailed()) {
				return true;
			}
		return false;
	}

	private SharedConnection createAndInitializeAMQConnection( Semaphore semaphore, String aBrokerURI) throws Exception {
		if ( stopped ) {
			return null;
		}
		// This only effects Consumer
		// Create AMQ specific connection validator. It uses
		// AMQ specific approach to test the state of the connection
		ActiveMQConnectionValidator connectionValidator = new ActiveMQConnectionValidator();
		//Initalize the connection Factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(aBrokerURI);
		connectionFactory.setTrustAllPackages(true);
		connectionFactory.setUserName(amqUser);
		connectionFactory.setPassword(amqPassword);
		// Create a singleton shared connection object
		SharedConnection sharedConnection = sharedConnections.get(aBrokerURI);
		sharedConnection.setConnectionFactory(connectionFactory);
/*
		new SharedConnection(
				connectionFactory,
				//new ActiveMQConnectionFactory(aBrokerURI),
				aBrokerURI);
		sharedConnection.setSemaphore(semaphore);
	*/
		// Add AMQ specific connection validator
		sharedConnection
				.setConnectionValidator(connectionValidator);
		// Connect to broker. Throws exception if unable to connect
		sharedConnection.create();
		addPrefetch((ActiveMQConnection) sharedConnection
				.getConnection());
		((ActiveMQConnection) sharedConnection.getConnection()).setExceptionListener(new ClientExceptionListener());
		//System.out.println(">>>>>>>>>>>>>>>> Starting Connection to Broker:"+aBrokerURI);
		sharedConnection.start();
		sharedConnections.put( aBrokerURI, sharedConnection);

		if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(
				Level.INFO)) {
			UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO,
					CLASS_NAME.getName(), "createAndInitializeAMQConnection",
					JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
					"UIMAJMS_client_connection_setup_INFO",
					new Object[] { aBrokerURI });
		}
		return sharedConnection;
	}
	protected SharedConnection createSharedConnection(String aBrokerURI) throws Exception {
		Semaphore perBrokerSemaphore;
		SharedConnection sharedConnection;
		try {
			// Use global sharedConnectionSemaphore to lock the code below to 
			// prevent two threads from creating per broker semaphore in case 
			// sharedConnection equals null. 
			sharedConnectionSemaphore.acquire();
			// fetch shared connection object for a given broker URI
			sharedConnection = sharedConnections.get(aBrokerURI);
			// if null, we need create create a new one and start the AMQ connection.
			// Each shared connection has its own binary semaphore to guard critical
			// code.
			if ( sharedConnection == null  ) {
				// create dedicated semaphore for this broker
				perBrokerSemaphore = new Semaphore(1);
				sharedConnection = new SharedConnection(null,aBrokerURI);
				sharedConnection.setSemaphore(perBrokerSemaphore);
				sharedConnections.put( aBrokerURI, sharedConnection);
				
				//sharedConnection = createAndInitializeAMQConnection(perBrokerSemaphore, aBrokerURI);
		    } else {
		    	// fetch dedicated semaphore from shared connectiion object
		    	perBrokerSemaphore = sharedConnection.getSemaphore();
		    }

		} catch( Exception e) {
			throw e;
		} finally {
			sharedConnectionSemaphore.release();
		}
		try {
			// Used broker specific semaphore to lock critical code 
			if ( !perBrokerSemaphore.tryAcquire(2, TimeUnit.SECONDS) ) {
				throw new TimeoutException("UIMA-AS Client Timed Out Waiting to Acquire Broker Semaphore (2 Seconds) - Broker:"+aBrokerURI);
			}
			
		} catch( Exception e) {
			throw e;
		}
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> THREAD:"+Thread.currentThread().getId()+" LOCK: "+aBrokerURI);
		try {
			// check if AMQ connection is still valid
			if ( connectionClosedOrInvalid() ) {
				// connection either stale or not present. Create new shared connection object for this broker
				sharedConnection = createAndInitializeAMQConnection(perBrokerSemaphore, aBrokerURI);
			}
			// Create and initialize dispatch thread with JMS Producer
			initializeProducer(brokerURI, endpoint, sharedConnection.getConnection());
            // Create JMS Consumer and plug in Listener for processing replies
			initializeConsumer(brokerURI, sharedConnection.getConnection());

		    // Increment number of client instances. SharedConnection object is a static
		    // and is used to share a single JMS connection. The connection is closed
		    // when the last client finishes processing and calls stop().
		    if (sharedConnection != null) {
		       sharedConnection.registerClient(this);
		    }
		} catch( Exception e) {
			throw e;
		} finally {
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  THREAD:"+Thread.currentThread().getId()+" UN-LOCK: "+aBrokerURI);

			perBrokerSemaphore.release();
		}
		return sharedConnection;
	}

  private void addPrefetch(ActiveMQConnection aConnection) {
    ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
    prefetchPolicy.setQueuePrefetch(5);
    ((ActiveMQConnection) aConnection).setPrefetchPolicy(prefetchPolicy);
  }

  protected SharedConnection validateConnection(String aBrokerURI) throws Exception {
    // checks if a sharedConnection exists and if not creates a new one
    return createSharedConnection(aBrokerURI);
  }

  protected Session getSession(String aBrokerURI) throws Exception {
	SharedConnection sharedConnection = validateConnection(aBrokerURI);
    return getSession(sharedConnection.getConnection());
  }

  protected Session getSession(Connection aConnection) throws Exception {
    session = aConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    return session;
  }

  protected MessageProducer lookupProducerForEndpoint(Endpoint anEndpoint) throws Exception {
    if (lookupConnection(brokerURI) == null || producerSession == null) {
      throw new ResourceInitializationException();
    }
    Destination dest = producerSession.createQueue(anEndpoint.getEndpoint());
    return producerSession.createProducer(dest);
  }

  protected void initializeProducer(String aBrokerURI, String aQueueName) throws Exception {
    // Check if a sharedConnection exists. If not it creates one
    SharedConnection sharedConnection = createSharedConnection(aBrokerURI);
    initializeProducer(aBrokerURI, aQueueName, sharedConnection.getConnection());
  }

  protected void initializeProducer(String aBrokerURI, String aQueueName, Connection aConnection)
          throws Exception {
    if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
      UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
              "initializeProducer", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
              "UIMAJMS_init_jms_producer_INFO", new Object[] { aBrokerURI, aQueueName });
    }
    brokerURI = aBrokerURI;
    // Create a worker thread for sending messages. Jms sessions are single threaded
    // and it is illegal (per JMS spec) to use the same sesssion from multiple threads.
    // The worker thread solves this problem. As it is the only thread that owns the
    // session and uses it to create message producer.
    // The worker thread blocks waiting for messages from application threads. The
    // application threads add messages to the shared "queue" (in-memory queue not
    // jms queue) and the worker thread consumes them. The worker thread is not
    // serialializing CASes. This work is done in application threads.

    // create a Message Dispatcher object. In its constructor it acquires a shared
    // semaphore producerSemaphore and holds it until the producer is created an
    // and initialized. Once this happens or there is an error, the semaphore is
    // released.
    sender = new ActiveMQMessageSender(aConnection, aQueueName, this);
    producerInitialized = false;
    Thread t = new Thread((BaseMessageSender) sender);
    // Start the worker thread. The jms session and message producer are created. Once
    // the message producer is created, the worker thread notifies this thread by
    // calling onProducerInitialized() where the global flag 'producerInitialized' is
    // set to true. After the notification, the worker thread notifies this instance
    // that the producer is fully initialized and finally begins to wait for messages
    // in pendingMessageList. Upon arrival, each message is removed from
    // pendingMessageList and it is sent to a destination.

    t.start();

    try {
      // Block waiting for the Sender to complete initializing the Producer.
      // The sender will release the lock once it instantiates and initializes
      // the Producer object or if there is an error
      producerSemaphore.acquire();
    } catch (InterruptedException ex) {

    } finally {
      producerSemaphore.release();
    }
    // Check if the worker thread failed to initialize.
    if (sender.failed()) {
      // Worker thread failed to initialize. Log the reason and stop the uima ee client
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                "initializeProducer", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                "UIMAJMS_worker_thread_failed_to_initialize__WARNING",
                new Object[] { sender.getReasonForFailure() });
      }
      stop();
      return;
    }
  }

  /**
   * Create a JMS Consumer on a temporary queue. Service replies will be handled by this consumer.
   * 
   * @param aBrokerURI
   * @throws Exception
   */
  protected void initializeConsumer(String aBrokerURI) throws Exception {
	  SharedConnection  sharedConnection = createSharedConnection(aBrokerURI);
    initializeConsumer(aBrokerURI, sharedConnection.getConnection());
  }

  protected void initializeConsumer(String aBrokerURI, Connection connection) throws Exception {
    // In case we are recovering from a bad broker connection, invalidate old
    // JMS objects first.
    if ( consumerSession != null ) {
      try {
        consumer.close();
        consumerSession.close();
      } catch( Exception e) {
        //  ignore, creating a new Session below
      }
    }
    
    consumerSession = getSession(connection);
    consumerDestination = consumerSession.createTemporaryQueue();
    if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
      UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
              "initializeConsumer", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
              "UIMAJMS_init_jms_consumer_INFO",
              new Object[] { aBrokerURI, consumerDestination.getQueueName() });
    }
    consumer = consumerSession.createConsumer(consumerDestination);
    consumer.setMessageListener(this);
  }
  
  /**
   * Replaces place holder with syntax ${pname} with a system property whose name is pname
   * 
   * @param aPlaceholder to resolve
   * @return - the actual broker URL
   * @throws ResourceInitializationException
   */
  private String replacePlaceholder(String aPlaceholder) throws ResourceInitializationException {
    //  find placeholder starting and ending positions
    // If no '${' or following '}' leave as-is
    int startPos = aPlaceholder.indexOf("${");
    if (startPos < 0) {
      return aPlaceholder;
    }
    int endPos = aPlaceholder.indexOf("}", startPos);
    if (endPos < 0) {
      return aPlaceholder;
    }
    //  extract the name
    String placeholder = aPlaceholder.substring(startPos+2, endPos);
    //  using the name, find the broker URL. This property must exist or exception is thrown
    String url = System.getProperty(placeholder);
    //  the property is missing 
    if ( url == null ) {
      throw new ResourceInitializationException(new Exception("UIMA AS Client Initialization Exception. Value for placeholder:"+placeholder+" is not defined in the system properties."));
    }
    return aPlaceholder.substring(0, startPos) + url + aPlaceholder.substring(endPos+1);
  }
  /**
   * Initialize the uima ee client. Takes initialization parameters from the
   * <code>anApplicationContext</code> map.
   */
  public synchronized void initialize(Map anApplicationContext)
          throws ResourceInitializationException {
    // Add ShutdownHook to make sure the connection to the
    // broker is always closed on process exit.
    shutdownHookThread = new Thread(new UimaASShutdownHook(this));
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);
           
    // throws an exception if verions of UIMA-AS is not compatible with UIMA SDK
    VersionCompatibilityChecker.check(CLASS_NAME, "UIMA AS Client", "initialize");

    if (running) {
      throw new ResourceInitializationException(new UIMA_IllegalStateException());
    }
    reset();
    Properties performanceTuningSettings = null;

    if (!anApplicationContext.containsKey(UimaAsynchronousEngine.ServerUri)) {
      throw new ResourceInitializationException();
    }
    if (!anApplicationContext.containsKey(UimaAsynchronousEngine.ENDPOINT)) {
      throw new ResourceInitializationException();
    }
    ResourceManager rm = null;
    if (anApplicationContext.containsKey(Resource.PARAM_RESOURCE_MANAGER)) {
      rm = (ResourceManager) anApplicationContext.get(Resource.PARAM_RESOURCE_MANAGER);
    } else {
      rm = UIMAFramework.newDefaultResourceManager();
    }
    performanceTuningSettings = new Properties();
    if (anApplicationContext.containsKey(UIMAFramework.CAS_INITIAL_HEAP_SIZE)) {
      String cas_initial_heap_size = (String) anApplicationContext
              .get(UIMAFramework.CAS_INITIAL_HEAP_SIZE);
      performanceTuningSettings.put(UIMAFramework.CAS_INITIAL_HEAP_SIZE, cas_initial_heap_size);
    }
    asynchManager = new AsynchAECasManager_impl(rm);

    brokerURI = (String) anApplicationContext.get(UimaAsynchronousEngine.ServerUri);
    endpoint = (String) anApplicationContext.get(UimaAsynchronousEngine.ENDPOINT);
    
    //  Check if a placeholder is passed in instead of actual broker URL or endpoint. 
    //  The placeholder has the syntax ${placeholderName} and may be imbedded in text.
    //  A system property with placeholderName must exist for successful placeholder resolution.
    //  Throws ResourceInitializationException if placeholder is not in the System properties.
    brokerURI = replacePlaceholder(brokerURI); 
    endpoint = replacePlaceholder(endpoint); 

    clientSideJmxStats.setEndpointName(endpoint);
    int casPoolSize = 1;

    if (anApplicationContext.containsKey(UimaAsynchronousEngine.CasPoolSize)) {
      casPoolSize = ((Integer) anApplicationContext.get(UimaAsynchronousEngine.CasPoolSize))
              .intValue();
      clientSideJmxStats.setCasPoolSize(casPoolSize);
    }
    if ( anApplicationContext.containsKey(UimaAsynchronousEngine.TargetSelectorProperty) ) {
        serviceTargetSelector = 
        		(String)anApplicationContext.get(UimaAsynchronousEngine.TargetSelectorProperty);
    }
    
    if (anApplicationContext.containsKey(UimaAsynchronousEngine.Timeout)) {
      processTimeout = ((Integer) anApplicationContext.get(UimaAsynchronousEngine.Timeout))
              .intValue();
    }

    if (anApplicationContext.containsKey(UimaAsynchronousEngine.GetMetaTimeout)) {
      metadataTimeout = ((Integer) anApplicationContext.get(UimaAsynchronousEngine.GetMetaTimeout))
              .intValue();
    }

    if (anApplicationContext.containsKey(UimaAsynchronousEngine.CpcTimeout)) {
      cpcTimeout = ((Integer) anApplicationContext.get(UimaAsynchronousEngine.CpcTimeout))
              .intValue();
    }
    if (anApplicationContext.containsKey(UimaAsynchronousEngine.ApplicationName)) {
      applicationName = (String) anApplicationContext.get(UimaAsynchronousEngine.ApplicationName);
    }
    if (anApplicationContext.containsKey(UimaAsynchronousEngine.SERIALIZATION_STRATEGY)) {
      final String serializationStrategy = (String) anApplicationContext.get(UimaAsynchronousEngine.SERIALIZATION_STRATEGY);
      // change this to support compressed filitered as the default
      setSerialFormat((serializationStrategy.equalsIgnoreCase("xmi")) ? SerialFormat.XMI : SerialFormat.BINARY);
//      setSerialFormat((serializationStrategy.equalsIgnoreCase("xmi")) ? SerialFormat.XMI : SerialFormat.COMPRESSED_FILTERED);
      clientSideJmxStats.setSerialization(getSerialFormat());
    }
    if (anApplicationContext.containsKey(UimaAsynchronousEngine.userName)) {
        amqUser = (String) anApplicationContext
                .get(UimaAsynchronousEngine.userName);
    }
    if (anApplicationContext.containsKey(UimaAsynchronousEngine.password)) {
    	amqPassword = (String) anApplicationContext
    	.get(UimaAsynchronousEngine.password);
    }

    if (anApplicationContext.containsKey(UimaAsynchronousEngine.TimerPerCAS)) {
        timerPerCAS = ((Boolean) anApplicationContext.get(UimaAsynchronousEngine.TimerPerCAS))
                .booleanValue();
      }
    if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.CONFIG)) {
      UIMAFramework.getLogger(CLASS_NAME)
              .logrb(
                      Level.CONFIG,
                      CLASS_NAME.getName(),
                      "initialize",
                      JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                      "UIMAJMS_init_uimaee_client__CONFIG",
                      new Object[] { brokerURI, 0, casPoolSize, processTimeout, metadataTimeout,
                          cpcTimeout,timerPerCAS });
    }
    super.serviceDelegate = new ClientServiceDelegate(endpoint, applicationName, this);
    super.serviceDelegate.setCasProcessTimeout(processTimeout);
    super.serviceDelegate.setGetMetaTimeout(metadataTimeout);
    try {
      // Generate unique identifier
      String uuid = UUIDGenerator.generate();
      // JMX does not allow ':' in the ObjectName so replace these with underscore
      uuid = uuid.replaceAll(":", "_");
      uuid = uuid.replaceAll("-", "_");
      applicationName += "_" + uuid;
      jmxManager = new JmxManager("org.apache.uima");
      clientSideJmxStats.setApplicationName(applicationName);
      clientJmxObjectName = new ObjectName("org.apache.uima:name=" + applicationName);
      jmxManager.registerMBean(clientSideJmxStats, clientJmxObjectName);

      // Check if sharedConnection exists. If not create a new one. The sharedConnection
      // is static and shared by all instances of UIMA AS client in a jvm. The check
      // is made in a critical section by first acquiring a global static semaphore to
      // prevent a race condition.
      createSharedConnection(brokerURI);
      running = true;
      //  This is done to give the broker enough time to 'finalize' creation of
      //  temp reply queue. It's been observed (on MAC OS only) that AMQ
      //  broker QueueSession.createTemporaryQueue() call is not synchronous. Meaning,
      //  return from createTemporaryQueue() does not guarantee immediate availability
      //  of the temp queue. It seems like this operation is asynchronous, causing: 
      //  "InvalidDestinationException: Cannot publish to a deleted Destination..."
      //  on the service side when it tries to reply to the client.
      try {
        wait(100);
      } catch( InterruptedException e) {}
      sendMetaRequest();
      waitForMetadataReply();
      if (abort || !running) {
        if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                  "initialize", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                  "UIMAJMS_aborting_as_WARNING", new Object[] { "Metadata Timeout" });
        }
        throw new ResourceInitializationException(new UimaASMetaRequestTimeout());
      } else {
        if (collectionReader != null) {
          asynchManager.addMetadata(collectionReader.getProcessingResourceMetaData());
        }

        asynchManager.initialize(casPoolSize, "ApplicationCasPoolContext",
                performanceTuningSettings);

        // Create a special CasPool of size 1 to be used for deserializing CASes from a Cas
        // Multiplier
        if (super.resourceMetadata != null
                && super.resourceMetadata instanceof AnalysisEngineMetaData) {
          if (((AnalysisEngineMetaData) super.resourceMetadata).getOperationalProperties()
                  .getOutputsNewCASes()) {
            // Create a Shadow CAS Pool used to de-serialize CASes produced by a CAS Multiplier
            asynchManager.initialize(1, SHADOW_CAS_POOL, performanceTuningSettings);
          }
        }
        initialized = true;
        remoteService = true;
        // running = true;

        for (int i = 0; listeners != null && i < listeners.size(); i++) {
          ((UimaASStatusCallbackListener) listeners.get(i)).initializationComplete(null);
        }
      }

    } catch (ResourceInitializationException e) {
      state = ClientState.FAILED;
      notifyOnInitializationFailure(e);
      throw e;
    } catch (Exception e) {
      state = ClientState.FAILED;
      notifyOnInitializationFailure(e);
      throw new ResourceInitializationException(e);
    }
    if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
      UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(), "initialize",
              JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_as_initialized__INFO",
              new Object[] { UimaAsynchronousEngine.SERIALIZATION_STRATEGY });
    }
    // Acquire cpcReady semaphore to block sending CPC request until
    // ALL outstanding CASes are received.
    super.acquireCpcReadySemaphore();
    state = ClientState.RUNNING;
  }

  /**
   * First generates a Spring context from a given deploy descriptor and than deploys the context
   * into a Spring Container.
   * 
   * @param aDeploymentDescriptor
   *          - deployment descriptor to generate Spring Context from
   * @param anApplicationContext
   *          - a Map containing properties required by dd2spring
   * 
   * @return - a unique spring container id
   * 
   */
  public String deploy(String aDeploymentDescriptor, Map anApplicationContext) throws Exception {
	  String springContext = null;
	  try {
		springContext = generateSpringContext(aDeploymentDescriptor, anApplicationContext);

        SpringContainerDeployer springDeployer = new SpringContainerDeployer(springContainerRegistry, this);

    	String id = springDeployer.deploy(springContext);
      if ( springDeployer.isInitialized() ) {
        springDeployer.startListeners();
      }
      return id;
    } catch (Exception e) {
      running = true;
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                  "main", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                  "UIMAJMS_exception__WARNING", e);
      }

      throw e;
    } finally {
	  String uimaAsDebug = (String) anApplicationContext.get(UimaAsynchronousEngine.UimaEeDebug);
	  if ( springContext != null && (null == uimaAsDebug || uimaAsDebug.equals("") ) ) {
           disposeContextFiles(springContext);
	  }
    }
  }
  private void disposeContextFiles(String ...contextFiles) {
    for( String contextFile: contextFiles) {
      File file = new File(contextFile);
      if ( file.exists()) {
        file.delete();
      }
    }
  }
  /**
	 * 
	 */
  public String deploy(String[] aDeploymentDescriptorList, Map anApplicationContext)
          throws Exception {
    if (aDeploymentDescriptorList == null) {
      throw new ResourceConfigurationException(UIMA_IllegalArgumentException.ILLEGAL_ARGUMENT,
              new Object[] { "Null", "DeploymentDescriptorList", "deploy()" });
    }

    if (aDeploymentDescriptorList.length == 0) {
      throw new ResourceConfigurationException(
              ResourceConfigurationException.MANDATORY_VALUE_MISSING,
              new Object[] { "DeploymentDescriptorList" });
    }
    String[] springContextFiles = new String[aDeploymentDescriptorList.length];

    for (int i = 0; i < aDeploymentDescriptorList.length; i++) {
      springContextFiles[i] = generateSpringContext(aDeploymentDescriptorList[i],
              anApplicationContext);
    }

    SpringContainerDeployer springDeployer = new SpringContainerDeployer(springContainerRegistry);
    try {
      String id = springDeployer.deploy(springContextFiles);
      if ( springDeployer.isInitialized() ) {
        springDeployer.startListeners();
      }
      return id;
    } catch (ResourceInitializationException e) {
      running = true;
      throw e;
    } finally {
	String uimaAsDebug = (String) anApplicationContext.get(UimaAsynchronousEngine.UimaEeDebug);
	if ( null == uimaAsDebug || uimaAsDebug.equals("")) {
           disposeContextFiles(springContextFiles);
	}

	//      disposeContextFiles(springContextFiles);
    }

  }

  public void undeploy() throws Exception {
    Iterator containerIterator = springContainerRegistry.keySet().iterator();
    if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
        String msg ="undeploying "+springContainerRegistry.size()+" Containers";
        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "undeploy",
               JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
                new Object[] { msg });
    }
    while (containerIterator.hasNext()) {
      String containerId = (String) containerIterator.next();
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
          String msg = "Undeploying Container Id:"+containerId;
          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "undeploy",
                 JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
                  new Object[] { msg });
      }
      undeploy(containerId);
    }
  }

  public void undeploy(String aSpringContainerId) throws Exception {
    this.undeploy(aSpringContainerId, SpringContainerDeployer.STOP_NOW);
  }

  /**
   * Undeploys Spring container with a given container Id. All deployed Spring containers are
   * registered in the local registry under a unique id.
   * 
   */
  public void undeploy(String aSpringContainerId, int stop_level) throws Exception {
    if (aSpringContainerId == null  ) {
      return;
    }
    
    UimaEEAdminSpringContext adminContext = null;
    if (!springContainerRegistry.containsKey(aSpringContainerId)) {
        return;
        // throw new InvalidContainerException("Invalid Spring container Id:" + aSpringContainerId +
        // ". Unable to undeploy the Spring container");
      }
      // Fetch an administrative context which contains a Spring Container
      adminContext = (UimaEEAdminSpringContext) springContainerRegistry.get(aSpringContainerId);
      if (adminContext == null) {
        throw new InvalidContainerException(
                "Spring Container Does Not Contain Valid UimaEEAdminSpringContext Object");
      }
      // Fetch instance of the Container from its context
      ApplicationContext ctx = adminContext.getSpringContainer();
      // Query the container for objects that implement
      // ControllerLifecycle interface. These
      // objects are typically of type AnalysisEngineController or
      // UimacppServiceController.
      String[] asyncServiceList = ctx
              .getBeanNamesForType(org.apache.uima.aae.controller.ControllerLifecycle.class);
      // Given a valid list of controllers select the first from the list
      // and
      // initiate a shutdown. We don't care which controller will be
      // invoked. In case of
      // AggregateAnalysisEngineController the terminate event will
      // propagate all the way
      // to the top controller in the hierarchy and the shutdown will take
      // place from there.
      // If the controller is of kind UimecppServiceController or
      // PrimitiveAnalysisController
      // the termination logic will be immediately triggered in the
      // terminate() method.
      if (asyncServiceList != null && asyncServiceList.length > 0) {
        boolean topLevelController = false;
        ControllerLifecycle ctrer = null;
        int indx = 0;
        while (!topLevelController) {
          ctrer = (ControllerLifecycle) ctx.getBean(asyncServiceList[indx++]);
          if (ctrer instanceof UimacppServiceController
                  || ((AnalysisEngineController) ctrer).isTopLevelComponent()) {
            topLevelController = true;
          }
        }
        // Send a trigger to initiate shutdown.
        if (ctrer != null) {
          if (ctrer instanceof AnalysisEngineController &&
        		  ((AnalysisEngineController) ctrer).getControllerLatch() != null ) {
            ((AnalysisEngineController) ctrer).getControllerLatch().release();
          }
          if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
              String msg = "++++++++++++++++++++++ calling terminate()-service:"+((AnalysisEngineController) ctrer).getComponentName();
              UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "undeploy",
                     JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
                      new Object[] { msg });
          }
          switch (stop_level) {
            case SpringContainerDeployer.QUIESCE_AND_STOP:
                if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
                    String msg = "++++++++++++++++++++++ calling quiesceAndStop()";
                    UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "undeploy",
                           JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
                            new Object[] { msg });
                }
              ((AnalysisEngineController) ctrer).quiesceAndStop();

              break;
            case SpringContainerDeployer.STOP_NOW:
                if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
                    String msg = "++++++++++++++++++++++ calling terminate()";
                    UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "undeploy",
                           JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
                            new Object[] { msg });
                }
              ((AnalysisEngineController) ctrer).terminate();
              break;
          }
        }
      }
      if (ctx instanceof FileSystemXmlApplicationContext) {
        ((FileSystemXmlApplicationContext) ctx).destroy();
        if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.FINEST)) {
            String msg = "---------------------- Destroying Application Context:"+((FileSystemXmlApplicationContext) ctx).getApplicationName();
            UIMAFramework.getLogger(CLASS_NAME).logrb(Level.FINEST, CLASS_NAME.getName(), "undeploy",
                   JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMAJMS_debug_msg__FINEST",
                    new Object[] { msg });
        }
      }
      // Remove the container from a local registry
      springContainerRegistry.remove(aSpringContainerId);
  }

  /**
   * Use dd2spring to generate Spring context file from a given deployment descriptor file.
   * 
   * @param aDeploymentDescriptor
   *          - deployment descriptor to generate Spring Context from
   * @param anApplicationContext
   *          - a Map containing properties required by dd2spring
   * @return - an absolute path to the generated Spring Context file
   * 
   * @throws Exception
   *           - if failure occurs
   */
  private String generateSpringContext(String aDeploymentDescriptor, Map anApplicationContext)
          throws Exception {

    String dd2SpringXsltFilePath = null;
    String saxonClasspath = null;

    if (anApplicationContext.containsKey(UimaAsynchronousEngine.DD2SpringXsltFilePath)) {
      dd2SpringXsltFilePath = (String) anApplicationContext
              .get(UimaAsynchronousEngine.DD2SpringXsltFilePath);
    } else {
      throw new ResourceConfigurationException(
              ResourceConfigurationException.MANDATORY_VALUE_MISSING,
              new Object[] { "Xslt File Path" });
    }
    if (anApplicationContext.containsKey(UimaAsynchronousEngine.SaxonClasspath)) {
      saxonClasspath = (String) anApplicationContext.get(UimaAsynchronousEngine.SaxonClasspath);
    } else {
      throw new ResourceConfigurationException(
              ResourceConfigurationException.MANDATORY_VALUE_MISSING,
              new Object[] { "Saxon Classpath" });
    }

    Dd2spring dd2Spring = new Dd2spring();
    File springContextFile = dd2Spring.convertDd2Spring(aDeploymentDescriptor,
            dd2SpringXsltFilePath, saxonClasspath, (String) anApplicationContext
                    .get(UimaAsynchronousEngine.UimaEeDebug));

    return springContextFile.getAbsolutePath();
  }

  /**
   * Deploys provided context files ( and beans) in a new Spring container.
   * 
   */
  protected String deploySpringContainer(String[] springContextFiles)
          throws ResourceInitializationException {

    SpringContainerDeployer springDeployer = new SpringContainerDeployer(this);
    try {
      return springDeployer.deploy(springContextFiles);
    } catch (ResourceInitializationException e) {
      // turn on the global flag so that the stop() can do the cleanup
      running = true;
      throw e;
    }
  }

  protected void waitForServiceNotification() throws Exception {
    while (!serviceInitializationCompleted) {
      if (serviceInitializationException) {
        throw new ResourceInitializationException();
      }
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
                "waitForServiceNotification", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                "UIMAJMS_awaiting_container_init__INFO", new Object[] {});
      }
      try {
        serviceSemaphore.acquire();
      } catch (InterruptedException e) {
      } finally {
        serviceSemaphore.release();
      }
      if (serviceInitializationException) {
        throw new ResourceInitializationException();
      }
    }
  }

  protected void deployEmbeddedBroker() throws Exception {
    // TBI
  }

  public static void main(String[] args) {
    try {

      BaseUIMAAsynchronousEngineCommon_impl uimaee = new BaseUIMAAsynchronousEngine_impl();

      Map appContext = new HashMap();
      appContext.put(UimaAsynchronousEngine.DD2SpringXsltFilePath, args[1]);
      appContext.put(UimaAsynchronousEngine.SaxonClasspath, args[2]);
      String containerId = uimaee.deploy(args[0], appContext); // args[1],
      // args[2]);

      uimaee.undeploy(containerId);
    } catch (Exception e) {
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                "main", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                "UIMAJMS_exception__WARNING", e);
      }
    }
  }

  public void setReleaseCASMessage(TextMessage msg, String aCasReferenceId) throws Exception {
    msg.setIntProperty(AsynchAEMessage.Payload, AsynchAEMessage.None);
    msg.setStringProperty(AsynchAEMessage.CasReference, aCasReferenceId);
    msg.setIntProperty(AsynchAEMessage.MessageType, AsynchAEMessage.Request);
    msg.setIntProperty(AsynchAEMessage.Command, AsynchAEMessage.ReleaseCAS);
    msg.setStringProperty(UIMAMessage.ServerURI, brokerURI);
    msg.setJMSReplyTo(consumerDestination);
  }

  public void notifyOnInitializationFailure(Exception e) {
    notifyOnInitializationFailure(null, e);
  }

  public void notifyOnInitializationSuccess() {
    notifyOnInitializationSuccess(null);
  }

  public void notifyOnInitializationFailure(AnalysisEngineController aController, Exception e) {

    // Initialization exception. Notify blocking thread and indicate a problem
    serviceInitializationException = true;
    if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
      UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
              "notifyOnInitializationFailure", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
              "UIMAJMS_container_init_exception__WARNING", e);
    }
    serviceSemaphore.release();
  }

  public void notifyOnInitializationSuccess(AnalysisEngineController aController) {
    serviceInitializationCompleted = true;
    serviceSemaphore.release();
  }

  

  public void notifyOnTermination(String aServiceName, EventTrigger cause) {
    for (int i = 0; listeners != null && i < listeners.size(); i++) {
        UimaAsBaseCallbackListener statCL = (UimaAsBaseCallbackListener) listeners.get(i);
        statCL.onUimaAsServiceExit(cause);
    }
  }

  public void notifyOnTermination(String aServiceName, String aCasReferenceId, Exception cause) {
//    super.n
  }

  protected MessageProducer getMessageProducer(Destination destination) throws Exception {
    return sender.getMessageProducer(destination);
  }

  /**
   * Request Uima AS client to initiate sending Stop requests to a service for all outstanding CASes
   * awaiting reply.
   * 
   */
  public void stopProducingCases() {
    
    String[] casIdsPendingReply = serviceDelegate.getDelegateCasIdsPendingReply();
    if ( casIdsPendingReply != null && casIdsPendingReply.length > 0 ) {
      for( String casReferenceId : casIdsPendingReply ) {
        // The Cas is still being processed
        ClientRequest clientCachedRequest = (ClientRequest) clientCache
                .get(casReferenceId);
        if (clientCachedRequest != null && !clientCachedRequest.isMetaRequest()
                && clientCachedRequest.getCasReferenceId() != null) {
          stopProducingCases(casReferenceId, clientCachedRequest.getFreeCasNotificationQueue());
        } 
      }
    }
  }

  /**
   * Request Uima AS client to initiate sending Stop request to a service for a given CAS id If the
   * service is a Cas Multiplier, it will stop producing new CASes, will wait until all child CASes
   * finish and finally returns the input CAS.
   * 
   */
  public void stopProducingCases(String aCasReferenceId) {
    // The Cas is still being processed
    ClientRequest clientCachedRequest = (ClientRequest) clientCache.get(aCasReferenceId);
    if (clientCachedRequest != null) {
      stopProducingCases(aCasReferenceId, clientCachedRequest.getFreeCasNotificationQueue());
    }
  }

//  private void stopProducingCases(ClientRequest clientCachedRequest) {
  private void stopProducingCases(String casReferenceId, Destination cmFreeCasQueue) {
    try {
//      if (clientCachedRequest.getFreeCasNotificationQueue() != null) {
      if (cmFreeCasQueue != null) {
        TextMessage msg = createTextMessage();
        msg.setText("");
        msg.setIntProperty(AsynchAEMessage.Payload, AsynchAEMessage.None);
//        msg.setStringProperty(AsynchAEMessage.CasReference, clientCachedRequest.getCasReferenceId());
        msg.setStringProperty(AsynchAEMessage.CasReference, casReferenceId);
        msg.setIntProperty(AsynchAEMessage.MessageType, AsynchAEMessage.Request);
        msg.setIntProperty(AsynchAEMessage.Command, AsynchAEMessage.Stop);
        msg.setStringProperty(UIMAMessage.ServerURI, brokerURI);
        try {
          MessageProducer msgProducer = getMessageProducer(cmFreeCasQueue);
          if (msgProducer != null) {
        	  
              if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.INFO)) {
                  UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, CLASS_NAME.getName(),
                          "stopProducingCases", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                          "UIMAJMS_client_sending_stop_to_service__INFO", new Object[] {casReferenceId,cmFreeCasQueue});
              }
            // Send STOP message to Cas Multiplier Service
            msgProducer.send(msg);
          } else {
              if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
                  UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                          "stopProducingCases", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                          "UIMAJMS_client_unable_to_send_stop_to_cm__WARNING");
              }
          }

        } catch (Exception ex) {
          if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
            UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                    "stopProducingCases", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                    "UIMAJMS_exception__WARNING",
                    ex);
          }
        }
      }
    } catch (Exception e) {
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                "stopProducingCases", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                "UIMAJMS_exception__WARNING", e);
      }
    }
  }
  protected MessageSender getDispatcher() {
    return sender;
  }
  public void notifyOnReconnecting(String aMessage) {
  
  }
  
  public void notifyOnReconnectionSuccess() {
    
  }


  public void onApplicationEvent(ApplicationEvent event) {
	for (int i = 0; listeners != null && i < listeners.size(); i++) {
	    UimaAsBaseCallbackListener statCL = (UimaAsBaseCallbackListener) listeners.get(i);
	    if ( event instanceof UimaASApplicationExitEvent) {
		   statCL.onUimaAsServiceExit( ((UimaASApplicationExitEvent)event).getEventTrigger());
	    }
    }
  }
  public class ClientExceptionListener implements ExceptionListener {

	@Override
	public void onException(JMSException arg0) {
		arg0.printStackTrace();
		
	}
	  
  }
}
