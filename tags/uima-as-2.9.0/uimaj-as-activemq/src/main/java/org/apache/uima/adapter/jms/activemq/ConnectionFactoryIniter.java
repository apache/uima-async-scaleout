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
package org.apache.uima.adapter.jms.activemq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ConnectionFactoryIniter {
	ActiveMQConnectionFactory factory;
	 String whiteListedPackages=null;
    List<String> defaultPackages = 
	Arrays.asList("org.apache.uima.aae.error",
			 "org.apache.uima.analysis_engine",
			 "org.apache.uima.resource",
			 "org.apache.activemq",
			 "org.fusesource.hawtbuf",
			 "com.thoughtworks.xstream.mapper",
			 "java.lang",
			 "java.util");
	public ConnectionFactoryIniter(ActiveMQConnectionFactory factory) {
		this.factory = factory;
	    whiteListedPackages = 
	    		System.getProperty("uima.as.white.list.packages");
	}
	public void whiteListPackages() {
		if ( whiteListedPackages != null && whiteListedPackages.trim().length() > 0 ) {
			whiteListPackages(whiteListedPackages);
		} else {
			whiteListPackages(defaultPackages);
		}
	}
	public void whiteListPackages(List<String> packageList) {
		if ( !packageList.equals(defaultPackages)) {
			packageList.addAll(defaultPackages);
		}
		updateFactory(packageList);
	}
	public void whiteListPackages(String packageListAsString ) {
    	String[] pkgArray = packageListAsString.split(",");
    	List<String> list = new ArrayList<String>();
    	for( String pkg : pkgArray ) {
    		list.add(pkg.trim());
    	}
    	whiteListPackages(list);
	}
	private void updateFactory(List<String> packageList) {
		factory.setTrustedPackages(packageList);
	}
}
