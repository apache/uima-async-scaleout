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
package org.apache.uima.aae.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.uima.aae.client.UimaAsynchronousEngine.Transport;

public class UimaAS {
/*	
	private UimaAS() {
		
	}
	
	private static class UimaAsSingelton {
		private UimaAsSingelton(){}
		private static final UimaAS instance = 
				new UimaAS();
	}

	public static UimaAS getInstance() {
		return UimaAsSingelton.instance;
	}
	*/
	public static UimaAsynchronousEngine newInstance(Transport transport) 
			throws ClassNotFoundException, NoSuchMethodException, 
			InstantiationException, IllegalAccessException, InvocationTargetException{
		Class<?>[] type = {Transport.class};
		Class<?> uimaClientClz = 
				Class.forName("org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl");
		Constructor<?> constructor = uimaClientClz.getConstructor(type);
		Object[] argInstance = {transport};
		//return (UimaAsynchronousEngine)uimaClientClz.newInstance();
		return (UimaAsynchronousEngine)constructor.newInstance(argInstance);
	}
	/*
	public static UimaAsynchronousEngine newJmsClient() 
			throws ClassNotFoundException, NoSuchMethodException, 
			InstantiationException, IllegalAccessException, 
			InvocationTargetException, InvocationTargetException {
		UimaAsynchronousEngine client = newClient(Transport.JMS);
		return client;
	}
	public static UimaAsynchronousEngine newJavaClient() 
			throws ClassNotFoundException, NoSuchMethodException, 
			InstantiationException, IllegalAccessException, InvocationTargetException {
		UimaAsynchronousEngine client = newClient(Transport.Java);
		return client;
	}
	*/
}
