/*
 * An XML document type.
 * Localname: analysisEngineDeploymentDescription
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * A document containing one analysisEngineDeploymentDescription(@http://uima.apache.org/resourceSpecifier) element.
 *
 * This is a complex type.
 */
public class AnalysisEngineDeploymentDescriptionDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionDocument
{
    private static final long serialVersionUID = 1L;
    
    public AnalysisEngineDeploymentDescriptionDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ANALYSISENGINEDEPLOYMENTDESCRIPTION$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "analysisEngineDeploymentDescription");
    
    
    /**
     * Gets the "analysisEngineDeploymentDescription" element
     */
    public org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType getAnalysisEngineDeploymentDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType target = null;
            target = (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType)get_store().find_element_user(ANALYSISENGINEDEPLOYMENTDESCRIPTION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "analysisEngineDeploymentDescription" element
     */
    public void setAnalysisEngineDeploymentDescription(org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType analysisEngineDeploymentDescription)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType target = null;
            target = (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType)get_store().find_element_user(ANALYSISENGINEDEPLOYMENTDESCRIPTION$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType)get_store().add_element_user(ANALYSISENGINEDEPLOYMENTDESCRIPTION$0);
            }
            target.set(analysisEngineDeploymentDescription);
        }
    }
    
    /**
     * Appends and returns a new empty "analysisEngineDeploymentDescription" element
     */
    public org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType addNewAnalysisEngineDeploymentDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType target = null;
            target = (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType)get_store().add_element_user(ANALYSISENGINEDEPLOYMENTDESCRIPTION$0);
            return target;
        }
    }
}
