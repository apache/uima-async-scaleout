/*
 * XML Type:  deploymentType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.DeploymentType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML deploymentType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface DeploymentType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DeploymentType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("deploymenttype5271type");
    
    /**
     * Gets the "casPool" element
     */
    org.apache.uima.resourceSpecifier.CasPoolType getCasPool();
    
    /**
     * Sets the "casPool" element
     */
    void setCasPool(org.apache.uima.resourceSpecifier.CasPoolType casPool);
    
    /**
     * Appends and returns a new empty "casPool" element
     */
    org.apache.uima.resourceSpecifier.CasPoolType addNewCasPool();
    
    /**
     * Gets the "service" element
     */
    org.apache.uima.resourceSpecifier.ServiceType getService();
    
    /**
     * Sets the "service" element
     */
    void setService(org.apache.uima.resourceSpecifier.ServiceType service);
    
    /**
     * Appends and returns a new empty "service" element
     */
    org.apache.uima.resourceSpecifier.ServiceType addNewService();
    
    /**
     * Gets the "protocol" attribute
     */
    java.lang.String getProtocol();
    
    /**
     * Gets (as xml) the "protocol" attribute
     */
    org.apache.xmlbeans.XmlString xgetProtocol();
    
    /**
     * True if has "protocol" attribute
     */
    boolean isSetProtocol();
    
    /**
     * Sets the "protocol" attribute
     */
    void setProtocol(java.lang.String protocol);
    
    /**
     * Sets (as xml) the "protocol" attribute
     */
    void xsetProtocol(org.apache.xmlbeans.XmlString protocol);
    
    /**
     * Unsets the "protocol" attribute
     */
    void unsetProtocol();
    
    /**
     * Gets the "provider" attribute
     */
    java.lang.String getProvider();
    
    /**
     * Gets (as xml) the "provider" attribute
     */
    org.apache.xmlbeans.XmlString xgetProvider();
    
    /**
     * True if has "provider" attribute
     */
    boolean isSetProvider();
    
    /**
     * Sets the "provider" attribute
     */
    void setProvider(java.lang.String provider);
    
    /**
     * Sets (as xml) the "provider" attribute
     */
    void xsetProvider(org.apache.xmlbeans.XmlString provider);
    
    /**
     * Unsets the "provider" attribute
     */
    void unsetProvider();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.DeploymentType newInstance() {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.DeploymentType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.DeploymentType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
