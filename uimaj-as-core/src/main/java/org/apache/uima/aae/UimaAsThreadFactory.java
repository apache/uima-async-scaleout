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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController;
import org.apache.uima.aae.controller.PrimitiveAnalysisEngineController_impl;
import org.apache.uima.util.Level;

/**
 * Custom ThreadFactory for use in the TaskExecutor. The TaskExecutor is plugged in by Spring from
 * spring xml file generated by dd2spring. The TaskExecutor is only defined for PrimitiveControllers
 * and its main purpose is to provide thread pooling and management. Each new thread produced by
 * this ThreadFactory is used to initialize a dedicated AE instance in the PrimitiveController.
 * 
 * 
 */
public class UimaAsThreadFactory implements ThreadFactory {
  
  private static final Class CLASS_NAME = UimaAsThreadFactory.class;
  private static final String THREAD_POOL = "[UIMA AS ThreadPool ";
  private PrimitiveAnalysisEngineController controller;

  private ThreadGroup theThreadGroup;

  private String threadNamePrefix=null;
  
  private boolean isDaemon=false;
  
  public static AtomicInteger poolIdGenerator = new AtomicInteger();
  
  private final int poolId = poolIdGenerator.incrementAndGet();
  
  public UimaAsThreadFactory(ThreadGroup tGroup) {
    this(tGroup,null);
  }
    /**
   * 
   * 
   * @param tGroup
   * @param aController
   */
  public UimaAsThreadFactory(ThreadGroup tGroup, PrimitiveAnalysisEngineController aController) {
    controller = aController;
    theThreadGroup = tGroup;
  }
  public void setThreadNamePrefix(String prefix) {
    threadNamePrefix = prefix;
  }
  public void setThreadGroup( ThreadGroup tGroup) {
    theThreadGroup = tGroup;
  }
  public void setDaemon(boolean daemon) {
 //   isDaemon = daemon;
  }
  public void stop() {
  }

  /**
   * Creates a new thread, initializes instance of AE via a call on a given PrimitiveController.
   * Once the thread finishes initializing AE instance in the controller, it calls run() on a given
   * Runnable. This Runnable is a Worker instance managed by the TaskExecutor. When the thread calls
   * run() on the Runnable it blocks until the Worker releases it.
   */
  public Thread newThread(final Runnable r) {
    Thread newThread = null;
    try {
      newThread = new Thread(theThreadGroup, new Runnable() {
        public void run() {
          if ( threadNamePrefix == null ) {
             if ( controller != null ) {
               threadNamePrefix = THREAD_POOL+poolId+"] "+controller.getComponentName() + " Process Thread";
             } else {
               threadNamePrefix = THREAD_POOL+poolId+"] ";
             }
          } 
          Thread.currentThread().setName( threadNamePrefix +" - "                 
                          + Thread.currentThread().getId());
          try {
            if (controller != null && !controller.threadAssignedToAE()) {
              // call the controller to initialize next instance of AE. Once initialized this
              // AE instance process() method will only be called from this thread
			UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, getClass().getName(),
					"UimaAsThreadFactory.run()", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
					"UIMAEE_calling_ae_initialize__INFO", new Object[] {controller.getComponentName(),Thread.currentThread().getId()});
              controller.initializeAnalysisEngine();
            }
            // Call given Worker (Runnable) run() method and block. This call block until the
            // TaskExecutor is terminated.
            r.run();
          } catch (Throwable e) {
        	  UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, getClass().getName(),
                      "UimaAsThreadFactory", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
                      "UIMAEE_exception__WARNING", e);
        	  if ( controller != null ) {
              	  Exception ex;
              	  if ( e instanceof Exception ) {
              		  ex = (Exception)e;
              	  } else {
              		  ex = new Exception(e);
              	  }
            	  controller.notifyListenersWithInitializationStatus(ex);
        	  } 
            return;
          } finally {
              if ( controller instanceof PrimitiveAnalysisEngineController_impl ) {
       			 UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, getClass().getName(),
        					"UimaAsThreadFactory.run()", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
        					"UIMAEE_process_thread_exiting__INFO", new Object[] {controller.getComponentName(),Thread.currentThread().getId()});
            	  ((PrimitiveAnalysisEngineController_impl)controller).destroyAE();
        			 UIMAFramework.getLogger(CLASS_NAME).logrb(Level.INFO, getClass().getName(),
         					"UimaAsThreadFactory.run()", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
         					"UIMAEE_ae_instance_destroy_called__INFO", new Object[] {controller.getComponentName(),Thread.currentThread().getId()});
              }
          }
        
        }
      });
    } catch (Exception e) {
      if (UIMAFramework.getLogger(CLASS_NAME).isLoggable(Level.WARNING)) {
        if ( controller != null ) {
          UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, CLASS_NAME.getName(),
                  "UimaAsThreadFactory", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
                  "UIMAEE_service_exception_WARNING", controller.getComponentName());
        }

        UIMAFramework.getLogger(CLASS_NAME).logrb(Level.WARNING, getClass().getName(),
                "UimaAsThreadFactory", UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
                "UIMAEE_exception__WARNING", e);
      }
    }
    if ( newThread != null ) {
      newThread.setDaemon(isDaemon);
    }
    return newThread;
  }
}
