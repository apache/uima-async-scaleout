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
package org.apache.uima.resourceSpecifier.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument;
import org.apache.uima.resourceSpecifier.factory.impl.CollectionProcessCompleteErrorHandlingSettingsImpl;
import org.apache.uima.resourceSpecifier.factory.impl.ColocatedDelegateConfigurationImpl;
import org.apache.uima.resourceSpecifier.factory.impl.DelegateConfigurationImpl;
import org.apache.uima.resourceSpecifier.factory.impl.GetMetaErrorHandlingSettingsImpl;
import org.apache.uima.resourceSpecifier.factory.impl.ProcessErrorHandlingSettingsImpl;
import org.apache.uima.resourceSpecifier.factory.impl.RemoteDelegateConfigurationImpl;
import org.apache.uima.resourceSpecifier.factory.impl.ServiceContextImpl;
import org.apache.uima.resourceSpecifier.factory.impl.UimaASAggregateDeploymentDescriptorImpl;
import org.apache.uima.resourceSpecifier.factory.impl.UimaASDeploymentDescriptorImpl;
import org.apache.uima.resourceSpecifier.factory.impl.UimaASPrimitiveDeploymentDescriptorImpl;
import org.apache.xmlbeans.XmlException;

/**
 * Factory class providing static API to create UIMA AS Deployment Descriptor.
 * <p>
 *  
 */
public final class DeploymentDescriptorFactory {

  /**
   * Instantiates a new deployment descriptor factory.
   */
  private DeploymentDescriptorFactory() {
    //  Forbid instantiation. This class is meant for static access only.
  }

  /**
   * Parses provided UIMA AS deployment descriptor contents (as String) and returns a Java Object representing
   * the descriptor.
   *
   * @param xmlDescriptor - deployment descriptor xml contents as String
   * @return - Java Object representing deployment descriptor
   * @throws ResourceInitializationException the resource initialization exception
   */
  public static UimaASDeploymentDescriptor createDeploymentDescriptor(String xmlDescriptor) 
  throws ResourceInitializationException {
    try {
      return new UimaASDeploymentDescriptorImpl(AnalysisEngineDeploymentDescriptionDocument.Factory.parse(xmlDescriptor),new ServiceContextImpl("","","","") );
    } catch( XmlException e ) {
      throw new ResourceInitializationException(e);
    }
  }
  /**
   * Parses provided UIMA AS deployment descriptor xml file and returns a Java Object representing
   * the descriptor.
   *
   * @param xmlFileDescriptor - deployment descriptor file
   * @return - Java Object representing deployment descriptor
   * @throws ResourceInitializationException the resource initialization exception
   */
  public static UimaASDeploymentDescriptor createDeploymentDescriptor(File xmlFileDescriptor) 
  throws ResourceInitializationException {
    try {
      return new UimaASDeploymentDescriptorImpl(AnalysisEngineDeploymentDescriptionDocument.Factory.parse(xmlFileDescriptor),new ServiceContextImpl("","","","") );
    } catch( Exception e ) {
      throw new ResourceInitializationException(e);
    }
  }
  
