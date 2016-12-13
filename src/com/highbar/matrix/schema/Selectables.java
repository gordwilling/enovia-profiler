package com.highbar.matrix.schema;

import java.util.AbstractList;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 *   <dd>
 *     Utility class for working with Selectable objects
 *   </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class Selectables
{
    /**
     * Gets a list of Selectables (in String form) backed by the specified array
     * @param selectables an array of {@link Selectable} instances
     * @return a list backed by the specified array
     */
    public static List<String> toList( final Selectable... selectables )
    {
        return new AbstractList<String>() {

            public String get( final int index )
            {
                return selectables[index].s();
            }

            public int size()
            {
                return selectables.length;
            }
        };
    }
}