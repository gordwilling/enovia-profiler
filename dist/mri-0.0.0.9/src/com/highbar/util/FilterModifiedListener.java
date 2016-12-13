package com.highbar.util;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Used to indicate that some filter criteria has changed
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 * @see Filter
 * @see FilteredList
 */
public interface FilterModifiedListener
{
    /**
     * Implement this method to respond accordingly when some filter criteria changes
     */
    void onFilterModify();
}