/*
 * XML Type:  casMultiplierType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.CasMultiplierType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML casMultiplierType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.CasMultiplierType.
 */
public class CasMultiplierTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.CasMultiplierType
{
    private static final long serialVersionUID = 1L;
    
    public CasMultiplierTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected CasMultiplierTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName POOLSIZE$0 = 
        new javax.xml.namespace.QName("", "poolSize");
    private static final javax.xml.namespace.QName INITIALFSHEAPSIZE$2 = 
        new javax.xml.namespace.QName("", "initialFsHeapSize");
    private static final javax.xml.namespace.QName PROCESSPARENTLAST$4 = 
        new javax.xml.namespace.QName("", "processParentLast");
    private static final javax.xml.namespace.QName DISABLEJCASCACHE$6 = 
        new javax.xml.namespace.QName("", "disableJCasCache");
    
    
    /**
     * Gets the "poolSize" attribute
     */
    public int getPoolSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(POOLSIZE$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(POOLSIZE$0);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "poolSize" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetPoolSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(POOLSIZE$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(POOLSIZE$0);
            }
            return target;
        }
    }
    
    /**
     * True if has "poolSize" attribute
     */
    public boolean isSetPoolSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(POOLSIZE$0) != null;
        }
    }
    
    /**
     * Sets the "poolSize" attribute
     */
    public void setPoolSize(int poolSize)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(POOLSIZE$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(POOLSIZE$0);
            }
            target.setIntValue(poolSize);
        }
    }
    
    /**
     * Sets (as xml) the "poolSize" attribute
     */
    public void xsetPoolSize(org.apache.xmlbeans.XmlInt poolSize)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(POOLSIZE$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(POOLSIZE$0);
            }
            target.set(poolSize);
        }
    }
    
    /**
     * Unsets the "poolSize" attribute
     */
    public void unsetPoolSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(POOLSIZE$0);
        }
    }
    
    /**
     * Gets the "initialFsHeapSize" attribute
     */
    public java.lang.String getInitialFsHeapSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "initialFsHeapSize" attribute
     */
    public org.apache.xmlbeans.XmlString xgetInitialFsHeapSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
            return target;
        }
    }
    
    /**
     * True if has "initialFsHeapSize" attribute
     */
    public boolean isSetInitialFsHeapSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(INITIALFSHEAPSIZE$2) != null;
        }
    }
    
    /**
     * Sets the "initialFsHeapSize" attribute
     */
    public void setInitialFsHeapSize(java.lang.String initialFsHeapSize)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(INITIALFSHEAPSIZE$2);
            }
            target.setStringValue(initialFsHeapSize);
        }
    }
    
    /**
     * Sets (as xml) the "initialFsHeapSize" attribute
     */
    public void xsetInitialFsHeapSize(org.apache.xmlbeans.XmlString initialFsHeapSize)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(INITIALFSHEAPSIZE$2);
            }
            target.set(initialFsHeapSize);
        }
    }
    
    /**
     * Unsets the "initialFsHeapSize" attribute
     */
    public void unsetInitialFsHeapSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(INITIALFSHEAPSIZE$2);
        }
    }
    
    /**
     * Gets the "processParentLast" attribute
     */
    public java.lang.String getProcessParentLast()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROCESSPARENTLAST$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROCESSPARENTLAST$4);
            }
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "processParentLast" attribute
     */
    public org.apache.xmlbeans.XmlString xgetProcessParentLast()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROCESSPARENTLAST$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_default_attribute_value(PROCESSPARENTLAST$4);
            }
            return target;
        }
    }
    
    /**
     * True if has "processParentLast" attribute
     */
    public boolean isSetProcessParentLast()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PROCESSPARENTLAST$4) != null;
        }
    }
    
    /**
     * Sets the "processParentLast" attribute
     */
    public void setProcessParentLast(java.lang.String processParentLast)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROCESSPARENTLAST$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROCESSPARENTLAST$4);
            }
            target.setStringValue(processParentLast);
        }
    }
    
    /**
     * Sets (as xml) the "processParentLast" attribute
     */
    public void xsetProcessParentLast(org.apache.xmlbeans.XmlString processParentLast)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROCESSPARENTLAST$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROCESSPARENTLAST$4);
            }
            target.set(processParentLast);
        }
    }
    
    /**
     * Unsets the "processParentLast" attribute
     */
    public void unsetProcessParentLast()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PROCESSPARENTLAST$4);
        }
    }
    
    /**
     * Gets the "disableJCasCache" attribute
     */
    public boolean getDisableJCasCache()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DISABLEJCASCACHE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(DISABLEJCASCACHE$6);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "disableJCasCache" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetDisableJCasCache()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DISABLEJCASCACHE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(DISABLEJCASCACHE$6);
            }
            return target;
        }
    }
    
    /**
     * True if has "disableJCasCache" attribute
     */
    public boolean isSetDisableJCasCache()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(DISABLEJCASCACHE$6) != null;
        }
    }
    
    /**
     * Sets the "disableJCasCache" attribute
     */
    public void setDisableJCasCache(boolean disableJCasCache)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DISABLEJCASCACHE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DISABLEJCASCACHE$6);
            }
            target.setBooleanValue(disableJCasCache);
        }
    }
    
    /**
     * Sets (as xml) the "disableJCasCache" attribute
     */
    public void xsetDisableJCasCache(org.apache.xmlbeans.XmlBoolean disableJCasCache)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DISABLEJCASCACHE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(DISABLEJCASCACHE$6);
            }
            target.set(disableJCasCache);
        }
    }
    
    /**
     * Unsets the "disableJCasCache" attribute
     */
    public void unsetDisableJCasCache()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(DISABLEJCASCACHE$6);
        }
    }
}
