/*
 * XML Type:  remoteAnalysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML remoteAnalysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface RemoteAnalysisEngineType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RemoteAnalysisEngineType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("remoteanalysisenginetype7cb2type");
    
    /**
     * Gets the "inputQueue" element
     */
    org.apache.uima.resourceSpecifier.InputQueueType getInputQueue();
    
    /**
     * Sets the "inputQueue" element
     */
    void setInputQueue(org.apache.uima.resourceSpecifier.InputQueueType inputQueue);
    
    /**
     * Appends and returns a new empty "inputQueue" element
     */
    org.apache.uima.resourceSpecifier.InputQueueType addNewInputQueue();
    
    /**
     * Gets the "serializer" element
     */
    org.apache.uima.resourceSpecifier.SerializerType getSerializer();
    
    /**
     * Sets the "serializer" element
     */
    void setSerializer(org.apache.uima.resourceSpecifier.SerializerType serializer);
    
    /**
     * Appends and returns a new empty "serializer" element
     */
    org.apache.uima.resourceSpecifier.SerializerType addNewSerializer();
    
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
     * Gets the "asyncAggregateErrorConfiguration" element
     */
    org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType getAsyncAggregateErrorConfiguration();
    
    /**
     * True if has "asyncAggregateErrorConfiguration" element
     */
    boolean isSetAsyncAggregateErrorConfiguration();
    
    /**
     * Sets the "asyncAggregateErrorConfiguration" element
     */
    void setAsyncAggregateErrorConfiguration(org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType asyncAggregateErrorConfiguration);
    
    /**
     * Appends and returns a new empty "asyncAggregateErrorConfiguration" element
     */
    org.apache.uima.resourceSpecifier.AsyncAggregateErrorConfigurationType addNewAsyncAggregateErrorConfiguration();
    
    /**
     * Unsets the "asyncAggregateErrorConfiguration" element
     */
    void unsetAsyncAggregateErrorConfiguration();
    
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
     * Gets the "remoteReplyQueueScaleout" attribute
     */
    int getRemoteReplyQueueScaleout();
    
    /**
     * Gets (as xml) the "remoteReplyQueueScaleout" attribute
     */
    org.apache.xmlbeans.XmlInt xgetRemoteReplyQueueScaleout();
    
    /**
     * True if has "remoteReplyQueueScaleout" attribute
     */
    boolean isSetRemoteReplyQueueScaleout();
    
    /**
     * Sets the "remoteReplyQueueScaleout" attribute
     */
    void setRemoteReplyQueueScaleout(int remoteReplyQueueScaleout);
    
    /**
     * Sets (as xml) the "remoteReplyQueueScaleout" attribute
     */
    void xsetRemoteReplyQueueScaleout(org.apache.xmlbeans.XmlInt remoteReplyQueueScaleout);
    
    /**
     * Unsets the "remoteReplyQueueScaleout" attribute
     */
    void unsetRemoteReplyQueueScaleout();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType newInstance() {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.RemoteAnalysisEngineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
