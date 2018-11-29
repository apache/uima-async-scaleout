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
package org.apache.uima.aae.service.command;

import java.io.ByteArrayInputStream;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.controller.AggregateAnalysisEngineController;
import org.apache.uima.aae.controller.AnalysisEngineController;
import org.apache.uima.aae.controller.BaseAnalysisEngineController.ServiceState;
import org.apache.uima.aae.controller.Endpoint;
import org.apache.uima.aae.message.AsynchAEMessage;
import org.apache.uima.aae.message.MessageContext;
import org.apache.uima.aae.message.Origin;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.util.XMLInputSource;

public class GetMetaResponseCommand extends AbstractUimaAsCommand {
//	private MessageContext mc;
	
	public GetMetaResponseCommand(MessageContext mc, AnalysisEngineController controller) {
		super(controller, mc);
	//	this.mc = mc;
	}
	public void execute() throws Exception {
		System.out.println(".......... GetMetaResponseCommand.execute()- handling GetMeta response - Controller:"+controller.getName());
      //  Endpoint endpoint = mc.getEndpoint();
        int payload = //mc
                super.getMessageIntProperty(AsynchAEMessage.Payload);
        

       if (AsynchAEMessage.Exception == payload) {
            return;
          }
       
//       String fromEndpoint =// mc
//           super.getMessageStringProperty(AsynchAEMessage.MessageFrom);
    	Origin origin = (Origin) super.getMessageContext().getMessageObjectProperty(AsynchAEMessage.MessageFrom);
    	String delegateKey = origin.getName();//.split(":")[1];
 System.out.println("++++++++++++++++++++++  delegateKey: "+delegateKey);
   //   	String delegateKey = ((AggregateAnalysisEngineController) controller)
 //              .lookUpDelegateKey(fromEndpoint);
          ResourceMetaData resource = null;
          int serializationSupportedByRemote = AsynchAEMessage.None;
//          ((MessageContext) anObjectToHandle).getMessageIntProperty(AsynchAEMessage.SERIALIZATION);

          if ( serializationSupportedByRemote == AsynchAEMessage.None ) {
          	resource = super.getResourceMetaData();
          			//(ResourceMetaData)
          			//((MessageContext)mc).getMessageObjectProperty(AsynchAEMessage.AEMetadata);
          } else {
              String analysisEngineMetadata = super.getStringMessage();
            		  //((MessageContext) mc).getStringMessage();
              ByteArrayInputStream bis = new ByteArrayInputStream(analysisEngineMetadata.getBytes());
              XMLInputSource in1 = new XMLInputSource(bis, null);
              resource = UIMAFramework.getXMLParser().parseResourceMetaData(in1);
          }
          String fromServer = null;
//          if (((MessageContext) mc).propertyExists(AsynchAEMessage.EndpointServer)) {
            if ( super.propertyExists(AsynchAEMessage.EndpointServer)) {
            fromServer = super.getMessageStringProperty(AsynchAEMessage.EndpointServer);
            		//((MessageContext) mc)
                    //.getMessageStringProperty(AsynchAEMessage.EndpointServer);
          }
          ((AggregateAnalysisEngineController)controller).changeCollocatedDelegateState(delegateKey, ServiceState.RUNNING);
          // If old service does not echo back the external broker name then the queue name must
          // be unique.
          // The ServerURI set by the service may be its local name for the broker, e.g.
          // tcp://localhost:61616
 
          
          ((AggregateAnalysisEngineController) controller).mergeTypeSystem(
                  resource, delegateKey, fromServer);
          ((AggregateAnalysisEngineController) controller).setRemoteSerializationSupported(serializationSupportedByRemote, delegateKey, fromServer);

//          ((AggregateAnalysisEngineController) controller).mergeTypeSystem(
//                  resource, fromEndpoint, fromServer);
//          ((AggregateAnalysisEngineController) controller).setRemoteSerializationSupported(serializationSupportedByRemote, fromEndpoint, fromServer);
//
	}

}
