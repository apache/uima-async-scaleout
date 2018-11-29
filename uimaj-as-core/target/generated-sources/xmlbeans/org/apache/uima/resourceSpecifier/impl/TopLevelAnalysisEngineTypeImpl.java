/*
 * XML Type:  topLevelAnalysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML topLevelAnalysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class TopLevelAnalysisEngineTypeImpl extends org.apache.uima.resourceSpecifier.impl.AnalysisEngineTypeImpl implements org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType
{
    private static final long serialVersionUID = 1L;
    
    public TopLevelAnalysisEngineTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ASYNCPRIMITIVEERRORCONFIGURATION$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "asyncPrimitiveErrorConfiguration");
    
    
    /**
     * Gets the "asyncPrimitiveErrorConfiguration" element
     */
    public org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType getAsyncPrimitiveErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType)get_store().find_element_user(ASYNCPRIMITIVEERRORCONFIGURATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "asyncPrimitiveErrorConfiguration" element
     */
    public boolean isSetAsyncPrimitiveErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ASYNCPRIMITIVEERRORCONFIGURATION$0) != 0;
        }
    }
    
    /**
     * Sets the "asyncPrimitiveErrorConfiguration" element
     */
    public void setAsyncPrimitiveErrorConfiguration(org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType asyncPrimitiveErrorConfiguration)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType)get_store().find_element_user(ASYNCPRIMITIVEERRORCONFIGURATION$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType)get_store().add_element_user(ASYNCPRIMITIVEERRORCONFIGURATION$0);
            }
            target.set(asyncPrimitiveErrorConfiguration);
        }
    }
    
    /**
     * Appends and returns a new empty "asyncPrimitiveErrorConfiguration" element
     */
    public org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType addNewAsyncPrimitiveErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType)get_store().add_element_user(ASYNCPRIMITIVEERRORCONFIGURATION$0);
            return target;
        }
    }
    
    /**
     * Unsets the "asyncPrimitiveErrorConfiguration" element
     */
    public void unsetAsyncPrimitiveErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ASYNCPRIMITIVEERRORCONFIGURATION$0, 0);
        }
    }
}
