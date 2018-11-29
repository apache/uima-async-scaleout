/*
 * XML Type:  serializerType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.SerializerType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML serializerType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.SerializerType.
 */
public class SerializerTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.SerializerType
{
    private static final long serialVersionUID = 1L;
    
    public SerializerTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected SerializerTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName METHOD$0 = 
        new javax.xml.namespace.QName("", "method");
    
    
    /**
     * Gets the "method" attribute
     */
    public org.apache.uima.resourceSpecifier.SerializerType.Method.Enum getMethod()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(METHOD$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(METHOD$0);
            }
            if (target == null)
            {
                return null;
            }
            return (org.apache.uima.resourceSpecifier.SerializerType.Method.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "method" attribute
     */
    public org.apache.uima.resourceSpecifier.SerializerType.Method xgetMethod()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.SerializerType.Method target = null;
            target = (org.apache.uima.resourceSpecifier.SerializerType.Method)get_store().find_attribute_user(METHOD$0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.SerializerType.Method)get_default_attribute_value(METHOD$0);
            }
            return target;
        }
    }
    
    /**
     * True if has "method" attribute
     */
    public boolean isSetMethod()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(METHOD$0) != null;
        }
    }
    
    /**
     * Sets the "method" attribute
     */
    public void setMethod(org.apache.uima.resourceSpecifier.SerializerType.Method.Enum method)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(METHOD$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(METHOD$0);
            }
            target.setEnumValue(method);
        }
    }
    
    /**
     * Sets (as xml) the "method" attribute
     */
    public void xsetMethod(org.apache.uima.resourceSpecifier.SerializerType.Method method)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.SerializerType.Method target = null;
            target = (org.apache.uima.resourceSpecifier.SerializerType.Method)get_store().find_attribute_user(METHOD$0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.SerializerType.Method)get_store().add_attribute_user(METHOD$0);
            }
            target.set(method);
        }
    }
    
    /**
     * Unsets the "method" attribute
     */
    public void unsetMethod()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(METHOD$0);
        }
    }
    /**
     * An XML method(@).
     *
     * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.SerializerType$Method.
     */
    public static class MethodImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements org.apache.uima.resourceSpecifier.SerializerType.Method
    {
        private static final long serialVersionUID = 1L;
        
        public MethodImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType, false);
        }
        
        protected MethodImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
        {
            super(sType, b);
        }
    }
}
