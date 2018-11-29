/*
 * XML Type:  delegatesType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.DelegatesType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML delegatesType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface DelegatesType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DelegatesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("delegatestype3a7atype");
    
    /**
     * Gets array of all "analysisEngine" elements
     */
    org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType[] getAnalysisEngineArray();
    
    /**
     * Gets ith "analysisEngine" element
     */
    org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType getAnalysisEngineArray(int i);
    
    /**
     * Returns number of "analysisEngine" element
     */
    int sizeOfAnalysisEngineArray();
    
    /**
     * Sets array of all "analysisEngine" element
     */
    void setAnalysisEngineArray(org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType[] analysisEngineArray);
    
    /**
     * Sets ith "analysisEngine" element
     */
    void setAnalysisEngineArray(int i, org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType analysisEngine);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "analysisEngine" element
     */
    org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType insertNewAnalysisEngine(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "analysisEngine" element
     */
    org.apache.uima.resourceSpecifier.DelegateAnalysisEngineType addNewAnalysisEngine();
    
    /**
     * Removes the ith "analysisEngine" element
     */
    void removeAnalysisEngine(int i);
    
    /**
     * Gets array of all "remoteAnalysisEngine" elements
     */
    org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType[] getRemoteAnalysisEngineArray();
    
    /**
     * Gets ith "remoteAnalysisEngine" element
     */
    org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType getRemoteAnalysisEngineArray(int i);
    
    /**
     * Returns number of "remoteAnalysisEngine" element
     */
    int sizeOfRemoteAnalysisEngineArray();
    
    /**
     * Sets array of all "remoteAnalysisEngine" element
     */
    void setRemoteAnalysisEngineArray(org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType[] remoteAnalysisEngineArray);
    
    /**
     * Sets ith "remoteAnalysisEngine" element
     */
    void setRemoteAnalysisEngineArray(int i, org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType remoteAnalysisEngine);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "remoteAnalysisEngine" element
     */
    org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType insertNewRemoteAnalysisEngine(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "remoteAnalysisEngine" element
     */
    org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType addNewRemoteAnalysisEngine();
    
    /**
     * Removes the ith "remoteAnalysisEngine" element
     */
    void removeRemoteAnalysisEngine(int i);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.DelegatesType newInstance() {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.DelegatesType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.DelegatesType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
