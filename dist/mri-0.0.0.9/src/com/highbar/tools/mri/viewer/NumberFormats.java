package com.highbar.tools.mri.viewer;

import java.text.DecimalFormat;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Utility class to format numbers
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class NumberFormats
{
    public static String nanosToMillis( double nanos )
    {
        return new DecimalFormat( "0.00" ).format( nanos / 1000000d );
    }
}
