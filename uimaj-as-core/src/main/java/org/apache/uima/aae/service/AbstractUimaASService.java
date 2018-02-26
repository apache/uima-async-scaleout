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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.uima.aae.InProcessCache;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.ControllerStatusListener;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.as.client.DirectMessage;
import org.apache.uima.as.client.Listener;
import org.apache.uima.as.client.Listener.Type;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceSpecifier;

public abstract class AbstractUimaASService {
	public final String id = UUID.randomUUID().toString();

	protected AnalysisEngineController controller;
	protected ResourceSpecifier resourceSpecifier = null;
	protected InProcessCache inProcessCache;
	protected String name;

	private void startListener(Listener listener, AnalysisEngineController ctrl) {
		listener.start();
		if (!ctrl.isPrimitive()) {
			if (listener.getType().equals(Type.Reply)) {

				System.out.println("...Controller:" + ctrl.getComponentName() + " Started Listener type:"
						+ listener.getType() + " hashcode:" + listener.hashCode() + " Delegate:" + listener.getName());
			} else {
				System.out.println("...Controller:" + ctrl.getComponentName() + " Started Listener type:"
						+ listener.getType() + " hashcode:" + listener.hashCode());
			}
		} else {
			System.out.println("...Controller:" + ctrl.getComponentName() + " Started Listener type:"
					+ listener.getType() + " hashcode:" + listener.hashCode());

		}
	}

	private void startListenersFromList(List<Listener> listeners, AnalysisEngineController ctrl) {
		for (Listener l : listeners) {
			startListener(l, ctrl);
		}

	}

	protected void startListeners(AnalysisEngineController ctrl) {
		if (ctrl instanceof AggregateAnalysisEngineController) {
			for (AnalysisEngineController c : ((AggregateAnalysisEngineController) ctrl).getChildControllerList()) {
				if (c instanceof AggregateAnalysisEngineController) {
					// recurse until last inner aggregate is reached
					startListeners(c);
					// now start a listener for each remote delegate. This listener will handle
					// replies
					startListenersFromList(c.getAllListeners(), c);
				}
			}
		}
		startListenersFromList(ctrl.getAllListeners(), ctrl);
	}

	public void start() throws Exception {

		startListeners(controller);

//		controller.getControllerLatch().release();
//		controller.initializeVMTransport(1);
		System.out.println(
				".........." + controller.getName() + " AbstractUimaASService.start() ............ 1");
		CountDownLatch latch = new CountDownLatch(1);
		ControllerStatusListener l = new ControllerStatusListener(latch);
		controller.addControllerCallbackListener(l);
		System.out.println(
				".........." + controller.getName() + " AbstractUimaASService.start() ............ 2");

		if (controller instanceof AggregateAnalysisEngineController) {
			System.out
					.println("..........." + controller.getName() + " Aggregate sending GetMeta to delegates");
			((AggregateAnalysisEngineController) controller).sendRequestForMetadataToRemoteDelegates();
			System.out
			.println("..........." + controller.getName() + " Aggregate waiting on a latch.hashcode="+latch.hashCode());
			if ( controller.isTopLevelComponent()) {
				latch.await();
			}
		}
		System.out.println(
				".........." + controller.getComponentName() + " AbstractUimaASService.start() ............ 3");

	}

	public CAS getCAS() throws Exception {
		return null;
	}

	public void process(CAS cas, String casReferenceId) throws Exception {
	}

	public void sendGetMetaRequest() throws Exception {
	}

	public void collectionProcessComplete() throws Exception {
	}

	public AnalysisEngineMetaData getMetaData() throws Exception {
		return null;
	}

	public void removeFromCache(String casReferenceId) {
	}

	public void releaseCAS(String casReferenceId, BlockingQueue<DirectMessage> releaseCASQueue) throws Exception {
	}
}
