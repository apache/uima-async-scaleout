/*
 * XML Type:  collectionProcessCompleteErrorsType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML collectionProcessCompleteErrorsType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType.
 */
public interface CollectionProcessCompleteErrorsType extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(CollectionProcessCompleteErrorsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("collectionprocesscompleteerrorstype4653type");
    
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
     * Gets the "additionalErrorAction" attribute
     */
    java.lang.String getAdditionalErrorAction();
    
    /**
     * Gets (as xml) the "additionalErrorAction" attribute
     */
    org.apache.xmlbeans.XmlString xgetAdditionalErrorAction();
    
    /**
     * True if has "additionalErrorAction" attribute
     */
    boolean isSetAdditionalErrorAction();
    
    /**
     * Sets the "additionalErrorAction" attribute
     */
    void setAdditionalErrorAction(java.lang.String additionalErrorAction);
    
    /**
     * Sets (as xml) the "additionalErrorAction" attribute
     */
    void xsetAdditionalErrorAction(org.apache.xmlbeans.XmlString additionalErrorAction);
    
    /**
     * Unsets the "additionalErrorAction" attribute
     */
    void unsetAdditionalErrorAction();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType newInstance() {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
