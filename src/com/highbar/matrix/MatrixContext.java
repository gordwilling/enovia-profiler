package com.highbar.matrix;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class wraps a matrix.db.Context object and exposes some common context-related operations.
 *   Runtime exceptions wrap any MatrixExceptions that may be thrown.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MatrixContext
{
    private Context context;

    public MatrixContext( String user, String password )
    {
        try
        {
            context = new Context( "" );
            context.setUser( user );
            context.setPassword( password );
        }
        catch( MatrixException e )
        {
            throw new MatrixAccessException( e );
        }
    }

    public MatrixTransaction startWriteTransaction()
    {
        MatrixTransaction t = new MatrixTransaction( this );

        try
        {
            context.start( true );
        }
        catch( MatrixException e )
        {
            throw new MatrixAccessException( e );
        }

        return t;
    }

    public MatrixTransaction startReadTransaction()
    {
        MatrixTransaction t = new MatrixTransaction( this );

        try
        {
            context.start( false );
        }
        catch( MatrixException e )
        {
            throw new MatrixAccessException( e );
        }

        return t;
    }

    public void connect()
    {
        try
        {
            context.connect();
        }
        catch( MatrixException e )
        {
            throw new MatrixAccessException( e );
        }
    }

    public void disconnect()
    {
        try
        {
            context.disconnect();
        }
        catch( MatrixException e )
        {
            throw new MatrixAccessException( e );
        }
    }

    public Context getContext()
    {
        return context;
    }
}