  /**
   * Parses provided UIMA AS deployment descriptor InputStream and returns a Java Object representing
   * the descriptor.
   *
   * @param xmlFileDescriptor - deployment descriptor InputStream
   * @return - Java Object representing deployment descriptor
   * @throws ResourceInitializationException the resource initialization exception
   */
  public static UimaASDeploymentDescriptor createDeploymentDescriptor(InputStream descriptorInputStream) 
  throws ResourceInitializationException {
    try {
      return new UimaASDeploymentDescriptorImpl(AnalysisEngineDeploymentDescriptionDocument.Factory.parse(descriptorInputStream),new ServiceContextImpl("","","","") );
    } catch( Exception e ) {
      throw new ResourceInitializationException(e);
    }
  }

  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @param context the context
   * @param delegateConfigurations the delegate configurations
   * @return the uima as aggregate deployment descriptor
   * @throws ResourceInitializationException the resource initialization exception
   */
  public static UimaASAggregateDeploymentDescriptor createAggregateDeploymentDescriptor(ServiceContext context, DelegateConfiguration ... delegateConfigurations) 
  throws ResourceInitializationException {
    return new UimaASAggregateDeploymentDescriptorImpl(AnalysisEngineDeploymentDescriptionDocument.Factory.newInstance(), context, delegateConfigurations);
  }
  
  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @param context the context
   * @return the uima as primitive deployment descriptor
   * @throws ResourceInitializationException the resource initialization exception
   */
  public static UimaASPrimitiveDeploymentDescriptor createPrimitiveDeploymentDescriptor(ServiceContext context) 
  throws ResourceInitializationException {
    return new UimaASPrimitiveDeploymentDescriptorImpl(AnalysisEngineDeploymentDescriptionDocument.Factory.newInstance(),context);
  }
  /**
 * Creates a new DeploymentDescriptor object.
 *
 * @param key the key
 * @param errorHandlingSettings the error handling settings
 * @return the delegate configuration
 */
public static DelegateConfiguration createPrimitiveDelegateConfiguration(String key, ErrorHandlingSettings ...errorHandlingSettings) {
    return new ColocatedDelegateConfigurationImpl(key, new DelegateConfigurationImpl[0], errorHandlingSettings);
  }
  
  
  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @param key the key
   * @param delegateConfiguration the delegate configuration
   * @return the colocated delegate configuration
   */
  public static ColocatedDelegateConfiguration createAggregateDelegateConfiguration(String key, DelegateConfiguration ...delegateConfiguration) {
    return new ColocatedDelegateConfigurationImpl(key, delegateConfiguration);
  }
  
  
  /**
 * Creates a new DeploymentDescriptor object.
 *
 * @param key the key
 * @param brokerURL the broker url
 * @param endpoint the endpoint
 * @param serialization the serialization
 * @param errorHandlingSettings the error handling settings
 * @return the remote delegate configuration
 */
public static RemoteDelegateConfiguration createRemoteDelegateConfiguration(String key, String brokerURL, String endpoint, SerializationStrategy serialization, ErrorHandlingSettings ...errorHandlingSettings) {
    return new RemoteDelegateConfigurationImpl(key,brokerURL,endpoint,serialization,errorHandlingSettings);
  }
  
  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @return the gets the meta error handling settings
   */
  public static GetMetaErrorHandlingSettings createGetMetaErrorHandlingSettings() {
    return new GetMetaErrorHandlingSettingsImpl();
  }

  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @param retryCount the retry count
   * @param timeout the timeout
   * @param action the action
   * @return the gets the meta error handling settings
   */
  public static GetMetaErrorHandlingSettings createGetMetaErrorHandlingSettings(int retryCount, int timeout, Action action) {
    return new GetMetaErrorHandlingSettingsImpl(retryCount, timeout, action);
  }
  
  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @return the process error handling settings
   */
  public static ProcessErrorHandlingSettings createProcessErrorHandlingSettings() {
    return new ProcessErrorHandlingSettingsImpl();
  }
  
  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @param retryCount the retry count
   * @param timeout the timeout
   * @param action the action
   * @param continueOnRetryFailure the continue on retry failure
   * @param thresholdCount the threshold count
   * @param thresholdWindow the threshold window
   * @return the process error handling settings
   */
  public static ProcessErrorHandlingSettings createProcessErrorHandlingSettings(int retryCount, int timeout, Action action, boolean continueOnRetryFailure,int thresholdCount, int thresholdWindow) {
    return new ProcessErrorHandlingSettingsImpl(retryCount,timeout, action,continueOnRetryFailure, thresholdCount, thresholdWindow );
  }
  
  /**
   * Creates a new DeploymentDescriptor object.
   *
   * @param timeout the timeout
   * @param action the action
   * @return the collection process complete error handling settings
   */
  public static CollectionProcessCompleteErrorHandlingSettings createCollectionProcessCompleteErrorHandlingSettings(int timeout, Action action) {
    return new CollectionProcessCompleteErrorHandlingSettingsImpl(timeout, action);
  }
  


}
