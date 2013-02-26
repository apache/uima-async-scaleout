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

package org.apache.uima.examples.as;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.util.ProcessTraceEvent;
import org.apache.uima.util.XMLInputSource;

/**
 * Example application that calls a Remote Asynchronous Analysis Engine on a collection.
 * <p>
 * Arguments: brokerUrl endpoint [options] [-t Timeout] [-i]
 * <p>
 * This connects to a remote AE at specified brokerUrl and endpoint (which must match what is in the
 * service's deployment descriptor. The following optional arguments are accepted:
 * <ul>
 * <li>-d Specifies a deployment descriptor. The specified service will be deployed before
 * processing begin, and the service will be undeployed after processing completes. Multiple -d
 * entries can be given.</li>
 * <li>-c Specifies a CollectionReader descriptor. The client will read CASes from the
 * CollectionReader and send them to the service for processing. If this option is omitted, one
 * empty CAS will be sent to the service (useful for services containing a CAS Multiplier acting as
 * a collection reader).</li>
 * <li>-p Specifies CAS pool size, which determines the maximum number of requests that can be
 * outstanding.</li>
 * <li>-f Specifies the initial FS heap size in bytes of each CAS in the pool.</li>
 * <li>-o Specifies an Output Directory. All CASes received by the client's CallbackListener will be
 * serialized to XMI in the specified OutputDir. If omitted, no XMI files will be output.</li>
 * <li>-t Specifies a timeout period in seconds. If a CAS does not return within this time period it
 * is considered an error. By default there is no timeout, so the client will wait forever.</li>
 * <li>-i Causes the client to ignore errors returned from the service. If not specified, the client
 * terminates on the first error.</li>
 * <li>-log Output details on each process request.</li>
 * <li>-uimaEeDebug true causes various debugging things to happen, including *not* deleting the
 * generated spring file generated by running dd-2-spring. This parameter only affects deployments
 * specified using the -d parameter that follow it in the command line sequence.</li>
 * </ul>
 */
public class RunRemoteAsyncAE {
  private String brokerUrl = null;

  private String endpoint = null;

  private File collectionReaderDescriptor = null;

  private int casPoolSize = 2;

  private int fsHeapSize = 2000000;

  private File outputDir = null;

  private int timeout = 0;

  private int getmeta_timeout = 60;

  private int cpc_timeout = 0;

  private boolean ignoreErrors = false;

  private boolean logCas = false;

  /**
   * Start time of the processing - used to compute elapsed time.
   */
  private static long mStartTime = System.nanoTime() / 1000000;

  private UimaAsynchronousEngine uimaEEEngine = null;

  Map<String, Object> appCtx;

  // For logging CAS activity
  private ConcurrentHashMap casMap = new ConcurrentHashMap();

