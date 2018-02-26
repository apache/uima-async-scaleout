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
package org.apache.uima.aae.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class UimaAsServiceRegistry  implements ServiceRegistry {
	private Map<String, List<UimaASService>> serviceRegistry = null;
	
	private UimaAsServiceRegistry() {
		serviceRegistry = new ConcurrentHashMap<>(); 
	}
	
	private static class UimaAsServiceRegistrySingleton {
		private UimaAsServiceRegistrySingleton(){}
		private static final UimaAsServiceRegistry instance =
				new UimaAsServiceRegistry();
	}
	public static ServiceRegistry getInstance() {
		return UimaAsServiceRegistrySingleton.instance;
	}
	public synchronized void register(UimaASService service) {
		System.out.println("*********** Adding Service:"+service.getName()+" Key:"+service.getEndpoint()+" To Registry:"+serviceRegistry.hashCode());
		List<UimaASService> list;
		if ( serviceRegistry.containsKey(service.getEndpoint())) {
			list = serviceRegistry.get(service.getEndpoint());
		} else {
			list = Collections.synchronizedList(new ArrayList<UimaASService>());
			serviceRegistry.put(service.getEndpoint(), list);
		}
		list.add( service);
	}
	public synchronized void unregister(UimaASService service) {
		Iterator<Entry<String, List<UimaASService>>> iterator = 
				serviceRegistry.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			Iterator<UimaASService> listIterator = iterator.next().getValue().iterator();
			while( listIterator.hasNext()) {
				if ( listIterator.next().getId().equals(service.getId())) {
					listIterator.remove();
					System.out.println("*********** Removed Service:"+service.getName()+" Key:"+service.getEndpoint()+" To Registry:"+serviceRegistry.hashCode());

					return;
				}
			}	
		}
	}
	public synchronized UimaASService lookupById(String serviceId) {
		Iterator<Entry<String, List<UimaASService>>> iterator = 
				serviceRegistry.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			Iterator<UimaASService> listIterator = iterator.next().getValue().iterator();
			while( listIterator.hasNext()) {
				UimaASService service = listIterator.next();
				if ( service.getId().equals(serviceId)) {
					return service;
				}
			}	

		}
		throw new ServiceNotFoundException("Service with id "+serviceId+" not found in ServiceRegistry");
	}
	public synchronized UimaASService lookupByEndpoint(String serviceEndpoint) {
		Iterator<Entry<String, List<UimaASService>>> iterator = 
				serviceRegistry.entrySet().iterator();
		
		while( iterator.hasNext() ) {
			Iterator<UimaASService> listIterator = iterator.next().getValue().iterator();
			while( listIterator.hasNext()) {
				UimaASService service = listIterator.next();
				if ( service.getEndpoint().equals(serviceEndpoint)) {
					return service;
				}
			}	
		}
		throw new ServiceNotFoundException("Service with name "+serviceEndpoint+" not found in ServiceRegistry");
	}
	public synchronized Map<String, List<UimaASService>> getServiceList() {
		return serviceRegistry;
	}
	public synchronized void clear() {
		serviceRegistry.clear();
	}

}
