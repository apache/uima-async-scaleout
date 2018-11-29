/*
 * XML Type:  deploymentType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.DeploymentType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML deploymentType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class DeploymentTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.DeploymentType
{
    private static final long serialVersionUID = 1L;
    
    public DeploymentTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CASPOOL$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "casPool");
    private static final javax.xml.namespace.QName SERVICE$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "service");
    private static final javax.xml.namespace.QName PROTOCOL$4 = 
        new javax.xml.namespace.QName("", "protocol");
    private static final javax.xml.namespace.QName PROVIDER$6 = 
        new javax.xml.namespace.QName("", "provider");
    
    
    /**
     * Gets the "casPool" element
     */
    public org.apache.uima.resourceSpecifier.CasPoolType getCasPool()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasPoolType target = null;
            target = (org.apache.uima.resourceSpecifier.CasPoolType)get_store().find_element_user(CASPOOL$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "casPool" element
     */
    public void setCasPool(org.apache.uima.resourceSpecifier.CasPoolType casPool)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasPoolType target = null;
            target = (org.apache.uima.resourceSpecifier.CasPoolType)get_store().find_element_user(CASPOOL$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.CasPoolType)get_store().add_element_user(CASPOOL$0);
            }
            target.set(casPool);
        }
    }
    
    /**
     * Appends and returns a new empty "casPool" element
     */
    public org.apache.uima.resourceSpecifier.CasPoolType addNewCasPool()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasPoolType target = null;
            target = (org.apache.uima.resourceSpecifier.CasPoolType)get_store().add_element_user(CASPOOL$0);
            return target;
        }
    }
    
    /**
     * Gets the "service" element
     */
    public org.apache.uima.resourceSpecifier.ServiceType getService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ServiceType target = null;
            target = (org.apache.uima.resourceSpecifier.ServiceType)get_store().find_element_user(SERVICE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "service" element
     */
    public void setService(org.apache.uima.resourceSpecifier.ServiceType service)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ServiceType target = null;
            target = (org.apache.uima.resourceSpecifier.ServiceType)get_store().find_element_user(SERVICE$2, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.ServiceType)get_store().add_element_user(SERVICE$2);
            }
            target.set(service);
        }
    }
    
    /**
     * Appends and returns a new empty "service" element
     */
    public org.apache.uima.resourceSpecifier.ServiceType addNewService()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ServiceType target = null;
            target = (org.apache.uima.resourceSpecifier.ServiceType)get_store().add_element_user(SERVICE$2);
            return target;
        }
    }
    
    /**
     * Gets the "protocol" attribute
     */
    public java.lang.String getProtocol()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROTOCOL$4);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "protocol" attribute
     */
    public org.apache.xmlbeans.XmlString xgetProtocol()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROTOCOL$4);
            return target;
        }
    }
    
    /**
     * True if has "protocol" attribute
     */
    public boolean isSetProtocol()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PROTOCOL$4) != null;
        }
    }
    
    /**
     * Sets the "protocol" attribute
     */
    public void setProtocol(java.lang.String protocol)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROTOCOL$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROTOCOL$4);
            }
            target.setStringValue(protocol);
        }
    }
    
    /**
     * Sets (as xml) the "protocol" attribute
     */
    public void xsetProtocol(org.apache.xmlbeans.XmlString protocol)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROTOCOL$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROTOCOL$4);
            }
            target.set(protocol);
        }
    }
    
    /**
     * Unsets the "protocol" attribute
     */
    public void unsetProtocol()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PROTOCOL$4);
        }
    }
    
    /**
     * Gets the "provider" attribute
     */
    public java.lang.String getProvider()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROVIDER$6);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "provider" attribute
     */
    public org.apache.xmlbeans.XmlString xgetProvider()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROVIDER$6);
            return target;
        }
    }
    
    /**
     * True if has "provider" attribute
     */
    public boolean isSetProvider()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PROVIDER$6) != null;
        }
    }
    
    /**
     * Sets the "provider" attribute
     */
    public void setProvider(java.lang.String provider)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROVIDER$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROVIDER$6);
            }
            target.setStringValue(provider);
        }
    }
    
    /**
     * Sets (as xml) the "provider" attribute
     */
    public void xsetProvider(org.apache.xmlbeans.XmlString provider)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(PROVIDER$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(PROVIDER$6);
            }
            target.set(provider);
        }
    }
    
    /**
     * Unsets the "provider" attribute
     */
    public void unsetProvider()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PROVIDER$6);
        }
    }
}
