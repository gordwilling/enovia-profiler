package com.highbar.matrix;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   Runtime exception type for communicating Matrix access errors.  Typically, API code would
 *   catch a MatrixException and wrap it in one of these.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MatrixAccessException extends RuntimeException
{
    public MatrixAccessException( Throwable cause )
    {
        super( cause );
    }

    public MatrixAccessException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public MatrixAccessException( String message )
    {
        super( message );
    }
}