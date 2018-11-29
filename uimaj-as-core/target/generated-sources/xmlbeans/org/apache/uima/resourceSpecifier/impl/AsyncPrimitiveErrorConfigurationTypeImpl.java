/*
 * XML Type:  asyncPrimitiveErrorConfigurationType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML asyncPrimitiveErrorConfigurationType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class AsyncPrimitiveErrorConfigurationTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType
{
    private static final long serialVersionUID = 1L;
    
    public AsyncPrimitiveErrorConfigurationTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PROCESSCASERRORS$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "processCasErrors");
    private static final javax.xml.namespace.QName COLLECTIONPROCESSCOMPLETEERRORS$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "collectionProcessCompleteErrors");
    
    
    /**
     * Gets the "processCasErrors" element
     */
    public org.apache.uima.resourceSpecifier.ProcessCasErrorsType getProcessCasErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ProcessCasErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().find_element_user(PROCESSCASERRORS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "processCasErrors" element
     */
    public boolean isSetProcessCasErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PROCESSCASERRORS$0) != 0;
        }
    }
    
    /**
     * Sets the "processCasErrors" element
     */
    public void setProcessCasErrors(org.apache.uima.resourceSpecifier.ProcessCasErrorsType processCasErrors)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ProcessCasErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().find_element_user(PROCESSCASERRORS$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().add_element_user(PROCESSCASERRORS$0);
            }
            target.set(processCasErrors);
        }
    }
    
    /**
     * Appends and returns a new empty "processCasErrors" element
     */
    public org.apache.uima.resourceSpecifier.ProcessCasErrorsType addNewProcessCasErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ProcessCasErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().add_element_user(PROCESSCASERRORS$0);
            return target;
        }
    }
    
    /**
     * Unsets the "processCasErrors" element
     */
    public void unsetProcessCasErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PROCESSCASERRORS$0, 0);
        }
    }
    
    /**
     * Gets the "collectionProcessCompleteErrors" element
     */
    public org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType getCollectionProcessCompleteErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().find_element_user(COLLECTIONPROCESSCOMPLETEERRORS$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "collectionProcessCompleteErrors" element
     */
    public boolean isSetCollectionProcessCompleteErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(COLLECTIONPROCESSCOMPLETEERRORS$2) != 0;
        }
    }
    
    /**
     * Sets the "collectionProcessCompleteErrors" element
     */
    public void setCollectionProcessCompleteErrors(org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType collectionProcessCompleteErrors)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().find_element_user(COLLECTIONPROCESSCOMPLETEERRORS$2, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().add_element_user(COLLECTIONPROCESSCOMPLETEERRORS$2);
            }
            target.set(collectionProcessCompleteErrors);
        }
    }
    
    /**
     * Appends and returns a new empty "collectionProcessCompleteErrors" element
     */
    public org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType addNewCollectionProcessCompleteErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().add_element_user(COLLECTIONPROCESSCOMPLETEERRORS$2);
            return target;
        }
    }
    
    /**
     * Unsets the "collectionProcessCompleteErrors" element
     */
    public void unsetCollectionProcessCompleteErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(COLLECTIONPROCESSCOMPLETEERRORS$2, 0);
        }
    }
}