  /**
   * Constructor for the class. Parses command line arguments and sets the values of fields in this
   * instance. If command line is invalid prints a message and calls System.exit().
   * 
   * @param args
   *          command line arguments into the program - see class description
   */
  public RunRemoteAsyncAE(String args[]) throws Exception {

    appCtx = new HashMap<String, Object>();
    appCtx.put(UimaAsynchronousEngine.DD2SpringXsltFilePath, System.getenv("UIMA_HOME")
            + "/bin/dd2spring.xsl");
    appCtx.put(UimaAsynchronousEngine.SaxonClasspath, "file:" + System.getenv("UIMA_HOME")
            + "/saxon/saxon8.jar");

    // parse command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        if (args[i].equals("-log")) {
          logCas = true;
        } else if (args[i].equals("-b")) {
          appCtx.put(UimaAsynchronousEngine.SerializationStrategy, "binary");
        } else if (args[i].equals("-i")) {
          ignoreErrors = true;
        } else {
          if (i + 1 >= args.length) {
            printUsageAndExit();
          }
          if (args[i].equals("-c")) {
            collectionReaderDescriptor = new File(args[++i]);
            if (!collectionReaderDescriptor.exists()) {
              System.err.println("Collection Reader Descriptor file "
                      + collectionReaderDescriptor.getPath() + " does not exist.");
              printUsageAndExit();
            }
          } else if (args[i].equals("-p")) {
            casPoolSize = Integer.parseInt(args[++i]);
          } else if (args[i].equals("-f")) {
            fsHeapSize = Integer.parseInt(args[++i]);
          } else if (args[i].equals("-o")) {
            outputDir = new File(args[++i]);
            if (outputDir.exists()) {
              if (!outputDir.isDirectory()) {
                System.err.println(outputDir.getPath() + " is not a directory.");
                printUsageAndExit();
              }
            } else {
              if (!outputDir.mkdirs()) {
                System.err.println("Could not create directory " + outputDir.getPath());
                printUsageAndExit();
              }
            }
          } else if (args[i].equals("-d")) {
            if (uimaEEEngine == null) {
              // create Asynchronous Engine
              uimaEEEngine = new BaseUIMAAsynchronousEngine_impl();
            }
            String service = args[++i];
            System.out.println("Attempting to deploy " + service + " ...");

            uimaEEEngine.deploy(service, appCtx);
          } else if (args[i].equals("-t")) {
            timeout = Integer.parseInt(args[++i]);
          } else if (args[i].equals("-it")) {
            getmeta_timeout = Integer.parseInt(args[++i]);
          } else if (args[i].equals("-cpct")) {
            cpc_timeout = Integer.parseInt(args[++i]);
          } else if (args[i].equals(UimaAsynchronousEngine.UimaEeDebug)) {
            appCtx.put(UimaAsynchronousEngine.UimaEeDebug, args[++i]);
          } else {
            System.err.println("Unknown switch " + args[i]);
            printUsageAndExit();
          }
        }
      } else {
        if (brokerUrl == null) {
          brokerUrl = args[i];
          // Set System property that may be used by Spring while resolving a broker URL
          // placeholder in the deployment descriptor. This is only used when launching
          // RunRemoteAsyncAE with -d option.
          System.setProperty("defaultBrokerURL", brokerUrl);
        } else if (endpoint == null) {
          endpoint = args[i];
        } else {
          printUsageAndExit();
        }
      }
    }
    if (brokerUrl == null || endpoint == null) {
      printUsageAndExit();
    }
  }

  public void run() throws Exception {

    if (uimaEEEngine == null) {
      // create Asynchronous Engine
      uimaEEEngine = new BaseUIMAAsynchronousEngine_impl();
    }

    // add Collection Reader if specified
    if (collectionReaderDescriptor != null) {
      CollectionReaderDescription collectionReaderDescription = UIMAFramework.getXMLParser()
              .parseCollectionReaderDescription(new XMLInputSource(collectionReaderDescriptor));

      CollectionReader collectionReader = UIMAFramework
              .produceCollectionReader(collectionReaderDescription);

      uimaEEEngine.setCollectionReader(collectionReader);
    }
    uimaEEEngine.addStatusCallbackListener(new StatusCallbackListenerImpl());

    // set server URI and Endpoint
    // Add Broker URI
    appCtx.put(UimaAsynchronousEngine.ServerUri, brokerUrl);
    // Add Queue Name
    appCtx.put(UimaAsynchronousEngine.Endpoint, endpoint);
    // Add timeouts (UIMA EE expects it in milliseconds, but we use seconds on the command line)
    appCtx.put(UimaAsynchronousEngine.Timeout, timeout * 1000);
    appCtx.put(UimaAsynchronousEngine.GetMetaTimeout, getmeta_timeout * 1000);
    appCtx.put(UimaAsynchronousEngine.CpcTimeout, cpc_timeout * 1000);

    // Add the Cas Pool Size and initial FS heap size
    appCtx.put(UimaAsynchronousEngine.CasPoolSize, casPoolSize);
    appCtx.put(UIMAFramework.CAS_INITIAL_HEAP_SIZE, Integer.valueOf(fsHeapSize / 4).toString());

    // initialize
    uimaEEEngine.initialize(appCtx);

    // run
    if (logCas) {
      System.out.println("\nService-IPaddr\tSent\tDuration");
    }
    if (collectionReaderDescriptor != null) {
      uimaEEEngine.process();
    } else {
      // send an empty CAS
      CAS cas = uimaEEEngine.getCAS();
      uimaEEEngine.sendCAS(cas);
      uimaEEEngine.collectionProcessingComplete();
    }

    // if (logCas) {
    // System.out.println();
    // List<String> log = getLog();
    // Iterator logline = log.iterator();
    // while (logline.hasNext()) {
    // System.out.println(logline.next());
    // }
    // }
    uimaEEEngine.stop();
  }

  /**
   * 
   */
  private static void printUsageAndExit() {
    System.out
            .println("Usage: runRemoteAsyncAE brokerUrl endpoint [options]\n\n"
                    + "This connects to a remote AE at specified brokerUrl and endpoint (which must match what is in the service's\n"
                    + "deployment descriptor.\n\nThe following optional arguments are accepted:\n"
                    + "-d  Specifies a deployment descriptor. The specified service will be deployed before processing begins."
                    + " Multiple -d entries can be given.\n"
                    + "-c  Specifies a CollectionReader descriptor.  The client will read CASes from the CollectionReader"
                    + " and send them to the service for processing.  If this option is omitted, one empty CAS will be"
                    + " sent to the service (useful for services containing a CAS Multiplier acting as a collection reader).\n"
                    + "-b  Use binary serialization (default is xmi)\n"
                    + "-p  Specifies CAS pool size, which determines the maximum number of requests that can be outstanding.\n"
                    + "-f  Specifies the initial FS heap size in bytes of each CAS in the pool.\n"
                    + "-o  Specifies an Output Directory.  All CASes received by the client's CallbackListener will be serialized to XMI"
                    + " in the specified OutputDir.  If omitted, no XMI files will be output.\n"
                    + "-t  Specifies a timeout period in seconds.  If a CAS does not return within this time period it"
                    + " is considered an error.  By default there is no timeout, so the client will wait forever.\n"
                    + "-it  Specifies a timeout period in seconds.  If the initialization request does not return within this time period it"
                    + " is considered an error.  By default the timeout is 60 seconds.\n"
                    + "-cpct  Specifies a timeout period in seconds.  When all CAS requests are completed, a collection process complete"
                    + " command is sent.  By default there is no timeout for CPC, so the client will wait forever.\n"
                    + "-log Output details on each process request: IP address of service that handled the request,\n"
                    + "     time in ms the CAS was sent, duration in ms from sendCas to receipt.\n"
                    + "-i  Causes the client to ignore errors returned from the service.  If not specified, the client"
                    + " terminates on the first error (including a timeout if the -t option is specified.\n"
                    + "-uimaEeDebug true    This is optional. Leave it out for normal operation. If specified, causes"
                    + " additional debugging things to happen, including *not* deleting the generated Spring xml file generated"
                    + " from running dd2spring.  It only affects deployments specified using the -d parameter that follow it on"
                    + " the command line");
    System.exit(1);
  }

  /**
   * main class.
   * 
   * @param args
   *          Command line arguments - see class description
   */
  public static void main(String[] args) throws Exception {
    RunRemoteAsyncAE runner = new RunRemoteAsyncAE(args);
    runner.run();
  }

  /**
   * Callback Listener. Receives event notifications from CPE.
   * 
   * 
   */
  class StatusCallbackListenerImpl extends UimaAsBaseCallbackListener {
    int entityCount = 0;

    long size = 0;

    /**
     * Called when the initialization is completed.
     * 
     * @see org.apache.uima.collection.processing.StatusCallbackListener#initializationComplete()
     */
    public void initializationComplete(EntityProcessStatus aStatus) {
      if (aStatus != null && aStatus.isException()) {
        System.err.println("Error on getMeta call to remote service:");
        List exceptions = aStatus.getExceptions();
        for (int i = 0; i < exceptions.size(); i++) {
          ((Throwable) exceptions.get(i)).printStackTrace();
        }
        System.err.println("Terminating Client...");
        stop();
        
      }
      System.out.println("UIMA AS Service Initialization Complete");
    }
    private void stop() {
      try {
        uimaEEEngine.stop();
      } catch( Exception e) {
        
      }
      System.exit(1);
      
    }
    /**
     * Called when the collection processing is completed.
     * 
     * @see org.apache.uima.collection.processing.StatusCallbackListener#collectionProcessComplete()
     */
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
      if (aStatus != null && aStatus.isException()) {
        System.err.println("Error on collection process complete call to remote service:");
        List exceptions = aStatus.getExceptions();
        for (int i = 0; i < exceptions.size(); i++) {
          ((Throwable) exceptions.get(i)).printStackTrace();
        }
        System.err.println("Terminating Client...");
        stop();
      }
      System.out.print("Completed " + entityCount + " documents");
      if (size > 0) {
        System.out.print("; " + size + " characters");
      }
      System.out.println();
      long elapsedTime = System.nanoTime() / 1000000 - mStartTime;
      System.out.println("Time Elapsed : " + elapsedTime + " ms ");

      String perfReport = uimaEEEngine.getPerformanceReport();
      if (perfReport != null) {
        System.out.println("\n\n ------------------ PERFORMANCE REPORT ------------------\n");
        System.out.println(uimaEEEngine.getPerformanceReport());
      }
      // stop the JVM.
      stop();
    }

    /**
     * Called when the processing of a Document is completed. <br>
     * The process status can be looked at and corresponding actions taken.
     * 
     * @param aCas
     *          CAS corresponding to the completed processing
     * @param aStatus
     *          EntityProcessStatus that holds the status of all the events for aEntity
     */
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
      if (aStatus != null) {
        if (aStatus.isException()) {
          System.err.println("Error on process CAS call to remote service:");
          List exceptions = aStatus.getExceptions();
          for (int i = 0; i < exceptions.size(); i++) {
            ((Throwable) exceptions.get(i)).printStackTrace();
          }
          if (!ignoreErrors) {
            System.err.println("Terminating Client...");
            stop();
          }
        }
        if (logCas) {
          String ip = "no IP";
          List eList = aStatus.getProcessTrace().getEventsByComponentName("UimaEE", false);
          for (int e = 0; e < eList.size(); e++) {
            ProcessTraceEvent event = (ProcessTraceEvent) eList.get(e);
            if (event.getDescription().equals("Service IP")) {
              ip = event.getResultMessage();
            }
          }
          String casId = ((UimaASProcessStatus) aStatus).getCasReferenceId();
          if (casId != null) {
            long current = System.nanoTime() / 1000000 - mStartTime;
            if (casMap.containsKey(casId)) {
              Object value = casMap.get(casId);
              if (value != null && value instanceof Long) {
                long start = ((Long) value).longValue();
                System.out.println(ip + "\t" + start + "\t" + (current - start));
              }
            }
          }

        } else {
          System.out.print(".");
          if (0 == (entityCount + 1) % 50) {
            System.out.print((entityCount + 1) + " processed\n");
          }
        }
      }

      // if output dir specified, dump CAS to XMI
      if (outputDir != null) {
        // try to retrieve the filename of the input file from the CAS
        File outFile = null;
        Type srcDocInfoType = aCas.getTypeSystem().getType(
                "org.apache.uima.examples.SourceDocumentInformation");
        if (srcDocInfoType != null) {
          FSIterator it = aCas.getIndexRepository().getAllIndexedFS(srcDocInfoType);
          if (it.hasNext()) {
            FeatureStructure srcDocInfoFs = it.get();
            Feature uriFeat = srcDocInfoType.getFeatureByBaseName("uri");
            Feature offsetInSourceFeat = srcDocInfoType.getFeatureByBaseName("offsetInSource");
            String uri = srcDocInfoFs.getStringValue(uriFeat);
            int offsetInSource = srcDocInfoFs.getIntValue(offsetInSourceFeat);
            File inFile;
            try {
              inFile = new File(new URL(uri).getPath());
              String outFileName = inFile.getName();
              if (offsetInSource > 0) {
                outFileName += ("_" + offsetInSource);
              }
              outFileName += ".xmi";
              outFile = new File(outputDir, outFileName);
            } catch (MalformedURLException e1) {
              // invalid URI, use default processing below
            }
          }
        }
        if (outFile == null) {
          outFile = new File(outputDir, "doc" + entityCount);
        }
        try {
          FileOutputStream outStream = new FileOutputStream(outFile);
          try {
            XmiCasSerializer.serialize(aCas, outStream);
          } finally {
            outStream.close();
          }
        } catch (Exception e) {
          System.err.println("Could not save CAS to XMI file");
          e.printStackTrace();
        }
      }

      // update stats
      entityCount++;
      String docText = aCas.getDocumentText();
      if (docText != null) {
        size += docText.length();
      }

      // Called just before sendCas with next CAS from collection reader
    }

    public void onBeforeMessageSend(UimaASProcessStatus status) {
      long current = System.nanoTime() / 1000000 - mStartTime;
      casMap.put(status.getCasReferenceId(), current);
    }
    /**
     * This method is called when a CAS is picked up by remote UIMA AS
     * from a queue right before processing. This callback identifies
     * on which machine the CAS is being processed and by which UIMA AS
     * service (PID).
     */
    public void onBeforeProcessCAS(UimaASProcessStatus status, String nodeIP, String pid) {
      
    }

  }

}
