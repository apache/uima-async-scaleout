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

package org.apache.uima.adapter.jms.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.util.Level;

public class Dd2spring {

  private static final Class<Dd2spring> THIS_CLASS = Dd2spring.class;

  private ClassLoader saxonClassLoader;
  
  /**
   * Test driver arg = path_to_source, path_to_xslt, path_to_saxon_jar, uima-as-debug flag
   * 
   * @param args
   */
  public static void main(String[] args) {
	  try {
		    new Dd2spring().convertDd2Spring(args[0], args[1], args[2], args[3]);
		  
	  } catch ( Exception e) {
		  e.printStackTrace();
	  }
  }

  public File convertDd2Spring(String ddFilePath, String dd2SpringXsltFilePath,
          String saxonClasspath, String uimaAsDebug) throws Exception {

    URL urlForSaxonClassPath;
    try {
      urlForSaxonClassPath = new URL(saxonClasspath);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      UIMAFramework.getLogger(THIS_CLASS).logrb(Level.CONFIG, THIS_CLASS.getName(),
              "convertDD2Spring", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
              "UIMA_dd2spring_Cannot_convert_saxon_classpath_to_a_URL_SEVERE",
              new Object[] { saxonClasspath });
      return null;
    }

    File tempFile;
    try {
      tempFile = File.createTempFile("UIMAdd2springOutput", ".xml");
    } catch (IOException e) {
      e.printStackTrace();
      UIMAFramework.getLogger(THIS_CLASS).logrb(Level.CONFIG, THIS_CLASS.getName(),
              "convertDD2Spring", JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
              "UIMA_dd2spring_cant_create_temp_output_file_SEVERE");
      return null;
    }

    // UIMA-5022 No longer capture output and scan for "ERROR: " as that hid
    // errors that Saxon followed by calling exit!
    // Processing now terminates on the first error.
    convertDd2Spring(tempFile, ddFilePath, dd2SpringXsltFilePath, urlForSaxonClassPath);

    // delete the file when terminating if
    // a) uimaAsDebug is not specified (is null) or
    // b) uimaAsDebug is specified, but is ""
    if (null == uimaAsDebug || uimaAsDebug.equals("")) {
      tempFile.deleteOnExit();
    }

    return tempFile;
  }

  /**
   *
   * @param tempFile
   *          file to hold generated Spring from dd2spring transform
   * @param ddFilePath
   *          file path to UIMA Deployment Descriptor - passed to saxon
   * @param dd2SpringXsltFilePath
   *          file path to dd2spring.xslt transformation file - passed to saxon
   * @param saxonClasspathURL
   *          classpath for saxon8.jar
   */
  public void convertDd2Spring(File tempFile, String ddFilePath, String dd2SpringXsltFilePath,
          URL saxonClasspathURL) throws Exception {

    // UIMA-5117 Check for saxon9.  If it is in the users's classpath an NPE is thrown in 
    // net.sf.saxon.event.ReceivingContentHandler.getNodeName while handling a getMeta request.
    try {
      Class<?> saxonVersionClass = Class.forName("net.sf.saxon.Version");
      Method versionMethod = saxonVersionClass.getMethod("getProductVersion");
      String version = (String) versionMethod.invoke(null);
      if (version.startsWith("9")) {
        UIMAFramework.getLogger(THIS_CLASS).logrb(Level.SEVERE, THIS_CLASS.getName(), "convertDD2Spring", 
                UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE, "UIMAEE_exception__SEVERE",
                new Object[] { "saxon9 must not be in classpath" });
        throw new Dd2springException("saxon9 found in classpath - dd2spring transformation and UIMA-AS do not support saxon9");
      }
    } catch (ClassNotFoundException e) {
      // OK - saxon not in classpath
    }
    
    // UIMA-5117 - Add shutdown hook so can log when saxon gives up and calls exit :(
    ShutdownHook shutdownHook = new ShutdownHook();
    Runtime.getRuntime().addShutdownHook(shutdownHook);
    
    // Create a classloader with saxon8 that delegates to the user's classloader
    ClassLoader currentClassloader = Thread.currentThread().getContextClassLoader();
    if (null == saxonClassLoader) {
      URL[] classLoaderUrls = new URL[] { saxonClasspathURL };
      saxonClassLoader = new URLClassLoader(classLoaderUrls, currentClassloader);
    }
      
    // args for saxon
    // -l -s deployment_descriptor} -o output_file_path dd2spring.xsl_file_path <-x sax_parser_class>
    // If a custom framework includes a custom XML parser we may also need a custom parser for Saxon,
    // so check for the existence of a class with "_SAXParser" appended to the framework name.

    List<String> argsForSaxon = new ArrayList<String>();
    String uimaFrameworkClass = System.getProperty("uima.framework_impl");
    if (uimaFrameworkClass != null) {
      String saxonParserClass = uimaFrameworkClass + "_SAXParser";
      try {
        saxonClassLoader.loadClass(saxonParserClass);
        argsForSaxon.add("-x");
        argsForSaxon.add(saxonParserClass);
      } catch (ClassNotFoundException e) {
        // No parser class defined
      }
    }
    argsForSaxon.add("-l"); // turn on line numbers
    argsForSaxon.add("-s"); // source file
    argsForSaxon.add(ddFilePath); // source file
    argsForSaxon.add("-o"); // output file
    argsForSaxon.add(tempFile.getAbsolutePath()); // output file
    argsForSaxon.add(dd2SpringXsltFilePath); // xslt transform to apply

    UIMAFramework.getLogger(THIS_CLASS).log(Level.INFO, "Saxon args: " + argsForSaxon);

    // Set the thread classloader so that all classes are loaded from this
    Thread.currentThread().setContextClassLoader(saxonClassLoader);
    Class<?> mainStartClass = null;
    try {
      mainStartClass = Class.forName("net.sf.saxon.Transform", true, saxonClassLoader);
      Method mainMethod = mainStartClass.getMethod("main", String[].class);
      mainMethod.invoke(null, new Object[] { argsForSaxon.toArray(new String[argsForSaxon.size()]) });
    } catch (ClassNotFoundException e) {
      System.err.println("Error - can't load Saxon jar from " + saxonClasspathURL + " for dd2spring transformation.");
      e.printStackTrace();
      UIMAFramework.getLogger(THIS_CLASS).logrb(Level.SEVERE, THIS_CLASS.getName(), "convertDD2Spring", 
              JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMA_dd2spring_saxon_missing_SEVERE");
      throw e;
    } catch (Exception e) {
      System.err.println("Error - dd2spring transformation failed:");
      e.printStackTrace();
      UIMAFramework.getLogger(THIS_CLASS).logrb(Level.SEVERE, THIS_CLASS.getName(), "convertDD2Spring", 
              JmsConstants.JMS_LOG_RESOURCE_BUNDLE, "UIMA_dd2spring_internal_error_calling_saxon");
      throw e;
    } finally {
      // Restore original classloader and remove used shutdown hook
      Thread.currentThread().setContextClassLoader(currentClassloader);
      Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }
    return;
  }

  // Shutdown hook that reports when Saxon calls exit!
  private class ShutdownHook extends Thread {
    public void run() {
      System.err.println("ERROR in dd2spring Saxon transformation ... System.exit called");
      System.err.flush();
    }
  }

}
