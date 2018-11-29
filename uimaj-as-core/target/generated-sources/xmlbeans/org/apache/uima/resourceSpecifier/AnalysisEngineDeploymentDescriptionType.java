/*
 * XML Type:  analysisEngineDeploymentDescriptionType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML analysisEngineDeploymentDescriptionType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface AnalysisEngineDeploymentDescriptionType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AnalysisEngineDeploymentDescriptionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("analysisenginedeploymentdescriptiontypef84ftype");
    
    /**
     * Gets the "name" element
     */
    java.lang.String getName();
    
    /**
     * Gets (as xml) the "name" element
     */
    org.apache.xmlbeans.XmlString xgetName();
    
    /**
     * Sets the "name" element
     */
    void setName(java.lang.String name);
    
    /**
     * Sets (as xml) the "name" element
     */
    void xsetName(org.apache.xmlbeans.XmlString name);
    
    /**
     * Gets the "description" element
     */
    java.lang.String getDescription();
    
    /**
     * Gets (as xml) the "description" element
     */
    org.apache.xmlbeans.XmlString xgetDescription();
    
    /**
     * Sets the "description" element
     */
    void setDescription(java.lang.String description);
    
    /**
     * Sets (as xml) the "description" element
     */
    void xsetDescription(org.apache.xmlbeans.XmlString description);
    
    /**
     * Gets the "version" element
     */
    java.lang.String getVersion();
    
    /**
     * Gets (as xml) the "version" element
     */
    org.apache.xmlbeans.XmlString xgetVersion();
    
    /**
     * True if has "version" element
     */
    boolean isSetVersion();
    
    /**
     * Sets the "version" element
     */
    void setVersion(java.lang.String version);
    
    /**
     * Sets (as xml) the "version" element
     */
    void xsetVersion(org.apache.xmlbeans.XmlString version);
    
    /**
     * Unsets the "version" element
     */
    void unsetVersion();
    
    /**
     * Gets the "vendor" element
     */
    java.lang.String getVendor();
    
    /**
     * Gets (as xml) the "vendor" element
     */
    org.apache.xmlbeans.XmlString xgetVendor();
    
    /**
     * True if has "vendor" element
     */
    boolean isSetVendor();
    
    /**
     * Sets the "vendor" element
     */
    void setVendor(java.lang.String vendor);
    
    /**
     * Sets (as xml) the "vendor" element
     */
    void xsetVendor(org.apache.xmlbeans.XmlString vendor);
    
    /**
     * Unsets the "vendor" element
     */
    void unsetVendor();
    
    /**
     * Gets the "deployment" element
     */
    org.apache.uima.resourceSpecifier.DeploymentType getDeployment();
    
    /**
     * Sets the "deployment" element
     */
    void setDeployment(org.apache.uima.resourceSpecifier.DeploymentType deployment);
    
    /**
     * Appends and returns a new empty "deployment" element
     */
    org.apache.uima.resourceSpecifier.DeploymentType addNewDeployment();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType newInstance() {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineDeploymentDescriptionType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
