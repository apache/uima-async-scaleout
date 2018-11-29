/*
 * XML Type:  processCasErrorsType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.ProcessCasErrorsType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML processCasErrorsType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.ProcessCasErrorsType.
 */
public interface ProcessCasErrorsType extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ProcessCasErrorsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("processcaserrorstyped985type");
    
    /**
     * Gets the "maxRetries" attribute
     */
    int getMaxRetries();
    
    /**
     * Gets (as xml) the "maxRetries" attribute
     */
    org.apache.xmlbeans.XmlInt xgetMaxRetries();
    
    /**
     * True if has "maxRetries" attribute
     */
    boolean isSetMaxRetries();
    
    /**
     * Sets the "maxRetries" attribute
     */
    void setMaxRetries(int maxRetries);
    
    /**
     * Sets (as xml) the "maxRetries" attribute
     */
    void xsetMaxRetries(org.apache.xmlbeans.XmlInt maxRetries);
    
    /**
     * Unsets the "maxRetries" attribute
     */
    void unsetMaxRetries();
    
    /**
     * Gets the "timeout" attribute
     */
    int getTimeout();
    
    /**
     * Gets (as xml) the "timeout" attribute
     */
    org.apache.xmlbeans.XmlInt xgetTimeout();
    
    /**
     * True if has "timeout" attribute
     */
    boolean isSetTimeout();
    
    /**
     * Sets the "timeout" attribute
     */
    void setTimeout(int timeout);
    
    /**
     * Sets (as xml) the "timeout" attribute
     */
    void xsetTimeout(org.apache.xmlbeans.XmlInt timeout);
    
    /**
     * Unsets the "timeout" attribute
     */
    void unsetTimeout();
    
    /**
     * Gets the "thresholdCount" attribute
     */
    int getThresholdCount();
    
    /**
     * Gets (as xml) the "thresholdCount" attribute
     */
    org.apache.xmlbeans.XmlInt xgetThresholdCount();
    
    /**
     * True if has "thresholdCount" attribute
     */
    boolean isSetThresholdCount();
    
    /**
     * Sets the "thresholdCount" attribute
     */
    void setThresholdCount(int thresholdCount);
    
    /**
     * Sets (as xml) the "thresholdCount" attribute
     */
    void xsetThresholdCount(org.apache.xmlbeans.XmlInt thresholdCount);
    
    /**
     * Unsets the "thresholdCount" attribute
     */
    void unsetThresholdCount();
    
    /**
     * Gets the "continueOnRetryFailure" attribute
     */
    java.lang.String getContinueOnRetryFailure();
    
    /**
     * Gets (as xml) the "continueOnRetryFailure" attribute
     */
    org.apache.xmlbeans.XmlString xgetContinueOnRetryFailure();
    
    /**
     * True if has "continueOnRetryFailure" attribute
     */
    boolean isSetContinueOnRetryFailure();
    
    /**
     * Sets the "continueOnRetryFailure" attribute
     */
    void setContinueOnRetryFailure(java.lang.String continueOnRetryFailure);
    
    /**
     * Sets (as xml) the "continueOnRetryFailure" attribute
     */
    void xsetContinueOnRetryFailure(org.apache.xmlbeans.XmlString continueOnRetryFailure);
    
    /**
     * Unsets the "continueOnRetryFailure" attribute
     */
    void unsetContinueOnRetryFailure();
    
    /**
     * Gets the "thresholdWindow" attribute
     */
    int getThresholdWindow();
    
    /**
     * Gets (as xml) the "thresholdWindow" attribute
     */
    org.apache.xmlbeans.XmlInt xgetThresholdWindow();
    
    /**
     * True if has "thresholdWindow" attribute
     */
    boolean isSetThresholdWindow();
    
    /**
     * Sets the "thresholdWindow" attribute
     */
    void setThresholdWindow(int thresholdWindow);
    
    /**
     * Sets (as xml) the "thresholdWindow" attribute
     */
    void xsetThresholdWindow(org.apache.xmlbeans.XmlInt thresholdWindow);
    
    /**
     * Unsets the "thresholdWindow" attribute
     */
    void unsetThresholdWindow();
    
    /**
     * Gets the "thresholdAction" attribute
     */
    java.lang.String getThresholdAction();
    
    /**
     * Gets (as xml) the "thresholdAction" attribute
     */
    org.apache.xmlbeans.XmlString xgetThresholdAction();
    
    /**
     * True if has "thresholdAction" attribute
     */
    boolean isSetThresholdAction();
    
    /**
     * Sets the "thresholdAction" attribute
     */
    void setThresholdAction(java.lang.String thresholdAction);
    
    /**
     * Sets (as xml) the "thresholdAction" attribute
     */
    void xsetThresholdAction(org.apache.xmlbeans.XmlString thresholdAction);
    
    /**
     * Unsets the "thresholdAction" attribute
     */
    void unsetThresholdAction();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType newInstance() {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.ProcessCasErrorsType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.ProcessCasErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
