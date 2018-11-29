/*
 * XML Type:  collectionProcessCompleteErrorsType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML collectionProcessCompleteErrorsType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType.
 */
public class CollectionProcessCompleteErrorsTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType
{
    private static final long serialVersionUID = 1L;
    
    public CollectionProcessCompleteErrorsTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected CollectionProcessCompleteErrorsTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName TIMEOUT$0 = 
        new javax.xml.namespace.QName("", "timeout");
    private static final javax.xml.namespace.QName ADDITIONALERRORACTION$2 = 
        new javax.xml.namespace.QName("", "additionalErrorAction");
    
    
    /**
     * Gets the "timeout" attribute
     */
    public int getTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(TIMEOUT$0);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "timeout" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(TIMEOUT$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(TIMEOUT$0);
            }
            return target;
        }
    }
    
    /**
     * True if has "timeout" attribute
     */
    public boolean isSetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(TIMEOUT$0) != null;
        }
    }
    
    /**
     * Sets the "timeout" attribute
     */
    public void setTimeout(int timeout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TIMEOUT$0);
            }
            target.setIntValue(timeout);
        }
    }
    
    /**
     * Sets (as xml) the "timeout" attribute
     */
    public void xsetTimeout(org.apache.xmlbeans.XmlInt timeout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(TIMEOUT$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(TIMEOUT$0);
            }
            target.set(timeout);
        }
    }
    
    /**
     * Unsets the "timeout" attribute
     */
    public void unsetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(TIMEOUT$0);
        }
    }
    
    /**
     * Gets the "additionalErrorAction" attribute
     */
    public java.lang.String getAdditionalErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ADDITIONALERRORACTION$2);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "additionalErrorAction" attribute
     */
    public org.apache.xmlbeans.XmlString xgetAdditionalErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ADDITIONALERRORACTION$2);
            return target;
        }
    }
    
    /**
     * True if has "additionalErrorAction" attribute
     */
    public boolean isSetAdditionalErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ADDITIONALERRORACTION$2) != null;
        }
    }
    
    /**
     * Sets the "additionalErrorAction" attribute
     */
    public void setAdditionalErrorAction(java.lang.String additionalErrorAction)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ADDITIONALERRORACTION$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ADDITIONALERRORACTION$2);
            }
            target.setStringValue(additionalErrorAction);
        }
    }
    
    /**
     * Sets (as xml) the "additionalErrorAction" attribute
     */
    public void xsetAdditionalErrorAction(org.apache.xmlbeans.XmlString additionalErrorAction)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ADDITIONALERRORACTION$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ADDITIONALERRORACTION$2);
            }
            target.set(additionalErrorAction);
        }
    }
    
    /**
     * Unsets the "additionalErrorAction" attribute
     */
    public void unsetAdditionalErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ADDITIONALERRORACTION$2);
        }
    }
}
