/*
 * XML Type:  getMetadataErrorsType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.GetMetadataErrorsType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML getMetadataErrorsType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.GetMetadataErrorsType.
 */
public class GetMetadataErrorsTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.GetMetadataErrorsType
{
    private static final long serialVersionUID = 1L;
    
    public GetMetadataErrorsTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected GetMetadataErrorsTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName MAXRETRIES$0 = 
        new javax.xml.namespace.QName("", "maxRetries");
    private static final javax.xml.namespace.QName TIMEOUT$2 = 
        new javax.xml.namespace.QName("", "timeout");
    private static final javax.xml.namespace.QName ERRORACTION$4 = 
        new javax.xml.namespace.QName("", "errorAction");
    
    
    /**
     * Gets the "maxRetries" attribute
     */
    public int getMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(MAXRETRIES$0);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "maxRetries" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(MAXRETRIES$0);
            }
            return target;
        }
    }
    
    /**
     * True if has "maxRetries" attribute
     */
    public boolean isSetMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(MAXRETRIES$0) != null;
        }
    }
    
    /**
     * Sets the "maxRetries" attribute
     */
    public void setMaxRetries(int maxRetries)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(MAXRETRIES$0);
            }
            target.setIntValue(maxRetries);
        }
    }
    
    /**
     * Sets (as xml) the "maxRetries" attribute
     */
    public void xsetMaxRetries(org.apache.xmlbeans.XmlInt maxRetries)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(MAXRETRIES$0);
            }
            target.set(maxRetries);
        }
    }
    
    /**
     * Unsets the "maxRetries" attribute
     */
    public void unsetMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(MAXRETRIES$0);
        }
    }
    
    /**
     * Gets the "timeout" attribute
     */
    public int getTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(TIMEOUT$2);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(TIMEOUT$2);
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
            return get_store().find_attribute_user(TIMEOUT$2) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TIMEOUT$2);
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
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(TIMEOUT$2);
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
            get_store().remove_attribute(TIMEOUT$2);
        }
    }
    
    /**
     * Gets the "errorAction" attribute
     */
    public java.lang.String getErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ERRORACTION$4);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "errorAction" attribute
     */
    public org.apache.xmlbeans.XmlString xgetErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ERRORACTION$4);
            return target;
        }
    }
    
    /**
     * True if has "errorAction" attribute
     */
    public boolean isSetErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ERRORACTION$4) != null;
        }
    }
    
    /**
     * Sets the "errorAction" attribute
     */
    public void setErrorAction(java.lang.String errorAction)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ERRORACTION$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ERRORACTION$4);
            }
            target.setStringValue(errorAction);
        }
    }
    
    /**
     * Sets (as xml) the "errorAction" attribute
     */
    public void xsetErrorAction(org.apache.xmlbeans.XmlString errorAction)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ERRORACTION$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ERRORACTION$4);
            }
            target.set(errorAction);
        }
    }
    
    /**
     * Unsets the "errorAction" attribute
     */
    public void unsetErrorAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ERRORACTION$4);
        }
    }
}
