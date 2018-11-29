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
package org.apache.uima.ee.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.InputChannel.ChannelType;
import org.apache.uima.aae.client.UimaAS;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;
import org.apache.uima.aae.monitor.statistics.AnalysisEnginePerformanceMetrics;
import org.apache.uima.aae.service.UimaASService;
import org.apache.uima.aae.service.UimaAsServiceRegistry;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.adapter.jms.activemq.JmsInputChannel;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.ee.test.utils.BaseTestSupport;
import org.apache.uima.ee.test.utils.UimaASTestRunner;
import org.apache.uima.internal.util.XMLUtils;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resourceSpecifier.factory.DeploymentDescriptorFactory;
import org.apache.uima.resourceSpecifier.factory.UimaASPrimitiveDeploymentDescriptor;
import org.apache.uima.resourceSpecifier.factory.impl.ServiceContextImpl;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.Assert;
@RunWith(UimaASTestRunner.class)
public class TestUimaASNoErrors extends BaseTestSupport {
	
	private UimaAsynchronousEngine getClient(Transport transport) throws Exception {
 	   UimaAsynchronousEngine client;
 	   
		switch(transport) {
 	    case JMS:
 	    	client = UimaAS.newInstance(transport);
 	    	break;
 	    case Java:
 	    	client = UimaAS.newInstance(transport);
 	    	break;
 	    default:
 	    	throw new IllegalArgumentException("*** ERROR Invalid Transport");	
 	    }
		return client;

	}
	private Map<String, Object> defaultContext(String aTopLevelServiceQueueName) {
		Map<String, Object> appCtx = new HashMap<>();
	    appCtx.put(UimaAsynchronousEngine.ENDPOINT, aTopLevelServiceQueueName);
	    appCtx.put(UimaAsynchronousEngine.CasPoolSize, Integer.valueOf(1));
	    appCtx.put(UimaAsynchronousEngine.Timeout, 0);
	    appCtx.put(UimaAsynchronousEngine.GetMetaTimeout,0);

		return appCtx;
	}
	
	private String deployTopLevelService(Map<String, Object> appCtx, Transport transport, UimaAsynchronousEngine uimaAsClient, String topLevelDescriptor, String topLevelQueueName) 
	throws Exception {
		String serviceId = null;

		if (Transport.Java.equals(transport)) {
			serviceId = deployJavaService(uimaAsClient, topLevelDescriptor);
		    appCtx.put(UimaAsynchronousEngine.ServerUri, "java");
			appCtx.put(UimaAsynchronousEngine.ClientTransport, Transport.Java);

		} else if (Transport.JMS.equals(transport)) {
		    appCtx.put(UimaAsynchronousEngine.ServerUri, String.valueOf(getMasterConnectorURI(broker)));
			appCtx.put(UimaAsynchronousEngine.ClientTransport, Transport.JMS);
			serviceId = deployJmsService(uimaAsClient, topLevelDescriptor);
		} else {
			throw new IllegalArgumentException(
					"Invalid Client Transport - Expected either Transport.JMS or Transport.Java");
		}
		return serviceId;
	}
    private String getMasterConnectorURI(BrokerService b) {

//    {
//    		System.setProperty("ProtocolOverride", Protocol.JAVA.name());
//    		System.setProperty("ProviderOverride", Provider.JAVA.name());
//
//   	}
	return b.getDefaultSocketURIString();
    }  
    
    
    @Test
    public void testDeploy() throws Exception {
    	
    	UimaAsynchronousEngine uimaAS = getClient(Transport.Java);
    
    	Map ctx = new HashMap<>();
    	ctx.put(UimaAsynchronousEngine.Provider,"java");
        ctx.put(UimaAsynchronousEngine.Protocol,"java");
/*
    	ctx.put(UimaAsynchronousEngine.Provider,"activemq");
        ctx.put(UimaAsynchronousEngine.Protocol,"jms");
        */
        uimaAS.deploy(relativePath + "/Deploy_NoOpAnnotator.xml", ctx);

	    runTest2(null, uimaAS, getMasterConnectorURI(broker),
	            "NoOpAnnotatorQueue", 1, PROCESS_LATCH);

	    uimaAS.stop();
    
    }
    @Test
    public void testJmsServiceAdapter() throws Exception {
	  Logger.getLogger(this.getClass()).info("-------------- testJmsServiceAdapter -------------");
	  //setUp();
	  BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
    try {
        deployService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
        deployService(eeUimaEngine, relativePath + "/Deploy_SyncAggregateWithJmsService.xml");
        runTest(null, eeUimaEngine, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
                0, PROCESS_LATCH);
       
    } catch( Exception e ) {
    	throw e;
    }
  }
    /*
     * 
     * 	 


     	  
     	  @Test
	  public void testMultipleSyncClientsWithMultipleBrokers() throws Exception  {
		    System.out.println("-------------- testMultipleSyncClientsWithMultipleBrokers -------------");
		    
		    class RunnableClient implements Runnable {
		    	String brokerURL;
		    	BaseTestSupport testSupport;
	            BaseUIMAAsynchronousEngine_impl uimaAsEngine;
		    	
		    	RunnableClient(String brokerURL,BaseTestSupport testSupport) {
		    		this.brokerURL = brokerURL;
		    		this.testSupport = testSupport;
		    	}
		    	public void initialize(String dd, String serviceEndpoint) throws Exception {
		    		uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
		            // Deploy Uima AS Primitive Service
		            deployService(uimaAsEngine, dd);

		    		@SuppressWarnings("unchecked")
				  Map<String, Object> appCtx = buildContext(brokerURL, serviceEndpoint);
			  	  appCtx.put(UimaAsynchronousEngine.Timeout, 1100);
			  	  appCtx.put(UimaAsynchronousEngine.CpcTimeout, 1100);
			  	  testSupport.initialize(uimaAsEngine, appCtx);
			  	  waitUntilInitialized();
		    	}
				public void run() {
					try {
			            for (int i = 0; i < 1000; i++) {
				              CAS cas = uimaAsEngine.getCAS();
				              cas.setDocumentText("Some Text");
			//	              System.out.println("UIMA AS Client#"+ Thread.currentThread().getId()+" Sending CAS#"+(i + 1) + " Request to a Service Managed by Broker:"+brokerURL);
				              try {
					                uimaAsEngine.sendAndReceiveCAS(cas);
				              } catch( Exception e) {
				            	  e.printStackTrace();
				              } finally {
				                cas.release();
				              }
				            }
				            System.out.println("Thread:"+Thread.currentThread().getId()+" Completed run()");
				            uimaAsEngine.stop();
					} catch( Exception e) {
						e.printStackTrace();
					}

				}
		    	
		    }
		    
		    ExecutorService executor = Executors.newCachedThreadPool();

		    //	change broker URl in system properties
		    System.setProperty("BrokerURL", getMasterConnectorURI(broker).toString());
		    
		    RunnableClient client1 = 
		    		new RunnableClient(getMasterConnectorURI(broker), this);
		    client1.initialize(relativePath + "/Deploy_NoOpAnnotatorWithPlaceholder.xml", "NoOpAnnotatorQueue");

		    final BrokerService broker2 = setupSecondaryBroker(true);

		    //	change broker URl in system properties
		    System.setProperty("BrokerURL", broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString());

		    RunnableClient client2 = 
		    		new RunnableClient(broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString(), this);
		    client2.initialize(relativePath + "/Deploy_NoOpAnnotatorWithPlaceholder.xml", "NoOpAnnotatorQueue");

		    Future<?> f1 = executor.submit(client1);
		    Future<?> f2 = executor.submit(client2);
		    f1.get();
		    f2.get();
		    executor.shutdownNow();
		    while( !executor.isShutdown() ) {
		    	synchronized(broker) {
		    		broker.wait(500);
		    	}
		    }
		    try {
			    broker2.stop();
//			    broker.stop();
//			    broker.waitUntilStopped();

		    } catch( Exception e) {
		    	// ignore this one. Always thrown by AMQ on stop().
		    }
		}
	  




     	  



 
	  

     */
	  /*
	   * Tests Uima AS client placeholder handling and substitution. The Uima Aggregate instantiates
	   * UIMA AS client proxy using Jms Client Descriptor that contains a placeholder
	   * ${defaultBrokerURL} instead of hard coded Broker URL. The client parses the 
	   * placeholder string, retrieves the name (defaultBrokerURL) and uses it to look
	   * up tha actual broker URL in System properties.
	   */
	  /**
	   * Tests use of a JMS Service Adapter and an override of the MultipleDeploymentAllowed. 
	   * In this test, the AE descriptor of the remote service is configured with MultipleDeploymentAllowed=false
	   * Without the override this causes an exception when instantiating Uima aggregate with
	   * MultipleDeploymentAllowed=true. 
	   * 
	   * @throws Exception
	   */
 
    /*
     * 
     * Uncomment test !!!!!!!!!!!!!!!11

	  */
	  /**
	   * Tests Uima-AS client ability to test sendAndReceive in multiple/concurrent threads It spawns 4
	   * thread each sending 100 CASes to a Primitive Uima-AS service
	   * 
	   * @throws Exception
	   */
    /*
	  @Test
	  public void testSynchCallProcessWithMultipleThreads() throws Exception {
	    System.out.println("-------------- testSynchCallProcessWithMultipleThreads -------------");
	    int howManyCASesPerRunningThread = 100;
	    int howManyRunningThreads = 4;
	    runTestWithMultipleThreads(relativePath + "/Deploy_PersonTitleAnnotator.xml",
	            "PersonTitleAnnotatorQueue", howManyCASesPerRunningThread, howManyRunningThreads, 0, 0);
	  }
	*/

	/*

	*/
	@Test
	public void testCancelProcessAggregateWithRemoteMultiplierOverJava() throws Exception {
		// DOES NOT WORK
		testCancelProcessAggregateWithRemoteMultiplier(Transport.Java);
	}

