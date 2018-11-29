/*
 * XML Type:  topDescriptorType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.TopDescriptorType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML topDescriptorType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class TopDescriptorTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.TopDescriptorType
{
    private static final long serialVersionUID = 1L;
    
    public TopDescriptorTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName IMPORT$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "import");
    
    
    /**
     * Gets the "import" element
     */
    public org.apache.uima.resourceSpecifier.ImportType getImport()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ImportType target = null;
            target = (org.apache.uima.resourceSpecifier.ImportType)get_store().find_element_user(IMPORT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "import" element
     */
    public void setImport(org.apache.uima.resourceSpecifier.ImportType ximport)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ImportType target = null;
            target = (org.apache.uima.resourceSpecifier.ImportType)get_store().find_element_user(IMPORT$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.ImportType)get_store().add_element_user(IMPORT$0);
            }
            target.set(ximport);
        }
    }
    
    /**
     * Appends and returns a new empty "import" element
     */
    public org.apache.uima.resourceSpecifier.ImportType addNewImport()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ImportType target = null;
            target = (org.apache.uima.resourceSpecifier.ImportType)get_store().add_element_user(IMPORT$0);
            return target;
        }
    }
}
