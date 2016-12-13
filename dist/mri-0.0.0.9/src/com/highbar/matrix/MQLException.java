package com.highbar.matrix;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This exception is thrown by {@link MQL#run(matrix.db.Context, String)} when its internal MQLCommand executes
 *   and populates its error property.  
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MQLException extends RuntimeException
{
    public MQLException( Throwable t )
    {
        super( t );
    }

    public MQLException( String message, Throwable t )
    {
        super( message, t );
    }

    public MQLException( String message )
    {
        super( message );
    }
}
