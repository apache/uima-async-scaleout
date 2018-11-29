/*
 * XML Type:  serviceType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.ServiceType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML serviceType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class ServiceTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.ServiceType
{
    private static final long serialVersionUID = 1L;
    
    public ServiceTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INPUTQUEUE$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "inputQueue");
    private static final javax.xml.namespace.QName TOPDESCRIPTOR$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "topDescriptor");
    private static final javax.xml.namespace.QName ENVIRONMENTVARIABLES$4 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "environmentVariables");
    private static final javax.xml.namespace.QName ANALYSISENGINE$6 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "analysisEngine");
    
    
    /**
     * Gets the "inputQueue" element
     */
    public org.apache.uima.resourceSpecifier.InputQueueType getInputQueue()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.InputQueueType target = null;
            target = (org.apache.uima.resourceSpecifier.InputQueueType)get_store().find_element_user(INPUTQUEUE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "inputQueue" element
     */
    public void setInputQueue(org.apache.uima.resourceSpecifier.InputQueueType inputQueue)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.InputQueueType target = null;
            target = (org.apache.uima.resourceSpecifier.InputQueueType)get_store().find_element_user(INPUTQUEUE$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.InputQueueType)get_store().add_element_user(INPUTQUEUE$0);
            }
            target.set(inputQueue);
        }
    }
    
    /**
     * Appends and returns a new empty "inputQueue" element
     */
    public org.apache.uima.resourceSpecifier.InputQueueType addNewInputQueue()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.InputQueueType target = null;
            target = (org.apache.uima.resourceSpecifier.InputQueueType)get_store().add_element_user(INPUTQUEUE$0);
            return target;
        }
    }
    
    /**
     * Gets the "topDescriptor" element
     */
    public org.apache.uima.resourceSpecifier.TopDescriptorType getTopDescriptor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.TopDescriptorType target = null;
            target = (org.apache.uima.resourceSpecifier.TopDescriptorType)get_store().find_element_user(TOPDESCRIPTOR$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "topDescriptor" element
     */
    public void setTopDescriptor(org.apache.uima.resourceSpecifier.TopDescriptorType topDescriptor)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.TopDescriptorType target = null;
            target = (org.apache.uima.resourceSpecifier.TopDescriptorType)get_store().find_element_user(TOPDESCRIPTOR$2, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.TopDescriptorType)get_store().add_element_user(TOPDESCRIPTOR$2);
            }
            target.set(topDescriptor);
        }
    }
    
    /**
     * Appends and returns a new empty "topDescriptor" element
     */
    public org.apache.uima.resourceSpecifier.TopDescriptorType addNewTopDescriptor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.TopDescriptorType target = null;
            target = (org.apache.uima.resourceSpecifier.TopDescriptorType)get_store().add_element_user(TOPDESCRIPTOR$2);
            return target;
        }
    }
    
    /**
     * Gets the "environmentVariables" element
     */
    public org.apache.uima.resourceSpecifier.EnvironmentVariablesType getEnvironmentVariables()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariablesType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariablesType)get_store().find_element_user(ENVIRONMENTVARIABLES$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "environmentVariables" element
     */
    public boolean isSetEnvironmentVariables()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ENVIRONMENTVARIABLES$4) != 0;
        }
    }
    
    /**
     * Sets the "environmentVariables" element
     */
    public void setEnvironmentVariables(org.apache.uima.resourceSpecifier.EnvironmentVariablesType environmentVariables)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariablesType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariablesType)get_store().find_element_user(ENVIRONMENTVARIABLES$4, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.EnvironmentVariablesType)get_store().add_element_user(ENVIRONMENTVARIABLES$4);
            }
            target.set(environmentVariables);
        }
    }
    
    /**
     * Appends and returns a new empty "environmentVariables" element
     */
    public org.apache.uima.resourceSpecifier.EnvironmentVariablesType addNewEnvironmentVariables()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariablesType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariablesType)get_store().add_element_user(ENVIRONMENTVARIABLES$4);
            return target;
        }
    }
    
    /**
     * Unsets the "environmentVariables" element
     */
    public void unsetEnvironmentVariables()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ENVIRONMENTVARIABLES$4, 0);
        }
    }
    
    /**
     * Gets the "analysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType getAnalysisEngine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType)get_store().find_element_user(ANALYSISENGINE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "analysisEngine" element
     */
    public void setAnalysisEngine(org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType analysisEngine)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType)get_store().find_element_user(ANALYSISENGINE$6, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType)get_store().add_element_user(ANALYSISENGINE$6);
            }
            target.set(analysisEngine);
        }
    }
    
    /**
     * Appends and returns a new empty "analysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType addNewAnalysisEngine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType)get_store().add_element_user(ANALYSISENGINE$6);
            return target;
        }
    }
}
