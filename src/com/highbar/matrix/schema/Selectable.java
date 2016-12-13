package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *  This interface provides a method to retrieve the selectable form of a matrix property 
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public interface Selectable
{
    /**
     * Gets the selectable form of the matrix property name.  For basic properties, the value
     * returned will be the same as the name.  For attributes, the value returned is in the
     * form "attribute[name]"
     * @return the selectable form of the matrix property name
     */
    public String s();
}
