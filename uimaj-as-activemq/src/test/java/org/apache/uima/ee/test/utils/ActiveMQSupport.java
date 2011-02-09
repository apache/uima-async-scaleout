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

package org.apache.uima.ee.test.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.apache.uima.UIMAFramework;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.util.Level;

public class ActiveMQSupport extends TestCase {
  private static final Class CLASS_NAME = ActiveMQSupport.class;

  protected static BrokerService broker;

  protected String uri = null;

  protected static ThreadGroup brokerThreadGroup = null;

  protected static TransportConnector tcpConnector = null;

  protected static final String relativePath = "src" + System.getProperty("file.separator")
          + "test" + System.getProperty("file.separator") + "resources"
          + System.getProperty("file.separator") + "deployment";

  protected static final String relativeDataPath = "src" + System.getProperty("file.separator")
          + "test" + System.getProperty("file.separator") + "resources"
          + System.getProperty("file.separator") + "data";

  private static Thread brokerThread = null;

  protected static TransportConnector httpConnector = null;

  public static Semaphore brokerSemaphore = new Semaphore(1);

  protected synchronized void setUp() throws Exception {
    System.out.println("\nSetting Up New Test - Thread Id:" + Thread.currentThread().getId());
    super.setUp();
    if (brokerThreadGroup == null) {
      brokerThreadGroup = new ThreadGroup("BrokerThreadGroup");

      // Acquire a semaphore to force this thread to wait until the broker
      // starts and initializes
      brokerSemaphore.acquire();

      brokerThread = new Thread(brokerThreadGroup, "BrokerThread") {
        public void run() {
          try {
            broker = createBroker();
            broker.start();
            broker.setMasterConnectorURI(uri);
            addHttpConnector(8888);

            brokerSemaphore.release(); // broker started
          } catch (Exception e) {
            if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
              UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                      "setUp", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                      "UIMAJMS_exception__WARNING", e);
            }
          }
        }
      };

