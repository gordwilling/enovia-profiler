package com.highbar.util;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This interface enables data structure filtering based on some custom criteria
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 * @see FilteredList
 * @see FilterModifiedListener
 */
public interface Filter<T>
{
    /**
     * Indicates if the specified value should be filtered or not
     * @param value the value to check
     * @return true if the value can pass through the filter, false otherwise
     */
    boolean accept( T value );
}