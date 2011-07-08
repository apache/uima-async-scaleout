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
   * Parses provided UIMA AS deployment descriptor xml file and returns a Java Object representing
   * the descriptor.
   *
   * @param xmlDescriptor - deployment descriptor path
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
/*
  public static UimaASAggregateDeploymentDescriptor createAggregateDeploymentDescriptor(ServiceContext context, DelegateConfiguration ... delegateConfigurations) 
  throws ResourceInitializationException {
//    props.remove(UimaASDeploymentDescriptor.AGGREGATE);
//    props.put(UimaASDeploymentDescriptor.AGGREGATE,"true");
    File descriptor = new File(context.getDescriptor());
    if ( !descriptor.exists() ) {
      throw new ResourceInitializationException(new FileNotFoundException("Provided Aggregate Analysis Engine Decriptor Not Found:"+context.getDescriptor()));
    }
    try {
      ResourceSpecifier resourceSpecifier = 
        UimaClassFactory.produceResourceSpecifier(context.getDescriptor());
      if ( resourceSpecifier instanceof AnalysisEngineDescription ) {
        if ( ((AnalysisEngineDescription)resourceSpecifier).isPrimitive() ) {
          throw new ResourceInitializationException(new Exception("Invalid Resource Specifier. Expected Aggregate Analysis Engine Descriptor but received Primitive Analysis Engine Descriptor"));
        } else { // aggregate 
          
          Map<String,ResourceSpecifier> delegates = 
            ((AnalysisEngineDescription)resourceSpecifier).getDelegateAnalysisEngineSpecifiers();
          Set<String> keys = delegates.keySet();
          Set<String> validKeys = new LinkedHashSet<String>();
          
          StringBuffer sb = new StringBuffer();
          for( String key : keys) {
            sb.append(key).append(",");
          }
          System.out.println("Delegate Keys:"+sb.toString());
//          for( DelegateConfiguration cfg : delegateConfigurations ) {
//            if ( !keys.contains(cfg.getKey())) {
//              throw new ResourceInitializationException(new Exception("Invalid Delegate Key:"+cfg.getKey()+". Make sure the delegate key is defined in provided Aggregate Analysis Engine Descriptor:"+aeDescriptor));
//            }
//            if ( validKeys.add(cfg.getKey()) == false ) {  // check for duplicate key
//              throw new ResourceInitializationException(new Exception("Duplicate Delegate Key:"+cfg.getKey()+". Make sure the delegate key is unique"));
//            }
//          }
        }
      } else {
        throw new ResourceInitializationException(new Exception("Invalid Resource Specifier. Expected AnalysisEngineDescription but received "+resourceSpecifier.getClass().getName()));
      }
    } catch( Exception ex) {
      throw new ResourceInitializationException(ex);
    }
    UimaASAggregateDeploymentDescriptor dd = 
      createAggregateDeploymentDescriptor(context, delegateConfigurations);
    //Deployment d = dd.getDeployment();

    //Service service = d.getService();
    //service.getTopDescriptor().getImport().setLocation(aeDescriptor);
    
    
//    TopLevelAnalysisEngine tae = service.getTopLevelAnalysisEngine();
//    Delegates delegates = tae.getDelegates();
//    delegates.
    //    TopLevelAnalysisEngine taes = service.getTopLevelAnalysisEngine();
//    taes.getInputQueueScaleout().setNumberOfInstances(1);
//    taes.getReplyQueueScaleout().setNumberOfInstances(1);
//    taes.getPrimitiveErrorConfiguration().getProcessCasErrors().setThresholdCount(0);
//    taes.getPrimitiveErrorConfiguration().getProcessCasErrors().setThresholdWindow(0);
//    taes.getPrimitiveErrorConfiguration().getProcessCasErrors().setThresholdAction(Action.Terminate);
    
    
    return dd;
  }

  
  public static UimaASDeploymentDescriptor createAggregate(ServiceContext context, DelegateConfiguration ... delegateConfigurations) 
  throws ResourceInitializationException {
//    props.remove(UimaASDeploymentDescriptor.AGGREGATE);
//    props.put(UimaASDeploymentDescriptor.AGGREGATE,"true");
    File descriptor = new File(context.getDescriptor());
    if ( !descriptor.exists() ) {
      throw new ResourceInitializationException(new FileNotFoundException("Provided Aggregate Analysis Engine Decriptor Not Found:"+context.getDescriptor()));
    }
    try {
      ResourceSpecifier resourceSpecifier = 
        UimaClassFactory.produceResourceSpecifier(context.getDescriptor());
      if ( resourceSpecifier instanceof AnalysisEngineDescription ) {
        if ( ((AnalysisEngineDescription)resourceSpecifier).isPrimitive() ) {
          throw new ResourceInitializationException(new Exception("Invalid Resource Specifier. Expected Aggregate Analysis Engine Descriptor but received Primitive Analysis Engine Descriptor"));
        } else { // aggregate 
          
          Map<String,ResourceSpecifier> delegates = 
            ((AnalysisEngineDescription)resourceSpecifier).getDelegateAnalysisEngineSpecifiers();
          Set<String> keys = delegates.keySet();
          Set<String> validKeys = new LinkedHashSet<String>();
          
          StringBuffer sb = new StringBuffer();
          for( String key : keys) {
            sb.append(key).append(",");
          }
          System.out.println("Delegate Keys:"+sb.toString());
//          for( DelegateConfiguration cfg : delegateConfigurations ) {
//            if ( !keys.contains(cfg.getKey())) {
//              throw new ResourceInitializationException(new Exception("Invalid Delegate Key:"+cfg.getKey()+". Make sure the delegate key is defined in provided Aggregate Analysis Engine Descriptor:"+aeDescriptor));
//            }
//            if ( validKeys.add(cfg.getKey()) == false ) {  // check for duplicate key
//              throw new ResourceInitializationException(new Exception("Duplicate Delegate Key:"+cfg.getKey()+". Make sure the delegate key is unique"));
//            }
//          }
        }
      } else {
        throw new ResourceInitializationException(new Exception("Invalid Resource Specifier. Expected AnalysisEngineDescription but received "+resourceSpecifier.getClass().getName()));
      }
    } catch( Exception ex) {
      throw new ResourceInitializationException(ex);
    }
    UimaASDeploymentDescriptor dd = createDeploymentDescriptor(context, delegateConfigurations);
    //Deployment d = dd.getDeployment();

    //Service service = d.getService();
    //service.getTopDescriptor().getImport().setLocation(aeDescriptor);
    
    
//    TopLevelAnalysisEngine tae = service.getTopLevelAnalysisEngine();
//    Delegates delegates = tae.getDelegates();
//    delegates.
    //    TopLevelAnalysisEngine taes = service.getTopLevelAnalysisEngine();
//    taes.getInputQueueScaleout().setNumberOfInstances(1);
//    taes.getReplyQueueScaleout().setNumberOfInstances(1);
//    taes.getPrimitiveErrorConfiguration().getProcessCasErrors().setThresholdCount(0);
//    taes.getPrimitiveErrorConfiguration().getProcessCasErrors().setThresholdWindow(0);
//    taes.getPrimitiveErrorConfiguration().getProcessCasErrors().setThresholdAction(Action.Terminate);
    
    
    return dd;
  }
*/
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
  
  
//  public static RemoteDelegateConfiguration createRemoteDelegateConfiguration() {
//    return new RemoteDelegateConfigurationImpl();
//  }
  
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
  

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
  
    
    ServiceContext context =
      new ServiceContextImpl("Person Title", "Person Title Annotator", "../descriptors/analysis_engine/PersonTitleAnnotator.xml","PersonTitleQueue");
    try {
      context.setAsync(false);
      
      UimaASPrimitiveDeploymentDescriptor primitiveDD = 
        DeploymentDescriptorFactory.createPrimitiveDeploymentDescriptor(context);
      primitiveDD.setScaleup(5);
      System.out.println(primitiveDD.toXML());
    } catch( ResourceInitializationException e) {
      e.printStackTrace();
    }
    
    RemoteDelegateConfiguration remoteDelegate = 
      DeploymentDescriptorFactory.createRemoteDelegateConfiguration("RoomNumber","tcp://localhost:61616","RoomNumberQueue",SerializationStrategy.xmi);
    GetMetaErrorHandlingSettings getMetaErrorSettings = 
      DeploymentDescriptorFactory.createGetMetaErrorHandlingSettings(3,300,Action.Terminate);
    ProcessErrorHandlingSettings processErrorSettings = 
      DeploymentDescriptorFactory.createProcessErrorHandlingSettings(5,5000,Action.Terminate,true,0,0);
    CollectionProcessCompleteErrorHandlingSettings cpcErrorSettings = 
      DeploymentDescriptorFactory.createCollectionProcessCompleteErrorHandlingSettings(1000,Action.Terminate);

    remoteDelegate.setGetMetaErrorHandlingSettings(getMetaErrorSettings);
    remoteDelegate.setProcessErrorHandlingSettings(processErrorSettings);
    remoteDelegate.setCollectionProcessCompleteErrorHandlingSettings(cpcErrorSettings);
    
    RemoteDelegateConfiguration remoteDelegate2 = 
      DeploymentDescriptorFactory.
        createRemoteDelegateConfiguration("RemoteDateTime",
                                          "tcp://localhost:61616",
                                          "DateTimeQueue",
                                          SerializationStrategy.xmi,
                                          DeploymentDescriptorFactory.createGetMetaErrorHandlingSettings(1,100,Action.Terminate),
                                          DeploymentDescriptorFactory.createProcessErrorHandlingSettings(2,10000,Action.Terminate,true,0,0));

    DelegateConfiguration colocatedDelegateCM = 
      DeploymentDescriptorFactory.
          createPrimitiveDelegateConfiguration("Meeting",
                                                DeploymentDescriptorFactory.createGetMetaErrorHandlingSettings(1,100,Action.Terminate),
                                                DeploymentDescriptorFactory.createProcessErrorHandlingSettings(2,10000,Action.Terminate,true,0,0)        
          );
    colocatedDelegateCM.setCasMultiplier(true);
    colocatedDelegateCM.setCasPoolSize(5);

    DelegateConfiguration colocatedDelegate1 = 
      DeploymentDescriptorFactory.createPrimitiveDelegateConfiguration("AnotherMeeting");
    colocatedDelegate1.setCasMultiplier(true);
    
    DelegateConfiguration colocatedAggregate = 
      DeploymentDescriptorFactory.createAggregateDelegateConfiguration("DateTimeAggregate", colocatedDelegate1, remoteDelegate);
    
    ServiceContext aggregateContext =
      new ServiceContextImpl("Aggregate Annotator", 
              "Aggregate Annotator that does nothing", 
              "C:/uima/releases/apache-uima-as-2.3.1/examples/descriptors/tutorial/ex4/MeetingDetectorTAE.xml",
              "AggregateQueue");
    try {
      UimaASAggregateDeploymentDescriptor dd = 
        DeploymentDescriptorFactory.createAggregateDeploymentDescriptor(aggregateContext, colocatedDelegateCM, remoteDelegate2, colocatedAggregate);
      System.out.println(dd.toXML());

      List<DelegateAnalysisEngine> delegates = dd.getDelegates();
      for( DelegateAnalysisEngine delegate : delegates ) {
        if ( delegate.isRemote() ) {
          System.out.println("Delegate:"+delegate.getKey()+" is REMOTE");
        } else if ( delegate.isAggregate() ) {
          System.out.println("Delegate:"+delegate.getKey()+" is AGGREGATE");
        } else {
          System.out.println("Delegate:"+delegate.getKey()+" is PRIMITIVE");
        }
      }
    } catch( ResourceInitializationException e) {
      e.printStackTrace();
    }
  }
}
