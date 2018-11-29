/*
 * XML Type:  topLevelAnalysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML topLevelAnalysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface TopLevelAnalysisEngineType extends org.apache.uima.resourceSpecifier.AnalysisEngineType
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(TopLevelAnalysisEngineType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("toplevelanalysisenginetype8669type");
    
    /**
     * Gets the "asyncPrimitiveErrorConfiguration" element
     */
    org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType getAsyncPrimitiveErrorConfiguration();
    
    /**
     * True if has "asyncPrimitiveErrorConfiguration" element
     */
    boolean isSetAsyncPrimitiveErrorConfiguration();
    
    /**
     * Sets the "asyncPrimitiveErrorConfiguration" element
     */
    void setAsyncPrimitiveErrorConfiguration(org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType asyncPrimitiveErrorConfiguration);
    
    /**
     * Appends and returns a new empty "asyncPrimitiveErrorConfiguration" element
     */
    org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType addNewAsyncPrimitiveErrorConfiguration();
    
    /**
     * Unsets the "asyncPrimitiveErrorConfiguration" element
     */
    void unsetAsyncPrimitiveErrorConfiguration();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType newInstance() {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.TopLevelAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
