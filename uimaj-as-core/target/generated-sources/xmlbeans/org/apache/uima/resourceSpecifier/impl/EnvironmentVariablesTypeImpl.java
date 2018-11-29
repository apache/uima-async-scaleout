/*
 * XML Type:  environmentVariablesType
 * Namespace: http://uima.apache.org/resourceSpecifier
 * Java type: org.apache.uima.resourceSpecifier.EnvironmentVariablesType
 *
 * Automatically generated - do not modify.
 */
package org.apache.uima.resourceSpecifier.impl;
/**
 * An XML environmentVariablesType(@http://uima.apache.org/resourceSpecifier).
 *
 * This is a complex type.
 */
public class EnvironmentVariablesTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.apache.uima.resourceSpecifier.EnvironmentVariablesType
{
    private static final long serialVersionUID = 1L;
    
    public EnvironmentVariablesTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ENVIRONMENTVARIABLE$0 = 
        new javax.xml.namespace.QName("http://uima.apache.org/resourceSpecifier", "environmentVariable");
    
    
    /**
     * Gets array of all "environmentVariable" elements
     */
    public org.apache.uima.resourceSpecifier.EnvironmentVariableType[] getEnvironmentVariableArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(ENVIRONMENTVARIABLE$0, targetList);
            org.apache.uima.resourceSpecifier.EnvironmentVariableType[] result = new org.apache.uima.resourceSpecifier.EnvironmentVariableType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "environmentVariable" element
     */
    public org.apache.uima.resourceSpecifier.EnvironmentVariableType getEnvironmentVariableArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariableType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariableType)get_store().find_element_user(ENVIRONMENTVARIABLE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "environmentVariable" element
     */
    public int sizeOfEnvironmentVariableArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ENVIRONMENTVARIABLE$0);
        }
    }
    
    /**
     * Sets array of all "environmentVariable" element
     */
    public void setEnvironmentVariableArray(org.apache.uima.resourceSpecifier.EnvironmentVariableType[] environmentVariableArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(environmentVariableArray, ENVIRONMENTVARIABLE$0);
        }
    }
    
    /**
     * Sets ith "environmentVariable" element
     */
    public void setEnvironmentVariableArray(int i, org.apache.uima.resourceSpecifier.EnvironmentVariableType environmentVariable)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariableType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariableType)get_store().find_element_user(ENVIRONMENTVARIABLE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(environmentVariable);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "environmentVariable" element
     */
    public org.apache.uima.resourceSpecifier.EnvironmentVariableType insertNewEnvironmentVariable(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariableType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariableType)get_store().insert_element_user(ENVIRONMENTVARIABLE$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "environmentVariable" element
     */
    public org.apache.uima.resourceSpecifier.EnvironmentVariableType addNewEnvironmentVariable()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.uima.resourceSpecifier.EnvironmentVariableType target = null;
            target = (org.apache.uima.resourceSpecifier.EnvironmentVariableType)get_store().add_element_user(ENVIRONMENTVARIABLE$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "environmentVariable" element
     */
    public void removeEnvironmentVariable(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ENVIRONMENTVARIABLE$0, i);
        }
    }
}
