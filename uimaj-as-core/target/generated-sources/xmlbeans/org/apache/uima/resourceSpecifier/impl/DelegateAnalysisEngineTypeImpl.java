/*
 * XML Type:  delegateAnalysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML delegateAnalysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class DelegateAnalysisEngineTypeImpl extends org.apache.uima.resourceSpecifier.impl.AnalysisEngineTypeImpl implements org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType
{
    private static final long serialVersionUID = 1L;
    
    public DelegateAnalysisEngineTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ASYNCAGGREGATEERRORCONFIGURATION$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "asyncAggregateErrorConfiguration");
    
    
    /**
     * Gets the "asyncAggregateErrorConfiguration" element
     */
    public org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType getAsyncAggregateErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().find_element_user(ASYNCAGGREGATEERRORCONFIGURATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "asyncAggregateErrorConfiguration" element
     */
    public boolean isSetAsyncAggregateErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ASYNCAGGREGATEERRORCONFIGURATION$0) != 0;
        }
    }
    
    /**
     * Sets the "asyncAggregateErrorConfiguration" element
     */
    public void setAsyncAggregateErrorConfiguration(org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType asyncAggregateErrorConfiguration)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().find_element_user(ASYNCAGGREGATEERRORCONFIGURATION$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().add_element_user(ASYNCAGGREGATEERRORCONFIGURATION$0);
            }
            target.set(asyncAggregateErrorConfiguration);
        }
    }
    
    /**
     * Appends and returns a new empty "asyncAggregateErrorConfiguration" element
     */
    public org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType addNewAsyncAggregateErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().add_element_user(ASYNCAGGREGATEERRORCONFIGURATION$0);
            return target;
        }
    }
    
    /**
     * Unsets the "asyncAggregateErrorConfiguration" element
     */
    public void unsetAsyncAggregateErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ASYNCAGGREGATEERRORCONFIGURATION$0, 0);
        }
    }
}
