package com.highbar.util;

import java.util.Collection;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Utility class for working with Strings
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2003 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class Strings
{
    public static String valueOf( Collection c, String separator )
    {
        StringBuilder value = new StringBuilder();

        for ( Object o : c )
        {
            value.append( o ).append( separator );
        }
        // trim off the last separator
        if ( value.length() != 0 )
        {
            value.setLength( value.length() - separator.length() );
        }

        return value.toString();
    }
}