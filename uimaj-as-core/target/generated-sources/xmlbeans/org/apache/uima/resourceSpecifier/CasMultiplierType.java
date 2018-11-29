/*
 * XML Type:  casMultiplierType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.CasMultiplierType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML casMultiplierType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.CasMultiplierType.
 */
public interface CasMultiplierType extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CasMultiplierType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("casmultipliertypef712type");
    
    /**
     * Gets the "poolSize" attribute
     */
    int getPoolSize();
    
    /**
     * Gets (as xml) the "poolSize" attribute
     */
    org.apache.xmlbeans.XmlInt xgetPoolSize();
    
    /**
     * True if has "poolSize" attribute
     */
    boolean isSetPoolSize();
    
    /**
     * Sets the "poolSize" attribute
     */
    void setPoolSize(int poolSize);
    
    /**
     * Sets (as xml) the "poolSize" attribute
     */
    void xsetPoolSize(org.apache.xmlbeans.XmlInt poolSize);
    
    /**
     * Unsets the "poolSize" attribute
     */
    void unsetPoolSize();
    
    /**
     * Gets the "initialFsHeapSize" attribute
     */
    java.lang.String getInitialFsHeapSize();
    
    /**
     * Gets (as xml) the "initialFsHeapSize" attribute
     */
    org.apache.xmlbeans.XmlString xgetInitialFsHeapSize();
    
    /**
     * True if has "initialFsHeapSize" attribute
     */
    boolean isSetInitialFsHeapSize();
    
    /**
     * Sets the "initialFsHeapSize" attribute
     */
    void setInitialFsHeapSize(java.lang.String initialFsHeapSize);
    
    /**
     * Sets (as xml) the "initialFsHeapSize" attribute
     */
    void xsetInitialFsHeapSize(org.apache.xmlbeans.XmlString initialFsHeapSize);
    
    /**
     * Unsets the "initialFsHeapSize" attribute
     */
    void unsetInitialFsHeapSize();
    
    /**
     * Gets the "processParentLast" attribute
     */
    java.lang.String getProcessParentLast();
    
    /**
     * Gets (as xml) the "processParentLast" attribute
     */
    org.apache.xmlbeans.XmlString xgetProcessParentLast();
    
    /**
     * True if has "processParentLast" attribute
     */
    boolean isSetProcessParentLast();
    
    /**
     * Sets the "processParentLast" attribute
     */
    void setProcessParentLast(java.lang.String processParentLast);
    
    /**
     * Sets (as xml) the "processParentLast" attribute
     */
    void xsetProcessParentLast(org.apache.xmlbeans.XmlString processParentLast);
    
    /**
     * Unsets the "processParentLast" attribute
     */
    void unsetProcessParentLast();
    
    /**
     * Gets the "disableJCasCache" attribute
     */
    boolean getDisableJCasCache();
    
    /**
     * Gets (as xml) the "disableJCasCache" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetDisableJCasCache();
    
    /**
     * True if has "disableJCasCache" attribute
     */
    boolean isSetDisableJCasCache();
    
    /**
     * Sets the "disableJCasCache" attribute
     */
    void setDisableJCasCache(boolean disableJCasCache);
    
    /**
     * Sets (as xml) the "disableJCasCache" attribute
     */
    void xsetDisableJCasCache(org.apache.xmlbeans.XmlBoolean disableJCasCache);
    
    /**
     * Unsets the "disableJCasCache" attribute
     */
    void unsetDisableJCasCache();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.CasMultiplierType newInstance() {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.CasMultiplierType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.CasMultiplierType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
