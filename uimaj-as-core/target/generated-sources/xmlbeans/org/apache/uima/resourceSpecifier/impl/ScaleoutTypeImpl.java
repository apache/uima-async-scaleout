/*
 * XML Type:  scaleoutType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.ScaleoutType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML scaleoutType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.ScaleoutType.
 */
public class ScaleoutTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.ScaleoutType
{
    private static final long serialVersionUID = 1L;
    
    public ScaleoutTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected ScaleoutTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName NUMBEROFINSTANCES$0 = 
        new javax.xml.namespace.QName("", "numberOfInstances");
    
    
    /**
     * Gets the "numberOfInstances" attribute
     */
    public int getNumberOfInstances()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NUMBEROFINSTANCES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(NUMBEROFINSTANCES$0);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "numberOfInstances" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetNumberOfInstances()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(NUMBEROFINSTANCES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(NUMBEROFINSTANCES$0);
            }
            return target;
        }
    }
    
    /**
     * True if has "numberOfInstances" attribute
     */
    public boolean isSetNumberOfInstances()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(NUMBEROFINSTANCES$0) != null;
        }
    }
    
    /**
     * Sets the "numberOfInstances" attribute
     */
    public void setNumberOfInstances(int numberOfInstances)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NUMBEROFINSTANCES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(NUMBEROFINSTANCES$0);
            }
            target.setIntValue(numberOfInstances);
        }
    }
    
    /**
     * Sets (as xml) the "numberOfInstances" attribute
     */
    public void xsetNumberOfInstances(org.apache.xmlbeans.XmlInt numberOfInstances)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(NUMBEROFINSTANCES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(NUMBEROFINSTANCES$0);
            }
            target.set(numberOfInstances);
        }
    }
    
    /**
     * Unsets the "numberOfInstances" attribute
     */
    public void unsetNumberOfInstances()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(NUMBEROFINSTANCES$0);
        }
    }
}
