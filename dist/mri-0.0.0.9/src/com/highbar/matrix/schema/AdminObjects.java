package com.highbar.matrix.schema;

import java.util.AbstractList;
import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 *   <dd>
 *     Utility class for working with Admin objects
 *   </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class AdminObjects
{
    /**
     * Gets a list of AdminObjects backed by the specified array
     * @param adminObjects an array of {@link AdminObject} instances
     * @return a list backed by the specified array
     */
    public static List<String> toList( final AdminObject... adminObjects )
    {
        return new AbstractList<String>() {

            public String get( final int index )
            {
                return adminObjects[index].n();
            }

            public int size()
            {
                return adminObjects.length;
            }
        };
    }
}