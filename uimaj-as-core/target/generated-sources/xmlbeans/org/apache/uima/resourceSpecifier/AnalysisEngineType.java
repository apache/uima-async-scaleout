/*
 * XML Type:  analysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML analysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface AnalysisEngineType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AnalysisEngineType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("analysisenginetype8618type");
    
    /**
     * Gets the "scaleout" element
     */
    org.apache.uima.resourceSpecifier.ScaleoutType getScaleout();
    
    /**
     * True if has "scaleout" element
     */
    boolean isSetScaleout();
    
    /**
     * Sets the "scaleout" element
     */
    void setScaleout(org.apache.uima.resourceSpecifier.ScaleoutType scaleout);
    
    /**
     * Appends and returns a new empty "scaleout" element
     */
    org.apache.uima.resourceSpecifier.ScaleoutType addNewScaleout();
    
    /**
     * Unsets the "scaleout" element
     */
    void unsetScaleout();
    
    /**
     * Gets the "delegates" element
     */
    org.apache.uima.resourceSpecifier.DelegatesType getDelegates();
    
    /**
     * True if has "delegates" element
     */
    boolean isSetDelegates();
    
    /**
     * Sets the "delegates" element
     */
    void setDelegates(org.apache.uima.resourceSpecifier.DelegatesType delegates);
    
    /**
     * Appends and returns a new empty "delegates" element
     */
    org.apache.uima.resourceSpecifier.DelegatesType addNewDelegates();
    
    /**
     * Unsets the "delegates" element
     */
    void unsetDelegates();
    
    /**
     * Gets the "casMultiplier" element
     */
    org.apache.uima.resourceSpecifier.CasMultiplierType getCasMultiplier();
    
    /**
     * True if has "casMultiplier" element
     */
    boolean isSetCasMultiplier();
    
    /**
     * Sets the "casMultiplier" element
     */
    void setCasMultiplier(org.apache.uima.resourceSpecifier.CasMultiplierType casMultiplier);
    
    /**
     * Appends and returns a new empty "casMultiplier" element
     */
    org.apache.uima.resourceSpecifier.CasMultiplierType addNewCasMultiplier();
    
    /**
     * Unsets the "casMultiplier" element
     */
    void unsetCasMultiplier();
    
    /**
     * Gets the "key" attribute
     */
    java.lang.String getKey();
    
    /**
     * Gets (as xml) the "key" attribute
     */
    org.apache.xmlbeans.XmlString xgetKey();
    
    /**
     * True if has "key" attribute
     */
    boolean isSetKey();
    
    /**
     * Sets the "key" attribute
     */
    void setKey(java.lang.String key);
    
    /**
     * Sets (as xml) the "key" attribute
     */
    void xsetKey(org.apache.xmlbeans.XmlString key);
    
    /**
     * Unsets the "key" attribute
     */
    void unsetKey();
    
    /**
     * Gets the "async" attribute
     */
    java.lang.String getAsync();
    
    /**
     * Gets (as xml) the "async" attribute
     */
    org.apache.xmlbeans.XmlString xgetAsync();
    
    /**
     * True if has "async" attribute
     */
    boolean isSetAsync();
    
    /**
     * Sets the "async" attribute
     */
    void setAsync(java.lang.String async);
    
    /**
     * Sets (as xml) the "async" attribute
     */
    void xsetAsync(org.apache.xmlbeans.XmlString async);
    
    /**
     * Unsets the "async" attribute
     */
    void unsetAsync();
    
    /**
     * Gets the "internalReplyQueueScaleout" attribute
     */
    java.lang.String getInternalReplyQueueScaleout();
    
    /**
     * Gets (as xml) the "internalReplyQueueScaleout" attribute
     */
    org.apache.xmlbeans.XmlString xgetInternalReplyQueueScaleout();
    
    /**
     * True if has "internalReplyQueueScaleout" attribute
     */
    boolean isSetInternalReplyQueueScaleout();
    
    /**
     * Sets the "internalReplyQueueScaleout" attribute
     */
    void setInternalReplyQueueScaleout(java.lang.String internalReplyQueueScaleout);
    
    /**
     * Sets (as xml) the "internalReplyQueueScaleout" attribute
     */
    void xsetInternalReplyQueueScaleout(org.apache.xmlbeans.XmlString internalReplyQueueScaleout);
    
    /**
     * Unsets the "internalReplyQueueScaleout" attribute
     */
    void unsetInternalReplyQueueScaleout();
    
    /**
     * Gets the "inputQueueScaleout" attribute
     */
    java.lang.String getInputQueueScaleout();
    
    /**
     * Gets (as xml) the "inputQueueScaleout" attribute
     */
    org.apache.xmlbeans.XmlString xgetInputQueueScaleout();
    
    /**
     * True if has "inputQueueScaleout" attribute
     */
    boolean isSetInputQueueScaleout();
    
    /**
     * Sets the "inputQueueScaleout" attribute
     */
    void setInputQueueScaleout(java.lang.String inputQueueScaleout);
    
    /**
     * Sets (as xml) the "inputQueueScaleout" attribute
     */
    void xsetInputQueueScaleout(org.apache.xmlbeans.XmlString inputQueueScaleout);
    
    /**
     * Unsets the "inputQueueScaleout" attribute
     */
    void unsetInputQueueScaleout();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType newInstance() {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.AnalysisEngineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.AnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