	@Test
	public void testCancelProcessAggregateWithRemoteMultiplierOverJms() throws Exception {
		// WORKS
		testCancelProcessAggregateWithRemoteMultiplier(Transport.JMS);
	}
	public void testCancelProcessAggregateWithRemoteMultiplier(Transport transport) throws Exception {
		System.out.println("-------------- testStopAggregateWithRemoteMultiplier -------------");
		System.setProperty("BrokerURL", getMasterConnectorURI(broker));
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");

		deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplierWith1MillionDocs.xml");
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithRemoteMultiplier.xml", "TopLevelTaeQueue");
//		Service(eeUimaEngine, relativePath + "/Deploy_AggregateWithRemoteMultiplier.xml");
		// Spin a thread to cancel Process after 20 seconds
		spinShutdownThread(uimaAsClient, 20000);
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 1,
				EXCEPTION_LATCH);
	}
	@Test
	public void testProcessWithAggregateUsingRemoteMergerOverJava() throws Exception {
		testProcessWithAggregateUsingRemoteMerger(Transport.Java);
	}

	@Test
	public void testProcessWithAggregateUsingRemoteMergerOverJms() throws Exception {
		testProcessWithAggregateUsingRemoteMerger(Transport.JMS);
	}

	public void testProcessWithAggregateUsingRemoteMerger(Transport transport) throws Exception {
		System.out.println("-------------- testProcessWithAggregateUsingRemoteMerger -------------");
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMerger.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithRemoteMerger.xml",
				"TopLevelTaeQueue");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 1,
				PROCESS_LATCH);
	}

	@Test
	public void testProcessWithAggregateUsingCollocatedMergerOverJava() throws Exception {
		testProcessWithAggregateUsingCollocatedMerger(Transport.Java);
	}

	@Test
	public void testProcessWithAggregateUsingCollocatedMergerOverJms() throws Exception {
		testProcessWithAggregateUsingCollocatedMerger(Transport.JMS);
	}

	public void testProcessWithAggregateUsingCollocatedMerger(Transport transport) throws Exception {
		System.out.println("-------------- testProcessWithAggregateUsingCollocatedMerger -------------");
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient,
				relativePath + "/Deploy_AggregateWithCollocatedMerger.xml", "TopLevelTaeQueue");

		// deployJavaService(eeUimaEngine, relativePath +
		// "/Deploy_AggregateWithCollocatedMerger.xml");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 1,
				PROCESS_LATCH);
	}
	@Test
	public void testDeployAggregateServiceWithBrokerPlaceholderOverJava() throws Exception {
		testDeployAggregateServiceWithBrokerPlaceholder(Transport.Java);
	}

	@Test
	public void testDeployAggregateServiceWithBrokerPlaceholderOverJms() throws Exception {
		testDeployAggregateServiceWithBrokerPlaceholder(Transport.JMS);
	}

	public void testDeployAggregateServiceWithBrokerPlaceholder(final Transport transport) throws Exception {
		System.out.println("-------------- testDeployAggregateServiceWithBrokerPlaceholder -------------");
		final UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//final BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();

		System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");

		try {
			Thread t = new Thread() {
				public void run() {
					BrokerService bs = null;
					try {
						// at this point the top level service should show a connection error
						synchronized (this) {
							this.wait(5000); // wait for 5 secs
						}
						// Create a new broker that runs a different port that the rest of testcases
						bs = setupSecondaryBroker(false);
						System.setProperty("AggregateBroker",
								bs.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString());
						System.setProperty("NoOpBroker",
								bs.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString());
						deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorUsingPlaceholder.xml");
						Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
						deployTopLevelService(appCtx, transport, uimaAsClient,
								relativePath + "/Deploy_AggregateAnnotatorUsingPlaceholder.xml", "TopLevelTaeQueue");

						// deployJavaService(eeUimaEngine, relativePath
						// + "/Deploy_AggregateAnnotatorUsingPlaceholder.xml");
						// Start the uima AS client. It connects to the top level service and sends
						// 10 messages
					    if ( transport.equals(Transport.JMS)) {
							appCtx.put(UimaAsynchronousEngine.ServerUri, System.getProperty("AggregateBroker"));
					    }
						runTest(appCtx, uimaAsClient, System.getProperty("AggregateBroker"), "TopLevelTaeQueue", 1,
								PROCESS_LATCH);
					} catch (InterruptedException e) {
					} catch (Exception e) {
						e.printStackTrace();
						fail(e.getMessage());
					} finally {
						if (bs != null) {
							try {
								bs.stop();
								bs.waitUntilStopped();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			};
			t.start();
			t.join();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testStopAggregateWithRemoteMultiplierOverJava() throws Exception {
		testStopAggregateWithRemoteMultiplier(Transport.Java);
	}
	@Test
	public void testStopAggregateWithRemoteMultiplierOverJms() throws Exception {
		testStopAggregateWithRemoteMultiplier(Transport.JMS);
	}
	public void testStopAggregateWithRemoteMultiplier(Transport transport) throws Exception {
		System.out.println("-------------- testStopAggregateWithRemoteMultiplier -------------");

		System.setProperty("BrokerURL", getMasterConnectorURI(broker));
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplier.xml");
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorWithExceptionOn5thCAS.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");

		deployTopLevelService(appCtx, transport, uimaAsClient,
				relativePath + "/Deploy_AggregateWithRemoteMultiplier.xml", "TopLevelTaeQueue");
		// deployService(eeUimaEngine, relativePath +
		// "/Deploy_AggregateWithRemoteMultiplier.xml");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 1,
				EXCEPTION_LATCH);
	}

	@Test
	public void testAggregateHttpTunnellingOverJava() throws Exception {
		testAggregateHttpTunnelling(Transport.Java);
	}
	@Test
	public void testAggregateHttpTunnellingOverJms() throws Exception {
		testAggregateHttpTunnelling(Transport.JMS);
	}
	public void testAggregateHttpTunnelling(Transport transport) throws Exception {
		System.out.println("-------------- testAggregateHttpTunnelling -------------");

		// Create Uima-AS Client
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		// Deploy remote service
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		// Deploy top level aggregate that communicates with the remote via Http
		// Tunnelling
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient,
				relativePath + "/Deploy_AggregateAnnotatorWithHttpDelegate.xml","TopLevelTaeQueue");
//		deployJmsService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotatorWithHttpDelegate.xml");
	    //appCtx.put(UimaAsynchronousEngine.ClientTransport, Transport.JMS);

		// Initialize and run the Test. Wait for a completion and cleanup resources.
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 10, CPC_LATCH);
	}
	

	@Test
	public void testJmsServiceAdapterWithOverrideOverJava() throws Exception {
		testJmsServiceAdapterWithOverride(Transport.Java);
	}

	@Test
	public void testJmsServiceAdapterWithOverrideOverJms() throws Exception {
		testJmsServiceAdapterWithOverride(Transport.JMS);
	}

	public void testJmsServiceAdapterWithOverride(Transport transport) throws Exception {
		System.out.println("-------------- testJmsServiceAdapterWithOverride -------------");
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_SingleInstancePersonTitleAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient,
				relativePath + "/Deploy_SyncAggregateWithJmsServiceAndScaleoutOverride.xml", "TopLevelTaeQueue");

		// deployJavaService(eeUimaEngine, relativePath +
		// "/Deploy_SyncAggregateWithJmsServiceAndScaleoutOverride.xml");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 10,
				PROCESS_LATCH);
	}
    
	@Test
	public void testJmsServiceAdapterWithPlaceholderOverJava() throws Exception {
		testJmsServiceAdapterWithPlaceholder(Transport.Java);
	}

	@Test
	public void testJmsServiceAdapterWithPlaceholderOverJms() throws Exception {
		testJmsServiceAdapterWithPlaceholder(Transport.JMS);
	}

	public void testJmsServiceAdapterWithPlaceholder(Transport transport) throws Exception {
		System.out.println("-------------- testJmsServiceAdapterWithPlaceholder -------------");
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient,
				relativePath + "/Deploy_SyncAggregateWithJmsServiceUsingPlaceholder.xml", "TopLevelTaeQueue");

		// deployJavaService(eeUimaEngine, relativePath +
		// "/Deploy_SyncAggregateWithJmsServiceUsingPlaceholder.xml");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 10,
				PROCESS_LATCH);
	}
	@Test
	public void testScaledSyncAggregateProcessOverJava() throws Exception {
		testScaledSyncAggregateProcess(Transport.Java);
	}

	@Test
	public void testScaledSyncAggregateProcessOverJms() throws Exception {
		testScaledSyncAggregateProcess(Transport.JMS);
	}

	public void testScaledSyncAggregateProcess(Transport transport) throws Exception {
		System.out.println("-------------- testScaledSyncAggregateProcess -------------");
		// Instantiate Uima-AS Client
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		// Deploy Uima-AS Primitive Service
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient,
				relativePath + "/Deploy_ScaledPrimitiveAggregateAnnotator.xml", "TopLevelTaeQueue");

		// deployJavaService(eeUimaEngine, relativePath +
		// "/Deploy_ScaledPrimitiveAggregateAnnotator.xml");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 5,
				PROCESS_LATCH);
		System.out.println(uimaAsClient.getPerformanceReport());
	}
    @Test
	public void testComplexDeploymentOverJava() throws Exception {
    	testComplexDeployment(Transport.Java);
    }
    @Test
  	public void testComplexDeploymentOverJms() throws Exception {
      	testComplexDeployment(Transport.JMS);
      }
     public void testComplexDeployment(Transport transport) throws Exception {
	    System.out.println("-------------- testComplexDeployment -------------");
	    Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
	    
        UimaAsynchronousEngine uimaAsClient = getClient(transport);
        Map anApplicationContext = new HashMap<>();
         uimaAsClient.deploy(relativePath + "/Deploy_NoOpAnnotator.xml", anApplicationContext);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy replicated services for the inner remote aggregate CM
	    StringBuilder sb = new StringBuilder(relativePath).append("/Deploy_NoOpAnnotator.xml");
	    deployService(transport, uimaAsClient, sb.toString()) ; //relativePath + "/Deploy_NoOpAnnotator.xml");
//	    deployJmsService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
//	    deployJmsService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
//	    deployJmsService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
	    // Deploy an instance of a remote aggregate CM containing a collocated Cas Multiplier
	    // CM --> Replicated Remote Primitive --> NoOp CC
//	    deployJmsService(eeUimaEngine, relativePath + "/Deploy_CMAggregateWithCollocatedCM.xml");
	    sb.setLength(0);   // clear
	    sb.append(relativePath).append("/Deploy_CMAggregateWithCollocatedCM.xml");
	    deployService(transport, uimaAsClient, sb.toString()); //relativePath + "/Deploy_CMAggregateWithCollocatedCM.xml");
	    // Deploy top level Aggregate Cas Multiplier with 2 collocated Cas Multipliers
	    // CM1 --> CM2 --> Remote AggregateCM --> Candidate Answer --> CC
	    deployTopLevelService(appCtx, transport,uimaAsClient,relativePath + "/Deploy_TopLevelComplexAggregateCM.xml","TopLevelTaeQueue");
	    runTest2(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "TopLevelTaeQueue", 1, PROCESS_LATCH);
	  }
	  @Test
	  public void testDeployAggregateWithCollocatedAggregateServiceOverJava() throws Exception {
		  testDeployAggregateWithCollocatedAggregateService(Transport.Java);
	  }
	  @Test
	  public void testDeployAggregateWithCollocatedAggregateServiceOverJms() throws Exception {
		  testDeployAggregateWithCollocatedAggregateService(Transport.JMS);
	  }

	public void testDeployAggregateWithCollocatedAggregateService(Transport transport) throws Exception {
		System.out.println("-------------- testDeployAggregateWithCollocatedAggregateService -------------");
	    Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployTopLevelService(appCtx,transport,uimaAsClient,relativePath + "/Deploy_ComplexAggregate.xml","TopLevelTaeQueue");
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 10,
				PROCESS_LATCH);

	}
	@Test
	public void testProcessWithAggregateUsingRemoteMultiplierOnSeparateBrokerOverJava() throws Exception {
		testProcessWithAggregateUsingRemoteMultiplierOnSeparateBroker(Transport.Java);
	}
	@Test
	public void testProcessWithAggregateUsingRemoteMultiplierOnSeparateBrokerOverJms() throws Exception {
		testProcessWithAggregateUsingRemoteMultiplierOnSeparateBroker(Transport.JMS);
	}
	
	public void testProcessWithAggregateUsingRemoteMultiplierOnSeparateBroker(Transport transport) throws Exception {
		System.out
				.println("-------------- testProcessWithAggregateUsingRemoteMultiplierOnSeparateBroker -------------");
		System.setProperty("activemq.broker.jmx.domain", "org.apache.activemq.test");
		BrokerService broker2 = setupSecondaryBroker(true);
		System.setProperty("BrokerURL", broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString());
	    Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplier.xml");
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployTopLevelService(appCtx,transport,uimaAsClient,relativePath + "/Deploy_AggregateWithRemoteMultiplier.xml","TopLevelTaeQueue");

//		Map<String, Object> appCtx = new HashMap<>();
		appCtx.put(UimaAsynchronousEngine.ServerUri,
				broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString());
//		appCtx.put(UimaAsynchronousEngine.ENDPOINT, "TopLevelTaeQueue");
//		appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, 0);
		runTest(appCtx, uimaAsClient, broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString(),
				"TopLevelTaeQueue", 10, PROCESS_LATCH);
		super.cleanBroker(broker2);

		broker2.stop();
		broker2.waitUntilStopped();

	}

	 @Test
	 public void testProcessWithAggregateUsingRemoteMultiplierOverJava() throws Exception {
		 testProcessWithAggregateUsingRemoteMultiplier(Transport.Java);
	 }
	 @Test
	 public void testProcessWithAggregateUsingRemoteMultiplierOverJms() throws Exception {
		 testProcessWithAggregateUsingRemoteMultiplier(Transport.JMS);
	 }

	public void testProcessWithAggregateUsingRemoteMultiplier(Transport transport) throws Exception {
		System.out.println("-------------- testProcessWithAggregateUsingRemoteMultiplier -------------");
		System.setProperty("BrokerURL", getMasterConnectorURI(broker));
	    Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplier.xml");
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployTopLevelService(appCtx, transport,uimaAsClient,relativePath + "/Deploy_AggregateWithRemoteMultiplier.xml","TopLevelTaeQueue");

		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 1,
				PROCESS_LATCH);
	}

 @Test
 public void testClientProcessWithRemoteMultiplierOverJava() throws Exception {
	 testClientProcessWithRemoteMultiplier(Transport.Java);
 }
 @Test
 public void testClientProcessWithRemoteMultiplierOverJms() throws Exception {
	 testClientProcessWithRemoteMultiplier(Transport.JMS);
 }
 public void testClientProcessWithRemoteMultiplier(Transport transport) throws Exception {
   System.out.println("-------------- testClientProcessWithRemoteMultiplier -------------");
   Map<String, Object> appCtx = defaultContext("TestMultiplierQueue");
   UimaAsynchronousEngine uimaAsClient = getClient(transport);
   //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
   deployTopLevelService(appCtx,transport,uimaAsClient,relativePath + "/Deploy_RemoteCasMultiplier.xml","TestMultiplierQueue");

//   Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//           "TestMultiplierQueue");
   appCtx.remove(UimaAsynchronousEngine.ShadowCasPoolSize);
   appCtx.put(UimaAsynchronousEngine.ShadowCasPoolSize, Integer.valueOf(1));
   runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
           "TestMultiplierQueue", 1, PROCESS_LATCH);
 }

 @Test
 public void testClientProcessWithComplexAggregateRemoteMultiplierOverJava() throws Exception {
	 testClientProcessWithComplexAggregateRemoteMultiplier(Transport.Java);
 }
 @Test
 public void testClientProcessWithComplexAggregateRemoteMultiplierOverJms() throws Exception {
	 testClientProcessWithComplexAggregateRemoteMultiplier(Transport .JMS);
 }
 public void testClientProcessWithComplexAggregateRemoteMultiplier(Transport transport) throws Exception {

   System.out
           .println("-------------- testClientProcessWithComplexAggregateRemoteMultiplier -------------");
   Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
   UimaAsynchronousEngine uimaAsClient = getClient(transport);
   //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
   deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
   deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplierWith10Docs_1.xml");
   deployTopLevelService(appCtx, transport,uimaAsClient,relativePath + "/Deploy_CasMultiplierAggregateWithRemoteCasMultiplier.xml","TopLevelTaeQueue");
   runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
           1, PROCESS_LATCH);
 }

 @Test
 public void testProcessWithAggregateUsing2RemoteMultipliersOverJava() throws Exception {
	 testProcessWithAggregateUsing2RemoteMultipliers(Transport.Java);
 }
 @Test
 public void testProcessWithAggregateUsing2RemoteMultipliersOverJms() throws Exception {
	 testProcessWithAggregateUsing2RemoteMultipliers(Transport.JMS);
 }
 public void testProcessWithAggregateUsing2RemoteMultipliers(Transport transport) throws Exception {
   System.out
           .println("-------------- testProcessWithAggregateUsing2RemoteMultipliers -------------");
   Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
   UimaAsynchronousEngine uimaAsClient = getClient(transport);
   //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
   deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
   deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplierWith10Docs_1.xml");
   deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplierWith10Docs_2.xml");
   deployTopLevelService(appCtx, transport,uimaAsClient,relativePath + "/Deploy_AggregateWith2RemoteMultipliers.xml","TopLevelTaeQueue");

   runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
           1, PROCESS_LATCH);
 }


	  @Test
	  public void testClientWithAggregateMultiplierOverJava() throws Exception {
		  testClientWithAggregateMultiplier(Transport.Java);
	  }
	  @Test
	  public void testClientWithAggregateMultiplierOverJms() throws Exception {
		  testClientWithAggregateMultiplier(Transport.JMS);
	  }

	public void testClientWithAggregateMultiplier(Transport transport) throws Exception {
		System.out.println("-------------- testClientWithAggregateMultiplier -------------");
		System.setProperty("BrokerURL", broker.getConnectorByName(DEFAULT_BROKER_URL_KEY).getUri().toString());
		//Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		//BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplier.xml");
		deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateMultiplier.xml","TopLevelTaeQueue");
//		Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue");
		appCtx.remove(UimaAsynchronousEngine.ShadowCasPoolSize);
		appCtx.put(UimaAsynchronousEngine.ShadowCasPoolSize, Integer.valueOf(1));
		runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue", 1,
				PROCESS_LATCH);
		System.out.println("-------------- End testClientWithAggregateMultiplier -------------");

	}
	   @Test
	    public void testClient() throws Exception {
	      System.out.println("-------------- testClient -------------");
	      System.setProperty("BrokerURL", broker.getConnectorByName(DEFAULT_BROKER_URL_KEY).getUri().toString());
	      UimaAsynchronousEngine uimaAsClient1 = getClient(Transport.JMS);
	      UimaAsynchronousEngine uimaAsClient2 = getClient(Transport.JMS);
	      //BaseUIMAAsynchronousEngine_impl uimaAsEngine1 = new BaseUIMAAsynchronousEngine_impl();
	      //BaseUIMAAsynchronousEngine_impl uimaAsEngine2 = new BaseUIMAAsynchronousEngine_impl();
	      String sid1= deployJmsService(uimaAsClient1, relativePath.concat( "/Deploy_AggregateMultiplierWith30SecDelay.xml") );
	      String sid2 = deployJavaService(uimaAsClient2, relativePath + "/Deploy_AggregateMultiplierWith30SecDelay.xml");
	      
	      uimaAsClient1.undeploy(sid1);
	      
	      uimaAsClient2.undeploy(sid2);
	    }
	    
	    @Test
	    public void testClientWithPrimitives() throws Exception {
	      System.out.println("-------------- testClientRecoveryFromBrokerFailure -------------");
	      System.setProperty("BrokerURL", broker.getConnectorByName(DEFAULT_BROKER_URL_KEY).getUri().toString());
	      UimaAsynchronousEngine uimaAsClient1 = getClient(Transport.JMS);
	      UimaAsynchronousEngine uimaAsClient2 = getClient(Transport.JMS);
	      //BaseUIMAAsynchronousEngine_impl uimaAsClient1 = new BaseUIMAAsynchronousEngine_impl();
	      //BaseUIMAAsynchronousEngine_impl uimaAsEngine2 = new BaseUIMAAsynchronousEngine_impl();
	      String sid1= deployJmsService(uimaAsClient1, relativePath + "/Deploy_NoOpAnnotator.xml");
	      String sid2 = deployJavaService(uimaAsClient2, relativePath + "/Deploy_NoOpAnnotator.xml");
	      
	      uimaAsClient1.undeploy(sid1);
	      
	      uimaAsClient2.undeploy(sid2);
	      
	    }

	        @Test
	    public void testServiceWithHttpListeners() throws Exception {
	  	    System.out.println("-------------- testServiceWithHttpListeners -------------");
	  	    // Need java monitor object on which to sleep
	  	    Object waitObject = new Object();
	  	    // Custom spring listener with handleListenerSetupFailure() overriden to 
	  	    // capture AMQ exception.
	  	    TestDefaultMessageListenerContainer c = new TestDefaultMessageListenerContainer();
	  	    c.setConnectionFactory(new ActiveMQConnectionFactory("http://localhost:18888"));
	  	    c.setDestinationName("TestQ");
	  	    c.setConcurrentConsumers(2);
	  	    c.setBeanName("TestBean");
	  	    c.setMessageListener(new JmsInputChannel(ChannelType.REQUEST_REPLY));
	  	    c.initialize();
	  	    c.start();
	  	    
	  	    if ( c.isRunning() ) {
	  		    System.out.println("... Listener Ready");
	  	    	
	  	    }
	  	    // Keep-alive has a default 30 secs timeout. Sleep for bit longer than that
	  	    // If there is an exception due to keep-alive, an exception handler will be
	  	    // called on the TestDefaultMessageListenerContainer instance where we 
	  	    // capture the error.
	  	    System.out.println("... Waiting for 40 secs");
	  	    try {
	  	    	synchronized(waitObject) {
	  	    		waitObject.wait(40000);
	  	    	}
	  	    	// had there been broker issues relateds to keep-alive the listener's failed
	  	    	// flag would have been set by now. Check it and fail the test 
	  	    	if ( c.failed() ) {
	  		    	fail("Broker Failed - Reason:"+c.getReasonForFailure());
	  	    	} else {
	  	    		System.out.println("Stopping Listener");
	  	    		c.stop();

	  	    	}
	  	    } catch( Exception e) {
	  	    	e.printStackTrace();
	  	    	fail(e.getMessage());
	  	    }
	    }

	        
	  @Test
	  public void testCompressedTypeFilteringOverJava() throws Exception {
		  testCompressedTypeFiltering(Transport.Java);
	  }
	  
	  @Test
	  public void testCompressedTypeFilteringOverJms() throws Exception {
		  testCompressedTypeFiltering(Transport.JMS);
	  }
	  public void testCompressedTypeFiltering(Transport transport) throws Exception {
	    System.out.println("-------------- testCompressedTypeFiltering -------------");
	    // Instantiate Uima-AS Client
	    final UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //final BaseUIMAAsynchronousEngine_impl uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
  	    deployJmsService(uimaAsClient, relativePath + "/Deploy_RoomNumberAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("MeetingDetectorTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_MeetingDetectorTAE_RemoteRoomNumberBinary.xml","MeetingDetectorTaeQueue");

//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)), "MeetingDetectorTaeQueue");
	    // Set an explicit getMeta (Ping)timeout
	    appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, 2000);
	    // Set an explicit process timeout so to test the ping on timeout
	    appCtx.put(UimaAsynchronousEngine.Timeout, 1000);
	    appCtx.put(UimaAsynchronousEngine.SERIALIZATION_STRATEGY, "binary");

	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "MeetingDetectorTaeQueue", 1, PROCESS_LATCH);
	  }

	  
	  /**
	   * Tests Broker startup and shutdown
	   */
	  @Test
	  public void testBrokerLifecycle() {
	    System.out.println("-------------- testBrokerLifecycle -------------");
	    System.out.println("UIMA_HOME=" + System.getenv("UIMA_HOME")
	            + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator")
	            + "dd2spring.xsl");
	  }
	  @Test
	  public void testGenerateAndDeployPrimitiveDDOverJava() throws Exception {
		  testGenerateAndDeployPrimitiveDD(Transport.Java);
	  }
	  @Test
	  public void testGenerateAndDeployPrimitiveDDOverJms() throws Exception {
		  testGenerateAndDeployPrimitiveDD(Transport.JMS);
	  }
	  public void testGenerateAndDeployPrimitiveDD(Transport transport) throws Exception {
		    System.out.println("-------------- testGenerateAndDeployPrimitiveDD -------------");
		  File directory = new File (".");
		  // Set up a context object containing basic service deployment
		  // information
		  org.apache.uima.resourceSpecifier.factory.ServiceContext context = new ServiceContextImpl("PersonTitle",
				  "PersonTitle Annotator Description",
				  directory.getCanonicalPath() + 
				  System.getProperty("file.separator")+
				  resourceDirPath+
				  System.getProperty("file.separator")+
				  "descriptors" +
				  System.getProperty("file.separator")+
				  "analysis_engine" +
				  System.getProperty("file.separator")+
				  "PersonTitleAnnotator.xml", 
				  "PersonTitleAnnotatorQueue",
				  getMasterConnectorURI(broker));
		  context.setCasPoolSize(2);
		  // create DD with default settings
		  UimaASPrimitiveDeploymentDescriptor dd = DeploymentDescriptorFactory
				  .createPrimitiveDeploymentDescriptor(context);

		  // Get default Error Handler for process and change error threshold
		  dd.getProcessErrorHandlingSettings().setThresholdCount(4);

		  // Two instances of AE in a jvm
		  dd.setScaleup(2);

		  // Generate deployment descriptor in xml format
		  String ddXML = dd.toXML();
		  System.out.println(ddXML);
		  
		  File tempFile = File.createTempFile("Deploy_PersonTitle", ".xml");
		  BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		  out.write(ddXML);
		  out.close();
		  char FS = System.getProperty("file.separator").charAt(0);
		  
		// create Map to hold required parameters
		  Map<String,Object> appCtx = new HashMap<String,Object>();
		  UimaAsynchronousEngine uimaAsClient = getClient(transport);
		  //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		  String aSpringContainerId =
				  deployTopLevelService(appCtx, transport, uimaAsClient, tempFile.getAbsolutePath(), "");

//		      eeUimaEngine.deploy(tempFile.getAbsolutePath(), appCtx);
		  
		  uimaAsClient.undeploy(aSpringContainerId);
		  uimaAsClient.stop();
		  
		  
	  }
	  @Test
	  public void testSendAndReceiveOverJava() throws Exception {
		  testSendAndReceive(Transport.Java);
	  }
	  
	  @Test
	  public void testSendAndReceiveOverJms() throws Exception {
		  testSendAndReceive(Transport.JMS);
	  }
	  public void testSendAndReceive(Transport transport) throws Exception  {
		  UimaAsynchronousEngine uimaAsClient = getClient(transport);
	      //BaseUIMAAsynchronousEngine_impl uimaAsEngine 
	      //	= new BaseUIMAAsynchronousEngine_impl();
	      deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	      deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		  Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		  deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotator.xml", "TopLevelTaeQueue");
	      
//	      appCtx.put(UimaAsynchronousEngine.Timeout, 1100);
	      appCtx.put(UimaAsynchronousEngine.Timeout, 0);
	      appCtx.put(UimaAsynchronousEngine.CpcTimeout, 1100);
	      initialize(uimaAsClient, appCtx);
	      waitUntilInitialized();
	      int errorCount = 0;
	      List<AnalysisEnginePerformanceMetrics> componentMetricsList = 
	    		  new ArrayList<AnalysisEnginePerformanceMetrics>();
	      for (int i = 0; i < 1; i++) {
	        CAS cas = uimaAsClient.getCAS();
	        cas.reset();
	        cas.setDocumentText("Some Text");
	  //      System.out.println("UIMA AS Client Sending CAS#" + (i + 1) + " Request to a Service");
	        try {
	        	uimaAsClient.sendAndReceiveCAS(cas,componentMetricsList);
	          System.out.println("-------> Client Received Performance Metrics of Size:"+componentMetricsList.size());
	          for( AnalysisEnginePerformanceMetrics m :componentMetricsList ) {
	        	  System.out.println(".............. Component:"+m.getName()+" AnalysisTime:"+m.getAnalysisTime());
	          }
	      //  	uimaAsEngine.sendCAS(cas);
	          System.out.println("----------------------------------------------------");
	          componentMetricsList.clear();
	        } catch( Exception e) {
	          errorCount++;
	        } finally {
	          cas.release();
	          componentMetricsList.clear();
	        }
	      }
	      
	      
	      Map<String, List<UimaASService>> services =
	    		  UimaAsServiceRegistry.getInstance().getServiceList();
		  for( Entry<String, List<UimaASService>> serviceListEntry : services.entrySet()) {
				Iterator<UimaASService> listIterator = serviceListEntry.getValue().iterator();
				while( listIterator.hasNext()) {
					UimaASService service = listIterator.next();
			    	  System.out.println("Registered Service:"+service.getName()+" Queue:"+service.getEndpoint());
					
				}
		  }	
		  uimaAsClient.stop();
	  }

	  
	  
	  @Test
	  public void testClientHttpTunnellingToAggregate() throws Exception {
		  System.out.println("-------------- testClientHttpTunnellingToAggregate -------------");
	    // Add HTTP Connector to the broker. 
	    String httpURI = getHttpURI();
	    // Create Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(Transport.JMS);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy remote service
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_AggregateAnnotator.xml");
	    // Initialize and run the Test. Wait for a completion and cleanup resources.
	    System.out.println("-------- Connecting Client To Service: "+httpURI);
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
//		deployTopLevelService(appCtx, Transport.JMS, eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml", "NoOpAnnotatorQueue");
	    appCtx.put(UimaAsynchronousEngine.ServerUri, httpURI);
	    appCtx.put(UimaAsynchronousEngine.ClientTransport, Transport.JMS);

	    
	    runTest(appCtx, uimaAsClient, httpURI, "TopLevelTaeQueue", 1, CPC_LATCH);
	  }
	  @Test
	  public void testClientHttpTunnelling() throws Exception {
	    System.out.println("-------------- testClientHttpTunnelling -------------");
	    String httpURI = getHttpURI();
	    // Create Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(Transport.JMS);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		Map<String, Object> appCtx = defaultContext("NoOpAnnotatorQueue");
		deployTopLevelService(appCtx, Transport.JMS, uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml", "NoOpAnnotatorQueue");

	    // Deploy remote service
//	    deployJmsService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
	    // Initialize and run the Test. Wait for a completion and cleanup resources.
	    System.out.println("-------- Connecting Client To Service: "+httpURI);
	    appCtx.put(UimaAsynchronousEngine.ServerUri, httpURI);
	    appCtx.put(UimaAsynchronousEngine.ClientTransport, Transport.JMS);

	    runTest(appCtx, uimaAsClient, httpURI, "NoOpAnnotatorQueue", 1, PROCESS_LATCH);
	  }


	  @Test
	  public void testClientHttpTunnellingWithDoubleByteText() throws Exception {
	    System.out.println("-------------- testClientHttpTunnellingWithDoubleByteText -------------");

	    BufferedReader in = null;
	    try {
	      File file = new File(relativeDataPath + "/DoubleByteText.txt");
	      System.out.println("Checking for existence of File:" + file.getAbsolutePath());
	      // Process only if the file exists
	      if (file.exists()) {
	        System.out
	                .println(" *** DoubleByteText.txt exists and will be sent through http connector.");
	        System.out.println(" ***   If the vanilla activemq release is being used,");
	        System.out
	                .println(" ***   and DoubleByteText.txt is bigger than 64KB or so, this test case will hang.");
	        System.out
	                .println(" *** To fix, override the classpath with the jar files in and under the");
	        System.out
	                .println(" ***   apache-uima-as/uima-as/src/main/apache-activemq-X.y.z directory");
	        System.out.println(" ***   in the apache-uima-as source distribution.");

	        String httpURI = getHttpURI();
	      // Create Uima-AS Client
	        UimaAsynchronousEngine uimaAsClient = getClient(Transport.JMS);
	        //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	        // Deploy remote service
			Map<String, Object> appCtx = defaultContext("NoOpAnnotatorQueue");
			deployTopLevelService(appCtx, Transport.JMS, uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml", "NoOpAnnotatorQueue");

//	        deployJmsService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
	        
	        InputStream fis = new FileInputStream(file);
	        Reader rd = new InputStreamReader(fis, "UTF-8");
	        in = new BufferedReader(rd);
	        // Set the double-byte text. This is what will be sent to the service
	        String line = in.readLine();
	        super.setDoubleByteText(line);
	        int err = XMLUtils.checkForNonXmlCharacters(line);
	        if (err >= 0) {
	          fail("Illegal XML char at offset " + err);
	        }
	        System.out.println("-------- Connecting Client To Service: "+httpURI);
	        // Initialize and run the Test. Wait for a completion and cleanup resources.
		    appCtx.put(UimaAsynchronousEngine.ServerUri, httpURI);
		    appCtx.put(UimaAsynchronousEngine.ClientTransport, Transport.JMS);

	        runTest(appCtx, uimaAsClient, httpURI, "NoOpAnnotatorQueue", 1, CPC_LATCH);
	      }
	    } catch (Exception e) {
	      // Double-Byte Text file not present. Continue on with the next test
	      e.printStackTrace();
	      fail("Could not complete test");
	    } finally {
	      if (in != null) {
	        in.close();
	      }
	    }
	  }

	  @Test
	  public void testClientCRProcessOverJava() throws Exception {
		  testClientCRProcess(Transport.Java);
	  }
	  @Test
	  public void testClientCRProcessOverJms() throws Exception {
		  testClientCRProcess(Transport.JMS);
	  }
	  public void testClientCRProcess(Transport transport) throws Exception {
		    System.out.println("-------------- testClientCRProcess -------------");
		    super.resetCASesProcessed();
		    
		    // Instantiate Uima AS Client
		    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		    //final BaseUIMAAsynchronousEngine_impl uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
//		    UIMAFramework.getLogger(BaseUIMAAsynchronousEngineCommon_impl.class).setLevel(Level.FINEST);
//		    UIMAFramework.getLogger(BaseUIMAAsynchronousEngine_impl.class).setLevel(Level.FINEST);
//		    UIMAFramework.getLogger().setLevel(Level.FINEST);
//		    UIMAFramework.getLogger().setOutputStream(System.out);
		    // Deploy Uima AS Primitive Service
			Map<String, Object> appCtx = defaultContext("NoOpAnnotatorQueueLongDelay");
			deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorWithLongDelay.xml", "NoOpAnnotatorQueueLongDelay");

//		    deployJavaService(uimaAsEngine, relativePath + "/Deploy_NoOpAnnotatorWithLongDelay.xml");
//		    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//		            "NoOpAnnotatorQueueLongDelay");
//		    appCtx.put(UimaAsynchronousEngine.Timeout, 0);
		    appCtx.put(UimaAsynchronousEngine.CpcTimeout, 1100);
		    appCtx.put(UimaAsynchronousEngine.CasPoolSize,10);
		    
		    String collectionReaderDescriptor =
		    		resourceDirPath + System.getProperty("file.separator") +
		    		"descriptors"+ System.getProperty("file.separator") +
		    		"collection_reader"+ System.getProperty("file.separator") +
		    		"FileSystemCollectionReader.xml";
		   		  
	         // add Collection Reader if specified
	         try {
	             CollectionReaderDescription collectionReaderDescription = 
	                     UIMAFramework.getXMLParser()
	                             .parseCollectionReaderDescription(new XMLInputSource(collectionReaderDescriptor));
	             collectionReaderDescription.getCollectionReaderMetaData().
	             	getConfigurationParameterSettings().
	             		setParameterValue("InputDirectory", relativeDataPath);
	             CollectionReader collectionReader = UIMAFramework
	                     .produceCollectionReader(collectionReaderDescription);
	             uimaAsClient.setCollectionReader(collectionReader);	    
	         } catch( Throwable e) {
	        	 e.printStackTrace();
	         }
		    
		    initialize(uimaAsClient, appCtx);
		    waitUntilInitialized();
		    
		    uimaAsClient.process();

		    Assert.assertEquals(8, getNumberOfCASesProcessed());
		    System.clearProperty("DefaultBrokerURL");
		    uimaAsClient.stop();
		  }

	  @Test
	  public void testClientProcessOverJava() throws Exception {
		  testClientProcess(Transport.Java);
	  }
	  @Test
	  public void testClientProcessOverJms() throws Exception {
		  testClientProcess(Transport.JMS);
	  }
	  public void testClientProcess(Transport transport) throws Exception {
	    System.out.println("-------------- testClientProcess -------------");
	    
	    // Instantiate Uima AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //final BaseUIMAAsynchronousEngine_impl uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima AS Primitive Service
		Map<String, Object> appCtx = defaultContext("PersonTitleAnnotatorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml", "PersonTitleAnnotatorQueue");

//	    deployJavaService(uimaAsEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");
	    
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//				"PersonTitleAnnotatorQueue");
	  
	    
//	    Map<String, Object> appCtx = buildContext("tcp://localhost:61616",
//				"TopLevelTaeQueue");
//	    appCtx.put(UimaAsynchronousEngine.Timeout, 0);
	    appCtx.put(UimaAsynchronousEngine.CpcTimeout, 1100);
	    appCtx.put(UimaAsynchronousEngine.CasPoolSize,2);
	    initialize(uimaAsClient, appCtx);
	    waitUntilInitialized();

	    for (int i = 0; i < 2; i++) {
	      CAS cas = uimaAsClient.getCAS();
	      cas.setDocumentText("Some Text");
	      System.out.println("UIMA AS Client Sending CAS#" + (i + 1) + " Request to a Service");
	      uimaAsClient.sendCAS(cas);
	    }
	    
	    uimaAsClient.collectionProcessingComplete();
	    System.clearProperty("DefaultBrokerURL");
	    uimaAsClient.stop();
	  }
	  
	 @Test
	  public void testClientBrokerPlaceholderSubstitutionOverJava() throws Exception {
		 testClientBrokerPlaceholderSubstitution(Transport.Java);
	 }
	 @Test
	  public void testClientBrokerPlaceholderSubstitutionOverJms() throws Exception {
		 testClientBrokerPlaceholderSubstitution(Transport.JMS);
	 }
	  public void testClientBrokerPlaceholderSubstitution(Transport transport) throws Exception {
	    System.out.println("-------------- testClientBrokerPlaceholderSubstitution -------------");
	    System.setProperty( "defaultBrokerURL", getMasterConnectorURI(broker));
	    // Instantiate Uima AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima AS Primitive Service
		Map<String, Object> appCtx = defaultContext("PersonTitleAnnotatorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml", "PersonTitleAnnotatorQueue");

	  //  deployJavaService(uimaAsEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");

	   // Map<String, Object> appCtx = buildContext("${defaultBrokerURL}","PersonTitleAnnotatorQueue");

	    initialize(uimaAsClient, appCtx);
	    waitUntilInitialized();
	    for (int i = 0; i < 10; i++) {
	      CAS cas = uimaAsClient.getCAS();
	      cas.setDocumentText("Some Text");
	 //     System.out.println("UIMA AS Client Sending CAS#" + (i + 1) + " Request to a Service");
	      uimaAsClient.sendCAS(cas);
	    }
	    uimaAsClient.collectionProcessingComplete();
	    uimaAsClient.stop();
	    
	  }

	  @Test
	  public void testClientEndpointPlaceholderSubstitutionOverJava() throws Exception {
		  testClientEndpointPlaceholderSubstitution(Transport.Java);
	  }
	  @Test
	  public void testClientEndpointPlaceholderSubstitutionOverJms() throws Exception {
		  testClientEndpointPlaceholderSubstitution(Transport.JMS);
	  }
	  public void testClientEndpointPlaceholderSubstitution(Transport transport) throws Exception {
		    System.out.println("-------------- testClientEndpointPlaceholderSubstitution -------------");
		    // Instantiate Uima AS Client
		    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		    //BaseUIMAAsynchronousEngine_impl uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
		    // Nest the placeholders in the broker & endpoint strings
		    String url = getMasterConnectorURI(broker);
		    System.setProperty( "defaultBrokerURL", url.substring(2,url.length()-2));
		    String brokerUrl = url.substring(0,2) + "${defaultBrokerURL}" + url.substring(url.length()-2);	    
		    System.setProperty( "PersonTitleEndpoint", "TitleAnnotator");
		    String endpoint = "Person${PersonTitleEndpoint}Queue";  // "PersonTitleAnnotatorQueue"
		    // Deploy Uima AS Primitive Service
			Map<String, Object> appCtx = defaultContext(endpoint);
			deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml",endpoint);

//		    deployJavaService(uimaAsEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");


		    //Map<String, Object> appCtx = buildContext(brokerUrl, endpoint);

		    initialize(uimaAsClient, appCtx);
		    waitUntilInitialized();
		    for (int i = 0; i < 10; i++) {
		      CAS cas = uimaAsClient.getCAS();
		      cas.setDocumentText("Some Text");
//		      System.out.println("UIMA AS Client Sending CAS#" + (i + 1) + " Request to a Service");
		      uimaAsClient.sendCAS(cas);
		    }
		    uimaAsClient.collectionProcessingComplete();
		    uimaAsClient.stop();
		    
		  }
	   /**
	   * Tests deployment of a primitive Uima-AS Service (PersontTitleAnnotator). Deploys the primitive
	   * in the same jvm using Uima-AS Client API and blocks on a monitor until the Uima Client calls
	   * initializationComplete() method. Once the primitive service starts it is expected to send its
	   * metadata to the Uima client which in turn notifies this object with a call to
	   * initializationComplete() where the monitor is signaled to unblock the thread. This code will
	   * block if the Uima Client does not call initializationComplete()
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testDeployPrimitiveServiceOverJava() throws Exception {
		  testDeployPrimitiveService(Transport.Java);
	  }
	  @Test
	  public void testDeployPrimitiveServiceOverJms() throws Exception {
		  testDeployPrimitiveService(Transport.JMS);
	  }
	  public void testDeployPrimitiveService(Transport transport) throws Exception {
	    System.out.println("-------------- testDeployPrimitiveService -------------");
	    // Instantiate Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
		Map<String, Object> appCtx = defaultContext("PersonTitleAnnotatorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml","PersonTitleAnnotatorQueue");

	//    deployJavaService(eeUimaEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "PersonTitleAnnotatorQueue", 1, PROCESS_LATCH);
	    System.out.println("-------------- Terminating testDeployPrimitiveService -------------");
	  }
	 @Test
	 public void testTypeSystemMergeOverJava() throws Exception {
		 testTypeSystemMerge(Transport.Java);
	 }
	 @Test
	 public void testTypeSystemMergeOverJms() throws Exception {
		 testTypeSystemMerge(Transport.JMS);
	 }
	  public void testTypeSystemMerge(Transport transport) throws Exception {
	    System.out.println("-------------- testTypeSystemMerge -------------");
	    // Instantiate Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
	    deployJmsService(uimaAsClient, relativePath+ "/Deploy_GovernmentOfficialRecognizer.xml");
	    deployJmsService(uimaAsClient, relativePath+ "/Deploy_NamesAndPersonTitlesRecognizer.xml");
	    deployJmsService(uimaAsClient, relativePath+ "/Deploy_TokenSentenceRecognizer.xml");
	    
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateToTestTSMerge.xml","TopLevelTaeQueue");

	   // deployJavaService(eeUimaEngine, relativePath+ "/Deploy_AggregateToTestTSMerge.xml");
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	    "TopLevelTaeQueue");
	    
	    try {
	      initialize(uimaAsClient, appCtx);
	      waitUntilInitialized();
	      //  Check if the type system returned from the service contains
	      //  expected types
	      CAS cas = uimaAsClient.getCAS();
	      TypeSystem ts = cas.getTypeSystem();
	      //  "example.EmailsAddress" type was 'contributed' by the Flow Controller
	      if ( ts.getType("example.EmailAddress") == null ) {
	        fail("Incomplete Type system. Expected Type 'example.EmailAddress' missing from the CAS type system");
	      } else if ( ts.getType("example.GovernmentOfficial") == null) {
	        fail("Incomplete Type system. Expected Type 'example.GovernmentOfficial' missing from the CAS type system");
	      } else if ( ts.getType("example.Name") == null) {
	        fail("Incomplete Type system. Expected Type 'example.Name' missing from the CAS type system");
	      } else if ( ts.getType("example.PersonTitle") == null) {
	        fail("Incomplete Type system. Expected Type 'example.PersonTitle' missing from the CAS type system");
	      } else if ( ts.getType("example.PersonTitleKind") == null) {
	        fail("Incomplete Type system. Expected Type 'example.PersonTitleKind' missing from the CAS type system");
	      } else if ( ts.getType("org.apache.uima.examples.tokenizer.Sentence") == null) {
	        fail("Incomplete Type system. Expected Type 'org.apache.uima.examples.tokenizer.Sentence' missing from the CAS type system");
	      } else if ( ts.getType("org.apache.uima.examples.tokenizer.Token") == null) {
	        fail("Incomplete Type system. Expected Type 'org.apache.uima.examples.tokenizer.Token' missing from the CAS type system");
	      } 
	      
	    } catch (ResourceInitializationException e) {
	        fail("Initialization Exception");
	    } catch (Exception e) {
	    } finally {
	    	uimaAsClient.stop();
	    }
	  }
	 /**
	   * Tests sending CPC request from a client that does not send CASes to a service
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testCpCWithNoCASesSentOverJava() throws Exception {
		  testCpCWithNoCASesSent(Transport.Java);
	  }
	  @Test
	  public void testCpCWithNoCASesSentOverJms() throws Exception {
		  testCpCWithNoCASesSent(Transport.JMS);
	  }
	  public void testCpCWithNoCASesSent(Transport transport) throws Exception {
	    System.out.println("-------------- testCpCWithNoCASesSent -------------");
	    // Instantiate Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
		Map<String, Object> appCtx = defaultContext("PersonTitleAnnotatorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml","PersonTitleAnnotatorQueue");

	//    deployJavaService(uimaAsEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	            "PersonTitleAnnotatorQueue");
	    initialize(uimaAsClient, appCtx);
	    waitUntilInitialized();

	    for (int i = 0; i < 10; i++) {
	      System.out.println("UIMA AS Client Sending CPC Request to a Service");
	      uimaAsClient.collectionProcessingComplete();
	    }
	    uimaAsClient.stop();
	  }

	@Test
	public void testDeployAsyncAggregateServiceOverJava() throws Exception {
//		URL url = TestUimaASNoErrors.class.getResource("/Deploy_AsyncAggregate.xml");
		
		testDeployAsyncAggregateService(Transport.Java);
	}

	public void testDeployAsyncAggregateService(Transport transport) throws Exception {
		System.out.println("-------------- testDeployAggregateService -------------");
		UimaAsynchronousEngine uimaAsClient = getClient(transport);
		System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AsyncAggregate.xml",
				"TopLevelTaeQueue");

		appCtx.put(UimaAsynchronousEngine.Timeout, 0);
		appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, 0);

		addExceptionToignore(org.apache.uima.aae.error.UimaEEServiceException.class);

		runTest(appCtx, uimaAsClient, "tcp://localhost:61616", "TopLevelTaeQueue", 2, PROCESS_LATCH);
	}

	 @Test
	 public void testDeployAggregateServiceOverJava() throws Exception {
		 testDeployAggregateService(Transport.Java);
	 }
	 @Test
	 public void testDeployAggregateServiceOverJms() throws Exception {
		 testDeployAggregateService(Transport.JMS);
	 }
	  public void testDeployAggregateService(Transport transport) throws Exception {
	    System.out.println("-------------- testDeployAggregateService -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    
	    
	       System.setProperty("NoOpBroker", "tcp::/localhost:61616");
	       System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");
	      // deployService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
			deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorUsingPlaceholder.xml");

	       Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
			deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotator.xml","TopLevelTaeQueue");

	//       deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotator.xml");

	  //     Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	      		Map<String, Object> appCtx = buildContext("tcp://localhost:61616",
	    //           "TopLevelTaeQueue");
//	       appCtx.put(UimaAsynchronousEngine.Timeout, 1000);
	       appCtx.put(UimaAsynchronousEngine.Timeout, 0);
	       appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, 0);
	       
	       addExceptionToignore(org.apache.uima.aae.error.UimaEEServiceException.class); 
	       
//	       runTest(appCtx, eeUimaEngine, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	       runTest(appCtx, uimaAsClient, "tcp://localhost:61616", "TopLevelTaeQueue",
	               1, PROCESS_LATCH);
	  }
	 
	 
/*
	 @Test
	  public void testDeployAggregateService() throws Exception {
	    System.out.println("-------------- testDeployAggregateService -------------");
	    BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    
	    
	 //   System.setProperty("BrokerURL", "tcp::/localhost:61616");

	    
	    System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");
	  //  String serviceId = deployService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotator.xml");
//	    System.setProperty("NoOpService", serviceId);
	    
	 //   deployService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotator.xml");
	    
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	   		Map<String, Object> appCtx = buildContext("tcp://localhost:61616",
//	    		 "NoOpAnnotatorQueue");
	 //           "TopLevelTaeQueue");
	    Map<String, Object> appCtx = buildContext("tcp://localhost:61616",
	         "MeetingDetectorTaeQueue");
	    appCtx.put(UimaAsynchronousEngine.Timeout, 0);
	    appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, 0);
	    
//	    addExceptionToignore(org.apache.uima.aae.error.UimaEEServiceException.class); 
	    
//	    runTest(appCtx, eeUimaEngine, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	    
	    
	  //  runTest(appCtx, eeUimaEngine, "tcp://localhost:61616", "TopLevelTaeQueue",
	  //          1, PROCESS_LATCH);
	    runTest(appCtx, eeUimaEngine, "tcp://localhost:61616", "MeetingDetectorTaeQueue",
	            1, PROCESS_LATCH);
	  }
	 */
	  /**
	   * Sends total of 10 CASes to async aggregate configured to process 2 CASes at a time.
	   * The inner NoOp annotator is configured to sleep for 5 seconds. The client should
	   * be receiving 2 ACKs simultaneously confirming that the aggregate is processing 2 
	   * input CASes at the same time.
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testDeployAggregateServiceWithScaledInnerNoOpOverJava() throws Exception {
		  testDeployAggregateServiceWithScaledInnerNoOp(Transport.Java);
	  }
	  @Test
	  public void testDeployAggregateServiceWithScaledInnerNoOpOverJms() throws Exception {
		  testDeployAggregateServiceWithScaledInnerNoOp(Transport.JMS);
	  }
	  public void testDeployAggregateServiceWithScaledInnerNoOp(Transport transport) throws Exception {
		    System.out.println("-------------- testDeployAggregateServiceWithScaledInnerNoOp -------------");
		    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		    System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");
			Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
			deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotatorWithScaledInnerNoOp.xml","TopLevelTaeQueue");

//		    deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotatorWithScaledInnerNoOp.xml");
//		    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//		            "TopLevelTaeQueue");
//		    appCtx.put(UimaAsynchronousEngine.Timeout, 0);
		    appCtx.put(UimaAsynchronousEngine.CasPoolSize, 5);
		    appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, 0);
		    
		    addExceptionToignore(org.apache.uima.aae.error.UimaEEServiceException.class); 
		    
		    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
		            10, PROCESS_LATCH);
		    System.out.println("-------------- End testDeployAggregateServiceWithScaledInnerNoOp -------------");

		  }



	  /**
	   * Tests a simple Aggregate with one remote Delegate and collocated Cas Multiplier
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testDeployAggregateServiceWithTempReplyQueueOverJava() throws Exception {
		  testDeployAggregateServiceWithTempReplyQueue(Transport.Java);
	  }
	  @Test
	  public void testDeployAggregateServiceWithTempReplyQueueOverJms() throws Exception {
		  testDeployAggregateServiceWithTempReplyQueue(Transport.JMS);
	  }
	  public void testDeployAggregateServiceWithTempReplyQueue(Transport transport) throws Exception {
	    System.out.println("-------------- testDeployAggregateServiceWithTempReplyQueue -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateUsingRemoteTempQueue.xml","TopLevelTaeQueue");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateUsingRemoteTempQueue.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);

	  }

	  /**
	   * Tests a simple Aggregate with one remote Delegate and collocated Cas Multiplier
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testProcessAggregateServiceWith1000DocsOverJava() throws Exception {
		  testProcessAggregateServiceWith1000Docs(Transport.Java);
	  }
	  @Test
	  public void testProcessAggregateServiceWith1000DocsOverJms() throws Exception {
		  testProcessAggregateServiceWith1000Docs(Transport.JMS);
	  }
	  public void testProcessAggregateServiceWith1000Docs(Transport transport) throws Exception {
	    System.out.println("-------------- testProcessAggregateServiceWith1000Docs -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotatorWithInternalCM1000Docs.xml","TopLevelTaeQueue");

//	    deployJavaService(eeUimaEngine, relativePath
//	            + "/Deploy_AggregateAnnotatorWithInternalCM1000Docs.xml");
	    
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);

	  }

	  @Test
	  public void testProcessAggregateWithInnerAggregateCMOverJava() throws Exception {
		  testProcessAggregateWithInnerAggregateCM(Transport.Java);
	  }
	  @Test
	  public void testProcessAggregateWithInnerAggregateCMOverJms() throws Exception {
		  testProcessAggregateWithInnerAggregateCM(Transport.JMS);
	  }
	  public void testProcessAggregateWithInnerAggregateCM(Transport transport) throws Exception {
	    System.out.println("-------------- testProcessAggregateWithInnerAggregateCM() -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_ComplexAggregateWithInnerAggregateCM.xml","TopLevelTaeQueue");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_ComplexAggregateWithInnerAggregateCM.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	    System.out.println("-------------- End testProcessAggregateWithInnerAggregateCM() -------------");

	  }
	  @Test
	  public void testAggregateWithInnerSynchAggregateCMOverJava() throws Exception {
		  testAggregateWithInnerSynchAggregateCM(Transport.Java);
	  }
	  @Test
	  public void testAggregateWithInnerSynchAggregateCMOverJms() throws Exception {
		  testAggregateWithInnerSynchAggregateCM(Transport.JMS);
	  }
	  
	  public void testAggregateWithInnerSynchAggregateCM(Transport transport) throws Exception {
		    System.out.println("-------------- testAggregateWithInnerSynchAggregateCM() -------------");
		    UimaAsynchronousEngine uimaAsClient = getClient(transport);
		    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		    System.setProperty(JmsConstants.SessionTimeoutOverride, "2500000");
			Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
			deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_ComplexAggregateWithInnerUimaAggregateCM.xml","TopLevelTaeQueue");

		   // deployJavaService(eeUimaEngine, relativePath + "/Deploy_ComplexAggregateWithInnerUimaAggregateCM.xml");
		    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
		            1, PROCESS_LATCH);
		    System.out.println("-------------- testAggregateWithInnerSynchAggregateCM() -------------");

		  }

	 /**
	   * Deploys a Primitive Uima-AS service and sends 5 CASes to it.
	   * 
	   * @throws Exception
	   */

	  @Test
	  public void testPrimitiveServiceProcessOverJava() throws Exception {
		  testPrimitiveServiceProcess(Transport.Java);
	  }
	  @Test
	  public void testPrimitiveServiceProcessOverJms() throws Exception {
		  testPrimitiveServiceProcess(Transport.JMS);
	  }
	  public void testPrimitiveServiceProcess(Transport transport) throws Exception {
	    System.out.println("-------------- testPrimitiveServiceProcess -------------");
	    // Instantiate Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
		Map<String, Object> appCtx = defaultContext("PersonTitleAnnotatorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml","PersonTitleAnnotatorQueue");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "PersonTitleAnnotatorQueue", 5, PROCESS_LATCH);
	  }

	  /**
	   * Deploys a Primitive Uima-AS service and sends 5 CASes to it.
	   * 
	   * @throws Exception
	   */

	  @Test
	  public void testSyncAggregateProcessOverJava() throws Exception {
		  testSyncAggregateProcess(Transport.Java); 
	  }
	  @Test
	  public void testSyncAggregateProcessOverJms() throws Exception {
		  testSyncAggregateProcess(Transport.JMS);
	  }
	  public void testSyncAggregateProcess(Transport transport) throws Exception {
	    System.out.println("-------------- testSyncAggregateProcess -------------");
	    // Instantiate Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
		Map<String, Object> appCtx = defaultContext("MeetingDetectorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_MeetingDetectorAggregate.xml","MeetingDetectorQueue");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_MeetingDetectorAggregate.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "MeetingDetectorQueue", 5, PROCESS_LATCH);
	  }


	  /**
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testPrimitiveProcessCallWithLongDelayOverJava() throws Exception {
		  testPrimitiveProcessCallWithLongDelay(Transport.Java);
	  }
	  @Test
	  public void testPrimitiveProcessCallWithLongDelayOverJms() throws Exception {
		  testPrimitiveProcessCallWithLongDelay(Transport.JMS);
	  }
	  public void testPrimitiveProcessCallWithLongDelay(Transport transport) throws Exception {
	    System.out.println("-------------- testPrimitiveProcessCallWithLongDelay -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Service
		Map<String, Object> appCtx = defaultContext("NoOpAnnotatorQueueLongDelay");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorWithLongDelay.xml","NoOpAnnotatorQueueLongDelay");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_NoOpAnnotatorWithLongDelay.xml");
	    // We expect 18000ms to be spent in process method
	    super.setExpectedProcessTime(6000);

//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	            "NoOpAnnotatorQueueLongDelay");
	    appCtx.remove(UimaAsynchronousEngine.ReplyWindow);
	    appCtx.put(UimaAsynchronousEngine.ReplyWindow, 1);
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "NoOpAnnotatorQueueLongDelay", 4, PROCESS_LATCH, true);
	  }

	  /**
	   * Tests time spent in process CAS. The CAS is sent to three remote delegates each with a delay of
	   * 6000ms in the process method. The aggregate is expected to sum up the time spent in each
	   * annotator process method. The final sum is returned to the client (the test) and compared
	   * against expected 18000ms. The test actually allows for 20ms margin to account for any overhead
	   * (garbage collecting, slow cpu, etc)
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testAggregateProcessCallWithLongDelayOverJava() throws Exception {
		  testAggregateProcessCallWithLongDelay(Transport.Java);
	  }
	  @Test
	  public void testAggregateProcessCallWithLongDelayOverJms() throws Exception {
		  testAggregateProcessCallWithLongDelay(Transport.JMS);
	  }
	  public void testAggregateProcessCallWithLongDelay(Transport transport) throws Exception {

	    System.out.println("-------------- testAggregateProcessCallWithLongDelay -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Services each with 6000ms delay in process()
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorAWithLongDelay.xml");
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorBWithLongDelay.xml");
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorCWithLongDelay.xml");
	    
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotatorWithLongDelay.xml","TopLevelTaeQueue");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotatorWithLongDelay.xml");
	    // We expect 18000ms to be spent in process method
	    super.setExpectedProcessTime(18000);
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	            "TopLevelTaeQueue");
	    appCtx.remove(UimaAsynchronousEngine.ReplyWindow);
	    // make sure we only send 1 CAS at a time
	    appCtx.put(UimaAsynchronousEngine.ReplyWindow, 1);
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	            "TopLevelTaeQueue", 1, PROCESS_LATCH, true);
	    System.out.println("///////////////");
	  }

	  /**
	   * Tests Aggregate configuration where the Cas Multiplier delegate is the last delegate in the
	   * Aggregate's pipeline
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testAggregateProcessCallWithLastCMOverJava() throws Exception {
		  testAggregateProcessCallWithLastCM(Transport.Java);
	  }
	  @Test
	  public void testAggregateProcessCallWithLastCMOverJms() throws Exception {
		  testAggregateProcessCallWithLastCM(Transport.JMS);
	  }
	  public void testAggregateProcessCallWithLastCM(Transport transport) throws Exception {
	    System.out.println("-------------- testAggregateProcessCallWithLastCM -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    // Deploy Uima-AS Primitive Services each with 6000ms delay in process()
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithLastCM.xml","TopLevelTaeQueue");

	    //deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateWithLastCM.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH, true);
	  }


	/**
	   * Tests a parallel flow in the Uima-AS aggregate.
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testProcessWithParallelFlowOverJava() throws Exception {
		  testProcessWithParallelFlow(Transport.Java);
	  }
	  @Test
	  public void testProcessWithParallelFlowOverJms() throws Exception {
		  testProcessWithParallelFlow(Transport.JMS);
	  }
	  public void testProcessWithParallelFlow(Transport transport) throws Exception {
	    System.out.println("-------------- testProcessWithParallelFlow -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator2.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithParallelFlow.xml","TopLevelTaeQueue");

	    //deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateWithParallelFlow.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  }


	  @Test
	  public void testProcessWithAggregateUsingCollocatedMultiplierOverJava() throws Exception {
		  testProcessWithAggregateUsingCollocatedMultiplier(Transport.Java);
	  }
	  @Test
	  public void testProcessWithAggregateUsingCollocatedMultiplierOverJms() throws Exception {
		  testProcessWithAggregateUsingCollocatedMultiplier(Transport.JMS);
	  }
	  public void testProcessWithAggregateUsingCollocatedMultiplier(Transport transport) throws Exception {
	    System.out
	            .println("-------------- testProcessWithAggregateUsingCollocatedMultiplier -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotator.xml","TopLevelTaeQueue");

	   // deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotator.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  }

	  @Test
	  public void testParentProcessLastOverJava() throws Exception {
		  testParentProcessLast(Transport.Java);
	  }
	  @Test
	  public void testParentProcessLastOverJms() throws Exception {
		  testParentProcessLast(Transport.JMS);
	  }
	  public void testParentProcessLast(Transport transport) throws Exception {
	    System.out
	            .println("-------------- testParentProcessLast -------------");
	    System.setProperty("BrokerURL", getMasterConnectorURI(broker));
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_RemoteCasMultiplierWith10Docs_1.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateAnnotatorWithProcessParentLastCMs.xml","TopLevelTaeQueue");

	   // deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateAnnotatorWithProcessParentLastCMs.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  }

	  /**
	   * Starts two remote delegates on one broker and a top level client aggregate on 
	   * another. Tests sending Free Cas requests to the appropriate broker. 
	   * 
	   * @throws Exception
	   */

	  
	  /**
	   * First CM feeds 100 CASes to a "merger" CM that generates one output CAS for every 5 input.
	   * Second CM creates unique document text that is checked by the last component. The default FC
	   * should let 4 childless CASes through, replacing every 5th by its child.
	   * 
	   * @throws Exception
	   */

	  

	 
	  @Test
	  public void testProcessWithAggregateUsing2CollocatedMultipliersOverJava() throws Exception {
		  testProcessWithAggregateUsing2CollocatedMultipliers(Transport.Java);
	  }
	  @Test
	  public void testProcessWithAggregateUsing2CollocatedMultipliersOverJms() throws Exception {
		  testProcessWithAggregateUsing2CollocatedMultipliers(Transport.JMS);
	  }
	  
	  public void testProcessWithAggregateUsing2CollocatedMultipliers(Transport transport) throws Exception {
	    System.out
	            .println("-------------- testProcessWithAggregateUsing2CollocatedMultipliers -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWith2Multipliers.xml","TopLevelTaeQueue");

	   // deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateWith2Multipliers.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  }

	  @Test
	  public void testProcessAggregateWithInnerCMAggregateOverJava() throws Exception {
		  testProcessAggregateWithInnerCMAggregate(Transport.Java);
	  }
	  @Test
	  public void testProcessAggregateWithInnerCMAggregateOverJms() throws Exception {
		  testProcessAggregateWithInnerCMAggregate(Transport.JMS);
	  }
	  public void testProcessAggregateWithInnerCMAggregate(Transport transport) throws Exception {
	    System.out.println("-------------- testProcessAggregateWithInnerCMAggregate -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_TopAggregateWithInnerAggregateCM.xml","TopLevelTaeQueue");

	   // deployJavaService(eeUimaEngine, relativePath + "/Deploy_TopAggregateWithInnerAggregateCM.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  }



	  @Test
	  public void testTypesystemMergeWithMultiplierOverJava() throws Exception {
		  testTypesystemMergeWithMultiplier(Transport.Java);
	  }
	  @Test
	  public void testTypesystemMergeWithMultiplierOverJms() throws Exception {
		  testTypesystemMergeWithMultiplier(Transport.JMS);
	  }
	  public void testTypesystemMergeWithMultiplier(Transport transport) throws Exception {
	    System.out.println("-------------- testTypesystemMergeWithMultiplier -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithMergedTypes.xml","TopLevelTaeQueue");

	    //deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateWithMergedTypes.xml");
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  } 

	  @Test
	  public void testCancelProcessAggregateWithCollocatedMultiplierOverJava() throws Exception {
		  testCancelProcessAggregateWithCollocatedMultiplier(Transport.Java);
	  }
	  @Test
	  public void testCancelProcessAggregateWithCollocatedMultiplierOverJms() throws Exception {
		  testCancelProcessAggregateWithCollocatedMultiplier(Transport.JMS); 
	  }
	  public void testCancelProcessAggregateWithCollocatedMultiplier(Transport transport) throws Exception {
	    System.out
	            .println("-------------- testCancelProcessAggregateWithCollocatedMultiplier -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
		Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_ComplexAggregateWith1MillionDocs.xml","TopLevelTaeQueue");

	   // deployJavaService(eeUimaEngine, relativePath + "/Deploy_ComplexAggregateWith1MillionDocs.xml");
	    // Spin a thread to cancel Process after 20 seconds
	    spinShutdownThread(uimaAsClient, 20000);
	    runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	            1, PROCESS_LATCH);
	  }



	 /**
	   * Tests exception thrown in the Uima-AS Client when the Collection Reader is added after the uima
	   * ee client is initialized
	   * 
	   * @throws Exception
	   */

	  @Test
	  public void testCollectionReaderOverJava() throws Exception {
		  testCollectionReader(Transport.Java);
	  }
	  @Test
	  public void testCollectionReaderOverJms() throws Exception {
		  testCollectionReader(Transport.JMS);
	  }
	  public void testCollectionReader(Transport transport) throws Exception {
	    System.out.println("-------------- testCollectionReader -------------");
	    // Instantiate Uima-AS Client
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
		Map<String, Object> appCtx = defaultContext("PersonTitleAnnotatorQueue");
		deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_PersonTitleAnnotator.xml","PersonTitleAnnotatorQueue");

//	    deployJavaService(eeUimaEngine, relativePath + "/Deploy_PersonTitleAnnotator.xml");
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	            "PersonTitleAnnotatorQueue");
	    // reduce the cas pool size and reply window
	    appCtx.remove(UimaAsynchronousEngine.CasPoolSize);
	    appCtx.put(UimaAsynchronousEngine.CasPoolSize, Integer.valueOf(2));
	    appCtx.remove(UimaAsynchronousEngine.ReplyWindow);
	    appCtx.put(UimaAsynchronousEngine.ReplyWindow, 1);

	    // set the collection reader
	    String filename = super
	            .getFilepathFromClassloader("descriptors/collection_reader/ExtendedTestFileSystemCollectionReader.xml");
	    if (filename == null) {
	      fail("Unable to find file:" + "descriptors/collection_reader/ExtendedTestFileSystemCollectionReader.xml"
	              + "in classloader");
	    }
	    File collectionReaderDescriptor = new File(filename);
	    CollectionReaderDescription collectionReaderDescription = UIMAFramework.getXMLParser()
	            .parseCollectionReaderDescription(new XMLInputSource(collectionReaderDescriptor));
	    CollectionReader collectionReader = UIMAFramework
	            .produceCollectionReader(collectionReaderDescription);
	    uimaAsClient.setCollectionReader(collectionReader);
	    initialize(uimaAsClient, appCtx);
	    waitUntilInitialized();
	    runCrTest(uimaAsClient, 7);
	    synchronized (this) {
	      wait(50);
	    }
	    uimaAsClient.stop();
	  }



	 @Test
	 public void testDeployAgainAndAgainOverJava() throws Exception {
		 testDeployAgainAndAgain(Transport.Java);
	 }
	 @Test
	 public void testDeployAgainAndAgainOverJms() throws Exception {
		 testDeployAgainAndAgain(Transport.JMS);
	 }
	  public void testDeployAgainAndAgain(Transport transport) throws Exception {
	    System.out.println("-------------- testDeployAgainAndAgain -------------");
	    for (int num = 1; num <= 50; num++) {
	    	UimaAsynchronousEngine uimaAsClient = getClient(transport);
	      //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl(); 
	      System.out.println("\nRunning iteration " + num);
	      deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator.xml");
	      deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator2.xml");
		  Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		  deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithParallelFlow.xml","TopLevelTaeQueue");

	      //deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateWithParallelFlow.xml");
	      runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)),
	              "TopLevelTaeQueue", 1, PROCESS_LATCH);
	    }
	  }
	  @Test
	  public void testMultipleASClients() throws Exception  {
		    System.out.println("-------------- testMultipleSyncClientsWithMultipleBrokers -------------");
		    
		    class RunnableClient implements Runnable {
		    	String brokerURL;
		    	BaseTestSupport testSupport;
		    	UimaAsynchronousEngine uimaAsClient;// = getClient(transport);
	          //BaseUIMAAsynchronousEngine_impl uimaAsEngine;
	          String serviceEndpoint;
	          
	          
	          RunnableClient(BaseTestSupport testSupport, String brokerURL,String serviceEndpoint) throws Exception {
		    		this.brokerURL = brokerURL;
		    		this.testSupport = testSupport;
		    		this.serviceEndpoint = serviceEndpoint;
		    		uimaAsClient = getClient(Transport.JMS);
		    		//uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
		    	}
		    	public UimaAsynchronousEngine getUimaAsClient() {
		    		return uimaAsClient;
		    	}
		    	public void initialize() throws Exception {
		    		@SuppressWarnings("unchecked")
				  Map<String, Object> appCtx = buildContext(brokerURL, serviceEndpoint);
		    		appCtx.put(UimaAsynchronousEngine.Timeout, 1100);
			  	  appCtx.put(UimaAsynchronousEngine.CpcTimeout, 1100);
			  	  testSupport.initialize(getUimaAsClient(), appCtx);
			  	  waitUntilInitialized();
			  	  
		    	}
				public void run() {
					try {
						initialize();
						System.out.println("Thread:"+Thread.currentThread().getId()+" Completed GetMeta() broker:"+brokerURL);
					} catch( Exception e) {
						e.printStackTrace();
					} finally {
						try {
							uimaAsClient.stop();
						} catch( Exception e) {
							e.printStackTrace();
						}
					}

				}
		    	
		    }
		    
		    ExecutorService executor = Executors.newCachedThreadPool();
	      String serviceId1;
	      String serviceId2;

		    //	change broker URl in system properties
		    System.setProperty("BrokerURL", getMasterConnectorURI(broker).toString());
		    
		    RunnableClient client1 = 
		    		new RunnableClient(this, getMasterConnectorURI(broker), "NoOpAnnotatorQueue");
		    UimaAsynchronousEngine engine = client1.getUimaAsClient();
		    serviceId1 = deployJmsService(engine, relativePath + "/Deploy_NoOpAnnotatorWithPlaceholder.xml");

		    final BrokerService broker2 = setupSecondaryBroker(true);
		    //	change broker URl in system properties
		    System.setProperty("BrokerURL", broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString());
		    
		    RunnableClient client2 = 
		    		new RunnableClient(this, "failover:tcp://f5n633:51514,tcp://f12n1133:51514","NoOpAnnotatorQueue");//broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString(), "NoOpAnnotatorQueue");
		    		//new RunnableClient(this, "failover:ssl://f5n6:51514,ssl://f12n11:51514","NoOpAnnotatorQueue");//broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString(), "NoOpAnnotatorQueue");
		    UimaAsynchronousEngine engine2 = client2.getUimaAsClient();
//		    serviceId2 = deployService(engine2, relativePath + "/Deploy_NoOpAnnotatorWithPlaceholder.xml");

		    
		    for( int x = 0; x < 100; x++) {
			    List<Future<?>> list1 = new ArrayList<Future<?>>();
			    List<Future<?>> list2 = new ArrayList<Future<?>>();
			    String b;
			  /*
			    if ( x % 2 == 0 ) {
			    	b = getMasterConnectorURI(broker);
			    } else {
			    	b = "failover:ssl://f5n6:51514,ssl://f12n11:51514";//broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString();
			    }
			    */
			    List<Future<?>> list = new ArrayList<Future<?>>();
			    for (int i = 0; i < 20; i++) {
			    	  if ( i % 2 == 0 ) {
					    	b = getMasterConnectorURI(broker);
					    	list = list1;
					    } else {
					    	b = "failover:tcp://f5n633:51514,tcp://f12n1133:51514?maxReconnectAttempts=2&timeout=300&transport.maxReconnectAttempts=2&transport.timeout=300&startupMaxReconnectAttempts=1";//broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString();
//					    	b = "failover:ssl://f5n6:51514,ssl://f12n11:51514?maxReconnectAttempts=2&timeout=300&transport.maxReconnectAttempts=2&transport.timeout=300&startupMaxReconnectAttempts=1";//broker2.getConnectorByName(DEFAULT_BROKER_URL_KEY_2).getUri().toString();
					    	list = list2;
					    }
				    RunnableClient client = 
				    		new RunnableClient(this, b, "NoOpAnnotatorQueue");
				    list.add(executor.submit(client));;
			    }
			    /*
			    
				for (int i = 0; i < 10; i++) {
			    	try {
				    	list.get(i).get();//1, TimeUnit.SECONDS);
			    	} catch( Exception e) {
			    		e.printStackTrace();
				    	list.get(i).cancel(true);
			    	}
			    }
			    */

		    Worker worker1 = new Worker(list1);
		    Worker worker2 = new Worker(list1);
		    Thread t1 = new Thread(worker1);
		    Thread t2 = new Thread(worker2);
		    t1.start();
		    t2.start();
		    
		    t1.join();
		    t2.join();
		    
		    list.clear();
		    
		    }
		//    engine2.undeploy(serviceId2);
		    engine.undeploy(serviceId1);
		    
		    //engine2.stop();
		    executor.shutdownNow();
		    while( !executor.isShutdown() ) {
		    	synchronized(broker) {
		    		broker.wait(100);
		    	}
		    }
		    broker2.stop();
		    broker2.waitUntilStopped();
		    //broker.stop();
		    //broker.waitUntilStopped();
		    //System.out.println("Done");
		}
	  @Test
	  public void testAsynchronousTerminateOverJava() throws Exception {
		  testAsynchronousTerminate(Transport.Java);
	  }
	  @Test
	  public void testAsynchronousTerminateOverJms() throws Exception {
		  testAsynchronousTerminate(Transport.JMS);
	  }
	  public void testAsynchronousTerminate(Transport transport) throws Exception {
	    System.out.println("-------------- testAsynchronousTerminate -------------");
	    UimaAsynchronousEngine uimaAsClient = getClient(transport);
	    //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
//	    Map<String, Object> appCtx = buildContext(String.valueOf(getMasterConnectorURI(broker)),
//	    "TopLevelTaeQueue");
	    try {
	      deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotatorWithDelay.xml");
	      deployJmsService(uimaAsClient, relativePath + "/Deploy_NoOpAnnotator2.xml");
		  Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
		  deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_AggregateWithParallelFlow.xml","TopLevelTaeQueue");

	     // deployJavaService(eeUimaEngine, relativePath + "/Deploy_AggregateWithParallelFlow.xml");
	      initialize(uimaAsClient, appCtx);
	      // Wait until the top level service returns its metadata
	      waitUntilInitialized();
	    } catch( Exception e) {
				throw e;
	    }


	    CAS cas = uimaAsClient.getCAS();
	    System.out.println(" Sending CAS to kick off aggregate w/colocated CasMultiplier");
	    uimaAsClient.sendCAS(cas);

	    System.out.println(" Waiting 1 seconds");
	    Thread.sleep(1000);

	    System.out.println(" Trying to stop service");
	    uimaAsClient.stop();
	    System.out.println(" stop() returned!");
	    Object o = new Object();
	    
	  }
	  
	 

	  /**
	   * Test use of a JMS Service Adapter. Invoke from a synchronous aggregate to emulate usage from
	   * RunAE or RunCPE.
	   * 
	   * @throws Exception
	   */
	    @Test
	    public void testJmsServiceAdapterOverJava() throws Exception {
	    	testJmsServiceAdapter(Transport.Java);
	    }
	    @Test
	    public void testJmsServiceAdapterOverJms() throws Exception {
	    	testJmsServiceAdapter(Transport.JMS);
	    }
	    public void testJmsServiceAdapter(Transport transport) throws Exception {
		  Logger.getLogger(this.getClass()).info("-------------- testJmsServiceAdapter -------------");
		  //setUp();
		  UimaAsynchronousEngine uimaAsClient = getClient(transport);
		  //BaseUIMAAsynchronousEngine_impl eeUimaEngine = new BaseUIMAAsynchronousEngine_impl();
	    try {

	        deployJmsService(uimaAsClient, relativePath.concat("/Deploy_NoOpAnnotator.xml"));
			Map<String, Object> appCtx = defaultContext("TopLevelTaeQueue");
			deployTopLevelService(appCtx, transport, uimaAsClient, relativePath + "/Deploy_SyncAggregateWithJmsService.xml","TopLevelTaeQueue");

	        //deployJavaService(eeUimaEngine, relativePath + "/Deploy_SyncAggregateWithJmsService.xml");
	        runTest(appCtx, uimaAsClient, String.valueOf(getMasterConnectorURI(broker)), "TopLevelTaeQueue",
	                1, PROCESS_LATCH);
	       
	    } catch( Exception e ) {
	    	throw e;
	    }
	  }



	  private static class TestListener extends UimaAsBaseCallbackListener implements Runnable {
	    private String casReferenceId = null;

	    private Object monitor = new Object();

	    public TestListener(TestUimaASExtended aTester) {
	    }

	    public void collectionProcessComplete(EntityProcessStatus arg0) {
	      // TODO Auto-generated method stub

	    }

	    public void onBeforeMessageSend(UimaASProcessStatus status) {
	      System.out.println("TestListener Received onBeforeMessageSend Notification with Cas:"
	              + status.getCasReferenceId());
	    }

	    public void entityProcessComplete(CAS aCAS, EntityProcessStatus aProcessStatus) {
	      if (aProcessStatus.isException()) {
	        if (aProcessStatus instanceof UimaASProcessStatus) {
	          casReferenceId = ((UimaASProcessStatus) aProcessStatus).getCasReferenceId();
	          if (casReferenceId != null) {
	            synchronized (monitor) {
	              monitor.notifyAll();
	            }
	          }
	        }
	      }
	    }

	    public void initializationComplete(EntityProcessStatus arg0) {
	      // TODO Auto-generated method stub

	    }

	    public String getCasReferenceId() {
	      synchronized (monitor) {
	        while (casReferenceId == null) {
	          try {
	            monitor.wait();
	          } catch (InterruptedException e) {
	          }
	        }
	      }
	      return casReferenceId;
	    }

	    public void doStop() {
	    }

	    public void run() {
	      System.out.println("Stopping TestListener Callback Listener Thread");
	    }
	  }
	  
	  private class Worker implements Runnable {

		  
		List<Future<?>> list = new ArrayList<Future<?>>();
		
		public Worker(List<Future<?>> list ) {
			this.list = list;
		}
		@Override
		public void run() {
			for (int i = 0; i < list.size(); i++) {
		    	try {
			    	list.get(i).get();//1, TimeUnit.SECONDS);
		    	} catch( Exception e) {
		    		e.printStackTrace();
			    	list.get(i).cancel(true);
		    	}
		    }

		}
		  
	  }
	
}
