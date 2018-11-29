/*
 * XML Type:  asyncAggregateErrorConfigurationType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML asyncAggregateErrorConfigurationType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class AsyncAggregateErrorConfigurationTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType
{
    private static final long serialVersionUID = 1L;
    
    public AsyncAggregateErrorConfigurationTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETMETADATAERRORS$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "getMetadataErrors");
    private static final javax.xml.namespace.QName PROCESSCASERRORS$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "processCasErrors");
    private static final javax.xml.namespace.QName COLLECTIONPROCESSCOMPLETEERRORS$4 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "collectionProcessCompleteErrors");
    
    
    /**
     * Gets the "getMetadataErrors" element
     */
    public org.apache.uima.resourceSpecifier.GetMetadataErrorsType getGetMetadataErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.GetMetadataErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.GetMetadataErrorsType)get_store().find_element_user(GETMETADATAERRORS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "getMetadataErrors" element
     */
    public boolean isSetGetMetadataErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GETMETADATAERRORS$0) != 0;
        }
    }
    
    /**
     * Sets the "getMetadataErrors" element
     */
    public void setGetMetadataErrors(org.apache.uima.resourceSpecifier.GetMetadataErrorsType getMetadataErrors)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.GetMetadataErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.GetMetadataErrorsType)get_store().find_element_user(GETMETADATAERRORS$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.GetMetadataErrorsType)get_store().add_element_user(GETMETADATAERRORS$0);
            }
            target.set(getMetadataErrors);
        }
    }
    
    /**
     * Appends and returns a new empty "getMetadataErrors" element
     */
    public org.apache.uima.resourceSpecifier.GetMetadataErrorsType addNewGetMetadataErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.GetMetadataErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.GetMetadataErrorsType)get_store().add_element_user(GETMETADATAERRORS$0);
            return target;
        }
    }
    
    /**
     * Unsets the "getMetadataErrors" element
     */
    public void unsetGetMetadataErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GETMETADATAERRORS$0, 0);
        }
    }
    
    /**
     * Gets the "processCasErrors" element
     */
    public org.apache.uima.resourceSpecifier.ProcessCasErrorsType getProcessCasErrors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ProcessCasErrorsType target = null;
            target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().find_element_user(PROCESSCASERRORS$2, 0);
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
            return get_store().count_elements(PROCESSCASERRORS$2) != 0;
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
            target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().find_element_user(PROCESSCASERRORS$2, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().add_element_user(PROCESSCASERRORS$2);
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
            target = (org.apache.uima.resourceSpecifier.ProcessCasErrorsType)get_store().add_element_user(PROCESSCASERRORS$2);
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
            get_store().remove_element(PROCESSCASERRORS$2, 0);
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
            target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().find_element_user(COLLECTIONPROCESSCOMPLETEERRORS$4, 0);
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
            return get_store().count_elements(COLLECTIONPROCESSCOMPLETEERRORS$4) != 0;
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
            target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().find_element_user(COLLECTIONPROCESSCOMPLETEERRORS$4, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().add_element_user(COLLECTIONPROCESSCOMPLETEERRORS$4);
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
            target = (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType)get_store().add_element_user(COLLECTIONPROCESSCOMPLETEERRORS$4);
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
            get_store().remove_element(COLLECTIONPROCESSCOMPLETEERRORS$4, 0);
        }
    }
}
