/*
 * XML Type:  analysisEngineType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.AnalysisEngineType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML analysisEngineType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class AnalysisEngineTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.AnalysisEngineType
{
    private static final long serialVersionUID = 1L;
    
    public AnalysisEngineTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SCALEOUT$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "scaleout");
    private static final javax.xml.namespace.QName DELEGATES$2 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "delegates");
    private static final javax.xml.namespace.QName CASMULTIPLIER$4 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "casMultiplier");
    private static final javax.xml.namespace.QName KEY$6 = 
        new javax.xml.namespace.QName("", "key");
    private static final javax.xml.namespace.QName ASYNC$8 = 
        new javax.xml.namespace.QName("", "async");
    private static final javax.xml.namespace.QName INTERNALREPLYQUEUESCALEOUT$10 = 
        new javax.xml.namespace.QName("", "internalReplyQueueScaleout");
    private static final javax.xml.namespace.QName INPUTQUEUESCALEOUT$12 = 
        new javax.xml.namespace.QName("", "inputQueueScaleout");
    
    
    /**
     * Gets the "scaleout" element
     */
    public org.apache.uima.resourceSpecifier.ScaleoutType getScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ScaleoutType target = null;
            target = (org.apache.uima.resourceSpecifier.ScaleoutType)get_store().find_element_user(SCALEOUT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "scaleout" element
     */
    public boolean isSetScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SCALEOUT$0) != 0;
        }
    }
    
    /**
     * Sets the "scaleout" element
     */
    public void setScaleout(org.apache.uima.resourceSpecifier.ScaleoutType scaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ScaleoutType target = null;
            target = (org.apache.uima.resourceSpecifier.ScaleoutType)get_store().find_element_user(SCALEOUT$0, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.ScaleoutType)get_store().add_element_user(SCALEOUT$0);
            }
            target.set(scaleout);
        }
    }
    
    /**
     * Appends and returns a new empty "scaleout" element
     */
    public org.apache.uima.resourceSpecifier.ScaleoutType addNewScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.ScaleoutType target = null;
            target = (org.apache.uima.resourceSpecifier.ScaleoutType)get_store().add_element_user(SCALEOUT$0);
            return target;
        }
    }
    
    /**
     * Unsets the "scaleout" element
     */
    public void unsetScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SCALEOUT$0, 0);
        }
    }
    
    /**
     * Gets the "delegates" element
     */
    public org.apache.uima.resourceSpecifier.DelegatesType getDelegates()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegatesType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegatesType)get_store().find_element_user(DELEGATES$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "delegates" element
     */
    public boolean isSetDelegates()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DELEGATES$2) != 0;
        }
    }
    
    /**
     * Sets the "delegates" element
     */
    public void setDelegates(org.apache.uima.resourceSpecifier.DelegatesType delegates)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegatesType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegatesType)get_store().find_element_user(DELEGATES$2, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.DelegatesType)get_store().add_element_user(DELEGATES$2);
            }
            target.set(delegates);
        }
    }
    
    /**
     * Appends and returns a new empty "delegates" element
     */
    public org.apache.uima.resourceSpecifier.DelegatesType addNewDelegates()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.DelegatesType target = null;
            target = (org.apache.uima.resourceSpecifier.DelegatesType)get_store().add_element_user(DELEGATES$2);
            return target;
        }
    }
    
    /**
     * Unsets the "delegates" element
     */
    public void unsetDelegates()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DELEGATES$2, 0);
        }
    }
    
    /**
     * Gets the "casMultiplier" element
     */
    public org.apache.uima.resourceSpecifier.CasMultiplierType getCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasMultiplierType target = null;
            target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().find_element_user(CASMULTIPLIER$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "casMultiplier" element
     */
    public boolean isSetCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CASMULTIPLIER$4) != 0;
        }
    }
    
    /**
     * Sets the "casMultiplier" element
     */
    public void setCasMultiplier(org.apache.uima.resourceSpecifier.CasMultiplierType casMultiplier)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasMultiplierType target = null;
            target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().find_element_user(CASMULTIPLIER$4, 0);
            if (target == null)
            {
                target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().add_element_user(CASMULTIPLIER$4);
            }
            target.set(casMultiplier);
        }
    }
    
    /**
     * Appends and returns a new empty "casMultiplier" element
     */
    public org.apache.uima.resourceSpecifier.CasMultiplierType addNewCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.CasMultiplierType target = null;
            target = (org.apache.uima.resourceSpecifier.CasMultiplierType)get_store().add_element_user(CASMULTIPLIER$4);
            return target;
        }
    }
    
    /**
     * Unsets the "casMultiplier" element
     */
    public void unsetCasMultiplier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CASMULTIPLIER$4, 0);
        }
    }
    
    /**
     * Gets the "key" attribute
     */
    public java.lang.String getKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(KEY$6);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "key" attribute
     */
    public org.apache.xmlbeans.XmlString xgetKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(KEY$6);
            return target;
        }
    }
    
    /**
     * True if has "key" attribute
     */
    public boolean isSetKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(KEY$6) != null;
        }
    }
    
    /**
     * Sets the "key" attribute
     */
    public void setKey(java.lang.String key)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(KEY$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(KEY$6);
            }
            target.setStringValue(key);
        }
    }
    
    /**
     * Sets (as xml) the "key" attribute
     */
    public void xsetKey(org.apache.xmlbeans.XmlString key)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(KEY$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(KEY$6);
            }
            target.set(key);
        }
    }
    
    /**
     * Unsets the "key" attribute
     */
    public void unsetKey()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(KEY$6);
        }
    }
    
    /**
     * Gets the "async" attribute
     */
    public java.lang.String getAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ASYNC$8);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "async" attribute
     */
    public org.apache.xmlbeans.XmlString xgetAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ASYNC$8);
            return target;
        }
    }
    
    /**
     * True if has "async" attribute
     */
    public boolean isSetAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ASYNC$8) != null;
        }
    }
    
    /**
     * Sets the "async" attribute
     */
    public void setAsync(java.lang.String async)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ASYNC$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ASYNC$8);
            }
            target.setStringValue(async);
        }
    }
    
    /**
     * Sets (as xml) the "async" attribute
     */
    public void xsetAsync(org.apache.xmlbeans.XmlString async)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ASYNC$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ASYNC$8);
            }
            target.set(async);
        }
    }
    
    /**
     * Unsets the "async" attribute
     */
    public void unsetAsync()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ASYNC$8);
        }
    }
    
    /**
     * Gets the "internalReplyQueueScaleout" attribute
     */
    public java.lang.String getInternalReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INTERNALREPLYQUEUESCALEOUT$10);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "internalReplyQueueScaleout" attribute
     */
    public org.apache.xmlbeans.XmlString xgetInternalReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(INTERNALREPLYQUEUESCALEOUT$10);
            return target;
        }
    }
    
    /**
     * True if has "internalReplyQueueScaleout" attribute
     */
    public boolean isSetInternalReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(INTERNALREPLYQUEUESCALEOUT$10) != null;
        }
    }
    
    /**
     * Sets the "internalReplyQueueScaleout" attribute
     */
    public void setInternalReplyQueueScaleout(java.lang.String internalReplyQueueScaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INTERNALREPLYQUEUESCALEOUT$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(INTERNALREPLYQUEUESCALEOUT$10);
            }
            target.setStringValue(internalReplyQueueScaleout);
        }
    }
    
    /**
     * Sets (as xml) the "internalReplyQueueScaleout" attribute
     */
    public void xsetInternalReplyQueueScaleout(org.apache.xmlbeans.XmlString internalReplyQueueScaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(INTERNALREPLYQUEUESCALEOUT$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(INTERNALREPLYQUEUESCALEOUT$10);
            }
            target.set(internalReplyQueueScaleout);
        }
    }
    
    /**
     * Unsets the "internalReplyQueueScaleout" attribute
     */
    public void unsetInternalReplyQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(INTERNALREPLYQUEUESCALEOUT$10);
        }
    }
    
    /**
     * Gets the "inputQueueScaleout" attribute
     */
    public java.lang.String getInputQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INPUTQUEUESCALEOUT$12);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "inputQueueScaleout" attribute
     */
    public org.apache.xmlbeans.XmlString xgetInputQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(INPUTQUEUESCALEOUT$12);
            return target;
        }
    }
    
    /**
     * True if has "inputQueueScaleout" attribute
     */
    public boolean isSetInputQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(INPUTQUEUESCALEOUT$12) != null;
        }
    }
    
    /**
     * Sets the "inputQueueScaleout" attribute
     */
    public void setInputQueueScaleout(java.lang.String inputQueueScaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INPUTQUEUESCALEOUT$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(INPUTQUEUESCALEOUT$12);
            }
            target.setStringValue(inputQueueScaleout);
        }
    }
    
    /**
     * Sets (as xml) the "inputQueueScaleout" attribute
     */
    public void xsetInputQueueScaleout(org.apache.xmlbeans.XmlString inputQueueScaleout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(INPUTQUEUESCALEOUT$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(INPUTQUEUESCALEOUT$12);
            }
            target.set(inputQueueScaleout);
        }
    }
    
    /**
     * Unsets the "inputQueueScaleout" attribute
     */
    public void unsetInputQueueScaleout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(INPUTQUEUESCALEOUT$12);
        }
    }
}
