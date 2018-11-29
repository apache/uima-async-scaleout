/*
 * XML Type:  delegatesType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.DelegatesType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML delegatesType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class DelegatesTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.DelegatesType
{
    private static final long serialVersionUID = 1L;
    
    public DelegatesTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ANALYSISENGINE$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "analysisEngine");
    private static final javax.xml.namespace.QName REMOTEANALYSISENGINE$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "remoteAnalysisEngine");
    
    
    /**
     * Gets array of all "analysisEngine" elements
     */
    public org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType[] getAnalysisEngineArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(ANALYSISENGINE$0, targetList);
            org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType[] result = new org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "analysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType getAnalysisEngineArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType)get_store().find_element_user(ANALYSISENGINE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "analysisEngine" element
     */
    public int sizeOfAnalysisEngineArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ANALYSISENGINE$0);
        }
    }
    
    /**
     * Sets array of all "analysisEngine" element
     */
    public void setAnalysisEngineArray(org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType[] analysisEngineArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(analysisEngineArray, ANALYSISENGINE$0);
        }
    }
    
    /**
     * Sets ith "analysisEngine" element
     */
    public void setAnalysisEngineArray(int i, org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType analysisEngine)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType)get_store().find_element_user(ANALYSISENGINE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(analysisEngine);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "analysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType insertNewAnalysisEngine(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType)get_store().insert_element_user(ANALYSISENGINE$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "analysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType addNewAnalysisEngine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType)get_store().add_element_user(ANALYSISENGINE$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "analysisEngine" element
     */
    public void removeAnalysisEngine(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ANALYSISENGINE$0, i);
        }
    }
    
    /**
     * Gets array of all "remoteAnalysisEngine" elements
     */
    public org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType[] getRemoteAnalysisEngineArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(REMOTEANALYSISENGINE$2, targetList);
            org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType[] result = new org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "remoteAnalysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType getRemoteAnalysisEngineArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType)get_store().find_element_user(REMOTEANALYSISENGINE$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "remoteAnalysisEngine" element
     */
    public int sizeOfRemoteAnalysisEngineArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(REMOTEANALYSISENGINE$2);
        }
    }
    
    /**
     * Sets array of all "remoteAnalysisEngine" element
     */
    public void setRemoteAnalysisEngineArray(org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType[] remoteAnalysisEngineArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(remoteAnalysisEngineArray, REMOTEANALYSISENGINE$2);
        }
    }
    
    /**
     * Sets ith "remoteAnalysisEngine" element
     */
    public void setRemoteAnalysisEngineArray(int i, org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType remoteAnalysisEngine)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType)get_store().find_element_user(REMOTEANALYSISENGINE$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(remoteAnalysisEngine);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "remoteAnalysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType insertNewRemoteAnalysisEngine(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType)get_store().insert_element_user(REMOTEANALYSISENGINE$2, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "remoteAnalysisEngine" element
     */
    public org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType addNewRemoteAnalysisEngine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType target = null;
            target = (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType)get_store().add_element_user(REMOTEANALYSISENGINE$2);
            return target;
        }
    }
    
    /**
     * Removes the ith "remoteAnalysisEngine" element
     */
    public void removeRemoteAnalysisEngine(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(REMOTEANALYSISENGINE$2, i);
        }
    }
}
