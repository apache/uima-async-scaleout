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

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.error.AsynchAEException;
import org.apache.uima.impl.UimaVersion;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

public class VersionCompatibilityChecker {

	public static void check(Class<?> claz, String componentName, String method) throws ResourceInitializationException {
		boolean majorMatch = UimaAsVersion.getMajorVersion() == UimaVersion.getMajorVersion();
		boolean minorOK = UimaAsVersion.getMinorVersion() <= UimaVersion.getMinorVersion();
		if ( !majorMatch || (majorMatch && !minorOK) ) {
	       UIMAFramework.getLogger(claz).logrb(
	                    Level.WARNING,
	                   claz.getName(),
	                   method,
	                    UIMAEE_Constants.JMS_LOG_RESOURCE_BUNDLE,
	                    "UIMAEE_incompatible_version_WARNING",
	                    new Object[] { componentName, UimaAsVersion.getFullVersionString(),
	                      UimaVersion.getFullVersionString() });
	       throw new ResourceInitializationException(new AsynchAEException(
	                    "Version of UIMA-AS is Incompatible with a Version of UIMA Core. UIMA-AS Version is built to depend on Core UIMA version:"
	                            + UimaAsVersion.getFullVersionString() + " but is running with version:"
	                            + UimaVersion.getFullVersionString()));
	    }

	}
}
