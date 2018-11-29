/*
 * XML Type:  analysisEngineDeploymentDescriptionType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML analysisEngineDeploymentDescriptionType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class AnalysisEngineDeploymentDescriptionTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType
{
    private static final long serialVersionUID = 1L;
    
    public AnalysisEngineDeploymentDescriptionTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName NAME$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "name");
    private static final javax.xml.namespace.QName DESCRIPTION$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "description");
    private static final javax.xml.namespace.QName VERSION$4 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "version");
    private static final javax.xml.namespace.QName VENDOR$6 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "vendor");
    private static final javax.xml.namespace.QName DEPLOYMENT$8 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "deployment");
    
    
    /**
     * Gets the "name" element
     */
    public java.lang.String getName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "name" element
     */
    public org.apache.xmlbeans.XmlString xgetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "name" element
     */
    public void setName(java.lang.String name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NAME$0);
            }
            target.setStringValue(name);
        }
    }
    
    /**
     * Sets (as xml) the "name" element
     */
    public void xsetName(org.apache.xmlbeans.XmlString name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$0);
            }
            target.set(name);
        }
    }
    
    /**
     * Gets the "description" element
     */
    public java.lang.String getDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESCRIPTION$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "description" element
     */
    public org.apache.xmlbeans.XmlString xgetDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "description" element
     */
    public void setDescription(java.lang.String description)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESCRIPTION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DESCRIPTION$2);
            }
            target.setStringValue(description);
        }
    }
    
    /**
     * Sets (as xml) the "description" element
     */
    public void xsetDescription(org.apache.xmlbeans.XmlString description)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DESCRIPTION$2);
            }
            target.set(description);
        }
    }
    
    /**
     * Gets the "version" element
     */
    public java.lang.String getVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VERSION$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "version" element
     */
    public org.apache.xmlbeans.XmlString xgetVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "version" element
     */
    public boolean isSetVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(VERSION$4) != 0;
        }
    }
    
    /**
     * Sets the "version" element
     */
    public void setVersion(java.lang.String version)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VERSION$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VERSION$4);
            }
            target.setStringValue(version);
        }
    }
    
    /**
     * Sets (as xml) the "version" element
     */
    public void xsetVersion(org.apache.xmlbeans.XmlString version)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VERSION$4);
            }
            target.set(version);
        }
    }
    
    /**
     * Unsets the "version" element
     */
    public void unsetVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(VERSION$4, 0);
        }
    }
    
    /**
     * Gets the "vendor" element
     */
    public java.lang.String getVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VENDOR$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "vendor" element
     */
    public org.apache.xmlbeans.XmlString xgetVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VENDOR$6, 0);
            return target;
        }
    }
    
    /**
     * True if has "vendor" element
     */
    public boolean isSetVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(VENDOR$6) != 0;
        }
    }
    
    /**
     * Sets the "vendor" element
     */
    public void setVendor(java.lang.String vendor)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VENDOR$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VENDOR$6);
            }
            target.setStringValue(vendor);
        }
    }
    
    /**
     * Sets (as xml) the "vendor" element
     */
    public void xsetVendor(org.apache.xmlbeans.XmlString vendor)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VENDOR$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VENDOR$6);
            }
            target.set(vendor);
        }
    }
    
    /**
     * Unsets the "vendor" element
     */
    public void unsetVendor()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(VENDOR$6, 0);
        }
    }
    
    /**
     * Gets the "deployment" element
     */
    public org.apache.uima.resourceSpecifier.DeploymentType getDeployment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DeploymentType target = null;
            target = (org.apache.uima.resourceSpecifier.DeploymentType)get_store().find_element_user(DEPLOYMENT$8, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "deployment" element
     */
    public void setDeployment(org.apache.uima.resourceSpecifier.DeploymentType deployment)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DeploymentType target = null;
            target = (org.apache.uima.resourceSpecifier.DeploymentType)get_store().find_element_user(DEPLOYMENT$8, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.DeploymentType)get_store().add_element_user(DEPLOYMENT$8);
            }
            target.set(deployment);
        }
    }
    
    /**
     * Appends and returns a new empty "deployment" element
     */
    public org.apache.uima.resourceSpecifier.DeploymentType addNewDeployment()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DeploymentType target = null;
            target = (org.apache.uima.resourceSpecifier.DeploymentType)get_store().add_element_user(DEPLOYMENT$8);
            return target;
        }
    }
}
