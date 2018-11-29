/*
 * XML Type:  inputQueueType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.InputQueueType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML inputQueueType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.InputQueueType.
 */
public class InputQueueTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.InputQueueType
{
    private static final long serialVersionUID = 1L;
    
    public InputQueueTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected InputQueueTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName ENDPOINT$0 = 
        new javax.xml.namespace.QName("", "endpoint");
    private static final javax.xml.namespace.QName BROKERURL$2 = 
        new javax.xml.namespace.QName("", "brokerURL");
    private static final javax.xml.namespace.QName PREFETCH$4 = 
        new javax.xml.namespace.QName("", "prefetch");
    
    
    /**
     * Gets the "endpoint" attribute
     */
    public java.lang.String getEndpoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ENDPOINT$0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "endpoint" attribute
     */
    public org.apache.xmlbeans.XmlString xgetEndpoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ENDPOINT$0);
            return target;
        }
    }
    
    /**
     * True if has "endpoint" attribute
     */
    public boolean isSetEndpoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ENDPOINT$0) != null;
        }
    }
    
    /**
     * Sets the "endpoint" attribute
     */
    public void setEndpoint(java.lang.String endpoint)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ENDPOINT$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ENDPOINT$0);
            }
            target.setStringValue(endpoint);
        }
    }
    
    /**
     * Sets (as xml) the "endpoint" attribute
     */
    public void xsetEndpoint(org.apache.xmlbeans.XmlString endpoint)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ENDPOINT$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ENDPOINT$0);
            }
            target.set(endpoint);
        }
    }
    
    /**
     * Unsets the "endpoint" attribute
     */
    public void unsetEndpoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ENDPOINT$0);
        }
    }
    
    /**
     * Gets the "brokerURL" attribute
     */
    public java.lang.String getBrokerURL()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(BROKERURL$2);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "brokerURL" attribute
     */
    public org.apache.xmlbeans.XmlString xgetBrokerURL()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(BROKERURL$2);
            return target;
        }
    }
    
    /**
     * True if has "brokerURL" attribute
     */
    public boolean isSetBrokerURL()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(BROKERURL$2) != null;
        }
    }
    
    /**
     * Sets the "brokerURL" attribute
     */
    public void setBrokerURL(java.lang.String brokerURL)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(BROKERURL$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(BROKERURL$2);
            }
            target.setStringValue(brokerURL);
        }
    }
    
    /**
     * Sets (as xml) the "brokerURL" attribute
     */
    public void xsetBrokerURL(org.apache.xmlbeans.XmlString brokerURL)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(BROKERURL$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(BROKERURL$2);
            }
            target.set(brokerURL);
        }
    }
    
    /**
     * Unsets the "brokerURL" attribute
     */
    public void unsetBrokerURL()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(BROKERURL$2);
        }
    }
    
    /**
     * Gets the "prefetch" attribute
     */
    public int getPrefetch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PREFETCH$4);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "prefetch" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetPrefetch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(PREFETCH$4);
            return target;
        }
    }
    
    /**
     * True if has "prefetch" attribute
     */
    public boolean isSetPrefetch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PREFETCH$4) != null;
        }
    }
    
    /**
     * Sets the "prefetch" attribute
     */
    public void setPrefetch(int prefetch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PREFETCH$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PREFETCH$4);
            }
            target.setIntValue(prefetch);
        }
    }
    
    /**
     * Sets (as xml) the "prefetch" attribute
     */
    public void xsetPrefetch(org.apache.xmlbeans.XmlInt prefetch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(PREFETCH$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(PREFETCH$4);
            }
            target.set(prefetch);
        }
    }
    
    /**
     * Unsets the "prefetch" attribute
     */
    public void unsetPrefetch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PREFETCH$4);
        }
    }
}
