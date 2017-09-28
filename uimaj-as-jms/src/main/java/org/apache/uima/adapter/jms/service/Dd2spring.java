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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.UIMAEE_Constants;
import org.apache.uima.adapter.jms.JmsConstants;
import org.apache.uima.util.Level;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class Dd2spring {

	private static final Class<Dd2spring> THIS_CLASS = Dd2spring.class;

	private ClassLoader saxonClassLoader;

	/**
	 * Test driver arg = path_to_source, path_to_xslt, path_to_saxon_jar,
	 * uima-as-debug flag
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Dd2spring()
					.convertDd2Spring(args[0], args[1], args[2], args[3]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File convertDd2Spring(String ddFilePath,
			String dd2SpringXsltFilePath, String saxonClasspath,
			String uimaAsDebug) throws Exception {

		URL urlForSaxonClassPath;
		try {
			urlForSaxonClassPath = new URL(saxonClasspath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			UIMAFramework
					.getLogger(THIS_CLASS)
					.logrb(Level.CONFIG,
							THIS_CLASS.getName(),
							"convertDD2Spring",
							JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
							"UIMA_dd2spring_Cannot_convert_saxon_classpath_to_a_URL_SEVERE",
							new Object[] { saxonClasspath });
			return null;
		}

		File tempFile;
		try {
			tempFile = File.createTempFile("UIMAdd2springOutput", ".xml");
		} catch (IOException e) {
			e.printStackTrace();
			UIMAFramework.getLogger(THIS_CLASS).logrb(Level.CONFIG,
					THIS_CLASS.getName(), "convertDD2Spring",
					JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
					"UIMA_dd2spring_cant_create_temp_output_file_SEVERE");
			return null;
		}

		// UIMA-5022 No longer capture output and scan for "ERROR: " as that hid
		// errors that Saxon followed by calling exit!
		// Processing now terminates on the first error.
		convertDd2Spring(tempFile, ddFilePath, dd2SpringXsltFilePath,
				urlForSaxonClassPath);

		// delete the file when terminating if
		// a) uimaAsDebug is not specified (is null) or
		// b) uimaAsDebug is specified, but is ""
		if (null == uimaAsDebug || uimaAsDebug.equals("")) {
			tempFile.deleteOnExit();
		}

		return tempFile;
	}

	private void testForSaxon9InClasspath() throws Exception {
		// UIMA-5117 Check for saxon9. If it is in the users's classpath an NPE
		// is thrown in
		// net.sf.saxon.event.ReceivingContentHandler.getNodeName while handling
		// a getMeta request.
		try {
			Class<?> saxonVersionClass = Class.forName("net.sf.saxon.Version");
			Method versionMethod = saxonVersionClass
					.getMethod("getProductVersion");
			String version = (String) versionMethod.invoke(null);
			if (version.startsWith("9")) {
				UIMAFramework.getLogger(THIS_CLASS).logrb(Level.SEVERE,
						THIS_CLASS.getName(), "convertDD2Spring",
						UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
						"UIMAEE_exception__SEVERE",
						new Object[] { "saxon9 must not be in classpath" });
				throw new Dd2springException(
						"saxon9 found in classpath - dd2spring transformation and UIMA-AS do not support saxon9");
			}
		} catch (ClassNotFoundException e) {
			// OK - saxon not in classpath
		}
	}

	/**
	 * 
	 * @param tempFile
	 *            file to hold generated Spring from dd2spring transform
	 * @param ddFilePath
	 *            file path to UIMA Deployment Descriptor - passed to saxon
	 * @param dd2SpringXsltFilePath
	 *            file path to dd2spring.xslt transformation file - passed to
	 *            saxon
	 * @param saxonClasspathURL
	 *            classpath for saxon8.jar
	 */
	public void convertDd2Spring(File tempFile, String ddFilePath,
			String dd2SpringXsltFilePath, URL saxonClasspathURL)
			throws Exception {

		testForSaxon9InClasspath();

		// UIMA-5117 - Add shutdown hook so can log when saxon gives up and
		// calls exit :(
		ShutdownHook shutdownHook = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		// Create a classloader with saxon8 that delegates to the user's
		// classloader
		ClassLoader currentClassloader = Thread.currentThread()
				.getContextClassLoader();
		if (null == saxonClassLoader) {
			URL[] classLoaderUrls = new URL[] { saxonClasspathURL };
			saxonClassLoader = new URLClassLoader(classLoaderUrls,
					currentClassloader);
		}
		// configure Saxon with these settings
		SaxonInputs saxonConfig = new SaxonInputs(ddFilePath, tempFile,
				dd2SpringXsltFilePath, saxonClassLoader, currentClassloader);
		
		// creates either command line or java based interface to Saxon
		SaxonInterface saxon = SaxonInterfaceFactory.newSaxonInterface(
				saxonConfig);

		try {

			saxon.convertDD2Spring();

		} catch (ClassNotFoundException e) {
			System.err.println("Error - can't load Saxon jar from "
					+ saxonClasspathURL + " for dd2spring transformation.");
			e.printStackTrace();
			UIMAFramework.getLogger(THIS_CLASS).logrb(Level.SEVERE,
					THIS_CLASS.getName(), "convertDD2Spring",
					JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
					"UIMA_dd2spring_saxon_missing_SEVERE");
			throw e;
		} catch (Exception e) {
			System.err.println("Error - dd2spring transformation failed:");
			e.printStackTrace();
			UIMAFramework.getLogger(THIS_CLASS).logrb(Level.SEVERE,
					THIS_CLASS.getName(), "convertDD2Spring",
					JmsConstants.JMS_LOG_RESOURCE_BUNDLE,
					"UIMA_dd2spring_internal_error_calling_saxon");
			throw e;
		} finally {
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
		}
		return;
	}

	// Shutdown hook that reports when Saxon calls exit!
	private class ShutdownHook extends Thread {
		public void run() {
			System.err
					.println("ERROR in dd2spring Saxon transformation ... System.exit called");
			System.err.flush();
		}
	}

	/**
	 * The SaxonInputs class is used to configure Saxon
	 *
	 */
	private class SaxonInputs {
		String ddFilePath;
		File springFilePath;
		String xsltPath;
		ClassLoader currentClassLoader;
		ClassLoader saxonClassLoader;
		public SaxonInputs(String ddFilePath, File springFilePath,
				String xsltPath, ClassLoader saxonClassloader,
				ClassLoader currentClassloader) {
			super();
			this.ddFilePath = ddFilePath;
			this.springFilePath = springFilePath;
			this.xsltPath = xsltPath;
			this.currentClassLoader = currentClassloader;
			this.saxonClassLoader = saxonClassloader;
		}

		public ClassLoader getCurrentClassLoader() {
			return currentClassLoader;
		}
		public ClassLoader getSaxonClassLoader() {
			return saxonClassLoader;
		}

		public String getDDFilePath() {
			return ddFilePath;
		}

		public File getSpringFilePath() {
			return springFilePath;
		}

		public String getXSLTPath() {
			return xsltPath;
		}
	}
	// Factory to produce desired Saxon interface. Currently two
	// are supported: command line based and java API based. The
	// default is java API interface. The command line is used
	// to provide Saxon a custom parser.
	private static class SaxonInterfaceFactory {
		public static SaxonInterface newSaxonInterface(	SaxonInputs saxonConfig) {
			SaxonInterface saxon;
			if (System.getProperty("uima.framework_impl") != null) {
				// The command line based Saxon interface is used to plug-in
				// custom parser into Saxon
				saxon = new SaxonCommandLineInterface(saxonConfig);
			} else {
				// Default, use java API based Saxon interface
				saxon = new SaxonJavaInterface(saxonConfig);
			}
			return saxon;
		}
	}

	private interface SaxonInterface {
		public void convertDD2Spring() throws Exception;
	}

	private static class SaxonCommandLineInterface implements SaxonInterface {
		private List<String> argsForSaxon = new ArrayList<String>();
		private SaxonInputs saxonConfig;
		
		SaxonCommandLineInterface(SaxonInputs saxonConfig) {
			// args for saxon
			// -l -s deployment_descriptor} -o output_file_path
			// dd2spring.xsl_file_path <-x sax_parser_class>
			// If a custom framework includes a custom XML parser we may also
			// need a custom parser for Saxon,
			// so check for the existence of a class with "_SAXParser" appended
			// to the framework name.
			this.saxonConfig = saxonConfig;
			argsForSaxon.add("-l"); // turn on line numbers
			argsForSaxon.add("-s"); // source file
			argsForSaxon.add(saxonConfig.getDDFilePath()); // source file
			argsForSaxon.add("-o"); // output file
			argsForSaxon.add(saxonConfig.getSpringFilePath().getAbsolutePath()); // output
																					// file
			argsForSaxon.add(saxonConfig.getXSLTPath()); // xslt transform to
															// apply
			// test if there is a custom parser defined
			String uimaFrameworkClass = System
					.getProperty("uima.framework_impl");

			if (uimaFrameworkClass != null) {
				String saxonParserClass = uimaFrameworkClass + "_SAXParser";
				try {
					saxonConfig.getSaxonClassLoader().loadClass(saxonParserClass);
					argsForSaxon.add("-x");
					argsForSaxon.add(saxonParserClass);
				} catch (ClassNotFoundException e) {
					// No parser class defined
				}
			}
			UIMAFramework.getLogger(THIS_CLASS).log(Level.INFO,
					"Saxon args: " + argsForSaxon);

		}

		public void convertDD2Spring() throws Exception {
			try {
				// Set the thread classloader so that all classes are loaded from this
				Thread.currentThread().setContextClassLoader(saxonConfig.getSaxonClassLoader());
				Class<?> mainStartClass = null;
				mainStartClass = Class.forName("net.sf.saxon.Transform", true,
						saxonConfig.getSaxonClassLoader());
				Method mainMethod = mainStartClass
						.getMethod("main", String[].class);
				mainMethod.invoke(null, new Object[] { argsForSaxon
						.toArray(new String[argsForSaxon.size()]) });

			} finally {
				// Restore original classloader and remove used shutdown hook
				Thread.currentThread().setContextClassLoader(saxonConfig.getCurrentClassLoader());

			}
		}

	}

	private static class SaxonJavaInterface implements SaxonInterface {
		private SaxonInputs saxonConfig;

		SaxonJavaInterface(SaxonInputs saxonConfig) {
			this.saxonConfig = saxonConfig;
		}

		private void configure(Object xmlReaderObject) throws Exception {
			Class<?> xmlReaderClass = Class.forName("org.xml.sax.XMLReader",
					true, saxonConfig.getSaxonClassLoader());
			Method setFeatureMethod = xmlReaderClass.getMethod("setFeature",
					new Class[] { String.class, boolean.class });
			setFeatureMethod.invoke(xmlReaderObject,
					"http://xml.org/sax/features/external-general-entities",
					false);
			setFeatureMethod.invoke(xmlReaderObject,
					"http://xml.org/sax/features/external-parameter-entities",
					false);
			setFeatureMethod
					.invoke(xmlReaderObject,
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);
			setFeatureMethod.invoke(xmlReaderObject,
					"http://apache.org/xml/features/disallow-doctype-decl",
					true);

		}

		private void beforeProcess() {
			/* *********************************************************************************** */
			/*
			 * The following properties are required by Saxon. Dont remove these
			 * settings
			 */
			/* *********************************************************************************** */

			System.setProperty("javax.xml.parsers.SAXParserFactory",
					"org.apache.xerces.jaxp.SAXParserFactoryImpl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");

		}

		private void afterProcess() {
			/* *********************************************************************************** */
			/*
			 * Don't remove both clearProperty() calls below. Downstream parsers
			 * may get confused.
			 */
			/* *********************************************************************************** */
			System.clearProperty("javax.xml.parsers.SAXParserFactory");
			System.clearProperty("javax.xml.transform.TransformerFactory");
			/* *********************************************************************************** */

		}

		public void convertDD2Spring() throws Exception {
			beforeProcess();
			BufferedWriter writer = null;
			try {
				// Set the thread classloader so that all classes are loaded from this
				Thread.currentThread().setContextClassLoader(saxonConfig.getSaxonClassLoader());

				Class<?> configClass = Class.forName(
						"net.sf.saxon.Configuration", true, saxonConfig.getSaxonClassLoader());
				Object oc = configClass.newInstance();
				Method getXMLReaderMethod = configClass
						.getMethod("getStyleParser");

				// get Saxon xml parser
				Object xmlReaderObject = getXMLReaderMethod.invoke(oc);
				configure(xmlReaderObject);
				StringWriter out = new StringWriter();

				TransformerFactory tFactory = TransformerFactory.newInstance();

				StreamSource xslSource = new StreamSource(new File(
						saxonConfig.getXSLTPath()));
				StreamResult xmlResult = new StreamResult(out);
				Transformer transformer = tFactory.newTransformer(xslSource);
				// Need an absolute file path for the dd. This is fed to
				// InputSource below
				// Without absolute DD path, relative paths dont work.
				File ddAbsFilePath = new File(saxonConfig.getDDFilePath());
				// System.out.println("DD:"+ddAbsFilePath.getAbsolutePath());
				SAXSource source = new SAXSource(new InputSource(
						ddAbsFilePath.getAbsolutePath()));
				source.setXMLReader((XMLReader) xmlReaderObject);

				transformer.transform(source, xmlResult);
				// Save transformed DD for loading into Spring Framework
				writer = new BufferedWriter(new FileWriter(
						saxonConfig.getSpringFilePath()));
				writer.write(out.toString());
				
				if ( UIMAFramework
					.getLogger(THIS_CLASS)
					.isLoggable(Level.FINEST) ) {
					System.out.println("Finished Writing to temp file:"
							+ saxonConfig.getSpringFilePath().getAbsolutePath());
					String result = out.toString();
					System.out.println(result);

				}
				String result = out.toString();
				System.out.println(result);

			} finally {
				if ( writer != null ) {
					try {
						writer.close();
					} catch(Exception e){}
				}

				afterProcess();
				// Restore original classloader and remove used shutdown hook
				Thread.currentThread().setContextClassLoader(saxonConfig.getCurrentClassLoader());

			}

		}

	}
}
