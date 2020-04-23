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

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class UimaASJunitTestFailFastListener extends RunListener {
	
	private RunNotifier runNotifier;

	public UimaASJunitTestFailFastListener() {
		
	}
	/**
	 * Default, empty constructor
	 */
	public UimaASJunitTestFailFastListener(RunNotifier runNotifier) {
		super();
		this.runNotifier = runNotifier;
	}

	/**
	 * Throws an exception to terminate the whole test run, on the first failure
	 */
	@Override
	public void testFailure(Failure failure) throws Exception {
		failure.getException().printStackTrace();
		System.err.println("!!!!!!!!!!!!!!! - Test Failed: "+failure.getDescription()+" Header:"+failure.getTestHeader());
		if ( runNotifier != null ) {
			this.runNotifier.pleaseStop();
			
		} else {
			//System.exit(-1);
		}
	}
}