      brokerThread.start();
      try {
        // wait for the broker to start and initialize. The semaphore is
        // released
        // in the run method above
        brokerSemaphore.acquire();
      } finally {
        brokerSemaphore.release();
      }
    } else {
      cleanBroker(broker);
    }
  }
  protected void cleanBroker( BrokerService targetBroker) throws Exception {
    // Remove messages from all queues
    targetBroker.deleteAllMessages();
    org.apache.activemq.broker.Connection[] connections = targetBroker.getRegionBroker().getClients();
    for( org.apache.activemq.broker.Connection connection : connections) {
      try {
        connection.stop();
      } catch( Exception e) {
        e.printStackTrace();
      }
    }

    ActiveMQDestination[] destinations = targetBroker.getRegionBroker().getDestinations();

    if ( destinations != null ) {
      for( ActiveMQDestination destination: destinations ) {
        if ( !destination.isTopic() ) {
          targetBroker.removeDestination(destination);
        }
      }
    }
  }
  
  protected String addHttpConnector(int aDefaultPort) throws Exception {
    return addHttpConnector(broker, aDefaultPort);
  }
  protected String addHttpConnector(BrokerService aBroker, int aDefaultPort) throws Exception {
    try {
      String httpURI = generateInternalURI("http", aDefaultPort);
      httpConnector = aBroker.addConnector(httpURI);
      
      //  Use reflection to determine if the AMQ version is at least 5.2. If it is, we must
      //  plug in a broker to the httpConnector otherwise we get NPE when starting the connector.
      //  AMQ version 4.1.1 doesn't exhibit this problem.
      try {
        Method m = httpConnector.getClass().getDeclaredMethod("setBrokerService", new Class[] {BrokerService.class});
        m.invoke(httpConnector, aBroker);
      } catch ( NoSuchMethodException e) {
        //  Ignore, this is not AMQ 5.2
      }
      System.out.println("Adding HTTP Connector:" + httpConnector.getConnectUri());
      httpConnector.start();
      return httpURI;
    } catch (Exception e) {
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                "addHttpConnector", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                "UIMAJMS_exception__WARNING", e);
      }
      throw e;
    }
  }
  protected String getHttpURI() throws Exception {
    while ( httpConnector == null  ) {
     synchronized(this) {
       this.wait(100); 
     }
    }
    return httpConnector.getConnectUri().toString();
  }
  protected void removeQueue(String aQueueName) throws Exception {
    httpConnector.stop();
  }

  protected void removeHttpConnector() throws Exception {
    httpConnector.stop();
    broker.removeConnector(httpConnector);
  }

  private String generateInternalURI(String aProtocol, int aDefaultPort) throws Exception {
    boolean success = false;
    int openPort = aDefaultPort;
    ServerSocket ssocket = null;

    while (!success) {
      try {
        ssocket = new ServerSocket(openPort);
        // String uri = aProtocol + "://" +
        // ssocket.getInetAddress().getLocalHost().getCanonicalHostName()
        // + ":" + openPort;
        String uri = aProtocol + "://localhost:" + openPort;
        success = true;
        return uri;
      } catch (Exception e) {
        if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                  "generateInternalURI", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
                  "UIMAJMS_exception__WARNING", e);
        }
        throw e;
      } finally {
        try {
          if (ssocket != null) {
            ssocket.close();
          }
        } catch (IOException ioe) {
        }
      }
    }
    return null;

  }

  protected String getBrokerUri() {
    return uri;
  }

  protected ConnectionFactory createConnectionFactory() throws Exception {
    return new ActiveMQConnectionFactory(uri);
  }

  protected Connection getConnection() throws Exception {
    return createConnectionFactory().createConnection();
  }

  public BrokerService createBroker() throws Exception {
    return createBroker(8118, true, false);
  }

  protected BrokerService createBroker(int port, boolean useJmx, boolean secondaryBroker) throws Exception {
    ServerSocket ssocket = null;
    System.out.println(">>>> Starting Broker On Port:" + port);
    try {
      ssocket = new ServerSocket();
      String hostName = "localhost"; //ssocket.getInetAddress().getLocalHost().getCanonicalHostName();
      uri = "tcp://" + hostName + ":" + port;
      BrokerService broker = BrokerFactory.createBroker(new URI("broker:()/" + hostName
              + "?persistent=false"));
      if ( secondaryBroker ) {
        broker.getManagementContext().setJmxDomainName(broker.getManagementContext().getJmxDomainName()+".test");      
      }
      broker.setUseJmx(useJmx);
      tcpConnector = broker.addConnector(uri);

      PolicyEntry policy = new PolicyEntry();
      policy.setDeadLetterStrategy(new SharedDeadLetterStrategy());

      PolicyMap pMap = new PolicyMap();
      pMap.setDefaultEntry(policy);

      broker.setDestinationPolicy(pMap);
      broker.setPersistenceAdapter(new MemoryPersistenceAdapter());
      broker.setPersistent(false);
      broker.setUseShutdownHook(true);
      broker.setUseLoggingForShutdownErrors(false);
      try {
          Method method = broker.getClass().getDeclaredMethod("setSchedulerSupport", new Class[] {Boolean.TYPE});
          method.invoke(broker, new Object[] {Boolean.FALSE});
      } catch( NoSuchMethodException e) {
    	  //	ignore
      }

      return broker;
    } finally {
      if (ssocket != null)
        ssocket.close();
    }
  }

  protected void stopBroker() throws Exception {
    if (broker != null) {
      System.out.println(">>> Stopping Broker");
      if (tcpConnector != null) {
        tcpConnector.stop();
        broker.removeConnector(tcpConnector);
        System.out.println("Broker Connector:" + tcpConnector.getUri().toString() + " is stopped");
      }
      removeHttpConnector();
      broker.deleteAllMessages();
      broker.stop();
      System.out.println(">>> Broker Stopped");
    }
  }

  protected synchronized void tearDown() throws Exception {
    super.tearDown();
    System.clearProperty("activemq.broker.jmx.domain");
    System.clearProperty("BrokerURL");


  }

}
