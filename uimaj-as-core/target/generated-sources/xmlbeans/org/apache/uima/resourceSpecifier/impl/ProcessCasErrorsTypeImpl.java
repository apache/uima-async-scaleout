/*
 * XML Type:  processCasErrorsType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.ProcessCasErrorsType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML processCasErrorsType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is an atomic type that is a restriction of org.apache.uima.resourceSpecifier.ProcessCasErrorsType.
 */
public class ProcessCasErrorsTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements org.apache.uima.resourceSpecifier.ProcessCasErrorsType
{
    private static final long serialVersionUID = 1L;
    
    public ProcessCasErrorsTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected ProcessCasErrorsTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName MAXRETRIES$0 = 
        new javax.xml.namespace.QName("", "maxRetries");
    private static final javax.xml.namespace.QName TIMEOUT$2 = 
        new javax.xml.namespace.QName("", "timeout");
    private static final javax.xml.namespace.QName THRESHOLDCOUNT$4 = 
        new javax.xml.namespace.QName("", "thresholdCount");
    private static final javax.xml.namespace.QName CONTINUEONRETRYFAILURE$6 = 
        new javax.xml.namespace.QName("", "continueOnRetryFailure");
    private static final javax.xml.namespace.QName THRESHOLDWINDOW$8 = 
        new javax.xml.namespace.QName("", "thresholdWindow");
    private static final javax.xml.namespace.QName THRESHOLDACTION$10 = 
        new javax.xml.namespace.QName("", "thresholdAction");
    
    
    /**
     * Gets the "maxRetries" attribute
     */
    public int getMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(MAXRETRIES$0);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "maxRetries" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(MAXRETRIES$0);
            }
            return target;
        }
    }
    
    /**
     * True if has "maxRetries" attribute
     */
    public boolean isSetMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(MAXRETRIES$0) != null;
        }
    }
    
    /**
     * Sets the "maxRetries" attribute
     */
    public void setMaxRetries(int maxRetries)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(MAXRETRIES$0);
            }
            target.setIntValue(maxRetries);
        }
    }
    
    /**
     * Sets (as xml) the "maxRetries" attribute
     */
    public void xsetMaxRetries(org.apache.xmlbeans.XmlInt maxRetries)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(MAXRETRIES$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(MAXRETRIES$0);
            }
            target.set(maxRetries);
        }
    }
    
    /**
     * Unsets the "maxRetries" attribute
     */
    public void unsetMaxRetries()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(MAXRETRIES$0);
        }
    }
    
    /**
     * Gets the "timeout" attribute
     */
    public int getTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(TIMEOUT$2);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "timeout" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(TIMEOUT$2);
            }
            return target;
        }
    }
    
    /**
     * True if has "timeout" attribute
     */
    public boolean isSetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(TIMEOUT$2) != null;
        }
    }
    
    /**
     * Sets the "timeout" attribute
     */
    public void setTimeout(int timeout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TIMEOUT$2);
            }
            target.setIntValue(timeout);
        }
    }
    
    /**
     * Sets (as xml) the "timeout" attribute
     */
    public void xsetTimeout(org.apache.xmlbeans.XmlInt timeout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(TIMEOUT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(TIMEOUT$2);
            }
            target.set(timeout);
        }
    }
    
    /**
     * Unsets the "timeout" attribute
     */
    public void unsetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(TIMEOUT$2);
        }
    }
    
    /**
     * Gets the "thresholdCount" attribute
     */
    public int getThresholdCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLDCOUNT$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(THRESHOLDCOUNT$4);
            }
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "thresholdCount" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetThresholdCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(THRESHOLDCOUNT$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_default_attribute_value(THRESHOLDCOUNT$4);
            }
            return target;
        }
    }
    
    /**
     * True if has "thresholdCount" attribute
     */
    public boolean isSetThresholdCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(THRESHOLDCOUNT$4) != null;
        }
    }
    
    /**
     * Sets the "thresholdCount" attribute
     */
    public void setThresholdCount(int thresholdCount)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLDCOUNT$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(THRESHOLDCOUNT$4);
            }
            target.setIntValue(thresholdCount);
        }
    }
    
    /**
     * Sets (as xml) the "thresholdCount" attribute
     */
    public void xsetThresholdCount(org.apache.xmlbeans.XmlInt thresholdCount)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(THRESHOLDCOUNT$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(THRESHOLDCOUNT$4);
            }
            target.set(thresholdCount);
        }
    }
    
    /**
     * Unsets the "thresholdCount" attribute
     */
    public void unsetThresholdCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(THRESHOLDCOUNT$4);
        }
    }
    
    /**
     * Gets the "continueOnRetryFailure" attribute
     */
    public java.lang.String getContinueOnRetryFailure()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CONTINUEONRETRYFAILURE$6);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "continueOnRetryFailure" attribute
     */
    public org.apache.xmlbeans.XmlString xgetContinueOnRetryFailure()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(CONTINUEONRETRYFAILURE$6);
            return target;
        }
    }
    
    /**
     * True if has "continueOnRetryFailure" attribute
     */
    public boolean isSetContinueOnRetryFailure()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(CONTINUEONRETRYFAILURE$6) != null;
        }
    }
    
    /**
     * Sets the "continueOnRetryFailure" attribute
     */
    public void setContinueOnRetryFailure(java.lang.String continueOnRetryFailure)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CONTINUEONRETRYFAILURE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(CONTINUEONRETRYFAILURE$6);
            }
            target.setStringValue(continueOnRetryFailure);
        }
    }
    
    /**
     * Sets (as xml) the "continueOnRetryFailure" attribute
     */
    public void xsetContinueOnRetryFailure(org.apache.xmlbeans.XmlString continueOnRetryFailure)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(CONTINUEONRETRYFAILURE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(CONTINUEONRETRYFAILURE$6);
            }
            target.set(continueOnRetryFailure);
        }
    }
    
    /**
     * Unsets the "continueOnRetryFailure" attribute
     */
    public void unsetContinueOnRetryFailure()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(CONTINUEONRETRYFAILURE$6);
        }
    }
    
    /**
     * Gets the "thresholdWindow" attribute
     */
    public int getThresholdWindow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLDWINDOW$8);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "thresholdWindow" attribute
     */
    public org.apache.xmlbeans.XmlInt xgetThresholdWindow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(THRESHOLDWINDOW$8);
            return target;
        }
    }
    
    /**
     * True if has "thresholdWindow" attribute
     */
    public boolean isSetThresholdWindow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(THRESHOLDWINDOW$8) != null;
        }
    }
    
    /**
     * Sets the "thresholdWindow" attribute
     */
    public void setThresholdWindow(int thresholdWindow)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLDWINDOW$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(THRESHOLDWINDOW$8);
            }
            target.setIntValue(thresholdWindow);
        }
    }
    
    /**
     * Sets (as xml) the "thresholdWindow" attribute
     */
    public void xsetThresholdWindow(org.apache.xmlbeans.XmlInt thresholdWindow)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_attribute_user(THRESHOLDWINDOW$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_attribute_user(THRESHOLDWINDOW$8);
            }
            target.set(thresholdWindow);
        }
    }
    
    /**
     * Unsets the "thresholdWindow" attribute
     */
    public void unsetThresholdWindow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(THRESHOLDWINDOW$8);
        }
    }
    
    /**
     * Gets the "thresholdAction" attribute
     */
    public java.lang.String getThresholdAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLDACTION$10);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "thresholdAction" attribute
     */
    public org.apache.xmlbeans.XmlString xgetThresholdAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(THRESHOLDACTION$10);
            return target;
        }
    }
    
    /**
     * True if has "thresholdAction" attribute
     */
    public boolean isSetThresholdAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(THRESHOLDACTION$10) != null;
        }
    }
    
    /**
     * Sets the "thresholdAction" attribute
     */
    public void setThresholdAction(java.lang.String thresholdAction)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLDACTION$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(THRESHOLDACTION$10);
            }
            target.setStringValue(thresholdAction);
        }
    }
    
    /**
     * Sets (as xml) the "thresholdAction" attribute
     */
    public void xsetThresholdAction(org.apache.xmlbeans.XmlString thresholdAction)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(THRESHOLDACTION$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(THRESHOLDACTION$10);
            }
            target.set(thresholdAction);
        }
    }
    
    /**
     * Unsets the "thresholdAction" attribute
     */
    public void unsetThresholdAction()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(THRESHOLDACTION$10);
        }
    }
}
