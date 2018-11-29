/*
 * XML Type:  asyncPrimitiveErrorConfigurationType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier;


/**
 * An XML asyncPrimitiveErrorConfigurationType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public interface AsyncPrimitiveErrorConfigurationType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AsyncPrimitiveErrorConfigurationType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF92DAFB1CE99BF2EBEED68120E39AEA0").resolveHandle("asyncprimitiveerrorconfigurationtype797dtype");
    
    /**
     * Gets the "processCasErrors" element
     */
    org.apache.uima.resourceSpecifier.ProcessCasErrorsType getProcessCasErrors();
    
    /**
     * True if has "processCasErrors" element
     */
    boolean isSetProcessCasErrors();
    
    /**
     * Sets the "processCasErrors" element
     */
    void setProcessCasErrors(org.apache.uima.resourceSpecifier.ProcessCasErrorsType processCasErrors);
    
    /**
     * Appends and returns a new empty "processCasErrors" element
     */
    org.apache.uima.resourceSpecifier.ProcessCasErrorsType addNewProcessCasErrors();
    
    /**
     * Unsets the "processCasErrors" element
     */
    void unsetProcessCasErrors();
    
    /**
     * Gets the "collectionProcessCompleteErrors" element
     */
    org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType getCollectionProcessCompleteErrors();
    
    /**
     * True if has "collectionProcessCompleteErrors" element
     */
    boolean isSetCollectionProcessCompleteErrors();
    
    /**
     * Sets the "collectionProcessCompleteErrors" element
     */
    void setCollectionProcessCompleteErrors(org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType collectionProcessCompleteErrors);
    
    /**
     * Appends and returns a new empty "collectionProcessCompleteErrors" element
     */
    org.apache.uima.resourceSpecifier.CollectionProcessCompleteErrorsType addNewCollectionProcessCompleteErrors();
    
    /**
     * Unsets the "collectionProcessCompleteErrors" element
     */
    void unsetCollectionProcessCompleteErrors();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType newInstance() {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.apache.uima.resourceSpecifier.AsyncPrimitiveErrorConfigurationType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
