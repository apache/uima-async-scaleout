/*
 * XML Type:  casPoolType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.CasPoolType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML casPoolType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.CasPoolType.
 */
public class CasPoolTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.CasPoolType
{
    private static final long serialVersionUID = 1L;
    
    public CasPoolTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected CasPoolTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName NUMBEROFCASES$0 = 
        new javax.xml.namespace.QName("", "numberOfCASes");
    private static final javax.xml.namespace.QName INITIALFSHEAPSIZE$2 = 
        new javax.xml.namespace.QName("", "initialFsHeapSize");
    private static final javax.xml.namespace.QName DISABLEJCASCACHE$4 = 
        new javax.xml.namespace.QName("", "disableJCasCache");
    
    
    /**
     * Gets the "numberOfCASes" attribute
     */
    public int getNumberOfCASes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NUMBEROFCASES$0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "numberOfCASes" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetNumberOfCASes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(NUMBEROFCASES$0);
            return target;
        }
    }
    
    /**
     * True if has "numberOfCASes" attribute
     */
    public boolean isSetNumberOfCASes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(NUMBEROFCASES$0) != null;
        }
    }
    
    /**
     * Sets the "numberOfCASes" attribute
     */
    public void setNumberOfCASes(int numberOfCASes)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NUMBEROFCASES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(NUMBEROFCASES$0);
            }
            target.setIntValue(numberOfCASes);
        }
    }
    
    /**
     * Sets (as xml) the "numberOfCASes" attribute
     */
    public void xsetNumberOfCASes(org.apache.xmlbeans.XmlInt numberOfCASes)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(NUMBEROFCASES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(NUMBEROFCASES$0);
            }
            target.set(numberOfCASes);
        }
    }
    
    /**
     * Unsets the "numberOfCASes" attribute
     */
    public void unsetNumberOfCASes()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(NUMBEROFCASES$0);
        }
    }
    
    /**
     * Gets the "initialFsHeapSize" attribute
     */
    public int getInitialFsHeapSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "initialFsHeapSize" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetInitialFsHeapSize()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
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
    public void setInitialFsHeapSize(int initialFsHeapSize)
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
            target.setIntValue(initialFsHeapSize);
        }
    }
    
    /**
     * Sets (as xml) the "initialFsHeapSize" attribute
     */
    public void xsetInitialFsHeapSize(org.apache.xmlbeans.XmlInt initialFsHeapSize)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(INITIALFSHEAPSIZE$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(INITIALFSHEAPSIZE$2);
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
     * Gets the "disableJCasCache" attribute
     */
    public boolean getDisableJCasCache()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DISABLEJCASCACHE$4);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DISABLEJCASCACHE$4);
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
            return get_store().find_attribute_user(DISABLEJCASCACHE$4) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DISABLEJCASCACHE$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DISABLEJCASCACHE$4);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DISABLEJCASCACHE$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(DISABLEJCASCACHE$4);
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
            get_store().remove_attribute(DISABLEJCASCACHE$4);
        }
    }
}
