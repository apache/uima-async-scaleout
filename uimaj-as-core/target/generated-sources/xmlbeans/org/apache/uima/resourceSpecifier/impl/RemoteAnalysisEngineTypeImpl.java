/*
 * XML Type:  remoteAnalysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML remoteAnalysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class RemoteAnalysisEngineTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType
{
    private static final long serialVersionUID = 1L;
    
    public RemoteAnalysisEngineTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INPUTQUEUE$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "inputQueue");
    private static final javax.xml.namespace.QName SERIALIZER$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "serializer");
    private static final javax.xml.namespace.QName CASMULTIPLIER$4 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "casMultiplier");
    private static final javax.xml.namespace.QName ASYNCAGGREGATEERRORCONFIGURATION$6 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "asyncAggregateErrorConfiguration");
    private static final javax.xml.namespace.QName ASYNC$8 = 
        new javax.xml.namespace.QName("", "async");
    private static final javax.xml.namespace.QName KEY$10 = 
        new javax.xml.namespace.QName("", "key");
    private static final javax.xml.namespace.QName REMOTEREPLYQUEUESCALEOUT$12 = 
        new javax.xml.namespace.QName("", "remoteReplyQueueScaleout");
    
    
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
     * Gets the "serializer" element
     */
    public org.apache.uima.resourceSpecifier.SerializerType getSerializer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.SerializerType target = null;
            target = (org.apache.uima.resourceSpecifier.SerializerType)get_store().find_element_user(SERIALIZER$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "serializer" element
     */
    public void setSerializer(org.apache.uima.resourceSpecifier.SerializerType serializer)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.SerializerType target = null;
            target = (org.apache.uima.resourceSpecifier.SerializerType)get_store().find_element_user(SERIALIZER$2, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.SerializerType)get_store().add_element_user(SERIALIZER$2);
            }
            target.set(serializer);
        }
    }
    
    /**
     * Appends and returns a new empty "serializer" element
     */
    public org.apache.uima.resourceSpecifier.SerializerType addNewSerializer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.SerializerType target = null;
            target = (org.apache.uima.resourceSpecifier.SerializerType)get_store().add_element_user(SERIALIZER$2);
            return target;
        }
    }
    
    /**
     * Gets the "casMultiplier" element
     */
    public org.apache.uima.resourceSpecifier.CasMultiplierType getCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasMultiplierType target = null;
            target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().find_element_user(CASMULTIPLIER$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "casMultiplier" element
     */
    public boolean isSetCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CASMULTIPLIER$4) != 0;
        }
    }
    
    /**
     * Sets the "casMultiplier" element
     */
    public void setCasMultiplier(org.apache.uima.resourceSpecifier.CasMultiplierType casMultiplier)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasMultiplierType target = null;
            target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().find_element_user(CASMULTIPLIER$4, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().add_element_user(CASMULTIPLIER$4);
            }
            target.set(casMultiplier);
        }
    }
    
    /**
     * Appends and returns a new empty "casMultiplier" element
     */
    public org.apache.uima.resourceSpecifier.CasMultiplierType addNewCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasMultiplierType target = null;
            target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().add_element_user(CASMULTIPLIER$4);
            return target;
        }
    }
    
    /**
     * Unsets the "casMultiplier" element
     */
    public void unsetCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CASMULTIPLIER$4, 0);
        }
    }
    
    /**
     * Gets the "asyncAggregateErrorConfiguration" element
     */
    public org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType getAsyncAggregateErrorConfiguration()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType target = null;
            target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().find_element_user(ASYNCAGGREGATEERRORCONFIGURATION$6, 0);
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
            return get_store().count_elements(ASYNCAGGREGATEERRORCONFIGURATION$6) != 0;
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
            target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().find_element_user(ASYNCAGGREGATEERRORCONFIGURATION$6, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().add_element_user(ASYNCAGGREGATEERRORCONFIGURATION$6);
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
            target = (org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType)get_store().add_element_user(ASYNCAGGREGATEERRORCONFIGURATION$6);
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
            get_store().remove_element(ASYNCAGGREGATEERRORCONFIGURATION$6, 0);
        }
    }
    
    /**
     * Gets the "async" attribute
     */
    public java.lang.String getAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ASYNC$8);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "async" attribute
     */
    public org.apache.xmlbeans.XmlString xgetAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ASYNC$8);
            return target;
        }
    }
    
    /**
     * True if has "async" attribute
     */
    public boolean isSetAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ASYNC$8) != null;
        }
    }
    
    /**
     * Sets the "async" attribute
     */
    public void setAsync(java.lang.String async)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ASYNC$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ASYNC$8);
            }
            target.setStringValue(async);
        }
    }
    
    /**
     * Sets (as xml) the "async" attribute
     */
    public void xsetAsync(org.apache.xmlbeans.XmlString async)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ASYNC$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ASYNC$8);
            }
            target.set(async);
        }
    }
    
    /**
     * Unsets the "async" attribute
     */
    public void unsetAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ASYNC$8);
        }
    }
    
    /**
     * Gets the "key" attribute
     */
    public java.lang.String getKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(KEY$10);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "key" attribute
     */
    public org.apache.xmlbeans.XmlString xgetKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(KEY$10);
            return target;
        }
    }
    
    /**
     * True if has "key" attribute
     */
    public boolean isSetKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(KEY$10) != null;
        }
    }
    
    /**
     * Sets the "key" attribute
     */
    public void setKey(java.lang.String key)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(KEY$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(KEY$10);
            }
            target.setStringValue(key);
        }
    }
    
    /**
     * Sets (as xml) the "key" attribute
     */
    public void xsetKey(org.apache.xmlbeans.XmlString key)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(KEY$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(KEY$10);
            }
            target.set(key);
        }
    }
    
    /**
     * Unsets the "key" attribute
     */
    public void unsetKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(KEY$10);
        }
    }
    
    /**
     * Gets the "remoteReplyQueueScaleout" attribute
     */
    public int getRemoteReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(REMOTEREPLYQUEUESCALEOUT$12);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "remoteReplyQueueScaleout" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetRemoteReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(REMOTEREPLYQUEUESCALEOUT$12);
            return target;
        }
    }
    
    /**
     * True if has "remoteReplyQueueScaleout" attribute
     */
    public boolean isSetRemoteReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(REMOTEREPLYQUEUESCALEOUT$12) != null;
        }
    }
    
    /**
     * Sets the "remoteReplyQueueScaleout" attribute
     */
    public void setRemoteReplyQueueScaleout(int remoteReplyQueueScaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(REMOTEREPLYQUEUESCALEOUT$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(REMOTEREPLYQUEUESCALEOUT$12);
            }
            target.setIntValue(remoteReplyQueueScaleout);
        }
    }
    
    /**
     * Sets (as xml) the "remoteReplyQueueScaleout" attribute
     */
    public void xsetRemoteReplyQueueScaleout(org.apache.xmlbeans.XmlInt remoteReplyQueueScaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(REMOTEREPLYQUEUESCALEOUT$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(REMOTEREPLYQUEUESCALEOUT$12);
            }
            target.set(remoteReplyQueueScaleout);
        }
    }
    
    /**
     * Unsets the "remoteReplyQueueScaleout" attribute
     */
    public void unsetRemoteReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(REMOTEREPLYQUEUESCALEOUT$12);
        }
    }
}
