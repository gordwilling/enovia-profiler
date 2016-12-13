package com.highbar.matrix.schema;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This interface defines common properties of Matrix admin objects
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public interface AdminObject
{
    /**
     * Get the actual name of a Matrix admin object
     * @return the actual name the Matrix admin object
     */
    public String n();
}
